package com.grinder.game.model.commands.impl;

import com.grinder.game.World;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.util.Misc;

import java.util.Optional;

public class PNPCCommand implements Command {

    @Override
    public String getSyntax() {
        return "[id]";
    }

    @Override
    public String getDescription() {
        return "Transforms you into the selected NPC id.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        final Player target;
        if(parts.length > 2){
            final String otherName = Misc.formatName(parts[2].toLowerCase());
            final Optional<Player> optionalOther = World.findPlayerByName(otherName);
            if(optionalOther.isEmpty()){
                player.sendMessage("Player "+otherName+" could not be found online!");
                return;
            }
            target = optionalOther.get();
        } else
            target = player;

        target.setNpcTransformationId(Integer.parseInt(parts[1]));
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
    }

}
