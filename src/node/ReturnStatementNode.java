package node;

public class ReturnStatementNode extends ASTNode {
    public ASTNode expression;

    public ReturnStatementNode(ASTNode expression) {
        this.expression = expression;
    }
}
