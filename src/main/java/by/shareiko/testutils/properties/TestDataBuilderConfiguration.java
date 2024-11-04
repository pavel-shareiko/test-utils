package by.shareiko.testutils.properties;

import by.shareiko.testutils.ui.model.FieldConfiguration;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;

import java.util.List;

public class TestDataBuilderConfiguration {
    private String builderName;
    private String destinationPackage;
    private PsiClass sourceClass;
    private PsiClass baseInterface;
    private List<FieldConfiguration> selectedFields;
    private VirtualFile sourceRoot;
    private List<ClassDecorator> decorators;

    public static TestDataBuilderConfigurationBuilder builder() {
        return TestDataBuilderConfigurationBuilder.aTestDataBuilderConfiguration();
    }

    public FieldConfiguration getConfigurationForPsiField(String fieldName) {
        return selectedFields.stream()
                .filter(f -> f.getPsiField().getName().equals(fieldName))
                .findFirst()
                .orElse(null);
    }

    public String getBuilderName() {
        return builderName;
    }

    public void setBuilderName(String builderName) {
        this.builderName = builderName;
    }

    public String getDestinationPackage() {
        return destinationPackage;
    }

    public void setDestinationPackage(String destinationPackage) {
        this.destinationPackage = destinationPackage;
    }

    public PsiClass getBaseInterface() {
        return baseInterface;
    }

    public void setBaseInterface(PsiClass baseInterface) {
        this.baseInterface = baseInterface;
    }

    public List<FieldConfiguration> getSelectedFields() {
        return selectedFields;
    }

    public void setSelectedFields(List<FieldConfiguration> selectedFields) {
        this.selectedFields = selectedFields;
    }

    public VirtualFile getSourceRoot() {
        return sourceRoot;
    }

    public void setSourceRoot(VirtualFile sourceRoot) {
        this.sourceRoot = sourceRoot;
    }

    public PsiClass getSourceClass() {
        return sourceClass;
    }

    public void setSourceClass(PsiClass sourceClass) {
        this.sourceClass = sourceClass;
    }

    public List<ClassDecorator> getDecorators() {
        return decorators;
    }

    public void setDecorators(List<ClassDecorator> decorators) {
        this.decorators = decorators;
    }

    @Override
    public String toString() {
        return "TestDataBuilderConfiguration{" +
                "builderName='" + builderName + '\'' +
                ", destinationPackage='" + destinationPackage + '\'' +
                ", sourceClass=" + sourceClass +
                ", baseInterface=" + baseInterface +
                ", selectedFields=" + selectedFields +
                ", sourceRoot=" + sourceRoot +
                ", decorators=" + decorators +
                '}';
    }
}
