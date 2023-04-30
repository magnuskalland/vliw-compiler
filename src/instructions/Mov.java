package src.instructions;

import java.util.Map;

public class Mov extends AbstractProducer implements IInstructionAlu, IConsumer {
    int source;
    private int originalSource;

    public Mov(int id, String mnemonic, int dest, int source) {
        super(id, mnemonic, dest);
        this.source = source;
        originalSource = source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getSource() {
        return source;
    }

    @Override
    public String toString() {
        return String.format("%s %s x%d, x%d", predicateToString(), mnemonic, dest, source);
    }

    @Override
    public int[] getReadRegisters() {
        return new int[] { source };
    }

    @Override
    public void reassign(int oldReg, int newReg) {
        if (originalSource == oldReg) {
            source = newReg;
        }
    }
}
