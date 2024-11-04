package by.shareiko.testutils.ui.components;

import by.shareiko.testutils.properties.AnnotationClassDecorator;
import by.shareiko.testutils.properties.ClassDecorator;
import com.intellij.ui.components.JBCheckBox;

import javax.swing.*;

public class WithAnnotationDecoratorComponentProvider extends JPanel implements ClassDecoratorComponentProvider {
    private final JBCheckBox cbRequested;

    public WithAnnotationDecoratorComponentProvider() {
        this.cbRequested = new JBCheckBox("Add @With", true);
        add(this.cbRequested);
    }

    @Override
    public boolean isRequested() {
        return cbRequested.isSelected();
    }

    @Override
    public ClassDecorator getDecorator() {
        return new AnnotationClassDecorator("@lombok.With");
    }

    @Override
    public JComponent getComponent() {
        return this;
    }
}
