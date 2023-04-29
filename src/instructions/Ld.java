package src.instructions;

public class Ld extends AbstractProducer implements IInstructionMem, IConsumer {
    int imm, addr;

    public Ld(int id, String mnemonic, int dest, int imm, int addr) {
        super(id, mnemonic, dest);
        this.imm = imm;
        this.addr = addr;
    }

    @Override
    public String toString() {
        return String.format("%s %s x%s, %d(x%d)", predicateToString(), mnemonic, dest, imm, addr);
    }

    public int getImm() {
        return imm;
    }

    public int getAddr() {
        return addr;
    }

    @Override
    public int[] getReadRegisters() {
        return new int[] { addr };
    }
}
