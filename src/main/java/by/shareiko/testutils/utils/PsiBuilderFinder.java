package by.shareiko.testutils.utils;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiPrimitiveType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class PsiBuilderFinder {
    /**
     * Finds a suitable builder method in a class or returns null if such method cannot be found.
     * Builder method should follow following criteria:
     * <ol>
     *     <li>Method has <code>public</code> or <code>protected</code> access modifier</li>
     *     <li>Method does not accept any parameters</li>
     *     <li>Method is not <code>void</code></li>
     *     <li>Exactly one method matching listed criteria is present</li>
     * </ol>
     *
     * @param psiClass class to find method in
     * @return build method or null
     */
    public static @Nullable PsiMethod findBuilderMethod(@NotNull PsiClass psiClass) {
        PsiMethod[] methodCandidates = Arrays.stream(psiClass.getMethods())
                .filter(m -> m.getModifierList().hasModifierProperty(PsiModifier.PUBLIC)
                        || m.getModifierList().hasModifierProperty(PsiModifier.PROTECTED)
                        || m.getModifierList().hasModifierProperty(PsiModifier.DEFAULT))
                .filter(m -> m.getParameterList().getParametersCount() == 0)
                .filter(m -> m.getReturnType() != null && !hasVoidReturnType(m))
                .toArray(PsiMethod[]::new);

        if (methodCandidates.length != 1) {
            return null;
        }

        return methodCandidates[0];
    }

    private static boolean hasVoidReturnType(PsiMethod m) {
        return m.getReturnType() instanceof PsiPrimitiveType && ((PsiPrimitiveType) m.getReturnType()).getName().equals("void");
    }

    /**
     * Checks whether the specified class is an interface or abstract class and that it has valid builder method
     * <br>See {@link PsiBuilderFinder#findBuilderMethod(PsiClass)}
     *
     * @param psiClass class to check
     * @return true if interface has exactly one valid builder method, false otherwise
     */
    @Contract("null -> false; _ -> _")
    public static boolean isValidBuilderInterface(@Nullable PsiClass psiClass) {
        if (psiClass == null) {
            return false;
        }
        return (psiClass.isInterface() || (psiClass.getModifierList() != null && psiClass.getModifierList().hasExplicitModifier(PsiModifier.ABSTRACT)))
                && findBuilderMethod(psiClass) != null;
    }
}
