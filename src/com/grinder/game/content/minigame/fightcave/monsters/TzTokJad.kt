package com.grinder.game.content.minigame.fightcave.monsters

import com.grinder.game.content.minigame.fightcave.Constants
import com.grinder.game.content.minigame.fightcave.FightCaveNpc
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.hit.damage.Damage
import com.grinder.game.model.areas.instanced.FightCaveArea
import com.grinder.game.content.minigame.fightcave.monsters.YtHurKot
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.content.minigame.fightcave.FightCaveAttack
import com.grinder.game.entity.agent.npc.monster.boss.OutOfRangePolicy
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.projectile.ProjectileTemplateBuilder
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.hit.HitTemplateBuilder
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.game.model.Position
import com.grinder.game.model.sound.Sound
import com.grinder.util.DistanceUtil
import com.grinder.util.Priority
import java.util.stream.Stream

/**
 * @author L E G E N D
 */
class TzTokJad(position: Position?) : FightCaveNpc(Constants.JAD, position!!), AttackProvider {
    private var healersSpawned = false
    override fun attackTypes(): AttackTypeProvider {
        return AttackType.equalChances(AttackType.MAGIC, AttackType.RANGED, AttackType.MELEE)
    }

    override fun fetchAttackDuration(type: AttackType): Int {
        return 8
    }

    override fun attackRange(type: AttackType): Int {
        return if (type == AttackType.MELEE) 1 else 12
    }

    override fun decrementHealth(damage: Damage): Damage {
        if (hitpoints <= 125 && !healersSpawned) {
            spawnHealers()
        }
        return super.decrementHealth(damage)
    }

    fun spawnHealers() {
        val cave = area as FightCaveArea
        healersSpawned = true
        for (i in 0..3) {
            val healer = YtHurKot(Constants.JAD_HEALER, Position(2397, 5088, owner.z))
            healer.owner = owner
            cave.add(healer)
            healer.spawn()
            healer.activate(this)
            healer.motion.followTarget(this, true, false)
        }
    }

    override fun generateAttack(): BossAttack {
        val attack = FightCaveAttack(this)
        attack.setType(AttackType.MAGIC)
        return attack
    }

    override fun checkAttackRangeForTypeExclusion(type: AttackType): Boolean {
        return true
    }

    override fun attackRangePolicy(type: AttackType): OutOfRangePolicy {
        return if (type == AttackType.MELEE) OutOfRangePolicy.EXCLUDE_TYPE else OutOfRangePolicy.TRACE_TO_TARGET
    }

    override fun fetchProjectiles(type: AttackType): Stream<ProjectileTemplate> {
        if (type == AttackType.MAGIC) {
            val magicProjectile1 = ProjectileTemplateBuilder(448)
                .setSourceSize(4)
                .setSourceOffset(4)
                .setStartHeight(124)
                .setEndHeight(32)
                .setCurve(16)
                .setSpeed(if (asNpc.combat.target != null) (60 - (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 4).coerceAtMost(40)) else 60) // Fast
                .setDelay(45)
                .build()
            val magicProjectile2 = ProjectileTemplateBuilder(449)
                .setSourceSize(4)
                .setSourceOffset(4)
                .setStartHeight(124)
                .setEndHeight(32)
                .setCurve(16)
                .setSpeed(if (asNpc.combat.target != null) (60 - (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 4).coerceAtMost(40)) else 60) // Normal
                .setDelay(55)
                .build()
            val magicProjectile3 = ProjectileTemplateBuilder(450)
                .setSourceSize(4)
                .setSourceOffset(4)
                .setStartHeight(124)
                .setEndHeight(32)
                .setCurve(16)
                .setSpeed(if (asNpc.combat.target != null) (60 - (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 4).coerceAtMost(40)) else 60) // Slow
                .setDelay(65)
                .build()
            performGraphic(Graphic(447, 0, height * 50 + 230))
            return Stream.of(magicProjectile1, magicProjectile2, magicProjectile3)
        }
        return Stream.empty()
    }

    override fun fetchHits(type: AttackType): Stream<HitTemplate> {
        val builder = HitTemplate.builder(type)
            .setDelay(if (type == AttackType.RANGED) 4 else if (type == AttackType.MAGIC) 3 else 0)
        val player = owner.asPlayer
        if (type == AttackType.RANGED) {
            player.performGraphic(Graphic(451, 60, GraphicHeight.HIGH, Priority.HIGHEST))
            builder.setSuccessOrFailedGraphic(Graphic(157, 0, GraphicHeight.HIGH, Priority.HIGH))
            player.playSound(Sound(163, 50))
        }
        if (type == AttackType.MAGIC) {
            builder.setSuccessOrFailedGraphic(Graphic(157, 25, GraphicHeight.HIGH, Priority.HIGH))
            player.playSound(Sound(159, 0))
            player.playSound(Sound(162, 50))
            player.playSound(Sound(163, 115))
        }
        if (type == AttackType.MELEE) {
            player.playSound(Sound(408, 0))
        }
        return builder.buildAsStream()
    }
}