package com.grinder.game.entity.agent.combat.attack.weapon.ranged;

import com.grinder.game.content.skill.SkillManager;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonEffect;
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonType;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.monster.MonsterRace;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.GraphicHeight;
import com.grinder.game.model.Skill;
import com.grinder.util.ItemID;
import com.grinder.util.Logging;
import com.grinder.util.time.TimerUtil;

import java.util.function.Consumer;

import static com.grinder.game.model.sound.Sounds.*;

/**
 * A table of constants that hold data for all ranged ammo.
 *
 * @author Swiffy96
 * @author Professor Oak
 * @author Stan van der Bend
 * @author R-Y-M-R
 */
public class AmmunitionEffect {

    private static final int RUBY_BOLT_SPECIAL_EFFECT_DAMAGE_CAP = 100;

    private static final Graphic ENCHANTED_OPAL_BOLT_EFFECT_GRAPHIC = new Graphic(749);
    private static final Graphic ENCHANTED_PEARL_BOLT_EFFECT_GRAPHIC = new Graphic(750);
    private static final Graphic ENCHANTED_SAPPHIRE_BOLT_EFFECT_GRAPHIC = new Graphic(751);
    private static final Graphic ENCHANTED_EMERALD_BOLT_EFFECT_GRAPHIC = new Graphic(752);
    private static final Graphic ENCHANTED_ONYX_BOLT_EFFECT_GRAPHIC = new Graphic(753);
    private static final Graphic ENCHANTED_RUBY_BOLT_EFFECT_GRAPHIC = new Graphic(754);
    private static final Graphic ENCHANTED_JADE_BOLT_EFFECT_GRAPHIC = new Graphic(755);
    private static final Graphic ENCHANTED_DRAGON_BOLT_EFFECT_GRAPHIC = new Graphic(756);
    private static final Graphic ENCHANTED_TOPAZ_BOLT_EFFECT_GRAPHIC = new Graphic(757);
    private static final Graphic ENCHANTED_DIAMOND_BOLT_EFFECT_GRAPHIC = new Graphic(758, GraphicHeight.MIDDLE);

    public static int transformDamage(Ammunition ammunition, Player actor, Agent target, int damage) {
        final boolean ZARYTE = hasZaryte(actor);

        double multiplier = 1.0;

        switch (ammunition) {

            case ENCHANTED_OPAL_BOLT:
            case ENCHANTED_OPAL_DRAGON_BOLT:
                damage += 0.1 * actor.getSkills().getLevel(Skill.RANGED);
                if (ZARYTE)
                    damage *= 1.1;

                target.performGraphic(ENCHANTED_OPAL_BOLT_EFFECT_GRAPHIC);
                target.ifPlayer(sendSound(actor, OPAL_BOLTS));
                break;

            case ENCHANTED_PEARL_BOLT:
            case ENCHANTED_PEARL_DRAGON_BOLT:
                damage += Math.round(0.05 * actor.getSkills().getLevel(Skill.RANGED));
                if (MonsterRace.Companion.isRace(target, MonsterRace.FIERY)) // The fiery attribute is assigned to monsters that are weak to the special effect of pearl bolts (e).
                    damage *= 1.33; // The special effect of pearl bolts (e) will have 33.3% increased damage.
                if (ZARYTE)
                    damage *= 1.1;

                target.performGraphic(ENCHANTED_PEARL_BOLT_EFFECT_GRAPHIC);
                target.ifPlayer(sendSound(actor, PEARL_BOLTS));
                break;

            case ENCHANTED_SAPPHIRE_BOLT:
            case ENCHANTED_SAPPHIRE_DRAGON_BOLT:
                target.performGraphic(ENCHANTED_SAPPHIRE_BOLT_EFFECT_GRAPHIC);
                target.ifPlayer(sendSound(actor, SAPPHIRE_BOLTS).andThen(player -> {
                    final SkillManager skills = player.getSkillManager();
                    int drainAmount = 20;
                    if (ZARYTE)
                        drainAmount *= 1.1;
                    skills.decreaseLevelTemporarily(Skill.PRAYER, drainAmount, 0);
                    skills.setCurrentLevel(Skill.PRAYER, skills.getCurrentLevel(Skill.PRAYER) - drainAmount, true);
                    player.sendMessage("Your Prayer level has been leeched.");
                }));
                break;

            case ENCHANTED_EMERALD_BOLT:
            case ENCHANTED_EMERALD_DRAGON_BOLT:
                if (ZARYTE) {
                    PoisonEffect.applyPoisonTo(target, PoisonType.EXTRA);
                } else {
                    PoisonEffect.applyPoisonTo(target, PoisonType.MILD);
                }

                target.performGraphic(ENCHANTED_EMERALD_BOLT_EFFECT_GRAPHIC);
                target.ifPlayer(sendSound(actor, EMERALD_BOLTS));
                break;

            case ENCHANTED_ONYX_BOLT:
            case ENCHANTED_ONYX_DRAGON_BOLT:
                final SkillManager skills = actor.getSkillManager();

                multiplier = 1.20;
                int heal = (int) (damage * 0.25) + 10;
                if (ZARYTE) {
                    multiplier *= 1.1;
                    heal *= 1.1;
                }

                if (heal + skills.getCurrentLevel(Skill.HITPOINTS) > skills.getMaxLevel(Skill.HITPOINTS))
                    heal = skills.getMaxLevel(Skill.HITPOINTS) - skills.getCurrentLevel(Skill.HITPOINTS);

                actor.setHitpoints(actor.getHitpoints() + heal);
                skills.updateSkill(Skill.HITPOINTS);

                target.performGraphic(ENCHANTED_ONYX_BOLT_EFFECT_GRAPHIC);
                target.ifPlayer(sendSound(actor, ONYX_BOLTS));
                break;

            case ENCHANTED_JADE_BOLT:
            case ENCHANTED_JADE_DRAGON_BOLT:
                multiplier = 1.05;
                if (ZARYTE)
                    multiplier *= 1.1;

                target.performGraphic(ENCHANTED_JADE_BOLT_EFFECT_GRAPHIC);
                target.ifPlayer(sendSound(actor, JADE_BOLTS));
                break;
            case ENCHANTED_RUBY_BOLT:
            case ENCHANTED_RUBY_DRAGON_BOLT:
                if (actor.getSkillManager().getCurrentLevel(Skill.HITPOINTS) > 10) {

                    final float drainRate = target instanceof NPC ? 0.25F : 0.20F;

                    final int maxHit = ZARYTE ?
                            (int) Math.min(Math.floor(target.getHitpoints() * (drainRate * 1.1)), RUBY_BOLT_SPECIAL_EFFECT_DAMAGE_CAP * 1.1) // The zaryte MaxHit calculation
                            : (int) Math.min(Math.floor(target.getHitpoints() * drainRate), RUBY_BOLT_SPECIAL_EFFECT_DAMAGE_CAP); // The normal MaxHit calculation

                    final int drainedLifePoints = (int) Math.ceil(actor.getSkillManager().getCurrentLevel(Skill.HITPOINTS) * 0.10F);

                    damage = maxHit;

                    if (drainedLifePoints > 0)
                        actor.getCombat().queue(Damage.create(drainedLifePoints));

                    target.performGraphic(ENCHANTED_RUBY_BOLT_EFFECT_GRAPHIC);
                    target.ifPlayer(sendSound(actor, RUBY_BOLTS));
                }
                break;

            case ENCHANTED_DRAGON_BOLT:
            case ENCHANTED_DRAGON_DRAGON_BOLT:
                final boolean fireProtected = target.getAsOptionalPlayer()
                    .filter(player -> TimerUtil.hasActiveFireProtection(player) || TimerUtil.hasActiveSuperFireProtection(player) || EquipmentUtil.isWearingDragonFireProtection(player))
                    .isPresent();


                if (!fireProtected && !target.isNpc()) {
                    damage += 0.2 * actor.getSkills().getLevel(Skill.RANGED);
                    if (ZARYTE)
                        damage *= 1.1;
                    target.performGraphic(ENCHANTED_DRAGON_BOLT_EFFECT_GRAPHIC);
                    target.ifPlayer(sendSound(actor, DRAGON_BOLTS));
                }
                if (target.isNpc()) {
                    damage += 25;
                    if (ZARYTE)
                        damage *= 1.1;
                    target.performGraphic(ENCHANTED_DRAGON_BOLT_EFFECT_GRAPHIC);
                    target.ifPlayer(sendSound(actor, DRAGON_BOLTS));
                }
                break;
            case ENCHANTED_TOPAZ_BOLT:
            case ENCHANTED_TOPAZ_DRAGON_BOLT:
                target.performGraphic(ENCHANTED_TOPAZ_BOLT_EFFECT_GRAPHIC);
                target.ifPlayer(sendSound(actor, TOPAZ_BOLTS).andThen(player -> {
                    int amount = 3;
                    if (ZARYTE)
                        amount = 4;
                    player.getSkillManager().decreaseLevelTemporarily(Skill.MAGIC, amount, 0);
                    player.sendMessage("Your Magic level has been reduced.");
                }));
                break;

            case ENCHANTED_DIAMOND_BOLT:
            case ENCHANTED_DIAMOND_DRAGON_BOLT:
                multiplier = 1.15;
                if (ZARYTE)
                    multiplier *= 1.1;
                target.performGraphic(ENCHANTED_DIAMOND_BOLT_EFFECT_GRAPHIC);
                target.ifPlayer(sendSound(actor, DIAMOND_BOLTS));
                break;
        }

        damage *= multiplier;
        return Math.min(100, damage);
    }

    private static Consumer<Player> sendSound(final Player actor, int sound){
        actor.getPacketSender().sendSound(sound);
        return target -> target.getPacketSender().sendSound(sound);
    }

    /**
     * Zaryte applies 10% increased damage and 10% increased damage cap.
     * @param player
     * @return true if player has a Zaryte Crossbow equipped.
     */
    public static boolean hasZaryte(Player player) {
        return player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId() == ItemID.ZARYTE_CROSSBOW;
    }

}
