package com.grinder.game.content.item.charging

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.item.AttributableItem
import com.grinder.game.model.item.Item
import com.grinder.util.ItemID
import java.util.*

interface RevenantEtherChargeable : ItemChargeable {

    fun carriedCharges(player: Player): Int {
        return arrayOf(player.inventory.items, player.equipment.items).flatten().toList()
                .filterNotNull()
                .map { getCharges(it) }
                .sum()
    }

    // revenant ether drop is handled customly in in Chargeables.kt
    override fun toChargeItems(item: AttributableItem): Array<Item> {
        return emptyArray()
    }

    fun name(): String

    override fun dropPolicy() = ChargeableDropPolicy.KEEP_IN_INVENTORY

    override val deathPolicy: ChargeableDeathPolicy
        get() = ChargeableDeathPolicy.DROP_UNCHARGED

    override fun dropMessageText(): Optional<String> {
        return Optional.of("You carefully remove all the ether from the ${name()}.")
    }

    override fun dropDialogueText(): Array<String> {
        return arrayOf(
                "This will uncharge your ${name()}",
                "and return any revenant ether it contains.")
    }

    override fun chargeItemReturnedOnDrop(charges: Int): Optional<Item> {
        return if (charges <= 0)
            Optional.empty()
        else
            Optional.of(Item(ItemID.REVENANT_ETHER, charges))
    }
}