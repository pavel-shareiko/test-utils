package by.shareiko.testutils.ui.components;

import by.shareiko.testutils.properties.ElementDecorator;
import com.intellij.psi.PsiElement;

import javax.swing.*;

public interface ElementDecoratorComponentProvider<T extends PsiElement> {
    boolean isRequested();

    ElementDecorator<T> getDecorator();

    JComponent getComponent();

    default boolean isMultiline() {
        return false;
    }
}
