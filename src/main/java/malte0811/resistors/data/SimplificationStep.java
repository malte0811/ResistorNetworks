package malte0811.resistors.data;

import java.util.function.UnaryOperator;

public record SimplificationStep<NodeKey>(
        ResistorNetwork<NodeKey> simplifiedNetwork,
        // TODO explicit linear combinations so we can compose these
        UnaryOperator<VoltageMap<NodeKey>> reduceFixedVoltages,
        UnaryOperator<VoltageMap<NodeKey>> extendSolution
) {
}
