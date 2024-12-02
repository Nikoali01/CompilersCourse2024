package node;

public class ArrayTypeNode extends ASTNode {
    public String name;
    public int size;

    public ArrayTypeNode(String name, int size) {
        this.name = name;
        this.size = size;
    }

}