package src.instructions;

public class Mulu extends AbstractInstructionMul implements IConsumer {
    public Mulu(int id, String mnemonic, int dest, int opa, int opb) {
        super(id, mnemonic, dest, opa, opb);
    }

    @Override
    public int[] getReadRegisters() {
        return new int[] { opa, opb };
    }
}
