package com.grinder.util.time;

import java.util.concurrent.TimeUnit;

/**
 * Created by Stan van der Bend on 28/12/2017.
 *
 * project: OSRS_PK_Server
 * package: com.elvarg.util
 */
public enum TimeUtil {

    MILLIS,
    CLIENT_CYCLES,
    GAME_CYCLES;

    private final static double TIME_CONSTANT =  0.02857D;
    private final static double CYCLE_DURATION_IN_MILLIS =  600;

    public long toMillis(long duration){
        if(equals(CLIENT_CYCLES))
            return (int) (toGameCycles(duration) * CYCLE_DURATION_IN_MILLIS);
        if(equals(GAME_CYCLES))
            return (int) (duration * CYCLE_DURATION_IN_MILLIS);
        else return duration;
    }

    public int toSeconds(long duration) {
        return (int) TimeUnit.MILLISECONDS.toSeconds(toMillis(duration));
    }

    public long toClientCycles(long duration){
        if(equals(MILLIS))
            return (int) (toGameCycles(duration) / TIME_CONSTANT);
        if(equals(GAME_CYCLES))
            return (int) (duration / TIME_CONSTANT);
        else return duration;
    }

    public int toGameCycles(long duration){
        if(equals(MILLIS))
            return (int) (duration / CYCLE_DURATION_IN_MILLIS);
        if(equals(CLIENT_CYCLES))
            return (int) (duration * TIME_CONSTANT);
        else return Math.toIntExact(duration);
    }

}
