package malte0811.resistors.data;

import java.util.*;

public class ResistorNetwork<NodeKey> implements ReadOnlyNetwork<NodeKey> {
    private final Set<NodeKey> fixedNodes;
    private final Map<NodeKey, List<ResistorEdge<NodeKey>>> resistors;

    public ResistorNetwork() {
        this.fixedNodes = new HashSet<>();
        this.resistors = new HashMap<>();
    }

    private ResistorNetwork(Set<NodeKey> fixedNodes, Map<NodeKey, List<ResistorEdge<NodeKey>>> resistors) {
        this.fixedNodes = fixedNodes;
        this.resistors = resistors;
    }

    @Override
    public ResistorNetwork<NodeKey> copy() {
        return new ResistorNetwork<>(new HashSet<>(this.fixedNodes), new HashMap<>(this.resistors));
    }

    @Override
    public Collection<NodeKey> getNodes() {
        return this.resistors.keySet();
    }

    @Override
    public List<ResistorEdge<NodeKey>> getIncidentResistors(NodeKey node) {
        return Collections.unmodifiableList(this.resistors.getOrDefault(node, List.of()));
    }

    public ResistorNetwork<NodeKey> addResistor(NodeKey endA, NodeKey endB, double resistance) {
        addHalfResistor(endA, endB, resistance);
        addHalfResistor(endB, endA, resistance);
        return this;
    }

    public ResistorNetwork<NodeKey> markFixed(NodeKey fixed) {
        this.fixedNodes.add(fixed);
        return this;
    }

    @Override
    public boolean isFixed(NodeKey query) {
        return this.fixedNodes.contains(query);
    }

    public ResistorNetwork<NodeKey> removeNode(NodeKey toRemove) {
        this.fixedNodes.remove(toRemove);
        for (final var resistor : getIncidentResistors(toRemove)) {
            final var fromOtherEnd = this.resistors.get(resistor.otherEnd());
            fromOtherEnd.removeIf(e -> Objects.equals(e.otherEnd(), toRemove));
        }
        this.resistors.remove(toRemove);
        return this;
    }

    private void addHalfResistor(NodeKey from, NodeKey to, double resistance) {
        final var edges = this.resistors.computeIfAbsent(from, $ -> new ArrayList<>());
        for (int i = 0; i < edges.size(); ++i) {
            final var existingEdge = edges.get(i);
            if (Objects.equals(existingEdge.otherEnd(), to)) {
                final var totalResistance = 1 / (1 / existingEdge.resistance() + 1 / resistance);
                edges.set(i, new ResistorEdge<>(totalResistance, existingEdge.otherEnd()));
                return;
            }
        }
        edges.add(new ResistorEdge<>(resistance, to));
    }
}
