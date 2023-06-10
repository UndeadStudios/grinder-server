package com.grinder.net.packet.impl;

import com.grinder.game.World;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.net.packet.*;
import com.grinder.util.Misc;
import com.grinder.util.TextUtil;

import java.util.Optional;

/**
 * This packet listener is called when a player is doing something relative to
 * their friends or ignore list, such as adding or deleting a player from said
 * list.
 *
 * @author relex lawl
 */
public class PlayerRelationPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {
        try {

            final long username;
            final String message;
            final byte[] recompressed;

            if(packetOpcode == PacketConstants.SEND_PM_OPCODE){
                Packet packet = packetReader.getPacket();
                GamePacketReader reader = new GamePacketReader(packet);

                username = reader.getSigned(DataType.LONG);
                int length = packet.getLength() - Long.BYTES;

                byte[] originalCompressed = new byte[length];
                reader.getBytes(originalCompressed);

                String decompressed = TextUtil.decompress(originalCompressed, length);
                decompressed = TextUtil.filterInvalidCharacters(decompressed);
                decompressed = TextUtil.capitalize(decompressed);

                message = decompressed;

                recompressed = new byte[length];
                TextUtil.compress(decompressed, recompressed);

            } else {
                username = packetReader.readLong();
                message = null;
                recompressed = null;
            }

            if (username < 0)
                return;

            switch (packetOpcode) {
                case PacketConstants.ADD_FRIEND_OPCODE:
                    player.getRelations().addFriend(username);
                    break;
                case PacketConstants.ADD_IGNORE_OPCODE:
                    player.getRelations().addIgnore(username);
                    break;
                case PacketConstants.REMOVE_FRIEND_OPCODE:
                    player.getRelations().deleteFriend(username);
                    break;
                case PacketConstants.REMOVE_IGNORE_OPCODE:
                    player.getRelations().deleteIgnore(username);
                    break;
                case PacketConstants.SEND_PM_OPCODE:

                    if (message.isEmpty())
                        return;

                    if (player.isMuted()) {
                        player.sendMessage("You're muted and can't message others.");
                        return;
                    }

                    final Optional<Player> friend = World.findPlayerByName(Misc
                            .formatText(Misc.longToString(username))
                            .replaceAll("_", " ")
                    );

                    /*if (friend.get().getRelations().getIgnoreList().contains(player)) {
                        player.sendMessage("You're being ignored by " + player.getUsername() + " and your messages won't deliver.");
                        return;
                    }

                    if (player.getRelations().getIgnoreList().contains(friend.get())) {
                        player.sendMessage("You're being ignored by " + friend.get().getUsername() + " and your messages won't deliver.");
                        return;
                    }*/

                    if (friend.isPresent()) {
                        if (friend.get().getRelations().isPrivateOff()) {
                            player.sendMessage("That player is currently offline.");
                            return;
                        }
                    }

                    if (friend.isPresent()) {
                        player.getRelations().message(friend.get(), message, recompressed);
                    } else
                        player.sendMessage("That player is currently offline.");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
