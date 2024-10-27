package by.shareiko.testutils.extension;

import by.shareiko.testutils.properties.TestDataBuilderConfiguration;
import by.shareiko.testutils.service.BuilderGenerationService;
import by.shareiko.testutils.ui.dialog.GenerateBuilderDialog;
import by.shareiko.testutils.utils.PsiUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class TestDataBuilderGenerationAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(CommonDataKeys.EDITOR);

        if (project == null || psiFile == null || editor == null) {
            return;
        }

        PsiClass selectedClass = PsiUtils.getSelectedClass(psiFile, editor);
        if (selectedClass == null) {
            return;
        }
        if (selectedClass.isInterface() || selectedClass.getFields().length == 0) {
            return;
        }

        GenerateBuilderDialog dialog = showBuilderGenerationDialog(project, selectedClass);
        if (!dialog.isOK()) {
            return;
        }

        TestDataBuilderConfiguration builderConfiguration = dialog.getBuilderConfiguration();
        var generationService = ApplicationManager.getApplication().getService(BuilderGenerationService.class);

        generationService.generateBuilder(project, builderConfiguration);
    }

    private static GenerateBuilderDialog showBuilderGenerationDialog(Project project, PsiClass selectedClass) {
        GenerateBuilderDialog generateBuilderDialog = new GenerateBuilderDialog(project, selectedClass);
        generateBuilderDialog.show();

        return generateBuilderDialog;
    }
}
