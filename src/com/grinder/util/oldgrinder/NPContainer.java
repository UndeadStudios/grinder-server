package com.grinder.util.oldgrinder;

import com.grinder.game.definition.NpcDefinition;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-03-07
 */
public class NPContainer {

    public static String getNPCName(int npcID) {
        final NpcDefinition definition = NpcDefinition.forId(npcID);
        final String name = definition != null ? definition.getName() : null;
        return name != null ? name : "undefined";
    }

}
