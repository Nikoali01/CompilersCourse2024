import node.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JasminCodeGenerator {
    private StringBuilder jasminCode = new StringBuilder();
    private Map<String, VariableInfo> symbolTable = new HashMap<>();
    private int variableIndex = 0;

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
        } else if (node instanceof ForLoopNode forLoopNode) {
            generateForLoop(forLoopNode);
        } else if (node instanceof WhileLoopNode whileLoopNode) {
            generateWhileLoop(whileLoopNode);
        } else if (node instanceof ArrayDeclarationNode arrayNode) {
            generateArrayDeclaration(arrayNode);
        } else if (node instanceof FunctionCallNode functionCallNode) {
            generateFunctionCall(functionCallNode);
        } else if (node instanceof RecordDeclarationNode recordNode) {
            generateRecordDeclaration(recordNode);
        } else if (node instanceof RoutineDeclarationNode routineNode) {
            generateRoutineDeclaration(routineNode);
        } else if (node instanceof ReturnStatementNode returnNode) {
            generateReturnStatement(returnNode);
        } else {
            throw new UnsupportedOperationException("Unsupported ASTNode: " + node.getClass().getSimpleName());
        }
    }

    private void generateReturnStatement(ReturnStatementNode node) {
        if (node.expression != null) {
            generateStatement(node.expression);

            String returnType = mapTypeToReturnInstruction(node.expression.toString());

            jasminCode.append(returnType).append("\n");
        } else {
            jasminCode.append("return\n");
        }
    }

    private String mapTypeToReturnInstruction(String typeName) {
        return switch (typeName) {
            case "integer", "boolean" -> "ireturn";
            case "real" -> "dreturn";
            case "string" -> "areturn";
            default -> "ireturn";
//            default -> throw new UnsupportedOperationException("Unsupported return type: " + typeName);
        };
    }

    private String generateMethodDescriptor(RoutineDeclarationNode node) {
        StringBuilder descriptor = new StringBuilder("(");
        for (ParamNode param : node.params) {
            TypeNode test = (TypeNode) param.type;
            descriptor.append(mapTypeToDescriptor(test.typeName));
        }
        descriptor.append(")");
        if (node.returnType != null) {
            TypeNode returnType = (TypeNode) node.returnType;
            descriptor.append(mapTypeToDescriptor(returnType.typeName));
        } else {
            descriptor.append("V"); // void
        }
        return descriptor.toString();
    }

    private String mapTypeToDescriptor(String typeName) {
        return switch (typeName) {
            case "integer" -> "I";
            case "real" -> "D";
            case "string" -> "Ljava/lang/String;";
            case "boolean" -> "Z";
            default -> throw new UnsupportedOperationException("Unsupported type: " + typeName);
        };
    }

    private String generateReturnInstruction(String typeName) {
        return switch (typeName) {
            case "integer", "boolean" -> "ireturn\n";
            case "real" -> "dreturn\n";
            case "string" -> "areturn\n";
            default -> "return\n"; // Для void
        };
    }

    private void generateRoutineDeclaration(RoutineDeclarationNode node) {
        String methodName = node.identifier;
        String methodDescriptor = generateMethodDescriptor(node);

        jasminCode.append(".method public static ").append(methodName).append(methodDescriptor).append("\n");
        jasminCode.append(".limit stack 10\n");
        jasminCode.append(".limit locals ").append(10 + node.params.size()).append("\n");

        int paramIndex = 0;
        for (ParamNode param : node.params) {
            TypeNode test = (TypeNode) param.type;
            String paramType = mapTypeToDescriptor(test.typeName);
            symbolTable.put(param.identifier, new VariableInfo(paramType, paramIndex, false, 1));
            paramIndex += paramType.equals("real") ? 2 : 1; // Реальный тип занимает 2 ячейки
        }

        for (ASTNode statement : node.body) {
            generateStatement(statement);
        }

        if (node.returnType != null) {
            jasminCode.append(generateReturnInstruction(node.returnType.toString()));
        }

        jasminCode.append(".end method\n\n");
    }

    private void generateIfStatement(IfStatementNode node) {
        String endLabel = generateUniqueLabel();  // Label for the end of the if block
        String elseLabel = generateUniqueLabel(); // Label for the else block

        BinaryOperationNode binaryOperationNode = (BinaryOperationNode) node.condition;
        generateBinaryOperation(binaryOperationNode);

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

        for (int i = 0; i < node.thenStatements.size(); i++) {
            generateStatement(node.thenStatements.get(i));
        }
//        generateStatement(node.thenStatements.getFirst());  // This processes the statements in the "then" block

        jasminCode.append("goto ").append(endLabel).append("\n");

        jasminCode.append(elseLabel).append(":\n");
        for (int i = 0; i < node.elseStatements.size(); i++) {
            generateStatement(node.elseStatements.get(i));
        }
//        generateStatement(node.elseStatements.getFirst());  // This processes the statements in the "else" block

        jasminCode.append(endLabel).append(":\n");
    }

    private String generateUniqueLabel() {
        return "L" + variableIndex++;
    }

    private void generateBinaryOperation(BinaryOperationNode node) {
        generateStatement(node.left);

        generateStatement(node.right);
        boolean isDouble = false;

        if (node.left instanceof LiteralNode left && left.value instanceof Double) {
            isDouble = true;
        } else if (node.left instanceof IdentifierNode leftIdentifier) {
            String varName = leftIdentifier.name;
            if (symbolTable.containsKey(varName) && symbolTable.get(varName).type.equals("real")) {
                isDouble = true;
            }
        }

        if (node.right instanceof LiteralNode right && right.value instanceof Double) {
            isDouble = true;
        } else if (node.right instanceof IdentifierNode rightIdentifier) {
            String varName = rightIdentifier.name;
            if (symbolTable.containsKey(varName) && symbolTable.get(varName).type.equals("real")) {
                isDouble = true;
            }
        }

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
                symbolTable.put(varName, new VariableInfo("real", variableIndex, false, 1));
                variableIndex += 2;  // Double takes 2 slots (as it's a 64-bit value)
            } else if (typeNode.typeName.equals("string")) {
                // If the type is 'string', we store the value in a string local variable
                generateStatement(node.expression != null ? node.expression : new LiteralNode("\"\"")); // Default to empty string
                jasminCode.append("astore ").append(variableIndex).append("\n");
                symbolTable.put(varName, new VariableInfo("string", variableIndex, false, 1));
                variableIndex++;
                // String takes 1 slot
            } else if (typeNode.typeName.equals("integer")) {
                // If the type is 'integer', we store the value in an integer local variable
                generateStatement(node.expression != null ? node.expression : new LiteralNode(0));
                jasminCode.append("istore ").append(variableIndex).append("\n");
                symbolTable.put(varName, new VariableInfo("integer", variableIndex++, false, 1));  // Integer takes 1 slot
            } else if (typeNode.typeName.equals("boolean")) {
                // If the type is 'boolean', we store the value in a boolean local variable
                generateStatement(node.expression != null ? node.expression : new LiteralNode(false));
                jasminCode.append("istore ").append(variableIndex).append("\n");
                symbolTable.put(varName, new VariableInfo("boolean", variableIndex++, false, 1));  // Boolean takes 1 slot

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
            if (varInfo.type.equals("real") || varInfo.type.equals("R")) {
                jasminCode.append("dload ").append(varInfo.index).append("\n");
            } else if (varInfo.type.equals("string") || varInfo.type.equals("S")) {
                jasminCode.append("aload ").append(varInfo.index).append("\n");
            } else if (varInfo.type.equals("integer") || varInfo.type.equals("I")) {
                jasminCode.append("iload ").append(varInfo.index).append("\n");
            } else {
                throw new UnsupportedOperationException("Unsupported variable type: " + varInfo.type);
            }
        }
    }

    private void generateWhileLoop(WhileLoopNode node) {
        String startLabel = generateUniqueLabel();
        String endLabel = generateUniqueLabel();

        jasminCode.append(startLabel).append(":\n");

        generateStatement(node.condition);

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

        } else {
            throw new UnsupportedOperationException("While loop condition must be a binary operation.");
        }

        for (ASTNode statement : node.body) {
            generateStatement(statement);
        }

        jasminCode.append("goto ").append(startLabel).append("\n");

        jasminCode.append(endLabel).append(":\n");
    }

    private boolean isDoubleComparison(BinaryOperationNode node) {
        return isDoubleType(node.left) || isDoubleType(node.right);
    }

    private boolean isDoubleType(ASTNode node) {
        if (node instanceof LiteralNode literalNode) {
            return literalNode.value instanceof Double;
        } else if (node instanceof IdentifierNode identifierNode) {
            String varName = identifierNode.name;
            return symbolTable.containsKey(varName) && "real".equals(symbolTable.get(varName).type);
        }
        return false;
    }

    private void generateArrayDeclaration(ArrayDeclarationNode node) {
        jasminCode.append("ldc ").append(node.size).append("\n");
        jasminCode.append("newarray int\n"); // Поддержка массивов int
        symbolTable.put(node.identifier, new VariableInfo("int[]", variableIndex, true, node.size));
        jasminCode.append("astore ").append(variableIndex++).append("\n");
    }

    private void generateFunctionCall(FunctionCallNode node) {
        for (ASTNode arg : node.arguments) {
            generateStatement(arg);
        }
        jasminCode.append("invokestatic Main/").append(node.identifier)
                .append("(").append(getArgumentDescriptor(node.arguments)).append(")V\n");
    }

    private String getArgumentDescriptor(List<ASTNode> arguments) {
        StringBuilder descriptor = new StringBuilder();
        for (ASTNode arg : arguments) {
            if (arg instanceof LiteralNode literalNode) {
                if (literalNode.value instanceof Integer) {
                    descriptor.append("I");
                } else if (literalNode.value instanceof Double) {
                    descriptor.append("D");
                }
            }
        }
        return descriptor.toString();
    }

    private void generateRecordDeclaration(RecordDeclarationNode node) {
        jasminCode.append(".class public ").append(node.identifier).append("\n");
        jasminCode.append(".super java/lang/Object\n\n");

        // Register the record type in the symbolTable
        symbolTable.put(node.identifier, new VariableInfo("L" + node.identifier + ";", -1, false, 0));

        for (VarDeclarationNode field : node.fields) {
            jasminCode.append(".field public ").append(field.identifier)
                    .append(" ").append(getJasminType((TypeNode) field.type)).append("\n");
        }

        jasminCode.append(".method public <init>()V\n");
        jasminCode.append("aload_0\n");
        jasminCode.append("invokenonvirtual java/lang/Object/<init>()V\n");
        jasminCode.append("return\n");
        jasminCode.append(".end method\n\n");
    }

    private String getJasminType(TypeNode type) {
        return switch (type.typeName) {
            case "integer" -> "I";
            case "real" -> "D";
            case "string" -> "Ljava/lang/String;";
            default -> {
                if (symbolTable.containsKey(type.typeName)) {
                    yield "L" + type.typeName + ";"; // Reference type for user-defined records
                } else {
                    throw new UnsupportedOperationException("Unsupported type: " + type.typeName);
                }
            }
        };
    }

    private void generateForLoop(ForLoopNode node) {
        String startLabel = generateUniqueLabel();
        String endLabel = generateUniqueLabel();

        generateVarDeclaration(new VarDeclarationNode(
                node.identifier,
                new TypeNode("integer"),
                node.startExpression
        ));

        jasminCode.append(startLabel).append(":\n");

        jasminCode.append("iload ").append(symbolTable.get(node.identifier).index).append("\n");
        generateStatement(node.endExpression);

        jasminCode.append("if_icmpgt ").append(endLabel).append("\n");

        for (ASTNode statement : node.body) {
            generateStatement(statement);
        }

        jasminCode.append("iinc ").append(symbolTable.get(node.identifier).index).append(" 1\n");

        jasminCode.append("goto ").append(startLabel).append("\n");

        jasminCode.append(endLabel).append(":\n");
    }

    private record VariableInfo(String type, int index, boolean isArray, int arraySize) {
    }
}
