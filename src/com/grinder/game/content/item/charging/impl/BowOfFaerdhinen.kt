package com.grinder.game.content.item.charging.impl

import com.grinder.game.content.item.MorphItems.notTransformed
import com.grinder.game.content.item.charging.Charge
import com.grinder.game.content.item.charging.ChargeableDeathPolicy
import com.grinder.game.content.item.charging.ItemChargeable
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterfaces
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses
import com.grinder.game.entity.getBoolean
import com.grinder.game.model.*
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.interfaces.dialogue.firstOption
import com.grinder.game.model.item.AttributableItem
import com.grinder.game.model.item.AttributeKey
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.ItemContainer
import com.grinder.game.model.sound.Sounds
import com.grinder.util.ItemID
import com.grinder.util.Misc

/**
 * @author R-Y-M-R
 * @since 5/27/2022
 * @see <a href="https://www.rune-server.ee/members/necrotic/">RuneServer</a>
 */
@Charge(BowOfFaerdhinen.CHARGED, BowOfFaerdhinen.UNCHARGED)
object BowOfFaerdhinen : ItemChargeable {

    private val CHARGES_KEY = AttributeKey(Attribute.BOW_OF_FAERDHINEN_CHARGES)

    private const val MAX_CHARGES = 20_000
    const val UNCHARGED = ItemID.BOW_OF_FAERDHINEN_INACTIVE
    const val CHARGED = ItemID.BOW_OF_FAERDHINEN
    const val NAME = "Bow of Faerdhinen"

    init {
        onEquipAction(UNCHARGED) {
            player.sendMessage("You must charge your $NAME with Crystal shards before you can use it.")
        }
        onSecondInventoryAction(CHARGED) { // uncharge
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
            decrementCharges(player, player.inventory[getSlot()])
        }
        onThirdInventoryAction(CHARGED) { // check
            check(player, player.inventory, getItemId(), getSlot())
        }

        onSecondContainerEquipmentAction(CHARGED, UNCHARGED) {
            check(player, player.equipment, getItemId(), getSlot())
        }

        // onFourthInventoryAction // should be unchargeBow method. Where is 4th action handler?
        onDropAction(CHARGED) {
            unchargeBow(player, getSlot())
        }
    }

    override val deathPolicy: ChargeableDeathPolicy
        get() = ChargeableDeathPolicy.DROP_UNCHARGED

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
        if (used != BATTERY) return false
        if (with != UNCHARGED && with != CHARGED) return false

        var bow = player.inventory[withSlot].asAttributable
        val chargesInInventory = player.inventory.getById(BATTERY).amount

        if (chargesInInventory < 1) {
            player.sendMessage("You need at least 1 Crystal shard to charge the $NAME.")
            return false
        }

        DialogueBuilder(DialogueType.STATEMENT)
            .setText("You can charge your bow with $CHARGES_PER_BATTERY charges per Crystal shard.",
                "The $NAME can hold up to ${Misc.format(MAX_CHARGES)} charges.",
                "Your shards cannot be retrieved later.",
                "Are you sure you want to charge your Bow?"
            ).add(DialogueType.OPTION)
            .firstOption("Confirm.") {

                player.removeInterfaces()

                var currentCharges = 0
                if (player.inventory[withSlot].hasAttributes())
                    currentCharges = getCharges(player.inventory.atSlot(withSlot))  // The current charges

                val maxCap = MAX_CHARGES - currentCharges                           // The "soft cap", aka how much we can possibly add to the existing charges
                val additionCap : Int = maxCap / CHARGES_PER_BATTERY                // The cap is divided by 100 in this case, as each shard = 100 charges

                player.requestInput(Integer::class.java, "Each shard gives $CHARGES_PER_BATTERY charges. How many shards to use? (Max: ${Misc.format(additionCap)})") { answer ->
                    var toAdd = answer.toInt()
                    if (toAdd < 1) {
                        player.sendMessage("You should add at least 1 charge to it.")
                        return@requestInput
                    }

                    if (toAdd > chargesInInventory)                                 // If the input was greater then the charges we hold
                        toAdd = chargesInInventory                                  // Only consume the charges we have
                    if (toAdd > additionCap)                                        // If we're over the cap
                        toAdd = additionCap                                         // Default to the cap

                    if (bow == null) {
                        bow = AttributableItem(CHARGED)
                        player.inventory[withSlot] = bow
                        player.inventory.refreshItems()
                    }

                    player.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)
                    bow.setAttribute(CHARGES_KEY, toAdd * CHARGES_PER_BATTERY + currentCharges)
                    player.inventory.delete(used, toAdd)
                    player.itemStatement(CHARGED, 200, "Your bow is now filled with ${Misc.format(getCharges(player.inventory.atSlot(withSlot)))}</col> charges.")
                }
            }
            .addCancel("Cancel.")
            .start(player)
        return true
    }

    private fun check(player: Player, container: ItemContainer, itemId: Int, itemSlot: Int) {
        if (itemId == CHARGED) {
            val charges = getCharges(container.get(itemSlot))
            player.message("Your $NAME has ${Misc.format(charges)}</col> charges remaining before it will degrade.")
        } else
            player.message("Your $NAME does not have any more charges.")
    }

    override fun decrementCharges(player: Player, item: Item) {
        if (item.hasAttributes()) {
            val charges = item.asAttributable.decrement(CHARGES_KEY) ?: 0
            if (charges <= 0) {
                player.equipment.replace(item, Item(UNCHARGED))
                WeaponInterfaces.assign(player)
                player.combat.reset(false)
                EquipmentBonuses.update(player)
                player.equipment.refreshItems()
                player.sendMessage(COL+"Your bow has run out of charges!</col>")
            }
        }
    }

    private fun unchargeBow(player: Player, slot: Int) {
        val bow = player.inventory.get(slot).asAttributable ?: return

        DialogueBuilder(DialogueType.STATEMENT)
            .setText("Removing charges from your $NAME will make it tradeable.",
                "All Crystal Shards attached will be lost and charges discarded.",
                "Are you sure you want to uncharge your $NAME?"
            ).add(DialogueType.OPTION)
            .firstOption("Uncharge your $NAME.") {

                player.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)
                player.inventory.replace(bow, Item(UNCHARGED))
                player.inventory.refreshItems()
                player.itemStatement(UNCHARGED, 200, "You have removed all of the charges", "from the $NAME. All Crystal shards", "were lost in the process.")
            }
            .addCancel("Cancel.")
            .start(player)
    }

    override fun toChargeItems(item: AttributableItem): Array<Item> {
        val charges = getCharges(item) / CHARGES_PER_BATTERY
        if (charges <= 0)
            return emptyArray()
        return if (charges > 0)
            arrayOf(Item(ItemID.CRYSTAL_SHARD, charges))
        else
            emptyArray()
    }

    override fun getCharges(item: Item): Int {
        return if (!item.hasAttributes() || item.id != CHARGED) 0
        else item.asAttributable.getAttribute(CHARGES_KEY) ?: 0
    }

    private const val BATTERY = ItemID.CRYSTAL_SHARD
    private const val CHARGES_PER_BATTERY = 100
    private const val COL = "<col=8B008B>"
}