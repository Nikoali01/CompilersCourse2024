package node;

import java.util.List;

public class FunctionCallNode extends ASTNode {
    public String identifier;
    public List<ASTNode> arguments;

    public FunctionCallNode(String identifier, List<ASTNode> arguments) {
        this.identifier = identifier;
        this.arguments = arguments;
    }
}
