package by.shareiko.testutils.ui.components;

import by.shareiko.testutils.properties.ClassDecorator;
import com.intellij.psi.PsiClass;

public interface ClassDecoratorComponentProvider extends ElementDecoratorComponentProvider<PsiClass> {
    @Override
    ClassDecorator getDecorator();
}
