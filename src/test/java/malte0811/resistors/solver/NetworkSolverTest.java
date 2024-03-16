package malte0811.resistors.solver;

import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import malte0811.resistors.data.LinearCombination;
import malte0811.resistors.data.MutableLinearCombination;
import malte0811.resistors.data.MutableNetwork;
import malte0811.resistors.data.ResistorNetwork;
import org.junit.jupiter.api.Test;

import java.util.Map;

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
        assertEqual(Map.of(positive, 1.), solution.get(positive));
        assertEqual(Map.of(negative, 1.), solution.get(negative));
        assertEqual(Map.of(positive, 0.9, negative, 0.1), solution.get(innerRight));
        assertEqual(Map.of(positive, 0.5, negative, 0.5), solution.get(innerLeft1));
        assertEqual(Map.of(positive, 0.5, negative, 0.5), solution.get(leaf));
        assertEqual(Map.of(positive, 5 / 6., negative, 1 / 6.), solution.get(innerLeft2));
    }

    private void assertEqual(Map<String, Double> expected, LinearCombination<String> actual) {
        for (final var entry : expected.entrySet()) {
            assertEquals(entry.getValue(), actual.get(entry.getKey()), 1e-3);
        }
    }
}