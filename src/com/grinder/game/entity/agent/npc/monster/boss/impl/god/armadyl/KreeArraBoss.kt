package com.grinder.game.entity.agent.npc.monster.boss.impl.god.armadyl

import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.AttackType.*
import com.grinder.game.entity.agent.combat.attack.AttackTypeProvider
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.combat.attack.strategy.RangedAttackStrategy
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.God
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.game.model.Position
import com.grinder.game.model.areas.godwars.GodChamber
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.util.DistanceUtil
import com.grinder.util.Misc
import java.util.Optional.empty
import java.util.Optional.of
import java.util.stream.Stream

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-26
 */
class KreeArraBoss(npcId: Int, position: Position, inGodWars: Boolean)
    : God(npcId, position, inGodWars), AttackProvider {

    init {
        race = MonsterRace.AVIANSIE
    }

    override fun generateAttack() = BossAttack(this).apply {
        setType(RANGED)
    }

    override fun chamber()= GodChamber.ARMADYL
    override fun attackTypes(): AttackTypeProvider {
        return builder()
                .add(Builder.Odds.ONE_FOURTH, MELEE)
                .add(Builder.Odds.FOUR_FIFTH, RANGED)
                .add(Builder.Odds.ONE_FOURTH, MAGIC)
                .build()
    }
    override fun fetchAttackDuration(type: AttackType?) = 3
    override fun attackRange(type: AttackType) = if (type == MELEE) 1 else 12
    override fun getAttackAnimation(type: AttackType?) =  when (type) {
        RANGED -> Animation(6980)
        MAGIC -> Animation(6980)
        else -> Animation(6981)
    }
    override fun fetchProjectiles(type: AttackType?): Stream<ProjectileTemplate> = when (type) {
        RANGED -> Stream.of(createProjectile(1199))
        MAGIC -> Stream.of(createProjectile(1198))
        else -> Stream.empty()
    }

    override fun fetchHits(type: AttackType): Stream<HitTemplate> {
        val builder = HitTemplate.builder(type)
        when (type) {
            RANGED -> {
                builder.setDelay(if (asNpc.combat.target == null) 0 else RangedAttackStrategy.getNPCRangeHitDelay(asNpc, asNpc.combat.target))
                    .setDefenceStat(EquipmentBonuses.DEFENCE_RANGE)
            }
            MAGIC -> {
                builder.setDelay(if (asNpc.combat.target == null) 0 else MagicAttackStrategy.getMagicSpellHitDelay(asNpc, asNpc.combat.target))
                    .setDefenceStat(EquipmentBonuses.DEFENCE_MAGIC)
            }
            else -> {
                builder.setDelay(0).setDefenceStat(EquipmentBonuses.DEFENCE_SLASH)
            }
        }
        return builder.buildAsStream()
    }

    override fun getMaxHit(type: AttackType): Int {
        return if (type == MELEE) 26 else if (type == RANGED) 71 else if (type == MAGIC) 21 else super.getMaxHit(type)
    }

    override fun checkAttackRangeForTypeExclusion(type: AttackType): Boolean {
        return type == MELEE
    }

    override fun fetchTextAboveHead(type: AttackType?) = if(Misc.randomChance(10F))
        of("Skreeeee!")
    else
        empty()

    private fun createProjectile(id: Int) = ProjectileTemplate
            .builder(id)
            .setSourceOffset(5)
            .setSourceSize(5)
            .setStartHeight(50)
            .setEndHeight(43)
            .setSpeed(if (asNpc.combat.target != null) (13 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 2).coerceAtMost(30)) else 13)
            .setDelay(30)
            .build()

    public override fun maxTargetsHitPerAttack(type: AttackType) = if (type == MELEE) 1 else 4

}
