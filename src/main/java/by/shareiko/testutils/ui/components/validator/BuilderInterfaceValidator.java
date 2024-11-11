package by.shareiko.testutils.ui.components.validator;

import by.shareiko.testutils.ui.components.ClassNameReferenceEditor;
import by.shareiko.testutils.utils.PsiBuilderFinder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.ClassUtil;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class BuilderInterfaceValidator implements Supplier<ValidationInfo> {
    private final ClassNameReferenceEditor component;
    private final Project project;

    public BuilderInterfaceValidator(ClassNameReferenceEditor component, Project project) {
        this.component = component;
        this.project = project;
    }

    @Override
    public ValidationInfo get() {
        String text = component.getText();
        if (text.isEmpty()) {
            return validationError("Specify builder interface class");
        }

        PsiClass psiClass = ClassUtil.findPsiClass(PsiManager.getInstance(project), text);
        if (psiClass == null) {
            return validationError("Builder interface cannot be found");
        }

        boolean isValidBuilderInterface = PsiBuilderFinder.isValidBuilderInterface(psiClass);
        if (!isValidBuilderInterface) {
            return validationError("Selected class is not valid Builder interface");
        }

        return validationSuccess();
    }

    private @NotNull ValidationInfo validationError(String message) {
        component.getEditorTextField().putClientProperty("JComponent.outline", "error");
        return new ValidationInfo(message, component);
    }

    private ValidationInfo validationSuccess() {
        component.getEditorTextField().putClientProperty("JComponent.outline", null);
        return null;
    }
}
