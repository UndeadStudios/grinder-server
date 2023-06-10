package com.grinder.util.debug;

import com.grinder.game.entity.agent.player.Player;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-27
 */
public class DebugTab {

    private final static int START_ID = 23141;

    private final Player player;

    private final DebugType type;

    public DebugTab(Player player, DebugType type) {
        this.player = player;
        this.type = type;
    }

    public void update(){

        player.getPacketSender().sendWalkableInterface(23139);

        type.provide(player).ifPresent(debugListener -> {

            int id = START_ID;

            for(final String line: debugListener.lines())
                player.getPacketSender().sendString(id++, line);
        });

    }

}
