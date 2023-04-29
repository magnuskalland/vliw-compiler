package src.instructions;

interface IThreeCycleLatency {
    default int getLatency() {
        return 3;
    }
}