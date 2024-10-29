package node;

public class VarDeclarationNode extends ASTNode {
    String identifier;
    ASTNode type;
    public ASTNode expression;

    public VarDeclarationNode(String identifier, ASTNode type, ASTNode expression) {
        this.identifier = identifier;
        this.type = type;
        this.expression = expression;
    }
}
