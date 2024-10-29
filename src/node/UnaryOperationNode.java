package node;

import tokens.TokenType;

public class UnaryOperationNode extends ASTNode {
    public TokenType operator;
    public ASTNode operand;

    public UnaryOperationNode(TokenType operator, ASTNode operand) {
        this.operator = operator;
        this.operand = operand;
    }
}
