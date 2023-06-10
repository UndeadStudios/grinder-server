package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.commands.Command;

import java.util.concurrent.atomic.AtomicInteger;

public class StaffOnline implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Displays a list of online staff.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        sendStaffList(player);
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

    public static void sendStaffList(Player player) {

        player.getPacketSender().sendString(8144, "@gre@Server - Staff Online");

        final AtomicInteger childId = new AtomicInteger(8145);

        PlayerUtil.getStaffOnlineList()
                .sorted((z1, z2) -> Integer.compare(z2.getRights().ordinal(), z1.getRights().ordinal()))
                .forEachOrdered(staffPlayer -> {
                    if(childId.get() == 8146)
                        childId.incrementAndGet();
                    player.getPacketSender()
                            .sendString(childId.getAndIncrement(), staffPlayer.getRights().toString() + " - " + PlayerUtil.getImages(staffPlayer) +"" + staffPlayer.getUsername());
                });

        player.getPacketSender().clearInterfaceText(childId.get(), 8195);
        player.getPacketSender().sendScrollbarHeight(8143, Math.max(215, PlayerUtil.getStaffOnlineList().toArray().length * 20));
        player.getPacketSender().sendInterface(8134);
    }
}
