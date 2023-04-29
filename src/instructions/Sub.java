package src.instructions;

public class Sub extends AbstractThreeOperand implements IInstructionAlu, IConsumer {
    public Sub(int id, String mnemonic, int dest, int opa, int opb) {
        super(id, mnemonic, dest, opa, opb);
    }

    @Override
    public int[] getReadRegisters() {
        return new int[] { opa, opb };
    }
}
