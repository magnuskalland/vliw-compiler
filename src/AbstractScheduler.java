package src;

import java.util.ArrayList;

import src.instructions.DecodedInstruction;

abstract class AbstractScheduler {
    Schedule schedule;
    private ArrayList<DecodedInstruction> program;
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
        this.program = program.getProgram();
        this.dependencyTable = dependencyTable;
        this.initiationInterval = program.optimalInitiationInterval();
        this.loopStart = program.getLoopStart();
        this.loopEnd = program.getLoopEnd();
        this.schedule = new Schedule();
    }

    protected boolean checkInterloopDependency(DecodedInstruction instr, Bundle bundle1, Bundle bundle2) {
        return bundle1.getAddress() + instr.getLatency() <= bundle2.getAddress() + initiationInterval;
    }

}
