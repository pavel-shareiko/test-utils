package by.shareiko.testutils.ui.dialog;

import by.shareiko.testutils.properties.TestDataBuilderConfiguration;
import by.shareiko.testutils.ui.UIConstants;
import by.shareiko.testutils.ui.components.panel.FieldOptionsPanel;
import by.shareiko.testutils.ui.components.panel.FieldSelectionPanel;
import by.shareiko.testutils.ui.components.selector.SourceRootSelector;
import by.shareiko.testutils.ui.components.validator.BuilderInterfaceValidator;
import by.shareiko.testutils.ui.components.validator.ClassNameValidator;
import by.shareiko.testutils.ui.model.FieldConfiguration;
import by.shareiko.testutils.utils.PsiUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.JavaProjectRootsUtil;
import com.intellij.openapi.ui.ComponentValidator;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.ProjectScope;
import com.intellij.refactoring.ui.ClassNameReferenceEditor;
import com.intellij.refactoring.ui.PackageNameReferenceEditorCombo;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.JBSplitter;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class GenerateBuilderDialog extends DialogWrapper {
    public static final String DEFAULT_BUILDER_SUFFIX = "TestDataBuilder";
    public static final String DEFAULT_DIALOG_TITLE = "Generate Builder";

    private static final String RECENTS_KEY = "CreateTestDataBuilderDialog.RecentsKey";
    private static final String SPLITTER_PROPORTION_KEY = "CreateTestDataBuilderDialog.SplitterProportion";

    private final PsiClass sourceClass;
    private final Project project;
    private final JTextField targetClassNameField;
    private final PackageNameReferenceEditorCombo packageNameField;
    private final SourceRootSelector sourceRootSelector;
    private final ClassNameReferenceEditor builderInterfaceField;
    private final FieldSelectionPanel fieldSelectionPanel;
    private final FieldOptionsPanel fieldOptionsPanel;

    public GenerateBuilderDialog(@NotNull Project project, PsiClass sourceClass) {
        super(project);

        this.project = project;
        this.sourceClass = sourceClass;
        this.targetClassNameField = new JTextField(sourceClass.getName() + DEFAULT_BUILDER_SUFFIX);
        this.packageNameField = new PackageNameReferenceEditorCombo(
                PsiUtils.getPackageName(sourceClass),
                project,
                RECENTS_KEY,
                "Select Destination Package"
        );
        this.fieldSelectionPanel = new FieldSelectionPanel(sourceClass);
        this.fieldOptionsPanel = new FieldOptionsPanel(fieldSelectionPanel, project);

        // TODO: use RecentsManager for this
        this.builderInterfaceField = new ClassNameReferenceEditor(project, null,
                JavaProjectRootsUtil.getScopeWithoutGeneratedSources(ProjectScope.getProjectScope(project), project));
        this.sourceRootSelector = new SourceRootSelector(project);

        setTitle(DEFAULT_DIALOG_TITLE);
    }

    @Override
    public void show() {
        super.init();
        super.show();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setPreferredSize(new Dimension(800, 600));
        GridBagConstraints gbConstraints = new GridBagConstraints();
        mainPanel.setBorder(IdeBorderFactory.createBorder());

        /* Builder name */
        gbConstraints.insets = UIConstants.DEFAULT_INSETS;
        gbConstraints.gridx = 0;
        gbConstraints.gridy = 0;
        gbConstraints.weightx = 0;
        gbConstraints.gridwidth = 1;
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.anchor = GridBagConstraints.WEST;
        mainPanel.add(new JLabel("Builder name"), gbConstraints);

        gbConstraints.gridx = 1;
        gbConstraints.weightx = 1;
        gbConstraints.gridwidth = 1;
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.anchor = GridBagConstraints.WEST;
        mainPanel.add(targetClassNameField, gbConstraints);
        /* Builder name */

        /* Destination package */
        gbConstraints.gridx = 0;
        gbConstraints.gridy = 1;
        gbConstraints.weightx = 0;
        gbConstraints.gridwidth = 1;
        mainPanel.add(new JLabel("Destination package"), gbConstraints);

        gbConstraints.gridx = 1;
        gbConstraints.weightx = 1;
        mainPanel.add(packageNameField, gbConstraints);
        /* Destination package */

        /* Test Source Root */
        gbConstraints.gridx = 0;
        gbConstraints.gridy = 2;
        mainPanel.add(new JLabel("Test Source Root"), gbConstraints);

        gbConstraints.gridx = 1;
        mainPanel.add(sourceRootSelector, gbConstraints);
        /* Test Source Root */

        /* Builder Interface */
        gbConstraints.gridx = 0;
        gbConstraints.gridy = 3;
        mainPanel.add(new JLabel("Test Data Builder Interface"), gbConstraints);

        gbConstraints.gridx = 1;
        mainPanel.add(builderInterfaceField, gbConstraints);
        /* Builder interface */

        /* Fields selection */
        gbConstraints.gridy = 4;
        gbConstraints.gridx = 0;
        gbConstraints.fill = GridBagConstraints.BOTH;
        gbConstraints.weighty = 1;
        gbConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gbConstraints.gridheight = GridBagConstraints.REMAINDER;

        JBSplitter splitter = new JBSplitter(false, SPLITTER_PROPORTION_KEY, 0.5f);

        fieldSelectionPanel.init();
        splitter.setFirstComponent(fieldSelectionPanel);

        fieldOptionsPanel.init();
        splitter.setSecondComponent(fieldOptionsPanel);

        splitter.setBorder(JBUI.Borders.empty());
        fieldSelectionPanel.setBorder(IdeBorderFactory.createBorder());

        mainPanel.add(splitter, gbConstraints);
        /* Fields selection */

        addValidation();
        return mainPanel;
    }

    private void addValidation() {
        new ComponentValidator(getDisposable())
                .withValidator(new ClassNameValidator(targetClassNameField))
                .installOn(targetClassNameField);

        new ComponentValidator(getDisposable())
                .withValidator(new BuilderInterfaceValidator(builderInterfaceField, project))
                .withOutlineProvider(ComponentValidator.CWBB_PROVIDER)
                .installOn(builderInterfaceField);
    }

    @Override
    protected @NotNull List<ValidationInfo> doValidateAll() {
        List<ValidationInfo> validationErrors = super.doValidateAll();
        validationErrors.addAll(
                Stream.of(targetClassNameField, packageNameField, sourceRootSelector, builderInterfaceField)
                        .map(ComponentValidator::getInstance)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .peek(ComponentValidator::revalidate)
                        .map(ComponentValidator::getValidationInfo)
                        .filter(Objects::nonNull)
                        .peek(vi -> {
                            if (!vi.warning) {
                                vi.okEnabled = false;
                            }
                        })
                        .toList());

        return validationErrors;
    }

    public TestDataBuilderConfiguration getBuilderConfiguration() {
        return TestDataBuilderConfiguration.builder()
                .withSourceClass(sourceClass)
                .withBuilderName(targetClassNameField.getText().trim())
                .withDestinationPackage(packageNameField.getText().trim())
                .withSourceRoot((VirtualFile) sourceRootSelector.getSelectedItem())
                .withBaseInterface(PsiUtils.getClassFromProject(builderInterfaceField.getText(), project))
                .withSelectedFields(getSelectedFieldsConfiguration())
                .build();
    }

    private List<FieldConfiguration> getSelectedFieldsConfiguration() {
        List<FieldConfiguration> selectedFields = new ArrayList<>();

        for (var selectedField : fieldSelectionPanel.getSelectedFields()) {
            FieldConfiguration fieldConfiguration = fieldOptionsPanel.getFieldConfiguration(selectedField);
            selectedFields.add(fieldConfiguration);
        }

        return selectedFields;
    }

}
