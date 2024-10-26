package by.shareiko.testutils.ui.components.validator;

import com.intellij.openapi.ui.ValidationInfo;

import javax.lang.model.SourceVersion;
import javax.swing.*;
import java.util.function.Supplier;

public class ClassNameValidator implements Supplier<ValidationInfo> {
    private final JTextField classNameField;

    public ClassNameValidator(JTextField classNameField) {
        this.classNameField = classNameField;
    }

    @Override
    public ValidationInfo get() {
        boolean isValidName = SourceVersion.isIdentifier(classNameField.getText());
        if (!isValidName) {
            return new ValidationInfo("'%s' is not a valid class name.".formatted(classNameField.getText()),
                    classNameField);
        }
        return null;
    }
}
