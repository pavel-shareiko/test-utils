package by.shareiko.testutils.properties;

import com.intellij.psi.PsiElement;

public interface ElementDecorator<T extends PsiElement> {
    void decorate(T psiClass);
}
