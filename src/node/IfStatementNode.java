package node;

import java.util.List;

public class IfStatementNode extends ASTNode {
    public ASTNode condition;
    public List<ASTNode> thenStatements;
    public List<ASTNode> elsifStatements;
    public List<ASTNode> elseStatements;

    public IfStatementNode(ASTNode condition, List<ASTNode> thenStatements, List<ASTNode> elsifStatements, List<ASTNode> elseStatements) {
        this.condition = condition;
        this.thenStatements = thenStatements;
        this.elsifStatements = elsifStatements;
        this.elseStatements = elseStatements;
    }

    public boolean containsReturn() {
        return containsReturnInBlock(thenStatements) || containsReturnInBlock(elsifStatements) || containsReturnInBlock(elseStatements);
    }

    private boolean containsReturnInBlock(List<ASTNode> statements) {
        if (statements == null) return false;
        for (ASTNode node : statements) {
            if (node instanceof ReturnStatementNode) {
                return true;
            }
            if (node instanceof IfStatementNode ifNode && ifNode.containsReturn()) {
                return true;
            }
            if (node instanceof WhileLoopNode whileNode && containsReturnInBlock(whileNode.body)) {
                return true;
            }
        }
        return false;
    }
}
