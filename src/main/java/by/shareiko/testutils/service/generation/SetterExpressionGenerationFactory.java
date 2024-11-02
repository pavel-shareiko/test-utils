package by.shareiko.testutils.service.generation;

import by.shareiko.testutils.utils.PsiModuleUtils;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;

public class SetterExpressionGenerationFactory {

    public static final String LOMBOK_BUILDER = "lombok.Builder";
    public static final String LOMBOK_SETTER = "lombok.Setter";
    public static final String LOMBOK_DATA = "lombok.Data";

    public static SetterExpressionGenerationStrategy getInstance(Project project, PsiField psiField) {
        PsiClass containingClass = psiField.getContainingClass();
        if (containingClass == null) {
            return null;
        }

        if (hasLombokEnabled(project, psiField.getContainingFile())) {
            if (canUseLombokBuilder(containingClass)) {
                return new LombokBuilderSetterExpressionGenerationStrategy();
            }

            if (canUseLombokSetter(psiField, containingClass)) {
                return new LombokSetterExpressionGenerationStrategy();
            }
        }

        return new JavaBeansSetterExpressionGenerationStrategy();
    }

    private static boolean canUseLombokBuilder(PsiClass containingClass) {
        PsiAnnotation lombokBuilder = containingClass.getAnnotation(LOMBOK_BUILDER);
        if (lombokBuilder == null) {
            return false;
        }

        String access = AnnotationUtil.getStringAttributeValue(lombokBuilder, "access");
        return access == null || !access.equals("PRIVATE");
    }

    private static boolean canUseLombokSetter(PsiField psiField, PsiClass containingClass) {
        return (containingClass.hasAnnotation(LOMBOK_SETTER) || psiField.hasAnnotation(LOMBOK_SETTER))
                || containingClass.hasAnnotation(LOMBOK_DATA);
    }

    private static boolean hasLombokEnabled(Project project, PsiFile containingFile) {
        return PsiModuleUtils.isLombokEnabled(PsiModuleUtils.getFileModule(project, containingFile));
    }
}
