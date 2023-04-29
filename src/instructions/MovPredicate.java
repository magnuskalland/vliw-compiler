package src.instructions;

public class MovPredicate extends TwoOperand {
    int dest;
    boolean value;

    public MovPredicate(String mnemonic, int dest, boolean val) {
        super(mnemonic);
        this.dest = dest;
        this.value = val;
    }

    @Override
    public String toString() {
        return String.format("%s p%d, %s\n", mnemonic, dest, value);
    }
}
