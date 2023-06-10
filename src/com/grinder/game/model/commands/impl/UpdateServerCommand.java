package com.grinder.game.model.commands.impl;

import com.grinder.Server;
import com.grinder.game.World;
import com.grinder.game.content.clan.GlobalClanChatManager;
import com.grinder.game.content.skill.skillable.impl.hunter.HunterTraps;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerSaving;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.net.codec.database.SQLManager;

import java.util.concurrent.TimeUnit;

public class UpdateServerCommand implements Command {

    @Override
    public String getSyntax() {
        return "[seconds]";
    }

    @Override
    public String getDescription() {
        return "Sets the server update mode timer.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        int time = Integer.parseInt(parts[1]);
        final int executeTimer = (int) (time + 10);
        if (time > 0) {
            Server.setUpdating(true);
            for (Player players : World.getPlayers()) {
                if (players == null) {
                    continue;
                }
                PlayerSaving.save(players);
                players.getPacketSender().sendSystemUpdate(executeTimer);
            }
            TaskManager.submit(new Task(executeTimer + 4) {
                @Override
                protected void execute() {
                    HunterTraps.INSTANCE.save();
                    for (Player player : World.getPlayers()) {
                        if (player != null) {
                            PlayerSaving.save(player);
                            player.requestClientLogout();
                        }
                    }
                    GlobalClanChatManager.save();
                    try {
                        SQLManager.Companion.getINSTANCE().shutdown(5, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        Server.getLogger().error("Could not shut down SQLManager!", e);
                    }
                    Server.getLogger().info("Update task finished!");
                    Server.updatingCompleted.set(true);
                    stop();
                }
            });
        }
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER || rights == PlayerRights.CO_OWNER);
    }

}
