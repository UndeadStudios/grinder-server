package com.grinder.game.entity.agent.combat.attack.strategy

import com.grinder.game.content.dueling.DuelRule
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackStrategy
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterface.HALBERD
import com.grinder.game.entity.agent.combat.attack.weapon.melee.HolyScytheOfViturEffect
import com.grinder.game.entity.agent.combat.attack.weapon.melee.LadykillerScytheEffect
import com.grinder.game.entity.agent.combat.attack.weapon.melee.SanguineScytheOfViturEffect
import com.grinder.game.entity.agent.combat.attack.weapon.melee.ScytheOfViturEffect
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.agent.player.statement
import com.grinder.game.model.Animation
import com.grinder.util.Priority

/**
 * Represents an [AttackStrategy] for [melee][AttackType.MELEE] combat.
 *
 * @author Professor Oak
 * @author Stan van der Bend (converted to Kotlin, fix-ups)
 */
open class SpecialAttackStrategy : AttackStrategy<Agent> {

    override fun type() = AttackType.SPECIAL

    /**
     * Create outgoing [hit(s)][Hit] to be queued for the [target].
     *
     * The tick delay for application of the hits is 0 if the [actor]
     * is a [Player] and the [target] a [NPC], otherwise the delay is 1.
     */
    override fun createHits(actor: Agent, target: Agent): Array<Hit> {
        val template = HitTemplate
                .builder(type())
                .setDelay(if(actor is NPC) 0 else if(target.isPlayer) 0 else 1)
                .build()

        return arrayOf(Hit(actor, target, this, template))
    }

    override fun canAttack(actor: Agent, target: Agent): Boolean {
        //actor.positionToFace = target.position
        // Duel, disabled melee?
        if (actor is Player) {
            if (actor.dueling.inDuel() && actor.dueling.rules[DuelRule.NO_MELEE.ordinal]) {
                actor.statement("Melee has been disabled in this duel!")
                actor.combat.reset(false)
                return false
            }
            if (target is NPC) {
                if(MonsterRace.isRace(target, MonsterRace.AVIANSIE)){
                    actor.message(""+ target.asNpc.fetchDefinition().name +" is flying too high for you to attack using melee.")
                    actor.combat.reset(false)
                    return false
                }
            }
        }
        return true
    }

    override fun duration(actor: Agent) = actor.baseAttackSpeed

    override fun requiredDistance(actor: Agent): Int {
        return if (actor.combat.uses(HALBERD)) 2 else 1
    }

    override fun animate(actor: Agent) {
        val animation = actor.attackAnim
        if (animation != -1)
            actor.performAnimation(Animation(animation, 0, 1, Priority.HIGH))
    }

    override fun postHitAction(actor: Agent, target: Agent) {
        if (actor is Player) {
            if (ScytheOfViturEffect.canUse(actor))
                ScytheOfViturEffect.postHit(actor, target)
            else if (HolyScytheOfViturEffect.canUse(actor))
                HolyScytheOfViturEffect.postHit(actor, target)
            else if (SanguineScytheOfViturEffect.canUse(actor))
                SanguineScytheOfViturEffect.postHit(actor, target)
            else if (LadykillerScytheEffect.canUse(actor))
                LadykillerScytheEffect.postHit(actor, target)
        }
        val blockAnim = target.blockAnim
        if(blockAnim > 0)
            target.performAnimation(Animation(blockAnim, 0))
    }

    companion object {
        /**
         * The default special combat attackStrategy.
         */
		@JvmField
		val INSTANCE = SpecialAttackStrategy()
    }
}