package com.grinder.game.entity.agent.player

class PlayerChatSettings {

    /**
     * The array of modes' statuses:
     * 0 = Game
     * 1 = Public
     * 2 = Private
     * 3 = Clan
     * 4 = Requests
     * 5 = yell
     */
    var modes = IntArray(6)

    fun onLogin(player: Player): PlayerChatSettings {
        player.packetSender.sendChatOptions(modes)
        return this
    }
}