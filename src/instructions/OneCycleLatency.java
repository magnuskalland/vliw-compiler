package src.instructions;

public abstract class OneCycleLatency extends DecodedInstruction {
    public OneCycleLatency(String mnemonic) {
        super(mnemonic);
    }

    @Override
    public int getLatency() {
        return 1;
    }
}
