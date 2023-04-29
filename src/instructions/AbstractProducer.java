package src.instructions;

public abstract class AbstractProducer extends AbstractInstruction {
    protected int dest;

    public AbstractProducer(int id, String mnemonic, int dest) {
        super(id, mnemonic);
        this.dest = dest;
    }

    public int getDestination() {
        return dest;
    }
}
