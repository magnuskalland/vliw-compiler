package src.instructions;

public abstract class AbstractInstruction {
    protected int id;
    protected String mnemonic;
    protected Predicate predicate = null;
    protected int stage = -1;

    public AbstractInstruction(int id, String mnemonic) {
        this.id = id;
        this.mnemonic = mnemonic;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public int getStage() {
        return stage;
    }

    public int getId() {
        return id;
    }

    public int getAddress() {
        return id;
    }

    public void setPredicate(int reg) {
        predicate = new Predicate(reg);
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public String predicateToString() {
        return predicate == null ? "" : predicate.toString();
    }

    @Override
    public abstract String toString();

    public int getLatency() {
        return 1;
    }

    private class Predicate {
        int reg;

        public Predicate(int reg) {
            this.reg = reg;
        }

        @Override
        public String toString() {
            return String.format("(p%d)", reg);
        }
    }
}
