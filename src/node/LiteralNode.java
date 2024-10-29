package node;

public class LiteralNode extends ASTNode {
    public Object value;

    public LiteralNode(Object value) {
        this.value = value;
    }
}
