package com.grinder.game.entity.agent.combat.attack.strategy.npc.monster

import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackStrategy
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.event.impl.DragonFireEvent
import com.grinder.game.entity.agent.combat.event.impl.WyvernIceEvent
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.util.Misc

class SkeletalWyvernAttack : AttackStrategy<NPC> {

    private var currentAttackType = AttackType.MELEE

    override fun animate(actor: NPC) {
        if (currentAttackType == AttackType.MAGIC) {
            actor.performAnimation(MAGE)
            actor.performGraphic(BREATH_START)
            actor.performGraphic(BREATH)
        } else {
            actor.performAnimation(MELEE)
        }
    }

    override fun duration(actor: NPC): Int {
        return if (currentAttackType == AttackType.MAGIC) 6 else 4
    }

    override fun requiredDistance(actor: Agent) = 1

    override fun type() = currentAttackType

    override fun createHits(actor: NPC, target: Agent): Array<Hit> {
        val hit = Hit(actor, target, this, true, 0)
        if (target.isPlayer) {
            val p = target.asPlayer
            if (currentAttackType == AttackType.MAGIC) {
                var extendedHit = 35
                if (EquipmentUtil.isWearingWyvernBreathProtection(p)) extendedHit -= 25
                if (PrayerHandler.isActivated(p, PrayerHandler.PROTECT_FROM_MAGIC)) extendedHit -= 20
                if (extendedHit < 0) {
                    extendedHit = 0
                    p.sendMessage("You're protected from the wyvern ice breath!")
                }
                if (extendedHit > 20) {
                    p.sendMessage("The wyvern's ice breath chills you to the bone!")
                    p.sendMessage("You should equip an elemental, mind or dragonfire shield.")
                    p.say("Ow!")
                }
                hit.damages[0].set(Misc.getRandomInclusive(extendedHit))
            }
        }
        return arrayOf(hit)
    }

    override fun postHitAction(actor: NPC, target: Agent) {

        if (currentAttackType == AttackType.MAGIC)
            target.combat.submit(WyvernIceEvent())

        currentAttackType = if (Misc.randomInclusive(0, 3) == 3) {
            AttackType.MAGIC
        } else {
            AttackType.MELEE
        }
    }

    companion object {
        private val BREATH = Graphic(501, GraphicHeight.MIDDLE)
        private val BREATH_START = Graphic(499, GraphicHeight.MIDDLE)
        private val MELEE = Animation(2985)
        private val MAGE = Animation(2988)
    }
}