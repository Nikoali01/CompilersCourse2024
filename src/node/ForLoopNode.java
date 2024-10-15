package node;

import java.util.List;

public class ForLoopNode extends ASTNode {
    String identifier;
    ASTNode startExpression;
    ASTNode endExpression;
    List<ASTNode> body;

    public ForLoopNode(String identifier, ASTNode startExpression, ASTNode endExpression, List<ASTNode> body) {
        this.identifier = identifier;
        this.startExpression = startExpression;
        this.endExpression = endExpression;
        this.body = body;
    }
}
