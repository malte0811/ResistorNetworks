package malte0811.resistors.solver;

import com.google.common.collect.Lists;
import malte0811.resistors.data.NetworkTransformation;
import malte0811.resistors.data.ResistorNetwork;
import malte0811.resistors.data.SimplificationStep;
import malte0811.resistors.simplifier.NetworkSimplifier;
import malte0811.resistors.simplifier.OuterStar;
import malte0811.resistors.simplifier.RemoveLeaves;
import malte0811.resistors.simplifier.SimplifyPaths;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NetworkSolver<NodeKey> {
    private final List<NetworkSimplifier<NodeKey>> simplifiers = List.of(
            new RemoveLeaves<>(),
            new SimplifyPaths<>(),
            new OuterStar<>()
    );
    private final SimpleSolvers<NodeKey> simple = new SimpleSolvers<>();

    public NetworkTransformation<NodeKey> solve(ResistorNetwork<NodeKey> current) {
        List<SimplificationStep<NodeKey>> simplifications = new ArrayList<>();
        boolean simplified;
        do {
            final var maybeSolution = simple.maybeSolve(current);
            if (maybeSolution.isPresent()) {
                return extend(maybeSolution.get(), simplifications);
            }
            Optional<SimplificationStep<NodeKey>> nextSimplification = simplify(current);
            simplified = nextSimplification.isPresent();
            if (simplified) {
                simplifications.add(nextSimplification.get());
                current = nextSimplification.get().simplifiedNetwork();
            }
        } while (simplified);
        // TODO full nodal solver here!
        throw new UnsupportedOperationException();
    }

    private NetworkTransformation<NodeKey> extend(
            NetworkTransformation<NodeKey> innerSolution, List<SimplificationStep<NodeKey>> simplifications
    ) {
        var solution = innerSolution;
        for (final var step : Lists.reverse(simplifications)) {
            solution = step.extendSolution(solution);
        }
        return solution;
    }

    private Optional<SimplificationStep<NodeKey>> simplify(ResistorNetwork<NodeKey> network) {
        for (final var simplifier : simplifiers) {
            final var simplified = simplifier.simplify(network);
            if (simplified.isPresent()) {
                return simplified;
            }
        }
        return Optional.empty();
    }
}
