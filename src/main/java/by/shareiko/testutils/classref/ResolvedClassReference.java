package by.shareiko.testutils.classref;

import com.intellij.psi.PsiClass;

public record ResolvedClassReference(String referenceText, PsiClass resolvedClass) {
}
