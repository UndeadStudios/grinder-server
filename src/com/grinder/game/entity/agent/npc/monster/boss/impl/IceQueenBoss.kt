package com.grinder.game.entity.agent.npc.monster.boss.impl

import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.AttackMode
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackType.Builder.Odds.ONE_FIFTH
import com.grinder.game.entity.agent.combat.attack.AttackType.Builder.Odds.ONE_THIRD
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.combat.event.impl.FreezeEvent
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.onOutgoingHitApplied
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.npc.monster.boss.minion.BossMinion
import com.grinder.game.entity.agent.npc.monster.boss.minion.BossMinionPolicy
import com.grinder.game.entity.agent.player.decreaseLevel
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.agent.player.playAreaSound
import com.grinder.game.model.*
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.sound.AreaSound
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.util.DistanceUtil
import com.grinder.util.Misc
import com.grinder.util.NpcID
import java.util.*
import java.util.stream.Stream
import kotlin.random.Random

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   29/09/2019
 * @version 1.0
 */
class IceQueenBoss(npcId: Int, position : Position) : Boss(npcId, position), AttackProvider {

    init {
        race = MonsterRace.HUMAN
        movementCoordinator.radius = 4
        combat.onOutgoingHitApplied {
            if(attackType == AttackType.MAGIC){
                if(isAccurate){
                    target?.ifPlayer { player ->
                        player.combat.submit(FreezeEvent(10, false))
                        if (Random.nextBoolean()) {
                            val decrementAmount = Misc.getRandomInclusive(if (player.hasActivePrayer(PrayerHandler.PROTECT_FROM_MAGIC)) Misc.random(15) else Misc.randomInclusive(10, 25))
                            player.decreaseLevel(Skill.PRAYER, decrementAmount)
                            player.message("@red@You feel your prayer being drained slighty!")
                            player.playAreaSound(3239, radius = 1)
                        }
                    }
                } else {
                    target.performGraphic(Graphic(85))
                    target?.ifPlayer { player ->
                        player.playAreaSound(Sounds.MAGIC_SPLASH, radius = 1)
                    }
                }
            }
        }
    }

    override fun generateAttack() = object : BossAttack(this) {
            override fun sequence(actor: Boss, target: Agent) {

                if(type() == AttackType.SPECIAL){
                    combat.extendNextAttackDelay(5)
                    target.combat.resetCombatWith(actor)
                    TaskManager.submit(object : Task(1, true) {
                        var cycle = 0
                        val nextPosition = arrayOf(
                                Position(2875, 9955, 0),
                                Position(2874, 9958, 0),
                                Position(2865, 9954, 0),
                                Position(2858, 9955, 0)
                        ).random()

                        override fun execute() {
                            when(cycle) {
                                0 -> performAnimation(Animation(7705))
                                1 -> performGraphic(Graphic(1019))
                                2 -> setVisible(false)
                                4 -> {
                                    stop()
                                    setVisible(true)
                                    moveTo(nextPosition)
                                    performGraphic(Graphic(714, GraphicHeight.MIDDLE))
                                    performAnimation(Animation(715))
                                    combat.initiateCombat(target)
                                }
                            }
                            cycle++
                        }
                    })
                } else {
                    setType(AttackType.MAGIC)
                    super.sequence(actor, target)
                }
            }
    }

    private var spell : Spell? = null

    override fun randomizeAttack() {
        super.randomizeAttack()
        if(bossAttack.type() == AttackType.MAGIC)
            spell = Spell.values().random()
    }

    override fun fetchAttackSound(type: AttackType?): Optional<Sound> {
        return when(type) {
            AttackType.MELEE -> Optional.of(Sound(2565))
            AttackType.SPECIAL -> Optional.of(AreaSound(3991, 0, 1, 7))
            AttackType.MAGIC -> {
                if (spell?.animationId == 711) {
                    Optional.of(AreaSound(Sounds.WATER_WAVE_CAST, 0, 1, 7))
                } else {
                    Optional.of(AreaSound(Sounds.ICE_BURST_CAST, 0, 1, 7))
                }
            }
            else -> Optional.empty()
        }
    }

    override fun attackTypes() = AttackType.builder()
            .add(ONE_FIFTH, AttackType.MELEE)
            .add(3F, AttackType.SPECIAL)
            .add(ONE_THIRD, AttackType.MAGIC)
            .build()!!

    override fun attackRange(type: AttackType) = when(type){
        AttackType.MELEE -> 1
        else -> 8
    }

    override fun checkAttackRangeForTypeExclusion(type: AttackType): Boolean {
        return true
    }

    override fun maxTargetsHitPerAttack(type: AttackType) = 5

    override fun fetchAttackDuration(type: AttackType?) = when(type){
        AttackType.MELEE -> 4
        AttackType.MAGIC -> 4
        else -> 5
    }

    override fun skipNextRetreatSequence() = true

    override fun getAttackAnimation(type: AttackType?) = Animation(when(type){
        AttackType.MELEE -> 422 + Misc.getRandomInclusive(1)
        AttackType.MAGIC -> spell?.animationId?:-1
        else -> attackAnim
    })

    override fun fetchProjectiles(type: AttackType?): Stream<ProjectileTemplate> {
        if(type == AttackType.MAGIC){
            return (spell?.let {
                Stream.of(ProjectileTemplate.builder(it.projectileId())
                    .setSpeed(if (asNpc.combat.target != null) (5 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 2).coerceAtMost(25)) else 5)
                    .setDelay(65)
                    .build())
            })?:Stream.empty()
        }
        return Stream.empty()
    }

    override fun fetchHits(type: AttackType?): Stream<HitTemplate> = when(type) {
        AttackType.MAGIC -> HitTemplate
            .builder(AttackType.MAGIC)
            .setDelay(if (asNpc.combat.target == null) 2 else MagicAttackStrategy.getMagicSpellHitDelay(asNpc, asNpc.combat.target))
            .setDefenceStat(EquipmentBonuses.DEFENCE_MAGIC)
            .setSuccessOrFailedGraphic(spell?.hitGraphic)
            .setSuccessOrFailedSound(if (spell?.animationId == 711) Sound(Sounds.WATER_WAVE_CONTACT) else Sound(Sounds.ICE_BURST_CONTACT))
            .setIgnorePrayer(Misc.random(3) == 1)
            .buildAsStream()
        else -> Stream.empty()
    }

    override fun fetchAttackGraphic(type: AttackType?) = when(type) {
        AttackType.MAGIC -> Optional.ofNullable(spell?.attackGraphic)
        else -> Optional.empty()
    }

    override fun fetchTextAboveHead(type: AttackType?) = when {
        type == AttackType.SPECIAL -> Optional.of("Feel your faith leave you!")
        Misc.randomChance(12.5F) -> Optional.of(MESSAGES.random())
        else -> Optional.empty()
    }

    override fun useSmartPathfinding(): Boolean {
        return true
    }

    companion object {
        val MESSAGES = arrayOf(
                "This Kingdom is mine!",
                "My Supernova will end this world!",
                "You shouldn't be alive.",
                "I will watch you die.",
                "Are you so eager to die?",
                "Even the Gods fear my wrath!",
                "Never again will you see the light of day.",
                "No one will hear your screams.",
                "Are you dead yet?"
        )
    }

    class IceWarrior(boss: IceQueenBoss, position: Position) : BossMinion<IceQueenBoss>(
        boss,
        NpcID.ICE_WARRIOR_2851,
        position,
        BossMinionPolicy.NO_RESPAWN,
        BossMinionPolicy.REMOVE_WHEN_BOSS_REMOVED,
        BossMinionPolicy.ATTACK_PREFERRED_OPPONENT
    ) {
        init {
            movementCoordinator.radius = 4
        }
    }

    enum class Spell(val animationId: Int, val attackGraphic: Graphic, val hitGraphic: Graphic) : ProjectileTemplate {
        FIRST(711, Graphic(161, GraphicHeight.HIGH), Graphic(163, GraphicHeight.HIGH))
        {
            override fun sourceSize() = 1
            override fun sourceOffset() = 1
            override fun projectileId() = 162
            override fun startHeight() = 55
            override fun endHeight() = 43
            override fun curve() = 280
            override fun lifetime() = 10
            override fun delay() = 75
        },
        SECOND(1979, Graphic(366, GraphicHeight.LOW), Graphic(363))
        {
            override fun sourceSize() = 1
            override fun sourceOffset() = 1
            override fun projectileId() = 362
            override fun startHeight() = 0
            override fun endHeight() = 0
            override fun curve() = 6
            override fun lifetime() = 10
            override fun delay() = 75
        }
    }
}