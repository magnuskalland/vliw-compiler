package src.instructions;

public abstract class Branch extends OneCycleLatency {
    int label;

    public Branch(String mnemonic, int label) {
        super(mnemonic);
        this.label = label;
    }

    int getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return String.format("%s, %s\n", mnemonic, label);
    }

    @Override
    public int getLatency() {
        return 1;
    }
}
