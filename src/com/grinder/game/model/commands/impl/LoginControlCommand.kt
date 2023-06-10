package com.grinder.game.model.commands.impl

import com.grinder.game.GameConstants
import com.grinder.game.World
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerUtil
import com.grinder.game.model.commands.DeveloperCommand
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.interfaces.dialogue.firstOption
import com.grinder.game.model.interfaces.dialogue.secondOption
import com.grinder.game.model.interfaces.dialogue.thirdOption
import com.grinder.game.model.interfaces.dialogue.fourthOption
import com.grinder.game.model.interfaces.dialogue.fifthOption
import com.grinder.game.model.interfaces.syntax.EnterSyntax
import com.grinder.game.model.punishment.PunishmentManager
import com.grinder.game.model.punishment.PunishmentType
import java.util.function.IntConsumer

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   05/12/2019
 * @version 1.0
 */
class LoginControlCommand : DeveloperCommand() {

    override fun getSyntax() = ""

    override fun getDescription() = "Tweak bot login settings for extra security."

    override fun execute(player: Player, command: String, parts: Array<out String>?) {
        val optionMenu = DialogueBuilder(DialogueType.OPTION)

        optionMenu
                .firstOption("Edit Spawn Location (${World.startPosition.compactString()})") {
                    val pos = World.startPosition
                    DialogueBuilder(DialogueType.OPTION)
                            .setOptionTitle("Edit Coordinate")
                            .firstOption("X     (${pos.x})") { promptIntSyntax(it, "Spawn-X", IntConsumer { pos.x = it }) }
                            .secondOption("Y     (${pos.y})") { promptIntSyntax(it, "Spawn-Y", IntConsumer { pos.y= it }) }
                            .thirdOption("Z     (${pos.z})") { promptIntSyntax(it, "Spawn-Z", IntConsumer { pos.z = it }) }
                            .fourthOption("X, Y, Z") {
                                it.enterSyntax = object: EnterSyntax {
                                    override fun handleSyntax(player: Player?, input: String?) {
                                        val coordinates = input?.split(",")?.map { it.trim().toIntOrNull() }
                                        if(coordinates != null){
                                            coordinates[0]?.let { pos.x = it }
                                            coordinates[1]?.let { pos.y = it }
                                            coordinates[2]?.let { pos.z = it }
                                            DialogueBuilder(DialogueType.STATEMENT)
                                                    .setText("You set the spawn-position of the npc to ${pos.compactString()}", "This change is not permanent!")
                                                    .start(player?:return)
                                        }
                                    }
                                    override fun handleSyntax(player: Player?, input: Int) {}
                                }
                                it.packetSender.sendEnterInputPrompt("Please enter like this: x, y, z (e.g.: ${pos.compactString()})")
                            }
                            .start(it)
                }.secondOption("Reset Spawn Location") {
                    World.startPosition = GameConstants.DEFAULT_POSITION.clone()
                    player.sendMessage("You have reset the spawn location back to default")
                    player.packetSender.sendInterfaceRemoval();
                }.thirdOption("Ban All At Spawn Location") {
                    if(World.startPosition.sameAs(GameConstants.DEFAULT_POSITION)){
                        DialogueBuilder(DialogueType.STATEMENT)
                                .setText("You can't ban everyone in home!")
                                .start(player)
                        PlayerUtil.broadcastPlayerHighStaffMessage("${it.username} has just attempted to ban everyone at home, please notify lou.")
                        return@thirdOption
                    }
                    player.packetSender.sendInterfaceRemoval()
                    World.players.forEach { other ->
                        if(other != null){
                            if(other.position.isWithinDistance(World.startPosition)){
                                val otherName = other.username
                                val otherChannel = other.session?.channel
                                it.sendMessage("Banned $otherName connected from $otherChannel")
                                PunishmentManager.submit(it, otherName, PunishmentType.MAC_BAN)
                                PunishmentManager.submit(it, otherName, PunishmentType.IP_BAN)
                                PunishmentManager.submit(it, otherName, PunishmentType.BAN)
                                otherChannel?.disconnect()
                            }
                        }
                    }

                }.fourthOption("Lock All New Accounts") {
                    LOCK_ALL_NEW_PLAYERS = !LOCK_ALL_NEW_PLAYERS
                    it.sendMessage("You have set LOCK_ALL_NEW_PLAYERS to $LOCK_ALL_NEW_PLAYERS !")
                    player.packetSender.sendInterfaceRemoval();
                }
                .start(player)
    }
    private fun promptIntSyntax(player: Player, paramName: String, consumer: IntConsumer) {
        player.enterSyntax = object : EnterSyntax {
            override fun handleSyntax(player: Player?, input: String?) {
                input?.toIntOrNull()?.let {
                    handleSyntax(player, it)
                }
            }
            override fun handleSyntax(player: Player?, input: Int) {
                consumer.accept(input)
                DialogueBuilder(DialogueType.STATEMENT)
                        .setText(
                                "You have set the $paramName of the world spawn location to $input",
                                "This change is not permanent!")
                        .start(player?:return)
            }
        }
        player.packetSender.sendEnterInputPrompt("Set the $paramName of the world spawn location (integer values only)!")
    }

    companion object {
        var LOCK_ALL_NEW_PLAYERS = false
    }
}