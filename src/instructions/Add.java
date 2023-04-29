package src.instructions;

public class Add extends AbstractThreeOperand implements IInstructionAlu, IConsumer {
    public Add(int id, String mnemonic, int dest, int opa, int opb) {
        super(id, mnemonic, dest, opa, opb);
    }

    @Override
    public int[] getReadRegisters() {
        return new int[] { opa, opb };
    }
}
