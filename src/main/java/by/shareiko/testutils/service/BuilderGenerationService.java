package by.shareiko.testutils.service;

import by.shareiko.testutils.properties.TestDataBuilderConfiguration;
import by.shareiko.testutils.service.generation.TestDataBuilderGenerator;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.Nullable;

@Service
public final class BuilderGenerationService {

    public static final String ACTION_GROUP_ID = "TDBGeneration";

    public void generateBuilder(Project project, TestDataBuilderConfiguration config) {
        PsiFile existingFile = findExistingBuilderFile(project, config);
        if (existingFile != null) {
            int choice = Messages.showYesNoDialog(
                    project,
                    "Class file " + config.getBuilderName() + " already exists. Overwrite it?",
                    "File Already Exists",
                    "Delete and Regenerate",
                    "Cancel",
                    Messages.getQuestionIcon()
            );

            if (choice == Messages.YES) {
                deleteFile(project, existingFile);
            } else {
                return;
            }
        }

        WriteCommandAction.runWriteCommandAction(
                project,
                "GenerateTestDataBuilder",
                ACTION_GROUP_ID,
                () -> doGenerateBuilder(project, config)
        );
    }

    private static void deleteFile(Project project, PsiFile classFile) {
        WriteCommandAction.runWriteCommandAction(
                project,
                "DeleteOldImplementation",
                ACTION_GROUP_ID,
                classFile::delete
        );
    }

    private void doGenerateBuilder(Project project, TestDataBuilderConfiguration config) {
        PsiDirectory sourceRootDir = PsiManager.getInstance(project).findDirectory(config.getSourceRoot());
        if (sourceRootDir == null) {
            throw new IllegalArgumentException("Source root directory not found.");
        }

        PsiDirectory targetDir = createOrFindPackageDirectory(sourceRootDir, config.getDestinationPackage(), true);

        TestDataBuilderGenerator generator = new TestDataBuilderGenerator(project, targetDir, config);
        PsiClass builderClass = generator.generate();

        navigateToClass(project, builderClass);
    }

    private static @Nullable PsiFile findExistingBuilderFile(Project project, TestDataBuilderConfiguration config) {
        PsiDirectory sourceRootDir = PsiManager.getInstance(project).findDirectory(config.getSourceRoot());
        if (sourceRootDir == null) {
            return null;
        }

        PsiDirectory targetDir = createOrFindPackageDirectory(sourceRootDir, config.getDestinationPackage(), false);
        if (targetDir == null) {
            return null;
        }
        String expectedFileName = config.getBuilderName() + ".java";
        return targetDir.findFile(expectedFileName);
    }

    private static PsiDirectory createOrFindPackageDirectory(
            PsiDirectory sourceRootDir,
            String destinationPackage,
            boolean createSubdirectories
    ) {
        String[] packagePath = destinationPackage.split("\\.");
        PsiDirectory currentDir = sourceRootDir;

        for (String packagePart : packagePath) {
            PsiDirectory subDir = currentDir.findSubdirectory(packagePart);
            if (subDir == null) {
                if (!createSubdirectories) {
                    return null;
                }
                subDir = currentDir.createSubdirectory(packagePart);
            }
            currentDir = subDir;
        }
        return currentDir;
    }

    private static void navigateToClass(Project project, PsiClass builderClass) {
        VirtualFile file = builderClass.getContainingFile().getVirtualFile();
        if (file != null) {
            FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, file), true);
        }
    }
}
