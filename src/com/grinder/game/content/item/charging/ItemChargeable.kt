package com.grinder.game.content.item.charging

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.item.Item
import java.util.*

interface ItemChargeable : Chargeable {

    override val deathPolicy: ChargeableDeathPolicy
        get() = ChargeableDeathPolicy.DROP_UNCHARGED

    fun charge(player: Player, used: Int, with: Int, withSlot: Int): Boolean

    fun decrementCharges(player: Player, item: Item)
}