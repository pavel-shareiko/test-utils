package by.shareiko.testutils.service.generation;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.*;
import org.codehaus.plexus.util.StringUtils;

public class JavaBeansSetterExpressionGenerationStrategy implements SetterExpressionGenerationStrategy {
    private static final Logger LOG = Logger.getInstance(LombokSetterExpressionGenerationStrategy.class);

    @Override
    public SetterExpression getSetterExpression(PsiField field) {
        PsiClass containingClass = field.getContainingClass();
        String fieldName = field.getName();
        if (containingClass == null) {
            LOG.warn("Cannot generate setter expression for field '%s' because it has no containing class".formatted(fieldName));
            return null;
        }


        PsiMethod[] jBeansSetters = containingClass.findMethodsByName(buildJBeansSetterMethodName(fieldName), true);
        if (jBeansSetters.length > 0) {
            PsiMethod setterPsiMethod = jBeansSetters[0];
            if (canBeSetterMethod(setterPsiMethod, field)) {
                PsiType returnType = setterPsiMethod.getReturnType();
                return new SetterExpressionImpl(canBeChained(field, returnType), setterPsiMethod.getName());
            }
        }

        PsiMethod[] possibleFluentSetters = containingClass.findMethodsByName(fieldName, true);
        if (possibleFluentSetters.length > 0) {
            PsiMethod setterPsiMethod = possibleFluentSetters[0];
            if (canBeSetterMethod(setterPsiMethod, field)) {
                PsiType returnType = setterPsiMethod.getReturnType();
                return new SetterExpressionImpl(canBeChained(field, returnType), setterPsiMethod.getName());
            }
        }

        return null;
    }

    private static boolean canBeSetterMethod(PsiMethod setterPsiMethod, PsiField field) {
        if (setterPsiMethod.getParameterList().getParametersCount() != 1) {
            return false;
        }

        PsiParameter parameter = setterPsiMethod.getParameterList().getParameters()[0];
        return field.getType().isAssignableFrom(parameter.getType());
    }

    private static boolean canBeChained(PsiField field, PsiType returnType) {
        return returnType != null && returnType.isAssignableFrom(field.getType());
    }

    private String buildJBeansSetterMethodName(String fieldName) {
        return "set" + StringUtils.capitalise(fieldName);
    }
}
