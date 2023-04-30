package src;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import src.instructions.AbstractInstruction;
import src.instructions.AbstractInstructionBranch;
import src.instructions.AbstractProducer;
import src.instructions.IConsumer;
import src.instructions.Mov;

class SimpleScheduler extends AbstractScheduler {
    static final int REG_START = 1;
    private int allocatedRegisters = 0;

    public SimpleScheduler(DecodedProgram program, DependencyTable dependencyTable) {
        super(program, dependencyTable);
    }

    @Override
    public Schedule schedule() {
        scheduleBasicBlockZero();
        if (loopStart > 0) {
            scheduleBasicBlockOne();

        }
        scheduleBasicBlockTwo();

        fixInterLoopDependencies();

        if (programLoopStart > 0) {
            allocateRegisters();
            loop.setLabel(loopStart);
            loop.setScheduledAddress(loopEnd - 1);
            schedule.setHardAddress(loop);

        }

        return schedule;
    }

    private void allocateRegisters() {
        int newDestination;
        Set<AbstractProducer> interloopDependency;
        Set<Mov> movs = new HashSet<>();
        Mov mov;

        for (AbstractInstruction producer : schedule.getInProgramOrder(0, schedule.length())) {
            if (!(producer instanceof AbstractProducer)) {
                continue;
            }
            newDestination = REG_START + allocatedRegisters++;

            for (AbstractInstruction consumer : program) {
                if (!(consumer instanceof IConsumer)) {
                    continue;
                }
                if (producer.getId() >= consumer.getId()) {
                    continue;
                }

                /* step 2: reassign source registers to newly allocated destination registers */
                ((IConsumer) consumer).reassign(((AbstractProducer) producer).getDestination(), newDestination);
            }
            /* step 1, assign fresh registers to producers */
            ((AbstractProducer) producer).setDestination(newDestination);
        }

        for (AbstractInstruction consumer : schedule.getInProgramOrder(0, schedule.length())) {
            if (!(consumer instanceof IConsumer)) {
                continue;
            }

            for (int i = 0; i < ((IConsumer) consumer).getReadRegisters().length; i++) {
                boolean written = false;
                for (AbstractInstruction producer : schedule.getInProgramOrder(0, consumer.getScheduledAddress())) {
                    if (!(producer instanceof AbstractProducer)) {
                        continue;
                    }
                    if (((AbstractProducer) producer)
                            .getDestination() == ((IConsumer) consumer).getReadRegisters()[i]) {
                        written = true;
                        break;
                    }
                }
                if (!written) {
                    /* step 4: allocate fresh registers to unwritten sources */
                    ((IConsumer) consumer).reassign(((IConsumer) consumer).getReadRegisters()[i],
                            REG_START + allocatedRegisters++);
                }
            }
        }

        for (AbstractInstruction consumer : schedule.getInProgramOrder(loopStart, loopEnd)) {
            if (!(consumer instanceof IConsumer) || !(consumer instanceof AbstractProducer)) {
                continue;
            }

            interloopDependency = interloopDependencies.get(consumer.getId());
            for (AbstractProducer dependency : interloopDependency) {
                if (dependency.getAddress() > loopStart) {
                    continue;
                }

                for (int i = 0; i < ((IConsumer) consumer).getReadRegisters().length; i++) {
                    if (dependency.getDestination() == ((IConsumer) consumer).getReadRegisters()[i]) {

                        mov = new Mov(consumer.getId(), "mov", dependency.getDestination(),
                                ((AbstractProducer) consumer).getDestination());

                        evaluateRegisterAllocationMovs(movs, mov);

                        /* step 3: fix interloop dependencies */

                    }
                }
            }
        }

        /* add all from set */
        for (Mov m : movs) {
            int pushedEnd, newLoopEnd = loopEnd;
            pushedEnd = schedule.addEarliest(loopEnd - 1, newLoopEnd, m);
            if (pushedEnd > loopStart + initiationInterval) {
                newLoopEnd = pushedEnd;
                loopEnd = pushedEnd;
            }
        }

    }

    private void evaluateRegisterAllocationMovs(Set<Mov> movs, Mov newMov) {
        boolean add = true;
        for (Mov m : movs) {
            if (m.getDestination() == newMov.getDestination()) {
                if (m.getId() < newMov.getId()) {
                    m.setDestination(newMov.getDestination());
                    m.setSource(newMov.getSource());
                }
                add = false;
            }

        }
        if (add) {
            movs.add(newMov);
        }
    }

}
