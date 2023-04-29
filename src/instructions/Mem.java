package src.instructions;

public abstract class Mem extends OneCycleLatency {
    int reg, imm, addr;

    public Mem(String mnemonic, int reg, int imm, int addr) {
        super(mnemonic);
        this.reg = reg;
        this.imm = imm;
        this.addr = addr;
    }

    @Override
    public String toString() {
        return String.format("%s x%s, %d(x%d)\n", mnemonic, reg, imm, addr);
    }
}
