package node;

public class PrintStatementNode extends ASTNode {
    public ASTNode expression;

    public PrintStatementNode(ASTNode expression) {
        this.expression = expression;
    }
}
