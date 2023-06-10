package com.grinder.game.content.skill.skillable.impl.runecrafting.abyss

import com.grinder.game.content.quest.QuestManager
import com.grinder.game.content.skill.skillable.impl.runecrafting.OuraniaAltar
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler
import com.grinder.game.entity.agent.movement.teleportation.TeleportType
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.model.Position
import com.grinder.game.model.SkullType
import com.grinder.game.model.interfaces.dialogue.*
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.shop.ShopManager
import com.grinder.util.ItemID
import com.grinder.util.NpcID
import com.grinder.util.ShopIdentifiers

object MageOfZamorak {

    /**
     *  Positions in the Abyss that the player will randomly teleport in to.
     */
    val TELEPORT_POSITIONS = listOf(
        Position(3035, 4855),
        Position(3022, 4847),
        Position(3017, 4816),
        Position(3040, 4810),
        Position(3062, 4817)
    )

    val BRACELETS = mapOf<Int, Int>(
        ItemID.ABYSSAL_BRACELET_1_ to 1,
        ItemID.ABYSSAL_BRACELET_2_ to 2,
        ItemID.ABYSSAL_BRACELET_3_ to 3,
        ItemID.ABYSSAL_BRACELET_4_ to 4,
        ItemID.ABYSSAL_BRACELET_5_ to 5
    )

    fun startDialogue(player: Player, npcId: Int) {
        DialogueBuilder(DialogueType.NPC_STATEMENT)
            .setExpression(DialogueExpression.CALM)
            .setNpcChatHead(NpcID.MAGE_OF_ZAMORAK)
            .setText("What do you want?")
            .add(DialogueType.OPTION)
            .firstOption("Teleport me to the Ourania altar please.") {
                ouraniaTeleport(player, npcId)
            }
            .secondOption("What do you have for sale?") {
                ShopManager.open(player, ShopIdentifiers.RUNECRAFTING_STORE)
            }
            .thirdOption("Teleport me to the Abyss.") {
                abyssTeleport(player, npcId)
            }
            .fourthOption("Teleport me to the essence mine.") {
                mineTeleport(player, npcId)
            }
            .addCancel("Nothing actually.")
            .start(player)
    }

    fun ouraniaTeleport(player: Player, npcId: Int) {
        if(!QuestManager.hasCompletedQuest(player, "Rune Mysteries")) {
            player.sendMessage("You need to complete the quest 'Rune Mysteries' to be able to use this teleport.")
            return
        }
        if (TeleportHandler.checkReqs(player, OuraniaAltar.ALTAR_POSITION, true, true, player.spellbook.teleportType)) {
            TeleportHandler.teleportFromNPC(
                player, OuraniaAltar.ALTAR_POSITION,
                TeleportType.ANCIENT_WIZARD, false, true, npcId
            )
        }
    }

    fun abyssTeleport(player: Player, npcId: Int) {
        if(!QuestManager.hasCompletedQuest(player, "Rune Mysteries")) {
            player.sendMessage("You need to complete the quest 'Rune Mysteries' to be able to use this teleport.")
            return
        }
        val pos = TELEPORT_POSITIONS.random()
        if (TeleportHandler.checkReqs(player, pos, true, true, player.spellbook.teleportType)) {
            TeleportHandler.teleportFromNPC(player, pos, TeleportType.MAGE_OF_ZAMORAK, false, true, npcId)

            if (hasBracelet(player)) {
                useBraceletCharge(player)
                player.message("The Abyssal bracelet you are wearing prevents you from being skulled.")
            } else {
                player.skullType = SkullType.WHITE_SKULL
                player.skullTimer = (300 / 0.8).toInt()
                player.updateAppearance()
            }
        }
    }

    fun mineTeleport(player: Player, npcId: Int) {
        if(!QuestManager.hasCompletedQuest(player, "Rune Mysteries")) {
            player.sendMessage("You need to complete the quest 'Rune Mysteries' to be able to do this.")
            return
        }
        if (TeleportHandler.checkReqs(player, Position(2910, 4832, 0), true, true, player.spellbook.teleportType)) {
            TeleportHandler.teleportFromNPC(
                player, Position(2910, 4832, 0),
                TeleportType.MAGE_OF_ZAMORAK, false, true, npcId
            )
        }
    }

    fun hasBracelet(player: Player) = getBraceletCharge(player) != null

    fun getBraceletCharge(player: Player): Int? {
        return BRACELETS.entries.find { player.equipment.contains(it.key) }?.value
    }

    fun useBraceletCharge(player: Player) {
        val charge = getBraceletCharge(player)!!

        player.inventory.delete(Item(BRACELETS[charge]!!, 1))
        if (charge > 1) {
            player.inventory.add(BRACELETS[charge - 1]!!, 1)
        }
    }
}