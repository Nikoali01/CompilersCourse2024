package node;

public class ArrayDeclarationNode extends ASTNode {
    String identifier;
    int size;
    ASTNode type;

    public ArrayDeclarationNode(String identifier, int size, ASTNode type) {
        this.identifier = identifier;
        this.size = size;
        this.type = type;
    }
}
