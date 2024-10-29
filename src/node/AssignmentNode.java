package node;

public class AssignmentNode extends ASTNode {
    public ASTNode lvalue;
    public ASTNode expression;

    public AssignmentNode(ASTNode lvalue, ASTNode expression) {
        this.lvalue = lvalue;
        this.expression = expression;
    }
}
