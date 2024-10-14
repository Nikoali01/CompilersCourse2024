// Определение токенов
class Token {
    TokenType type;
    TokenType commonType;
    String text;
    Span span;

    public Token(Span span, TokenType type, String text) {
        this.span = span;
        this.type = type;
        this.text = text;
    }
}

// Перечисление типов токено
