package malte0811.resistors.data;

public interface ReadOnlyVoltageMap<NodeKey> {
    double getVoltage(NodeKey key);

    VoltageMap<NodeKey> copy();
}
