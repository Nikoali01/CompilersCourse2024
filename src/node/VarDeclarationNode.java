package node;

public class VarDeclarationNode extends ASTNode {
    public String identifier;
    public ASTNode type;
    public ASTNode expression;

    public VarDeclarationNode(String identifier, ASTNode type, ASTNode expression) {
        this.identifier = identifier;
        this.type = type;
        this.expression = expression;
    }
}
