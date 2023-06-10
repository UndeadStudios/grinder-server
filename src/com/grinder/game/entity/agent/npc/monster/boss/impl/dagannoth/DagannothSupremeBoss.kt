package com.grinder.game.entity.agent.npc.monster.boss.impl.dagannoth

import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.RangedAttackStrategy
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.onOutgoingHitApplied
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.GraphicHeight
import com.grinder.game.model.Position
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.util.DistanceUtil
import java.util.stream.Stream

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   30/09/2019
 * @version 1.0
 */
class DagannothSupremeBoss(npcId: Int, position: Position = Position(2907, 4448, 0), customVariant: Boolean = false)
    : DagannothBoss(npcId, position, customVariant), AttackProvider {

    init {
        race = MonsterRace.DAGANNOTH
    }

    override fun generateAttack(): BossAttack {
        val attack = BossAttack(this)
        attack.setType(AttackType.RANGED)
        return attack
    }
    override fun getRetreatPolicy() = MonsterRetreatPolicy.NEVER
    override fun attackTypes() = AttackType.MAGIC
    override fun attackRange(type: AttackType) = 7
    override fun maxTargetsHitPerAttack(type: AttackType) = 1
    override fun fetchAttackDuration(type: AttackType?) = 4
    override fun getAttackAnimation(type: AttackType?) = Animation(2855)
    override fun fetchProjectiles(type: AttackType?): Stream<ProjectileTemplate> = ProjectileTemplate
            .builder(475)
            .setSourceOffset(3)
            .setSourceSize(3)
            .setStartHeight(80)
            .setEndHeight(35)
            .setCurve(14)
            .setSpeed(if (asNpc.combat.target != null) (8 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 2).coerceAtMost(30)) else 8)
            .setDelay(40)
            .buildAsStream()
    override fun fetchHits(type: AttackType?) = HitTemplate
            .builder(AttackType.RANGED)
            .setDelay(if (asNpc.combat.target == null) 0 else RangedAttackStrategy.getNPCRangeHitDelay(asNpc, asNpc.combat.target))
            .setDefenceStat(EquipmentBonuses.DEFENCE_RANGE)
            .buildAsStream()
}