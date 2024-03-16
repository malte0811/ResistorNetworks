package malte0811.resistors.data;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;

import javax.annotation.Nonnull;

public final class MutableLinearCombination<NodeKey> implements LinearCombination<NodeKey> {
    private final Object2DoubleOpenHashMap<NodeKey> coefficients;

    public static <NodeKey> MutableLinearCombination<NodeKey> simple(NodeKey variable, double coefficient) {
        return new MutableLinearCombination<NodeKey>().add(variable, coefficient);
    }

    public MutableLinearCombination(Object2DoubleOpenHashMap<NodeKey> coefficients) { this.coefficients = coefficients; }

    public MutableLinearCombination() {
        this(new Object2DoubleOpenHashMap<>());
    }

    public MutableLinearCombination<NodeKey> add(NodeKey key, double amount) {
        this.coefficients.addTo(key, amount);
        return this;
    }

    @Override
    public MutableLinearCombination<NodeKey> copy() {
        return new MutableLinearCombination<>(new Object2DoubleOpenHashMap<>(this.coefficients()));
    }

    @Override
    public LinearCombination<NodeKey> replaceBy(NodeKey nodeKey, LinearCombination<NodeKey> value) {
        final var oldCoefficient = this.coefficients().removeDouble(nodeKey);
        if (oldCoefficient == 0) {
            return this;
        } else {
            return copy().addScaled(value, oldCoefficient);
        }
    }

    public LinearCombination<NodeKey> addScaled(LinearCombination<NodeKey> toAdd, double factor) {
        for (final var entry : toAdd.coefficients().object2DoubleEntrySet()) {
            add(entry.getKey(), entry.getDoubleValue() * factor);
        }
        return this;
    }

    @Override
    public double applyTo(Object2DoubleMap<NodeKey> vector) {
        double result = 0;
        for (final var entry : coefficients().object2DoubleEntrySet()) {
            result += entry.getDoubleValue() * vector.getOrDefault(entry.getKey(), 0);
        }
        return result;
    }

    @Override
    @Nonnull
    public Object2DoubleMap<NodeKey> coefficients() {
        return coefficients;
    }
}
