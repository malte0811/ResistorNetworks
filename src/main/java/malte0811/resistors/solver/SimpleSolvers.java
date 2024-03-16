package malte0811.resistors.solver;

import malte0811.resistors.data.MutableLinearCombination;
import malte0811.resistors.data.NetworkTransformation;
import malte0811.resistors.data.ResistorNetwork;

import java.util.Optional;

public class SimpleSolvers<NodeKey> {
    public Optional<NetworkTransformation<NodeKey>> maybeSolve(ResistorNetwork<NodeKey> net) {
        final var fewSources = solveFewSources(net);
        if (fewSources.isPresent()) {
            return fewSources;
        } else {
            return solveAllFixed(net);
        }
    }

    public Optional<NetworkTransformation<NodeKey>> solveFewSources(ResistorNetwork<NodeKey> net) {
        final var sources = net.getFixedNodes();
        final MutableLinearCombination<NodeKey> voltageEverywhere;
        if (sources.isEmpty()) {
            voltageEverywhere = new MutableLinearCombination<>();
        } else if (sources.size() == 1) {
            voltageEverywhere = MutableLinearCombination.simple(sources.iterator().next(), 1);
        } else {
            return Optional.empty();
        }
        final var solution = new NetworkTransformation<NodeKey>();
        for (final var node : net.getNodes()) {
            solution.voltageMap().put(node, voltageEverywhere);
        }
        return Optional.of(solution);
    }

    public Optional<NetworkTransformation<NodeKey>> solveAllFixed(ResistorNetwork<NodeKey> net) {
        if (net.getNodes().size() == net.getFixedNodes().size()) {
            return Optional.of(NetworkTransformation.identity(net));
        } else {
            return Optional.empty();
        }
    }
}
