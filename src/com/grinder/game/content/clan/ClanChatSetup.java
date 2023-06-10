package com.grinder.game.content.clan;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.net.packet.PacketSender;
import com.grinder.util.Misc;

import java.util.List;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-12
 */
class ClanChatSetup {

    static void open(final Player player, final ClanChat clanChat) {

        final PacketSender packetSender = player.getPacketSender();

        if (clanChat == null) {
            packetSender.sendString(43706, "N/A");
            packetSender.sendString(43709, "Anyone");
            packetSender.sendString(43712, "Anyone");
            packetSender.sendString(43715, "Only me");
            packetSender.sendString(43718, "Only me");
        } else {
            packetSender.sendString(43706, Misc.capitalizeWords(clanChat.getName()));

            final ClanChatRank requiredRanKForEntering = clanChat.findRequirement(ClanChatAction.ENTER).orElse(ClanChatRank.ANYONE);
            packetSender.sendString(43709, requiredRanKForEntering.format());

            final ClanChatRank requiredRanKForTalking = clanChat.findRequirement(ClanChatAction.TALK).orElse(ClanChatRank.ANYONE);
            packetSender.sendString(43712, requiredRanKForTalking.format());

            final ClanChatRank requiredRanKForKicking = clanChat.findRequirement(ClanChatAction.KICK).orElse(ClanChatRank.OWNER);
            packetSender.sendString(43715, requiredRanKForKicking.format());

            final ClanChatRank requiredRanKForBanning = clanChat.findRequirement(ClanChatAction.BAN).orElse(ClanChatRank.OWNER);
            packetSender.sendString(43718, requiredRanKForBanning.format());
        }

        updateMembers(player, clanChat);

        packetSender.sendInterface(ClanChatConstants.CLAN_SETUP_INTERFACE_ID);
    }

    /**
     * Updates the banned and ranked member lists in the clan chat setup interface.
     *
     * @param player the {@link Player} to update the interface for.
     */
    static void updateMembers(final Player player, final ClanChat clanChat) {

        final PacketSender packetSender = player.getPacketSender();

        int rankedMemberListChildId = 43723;
        int bannedMemberListChildId = 43824;

        packetSender.clearInterfaceText(rankedMemberListChildId, rankedMemberListChildId + ClanChatConstants.MAX_MEMBERS);
        packetSender.clearInterfaceText(bannedMemberListChildId, bannedMemberListChildId + ClanChatConstants.MAX_MEMBERS);

        if (clanChat == null)
            return;

        final List<ClanMember> rankedMembers = clanChat.rankedMembers();
        final List<ClanChatBan> bannedMembers = clanChat.bannedMembers();

        final int rankedScrollMax = Math.max(rankedMembers.size() * 14 + 2, 210);
        final int bannedScrollMax = Math.max(bannedMembers.size() * 14 + 2, 210);

        for (final ClanMember member : rankedMembers) {
            final String name = member.getName();
            final int rankId = member.getRank().getSpriteId();
            packetSender.sendString(rankedMemberListChildId++, "<clan="+rankId+">"+name);
        }

        for (final ClanChatBan banEntry : bannedMembers) {
            final String name = banEntry.getName();
            packetSender.sendString(bannedMemberListChildId++, name);
        }

        packetSender.sendScrollbarHeight(43722, rankedScrollMax);
        packetSender.sendScrollbarHeight(43823, bannedScrollMax);
    }
}
