package optimization;

import node.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionInliningOptimizer {
    private final Map<String, RoutineDeclarationNode> routines = new HashMap<>();

    public void collectFunctions(List<ASTNode> nodes) {
        for (ASTNode node : nodes) {
            if (node instanceof RoutineDeclarationNode routine) {
                routines.put(routine.identifier, routine);
            }
        }
    }

    public ASTNode inlineFunctionCalls(ASTNode node) {
        if (node instanceof FunctionCallNode functionCall) {
            RoutineDeclarationNode routine = routines.get(functionCall.identifier);
            if (routine != null && isSimpleFunction(routine)) {
                ASTNode firstStatement = routine.body.getFirst();

                if (firstStatement instanceof ReturnStatementNode returnNode) {
                    return returnNode.expression;
                }

                if (firstStatement instanceof PrintStatementNode printNode) {
                    return new PrintStatementNode(printNode.expression);
                }
            }
        } else if (node instanceof PrintStatementNode printNode) {
            return new PrintStatementNode(inlineFunctionCalls(printNode.expression));
        }
        return node;
    }

    private boolean isSimpleFunction(RoutineDeclarationNode routine) {
        if (routine.body.size() == 1) {
            ASTNode firstStatement = routine.body.getFirst();
            return firstStatement instanceof ReturnStatementNode || firstStatement instanceof PrintStatementNode;
        }
        return false;
    }
}
