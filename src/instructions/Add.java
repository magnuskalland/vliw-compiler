package src.instructions;

public class Add extends AbstractThreeOperand implements IInstructionAlu, IConsumer {
    private int originalOpa, originalOpb;

    public Add(int id, String mnemonic, int dest, int opa, int opb) {
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
