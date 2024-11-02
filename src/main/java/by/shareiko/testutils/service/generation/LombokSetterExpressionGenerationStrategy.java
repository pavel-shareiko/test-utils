package by.shareiko.testutils.service.generation;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class LombokSetterExpressionGenerationStrategy implements SetterExpressionGenerationStrategy {
    private static final Logger LOG = Logger.getInstance(LombokSetterExpressionGenerationStrategy.class);

    @Override
    public SetterExpression getSetterExpression(PsiField field) {
        PsiClass containingClass = field.getContainingClass();
        if (containingClass == null) {
            LOG.warn("Cannot generate setter expression for field '%s' because it has no containing class".formatted(field.getName()));
            return null;
        }

        PsiAnnotation dataAnnotation = containingClass.getAnnotation(SetterExpressionGenerationFactory.LOMBOK_DATA);
        PsiAnnotation setterAnnotation = Optional.ofNullable(containingClass.getAnnotation(SetterExpressionGenerationFactory.LOMBOK_SETTER))
                        .orElseGet(() -> field.getAnnotation(SetterExpressionGenerationFactory.LOMBOK_SETTER));

        if (canUseSetterAnnotation(setterAnnotation) || canUseDataAnnotation(dataAnnotation)) {
            return buildSetterExpression(field);
        }

        LOG.warn("Cannot generate setter expression for field '%s' because it has neither @lombok.Setter nor @lombok.Data annotations".formatted(field.getName()));
        return null;
    }

    private static boolean canUseDataAnnotation(PsiAnnotation dataAnnotation) {
        return dataAnnotation != null;
    }

    private boolean canUseSetterAnnotation(PsiAnnotation setterAnnotation) {
        return setterAnnotation != null &&
                !"PRIVATE".equals(AnnotationUtil.getStringAttributeValue(setterAnnotation, "value"));
    }

    private SetterExpression buildSetterExpression(PsiField field) {
        return new SetterExpressionImpl(false, buildSetExpressionString(field));
    }

    private static @NotNull String buildSetExpressionString(PsiField field) {
        return "set" + StringUtils.capitalise(field.getName());
    }
}
