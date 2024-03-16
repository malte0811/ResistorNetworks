package malte0811.resistors.data;

import java.util.*;

public interface ResistorNetwork<NodeKey> {
    MutableNetwork<NodeKey> copy();

    Collection<NodeKey> getNodes();

    Collection<NodeKey> getFixedNodes();

    List<ResistorEdge<NodeKey>> getIncidentResistors(NodeKey node);

    boolean isFixed(NodeKey query);

    record ResistorEdge<NodeKey>(double resistance, NodeKey otherEnd) {}
}
