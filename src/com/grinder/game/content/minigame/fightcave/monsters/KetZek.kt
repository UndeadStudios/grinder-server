package com.grinder.game.content.minigame.fightcave.monsters


import com.grinder.game.content.minigame.fightcave.FightCaveNpc
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.npc.monster.boss.OutOfRangePolicy
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.hit.HitTemplateBuilder
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.content.minigame.fightcave.FightCaveAttack
import com.grinder.game.model.Graphic
import com.grinder.game.model.Position
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.util.DistanceUtil
import com.grinder.util.Priority
import java.util.stream.Stream

/**
 * @author L E G E N D
 */
class KetZek(id: Int, position: Position?) : FightCaveNpc(id, position!!), AttackProvider {
    override fun attackTypes(): AttackTypeProvider {
        return AttackType.equalChances(AttackType.MELEE, AttackType.MAGIC)
    }

    override fun checkAttackRangeForTypeExclusion(type: AttackType): Boolean {
        return true
    }

    override fun attackRangePolicy(type: AttackType): OutOfRangePolicy {
        return when (type) {
            AttackType.MELEE -> OutOfRangePolicy.EXCLUDE_TYPE
            else -> OutOfRangePolicy.TRACE_TO_TARGET
        }
    }

    override fun fetchHits(type: AttackType): Stream<HitTemplate> {
        val builder = HitTemplate.builder(type).setDelay(3)
        if (type == AttackType.MAGIC) {
            builder.setSuccessOrFailedGraphic(Graphic(446, Priority.HIGHEST))
        }
        return builder.buildAsStream()
    }

    override fun generateAttack(): BossAttack {
        val attack = FightCaveAttack(this)
        attack.setType(AttackType.MAGIC)
        return attack
    }

    override fun getMaxHit(type: AttackType): Int {
        return if (type == AttackType.MELEE) 53 else if (type == AttackType.MAGIC) 49 else super.getMaxHit(type)
    }

    override fun fetchProjectiles(type: AttackType): Stream<ProjectileTemplate> {
        return if (type == AttackType.MAGIC) {
            ProjectileTemplate
                .builder(445)
                .setSourceSize(4)
                .setSourceOffset(4)
                .setStartHeight(124)
                .setEndHeight(32)
                .setCurve(25)
                .setSpeed(if (asNpc.combat.target != null) (50 - (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 4).coerceAtMost(49)) else 50)
                .setDelay(40)
                .buildAsStream()
        } else Stream.empty()
    }
}