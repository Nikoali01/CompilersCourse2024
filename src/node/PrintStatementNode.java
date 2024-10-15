package node;

public class PrintStatementNode extends ASTNode {
    ASTNode expression;

    public PrintStatementNode(ASTNode expression) {
        this.expression = expression;
    }
}
