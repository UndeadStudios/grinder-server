package com.grinder.game.entity.agent.npc.monster.boss.impl.god.saradomin

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.Attack
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.GodMinion
import com.grinder.game.model.Animation
import com.grinder.game.model.Position
import com.grinder.util.NpcID

/**
 * https://oldschool.runescape.wiki/w/Starlight
 *
 * "Starlight the unicorn is a bodyguard of Commander Zilyana, along with Growler and Bree.
 * She uses powerful melee attacks to bring down her enemies, which has a max hit of about 16.
 * She is the strongest known Unicorn in the game."
 *
 * @see Bree
 * @see Growler
 * @see CommanderZilyanaBoss
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/10/2019
 * @version 1.0
 */
class Starlight(bossNPC: CommanderZilyanaBoss): GodMinion<CommanderZilyanaBoss>
(
        bossNPC,
        NpcID.STARLIGHT,
        Position(2896, 5271, bossNPC.position.z)
) {
    private val strategy = Strategy(Provider(this))

    override fun getAttackStrategy() = strategy

    class Strategy(provider: AttackProvider) : Attack<Starlight>(provider) {
        override fun requiredDistance(actor: Agent) = 1
        override fun type() = AttackType.MELEE
    }

    class Provider(val asNpc: NPC) : AttackProvider {

        override fun fetchAttackDuration(type: AttackType?) = 5
        override fun getAttackAnimation(type: AttackType?) = Animation(6376)
        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(AttackType.MELEE)
                .setDelay(0)
                .buildAsStream()
    }
}