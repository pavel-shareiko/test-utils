package by.shareiko.testutils.ui.components;

import by.shareiko.testutils.properties.AnnotationClassDecorator;
import by.shareiko.testutils.properties.ClassDecorator;
import by.shareiko.testutils.ui.utils.VisibilityManager;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class NoArgsConstructorAnnotationDecoratorComponentProvider
        extends JBPanel<NoArgsConstructorAnnotationDecoratorComponentProvider>
        implements ClassDecoratorComponentProvider {

    private static final String NO_ARGS_CONSTRUCTOR = "@lombok.NoArgsConstructor";

    private final JBCheckBox cbRequested;
    private final JBCheckBox cbUseStaticConstructor;
    private final JBTextField tfStaticConstructor;

    public NoArgsConstructorAnnotationDecoratorComponentProvider(String defaultStaticConstructorValue) {
        this.cbRequested = new JBCheckBox("Add @NoArgsConstructor", true);
        this.cbUseStaticConstructor = new JBCheckBox("Use static constructor", true);
        this.tfStaticConstructor = new JBTextField(defaultStaticConstructorValue);

        setLayout(new GridBagLayout());

        var gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = JBUI.insets(5);
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;

        gbc.gridy = 0;
        add(this.cbRequested, gbc);

        gbc.gridy = 1;
        gbc.insets = JBUI.insets(5, 25, 5, 5);
        add(this.cbUseStaticConstructor, gbc);

        gbc.gridy = 2;
        gbc.insets = JBUI.insets(1, 25, 5, 5);
        this.tfStaticConstructor.setColumns(15);
        add(this.tfStaticConstructor, gbc);

        VisibilityManager.create(this.cbRequested)
                .withChild(this.cbUseStaticConstructor)
                .withChild(this.tfStaticConstructor)
                .install();
    }

    @Override
    public boolean isRequested() {
        return cbRequested.isSelected();
    }

    @Override
    public ClassDecorator getDecorator() {
        return new AnnotationClassDecorator(buildAnnotation());
    }

    private @NotNull String buildAnnotation() {
        if (cbUseStaticConstructor.isSelected() && !tfStaticConstructor.getText().isEmpty()) {
            return NO_ARGS_CONSTRUCTOR + ("(staticName=\"%s\")".formatted(tfStaticConstructor.getText()));
        }
        return NO_ARGS_CONSTRUCTOR;
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public boolean isMultiline() {
        return true;
    }
}
