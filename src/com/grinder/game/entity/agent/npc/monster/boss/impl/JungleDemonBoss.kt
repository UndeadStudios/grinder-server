package com.grinder.game.entity.agent.npc.monster.boss.impl

import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.npc.monster.boss.OutOfRangePolicy
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.game.model.Position
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.DistanceUtil
import com.grinder.util.Misc
import java.util.*
import java.util.stream.Stream

/**
 * https://oldschool.runescape.wiki/w/Jungle_Demon
 *
 * "The Jungle Demon alternates between all four blast spells for its magic attacks,
 * and its melee attack is like a halberd (which means that it can hit two tiles away).
 * Both types of attacks can hit up to 32 damage."
 *
 * TODO: add sounds
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/04/2020
 * @version 1.0
 */
class JungleDemonBoss(id: Int, position: Position) : Boss(id, position), AttackProvider {

    private var spell: Spell? = null

    init {
        race = MonsterRace.DEMON
    }

    override fun attackTypes() = AttackType.equalChances(AttackType.MAGIC, AttackType.MELEE)

    override fun generateAttack(): BossAttack {
        val attack = BossAttack(this)
        attack.setType(AttackType.MAGIC)
        return attack
    }
    override fun randomizeAttack() {
        super.randomizeAttack()
        if (bossAttack.type() == AttackType.MAGIC) {
            spell = Misc.random(*Spell.values())
        }
    }
    override fun maxTargetsHitPerAttack(type: AttackType) = 1
    override fun checkAttackRangeForTypeExclusion(type: AttackType): Boolean {
        return true
    }

    override fun attackRange(type: AttackType) = when(type) {
        AttackType.MAGIC -> 15
        else -> 2
    }

    // It attacks from far, but when you are close it will attack with melee and magic
    override fun attackRangePolicy(type: AttackType): OutOfRangePolicy {
        return when (type) {
            AttackType.MELEE -> OutOfRangePolicy.EXCLUDE_TYPE
            else -> OutOfRangePolicy.TRACE_TO_TARGET
        }
    }

    override fun fetchHits(type: AttackType?): Stream<HitTemplate> = when(type) {
        AttackType.MAGIC ->
        {
            HitTemplate
                .builder(AttackType.MAGIC)
                .setDelay(if (type == AttackType.MAGIC) (if (asNpc.combat.target == null) 0 else MagicAttackStrategy.getMagicSpellHitDelay(asNpc, asNpc.combat.target)) else 0)
                .setIgnorePrayer(Misc.random(3) == 1)
                .setDefenceStat(EquipmentBonuses.DEFENCE_MAGIC)
                .also {
                    if(type == AttackType.MAGIC)
                        it.setSuccessOrFailedGraphic(spell?.endGraphic)
                            .setSuccessSound(spell?.sound)
                }.buildAsStream()
        }
        else ->
            HitTemplate
                .builder(AttackType.MELEE)
                .setDelay(0)
                .setDefenceStat(EquipmentBonuses.DEFENCE_CRUSH)
                .buildAsStream()
    }

    override fun getAttackAnimation(type: AttackType?) = Animation(if(type == AttackType.MAGIC) 69 else 64)
    override fun fetchAttackDuration(type: AttackType?) = 6
    override fun fetchProjectiles(type: AttackType?): Stream<ProjectileTemplate> {
        if(type == AttackType.MAGIC){
            return (spell?.let {
                Stream.of(ProjectileTemplate.builder(it.projectileId)
                        .setSourceSize(3)
                        .setSourceOffset(3)
                        .setStartHeight(93)
                        .setEndHeight(40)
                        .setSpeed(if (asNpc.combat.target != null) (45 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 3.5).coerceAtMost(
                            50.0
                        ).toInt()) else 45)
                        .setDelay(22)
                        .setCurve(280)
                        .build())
            })?:Stream.empty()
        }
        return Stream.empty()
    }

    override fun fetchAttackGraphic(type: AttackType?): Optional<Graphic> {
        if(type == AttackType.MAGIC)
            return Optional.ofNullable(spell?.startGraphic)
        return Optional.empty()
    }

    enum class Spell(val projectileId: Int, val startGraphic: Graphic, val endGraphic: Graphic, val sound: Sound)
    {
        WIND_BLAST(159, Graphic(158, 2, 320), Graphic(160, 3, GraphicHeight.HIGH), Sound(Sounds.WIND_WAVE_CONTACT)),
        WATER_BLAST(162, Graphic(161, 2, 320), Graphic(163, 3, GraphicHeight.HIGH), Sound(Sounds.WATER_WAVE_CONTACT)),
        EARTH_BLAST(165, Graphic(164, 2, 320), Graphic(166, 3, GraphicHeight.HIGH), Sound(Sounds.EARTH_WAVE_CONTACT)),
        FIRE_BLAST(156, Graphic(155, 2, 320), Graphic(157, 3, GraphicHeight.HIGH), Sound(Sounds.FIRE_WAVE_CONTACT))
    }
}