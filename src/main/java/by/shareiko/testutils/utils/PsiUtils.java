package by.shareiko.testutils.utils;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.JavaProjectRootsUtil;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import org.jetbrains.annotations.NotNull;

public class PsiUtils {
    public static PsiClass getSelectedClass(PsiFile file, Editor editor) {
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = file.findElementAt(offset);

        while (element != null && !(element instanceof PsiClass)) {
            element = element.getParent();
        }

        return element == null ? null : (PsiClass) element;
    }

    public static String getPackageName(PsiClass psiClass) {
        if (psiClass == null) return null;
        if (psiClass.getQualifiedName() == null) return null;
        if (!psiClass.getQualifiedName().contains(".")) return "";

        return psiClass.getQualifiedName().substring(0, psiClass.getQualifiedName().lastIndexOf('.'));
    }

    public static PsiClass getClassFromProject(String fqn, Project project) {
        return JavaPsiFacade.getInstance(project).findClass(fqn, getScopeWithoutGeneratedSources(project));
    }

    private static @NotNull GlobalSearchScope getScopeWithoutGeneratedSources(Project project) {
        return JavaProjectRootsUtil.getScopeWithoutGeneratedSources(ProjectScope.getProjectScope(project), project);
    }

}
