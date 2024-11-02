package by.shareiko.testutils.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;

public class PsiVirtualFileUtils {
    public static boolean isTestsSourceRoot(Project project, VirtualFile file) {
        ProjectFileIndex fileIndex = ProjectRootManager.getInstance(project).getFileIndex();

        return fileIndex.isInTestSourceContent(file);
    }
}
