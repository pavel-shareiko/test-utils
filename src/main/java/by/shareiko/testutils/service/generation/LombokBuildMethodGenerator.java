package by.shareiko.testutils.service.generation;

import by.shareiko.testutils.properties.TestDataBuilderConfiguration;
import by.shareiko.testutils.ui.model.FieldConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.List;

public class LombokBuildMethodGenerator implements BuildMethodGenerator {
    private final Project project;

    public LombokBuildMethodGenerator(Project project) {
        this.project = project;
    }

    @Override
    public PsiMethod generate(TestDataBuilderConfiguration config) {
        List<FieldConfiguration> selectedFields = config.getSelectedFields();

        var builderConfig = new BuildMethodBuilderConfiguration(project, config)
                .withExpressionProvider(new LombokExpressionProvider(project, config))
                .withMethodName(config.getBaseInterface().getMethods()[0].getName())
                .withFields(selectedFields);

        return new BuildMethodBuilder(builderConfig).build();
    }
}
