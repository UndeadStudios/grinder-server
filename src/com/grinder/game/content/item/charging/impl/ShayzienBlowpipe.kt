package com.grinder.game.content.item.charging.impl

import com.grinder.game.content.item.MorphItems.notTransformed
import com.grinder.game.content.item.charging.Charge
import com.grinder.game.content.item.charging.ItemChargeable
import com.grinder.game.definition.ItemDefinition
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerStatus
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.agent.player.removeInterfaces
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
import com.grinder.game.model.sound.Sounds
import com.grinder.util.ItemID
import com.grinder.util.Misc
import com.grinder.util.oldgrinder.EquipSlot
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.min

@Charge(ShayzienBlowpipe.CHARGED, ShayzienBlowpipe.UNCHARGED)
object ShayzienBlowpipe : ItemChargeable {

    const val CHARGED = 15834

    const val UNCHARGED = 15832

    val SCALES = AttributeKey("shayzien-blowpipe-scales")
    val DARTS = AttributeKey("shayzien-blowpipe-DARTS")
    val DARTS_TYPE = AttributeKey("shayzien-blowpipe-darts-type")

    val MAX_CHARGES = 16383

    val ALL_DARTS = listOf(
            ItemID.BRONZE_DART, ItemID.IRON_DART,
            ItemID.STEEL_DART, ItemID.MITHRIL_DART,
            ItemID.ADAMANT_DART, ItemID.RUNE_DART,
            ItemID.DRAGON_DART
    )

    init {
        onSecondInventoryAction(CHARGED, UNCHARGED) {
            if(getItemId() == CHARGED)
                empty(player, getSlot())
            else
                dismantle(player, getSlot())
        }
        onSecondContainerEquipmentAction(CHARGED) {
            check(player, player.equipment[EquipSlot.WEAPON])
        }
        onThirdInventoryAction(CHARGED) {
            check(player, player.inventory[getSlot()])
        }
    }

    override fun toChargeItems(item: AttributableItem): Array<Item> {
        val scales = getScales(item)
        val darts = getDarts(item)
        val dartType = getDartType(item)
        val list = ArrayList<Item>()
        if (scales > 0)
            list.add(Item(ItemID.ZULRAHS_SCALES, scales))
        if (dartType != null && darts > 0)
            list.add(Item(dartType, darts))
        return list.toTypedArray()
    }

    override fun  getCharges(item: Item): Int {
        return if (!item.hasAttributes()) 0
        else min(getScales(item), getDarts(item))
    }

    fun getScales(item: Item) : Int {
        return if (!item.hasAttributes()) 0
        else item.asAttributable.getAttribute(SCALES) ?: 0
    }

    fun getDarts(item: Item) : Int {
        return if (!item.hasAttributes()) 0
        else item.asAttributable.getAttribute(DARTS) ?: 0
    }

    fun getDartType(item: Item): Int? {
        return if (!item.hasAttributes()) null
        else item.asAttributable.getAttribute(DARTS_TYPE)
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
        if (used != ItemID.ZULRAHS_SCALES && !ItemDefinition.forId(used).name.contains("dart"))
            return false

        if (with != CHARGED && with != UNCHARGED)
            return false

        val item = player.inventory[withSlot] ?: return false

        if(used == ItemID.ZULRAHS_SCALES) {
            chargeScales(player, withSlot, player.inventory.getAmount(ItemID.ZULRAHS_SCALES))
            return true
        }

        val dartType = getDartType(item)
        val dartDef = ItemDefinition.forId(used)

        if (dartDef.name.contains("p") && dartDef.name.contains("dart")) {
            player.sendMessage("You can't use that type of dart - the venom doesn't mix with other poisons.")
            return true
        }

        return if(dartType == null || dartType == used) {
            chargeDarts(player, withSlot, player.inventory.getAmount(used), used)
            true
        } else {
            player.sendMessage("Your blowpipe currently contains a different sort of dart.")
            true
        }

    }

    fun chargeDarts(player: Player, slot: Int, charges: Int, dartType: Int) {
        val item = player.inventory[slot]!!
        val blowpipe = if(item.hasAttributes()) item.asAttributable else null
        val current = if(blowpipe == null) 0 else blowpipe.getAttribute(DARTS) ?: 0
        val amount = charges.coerceAtMost(MAX_CHARGES - current)


        if(amount == 0) {
            player.sendMessage("Your blowpipe can't hold any more darts.")
            return
        }


        player.inventory.delete(dartType, amount)

        if(blowpipe == null) {
            val replace = AttributableItem(CHARGED)
            replace.setAttribute(DARTS, amount)
            replace.setAttribute(DARTS_TYPE, dartType)

            player.inventory.replace(item, replace)
            player.inventory.refreshItems()
        } else {
            blowpipe.increase(DARTS, amount)

            if(blowpipe.getAttribute(DARTS_TYPE) == null) {
                blowpipe.setAttribute(DARTS_TYPE, dartType)
            }
        }

        //player.message("You charge your blowpipe with $charges ${Item(dartType).definition.name}s.")
        check(player, player.inventory[slot])

    }

    fun chargeScales(player: Player, slot: Int, charges: Int) {
        val item = player.inventory[slot]!!
        val blowpipe = if(item.hasAttributes()) item.asAttributable else null
        val current = if(blowpipe == null) 0 else blowpipe.getAttribute(SCALES) ?: 0
        val amount = charges.coerceAtMost(MAX_CHARGES - current)
        player.inventory.delete(ItemID.ZULRAHS_SCALES, amount)

        if(amount == 0) {
            player.sendMessage("Your blowpipe can't hold any more scales.")
            return
        }

        if(blowpipe == null) {
            val replace = AttributableItem(CHARGED)
            replace.setAttribute(SCALES, amount)

            player.inventory.replace(item, replace)
            player.inventory.refreshItems()
        } else {
            blowpipe.increase(SCALES, amount)
        }

        //player.message("You charge your blowpipe with $charges scales.")
        check(player, player.inventory[slot])
    }

    fun empty(player: Player, slot: Int) {
        val blowpipe = player.inventory.get(slot).asAttributable ?: return

        var slotsNeeded = 2
        val darts = blowpipe.getAttribute(DARTS)
        val scales = blowpipe.getAttribute(SCALES)
        val dartsType = blowpipe.getAttribute(DARTS_TYPE)

        if(scales == null || player.inventory.contains(ItemID.ZULRAHS_SCALES)) slotsNeeded--
        if(dartsType == null || player.inventory.contains(dartsType)) {
            slotsNeeded--
        }

        if(player.inventory.countFreeSlots() < slotsNeeded) {
            player.message("You need $slotsNeeded free inventory spaces before doing this.")
            return
        }

        if(scales != null) {
            player.inventory.add(ItemID.ZULRAHS_SCALES, scales)
        }

        if(darts != null) {
            player.inventory.add(blowpipe.getAttribute(DARTS_TYPE)!!, darts)
        }

        player.inventory.replace(slot, Item(UNCHARGED))
        player.inventory.refreshItems()

        player.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)

        player.message("You have removed all the scales and darts from your blowpipe.")
    }

    fun getDartsUsed(player: Player) : Optional<Int> {
        val blowpipe = player.equipment[EquipSlot.WEAPON].asAttributable ?: return Optional.empty<Int>()
        val charges = blowpipe.getAttribute(DARTS) ?: return Optional.empty<Int>()
        val dart = blowpipe.getAttribute(DARTS_TYPE) ?: return Optional.empty<Int>()

        return Optional.of(dart)
    }

    override fun decrementCharges(player: Player, item: Item) {
        if (item.hasAttributes()) {
            item.asAttributable.also {
                it.decrement(SCALES)
                it.decrement(DARTS)
            }
        }
    }

    fun dismantle(player: Player, slot: Int) {
        DialogueBuilder(DialogueType.ITEM_STATEMENT)
                .setItem(CHARGED, 200)
                .setText(
                        "This will dismantle the blowpipe into 20,000 Zulrah scales."
                )
                .add(DialogueType.OPTION)
                .firstOption("Dismantle.") {
                    if(player.inventory[slot]?.id != UNCHARGED) return@firstOption
                    player.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)
                    player.inventory.delete(Item(UNCHARGED, 1), slot)
                    player.inventory.add(ItemID.ZULRAHS_SCALES, 20_000)
                    //player.sendMessage("You dismantle the Toxic Blowpipe and receive 20,000 Zulrah scales.")
                    player.removeInterfaces()
                }
                .addCancel("Nevermind.")
                .start(player)
    }

    fun check(player: Player, item: Item) {
        val blowpipe = item.asAttributable ?: return

        val darts = blowpipe.getAttribute(DARTS) ?: 0
        val scales = blowpipe.getAttribute(SCALES) ?: 0
        val type = blowpipe.getAttribute(DARTS_TYPE)
        val name = if(type != null) ItemDefinition.forId(type).name else "None"

        val pct = if(scales > 0) (scales.toDouble() / MAX_CHARGES.toDouble()) * 100.0 else 0.0
        val rounded = String.format("%.1f", pct)
        // good color: 36d90f
        if(type == null) { //18AC02
            player.sendMessage("Darts: <col=186512>$name</col>. Scales: <col=186512>${Misc.format(scales)} ($rounded%)</col>.")
        } else {
            player.sendMessage("Darts: <col=186512>$name x ${Misc.format(darts)}</col>. Scales: <col=186512>${Misc.format(scales)} ($rounded%)</col>.")
        }
        player.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)
    }
}
