package semantic;

import node.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class DeclarationChecker {
    private final Set<String> functionIdentifiers = new HashSet<>();
    private final Stack<Set<String>> variableScopes = new Stack<>();

    public DeclarationChecker() {
        variableScopes.push(new HashSet<>()); // Initialize global scope
    }

    public void checkDeclarations(ProgramNode program) {
        for (ASTNode statement : program.statements) {
            checkDeclaration(statement);
        }
    }

    private void checkDeclaration(ASTNode statement) {
        switch (statement) {
            case RoutineDeclarationNode routineDeclarationNode -> {
                System.out.println("Function declared: " + routineDeclarationNode.identifier);
                functionIdentifiers.add(routineDeclarationNode.identifier);
                checkRoutineBody(routineDeclarationNode.body);
            }
            case VarDeclarationNode varDeclarationNode -> addVariable(varDeclarationNode.identifier);
            case PrintStatementNode printStatementNode -> checkPrintStatement(printStatementNode);
            case FunctionCallNode functionCallNode -> checkFunctionCall(functionCallNode);
            case IfStatementNode ifStatementNode -> checkIfStatement(ifStatementNode);
            case WhileLoopNode whileLoopNode -> checkWhileLoop(whileLoopNode);
            case ForLoopNode forLoopNode -> checkForLoop(forLoopNode);
            case AssignmentNode assignmentNode -> checkAssignment(assignmentNode);
            case null, default -> {
            }
        }
    }

    private void checkAssignment(AssignmentNode assignment) {
        if (assignment.lvalue instanceof IdentifierNode) {
            String variableName = ((IdentifierNode) assignment.lvalue).name;
            if (!isVariableDeclared(variableName)) {
                throw new RuntimeException("Variable " + variableName + " assigned before declaration.");
            }
        }
        checkExpression(assignment.expression);
    }

    private void checkRoutineBody(List<ASTNode> body) {
        for (ASTNode statement : body) {
            System.out.println("Node " + statement);
            checkDeclaration(statement);
        }
    }

    private void checkIfStatement(IfStatementNode ifStatement) {
        checkExpression(ifStatement.condition);
        enterNewScope();
        checkRoutineBody(ifStatement.thenStatements);
        exitCurrentScope();

        for (ASTNode elsifStatement : ifStatement.elsifStatements) {
            checkDeclaration(elsifStatement);
            exitCurrentScope();
        }

        if (ifStatement.elseStatements != null) {
            checkRoutineBody(ifStatement.elseStatements);
            exitCurrentScope();
        }
    }

    private void checkWhileLoop(WhileLoopNode whileLoop) {
        checkDeclaration(whileLoop.condition); // Check the loop condition
        checkRoutineBody(whileLoop.body); // Check the loop body
        exitCurrentScope(); // Exit the scope for the while loop
    }

    private void checkForLoop(ForLoopNode forLoop) {
        checkRoutineBody(forLoop.body); // Check the loop body
        exitCurrentScope(); // Exit the scope for the for loop
    }

    private void checkPrintStatement(PrintStatementNode printStatement) {
        ASTNode expression = printStatement.expression;
        if (expression instanceof IdentifierNode) {
            String variableName = ((IdentifierNode) expression).name;
            if (!isVariableDeclared(variableName)) {
                throw new RuntimeException("Variable " + variableName + " used before declaration.");
            }
        } else if (expression instanceof FunctionCallNode) {
            System.out.println("Function in print");
            checkFunctionCall((FunctionCallNode) expression);
        } else {
            checkExpression(expression);
        }
    }

    private void checkExpression(ASTNode expression) {
        if (expression instanceof IdentifierNode) {
            String variableName = ((IdentifierNode) expression).name;
            if (!isVariableDeclared(variableName)) {
                throw new RuntimeException("Variable " + variableName + " used before declaration.");
            }
        } else if (expression instanceof BinaryOperationNode) {
            checkExpression(((BinaryOperationNode) expression).left);
            checkExpression(((BinaryOperationNode) expression).right);
        } else if (expression instanceof FunctionCallNode) {
            checkFunctionCall((FunctionCallNode) expression);
        }
    }

    private void checkFunctionCall(FunctionCallNode functionCall) {
        if (!functionIdentifiers.contains(functionCall.identifier)) {
            throw new RuntimeException("Function " + functionCall.identifier + " called before declaration.");
        }

        for (ASTNode arg : functionCall.arguments) {
            if (arg instanceof IdentifierNode) {
                String variableName = ((IdentifierNode) arg).name;
                if (!isVariableDeclared(variableName)) {
                    throw new RuntimeException("Variable " + variableName + " used before declaration in function call.");
                }
            } else {
                checkExpression(arg);
            }
        }
    }

    private void addVariable(String variableName) {
        variableScopes.peek().add(variableName);
    }

    private boolean isVariableDeclared(String variableName) {
        for (int i = variableScopes.size() - 1; i >= 0; i--) {
            if (variableScopes.get(i).contains(variableName)) {
                return true;
            }
        }
        return false;
    }

    private void enterNewScope() {
        variableScopes.push(new HashSet<>());
    }

    private void exitCurrentScope() {
        if (!variableScopes.isEmpty()) {
            variableScopes.pop();
        }
    }
}
