package src;

import java.util.Arrays;

import src.instructions.*;

class Bundle {

    private AbstractInstruction[] slots = new AbstractInstruction[5];
    private int address;

    public final int ALU0 = 0;
    public final int ALU1 = 1;
    public final int MUL = 2;
    public final int MEM = 3;
    public final int BRANCH = 4;

    public Bundle(int address) {
        Arrays.fill(slots, null);
        this.address = address;
    }

    public int getAddress() {
        return address;
    }

    public AbstractInstruction[] getSlots() {
        return slots;
    }

    public boolean addInstruction(AbstractInstruction instr) {
        instr.setScheduledAddress(address);
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

    public int getSlot(AbstractInstruction instr) {
        if (instr instanceof IInstructionAlu) {
            if (slots[0] == instr) {
                return ALU0;
            }
            if (slots[1] == null) {
                return ALU1;
            }
        }

        else if (instr instanceof AbstractInstructionMul) {
            return MUL;
        }

        else if (instr instanceof IInstructionMem) {
            return MEM;
        }

        else if (instr instanceof AbstractInstructionBranch) {
            return BRANCH;
        }

        System.out.printf("Instruction %s not in bundle\n", instr);
        System.exit(1);
        return -1;
    }

    public void insertReserved(Reserved reserved, int slot) {
        if (slots[slot] == null) {
            slots[slot] = reserved;
        }
    }

    public boolean full() {
        for (int i = 0; i < slots.length; i++)
            if (slots[i] == null)
                return false;
        return true;
    }

    public String toStringCustom() {
        int width = 80;
        StringBuilder sb = new StringBuilder();

        /* preamble */
        sb.append(new String(new char[width]).replace("\0", "-"));
        sb.append("\n");
        sb.append("| PC |     ALU0     |     ALU1     |     MULT     |    MEM    |     BRANCH     |");
        sb.append("\n");
        sb.append(new String(new char[width]).replace("\0", "-"));
        sb.append("\n");

        /* body */
        sb.append(String.format("| %2d | %-12s | %-12s | %-12s | %-8s | %-12s |",
                address, getInstruction(slots[0]), getInstruction(slots[1]), getInstruction(slots[2]),
                getInstruction(slots[3]), getInstruction(slots[4])));
        sb.append("\n");
        sb.append(new String(new char[width]).replace("\0", "-"));
        sb.append("\n");

        return sb.toString();
    }

    @Override
    public String toString() {
        return Arrays.toString(slots);
    }

    public AbstractInstruction getInstruction(AbstractInstruction instr) {
        if (instr == null) {
            return new Nop(-1, "nop");
        }
        return instr;
    }

}
