import java.util.List;

public class Main {
    public static void main(String[] args) {
        Lexer lexer = new Lexer();
        FileToString fileToString = new FileToString();
        String inputFileContent = fileToString.getStringFromTheLink("/Users/emildavlityarov/CompilersCourse2024/LexicalSyntaxAnalyzer/src/12.i");
        List<Token> tokens = lexer.lex(inputFileContent);
        Parser parser = new Parser(tokens);
        ProgramNode program = parser.parse();
        System.out.println(program);
//        lexer.generateModifiedFile("12.i", "12_lex.i");
    }
}
