package com.grinder.game.entity.agent.npc.monster.boss.impl

import com.grinder.game.content.minigame.aquaisneige.AquaisNeigeAttack
import com.grinder.game.entity.Entity
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackType.*
import com.grinder.game.entity.agent.combat.attack.AttackType.Builder.Odds
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.combat.attack.strategy.RangedAttackStrategy
import com.grinder.game.entity.agent.combat.event.impl.FreezeEvent
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.onOutgoingHitApplied
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.npc.monster.boss.OutOfRangePolicy
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.*
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.DistanceUtil
import com.grinder.util.Misc
import com.grinder.util.Priority
import java.util.*
import java.util.stream.Stream

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   09/09/2019
 * @version 1.0
 */
class TheUntouchableBoss(npcId: Int, position: Position)
    : Boss(npcId, position), AttackProvider{

    private lateinit var magicStyle : MagicStyle

    init {
        combat.onOutgoingHitApplied {
            target.ifPlayer {
                when(attackType){
                    MAGIC -> {

                        when (magicStyle) {
                            MagicStyle.TELEBLOCK ->  it.combat.teleBlockTimer.let { teleblock ->
                                    teleblock.start(300)
                                    if (teleblock.finished())
                                    it.message("<col=4f006f>A teleblock spell has been cast on you. It will expire in 5 minutes.")
                                    it.packetSender.sendEffectTimer(300, EffectTimer.TELE_BLOCK)
                                return@ifPlayer
                            }

                            MagicStyle.SILK -> { it.skillManager.decreaseLevelTemporarily(Skill.PRAYER, Misc.getRandomInclusive(15), 0)
                                return@ifPlayer
                            }
                            MagicStyle.FREEZE -> {
                                it.combat.submit(FreezeEvent(5, true))
                                return@ifPlayer
                            }
                            MagicStyle.SKILL_DECREASE -> {
                                it.skillManager.let { skills ->
                                    skills.decreaseLevelTemporarily(Skill.ATTACK, Misc.getRandomInclusive(13), 0)
                                    skills.decreaseLevelTemporarily(Skill.ATTACK, Misc.getRandomInclusive(13), 0)
                                    skills.decreaseLevelTemporarily(Skill.STRENGTH, Misc.getRandomInclusive(14), 0)
                                    skills.decreaseLevelTemporarily(Skill.DEFENCE, Misc.getRandomInclusive(12), 0)
                                    skills.decreaseLevelTemporarily(Skill.MAGIC, Misc.getRandomInclusive(13), 0)
                                    skills.decreaseLevelTemporarily(Skill.RANGED, Misc.getRandomInclusive(14), 0)
                                }
                                return@ifPlayer
                            }
                        }
                    }
                }
            }
        }
    }

    override fun getRetreatPolicy(): MonsterRetreatPolicy {
        return MonsterRetreatPolicy.RETREAT_IF_OUT_OF_COMBAT
    }

    override fun generateAttack(): BossAttack {
        val attack = BossAttack(this)
        attack.setType(MAGIC)
        return attack
    }

    override fun randomizeAttack() {
        super.randomizeAttack()
        if(bossAttack.type() == MAGIC)
            magicStyle = Misc.random(*MagicStyle.values())
    }

    override fun getMaxHit(type: AttackType?): Int {
        return when {
            bossAttack.type() == SPECIAL -> 55
            bossAttack.type() == MELEE -> 44
            else -> 50
        }
    }

    override fun attackTypes() = builder()
            .add(Odds.TWO_FOURTH, MELEE)
            .add(Odds.ONE_FIFTH, SPECIAL)
            .add(Odds.FOUR_FIFTH, MAGIC)
            .build()!!

    override fun attackRange(type: AttackType) = if (type == MELEE) 1 else 6

    override fun checkAttackRangeForTypeExclusion(type: AttackType): Boolean {
        return true
    }

    // It attacks from far, but when you are close it will attack with melee and magic
    override fun attackRangePolicy(type: AttackType): OutOfRangePolicy {
        return when (type) {
            MELEE -> OutOfRangePolicy.EXCLUDE_TYPE
            else -> OutOfRangePolicy.TRACE_TO_TARGET
        }
    }

    override fun maxTargetsHitPerAttack(type: AttackType) =  if (type == MELEE) 1 else 5

    override fun fetchAttackDuration(type: AttackType?) = 6

    override fun getAttackAnimation(type: AttackType?) = Animation(6329)

    override fun fetchProjectiles(type: AttackType?): Stream<ProjectileTemplate> {
        when (type) {
            SPECIAL -> {
                return Stream.of(
                    ProjectileTemplate.builder(258)
                        .setSourceSize(3)
                        .setSourceOffset(1)
                        .setHeights(40, 35)
                        .setCurve(280)
                        .setSpeed(if (asNpc.combat.target != null) (25 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 3.coerceAtMost(35))) else 25)
                        .setDelay(35)
                        .build())
            }
            MAGIC -> {
                return (magicStyle?.let {
                    Stream.of(
                        ProjectileTemplate.builder(it.projectileId())
                            .setSpeed(if (asNpc.combat.target != null) (25 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 3.coerceAtMost(35))) else 25)
                            .build())
                })?:Stream.empty()
            }
        }
        return Stream.empty()
    }

    override fun fetchHits(type: AttackType?): Stream<HitTemplate> = when(type) {
        MAGIC -> HitTemplate
                .builder(MAGIC)
                .setDelay(if (asNpc.combat.target == null) 2 else MagicAttackStrategy.getMagicSpellHitDelay(asNpc, asNpc.combat.target))
                .setDefenceStat(EquipmentBonuses.DEFENCE_MAGIC)
                .setSuccessOrFailedGraphic(magicStyle.graphic)
                .setSuccessOrFailedSound(magicStyle.sound)
                .buildAsStream()
        SPECIAL -> HitTemplate
                .builder(SPECIAL)
                .setDelay(if (asNpc.combat.target == null) 2 else MagicAttackStrategy.getMagicSpellHitDelay(asNpc, asNpc.combat.target))
                .setDefenceStat(EquipmentBonuses.DEFENCE_MAGIC)
                .setIgnorePrayer(true)
                .setSuccessOrFailedGraphic(Graphic(197))
                .setSuccessOrFailedSound(Sound(Sounds.EXTINGUISH_FIRE))
                .buildAsStream()
        else -> Stream.of(MELEE_HIT)
    }

    override fun fetchTextAboveHead(type: AttackType?): Optional<String>  {
        if(Misc.randomChance(20.0F)) {
            val shouts = when(type){
                SPECIAL -> specialShouts
                MAGIC -> magicStyle.shouts
                else -> emptyArray()
            }
            if(shouts.isNotEmpty())
                return Optional.of(Misc.randomString(*shouts))
        }
        return Optional.empty()
    }

    companion object {
        private val specialShouts = arrayOf("You make me powerful!", "Your faith means nothing here!")
        }


    enum class MagicStyle(val graphic: Graphic, val sound: Sound, vararg val shouts: String) : ProjectileTemplate {
        FREEZE(Graphic(187, GraphicHeight.MIDDLE), Sound(65), "AT LEAST!", "Fill my soul with smoke!", "Flood my lungs with blood!", "I demand a blood sacrifice!") {
            override fun sourceSize() = 3
            override fun sourceOffset() = 1
            override fun projectileId() = 484
            override fun startHeight() = 40
            override fun endHeight() = 35
            override fun curve() = 280
            override fun lifetime() = 20
            override fun delay() = 35
        }, SKILL_DECREASE(Graphic(1460, GraphicHeight.HIGH, Priority.HIGHEST), Sound(Sounds.WATER_SURGE_CONTACT), "Feel my winter soul!") {
            override fun sourceSize() = 3
            override fun sourceOffset() = 1
            override fun projectileId() = 1459
            override fun startHeight() = 40
            override fun endHeight() = 35
            override fun curve() = 280
            override fun lifetime() = 20
            override fun delay() = 40
        }, SILK(Graphic(281), Sound(105), *specialShouts) {
            override fun sourceSize() = 3
            override fun sourceOffset() = 1
            override fun projectileId() = 280
            override fun startHeight() = 40
            override fun endHeight() = 35
            override fun curve() = 280
            override fun lifetime() = 20
            override fun delay() = 35
        }, TELEBLOCK(Graphic(144, GraphicHeight.HIGH), Sound(Sounds.TELE_BLOCK_CONTACT), "You can't escape my wrath!", "Are you going anywhere? I don't think so!", "Taste my wrath!") {
            override fun sourceSize() = 3
            override fun sourceOffset() = 1
            override fun projectileId() = 143
            override fun startHeight() = 40
            override fun endHeight() = 35
            override fun curve() = 280
            override fun lifetime() = 20
            override fun delay() = 40
        };
    }
    private val MELEE_HIT = HitTemplate.builder(MELEE).setDelay(0).setDefenceStat(EquipmentBonuses.DEFENCE_STAB).build()


}