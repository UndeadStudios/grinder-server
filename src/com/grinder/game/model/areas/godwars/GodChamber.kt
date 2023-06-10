package com.grinder.game.model.areas.godwars

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.areas.AreaManager
import com.grinder.game.model.attribute.Attribute

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   17/10/2019
 * @version 1.0
 */
enum class GodChamber(val area: GodChamberArea<*>) {

    BANDOS(BandosChamber()),
    ARMADYL(ArmadylChamber()),
    ZAMORAK(ZamorakChamber()),
    SARADOMIN(SaradominChamber());

    companion object {
        @JvmStatic
        fun resetKillCounts(player: Player) {
            player.attributes.reset(Attribute.BANDOS_KILL_COUNT)
            player.attributes.reset(Attribute.ZAMORAK_KILL_COUNT)
            player.attributes.reset(Attribute.SARADOMIN_KILL_COUNT)
            player.attributes.reset(Attribute.ARMADYL_KILL_COUNT)
        }
    }

    open fun requestPlayerCount(player: Player) {
        val message = when (val playerCount = AreaManager.getPlayersInArena(area)) {
            0 -> "There are currently no players in the room!"
            1 -> "There is currently one player in the room!"
            else -> "There are currently $playerCount players in the room!"
        }
        player.packetSender.sendMessage(message, 1000)
    }
}