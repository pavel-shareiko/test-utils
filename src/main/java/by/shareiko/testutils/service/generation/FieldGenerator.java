package by.shareiko.testutils.service.generation;

import by.shareiko.testutils.ui.model.FieldConfiguration;
import com.intellij.psi.PsiField;

public interface FieldGenerator {
    PsiField generate(FieldConfiguration config);
}
