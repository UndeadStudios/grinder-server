package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.commands.Command;
import com.grinder.game.content.minigame.MinigameManager;
import com.grinder.game.task.TaskManager;

public class MinigameCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Begins a game of battle royale";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        MinigameManager.enterGame(player, MinigameManager.BATTLE_ROYALE);
        TaskManager.submit(10, () -> MinigameManager.BATTLE_ROYALE.defeated(player, player));

    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER || player.getUsername().equals("Stan");
    }
}
