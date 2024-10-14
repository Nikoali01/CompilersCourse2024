import java.util.List;
import java.util.ArrayList;

// Определение узлов для AST
abstract class ASTNode {}

class ProgramNode extends ASTNode {
    List<ASTNode> statements;

    ProgramNode(List<ASTNode> statements) {
        this.statements = statements;
    }
}

class VarDeclarationNode extends ASTNode {
    String identifier;
    ASTNode type;
    ASTNode expression;

    VarDeclarationNode(String identifier, ASTNode type, ASTNode expression) {
        this.identifier = identifier;
        this.type = type;
        this.expression = expression;
    }
}

class AssignmentNode extends ASTNode {
    ASTNode lvalue;
    ASTNode expression;

    AssignmentNode(ASTNode lvalue, ASTNode expression) {
        this.lvalue = lvalue;
        this.expression = expression;
    }
}

class IfStatementNode extends ASTNode {
    ASTNode condition;
    List<ASTNode> thenStatements;
    List<ASTNode> elsifStatements;
    List<ASTNode> elseStatements;

    IfStatementNode(ASTNode condition, List<ASTNode> thenStatements, List<ASTNode> elsifStatements, List<ASTNode> elseStatements) {
        this.condition = condition;
        this.thenStatements = thenStatements;
        this.elsifStatements = elsifStatements;
        this.elseStatements = elseStatements;
    }
}

class WhileLoopNode extends ASTNode {
    ASTNode condition;
    List<ASTNode> body;

    WhileLoopNode(ASTNode condition, List<ASTNode> body) {
        this.condition = condition;
        this.body = body;
    }
}

class ForLoopNode extends ASTNode {
    String identifier;
    ASTNode startExpression;
    ASTNode endExpression;
    List<ASTNode> body;

    ForLoopNode(String identifier, ASTNode startExpression, ASTNode endExpression, List<ASTNode> body) {
        this.identifier = identifier;
        this.startExpression = startExpression;
        this.endExpression = endExpression;
        this.body = body;
    }
}

class RoutineDeclarationNode extends ASTNode {
    String identifier;
    List<ParamNode> params;
    ASTNode returnType;
    List<ASTNode> body;

    RoutineDeclarationNode(String identifier, List<ParamNode> params, ASTNode returnType, List<ASTNode> body) {
        this.identifier = identifier;
        this.params = params;
        this.returnType = returnType;
        this.body = body;
    }
}

class ParamNode extends ASTNode {
    String identifier;
    ASTNode type;

    ParamNode(String identifier, ASTNode type) {
        this.identifier = identifier;
        this.type = type;
    }
}

class ReturnStatementNode extends ASTNode {
    ASTNode expression;

    ReturnStatementNode(ASTNode expression) {
        this.expression = expression;
    }
}

class PrintStatementNode extends ASTNode {
    ASTNode expression;

    PrintStatementNode(ASTNode expression) {
        this.expression = expression;
    }
}

class RecordDeclarationNode extends ASTNode {
    String identifier;
    List<VarDeclarationNode> fields;

    RecordDeclarationNode(String identifier, List<VarDeclarationNode> fields) {
        this.identifier = identifier;
        this.fields = fields;
    }
}

class ArrayDeclarationNode extends ASTNode {
    String identifier;
    int size;
    ASTNode type;

    ArrayDeclarationNode(String identifier, int size, ASTNode type) {
        this.identifier = identifier;
        this.size = size;
        this.type = type;
    }
}

class FunctionCallNode extends ASTNode {
    String identifier;
    List<ASTNode> arguments;

    FunctionCallNode(String identifier, List<ASTNode> arguments) {
        this.identifier = identifier;
        this.arguments = arguments;
    }
}

class TypeNode extends ASTNode {
    String typeName;

    TypeNode(String typeName) {
        this.typeName = typeName;
    }
}

class BinaryOperationNode extends ASTNode {
    ASTNode left;
    TokenType operator;
    ASTNode right;

    BinaryOperationNode(ASTNode left, TokenType operator, ASTNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
}

class UnaryOperationNode extends ASTNode {
    TokenType operator;
    ASTNode operand;

    UnaryOperationNode(TokenType operator, ASTNode operand) {
        this.operator = operator;
        this.operand = operand;
    }
}

class LiteralNode extends ASTNode {
    Object value;

    LiteralNode(Object value) {
        this.value = value;
    }
}

class IdentifierNode extends ASTNode {
    String name;

    IdentifierNode(String name) {
        this.name = name;
    }
}

class LValueNode extends ASTNode {
    ASTNode base;
    String field;
    ASTNode index;

    LValueNode(ASTNode base, String field, ASTNode index) {
        this.base = base;
        this.field = field;
        this.index = index;
    }
}

// Парсер
public class Parser {
    private List<Token> tokens;
    private int current;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.current = 0;
    }

    private Token consume(TokenType type, String errorMessage) {
        Token token = peek();
        if (token.type == type) {
            advance();
            return token;
        }
        throw new RuntimeException(errorMessage);
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        return !isAtEnd() && peek().type == type;
    }

    public ProgramNode parse() {
        List<ASTNode> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(parseStatement());
        }
        return new ProgramNode(statements);
    }

    private ASTNode parseStatement() {
        if (match(TokenType.VAR)) return parseVarDeclaration();
        if (match(TokenType.IF)) return parseIfStatement();
        if (match(TokenType.WHILE)) return parseWhileLoop();
        if (match(TokenType.FOR)) return parseForLoop();
        if (match(TokenType.ROUTINE)) return parseRoutineDeclaration();
        if (match(TokenType.RETURN)) return parseReturnStatement();
        if (match(TokenType.PRINT)) return parsePrintStatement();
        if (match(TokenType.TYPE)) return parseRecordOrArrayDeclaration();
        return parseAssignmentOrFunctionCall();
    }

    private VarDeclarationNode parseVarDeclaration() {
        Token identifier = consume(TokenType.IDENTIFIER, "Expected identifier");
        ASTNode type = null;

        if (match(TokenType.COLON)) {
            type = parseType();
        }

        ASTNode expression = null;
        if (match(TokenType.IS)) {
            expression = parseExpression();
        }
        consume(TokenType.SEMICOLON, "Expected semicolon");
        return new VarDeclarationNode(identifier.text, type, expression);
    }

    private AssignmentNode parseAssignmentOrFunctionCall() {
        ASTNode lvalue = parseLValue();
        if (match(TokenType.ASSIGN)) {
            ASTNode expression = parseExpression();
            consume(TokenType.SEMICOLON, "Expected semicolon");
            return new AssignmentNode(lvalue, expression);
        }
        throw new RuntimeException("Expected assignment");
    }

    private ASTNode parseLValue() {
        ASTNode base = new IdentifierNode(consume(TokenType.IDENTIFIER, "Expected identifier").text);
        while (match(TokenType.DOT, TokenType.LBRACKET)) {
            if (previous().type == TokenType.DOT) {
                base = new LValueNode(base, consume(TokenType.IDENTIFIER, "Expected field").text, null);
            } else {
                ASTNode index = parseExpression(); // Ensure this parses the expression correctly
                consume(TokenType.RBRACKET, "Expected closing bracket");
                base = new LValueNode(base, null, index);
            }
        }
        return base;
    }


    private IfStatementNode parseIfStatement() {
        ASTNode condition = parseExpression();
        consume(TokenType.THEN, "Expected 'then'");
        List<ASTNode> thenStatements = parseBlock();
        List<ASTNode> elsifStatements = new ArrayList<>();
        while (match(TokenType.ELSIF)) {
            ASTNode elsifCondition = parseExpression();
            consume(TokenType.THEN, "Expected 'then'");
            elsifStatements.add(new IfStatementNode(elsifCondition, parseBlock(), null, null));
        }
        List<ASTNode> elseStatements = new ArrayList<>();
        if (match(TokenType.ELSE)) {
            elseStatements = parseBlock();
        }
        consume(TokenType.END, "Expected 'end'");
        consume(TokenType.SEMICOLON, "Expected semicolon");
        return new IfStatementNode(condition, thenStatements, elsifStatements, elseStatements);
    }

    private WhileLoopNode parseWhileLoop() {
        ASTNode condition = parseExpression();
        consume(TokenType.LOOP, "Expected 'loop'");
        List<ASTNode> body = parseBlock();
        consume(TokenType.END, "Expected 'end'");
        consume(TokenType.SEMICOLON, "Expected semicolon");
        return new WhileLoopNode(condition, body);
    }

    private ForLoopNode parseForLoop() {
        Token identifier = consume(TokenType.IDENTIFIER, "Expected identifier");
        consume(TokenType.IN, "Expected 'in'");  // Ожидаем 'in'
        ASTNode startExpression = parseExpression();
        consume(TokenType.RANGE_OPERATOR, "Expected '..'");  // Ожидаем диапазон '..'
        ASTNode endExpression = parseExpression();
        consume(TokenType.LOOP, "Expected 'loop'");
        List<ASTNode> body = parseBlock();
        consume(TokenType.END, "Expected 'end'");
        consume(TokenType.SEMICOLON, "Expected semicolon");
        return new ForLoopNode(identifier.text, startExpression, endExpression, body);
    }


    private RoutineDeclarationNode parseRoutineDeclaration() {
        Token identifier = consume(TokenType.IDENTIFIER, "Expected identifier for routine");
        consume(TokenType.LPAREN, "Expected '(' after routine name");

        // Parse the parameters
        List<ParamNode> params = new ArrayList<>();
        if (!check(TokenType.RPAREN)) {
            do {
                Token paramIdentifier = consume(TokenType.IDENTIFIER, "Expected parameter name");
                consume(TokenType.COLON, "Expected ':' after parameter name");
                ASTNode type = parseType();
                params.add(new ParamNode(paramIdentifier.text, type));
            } while (match(TokenType.COMMA)); // Handle multiple parameters
        }
        consume(TokenType.RPAREN, "Expected ')' after parameters");

        // Parse return type (if any)
        ASTNode returnType = null;
        if (match(TokenType.COLON)) {
            returnType = parseType();
        }

        // Parse body of the routine
        consume(TokenType.IS, "Expected 'is' before routine body");
        List<ASTNode> body = parseBlock();  // Routine body is a block of statements
        consume(TokenType.END, "Expected 'end' after routine body");
        consume(TokenType.SEMICOLON, "Expected semicolon after 'end'");

        // Return the constructed RoutineDeclarationNode
        return new RoutineDeclarationNode(identifier.text, params, returnType, body);
    }


    private ReturnStatementNode parseReturnStatement() {
        ASTNode expression = null;
        if (!check(TokenType.SEMICOLON)) {
            expression = parseExpression();
        }
        consume(TokenType.SEMICOLON, "Expected semicolon");
        return new ReturnStatementNode(expression);
    }

    private PrintStatementNode parsePrintStatement() {
        ASTNode expression = parseExpression();
        consume(TokenType.SEMICOLON, "Expected semicolon");
        return new PrintStatementNode(expression);
    }

    private ASTNode parseRecordOrArrayDeclaration() {
        Token identifier = consume(TokenType.IDENTIFIER, "Expected identifier");
        consume(TokenType.IS, "Expected 'is'");

        if (match(TokenType.RECORD)) {
            List<VarDeclarationNode> fields = new ArrayList<>();
            while (!check(TokenType.END)) {
                fields.add(parseVarDeclaration());
            }
            consume(TokenType.END, "Expected 'end'");
            consume(TokenType.SEMICOLON, "Expected semicolon");
            return new RecordDeclarationNode(identifier.text, fields);

        } else if (match(TokenType.ARRAY)) {
            consume(TokenType.LBRACKET, "Expected '['");
            Token sizeToken = consume(TokenType.NUMBER, "Expected array size");
            consume(TokenType.RBRACKET, "Expected ']'");
            consume(TokenType.OF, "Expected 'of'");
            ASTNode type = parseType();  // Parse the type here after "of"
            consume(TokenType.SEMICOLON, "Expected semicolon");
            return new ArrayDeclarationNode(identifier.text, Integer.parseInt(sizeToken.text), type);
        }
        throw new RuntimeException("Expected 'record' or 'array'");
    }

    private List<ASTNode> parseBlock() {
        List<ASTNode> statements = new ArrayList<>();
        while (!check(TokenType.END) && !check(TokenType.ELSIF) && !check(TokenType.ELSE) && !isAtEnd()) {
            statements.add(parseStatement());
        }
        return statements;
    }

    private ASTNode parseExpression() {
        return parseLogicalOr();
    }

    private ASTNode parseLogicalOr() {
        ASTNode left = parseLogicalAnd();
        while (match(TokenType.OR)) {
            TokenType operator = previous().type;
            ASTNode right = parseLogicalAnd();
            left = new BinaryOperationNode(left, operator, right);
        }
        return left;
    }

    private ASTNode parseLogicalAnd() {
        ASTNode left = parseEquality();
        while (match(TokenType.AND)) {
            TokenType operator = previous().type;
            ASTNode right = parseEquality();
            left = new BinaryOperationNode(left, operator, right);
        }
        return left;
    }

    private ASTNode parseEquality() {
        ASTNode left = parseComparison();
        while (match(TokenType.EQUAL, TokenType.NOT_EQUAL)) {
            TokenType operator = previous().type;
            ASTNode right = parseComparison();
            left = new BinaryOperationNode(left, operator, right);
        }
        return left;
    }

    private ASTNode parseComparison() {
        ASTNode left = parseTerm();
        while (match(TokenType.LESS, TokenType.LESS_EQUAL, TokenType.GREATER, TokenType.GREATER_EQUAL)) {
            TokenType operator = previous().type;
            ASTNode right = parseTerm();
            left = new BinaryOperationNode(left, operator, right);
        }
        return left;
    }

    private ASTNode parseTerm() {
        ASTNode left = parseFactor();
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            TokenType operator = previous().type;
            ASTNode right = parseFactor();
            left = new BinaryOperationNode(left, operator, right);
        }
        return left;
    }

    private ASTNode parseFactor() {
        ASTNode left = parseUnary();
        while (match(TokenType.STAR, TokenType.SLASH)) {
            TokenType operator = previous().type;
            ASTNode right = parseUnary();
            left = new BinaryOperationNode(left, operator, right);
        }
        return left;
    }

    private ASTNode parseUnary() {
        if (match(TokenType.NOT, TokenType.MINUS)) {
            TokenType operator = previous().type;
            ASTNode operand = parsePrimary();
            return new UnaryOperationNode(operator, operand);
        }
        return parsePrimary();
    }

    private ASTNode parsePrimary() {
        if (match(TokenType.NUMBER)) {
            return new LiteralNode(Double.parseDouble(previous().text));
        }
        if (match(TokenType.STRING)) {
            return new LiteralNode(previous().text);
        }
        if (match(TokenType.TRUE)) {
            return new LiteralNode(true);
        }
        if (match(TokenType.FALSE)) {
            return new LiteralNode(false);
        }
        if (match(TokenType.IDENTIFIER)) {
            // Check for array access right after identifier
            ASTNode base = new IdentifierNode(previous().text);
            while (match(TokenType.LBRACKET)) {
                ASTNode index = parseExpression();  // Parse the index expression
                consume(TokenType.RBRACKET, "Expected closing bracket");
                base = new LValueNode(base, null, index);  // Create LValueNode for array access
            }
            return base;
        }
        if (match(TokenType.LPAREN)) {
            ASTNode expression = parseExpression();
            consume(TokenType.RPAREN, "Expected ')'");
            return expression;
        }
        throw new RuntimeException("Expected expression");
    }

    private ASTNode parseType() {
        // Check for array type
        if (match(TokenType.ARRAY)) {
            consume(TokenType.LBRACKET, "Expected '['");
            Token sizeToken = consume(TokenType.NUMBER, "Expected array size");
            consume(TokenType.RBRACKET, "Expected ']'");
//            consume(TokenType.OF, "Expected 'of'");
            Token typeName = consume(TokenType.IDENTIFIER, "Expected type name");

            // Return an ArrayTypeNode that contains the size and type
            return new ArrayTypeNode(typeName.text, Integer.parseInt(sizeToken.text));
        }

        // For other types, we expect an identifier
        Token typeName = consume(TokenType.IDENTIFIER, "Expected type name");
        return new TypeNode(typeName.text);
    }
}
