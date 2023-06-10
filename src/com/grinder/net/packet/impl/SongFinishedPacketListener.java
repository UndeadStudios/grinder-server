package com.grinder.net.packet.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;


public class SongFinishedPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {
        //System.out.println("MUSIC SONG FINIHED PLAYING");
    	player.getMusic().playAreaRandomMusic(player);
    }
}
