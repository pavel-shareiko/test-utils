package by.shareiko.testutils.service.generation;

import by.shareiko.testutils.ui.model.FieldConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

public class FieldGeneratorImpl implements FieldGenerator {
    private final PsiElementFactory elementFactory;
    private final PsiClass targetClass;

    public FieldGeneratorImpl(Project project, PsiClass targetClass) {
        this.elementFactory = PsiElementFactory.getInstance(project);
        this.targetClass = targetClass;
    }

    @Override
    public PsiField generate(FieldConfiguration config) {
        String accessModifier = config.getAccessModifier();
        String fieldName = config.getFieldName();
        String defaultValue = config.getDefaultValue();
        PsiType type = config.getPsiField().getType();

        String fieldDeclaration = "%s %s %s;".formatted(accessModifier, type.getCanonicalText(), fieldName);

        PsiField field = elementFactory.createFieldFromText(fieldDeclaration, targetClass);

        PsiExpression initializer = getExpressionFromText(defaultValue);
        if (initializer != null) {
            field.setInitializer(initializer);
        }

        return field;
    }

    private PsiExpression getExpressionFromText(String initializer) {
        if (initializer == null || initializer.isEmpty()) {
            return null;
        }

        return elementFactory.createExpressionFromText(initializer, targetClass);
    }

}
