package by.shareiko.testutils.classref;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;

public interface UnqualifiedClassNameReferenceResolver {
    ClassReferenceResolution resolve(Project project, PsiClass sourceClass, String initializerText);
}
