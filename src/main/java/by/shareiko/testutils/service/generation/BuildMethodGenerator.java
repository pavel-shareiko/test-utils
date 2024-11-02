package by.shareiko.testutils.service.generation;

import by.shareiko.testutils.properties.TestDataBuilderConfiguration;
import com.intellij.psi.PsiMethod;

public interface BuildMethodGenerator {

    PsiMethod generate(TestDataBuilderConfiguration config);
}
