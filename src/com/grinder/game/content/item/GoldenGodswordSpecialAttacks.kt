package com.grinder.game.content.item

import com.grinder.game.definition.ItemDefinition
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.playSound
import com.grinder.game.entity.setBoolean
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.sound.Sounds
import com.grinder.util.ItemID

/**
 * @author R-Y-M-R
 * @since 5/31/2022
 * @see <a href="https://www.rune-server.ee/members/necrotic/">RuneServer</a>
 * @version 1
 *
 * A enum containing all data relevant for Golden GS Special Attacks
 */
enum class GoldenGodswordSpecialAttacks(val kitId: Int, val goldenAttribute: String, val gsId : Int, val goldenSpecId : Int) {
    ARMA(ItemID.GOLDEN_ARMADYL_SPECIAL_ATTACK, Attribute.GOLDEN_AGS, ItemID.ARMADYL_GODSWORD, 1747),
    SARA(ItemID.GOLDEN_SARADOMIN_SPECIAL_ATTACK, Attribute.GOLDEN_SGS, ItemID.SARADOMIN_GODSWORD, 1745),
    BANDOS(ItemID.GOLDEN_BANDOS_SPECIAL_ATTACK, Attribute.GOLDEN_BGS, ItemID.BANDOS_GODSWORD, 1748),
    ZAMMY(ItemID.GOLDEN_ZAMORAK_SPECIAL_ATTACK, Attribute.GOLDEN_ZGS, ItemID.ZAMORAK_GODSWORD, 1746);

    companion object Handler {

        /**
         * Handles redemption of GGSA kit
         */
        fun redeem(player : Player, used : Int, usedWith : Int) {
            for (gs in values()) {  // For all the kits
                if (used == gs.kitId && usedWith == gs.gsId) { // If the used ids match the kit ids
                    if (player.attributes.bool(gs.goldenAttribute)) { // Check if a player is using a gs kit twice
                        player.sendMessage("You have already redeemed a ${ItemDefinition.forId(gs.kitId).name}.")
                    } else { // Standard redemption
                        player.inventory.delete(used, 1) // Delete the kit
                        player.setBoolean(gs.goldenAttribute, true) // Set the golden attribute for that GS to true

                        DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER) // build a nice dialogue
                            .setItem(gs.gsId, 200)
                            .setText("You have redeemed the @dre@${ItemDefinition.forId(gs.kitId).name}</col>.",
                            "Your special attacks will now have a vibrant yellow effect!")
                            .start(player)

                        player.playSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND) // play a nice sound

                    }
                    break
                } else if (used == gs.kitId) { // if the kitid matched but the item was not a godsword, tell them to use it on a godsword.
                    player.sendMessage("Try redeeming this with the ${ItemDefinition.forId(gs.gsId).name}.")
                }
            }
        }

        /**
         * Returns true if we have redeemed a GGSA kit
         */
        fun hasGoldenSpec(player : Player, godswordId : Int): Boolean {
            for (gs in values()) {
                if (godswordId == gs.gsId) {
                    return player.attributes.bool(gs.goldenAttribute)
                }
            }
            return false
        }
    }
}