package src.instructions;

public class Movi extends TwoOperand {
    int dest, imm;

    public Movi(String mnemonic, int dest, int imm) {
        super(mnemonic);
        this.dest = dest;
        this.imm = imm;
    }

    @Override
    public String toString() {
        return String.format("%s x%d, %d\n", mnemonic, dest, imm);
    }
}
