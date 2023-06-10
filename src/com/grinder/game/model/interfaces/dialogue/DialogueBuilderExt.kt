package com.grinder.game.model.interfaces.dialogue

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.removeInterfaces
import com.grinder.game.entity.agent.player.removeInventoryItem
import com.grinder.game.entity.passedTime
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.item.Item
import java.util.function.Consumer

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/12/2019
 * @version 1.0
 */

fun DialogueBuilder.firstOption(text: String, consumer: (Player) -> Unit) = firstOption(text, Consumer { consumer.invoke(it) })
fun DialogueBuilder.secondOption(text: String, consumer: (Player) -> Unit) = secondOption(text, Consumer { consumer.invoke(it) })
fun DialogueBuilder.thirdOption(text: String, consumer: (Player) -> Unit) = thirdOption(text, Consumer { consumer.invoke(it) })
fun DialogueBuilder.fourthOption(text: String, consumer: (Player) -> Unit) = fourthOption(text, Consumer { consumer.invoke(it) })
fun DialogueBuilder.fifthOption(text: String, consumer: (Player) -> Unit) = fifthOption(text, Consumer { consumer.invoke(it) })

fun DialogueBuilder.setAction(consumer: (Player) -> Unit) = setAction(Consumer { consumer.invoke(it) })
fun DialogueBuilder.setPostAction(consumer: (Player) -> Unit) = setPostAction(Consumer { consumer.invoke(it) })

fun Player.promptRedeemDialogue(item: Item, consumer: (Player) -> Unit) {
    DialogueBuilder(DialogueType.STATEMENT)
            .setText("Are you use you want to redeem this item?",
                    "Once you do, you wont be able to undo this.")
            .add(DialogueType.OPTION)
            .firstOption("Redeem.") {
                it.removeInterfaces()
                if (it.passedTime(Attribute.GENERIC_ACTION, 1, message = false)){
                    if (it.removeInventoryItem(item)){
                        consumer.invoke(it)
                    }
                }
            }
            .addCancel()
            .start(this)
}
