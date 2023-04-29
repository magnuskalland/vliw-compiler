package src.instructions;

public abstract class ThreeOperand extends Alu {
    int dest, opa, opb;

    public ThreeOperand(String mnemonic, int dest, int opa, int opb) {
        super(mnemonic);
        this.dest = dest;
        this.opa = opa;
        this.opb = opb;
    }

    @Override
    public String toString() {
        return String.format("%s x%d, x%d, x%d\n", mnemonic, dest, opa, opb);
    }
}
