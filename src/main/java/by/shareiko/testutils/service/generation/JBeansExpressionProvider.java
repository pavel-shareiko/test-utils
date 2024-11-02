package by.shareiko.testutils.service.generation;

import by.shareiko.testutils.properties.TestDataBuilderConfiguration;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;

public class JBeansExpressionProvider implements ExpressionProvider {
    private final TestDataBuilderConfiguration config;

    public JBeansExpressionProvider(TestDataBuilderConfiguration config) {
        this.config = config;
    }

    @Override
    public DeclarationExpression getDeclarationExpression() {
        PsiClass sourceClass = config.getSourceClass();
        if (!hasNoArgsConstructor(sourceClass)) {
            return null;
        }
        return new DeclarationExpressionImpl(
                "new " + sourceClass.getName() + "()",
                new VariableInfo("result", sourceClass.getQualifiedName()),
                false,
                null
        );
    }

    private boolean hasNoArgsConstructor(PsiClass sourceClass) {
        PsiMethod[] constructors = sourceClass.getConstructors();
        if (constructors.length == 0) {
            return true;
        }
        for (var constructor : constructors) {
            if (!constructor.getModifierList().hasModifierProperty(PsiModifier.PRIVATE)
                    && constructor.getParameterList().getParametersCount() == 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public SetterExpression getSetterForField(PsiField fieldName) {
        return new JavaBeansSetterExpressionGenerationStrategy().getSetterExpression(fieldName);
    }
}
