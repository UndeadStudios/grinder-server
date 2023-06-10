package com.grinder.game.content.minigame.aquaisneige.monsters

import com.grinder.game.content.minigame.aquaisneige.AquaisNeigeAttack
import com.grinder.game.content.minigame.aquaisneige.AquaisNeigeNpc
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.attack.strategy.RangedAttackStrategy
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.npc.monster.boss.OutOfRangePolicy
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.game.model.Position
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.sound.Sound
import com.grinder.util.DistanceUtil
import com.grinder.util.Priority
import java.util.stream.Stream

class HydroTroll(id: Int, position: Position?) : AquaisNeigeNpc(id, position!!), AttackProvider {
    override fun attackTypes(): AttackTypeProvider {
        return AttackType.equalChances(AttackType.MELEE, AttackType.RANGED)
    }

    override fun fetchProjectiles(type: AttackType): Stream<ProjectileTemplate> {
        return if (type != AttackType.RANGED) {
            Stream.empty()
        } else ProjectileTemplate.builder(51)
            .setSourceSize(2)
            .setSourceOffset(2)
            .setHeights(43, 31)
            .setSpeed(if (asNpc.combat.target != null) (30 - (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 5).coerceAtMost(29)) else 30)
            .setDelay(50)
            .setCurve(280)
            .buildAsStream()
    }

    override fun checkAttackRangeForTypeExclusion(type: AttackType): Boolean {
        return true
    }

    override fun generateAttack(): BossAttack {
        val attack = AquaisNeigeAttack(this)
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
        val builder = HitTemplate.builder(type).setDelay(if (type == AttackType.MELEE) 0 else if (asNpc.combat.target == null) 0 else RangedAttackStrategy.getNPCRangeHitDelay(asNpc, asNpc.combat.target))
        val player = owner.asPlayer
        if (type == AttackType.RANGED) {
            builder.setSuccessOrFailedGraphic(Graphic(620, GraphicHeight.LOW, Priority.HIGH))
            player.playSound(Sound(871, 20))
        }
        return builder.buildAsStream()
    }

    init {
        race = MonsterRace.TROLL
    }
}