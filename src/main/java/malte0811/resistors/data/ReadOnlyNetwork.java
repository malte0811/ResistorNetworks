package malte0811.resistors.data;

import java.util.*;

public interface ReadOnlyNetwork<NodeKey> {
    ResistorNetwork<NodeKey> copy();

    Collection<NodeKey> getNodes();

    List<ResistorEdge<NodeKey>> getIncidentResistors(NodeKey node);

    boolean isFixed(NodeKey query);

    record ResistorEdge<NodeKey>(double resistance, NodeKey otherEnd) {}
}
