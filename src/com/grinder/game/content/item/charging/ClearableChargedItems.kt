package com.grinder.game.content.item.charging

import com.grinder.game.content.item.charging.impl.AncientWyvernShield
import com.grinder.game.content.item.charging.impl.DragonFireShield
import com.grinder.game.content.item.charging.impl.DragonFireWard
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.passedTime
import com.grinder.game.model.Animation
import com.grinder.game.model.Graphic
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.interfaces.dialogue.firstOption
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.name
import com.grinder.game.model.onSecondInventoryAction
import com.grinder.game.model.sound.Sounds
import com.grinder.util.Priority
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

/**
 * Represents charged items that can be emptied through the second option
 * in the menu when right clicking a [charged item][chargedId] in the inventory.
 *
 * TODO: replace item ids with ItemIdentifier references
 * TODO: convert dialogue to use dialogue builder
 *
 * @author Unknown
 */
enum class ClearableChargedItems(
        val chargedId: Int,
        val unChargedId: Int,
        val inventoryUpdateDelay: Int = 0,
        val onUnchargeAction: Consumer<Player>? = null,
        val customUnchargeMessage: String? = null,
        val canUnchargeInCombat: Boolean = true
) {

    NEW_CRYSTAL_SHIELD(4224, 4207),
    CRYSTAL_SHIELD_FULL(4225, 4207),
    CRYSTAL_SHIELD_9(4226, 4207),
    CRYSTAL_SHIELD_8(4227, 4207),
    CRYSTAL_SHIELD_7(4228, 4207),
    CRYSTAL_SHIELD_6(4229, 4207),
    CRYSTAL_SHIELD_5(4230, 4207),
    CRYSTAL_SHIELD_4(4231, 4207),
    CRYSTAL_SHIELD_3(4232, 4207),
    CRYSTAL_SHIELD_2(4233, 4207),
    CRYSTAL_SHIELD_1(4234, 4207),
    NEW_CRYSTAL_SHIELD_I(11759, 4207),
    CRYSTAL_SHIELD_FULL_I(11760, 4207),
    CRYSTAL_SHIELD_9_I(11761, 4207),
    CRYSTAL_SHIELD_8_I(11762, 4207),
    CRYSTAL_SHIELD_7_I(11763, 4207),
    CRYSTAL_SHIELD_6_I(11764, 4207),
    CRYSTAL_SHIELD_5_I(11765, 4207),
    CRYSTAL_SHIELD_4_I(11766, 4207),
    CRYSTAL_SHIELD_3_I(11767, 4207),
    CRYSTAL_SHIELD_2_I(11768, 4207),
    CRYSTAL_SHIELD_1_I(11769, 4207),
    NEW_CRYSTAL_BOW(4212, 4207),
    CRYSTAL_BOW_FULL(4214, 4207),
    CRYSTAL_BOW_9(4215, 4207),
    CRYSTAL_BOW_8(4216, 4207),
    CRYSTAL_BOW_7(4217, 4207),
    CRYSTAL_BOW_6(4218, 4207),
    CRYSTAL_BOW_5(4219, 4207),
    CRYSTAL_BOW_4(4220, 4207),
    CRYSTAL_BOW_3(4221, 4207),
    CRYSTAL_BOW_2(4222, 4207),
    CRYSTAL_BOW_1(4223, 4207),
    NEW_CRYSTAL_BOW_I(11748, 4207),
    CRYSTAL_BOW_FULL_I(11749, 4207),
    CRYSTAL_BOW_9_I(11750, 4207),
    CRYSTAL_BOW_8_I(11751, 4207),
    CRYSTAL_BOW_7_I(11752, 4207),
    CRYSTAL_BOW_6_I(11753, 4207),
    CRYSTAL_BOW_5_I(11754, 4207),
    CRYSTAL_BOW_4_I(11755, 4207),
    CRYSTAL_BOW_3_I(11756, 4207),
    CRYSTAL_BOW_2_I(11757, 4207),
    CRYSTAL_BOW_1_I(11758, 4207),
    SEERS_RING_I(11770, 6731),
    ARCHERS_RING_I(11771, 6733),
    WARRIORS_RING_I(11772, 6735),
    BERSERKER_RING_I(11773, 6737),
    BLACK_MASK_10(8901, 8921),
    BLACK_MASK_9(8903, 8921),
    BLACK_MASK_8(8905, 8921),
    BLACK_MASK_7(8907, 8921),
    BLACK_MASK_6(8909, 8921),
    BLACK_MASK_5(8911, 8921),
    BLACK_MASK_4(8913, 8921),
    BLACK_MASK_3(8915, 8921),
    BLACK_MASK_2(8917, 8921),
    BLACK_MASK_1(8919, 8921),
    BLACK_MASK_10_I(11774, 11784),
    BLACK_MASK_9_I(11775, 11784),
    BLACK_MASK_8_I(11776, 11784),
    BLACK_MASK_7_I(11777, 11784),
    BLACK_MASK_6_I(11778, 11784),
    BLACK_MASK_5_I(11779, 11784),
    BLACK_MASK_4_I(11780, 11784),
    BLACK_MASK_3_I(11781, 11784),
    BLACK_MASK_2_I(11782, 11784),
    BLACK_MASK_1_I(11783, 11784),
    DRAGONFIRE_SHIELD(DragonFireShield.CHARGED, DragonFireShield.UNCHARGED,
            inventoryUpdateDelay = 2,
            onUnchargeAction = Consumer {
                it.performAnimation(Animation(6700, Priority.HIGH))
                it.performGraphic(Graphic(1160, Priority.MEDIUM))
                it.tempBlockMovement(1)
            },
            customUnchargeMessage = "You vent the shield's remaining charges harmlessly into the air.",
            canUnchargeInCombat = false),
    DRAGONFIRE_WARD(DragonFireWard.CHARGED, DragonFireWard.UNCHARGED,
            inventoryUpdateDelay = 2,
            onUnchargeAction = Consumer {
                it.performAnimation(Animation(7873, Priority.HIGH))
                it.performGraphic(Graphic(1160, Priority.MEDIUM))
                it.tempBlockMovement(1)
            },
            // todo: find correct message?
            customUnchargeMessage = "You vent the ward's remaining charges harmlessly into the air.",
            canUnchargeInCombat = false),
    ANCIENT_WYVERN_SHIELD(AncientWyvernShield.CHARGED, AncientWyvernShield.UNCHARGED,
            inventoryUpdateDelay = 2,
            onUnchargeAction = Consumer {
                it.performAnimation(Animation(7700, Priority.HIGH))
                it.performGraphic(Graphic(1396, Priority.MEDIUM))
                it.tempBlockMovement(1)
            },
            customUnchargeMessage = "You vent the shield's remaining charges harmlessly into the air.",
            canUnchargeInCombat = false);

    companion object {


        init {
            for (value in values()) {
                onSecondInventoryAction(value.chargedId) {
                    clearCharges(player, value)
                }
            }
        }

        private fun clearCharges(player: Player, type: ClearableChargedItems) {

            val chargedId = type.chargedId
            val unchargedId = type.unChargedId

            val amt = player.inventory.getAmount(chargedId)

            if (amt <= 0) {
                player.message("You don't have any item in your inventory that you can uncharge.")
                return
            }

            if(!player.passedTime(Attribute.GENERIC_ACTION, 1, TimeUnit.SECONDS, false))
                return

            if (player.busy()) {
                player.message("You can't do that when you're busy.")
                return
            }

            if(!type.canUnchargeInCombat){
                if(player.combat.isInCombat){
                    player.message("You must wait 10 seconds after being out of combat to do this!")
                    return
                }
            }

            DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                    .setItem(chargedId, 200)
                    .setText(
                            "Are you sure you want to uncharge this item?",
                            "Once you do, you won't be able to undo this.")
                    .add(DialogueType.OPTION)
                    .firstOption("Confirm.") {

                        val chargedItem = it.inventory.items.find { item ->
                            //item != null && item.hasAttributes() && item.id == chargedId
                            item != null && item.id == chargedId
                        }

                        if (chargedItem != null) {

                            it.message(type.customUnchargeMessage
                                    ?: "You have uncharged your @dre@${chargedItem.name()}</col>.")
                            it.replaceInventoryItem(
                                    toFind = chargedItem,
                                    toReplace = Item(unchargedId, chargedItem.amount),
                                    updateDelay = type.inventoryUpdateDelay)

                            type.onUnchargeAction?.accept(it)
                        }
                        player.removeInterfaces()
                        player.packetSender.sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND)
                    }
                    .addCancel("Nevermind.")
                    .start(player)
        }
    }
}