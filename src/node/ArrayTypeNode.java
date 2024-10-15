package node;

public class ArrayTypeNode extends ASTNode {
    private String name;
    private int size;

    public ArrayTypeNode(String name, int size) {
        this.name = name;
        this.size = size;
    }

}