package com.grinder.game.model.areas.godwars

import com.grinder.game.entity.agent.npc.monster.boss.impl.god.bandos.GeneralGraardorBoss
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.bandos.SergeantGrimspike
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.bandos.SergeantSteelwill
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.bandos.SergeantStrongstack
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
class BandosChamber : GodChamberArea<GeneralGraardorBoss>(Boundary(2863, 2877, 5349, 5372)) {
    override val god = GeneralGraardorBoss(NpcID.GENERAL_GRAARDOR, Position(2872, 5363, 2), true)
    override val minions = arrayOf(
            SergeantGrimspike(god),
            SergeantSteelwill(god),
            SergeantStrongstack(god)
    )
}