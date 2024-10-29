package semantic;

import node.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeclarationChecker {
    private final Set<String> functionIdentifiers = new HashSet<>();
    private final Set<String> variableIdentifiers = new HashSet<>();

    public void checkDeclarations(ProgramNode program) {
        for (ASTNode statement : program.statements) {
            checkDeclaration(statement);
        }
    }

    private void checkDeclaration(ASTNode statement) {
        switch (statement) {
            case RoutineDeclarationNode routineDeclarationNode -> {
                System.out.println(routineDeclarationNode.identifier);
                functionIdentifiers.add(routineDeclarationNode.identifier);
                checkRoutineBody(routineDeclarationNode.body);
            }
            case VarDeclarationNode varDeclarationNode -> variableIdentifiers.add(varDeclarationNode.identifier);
            case PrintStatementNode printStatementNode -> checkPrintStatement(printStatementNode);
            case FunctionCallNode functionCallNode -> checkFunctionCall(functionCallNode);
            case null, default -> {
            }
            // Handle other types of declarations as necessary
        }
    }

    private void checkRoutineBody(List<ASTNode> body) {
        for (ASTNode statement : body) {
            checkDeclaration(statement);
        }
    }

    private void checkPrintStatement(PrintStatementNode printStatement) {
        ASTNode expression = printStatement.expression;
        if (expression instanceof VarDeclarationNode) {
            String variableName = ((VarDeclarationNode) expression).identifier;
            if (!variableIdentifiers.contains(variableName)) {
                throw new RuntimeException("Variable " + variableName + " used before declaration.");
            }
        } else if (expression instanceof FunctionCallNode){
            System.out.println("Func in print");
            checkDeclaration(expression);
        } else {
            checkExpression(expression);
        }
    }

    private void checkExpression(ASTNode expression) {
        if (expression instanceof BinaryOperationNode) {
            checkExpression(((BinaryOperationNode) expression).left);
            checkExpression(((BinaryOperationNode) expression).right);
        }
    }

    private void checkFunctionCall(FunctionCallNode functionCall) {
        if (!functionIdentifiers.contains(functionCall.identifier)) {
            throw new RuntimeException("Function " + functionCall.identifier + " called before declaration.");
        }


        for (ASTNode arg : functionCall.arguments) {
            if (arg instanceof VarDeclarationNode) {
                String variableName = ((VarDeclarationNode) arg).identifier;
                if (!variableIdentifiers.contains(variableName)) {
                    throw new RuntimeException("Variable " + variableName + " used before declaration in function call.");
                }
            } else {
                checkExpression(arg);
            }
        }
    }
}
