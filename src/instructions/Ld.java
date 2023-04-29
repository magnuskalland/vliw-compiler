package src.instructions;

public class Ld extends Mem {
    public Ld(String mnemonic, int reg, int imm, int addr) {
        super(mnemonic, reg, imm, addr);
    }
}
