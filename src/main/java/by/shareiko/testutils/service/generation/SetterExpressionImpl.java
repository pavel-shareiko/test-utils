package by.shareiko.testutils.service.generation;

public class SetterExpressionImpl implements SetterExpression {
    private final boolean isChained;
    private final String text;

    public SetterExpressionImpl(boolean isChained, String text) {
        this.isChained = isChained;
        this.text = text;
    }

    @Override
    public boolean isChained() {
        return isChained;
    }

    @Override
    public String getText() {
        return text;
    }
}
