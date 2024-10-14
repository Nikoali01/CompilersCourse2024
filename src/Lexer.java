import java.util.*;
import java.util.regex.*;
import java.nio.file.*;
import java.io.*;

public class Lexer {
    private static final Map<TokenType, String> tokenPatterns = new LinkedHashMap<>();
    private static final Set<String> keywords = new HashSet<>();

    static {
        // Keywords from the language
        keywords.addAll(Arrays.asList(
                "var", "type", "routine", "is", "end", "record", "array", "while", "loop", "for", "to",
                "if", "then", "else", "elsif", "true", "false", "print", "return", "in"
        ));

        // Token patterns (regular expressions) for various tokens
        tokenPatterns.put(TokenType.STRING, "\"(\\\\.|[^\"\\\\])*\"");  // Strings
        tokenPatterns.put(TokenType.NUMBER, "\\d+(\\.\\d+)?");         // Numbers (integers and reals)
        tokenPatterns.put(TokenType.IDENTIFIER, "[a-zA-Z_][a-zA-Z0-9_]*"); // Identifiers

        // Operators and punctuation
        tokenPatterns.put(TokenType.PLUS, "\\+");
        tokenPatterns.put(TokenType.RANGE_OPERATOR, "\\.\\.");
        tokenPatterns.put(TokenType.MINUS, "-");
        tokenPatterns.put(TokenType.STAR, "\\*");
        tokenPatterns.put(TokenType.SLASH, "/");
        tokenPatterns.put(TokenType.ASSIGN, ":=");
        tokenPatterns.put(TokenType.EQUAL, "=");
        tokenPatterns.put(TokenType.NOT_EQUAL, "!=");
        tokenPatterns.put(TokenType.GREATER_EQUAL, ">=");
        tokenPatterns.put(TokenType.LESS_EQUAL, "<=");
        tokenPatterns.put(TokenType.LESS, "<");
        tokenPatterns.put(TokenType.GREATER, ">");
        tokenPatterns.put(TokenType.LPAREN, "\\(");
        tokenPatterns.put(TokenType.RPAREN, "\\)");
        tokenPatterns.put(TokenType.LBRACKET, "\\[");
        tokenPatterns.put(TokenType.RBRACKET, "\\]");
        tokenPatterns.put(TokenType.DOT, "\\.");
        tokenPatterns.put(TokenType.COMMA, ",");
        tokenPatterns.put(TokenType.COLON, ":");
        tokenPatterns.put(TokenType.SEMICOLON, ";");

        // Logical operators
        tokenPatterns.put(TokenType.AND, "and");
        tokenPatterns.put(TokenType.OR, "or");
        tokenPatterns.put(TokenType.NOT, "not");

        // Whitespace (ignored)
        tokenPatterns.put(TokenType.WHITESPACE, "\\s+");

        // End of input and unknown token
        tokenPatterns.put(TokenType.EOF, "$");
        tokenPatterns.put(TokenType.UNKNOWN, ".");
    }

    public List<Token> lex(String input) {
        List<Token> tokens = new ArrayList<>();
        StringBuilder tokenPatternBuilder = new StringBuilder();

        // Build the regex pattern by combining all token patterns
        for (Map.Entry<TokenType, String> entry : tokenPatterns.entrySet()) {
            tokenPatternBuilder.append(String.format("|(%s)", entry.getValue()));
        }
        Pattern tokenPattern = Pattern.compile(tokenPatternBuilder.substring(1));

        Matcher matcher = tokenPattern.matcher(input);
        long lineNum = 1;

        while (matcher.find()) {
            int begin = matcher.start();
            int end = matcher.end();

            Span span = new Span(lineNum, begin, end);
            String matchedText = matcher.group();
            TokenType type = TokenType.UNKNOWN;

            for (Map.Entry<TokenType, String> entry : tokenPatterns.entrySet()) {
                if (matchedText.matches(entry.getValue())) {
                    type = entry.getKey();
                    break;
                }
            }

            // Check if the matched text is a keyword
            if (type == TokenType.IDENTIFIER && keywords.contains(matchedText)) {
                type = TokenType.valueOf(matchedText.toUpperCase());
            }

            // Ignore whitespace tokens
            if (type == TokenType.WHITESPACE) {
                continue;
            }

            tokens.add(new Token(span, type, matchedText));

            if (type == TokenType.UNKNOWN) {
                System.out.println("Unknown token found: " + matchedText);
                System.exit(-1);
            }
        }

        return tokens;
    }

    public static void main(String[] args) throws IOException {
        Lexer lexer = new Lexer();
        String input = new String(Files.readAllBytes(Paths.get("path_to_your_source_file")));
        List<Token> tokens = lexer.lex(input);

        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
