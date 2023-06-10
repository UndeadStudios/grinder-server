package com.grinder.game.content.item.charging.impl

import com.grinder.game.content.item.MorphItems.notTransformed
import com.grinder.game.content.item.charging.Charge
import com.grinder.game.content.item.charging.ChargeableDeathPolicy
import com.grinder.game.content.item.charging.RevenantEtherChargeable
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.getBoolean
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.interfaces.dialogue.firstOption
import com.grinder.game.model.item.AttributableItem
import com.grinder.game.model.item.AttributeKey
import com.grinder.game.model.item.Item
import com.grinder.game.model.onSecondContainerEquipmentAction
import com.grinder.game.model.onThirdInventoryAction
import com.grinder.game.model.sound.Sounds
import com.grinder.util.ItemID
import com.grinder.util.Misc

@Charge(ViggorasChainmace.CHARGED, ViggorasChainmace.UNCHARGED)
object ViggorasChainmace : RevenantEtherChargeable {

    val CHARGES = AttributeKey("viggora-charges")

    const val CHARGED = ItemID.VIGGORA_CHAINMACE
    const val UNCHARGED = ItemID.VIGGORA_CHAINMACE_UNCHARGED

    init {
        onThirdInventoryAction(CHARGED, UNCHARGED){
            checkCharges(player, player.inventory[getSlot()])
        }
        onSecondContainerEquipmentAction(CHARGED, UNCHARGED) {
            checkCharges(player, player.equipment[getSlot()])
        }
    }

    override val deathPolicy: ChargeableDeathPolicy
        get() = ChargeableDeathPolicy.DROP_UNCHARGED

    private fun checkCharges(player: Player, item: Item) {
        if (item.id == CHARGED) {
            val charges = getCharges(item)
            player.message("Your chainmace has ${Misc.format(charges)} charges left powering it.")
        } else {
            DialogueBuilder(DialogueType.ITEM_STATEMENT)
                .setItem(UNCHARGED, 200)
                .setText("Dismantle the chainmace for 7,500 ether?", "Are you absolutely sure you want to do this?")
                .add(DialogueType.OPTION)
                .firstOption("Yes.") { player ->
                    player.removeInventoryItem(Item(UNCHARGED, 1), -1)
                    player.addInventoryItem(Item(21820, 7500), 0)
                    player.sendMessage("You have dismantled the chainmace for 7,500 ether.")
                    player.removeInterfaces()
                }
                .addCancel("No.")
                .start(player)
        }
    }

    override fun getCharges(item: Item) : Int {
        return if(!item.hasAttributes() || item.id != CHARGED) 0
        else item.asAttributable.getAttribute(CHARGES) ?: 0
    }

    override fun decrementCharges(player: Player, item: Item){
        if(item.hasAttributes()) {
            val charges = item.asAttributable.decrement(CHARGES) ?: 0

            if(charges <= 0) {
                player.equipment.replace(item, Item(UNCHARGED))
                player.sendMessage("Your chainmace has run out of ether.")
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
        if(used != ItemID.REVENANT_ETHER) return false
        if(with != UNCHARGED && with != CHARGED) return false

        var amount = player.inventory.getAmount(ItemID.REVENANT_ETHER)
        val newCharges = amount + getCharges(player.inventory[withSlot])

        if(newCharges - amount >= 16000) {
            player.sendMessage("Your chainmace is already fully charged.")
            return true
        }

        if (used == CHARGED || with == CHARGED) {
            if(newCharges > 16000) {
                amount = (16000 - (newCharges - amount)).coerceAtLeast(0)
            }
        } else {
            if (newCharges > 17000) {
                amount = (17000 - (newCharges - amount)).coerceAtLeast(0)
            }
        }

        val mace = player.inventory[withSlot].asAttributable

        if(mace == null) {
            if(amount < 1000 && (used == UNCHARGED || with == UNCHARGED)) {
                player.sendMessage("You require at least 1000 revenant ether to activate this weapon.")
                return true
            }
            if (used == UNCHARGED || with == UNCHARGED) {
                amount -= 1000
                player.inventory.delete(ItemID.REVENANT_ETHER, 1000)
            }


            val replace = AttributableItem(CHARGED)
            replace.setAttribute(CHARGES, amount)
            player.inventory.replace(player.inventory[withSlot], replace)
            player.inventory.refreshItems()

            player.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)
            player.sendMessage("You use " + Misc.format(1000) +" ether to activate this weapon.")
            if (amount > 1000) {
                player.sendMessage("You add a further " + Misc.format(amount) +" revenant ether to your weapon, " +
                        "giving it a total of " + Misc.format(amount) +" charges.")
            }
        } else {
            val charges = getCharges(mace)
            if(charges + amount < 1000 && (used == UNCHARGED || with == UNCHARGED)) {
                player.sendMessage("You need at least ${Misc.format(1000 - charges)} ether to activate this weapon.")
                return true
            }

            mace.increase(CHARGES, amount)
            player.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)

            player.sendMessage("You add a further " + Misc.format(amount) +" revenant ether to your weapon, " +
                    "giving it a total of " + Misc.format(amount + charges) +" charges.")
        }

        player.inventory.delete(ItemID.REVENANT_ETHER, amount)
        return true
    }

    override fun name() = "chainmace"
}