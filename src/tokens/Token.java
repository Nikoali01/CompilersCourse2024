package tokens;

public class Token {
    public TokenType type;
    TokenType commonType;
    public String text;
    Span span;

    public Token(Span span, TokenType type, String text) {
        this.span = span;
        this.type = type;
        this.text = text;
    }
}