package src;

import java.util.Arrays;

import src.instructions.*;

class Bundle {

    private AbstractInstruction[] slots;
    private int address;

    public Bundle(int address) {
        slots = new AbstractInstruction[5];
        Arrays.fill(slots, null);
        this.address = address;
    }

    public int getAddress() {
        return address;
    }

    public boolean addInstruction(AbstractInstruction instr) {
        if (instr instanceof IInstructionAlu) {
            if (slots[0] == null) {
                slots[0] = instr;
                return true;
            }
            if (slots[1] == null) {
                slots[1] = instr;
                return true;
            }
            return false;
        }

        else if (instr instanceof AbstractInstructionMul) {
            if (slots[2] == null) {
                slots[2] = instr;
                return true;
            }
            return false;
        } else if (instr instanceof IInstructionMem) {
            if (slots[3] == null) {
                slots[3] = instr;
                return true;
            }
            return false;
        } else if (instr instanceof AbstractInstructionBranch) {
            if (slots[4] == null) {
                slots[4] = instr;
                return true;
            }
            return false;
        }

        System.out.printf("Failed to pattern match %s\n", instr.toString());
        System.exit(1);
        return false;
    }

    public boolean full() {
        for (int i = 0; i < slots.length; i++)
            if (slots[i] == null)
                return false;
        return true;
    }

}
