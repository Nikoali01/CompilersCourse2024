package tokens;

public enum TokenType {
    // Single-character tokens
    PLUS, MINUS, STAR, SLASH, MOD, ASSIGN, EQUAL, NOT_EQUAL,
    LESS, LESS_EQUAL, GREATER, GREATER_EQUAL,
    LPAREN, RPAREN, LBRACKET, RBRACKET, DOT, COMMA, COLON, SEMICOLON,

    // Keywords
    VAR, IF, THEN, ELSE, ELSIF, END, WHILE, LOOP, FOR, TO,
    ROUTINE, IS, RETURNS, RETURN, PRINT, TYPE, RECORD, ARRAY,
    TRUE, FALSE, NOT, AND, OR,

    // Literals
    IDENTIFIER, NUMBER, STRING,

    // End of file
    EOF,

    UNKNOWN, WHITESPACE, IN, RANGE_OPERATOR
}


