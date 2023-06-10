package com.grinder.game.model.areas.godwars

import com.grinder.game.entity.agent.npc.monster.boss.impl.god.saradomin.Bree
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.saradomin.CommanderZilyanaBoss
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.saradomin.Growler
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.saradomin.Starlight
import com.grinder.game.model.Boundary
import com.grinder.game.model.Position
import com.grinder.util.NpcID

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/10/2019
 * @version 1.0
 */
class SaradominChamber : GodChamberArea<CommanderZilyanaBoss>(Boundary(2880, 2907, 5255, 5278)) {
    override val god = CommanderZilyanaBoss(NpcID.COMMANDER_ZILYANA, Position(2898, 5265, 0), true)
    override val minions= arrayOf(
            Bree(god),
            Growler(god),
            Starlight(god)
    )
}