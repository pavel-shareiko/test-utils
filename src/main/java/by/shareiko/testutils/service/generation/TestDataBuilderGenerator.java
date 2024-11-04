package by.shareiko.testutils.service.generation;

import by.shareiko.testutils.properties.TestDataBuilderConfiguration;
import by.shareiko.testutils.ui.model.FieldConfiguration;
import by.shareiko.testutils.classref.ClassReferenceResolution;
import by.shareiko.testutils.classref.SimpleUnqualifiedClassNameReferenceResolver;
import by.shareiko.testutils.classref.UnqualifiedClassNameReferenceResolver;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class TestDataBuilderGenerator {
    private final Project project;
    private final PsiDirectory targetDir;
    private final TestDataBuilderConfiguration config;

    public TestDataBuilderGenerator(Project project, PsiDirectory targetDir, TestDataBuilderConfiguration config) {
        this.project = project;
        this.targetDir = targetDir;
        this.config = config;
    }

    public PsiClass generate() {
        PsiClass builderClass = JavaDirectoryService.getInstance().createClass(targetDir, config.getBuilderName());
        builderClass.getImplementsList().add(createClassReference(config.getBaseInterface()));

        addFields(builderClass);
        addBuildMethodImplementation(builderClass);
        decorateClass(builderClass);

        tryResolveMissingImports(builderClass);

        beautifyCode(builderClass);
        return builderClass;
    }

    private void decorateClass(PsiClass builderClass) {
        config.getDecorators().forEach(d -> d.decorate(builderClass));
    }

    private void beautifyCode(PsiClass builderClass) {
        JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(project);
        styleManager.shortenClassReferences(builderClass);
        styleManager.optimizeImports(builderClass.getContainingFile());
    }

    private void addBuildMethodImplementation(PsiClass builderClass) {
        PsiMethod buildMethod = createBuildMethod(project, config);
        builderClass.add(buildMethod);
    }

    private void tryResolveMissingImports(PsiClass builderClass) {
        UnqualifiedClassNameReferenceResolver classNameReferenceResolver =
                new SimpleUnqualifiedClassNameReferenceResolver();

        var unresolvedReferences = new ArrayList<>();
        Arrays.stream(builderClass.getFields()).filter(f -> f.getInitializer() != null).forEach(f -> {
            PsiExpression initializer = f.getInitializer();
            String text = initializer.getText();
            ClassReferenceResolution resolution = classNameReferenceResolver.resolve(project, builderClass, text);

            PsiJavaFile javaFile = (PsiJavaFile) builderClass.getContainingFile();

            for (var resolvedReference : resolution.getResolvedClassReferences()) {
                javaFile.importClass(resolvedReference.resolvedClass());
            }

            unresolvedReferences.addAll(resolution.getUnresolvedClassReferences());
        });

        if (!unresolvedReferences.isEmpty()) {
            ApplicationManager.getApplication().invokeLater(() -> {
                Messages.showInfoMessage(
                        ("""
                                Unable to automatically resolve some simple class name references.\
                                You need to manually import required classes.\
                                Following classes have left unresolved: %s

                                <b>Hint:</b> Use fully qualified class names to avoid such errors in the future
                                """)
                                .formatted(unresolvedReferences),
                        "Unresolved Class References");
            });
        }
    }

    private void addFields(PsiClass builderClass) {
        FieldGenerator fieldGenerator = new FieldGeneratorImpl(project, builderClass);
        for (FieldConfiguration fieldConfig : config.getSelectedFields()) {
            PsiField field = fieldGenerator.generate(fieldConfig);
            builderClass.add(field);
        }
    }

    private PsiJavaCodeReferenceElement createClassReference(PsiClass interfaceClass) {
        PsiClass typeParameter = config.getSourceClass();
        PsiElementFactory elementFactory = JavaPsiFacade.getInstance(project).getElementFactory();

        String referenceText = interfaceClass.getQualifiedName();
        if (interfaceClass.hasTypeParameters() && typeParameter != null) {
            referenceText += "<" + typeParameter.getName() + ">";
        }

        return elementFactory.createReferenceFromText(Objects.requireNonNull(referenceText), interfaceClass);
    }


    private static PsiMethod createBuildMethod(Project project, TestDataBuilderConfiguration config) {
        BuildMethodGenerator buildMethodGenerator = BuildMethodGeneratorFactory.getInstance(project, config);
        return buildMethodGenerator.generate(config);
    }
}
