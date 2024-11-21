package by.shareiko.testutils.ui.components;

import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class SelectableFieldComponent extends JBPanel<SelectableFieldComponent> {
    private final JBCheckBox checkBox;
    private final PsiField psiField;
    private final JBLabel fieldLabel;
    private final JBLabel typeLabel;
    private boolean selected = true;
    private boolean active = false;

    public SelectableFieldComponent(PsiField psiField) {
        this.psiField = psiField;

        // Panel configuration
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        setBorder(JBUI.Borders.empty(5));

        // Create components
        checkBox = createCheckBox();

        // Display field information
        fieldLabel = new JBLabel(psiField.getName());
        typeLabel = createTypeLabel(psiField, fieldLabel);

        // Add components to the panel
        super.add(checkBox);
        super.add(fieldLabel);
        super.add(typeLabel);
    }

    public void deselect() {
        setActive(active = false);
    }

    public void select() {
        setActive(active = true);
    }

    public void toggleSelection() {
        setActive(active = !active);
    }

    private void setActive(boolean isActive) {
        var backgroundColor = getBackgroundColor(isActive);
        this.setBackground(backgroundColor);

        // manually set background color for checkbox
        this.checkBox.setBackground(backgroundColor);
    }

    public void addSelectionListener(Consumer<PsiField> listener) {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                listener.accept(psiField);
            }
        });
    }

    private Color getBackgroundColor(boolean isActive) {
        return isActive
                ? UIManager.getColor("List.selectionBackground")
                : UIManager.getColor("List.background");
    }

    private @NotNull JBCheckBox createCheckBox() {
        JBCheckBox newCheckBox = new JBCheckBox();
        newCheckBox.setSelected(selected);

        newCheckBox.addActionListener(e -> selected = checkBox.isSelected());

        return newCheckBox;
    }

    private @NotNull JBLabel createTypeLabel(PsiField psiField, JBLabel fieldLabel) {
        PsiType fieldType = psiField.getType();
        JBLabel typeLabel = new JBLabel(": " + fieldType.getPresentableText());
        typeLabel.setForeground(JBColor.GRAY);
        fieldLabel.setIcon(getFieldIcon());
        return typeLabel;
    }

    private @NotNull Icon getFieldIcon() {
        return isJavaLibraryType(psiField.getType())
                ? IconLoader.getIcon("/nodes/field.svg", getClass())
                : IconLoader.getIcon("/nodes/type.svg", getClass());
    }

    private boolean isJavaLibraryType(PsiType type) {
        // Check if the type is a primitive type
        if (type instanceof PsiPrimitiveType) {
            return true;
        }

        String typeQualifiedName = type.getCanonicalText();
        return typeQualifiedName.startsWith("java.");
    }

    public PsiField getPsiField() {
        return psiField;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isActive() {
        return active;
    }

    public JBCheckBox getCheckBox() {
        return checkBox;
    }
}
