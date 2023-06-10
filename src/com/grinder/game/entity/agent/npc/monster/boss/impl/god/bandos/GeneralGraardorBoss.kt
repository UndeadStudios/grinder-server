package com.grinder.game.entity.agent.npc.monster.boss.impl.god.bandos


import com.grinder.game.entity.agent.npc.monster.boss.impl.god.God
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.model.areas.godwars.GodChamber
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.attack.AttackType.Builder.Odds
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.combat.attack.strategy.RangedAttackStrategy
import com.grinder.game.entity.agent.npc.monster.boss.OutOfRangePolicy
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.bandos.GeneralGraardorBoss
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.hit.HitTemplateBuilder
import com.grinder.game.model.GraphicHeight
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.Position
import com.grinder.util.DistanceUtil
import com.grinder.util.Misc
import java.util.*
import java.util.stream.Stream

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-11
 */
class GeneralGraardorBoss(npcId: Int, position: Position?, inGodWars: Boolean) : God(npcId, position!!, inGodWars),
    AttackProvider {


    override fun chamber(): GodChamber {
        return GodChamber.BANDOS
    }

    override fun generateAttack(): BossAttack {
        val attack = BossAttack(this)
        attack.setType(AttackType.MELEE)
        return attack
    }

    override fun attackTypes(): AttackTypeProvider {
        return AttackType.builder()
            .add(Odds.ONE_THIRD, AttackType.RANGED)
            .add(Odds.TWO_THIRD, AttackType.MELEE)
            .build()
    }

    override fun attackRange(type: AttackType) = when(type) {
        AttackType.MELEE -> 1
        else -> 12
    }

    override fun attackRangePolicy(type: AttackType): OutOfRangePolicy {
        return OutOfRangePolicy.TRACE_TO_TARGET
    }

    override fun fetchAttackDuration(type: AttackType): Int {
        return 6
    }

    public override fun maxTargetsHitPerAttack(type: AttackType): Int {
        return if (type == AttackType.RANGED) 4 else 1
    }

    override fun getMaxHit(type: AttackType): Int {
        return if (type == AttackType.MELEE) 60 else if (type == AttackType.RANGED) 35 else super.getMaxHit(type)
    }

    override fun getAttackAnimation(type: AttackType): Animation {
        return if (type == AttackType.RANGED) RANGED_ANIMATION else MELEE_ANIMATION
    }

    override fun fetchProjectiles(type: AttackType): Stream<ProjectileTemplate> {
        return if (type == AttackType.RANGED) ProjectileTemplate.builder(1202)
            .setStartHeight(10)
            .setEndHeight(8)
            .setCurve(18)
            .setSpeed(if (asNpc.combat.target != null) (10 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 3).coerceAtMost(30)) else 10)
            .setDelay(33)
            .buildAsStream() else Stream.empty()
    }

    override fun fetchHits(type: AttackType): Stream<HitTemplate> {
        val builder = HitTemplate.builder(type)
        if (type == AttackType.RANGED) {
            builder.setDelay(if (asNpc.combat.target == null) 0 else RangedAttackStrategy.getNPCRangeHitDelay(asNpc, asNpc.combat.target))
                .setDefenceStat(EquipmentBonuses.DEFENCE_RANGE)
                .setSuccessOrFailedGraphic(Graphic(160, GraphicHeight.HIGH))
        } else {
            builder.setDelay(0).setDefenceStat(EquipmentBonuses.DEFENCE_CRUSH)
        }
        return builder.buildAsStream()
    }

    override fun fetchAttackGraphic(type: AttackType): Optional<Graphic> {
        return if (type == AttackType.RANGED) Optional.of(Graphic(1203, GraphicHeight.LOW)) else Optional.empty()
    }

    override fun fetchTextAboveHead(type: AttackType): Optional<String> {
        return if (Misc.randomChance(33.33f)) Optional.of(Misc.random(*CHATS_ABOVE_HEAD)) else Optional.empty()
    }

    companion object {
        val CHATS_ABOVE_HEAD = arrayOf(
            "Death to our enemies!", "CHAAARGE!", "GRRRAAAAAAR!", "We feast on the bones of our enemies tonight!",
            "For the glory of the Big High War God!", "Crush them underfoot!", "Break their bones!"
        )
        private val MELEE_ANIMATION = Animation(7018)
        private val RANGED_ANIMATION = Animation(7021)
    }

    init {
        race = MonsterRace.GOBLIN
    }
}