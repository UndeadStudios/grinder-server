package com.grinder.game.content.item.charging.impl

import com.grinder.game.content.item.MorphItems.notTransformed
import com.grinder.game.content.item.charging.Charge
import com.grinder.game.content.item.charging.ChargeableDeathPolicy
import com.grinder.game.content.item.charging.ChargeableDropPolicy
import com.grinder.game.content.item.charging.ItemChargeable
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.getBoolean
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.item.AttributableItem
import com.grinder.game.model.item.AttributeKey
import com.grinder.game.model.item.Item
import com.grinder.game.model.onSecondContainerEquipmentAction
import com.grinder.game.model.onSecondInventoryAction
import com.grinder.game.model.onThirdInventoryAction
import com.grinder.game.model.sound.Sounds
import com.grinder.util.ItemID
import com.grinder.util.Misc
import java.lang.Math.floorDiv
import java.util.*

@Charge(SanguinestiStaff.CHARGED, SanguinestiStaff.UNCHARGED)
object SanguinestiStaff : ItemChargeable {

    val CHARGES = AttributeKey("sang-charges")

    const val CHARGED = 22323
    const val UNCHARGED = 22481

    init {
        onSecondInventoryAction(CHARGED){
            if (!getItem()?.isValid!!) {
                return@onSecondInventoryAction;
            }
            if (!player.inventory.contains(CHARGED)) {
                return@onSecondInventoryAction
            }
            if (getSlot() < 0 || getSlot() > player.inventory.capacity()) return@onSecondInventoryAction

            if (player.inventory.get(getSlot()).id != getItemId()) return@onSecondInventoryAction

            if (player.inventory.get(getSlot()).amount <= 0) {
                return@onSecondInventoryAction
            }

            player.packetSender.sendInterfaceRemoval()
            charge(player, ItemID.BLOOD_RUNE, getItemId(), getSlot())
        }
        onThirdInventoryAction(CHARGED){
            checkCharges(player, player.inventory[getSlot()])
        }
        onThirdInventoryAction(UNCHARGED) {
            if (!getItem()?.isValid!!) {
                return@onThirdInventoryAction;
            }
            if (!player.inventory.contains(UNCHARGED)) {
                return@onThirdInventoryAction
            }
            if (getSlot() < 0 || getSlot() > player.inventory.capacity()) return@onThirdInventoryAction

            if (player.inventory.get(getSlot()).id != getItemId()) return@onThirdInventoryAction

            if (player.inventory.get(getSlot()).amount <= 0) {
                return@onThirdInventoryAction
            }
            player.packetSender.sendInterfaceRemoval()
            charge(player, ItemID.BLOOD_RUNE, getItemId(), getSlot())
        }
        onSecondContainerEquipmentAction(CHARGED, UNCHARGED) {
            checkCharges(player, player.equipment[getSlot()])
        }
    }

    override fun toChargeItems(item: AttributableItem): Array<Item> {
        val charges = getCharges(item)
        val runes = charges * 3
        return if (charges > 0)
            arrayOf(Item(ItemID.BLOOD_RUNE, runes))
        else
            emptyArray()
    }

    private fun checkCharges(player: Player, item: Item) {
        if (item.id == CHARGED) {
            val charges = getCharges(item)
            player.message("Your staff has ${Misc.format(charges)} charges left powering it.")
        } else
            player.message("Your staff does not have any more charges.")
    }

    override fun getCharges(item: Item) : Int {
        return if(!item.hasAttributes() || item.id != CHARGED || item.amount <= 0) 0
        else item.asAttributable.getAttribute(CHARGES) ?: 0
    }

    override fun decrementCharges(player: Player, item: Item) {
        if(item.hasAttributes()) {
            val charges = item.asAttributable.decrement(CHARGES) ?: 0

            if(charges <= 0) {
                player.equipment.replace(item, Item(UNCHARGED))
                player.sendMessage("Your staff has run out of charges.")
            }
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


        if(used != ItemID.BLOOD_RUNE)
            return false

        if(with != UNCHARGED && with != CHARGED)
            return false

        if (!player.inventory.contains(ItemID.BLOOD_RUNE)) {
            player.itemStatement(ItemID.BLOOD_RUNE, 200, "You need blood runes to charge the staff.")
            return true
        }

        var staff = player.inventory[withSlot].asAttributable

/*        if (!staff.isValid) {
            DiscordBot.INSTANCE.sendModMessage("[SERVER]: Tried to charge an invalid existing sanguinesti staff, player: " + player.username + ".")
            return false;
        }*/

        val runes = floorDiv(player.inventory.getAmount(ItemID.BLOOD_RUNE), 3)
        val charges = staff?.let { getCharges(it) }?:0
        val toAdd = (20_000 - charges).coerceAtMost(runes)

        if(toAdd <= 0) {
            if (charges >= 20_000 && (used == ItemID.SANGUINESTI_STAFF || with == ItemID.SANGUINESTI_STAFF))
                player.message("Your staff is already fully charged.")
            else
                player.message("You need to have at least 3 blood runes for one charge.")
            return true
        }

        player.removeInventoryItem(Item(ItemID.BLOOD_RUNE, toAdd * 3), -1)
        player.inventory.refreshItems()

        if(charges >= 20_000) {
            player.message("Your staff is already fully charged.")
            return true
        }
        if(staff == null) {
            staff = AttributableItem(CHARGED)
            player.inventory[withSlot] = staff
            player.inventory.refreshItems()
        }

        staff.increase(CHARGES, toAdd)
        player.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)
        player.inventory.refreshItems()

        player.itemStatement(CHARGED, 200, "Your staff is now filled with ${Misc.format(getCharges(player.inventory[withSlot]))}</col> charges.")
        return true
    }

    override fun dropPolicy() = ChargeableDropPolicy.KEEP_IN_INVENTORY

    override val deathPolicy: ChargeableDeathPolicy
        get() = ChargeableDeathPolicy.DROP_UNCHARGED

    override fun dropMessageText(): Optional<String> {
        return Optional.of("You have retrieved all of the charges from the staff.")
    }

    override fun dropDialogueText(): Array<String> {
        return arrayOf(
                "This will uncharge the staff and recover any",
                "blood runes and vials of blood used to charge it.")
    }

    override fun chargeItemReturnedOnDrop(charges: Int): Optional<Item> {
        return Optional.of(Item(ItemID.BLOOD_RUNE, charges * 3))
    }
}