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
        List<ASTNode> thenStatements = eliminateDeadCode(ifNode.thenStatements);
        List<ASTNode> elsifStatements = eliminateDeadCode(ifNode.elsifStatements);
        List<ASTNode> elseStatements = eliminateDeadCode(ifNode.elseStatements);

        return new IfStatementNode(ifNode.condition, thenStatements, elsifStatements, elseStatements);
    }

    private WhileLoopNode optimizeWhileNode(WhileLoopNode whileNode) {
        List<ASTNode> optimizedBody = eliminateDeadCode(whileNode.body);
        return new WhileLoopNode(whileNode.condition, optimizedBody);
    }
}
