package src.instructions;

public class Reserved extends AbstractInstruction {
    public Reserved(int id) {
        super(id, "reserved");
    }

    @Override
    public String toString() {
        return String.format("reserved(%d)", id);
    }

    @Override
    public int getLatency() {
        return 0;
    }
}
