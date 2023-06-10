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
import com.grinder.game.model.onSecondInventoryAction
import com.grinder.game.model.onThirdInventoryAction
import com.grinder.util.ItemID
import com.grinder.util.Misc
import com.grinder.util.oldgrinder.EquipSlot

@Charge(EtherBracelet.CHARGED, EtherBracelet.UNCHARGED)
object EtherBracelet : RevenantEtherChargeable {

    // TODO: Wearing the bracelet of ethereum now reduces incoming damage from revenants by 75%.

    val CHARGES = AttributeKey("ether-charges")
    val ACTIVE = AttributeKey("ether-active")

    const val CHARGED = 21816
    const val UNCHARGED = 21817

    init {
        onSecondInventoryAction(CHARGED, UNCHARGED){
            if(getItemId() == CHARGED) {
                if (toggleActive(player.inventory.get(getSlot())))
                    player.message("Your bracelet will now automatically absorb ether from defeated revenants.")
                else
                    player.message("Your bracelet will no longer automatically absorb ether from defeated revenants.")
            } else
                DialogueBuilder(DialogueType.ITEM_STATEMENT)
                        .setItem(UNCHARGED, 200)
                        .setText("Dismantle the bracelet for 250 ether?", "Are you absolutely sure you want to do this?")
                        .add(DialogueType.OPTION)
                        .firstOption("Yes."){ player ->
                            player.removeInventoryItem(Item(UNCHARGED, 1), -1)
                            player.addInventoryItem(Item(21820, 250), 0)
                            player.removeInterfaces()
                        }
                        .addCancel("No.")
                        .start(player)
        }
        onSecondContainerEquipmentAction(CHARGED, UNCHARGED) {
            if(getItemId() == CHARGED)
                check(player, player.equipment.get(EquipSlot.HANDS))
            else
                player.message("Your bracelet does not have any more charges.")
        }
        onThirdInventoryAction(CHARGED, UNCHARGED) {
            if(getItemId() == CHARGED)
                check(player, player.inventory[getSlot()])
            else
                player.message("You must charge your bracelet to toggle absorption.")
        }
    }

    override val deathPolicy: ChargeableDeathPolicy
        get() = ChargeableDeathPolicy.DROP_UNCHARGED

    fun getCharges(player: Player) = getCharges(player.equipment[EquipSlot.HANDS])

    fun isActive(player: Player) = isActive(player.equipment[EquipSlot.HANDS])

    fun isActive(item: Item): Boolean {
        return if(!item.hasAttributes() || item.id != CHARGED) false
        else item.asAttributable.getAttribute(ACTIVE) == 1
    }

    private fun toggleActive(item: Item): Boolean {
        if(!item.hasAttributes() || item.id != CHARGED)
            return false
        item.asAttributable.setAttribute(ACTIVE, if (isActive(item)) 0 else 1)
        return isActive(item)
    }

    override fun getCharges(item: Item) : Int {
        return if(!item.hasAttributes() || item.id != CHARGED) 0
        else item.asAttributable.getAttribute(CHARGES) ?: 0
    }

    override fun decrementCharges(player: Player, item: Item) {
        if(item.hasAttributes()) {
            val charges = item.asAttributable.decrement(CHARGES) ?: 0

            if(charges <= 0) {
                player.equipment.replace(item, Item(UNCHARGED))
                player.sendMessage("Your bracelet has run out of ether.")
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
            player.sendMessage("Your bracelet is already fully charged.")
            return true
        }

        if(newCharges > 16000) {
            amount = (16000 - (newCharges - amount)).coerceAtLeast(0)
        }

        val bracelet = player.inventory[withSlot].asAttributable

        if(bracelet == null) {
            val replace = AttributableItem(CHARGED)
            replace.setAttribute(CHARGES, amount)
            replace.setAttribute(ACTIVE, 1)
            player.inventory.replace(player.inventory[withSlot], replace)
            player.inventory.refreshItems()
        } else {
            bracelet.increase(CHARGES, amount)
        }

        player.inventory.delete(ItemID.REVENANT_ETHER, amount)
        check(player, player.inventory[withSlot])
        return true
    }

    fun handleEtherDrop(player: Player, item: Item): Boolean {
        val bracelet = player.equipment[EquipSlot.HANDS].asAttributable
        val previousCharges = bracelet.getAttribute(CHARGES)?:0

        if(previousCharges == 16_000)
            return false

        val totalNewCharges = previousCharges + item.amount
        val chargesToAdd = if(totalNewCharges > 16_000)
            16_000-previousCharges
        else
            item.amount

        item.amount -= chargesToAdd

        if(chargesToAdd > 0)
            bracelet.setAttribute(CHARGES, previousCharges + chargesToAdd)

        return item.amount <= 0
    }

    private fun check(player: Player, item: Item) {
        val etherBracelet = item.asAttributable ?: return

        val charges = etherBracelet.getAttribute(CHARGES) ?: 0

        val pct = if(charges > 0) (charges.toDouble() / 16000) * 100.0 else 0.0
        val rounded = String.format("%.1f", pct)

        if(isActive(item))
            player.message("The bracelet has ${Misc.format(charges)} <col=186512>($rounded%)</col> charges, it will absorb ether from defeated revenants.")
        else
            player.message("The bracelet has ${Misc.format(charges)} <col=186512>($rounded%)</col> charges, it will not absorb ether from defeated revenants.")
    }

    override fun name() = "bracelet"
}