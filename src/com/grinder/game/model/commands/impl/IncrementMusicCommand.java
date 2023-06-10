package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.Position;
import com.grinder.game.model.commands.Command;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;

public class IncrementMusicCommand implements Command {

    @Override
    public String getSyntax() {
        return "[MusicId]";
    }

    @Override
    public String getDescription() {
        return "Increments Played Music.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        int[] startMusicId = {Integer.parseInt(parts[1])};
        player.sendMessage("Music stops playing at 750, use it for stopping.");
        TaskManager.submit(new Task(20) {
            @Override
            public void execute() {
                player.getPacketSender().sendMusic(startMusicId[0], 4, 0);
                player.getPacketSender().sendMessage("Playing music: " + startMusicId[0]);
                startMusicId[0]++;
                if (startMusicId[0] >= 750) {
                    player.sendMessage("Music incrementation has been stopped.");
                    stop();
                }
            }
        });
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
    }
}
