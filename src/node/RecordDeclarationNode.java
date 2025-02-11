package node;

import java.util.List;

public class RecordDeclarationNode extends ASTNode {
    public String identifier;
    public List<VarDeclarationNode> fields;

    public RecordDeclarationNode(String identifier, List<VarDeclarationNode> fields) {
        this.identifier = identifier;
        this.fields = fields;
    }
}
