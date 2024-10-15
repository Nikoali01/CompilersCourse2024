package node;

public class LiteralNode extends ASTNode {
    Object value;

    public LiteralNode(Object value) {
        this.value = value;
    }
}
