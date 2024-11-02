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
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.Nullable;

@Service
public final class BuilderGenerationService {

    public static final String ACTION_GROUP_ID = "TDBGeneration";

    public void generateBuilder(Project project, TestDataBuilderConfiguration config) {
        PsiClass existingBuilderClass = findExistingBuilderClass(project, config);

        if (existingBuilderClass != null && existingBuilderClass.getContainingFile() != null) {
            int choice = Messages.showYesNoDialog(
                    project,
                    "Class " + config.getBuilderName() + " already exists. Overwrite it?",
                    "Class Already Exists",
                    "Delete and Regenerate",
                    "Cancel",
                    Messages.getQuestionIcon()
            );

            if (choice == Messages.YES) {
                deleteClassFile(project, existingBuilderClass);
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

    private static void deleteClassFile(Project project, PsiClass classFile) {
        WriteCommandAction.runWriteCommandAction(
                project,
                "DeleteOldImplementation",
                ACTION_GROUP_ID,
                () -> classFile.getContainingFile().delete()
        );
    }

    private void doGenerateBuilder(Project project, TestDataBuilderConfiguration config) {
        // Ensure the source root and package directory are available
        PsiDirectory sourceRootDir = PsiManager.getInstance(project).findDirectory(config.getSourceRoot());
        if (sourceRootDir == null) {
            throw new IllegalArgumentException("Source root directory not found.");
        }

        PsiDirectory targetDir = createOrFindPackageDirectory(sourceRootDir, config.getDestinationPackage());

        // Create the class file with builder implementation
        TestDataBuilderGenerator generator = new TestDataBuilderGenerator(project, targetDir, config);
        PsiClass builderClass = generator.generate();

        // Open the generated class in the editor
        navigateToClass(project, builderClass);
    }

    private static @Nullable PsiClass findExistingBuilderClass(Project project, TestDataBuilderConfiguration config) {
        return JavaPsiFacade.getInstance(project).findClass(
                config.getDestinationPackage() + "." + config.getBuilderName(),
                GlobalSearchScope.projectScope(project)
        );
    }

    private static PsiDirectory createOrFindPackageDirectory(PsiDirectory sourceRootDir, String destinationPackage) {
        String[] packagePath = destinationPackage.split("\\.");
        PsiDirectory currentDir = sourceRootDir;

        for (String packagePart : packagePath) {
            PsiDirectory subDir = currentDir.findSubdirectory(packagePart);
            if (subDir == null) {
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
