package com.grinder.game.entity.agent.combat.attack.strategy.npc.monster

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackMode
import com.grinder.game.entity.agent.combat.attack.AttackStrategy
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.combat.attack.strategy.RangedAttackStrategy
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.movement.MovementStatus
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.game.model.Skill
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.sound.Sounds
import com.grinder.util.DistanceUtil
import com.grinder.util.Misc
import com.grinder.util.TaskFunctions.delayBy

/**
 * Handles the spinolyp's combat.
 */
class SpinolypAttack : AttackStrategy<NPC> {

    var currentAttackType: AttackStrategy<Agent> = MagicAttackStrategy.INSTANCE

    override fun type(): AttackType? {
        return currentAttackType.type()
    }

    override fun canAttack(actor: NPC, target: Agent): Boolean {
        actor.motion.update(MovementStatus.DISABLED)
        return true
    }

    override fun createHits(actor: NPC, target: Agent): Array<Hit> {
        return arrayOf(Hit(actor, target, this, true, if (currentAttackType == MagicAttackStrategy.INSTANCE) MagicAttackStrategy.getMagicSpellHitDelay(actor, target) else if (actor.combat.target == null) 0 else RangedAttackStrategy.getNPCRangeHitDelay(actor, actor.combat.target)))
    }

    override fun sequence(actor: NPC, target: Agent) {

        if (Misc.randomInclusive(0, 2) == 2) {
            currentAttackType = MagicAttackStrategy.INSTANCE
        } else {
            currentAttackType = RangedAttackStrategy.INSTANCE
        }
        if (currentAttackType == MagicAttackStrategy.INSTANCE) {
            val projBldr = ProjectileTemplate
                .builder(94)
                .setSourceOffset(1)
                .setDelay(25)
                .setSpeed(if (actor.combat.target != null) (35 + (DistanceUtil.getChebyshevDistance(actor.position, actor.combat.target.position) * 3).coerceAtMost(35)) else 35)
                .setHeights(20, 43)
                .setCurve(280)

            val projectile = Projectile(actor, target, projBldr.build())
            projectile.sendProjectile()
        } else {
            val projBldr = ProjectileTemplate
                .builder(294)
                .setSourceOffset(1)
                .setDelay(25)
                .setSpeed(if (actor.combat.target != null) (20 + (DistanceUtil.getChebyshevDistance(actor.position, actor.combat.target.position) * 2).coerceAtMost(30)) else 20)
                .setHeights(60, 43)
                .setCurve(280)

            val projectile = Projectile(actor, target, projBldr.build())
            projectile.sendProjectile()
        }
    }

    override fun duration(actor: NPC) =  actor.baseAttackSpeed

    override fun requiredDistance(actor: Agent) = 7

    override fun animate(actor: NPC) {
        val animation = actor.attackAnim
        if (animation != -1)
            actor.performAnimation(Animation(animation))
    }

    override fun postHitAction(actor: NPC, target: Agent) {
    }

    override fun postHitEffect(hit: Hit) {
        if (hit.target != null) {
                if (currentAttackType == MagicAttackStrategy.INSTANCE) {
                    if (hit.totalDamage != 0) {
                        hit.target.performGraphic(MAGIC_COMBAT_GFX)
                        hit.target.asOptionalPlayer.ifPresent { player: Player -> player.packetSender.sendSound(Sounds.WATER_STRIKE_CONTACT) }
                        hit.target.asOptionalPlayer.ifPresent { player: Player ->
                            player.skillManager.setCurrentLevel(
                                Skill.PRAYER,
                                player.skillManager.getCurrentLevel(Skill.PRAYER) - 1,
                                true
                            )
                        }
                    } else {
                        hit.target.performGraphic(MAGIC_SPLASH_GFX)
                        hit.target.asOptionalPlayer.ifPresent { player: Player -> player.packetSender.sendSound(Sounds.MAGIC_SPLASH) }
                }
            }
        }
    }

    companion object {
        private val MAGIC_COMBAT_GFX = Graphic(95, GraphicHeight.HIGH)
        private val MAGIC_SPLASH_GFX = Graphic(85, GraphicHeight.HIGH)
    }
}