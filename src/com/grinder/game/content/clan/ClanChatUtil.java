package com.grinder.game.content.clan;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.bot.BotPlayer;
import com.grinder.net.packet.PacketSender;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.grinder.game.content.clan.ClanChatConstants.*;

/**
 * TODO: add documentation.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-04-22
 */
public class ClanChatUtil {

    static boolean isValidChannelName(final String channelName){
        return channelName != null && !channelName.equals("") && !channelName.equals("null");
    }

    private static void sortMembers(final ClanChat clanChat){

        clanChat.players().sort((o1, o2) -> {
            if (clanChat.getName().equalsIgnoreCase(DEFAULT_CLAN_NAME)) {
                return Integer.compare(o2.getRights().ordinal(), o1.getRights().ordinal());
            } else {
                final ClanChatRank rank1 = clanChat.getRank(o1);
                final ClanChatRank rank2 = clanChat.getRank(o2);
                if (rank1 == null && rank2 == null) return 1;
                if (rank1 == null) return 1;
                else if (rank2 == null) return -1;
                if (rank1.ordinal() == rank2.ordinal()) return 1;
                if (rank1 == ClanChatRank.OWNER) return -1;
                else if (rank2 == ClanChatRank.OWNER) return 1;
                if (rank1.ordinal() > rank2.ordinal()) return -1;
                return 1;
            }
        });

    }

    static void resetInterface(final Player player) {
        final PacketSender packetSender = player.getPacketSender();
        packetSender.sendScrollbarHeight(CLAN_SCROLL_BAR_ID, MAX_SCROLL_HEIGHT);
        packetSender.sendString(CLAN_TITLE_ID, "Clan Chat");
        packetSender.sendString(CLAN_NAME_ID, "Talking in: Not in chat");
        packetSender.sendString(CLAN_OWNER_ID, "Owner: None");
        packetSender.sendString(CLAN_JOIN_ID, "Join Chat");
        packetSender.clearInterfaceText(TAB_MEMBER_ID_START, TAB_MEMBER_ID_START + MAX_MEMBERS);
        packetSender.clearInterfaceText(TAB_MEMBER_ID_END, TAB_MEMBER_ID_END + MAX_MEMBERS);
        packetSender.sendConfig(LOOT_SHARING_CONFIG_ID, 0);
    }

    static void uponJoin(final Player player, final ClanChat clanChat){

        final List<Player> players = clanChat.players();

        for(Player other: players){
            if(other == player)
                continue;
            other.getPacketSender().sendClanMate(player.getLongUsername(), true);
            player.getPacketSender().sendClanMate(other.getLongUsername(), true);
        }
    }

    static void uponLeave(final Player player, final ClanChat clanChat){

        final List<Player> players = clanChat.players();

        for(Player other: players){
            other.getPacketSender().sendClanMate(player.getLongUsername(), false);
            player.getPacketSender().sendClanMate(other.getLongUsername(), false);
        }
    }

    static void updateInterfacesForMembers(final ClanChat clan) {

        sortMembers(clan);

        int memberCount = 0;
        int index = 0;

        final List<Player> players = clan.players();
        final String[] prefixes = new String[players.size()];
        final String[] names = new String[players.size()];

        for (final Player others : players) {
            if (others != null) {
                memberCount++;
                final ClanChatRank rank = clan.getRank(others);
                final int image = rank == null ? -1 : rank.getSpriteId();
                final String prefix = image != -1 ? ("<clan=" + (image) + ">") : "";
                prefixes[index] = prefix;
                names[index] = others.getUsername();
                index++;
            }
        }

        final int scrollMax = Math.max(memberCount * 14 + 2, MAX_SCROLL_HEIGHT);

        for (final Player member : players) {

            if (member != null) {

                boolean isBot = member instanceof BotPlayer;

                final PacketSender packetSender = member.getPacketSender();

                if(!isBot) {

                    if(memberCount < MAX_MEMBERS) {
                        packetSender.clearInterfaceText(TAB_MEMBER_ID_START + memberCount, TAB_MEMBER_ID_START + MAX_MEMBERS);
                        packetSender.clearInterfaceText(TAB_MEMBER_ID_END + memberCount, TAB_MEMBER_ID_END + MAX_MEMBERS);
                    }

                    for (int i = 0; i < memberCount; i++) {
                        packetSender.sendString(TAB_MEMBER_ID_START + i, prefixes[i], true);
                        packetSender.sendString(TAB_MEMBER_ID_END + i, names[i], true);
                    }

                    packetSender.sendScrollbarHeight(CLAN_SCROLL_BAR_ID, scrollMax);
                    packetSender.sendString(CLAN_TITLE_ID, "Clan Chat", true);

                    final ClanChatRank rank = clan.getRank(member);
                    packetSender.sendShowClanChatOptions(clan.hasPermission(ClanChatAction.KICK, rank));
                }
            }
        }
    }
}
