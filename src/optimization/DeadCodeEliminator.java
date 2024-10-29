package optimization;

import node.*;

import java.util.ArrayList;
import java.util.List;

public class DeadCodeEliminator {
    public List<ASTNode> eliminateDeadCode(List<ASTNode> nodes) {
        List<ASTNode> optimizedNodes = new ArrayList<>();
        boolean returnReached = false;

        for (ASTNode node : nodes) {
            if (returnReached) {
                continue;
            }

            switch (node) {
                case ReturnStatementNode ignored -> {
                    optimizedNodes.add(node);
                    returnReached = true;
                }
                case IfStatementNode ifNode -> {
                    IfStatementNode optimizedIf = optimizeIfNode(ifNode);
                    optimizedNodes.add(optimizedIf);
                    if (optimizedIf.containsReturn()) {
                        returnReached = true;
                    }
                }
                case WhileLoopNode whileNode -> {
                    WhileLoopNode optimizedWhile = optimizeWhileNode(whileNode);
                    optimizedNodes.add(optimizedWhile);
                }
                case null, default -> optimizedNodes.add(node);
            }
        }

        return optimizedNodes;
    }

    private IfStatementNode optimizeIfNode(IfStatementNode ifNode) {
        List<ASTNode> thenStatements = new ArrayList<>();
        List<ASTNode> elseStatements = new ArrayList<>();
        List<ASTNode> elseifStatements = new ArrayList<>();

        if (ifNode.thenStatements != null) {
            thenStatements = eliminateDeadCode(ifNode.thenStatements);
        }
        if (ifNode.elseStatements != null) {
            elseStatements = eliminateDeadCode(ifNode.elseStatements);
        }
        if (ifNode.elsifStatements != null) {
            for (int i = 0; i < ifNode.elsifStatements.size(); i++) {
                IfStatementNode optimizedIf = optimizeIfNode((IfStatementNode) ifNode.elsifStatements.get(i));
                elseifStatements.add(optimizedIf);
            }
        }

        return new IfStatementNode(ifNode.condition, thenStatements, elseifStatements, elseStatements);
    }

    private WhileLoopNode optimizeWhileNode(WhileLoopNode whileNode) {
        List<ASTNode> optimizedBody = eliminateDeadCode(whileNode.body);
        return new WhileLoopNode(whileNode.condition, optimizedBody);
    }
}
