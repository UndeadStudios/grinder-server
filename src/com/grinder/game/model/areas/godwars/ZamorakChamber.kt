package com.grinder.game.model.areas.godwars

import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.zamorak.BalfrugKreeyath
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.zamorak.KrilTsutsarothBoss
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.zamorak.TstanonKarlak
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.zamorak.ZaklnGritch
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.getBoolean
import com.grinder.game.model.Boundary
import com.grinder.game.model.Position
import com.grinder.game.model.attribute.Attribute
import com.grinder.util.ItemID
import com.grinder.util.NpcID

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/10/2019
 * @version 1.0
 */
class ZamorakChamber : GodChamberArea<KrilTsutsarothBoss>(Boundary(2917, 2942, 5317, 5332)) {
    override val god = KrilTsutsarothBoss(NpcID.KRIL_TSUTSAROTH, Position(2927, 5325, 2), true)
    override val minions = arrayOf(
            BalfrugKreeyath(god),
            TstanonKarlak(god),
            ZaklnGritch(god)
    )

    override fun enter(agent: Agent?) {
        if(agent is Player) {
            super.enter(agent)
        }
        if(agent is Player){
            if (!agent.getBoolean(Attribute.CONSUMED_SARADOMIN_LIGHT) && !agent.equipment.contains(ItemID.STAFF_OF_LIGHT)) {
                agent.packetSender.sendWalkableInterface(12414)
            } else {
                agent.packetSender.sendWalkableInterface(-1)
            }
        }
    }

    override fun process(agent: Agent?) {
    }

    override fun leave(agent: Agent?) {
        if(agent is Player){
            super.leave(agent)
            agent.packetSender.sendWalkableInterface(-1)
        }
    }
}