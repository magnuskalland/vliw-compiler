package src.instructions;

public class Nop extends OneCycleLatency {
    public Nop(String mnemonic) {
        super(mnemonic);
    }

    @Override
    public String toString() {
        return "nop";
    }
}
