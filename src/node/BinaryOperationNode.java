package node;

import tokens.TokenType;

public class BinaryOperationNode extends ASTNode {
    public ASTNode left;
    public TokenType operator;
    public ASTNode right;

    public BinaryOperationNode(ASTNode left, TokenType operator, ASTNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
}
