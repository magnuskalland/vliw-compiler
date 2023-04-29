package src.instructions;

public class Addi extends ThreeOperand {
    int dest, opa, opb;

    public Addi(String mnemonic, int dest, int opa, int opb) {
        super(mnemonic, dest, opa, opb);
    }
}
