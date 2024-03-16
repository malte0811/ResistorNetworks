package malte0811.resistors.simplifier;

import malte0811.resistors.data.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// TODO extend to more general "dead branches" even if those contain cycles? I.e. bridges with all sources on one side
public class RemoveLeaves<NodeKey> implements NetworkSimplifier<NodeKey> {
    @Override
    public Optional<SimplificationStep<NodeKey>> simplify(ResistorNetwork<NodeKey> fullNetwork) {
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
        return Optional.of(new SimplificationStep<>(
                simplified, NetworkTransformation.identity(simplified), extendToLeaves(simplified, leaves)
        ));
    }

    private NetworkTransformation<NodeKey> extendToLeaves(
            ResistorNetwork<NodeKey> simpleNet, List<Leaf<NodeKey>> leaves
    ) {
        final var result = NetworkTransformation.identity(simpleNet);
        for (final var leaf : leaves) {
            result.voltageMap().put(leaf.leaf, MutableLinearCombination.simple(leaf.neighbor, 1));
        }
        return result;
    }

    record Leaf<NodeKey>(NodeKey leaf, NodeKey neighbor) { }
}
