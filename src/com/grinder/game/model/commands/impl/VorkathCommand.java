package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.npc.monster.boss.impl.vorkath.VorkathBoss;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.instanced.VorkathArea;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

public class VorkathCommand implements Command {

	@Override
	public String getSyntax() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Starts a Vorkath boss instance.";
	}

    @Override
    public void execute(Player player, String command, String[] parts) {
    	if (player.busy()) {
        	return;
        }
        
		if (player.getArea() != null) {
        	if (player.getArea() instanceof VorkathArea) {
        		return;
        	}
    	}
		
      	int height = player.getIndex() << 2;
		player.moveTo(new Position(2272, 4054, height));
		
		if (player.getArea() != null) {
			player.getArea().leave(player);
		}

    	TaskManager.submit(new Task(3) {

			@Override
			protected void execute() {
//				final VorkathBoss vorkathBoss = new VorkathBoss(new Position(2269, 4062, height));
//				final VorkathArea area = new VorkathArea(vorkathBoss);
//
//				vorkathBoss.setPositionToFace(player.getPosition());
//				vorkathBoss.getMotion().update(MovementStatus.DISABLED);
//				vorkathBoss.setOwner(player);
//				vorkathBoss.spawn();
//				player.setPositionToFace(new Position(player.getPosition().getX(), player.getPosition().getY() + 2, height));
//				area.add(vorkathBoss);
//				area.enter(player);
				stop();
			}
    		
    	});
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
    }
}
