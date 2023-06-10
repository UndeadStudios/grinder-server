package com.grinder.game.entity.agent.npc.monster.boss.impl.god.zamorak

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
 * https://oldschool.runescape.wiki/w/Tstanon_Karlak
 *
 * "Tstanon Karlak is one of K'ril Tsutsaroth's bodyguards and
 * can be found in the God Wars Dungeon, along with Zakl'n Gritch and Balfrug Kreeyath.
 * Tstanon attacks with Melee and has a maximum hit of 15. Tstanon is usually not a threat when
 * K'ril is alive since most players have Protect from Melee active."
 *
 * @see ZaklnGritch
 * @see BalfrugKreeyath
 * @see KrilTsutsarothBoss
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/10/2019
 * @version 1.0
 */
class TstanonKarlak(bossNPC: KrilTsutsarothBoss): GodMinion<KrilTsutsarothBoss>
(
        bossNPC,
        NpcID.TSTANON_KARLAK,
        Position(2921, 5328, bossNPC.position.z)
) {

    init {
        race = MonsterRace.DEMON
    }

    private val strategy = Strategy(Provider(this))

    override fun getAttackStrategy() = strategy

    class Strategy(provider: AttackProvider) : Attack<TstanonKarlak>(provider) {
        override fun requiredDistance(actor: Agent) = 1
        override fun type() = AttackType.MELEE
    }

    class Provider(val asNpc: NPC) : AttackProvider {

        override fun fetchAttackDuration(type: AttackType?) = 5
        override fun getAttackAnimation(type: AttackType?) = Animation(4680)
        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(AttackType.MELEE)
                .setDelay(0)
                .buildAsStream()
    }
}