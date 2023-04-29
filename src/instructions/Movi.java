package src.instructions;

public class Movi extends AbstractProducer implements IInstructionAlu {
    int imm;

    public Movi(int id, String mnemonic, int dest, int imm) {
        super(id, mnemonic, dest);
        this.imm = imm;
    }

    @Override
    public String toString() {
        return String.format("%s %s x%d, %d", predicateToString(), mnemonic, dest, imm);
    }
}
