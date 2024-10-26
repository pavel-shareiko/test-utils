package by.shareiko.testutils.ui.components.panel;

import by.shareiko.testutils.ui.UIConstants;
import by.shareiko.testutils.ui.model.FieldConfiguration;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.PsiField;
import com.intellij.psi.util.AccessModifier;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.HashMap;
import java.util.Map;

public class FieldOptionsPanel extends JBPanel<FieldOptionsPanel> {
    private final FieldSelectionPanel fieldSelectionPanel;
    private final Map<PsiField, FieldConfiguration> fieldsConfiguration;

    private PsiField selectedField;
    private FieldConfiguration selectedFieldConfiguration;

    private LabeledComponent<JTextField> nameField = new LabeledComponent<>();
    private LabeledComponent<JTextField> defaultValueField = new LabeledComponent<>();
    private LabeledComponent<ComboBox<String>> accessLevelField = new LabeledComponent<>();
    private boolean reconfigurationInProgress = false;

    public FieldOptionsPanel(FieldSelectionPanel fieldSelectionPanel) {
        this.fieldSelectionPanel = fieldSelectionPanel;
        this.fieldsConfiguration = new HashMap<>();

        setLayout(new GridBagLayout());
    }

    public void init() {
        initFieldsConfiguration();

        this.nameField.component = new JTextField();
        this.nameField.component.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                if (!reconfigurationInProgress) {
                    selectedFieldConfiguration.setFieldName(nameField.component.getText());
                }
            }
        });

        this.defaultValueField.component = new JTextField();
        this.defaultValueField.component.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                if (!reconfigurationInProgress) {
                    selectedFieldConfiguration.setDefaultValue(defaultValueField.component.getText());
                }
            }
        });

        this.accessLevelField.component = new ComboBox<>(
                new String[]{"public", "protected", "private"}
        );
        this.accessLevelField.component.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED && !reconfigurationInProgress) {
                selectedFieldConfiguration.setAccessModifier(e.getItem().toString());
            }
        });

        nameField = this.addLabeledComponent(nameField.component, "Field Name: ", 0);
        defaultValueField = this.addLabeledComponent(defaultValueField.component, "Default Value: ", 1);
        accessLevelField = this.addLabeledComponent(accessLevelField.component, "Field Access Level: ", 2);

        // filler component
        addFillerComponent();

        setComponentsVisibility(selectedField != null);
    }

    private void addFillerComponent() {
        var gbConstraints = getGridBagConstraints(3);
        gbConstraints.weighty = 1;
        JPanel filler = new JPanel();
        filler.setPreferredSize(new Dimension(0, 0));
        add(filler, gbConstraints);
    }

    public FieldConfiguration getFieldConfiguration(PsiField selectedField) {
        return fieldsConfiguration.get(selectedField);

    }

    private <T extends JComponent> LabeledComponent<T> addLabeledComponent(T component,
                                                                           String labelText,
                                                                           int gridy) {
        var gbConstraints = getGridBagConstraints(gridy);

        gbConstraints.anchor = GridBagConstraints.WEST;
        JLabel label = new JLabel(labelText);
        super.add(label, gbConstraints);

        gbConstraints.anchor = GridBagConstraints.NORTHWEST;
        gbConstraints.gridx = 1;
        gbConstraints.weightx = 1;
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        super.add(component, gbConstraints);

        return new LabeledComponent<>(label, component);
    }

    private static @NotNull GridBagConstraints getGridBagConstraints(int gridy) {
        var gbConstraints = new GridBagConstraints();
        gbConstraints.insets = UIConstants.DEFAULT_INSETS;
        gbConstraints.gridy = gridy;
        gbConstraints.gridx = 0;
        gbConstraints.anchor = GridBagConstraints.NORTHWEST;
        gbConstraints.weightx = 0;
        gbConstraints.weighty = 0;
        gbConstraints.fill = GridBagConstraints.NONE;
        return gbConstraints;
    }

    private void initFieldsConfiguration() {
        for (PsiField psiField : fieldSelectionPanel.getPsiFields()) {
            this.fieldsConfiguration.put(psiField, getDefaultFieldConfiguration(psiField));
        }

        fieldSelectionPanel.addSelectionListener((selectedItem) -> {
            this.setVisible(selectedItem != null);

            this.selectedField = selectedItem;
            this.selectedFieldConfiguration = this.fieldsConfiguration.get(this.selectedField);

            this.reconfigurationInProgress = true;
            redrawConfigurationForField(selectedFieldConfiguration);
            this.reconfigurationInProgress = false;
        });
    }

    private void redrawConfigurationForField(FieldConfiguration config) {
        this.nameField.component.setText(config.getFieldName());
        this.defaultValueField.component.setText(config.getDefaultValue());
        this.accessLevelField.component.setSelectedItem(config.getAccessModifier());

        setComponentsVisibility(selectedField != null);
    }

    private void setComponentsVisibility(boolean isVisible) {
        this.nameField.setVisible(isVisible);
        this.defaultValueField.setVisible(isVisible);
        this.accessLevelField.setVisible(isVisible);
    }

    private static @NotNull FieldConfiguration getDefaultFieldConfiguration(PsiField field) {
        FieldConfiguration fieldConfiguration = new FieldConfiguration();
        fieldConfiguration.setFieldName(field.getName());
        fieldConfiguration.setDefaultValue(null);
        fieldConfiguration.setAccessModifier(AccessModifier.PRIVATE.toPsiModifier());
        return fieldConfiguration;
    }

    private static class LabeledComponent<T extends JComponent> {
        JLabel label;
        T component;

        public LabeledComponent(JLabel label, T component) {
            this.label = label;
            this.component = component;
        }

        public LabeledComponent() {
        }

        public void setVisible(boolean isVisible) {
            if (this.label != null)
                this.label.setVisible(isVisible);
            if (this.component != null)
                this.component.setVisible(isVisible);
        }
    }
}

