package src;

import java.util.ArrayList;
import java.util.Arrays;

import src.instructions.AbstractInstruction;
import src.instructions.Reserved;

public class Schedule {
    protected ArrayList<Bundle> schedule;

    public Schedule() {
        schedule = new ArrayList<>();
    }

    public int length() {
        return schedule.size();
    }

    public void insertReserved(AbstractInstruction instr,
            int initiationInterval, int bundle,
            int start, int end) {
        Reserved reserved = new Reserved(instr.getId());
        int offset = (bundle - start) % initiationInterval;
        int size = end - start;

        for (int i = 0; i < size; i += initiationInterval) {
            schedule.get((i + offset) % size + start).insertReserved(reserved, getBundleSlot(instr));
        }
    }

    public ArrayList<AbstractInstruction> getInProgramOrder(int start, int end) {
        ArrayList<AbstractInstruction> program = new ArrayList<>();
        Bundle bundle;
        for (int i = start; i < end; i++) {
            bundle = schedule.get(i);
            for (int j = 0; j < bundle.getSlots().length; j++) {
                program.add(bundle.getSlots()[j]);
            }
        }
        return program;
    }

    public int addEarliest(int earliest, int indexLimit, AbstractInstruction instr) {
        return recursiveAdd(earliest, indexLimit, instr);
    }

    private int recursiveAdd(int index, int indexLimit, AbstractInstruction instr) {
        Bundle bundle;

        /* need to add a new row in schedule */
        try {
            bundle = schedule.get(index);
        } catch (IndexOutOfBoundsException e) {
            for (int i = schedule.size(); i < index + 1; i++) {
                addEmptyBundle(i);
            }
            addToBundle(instr, index);
            return schedule.size();
        }

        /* need to push subsequent part of schedule */
        if (index == indexLimit) {
            addEmptyBundle(index);
            addToBundle(instr, index);
            return ++indexLimit;
        }

        /* add to current bundle */
        if (bundle.addInstruction(instr)) {
            return indexLimit;
        }
        return recursiveAdd(index + 1, indexLimit, instr);
    }

    public void addInitiationInterval(int initiationInterval) {
        int start = schedule.size();
        for (int i = start; i < start + initiationInterval; i++) {
            addEmptyBundle(i);
        }
    }

    public void setHardAddress(AbstractInstruction instr) {
        schedule.get(instr.getScheduledAddress()).addInstruction(instr);
    }

    public void addEmptyBundle(int index) {
        if (index == schedule.size()) {
            schedule.add(new Bundle(index));
            return;
        }
        schedule.add(index, new Bundle(index));
    }

    private void addToBundle(AbstractInstruction instr, int index) {
        schedule.get(index).addInstruction(instr);
    }

    public ArrayList<Bundle> getSchedule() {
        return schedule;
    }

    public int getBundleSlot(AbstractInstruction instr) {
        return schedule.get(instr.getScheduledAddress()).getSlot(instr);
    }

    public String toStringCustom() {
        int width = 100;
        StringBuilder sb = new StringBuilder();
        Bundle bundle;
        AbstractInstruction[] slots;

        /* preamble */
        sb.append(new String(new char[width]).replace("\0", "-"));
        sb.append("\n");
        sb.append(
                "| PC |       ALU0       |       ALU1       |       MULT       |      MEM      |       BRANCH       |");
        sb.append("\n");
        sb.append(new String(new char[width]).replace("\0", "-"));
        sb.append("\n");

        /* body */
        for (int i = 0; i < schedule.size(); i++) {
            bundle = schedule.get(i);
            slots = bundle.getSlots();
            sb.append(String.format("| %2d | %-16s | %-16s | %-16s | %-13s | %-18s |",
                    i, bundle.getInstruction(slots[0]), bundle.getInstruction(slots[1]),
                    bundle.getInstruction(slots[2]),
                    bundle.getInstruction(slots[3]), bundle.getInstruction(slots[4])));
            sb.append("\n");
            sb.append(new String(new char[width]).replace("\0", "-"));
            sb.append("\n");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return Arrays.toString(schedule.toArray());
    }

}
