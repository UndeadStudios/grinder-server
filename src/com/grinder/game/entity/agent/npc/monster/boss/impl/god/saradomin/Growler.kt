package com.grinder.game.entity.agent.npc.monster.boss.impl.god.saradomin

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.Attack
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.MagicAttackStrategy
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.GodMinion
import com.grinder.game.model.*
import com.grinder.game.model.projectile.ProjectileTemplate
import com.grinder.util.DistanceUtil
import com.grinder.util.NpcID
import com.grinder.util.Priority
import java.util.*
import java.util.stream.Stream

/**
 * https://oldschool.runescape.wiki/w/Growler
 *
 * "Growler is a bodyguard of Commander Zilyana, along with Starlight and Bree.
 * It attacks using an accurate magic attack which has a max hit of 16."
 *
 * @see Bree
 * @see Starlight
 * @see CommanderZilyanaBoss
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/10/2019
 * @version 1.0
 */
class Growler(bossNPC: CommanderZilyanaBoss): GodMinion<CommanderZilyanaBoss>
(
        bossNPC,
        NpcID.GROWLER,
        Position(2894, 5261, bossNPC.position.z)
) {

    private val strategy = Strategy(Provider(this))

    override fun getAttackStrategy() = strategy

    class Strategy(provider: AttackProvider) : Attack<Growler>(provider) {
        override fun requiredDistance(actor: Agent) = 8
        override fun type() = AttackType.MAGIC
    }

    class Provider(val asNpc: NPC) : AttackProvider {

        override fun fetchAttackDuration(type: AttackType?) = 5
        override fun getAttackAnimation(type: AttackType?) = Animation(7037)
        override fun fetchProjectiles(type: AttackType?): Stream<ProjectileTemplate> = ProjectileTemplate
                .builder(1183)
                .setSourceSize(2)
                .setSourceOffset(12)
                .setStartHeight(20)
                .setEndHeight(20)
                .setCurve(4)
                .setSpeed(if (asNpc.combat.target != null) (18 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 2).coerceAtMost(30)) else 18)
                .setDelay(40)
                .buildAsStream()
        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(AttackType.MAGIC)
                .setDelay(if (asNpc.combat.target == null) 0 else MagicAttackStrategy.getMagicSpellHitDelay(asNpc, asNpc.combat.target))
                .setSuccessOrFailedGraphic(Graphic(1184, GraphicHeight.MIDDLE, Priority.HIGH))
                .buildAsStream()

        override fun fetchAttackGraphic(type: AttackType?) = Optional.of(Graphic(1182, GraphicHeight.MIDDLE, Priority.HIGH))
    }
}