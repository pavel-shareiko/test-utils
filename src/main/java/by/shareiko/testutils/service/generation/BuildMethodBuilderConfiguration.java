package by.shareiko.testutils.service.generation;

import by.shareiko.testutils.properties.TestDataBuilderConfiguration;
import by.shareiko.testutils.ui.model.FieldConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;

import java.util.List;

public class BuildMethodBuilderConfiguration {
    private final Project project;
    private final PsiClass sourceClass;
    private List<FieldConfiguration> selectedFields;
    private ExpressionProvider expressionProvider;
    private String methodName;

    public BuildMethodBuilderConfiguration(Project project, TestDataBuilderConfiguration builderConfiguration) {
        this.project = project;
        this.sourceClass = builderConfiguration.getSourceClass();
        this.expressionProvider = new JBeansExpressionProvider(builderConfiguration);
        this.methodName = "build";
        this.selectedFields = builderConfiguration.getSelectedFields();
    }

    public BuildMethodBuilderConfiguration withFields(List<FieldConfiguration> selectedFields) {
        if (selectedFields != null) {
            this.selectedFields = selectedFields;
        }
        return this;
    }

    public BuildMethodBuilderConfiguration withExpressionProvider(ExpressionProvider expressionProvider) {
        if (expressionProvider != null) {
            this.expressionProvider = expressionProvider;
        }
        return this;
    }

    public BuildMethodBuilderConfiguration withMethodName(String methodName) {
        if (methodName != null) {
            this.methodName = methodName;
        }
        return this;
    }

    public List<FieldConfiguration> getSelectedFields() {
        return selectedFields;
    }

    public ExpressionProvider getExpressionProvider() {
        return expressionProvider;
    }

    public String getMethodName() {
        return methodName;
    }

    public Project getProject() {
        return project;
    }

    public PsiClass getSourceClass() {
        return sourceClass;
    }
}
