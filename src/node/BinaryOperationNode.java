package node;

import tokens.TokenType;

public class BinaryOperationNode extends ASTNode {
    ASTNode left;
    TokenType operator;
    ASTNode right;

    public BinaryOperationNode(ASTNode left, TokenType operator, ASTNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

}
