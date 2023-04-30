package src.instructions;

public interface IConsumer {
    public int[] getReadRegisters();

    public void reassign(int oldReg, int newReg);

}
