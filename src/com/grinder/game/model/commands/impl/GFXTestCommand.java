package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

public class GFXTestCommand implements Command {

    @Override
    public String getSyntax() {
        return "[startId]";
    }

    @Override
    public String getDescription() {
        return "Projectile's a gfx to your pet, up to id 1800.";
    }

	public int gfx = 0;
    @Override
    public void execute(Player player, String command, String[] parts) {
        gfx = Integer.parseInt(parts[1]);


        TaskManager.submit(new Task(3) {
            @Override
            public void execute() {
                new Projectile(player, player.getCurrentPet(), gfx, 55, 120, 31, 43, 0).sendProjectile();
                ++gfx;
                player.getPacketSender().sendMessage("Playing GFX: " + gfx);
                if (gfx >= 1800) {
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
