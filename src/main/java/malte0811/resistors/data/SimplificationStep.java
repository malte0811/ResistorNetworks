package malte0811.resistors.data;

public record SimplificationStep<NodeKey>(
        ResistorNetwork<NodeKey> simplifiedNetwork,
        NetworkTransformation<NodeKey> reduceFixedVoltages,
        NetworkTransformation<NodeKey> extendInnerSolution
) {
    public NetworkTransformation<NodeKey> extendSolution(NetworkTransformation<NodeKey> innerMap) {
        return reduceFixedVoltages.thenApply(innerMap).thenApply(extendInnerSolution);
    }
}
