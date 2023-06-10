package com.grinder.net.packet.impl;

import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;

public class ChatSettingsPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {
        @SuppressWarnings("unused")
        int gameMode = packetReader.readByte();

        int publicMode = packetReader.readByte();
        int privateMode = packetReader.readByte();
        int clanMode = packetReader.readByte();
        int tradeMode = packetReader.readByte();
        int yellMode = packetReader.readByte();

        //System.out.println(gameMode + " " + publicMode + " " + privateMode + " " + clanMode + " " + tradeMode + " " + yellMode);

		if (publicMode >= 0 && publicMode < 4)
		    player.getChatSettings().getModes()[1] = publicMode;

        if (privateMode >= 0 && privateMode < 3) {
            if (player.getChatSettings().getModes()[2] != privateMode) { // turn off private chat
                player.getChatSettings().getModes()[2] = privateMode;
                AchievementManager.processFor(AchievementType.HIDING, player);
            }
        }

        if (clanMode >= 0 && clanMode < 3)
            player.getChatSettings().getModes()[3] = clanMode;

        if (tradeMode >= 0 && tradeMode < 3)
            player.getChatSettings().getModes()[4] = tradeMode;

        if (yellMode >= 0 && yellMode < 3)
            player.getChatSettings().getModes()[5] = yellMode;

    }
}
