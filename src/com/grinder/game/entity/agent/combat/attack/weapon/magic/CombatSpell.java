package com.grinder.game.entity.agent.combat.attack.weapon.magic;

import java.util.Optional;

import com.grinder.game.content.item.charging.impl.ToxicStaffOfTheDead;
import com.grinder.game.content.item.charging.impl.TridentOfSeas;
import com.grinder.game.content.item.charging.impl.TridentOfSwamp;
import com.grinder.game.content.skill.skillable.impl.magic.Spell;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.special.magic.EldritchNightmareStaffSpecialAttack;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.util.ItemID;
import com.grinder.util.Priority;
import com.grinder.util.oldgrinder.EquipSlot;

/**
 * A {@link Spell} implementation used for combat related spells.
 *
 * @author lare96
 * @author Stan van der Bend
 */
public abstract class CombatSpell extends Spell {

    public static void incrementSpellCharges(Player player) {

        final CombatSpell autoSpell = player.getCombat().getAutocastSpell();
        final CombatSpell castSpell = player.getCombat().getCastSpell();
        Item weapon = player.getEquipment().get(EquipSlot.WEAPON);

        // Handle trident of seas
        if(usedSpell(CombatSpellType.TRIDENT_OF_THE_SEAS, autoSpell, castSpell)) {
            if (TridentOfSeas.INSTANCE.getCharges(weapon) > 0) {
                TridentOfSeas.INSTANCE.decrementCharges(player, weapon);
                return;
            }
            return;
        }  else if(usedSpell(CombatSpellType.TRIDENT_OF_THE_SWAMP, autoSpell, castSpell)) {
            if (TridentOfSwamp.INSTANCE.getCharges(weapon) > 0) {
                TridentOfSwamp.INSTANCE.decrementCharges(player, weapon);
                return;
            }
            return;
        }

       if (weapon.getId() == ItemID.TOXIC_STAFF_OF_THE_DEAD) {
           if (ToxicStaffOfTheDead.INSTANCE.getCharges(player.getEquipment().get(EquipSlot.WEAPON)) > 0) {
               ToxicStaffOfTheDead.INSTANCE.decrementCharges(player, player.getEquipment().get(EquipSlot.WEAPON));
               return;
           }
       }
    }

    private static boolean usedSpell(final CombatSpellType spellType, CombatSpell... spells){
        for(CombatSpell spell: spells){
            if(spell != spellType.getSpell())
                return false;
        }
        return true;
    }

    @Override
    public void startCast(final Agent caster, final Agent recipient) {
        castAnimation(caster)
                .map(animation -> new Animation(animation.getId(), animation.getDelay(), 1, Priority.HIGHEST))
                .ifPresent(caster::performAnimation);
        startGraphic().ifPresent(caster::performGraphic);
        castProjectile(caster, recipient)
                .ifPresent(projectile -> projectile.sendProjectile());
    }

    public int getAttackSpeed() {
        if (this instanceof CombatNormalSpell) {
        	 if (CombatSpellType.isAFasterNormalSpell(this))
                 return 4;
        }
        if (this instanceof CombatAncientSpell) {
            if (CombatSpellType.isAFasterAncientSpell(this))
                return 4;
        }/*
        if (this instanceof EldritchNightmareStaffSpecialAttack) {
            return 4;
        }*/
        return 5;
    }

    /**
     * Fired when the spell hits the victim.
     *
     * @param cast     the entity casting the spell.
     * @param castOn   the entity targeted by the spell.
     * @param accurate if the spell was accurate.
     * @param damage   the amount of damage inflicted by this spell.
     */
    public abstract void finishCast(Agent cast, Agent castOn, boolean accurate, int damage);

    /**
     * The animation played when the spell is cast.
     *
     * @return the animation played when the spell is cast.
     */
    public abstract Optional<Animation> castAnimation(Agent cast);

    /**
     * The starting graphic played when the spell is cast.
     *
     * @return the starting graphic played when the spell is cast.
     */
    public abstract Optional<Graphic> startGraphic();

    /**
     * The ending graphic played when the spell hits the victim.
     * @param cast   the entity casting the spell.
     * @param castOn the entity targeted by the spell.
     * @return the ending graphic played when the spell hits the victim.
     */
    public abstract Optional<Graphic> endGraphic(Agent cast, Agent castOn);

    /**
     * The projectile played when this spell is cast.
     *
     * @param cast   the entity casting the spell.
     * @param castOn the entity targeted by the spell.
     * @return the projectile played when this spell is cast.
     */
    public abstract Optional<Projectile> castProjectile(Agent cast, Agent castOn);

    /**
     * The fixed ID of the spell implementation as recognized by the protocol.
     *
     * @return the ID of the spell, or <tt>-1</tt> if there is no ID for this
     * spell.
     */
    public abstract int spellId();

    @Override
    public Optional<Item[]> itemsRequired(Player player) {
        return Optional.empty();
    }

    @Override
    public Optional<Item[]> equipmentRequired(Player player) {
        return Optional.empty();
    }

}