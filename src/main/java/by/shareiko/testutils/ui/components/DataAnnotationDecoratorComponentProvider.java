package by.shareiko.testutils.ui.components;

import by.shareiko.testutils.properties.AnnotationClassDecorator;
import by.shareiko.testutils.properties.ClassDecorator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;

public class DataAnnotationDecoratorComponentProvider
        extends JBPanel<DataAnnotationDecoratorComponentProvider>
        implements ClassDecoratorComponentProvider {
    private final JBCheckBox cbRequested;

    public DataAnnotationDecoratorComponentProvider() {
        this.cbRequested = new JBCheckBox("Add @Data", true);
        add(this.cbRequested);
    }

    @Override
    public boolean isRequested() {
        return cbRequested.isSelected();
    }

    @Override
    public ClassDecorator getDecorator() {
        return new AnnotationClassDecorator("@lombok.Data");
    }

    @Override
    public JComponent getComponent() {
        return this;
    }
}
