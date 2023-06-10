package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.MeleeSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.event.impl.StunEvent
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.movement.pathfinding.traverse.SmallTraversal
import com.grinder.game.entity.agent.movement.pathfinding.traverse.TraversalType
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.passedTime
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.game.task.TaskManager
import com.grinder.util.Priority
import com.grinder.util.timing.TimerKey
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * https://oldschool.runescape.wiki/w/Dragon_spear
 * https://oldschool.runescape.wiki/w/Zamorakian_spear
 * https://oldschool.runescape.wiki/w/Zamorakian_hasta
 *
 * Pushes an opponent back and stuns them for three seconds,
 * consuming 25% of the player's special attack energy.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   12/12/2020
 * @version 1.0
 */
class ShoveSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    private val traversalStrategy = SmallTraversal(TraversalType.Land, false)

    override fun special() = SpecialAttackType.SHOVE

    override fun createHits(actor: Agent, target: Agent): Array<Hit> {
        return emptyArray()
    }

    override fun onHit(actor: Agent, target: Agent) {
        val dummy = (target.isNpc && target.asNpc.fetchDefinition().name.contains("dummy"))

        if (target.size > 1 || dummy){
            cancelDrain = true
            actor.combat.reset(false)
            actor.ifPlayer {
                it.message(if (dummy) "You can't knock back the combat dummy!" else "That creature is too large to knock back!")
                SpecialAttackType.activate(it)
            }
            return
        }

        if (target is Player){
            if (!target.passedTime(Attribute.LAST_SHOVE_STUN, 4, TimeUnit.SECONDS,
                            message = false)){
                actor.messageIfPlayer("Your target is currently immune to your stun attack!")
                return
            }
            //target.markTime(Attribute.LAST_SHOVE_STUN)
        }

        if (target.timerRepository.has(TimerKey.STUN)){
            actor.messageIfPlayer("Your target is already stunned!")
            return
        }

        val position = target.position.clone()
        val negateDirection = position.getDirection(actor.position)

        target.combat.submit(StunEvent(3, true, resetCombat = false, resetMotion = true))
        actor.combat.reset(false)

        TaskManager.submit(5) {
            target.combat.target(actor)
        }

        if (!traversalStrategy.blocked(position, negateDirection)) {
            val targetPos = position.move(negateDirection)
            target.motion.pauseTasks(1)
            target.motion.enqueuePathToWithoutCollisionChecks(targetPos.x, targetPos.y)
        }
    }

    class Provider : SpecialAttackProvider {

        override fun getAttackAnimation(type: AttackType?) =
                Animation(1064, Priority.HIGH)

        override fun fetchAttackGraphic(type: AttackType?) = Optional.of(
                Graphic(253, 0, 80, Priority.HIGH))

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(Sounds.DRAGON_SPEAR_SPECIAL_SOUND))

        override fun fetchAttackDuration(type: AttackType?)
                = 4
    }

}