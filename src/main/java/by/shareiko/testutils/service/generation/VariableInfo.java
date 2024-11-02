package by.shareiko.testutils.service.generation;

public class VariableInfo {
    public final String name;
    public final String type;

    public VariableInfo(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
