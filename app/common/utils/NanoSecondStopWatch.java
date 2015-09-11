package common.utils;

/**
 * Created by IntelliJ IDEA.
 * Date: 28/5/14
 * Time: 11:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class NanoSecondStopWatch {
    private final long start;
    private long end = 0;

    public NanoSecondStopWatch() {
        this.start = System.nanoTime();
    }

    public void stop() {
        this.end = System.nanoTime();
    }

    public boolean isStopped() {
        return end != 0;
    }

    public long getElapsedNS() {
        if (end == 0) {
            return 0;
        } else {
            return end - start;
        }
    }

    public double getElapsedMS() {
        return ((double) getElapsedNS()) / 1000000.0;
    }

    public double getElapsedSecs() {
        return ((double) getElapsedNS()) / 1000000000.0;
    }

    public double getMSPerItem(int numItems) {
        return getElapsedMS() / ((double) numItems);
    }
}
