package src.instructions;

public class St extends AbstractInstruction implements IInstructionMem, IConsumer {
    int src, imm, addr;

    public St(int id, String mnemonic, int src, int imm, int addr) {
        super(id, mnemonic);
        this.src = src;
        this.imm = imm;
        this.addr = addr;
    }

    @Override
    public String toString() {
        return String.format("%s %s x%s, %d(x%d)", predicateToString(), mnemonic, src, imm, addr);
    }

    public int getImm() {
        return imm;
    }

    public int getAddr() {
        return addr;
    }

    @Override
    public int[] getReadRegisters() {
        return new int[] { src, addr };
    }
}
