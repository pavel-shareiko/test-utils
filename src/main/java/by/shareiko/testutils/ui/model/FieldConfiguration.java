package by.shareiko.testutils.ui.model;

import com.intellij.psi.PsiField;
import org.codehaus.plexus.util.StringUtils;

public class FieldConfiguration {
    private String defaultValue;
    private String fieldName;
    private String accessModifier;
    private PsiField psiField;

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

    public PsiField getPsiField() {
        return psiField;
    }

    public void setPsiField(PsiField field) {
        this.psiField = field;
    }

    public String getSetterMethodName() {
        if (this.fieldName == null) {
            return null;
        }

        return "set" + StringUtils.capitalise(this.fieldName);
    }

    @Override
    public String toString() {
        return "FieldConfiguration{" +
                "defaultValue='" + defaultValue + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", accessModifier='" + accessModifier + '\'' +
                ", psiField=" + psiField +
                '}';
    }
}
