package com.grinder.game.entity.agent.combat.attack.strategy.npc.monster

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackStrategy
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.model.Animation
import com.grinder.util.Misc

/**
 * Handles the monkey guard combat
 */
class MonkeyGuardAttack : AttackStrategy<NPC> {

    private enum class Attack {
        SPECIAL_ATTACK, DEFAULT_MELEE_ATTACK
    }

    private var attack = Attack.DEFAULT_MELEE_ATTACK

    override fun createHits(actor: NPC, target: Agent): Array<Hit> {
        return if (attack == Attack.SPECIAL_ATTACK)
            emptyArray()
        else
            arrayOf(Hit(actor, target, this, true, 0))
    }

    override fun sequence(actor: NPC, target: Agent) {
        if (!actor.isNpc || !target.isPlayer) return
        if (Misc.getRandomInclusive(7) == 1 && actor.hitpoints <= 70) {
            attack = Attack.SPECIAL_ATTACK
            actor.hitpoints = actor.hitpoints + 25
            actor.performAnimation(Animation(1405))
            target.asPlayer.packetSender.sendMessage("The monkey guard heals itself with honor!")
        } else {
            attack = Attack.DEFAULT_MELEE_ATTACK
        }
    }

    override fun duration(actor: NPC) = actor.baseAttackSpeed

    override fun requiredDistance(actor: Agent) = 1

    override fun animate(actor: NPC) {
        val animation = actor.attackAnim
        if (animation != -1)
            actor.performAnimation(Animation(animation))
    }

    override fun type() = AttackType.MELEE
}