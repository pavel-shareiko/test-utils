package by.shareiko.testutils.service.generation;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import org.codehaus.plexus.util.StringUtils;

public class LombokBuilderSetterExpressionGenerationStrategy implements SetterExpressionGenerationStrategy {
    private static final Logger LOG = Logger.getInstance(LombokBuilderSetterExpressionGenerationStrategy.class);

    @Override
    public SetterExpression getSetterExpression(PsiField field) {
        PsiClass containingClass = field.getContainingClass();
        if (containingClass == null) {
            LOG.warn("Cannot generate setter expression for field '%s' because it has no containing class"
                    .formatted(field.getName()));
            return null;
        }
        PsiAnnotation builderAnnotation = containingClass.getAnnotation(SetterExpressionGenerationFactory.LOMBOK_BUILDER);
        if (builderAnnotation == null) {
            LOG.warn("Cannot generate setter expression because containing class has no @lombok.Builder annotation");
            return null;
        }

        String setterPrefix = AnnotationUtil.getStringAttributeValue(builderAnnotation, "setterPrefix");
        if (setterPrefix == null || setterPrefix.isEmpty()) {
            return new SetterExpressionImpl(true, field.getName());
        }

        return new SetterExpressionImpl(true, setterPrefix + StringUtils.capitalise(field.getName()));
    }
}
