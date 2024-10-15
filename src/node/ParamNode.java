package node;

public class ParamNode extends ASTNode {
    String identifier;
    ASTNode type;

    public ParamNode(String identifier, ASTNode type) {
        this.identifier = identifier;
        this.type = type;
    }
}
