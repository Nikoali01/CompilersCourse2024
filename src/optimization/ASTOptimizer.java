package optimization;

import node.ASTNode;
import node.ProgramNode;
import node.RoutineDeclarationNode;

import java.util.ArrayList;
import java.util.List;

public class ASTOptimizer {
    private final ConstantExpressionSimplifier constSimplifier = new ConstantExpressionSimplifier();
    private final DeadCodeEliminator deadCodeEliminator = new DeadCodeEliminator();
    private final UnusedVariableRemover unusedVariableRemover = new UnusedVariableRemover();
    private final FunctionInliningOptimizer functionInliningOptimizer = new FunctionInliningOptimizer();

    public ProgramNode optimize(ProgramNode program) {
        program.statements.replaceAll(constSimplifier::simplify);
        for (ASTNode node : program.statements) {
            unusedVariableRemover.collectUsedVariables(node);
        }
        program = unusedVariableRemover.removeUnusedVariables(program);

        functionInliningOptimizer.collectFunctions(program.statements);
        for (int i = 0; i < program.statements.size(); i++) {
            program.statements.set(i, functionInliningOptimizer.inlineFunctionCalls(program.statements.get(i)));
        }

        List<ASTNode> optimizedStatements = new ArrayList<>();
        for (ASTNode statement : program.statements) {
            if (statement instanceof RoutineDeclarationNode routine) {
                routine.body = deadCodeEliminator.eliminateDeadCode(routine.body);
            }
            optimizedStatements.add(statement);
        }
        return new ProgramNode(optimizedStatements);
    }
}

