package com.grinder.game.model.areas.godwars

import com.grinder.game.entity.agent.npc.monster.boss.impl.god.armadyl.FlightKilisa
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.armadyl.FlockleaderGeerin
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.armadyl.KreeArraBoss
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.armadyl.WingmanSkree
import com.grinder.game.model.Boundary
import com.grinder.game.model.Position
import com.grinder.util.NpcID

/**
 * TODO: add documentation
 *
 * gfx 760 for grapple
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/10/2019
 * @version 1.0
 */
class ArmadylChamber : GodChamberArea<KreeArraBoss>(Boundary(2815, 2844, 5296, 5311)) {
    override val god = KreeArraBoss(NpcID.KREEARRA, Position(2828, 5302, 2), true)
    override val minions = arrayOf(
            FlightKilisa(god),
            FlockleaderGeerin(god),
            WingmanSkree(god)
    )
}