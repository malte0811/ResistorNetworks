package malte0811.resistors.simplifier;

import malte0811.resistors.data.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RemoveLeaves<NodeKey> implements NetworkSimplifier<NodeKey> {
    @Override
    public Optional<SimplificationStep<NodeKey>> simplify(ReadOnlyNetwork<NodeKey> fullNetwork) {
        List<Leaf<NodeKey>> leaves = new ArrayList<>();
        for (final var node : fullNetwork.getNodes()) {
            final var incident = fullNetwork.getIncidentResistors(node);
            if (incident.size() == 1 && !fullNetwork.isFixed(node)) {
                leaves.add(new Leaf<>(node, incident.get(0).otherEnd()));
            }
        }
        if (leaves.isEmpty()) {
            return Optional.empty();
        }
        final var simplified = fullNetwork.copy();
        for (final var leaf : leaves) {
            simplified.removeNode(leaf.leaf);
        }
        return Optional.of(new SimplificationStep<>(simplified, v -> v, simple -> extendToLeaves(simple, leaves)));
    }

    private ReadOnlyVoltageMap<NodeKey> extendToLeaves(
            ReadOnlyVoltageMap<NodeKey> simpleSolution, List<Leaf<NodeKey>> leaves
    ) {
        final var extended = simpleSolution.copy();
        for (final var leaf : leaves) {
            extended.voltage().put(leaf.leaf, simpleSolution.getVoltage(leaf.neighbor));
        }
        return extended;
    }

    record Leaf<NodeKey>(NodeKey leaf, NodeKey neighbor) { }
}
