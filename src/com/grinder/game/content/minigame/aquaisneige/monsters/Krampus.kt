package com.grinder.game.entity.agent.npc.monster.boss.impl

import com.grinder.game.content.minigame.aquaisneige.AquaisNeigeAttack
import com.grinder.game.content.minigame.aquaisneige.AquaisNeigeNpc
import com.grinder.game.content.minigame.fightcave.FightCaveAttack
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackType.Builder.Odds
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.npc.monster.boss.OutOfRangePolicy
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.game.model.Position
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.DistanceUtil
import com.grinder.util.Priority
import java.util.*
import java.util.stream.Stream


class Krampus(id: Int, position: Position) : AquaisNeigeNpc(id, position!!), AttackProvider {


    private var spell: Spell? = null

    init {
        race = MonsterRace.DEMON
    }

    override fun maxTargetsHitPerAttack(type: AttackType) = 1

    override fun generateAttack(): BossAttack {
        val attack = AquaisNeigeAttack(this)
        attack.setType(AttackType.MAGIC)
        return attack
    }

    override fun randomizeAttack() {
        super.randomizeAttack()
        if (bossAttack.type() == AttackType.MAGIC) {
            spell = Spell.WATER_WAVE
        }
    }

    override fun attackTypes(): AttackTypeProvider {
        return AttackType.builder()
            .add(Odds.SEVEN_TENTH, AttackType.MELEE)
            .add(Odds.THREE_TENTH, AttackType.MAGIC)
            .build()
    }

    override fun checkAttackRangeForTypeExclusion(type: AttackType): Boolean {
        return true
    }

    // It attacks from far, but when you are close it will attack with melee and magic
    override fun attackRangePolicy(type: AttackType): OutOfRangePolicy {
        return when (type) {
            AttackType.MELEE -> OutOfRangePolicy.EXCLUDE_TYPE
            else -> OutOfRangePolicy.TRACE_TO_TARGET
        }
    }

    override fun attackRange(type: AttackType) = when(type) {
        AttackType.MAGIC -> 12
        else -> 1
    }

    override fun fetchHits(type: AttackType) = HitTemplate
        .builder(type)
        .setDelay(if (type == AttackType.MAGIC) (if (asNpc.combat.target == null) 0 else MagicAttackStrategy.getMagicSpellHitDelay(asNpc, asNpc.combat.target)) else 0)
        .also {
            if(type == AttackType.MAGIC)
                it.setSuccessOrFailedGraphic(spell?.endGraphic)
                    .setSuccessSound(spell?.sound)
        }
        .buildAsStream()

    override fun getAttackAnimation(type: AttackType?) = (if(type == AttackType.MAGIC) MAGIC_ANIM else MELEE_ANIM)
    override fun fetchAttackDuration(type: AttackType?) = 6
    override fun fetchProjectiles(type: AttackType?): Stream<ProjectileTemplate> {
        if(type == AttackType.MAGIC){
            return (spell?.let {
                Stream.of(ProjectileTemplate.builder(it.projectileId)
                    .setSourceSize(3)
                    .setSourceOffset(3)
                    .setStartHeight(93)
                    .setEndHeight(40)
                    .setSpeed(if (asNpc.combat.target != null) (55 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 2).coerceAtMost(40)) else 55)
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
        WATER_WAVE(162, Graphic(161, 2, 320), Graphic(163, 3, GraphicHeight.HIGH), Sound(Sounds.WATER_WAVE_CONTACT))
    }

    companion object {
        val MELEE_ANIM = Animation(64, Priority.HIGHEST)
        val MAGIC_ANIM = Animation(69, Priority.HIGHEST)
    }
}