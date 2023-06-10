package com.grinder.game.content.quest.impl.rfd;

import java.util.HashMap;
/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/dexter+morgan/>
 */
public enum RFDUnlockableGlove {

    BRONZE(7454, 1),

    IRON(7455, 2),

    STEEL(7456, 3),

    BLACK(7457, 4),

    MITHRIL(7458, 5),

    ADAMANT(7459, 6),

    RUNE(7460, 7),

    DRAGON(7461, 8),

    ;

    private int id;

    private int completionsRequired;

    RFDUnlockableGlove(int id, int completionsRequired) {
        this.id = id;
        this.completionsRequired = completionsRequired;
    }

    public static final HashMap<Integer, Integer> FOR_ID = new HashMap<>();

    static {
        for (RFDUnlockableGlove g : values()) {
            FOR_ID.put(g.id, g.completionsRequired);
        }
    }
}
