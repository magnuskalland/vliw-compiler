package src.instructions;

public abstract class DecodedInstruction {
    public abstract int getLatency();

    protected String mnemonic;

    public DecodedInstruction(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    @Override
    public abstract String toString();

}
