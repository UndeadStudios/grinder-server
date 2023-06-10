package com.grinder.game.entity.agent.combat.attack.strategy.npc.monster

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackStrategy
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.event.CombatState
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.onState
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.sound.Sound
import com.grinder.util.Misc
import com.grinder.util.oldgrinder.Area

class AbyssalDemonAttack(npc: NPC) : AttackStrategy<NPC> {

    companion object {
        private val teleGFX = Graphic(409)
        private val teleSound = Sound(2398)
        private val teleAnim = Animation(2309)
    }

    init {
        npc.combat.onState(CombatState.STARTING_ATTACK) {
            val combatTarget = npc.combat.target
            if (combatTarget is Player && Misc.randomChance(10F))
                teleportPlayer(combatTarget, npc)
        }
    }

    override fun duration(actor: NPC) = actor.baseAttackSpeed
    override fun requiredDistance(actor: Agent) = 1
    override fun type() = AttackType.MELEE

    override fun createHits(actor: NPC, target: Agent): Array<Hit> {
        return arrayOf(Hit(actor, target, this, true, 0))
    }

    override fun animate(actor: NPC) {
        val animation = actor.attackAnim
        if (animation != -1)
            actor.performAnimation(Animation(animation))
    }

    private fun teleportPlayer(target: Agent, actor: NPC) {
        val toPlayer = Misc.randomBoolean()
        val checkPos = if (toPlayer) target.position else actor.position
        Area(1).getAbsolute(checkPos).findRandomOpenPosition(target.plane, 1, checkPos).ifPresent {
            actor.combat.setCancelNextAttack(true)
            if (toPlayer) {
                actor.moveTo(it)
                actor.performAnimation(teleAnim)
                actor.performGraphic(teleGFX)
            } else {
                target.moveTo(it)
                target.combat.reset(false)
                actor.setEntityInteraction(target)
            }
            if (target.isPlayer)
                target.asPlayer.playSound(teleSound)
        }
    }
}