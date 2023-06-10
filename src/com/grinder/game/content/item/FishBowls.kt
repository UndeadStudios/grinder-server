package com.grinder.game.content.item

import com.grinder.game.entity.agent.player.*
import com.grinder.game.model.*
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.item.Item
import com.grinder.util.ItemID

/**
 * Handles the fish bowl item interactions.
 */
object FishBowls {

    init {
        onThirdInventoryAction(
                ItemID.FISHBOWL,
                ItemID.FISHBOWL_2) {
            player.replaceInventoryItem(Item(getItemId()), Item(ItemID.EMPTY_FISHBOWL))
        }
        onFirstInventoryAction(
                ItemID.FISHBOWL_3,
                ItemID.FISHBOWL_4,
                ItemID.FISHBOWL_5) {
            onInteractionStart()
            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                    .setText("Good fish. Just keep swimming... swimming... swimming...")
                    .add(DialogueType.STATEMENT)
                    .setText("The fish swims. It is clearly an obedient fish.")
                    .start(player)
            player.performAnimation(Animation(when (getItemId()) {
                ItemID.FISHBOWL_3 -> 2782
                ItemID.FISHBOWL_4 -> 2785
                ItemID.FISHBOWL_5 -> 2788
                else -> return@onFirstInventoryAction
            }))
        }
        onThirdInventoryAction(
                ItemID.FISHBOWL_3,
                ItemID.FISHBOWL_4,
                ItemID.FISHBOWL_5) {
            onInteractionStart()
            val food = Item(ItemID.FISH_FOOD, 1)
            if (!player.removeInventoryItem(food)){
                player.message("You don't have any fish food to feed your fish.")
                return@onThirdInventoryAction
            }
            player.message("You feed your fish")
            player.performAnimation(Animation(when (getItemId()) {
                ItemID.FISHBOWL_3 -> 2781
                ItemID.FISHBOWL_4 -> 2784
                ItemID.FISHBOWL_5 -> 2787
                else -> return@onThirdInventoryAction
            }))
        }
        onEquipAction(
                ItemID.FISHBOWL_3,
                ItemID.FISHBOWL_4,
                ItemID.FISHBOWL_5) {
            onInteractionStart()
            val dialogueText = if (player.appearance.isMale)
                "Jump! 'Cmon girl, Jump!"
            else
                "Jump! 'Cmon boy, Jump!"
            DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                    .setText(dialogueText)
                    .add(DialogueType.STATEMENT)
                    .setText("The fish bumps into the side of the fishbowl.", "Then it swims some more.")
                    .add(DialogueType.PLAYER_STATEMENT)
                    .setText("Good fish...")
                    .start(player)
            player.performAnimation(Animation(when (getItemId()) {
                ItemID.FISHBOWL_3 -> 2780
                ItemID.FISHBOWL_4 -> 2783
                ItemID.FISHBOWL_5 -> 2786
                else -> return@onEquipAction
            }))
        }
    }

    private fun ItemActions.ItemClickAction.onInteractionStart() {
        player.resetInteractions()
        player.removeInterfaces()
    }
}