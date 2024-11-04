package by.shareiko.testutils.ui.dialog;

import by.shareiko.testutils.properties.TestDataBuilderConfiguration;
import by.shareiko.testutils.ui.components.panel.FieldOptionsPanel;
import by.shareiko.testutils.ui.components.panel.FieldSelectionPanel;
import by.shareiko.testutils.ui.components.panel.LombokDecoratorsPanel;
import by.shareiko.testutils.ui.components.selector.SourceRootSelector;
import by.shareiko.testutils.ui.components.validator.BuilderInterfaceValidator;
import by.shareiko.testutils.ui.components.validator.ClassNameValidator;
import by.shareiko.testutils.ui.model.FieldConfiguration;
import by.shareiko.testutils.utils.PsiModuleUtils;
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
import com.intellij.ui.TitledSeparator;
import com.intellij.util.ui.JBInsets;
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
    public static final JBInsets SEPARATOR_INSETS = JBUI.insets(4, 8);
    public static final JBInsets SECTION_INSETS = JBUI.insets(4, 16);

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
    private final LombokDecoratorsPanel lombokDecoratorsPanel;

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
        this.lombokDecoratorsPanel = new LombokDecoratorsPanel(sourceClass);

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
        GridBagConstraints gbc = new GridBagConstraints();
        mainPanel.setBorder(IdeBorderFactory.createBorder());

        /* Builder name */
        gbc.insets = SECTION_INSETS;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(new JLabel("Builder name"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(targetClassNameField, gbc);
        /* Builder name */

        /* Destination package */
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        mainPanel.add(new JLabel("Destination package"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        mainPanel.add(packageNameField, gbc);
        /* Destination package */

        /* Test Source Root */
        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Test Source Root"), gbc);

        gbc.gridx = 1;
        mainPanel.add(sourceRootSelector, gbc);
        /* Test Source Root */

        /* Builder Interface */
        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Test Data Builder Interface"), gbc);

        gbc.gridx = 1;
        mainPanel.add(builderInterfaceField, gbc);
        /* Builder interface */

        /* Decorators */
        if (PsiModuleUtils.isLombokEnabled(PsiModuleUtils.getFileModule(sourceClass))) {
            gbc.insets = SEPARATOR_INSETS;
            gbc.gridy++;
            gbc.gridx = 0;
            gbc.gridwidth = 2;
            mainPanel.add(new TitledSeparator("Lombok Decorators", lombokDecoratorsPanel), gbc);

            gbc.insets = SECTION_INSETS;
            gbc.gridy++;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            mainPanel.add(lombokDecoratorsPanel, gbc);
        }
        /* Decorators */

        /* Fields selection */
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = GridBagConstraints.REMAINDER;

        JBSplitter splitter = new JBSplitter(false, SPLITTER_PROPORTION_KEY, 0.5f);

        fieldSelectionPanel.init();
        splitter.setFirstComponent(fieldSelectionPanel);

        fieldOptionsPanel.init();
        splitter.setSecondComponent(fieldOptionsPanel);

        splitter.setBorder(JBUI.Borders.empty());
        fieldSelectionPanel.setBorder(IdeBorderFactory.createBorder());

        mainPanel.add(splitter, gbc);
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
                .withDecorators(PsiModuleUtils.isLombokEnabled(PsiModuleUtils.getFileModule(sourceClass))
                        ? lombokDecoratorsPanel.getDecorators()
                        : new ArrayList<>())
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
