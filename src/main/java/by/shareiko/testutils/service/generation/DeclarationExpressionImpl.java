package by.shareiko.testutils.service.generation;

public class DeclarationExpressionImpl implements DeclarationExpression {
    private final String text;
    private final VariableInfo variableName;
    private final boolean isChained;
    private final String finalizerText;

    public DeclarationExpressionImpl(String text, VariableInfo variableName, boolean isChained, String finalizerText) {
        this.text = text;
        this.variableName = variableName;
        this.isChained = isChained;
        this.finalizerText = finalizerText;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public VariableInfo getVariableInfo() {
        return variableName;
    }

    @Override
    public boolean isChained() {
        return isChained;
    }

    @Override
    public String getFinalizerText() {
        return finalizerText;
    }
}
