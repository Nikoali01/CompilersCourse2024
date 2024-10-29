import node.ProgramNode;
import optimization.ASTOptimizer;
import tokens.Token;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Lexer lexer = new Lexer();
        FileToString fileToString = new FileToString();
        String inputFileContent = fileToString.getStringFromTheLink("/Users/emildavlityarov/backend/CompilersCourse2024/src/1.i");
        List<Token> tokens = lexer.lex(inputFileContent);
        Parser parser = new Parser(tokens);
        ProgramNode program = parser.parse();

        ASTOptimizer optimizer = new ASTOptimizer();
        program = optimizer.optimize(program);
        System.out.println(program);

    }
}
