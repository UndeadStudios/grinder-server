package com.grinder.game.content.skill.skillable.impl.crafting

import com.grinder.game.content.skill.SkillUtil
import com.grinder.game.content.skill.skillable.ItemCreationSkillable
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Animation
import com.grinder.game.model.AnimationLoop
import com.grinder.game.model.Skill
import com.grinder.game.model.interfaces.syntax.impl.GlassBlowMenuX
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.RequiredItem
import com.grinder.util.ItemID

/**
 * Glass Blowing
 * @author Alex241
 * @since 16/12/2020
 **/

class GlassBlowing {

    companion object {

        const val requiredMoltenGlassAmount = 1
        const val glassBlowingPipe = ItemID.GLASSBLOWING_PIPE
        const val moltenGlass = ItemID.MOLTEN_GLASS
        val BLOW_ANIMATION = Animation(884)

        /**
         * @param [player] The Player Instance
         * @param [usedItem] The Item ID of the Used Item.
         * @param [usedWith] The Item ID of the Item used by the previous Item.
         */
        fun openInterface(player: Player, usedItem: Int, usedWith: Int): Boolean {
            if ((usedItem == glassBlowingPipe && usedWith == moltenGlass) || (usedItem == moltenGlass && usedWith == glassBlowingPipe)) {
                player.packetSender.sendInterface(11462);
                return true
            }

            return false
        }

        fun handleButton(player: Player, buttonId: Int) : Boolean {
            val glassBlowingValues = GlassBlowingData.values()
            glassBlowingValues.forEach { glassBlowingData ->
                when (buttonId) {
                    // Make 1
                    glassBlowingData.buttonId -> {
                        performGlassBlow(player, glassBlowingData, 1)
                        return true
                    }

                    // Make 5
                    glassBlowingData.buttonId - 1 -> {
                        performGlassBlow(player, glassBlowingData, 5)
                        return true
                    }

                    // Make 10
                    glassBlowingData.buttonId - 2 -> {
                        performGlassBlow(player, glassBlowingData, 10)
                        return true
                    }

                    // Make X
                    glassBlowingData.buttonId - 3, 11475 -> {
                        player.enterSyntax = GlassBlowMenuX(glassBlowingData)
                        player.packetSender.sendEnterAmountPrompt("Enter amount:")
                        return true
                    }
                }
            }

            return false
        }

        /**
         * @param [player] The Player Instance
         * @param [glassItem] The Produced Glass Object
         */
        fun performGlassBlow(player: Player, glassItem: GlassBlowingData, amount: Int) {
            player.packetSender.sendInterfaceRemoval()

            if (canCraft(player, glassItem)) {
                val requiredItemList = mutableListOf<RequiredItem>()
                requiredItemList.add(RequiredItem(Item(ItemID.MOLTEN_GLASS, 1), true))

                val skillable = ItemCreationSkillable(requiredItemList, Item(glassItem.itemId), amount, AnimationLoop(BLOW_ANIMATION, 1), glassItem.levelRequired, glassItem.rewardExperience.toInt(), Skill.CRAFTING, "You make a ${glassItem.itemName}", 2)
                SkillUtil.startSkillable(player, skillable)
            }
        }

        /**
         * @param [player] The Player Instance
         * @param [glassItem] The Produced Glass Object
         */
        fun canCraft(player: Player, glassItem: GlassBlowingData) : Boolean {
            // Level Requirement Check
            if (player.skillManager.getCurrentLevel(Skill.CRAFTING) < glassItem.levelRequired) {
                player.packetSender.sendMessage(
                    "You need a Crafting level of at least " + glassItem.levelRequired + " to craft this.", 1000
                )
                return false
            }

            if (!player.inventory.contains(ItemID.MOLTEN_GLASS)) {
                player.packetSender.sendMessage("You need Molten Glass to craft this")
                return false
            }

            return true
        }



    }
}