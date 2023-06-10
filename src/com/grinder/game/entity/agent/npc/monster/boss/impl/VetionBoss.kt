package com.grinder.game.entity.agent.npc.monster.boss.impl

import com.grinder.game.World
import com.grinder.game.definition.NpcDefinition
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackType.Builder.Odds
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponFightType
import com.grinder.game.entity.agent.combat.event.CombatState
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.hit.damage.Damage
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask
import com.grinder.game.entity.agent.combat.onIncomingHitApplied
import com.grinder.game.entity.agent.combat.onIncomingHitQueued
import com.grinder.game.entity.agent.combat.onState
import com.grinder.game.entity.agent.npc.monster.MonsterEvents
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.npc.monster.boss.minion.BossMinion
import com.grinder.game.entity.agent.npc.monster.boss.minion.BossMinionPolicy
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.agent.player.playAreaSound
import com.grinder.game.entity.markTime
import com.grinder.game.entity.passedTime
import com.grinder.game.entity.removeAttribute
import com.grinder.game.model.*
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.util.Misc
import com.grinder.util.NpcID
import com.grinder.util.oldgrinder.Area
import com.grinder.util.time.TimeUtil
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import java.util.function.Predicate
import java.util.stream.Stream

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-22
 */
class VetionBoss(npcId: Int, position: Position)
    : Boss(npcId, position), AttackProvider {

    companion object {
        private val MELEE_SWING_SCIMITAR = Animation(5499)
        private val MELEE_STAB_SCIMITAR = Animation(5487)
        private val MAGIC_SWING_SCIMITAR = Animation(5485)
        private val SMASH_GROUND = Animation(5507)
        private const val MORPH_TIMER = "vetion_morph_timer"
        private const val SHOCKWAVE_TIMER = "vetion_shockwave_timer"
    }

    private var aliveMinions = 0
    private var spawnedMinions = false
    private var performShockWave = false

    init {
        race = MonsterRace.UNDEAD
        combat.onIncomingHitQueued {
            if (aliveMinions > 0) {
                setNegateDamages(true)
                return@onIncomingHitQueued
            }
            if (!context.usedAny(*WeaponFightType.OFFENSIVE_CRUSH))
                multiplyDamage(0.50)
            if (context.used(AttackType.MAGIC))
                multiplyDamage(0.0)
        }
        combat.onIncomingHitApplied {
            if (!spawnedMinions && hitpoints <= fetchDefinition().hitpoints / 2)
                spawnHellHounds()
        }
        combat.onState(CombatState.STARTING_ATTACK) {
            if (performShockWave) {
                combat.targetStream(4)
                        .filter(inDistance())
                        .forEach(earthquakeHit().andThen {
                            it.ifPlayer { player ->
                                player.message("Vet'ion pummels the ground sending a shattering earthquake shockwave through you.")
                                player.playAreaSound(574, 10, 1, 0)
                            }
                        })
                performShockWave = false
                markTime(SHOCKWAVE_TIMER)
            }
            if (bossAttack.type() == AttackType.MAGIC) {
                generateFallingProjectiles(combat.target).forEach {
                    executeProjectile().accept(it!!)
                }
            }
        }
        onEvent {
            if (it == MonsterEvents.ADDED){
                removeAttribute(SHOCKWAVE_TIMER)
                removeAttribute(MORPH_TIMER)
                if (isMorphed)
                    resetTransformation()
            }
            if (it == MonsterEvents.POST_SEQUENCE){
                if (isAlive && isMorphed) {
                    if (passedTime(MORPH_TIMER, 5, TimeUnit.MINUTES)) {
                        resetTransformation()
                        hitpoints = fetchDefinition().hitpoints
                    }
                }
            }
        }
    }

    private fun spawnHellHounds() {
        say(if (isMorphed) "Bahh! Go, Dogs!!" else "Kill, my pets!")
        spawnedMinions = true
        aliveMinions = 2
        Stream.of(SkeletonHellHoundMinion(this), SkeletonHellHoundMinion(this))
                .forEach { it.spawn() }
    }

    override fun generateAttack() = BossAttack(this)

    override fun appendDeath() {
        if (isMorphed) {
            aliveMinions = 0
            super.appendDeath()
            return
        }
        markTime(MORPH_TIMER)
        say("Do it again!!")
        npcTransformationId = NpcID.VETION_REBORN
        hitpoints = fetchDefinition().hitpoints
        spawnedMinions = false
    }

    override fun fetchDefinition(): NpcDefinition {
        return if (isMorphed) NpcDefinition.forId(NpcID.VETION_REBORN) else super.fetchDefinition()
    }

    override fun randomizeAttack() {
        if (passedTime(SHOCKWAVE_TIMER, 8, TimeUnit.SECONDS) && Misc.randomChance(10.0f))
            performShockWave = true
        super.randomizeAttack()
    }

    override fun immuneToAttack(type: AttackType): Pair<Boolean, String?> {
        return Pair(aliveMinions > 0, "You must kill the dogs before attacking Vet'ion!")
    }

    override fun skipNextRetreatSequence() = aliveMinions > 0

    override fun getRetreatPolicy(): MonsterRetreatPolicy {
        return if (aliveMinions > 0)
            MonsterRetreatPolicy.STANDARD
        else
            MonsterRetreatPolicy.RETREAT_IF_OUT_OF_COMBAT
    }

    override fun attackTypes(): AttackTypeProvider {
        return if (performShockWave) AttackType.SPECIAL else AttackType.builder()
                .add(Odds.ONE_FOURTH, AttackType.MAGIC)
                .add(Odds.THREE_FOURTH, AttackType.MELEE)
                .build()
    }

    override fun attackRange(type: AttackType): Int {
        return if (type == AttackType.MELEE) 2 else 10
    }

    override fun maxTargetsHitPerAttack(type: AttackType): Int {
        return if (type == AttackType.MELEE) 1 else 4
    }

    override fun fetchAttackDuration(type: AttackType) = 6

    override fun getAttackAnimation(type: AttackType): Animation {
        return when {
            type == AttackType.MELEE -> Misc.random(MELEE_STAB_SCIMITAR, MELEE_SWING_SCIMITAR)
            performShockWave -> SMASH_GROUND
            else -> MAGIC_SWING_SCIMITAR
        }
    }

    override fun fetchHits(type: AttackType): Stream<HitTemplate> {
        return if (type == AttackType.MELEE) HitTemplate
                .builder(AttackType.MELEE).setDelay(0).setDefenceStat(EquipmentBonuses.DEFENCE_CRUSH)
                .buildAsStream()
        else Stream.empty()
    }

    private fun executeProjectile(): Consumer<Projectile> {
        return Consumer { projectile: Projectile ->
            val hitDelay = executeProjectile(projectile)
            World.spawn(TileGraphic(projectile.target, Graphic(281, hitDelay / 2, GraphicHeight.LOW)))
            TaskManager.submit(object : Task(TimeUtil.CLIENT_CYCLES.toGameCycles(hitDelay.toLong()), this, false) {
                override fun execute() {
                    stop()
                    combat.targetStream(12)
                            .filter(samePositionAs(projectile))
                            .forEach(lightningHit())
                }
            })
        }
    }

    private fun earthquakeHit(): Consumer<Agent> {
        return Consumer { character: Agent -> character.combat.queue(Damage(Misc.random(1, 45), DamageMask.REGULAR_HIT)) }
    }

    private fun lightningHit(): Consumer<Agent> {
        return Consumer { character: Agent -> character.combat.queue(Hit(this, character, bossAttack, true, 1, 1)) }
    }

    private fun inDistance(): Predicate<Agent> {
        return Predicate { character: Agent -> character.position.isWithinDistance(position, 3) }
    }

    private fun samePositionAs(projectile: Projectile): Predicate<Agent> {
        return Predicate { character: Agent -> character.position.sameAs(projectile.target) }
    }

    private fun generateFallingProjectiles(target: Agent): Array<Projectile?> {
        val positions = arrayOfNulls<Position>(3)
        val projectiles = arrayOfNulls<Projectile>(positions.size)
        val distance = 1.coerceAtMost(position.getDistance(target.position))
        val throwCalculated = !target.motion.completed()
        val perfectLine = Misc.randomChance(50.0f)
        val projectileDirection = if (throwCalculated) target.walkingDirection else walkingDirection
        val deltaX = projectileDirection.directionDelta[0]
        val deltaY = projectileDirection.directionDelta[1]
        val standard = deltaX + deltaY
        val position1 = target.position.copy()
        val position2 = if (throwCalculated) position1.copy().add((if (perfectLine) 2 else 2 - Misc.getRandomInclusive(4)) * standard, (if (perfectLine) 2 else 2 - Misc.getRandomInclusive(4)) * standard) else position1.copy().add(2 - Misc.getRandomInclusive(4), 2 - Misc.getRandomInclusive(4))
        val position3 = if (throwCalculated) position2.copy().add((if (perfectLine) 2 else 2 - Misc.getRandomInclusive(4)) * standard, (if (perfectLine) 2 else 2 - Misc.getRandomInclusive(4)) * standard) else position1.copy().add(2 - Misc.getRandomInclusive(4), 2 - Misc.getRandomInclusive(4))
        positions[0] = position1
        positions[1] = position2
        positions[2] = position3
        for (i in positions.indices) {
            projectiles[i] = Projectile(position, positions[i], 280, 35, 48, 80, 0, 42 - 10 / distance)
            target.asPlayer.packetSender.sendAreaEntitySound(target.asPlayer, 360)
        }
        return projectiles
    }

    internal class SkeletonHellHoundMinion(bossNPC: VetionBoss)
        : BossMinion<VetionBoss>(
            bossNPC,
            if (!bossNPC.isMorphed)
                NpcID.SKELETON_HELLHOUND_6613
            else
                NpcID.GREATER_SKELETON_HELLHOUND,
            bossNPC.position.clone().let {
                Area.fromAbsolute(it, Area.of(5, 5, 5, 5))
                        .findRandomOpenPosition(bossNPC.plane, 1, it)
                        .orElse(it)
            },
            BossMinionPolicy.NO_RESPAWN,
            BossMinionPolicy.REMOVE_WHEN_BOSS_REMOVED,
            BossMinionPolicy.ATTACK_PREFERRED_OPPONENT
    ) {
        init {
            onEvent { monsterEvent: MonsterEvents ->
                if (monsterEvent === MonsterEvents.REMOVED) {
                    bossNPC.aliveMinions--
                } else if (monsterEvent === MonsterEvents.ADDED) {
                    combat.setAutoRetaliate(true)
                    say("GRRRRRRR")
                }
            }
        }
    }
}