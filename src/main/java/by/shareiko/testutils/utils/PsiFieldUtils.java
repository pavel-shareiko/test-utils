package by.shareiko.testutils.utils;

import com.intellij.lang.jvm.JvmModifier;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;

import java.util.Arrays;

public class PsiFieldUtils {
    public static PsiField[] getWritableFields(PsiClass sourceClass) {
        return Arrays.stream(sourceClass.getFields())
                .filter(f -> !f.hasModifier(JvmModifier.STATIC))
                .filter(f -> !f.hasModifier(JvmModifier.FINAL))
                .toArray(PsiField[]::new);
    }
}
