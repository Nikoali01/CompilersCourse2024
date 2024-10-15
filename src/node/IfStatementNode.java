package node;

import java.util.List;

public class IfStatementNode extends ASTNode {
    ASTNode condition;
    List<ASTNode> thenStatements;
    List<ASTNode> elsifStatements;
    List<ASTNode> elseStatements;

    public IfStatementNode(ASTNode condition, List<ASTNode> thenStatements, List<ASTNode> elsifStatements, List<ASTNode> elseStatements) {
        this.condition = condition;
        this.thenStatements = thenStatements;
        this.elsifStatements = elsifStatements;
        this.elseStatements = elseStatements;
    }
}
