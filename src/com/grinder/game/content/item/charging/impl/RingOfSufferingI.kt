package com.grinder.game.content.item.charging.impl

import com.grinder.game.content.item.MorphItems.notTransformed
import com.grinder.game.content.item.charging.Charge
import com.grinder.game.content.item.charging.ChargeableDeathPolicy
import com.grinder.game.content.item.charging.ItemChargeable
import com.grinder.game.content.item.charging.impl.RingOfSufferingI.CHARGED
import com.grinder.game.content.item.charging.impl.RingOfSufferingI.UNCHARGED
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.getBoolean
import com.grinder.game.entity.setBoolean
import com.grinder.game.model.*
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.item.AttributableItem
import com.grinder.game.model.item.AttributeKey
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.ItemContainer
import com.grinder.game.model.item.name
import com.grinder.game.model.sound.Sounds
import com.grinder.util.ItemID
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min

/**
 * TODO: find correct dialogue options
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   24/11/2020
 * @version 1.0
 */
@Charge(CHARGED, UNCHARGED)
object RingOfSufferingI : ItemChargeable {

    val CHARGES = AttributeKey("ring_of_suffering_i_charges")

    const val CHARGED = ItemID.RING_OF_SUFFERING_RI_
    const val UNCHARGED = ItemID.RING_OF_SUFFERING_RI_

    const val RING_OF_RECOIL_TO_CHARGES_RATIO = 40
    const val MAX_CHARGES = 100_000

    init {
        onSecondInventoryAction(CHARGED) {
            checkCharges(player, player.inventory[getSlot()])
        }
        onSecondContainerEquipmentAction(CHARGED, UNCHARGED) {
            checkCharges(player, player.equipment[getSlot()])
        }
        onThirdInventoryAction(CHARGED){
            emptyCharges()
        }
        onDropAction(CHARGED) {
            val disabled = player.getBoolean(Attribute.DISABLE_RING_OF_SUFFERING_I_EFFECT)
            val toggleState =  if (disabled) "@gre@on@bla@" else "@red@off@bla@"
            player.sendOptionsKt(
                    "Toggle $toggleState" to {
                        player.setBoolean(Attribute.DISABLE_RING_OF_SUFFERING_I_EFFECT, !disabled)
                        if (disabled) {
                            player.packetSender.sendSound(Sounds.ACTIVATE_CRUSHER)
                        } else {
                            player.packetSender.sendSound(Sounds.DEACTIVATE_CRUSHER)
                        }
                        player.statement("You have toggled $toggleState your ring's effect.")
                    }
                    ,
                    "Empty." to {
                        emptyCharges()
                    },
                    title = "Ring Settings",
                    addCancel = true)
        }
    }

    override fun toChargeItems(item: AttributableItem): Array<Item> {
        val charges = getCharges(item)
        val rings = charges / RING_OF_RECOIL_TO_CHARGES_RATIO
        return if (charges > 0)
            arrayOf(Item(ItemID.RING_OF_RECOIL, rings))
        else
            emptyArray()
    }

    private fun ItemActions.ItemClickAction.emptyCharges() {
        val item = player.inventory[getSlot()]
        if (item is AttributableItem) {
            item.setAttribute(CHARGES, 0)
            player.replaceInventoryItem(item, Item(UNCHARGED, 1))
            player.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)
            player.message("You have emptied your ring from all charges.")
            player.removeInterfaces()
        }
    }

    fun handleRecoil(damage: Int, recoilDamagePercentage: Double, recoilDamageExtra: Int, actor: Player) {
        if (!actor.getBoolean(Attribute.DISABLE_RING_OF_SUFFERING_I_EFFECT)) {
            val ring = actor.equipment.getById(CHARGED)
            if (ring is AttributableItem) {
                val recoilDamage = ceil(damage * recoilDamagePercentage).toInt() + recoilDamageExtra
                val charges = ring.getAttribute(RingOfSufferingI.CHARGES)?:0
                val newCharges = charges - recoilDamage
                if (newCharges <= 0) {
                    actor.replaceEquipmentItem(Item(CHARGED), Item(UNCHARGED))
                    actor.message("Your ${ring.name()} is out of charges.")
                } else
                    ring.setAttribute(CHARGES, newCharges)
            }
        }
    }

    private fun checkCharges(player: Player, item: Item) {
        val charges = getCharges(item)
        if (charges <= 0)
            player.message("Your ring has no charges.")
        else
            player.message("Your ring has $charges charges.")
    }

    override val deathPolicy: ChargeableDeathPolicy
        get() = ChargeableDeathPolicy.DROP_UNCHARGED

    override fun getCharges(item: Item): Int {
        return if(!item.hasAttributes() || item.id != CHARGED) 0
        else item.asAttributable.getAttribute(CHARGES) ?: 0
    }

    override fun decrementCharges(player: Player, item: Item) {
        if(item.hasAttributes()) {
            val charges = item.asAttributable.decrement(CHARGES) ?: 0
            if(charges <= 0)
                player.replaceEquipmentItem(Item(CHARGED), Item(UNCHARGED), 0)
        }
    }

    override fun charge(player: Player, used: Int, with: Int, withSlot: Int): Boolean {
        if (player.getBoolean(
                Attribute.HAS_PENDING_RANDOM_EVENT,
                false
            ) || player.getBoolean(Attribute.HAS_PENDING_RANDOM_EVENT2, false)
        ) return false
        if (player.BLOCK_ALL_BUT_TALKING) return false
        if (player.isInTutorial) return false
        if (player.status === PlayerStatus.AWAY_FROM_KEYBOARD) return false

        if (player.getBoolean(Attribute.HAS_TRIGGER_RANDOM_EVENT, false)) {
            player.sendMessage("Please finish your random event before doing anything else.")
            return false
        }
        if (player.busy()) {
            player.sendMessage("You cannot do that when you are busy.")
            return false;
        }

        if (!player.notTransformed("do this", true, true)) return false

        if (used != ItemID.RING_OF_RECOIL)
            return false

        if (with != CHARGED && with != UNCHARGED)
            return false

        val charges = getCharges(player.inventory[withSlot])
        if (charges >= MAX_CHARGES) {
            player.sendMessage("Your ring already has the full $MAX_CHARGES charges.")
            return true
        }

        val ringsInInventory = player.inventory.getAmount(ItemID.RING_OF_RECOIL)
        val maxChargesFromRings = floor(ringsInInventory * RING_OF_RECOIL_TO_CHARGES_RATIO.toDouble()).toInt()
        val chargesToAdd = min(maxChargesFromRings, MAX_CHARGES-charges)

        if (chargesToAdd <= 0)
            return true

        if(player.removeInventoryItem(Item(ItemID.RING_OF_RECOIL, chargesToAdd / RING_OF_RECOIL_TO_CHARGES_RATIO), 0)) {
            player.message("You charge your ring with $chargesToAdd charges.")
            player.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)
            charge(player, chargesToAdd, player.inventory, withSlot)
            return true
        }
        return false
    }

    private fun charge(target: Player, charges: Int, container: ItemContainer, slot: Int) {

        val ring = container[slot]?:return

        if(ring.id == UNCHARGED){
            val charged = AttributableItem(CHARGED, 1)
            charged.setAttribute(CHARGES, charges)
            container.replace(ring, charged)
            container.refreshItems()
        } else if(ring is AttributableItem && ring.id == CHARGED){
            val oldCharges = getCharges(ring)
            if(oldCharges >= MAX_CHARGES){
                target.message("Your ring is already fully charged.")
                return
            }
            ring.setAttribute(CHARGES, min(oldCharges + charges, MAX_CHARGES))
        }
    }
}