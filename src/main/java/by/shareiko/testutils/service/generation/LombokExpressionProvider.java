package by.shareiko.testutils.service.generation;

import by.shareiko.testutils.properties.TestDataBuilderConfiguration;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import org.jetbrains.annotations.NotNull;

public class LombokExpressionProvider implements ExpressionProvider {
    private static final Logger LOG = Logger.getInstance(LombokExpressionProvider.class);

    private final Project project;
    private final TestDataBuilderConfiguration config;
    private final JBeansExpressionProvider delegate;

    public LombokExpressionProvider(Project project, TestDataBuilderConfiguration config) {
        this.project = project;
        this.config = config;

        this.delegate = new JBeansExpressionProvider(config);
    }

    @Override
    public DeclarationExpression getDeclarationExpression() {
        PsiAnnotation builderAnnotation = getLombokBuilderAnnotation(config.getSourceClass());
        if (builderAnnotation != null && canUseBuilder(builderAnnotation)) {
            return createDeclarationForLombokBuilder(builderAnnotation);
        }
        return delegate.getDeclarationExpression();
    }

    @Override
    public SetterExpression getSetterForField(PsiField field) {
        var strategy = SetterExpressionGenerationFactory.getInstance(
                project, config.getConfigurationForPsiField(field.getName()).getPsiField());
        if (strategy == null) {
            return delegate.getSetterForField(field);
        }

        LOG.info("Using '%s' to generate setter for field '%s'".formatted(strategy.getClass().getName(),
                field.getName()));
        return strategy.getSetterExpression(field);
    }

    private boolean canUseBuilder(PsiAnnotation builderAnnotation) {
        return !"PRIVATE".equals(AnnotationUtil.getStringAttributeValue(builderAnnotation, "access"));
    }

    private @NotNull DeclarationExpressionImpl createDeclarationForLombokBuilder(PsiAnnotation builderAnnotation) {
        String builderMethodName = AnnotationUtil.getStringAttributeValue(builderAnnotation, "builderMethodName");
        String buildMethodName = AnnotationUtil.getStringAttributeValue(builderAnnotation, "buildMethodName");

        return new DeclarationExpressionImpl(config.getSourceClass().getName() + "." + builderMethodName + "()", null,
                true, buildMethodName + "()");
    }

    private PsiAnnotation getLombokBuilderAnnotation(PsiClass clazz) {
        return clazz.getAnnotation("lombok.Builder");
    }
}
