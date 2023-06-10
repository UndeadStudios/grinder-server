package com.grinder.game.entity.agent.npc.monster.boss.impl.god.bandos

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.combat.attack.Attack
import com.grinder.game.entity.agent.combat.attack.AttackProvider
import com.grinder.game.entity.agent.combat.attack.AttackType
import com.grinder.game.entity.agent.combat.hit.HitTemplate
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.GodMinion
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.model.Animation
import com.grinder.game.model.Position
import com.grinder.util.NpcID

/**
 * https://oldschool.runescape.wiki/w/Sergeant_Strongstack
 *
 * "Sergeant Strongstack is one of the three sergeants fighting for General Graardor,
 * along with Sergeant Grimspike and Sergeant Steelwill.
 * Strongstack uses melee attacks and attacks by drifting
 * to each player in the room assuming there is more than one."
 *
 * @see SergeantSteelwill
 * @see SergeantGrimspike
 * @see GeneralGraardorBoss
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/10/2019
 * @version 1.0
 */
class SergeantStrongstack(bossNPC: GeneralGraardorBoss): GodMinion<GeneralGraardorBoss>
(
        bossNPC,
        NpcID.SERGEANT_STRONGSTACK,
        Position(2867, 5363, bossNPC.position.z)
) {

    init {
        race = MonsterRace.GOBLIN
    }

    private val strategy = Strategy(Provider(this))

    override fun getAttackStrategy() = strategy

    class Strategy(provider: AttackProvider) : Attack<SergeantStrongstack>(provider) {
        override fun requiredDistance(actor: Agent) = 1
        override fun type() = AttackType.MELEE
    }

    class Provider(val asNpc: NPC) : AttackProvider {

        override fun fetchAttackDuration(type: AttackType?) = 5
        override fun getAttackAnimation(type: AttackType?) = Animation(6154)
        override fun fetchHits(type: AttackType?) = HitTemplate
                .builder(AttackType.MELEE)
                .setDelay(0)
                .setDefenceStat(EquipmentBonuses.DEFENCE_CRUSH)
                .buildAsStream()
    }
}