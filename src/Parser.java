import node.*;
import tokens.Token;
import tokens.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private List<Token> tokens;
    private int current;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.current = 0;
    }

    private Token consume(TokenType type, String errorMessage) {
        System.out.println(type);
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

    private ASTNode parseAssignmentOrFunctionCall() {
        ASTNode lvalue = parseLValue();
        if (match(TokenType.ASSIGN)) {
            ASTNode expression = parseExpression();
            consume(TokenType.SEMICOLON, "Expected semicolon");
            return new AssignmentNode(lvalue, expression);
        }

        if (match(TokenType.LPAREN)) {
            List<ASTNode> arguments = new ArrayList<>();
            if (!check(TokenType.RPAREN)) {
                do {
                    arguments.add(parseExpression());
                } while (match(TokenType.COMMA));
            }
            consume(TokenType.RPAREN, "Expected closing parenthesis");
            consume(TokenType.SEMICOLON, "Expected semicolon after function call");
            return new FunctionCallNode(((IdentifierNode) lvalue).name, arguments);
        }
        throw new RuntimeException("Expected assignment or function call");
    }

    private ASTNode parseLValue() {
        ASTNode base = new IdentifierNode(consume(TokenType.IDENTIFIER, "Expected identifier").text);
        while (match(TokenType.DOT, TokenType.LBRACKET)) {
            if (previous().type == TokenType.DOT) {
                base = new LValueNode(base, consume(TokenType.IDENTIFIER, "Expected field").text, null);
            } else {
                ASTNode index = parseExpression();
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
        consume(TokenType.IN, "Expected 'in'");
        ASTNode startExpression = parseExpression();
        consume(TokenType.RANGE_OPERATOR, "Expected '..'");
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

        List<ParamNode> params = new ArrayList<>();
        if (!check(TokenType.RPAREN)) {
            do {
                Token paramIdentifier = consume(TokenType.IDENTIFIER, "Expected parameter name");
                consume(TokenType.COLON, "Expected ':' after parameter name");
                ASTNode type = parseType();
                params.add(new ParamNode(paramIdentifier.text, type));
            } while (match(TokenType.COMMA));
        }
        consume(TokenType.RPAREN, "Expected ')' after parameters");

        ASTNode returnType = null;
        if (match(TokenType.COLON)) {
            returnType = parseType();
        }

        consume(TokenType.IS, "Expected 'is' before routine body");
        List<ASTNode> body = parseBlock();
        consume(TokenType.END, "Expected 'end' after routine body");
        consume(TokenType.SEMICOLON, "Expected semicolon after 'end'");
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
            System.out.printf(identifier.text);
            return parseRecordDeclaration(identifier);
        } else if (match(TokenType.ARRAY)) {
            return parseArrayDeclaration(identifier);
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
        while (match(TokenType.STAR, TokenType.SLASH, TokenType.MOD)) {
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
            if (previous().text.contains(".")) {
                return new LiteralNode(Double.parseDouble(previous().text));
            }
            return new LiteralNode(Integer.parseInt(previous().text));
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
            String identifier = previous().text;

            ASTNode base = new IdentifierNode(identifier);

            base = handlePropertyAccess(base);

            while (match(TokenType.LBRACKET)) {
                ASTNode index = parseExpression();
                consume(TokenType.RBRACKET, "Expected closing bracket");
                base = new LValueNode(base, null, index);
            }

            base = handlePropertyAccess(base);

            if (match(TokenType.LPAREN)) {
                List<ASTNode> arguments = new ArrayList<>();
                if (!check(TokenType.RPAREN)) {
                    do {
                        arguments.add(parseExpression());
                    } while (match(TokenType.COMMA));
                }
                consume(TokenType.RPAREN, "Expected closing parenthesis");
                return new FunctionCallNode(identifier, arguments);
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


    private ASTNode handlePropertyAccess(ASTNode base) {
        while (match(TokenType.DOT)) {
            String propertyName = consume(TokenType.IDENTIFIER, "Expected property name").text;
            base = new LValueNode(base, propertyName, null);
        }
        return base;
    }

    private ASTNode parseArrayDeclaration(Token identifier) {
        consume(TokenType.LBRACKET, "Expected '['");
        Token sizeToken = consume(TokenType.NUMBER, "Expected array size");
        consume(TokenType.RBRACKET, "Expected ']'");
        ASTNode type = parseType();
        consume(TokenType.SEMICOLON, "Expected semicolon");
        return new ArrayDeclarationNode(identifier.text, Integer.parseInt(sizeToken.text), type);
    }

    private ASTNode parseRecordDeclaration(Token identifier) {
        List<VarDeclarationNode> fields = new ArrayList<>();
        while (!check(TokenType.END)) {
            advance();
            fields.add(parseVarDeclaration());
        }
        consume(TokenType.END, "Expected 'end'");
        consume(TokenType.SEMICOLON, "Expected semicolon");
        return new RecordDeclarationNode(identifier.text, fields);
    }


    private ASTNode parseType() {
        if (match(TokenType.ARRAY)) {
            consume(TokenType.LBRACKET, "Expected '['");
            Token sizeToken = consume(TokenType.NUMBER, "Expected array size");
            consume(TokenType.RBRACKET, "Expected ']'");
            Token typeName = consume(TokenType.IDENTIFIER, "Expected type name");
            return new ArrayTypeNode(typeName.text, Integer.parseInt(sizeToken.text));
        }

        Token typeName = consume(TokenType.IDENTIFIER, "Expected type name");
        return new TypeNode(typeName.text);
    }
}
