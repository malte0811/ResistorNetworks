package malte0811.resistors.solver;

import malte0811.resistors.data.LinearCombination;

import java.util.HashMap;
import java.util.Map;

public record NetworkSolution<NodeKey>(
        // For simplicity, this *has* to include the trivial expressions for fixed nodes
        Map<NodeKey, LinearCombination<NodeKey>> voltageFromFixed
) {
    public NetworkSolution() {
        this(new HashMap<>());
    }
}
