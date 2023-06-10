package com.grinder.net.packet.impl;

import com.grinder.game.content.pvm.ItemDropFinderInterface;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.service.ServiceManager;
import com.grinder.game.service.search.droptable.SearchDropTableRequest;
import com.grinder.game.service.search.droptable.SearchDropTableType;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 30/11/2019
 */
public class SendInputPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {
        final String input = packetReader.readString();
        final int id = packetReader.readInt();

        if (id == ItemDropFinderInterface.INPUT_ID) {
            player.setLastDropTableInputText(input);
            final SearchDropTableType type = player.isDropTablesItemSearch()
                    ? SearchDropTableType.NPC_DROP_TABLE_BY_ITEM
                    : SearchDropTableType.NPC_DROP_TABLE_BY_NAME;
            final SearchDropTableRequest request = new SearchDropTableRequest(player, input, type);
            ServiceManager.INSTANCE.getSearchService().addSearchRequest(request);
        }
    }
}
