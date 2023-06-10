package com.grinder.net.packet.impl;

import com.grinder.game.content.miscellaneous.YellCustomizer;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.commands.impl.YellCommand;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;

import java.util.regex.Pattern;

/**
 * This packet edits a player's yell title and colors
 *
 * @author xplicit
 */

public class YellEditPacketListener implements PacketListener {

    private static final Pattern INPUT_PATTERN = Pattern.compile("^(\\w|\\s){0,12}$");
    private static final Pattern HEX_PATTERN = Pattern.compile("^\\p{XDigit}{0,6}$");

    @Override
    public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {

        String title = packetReader.readString();

        if (player == null || player.getHitpoints() <= 0)
            return;

        if (title == null) {
            player.sendMessage("Invalid yell title entered");
            return;
        }

        if (INPUT_PATTERN.matcher(title).matches() && (PlayerUtil.isMember(player) || PlayerUtil.isHighStaff(player))) {
            // TODO: check for bad words or staff rank names
            player.setYellTitle(title);
        }

        int[] colors = player.getYellColors();
        int colorsIndexToStartAt = PlayerUtil.isMember(player) || PlayerUtil.isHighStaff(player) ? 0 : 4;

        for (int i = 0; i < colors.length; i++) {
            int color = packetReader.readInt();
            if (i < colorsIndexToStartAt)
                continue;
            if (HEX_PATTERN.matcher(String.format("%06X", (0xFFFFFF & color))).matches()) {
                colors[i] = color;
            }
        }
        player.setYellColors(colors);

        if (player.getInterfaceId() == YellCustomizer.INTERFACE_ID) {
            YellCustomizer.openInterface(player);
        }
        AchievementManager.processFor(AchievementType.FAN_OF_COLORS, player);
        player.getPacketSender().sendMessage("Any changes made have been saved. Example test message using your new yell title and colors:", 1000);
        player.getPacketSender().sendMessage(YellCommand.format(player, "This is a test message (only you can see it)."), 1000);

    }
}
