package by.shareiko.testutils.service.generation;

import by.shareiko.testutils.ui.model.FieldConfiguration;
import by.shareiko.testutils.utils.PsiAnnotationUtils;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BuildMethodBuilder {
    private final BuildMethodBuilderConfiguration builderConfig;
    private final PsiElementFactory elementFactory;
    private final StringBuilder methodBody;
    private boolean methodIsReady = false;
    private String methodSignature = "";

    public BuildMethodBuilder(BuildMethodBuilderConfiguration builderConfig) {
        this.builderConfig = builderConfig;
        this.elementFactory = JavaPsiFacade.getInstance(builderConfig.getProject()).getElementFactory();
        this.methodBody = new StringBuilder();
    }

    public PsiMethod build() {
        if (methodIsReady) {
            return createMethod();
        }

        this.methodSignature = buildMethodSignature();
        ExpressionProvider expressionProvider = builderConfig.getExpressionProvider();
        DeclarationExpression declarationExpression = expressionProvider.getDeclarationExpression();

        if (declarationExpression == null) {
            return createEmptyBuildMethod("Unable to instantiate '%s'. No default constructor or builder exist");
        }

        appendInitializerText(declarationExpression);
        appendFieldValueSetters(expressionProvider, declarationExpression);
        appendFinalizer(declarationExpression);
        appendReturnStatement(declarationExpression);

        return createMethod();
    }

    private String buildMethodSignature() {
        String accessModifier = "public";
        String returnType = builderConfig.getSourceClass().getQualifiedName();
        String methodName = builderConfig.getMethodName();

        return "{accessModifier} {returnType} {methodName}()"
                .replace("{accessModifier}", accessModifier)
                .replace("{returnType}", returnType)
                .replace("{methodName}", methodName);
    }

    private void appendInitializerText(DeclarationExpression declarationExpression) {
        if (declarationExpression.getVariableInfo() != null) {
            methodBody
                    .append(declarationExpression.getVariableInfo().getType())
                    .append(" ")
                    .append(declarationExpression.getVariableInfo().getName())
                    .append(" = ");
        }
        methodBody.append(declarationExpression.getText());

        if (!declarationExpression.isChained()) {
            methodBody.append(";");
        }

        methodBody.append("\n");
    }

    private void appendFieldValueSetters(ExpressionProvider expressionProvider, DeclarationExpression declarationExpression) {
        List<FieldConfiguration> selectedFields = builderConfig.getSelectedFields();
        for (var selectedField : selectedFields) {
            appendFieldValueSetter(selectedField, expressionProvider, declarationExpression);
        }
    }

    private void appendFieldValueSetter(FieldConfiguration selectedField, ExpressionProvider expressionProvider, DeclarationExpression declarationExpression) {
        SetterExpression setterForField = expressionProvider.getSetterForField(selectedField.getPsiField());
        if (setterForField == null) {
            return;
        }

        if (declarationExpression.isChained()) {
            methodBody.append(".");
        } else {
            methodBody.append(declarationExpression.getVariableInfo().getName()).append(".");
        }
        methodBody.append(setterForField.getText());
        methodBody.append("(").append(selectedField.getFieldName()).append(")");
        if (!declarationExpression.isChained()) {
            methodBody.append(";");
        }
        methodBody.append("\n");
    }

    private void appendReturnStatement(DeclarationExpression declarationExpression) {
        if (declarationExpression.getVariableInfo() == null) {
            methodBody.insert(0, "return ");
        } else {
            methodBody.append("return ").append(declarationExpression.getVariableInfo().getName());
        }
        methodBody.append(";");
    }

    private void appendFinalizer(DeclarationExpression declarationExpression) {
        if (declarationExpression.getFinalizerText() != null) {
            if (declarationExpression.isChained()) {
                methodBody.append(".");
            }
            methodBody.append(declarationExpression.getFinalizerText());
        }
    }

    @SuppressWarnings("SameParameterValue")
    private @NotNull PsiMethod createEmptyBuildMethod(String error) {
        this.methodBody.delete(0, methodBody.length());
        this.methodBody.append(("return null; /* " + error + " */").formatted(builderConfig.getSourceClass().getName()));
        return createMethod();
    }

    private @NotNull PsiMethod createMethod() {
        PsiMethod methodFromText = elementFactory.createMethodFromText(buildMethodDeclaration(), null);
        PsiAnnotationUtils.addAnnotationToElement(methodFromText, "@Override");
        this.methodIsReady = true;
        return methodFromText;
    }

    private @NotNull String buildMethodDeclaration() {
        return this.methodSignature + "{\n" + methodBody.toString() + "\n}";
    }

}
