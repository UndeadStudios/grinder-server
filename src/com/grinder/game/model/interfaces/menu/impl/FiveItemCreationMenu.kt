package com.grinder.game.model.interfaces.menu.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.interfaces.menu.CreationMenu
import com.grinder.game.model.interfaces.syntax.impl.CreationMenuX

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   14/02/2021
 */
class FiveItemCreationMenu(
    player: Player,
    title: String,
    action: CreationMenuAction,
    private val item1: Pair<Int, String>,
    private val item2: Pair<Int, String>,
    private val item3: Pair<Int, String>,
    private val item4: Pair<Int, String>,
    private val item5: Pair<Int, String>
) : CreationMenu(player, title, action) {

    override fun open(): CreationMenu {
        val packetSender = player.packetSender
        packetSender.sendString(8966, title)

        packetSender.sendInterfaceModel(8941, item1.first, 170)
        packetSender.sendString(8949, item1.second)

        packetSender.sendInterfaceModel(8942, item2.first, 170)
        packetSender.sendString(8953, item2.second)

        packetSender.sendInterfaceModel(8943, item3.first, 170)
        packetSender.sendString(8957, item3.second)

        packetSender.sendInterfaceModel(8944, item4.first, 170)
        packetSender.sendString(8961, item4.second)

        packetSender.sendInterfaceModel(8945, item5.first, 170)
        packetSender.sendString(8965, item5.second)

        packetSender.sendChatboxInterface(8938)
        return this
    }

    override fun handleButton(id: Int): Boolean {
        when (id) {
            8949 -> action.execute(0, item1.first, 1)
            8948 -> action.execute(0, item1.first, 5)
            8947 -> action.execute(0, item1.first, 10)
            8946 -> {
                player.enterSyntax = CreationMenuX(0, item1.first)
                player.packetSender.sendEnterAmountPrompt("Enter amount:")
            }
            8953 -> action.execute(0, item2.first, 1)
            8952 -> action.execute(0, item2.first, 5)
            8951 -> action.execute(0, item2.first, 10)
            8950 -> {
                player.enterSyntax = CreationMenuX(0, item2.first)
                player.packetSender.sendEnterAmountPrompt("Enter amount:")
            }
            8957 -> action.execute(0, item3.first, 1)
            8956 -> action.execute(0, item3.first, 5)
            8955 -> action.execute(0, item3.first, 10)
            8954 -> {
                player.enterSyntax = CreationMenuX(0, item3.first)
                player.packetSender.sendEnterAmountPrompt("Enter amount:")
            }
            8961 -> action.execute(0, item4.first, 1)
            8960 -> action.execute(0, item4.first, 5)
            8959 -> action.execute(0, item4.first, 10)
            8958 -> {
                player.enterSyntax = CreationMenuX(0, item4.first)
                player.packetSender.sendEnterAmountPrompt("Enter amount:")
            }
            8965 -> action.execute(0, item5.first, 1)
            8964 -> action.execute(0, item5.first, 5)
            8963 -> action.execute(0, item5.first, 10)
            8962 -> {
                player.enterSyntax = CreationMenuX(0, item5.first)
                player.packetSender.sendEnterAmountPrompt("Enter amount:")
            }
            else -> return false
        }
        return true
    }


}