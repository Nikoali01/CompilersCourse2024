import node.ProgramNode;
import semantic.DeclarationChecker;
import semantic.KeyWordUsageChecker;
import tokens.Token;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {

        File classFile = new File("/Users/emildavlityarov/Compilers/CompilerCourse2024/jasmin-2.4/Main.class");

        // Delete the file if it exists
        if (classFile.exists()) {
            boolean deleted = classFile.delete();
            if (deleted) {
                System.out.println("File deleted successfully.");
            } else {
                System.out.println("Failed to delete the file.");
            }
        } else {
            System.out.println("File does not exist.");
        }

        Lexer lexer = new Lexer();
        FileToString fileToString = new FileToString();
        String inputFileContent = fileToString.getStringFromTheLink("/Users/emildavlityarov/Compilers/CompilerCourse2024/src/1.i");
        List<Token> tokens = lexer.lex(inputFileContent);
        Parser parser = new Parser(tokens);
        ProgramNode program = parser.parse();

//        ASTOptimizer optimizer = new ASTOptimizer();
//        program = optimizer.optimize(program);
        KeyWordUsageChecker keyWordUsageChecker = new KeyWordUsageChecker();
        DeclarationChecker declarationChecker = new DeclarationChecker();
        keyWordUsageChecker.check(program);
//        declarationChecker.checkDeclarations(program);
        System.out.println(program.statements);

        JasminCodeGenerator generator = new JasminCodeGenerator();
        String jasminCode = generator.generate(program);
        try (FileWriter fileWriter = new FileWriter("Main.j")) {
            fileWriter.write(jasminCode);
        }
    }
}
