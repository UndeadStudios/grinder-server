package com.grinder.game.entity.agent.combat.attack.strategy

import com.grinder.game.content.dueling.DuelRule
import com.grinder.game.content.item.charging.impl.ToxicStaffOfTheDead
import com.grinder.game.content.item.charging.impl.TridentOfSwamp
import com.grinder.game.content.item.charging.impl.TridentOfSwampEnchanced
import com.grinder.game.content.item.jewerly.DoubleXPRing
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackStrategy
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatEffectSpell
import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpell
import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpellType
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonEffect
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonType
import com.grinder.game.entity.agent.combat.event.impl.SpellCastEvent
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.decreaseLevel
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil
import com.grinder.game.entity.agent.player.statement
import com.grinder.game.entity.getBoolean
import com.grinder.game.entity.setBoolean
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.Graphics
import com.grinder.game.model.Skill
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.sound.Sounds
import com.grinder.util.DistanceUtil
import com.grinder.util.ItemID
import com.grinder.util.Misc
import com.grinder.util.Priority
import com.grinder.util.oldgrinder.EquipSlot
import java.util.*

/**
 * Represents an [AttackStrategy] for magic combat.
 *
 * @author Professor Oak
 * @author Stan van der Bend
 */
open class MagicAttackStrategy : AttackStrategy<Agent> {

    override fun type() = AttackType.MAGIC

    override fun createHits(actor: Agent, target: Agent): Array<Hit> {

        val spell = actor.combat.spell

        val template = HitTemplate
            .builder(type())
            .setDelay(getMagicSpellHitDelay(actor, target))
            .build()

        val hit = Hit(actor, target, this, template)
        if (spell is CombatEffectSpell && !spell.isDealingDamage)
            hit.setNegateDamages(true)

        return arrayOf(hit)
    }

    override fun canAttack(actor: Agent, target: Agent): Boolean {
        //actor.positionToFace = target.position
        if (actor is Player) {
            if (actor.dueling.inDuel() && actor.dueling.rules[DuelRule.NO_MAGIC.ordinal]) {
                actor.combat.reset(false)
                actor.statement("Magic has been disabled in this duel!")
                return false
            }
            val combat = actor.combat
            combat.castSpell = combat.castSpell ?: combat.autocastSpell
            return if (combat.castSpell == null)
                false
            else
                combat.castSpell.canCast(actor, target, true)
        }
        return true
    }

    override fun sequence(actor: Agent, target: Agent) {
        val spell = actor.combat.spell
        if(spell != null){
            spell.startCast(actor, target)
            actor.combat.opponent = target;
            actor.positionToFace = target.position
            actor.combat.submit(SpellCastEvent(spell))
        }
        if (actor is NPC) {
            val def = actor.fetchDefinition()
            if (def.magicProjectile > 0) {
                val builder = ProjectileTemplate
                        .builder(def.magicProjectile) // Loaded from npc_defs.json
                        .setDelay(def.mageProjectileDelay)
                        .setSourceOffset(1)
                        .setSpeed(if (target != null) (10 + (DistanceUtil.getChebyshevDistance(actor.position, target.position) * 3).coerceAtMost(50)) else 10)
                        .setStartHeight(43)
                        .setEndHeight(31)
                        .setCurve(280)
                Projectile(actor, target, builder.build()).sendProjectile()
            }
        }
    }

    override fun duration(actor: Agent) =
        if (actor != null && actor is Player) {
            if (actor.combat.previousCast != null && actor.combat.weapon.id == ItemID.HARMONISED_NIGHTMARE_STAFF)
                4
            else if (actor.combat.previousCast != null && actor.combat.weapon.id == ItemID.ELDRITCH_NIGHTMARE_STAFF)
                5
            else if (actor.combat.previousCast != null && actor.combat.weapon.id == ItemID.NIGHTMARE_STAFF)
                5
            else if (actor.combat.previousCast != null && actor.combat.weapon.id == ItemID.VOLATILE_NIGHTMARE_STAFF)
                5 else if (actor.combat.previousCast != null)
                actor.combat.previousCast.attackSpeed
            else
                actor.baseAttackSpeed
        } else {
            if (actor.combat.previousCast != null)
                actor.combat.previousCast.attackSpeed
            else
                actor.baseAttackSpeed
        }

    override fun requiredDistance(actor: Agent) = 10

    override fun animate(actor: Agent) {
        if (actor is NPC) {
            val def = actor.fetchDefinition()
            if (def.magicAnim > 0)
                actor.performAnimation(Animation(def.magicAnim, Priority.HIGH))
            if (def.mageStartGfx != null)
                actor.performGraphic(def.mageStartGfx)
        }
    }

    override fun postHitAction(actor: Agent, target: Agent) {
        val combat = actor.combat
        val current = combat.castSpell
        actor.ifPlayer { CombatSpell.incrementSpellCharges(it) }
        combat.castSpell = null
        if (combat.autocastSpell == null) {
            actor.motion.cancelTask()
        }
        combat.previousCast = current
        if (combat.autocastSpell !== current)
            combat.reset(true)
    }

    private fun fightingNpcWithSerpentineHelm(target: Agent?, attacker: Agent?) =
        target is NPC && attacker is Player && attacker.equipment.containsAny(EquipmentUtil.SERPENTINE_HELM_ITEM_ID, EquipmentUtil.MAGMA_HELM_ITEM_ID, EquipmentUtil.TANZANITE_HELM_ITEM_ID)

    override fun postHitEffect(hit: Hit) {
        val attacker = hit.attacker
        val target = hit.target
        val accurate = hit.isAccurate
        val damage = hit.totalDamage
        attacker.ifPlayer {
            if (it.getBoolean(Attribute.FIRE_TOME_ACTIVATED))
                it.setBoolean(Attribute.FIRE_TOME_ACTIVATED, value = false, defaultValue = false)
            if (it.getBoolean(Attribute.WATER_TOME_ACTIVATED))
                it.setBoolean(Attribute.WATER_TOME_ACTIVATED, value = false, defaultValue = false)
        }
        if (!attacker.isAlive || !target.isAlive)
            return
        Optional.ofNullable(attacker.combat.previousCast)
            .ifPresent { previousSpell: CombatSpell ->
                if (accurate) {


                    // Trient of swamp passive effect: In addition, the trident possesses a passive effect in which successful hits with either the built-in attack or a manually-cast combat spell have a 25% chance of inflicting venom on the opponent.
                    // If the player is wearing a serpentine helm, the chance is increased to 100% if the target is a monster.
                    if (attacker.combat.castSpell == CombatSpellType.TRIDENT_OF_THE_SWAMP.spell || attacker.combat.autocastSpell == CombatSpellType.TRIDENT_OF_THE_SWAMP.spell) {
                        if (TridentOfSwamp.getCharges(attacker!!.asPlayer!!.equipment.get(EquipSlot.WEAPON)) > 0 || TridentOfSwampEnchanced.getCharges(attacker!!.asPlayer!!.equipment.get(EquipSlot.WEAPON)) > 0) { // If using a manually-cast spell, the staff must contain at least one charge for it to be able to apply venom, although this will not use up any charges.
                        if (fightingNpcWithSerpentineHelm(target, attacker)) {
                            if (!target.isVenomed)
                            PoisonEffect.applyPoisonTo(target, PoisonType.VENOM)
                        } else if (Misc.randomChance(25F)) {
                            if (!target.isVenomed)
                            PoisonEffect.applyPoisonTo(target, PoisonType.VENOM)
                        }
                    }
                    }

                    // Staff of the dead passive effect: players using combat spells when wielding the staff have a 25% chance of inflicting venom on their opponent. However, if the opponent is an NPC and the serpentine helm is worn in conjunction with the staff, there is a 100% chance of envenoming them.
                    // The player cannot envenom opponents using the staff's melee attack. However, scales will still be consumed.
                    if (attacker.isPlayer) {
                        if (attacker.asPlayer.equipment.items[EquipmentConstants.WEAPON_SLOT].id == ItemID.TOXIC_STAFF_OF_THE_DEAD) {
                                if (ToxicStaffOfTheDead.getCharges(attacker!!.asPlayer!!.equipment.get(EquipSlot.WEAPON)) > 0) { // If using a manually-cast spell, the staff must contain at least one charge for it to be able to apply venom, although this will not use up any charges.
                                    if (fightingNpcWithSerpentineHelm(target, attacker)) {
                                        if (!target.isVenomed)
                                        PoisonEffect.applyPoisonTo(target, PoisonType.VENOM)
                                    } else if (Misc.randomChance(25F)) {
                                        if (!target.isVenomed)
                                        PoisonEffect.applyPoisonTo(target, PoisonType.VENOM)
                                }
                            }
                        }

                        if (attacker!!.isPlayer && attacker!!.asPlayer.equipment.get(EquipSlot.RING).id == ItemID.RING_OF_CHAROS) {
                            DoubleXPRing.use(attacker!!.asPlayer)
                        }
                    }

                    if (target is Player) {
                        /*
                         * Blighted Aura:
                         *
                         * Magic attacks have a 25% chance of lowering the enemy's Strength by five levels repeatedly.
                         * The set effect does not activate if a spell splashes.
                         */
                        if (EquipmentUtil.isWearingAhrimSet(attacker)) {
                            if (Misc.randomChance(25F)) {
                                target.decreaseLevel(Skill.STRENGTH, 5)
                            }
                        }
                    }
                    // Send proper end graphics for the spell because it was accurate
                    previousSpell.endGraphic(attacker, target)
                        .ifPresent { graphic: Graphic? -> target.performGraphic(graphic) }
                } else {

                    // Send splash graphics for the spell because it wasn't accurate
                    target.performGraphic(Graphics.SPLASH_GRAPHIC)
                    if (attacker!!.isPlayer && attacker!!.asPlayer.equipment!!.get(EquipSlot.RING).id == ItemID.RING_OF_CHAROS) {
                        DoubleXPRing.use(attacker.asPlayer)
                    }
                    target.ifPlayer { player: Player -> player.packetSender.sendAreaPlayerSound(Sounds.MAGIC_SPLASH) }
                    attacker.ifPlayer { player: Player -> player.packetSender.sendAreaPlayerSound(Sounds.MAGIC_SPLASH) }
                }
                previousSpell.finishCast(attacker, target, accurate, damage)
            }
    }

    companion object {
        /**
         * The default magic combat attackStrategy
         */
        @JvmField
        val INSTANCE = MagicAttackStrategy()

        /*
        * Magic spells have the following hit delay based on distance (squares)
        * TODO: Add demonbane spells and grasp spells hit delay always 2 (when we have arceeus spellbook)
        * Note: The delays below are custom adjusted to be perfect better than on OSRS
         */
        fun getMagicSpellHitDelay(attacker: Agent, target: Agent): Int {
            if (attacker.isPlayer) { // For some reason the npc attacks are always delayed by 1
                return when (DistanceUtil.getChebyshevDistance(attacker.position, target.position)) {
                    in 0..3 -> 2
                    in 4..7 -> 3
                    in 8..10 -> 4
                    in 11..13 -> 5
                    else -> 6
                }
            }
            return when (DistanceUtil.getChebyshevDistance(attacker.position, target.position)) {
                /* 0 -> 2
                 1 -> 2
                 2 -> 2
                 3 -> 2
                 4 -> 3
                 5 -> 3
                 6 -> 3
                 7 -> 3
                 8 -> 4
                 9 -> 4
                 10 -> 4
                 else -> 5*/
                in 0..1 -> 1
                in 2..4 -> 2
                in 5..7 -> 3
                in 8..10 -> 4
                in 11..13 -> 5
                else -> 6
            }
        }
    }
}