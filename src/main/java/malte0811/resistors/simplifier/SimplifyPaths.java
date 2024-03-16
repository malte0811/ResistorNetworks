package malte0811.resistors.simplifier;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import malte0811.resistors.data.*;
import malte0811.resistors.data.ResistorNetwork.ResistorEdge;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SimplifyPaths<NodeKey> implements NetworkSimplifier<NodeKey> {
    @Override
    public Optional<SimplificationStep<NodeKey>> simplify(ResistorNetwork<NodeKey> fullNetwork) {
        // TODO simplify all paths at once? Need to be careful: endpoints of earlier paths can vanish later
        for (final var node : fullNetwork.getNodes()) {
            if (isValidInnerNode(fullNetwork, node)) {
                final var startEdges = fullNetwork.getIncidentResistors(node);
                final var path0 = discoverPath(fullNetwork, node, startEdges.get(0));
                final var path1 = discoverPath(fullNetwork, node, startEdges.get(1));
                final var totalPath = path0.prependReversed(path1);
                return Optional.of(contractPath(fullNetwork, totalPath));
            }
        }
        return Optional.empty();
    }

    private SimplificationStep<NodeKey> contractPath(ResistorNetwork<NodeKey> net, Path<NodeKey> path) {
        final var simplifiedNet = net.copy();
        for (final var internalNode : path.innerVertices) {
            simplifiedNet.removeNode(internalNode);
        }
        simplifiedNet.addResistor(path.start, path.end, path.totalResistance);
        return new SimplificationStep<>(
                simplifiedNet, NetworkTransformation.identity(simplifiedNet), this.extendToPath(simplifiedNet, path)
        );
    }

    private NetworkTransformation<NodeKey> extendToPath(ResistorNetwork<NodeKey> contracted, Path<NodeKey> path) {
        final var result = NetworkTransformation.identity(contracted);
        double resistanceSoFar = 0;
        for (int i = 0; i < path.innerVertices.size(); ++i) {
            resistanceSoFar += path.resistances.getDouble(i);
            final var lambda = resistanceSoFar / path.totalResistance;
            result.voltageMap().put(
                    path.innerVertices.get(i),
                    MutableLinearCombination.simple(path.start, 1 - lambda).add(path.end, lambda)
            );
        }
        return result;
    }

    private Path<NodeKey> discoverPath(ResistorNetwork<NodeKey> net, NodeKey start, ResistorEdge<NodeKey> firstEdge) {
        // This cannot run into infinite cycles: We guarantee that there is a voltage source, and we guarantee that the
        // network is connected. The only case where this would cycle is an isolated cycle without sources.
        List<NodeKey> innerNodes = new ArrayList<>();
        innerNodes.add(start);
        DoubleList resistances = new DoubleArrayList();
        resistances.add(firstEdge.resistance());
        var lastInnerNode = start;
        var lastEdge = firstEdge;
        while (isValidInnerNode(net, lastEdge.otherEnd())) {
            for (final var nextEdge : net.getIncidentResistors(lastEdge.otherEnd())) {
                if (!Objects.equals(nextEdge.otherEnd(), lastInnerNode)) {
                    lastInnerNode = lastEdge.otherEnd();
                    Preconditions.checkState(!innerNodes.contains(lastInnerNode));
                    innerNodes.add(lastInnerNode);
                    resistances.add(nextEdge.resistance());
                    lastEdge = nextEdge;
                    break;
                }
            }
        }
        return new Path<>(start, innerNodes, lastEdge.otherEnd(), resistances, resistances.doubleStream().sum());
    }

    private boolean isValidInnerNode(ResistorNetwork<NodeKey> net, NodeKey node) {
        return !net.isFixed(node) && net.getIncidentResistors(node).size() == 2;
    }

    private record Path<NodeKey>(
            NodeKey start,
            List<NodeKey> innerVertices,
            NodeKey end,
            DoubleList resistances,
            double totalResistance
    ) {
        public Path<NodeKey> prependReversed(Path<NodeKey> toPrepend) {
            Preconditions.checkArgument(Objects.equals(start, toPrepend.start));
            List<NodeKey> newInnerVertices = new ArrayList<>(Lists.reverse(toPrepend.innerVertices));
            newInnerVertices.add(start);
            newInnerVertices.addAll(this.innerVertices);
            DoubleList newResistances = new DoubleArrayList();
            for (int i = toPrepend.resistances.size() - 1; i >= 0; --i) {
                newResistances.add(toPrepend.resistances.getDouble(i));
            }
            newResistances.addAll(this.resistances);
            final var newTotalResistance = toPrepend.totalResistance + this.totalResistance;
            return new Path<>(toPrepend.end, newInnerVertices, end, newResistances, newTotalResistance);
        }
    }
}
