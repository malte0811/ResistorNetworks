package malte0811.resistors.data;

public record SimplificationStep<NodeKey>(
        ResistorNetwork<NodeKey> simplifiedNetwork,
        NetworkTransformation<NodeKey> reduceFixedVoltages,
        NetworkTransformation<NodeKey> extendInnerSolution,
        NetworkTransformation<NodeKey> addFromOriginalFixed
) {
    public SimplificationStep(
            ResistorNetwork<NodeKey> simplifiedNetwork,
            NetworkTransformation<NodeKey> reduceFixedVoltages,
            NetworkTransformation<NodeKey> extendInnerSolution
    ) {
        this(simplifiedNetwork, reduceFixedVoltages, extendInnerSolution, new NetworkTransformation<>());
    }

    public NetworkTransformation<NodeKey> extendSolution(NetworkTransformation<NodeKey> innerMap) {
        return reduceFixedVoltages.thenApply(innerMap)
                .thenApply(extendInnerSolution)
                .add(addFromOriginalFixed);
    }
}
