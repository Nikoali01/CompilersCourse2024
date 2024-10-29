package node;

import java.util.List;

public class WhileLoopNode extends ASTNode {
    public ASTNode condition;
    public List<ASTNode> body;

    public WhileLoopNode(ASTNode condition, List<ASTNode> body) {
        this.condition = condition;
        this.body = body;
    }
}

