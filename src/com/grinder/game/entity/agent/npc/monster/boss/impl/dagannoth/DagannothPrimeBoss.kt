package com.grinder.game.entity.agent.npc.monster.boss.impl.dagannoth

import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.combat.hit.Hit
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.combat.onOutgoingHitApplied
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.retreat.MonsterRetreatPolicy
import com.grinder.game.entity.agent.npc.monster.boss.BossAttack
import com.grinder.game.model.*
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
class DagannothPrimeBoss(npcId: Int, position: Position = Position(2913, 4458, 0), customVariant: Boolean = false)
    : DagannothBoss(npcId, position, customVariant), AttackProvider {

    init {
        race = MonsterRace.DAGANNOTH
        combat.onOutgoingHitApplied {
            target?.performGraphic(Graphic(if(totalDamage > 0) 163 else 85, GraphicHeight.HIGH))
        }
    }

    override fun generateAttack(): BossAttack {
        val attack = BossAttack(this)
        attack.setType(AttackType.MAGIC)
        return attack
    }
    override fun getRetreatPolicy() = MonsterRetreatPolicy.NEVER
    override fun attackTypes() = AttackType.MAGIC
    override fun attackRange(type: AttackType) = 7
    override fun maxTargetsHitPerAttack(type: AttackType) = 1
    override fun fetchAttackDuration(type: AttackType?) = 4
    override fun getAttackAnimation(type: AttackType?) = Animation(2854)
    override fun fetchProjectiles(type: AttackType?): Stream<ProjectileTemplate> = ProjectileTemplate
            .builder(162)
            .setSourceSize(3)
            .setSourceOffset(3)
            .setStartHeight(70)
            .setEndHeight(35)
            .setCurve(5)
            .setSpeed(if (asNpc.combat.target != null) (45 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 2).coerceAtMost(40)) else 45)
            .setDelay(10)
            .buildAsStream()
    override fun fetchHits(type: AttackType?) = HitTemplate
            .builder(AttackType.MAGIC)
            .setDelay(if (asNpc.combat.target == null) 0 else MagicAttackStrategy.getMagicSpellHitDelay(asNpc, asNpc.combat.target))
            .buildAsStream()
}