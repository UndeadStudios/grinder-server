package com.grinder.game.content.quest.impl.rfd;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/dexter+morgan/>
 */
public enum RFDBossWave {

    AGRITH_NA_NA(4880, 6),

    FLAMBEED(4881, 7),

    KARAMEL(4882, 8),

    DESSOURT(4883, 9),

    GELATINNOTH_MOTHER(4884, 10),

    CULINAROMANCER(6368, 11),

    ;

    private int id;

    private int stage;

    RFDBossWave(int id, int stage) {
        this.id = id;
        this.stage = stage;
    }

    public static final HashMap<Integer, Integer> FOR_ID = new HashMap<>();

    public static final ArrayList<Integer> BOSSES = new ArrayList<>();

    public static boolean isRFDBoss(int id) {
        return BOSSES.contains(id);
    }
    static {
        for(RFDBossWave w : values()) {
            FOR_ID.put(w.stage, w.id);
            BOSSES.add(w.id);
        }
    }
}
