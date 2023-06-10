package com.grinder.game.entity.agent.npc.monster.boss.impl.god.armadyl

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.Attack
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.attack.strategy.RangedAttackStrategy
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
 * https://oldschool.runescape.wiki/w/Flockleader_Geerin
 *
 * "Flockleader Geerin is one of the Aviansie bodyguards of Kree'arra,
 * along with Flight Kilisa and Wingman Skree.
 * She uses Ranged attacks with a max hit of 25 hitpoints.
 * When fighting Kree'arra, it is very unlikely Flockleader Geerin will do any damage at all,
 * as most players use the Protect from Missiles prayer to defend from Kree'arra."
 *
 * @see FlightKilisa
 * @see WingmanSkree
 * @see KreeArraBoss
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/10/2019
 * @version 1.0
 */
class FlockleaderGeerin(bossNPC: KreeArraBoss): GodMinion<KreeArraBoss>
(
        bossNPC,
        NpcID.FLOCKLEADER_GEERIN,
        Position(2835, 5300, bossNPC.position.z)
) {

    init {
        race = MonsterRace.AVIANSIE
    }

    private val strategy = Strategy(Provider(this))

    override fun getAttackStrategy() = strategy

    class Strategy(provider: AttackProvider) : Attack<FlockleaderGeerin>(provider) {
        override fun requiredDistance(actor: Agent) = 17
        override fun type() = AttackType.RANGED
    }

    class Provider(val asNpc: NPC) : AttackProvider {

        override fun fetchAttackDuration(type: AttackType?) = 5
        override fun getAttackAnimation(type: AttackType?) = Animation(6956)
        override fun fetchProjectiles(type: AttackType?): Stream<ProjectileTemplate> = ProjectileTemplate
                .builder(1192)
                .setSourceOffset(1)
                .setStartHeight(80)
                .setEndHeight(43)
                .setCurve(3)
                .setSpeed(if (asNpc.combat.target != null) (10 + (DistanceUtil.getChebyshevDistance(asNpc.position, asNpc.combat.target.position) * 2).coerceAtMost(30)) else 10)
                .setDelay(55)
                .buildAsStream()
        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(AttackType.RANGED)
                .setDelay(if (asNpc.combat.target == null) 0 else RangedAttackStrategy.getNPCRangeHitDelay(asNpc, asNpc.combat.target))
                .buildAsStream()
    }
}