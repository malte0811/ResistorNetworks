package malte0811.resistors.data;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;

import javax.annotation.Nonnull;

public interface LinearCombination<NodeKey> {
    MutableLinearCombination<NodeKey> copy();

    LinearCombination<NodeKey> replaceBy(NodeKey key, LinearCombination<NodeKey> value);

    double evaluate(Object2DoubleMap<NodeKey> vector);

    @Nonnull
    Object2DoubleMap<NodeKey> getCoefficients();

    double get(NodeKey key);
}
