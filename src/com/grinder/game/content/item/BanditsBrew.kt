package com.grinder.game.content.item

import com.grinder.game.entity.agent.player.*
import com.grinder.game.model.Skill
import com.grinder.game.model.consumable.ConsumableUtil
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.interfaces.dialogue.firstOption
import com.grinder.game.model.item.Item
import com.grinder.game.model.onFirstInventoryAction
import com.grinder.util.ItemID

/**
 * Handles the bandit brew potion, rewards drinker with 2M thieving experience.
 *
 * @author 2012
 */
object BanditsBrew {

    init {
        onFirstInventoryAction(ItemID.BANDITS_BREW) {
            if (player.canConsumeDrink(getItemId())){
                DialogueBuilder(DialogueType.STATEMENT)
                        .setText("There is no honour among thieves. They lie,",
                                "they cheat, and steal. Is it one deceiving",
                                "others for his own gains? Or is it one lost",
                                "in denial and illusion in the cycle of hatred?")
                        .add(DialogueType.STATEMENT)
                        .setText("Drinking this brew. You'll gain 2,000,000",
                                "Thieving experience. Is this something you",
                                "are interested in?")
                        .add(DialogueType.OPTION)
                        .firstOption("Can cheat a honest man.") {
                            it.removeInterfaces()
                            if (it.replaceInventoryItem(Item(getItemId()), Item(ItemID.BEER_GLASS))){
                                ConsumableUtil.onDrink(it)
                                it.addExperience(Skill.THIEVING, 2_000_000, true)
                                it.message("As you intoxicate yourself, you hear stories of the past and run towards the future.")
                                it.say("Maybe I shouldn't.. but one more wouldn't hurt..")
                                it.packetSender.sendJinglebitMusic(135, 0)
                                it.combat.reset(false)
                            }
                        }
                        .addCancel("Can't make a fool out of him.")
                        .start(player)
            }
        }
    }
}