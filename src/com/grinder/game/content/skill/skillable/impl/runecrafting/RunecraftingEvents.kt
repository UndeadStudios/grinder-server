package com.grinder.game.content.skill.skillable.impl.runecrafting

import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler
import com.grinder.game.entity.agent.movement.teleportation.TeleportType
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.ItemActions
import com.grinder.game.model.NPCActions
import com.grinder.game.model.ObjectActions
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.util.NpcID
import com.grinder.util.ObjectID

object RunecraftingEvents {

    init {

        ItemActions.onClick(Talisman.values().map { it.itemId }) {
            if (isDropAction()) { //Allows users to drop items
                return@onClick false
            }

            player.sendDevelopersMessage("Clicking Talisman ${itemActionMessage.opcode}")
            val talisman = Talisman.values().find { it.itemId == getItemId() }!!
            val canTeleport = TeleportHandler.checkReqs(player, talisman.altarPos, true, false, TeleportType.NORMAL)

            if (canTeleport) {
                TeleportHandler.teleport(player, talisman.altarPos, TeleportType.NORMAL, false, false)
                player.message("You use the power of the talisman to transport you to an altar.")
            }

            true
        }

        // Teleport the player to the altar when they click the ruin
        ObjectActions.onClick(Talisman.values().map { it.ruin }) { event ->
            val talisman = Talisman.getTalismanForRuin(event.objectActionMessage.objectId)
            talisman ?: return@onClick

            // Check that the player has a tiara
            if(!event.player.inventory.contains(talisman.tiara)
                    && !event.player.equipment.contains(talisman.tiara)) {
                DialogueBuilder(DialogueType.STATEMENT)
                        .setText("You need a ${talisman.toString().toLowerCase()} tiara.")
                        .start(event.player)
                talisman
            }

            AltarRunecrafting.teleportToAltar(event.player, talisman)
        }

        // Craft runes when the player clicks the altar
        ObjectActions.onClick(Altar.values().map { it.objectId }) { event ->
            val altar = Altar.getAltarForObject(event.objectActionMessage.objectId)
            altar ?: return@onClick

            AltarRunecrafting.craftRunes(event.player, altar)
        }

        // Ouranian altar
        ObjectActions.onClick(ObjectID.RUNECRAFTING_ALTAR) { event ->
            OuraniaAltar.craft(event.player)
            true
        }

        // Ouranian altar banker
        NPCActions.onClick(NpcID.ENIOLA) { event ->
            event.player.bankpin.openBank()
            true
        }
    }

    fun clickTiara(player: Player, itemId : Int) {
        val tiara = Talisman.values().find { it.tiara == itemId }
        tiara ?: return

        val canTeleport = TeleportHandler.checkReqs(player, tiara.altarPos, true, false, TeleportType.NORMAL)
        if (canTeleport) {
            TeleportHandler.teleport(player, tiara.altarPos, TeleportType.NORMAL, false, false)
            player.message("You use the power of the tiara to transport you to an altar.")
        }
    }

}