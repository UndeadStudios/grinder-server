package com.grinder.game.entity.agent.npc.monster.boss.impl.god.saradomin

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.Attack
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.RangedAttackStrategy
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.GodMinion
import com.grinder.game.model.*
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.util.DistanceUtil
import com.grinder.util.NpcID
import java.util.stream.Stream

/**
 * https://oldschool.runescape.wiki/w/Bree
 *
 * "Bree is the centaur bodyguard of Commander Zilyana,
 * commander of Saradomin's Encampment, along with Growler and Starlight.
 * He uses ranged attacks and has a chance to drop the Saradomin sword."
 *
 * @see Growler
 * @see Starlight
 * @see CommanderZilyanaBoss
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/10/2019
 * @version 1.0
 */
class Bree(bossNPC: CommanderZilyanaBoss): GodMinion<CommanderZilyanaBoss>
(
        bossNPC,
        NpcID.BREE,
        Position(2903, 5262, bossNPC.position.z)
) {

    private val strategy = Strategy(Provider(this))

    override fun getAttackStrategy() = strategy

    class Strategy(provider: AttackProvider) : Attack<Bree>(provider) {
        override fun requiredDistance(actor: Agent) = 8
        override fun type() = AttackType.RANGED
    }

    class Provider(val asNpc: NPC) : AttackProvider {

        override fun fetchAttackDuration(type: AttackType?) = 5
        override fun getAttackAnimation(type: AttackType?) = Animation(7026)
        override fun fetchProjectiles(type: AttackType?): Stream<ProjectileTemplate> = ProjectileTemplate
                .builder(1190)
                .setSourceOffset(1)
                .setSourceSize(1)
                .setStartHeight(56)
                .setEndHeight(34)
                .setCurve(10)
                .setSpeed(if (asNpc.combat.target != null) (12 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 3).coerceAtMost(30)) else 12)
                .setDelay(48)
                .buildAsStream()
        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(AttackType.RANGED)
                .setDelay(if (asNpc.combat.target == null) 0 else RangedAttackStrategy.getNPCRangeHitDelay(asNpc, asNpc.combat.target))
                .buildAsStream()
    }
}