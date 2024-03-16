package malte0811.resistors.data;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;

public record VoltageMap<NodeKey>(Object2DoubleMap<NodeKey> voltage) implements ReadOnlyVoltageMap<NodeKey> {
    @Override
    public double getVoltage(NodeKey nodeKey) {
        return voltage.getDouble(nodeKey);
    }

    @Override
    public VoltageMap<NodeKey> copy() {
        return new VoltageMap<>(new Object2DoubleOpenHashMap<>(this.voltage));
    }
}
