package src;

class SimpleScheduler extends AbstractScheduler {
    static final int REG_START = 1;

    public SimpleScheduler(DecodedProgram program, DependencyTable dependencyTable) {
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

}
