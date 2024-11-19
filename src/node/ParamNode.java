package node;

public class ParamNode extends ASTNode {
    public String identifier;
    public ASTNode type;

    public ParamNode(String identifier, ASTNode type) {
        this.identifier = identifier;
        this.type = type;
    }
}
