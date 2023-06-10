package com.grinder.game.content.item.charging.impl

import com.grinder.game.content.item.MorphItems.notTransformed
import com.grinder.game.content.item.charging.Charge
import com.grinder.game.content.item.charging.ChargeableDeathPolicy
import com.grinder.game.content.item.charging.ItemChargeable
import com.grinder.game.entity.agent.combat.event.impl.IncomingHitApplied
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerStatus
import com.grinder.game.entity.agent.player.itemStatement
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.getBoolean
import com.grinder.game.model.CombatActions.onEvent
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.item.AttributableItem
import com.grinder.game.model.item.AttributeKey
import com.grinder.game.model.item.Item
import com.grinder.game.model.onDropAction
import com.grinder.game.model.onSecondContainerEquipmentAction
import com.grinder.game.model.onThirdInventoryAction
import com.grinder.game.model.sound.Sounds
import com.grinder.util.ItemID
import com.grinder.util.Misc
import com.grinder.util.oldgrinder.EquipSlot

/**
 * @author FindZach
 *
 * Class that will handle our Magma helmet charging/uncharging
 */
@Charge(MagmaHelmet.CHARGED, MagmaHelmet.UNCHARGED)
object MagmaHelmet : ItemChargeable {

    val CHARGES_KEY = AttributeKey("zulrah-scales")
    val OLD_CHARGES_KEY = AttributeKey("magmahelmet-charges")

    fun renameChargesKey(item: AttributableItem) {
        if (item.attributes.containsKey(OLD_CHARGES_KEY)) {
            item.attributes[CHARGES_KEY] = item.attributes[OLD_CHARGES_KEY]?:0
            item.attributes.remove(OLD_CHARGES_KEY)
        }
    }

    const val CHARGED = ItemID.MAGMA_HELM
    const val UNCHARGED = ItemID.MAGMA_HELM_UNCHARGED_

    init {
        onSecondContainerEquipmentAction(CHARGED) {
            //sendPlayerCharge(player, player.equipment.atSlot(getSlot()))
            player.sendMessage("Your helmet currently has ${Misc.format(getCharges(player.equipment.atSlot(getSlot())))}</col> charges.")

        }

        onThirdInventoryAction(CHARGED) {
            sendPlayerCharge(player, player.inventory.atSlot(getSlot()))
        }

        onDropAction(CHARGED) {
            unchargeHelmet(player, getSlot())
        }

        onEvent(IncomingHitApplied::class) {
                ifActorIsPlayer { player ->
                    val helm = player.equipment.get(EquipSlot.HAT);
                    if (helm != null && getCharges(helm) > 0 && !combatEvent.hit.missed()) { //Degrade when we take damage
                        decrementCharges(player, player.equipment[EquipSlot.HAT])
                    }
                }
        }
    }

    override val deathPolicy: ChargeableDeathPolicy
        get() = ChargeableDeathPolicy.DROP_UNCHARGED

    //According to OSRS Wiki
    private const val MAX_CHARGE = 11_000;

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

        if (used != ItemID.ZULRAHS_SCALES) return false
        if (with != UNCHARGED && with != CHARGED) return false

        var helm = player.inventory[withSlot].asAttributable

        val couldAdd = player.inventory.getById(ItemID.ZULRAHS_SCALES).amount

        if (helm == null) {
            helm = AttributableItem(CHARGED)
            player.inventory[withSlot] = helm
        }

        player.inventory.refreshItems()

        val currAmt = getCharges(player.inventory.atSlot(withSlot))

        val finalDeduction = couldAdd.coerceAtMost(MAX_CHARGE - currAmt)

        if (finalDeduction <= 0) {
            player.itemStatement(CHARGED, 200, "Your helmet is already fully charged.")
            return false;
        }

        player.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)

        helm.setAttribute(CHARGES_KEY, currAmt + finalDeduction)
        player.inventory.delete(used, finalDeduction)

        player.itemStatement(CHARGED, 200, "Your helmet is now filled with ${Misc.format(getCharges(player.inventory.atSlot(withSlot)))}</col> charges.")
        return true
    }

    override fun decrementCharges(player: Player, item: Item) {
        if (item.hasAttributes()) {
            val charges = item.asAttributable.decrement(CHARGES_KEY) ?: 0

            if (charges <= 0) {
                player.equipment.replace(item, Item(UNCHARGED))
                player.equipment.refreshItems()
                player.sendMessage("Your helmet has run out of charges!")
            }
        }
    }

    private fun unchargeHelmet(player: Player, slot: Int) {
        val helmet = player.inventory.get(slot).asAttributable ?: return

        var slotsNeeded = 1
        val scales = helmet.getAttribute(CHARGES_KEY)

        if(player.inventory.contains(ItemID.ZULRAHS_SCALES)) slotsNeeded--

        if(player.inventory.countFreeSlots() < slotsNeeded) {
            player.message("You need $slotsNeeded free inventory spaces before doing this.")
            return
        }

        if(scales != null) {
            player.inventory.add(ItemID.ZULRAHS_SCALES, scales)
        }

        player.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)

        player.inventory.replace(slot, Item(MagmaHelmet.UNCHARGED))
        player.inventory.refreshItems()
        player.itemStatement(UNCHARGED, 200, "You have removed all of the scales from your helmet.")
        //player.message("You have removed all the scales from your helmet.")
    }

    override fun toChargeItems(item: AttributableItem): Array<Item> {
        val charges = getCharges(item)
        if (charges <= 0)
            return emptyArray()
        return if (charges > 0)
            arrayOf(Item(ItemID.ZULRAHS_SCALES, charges))
        else
            emptyArray()
    }

    override fun getCharges(item: Item): Int {
        return if (!item.hasAttributes() || item.id != CHARGED) 0
        else item.asAttributable.getAttribute(CHARGES_KEY) ?: 0
    }

    /**
     * Simple helper function to notify the user their Magma Helms charge
     */
    private fun sendPlayerCharge(player: Player, item: Item) {
        player.message("Your helmet currently has ${Misc.format(getCharges(item))}</col> charges.")

    }
}