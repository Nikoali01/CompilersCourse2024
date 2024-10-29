package optimization;

import node.*;
import tokens.TokenType;

public class ConstantExpressionSimplifier {
    public ASTNode simplify(ASTNode node) {
        if (node instanceof BinaryOperationNode binaryOp) {
            binaryOp.left = simplify(binaryOp.left);
            binaryOp.right = simplify(binaryOp.right);

            if (binaryOp.left instanceof LiteralNode leftLiteral && binaryOp.right instanceof LiteralNode rightLiteral) {
                Object leftVal = leftLiteral.value;
                Object rightVal = rightLiteral.value;
                Object result = applyBinaryOperator(leftVal, binaryOp.operator, rightVal);
                if (result != null) {
                    return new LiteralNode(result);
                }
            }
        } else if (node instanceof UnaryOperationNode unaryOp) {
            unaryOp.operand = simplify(unaryOp.operand);

            if (unaryOp.operand instanceof LiteralNode operandLiteral) {
                Object operandVal = operandLiteral.value;
                Object result = evaluateUnaryExpression(operandVal, unaryOp.operator);
                if (result != null) {
                    return new LiteralNode(result);
                }
            }
        } else if (node instanceof VarDeclarationNode varDecl) {
            varDecl.expression = simplify(varDecl.expression);
        } else if (node instanceof AssignmentNode assignment) {
            assignment.expression = simplify(assignment.expression);
        }
        return node;
    }

    private Object applyBinaryOperator(Object left, TokenType operator, Object right) {
        if (left instanceof Integer && right instanceof Integer) {
            return applyIntegerOperator((Integer) left, operator, (Integer) right);
        } else if (left instanceof Double && right instanceof Double) {
            return applyDoubleOperator((Double) left, operator, (Double) right);
        } else if (left instanceof Boolean && right instanceof Boolean) {
            return applyBooleanOperator((Boolean) left, operator, (Boolean) right);
        }
        return null;
    }

    private Integer applyIntegerOperator(int left, TokenType operator, int right) {
        return switch (operator) {
            case PLUS -> left + right;
            case MINUS -> left - right;
            case STAR -> left * right;
            case SLASH -> right != 0 ? left / right : null;
            case LESS -> left < right ? 1 : 0;
            case LESS_EQUAL -> left <= right ? 1 : 0;
            case GREATER -> left > right ? 1 : 0;
            case GREATER_EQUAL -> left >= right ? 1 : 0;
            case EQUAL -> left == right ? 1 : 0;
            default -> null;
        };
    }

    private Double applyDoubleOperator(double left, TokenType operator, double right) {
        return switch (operator) {
            case PLUS -> left + right;
            case MINUS -> left - right;
            case STAR -> left * right;
            case SLASH -> right != 0 ? left / right : null;
            case LESS -> left < right ? 1.0 : 0.0;
            case LESS_EQUAL -> left <= right ? 1.0 : 0.0;
            case GREATER -> left > right ? 1.0 : 0.0;
            case GREATER_EQUAL -> left >= right ? 1.0 : 0.0;
            case EQUAL -> left == right ? 1.0 : 0.0;
            default -> null;
        };
    }

    private Boolean applyBooleanOperator(boolean left, TokenType operator, boolean right) {
        return switch (operator) {
            case AND -> left && right;
            case OR -> left || right;
            default -> null;
        };
    }

    private Object evaluateUnaryExpression(Object operand, TokenType operator) {
        if (operand instanceof Boolean && operator == TokenType.NOT) {
            return !(Boolean) operand;
        } else if (operand instanceof Integer && operator == TokenType.MINUS) {
            return -(Integer) operand;
        }
        return null;
    }
}
