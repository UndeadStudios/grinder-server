package com.grinder.game.content.miscellaneous

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.getBoolean
import com.grinder.game.entity.setBoolean
import com.grinder.game.model.NPCActions
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueExpression
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.interfaces.dialogue.firstOption
import com.grinder.game.model.interfaces.dialogue.fourthOption
import com.grinder.game.model.interfaces.dialogue.secondOption
import com.grinder.game.model.interfaces.dialogue.setPostAction
import com.grinder.game.model.interfaces.dialogue.thirdOption
import com.grinder.game.model.item.container.shop.ShopManager
import com.grinder.util.ItemID
import com.grinder.util.NpcID
import com.grinder.util.ShopIdentifiers

/**
 * Created by Kyle Fricilone on Jun 01, 2020.
 */
object NulodionDialogue {

    private val PURCHASE_IDS = intArrayOf(
        ItemID.CANNON_BASE,
        ItemID.CANNON_STAND,
        ItemID.CANNON_BARRELS,
        ItemID.CANNON_FURNACE,
        ItemID.AMMO_MOULD,
        ItemID.INSTRUCTION_MANUAL
    )

    private val RECLAIM_IDS = intArrayOf(
        ItemID.CANNON_BASE,
        ItemID.CANNON_STAND,
        ItemID.CANNON_BARRELS,
        ItemID.CANNON_FURNACE
    )

    init {

        NPCActions.onClick(NpcID.NULODION) { action ->

            if (action.type == NPCActions.ClickAction.Type.FIRST_OPTION) {
                DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                    .setText("Hello.")

                    .add(DialogueType.NPC_STATEMENT)
                    .setNpcChatHead(NpcID.NULODION)
                    .setText("Hello traveller, how's things?")

                    .add(DialogueType.PLAYER_STATEMENT)
                    .setText("Not bad thanks, yourself?")

                    .add(DialogueType.NPC_STATEMENT)
                    .setNpcChatHead(NpcID.NULODION)
                    .setExpression(DialogueExpression.HAPPY)
                    .setText("I'm good, just working hard as usual...")

                    .add(DialogueType.OPTION)
                    .firstOption("I was hoping you might sell me a cannon?", NulodionDialogue::mainFirstOp)
                    .secondOption("Well, take care of yourself then.", NulodionDialogue::mainSecondOp)
                    .thirdOption("I want to know more about the cannon.", NulodionDialogue::mainThirdOp)
                    .fourthOption("I've lost my cannon.", NulodionDialogue::mainFourthOp)

                    .start(action.player)

                return@onClick true
            }

            return@onClick false
        }

    }


    private fun mainFirstOp(player: Player) {
        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
            .setExpression(DialogueExpression.HAPPY)
            .setText("I was hoping you might sell me a cannon?")

            .add(DialogueType.NPC_STATEMENT)
            .setNpcChatHead(NpcID.NULODION)
            .setExpression(DialogueExpression.THINKING)
            .setText("Hmmmmmm...")

            .add(DialogueType.NPC_STATEMENT)
            .setNpcChatHead(NpcID.NULODION)
            .setExpression(DialogueExpression.CALM_2)
            .setText(
                "I shouldn't really, but as you helped us so much, well, I",
                "could sort something out. I'll warn you though, they",
                "don't come cheap!"
            )

            .add(DialogueType.PLAYER_STATEMENT)
            .setExpression(DialogueExpression.HAPPY)
            .setText("How much?")

            .add(DialogueType.NPC_STATEMENT)
            .setNpcChatHead(NpcID.NULODION)
            .setExpression(DialogueExpression.CALM)
            .setText(
                "For the full setup, 2,000,000,000 coins. Or I can sell you",
                "the separate parts... but it'll cost extra!"
            )

            .add(DialogueType.PLAYER_STATEMENT)
            .setExpression(DialogueExpression.SURPRISED)
            .setText("That's not cheap!")

            .add(DialogueType.OPTION)
            .firstOption("Okay, I'll take a cannon please.", NulodionDialogue::subFirstOp)
            .secondOption("Can I look at the separate parts please?", NulodionDialogue::subSecondOp)
            .thirdOption("Sorry, that's too much for me.", NulodionDialogue::subThirdOp)
            .fourthOption("Have you any ammo or instructions to sell?", NulodionDialogue::subFourthOp)

            .start(player)
    }

    private fun mainSecondOp(player: Player) {
        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
            .setText("Well, take care of yourself then.")

            .add(DialogueType.NPC_STATEMENT)
            .setNpcChatHead(NpcID.NULODION)
            .setText("Indeed I will adventurer.")

            .start(player)
    }

    private fun mainThirdOp(player: Player) {
        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
            .setExpression(DialogueExpression.HAPPY)
            .setText("I want to know more about the cannon.")

            .add(DialogueType.NPC_STATEMENT)
            .setNpcChatHead(NpcID.NULODION)
            .setExpression(DialogueExpression.CALM_2)
            .setText(
                "There's only so much I can tell you, adventurer.",
                "We've been working on this little beauty for some time",
                "now."
            )

            .add(DialogueType.PLAYER_STATEMENT)
            .setText("Is it effective?")

            .add(DialogueType.NPC_STATEMENT)
            .setNpcChatHead(NpcID.NULODION)
            .setText(
                "In short bursts it's very effective, the most destructive",
                "weapon to date. The cannon automatically targets",
                "monsters close by. You just have to make the ammo",
                "and let rip."
            )

            .start(player)
    }

    private fun mainFourthOp(player: Player) {
        val reclaimable = player.getBoolean(Attribute.CANNON_RECLAIM_STATUS)

        val bldr = DialogueBuilder(DialogueType.PLAYER_STATEMENT)
            .setExpression(DialogueExpression.SAD)
            .setText("I've lost my cannon.")

            .add(DialogueType.NPC_STATEMENT)
            .setNpcChatHead(NpcID.NULODION)
            .setExpression(DialogueExpression.CALM)
            .setText("That's unfortunate... but don't worry, I can sort you", "out.")

        if (reclaimable) {
            bldr.setPostAction(NulodionDialogue::reclaim)

                .add(DialogueType.NPC_STATEMENT)
                .setNpcChatHead(NpcID.NULODION)
                .setText("Keep that quiet or I'll be in real trouble!")

                .add(DialogueType.PLAYER_STATEMENT)
                .setText("Of course.")
        } else {
            bldr.add(DialogueType.NPC_STATEMENT)
                .setNpcChatHead(NpcID.NULODION)
                .setExpression(DialogueExpression.CALM_2)
                .setText(
                    "Oh dear, I'm only allowed to replace cannons that were",
                    "stolen in action. I'm sorry, but you'll have to buy a",
                    "new set."
                )
        }

        bldr.start(player)
    }

    private fun subFirstOp(player: Player) {
        val enough = player.inventory.getAmount(ItemID.COINS) >= 2_000_000_000

        val bldr = DialogueBuilder(DialogueType.PLAYER_STATEMENT)
            .setText("Okay, I'll take a cannon please.")

            .add(DialogueType.NPC_STATEMENT)
            .setNpcChatHead(NpcID.NULODION)
            .setExpression(DialogueExpression.EVIL)
            .setText("Okay then, but keep it quiet... This thing's top secret!")

        if (enough) {
            bldr.add(DialogueType.STATEMENT)
                .setText("You give the Cannon engineer 2,000,000,000 coins...")
                .setPostAction {
                    it.inventory.delete(ItemID.COINS, 2_000_000_000)
                }

                .add(DialogueType.STATEMENT)
                .setText(
                    "He gives you the four parts that make the cannon, plus",
                    "an ammo mould and an instruction manual."
                )
                .setPostAction(NulodionDialogue::purchase)

                .add(DialogueType.NPC_STATEMENT)
                .setNpcChatHead(NpcID.NULODION)
                .setText("There you go, you be careful with that thing.")

                .add(DialogueType.PLAYER_STATEMENT)
                .setText("Will do, take care mate.")

                .add(DialogueType.NPC_STATEMENT)
                .setNpcChatHead(NpcID.NULODION)
                .setText("Take care adventurer.")

        } else {
            bldr.add(DialogueType.PLAYER_STATEMENT)
                .setExpression(DialogueExpression.SAD)
                .setText("Oops, I don't have enough money.")

                .add(DialogueType.NPC_STATEMENT)
                .setNpcChatHead(NpcID.NULODION)
                .setExpression(DialogueExpression.HAPPY)
                .setText("Sorry, I can't go any lower than that.")
        }

        bldr.start(player)
    }

    private fun subSecondOp(player: Player) {
        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
            .setExpression(DialogueExpression.HAPPY)
            .setText("Can I look at the separate parts please?")

            .add(DialogueType.NPC_STATEMENT)
            .setNpcChatHead(NpcID.NULODION)
            .setText("Of course")
            .setPostAction {
                ShopManager.open(it, ShopIdentifiers.MULTICANNON_PARTS_FOR_SALE)
            }

            .start(player)
    }

    private fun subThirdOp(player: Player) {
        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
            .setExpression(DialogueExpression.SAD)
            .setText("Sorry, that's too much for me.")

            .add(DialogueType.NPC_STATEMENT)
            .setNpcChatHead(NpcID.NULODION)
            .setExpression(DialogueExpression.HAPPY)
            .setText("Fair enough, it's too much for most of us.")

            .start(player)
    }

    private fun subFourthOp(player: Player) {
        DialogueBuilder(DialogueType.PLAYER_STATEMENT)
            .setExpression(DialogueExpression.HAPPY)
            .setText("Have you any ammo or instructions to sell?")

            .add(DialogueType.NPC_STATEMENT)
            .setNpcChatHead(NpcID.NULODION)
            .setText("Of course!")
            .setPostAction {
                ShopManager.open(it, ShopIdentifiers.MULTICANNON_PARTS_FOR_SALE)
            }

            .start(player)
    }

    private fun purchase(player: Player) {
        PURCHASE_IDS.forEach {
            if (player.inventory.canHold(it, 1)) {
                player.inventory.add(it, 1)
            } else {
                player.getBank(0).add(it, 1)
            }
        }
    }

    private fun reclaim(player: Player) {
        RECLAIM_IDS.forEach {
            if (player.inventory.canHold(it, 1)) {
                player.inventory.add(it, 1)
            } else {
                player.getBank(0).add(it, 1)
            }
        }

        player.packetSender.sendMessage("The dwarf gives you a new cannon.")
        player.setBoolean(Attribute.CANNON_RECLAIM_STATUS, false)
    }

}