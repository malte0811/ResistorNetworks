package malte0811.resistors.solver;

import malte0811.resistors.data.LinearCombination;
import malte0811.resistors.data.ResistorNetwork;

import java.util.Optional;

public class SimpleSolvers<NodeKey> {
    public Optional<NetworkSolution<NodeKey>> solveFewSources(ResistorNetwork<NodeKey> net) {
        final var sources = net.getFixedNodes();
        final LinearCombination<NodeKey> voltageEverywhere;
        if (sources.isEmpty()) {
            voltageEverywhere = new LinearCombination<>();
        } else if (sources.size() == 1) {
            voltageEverywhere = LinearCombination.simple(sources.iterator().next(), 1);
        } else {
            return Optional.empty();
        }
        final var solution = new NetworkSolution<NodeKey>();
        for (final var node : net.getNodes()) {
            solution.voltageFromFixed().put(node, voltageEverywhere);
        }
        return Optional.of(solution);
    }

    public Optional<NetworkSolution<NodeKey>> solveAllFixed(ResistorNetwork<NodeKey> net) {
        if (net.getNodes().size() != net.getFixedNodes().size()) { return Optional.empty(); }
        final var solution = new NetworkSolution<NodeKey>();
        for (final var node : net.getNodes()) {
            solution.voltageFromFixed().put(node, LinearCombination.simple(node, 1));
        }
        return Optional.of(solution);
    }
}
