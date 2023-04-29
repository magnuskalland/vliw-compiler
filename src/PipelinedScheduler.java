package src;

import src.instructions.*;

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

    @Override
    protected Schedule reschedule(int initiationInterval) {
        return null;
    }

    @Override
    public void scheduleBasicBlockZero() {
    }

    @Override
    public void scheduleBasicBlockOne() {

    }

    @Override
    public void scheduleBasicBlockTwo() {

    }

    private int rrbShiftCount(AbstractProducer consumer,
            AbstractProducer producer) {
        return producer.getDestination() + (consumer.getStage() - producer.getStage());
    }

    private class Reserved extends AbstractInstruction {
        public Reserved() {
            super(-1, "reserved");
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
