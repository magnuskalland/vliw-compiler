package src.instructions;

public class Sub extends ThreeOperand {
    int dest, opa, opb;

    public Sub(String mnemonic, int dest, int opa, int opb) {
        super(mnemonic, dest, opa, opb);
    }
}
