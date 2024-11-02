package by.shareiko.testutils.service.generation;

import com.intellij.psi.PsiField;

public interface ExpressionProvider {
    /**
     * Returns an expression for declaring a return variable
     *
     * @return InitializationExpression
     */
    DeclarationExpression getDeclarationExpression();

    /**
     * Returns an expression for setting the value for a specified field or null if there are no setter
     *
     * @param fieldName field to get expression for
     * @return SetterExpression
     */
    SetterExpression getSetterForField(PsiField fieldName);
}
