package com.grinder.game.entity.agent.npc.monster.boss.impl.god.armadyl

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.Attack
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.GodMinion
import com.grinder.game.model.Animation
import com.grinder.game.model.Position
import com.grinder.util.NpcID

/**
 * https://oldschool.runescape.wiki/w/Flight_Kilisa
 *
 * "Flight Kilisa is one of the aviansie bodyguards of Kree'arra,
 * along with Wingman Skree and Flockleader Geerin.
 * She uses melee attacks with a max hit of 18 hitpoints."
 *
 * @see FlockleaderGeerin
 * @see WingmanSkree
 * @see KreeArraBoss
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/10/2019
 * @version 1.0
 */
class FlightKilisa(bossNPC: KreeArraBoss): GodMinion<KreeArraBoss>
(
        bossNPC,
        NpcID.FLIGHT_KILISA,
        Position(2833, 5303, bossNPC.position.z)
) {

    init {
        race = MonsterRace.AVIANSIE
    }

    private val strategy = Strategy(Provider(this))

    override fun getAttackStrategy() = strategy

    class Strategy(provider: AttackProvider) : Attack<FlightKilisa>(provider) {
        override fun requiredDistance(actor: Agent) = 1
        override fun type() = AttackType.MELEE
    }

    class Provider(val asNpc: NPC) : AttackProvider {

        override fun fetchAttackDuration(type: AttackType?) = 5
        override fun getAttackAnimation(type: AttackType?) = Animation(6957)
        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(AttackType.MELEE)
                .setDelay(0)
                .buildAsStream()
    }
}