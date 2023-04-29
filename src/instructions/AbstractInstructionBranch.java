package src.instructions;

public abstract class AbstractInstructionBranch extends AbstractInstruction {
    int label;

    public AbstractInstructionBranch(int id, String mnemonic, int label) {
        super(id, mnemonic);
        this.label = label;
    }

    public int getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", predicateToString(), mnemonic, label);
    }
}
