package com.grinder.game.content.item.charging.impl

import com.grinder.game.content.item.MorphItems.notTransformed
import com.grinder.game.content.item.charging.*
import com.grinder.game.content.item.charging.Charge
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


object LadykillerScythe : ItemChargeable {

    val CHARGES = AttributeKey("ladykiller-scythe-charges")

    const val CHARGED = 15933
    const val UNCHARGED = 15931

    const val VIAL_OF_BLOOD = 22446

    init {
        onSecondInventoryAction(CHARGED, UNCHARGED) {
            if (getItemId() == UNCHARGED) {
                charge(player, ItemID.BLOOD_RUNE, getItemId(), getSlot())
            } else {
                ChargeablesUtil.createDropDialogue(ScytheOfVitur,
                        ChargeableDropPolicy.KEEP_IN_INVENTORY, UNCHARGED,
                        getSlot(),
                        player.inventory[getSlot()]
                ).start(player)
            }
        }
        onThirdInventoryAction(CHARGED){
            checkCharges(player, player.inventory[getSlot()])
        }
        onSecondContainerEquipmentAction(CHARGED, UNCHARGED) {
            checkCharges(player, player.equipment[getSlot()])
        }
    }

    override fun toChargeItems(item: AttributableItem): Array<Item> {
        val charges = getCharges(item) / 100
        if (charges <= 0)
            return emptyArray()
        val runes = charges * 300
        return if (charges > 0)
            arrayOf(
                    Item(ItemID.BLOOD_RUNE, runes),
                    Item(VIAL_OF_BLOOD, charges))
        else
            emptyArray()
    }

    private fun checkCharges(player: Player, item: Item) {
        if (item.id == CHARGED) {
            val charges = getCharges(item)
            player.message("Your scythe has ${Misc.format(charges)} charges left powering it.")
        } else
            player.message("Your scythe does not have any more charges.")
    }

    override fun getCharges(item: Item): Int {
        return if (!item.hasAttributes() || item.id != CHARGED) 0
        else item.asAttributable.getAttribute(CHARGES) ?: 0
    }

    override fun decrementCharges(player: Player, item: Item) {
        if (item.hasAttributes()) {
            val charges = item.asAttributable.decrement(CHARGES) ?: 0

            if (charges <= 0) {
                player.equipment.replace(item, Item(UNCHARGED))
                player.sendMessage("Your scythe has run out of charges.")
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

        if (used != ItemID.BLOOD_RUNE && used != VIAL_OF_BLOOD)
            return false

        if (with != UNCHARGED && with != CHARGED)
            return false

        var scythe = player.inventory[withSlot].asAttributable

        if (!player.inventory.contains(VIAL_OF_BLOOD)) {
            player.itemStatement(VIAL_OF_BLOOD, 200, "You need vials of blood to charge the scythe.")
            return true
        }

        if (!player.inventory.contains(ItemID.BLOOD_RUNE)) {
            player.itemStatement(ItemID.BLOOD_RUNE, 200, "You need blood runes to charge the scythe.")
            return true
        }

        val blood = player.inventory.getAmount(VIAL_OF_BLOOD)
        val runes = floorDiv(player.inventory.getAmount(ItemID.BLOOD_RUNE), 300)
        val canAdd = blood.coerceAtMost(runes)
        if(canAdd <= 0) {
            player.message("You need 300 blood runes and 1 vial of blood for 100 charges.")
            return true
        }

        val charges = scythe?.let { getCharges(it) }?:0

        if (charges >= 20_000) {
            player.message("Your scythe is already fully charged.")
            return true
        }

        val availableCharges = floorDiv((20_000 - charges), 100)
        val toAdd = canAdd.coerceAtMost(availableCharges)

        player.removeInventoryItem(Item(ItemID.BLOOD_RUNE, toAdd * 300), -1)
        player.removeInventoryItem(Item(VIAL_OF_BLOOD, toAdd), -1)

        if (scythe == null) {
            scythe = AttributableItem(CHARGED)
            player.inventory[withSlot] = scythe
        }
        player.inventory.refreshItems()

        player.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)

        scythe.setAttribute(CHARGES, (toAdd * 100 + charges).coerceAtMost(20_000))

        player.itemStatement(CHARGED, 200, "Your scythe is now filled with ${Misc.format(getCharges(player.inventory[withSlot]))}</col> charges.")
        return true
    }

    //override fun dropPolicy() = ChargeableDropPolicy.KEEP_IN_INVENTORY

    override val deathPolicy: ChargeableDeathPolicy
        get() = ChargeableDeathPolicy.DROP_UNCHARGED

    override fun dropMessageText(): Optional<String> {
        return Optional.of("You uncharge the scythe.")
    }

    override fun dropDialogueText(): Array<String> {
        return arrayOf(
                "This will uncharge the scythe and any blood runes",
                "and vials of blood used to charge it will not be refunded.")
    }
}