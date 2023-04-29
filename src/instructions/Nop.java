package src.instructions;

public class Nop extends AbstractInstruction {
    public Nop(int id, String mnemonic) {
        super(id, mnemonic);
    }

    @Override
    public String toString() {
        return "nop";
    }
}
