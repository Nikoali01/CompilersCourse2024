package semantic;

import node.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KeyWordUsageChecker {
    private final Set<String> functionIdentifiers = new HashSet<>();
    private String currentFunction;
    private boolean insideLoop = false;

    public void check(ProgramNode program) {
        for (ASTNode statement : program.statements) {
            checkStatement(statement);
        }
    }

    private void checkStatement(ASTNode statement) {
        switch (statement) {
            case RoutineDeclarationNode routineDeclarationNode -> {
                currentFunction = routineDeclarationNode.identifier;

                functionIdentifiers.add(currentFunction);

                checkRoutineBody(routineDeclarationNode.body);
                currentFunction = null;
            }
            case WhileLoopNode whileLoopNode -> {
                insideLoop = true;
                checkWhileLoop(whileLoopNode);
                insideLoop = false;
            }
            case ForLoopNode forLoopNode -> {
                insideLoop = true;
                checkForLoop(forLoopNode);
                insideLoop = false;
            }
            case ReturnStatementNode returnStatementNode -> checkReturnStatement(returnStatementNode);
            case PrintStatementNode printStatementNode -> {

            }
            case null, default -> {
            }
        }
    }

    private void checkRoutineBody(List<ASTNode> body) {
        for (ASTNode statement : body) {
            checkStatement(statement);
        }
    }

    private void checkWhileLoop(WhileLoopNode loop) {
        checkRoutineBody(loop.body);
    }

    private void checkForLoop(ForLoopNode loop) {
        checkRoutineBody(loop.body);
    }

    private void checkReturnStatement(ReturnStatementNode returnStatement) {
        if (currentFunction == null) {
            throw new RuntimeException("Return statement used outside of a function context.");
        }

    }
}
