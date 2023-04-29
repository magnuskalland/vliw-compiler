package src.instructions;

public class Mov extends TwoOperand {
    int dest, source;

    public Mov(String mnemonic, int dest, int source) {
        super(mnemonic);
        this.dest = dest;
        this.source = source;
    }

    @Override
    public String toString() {
        return String.format("%s x%d, x%d\n", mnemonic, dest, source);
    }
}
