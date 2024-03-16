package malte0811.resistors.simplifier;

import malte0811.resistors.data.*;
import malte0811.resistors.data.ResistorNetwork.ResistorEdge;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OuterStar<NodeKey> implements NetworkSimplifier<NodeKey> {
    @Override
    public Optional<SimplificationStep<NodeKey>> simplify(ResistorNetwork<NodeKey> fullNetwork) {
        for (final var center : fullNetwork.getNodes()) {
            final var maybeStar = growStarFromCenter(fullNetwork, center);
            if (maybeStar.isPresent()) {
                return Optional.of(simplifyStar(fullNetwork, maybeStar.get()));
            }
        }
        return Optional.empty();
    }

    private Optional<Star<NodeKey>> growStarFromCenter(ResistorNetwork<NodeKey> net, NodeKey center) {
        if (net.isFixed(center)) { return Optional.empty(); }
        List<ResistorEdge<NodeKey>> starResistors = new ArrayList<>();
        for (final var resistor : net.getIncidentResistors(center)) {
            final var endpoint = resistor.otherEnd();
            if (net.isFixed(endpoint) && net.getIncidentResistors(endpoint).size() == 1) {
                starResistors.add(resistor);
            }
        }
        if (starResistors.size() > 1) {
            return Optional.of(new Star<>(center, starResistors));
        } else {
            return Optional.empty();
        }
    }

    private SimplificationStep<NodeKey> simplifyStar(ResistorNetwork<NodeKey> net, Star<NodeKey> star) {
        final var simplifiedNetwork = net.copy();
        for (int i = 1; i < star.starResistors.size(); ++i) {
            simplifiedNetwork.removeNode(star.starResistors.get(i).otherEnd());
        }

        double newResistance = 1 / star.starResistors.stream().mapToDouble(r -> 1 / r.resistance()).sum();
        final var remainingFixed = star.starResistors.get(0).otherEnd();
        simplifiedNetwork.removeResistor(remainingFixed, star.center)
                .addResistor(remainingFixed, star.center, newResistance);

        final var newFixedVoltage = new MutableLinearCombination<NodeKey>();
        for (final var resistor : star.starResistors) {
            newFixedVoltage.add(resistor.otherEnd(), newResistance / resistor.resistance());
        }
        final var mapFixedVoltages = NetworkTransformation.identity(simplifiedNetwork);
        mapFixedVoltages.voltageMap().put(remainingFixed, newFixedVoltage);

        final var mapFlexibleVoltages = NetworkTransformation.identity(simplifiedNetwork);
        mapFlexibleVoltages.remove(remainingFixed);
        final var restoreFixedVoltages = new NetworkTransformation<NodeKey>();
        for (final var resistor : star.starResistors) {
            restoreFixedVoltages.voltageMap().put(
                    resistor.otherEnd(), MutableLinearCombination.simple(resistor.otherEnd(), 1)
            );
        }
        return new SimplificationStep<>(
                simplifiedNetwork,
                mapFixedVoltages,
                mapFlexibleVoltages,
                restoreFixedVoltages
        );
    }

    private record Star<NodeKey>(
        NodeKey center,
        List<ResistorEdge<NodeKey>> starResistors
    ) { }
}
