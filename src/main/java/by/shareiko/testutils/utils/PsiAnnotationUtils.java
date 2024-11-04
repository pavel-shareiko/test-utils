package by.shareiko.testutils.utils;

import com.intellij.psi.*;

public class PsiAnnotationUtils {
    public static void addAnnotationToElement(PsiElement psiElement, String annotationText) {
        PsiElementFactory elementFactory = JavaPsiFacade.getInstance(psiElement.getProject()).getElementFactory();
        PsiAnnotation annotation = elementFactory.createAnnotationFromText(annotationText, psiElement);
        if (psiElement instanceof PsiModifierListOwner listOwner) {
            PsiModifierList modifierList = listOwner.getModifierList();
            if (modifierList != null) {
                modifierList.addBefore(annotation, modifierList.getFirstChild());
            } else {
                psiElement.addBefore(annotation, null);
            }
        } else {
            psiElement.addBefore(annotation, null);
        }
    }
}
