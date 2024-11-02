package by.shareiko.testutils.classref;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.Nullable;

public class QualifiedClassReference {
    private final String packageName;
    private final String className;

    private QualifiedClassReference(String packageName, String className) {
        this.packageName = packageName;
        this.className = className;
    }

    public static QualifiedClassReference of(String packageName, String className) {
        if (packageName.endsWith(".")) {
            packageName = packageName.substring(0, packageName.length() - 1);
        }

        if (className.startsWith(".")) {
            className = className.substring(1);
        }
        return new QualifiedClassReference(packageName, className);
    }

    public String getFullyQualifiedName() {
        return packageName + "." + className;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

    public @Nullable PsiClass getPsiClass(Project project) {
        return JavaPsiFacade.getInstance(project).findClass(
                getFullyQualifiedName(),
                GlobalSearchScope.allScope(project));
    }
}
