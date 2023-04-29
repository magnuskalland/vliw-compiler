package src.instructions;

public class Mov extends AbstractProducer implements IInstructionAlu, IConsumer {
    int source;

    public Mov(int id, String mnemonic, int dest, int source) {
        super(id, mnemonic, dest);
        this.source = source;
    }

    @Override
    public String toString() {
        return String.format("%s %s x%d, x%d", predicateToString(), mnemonic, dest, source);
    }

    @Override
    public int[] getReadRegisters() {
        return new int[] { source };
    }
}
