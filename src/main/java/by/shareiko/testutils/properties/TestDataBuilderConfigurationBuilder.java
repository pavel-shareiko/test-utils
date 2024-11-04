package by.shareiko.testutils.properties;

import by.shareiko.testutils.ui.model.FieldConfiguration;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;

import java.util.List;

public final class TestDataBuilderConfigurationBuilder {
    private String builderName;
    private String destinationPackage;
    private PsiClass sourceClass;
    private PsiClass baseInterface;
    private List<FieldConfiguration> selectedFields;
    private VirtualFile sourceRoot;
    private List<ClassDecorator> decorators;

    private TestDataBuilderConfigurationBuilder() {
    }

    public static TestDataBuilderConfigurationBuilder aTestDataBuilderConfiguration() {
        return new TestDataBuilderConfigurationBuilder();
    }

    public TestDataBuilderConfigurationBuilder withBuilderName(String builderName) {
        this.builderName = builderName;
        return this;
    }

    public TestDataBuilderConfigurationBuilder withDestinationPackage(String destinationPackage) {
        this.destinationPackage = destinationPackage;
        return this;
    }

    public TestDataBuilderConfigurationBuilder withSourceClass(PsiClass sourceClass) {
        this.sourceClass = sourceClass;
        return this;
    }

    public TestDataBuilderConfigurationBuilder withBaseInterface(PsiClass baseInterface) {
        this.baseInterface = baseInterface;
        return this;
    }

    public TestDataBuilderConfigurationBuilder withSelectedFields(List<FieldConfiguration> selectedFields) {
        this.selectedFields = selectedFields;
        return this;
    }

    public TestDataBuilderConfigurationBuilder withSourceRoot(VirtualFile sourceRoot) {
        this.sourceRoot = sourceRoot;
        return this;
    }

    public TestDataBuilderConfigurationBuilder withDecorators(List<ClassDecorator> decorators) {
        this.decorators = decorators;
        return this;
    }

    public TestDataBuilderConfiguration build() {
        TestDataBuilderConfiguration testDataBuilderConfiguration = new TestDataBuilderConfiguration();
        testDataBuilderConfiguration.setBuilderName(builderName);
        testDataBuilderConfiguration.setDestinationPackage(destinationPackage);
        testDataBuilderConfiguration.setSourceClass(sourceClass);
        testDataBuilderConfiguration.setBaseInterface(baseInterface);
        testDataBuilderConfiguration.setSelectedFields(selectedFields);
        testDataBuilderConfiguration.setSourceRoot(sourceRoot);
        testDataBuilderConfiguration.setDecorators(decorators);
        return testDataBuilderConfiguration;
    }
}
