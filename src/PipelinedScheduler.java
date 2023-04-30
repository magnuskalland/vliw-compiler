package src;

import src.instructions.*;

class PipelinedScheduler extends AbstractScheduler {
    static final int NON_ROTATING_REG_START = 1;
    static final int ROTATING_REG_START = 32;
    private int registerStep;

    public PipelinedScheduler(DecodedProgram program, DependencyTable dependencyTable, int initiationInterval) {
        super(program, dependencyTable);
        this.initiationInterval = initiationInterval;
    }

    @Override
    public Schedule schedule() {
        scheduleBasicBlockZero();
        if (loopStart > 0) {
            scheduleBasicBlockOne();
        }
        scheduleBasicBlockTwo();

        if (!fixInterLoopDependencies()) {
            return null;
        }

        return schedule;
    }

    @Override
    public void scheduleBasicBlockOne() {
        AbstractInstruction instruction;
        int loopStages, slotCount = 0, earliest, newLoopEnd, pushedEnd;

        /* add all loop stages to schedule */
        for (int i = programLoopStart; i < programLoopEnd + 1; i++) {
            earliest = program.get(i) instanceof IConsumer
                    ? getEarliestAddress((IConsumer) program.get(i), loopStart)
                    : loopStart;

            earliest = earliest - loopStart;
            if (earliest > slotCount) {
                slotCount = earliest;
            }
        }

        loopStages = (int) Math.ceil((double) slotCount / initiationInterval);
        for (int i = 0; i < loopStages; i++) {
            schedule.addInitiationInterval(initiationInterval);
        }

        newLoopEnd = loopStart + loopStages * initiationInterval;

        /* schedule all instruction and add reserved slots */
        for (int i = programLoopStart; i < programLoopEnd + 1; i++) {
            instruction = program.get(i);
            if (instruction instanceof AbstractInstructionBranch) {
                earliest = loopStart + initiationInterval - 1;
            } else {
                earliest = instruction instanceof IConsumer
                        ? getEarliestAddress((IConsumer) instruction, loopStart)
                        : loopStart;
            }

            pushedEnd = schedule.addEarliest(earliest, newLoopEnd, instruction);
            if (pushedEnd > loopStart + initiationInterval) {
                newLoopEnd = pushedEnd;
            }

            /* insert reserved slot */
            schedule.insertReserved(instruction, initiationInterval, instruction.getScheduledAddress(), loopStart,
                    newLoopEnd);
        }

        loopEnd = schedule.getSchedule().size();
    }

    @Override
    protected boolean fixInterLoopDependencies() {
        Bundle bundle;
        AbstractInstruction instruction, reserved;
        AbstractInstruction[] slots;
        int index;

        for (int i = loopStart; i < loopEnd; i++) {
            bundle = schedule.getSchedule().get(i);
            /* go through interloop dependencies */
            for (int j = 0; j < bundle.getSlots().length; j++) {
                instruction = bundle.getSlots()[j];
                if (!(instruction instanceof IConsumer) || !(instruction instanceof Reserved)) {
                    continue;
                }

                /* check interloop dependencies */
                for (AbstractProducer producer : interloopDependencies.get(instruction.getId())) {
                    if (producer.getAddress() >= programLoopStart && producer.getAddress() <= programLoopEnd) {
                        if (!verifyInterloopDependency(producer, instruction)) {
                            return false;
                        }
                    }
                }
            }
        }

        /* look for reserved */
        for (int i = 0; i < 5; i++) {
            for (int j = loopStart; j < loopEnd; j++) {
                slots = schedule.getSchedule().get(j).getSlots();
                if (slots[i] == null
                        || !(slots[i] instanceof AbstractProducer)
                        || !(slots[i] instanceof IConsumer)
                        || (slots[i] instanceof Reserved)) {
                    continue;
                }
                instruction = slots[i];
                for (int k = instruction.getScheduledAddress() + 1; k < instruction.getScheduledAddress()
                        + instruction.getLatency(); k++) {

                    index = k % loopEnd;
                    if (index < loopStart) {
                        index += loopStart;
                    }
                    reserved = schedule.getSchedule().get(index).getSlots()[i];

                    if (!(reserved instanceof Reserved)) {
                        continue;
                    }

                    if (reserved.getId() == instruction.getId()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private int rrbShiftCount(AbstractProducer consumer,
            AbstractProducer producer) {
        return producer.getDestination() + (consumer.getStage() - producer.getStage());
    }
}
