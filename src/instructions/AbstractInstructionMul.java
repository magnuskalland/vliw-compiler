package src.instructions;

public abstract class AbstractInstructionMul extends AbstractProducer
        implements IThreeCycleLatency {
    int opa, opb;

    public AbstractInstructionMul(int id, String mnemonic, int dest, int opa, int opb) {
        super(id, mnemonic, dest);
        this.opa = opa;
        this.opb = opb;
    }

    @Override
    public String toString() {
        return String.format("%s %s x%d, x%d, x%d", predicateToString(), mnemonic, dest, opa, opb);
    }
}
