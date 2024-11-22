package node;

public class LValueNode extends ASTNode {
    public ASTNode base;
    public String field;
    public ASTNode index;

    public LValueNode(ASTNode base, String field, ASTNode index) {
        this.base = base;
        this.field = field;
        this.index = index;
    }
}

