package com.grinder.game.entity.agent.npc.monster.boss.impl.god.armadyl

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.Attack
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.GodMinion
import com.grinder.game.model.*
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.util.DistanceUtil
import com.grinder.util.NpcID
import java.util.stream.Stream

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/10/2019
 * @version 1.0
 */
class WingmanSkree(bossNPC: KreeArraBoss): GodMinion<KreeArraBoss>
(
        bossNPC,
        NpcID.WINGMAN_SKREE,
        Position(2840, 5299, bossNPC.position.z)
) {

    init {
        race = MonsterRace.AVIANSIE
    }

    private val strategy = Strategy(Provider(this))

    override fun getAttackStrategy() = strategy

    class Strategy(provider: AttackProvider) : Attack<WingmanSkree>(provider) {
        override fun requiredDistance(actor: Agent) = 17
        override fun type() = AttackType.MAGIC
    }

    class Provider(val asNpc: NPC) : AttackProvider {

        override fun fetchAttackDuration(type: AttackType?) = 5
        override fun getAttackAnimation(type: AttackType?) = Animation(6955)
        override fun fetchProjectiles(type: AttackType?): Stream<ProjectileTemplate> = ProjectileTemplate
                .builder(1201)
                .setSourceOffset(1)
                .setStartHeight(80)
                .setEndHeight(43)
                .setCurve(3)
                .setSpeed(if (asNpc.combat.target != null) (15 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 2).coerceAtMost(30)) else 15)
                .setDelay(35)
                .buildAsStream()
        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(AttackType.MAGIC)
                .setDelay(if (asNpc.combat.target == null) 0 else MagicAttackStrategy.getMagicSpellHitDelay(asNpc, asNpc.combat.target))
                .buildAsStream()
    }
}