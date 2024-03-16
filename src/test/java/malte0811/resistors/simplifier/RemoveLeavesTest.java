package malte0811.resistors.simplifier;

import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import malte0811.resistors.data.MutableNetwork;
import malte0811.resistors.data.MutableVoltageMap;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RemoveLeavesTest {
    private static final String LEAF_1 = "leaf1";
    private static final String LEAF_2 = "leaf2";
    private static final String LEAF_3 = "leaf3";
    private static final NetworkSimplifier<String> SIMPLIFIER = new RemoveLeaves<>();

    @Test
    public void testLeafRemoval() {
        final var network = new MutableNetwork<String>();
        final var centerA = "centerA";
        final var centerB = "centerB";
        final var centerC = "centerC";
        network.addResistor(centerA, centerB, 1)
                .addResistor(centerC, centerB, 1)
                .addResistor(LEAF_1, centerA, 1)
                .addResistor(LEAF_2, centerA, 1)
                .addResistor(LEAF_3, centerC, 1)
                .markFixed(centerC)
                .markFixed(centerA);
        final var maybeSimplified = SIMPLIFIER.simplify(network);
        assertTrue(maybeSimplified.isPresent());
        final var simplified = maybeSimplified.get().simplifiedNetwork();
        assertEquals(Set.copyOf(simplified.getNodes()), Set.of(centerA, centerB, centerC));
        assertEquals(simplified.getIncidentResistors(centerA).size(), 1);
        assertEquals(simplified.getIncidentResistors(centerB).size(), 2);
        assertEquals(simplified.getIncidentResistors(centerC).size(), 1);

        assertTrue(SIMPLIFIER.simplify(simplified).isEmpty());

        final var simplifiedMap = new MutableVoltageMap<>(new Object2DoubleOpenHashMap<String>());
        simplifiedMap.voltage().put(centerA, 1);
        simplifiedMap.voltage().put(centerB, 0.5);
        simplifiedMap.voltage().put(centerC, 0);
        final var expectedTotal = simplifiedMap.copy();
        expectedTotal.voltage().put(LEAF_1, 1);
        expectedTotal.voltage().put(LEAF_2, 1);
        expectedTotal.voltage().put(LEAF_3, 0);
        final var extended = maybeSimplified.get().extendSolution().apply(simplifiedMap);
        assertEquals(expectedTotal, extended);
    }

    @Test
    public void testFixedLeaf() {
        final var network = new MutableNetwork<String>();
        final var center = "centerA";
        network.addResistor(LEAF_1, center, 1)
                .addResistor(LEAF_2, center, 1)
                .addResistor(LEAF_3, center, 1)
                .markFixed(center)
                .markFixed(LEAF_1);
        final var maybeSimplified = SIMPLIFIER.simplify(network);
        assertTrue(maybeSimplified.isPresent());
        final var simplified = maybeSimplified.get().simplifiedNetwork();
        assertEquals(Set.copyOf(simplified.getNodes()), Set.of(center, LEAF_1));
    }
}