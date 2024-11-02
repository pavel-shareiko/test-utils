package by.shareiko.testutils.service.generation;

import by.shareiko.testutils.properties.TestDataBuilderConfiguration;
import by.shareiko.testutils.utils.PsiModuleUtils;
import com.intellij.openapi.project.Project;

public class BuildMethodGeneratorFactory {

    public static BuildMethodGenerator getInstance(Project project, TestDataBuilderConfiguration config) {
        if (PsiModuleUtils.isLombokEnabled(PsiModuleUtils.getFileModule(project, config.getSourceClass()))) {
            return new LombokBuildMethodGenerator(project);
        }

        return new JavaBeansBuildMethodGenerator(project);
    }
}
