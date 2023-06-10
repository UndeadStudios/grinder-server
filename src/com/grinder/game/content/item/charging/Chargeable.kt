package com.grinder.game.content.item.charging

import com.grinder.game.model.item.AttributableItem
import com.grinder.game.model.item.Item
import java.util.*

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   09/09/2020
 */
interface Chargeable {

    val deathPolicy : ChargeableDeathPolicy

    fun toChargeItems(item: AttributableItem) : Array<Item>

    fun getCharges(item: Item) : Int

    fun dropPolicy() = ChargeableDropPolicy.DROP_ON_FLOOR

    fun dropMessageText() : Optional<String> = Optional.empty()

    fun dropDialogueText() : Array<String> = arrayOf(
            "You will lose all charges upon dropping.",
            "Are you absolutely sure you want to drop it?")

    fun chargeItemReturnedOnDrop(charges: Int) : Optional<Item> = Optional.empty()
}