package src;

import java.util.ArrayList;
import java.util.HashMap;

import src.instructions.AbstractInstruction;
import src.instructions.MovPredicate;

abstract class AbstractScheduler {
    Schedule schedule;
    private HashMap<Integer, AbstractInstruction> originalProgram;
    DependencyTable dependencyTable;
    int initiationInterval, loopStart, loopEnd;

    public abstract Schedule schedule();

    protected abstract Schedule reschedule(int initiationInterval);

    /**
     * Schedule the first basic block, i.e. the block preceding loop/up to
     * loopStart.
     */
    public abstract void scheduleBasicBlockZero();

    /**
     * Schedule the second basic block, i.e. the loop. All addresses i s.t.
     * loopStart <= i <= loopEnd.
     */
    public abstract void scheduleBasicBlockOne();

    /**
     * Schedule the third basic block, i.e. after loop.
     */
    public abstract void scheduleBasicBlockTwo();

    public AbstractScheduler(DecodedProgram program, DependencyTable dependencyTable) {
        this.originalProgram = new HashMap<>();
        this.dependencyTable = dependencyTable;
        this.initiationInterval = program.optimalInitiationInterval();
        this.loopStart = program.getLoopStart();
        this.loopEnd = program.getLoopEnd();
        this.schedule = new Schedule();

        for (AbstractInstruction instr : program.getProgram()) {
            originalProgram.put(instr.getId(), instr);
        }
    }

    protected boolean checkInterloopDependency(AbstractInstruction instr, Bundle bundle1, Bundle bundle2) {
        return bundle1.getAddress() + instr.getLatency() <= bundle2.getAddress() + initiationInterval;
    }

    protected MovPredicate createPredicateInstruction(int reg, boolean value) {
        return new MovPredicate(-1, "mov", reg, value);
    }

}
