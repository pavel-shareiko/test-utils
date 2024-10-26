package by.shareiko.testutils.ui.components.validator;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.psi.*;
import com.intellij.psi.util.ClassUtil;
import com.intellij.refactoring.ui.ClassNameReferenceEditor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.Supplier;

public class BuilderInterfaceValidator implements Supplier<ValidationInfo> {
    private final ClassNameReferenceEditor component;
    private final Project project;

    public BuilderInterfaceValidator(ClassNameReferenceEditor component, Project project) {
        this.component = component;
        this.project = project;
    }

    @Override
    public ValidationInfo get() {
        String text = component.getText();
        if (text.isEmpty()) {
            return validationError("Specify builder interface class");
        }

        PsiClass psiClass = ClassUtil.findPsiClass(PsiManager.getInstance(project), text);
        if (psiClass == null) {
            return validationError("Builder interface cannot be found");
        }

        if (!psiClass.isInterface()) {
            return validationError("Builder interface should be an interface");
        }

        String buildMethodViolation = validateHasValidBuildMethod(psiClass);
        if (buildMethodViolation != null) {
            return validationError(buildMethodViolation);
        }

        return validationSuccess();
    }

    private static String validateHasValidBuildMethod(PsiClass psiClass) {
        PsiMethod[] declaredMethods = Arrays.stream(psiClass.getMethods())
                .filter(m -> m.getName().equals("build") || m.getModifierList().hasExplicitModifier(PsiModifier.DEFAULT))
                .toArray(PsiMethod[]::new);

        if (declaredMethods.length != 1) {
            return "Builder interface should have exactly one public method";
        }

        PsiMethod possibleBuildMethod = declaredMethods[0];
        PsiType returnType = possibleBuildMethod.getReturnType();
        if (returnType instanceof PsiPrimitiveType && ((PsiPrimitiveType) returnType).getName().equals("void")) {
            return "build() method should have a return type";
        }

        return null;
    }

    private @NotNull ValidationInfo validationError(String message) {
        component.getEditorTextField().putClientProperty("JComponent.outline", "error");
        return new ValidationInfo(message, component);
    }

    private ValidationInfo validationSuccess() {
        component.getEditorTextField().putClientProperty("JComponent.outline", null);
        return null;
    }
}
