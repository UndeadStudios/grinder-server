package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.areas.InstanceManager;
import com.grinder.game.model.commands.Command;

public class POScommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Shows your current position coordinates.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        String pos = "x,y,z {"+player.getPosition().compactString()+"} [id = "+ player.getPosition().getRegionId()+"]";
        System.out.println(pos);
        String pos1 = "private static final Position POSITION = new Position("+player.getPosition().getX()+", "+player.getPosition().getY()+");";
        System.out.println(pos1);
        player.getPacketSender().sendMessage(pos);
        if (player.getX() >= InstanceManager.WORLD_BASE_X) {
            int baseX = (player.getX() >> 8) << 8;
            int baseY = (player.getY() >> 8) << 8;
            player.sendMessage(String.format("offsets x,y { %d, %d }", (player.getX() - baseX), (player.getY() - baseY)));
        }
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
    }

}
