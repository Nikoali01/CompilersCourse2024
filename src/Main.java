import node.ProgramNode;
import optimization.ASTOptimizer;
import semantic.DeclarationChecker;
import semantic.KeyWordUsageChecker;
import tokens.Token;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        Lexer lexer = new Lexer();
        FileToString fileToString = new FileToString();
        String inputFileContent = fileToString.getStringFromTheLink("/Users/andrey/study/compilers/CompilersCourse2024/src/1.i");
        List<Token> tokens = lexer.lex(inputFileContent);
        Parser parser = new Parser(tokens);
        ProgramNode program = parser.parse();

        ASTOptimizer optimizer = new ASTOptimizer();
        program = optimizer.optimize(program);
        KeyWordUsageChecker keyWordUsageChecker = new KeyWordUsageChecker();
        DeclarationChecker declarationChecker = new DeclarationChecker();
        keyWordUsageChecker.check(program);
        declarationChecker.checkDeclarations(program);
        System.out.println(program);

        JasminCodeGenerator generator = new JasminCodeGenerator();
        String jasminCode = generator.generate(program);
        Files.writeString(Path.of("1.j"), jasminCode);


    }
}
