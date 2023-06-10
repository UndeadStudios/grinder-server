package com.grinder.game.content.skill.skillable.impl.runecrafting

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.statement
import com.grinder.game.model.Skill
import com.grinder.util.ItemID
import java.lang.Math.abs

object TiaraCrafting {

    /**
     * Create a runecrafting tiara from an empty tiara and a talisman.
     */
    fun makeTiara(player: Player, item: Int, obj: Int) {

        val altar = Altar.values().find { it.talisman == Talisman.getTalismanForRuin(abs(obj) - 53) }
        altar ?: return

        //if (item != ItemID.TIARA) return

        if (!player.inventory.contains((ItemID.TIARA)))
        {
            player.statement("You need an Tiara to bind a tiara here.")
            return;
        }
        if (!player.inventory.contains(altar.talisman.itemId)) {
            player.statement("You need a ${altar.toString().toLowerCase()} talisman to bind a tiara here.")
            return
        }
        /*
        if(altar.rune.canCraft(player.skillManager.getCurrentLevel(Skill.RUNECRAFTING))) {
            player.statement("You need a runecrafting level of ${altar.rune.requiredLevel()} to do that")
            return
        }*///All Tiaras can be crafted at level 1

        player.inventory.delete(ItemID.TIARA, 1)
        player.inventory.delete(altar.talisman.itemId, 1)
        player.inventory.add(altar.talisman.tiara, 1)

        player.skillManager.addExperience(Skill.RUNECRAFTING, altar.rune.exp * 2)
    }

}