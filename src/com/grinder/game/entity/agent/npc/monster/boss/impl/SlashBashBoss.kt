package com.grinder.game.entity.agent.npc.monster.boss.impl

import com.grinder.game.entity.agent.combat.attack.AttackMode
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackType.Builder.Odds
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.combat.attack.strategy.RangedAttackStrategy
import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpellType
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.onIncomingHitQueued
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.model.*
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.model.sound.Sound
import com.grinder.game.model.sound.Sounds
import com.grinder.util.DistanceUtil
import com.grinder.util.Misc
import java.util.*
import java.util.stream.Stream

/**
 * TODO: add documentation
 *
 * TODO: add Brutal arrows
 * TODO: add diseases mechanics
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-11
 */
class SlashBashBoss(npcId: Int, position: Position, private val inDefaultArea: Boolean)
    : Boss(npcId, position), AttackProvider {

    init {
        race = MonsterRace.UNDEAD
        combat.onIncomingHitQueued {
            if (!context.used(CombatSpellType.CRUMBLE_UNDEAD)
                    && !context.used(CombatSpellType.UNDEAD_BASH)
                    && context.fightType.mode === AttackMode.CRUSH)
                multiplyDamage(0.75)
        }
    }

    override fun generateAttack() = BossAttack(this)

    override fun attackTypes() = AttackType.builder()
            .add(Odds.TWO_FOURTH, AttackType.MELEE)
       // .add(Odds.TWO_THIRD, AttackType.MAGIC, AttackType.RANGED)
            .add(Odds.TWO_FOURTH, AttackType.MAGIC)
            .build()!!

    override fun attackRange(type: AttackType) = when(type) {
        AttackType.MELEE -> 1
        else -> 12
    }

    override fun fetchAttackDuration(type: AttackType) = 6

    override fun getMaxHit(type: AttackType): Int {
        return if (type == AttackType.MELEE) 57 else if (type == AttackType.RANGED || type == AttackType.MAGIC) 44 else super.getMaxHit(type)
    }

    override fun getRetreatPolicy(): MonsterRetreatPolicy {
        return MonsterRetreatPolicy.RETREAT_IF_OUT_OF_COMBAT
    }

    public override fun maxTargetsHitPerAttack(type: AttackType) = if (type == AttackType.MELEE) 1 else 4

    override fun getAttackAnimation(type: AttackType) = if (type == AttackType.MAGIC || type == AttackType.RANGED) PROJECTILE_ATTACK_ANIMATION else ATTACK_ANIMATION

    override fun fetchProjectiles(type: AttackType): Stream<ProjectileTemplate> {
        return when (type) {
            AttackType.MAGIC -> Stream.of(MAGIC_PROJECTILE)
            //AttackType.RANGED -> Stream.of(RANGED_PROJECTILE)
            else -> Stream.empty()
        }
    }

    override fun fetchHits(type: AttackType?): Stream<HitTemplate> = when(type) {
        AttackType.MAGIC -> HitTemplate
            .builder(AttackType.MAGIC)
            .setDelay(if (asNpc.combat.target == null) 2 else MagicAttackStrategy.getMagicSpellHitDelay(asNpc, asNpc.combat.target)+1)
            .setDefenceStat(EquipmentBonuses.DEFENCE_MAGIC)
            .setSuccessOrFailedGraphic(Graphic(329, GraphicHeight.HIGH))
            .setSuccessOrFailedSound(Sound(Sounds.MAGIC_DART_CONTACT))
           // .setSuccessOrFailedSound(if (spell?.animationId == 711) Sound(Sounds.WATER_WAVE_CONTACT) else Sound(Sounds.ICE_BURST_CONTACT))
            .buildAsStream()
        AttackType.MELEE ->
            HitTemplate
                .builder(AttackType.MELEE)
                .setDelay(0)
                .setDefenceStat(EquipmentBonuses.DEFENCE_CRUSH)
                .buildAsStream()
/*        AttackType.RANGED ->
            HitTemplate
                .builder(AttackType.RANGED)
                .setDelay(if (asNpc.combat.target == null) 1 else RangedAttackStrategy.getNPCRangeHitDelay(asNpc, asNpc.combat.target)+1)
                .setDefenceStat(EquipmentBonuses.DEFENCE_RANGE)
                .setSuccessOrFailedSound(Sound(2800))
                .buildAsStream()*/
        else -> Stream.empty()
    }

    override fun fetchAttackGraphic(type: AttackType): Optional<Graphic> {
        return if (type == AttackType.MAGIC) Optional.of(Graphic(501, GraphicHeight.HIGH)) else Optional.empty()
    }

    override val circumscribedBoundaries: List<Boundary>
        get() = if (inDefaultArea) listOf(Boundary(2846, 2868, 9625, 9654)) else emptyList()

    private val MAGIC_PROJECTILE = ProjectileTemplate.builder(328)
        .setSourceSize(4)
        .setSourceOffset(2)
        .setStartHeight(110)
        .setEndHeight(30)
        .setSpeed(if (asNpc.combat.target != null) (10 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 2).coerceAtMost(40)) else 10)
        .setDelay(75)
        .setCurve(280)
        .build()
/*    private val RANGED_PROJECTILE = ProjectileTemplate.builder(428)
        .setSourceSize(4)
        .setSourceOffset(2)
        .setStartHeight(110)
        .setEndHeight(30)
        .setSpeed(if (asNpc.combat.target != null) (10 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 2).coerceAtMost(40)) else 10)
        .setDelay(150)
        .setCurve(280)
        .build()*/

    companion object {
        private val ATTACK_ANIMATION = Animation(2102)
        private val PROJECTILE_ATTACK_ANIMATION = Animation(2101)

    }
}