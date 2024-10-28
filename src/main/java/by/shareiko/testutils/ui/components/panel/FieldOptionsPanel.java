package by.shareiko.testutils.ui.components.panel;

import by.shareiko.testutils.ui.UIConstants;
import by.shareiko.testutils.ui.model.FieldConfiguration;
import com.intellij.openapi.project.Project;
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
    private final Project project;
    private final Map<PsiField, FieldConfiguration> fieldsConfiguration;

    private PsiField selectedField;
    private FieldConfiguration selectedFieldConfiguration;

    private LabeledComponent<JTextField> nameField;
    private LabeledComponent<JTextField> defaultValueField;
    private LabeledComponent<ComboBox<String>> accessLevelField;
    private boolean reconfigurationInProgress = false;

    public FieldOptionsPanel(FieldSelectionPanel fieldSelectionPanel, @NotNull Project project) {
        this.fieldSelectionPanel = fieldSelectionPanel;
        this.project = project;
        this.fieldsConfiguration = new HashMap<>();

        setLayout(new GridBagLayout());
    }

    public void init() {
        initFieldsConfiguration();

        var nameFieldComponent = new JTextField();
        nameFieldComponent.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                if (!reconfigurationInProgress) {
                    selectedFieldConfiguration.setFieldName(nameField.getComponent().getText());
                }
            }
        });

        var defaultValueFieldComponent = new JTextField();
        defaultValueFieldComponent.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                if (!reconfigurationInProgress) {
                    selectedFieldConfiguration.setDefaultValue(defaultValueField.getComponent().getText());
                }
            }
        });

        var accessLevelFieldComponent = new ComboBox<>(new String[]{"public", "protected", "private"});
        accessLevelFieldComponent.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED && !reconfigurationInProgress) {
                selectedFieldConfiguration.setAccessModifier(e.getItem().toString());
            }
        });

        nameField = this.addLabeledComponent(nameFieldComponent, "Field Name", 0);
        defaultValueField = this.addLabeledComponent(defaultValueFieldComponent, "Default Value", 1);
        accessLevelField = this.addLabeledComponent(accessLevelFieldComponent, "Field Access Level", 2);

        // filler component
        addFillerComponent(3);

        setComponentsVisibility(selectedField != null);
    }

    private void addFillerComponent(int gridy) {
        var gbConstraints = new GridBagConstraints();
        gbConstraints.insets = UIConstants.DEFAULT_INSETS;
        gbConstraints.gridy = gridy;
        gbConstraints.gridx = 0;
        gbConstraints.anchor = GridBagConstraints.NORTHWEST;
        gbConstraints.weightx = 0;
        gbConstraints.fill = GridBagConstraints.NONE;
        gbConstraints.weighty = 1;
        JPanel filler = new JPanel();
        filler.setPreferredSize(new Dimension(0, 0));
        super.add(filler, gbConstraints);
    }

    public FieldConfiguration getFieldConfiguration(PsiField selectedField) {
        return fieldsConfiguration.get(selectedField);
    }

    private <T extends JComponent> LabeledComponent<T> addLabeledComponent(T component,
                                                                           String labelText,
                                                                           int gridy) {
        var gbConstraints = new GridBagConstraints();
        gbConstraints.gridy = gridy;
        gbConstraints.gridx = 0;
        gbConstraints.weightx = 1;
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.anchor = GridBagConstraints.WEST;

        LabeledComponent<T> labeledComponent = LabeledComponent.create(component, labelText, BorderLayout.WEST);
        super.add(labeledComponent, gbConstraints);
        return labeledComponent;
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
        this.nameField.getComponent().setText(config.getFieldName());
        this.defaultValueField.getComponent().setText(config.getDefaultValue());
        this.accessLevelField.getComponent().setSelectedItem(config.getAccessModifier());

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
        fieldConfiguration.setPsiField(field);
        return fieldConfiguration;
    }
}

