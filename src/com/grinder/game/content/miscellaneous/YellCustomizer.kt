package com.grinder.game.content.miscellaneous

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerUtil
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.agent.player.openInterface
import com.grinder.game.model.ButtonActions

object YellCustomizer {

    const val INTERFACE_ID = 51100
    const val INPUT_ID = INTERFACE_ID + 26
    const val DISCARD_CHANGES_BUTTON = INTERFACE_ID + 28
    private const val BRACKET_COLOR_BOX_ID = INTERFACE_ID + 15
    private const val BRACKET_SHADOW_COLOR_BOX_ID = INTERFACE_ID + 16
    private const val TITLE_COLOR_BOX_ID = INTERFACE_ID + 17
    private const val TITLE_SHADOW_COLOR_BOX_ID = INTERFACE_ID + 18
    private const val NAME_COLOR_BOX_ID = INTERFACE_ID + 19
    private const val NAME_SHADOW_COLOR_BOX_ID = INTERFACE_ID + 20
    private const val MESSAGE_COLOR_BOX_ID = INTERFACE_ID + 21
    private const val MESSAGE_SHADOW_COLOR_BOX_ID = INTERFACE_ID + 22
    private const val COLOR_OVERLAY_ID = INTERFACE_ID + 32
    private const val TITLE_OVERLAY_ID = INTERFACE_ID + 36

    init {
        ButtonActions.onClick(DISCARD_CHANGES_BUTTON) {
            openInterface(player)
            player.message("Any unsaved changes made have been discarded.")
        }
    }

    @JvmStatic
    fun openInterface(player: Player) {
        val colors = player.yellColors
        player.packetSender.sendStringColour(BRACKET_COLOR_BOX_ID, colors[0])
        player.packetSender.sendStringColour(BRACKET_SHADOW_COLOR_BOX_ID, colors[1])
        player.packetSender.sendStringColour(TITLE_COLOR_BOX_ID, colors[2])
        player.packetSender.sendStringColour(TITLE_SHADOW_COLOR_BOX_ID, colors[3])
        player.packetSender.sendStringColour(NAME_COLOR_BOX_ID, colors[4])
        player.packetSender.sendStringColour(NAME_SHADOW_COLOR_BOX_ID, colors[5])
        player.packetSender.sendStringColour(MESSAGE_COLOR_BOX_ID, colors[6])
        player.packetSender.sendStringColour(MESSAGE_SHADOW_COLOR_BOX_ID, colors[7])
        player.packetSender.sendString(INPUT_ID, player.yellTitle)
        player.packetSender.sendInterfaceDisplayState(COLOR_OVERLAY_ID, PlayerUtil.isMember(player) || PlayerUtil.isHighStaff(player))
        player.packetSender.sendInterfaceDisplayState(TITLE_OVERLAY_ID, PlayerUtil.isMember(player) || PlayerUtil.isHighStaff(player))
        player.openInterface(INTERFACE_ID)
    }
}