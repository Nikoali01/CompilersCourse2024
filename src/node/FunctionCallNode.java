package node;

import java.util.List;

public class FunctionCallNode extends ASTNode {
    String identifier;
    List<ASTNode> arguments;

    public FunctionCallNode(String identifier, List<ASTNode> arguments) {
        this.identifier = identifier;
        this.arguments = arguments;
    }
}
