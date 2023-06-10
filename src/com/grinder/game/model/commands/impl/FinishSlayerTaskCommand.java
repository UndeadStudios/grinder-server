package com.grinder.game.model.commands.impl;

import java.util.Optional;

import com.grinder.game.World;
import com.grinder.game.content.skill.skillable.impl.slayer.SlayerManager;
import com.grinder.game.definition.NpcDefinition;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.util.Misc;

public class FinishSlayerTaskCommand implements Command {

    @Override
    public String getSyntax() {
        return "[playerName]";
    }

    @Override
    public String getDescription() {
        return "Finishes the Slayer task for a player.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        String player2 = command.substring(parts[0].length() + 1);
        Optional<Player> plr = World.findPlayerByName(player2);
        player2 = Misc.capitalize(player2);
        if (!plr.isPresent()) {
            player.getPacketSender().sendMessage(player2 + " is not currently online.");
            return;
        }
        if (plr.get().getSlayer().getTask() == null) {
            player.sendMessage("The player does not have an active slayer task.");
            return;
        }
        plr.get().getSlayer().getTask().setAmountLeft(0);
        SlayerManager.completeTaskByCommand(plr.get());
        plr.get().getPacketSender().sendMessage("<img=742> @dre@" + player.getUsername() + "</col> has granted you an auto complete to your Slayer task.");
        player.sendMessage("<img=742> @dre@You have completed the Slayer task for " + plr.get().getUsername() + "</col>!");
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
    }

}
