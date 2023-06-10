package com.grinder.game.entity.agent.npc.monster.boss.impl.dagannoth

import com.grinder.game.entity.agent.npc.monster.MonsterRace
import com.grinder.game.entity.agent.npc.monster.boss.Boss
import com.grinder.game.model.Position

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   07/11/2019
 * @version 1.0
 */
abstract class DagannothBoss(id: Int, position: Position, val customVariant: Boolean = false) : Boss(id, position) {

    init {
        race = MonsterRace.DAGANNOTH
    }

    override fun respawn() {
        if (!this.customVariant) { // we do not want it to respawn if it is custom.
            super.respawn()
        }
    }
}