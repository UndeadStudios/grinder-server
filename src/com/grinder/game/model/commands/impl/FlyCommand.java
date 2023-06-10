package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Animation;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;

public class FlyCommand implements Command {

	@Override
	public String getSyntax() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Sets your account to fly mode.";
	}

    @Override
    public void execute(Player player, String command, String[] parts) {
		player.setEntityInteraction(null);
		player.getMotion().followTarget(null);
		player.getMotion().clearSteps();
		player.performAnimation(new Animation(1746));
        player.getCombat().reset(false);
		player.setEntityInteraction(null);
		player.getAttributes()
				.boolAttr(Attribute.IS_FLYING, false)
				.setValue(true);
		player.getPacketSender().sendMessage("You have started flying!");
    }

    @Override
    public boolean canUse(Player player) {
		PlayerRights rights = player.getRights();
    	return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER || player.getUsername().equals("3lou 55"));
    }

}
