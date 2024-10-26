package by.shareiko.testutils.ui.model;

public class FieldConfiguration {
    private String defaultValue;
    private String fieldName;
    private String accessModifier;

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getAccessModifier() {
        return accessModifier;
    }

    public void setAccessModifier(String accessModifier) {
        this.accessModifier = accessModifier;
    }

    @Override
    public String toString() {
        return "FieldConfiguration{" +
                "defaultValue='" + defaultValue + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", accessModifier='" + accessModifier + '\'' +
                '}';
    }
}
