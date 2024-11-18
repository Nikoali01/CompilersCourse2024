import node.*;
import tokens.TokenType;

import java.util.HashMap;
import java.util.Map;

public class JasminCodeGenerator {
    private StringBuilder jasminCode = new StringBuilder();
    private Map<String, VariableInfo> symbolTable = new HashMap<>();
    private int variableIndex = 0;

    private record VariableInfo(String type, int index) {}

    public String generate(ProgramNode program) {
        jasminCode.append(".class public Main\n");
        jasminCode.append(".super java/lang/Object\n\n");
        jasminCode.append(".method public static main([Ljava/lang/String;)V\n");
        jasminCode.append(".limit stack 10\n");
        jasminCode.append(".limit locals 10\n");

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
        } else {
            throw new UnsupportedOperationException("Unsupported ASTNode: " + node.getClass().getSimpleName());
        }
    }

    private void generateIfStatement(IfStatementNode node) {
        String endLabel = generateUniqueLabel();
        String elseLabel = generateUniqueLabel();

        // Генерируем условие
        generateStatement(node.condition);
        jasminCode.append("ifeq ").append(elseLabel).append("\n");

        // Генерируем блок then
        for (ASTNode statement : node.thenStatements) {
            generateStatement(statement);
        }

        // Переход в конец после блока then
        jasminCode.append("goto ").append(endLabel).append("\n");

        // Генерация блока else
        jasminCode.append(elseLabel).append(":\n");
        for (ASTNode statement : node.elseStatements) {
            generateStatement(statement);
        }

        // Конец if
        jasminCode.append(endLabel).append(":\n");
    }

    private String generateUniqueLabel() {
        return "L" + variableIndex++;
    }

    private void generateBinaryOperation(BinaryOperationNode node) {
        generateStatement(node.left);
        generateStatement(node.right);

        boolean isDouble = node.left instanceof LiteralNode left && left.value instanceof Double ||
                node.right instanceof LiteralNode right && right.value instanceof Double;

        switch (node.operator) {
            case PLUS -> jasminCode.append(isDouble ? "dadd\n" : "iadd\n");
            case SLASH -> jasminCode.append(isDouble ? "ddiv\n" : "idiv\n");
            case STAR -> jasminCode.append(isDouble ? "dmul\n" : "imul\n");
            case GREATER -> {
                if (2==3) {
                    jasminCode.append("dcmpg\n");  // Compare two doubles
                    jasminCode.append("ifgt LABEL_TRUE\n");  // Jump if greater (true)
                } else {
                    jasminCode.append("icmpgt LABEL_TRUE\n");  // Compare two integers
                    jasminCode.append("ifgt LABEL_TRUE\n");  // Jump if greater (true)
                }
                jasminCode.append("LABEL_TRUE: \n");
            }
            case LESS -> jasminCode.append(isDouble ? "dcmpl\n" : "icmplt\n");
            case EQUAL -> jasminCode.append(isDouble ? "dcmpg\n" : "icmpne\n");
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
            jasminCode.append("ldc \"").append(stringValue).append("\"\n");
        } else {
            throw new UnsupportedOperationException("Unsupported literal type: " + value.getClass().getSimpleName());
        }
    }


    private void generateVarDeclaration(VarDeclarationNode node) {
        String varName = node.identifier;
//        if (symbolTable.containsKey(varName)) {
//            throw new RuntimeException("Variable " + varName + " is already declared.");
//        }

        if (node.type instanceof TypeNode typeNode) {
            if (typeNode.typeName.equals("integer")) {
                generateStatement(node.expression != null ? node.expression : new LiteralNode(0.0));
                jasminCode.append("dstore_").append(variableIndex).append("\n");
                symbolTable.put(varName, new VariableInfo("real", variableIndex++));
                variableIndex += 2;
            } else if (typeNode.typeName.equals("real")) {
                generateStatement(node.expression != null ? node.expression : new LiteralNode(0.0));
                jasminCode.append("dstore_").append(variableIndex).append("\n");
                symbolTable.put(varName, new VariableInfo("real", variableIndex));
                variableIndex += 2; // Double занимает два слота
            } else if (typeNode.typeName.equals("string")) {
                // Handle string type
                generateStatement(node.expression != null ? node.expression : new LiteralNode("\"\""));
                jasminCode.append("astore_").append(variableIndex).append("\n"); // Store the string
                symbolTable.put(varName, new VariableInfo("string", variableIndex));
                variableIndex++; // Strings occupy one slot
            } else {
                throw new UnsupportedOperationException("Unsupported type: " + typeNode.typeName);
            }
        }
    }


    private void generateAssignment(AssignmentNode node) {
        generateStatement(node.expression);

        if (node.lvalue instanceof IdentifierNode identifierNode) {
            String varName = identifierNode.name;
            if (symbolTable.containsKey(varName)) {
                VariableInfo varInfo = symbolTable.get(varName);
                if (varInfo.type.equals("integer")) {
                    jasminCode.append("istore_").append(varInfo.index).append("\n");
                } else if (varInfo.type.equals("real")) {
                    jasminCode.append("dstore_").append(varInfo.index).append("\n");
                } else {
                    throw new UnsupportedOperationException("Unsupported variable type: " + varInfo.type);
                }
            } else {
                throw new RuntimeException("Variable " + varName + " is not declared.");
            }
        } else {
            throw new UnsupportedOperationException("Unsupported LValueNode type: " + node.lvalue.getClass().getSimpleName());
        }
    }

    private void generatePrint(PrintStatementNode node) {
        jasminCode.append("getstatic java/lang/System/out Ljava/io/PrintStream;\n");
        generateStatement(node.expression);

        if (node.expression instanceof IdentifierNode identifierNode &&
                symbolTable.get(identifierNode.name).type.equals("real")) {
            jasminCode.append("invokevirtual java/io/PrintStream/println(D)V\n");
        } else {
            jasminCode.append("invokevirtual java/io/PrintStream/println(I)V\n");
        }
    }

    private void generateIdentifier(IdentifierNode node) {
        String varName = node.name;
        if (symbolTable.containsKey(varName)) {
            VariableInfo varInfo = symbolTable.get(varName);
            if (varInfo.type.equals("integer")) {
                jasminCode.append("iload_").append(varInfo.index).append("\n");
            } else if (varInfo.type.equals("real")) {
                jasminCode.append("dload_").append(varInfo.index).append("\n");
            } else {
                throw new UnsupportedOperationException("Unsupported variable type: " + varInfo.type);
            }
        } else {
            throw new RuntimeException("Variable " + varName + " is not declared.");
        }
    }
}
