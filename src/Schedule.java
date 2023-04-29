package src;

import java.util.ArrayList;

public class Schedule {
    private int loopStart, loopEnd;
    private ArrayList<Bundle> schedule;

    public Schedule() {
        schedule = new ArrayList<>();
    }

    public ArrayList<Bundle> getSchedule() {
        return schedule;
    }

    public void setLoopStart(int l) {
        loopStart = l;
    }

    public int getLoopStart() {
        return loopStart;
    }

    public void setLoopEnd(int l) {
        loopEnd = l;
    }

    public int getLoopEnd() {
        return loopEnd;
    }
}
