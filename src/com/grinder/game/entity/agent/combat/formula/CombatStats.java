package com.grinder.game.entity.agent.combat.formula;

import com.grinder.game.definition.NpcDefinition;
import com.grinder.game.definition.NpcStatsDefinition;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.AttackContext;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.Ammunition;
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.RangedWeapon;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses;
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil;
import com.grinder.util.oldgrinder.EquipSlot;

import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * This class represents the direct combat stats of an {@link Agent}.
 *
 * In the case of {@link Player} agents, the stats are derived from {@link Player#getBonusManager()}.
 * In the case of {@link NPC} agents, the stats are derived from {@link NPC#getStatsDefinition()}
 * or from {@link NPC#fetchDefinition()} in case no stats definition is defined for the npc.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-07-17
 */
public final class CombatStats {

    /**
     * Creates a new {@link CombatStats} instance for the argued {@link Agent}.
     *
     * @see EquipmentBonuses            for {@link Player} agents.
     * @see NpcDefinition#getStats()    for {@link NPC} agents.
     *
     * @param agent the {@link Agent} for whom the {@link CombatStats} are derived.
     * @return a new {@link CombatStats} instance.
     */
    public static CombatStats create(final Agent agent){
        if(agent instanceof Player) {
            final EquipmentBonuses bonuses = agent.getAsPlayer().getBonusManager();
            return new CombatStats(
                    bonuses.getAttackBonus(),
                    new double[5],
                    bonuses.getDefenceBonus(),
                    bonuses.getOtherBonus());
        } else {
            final NPC npc = agent.getAsNpc();
            final NpcStatsDefinition statsDefinition = npc.getStatsDefinition();

            if(statsDefinition != null)
                return statsDefinition.getCombatStats();

            final NpcDefinition definition = agent.getAsNpc().fetchDefinition();
            final double[] stats = Arrays.stream(definition.getStats()).asDoubleStream().toArray();
            return new CombatStats(
                    Arrays.copyOfRange(stats, 0, 5),
                    new double[5],
                    Arrays.copyOfRange(stats, 10, 15),
                    Arrays.copyOfRange(stats, 15, 18)
            );
        }
    }

    private final double[] attackStats;
    private final double[] aggressiveStats;
    private final double[] defenceStats;
    private final double[] otherStats;

    /**
     * Gets attack stats.
     * @return The attack stats associated with combat.
     */
    public double[] getAttackStats() {
        return attackStats;
    }

    /**
     * Gets defensive stats.
     * @return The defence stats associated with combat.
     */
    public double[] getDefenceStats() {
        return defenceStats;
    }

    /**
     * Create a new {@link CombatStats} instance.
     *
     * @param attackStats   the stats used for accuracy calculations.
     * @param defenceStats  the stats used for defence calculations.
     * @param otherStats    the stats used for miscellaneous combat calculations.
     */
    private CombatStats(double[] attackStats, double[] aggressiveStats, double[] defenceStats, double[] otherStats) {
        this.attackStats = attackStats;
        this.aggressiveStats = aggressiveStats;
        this.defenceStats = defenceStats;
        this.otherStats = otherStats;
    }

    /**
     * Gives the attack bonus relevant to the details of the argued {@link AttackContext}.
     *
     * @param context the {@link AttackContext} detailing what stat should be used.
     * @return a double value representing the attack bonus.
     */
    public double getAttackBonus(AttackContext context){
        final OptionalInt optionalAttackStat = context.attackStat();
        if (optionalAttackStat.isPresent())
            return attackStats[optionalAttackStat.getAsInt()];
        if(context.used(AttackType.MAGIC))
            return attackStats[3];
        else if(context.used(AttackType.RANGED))
            return attackStats[4];
        else
            return Optional.ofNullable(context.getFightType())
                    .map(type -> attackStats[type.getBonusType()])
                    .orElse(Math.max(attackStats[0], Math.max(attackStats[1], attackStats[2])));
    }

    /**
     * Gives the defence bonus relevant to the details of the argued {@link AttackContext}.
     *
     * @param context the {@link AttackContext} detailing what stat should be used.
     * @return a double value representing the defence bonus.
     */
    public double getDefenceBonus(AttackContext context){
        final OptionalInt optionalDefenceStat = context.defenceStat();
        if (optionalDefenceStat.isPresent())
            return defenceStats[optionalDefenceStat.getAsInt()];
        if(context.used(AttackType.MAGIC))
            return defenceStats[3];
        else if(context.used(AttackType.RANGED))
            return defenceStats[4];
        else
            return Optional.ofNullable(context.getFightType())
                    .map(type -> defenceStats[type.getBonusType()])
                    .orElse(Math.max(defenceStats[0], Math.max(defenceStats[1], defenceStats[2])));
    }

    /**
     * Gives the strength bonus relevant to the details of the argued {@link AttackContext}.
     *
     * @param context the {@link AttackContext} detailing what stat should be used.
     * @return a double value representing the strength bonus.
     */
    public double getStrengthBonus(AttackContext context){
        if(context.used(AttackType.MAGIC))
            return otherStats[2];
        else if(context.used(AttackType.RANGED))
            return otherStats[1];
        else
            return otherStats[0];
    }

}
