package src.instructions;

public abstract class ThreeCycleLatency extends DecodedInstruction {
    public ThreeCycleLatency(String mnemonic) {
        super(mnemonic);
    }

    @Override
    public int getLatency() {
        return 3;
    }
}