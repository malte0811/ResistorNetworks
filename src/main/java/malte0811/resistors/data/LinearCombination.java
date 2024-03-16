package malte0811.resistors.data;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;

import java.util.Objects;

public final class LinearCombination<NodeKey> {
    private final Object2DoubleOpenHashMap<NodeKey> coefficients;

    public static <NodeKey> LinearCombination<NodeKey> simple(NodeKey variable, double coefficient) {
        final var result = new LinearCombination<NodeKey>();
        result.add(variable, coefficient);
        return result;
    }

    public LinearCombination(Object2DoubleOpenHashMap<NodeKey> coefficients) { this.coefficients = coefficients; }

    public LinearCombination() {
        this(new Object2DoubleOpenHashMap<>());
    }

    public Object2DoubleMap<NodeKey> coefficients() { return coefficients; }

    public void add(NodeKey key, double amount) {
        this.coefficients.addTo(key, amount);
    }
}
