package com.grinder.game.content.minigame.fightcave.monsters


import com.grinder.game.content.minigame.fightcave.FightCaveNpc
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.content.minigame.fightcave.FightCaveAttack
import com.grinder.game.entity.agent.npc.monster.boss.OutOfRangePolicy
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.hit.HitTemplateBuilder
import com.grinder.game.model.Graphic
import com.grinder.game.model.Position
import com.grinder.game.model.sound.Sound
import com.grinder.util.DistanceUtil
import com.grinder.util.Priority
import java.util.stream.Stream

/**
 * @author L E G E N D
 */
class TokXil(id: Int, position: Position?) : FightCaveNpc(id, position!!), AttackProvider {
    override fun attackTypes(): AttackTypeProvider {
        return AttackType.equalChances(AttackType.MELEE, AttackType.RANGED)
    }

    override fun fetchProjectiles(type: AttackType): Stream<ProjectileTemplate> {
        return if (type != AttackType.RANGED) {
            Stream.empty()
        } else ProjectileTemplate.builder(443)
            .setSourceSize(4)
            .setSourceOffset(4)
            .setStartHeight(124)
            .setEndHeight(32)
            .setCurve(25)
            .setSpeed(if (asNpc.combat.target != null) (30 - (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 5).coerceAtMost(29)) else 35)
            .setDelay(55)
            .buildAsStream()
    }

    override fun checkAttackRangeForTypeExclusion(type: AttackType): Boolean {
        return true
    }

    override fun generateAttack(): BossAttack {
        val attack = FightCaveAttack(this)
        attack.setType(AttackType.RANGED)
        return attack
    }

    override fun attackRangePolicy(type: AttackType): OutOfRangePolicy {
        return when (type) {
            AttackType.MELEE -> OutOfRangePolicy.EXCLUDE_TYPE
            else -> OutOfRangePolicy.TRACE_TO_TARGET
        }
    }

    override fun fetchHits(type: AttackType): Stream<HitTemplate> {
        val builder = HitTemplate.builder(type).setDelay(if (type == AttackType.MELEE) 0 else 2)
        val player = owner.asPlayer
        if (type == AttackType.RANGED) {
            builder.setSuccessOrFailedGraphic(Graphic(443, Priority.HIGH))
            player.playSound(Sound(601, 20))
            player.playSound(Sound(1184, 40))
        } else {
            player.playSound(Sound(600, 20))
        }
        return builder.buildAsStream()
    }
}