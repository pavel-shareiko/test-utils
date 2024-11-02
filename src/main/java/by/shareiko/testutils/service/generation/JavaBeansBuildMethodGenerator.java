package by.shareiko.testutils.service.generation;

import by.shareiko.testutils.properties.TestDataBuilderConfiguration;
import by.shareiko.testutils.ui.model.FieldConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;

import java.util.List;

public class JavaBeansBuildMethodGenerator implements BuildMethodGenerator {
    private final Project project;

    public JavaBeansBuildMethodGenerator(Project project) {
        this.project = project;
    }

    @Override
    public PsiMethod generate(TestDataBuilderConfiguration config) {
        List<FieldConfiguration> selectedFields = config.getSelectedFields();

        var builderConfig = new BuildMethodBuilderConfiguration(project, config)
                .withExpressionProvider(new JBeansExpressionProvider(config))
                .withMethodName(config.getBaseInterface().getMethods()[0].getName())
                .withFields(selectedFields);

        return new BuildMethodBuilder(builderConfig).build();
    }
}
