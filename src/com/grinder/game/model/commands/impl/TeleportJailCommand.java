package com.grinder.game.model.commands.impl;

import java.util.Optional;

import com.grinder.game.World;
import com.grinder.game.content.item.MorphItems;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.commands.Command;
import com.grinder.util.Misc;

import static com.grinder.game.entity.agent.player.PlayerRights.*;

public class TeleportJailCommand implements Command {

	@Override
	public String getSyntax() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Attempt to teleport a player to jail area.";
	}

	@Override
    public void execute(Player player, String command, String[] parts) {
		if (command.length() <= 8) {
			player.sendMessage("Wrong usage of the command!");
			return;
		}
        Optional<Player> plr = World.findPlayerByName(command.substring(parts[0].length() + 1));
        if (plr.isPresent()) {
        	if (plr.get().busy()) {
        		player.getPacketSender().sendMessage("<img=742> The player is currently busy and can't be teleported.");
        		return;
        	}
        	if (plr.get().getRelations().getIgnoreList().contains(player.getLongUsername())) {
        		player.getPacketSender().sendMessage("<img=742> You can't teleport players that have you on their ignore list.");
        		return;
        	}
			if (plr.get().isBlockingDisconnect()) {
				player.getPacketSender().sendMessage("<img=779> You can't use this command because " + plr.get().getUsername() +" is in a busy state!");
				return;
			}
    		if (plr.get().getHitpoints() <= 0)
    			return;
    		if (plr.get().getStatus() == PlayerStatus.TRADING) {
    			player.getPacketSender().sendMessage("The player is currently busy and can't be teleported.", 1000);
    			return;
    		}
    		if (plr.get().isJailed()) {
    			player.getPacketSender().sendMessage("Player is already jailed!");
    			return;
    		}
    		if (plr.get().getStatus() == PlayerStatus.BANKING) {
    			player.getPacketSender().sendMessage("The player is currently busy and can't be teleported.", 1000);
    			return;
    		}
    		if (plr.get().getStatus() == PlayerStatus.PRICE_CHECKING) {
    			player.getPacketSender().sendMessage("The player is currently busy and can't be teleported.", 1000);
    			return;
    		}
            if (plr.get().BLOCK_ALL_BUT_TALKING) {
    			player.getPacketSender().sendMessage("The player is currently busy and can't be teleported.", 1000);
            	return;
            }
            if (plr.get().isInTutorial()) {
    			player.getPacketSender().sendMessage("The player is currently busy and can't be teleported.", 1000);
            	return;
            }
    		if (EntityExtKt.getBoolean(plr.get(), Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(plr.get(), Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
    			player.getPacketSender().sendMessage("The player is currently busy and can't be teleported.", 1000);
    			return;
    		}
        	if (plr.get().getCombat().isInCombat()) {
    			player.getPacketSender().sendMessage("<img=742> The player is currently in combat and can't be teleported.", 1000);
    			return;
    		}
            if (AreaManager.inWilderness(plr.get()) && plr.get().getWildernessLevel() >= 20) {
            	player.sendMessage("<img=742> You can't teleport other players that are in deeper than level 20 Wilderness.");
            	return;
            }
        	if (plr.get().getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
        		player.getPacketSender().sendMessage("<img=779> You can't use afk command when it's already active.", 1000);
        		return;
        	}
			if (!MorphItems.INSTANCE.notTransformed(plr.get(), "", false, false))
				return;

    		if (plr.get().getStatus() == PlayerStatus.TRADING) {
    			player.getPacketSender().sendMessage("The player is currently busy and can't be teleported.", 1000);
    			return;
    		}
    		if (plr.get().getStatus() == PlayerStatus.DUELING) {
    			player.getPacketSender().sendMessage("The player is currently busy and can't be teleported.", 1000);
    			return;
    		}
    		player.performAnimation(new Animation(1818));
    		player.performGraphic(new Graphic(343));
    		//player.getPacketSender().sendSound(199)
    		plr.get().setTeleportToCaster(player.getAsPlayer());
            player.sendMessage("<img=742> You have sent a jail teleport request to @dre@" + plr.get().getUsername() + "</col>!");
            plr.get().getPacketSender().sendInterface(12468);
            plr.get().getPacketSender().sendString("" + PlayerUtil.getImages(player) + "" + player.getUsername() +"", 12558);
            plr.get().getPacketSender().sendString("Jail to chat!", 12560);
            plr.get().setTeleportDestination(new Position(3233 + Misc.getRandomInclusive(3), 9799 + Misc.getRandomInclusive(2), 0));
            
            player.getPacketSender().sendAreaPlayerSound(225);
            
        } else {
        	player.sendMessage("<img=742> The player that you're trying to teleport to jail is not currently online!");
        }
    }

    @Override
    public boolean canUse(Player player) {
        return player.getRights().anyMatch(SERVER_SUPPORTER, MODERATOR, GLOBAL_MODERATOR, ADMINISTRATOR, DEVELOPER, CO_OWNER, OWNER) || player.getUsername().equals("Shark Black");
    }

}
