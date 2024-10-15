package node;

public class ReturnStatementNode extends ASTNode {
    ASTNode expression;

    public ReturnStatementNode(ASTNode expression) {
        this.expression = expression;
    }
}
