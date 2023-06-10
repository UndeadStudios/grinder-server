package com.grinder.game.model.passages;

import java.util.Arrays;

/**
 * @author L E G E N D
 * Date: 2/6/2021
 * Time: 12:27 AM
 * Discord: "L E G E N D#4380"
 */
public class PassageUtils {

    public static boolean isWoodenGate(int id) {
        return Arrays.stream(new int[]{
                47, 48, 49, 50,
                166, 167, 15588, 15589,
                2050, 2051, 2052, 2053,
                1561, 1562, 1563, 1564,
                1558, 1559, 1560, 1567,
                2261, 2262, 2263, 2264,
                883, 23917, 23918, 23919,
                3725, 3726, 3727, 3728,
                4311, 4312, 4313, 4314,
                8810, 8811, 8812, 8813,
                12816, 12817, 12818, 12819,
                15510, 15511, 15512, 15513,
                15514, 15515, 15516, 15517,
                12989, 12988, 12987, 12986,
                24560, 24561}
        ).anyMatch(gateId -> gateId == id);
    }
}
