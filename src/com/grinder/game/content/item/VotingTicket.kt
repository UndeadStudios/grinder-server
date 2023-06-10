package com.grinder.game.content.item

import com.grinder.game.entity.agent.player.*
import com.grinder.game.model.attribute.AttributeManager
import com.grinder.game.model.interfaces.dialogue.promptRedeemDialogue
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.ItemContainerUtil
import com.grinder.game.model.onFirstInventoryAction
import com.grinder.util.ItemID
import com.grinder.util.Logging

object VotingTicket {

    init {
        onFirstInventoryAction(ItemID.VOTING_TICKET) {
            if (player.notInDangerOrAfkOrBusyOrInteracting()){
                val amount = player.inventory.getAmount(ItemID.VOTING_TICKET)
                if (amount > 0){
                    player.promptRedeemDialogue(Item(ItemID.VOTING_TICKET, amount)) {
                        it.points.increase(AttributeManager.Points.VOTING_POINTS, amount)
                    }
                }
            }
        }
    }

    /**
     * Adds voting tickets to [player]'s inventory.
     */
    @JvmStatic
    fun addVotingTickets(player: Player, value: Int) {
        if (player.inventory.countFreeSlots() > 0) {
            player.addInventoryItem(Item(ItemID.VOTING_TICKET, value))
        } else {
            ItemContainerUtil.dropUnder(player, ItemID.VOTING_TICKET, value)
        }
        player.message("@red@<img=766> You have received $value Voting ticket(s).")
    }

    @JvmStatic
    fun checkVotingTickets(player: Player) {
        val name = player.username
        val inventory = player.inventory
        if (inventory.getAmount(ItemID.VOTING_TICKET) > 50 || inventory.getAmount(15207) > 50) {
            PlayerUtil.broadcastPlayerMediumStaffMessage("<img=750> @red@ $name got 50+ voting tickets/boxes.")
            Logging.log("massvotingtickets", "$name got 50+ voting tickets/boxes in inventory")
        }
    }
}