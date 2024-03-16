package malte0811.resistors.simplifier;

import malte0811.resistors.data.ResistorNetwork;
import malte0811.resistors.data.SimplificationStep;

import java.util.Optional;

public interface NetworkSimplifier<NodeKey> {
    // TODO top-level special handling for networks with at most one source
    Optional<SimplificationStep<NodeKey>> simplify(ResistorNetwork<NodeKey> fullNetwork);
}
