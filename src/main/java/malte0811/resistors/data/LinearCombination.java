package malte0811.resistors.data;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;

import javax.annotation.Nonnull;
import java.util.Map;

public interface LinearCombination<NodeKey> {
    MutableLinearCombination<NodeKey> copy();

    LinearCombination<NodeKey> replaceBy(NodeKey key, LinearCombination<NodeKey> value);

    double applyTo(Object2DoubleMap<NodeKey> vector);

    @Nonnull
    Object2DoubleMap<NodeKey> coefficients();
}
