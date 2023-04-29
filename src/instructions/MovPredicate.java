package src.instructions;

public class MovPredicate extends AbstractInstruction implements IInstructionAlu {
    int dest;
    boolean value;

    public MovPredicate(int id, String mnemonic, int dest, boolean val) {
        super(id, mnemonic);
        this.dest = dest;
        this.value = val;
    }

    @Override
    public String toString() {
        return String.format("%s %s p%d, %s", predicateToString(), mnemonic, dest, value);
    }
}
