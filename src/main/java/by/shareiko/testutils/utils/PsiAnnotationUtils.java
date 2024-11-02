package by.shareiko.testutils.utils;

import com.intellij.psi.*;

public class PsiAnnotationUtils {
    public static void addAnnotationToElement(PsiElement psiElement, String annotationText) {
        PsiElementFactory elementFactory = JavaPsiFacade.getInstance(psiElement.getProject()).getElementFactory();
        PsiAnnotation annotation = elementFactory.createAnnotationFromText(annotationText, psiElement);
        if (psiElement instanceof PsiMethod psiMethod) {
            PsiModifierList modifierList = psiMethod.getModifierList();
            modifierList.addBefore(annotation, modifierList.getFirstChild());
        } else {
            psiElement.addBefore(annotation, null);
        }
    }
}
