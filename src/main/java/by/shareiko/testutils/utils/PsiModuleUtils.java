package by.shareiko.testutils.utils;

import com.intellij.java.library.JavaLibraryUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;

public class PsiModuleUtils {
    public static @Nullable Module getFileModule(PsiClass file) {
        return getFileModule(file.getProject(), file.getContainingFile().getVirtualFile());
    }

    public static @Nullable Module getFileModule(Project project, PsiClass file) {
        return getFileModule(project, file.getContainingFile().getVirtualFile());
    }

    public static @Nullable Module getFileModule(Project project, PsiFile containingFile) {
        return getFileModule(project, containingFile.getVirtualFile());
    }

    public static @Nullable Module getFileModule(Project project, VirtualFile file) {
        return ModuleUtil.findModuleForFile(file, project);
    }

    public static boolean isLombokEnabled(@Nullable Module module) {
        return hasLibrary(module, "org.projectlombok:lombok");
    }

    @SuppressWarnings("UnstableApiUsage")
    private static boolean hasLibrary(@Nullable Module module, String library) {
        return JavaLibraryUtil.hasLibraryJar(module, library);
    }
}
