package src;

import src.instructions.DecodedInstruction;

class PipelinedScheduler extends AbstractScheduler {
    static final int NON_ROTATING_REG_START = 1;
    static final int ROTATING_REG_START = 32;
    private int registerStep;

    public PipelinedScheduler(DecodedProgram program, DependencyTable dependencyTable) {
        super(program, dependencyTable);
    }

    @Override
    public Schedule schedule() {
        return null;
    }

    class Reserved extends DecodedInstruction {
        public Reserved() {
            super("reserved");
        }

        @Override
        public String toString() {
            return "--";
        }

        @Override
        public int getLatency() {
            return 0;
        }
    }
}
