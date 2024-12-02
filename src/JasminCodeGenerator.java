import node.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class JasminCodeGenerator {
    private StringBuilder completeJasmincode = new StringBuilder();
    private StringBuilder functionCode = new StringBuilder();
    private Map<String, VariableInfo> symbolTable = new HashMap<>();
    private Map<String, Map<String, String>> recordTypes = new HashMap<>();
    private Map<String, String> varRecord = new HashMap<>();
    private Map<String, String> functionParams = new HashMap<>();
    private List<String> generatedFiles = new ArrayList<>();
    private String sourceFileName;

    private int variableIndex = 0;

    JasminCodeGenerator(String filename) {
        sourceFileName = filename;
    }

    public static void printRecordTypes(Map<String, Map<String, String>> recordTypes) {
        // Обход внешнего Map (по рекордам)
        for (Map.Entry<String, Map<String, String>> recordEntry : recordTypes.entrySet()) {
            String recordName = recordEntry.getKey();
            Map<String, String> fields = recordEntry.getValue();

            System.out.println("Record: " + recordName);

            // Обход внутреннего Map (по полям и их типам)
            for (Map.Entry<String, String> fieldEntry : fields.entrySet()) {
                String fieldName = fieldEntry.getKey();
                String fieldType = fieldEntry.getValue();

                System.out.println("  Field: " + fieldName + " -> Type: " + fieldType);
            }
            System.out.println();  // Печатаем пустую строку после каждого рекорда
        }
    }

    public List<String> generate(ProgramNode program) throws IOException {
        completeJasmincode.append(".class public Main\n");
        completeJasmincode.append(".super java/lang/Object\n\n");
        completeJasmincode.append(".method public static main([Ljava/lang/String;)V\n");
        completeJasmincode.append(".limit stack 10\n");
        completeJasmincode.append(".limit locals 10\n");

        for (ASTNode statement : program.statements) {
            generateStatement(statement, completeJasmincode);
        }
        completeJasmincode.append("return\n");
        completeJasmincode.append(".end method\n");
        completeJasmincode.append(functionCode);
        printRecordTypes(recordTypes);
        writeToFile("Main.j", completeJasmincode.toString());
        return generatedFiles;
    }

    private void generateStatement(ASTNode node, StringBuilder jasminCode) throws IOException {
        if (node instanceof LiteralNode literalNode) {
            generateLiteral(literalNode, jasminCode);
        } else if (node instanceof VarDeclarationNode varNode) {
            generateVarDeclaration(varNode, jasminCode);
        } else if (node instanceof AssignmentNode assignmentNode) {
            generateAssignment(assignmentNode, jasminCode);
        } else if (node instanceof PrintStatementNode printNode) {
            generatePrint(printNode, jasminCode);
        } else if (node instanceof IdentifierNode identifierNode) {
            generateIdentifier(identifierNode, jasminCode);
        } else if (node instanceof BinaryOperationNode binaryNode) {
            generateBinaryOperation(binaryNode, jasminCode);
        } else if (node instanceof IfStatementNode ifNode) {
            generateIfStatement(ifNode, jasminCode);
        } else if (node instanceof ForLoopNode forLoopNode) {
            generateForLoop(forLoopNode, jasminCode);
        } else if (node instanceof WhileLoopNode whileLoopNode) {
            generateWhileLoop(whileLoopNode, jasminCode);
        } else if (node instanceof ArrayDeclarationNode arrayNode) {
            generateArrayDeclaration(arrayNode, jasminCode);
        } else if (node instanceof FunctionCallNode functionCallNode) {
            generateFunctionCall(functionCallNode, jasminCode);
        } else if (node instanceof RecordDeclarationNode recordNode) {
            generateRecordDeclaration(recordNode, jasminCode);
        } else if (node instanceof RoutineDeclarationNode routineNode) {
            generateRoutineDeclaration(routineNode, jasminCode);
        } else if (node instanceof ReturnStatementNode returnNode) {
            generateReturnStatement(returnNode, jasminCode);
        } else if (node instanceof LValueNode lvalueNode) {
            generateLValue(lvalueNode, jasminCode);
        } else {
            throw new UnsupportedOperationException("Unsupported ASTNode: " + node.getClass().getSimpleName());
        }
    }

    private void generateLValue(LValueNode node, StringBuilder jasminCode) throws IOException {
        generateStatement(node.base, jasminCode);
        if (node.field != null && node.base instanceof IdentifierNode identifierNode) {
            VariableInfo baseInfo = symbolTable.get(identifierNode.name);
            if (baseInfo == null || !baseInfo.type.startsWith("L")) {
                throw new UnsupportedOperationException("Base type must be a user-defined record.");
            }
            String fieldName = node.field;
            String varType = varRecord.get(((IdentifierNode) node.base).name);
            Map<String, String> recordVars = recordTypes.get(varType);
            String fieldType = recordVars.get(fieldName);
            jasminCode.append("getfield ")
                    .append(varType)
                    .append("/")
                    .append(fieldName)
                    .append(" ")
                    .append(fieldType)
                    .append("\n");

        } else if (node.base instanceof LValueNode) {
            generateLValue((LValueNode) node.base, jasminCode);

        }
    }

    private void generateReturnStatement(ReturnStatementNode node, StringBuilder jasminCode) throws IOException {
        if (node.expression != null) {
            generateStatement(node.expression, jasminCode);
            String returnType;
            if (node.expression instanceof BinaryOperationNode binaryNode) {
                if (checkDouble(binaryNode)) {
                    returnType = mapTypeToReturnInstruction("real");
                } else {
                    returnType = mapTypeToReturnInstruction("integer");
                }
            } else if (node.expression instanceof LValueNode lValueNode) {
                    // Array element access
                returnType = "ireturn";
                if (lValueNode.index instanceof LiteralNode literalNode) {
                    jasminCode.append("ldc ").append(literalNode.value);
                    jasminCode.append("\n");
                    jasminCode.append("iaload\n");
                }
            } else {
                returnType = mapTypeToReturnInstruction(node.expression.toString());
            }

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
            default -> "areturn";
//            default -> throw new UnsupportedOperationException("Unsupported return type: " + typeName);
        };
    }

    private String generateMethodDescriptor(RoutineDeclarationNode node) {
        StringBuilder descriptor = new StringBuilder("(");
        for (ParamNode param : node.params) {
            TypeNode test = (TypeNode) param.type;
            descriptor.append(mapTypeToDescriptor(test.typeName));
            variableIndex++;
        }
        descriptor.append(")");
        if (node.returnType != null) {
            TypeNode returnType = (TypeNode) node.returnType;
            descriptor.append(mapTypeToDescriptor(returnType.typeName));
        } else {
            descriptor.append("V");
        }
        return descriptor.toString();
    }

    private String mapTypeToDescriptor(String typeName) {
        return switch (typeName) {
            case "integer" -> "I";
            case "real" -> "D";
            case "string" -> "Ljava/lang/String;";
            case "boolean" -> "Z";
            default -> {
                if (symbolTable.containsKey(typeName)) {
                    yield "L" + typeName + ";";
                } else {
                    throw new UnsupportedOperationException("Unsupported type: " + typeName);
                }
            }
        };
    }

    private String generateReturnInstruction(String typeName) {
        return switch (typeName) {
            case "integer", "boolean" -> "ireturn\n";
            case "real" -> "dreturn\n";
            case "string" -> "areturn\n";
            default -> "return\n";
        };
    }

    private void generateRoutineDeclaration(RoutineDeclarationNode node, StringBuilder jasminCode) throws IOException {
        String methodName = node.identifier;
        String methodDescriptor = generateMethodDescriptor(node);
        functionCode.append(".method public static ").append(methodName).append(methodDescriptor).append("\n");
        functionParams.put(methodName, methodDescriptor);
        functionCode.append(".limit stack 10\n");
        functionCode.append(".limit locals ").append(10 + node.params.size()).append("\n");
        int paramIndex = 0;
        for (ParamNode param : node.params) {
            String paramType = ((TypeNode) param.type).typeName;
            String typeName = ((TypeNode) param.type).typeName;
            if (recordTypes.containsKey(typeName)) {
                varRecord.put(param.identifier, typeName);
                jasminCode.append("new ").append(typeName).append("\n");
                jasminCode.append("dup\n");
                jasminCode.append("invokespecial ").append(typeName).append("/<init>()V\n");
                jasminCode.append("astore ").append(variableIndex).append("\n");
                symbolTable.put(param.identifier, new VariableInfo("L" + typeName + ";", paramIndex++, false, 1));
            } else {
                symbolTable.put(param.identifier, new VariableInfo(paramType, paramIndex, false, 1));
                paramIndex += paramType.equals("D") ? 2 : 1;
            }
        }
        for (ASTNode statement : node.body) {
            generateStatement(statement, functionCode);
        }
        if (node.returnType != null) {
            functionCode.append(generateReturnInstruction(node.returnType.toString()));
        } else {
            functionCode.append("return\n");
        }
        functionCode.append(".end method\n\n");
    }

    private void generateFunctionCall(FunctionCallNode node, StringBuilder jasminCode) throws IOException {
        for (ASTNode arg : node.arguments) {
            generateStatement(arg, jasminCode);
        }
        jasminCode.append("invokestatic Main/")
                .append(node.identifier)
                .append(String.format("%s\n", functionParams.get(node.identifier)));
    }

    private void generateIfStatement(IfStatementNode node, StringBuilder jasminCode) throws IOException {
        String endLabel = generateUniqueLabel();
        String elseLabel = generateUniqueLabel();
        BinaryOperationNode binaryOperationNode = (BinaryOperationNode) node.condition;
        generateBinaryOperation(binaryOperationNode, jasminCode);
        switch (binaryOperationNode.operator) {
            case GREATER -> {
                jasminCode.append("ifle ").append(elseLabel).append("\n");
            }
            case LESS -> {
                jasminCode.append("ifge ").append(elseLabel).append("\n");
            }
            case EQUAL -> {
                jasminCode.append("ifne ").append(elseLabel).append("\n");
            }
            case GREATER_EQUAL -> {
                jasminCode.append("iflt ").append(elseLabel).append("\n");
            }
            case LESS_EQUAL -> {
                jasminCode.append("ifgt ").append(elseLabel).append("\n");
            }
            default ->
                    throw new UnsupportedOperationException("Unsupported operator in condition: " + binaryOperationNode.operator);
        }
        for (int i = 0; i < node.thenStatements.size(); i++) {
            generateStatement(node.thenStatements.get(i), jasminCode);
        }
//        generateStatement(node.thenStatements.getFirst());  // This processes the statements in the "then" block
        jasminCode.append("goto ").append(endLabel).append("\n");
        jasminCode.append(elseLabel).append(":\n");
        for (int i = 0; i < node.elseStatements.size(); i++) {
            generateStatement(node.elseStatements.get(i), jasminCode);
        }
//        generateStatement(node.elseStatements.getFirst());  // This processes the statements in the "else" block
        jasminCode.append(endLabel).append(":\n");
    }

    private String generateUniqueLabel() {
        return "L" + variableIndex++;
    }

    private boolean checkDouble(BinaryOperationNode node) {
        boolean isDouble = false;
        if (node.left instanceof LiteralNode left && left.value instanceof Double) {
            isDouble = true;
        } else if (node.left instanceof IdentifierNode leftIdentifier) {
            String varName = leftIdentifier.name;
            if (symbolTable.containsKey(varName) && symbolTable.get(varName).type.equals("real")) {
                isDouble = true;
            }
        } else if (node.left instanceof LValueNode left) {
            if (left.base instanceof IdentifierNode l) {
                String varName = l.name;
                VariableInfo a = symbolTable.get(varName);
                if (symbolTable.containsKey(varName) && a.type.equals("real")) {
                    isDouble = true;
                }
                String typeName = a.type.substring(1, a.type.length() - 1);
                String type = recordTypes.get(typeName).get(left.field);
                if (Objects.equals(type, "D")) {
                    isDouble = true;
                }
            }

        }

        if (node.right instanceof LiteralNode right && right.value instanceof Double) {
            isDouble = true;
        } else if (node.right instanceof IdentifierNode rightIdentifier) {
            String varName = rightIdentifier.name;
            if (symbolTable.containsKey(varName) && symbolTable.get(varName).type.equals("real")) {
                isDouble = true;
            }
        } else if (node.right instanceof LValueNode right) {
            if (right.base instanceof IdentifierNode r) {
                String varName = r.name;
                VariableInfo a = symbolTable.get(varName);
                if (symbolTable.containsKey(varName) && a.type.equals("real")) {
                    isDouble = true;
                }
                String typeName = a.type.substring(1, a.type.length() - 1);
                String type = recordTypes.get(typeName).get(right.field);
                if (Objects.equals(type, "D")) {
                    isDouble = true;
                }
            }
        }
        return isDouble;
    }

    private void generateBinaryOperation(BinaryOperationNode node, StringBuilder jasminCode) throws IOException {
        generateStatement(node.left, jasminCode);
        generateStatement(node.right, jasminCode);
        boolean isDouble = checkDouble(node);

        switch (node.operator) {
            case PLUS -> jasminCode.append(isDouble ? "dadd\n" : "iadd\n");
            case SLASH -> jasminCode.append(isDouble ? "ddiv\n" : "idiv\n");
            case MINUS -> jasminCode.append(isDouble ? "dsub\n" : "isub\n");
            case STAR -> jasminCode.append(isDouble ? "dmul\n" : "imul\n");
            case LESS_EQUAL -> {
                if (isDouble) {
                    jasminCode.append("dcmpg\n").append("ifle label_less_equal\n");
                } else {
                    jasminCode.append("if_icmple label_less_equal\n");
                }
            }
            case GREATER_EQUAL -> {
                if (isDouble) {
                    jasminCode.append("dcmpg\n").append("ifge label_greater_equal\n");
                } else {
                    jasminCode.append("if_icmpge label_greater_equal\n");
                }
            }
            case MOD -> {
                if (isDouble) {
                    jasminCode.append("drem\n");
                } else {
                    jasminCode.append("irem\n");
                }
            }
            case GREATER, EQUAL, LESS -> jasminCode.append("dcmpg\n");
            default -> throw new UnsupportedOperationException("Unsupported operator: " + node.operator);
        }
    }

    private void generateLiteral(LiteralNode node, StringBuilder jasminCode) {
        Object value = node.value;
        if (value instanceof Integer intValue) {
            jasminCode.append("ldc ").append(intValue).append("\n");
        } else if (value instanceof Double doubleValue) {
            jasminCode.append("ldc2_w ").append(doubleValue).append("\n");
        } else if (value instanceof String stringValue) {
            jasminCode.append("ldc ").append(stringValue).append("\n");
        } else if (value instanceof Boolean booleanValue) {
            jasminCode.append("iconst_").append(booleanValue ? 1 : 0).append("\n");
        } else if (value instanceof TypeNode typeNodeValue) {
            if (symbolTable.containsKey(typeNodeValue.typeName)) {
                jasminCode.append(String.format("new %s\n", typeNodeValue.typeName));
                jasminCode.append("dup\n");
                jasminCode.append(String.format("invokespecial %s/<init>()V\n", typeNodeValue.typeName));
            } else {
                jasminCode.append("aconst_null\n");
            }
        } else {
            throw new UnsupportedOperationException("Unsupported literal type: " + value.getClass().getSimpleName());
        }
    }

    private void generateVarDeclaration(VarDeclarationNode node, StringBuilder jasminCode) throws IOException {
        String varName = node.identifier;
        if (node.type instanceof TypeNode typeNode) {
            String typeName = typeNode.typeName;
            switch (typeName) {
                case "real":
                    generateStatement(node.expression != null ? node.expression : new LiteralNode(0.0), jasminCode);
                    jasminCode.append("dstore ").append(variableIndex).append("\n");
                    symbolTable.put(varName, new VariableInfo("real", variableIndex, false, 1));
                    variableIndex += 2; // Double занимает 2 слота
                    break;

                case "string":
                    generateStatement(node.expression != null ? node.expression : new LiteralNode("\"\""), jasminCode);
                    jasminCode.append("astore ").append(variableIndex).append("\n");
                    symbolTable.put(varName, new VariableInfo("string", variableIndex++, false, 1));
                    break;

                case "integer":
                    generateStatement(node.expression != null ? node.expression : new LiteralNode(0), jasminCode);
                    jasminCode.append("istore ").append(variableIndex).append("\n");
                    symbolTable.put(varName, new VariableInfo("integer", variableIndex++, false, 1));
                    break;

                case "boolean":
                    generateStatement(node.expression != null ? node.expression : new LiteralNode(false), jasminCode);
                    jasminCode.append("istore ").append(variableIndex).append("\n");
                    symbolTable.put(varName, new VariableInfo("boolean", variableIndex++, false, 1));
                    break;

                default:
                    if (symbolTable.containsKey(typeName)) {
                        varRecord.put(varName, typeName);
                        jasminCode.append("new ").append(typeName).append("\n");
                        jasminCode.append("dup\n");
                        jasminCode.append("invokespecial ").append(typeName).append("/<init>()V\n");
                        jasminCode.append("astore ").append(variableIndex).append("\n");
                        symbolTable.put(varName, new VariableInfo("L" + typeName + ";", variableIndex++, false, 1));
                    } else {
                        throw new UnsupportedOperationException("Unsupported type: " + typeName);
                    }
            }
        } else if (node.type instanceof ArrayTypeNode arrayNode) {
            String elementType = arrayNode.name;
            jasminCode.append("ldc ").append(arrayNode.size).append("\n");

            if ("integer".equals(elementType)) {
                jasminCode.append("newarray int\n");
            } else if ("real".equals(elementType)) {
                jasminCode.append("newarray double\n");
            } else if ("string".equals(elementType)) {
                jasminCode.append("anewarray java/lang/String\n");
            } else {
                throw new UnsupportedOperationException("Unsupported array type: " + elementType);
            }
            jasminCode.append("astore ").append(variableIndex).append("\n");
            symbolTable.put(varName, new VariableInfo(elementType + "[]", variableIndex++, true, arrayNode.size));
        }
    }

    private void generateAssignment(AssignmentNode node, StringBuilder jasminCode) throws IOException {
        if (node.lvalue instanceof IdentifierNode identifierNode) {
            generateStatement(node.expression, jasminCode);  // Generate code for the right-hand side expression
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
                        jasminCode.append("astore ").append(varInfo.index).append("\n");
//                        throw new UnsupportedOperationException("Unsupported variable type: " + varInfo.type);
                }
            }
        }
        if (node.lvalue instanceof LValueNode lvalueNode) {
            if (lvalueNode.field != null) {
                String varName = ((IdentifierNode) lvalueNode.base).name;
                String fieldName = lvalueNode.field;
                String varType = varRecord.get(((IdentifierNode) lvalueNode.base).name);
                Map<String, String> recordVars = recordTypes.get(varType);
                String fieldType = recordVars.get(fieldName);
                if (symbolTable.containsKey(varName)) {
                    VariableInfo varInfo = symbolTable.get(varName);
                    jasminCode.append("aload ").append(varInfo.index).append("\n");
                    generateStatement(node.expression, jasminCode);
                    jasminCode.append("putfield ").append(varType).append("/").append(fieldName).append(" ").append(fieldType).append("\n");
                }
            }

            if (lvalueNode.index != null) {
                String varName = ((IdentifierNode) lvalueNode.base).name;
                if (symbolTable.containsKey(varName)) {
                    VariableInfo varInfo = symbolTable.get(varName);
                    jasminCode.append("aload ").append(varInfo.index).append("\n");
                }
                generateStatement(lvalueNode.index, jasminCode);
                generateStatement(node.expression, jasminCode);
                jasminCode.append("iastore\n");
            }
        }
    }

    private void generatePrint(PrintStatementNode node, StringBuilder jasminCode) throws IOException {
        jasminCode.append("getstatic java/lang/System/out Ljava/io/PrintStream;\n");
        generateStatement(node.expression, jasminCode);
        if (node.expression instanceof IdentifierNode identifierNode) {
            if (symbolTable.get(identifierNode.name).type.equals("real")) {
                jasminCode.append("invokevirtual java/io/PrintStream/println(D)V\n");
            } else if (symbolTable.get(identifierNode.name).type.equals("integer")) {
                jasminCode.append("invokevirtual java/io/PrintStream/println(I)V\n");
            } else {
                jasminCode.append("invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\n");
            }
        } else if (node.expression instanceof LValueNode lValueNode) {
            handleLValuePrint(lValueNode, jasminCode);
        } else if (node.expression instanceof LiteralNode literalNode) {
            Object value = literalNode.value;
            if (value instanceof Integer intValue) {
                jasminCode.append("invokevirtual java/io/PrintStream/println(I)V\n");
            } else if (value instanceof Double doubleValue) {
                jasminCode.append("invokevirtual java/io/PrintStream/println(D)V\n");
            } else if (value instanceof String stringValue) {
                jasminCode.append("invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\n");
            }
        }
    }

    private void handleLValuePrint(LValueNode lValueNode, StringBuilder jasminCode) throws IOException {
        if (lValueNode.index == null) {
            // If it's not an array, resolve the field type and print
            String fieldType = resolveFieldType(lValueNode);
            if ("D".equals(fieldType)) {
                jasminCode.append("invokevirtual java/io/PrintStream/println(D)V\n");
            } else if ("I".equals(fieldType)) {
                jasminCode.append("invokevirtual java/io/PrintStream/println(I)V\n");
            } else if ("Ljava/lang/String;".equals(fieldType)) {
                jasminCode.append("invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\n");
            }
        } else {
            // If it's an array, process the array element
            if (!(lValueNode.base instanceof IdentifierNode identifierNode)) {
                throw new IllegalArgumentException("Array base must be an identifier.");
            }

            String varName = identifierNode.name;
            VariableInfo varInfo = symbolTable.get(varName);

            if (varInfo == null) {
                throw new IllegalArgumentException("Variable '" + varName + "' not found in symbolTable.");
            }

            // Load the array reference
//            jasminCode.append("aload ").append(varInfo.index).append("\n");

            // Load the index of the array
            generateStatement(lValueNode.index, jasminCode);

            // Load the element and print based on the array type
            if (varInfo.type.equals("integer[]")) {
                jasminCode.append("iaload\n");
                jasminCode.append("invokevirtual java/io/PrintStream/println(I)V\n");
            } else if (varInfo.type.equals("double[]")) {
                jasminCode.append("daload\n");
                jasminCode.append("invokevirtual java/io/PrintStream/println(D)V\n");
            } else if (varInfo.type.equals("Ljava/lang/String;[]")) {
                jasminCode.append("aaload\n");
                jasminCode.append("invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\n");
            } else {
                throw new UnsupportedOperationException("Unsupported array type: " + varInfo.type);
            }
        }
    }



    private String resolveFieldType(LValueNode lValueNode) {
        if (lValueNode.base instanceof IdentifierNode identifierNode) {
            String varType = varRecord.get(identifierNode.name);
            Map<String, String> recordVars = recordTypes.get(varType);
            return recordVars.get(lValueNode.field);
        } else if (lValueNode.base instanceof LValueNode nestedLValue) {
            return resolveFieldType(null);//нужно исправить
        }
        throw new UnsupportedOperationException("Unsupported base type for LValue.");
    }

    private void generateIdentifier(IdentifierNode node, StringBuilder jasminCode) {
        String varName = node.name;
        if (symbolTable.containsKey(varName)) {
            VariableInfo varInfo = symbolTable.get(varName);
            if (varInfo.type.equals("real") || varInfo.type.equals("R")) {
                jasminCode.append("dload ").append(varInfo.index).append("\n");
            } else if (varInfo.type.equals("string") || varInfo.type.equals("S")) {
                jasminCode.append("aload ").append(varInfo.index).append("\n");
            } else if (varInfo.type.equals("integer") || varInfo.type.equals("I")) {
                jasminCode.append("iload ").append(varInfo.index).append("\n");
            } else if (varInfo.type.startsWith("L")) { // Handle custom object types
                jasminCode.append("aload ").append(varInfo.index).append("\n");
            } else if (varInfo.type.endsWith("[]")) { // Handle custom object types
                jasminCode.append("aload ").append(varInfo.index).append("\n");
            } else {
                throw new UnsupportedOperationException("Unsupported variable type: " + varInfo.type);
            }
        }
    }

    private void generateWhileLoop(WhileLoopNode node, StringBuilder jasminCode) throws IOException {
        String startLabel = generateUniqueLabel();
        String endLabel = generateUniqueLabel();
        jasminCode.append(startLabel).append(":\n");
        generateStatement(node.condition, jasminCode);
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
            generateStatement(statement, jasminCode);
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

    private void generateArrayDeclaration(ArrayDeclarationNode node, StringBuilder jasminCode) {
        jasminCode.append("ldc ").append(node.size).append("\n");
        jasminCode.append("newarray int\n");
        symbolTable.put(node.identifier, new VariableInfo("int[]", variableIndex, true, node.size));
        jasminCode.append("astore ").append(variableIndex++).append("\n");
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

    private void generateRecordDeclaration(RecordDeclarationNode node, StringBuilder mainCode) throws IOException {
        StringBuilder recordCode = new StringBuilder();
        symbolTable.put(node.identifier, new VariableInfo("L" + node.identifier + ";", -1, false, 0));
        recordCode.append(".class public ").append(node.identifier).append("\n");
        recordCode.append(".super java/lang/Object\n\n");
        Map<String, String> fieldTypes = new HashMap<>();
        for (VarDeclarationNode field : node.fields) {
            String fieldType = getJasminType((TypeNode) field.type);
            fieldTypes.put(field.identifier, fieldType);
            recordCode.append(".field public ").append(field.identifier)
                    .append(" ").append(fieldType).append("\n");
        }
        recordTypes.put(node.identifier, fieldTypes);
        recordCode.append(".method public <init>()V\n");
        recordCode.append(".limit stack 10\n");
        recordCode.append(".limit locals 10\n");
        recordCode.append("    aload_0\n");
        recordCode.append("    invokenonvirtual java/lang/Object/<init>()V\n");

        for (VarDeclarationNode field : node.fields) {
            recordCode.append("    aload_0\n");
            String fieldType = getJasminType((TypeNode) field.type);
            if (fieldType.equals("I")) {
                recordCode.append("    iconst_0\n");
            } else if (fieldType.equals("D")) {
                recordCode.append("    dconst_0\n");
            } else {
                recordCode.append("    aconst_null\n");
            }
            recordCode.append("    putfield ").append(node.identifier).append("/")
                    .append(field.identifier).append(" ").append(fieldType).append("\n");
        }
        recordCode.append("    return\n");
        recordCode.append(".end method\n");
        writeToFile(node.identifier + ".j", recordCode.toString());
    }

    private void writeToFile(String fileName, String content) throws IOException {
        String path = "output/" + sourceFileName + "/" + fileName;
        String dirPath = "output/" + sourceFileName;

        File dir = new File(dirPath);
        if (!dir.exists()) {
            boolean dirCreated = dir.mkdirs();
            if (dirCreated) {
                System.out.println("Directory created: " + dir.getAbsolutePath());
            } else {
                System.out.println("Error while creating directory");
                System.exit(-1);
            }
        }

        File file = new File(path);
        if (!file.exists()) {
            System.out.println("File not found. Creating new file: " + file.getAbsolutePath());
            boolean created = file.createNewFile();
            if (created) {
                System.out.println("File created.");
            } else {
                System.out.println("Error.");
                System.exit(-1);
            }
        }
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(content);
            System.out.println("Record class generated: " + fileName);
            generatedFiles.add(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getJasminType(TypeNode type) {
        return switch (type.typeName) {
            case "integer" -> "I";
            case "real" -> "D";
            case "string" -> "Ljava/lang/String;";
            case "boolean" -> "Z";
            default -> {
                if (symbolTable.containsKey(type.typeName)) {
                    yield "L" + type.typeName + ";";
                } else {
                    throw new UnsupportedOperationException("Unsupported type: " + type.typeName);
                }
            }
        };
    }

    private void generateForLoop(ForLoopNode node, StringBuilder jasminCode) throws IOException {
        String startLabel = generateUniqueLabel();
        String endLabel = generateUniqueLabel();
        generateVarDeclaration(new VarDeclarationNode(
                node.identifier,
                new TypeNode("integer"),
                node.startExpression
        ), jasminCode);
        jasminCode.append(startLabel).append(":\n");
        jasminCode.append("iload ").append(symbolTable.get(node.identifier).index).append("\n");
        generateStatement(node.endExpression, jasminCode);
        jasminCode.append("if_icmpgt ").append(endLabel).append("\n");
        for (ASTNode statement : node.body) {
            generateStatement(statement, jasminCode);
        }
        jasminCode.append("iinc ").append(symbolTable.get(node.identifier).index).append(" 1\n");
        jasminCode.append("goto ").append(startLabel).append("\n");
        jasminCode.append(endLabel).append(":\n");
    }

    private record VariableInfo(String type, int index, boolean isArray, int arraySize) {
    }
}
