package com.grinder.game.entity.agent.combat.misc;

import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.skill.SkillManager;
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil;
import com.grinder.game.model.*;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.model.projectile.ProjectileTemplate;
import com.grinder.game.model.projectile.ProjectileTemplateBuilder;
import com.grinder.game.model.sound.Sounds;
import com.grinder.util.collection.CollectionUtil;
import com.grinder.util.Misc;

import java.util.List;

/**
 * Represents a helper class for handling combat prayers.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   02/07/2020
 * @version 1.0
 */
public final class CombatPrayer {

    public static final double ELYSIAN_DAMAGE_REDUCTION_MULTIPLIER = 0.75;
    public static final double DIVINE_DAMAGE_REDUCTION_MULTIPLIER = 0.70;

    /**
     * The amount of damage that will be drained by combat protection prayer.
     */
    public static final double PRAYER_DAMAGE_REDUCTION_RATIO_VS_PLAYER = 0.60D;

    public static final double PRAYER_DAMAGE_REDUCTION_RATIO_VS_BOSS_NPC = 0.0;

    public static final float VERAC_HIT_THROUGH_PRAYER_CHANCE = 25.0f;

    public static final double VENGEANCE_HIT_TO_DAMAGE_RATIO = 0.75;

    static final double REDEMPTION_HEAL_TO_DAMAGE_RATIO = 0.25;

    /**
     * Handles the retribution prayer effect.
     *
     * @param killed the {@link Player} who was killed.
     * @param killer the {@link Agent} who killed the player.
     */
    public static void handleRetribution(final Player killed, final Agent killer) {

        killed.performGraphic(new Graphic(437));
        final ProjectileTemplate template = new ProjectileTemplateBuilder(438)
                .setCurve(0)
                .setStartHeight(0)
                .setEndHeight(0)
                .build();

        for(Direction direction: Direction.values()){
            if(direction != Direction.NONE){
                final Position position = killed.getPosition().clone().move(direction);
                final Projectile projectile = new Projectile(killed.getPosition(), position, template);
                projectile.sendProjectile();
            }
        }
        /*
         * According to OSRS wiki
         *
         * "Using Retribution prayer to kill your opponent
         *  as you die will still give the win to your opponent."
         *
         * Source: (https://oldschoolrunescape.fandom.com/wiki/Duel_Arena)
         */
        if (killed.getDueling().inDuel())
            return;

        final int prayerLevel = killed.getSkillManager().getMaxLevel(Skill.PRAYER);
        final int retributionDamage = (int) (prayerLevel * 0.25);

        final List<Agent> nearbyAgents = CollectionUtil.merge(killed.getLocalNpcs(), killed.getLocalPlayers());

        for (Agent nearbyAgent : nearbyAgents) {
            if(AreaManager.inMulti(nearbyAgent) || nearbyAgent == killer) {
                if (nearbyAgent.getPosition().isWithinDistance(killer.getPosition(), 3)) {
                    final Damage damage = new Damage(retributionDamage, DamageMask.REGULAR_HIT);
                    nearbyAgent.getCombat().queue(damage);
                }
            }
        }
    }

    /**
     * Handles the redemption prayer effect.
     *
     * @param victim the {@link Player} targeted by the redemption prayer effect.
     * @param damage the damage done to the victim.
     */
    public static void handleRedemption(final Player victim, final int damage) {

        final SkillManager skills = victim.getSkillManager();
        final int postDamageHitPoints = victim.getHitpoints() - damage;

        if (postDamageHitPoints <= (skills.getMaxLevel(Skill.HITPOINTS) / 10)) {

            final int amountToHeal = (int) (skills.getMaxLevel(Skill.PRAYER) * REDEMPTION_HEAL_TO_DAMAGE_RATIO);

            skills.setCurrentLevel(Skill.HITPOINTS, victim.getHitpoints() + amountToHeal, true);
            skills.setCurrentLevel(Skill.PRAYER, 0, true);

            victim.performGraphic(Graphics.REDEMPTION_GRAPHIC);
            victim.getPacketSender().sendMessage("You've run out of prayer points!", 1_000);
            victim.getPoints().increase(AttributeManager.Points.RUN_OUT_OF_PRAYER_TIMES, 1); // Increase points
            victim.getPacketSender().sendSound(Sounds.PRAYER_FINISHES);
            PrayerHandler.deactivatePrayers(victim);
        }
    }

    /**
     * Handles the smite prayer effect.
     *
     * @param victim the {@link Player} targeted by the smite effect.
     * @param damage the damage that was done to the victim.
     */
    public static void handleSmite(final Player victim, final int damage) {
        victim.getSkillManager().decreaseLevelTemporarily(Skill.PRAYER, (damage / 4), 0);
        AchievementManager.processFor(AchievementType.KEEP_SMITING, (damage / 4), victim);
    }

    /**
     * In case of the target having enabled the right prayer for the argued {@link AttackType},
     * the damage is appropriately reduced.
     *
     * If the actor is wearing verac's gear (or is the verac's {@link NPC}),
     * then there is a 1/3rd chance that the prayer damage reduction won't be taken into effect.
     *
     * @param actor the {@link Agent} initiating the {@link Damage}.
     * @param target the {@link Agent} targeted to receive the {@link Damage}.
     * @param type the {@link AttackType} used to create the {@link Damage}.
     * @param damage the {@link Damage} queued for the target.
     */
    public static void applyProtectivePrayerDamageReduction(final Agent actor, final Agent target, final AttackType type, final Damage damage) {

        final boolean canHitThroughPrayer = EquipmentUtil.isWearingVeracSet(actor) && Misc.randomChance(VERAC_HIT_THROUGH_PRAYER_CHANCE);
        final int protectivePrayerForAttackID = PrayerHandler.getProtectingPrayer(type);

        if (!canHitThroughPrayer) {

            if (PrayerHandler.isActivated(target, protectivePrayerForAttackID)) {

                final double damageMultiplier = actor.protectionPrayerReductionMultiplier(type);

                damage.multiplyDamage(damageMultiplier);
            }
        }
    }
}
