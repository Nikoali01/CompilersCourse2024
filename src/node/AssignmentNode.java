package node;

public class AssignmentNode extends ASTNode {
    ASTNode lvalue;
    ASTNode expression;

    public AssignmentNode(ASTNode lvalue, ASTNode expression) {
        this.lvalue = lvalue;
        this.expression = expression;
    }
}
