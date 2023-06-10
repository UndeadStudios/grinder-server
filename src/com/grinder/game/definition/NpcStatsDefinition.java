package com.grinder.game.definition;

import com.grinder.game.entity.agent.combat.formula.CombatStats;

import java.util.Set;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-08-20
 */
public class NpcStatsDefinition {

    private final String name;
    private final Set<Integer> ids;
    private final int combatLevel;
    private final CombatStats combatStats;

    public NpcStatsDefinition(String name, Set<Integer> ids, int combatLevel, CombatStats combatStats) {
        this.name = name;
        this.ids = ids;
        this.combatLevel = combatLevel;
        this.combatStats = combatStats;
    }

    public String getName() {
        return name;
    }

    public Set<Integer> getIds() {
        return ids;
    }

    public int getCombatLevel() {
        return combatLevel;
    }

    public CombatStats getCombatStats() {
        return combatStats;
    }
}
