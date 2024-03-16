package malte0811.resistors.data;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;

public record MutableVoltageMap<NodeKey>(Object2DoubleMap<NodeKey> voltage) implements VoltageMap<NodeKey> {
    @Override
    public double getVoltage(NodeKey nodeKey) {
        return voltage.getDouble(nodeKey);
    }

    @Override
    public MutableVoltageMap<NodeKey> copy() {
        return new MutableVoltageMap<>(new Object2DoubleOpenHashMap<>(this.voltage));
    }
}
