package malte0811.resistors.data;

public interface VoltageMap<NodeKey> {
    double getVoltage(NodeKey key);

    MutableVoltageMap<NodeKey> copy();
}
