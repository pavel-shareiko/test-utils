package by.shareiko.testutils.service.generation;

import com.intellij.psi.PsiField;

public interface SetterExpressionGenerationStrategy {
    /**
     * Generates setter expression for a field
     *
     * @param field field to generate expression for
     * @return setter expression
     */
    SetterExpression getSetterExpression(PsiField field);
}
