package src.instructions;

public class MovLoop extends AbstractInstruction implements IInstructionAlu {
    String dest;
    int imm;

    public MovLoop(int id, String mnemonic, String dest, int imm) {
        super(id, mnemonic);
        this.dest = dest;
        this.imm = imm;
    }

    public String getDest() {
        return dest;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s, %d", predicateToString(), mnemonic, dest, imm);
    }
}
