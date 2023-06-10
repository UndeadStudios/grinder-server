package com.grinder.game.model.commands.impl

import com.grinder.game.definition.ItemDefinition
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.game.entity.agent.player.message
import com.grinder.game.message.impl.InterfaceActionClickMessage
import com.grinder.game.message.impl.ItemContainerActionMessage
import com.grinder.game.model.CommandActions.onCommand
import com.grinder.game.model.ItemActions
import com.grinder.game.model.commands.DeveloperCommand
import com.grinder.game.model.item.Item
import com.grinder.game.model.onFirstInventoryAction
import java.util.*

/**
 * @author Zach S <zach></zach>@findzach.com>
 * @since 12/17/2020
 */
class FindCommand : DeveloperCommand() {

    init {

    }

    override fun execute(player: Player, command: String, parts: Array<String>) {
        val itemName = java.lang.String.join(" ", *Arrays.copyOfRange(parts, 1, parts.size))
        val foundItems: MutableList<Item> = ArrayList()
        for (itemDefinition in ItemDefinition.definitions.values) {
            if (itemDefinition.name.toLowerCase().contains(itemName)) {
                //player.getPacketSender().sendInterfaceItems();
                if (itemDefinition.isNoted) continue
                foundItems.add(Item(itemDefinition.id, 1))
            }
        }
        player.packetSender.sendScrollbarHeight(56303, foundItems.size / 10 * 52 + if (foundItems.size % 10 == 0) 0 else 52)
        player.packetSender.sendInterfaceItems(56304, foundItems)
        player.packetSender.sendInterface(56300)
        player.packetSender.sendString(56305, "@gre@ Searching for @whi@[" + itemName + "] @gre@Found " + foundItems.size + " total results")
    }


    companion object {
        @JvmStatic
        fun handleInterfaceClick(player: Player, message: ItemContainerActionMessage) {
            if (PlayerRights.HIGH_STAFF.contains(player.rights) || player.username.toLowerCase() == "lou"
                    || player.username.toLowerCase() == "3lou 55"
                    || player.username.toLowerCase() == "mod grinder") {

                val itemId = message.itemId;
                val clickType = message.opcode

                var amt = 0;
                when (clickType) {
                    145 -> amt = 1
                    117 -> amt = 5
                    43 -> amt = 10
                    129 -> amt = 100
                    135 -> amt = Int.MAX_VALUE
                }

                player.sendDevelopersMessage("@red@[Dev] Spawning ItemID: $itemId")
                player.inventory.add(Item(itemId, amt))
            }
        }
    }
}