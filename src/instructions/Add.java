package src.instructions;

public class Add extends ThreeOperand {
    int dest, opa, opb;

    public Add(String mnemonic, int dest, int opa, int opb) {
        super(mnemonic, dest, opa, opb);
    }
}
