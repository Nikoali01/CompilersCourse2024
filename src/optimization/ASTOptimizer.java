package optimization;

import node.ASTNode;
import node.ProgramNode;
import node.RoutineDeclarationNode;

import java.util.ArrayList;
import java.util.List;

public class ASTOptimizer {
    private final ConstantExpressionSimplifier constSimplifier = new ConstantExpressionSimplifier();
    private final DeadCodeEliminator deadCodeEliminator = new DeadCodeEliminator();

    public ProgramNode optimize(ProgramNode program) {
        program.statements.replaceAll(constSimplifier::simplify);

        // TODO: Andrew and dead code
        List<ASTNode> optimizedStatements = new ArrayList<>();
        for (ASTNode statement : program.statements) {
//            if (statement instanceof RoutineDeclarationNode routine) {
//                routine.body = deadCodeEliminator.eliminateDeadCode(routine.body);
//            }
            optimizedStatements.add(statement);
        }
        return new ProgramNode(optimizedStatements);
    }
}

