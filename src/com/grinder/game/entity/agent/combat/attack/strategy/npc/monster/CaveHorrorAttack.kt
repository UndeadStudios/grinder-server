package com.grinder.game.entity.agent.combat.attack.strategy.npc.monster

import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackStrategy
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.MeleeAttackStrategy
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.sound.Sound
import com.grinder.util.ItemID
import com.grinder.util.oldgrinder.EquipSlot
import kotlin.random.Random

/**
 * BasaliskAttack. Uses melee but has a special attack that can send a RANGED attack towards the player.
 * The special decreases the stats of the player but can be negated by a mirror shield or V's shield.
 */
class CaveHorrorAttack: AttackStrategy<NPC> {

    override fun duration(actor: NPC) = actor.baseAttackSpeed
    override fun requiredDistance(actor: Agent) = 1
    override fun type() = AttackType.MELEE

    override fun createHits(actor: NPC, target: Agent): Array<Hit> {
        val template = HitTemplate
            .builder(AttackType.MELEE)
            .setDelay(0)
            .setIgnoreAttackStats(!isProtected)
            .setIgnoreStrengthStats(!isProtected)

        if (!isProtected) {
            val dmg = kotlin.math.floor(target.maxHitpoints * .1).toInt()
            template.setDamageRange(IntRange(dmg, dmg))
        }
        return arrayOf(Hit(actor, target, this, template.build()))
    }

    // Apparently a different attack anim is used for the gaze
    override fun animate(actor: NPC) {
        if (!isProtected || specialAttack)
            //actor.performAnimation(Animation(actor.asNpc.fetchDefinition().magicAnim))
             super.animate(actor)
        else
            super.animate(actor)
    }

    var isProtected = false
    var specialAttack = false

    override fun canAttack(actor: NPC, target: Agent): Boolean {
        val attack =  super.canAttack(actor, target)
        if (!attack)
            return false
        if (target.isPlayer)
            isProtected = target.asPlayer.equipment[EquipSlot.AMULET].id == ItemID.WITCHWOOD_ICON
                    || target.asPlayer.hasActivePrayer(PrayerHandler.PrayerType.PROTECT_FROM_MELEE.ordinal)
        specialAttack = Random.nextInt(2) == 0
        return true
    }
}