package malte0811.resistors.solver;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import malte0811.resistors.data.*;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class NetworkSolverTest {
    private static final NetworkSolver<String> SOLVER = new NetworkSolver<>();

    @Test
    public void testSimpleNetwork() {
        final var positive = "positive";
        final var negative = "negative";
        final var innerLeft1 = "innerLeft1";
        final var innerLeft2 = "innerLeft2";
        final var innerRight = "innerRight";
        final var leaf = "leaf";
        MutableNetwork<String> network = new MutableNetwork<>();
        network.addResistor(positive, innerLeft2, 1)
                .addResistor(innerLeft1, innerLeft2, 2)
                .addResistor(innerLeft1, negative, 3)
                .addResistor(innerLeft1, leaf, 3);
        network.addResistor(positive, innerRight, 10)
                .addResistor(negative, innerRight, 90);
        network.markFixed(negative).markFixed(positive);
        final var solution = SOLVER.solve(network);
        assertFixed(solution, positive);
        assertFixed(solution, negative);
        assertEqual(Map.of(positive, 0.9, negative, 0.1), solution.get(innerRight));
        assertEqual(Map.of(positive, 0.5, negative, 0.5), solution.get(innerLeft1));
        assertEqual(Map.of(positive, 0.5, negative, 0.5), solution.get(leaf));
        assertEqual(Map.of(positive, 5 / 6., negative, 1 / 6.), solution.get(innerLeft2));
        assertKirchhoffCurrentLaw(network, solution);
    }

    @Test
    public void testStar() {
        final var center = "center";
        final var leaf1 = "leaf1";
        final var leaf2 = "leaf2";
        final var leaf3 = "leaf3";
        MutableNetwork<String> network = new MutableNetwork<>();
        network.addResistor(center, leaf1, 1)
                .addResistor(center, leaf2, 2)
                .addResistor(center, leaf3, 3);
        network.markFixed(leaf1)
                .markFixed(leaf2)
                .markFixed(leaf3);
        final var solution = SOLVER.solve(network);
        assertFixed(solution, leaf1);
        assertFixed(solution, leaf2);
        assertFixed(solution, leaf3);
        assertEqual(
                Map.of(leaf1, 6 / 11., leaf2, 3 / 11., leaf3, 2 / 11.),
                solution.get(center)
        );
        assertKirchhoffCurrentLaw(network, solution);
    }

    @Test
    public void testTree() {
        final var inner1 = "inner1";
        final var inner2 = "inner2";
        final var inner3 = "inner3";
        final var leaf1 = "leaf1";
        final var leaf2 = "leaf2";
        final var leaf3 = "leaf3";
        final var leaf4 = "leaf4";
        final var leaf5 = "leaf5";
        MutableNetwork<String> network = new MutableNetwork<>();
        network.addResistor(inner1, inner2, 5)
                .addResistor(inner1, inner3, 3)
                .addResistor(leaf1, inner1, 2)
                .addResistor(leaf2, inner2, 9)
                .addResistor(leaf3, inner2, 17)
                .addResistor(leaf4, inner3, 83)
                .addResistor(leaf5, inner3, 3);
        network.markFixed(leaf1)
                .markFixed(leaf2)
                .markFixed(leaf3)
                .markFixed(leaf4)
                .markFixed(leaf5);
        assertKirchhoffCurrentLaw(network, SOLVER.solve(network));
    }

    private void assertFixed(NetworkTransformation<String> map, String node) {
        assertEqual(Map.of(node, 1.), map.get(node));
    }

    private void assertEqual(Map<String, Double> expected, LinearCombination<String> actual) {
        for (final var entry : expected.entrySet()) {
            assertEquals(entry.getValue(), actual.get(entry.getKey()), 1e-3);
        }
    }

    private void assertKirchhoffCurrentLaw(ResistorNetwork<String> network, NetworkTransformation<String> solution) {
        final var random = new Random(1234);
        Object2DoubleMap<String> fixedVoltages = new Object2DoubleOpenHashMap<>();
        for (final var source : network.getFixedNodes()) {
            fixedVoltages.put(source, random.nextDouble(1000));
        }
        final var voltages = solution.evaluate(fixedVoltages);
        for (final var node : network.getNodes()) {
            if (network.isFixed(node)) { continue; }
            final var nodeVoltage = voltages.getDouble(node);
            double current = 0;
            for (final var resistor : network.getIncidentResistors(node)) {
                final var endVoltage = voltages.getDouble(resistor.otherEnd());
                current += (nodeVoltage - endVoltage) / resistor.resistance();
            }
            assertEquals(0, current, 1e-3);
        }
    }
}