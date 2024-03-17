package malte0811.resistors.solver;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import malte0811.resistors.data.*;
import malte0811.resistors.simplifier.NetworkSimplifier;
import malte0811.resistors.simplifier.OuterStar;
import malte0811.resistors.simplifier.RemoveLeaves;
import malte0811.resistors.simplifier.SimplifyPaths;

import java.util.*;

public class NetworkSolver<NodeKey> {
    private final List<NetworkSimplifier<NodeKey>> simplifiers = List.of(
            new RemoveLeaves<>(),
            new SimplifyPaths<>(),
            new OuterStar<>()
    );
    private final SimpleSolvers<NodeKey> simple = new SimpleSolvers<>();

    public NetworkSolution<NodeKey> solve(ResistorNetwork<NodeKey> current) {
        List<SimplificationStep<NodeKey>> simplifications = new ArrayList<>();
        boolean simplified;
        do {
            final var maybeSolution = simple.maybeSolve(current);
            if (maybeSolution.isPresent()) {
                return new NetworkSolution<>(extend(maybeSolution.get(), simplifications), false);
            }
            Optional<SimplificationStep<NodeKey>> nextSimplification = simplify(current);
            simplified = nextSimplification.isPresent();
            if (simplified) {
                simplifications.add(nextSimplification.get());
                current = nextSimplification.get().simplifiedNetwork();
            }
        } while (simplified);
        return new NetworkSolution<>(extend(solveNodal(current), simplifications), true);
    }

    private NetworkTransformation<NodeKey> solveNodal(ResistorNetwork<NodeKey> net) {
        Object2IntMap<NodeKey> toIndex = new Object2IntOpenHashMap<>();
        for (final var node : net.getNodes()) {
            toIndex.put(node, toIndex.size());
        }
        final var matrix = new Matrix.MutableMatrix(toIndex.size(), toIndex.size());
        for (final var node : net.getNodes()) {
            final int ownIndex = toIndex.getInt(node);
            if (net.isFixed(node)) {
                matrix.set(ownIndex, ownIndex, 1);
            } else {
                for (final var resistor : net.getIncidentResistors(node)) {
                    final var endpointIndex = toIndex.getInt(resistor.otherEnd());
                    final var conductance = 1 / resistor.resistance();
                    matrix.add(ownIndex, ownIndex, conductance);
                    matrix.set(ownIndex, endpointIndex, -conductance);
                }
            }
        }
        final var inverse = LUDecomposer.invert(matrix);
        final Map<NodeKey, LinearCombination<NodeKey>> voltageMap = new HashMap<>();
        for (final var node : net.getNodes()) {
            final var nodeVoltage = new MutableLinearCombination<NodeKey>();
            final var nodeIndex = toIndex.getInt(node);
            // The other matrix entries correspond to current balances, which are always zero
            for (final var source : net.getFixedNodes()) {
                final var sourceIndex = toIndex.getInt(source);
                nodeVoltage.add(source, inverse.get(nodeIndex, sourceIndex));
            }
            voltageMap.put(node, nodeVoltage);
        }
        return new NetworkTransformation<>(voltageMap);
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

    public record NetworkSolution<NodeKey>(
            NetworkTransformation<NodeKey> fixedToAll,
            boolean usedNodalSolver
    ) { }
}
