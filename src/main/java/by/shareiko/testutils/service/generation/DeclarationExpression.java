package by.shareiko.testutils.service.generation;

public interface DeclarationExpression extends TextExpression {
    VariableInfo getVariableInfo();

    boolean isChained();

    String getFinalizerText();
}
