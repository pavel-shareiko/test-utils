package by.shareiko.testutils.utils;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.ui.EditorTextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class EditorTextFieldFactory {
    public static EditorTextField createTextField(@NotNull Project project) {
        return createTextField(project, null);
    }

    public static EditorTextField createTextField(@NotNull Project project, @Nullable String defaultValue) {
        return createTextField(project, defaultValue, null, null, true);
    }

    public static EditorTextField createTextField(@NotNull Project project,
                                                  @Nullable String defaultValue,
                                                  @Nullable PsiElement context,
                                                  @Nullable PsiType expectedType,
                                                  boolean isPhysical
    ) {
        PsiExpressionCodeFragment codeFragment = JavaCodeFragmentFactory.getInstance(project)
                .createExpressionCodeFragment(
                        Objects.requireNonNullElse(defaultValue, ""),
                        context,
                        expectedType,
                        isPhysical);
        Document document = PsiDocumentManager.getInstance(project).getDocument(codeFragment);
        return new EditorTextField(document, project, JavaFileType.INSTANCE);
    }

}
