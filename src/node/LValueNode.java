package node;

public class LValueNode extends ASTNode {
    ASTNode base;
    String field;
    ASTNode index;

    public LValueNode(ASTNode base, String field, ASTNode index) {
        this.base = base;
        this.field = field;
        this.index = index;
    }
}

