package com.grinder.game.entity.agent.combat.formula;

import com.grinder.game.content.skill.Skills;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.Combat;
import com.grinder.game.entity.agent.combat.PlayerCombat;
import com.grinder.game.entity.agent.combat.attack.AttackContext;
import com.grinder.game.entity.agent.combat.attack.AttackStrategy;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses;
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil;
import com.grinder.game.model.Skill;
import com.grinder.game.model.item.container.player.Equipment;

/**
 * This class represents a snapshot of a given combat situation, it is used
 * during combat calculations to keep track of an {@link Agent}'s stats and bonuses.
 *
 * @see CombatStats     The effective equipment bonuses of the {@link Agent}.
 * @see CombatBonuses   The miscellaneous bonuses of the {@link Agent}.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-07-17
 */
public final class CombatSnapshot {

    /**
     * Creates a new {@link CombatSnapshot} for the specified {@link Agent} given the combat details.
     *
     * @param agent     the {@link Agent} subject to the {@link CombatSnapshot} specifics.
     * @param details   the {@link AttackContext} detailing the relevant information of the combat situation.
     * @param type      the {@link AttackType} used by the attacker in the combat situation.
     * @return a new {@link CombatSnapshot}.
     */
    public static CombatSnapshot create(final Agent agent, final AttackContext details, final AttackType type){
        final CombatStats stats = CombatStats.create(agent);
        final CombatBonuses bonuses = CombatBonuses.create(agent, type);
        final Skills skills = agent.getSkills().copy();
        return new CombatSnapshot(agent, stats, bonuses, details, skills);
    }

    private final Agent agent;
    private final CombatStats stats;
    private final CombatBonuses bonuses;
    private final AttackContext context;
    private final Skills skills;

    /**
     * Create a new {@link CombatSnapshot}.
     *
     * @param stats     the {@link CombatStats} of the {@link Agent}.
     * @param bonuses   the {@link CombatBonuses} of the {@link Agent}.
     * @param context   the {@link AttackContext} detailing the relevant information about the combat situation.
     * @param skills    the {@link Skills} detailing the effective levels of the {@link Agent}.
     */
    private CombatSnapshot(Agent agent, CombatStats stats, CombatBonuses bonuses, AttackContext context, Skills skills) {
        this.agent = agent;
        this.stats = stats;
        this.bonuses = bonuses;
        this.context = context;
        this.skills = skills;
    }

    public static CombatSnapshot of(Agent agent, Agent target, AttackType attackType) {
        final Combat<?> combat = agent.getCombat();
        final AttackStrategy<?> strategy = combat.determineStrategy();
        return create(
                agent,
                new AttackContext(combat, target, strategy, attackType, false),
                attackType);
    }

    public static CombatSnapshot of(Agent agent, Agent target, AttackStrategy<?> strategy, AttackType attackType) {
        return create(agent, new AttackContext(agent.getCombat(), target, strategy, attackType, false), attackType);
    }

    public Agent getAgent() {
        return agent;
    }

    public CombatStats getStats() {
        return stats;
    }

    public CombatBonuses getBonuses() {
        return bonuses;
    }

    public AttackContext getContext() {
        return context;
    }

    public Skills getSkills() {
        return skills;
    }

    public int getStrengthLevel(){
        return skills.getLevel(Skill.STRENGTH);
    }

    public int getAttackLevel(){
        return skills.getLevel(Skill.ATTACK);
    }

    public int getDefenceLevel(){
        int baseLevel = skills.getLevel(Skill.DEFENCE);
        final Equipment equipment = context.getTargetEquipment();
        if(equipment != null) {
            if (EquipmentUtil.hasAnyAmuletOfTheDamned(equipment)) {
                if (EquipmentUtil.isWearingToragsSet(equipment)) {
                    int hpMissing = skills.getMaximumLevel(Skill.HITPOINTS) - skills.getLevel(Skill.HITPOINTS);
                    double maxDefence = skills.getMaximumLevel(Skill.DEFENCE);
                    baseLevel += (int) ((maxDefence / 100.0) * hpMissing);
                }
            }
        }
        return baseLevel;
    }

    public int getMagicLevel(){
        return skills.getLevel(Skill.MAGIC);
    }

    public int getRangedLevel(){
        return skills.getLevel(Skill.RANGED);
    }

    public int getHitPointsLost(){
        return skills.getMaximumLevel(Skill.HITPOINTS) - skills.getLevel(Skill.HITPOINTS);
    }

    public int getMaxHitPoints(){
        return skills.getMaximumLevel(Skill.HITPOINTS);
    }

    public int getPrayerPointsLost(){
        return skills.getMaximumLevel(Skill.PRAYER) - skills.getLevel(Skill.PRAYER);
    }

    public boolean isMagicalMelee() {
        return context.getTypeUsed() == AttackType.MELEE
                && context.defenceStat().orElse(-1) == EquipmentBonuses.DEFENCE_MAGIC;
    }
}
