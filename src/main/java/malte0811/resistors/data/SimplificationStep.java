package malte0811.resistors.data;

import java.util.function.UnaryOperator;

public record SimplificationStep<NodeKey>(
        ReadOnlyNetwork<NodeKey> simplifiedNetwork,
        // TODO explicit linear combinations so we can compose these
        UnaryOperator<ReadOnlyVoltageMap<NodeKey>> reduceFixedVoltages,
        UnaryOperator<ReadOnlyVoltageMap<NodeKey>> extendSolution
) {
}
