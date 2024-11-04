package by.shareiko.testutils.properties;

import by.shareiko.testutils.utils.PsiAnnotationUtils;
import com.intellij.psi.PsiClass;

public class AnnotationClassDecorator implements ClassDecorator {
    private final String annotation;

    public AnnotationClassDecorator(String annotation) {
        if (!annotation.startsWith("@")) {
            annotation = "@" + annotation;
        }
        this.annotation = annotation;
    }

    @Override
    public void decorate(PsiClass psiClass) {
        PsiAnnotationUtils.addAnnotationToElement(psiClass, annotation);
    }
}
