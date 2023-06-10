package com.grinder.game.content.miscellaneous.spirittree

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Animation
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.util.Misc
import com.grinder.util.TaskFunctions.delayBy
import java.util.function.Consumer
import org.apache.commons.lang.WordUtils

class SpiritTree {

    companion object {
        const val TELEPORT_ANIMATION = 828

        fun handleTeleports(player: Player, teleportArray: Array<SpiritTreeTeleportData>) {
            DialogueBuilder(DialogueType.STATEMENT).setText("You place your hands on the dry tough bark of the spirit tree, and",  "feel a surge of energy run through your veins.")
                    .add(DialogueType.OPTION)
                    .setOptionTitle("Select an Option")
                    .firstOption(WordUtils.capitalizeFully(teleportArray[0].toString().plus(".").replace("_", " ").capitalize()), Consumer { player2 ->
                    player2.packetSender.sendInterfaceRemoval()
                        player2.forceAnimation(Animation(TELEPORT_ANIMATION))
                        delayBy(1) {
                            player2.moveTo(teleportArray[0].position)
                        }
                    })
                    .secondOption(WordUtils.capitalizeFully(teleportArray[1].toString().plus(".").replace("_", " ").capitalize()), Consumer { player2 ->
                        player2.packetSender.sendInterfaceRemoval()
                        player2.forceAnimation(Animation(TELEPORT_ANIMATION))
                        delayBy(1) {
                            player2.moveTo(teleportArray[1].position)
                        }
                    })
                    .thirdOption(WordUtils.capitalizeFully(teleportArray[2].toString().plus(".").replace("_", " ").capitalize()), Consumer { player2 ->
                        player2.packetSender.sendInterfaceRemoval()
                        player2.forceAnimation(Animation(TELEPORT_ANIMATION))
                        delayBy(1) {
                            player2.moveTo(teleportArray[2].position)
                        }
                    })
                    .fourthOption(WordUtils.capitalizeFully(teleportArray[3].toString().plus(".").replace("_", " ").capitalize()), Consumer { player2 ->
                        player2.packetSender.sendInterfaceRemoval()
                        player2.forceAnimation(Animation(TELEPORT_ANIMATION))
                        delayBy(1) {
                            player2.moveTo(teleportArray[3].position)
                        }
                    }).addCancel("Cancel.")
                    .start(player)
        }
    }
}