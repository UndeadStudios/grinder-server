package com.grinder.game.entity.agent.npc.monster.boss.impl

import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.combat.attack.strategy.RangedAttackStrategy
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.movement.MovementStatus
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
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-10
 */
class SeaTrollQueenBoss(npcId: Int, position: Position?) : Boss(npcId, position!!), AttackProvider {

    private var spell: Spell? = null
    private var hitGraphic: Graphic? = null

    init {
        race = MonsterRace.TROLL
        motion.update(MovementStatus.DISABLED)
    }

    private enum class Spell(val projectileId: Int, val graphicId: Int) {
        FIRST(1166, 1167),
        SECOND(1252, 1253);
    }

    override fun generateAttack() = BossAttack(this)

    override fun randomizeAttack() {
        super.randomizeAttack()
        if (bossAttack.type() == AttackType.MAGIC) {
            spell = Misc.random(*Spell.values())
        }
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

    override fun attackTypes(): AttackTypeProvider {
        return AttackType.equalChances(AttackType.MELEE, AttackType.MAGIC, AttackType.RANGED)
    }

    public override fun maxTargetsHitPerAttack(type: AttackType): Int {
        return if (type == AttackType.MELEE) 1 else 4
    }

    override fun attackRange(type: AttackType): Int {
        return if (type == AttackType.MELEE) 3 else 15
    }

    override fun fetchAttackDuration(type: AttackType): Int {
        return baseAttackSpeed
    }

    override fun getAttackAnimation(type: AttackType): Animation {
        return Animation(3991)
    }

    override fun fetchProjectiles(type: AttackType): Stream<ProjectileTemplate> {
        if (type == AttackType.MAGIC)
            hitGraphic = Graphic(spell!!.graphicId, GraphicHeight.HIGH)
        return when (type) {
            AttackType.MAGIC -> ProjectileTemplate
                    .builder(spell!!.projectileId)
                    .setSourceSize(5)
                    .setStartHeight(31)
                    .setEndHeight(33)
                    .setCurve(12)
                    .setSpeed(if (asNpc.combat.target != null) (13 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 2).coerceAtMost(40)) else 13)
                    .setDelay(35)
                    .buildAsStream()
            AttackType.RANGED -> ProjectileTemplate
                    .builder(1017)
                    .setStartHeight(31)
                    .setEndHeight(35)
                    .setCurve(15)
                    .setSpeed(if (asNpc.combat.target != null) (1 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position))) else 1)
                    .setDelay(55)
                    .buildAsStream()
            else -> Stream.empty()
        }
    }

    override fun fetchHits(type: AttackType): Stream<HitTemplate> {
        when (type) {
            AttackType.RANGED -> {
                return HitTemplate
                    .builder(AttackType.RANGED)
                    .setDefenceStat(EquipmentBonuses.DEFENCE_RANGE)
                    .setDelay(if (asNpc.combat.target == null) 0 else RangedAttackStrategy.getNPCRangeHitDelay(asNpc, asNpc.combat.target))
                    .setSuccessOrFailedGraphic(Graphic(1195, GraphicHeight.MIDDLE))
                    .setSuccessOrFailedSound(Sound(1184))
                    .buildAsStream()
            }
            AttackType.MAGIC -> {
                return HitTemplate
                    .builder(AttackType.MAGIC)
                    .setDefenceStat(EquipmentBonuses.DEFENCE_MAGIC)
                    .setDelay(if (asNpc.combat.target == null) 0 else MagicAttackStrategy.getMagicSpellHitDelay(asNpc, asNpc.combat.target))
                    .setSuccessOrFailedGraphic(Graphic(spell!!.graphicId, GraphicHeight.HIGH))
                    .setSuccessOrFailedSound(if (spell?.projectileId == 1166) Sound(Sounds.FIRE_EXPLODING_SOUND) else Sound(Sounds.WATER_WAVE_CONTACT)).buildAsStream()
            }
            AttackType.MELEE -> {
                return HitTemplate
                    .builder(AttackType.MELEE)
                    .setDefenceStat(EquipmentBonuses.DEFENCE_SLASH)
                    .setDelay(0).buildAsStream()
            }
        }
        return Stream.empty()
    }

    override fun fetchAttackGraphic(type: AttackType): Optional<Graphic> {
        return if (type == AttackType.MAGIC)
            Optional.of(Graphic(1194))
        else
            Optional.empty()
    }
}