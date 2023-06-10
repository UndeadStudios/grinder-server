package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.entity.agent.player.PlayerRights;

public class DialogueCommand implements Command {

    @Override
    public String getSyntax() {
        return "[id]";
    }

    @Override
    public String getDescription() {
        return "Opens a chatbox dialogue.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        DialogueManager.start(player, Integer.valueOf(parts[1]));
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
    }

}
