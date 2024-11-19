package node;

import java.util.List;

public class RoutineDeclarationNode extends ASTNode {
    public String identifier;
    public List<ParamNode> params;
    public ASTNode returnType;
    public List<ASTNode> body;

    public RoutineDeclarationNode(String identifier, List<ParamNode> params, ASTNode returnType, List<ASTNode> body) {
        this.identifier = identifier;
        this.params = params;
        this.returnType = returnType;
        this.body = body;
    }
}
