import node.*;
import tokens.TokenType;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static tokens.TokenType.GREATER;
import static tokens.TokenType.LESS;

public class JasminCodeGenerator {
    private StringBuilder jasminCode = new StringBuilder();
    private Map<String, VariableInfo> symbolTable = new HashMap<>();
    private int variableIndex = 0;

    private record VariableInfo(String type, int index) {
    }

    public String generate(ProgramNode program) {
        jasminCode.append(".class public Main\n");
        jasminCode.append(".super java/lang/Object\n\n");
        jasminCode.append(".method public static main([Ljava/lang/String;)V\n");
        jasminCode.append(".limit stack 10\n");
        jasminCode.append(".limit locals 10\n");

        // Generate the code for all the statements in the program
        for (ASTNode statement : program.statements) {
            generateStatement(statement);
        }

        jasminCode.append("return\n");
        jasminCode.append(".end method\n");

        return jasminCode.toString();
    }

    private void generateStatement(ASTNode node) {
        if (node instanceof LiteralNode literalNode) {
            generateLiteral(literalNode);
        } else if (node instanceof VarDeclarationNode varNode) {
            generateVarDeclaration(varNode);
        } else if (node instanceof AssignmentNode assignmentNode) {
            generateAssignment(assignmentNode);
        } else if (node instanceof PrintStatementNode printNode) {
            generatePrint(printNode);
        } else if (node instanceof IdentifierNode identifierNode) {
            generateIdentifier(identifierNode);
        } else if (node instanceof BinaryOperationNode binaryNode) {
            generateBinaryOperation(binaryNode);
        } else if (node instanceof IfStatementNode ifNode) {
            generateIfStatement(ifNode);
        } else if (node instanceof ForLoopNode forLoopNode) {
            generateForLoop(forLoopNode);
        } else if (node instanceof WhileLoopNode whileLoopNode) {
            generateWhileLoop(whileLoopNode);
        } else {
            throw new UnsupportedOperationException("Unsupported ASTNode: " + node.getClass().getSimpleName());
        }
    }

    private void generateIfStatement(IfStatementNode node) {
        String endLabel = generateUniqueLabel();  // Label for the end of the if block
        String elseLabel = generateUniqueLabel(); // Label for the else block

        // Process the condition (comparison) dynamically
        BinaryOperationNode binaryOperationNode = (BinaryOperationNode) node.condition;
        generateBinaryOperation(binaryOperationNode);  // This processes the left and right nodes of the comparison

        // Generate the conditional jump based on the operator in the condition
        switch (binaryOperationNode.operator) {
            case GREATER -> {
                jasminCode.append("iflt ").append(elseLabel).append("\n"); // Jump to elseLabel if condition is false
            }
            case LESS -> {
                jasminCode.append("ifgt ").append(elseLabel).append("\n"); // Jump to elseLabel if condition is false
            }
            case EQUAL -> {
                jasminCode.append("ifeq ").append(elseLabel).append("\n"); // Jump to elseLabel if condition is false
            }
            default ->
                    throw new UnsupportedOperationException("Unsupported operator in condition: " + binaryOperationNode.operator);
        }

        // Generate the then block (if condition is true)
        for (int i = 0; i < node.thenStatements.size(); i++) {
            generateStatement(node.thenStatements.get(i));
        }
//        generateStatement(node.thenStatements.getFirst());  // This processes the statements in the "then" block

        // Jump to the end of the if block (skip the else block)
        jasminCode.append("goto ").append(endLabel).append("\n");

        // Generate the else block (if condition is false)
        jasminCode.append(elseLabel).append(":\n");
        for (int i = 0; i < node.elseStatements.size(); i++) {
            generateStatement(node.elseStatements.get(i));
        }
//        generateStatement(node.elseStatements.getFirst());  // This processes the statements in the "else" block

        // Add the end label to mark the end of the if block
        jasminCode.append(endLabel).append(":\n");
    }

    private String generateUniqueLabel() {
        return "L" + variableIndex++;
    }

    private void generateBinaryOperation(BinaryOperationNode node) {
        generateStatement(node.left);

        // If the left operand is an integer, convert it to double immediately after loading
//        if (node.left instanceof LiteralNode left && left.value instanceof Integer) {
//            jasminCode.append("i2d\n");  // Convert left operand to double if it's an integer literal
//        } else if (node.left instanceof IdentifierNode leftIdentifier) {
//            String varName = leftIdentifier.name;
//            if (symbolTable.containsKey(varName) && symbolTable.get(varName).type.equals("integer")) {
//                jasminCode.append("i2d\n");  // Convert left operand to double if it's an integer variable
//            }
//        } // Process the left operand (could be an identifier, literal, etc.)
        generateStatement(node.right);
//
//        // If the right operand is an integer, convert it to double immediately after loading
//        if (node.right instanceof LiteralNode right && right.value instanceof Integer) {
//            jasminCode.append("i2d\n");  // Convert right operand to double if it's an integer literal
//        } else if (node.right instanceof IdentifierNode rightIdentifier) {
//            String varName = rightIdentifier.name;
//            if (symbolTable.containsKey(varName) && symbolTable.get(varName).type.equals("integer")) {
//                jasminCode.append("i2d\n");  // Convert right operand to double if it's an integer variable
//            }
//        }// Process the right operand (could be an identifier, literal, etc.)

        boolean isDouble = false;

        // Check if the left operand is a double (literal or variable)
        if (node.left instanceof LiteralNode left && left.value instanceof Double) {
            isDouble = true;
        } else if (node.left instanceof IdentifierNode leftIdentifier) {
            String varName = leftIdentifier.name;
            if (symbolTable.containsKey(varName) && symbolTable.get(varName).type.equals("real")) {
                isDouble = true;
            }
        }

        // Check if the right operand is a double (literal or variable)
        if (node.right instanceof LiteralNode right && right.value instanceof Double) {
            isDouble = true;
        } else if (node.right instanceof IdentifierNode rightIdentifier) {
            String varName = rightIdentifier.name;
            if (symbolTable.containsKey(varName) && symbolTable.get(varName).type.equals("real")) {
                isDouble = true;
            }
        }
        // Generate the comparison instruction based on the operator
        switch (node.operator) {
            case PLUS -> jasminCode.append(isDouble ? "dadd\n" : "iadd\n");
            case SLASH -> jasminCode.append(isDouble ? "ddiv\n" : "idiv\n");
            case STAR -> jasminCode.append(isDouble ? "dmul\n" : "imul\n");
            case GREATER -> jasminCode.append("dcmpg\n");
            case LESS -> jasminCode.append("dcmpg\n");
            case EQUAL -> jasminCode.append("dcmpg\n");
            default -> throw new UnsupportedOperationException("Unsupported operator: " + node.operator);
        }
    }

    private void generateLiteral(LiteralNode node) {
        Object value = node.value;
        if (value instanceof Integer intValue) {
            jasminCode.append("ldc ").append(intValue).append("\n");
        } else if (value instanceof Double doubleValue) {
            jasminCode.append("ldc2_w ").append(doubleValue).append("\n");
        } else if (value instanceof String stringValue) {
            jasminCode.append("ldc ").append(stringValue).append("\n");
        } else {
            throw new UnsupportedOperationException("Unsupported literal type: " + value.getClass().getSimpleName());
        }
    }

    private void generateVarDeclaration(VarDeclarationNode node) {
        String varName = node.identifier;

        if (node.type instanceof TypeNode typeNode) {
            if (typeNode.typeName.equals("real")) {
                // If the type is 'real', we store the value in a double local variable
                generateStatement(node.expression != null ? node.expression : new LiteralNode(0.0)); // Default to 0.0
                jasminCode.append("dstore ").append(variableIndex).append("\n");
                symbolTable.put(varName, new VariableInfo("real", variableIndex));
                variableIndex += 2;  // Double takes 2 slots (as it's a 64-bit value)
            } else if (typeNode.typeName.equals("string")) {
                // If the type is 'string', we store the value in a string local variable
                generateStatement(node.expression != null ? node.expression : new LiteralNode("\"\"")); // Default to empty string
                jasminCode.append("astore ").append(variableIndex).append("\n");
                symbolTable.put(varName, new VariableInfo("string", variableIndex));
                variableIndex++;
                // String takes 1 slot
            } else if (typeNode.typeName.equals("integer")) {
                // If the type is 'integer', we store the value in an integer local variable
                generateStatement(node.expression != null ? node.expression : new LiteralNode(0));
                jasminCode.append("istore ").append(variableIndex).append("\n");
                symbolTable.put(varName, new VariableInfo("integer", variableIndex++));  // Integer takes 1 slot
            } else if (typeNode.typeName.equals("boolean")) {
                // If the type is 'boolean', we store the value in a boolean local variable
                generateStatement(node.expression != null ? node.expression : new LiteralNode(false));
                jasminCode.append("istore ").append(variableIndex).append("\n");
                symbolTable.put(varName, new VariableInfo("boolean", variableIndex++));  // Boolean takes 1 slot

            } else {
                throw new UnsupportedOperationException("Unsupported type: " + typeNode.typeName);
            }
        }
    }

    private void generateAssignment(AssignmentNode node) {
        generateStatement(node.expression);  // Generate code for the right-hand side expression

        if (node.lvalue instanceof IdentifierNode identifierNode) {
            String varName = identifierNode.name;

            if (symbolTable.containsKey(varName)) {
                VariableInfo varInfo = symbolTable.get(varName);
                switch (varInfo.type) {
                    case "real":
                        jasminCode.append("dstore ").append(varInfo.index).append("\n");
                        break;
                    case "string":
                        jasminCode.append("astore ").append(varInfo.index).append("\n");
                        break;
                    case "integer":
                        jasminCode.append("istore ").append(varInfo.index).append("\n");
                        break;
                    default:
                        throw new UnsupportedOperationException("Unsupported variable type: " + varInfo.type);
                }
            }
        }
    }

//    private void generatePrint(PrintStatementNode node) {
//        generateStatement(node.expression);
//        if (node.expression instanceof LiteralNode literalNode) {
//            if (literalNode.value instanceof Integer) {
//                jasminCode.append("getstatic java/lang/System/out Ljava/io/PrintStream;\n");
//                jasminCode.append("invokevirtual java/io/PrintStream/println(I)V\n");
//            } else if (literalNode.value instanceof Double) {
//                jasminCode.append("getstatic java/lang/System/out Ljava/io/PrintStream;\n");
//                jasminCode.append("invokevirtual java/io/PrintStream/println(D)V\n");
//            } else if (literalNode.value instanceof String) {
//                jasminCode.append("getstatic java/lang/System/out Ljava/io/PrintStream;\n");
//                jasminCode.append("invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\n");
//            }
//        }
//    }

    private void generatePrint(PrintStatementNode node) {
        jasminCode.append("getstatic java/lang/System/out Ljava/io/PrintStream;\n");
        generateStatement(node.expression);
        if (node.expression instanceof IdentifierNode identifierNode &&
                symbolTable.get(identifierNode.name).type.equals("real")) {
            jasminCode.append("invokevirtual java/io/PrintStream/println(D)V\n");
        } else if (node.expression instanceof IdentifierNode identifierNode &&
                symbolTable.get(identifierNode.name).type.equals("integer")) {
            jasminCode.append("invokevirtual java/io/PrintStream/println(I)V\n");
        } else {
            jasminCode.append("invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\n");
        }
    }

    private void generateIdentifier(IdentifierNode node) {
        String varName = node.name;
        if (symbolTable.containsKey(varName)) {
            VariableInfo varInfo = symbolTable.get(varName);
            if (varInfo.type.equals("real")) {
                jasminCode.append("dload ").append(varInfo.index).append("\n");
            } else if (varInfo.type.equals("string")) {
                jasminCode.append("aload ").append(varInfo.index).append("\n");
            } else if (varInfo.type.equals("integer")) {
                jasminCode.append("iload ").append(varInfo.index).append("\n");
            } else {
                throw new UnsupportedOperationException("Unsupported variable type: " + varInfo.type);
            }
        }
    }

    private void generateWhileLoop(WhileLoopNode node) {
        String startLabel = generateUniqueLabel();
        String endLabel = generateUniqueLabel();

        // Начало цикла
        jasminCode.append(startLabel).append(":\n");

        // Генерация кода для условия
        generateStatement(node.condition);

        // Определяем, что возвращает условие
        if (node.condition instanceof BinaryOperationNode conditionNode) {
            boolean isDouble = isDoubleComparison(conditionNode);
            switch (conditionNode.operator) {
                case GREATER -> {
                    jasminCode.append("iflt ").append(endLabel).append("\n");
                }
                case LESS -> {
                    jasminCode.append("ifgt ").append(endLabel).append("\n");
                }
                case EQUAL -> {
                    jasminCode.append("ifeq ").append(endLabel).append("\n");
                }
                default ->
                        throw new UnsupportedOperationException("Unsupported operator in condition: " + conditionNode.operator);
            }
//            if (isDouble) {
//                // Если условие сравнивает double, используем dcmpg и проверяем 0
//                jasminCode.append("ifeq ").append(endLabel).append("\n");
//            } else {
//                // Если условие работает с integer, просто проверяем
//                jasminCode.append("ifeq ").append(endLabel).append("\n");
//            }
        } else {
            throw new UnsupportedOperationException("While loop condition must be a binary operation.");
        }

        // Генерация тела цикла
        for (ASTNode statement : node.body) {
            generateStatement(statement);
        }

        // Возврат к началу цикла
        jasminCode.append("goto ").append(startLabel).append("\n");

        // Метка конца цикла
        jasminCode.append(endLabel).append(":\n");
    }

    // Вспомогательный метод для определения типа сравнения
    private boolean isDoubleComparison(BinaryOperationNode node) {
        return isDoubleType(node.left) || isDoubleType(node.right);
    }

    // Проверка, является ли выражение типа double
    private boolean isDoubleType(ASTNode node) {
        if (node instanceof LiteralNode literalNode) {
            return literalNode.value instanceof Double;
        } else if (node instanceof IdentifierNode identifierNode) {
            String varName = identifierNode.name;
            return symbolTable.containsKey(varName) && "real".equals(symbolTable.get(varName).type);
        }
        return false;
    }


    private void generateForLoop(ForLoopNode node) {
        String startLabel = generateUniqueLabel();
        String endLabel = generateUniqueLabel();

        // Declare and initialize the loop variable
        generateVarDeclaration(new VarDeclarationNode(
                node.identifier,
                new TypeNode("integer"),
                node.startExpression
        ));

        // Start label for the loop
        jasminCode.append(startLabel).append(":\n");

        // Load the loop variable and the end value
        jasminCode.append("iload ").append(symbolTable.get(node.identifier).index).append("\n");
        generateStatement(node.endExpression);

        // If loop variable exceeds end value, exit loop
        jasminCode.append("if_icmpgt ").append(endLabel).append("\n");

        // Generate body of the loop
        for (ASTNode statement : node.body) {
            generateStatement(statement);
        }

        // Increment the loop variable
        jasminCode.append("iinc ").append(symbolTable.get(node.identifier).index).append(" 1\n");

        // Jump back to the start of the loop
        jasminCode.append("goto ").append(startLabel).append("\n");

        // End label for the loop
        jasminCode.append(endLabel).append(":\n");
    }
}
