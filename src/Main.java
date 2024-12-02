import node.ProgramNode;
import optimization.ASTOptimizer;
import semantic.DeclarationChecker;
import semantic.KeyWordUsageChecker;
import tokens.Token;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {

        Lexer lexer = new Lexer();
        FileToString fileToString = new FileToString();
        String sourceFileName = "src/1.i";
        String inputFileContent = fileToString.getStringFromTheLink(sourceFileName);
        sourceFileName = Paths.get(sourceFileName).getFileName().toString();

        List<Token> tokens = lexer.lex(inputFileContent);
        Parser parser = new Parser(tokens);
        ProgramNode program = parser.parse();

        ASTOptimizer optimizer = new ASTOptimizer();
        program = optimizer.optimize(program);
        KeyWordUsageChecker keyWordUsageChecker = new KeyWordUsageChecker();
        DeclarationChecker declarationChecker = new DeclarationChecker();
        keyWordUsageChecker.check(program);
        declarationChecker.checkDeclarations(program);
        System.out.println(program.statements);

        JasminCodeGenerator generator = new JasminCodeGenerator(sourceFileName);
        List<String> generatedFiles = generator.generate(program);

        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        boolean isWindows = osName.contains("win");

        for (String fileName : generatedFiles) {
            try {
                String outputPath = "output/" + sourceFileName + "/" + fileName;
                File file = new File(outputPath);
                if (!file.exists()) {
                    System.out.println("File not found. Something is totally wrong...: " + file.getAbsolutePath());
                    System.exit(-1);
                }

                // Running Jasmin command
                String[] jasminCommand = new String[]{
                        "java", "-jar", "jasmin-2.4/jasmin.jar", outputPath
                };
                if (execute(jasminCommand) != 0) {
                    System.exit(-1);
                }

                // Handling .class file movement
                String classFileName = fileName.substring(0, fileName.length() - 2) + ".class";
                String destination = "output/" + sourceFileName + "/" + classFileName;
                String[] moveCommand;

                if (isWindows) {
                    moveCommand = new String[]{
                            "cmd", "/c", "move", classFileName, destination.replace("/", "\\")
                    };
                } else {
                    moveCommand = new String[]{
                            "mv", classFileName, destination
                    };
                }

                if (execute(moveCommand) != 0) {
                    System.exit(-1);
                }

                System.out.println("Success.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static int execute(String[] args) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(args);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            return process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
