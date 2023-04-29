package src.instructions;

public class MovLoop extends TwoOperand {
    String dest;
    int imm;

    public MovLoop(String mnemonic, String dest, int imm) {
        super(mnemonic);
        this.dest = dest;
        this.imm = imm;
    }

    @Override
    public String toString() {
        return String.format("%s %s, %d\n", mnemonic, dest, imm);
    }
}
