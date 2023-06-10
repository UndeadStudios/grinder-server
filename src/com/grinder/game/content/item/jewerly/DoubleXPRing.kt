package com.grinder.game.content.item.jewerly

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.agent.player.removeEquipmentItem
import com.grinder.game.entity.agent.player.replaceEquipmentItem
import com.grinder.game.model.item.AttributableItem
import com.grinder.game.model.item.AttributeKey
import com.grinder.game.model.item.Item
import com.grinder.game.model.onSecondContainerEquipmentAction
import com.grinder.game.model.onThirdInventoryAction
import com.grinder.util.ItemID
import com.grinder.util.Misc

/**
 * Handles double xp ring mechanics.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   03/09/2020
 */
object DoubleXPRing {

    val CHARGES = AttributeKey("double-xp-ring-charges")

    const val CHARGED = ItemID.RING_OF_CHAROS
    const val MAX_CHARGES = 1000

    init {
        onThirdInventoryAction(CHARGED) {
            checkCharges(player, player.inventory[getSlot()])
        }
        onSecondContainerEquipmentAction(CHARGED) {
            checkCharges(player, player.equipment[getSlot()])
        }
    }

    private fun checkCharges(player: Player,item: Item) {
        if (item.id == CHARGED) {
            val charges = MAX_CHARGES - getCharges(item)
            player.message("Your ring has ${Misc.format(charges)} charges left powering it.")
        } else
            player.message("Your ring does not have any more charges.")
    }

    fun getCharges(item: Item) : Int {
        return if(!item.hasAttributes() || item.id != CHARGED) 0
        else item.asAttributable.getAttribute(CHARGES) ?: 0
    }

    fun has(player: Player) = player.equipment.contains(CHARGED)

    fun use(player: Player): Boolean {
        if(has(player)){
            var ring = player.equipment.getById(CHARGED)
            if (ring !is AttributableItem){
                val attributableRing = AttributableItem(CHARGED)
                player.replaceEquipmentItem(ring, attributableRing, 0)
                ring = attributableRing
            }
            ring.increase(CHARGES, 1)
            val chargesLeft = ring.getAttribute(CHARGES)?:0
            if(chargesLeft >= MAX_CHARGES){
                player.message("Your @dre@Double combat ring</col> has vanished because it has run out of charges.")
                player.removeEquipmentItem(ring, updateDelay = 0)
            }
            return true
        }
        return false
    }
}