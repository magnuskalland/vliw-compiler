package src.instructions;

public class Addi extends AbstractThreeOperand implements IInstructionAlu, IConsumer {
    int imm;

    public Addi(int id, String mnemonic, int dest, int opa, int imm) {
        super(id, mnemonic, dest, opa, imm);
        this.opb = imm;
        this.imm = imm; // rename opb to imm
    }

    @Override
    public int[] getReadRegisters() {
        return new int[] { opa };
    }

    @Override
    public String toString() {
        return String.format("%s %s x%d, x%d, %d", predicateToString(), mnemonic, dest, opa, imm);
    }
}
