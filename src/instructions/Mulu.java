package src.instructions;

public class Mulu extends AbstractInstructionMul implements IConsumer {
    private int originalOpa, originalOpb;

    public Mulu(int id, String mnemonic, int dest, int opa, int opb) {
        super(id, mnemonic, dest, opa, opb);
        originalOpa = opa;
        originalOpb = opb;
    }

    @Override
    public int[] getReadRegisters() {
        return new int[] { opa, opb };
    }

    @Override
    public void reassign(int oldReg, int newReg) {
        if (originalOpa == oldReg) {
            opa = newReg;
        }
        if (originalOpb == oldReg) {
            opb = newReg;
        }
    }
}
