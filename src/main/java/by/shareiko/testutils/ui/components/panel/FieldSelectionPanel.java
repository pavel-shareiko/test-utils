package by.shareiko.testutils.ui.components.panel;

import by.shareiko.testutils.ui.components.SelectableFieldComponent;
import by.shareiko.testutils.utils.PsiFieldUtils;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class FieldSelectionPanel extends JBPanel<FieldSelectionPanel> {
    private final PsiField[] psiFields;
    private final Map<PsiField, SelectableFieldComponent> fieldList;
    private PsiField activeField;

    public FieldSelectionPanel(PsiClass sourceClass) {
        psiFields = PsiFieldUtils.getWritableFields(sourceClass);
        fieldList = new LinkedHashMap<>();

        setLayout(new GridBagLayout());
    }

    public void init() {
        createFieldComponents();
        registerDefaultSelectionListener();
    }

    private void createFieldComponents() {
        var gbConstraints = new GridBagConstraints();
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.anchor = GridBagConstraints.NORTHWEST;
        gbConstraints.insets = JBUI.emptyInsets();
        gbConstraints.gridy = 0;
        gbConstraints.gridx = 0;
        gbConstraints.weightx = 1;
        gbConstraints.weighty = 0;

        for (var psiField : psiFields) {
            var fieldComponent = new SelectableFieldComponent(psiField);
            this.fieldList.put(psiField, fieldComponent);
            this.add(fieldComponent, gbConstraints);
            gbConstraints.gridy++;
        }

        addFillerComponent(gbConstraints);
    }

    private void addFillerComponent(GridBagConstraints gbConstraints) {
        gbConstraints.weighty = 1;
        JPanel filler = new JPanel();
        filler.setPreferredSize(new Dimension(0, 0));
        add(filler, gbConstraints);
    }

    private void registerDefaultSelectionListener() {
        addSelectionListener(f -> {
            if (activeField != null && !activeField.equals(f)) {
                fieldList.get(activeField).deselect();
            }

            activeField = f;
            fieldList.get(activeField).select();
        });
    }

    public PsiField[] getPsiFields() {
        return psiFields;
    }

    public void addSelectionListener(Consumer<PsiField> listener) {
        this.fieldList.values().forEach(f -> f.addSelectionListener(listener));
    }

    public List<PsiField> getSelectedFields() {
        return this.fieldList.values().stream()
                .filter(SelectableFieldComponent::isSelected)
                .map(SelectableFieldComponent::getPsiField)
                .toList();
    }
}

