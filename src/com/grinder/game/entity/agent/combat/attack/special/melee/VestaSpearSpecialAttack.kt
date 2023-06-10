package com.grinder.game.entity.agent.combat.attack.special.melee

import com.grinder.game.World
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.Combat
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.special.MeleeSpecialAttack
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackProvider
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.markTime
import com.grinder.game.entity.passedTime
import com.grinder.game.entity.removeAttribute
import com.grinder.game.model.*
import com.grinder.game.model.Direction.NONE
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.sound.Sound
import com.grinder.util.Priority
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashSet


/**
 * https://oldschool.runescape.wiki/w/Vesta%27s_spear
 *
 * "Vesta's spear has a special attack, Spear Wall,
 * that consumes 50% of the player's special attack energy
 * and damages up to 16 targets within 8 tiles surrounding
 * the player (one if the player is outside a multicombat area).
 * In addition, the user becomes immune to melee attacks for 8 ticks (4.8 seconds)."
 *
 * @see Attribute.SPEAR_WALL_EFFECT for the melee immunity effect
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   19/05/2020
 * @version 1.0
 */
class VestaSpearSpecialAttack
    : MeleeSpecialAttack(Provider()) {

    override fun special() = SpecialAttackType.SPEAR_WALL

    override fun sequence(actor: Agent, target: Agent) {

        val agents = HashSet<Agent>()

        directions@for(direction in Direction.values()){
            if(direction == NONE)
                continue
            val pos = actor.position.clone().move(direction)
            val region = World.regions.fromPosition(pos)
            val entities = region.getEntities(pos)
            for(entity in entities){
                if(agents.size == 15)
                    break@directions
                if(entity != actor && entity != target) {
                    if(entity is Agent)
                        agents.add(entity)
                }
            }
        }

        agents.add(target)

        agents.forEach {

            val primaryTarget = it == target

            if(primaryTarget || (AreaManager.inMulti(it) && AreaManager.canAttack(actor, it))) {

                val hit1 = Hit(actor, it, this, HitTemplate
                        .builder(AttackType.MELEE)
                        .setDelay(0)
                        .build(), true)

                actor.combat.queueOutgoingHit(hit1)
            }
        }
    }

    override fun onHit(actor: Agent, target: Agent) {
        // make actor immune for melee attack sfor 8 ticks
        actor.markTime(Attribute.SPEAR_WALL_EFFECT)
    }

    class Provider : SpecialAttackProvider {

        override fun getAttackAnimation(type: AttackType?) =
                Animation(8184, Priority.HIGH)

        override fun fetchAttackGraphic(type: AttackType?) = Optional.of(
                Graphic(1627, GraphicHeight.MIDDLE, Priority.HIGH))

        override fun fetchAttackSound(type: AttackType?) = Optional.of(
                Sound(2763))

        override fun fetchAttackDuration(type: AttackType?)
                = 5
    }

    companion object {

        private const val DISABLED_MELEE_MESSAGE = "A mystical force prevents you from attacking this target"

        fun canBeAttackedBy(agent: Agent, attackerCombat: Combat<*>): Boolean{

            if(agent.passedTime(Attribute.SPEAR_WALL_EFFECT, 5, TimeUnit.SECONDS, updateIfPassed = false, message = false)){
                agent.removeAttribute(Attribute.SPEAR_WALL_EFFECT)
                return true
            }

            if(attackerCombat.isMeleeAttack) {
                attackerCombat.actor.messageIfPlayer(DISABLED_MELEE_MESSAGE, 1000)
                return false
            }

            return true
        }
    }
}