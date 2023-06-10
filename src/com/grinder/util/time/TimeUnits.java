package com.grinder.util.time;

/**
 * TODO: add documentation.
 *
 * @author Unknown
 * @version 1.0
 * @since 2019-04-22
 */
public enum TimeUnits {

    TICK(600),
    MILLISECOND(1),
    SECOND(1_000),
    MINUTE(60_000),
    HOUR(3_600_000),
    DAY(86_400_000),
    WEEK(604_800_000);

    long milliseconds;

    TimeUnits(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    public long getMilisecondValue() {
        return milliseconds;
    }

    public static TimeUnits getHighestUnitForMilliseconds(long milliseconds) {
        TimeUnits unitFound = MILLISECOND;
        for (TimeUnits unit : TimeUnits.values())
            if (unit.getMilisecondValue() > unitFound.getMilisecondValue() && unit.getMilisecondValue() < milliseconds && !unit.equals(TICK))
                unitFound = unit;
        return unitFound;
    }

    public static TimeUnits getSecondHighestUnitForMilliseconds(long milliseconds) {
        TimeUnits unitFound = MILLISECOND;
        TimeUnits secondUnitFound = MILLISECOND;
        for (TimeUnits unit : TimeUnits.values())
            if (unit.getMilisecondValue() > unitFound.getMilisecondValue() && unit.getMilisecondValue() < milliseconds && !unit.equals(TICK)) {
                secondUnitFound = unitFound;
                unitFound = unit;
            }
        return secondUnitFound;
    }
}
