package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.commands.Command;

import java.util.concurrent.atomic.AtomicInteger;

public class OnlineMiddlemenCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Displays a list of online middleman's.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
    	sendMiddlemenList(player);
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

    public static void sendMiddlemenList(Player player) {

        player.getPacketSender().sendString(8144, "@gre@Server - Middlemen Online");

        final AtomicInteger childId = new AtomicInteger(8145);

        PlayerUtil.getMiddleOnlineList()
                .sorted((z1, z2) -> Integer.compare(z2.getRights().ordinal(), z1.getRights().ordinal()))
                .forEachOrdered(MiddlemenPlayer -> {
                    if(childId.get() == 8146)
                        childId.incrementAndGet();
                    player.getPacketSender()
                            .sendString(childId.getAndIncrement(), "<img=939> " + MiddlemenPlayer.getUsername());
                });

        player.getPacketSender().clearInterfaceText(childId.get(), 8195);
        player.getPacketSender().sendScrollbarHeight(8143, Math.max(215, PlayerUtil.getStaffOnlineList().toArray().length * 20));
        player.getPacketSender().sendInterface(8134);
    }
}
