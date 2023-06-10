package com.grinder.game.model.commands.impl

import com.grinder.game.content.skill.skillable.impl.hunter_new.catching.Impling
import com.grinder.game.content.skill.skillable.impl.hunter_new.traps.bird.BirdSnare
import com.grinder.game.content.skill.skillable.impl.hunter_new.traps.box.BoxTrap
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.sendOptionsKt
import com.grinder.game.entity.agent.player.statement
import com.grinder.game.model.ButtonActions
import com.grinder.game.model.commands.DeveloperCommand
import com.grinder.net.codec.database.impl.LookUpPlayerPurchase
import com.grinder.net.codec.database.impl.LookUpPlayerVote

/**
 * A [DeveloperCommand] that can be used to enable/disable
 * buttons and objects.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   23/04/2020
 * @version 1.0
 */
class ToggleCommand : DeveloperCommand() {

    override fun getDescription() = "Can be used to enable/disable functions/buttons/objects."

    override fun execute(player: Player, command: String?, parts: Array<out String>?) {

        player.sendOptionsKt(
                "Store." to {
                    LookUpPlayerPurchase.ENABLED.set(LookUpPlayerPurchase.ENABLED.get().not())
                    player.statement("You "+(if(LookUpPlayerPurchase.ENABLED.get()) "Enabled" else "Disabled" + " purchase claiming."))
                },
                "Voting." to {
                    LookUpPlayerVote.ENABLED.set(LookUpPlayerVote.ENABLED.get().not())
                    player.statement("You "+(if(LookUpPlayerPurchase.ENABLED.get()) "Enabled" else "Disabled" + " vote claiming."))
                },
                "Button." to {
                    player.requestInput(Integer::class.java, "Enter button id") {
                        ButtonActions.toggleClickable(it?.toInt()?:-1)
                    }
                },
                "Box traps." to {
                    BoxTrap.ENABLED = !BoxTrap.ENABLED
                    player.statement("You "+(if(BoxTrap.ENABLED) "Enabled" else "Disabled")+" box traps.")
                },
                "Bird snares." to {
                    BirdSnare.ENABLED = !BirdSnare.ENABLED
                    player.statement("You "+(if(BirdSnare.ENABLED) "Enabled" else "Disabled")+" bird snares.")
                },
                "Looting imp jars." to {
                    Impling.ENABLED_LOOTING_JARS = !Impling.ENABLED_LOOTING_JARS
                    player.statement("You "+(if(Impling.ENABLED_LOOTING_JARS) "Enabled" else "Disabled")+" bird snares.")
                },
                "Catching imps." to {
                    Impling.ENABLED_CATCHING_IMPS = !Impling.ENABLED_CATCHING_IMPS
                    player.statement("You "+(if(Impling.ENABLED_CATCHING_IMPS) "Enabled" else "Disabled")+" bird snares.")
                }
        )
    }
}