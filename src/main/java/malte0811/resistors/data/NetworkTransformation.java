package malte0811.resistors.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public record NetworkTransformation<NodeKey>(
        // For simplicity, this *has* to include the trivial expressions for fixed nodes
        Map<NodeKey, LinearCombination<NodeKey>> voltageMap
) {
    public NetworkTransformation() {
        this(new HashMap<>());
    }

    public NetworkTransformation<NodeKey> thenApply(NetworkTransformation<NodeKey> other) {
        final var result = new NetworkTransformation<NodeKey>();
        for (final var entry : other.voltageMap().entrySet()) {
            result.voltageMap.put(entry.getKey(), inOriginalNetwork(entry.getValue()));
        }
        return result;
    }

    public LinearCombination<NodeKey> inOriginalNetwork(LinearCombination<NodeKey> afterTransform) {
        var result = new MutableLinearCombination<NodeKey>();
        for (final var entry : afterTransform.getCoefficients().object2DoubleEntrySet()) {
            result.addScaled(
                    Objects.requireNonNull(this.voltageMap.get(entry.getKey())),
                    entry.getDoubleValue()
            );
        }
        return result;
    }

    public LinearCombination<NodeKey> get(NodeKey key) {
        return voltageMap().get(key);
    }

    public static <NodeKey> NetworkTransformation<NodeKey> identity(ResistorNetwork<NodeKey> net) {
        final var solution = new NetworkTransformation<NodeKey>();
        for (final var node : net.getNodes()) {
            solution.voltageMap().put(node, MutableLinearCombination.simple(node, 1));
        }
        return solution;
    }
}
