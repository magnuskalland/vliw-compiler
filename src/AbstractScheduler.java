package src;

import java.util.ArrayList;
import java.util.Set;

import src.instructions.AbstractInstruction;
import src.instructions.AbstractInstructionBranch;
import src.instructions.AbstractProducer;
import src.instructions.IConsumer;
import src.instructions.MovPredicate;

abstract class AbstractScheduler {
    protected Schedule schedule;
    protected ArrayList<AbstractInstruction> program;
    protected DependencyTable dependencyTable;
    protected int initiationInterval, programLoopStart, programLoopEnd;
    protected int loopStart, loopEnd;

    protected ArrayList<Set<AbstractProducer>> localDependencies;
    protected ArrayList<Set<AbstractProducer>> interloopDependencies;
    protected ArrayList<Set<AbstractProducer>> loopInvariantDependencies;
    protected ArrayList<Set<AbstractProducer>> postLoopDependencies;

    protected AbstractInstructionBranch loop;

    public abstract Schedule schedule();

    public AbstractScheduler(DecodedProgram program, DependencyTable dependencyTable) {
        this.program = program.getProgram();
        this.dependencyTable = dependencyTable;
        this.initiationInterval = program.optimalInitiationInterval();
        this.programLoopStart = program.getLoopStart();
        this.programLoopEnd = program.getLoopEnd();
        this.schedule = new Schedule();

        localDependencies = dependencyTable.getLocalDependencies();
        interloopDependencies = dependencyTable.getInterloopDependencies();
        loopInvariantDependencies = dependencyTable.getLoopInvariantDependencies();
        postLoopDependencies = dependencyTable.getPostLoopDependencies();
    }

    /**
     * Schedule the first basic block, i.e. the block preceding loop/up to
     * loopStart.
     */
    protected void scheduleBasicBlockZero() {
        int newLoopStart = programLoopStart;
        for (int i = 0; i < programLoopStart; i++) {
            int earliest = program.get(i) instanceof IConsumer ? getEarliestAddress((IConsumer) program.get(i), 0)
                    : 0;
            newLoopStart = schedule.addEarliest(earliest, newLoopStart, program.get(i));
        }
        loopStart = schedule.getSchedule().size();
    }

    /**
     * Schedule the second basic block, i.e. the loop. All addresses i s.t.
     * loopStart <= i <= loopEnd.
     */
    public void scheduleBasicBlockOne() {
        schedule.addInitiationInterval(initiationInterval);
        int newLoopEnd = loopStart + initiationInterval;
        for (int i = programLoopStart; i < programLoopEnd + 1; i++) {
            if (program.get(i) instanceof AbstractInstructionBranch) {
                loop = (AbstractInstructionBranch) program.get(i);
                continue;
            }

            int earliest = program.get(i) instanceof IConsumer
                    ? getEarliestAddress((IConsumer) program.get(i), loopStart)
                    : loopStart;
            int pushedEnd = schedule.addEarliest(earliest, newLoopEnd, program.get(i));
            if (pushedEnd > loopStart + initiationInterval) {
                newLoopEnd = pushedEnd;
            }
        }
        loopEnd = schedule.getSchedule().size();
    }

    /**
     * Schedule the third basic block, i.e. after loop.
     */
    protected void scheduleBasicBlockTwo() {
        for (int i = programLoopEnd + 1; i < program.size(); i++) {
            int earliest = program.get(i) instanceof IConsumer ? getEarliestAddress((IConsumer) program.get(i), loopEnd)
                    : loopEnd;
            schedule.addEarliest(earliest, Integer.MAX_VALUE, program.get(i));
        }
    }

    protected boolean fixInterLoopDependencies() {
        Bundle bundle;
        AbstractInstruction consumer;

        for (int i = loopStart; i < loopEnd; i++) {
            bundle = schedule.getSchedule().get(i);

            /* go through interloop dependencies */
            for (int j = 0; j < bundle.getSlots().length; j++) {
                if (!(bundle.getSlots()[j] instanceof IConsumer)) {
                    continue;
                }

                consumer = bundle.getSlots()[j];
                for (AbstractProducer producer : interloopDependencies.get(consumer.getId())) {
                    if (producer.getAddress() >= programLoopStart && producer.getAddress() <= programLoopEnd) {
                        if (!verifyInterloopDependency(producer, consumer)) {
                            schedule.addEmptyBundle(loopEnd++);
                            initiationInterval++;
                            return fixInterLoopDependencies();
                        }
                    }
                }
            }
        }
        return true;
    }

    protected boolean verifyInterloopDependency(AbstractInstruction instr1, AbstractInstruction instr2) {
        return instr1.getScheduledAddress() + instr1.getLatency() <= 1 + instr2.getScheduledAddress()
                + initiationInterval;
    }

    protected MovPredicate createPredicateInstruction(int reg, boolean value) {
        return new MovPredicate(-1, "mov", reg, value);
    }

    protected int getEarliestAddress(IConsumer consumer, int base) {
        base = getEarliestAddressFromDependencyType(localDependencies.get(((AbstractInstruction) consumer).getId()),
                base);
        base = getEarliestAddressFromInterloopDependency(
                interloopDependencies.get(((AbstractInstruction) consumer).getId()),
                base);
        base = getEarliestAddressFromDependencyType(
                loopInvariantDependencies.get(((AbstractInstruction) consumer).getId()),
                base);
        base = getEarliestAddressFromDependencyType(postLoopDependencies.get(((AbstractInstruction) consumer).getId()),
                base);
        return base;
    }

    private int getEarliestAddressFromDependencyType(Set<AbstractProducer> dependencies, int base) {
        for (AbstractProducer producer : dependencies) {
            if (producer.getScheduledAddress() + producer.getLatency() >= base) {
                base = producer.getScheduledAddress() + producer.getLatency();
            }
        }
        return base;
    }

    private int getEarliestAddressFromInterloopDependency(Set<AbstractProducer> dependencies, int base) {

        for (AbstractProducer producer : dependencies) {
            /* a preloop dependency */
            if (producer.getAddress() < programLoopStart) {
                if (producer.getScheduledAddress() + producer.getLatency() >= base) {
                    base = producer.getScheduledAddress() + producer.getLatency();
                }
            }

            /* an actual interloop dependency */
            else if (producer.getAddress() >= programLoopStart && producer.getAddress() <= programLoopEnd) {
                int distance = loopEnd - producer.getScheduledAddress() + base - loopStart;
                if (distance + producer.getLatency() >= base) {
                    base = distance + producer.getLatency();
                }
            }
        }
        return base;
    }

}
