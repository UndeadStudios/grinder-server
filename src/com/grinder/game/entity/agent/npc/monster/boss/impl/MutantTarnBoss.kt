package com.grinder.game.entity.agent.npc.monster.boss.impl

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackType.Builder.Odds
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.attack.strategy.RangedAttackStrategy
import com.grinder.game.entity.agent.combat.event.CombatEvent
import com.grinder.game.entity.agent.combat.event.CombatEventListener
import com.grinder.game.entity.agent.combat.event.impl.IncomingHitApplied
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.hit.damage.Damage
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.npc.monster.boss.OutOfRangePolicy
import com.grinder.game.entity.agent.npc.monster.boss.minion.BossMinion
import com.grinder.game.entity.agent.npc.monster.boss.minion.BossMinionPolicy
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.game.model.Position
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.util.DistanceUtil
import com.grinder.util.Misc
import com.grinder.util.NpcID
import com.grinder.util.Priority
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * Represents the [NpcID.MUTANT_TARN] boss.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-26
 */
class MutantTarnBoss(npcId: Int, position: Position?) : Boss(npcId, position!!), AttackProvider {
    var raging = false
    override fun generateAttack(): BossAttack {
        return object : BossAttack(this) {
            override fun sequence(actor: Boss, target: Agent) {
                if (type() == AttackType.SPECIAL) generateWindStorms(actor, target) else super.sequence(actor, target)
            }
        }
        setPreferredAttackType(AttackType.MELEE)
    }

    private fun performRageAttack() {
        if (!raging) {
            val divisor: Float = if (hitpoints < fetchDefinition().hitpoints / 2) 20F else 30.toFloat()
            val chance = Math.max(5f, 100f - hitpoints.toFloat() / divisor) / 17.0f
            if (Misc.randomChance(chance)) {
                raging = true
                say("Raarhgg!")
                performAnimation(getAttackAnimation(AttackType.RANGED))
                val targets = combat.targetStream(20).collect(Collectors.toList())
                if (targets.isNotEmpty()) {
                    val target1 = Misc.random(targets)
                    val targetPosition1 = target1.position
                    val projectile1 = generateWindStormProjectile(centerPosition, targetPosition1, 120)
                    projectile1.sendProjectile()
                    projectile1.onArrival {
                        onWindStormArrival(this, projectile1, 5, false).run()
                        say("RaarHGGg!!")
                        performAnimation(getAttackAnimation(AttackType.RANGED))
                        val target2 = Misc.random(targets)
                        val targetPosition2 = target2.position
                        val projectile2 = generateWindStormProjectile(projectile1.target, targetPosition2, 85)
                        projectile2.sendProjectile()
                        projectile2.onArrival {
                            onWindStormArrival(this, projectile2, 5, false).run()
                            say("RAAHRrHGGg!!!")
                            performAnimation(getAttackAnimation(AttackType.RANGED))
                            val target3 = Misc.random(targets)
                            val targetPosition3 = target3.position
                            val projectile3 = generateWindStormProjectile(projectile2.target, targetPosition3, 70)
                            projectile3.sendProjectile()
                            projectile3.onArrival {
                                onWindStormArrival(this, projectile3, 5, true).run()
                                raging = false
                            }
                        }
                    }
                } else raging = false
                combat.extendNextAttackDelay(4)
            }
        }
    }

    override fun skipNextCombatSequence(): Boolean {
        return raging
    }

    private fun generateWindStorms(actor: Boss, target: Agent) {
        Stream.of(*generateWindProjectiles(target.position)).forEach { projectile: Projectile? ->
            projectile?.sendProjectile()
            projectile?.onArrival(onWindStormArrival(actor, projectile, 10, true))
        }
    }

    class TarnSmallTerrorDog(boss: MutantTarnBoss, position: Position) : BossMinion<MutantTarnBoss>(
        boss,
        NpcID.TERROR_DOG_6474,
        position,
        BossMinionPolicy.NO_RESPAWN,
        BossMinionPolicy.REMOVE_WHEN_BOSS_REMOVED,
        BossMinionPolicy.ATTACK_PREFERRED_OPPONENT
    ) {
        init {
            movementCoordinator.radius = 4
        }
    }

    class TarnBigTerrorDog(boss: MutantTarnBoss, position: Position) : BossMinion<MutantTarnBoss>(
        boss,
        NpcID.TERROR_DOG,
        position,
        BossMinionPolicy.NO_RESPAWN,
        BossMinionPolicy.REMOVE_WHEN_BOSS_REMOVED,
        BossMinionPolicy.ATTACK_PREFERRED_OPPONENT
    ) {
        init {
            movementCoordinator.radius = 4
        }
    }

    class TarnSpider(boss: MutantTarnBoss, position: Position) : BossMinion<MutantTarnBoss>(
        boss,
        NpcID.SPIDER_5239,
        position,
        BossMinionPolicy.NO_RESPAWN,
        BossMinionPolicy.REMOVE_WHEN_BOSS_REMOVED,
        BossMinionPolicy.ATTACK_PREFERRED_OPPONENT
    ) {
        init {
            movementCoordinator.radius = 4
        }
    }

    private fun onWindStormArrival(
        actor: Boss,
        projectile: Projectile,
        duration: Int,
        produceFadeProjectile: Boolean
    ): Runnable {
        return Runnable {
            TaskManager.submit(object : Task(true) {
                var tick = 0
                override fun execute() {
                    if (++tick == duration) {
                        if (produceFadeProjectile) {
                            val offspring = Projectile(
                                projectile.target,
                                projectile.target.copy().randomize(5),
                                ProjectileTemplate.builder(642)
                                    .setDelay(0)
                                    .setStartHeight(0)
                                    .setEndHeight(130)
                                    .setCurve(10)
                                    .setSpeed(70)
                                    .setSourceSize(1)
                                    .build()
                            )
                            offspring.sendProjectile()
                        }
                        stop()
                    } else sequenceProjectile()
                }

                private fun sequenceProjectile() {
                    actor.combat.targetStream(20).forEach { nearbyPlayer: Player ->
                        if (nearbyPlayer.position.sameAs(projectile.target)) {
                            nearbyPlayer.packetSender.sendGraphic(Graphic(783), projectile.target)
                            nearbyPlayer.combat.queue(Damage.create(0, 10))
                            nearbyPlayer.sendMessage("You got hit by the storm!")
                        }
                        if (tick == 1 || tick % 5 == 0) nearbyPlayer.packetSender.sendGraphic(
                            Graphic(643),
                            projectile.target
                        )
                    }
                }
            })
        }
    }

    override fun attackRangePolicy(type: AttackType): OutOfRangePolicy {
        return OutOfRangePolicy.TRACE_TO_TARGET
    }

    override fun getRetreatPolicy(): MonsterRetreatPolicy {
        return MonsterRetreatPolicy.NEVER
    }

    override fun attackTypes(): AttackTypeProvider {
        return AttackType.builder()
            .add(Odds.THREE_FOURTH, AttackType.MELEE)
            .add(Odds.TWO_FOURTH, AttackType.RANGED)
            .add(Odds.ONE_FIFTH, AttackType.SPECIAL)
            .build()
    }

    override fun checkAttackRangeForTypeExclusion(type: AttackType): Boolean {
        return type == AttackType.RANGED
    }

    override fun attackRange(type: AttackType): Int {
        return if (type == AttackType.MELEE) 1 else 12
    }

    /*@Override
    public int attackRange(@NotNull AttackType type) {
        return 1;
    }*/
    override fun fetchAttackDuration(type: AttackType): Int {
        return if (type == AttackType.MELEE) 4 else if (type == AttackType.SPECIAL) 10 else 6
    }

    public override fun maxTargetsHitPerAttack(type: AttackType): Int {
        return if (type == AttackType.RANGED) MAX_TARGETS else 1
    }

    override fun getAttackAnimation(type: AttackType): Animation {
        return if (type == AttackType.RANGED) Animation(5613, Priority.HIGH) else Animation(5617, Priority.HIGH)
    }

    override fun fetchProjectiles(type: AttackType): Stream<ProjectileTemplate> {
        return if (type == AttackType.RANGED) Stream.of(
            RANGED_PROJECTILE,
            RANGED_PROJECTILE_2,
            RANGED_PROJECTILE_3
        ) else Stream.empty()
    }

    override fun fetchHits(type: AttackType): Stream<HitTemplate> {
        return if (type == AttackType.SPECIAL) Stream.empty() else if (type == AttackType.RANGED) Stream.of(RANGED_HIT) else Stream.of(
            MELEE_HIT
        )
    }

    override fun fetchAttackGraphic(type: AttackType): Optional<Graphic> {
        return if (type == AttackType.RANGED) Optional.of(Graphic(1494, GraphicHeight.MIDDLE)) else Optional.empty()
    }

    override fun fetchTextAboveHead(type: AttackType): Optional<String> {
        return if (Misc.randomChance(33.33f)) Optional.of(Misc.random(*CHATS_ABOVE_HEAD)) else Optional.empty()
    }

    override fun getMaxHit(type: AttackType): Int {
        return if (type == AttackType.MELEE) 40 else 35
    }

    private fun generateWindProjectiles(targetPosition: Position): Array<Projectile?> {
        val positions = arrayOfNulls<Position>(5)
        val projectiles = arrayOfNulls<Projectile>(positions.size)
        val position1 = targetPosition.copy()
        val position2 = position1.copy().randomize(1)
        val position3 = position1.copy().randomize(2)
        val position4 = position1.copy().randomize(2)
        val position5 = position2.copy().randomize(3)
        positions[0] = position1
        positions[1] = position2
        positions[2] = position3
        positions[3] = position4
        positions[4] = position5
        for (i in positions.indices) projectiles[i] = generateWindStormProjectile(position, positions[i], 100)
        return projectiles
    }

    private fun generateWindStormProjectile(start: Position, position: Position?, speed: Int): Projectile {
        return Projectile(
            start, position, ProjectileTemplate.builder(642)
                .setDelay(20)
                .setStartHeight(0)
                .setEndHeight(0)
                .setCurve(0)
                .setSpeed(speed)
                .setSourceSize(1)
                .build()
        )
    }

    companion object {
        private const val MAX_TARGETS = 10
        private val CHATS_ABOVE_HEAD = arrayOf(
            "BONE CRUSH!!",
            "BOULDERS ROCK!!",
            "Hahaha, feel the pressure!",
            "Yuck! You ugly!",
            "Show me what you got MORTAL!",
            "FOOL!!"
        )
    }
        private val RANGED_HIT = HitTemplate.builder(AttackType.RANGED).setHitAmount(3).setDelay(if (asNpc.combat.target == null) 0 else RangedAttackStrategy.getNPCRangeHitDelay(asNpc, asNpc.combat.target)).build()
        private val MELEE_HIT = HitTemplate.builder(AttackType.MELEE).build()
        private val RANGED_PROJECTILE = ProjectileTemplate.builder(1493)
            .setStartHeight(83)
            .setEndHeight(33)
            .setCurve(6)
            .setSpeed(if (asNpc.combat.target != null) (8 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 2).coerceAtMost(30)) else 8)
            .setDelay(35)
            .build()
        private val RANGED_PROJECTILE_2 = ProjectileTemplate.builder(1493)
            .setStartHeight(74)
            .setEndHeight(33)
            .setCurve(22)
            .setSpeed(if (asNpc.combat.target != null) (8 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 2).coerceAtMost(30)) else 8)
            .setDelay(40)
            .build()
        private val RANGED_PROJECTILE_3 = ProjectileTemplate.builder(1493)
            .setStartHeight(43)
            .setEndHeight(38)
            .setCurve(20)
            .setSpeed(if (asNpc.combat.target != null) (3 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 2).coerceAtMost(30)) else 3)
            .setDelay(34)
            .build()

    init {
        combat.subscribe(object : CombatEventListener {
            override fun on(event: CombatEvent): Boolean {
                if (event is IncomingHitApplied) performRageAttack()
                return false
            }
        })
    }
}