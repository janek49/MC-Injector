package pl.janek49.iniektor.client.util;

public class Timer {
    private long lastTime = getCurrentTime();
    public Timer() {
        reset();
    }
    public long getCurrentTime() {
        return System.nanoTime() / 1000000;
    }
    public long getLastTime() {
        return lastTime;
    }
    public long getDifference() {
        return getCurrentTime() - lastTime;
    }
    public void reset() {
        lastTime = getCurrentTime();
    }
    public boolean hasReached(long milliseconds) {
        return getDifference() >= milliseconds;
    }
}
