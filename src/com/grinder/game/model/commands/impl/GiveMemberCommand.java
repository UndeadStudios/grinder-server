package com.grinder.game.model.commands.impl;

import java.util.Optional;

import com.grinder.game.World;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.*;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.commands.Command;
import com.grinder.util.Logging;
import com.grinder.util.Misc;

public class GiveMemberCommand implements Command {

	@Override
	public String getSyntax() {
		return "[playerName]";
	}

	@Override
	public String getDescription() {
		return "Promote a player to the member's rank.";
	}

    @Override
    public void execute(Player player, String command, String[] parts) {
    	if (command.length() <= 11) {
    		return;
    	}
        String player2 = command.substring(parts[0].length() + 1);
        Optional<Player> plr = World.findPlayerByName(player2);
        player2 = Misc.capitalize(player2);
        if (!PlayerSaving.playerExists(player2) && !plr.isPresent()) {
            player.getPacketSender().sendMessage(player2 + " is not a valid online player.");
            return;
        }
/*        if (PlayerUtil.isMember(plr.get())) {
        	player.getPacketSender().sendMessage(plr.get().getUsername() + " is already a member.");
        	return;
        }*/
		if (plr.get().getStatus() == PlayerStatus.TRADING
				|| plr.get().getStatus() == PlayerStatus.DUELING
				|| plr.get().getStatus() == PlayerStatus.DICING) {
			player.getPacketSender().sendMessage("<img=779> You can't use this command because " + player2 +" is in a busy state!");
			return;
		}
/*		if (plr.get().getGameMode().isIronman() || plr.get().getGameMode().isHardcore() || plr.get().getGameMode().isUltimate()) {
			player.getPacketSender().sendMessage("You can't promote a player with an Iron Man rank.");
			return;
		}*/
        if (plr.get().BLOCK_ALL_BUT_TALKING) {
        	return;
        }
        if (plr.get().isInTutorial()) {
        	return;
        }
		if (EntityExtKt.getBoolean(plr.get(), Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(plr.get(), Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
			return;
		}
    	if (player.busy()) {
    		player.getPacketSender().sendMessage("<img=779> You can't do that when you're busy.");
    		return;
    	}
    	if (plr.get().getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
    		player.getPacketSender().sendMessage("<img=779> " + player2 +" can't be promoted while AFK!");
    		return;
    	}
/*		if (PlayerUtil.isMember(plr.get())) {
			player.getPacketSender().sendMessage("<img=779> " + player2 + " is already a member.");
			return;
		}*/
		plr.get().getAttributes().numAttr(Attribute.AMOUNT_PAID, 0).setValue(50 + plr.get().getAttributes().numInt(Attribute.AMOUNT_PAID));

		if (plr.get().getRights() == PlayerRights.RUBY_MEMBER || plr.get().getAttributes().numInt(Attribute.AMOUNT_PAID) >= 49 || plr.get().getAttributes().containsKey(Attribute.FREE_RUBY_MEMBER_RANK)) {
			AchievementManager.processFor(AchievementType.SPREAD_LOVE, plr.get());
		}
		if (plr.get().getRights() == PlayerRights.TOPAZ_MEMBER || plr.get().getAttributes().numInt(Attribute.AMOUNT_PAID) >= 99) {
			AchievementManager.processFor(AchievementType.SUPERIOR_SUPPORT, plr.get());
		}
		if (plr.get().getRights() == PlayerRights.AMETHYST_MEMBER || plr.get().getAttributes().numInt(Attribute.AMOUNT_PAID) >= 150) {
			AchievementManager.processFor(AchievementType.EXTREME_SUPPORT, plr.get());
		}
		if (plr.get().getRights() == PlayerRights.LEGENDARY_MEMBER || plr.get().getAttributes().numInt(Attribute.AMOUNT_PAID) >= 249) {
			AchievementManager.processFor(AchievementType.LEGENDARY_SUPPORT, plr.get());
		}
		if (plr.get().getRights() == PlayerRights.PLATINUM_MEMBER || plr.get().getAttributes().numInt(Attribute.AMOUNT_PAID) >= 499) {
			AchievementManager.processFor(AchievementType.PLATINUM_SUPPORT, plr.get());
		}
		if (plr.get().getRights() == PlayerRights.TITANIUM_MEMBER || plr.get().getAttributes().numInt(Attribute.AMOUNT_PAID) >= 749) {
			AchievementManager.processFor(AchievementType.TITANIUM_SUPPORT, plr.get());
		}
		if (plr.get().getRights() == PlayerRights.DIAMOND_MEMBER || plr.get().getAttributes().numInt(Attribute.AMOUNT_PAID) >= 999) {
			AchievementManager.processFor(AchievementType.DIAMOND_SUPPORT, plr.get());
		}


        plr.get().getPacketSender().sendRights();
        player.getPacketSender().sendMessage("<img=745> " + player2 + " has been successfully given the member's rank with $50.00 value added!");
		player.getPacketSender().sendMessage("<img=745> " + player2 + " total paid for player is now @gre@$" + plr.get().getAttributes().numInt(Attribute.AMOUNT_PAID));
        plr.get().getPacketSender().sendMessage("<img=745> You have been given the member's rank by " + PlayerUtil.getImages(player) + "" + player.getUsername() +"!");
        plr.get().getPacketSender().sendMessage("<img=745> Please relog for your status to show up.");
        //PlayerUtil.broadcastMessage("<img=745> " + player2 + " has been given the member's rank by " + PlayerUtil.getImages(player) + "" + player.getUsername() +"!");
        Logging.log("promotions", "" + player.getUsername() + " gave the member's rank to: " + plr.get().getUsername() + "");
		//}
    }

    @Override
    public boolean canUse(Player player) {
    	return player.getUsername().equals("3lou 55") || player.getRights().equals(PlayerRights.DEVELOPER) || player.getRights().equals(PlayerRights.CO_OWNER) || player.getRights().equals(PlayerRights.ADMINISTRATOR)|| player.getUsername().equals("Lord Hunterr");
    }

}
