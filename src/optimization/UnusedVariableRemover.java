package optimization;

import node.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UnusedVariableRemover {
    private final Set<String> usedVariables = new HashSet<>();

    public void collectUsedVariables(ASTNode node) {
        if (node instanceof IdentifierNode identifierNode) {
            usedVariables.add(identifierNode.name);
        } else if (node instanceof BinaryOperationNode binaryOp) {
            collectUsedVariables(binaryOp.left);
            collectUsedVariables(binaryOp.right);
        } else if (node instanceof AssignmentNode assignment) {
            collectUsedVariables(assignment.lvalue);
            collectUsedVariables(assignment.expression);
        } else if (node instanceof UnaryOperationNode unaryOp) {
            collectUsedVariables(unaryOp.operand);
        } else if (node instanceof VarDeclarationNode varDecl) {
            collectUsedVariables(varDecl.expression);
        } else if (node instanceof FunctionCallNode functionCall) {
            for (ASTNode arg : functionCall.arguments) {
                collectUsedVariables(arg);
            }
        } else if (node instanceof ReturnStatementNode returnNode) {
            collectUsedVariables(returnNode.expression);
        } else if (node instanceof PrintStatementNode printNode) {
            collectUsedVariables(printNode.expression);
        } else if (node instanceof IfStatementNode ifNode) {
            collectUsedVariables(ifNode.condition);
            if (ifNode.thenStatements != null) {
                ifNode.thenStatements.forEach(this::collectUsedVariables);
            }
            if (ifNode.elsifStatements != null) {
                ifNode.elsifStatements.forEach(this::collectUsedVariables);
            }
            if (ifNode.elseStatements != null) {
                ifNode.elseStatements.forEach(this::collectUsedVariables);
            }
        } else if (node instanceof WhileLoopNode whileNode) {
            collectUsedVariables(whileNode.condition);
            whileNode.body.forEach(this::collectUsedVariables);
        } else if (node instanceof ForLoopNode forLoopNode) {
            collectUsedVariables(forLoopNode.endExpression);
            collectUsedVariables(forLoopNode.startExpression);
            forLoopNode.body.forEach(this::collectUsedVariables);
        } else if (node instanceof LValueNode lValueNode) {
            collectUsedVariables(lValueNode.base);
        }
    }

    public ProgramNode removeUnusedVariables(ProgramNode program) {
        List<ASTNode> optimizedStatements = new ArrayList<>();
        for (ASTNode statement : program.statements) {
            if (statement instanceof VarDeclarationNode varDecl) {
                String varName = varDecl.identifier;
                if (usedVariables.contains(varName)) {
                    optimizedStatements.add(statement);
                }
            } else {
                optimizedStatements.add(statement);
            }
        }
        return new ProgramNode(optimizedStatements);
    }
}
