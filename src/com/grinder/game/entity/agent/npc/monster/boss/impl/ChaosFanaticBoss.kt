package com.grinder.game.entity.agent.npc.monster.boss.impl

import com.grinder.game.World
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackType.Builder.Odds.FOUR_FIFTH
import com.grinder.game.entity.agent.combat.attack.AttackType.Builder.Odds.TWO_FIFTH
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.combat.event.CombatState
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.hit.damage.Damage
import com.grinder.game.entity.agent.combat.hit.damage.DamageMask
import com.grinder.game.entity.agent.combat.onOutgoingHitApplied
import com.grinder.game.entity.agent.combat.onState
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.npc.monster.boss.BossAttackUtil
import com.grinder.game.model.*
import com.grinder.game.model.projectile.Projectile
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.sound.AreaSound
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.util.DistanceUtil
import com.grinder.util.Misc
import com.grinder.util.time.TimeUtil
import java.util.*
import java.util.function.Consumer
import java.util.function.Predicate
import java.util.stream.Stream

/**
 * https://oldschool.runescape.wiki/w/Chaos_Fanatic
 *
 * "The Chaos Fanatic is an insane mage that resides west of the Lava Maze."
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   29/09/2019
 * @version 1.0
 */
class ChaosFanaticBoss(npcId: Int, position : Position) : Boss(npcId, position), AttackProvider {

    private var special: Special? = null

    init {
        race = MonsterRace.HUMAN
        combat.onState(CombatState.STARTING_ATTACK) {
            if (special == Special.SPLASH) {
                Arrays.stream(generateFallingProjectiles(combat.target))
                    .forEach(executeProjectile())
                combat.extendNextAttackDelay(4)
            }
        }
        combat.onOutgoingHitApplied {
            target.ifPlayer {
                if (Misc.randomChance(33.3f))
                    if (special == Special.DISARM)
                        BossAttackUtil.disarm(it)
            }
        }
    }

    override fun generateAttack(): BossAttack {
        val attack = BossAttack(this)
        attack.setType(AttackType.MAGIC)
        return attack
    }

    override fun randomizeAttack() {
        super.randomizeAttack()
        special = if (bossAttack.type() == AttackType.SPECIAL)
            Misc.random(Special.SPLASH, Special.DISARM)
        else null
    }

    override fun attackTypes() = AttackType.builder()
        .add(TWO_FIFTH, AttackType.SPECIAL)
        .add(FOUR_FIFTH, AttackType.MAGIC)
        .build()!!

    override fun attackRange(type: AttackType) = 8

    override fun checkAttackRangeForTypeExclusion(type: AttackType) = true

    override fun maxTargetsHitPerAttack(type: AttackType) = 5

    override fun fetchAttackDuration(type: AttackType?) = 2

    override fun getAttackAnimation(type: AttackType?) = Animation(811)

    override fun fetchProjectiles(type: AttackType?): Stream<ProjectileTemplate> = when(type) {
        AttackType.MAGIC -> ProjectileTemplate
            .builder(554)
            .setDelay(62)
            .setSpeed(if (asNpc.combat.target != null) ((DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 3).coerceAtMost(35)) else 0)
            .setCurve(0)
            .buildAsStream()
        else -> Stream.empty()
    }

    override fun fetchAttackSound(type: AttackType?): Optional<Sound> {
        return when(type) {
            AttackType.MELEE -> Optional.of(Sound(Sounds.KICK_SOUND))
            AttackType.SPECIAL -> Optional.of(AreaSound(344, 0, 1, 7))
            AttackType.MAGIC -> {
                Optional.of(AreaSound(343, 0, 1, 7))
            }
            else -> Optional.empty()
        }
    }

    override fun fetchHits(type: AttackType?): Stream<HitTemplate> =  if(type == AttackType.SPECIAL)
        Stream.empty()
    else
        HitTemplate.builder(type).setDelay(if (asNpc.combat.target == null) 0 else MagicAttackStrategy.getMagicSpellHitDelay(asNpc, asNpc.combat.target))
            .setSuccessOrFailedGraphic(Graphic(305, GraphicHeight.HIGH))
            .setSuccessOrFailedSound(Sound(Sounds.EARTH_WAVE_CONTACT))
            .buildAsStream()

    override fun fetchTextAboveHead(type: AttackType?) = when {
        Misc.randomChance(12.5F) -> Optional.of(MESSAGES.random())
        else -> Optional.empty()
    }

    private fun generateFallingProjectiles(target: Agent): Array<Projectile> {
        val positions = arrayOfNulls<Position>(3)
        val distance = 1.coerceAtMost(position.getDistance(target.position))
        val throwCalculated = !target.motion.completed()
        val projectileDirection = if (throwCalculated)
            target.walkingDirection
        else
            walkingDirection
        val deltaX = projectileDirection.directionDelta[0]
        val deltaY = projectileDirection.directionDelta[1]
        val standard = deltaX + deltaY
        val position1 = target.position.copy()
        val position2 = if (throwCalculated) position1.copy().add((2 - Misc.getRandomInclusive(4)) * standard, (2 - Misc.getRandomInclusive(4)) * standard) else position1.copy().add(2 - Misc.getRandomInclusive(4), 2 - Misc.getRandomInclusive(4))
        val position3 = if (throwCalculated) position2.copy().add((2 - Misc.getRandomInclusive(4)) * standard, (2 - Misc.getRandomInclusive(4)) * standard) else position1.copy().add(2 - Misc.getRandomInclusive(4), 2 - Misc.getRandomInclusive(4))
        positions[0] = position1
        positions[1] = position2
        positions[2] = position3
        return Array(positions.size) {
            Projectile(position, positions[it],
                551, 80, 48, 80, 0, 42 - 10 / distance)
        }
    }

    private fun executeProjectile(): Consumer<Projectile> {
        return Consumer { projectile: Projectile ->
            val hitDelay = executeProjectile(projectile)
            World.spawn(TileGraphic(projectile.target, Graphic(157, hitDelay / 2, GraphicHeight.MIDDLE)))
            TaskManager.submit(object : Task(TimeUtil.CLIENT_CYCLES.toGameCycles((hitDelay.toLong() /1.5).toLong()), false) {
                override fun execute() {
                    combat.targetStream(12)
                        .filter(samePositionAs(projectile))
                        .forEach(lightningHit())
                    stop()
                }
            })
        }
    }

    private fun lightningHit() = Consumer {
            character: Agent -> character.combat.queue(Hit(this, character, bossAttack, HitTemplate
        .builder(AttackType.SPECIAL)
        .setDamageRange(15..35)
        .setDelay(0)
        .setIgnoreAttackStats(true)
        .setIgnoreStrengthStats(true)
        .build()))
    }

    private fun samePositionAs(projectile: Projectile) = Predicate {
            character: Agent -> character.position.sameAs(projectile.target)
    }

    private enum class Special {
        DISARM,
        SPLASH;
    }

    companion object {
        val MESSAGES = arrayOf(
            "Burn!",
            "WEUGH!",
            "Develish Oxen Roll!",
            "All your wilderness are belong to them!",
            "AhehHeheuhHhahueHuUEehEahAH",
            "I shall call him squidgy and he shall be my squidgy!"
        )
    }
}