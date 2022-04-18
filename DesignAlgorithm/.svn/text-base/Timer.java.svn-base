package DesignAlgorithm;

/**
 * Purpose:
 * An Object to measure the elapsed time between the most recent call
 * to method resetTimer() and the call to method elapsedTime().  The
 * class creates and starts a timer when a Timer object is
 * instantiated, and returns the elapsed time in seconds whenever
 * elapsedTime() is called.
 **/
public class Timer {

    private long savedTime;

    /**
     * Instantiate a new Timer object, initialized to the time of creation.
     */
    public Timer() {
        resetTimer();
    }

    /**
     * Establish a new baseline time for timings.  In general you should
     * invoke resetTimer() immediately before performing some action
     * you're interested in timing.
     */
    public void resetTimer() {
        savedTime = System.nanoTime();
    }

    /**
     * Calculate the elapsed time since the more recent of the last
     * invocation of resetTimer() or since the Timer object was instantiated.
     * The Java virtual machine now measures to the nearest nanosecond.
     * @return
     * the elapsed time in seconds.
     */
    public double elapsedTime() {
        return (System.nanoTime() - savedTime) * 0.000000001 / 60;
    }
}
