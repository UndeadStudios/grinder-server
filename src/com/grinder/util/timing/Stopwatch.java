package com.grinder.util.timing;

import java.util.concurrent.TimeUnit;

public class Stopwatch {

    private long time = System.currentTimeMillis();

    public Stopwatch() {
        time = 0;
    }

    public Stopwatch headStart(long startAt) {
        time = System.currentTimeMillis() - startAt;
        return this;
    }

    public Stopwatch reset(long i) {
        time = i;
        return this;
    }

    public Stopwatch reset() {
        time = System.currentTimeMillis();
        return this;
    }

    public long elapsed() {
        return System.currentTimeMillis() - time;
    }

    public boolean elapsed(long time) {
        return elapsed() >= time;
    }

    public long getTime() {
        return time;
    }

    public String getTimeElapsed() {
        StringBuilder builder = new StringBuilder();

        long elapsed = elapsed();

        int minutesElapsed = (int) TimeUnit.MILLISECONDS.toMinutes(elapsed) % 60;
        int secondsElapsed = (int) TimeUnit.MILLISECONDS.toSeconds(elapsed) % 60;

        builder.append(minutesElapsed).append(":");

        if(secondsElapsed < 10) {
            builder.append(0);
        }

        builder.append(secondsElapsed);

        return builder.toString();
    }
}