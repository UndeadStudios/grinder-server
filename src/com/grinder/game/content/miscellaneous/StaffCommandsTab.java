package com.grinder.game.content.miscellaneous;

import com.grinder.game.content.gambling.lottery.Lottery;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.syntax.impl.StaffCommandSyntax;

/**
 * Handles the staff command tab
 * 
 * @author 2012
 *
 */
public class StaffCommandsTab {

	/**
	 * Handles the buttons
	 * 
	 * @param player
	 *            the player
	 * @param button
	 *            the button
	 * @return the command
	 */
	public static boolean handleButton(Player player, int button) {
		/*
		 * No rights
		 */
		if (player.getRights().equals(PlayerRights.NONE) || player.getRights().equals(PlayerRights.RUBY_MEMBER)
				|| player.getRights().equals(PlayerRights.AMETHYST_MEMBER)
				|| player.getRights().equals(PlayerRights.TOPAZ_MEMBER)
				|| player.getRights().equals(PlayerRights.LEGENDARY_MEMBER)
				|| player.getRights().equals(PlayerRights.PLATINUM_MEMBER)
				|| player.getRights().equals(PlayerRights.TITANIUM_MEMBER)
				|| player.getRights().equals(PlayerRights.DIAMOND_MEMBER)
				|| player.getRights().equals(PlayerRights.YOUTUBER)) {
			return false;
		}
		/*
		 * The action
		 */
		switch (button) {
		case 31511:
			//System.out.println(player.getRights().ordinal());
			if (player.getRights().ordinal() < 1 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("mute"));
			player.getPacketSender().sendEnterInputPrompt("Enter the name of the player you wish to mute:");
			return true;
		case 31512:
			if (player.getRights().ordinal() < 1 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("unmute"));
			player.getPacketSender().sendEnterInputPrompt("Enter the name of the player you wish to unmute:");
			return true;
		case 31513:
			if (player.getRights().ordinal() < 1 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("jail"));
			player.getPacketSender().sendEnterInputPrompt("Enter the name of the player you wish to jail:");
			return true;
		case 31514:
			if (player.getRights().ordinal() < 1 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("unjail"));
			player.getPacketSender().sendEnterInputPrompt("Enter the name of the player you wish to unjail to:");
			return true;
		case 31515:
			if (player.getRights().ordinal() < 1 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("warn"));
			player.getPacketSender().sendEnterInputPrompt("Enter the name of the player you wish to warn:");
			return true;
		case 31517:
			if (player.getRights().ordinal() < 2 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("kick"));
			player.getPacketSender().sendEnterInputPrompt("Enter the name of the player you wish to kick:");
			return true;
		case 31518:
			if (player.getRights().ordinal() < 2 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("ban"));
			player.getPacketSender().sendEnterInputPrompt("Enter the name of the player you wish to ban:");
			return true;
		case 31519:
			if (player.getRights().ordinal() < 2 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("unban"));
			player.getPacketSender().sendEnterInputPrompt("Enter the name of the player you wish to unban:");
			return true;
		case 31520:
			if (player.getRights().ordinal() < 2 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("jail2"));
			player.getPacketSender().sendEnterInputPrompt("Enter the name of the player you wish to jail:");
			return true;
		case 31521:
			if (player.getRights().ordinal() < 2 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("ipmute"));
			player.getPacketSender().sendEnterInputPrompt("Enter the name of the player you wish to IP Mute:");
			return true;
		case 31522:
			if (player.getRights().ordinal() < 2 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("unipmute"));
			player.getPacketSender().sendEnterInputPrompt("Enter the name of the player you wish to Un-IP Mute:");
			return true;
		case 31523:
			if (player.getRights().ordinal() < 3 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("ipban"));
			player.getPacketSender()
					.sendEnterInputPrompt("Enter the name of the player you wish to IP Ban:");
			return true;
		case 31524:
			if (player.getRights().ordinal() < 3 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("unipban"));
			player.getPacketSender()
			.sendEnterInputPrompt("Enter the name of the player you wish to Un-IP Ban:");
			return true;
		case 31525:
			if (player.getRights().ordinal() < 2 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("lock"));
			player.getPacketSender().sendEnterInputPrompt("Enter the name of the player you wish to lock:");
			return true;
		case 31526:
			if (player.getRights().ordinal() < 2 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("unlock"));
			player.getPacketSender().sendEnterInputPrompt("Enter the name of the player you wish to unlock:");
			return true;
		case 31527:
			if (player.getRights().ordinal() < 2 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("teleto"));
			player.getPacketSender().sendEnterInputPrompt("Enter the name of the player you wish to teleport to:");
			return true;
		case 31529:
			if (player.getRights().ordinal() < 3 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
	    	if (player.getRights().ordinal() < 4 && (AreaManager.inWilderness(player) || player.busy() || player.getStatus() == PlayerStatus.BANKING
	    			|| player.getStatus() == PlayerStatus.DICING || player.getStatus() == PlayerStatus.SHOPPING || player.getStatus() == PlayerStatus.TRADING
	    			|| player.getStatus() == PlayerStatus.PRICE_CHECKING || player.getCombat().isInCombat())) {
	    		player.getPacketSender().sendMessage("You're not eligible to use this command right now.");
	    		return false;
	    	}
	    	player.getBankpin().openBank();
	        //player.getPacketSender().sendMessage("You are " + player.isMember());
			return true;
		case 31530:
			if (player.getRights().ordinal() < 3 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("hostban"));
			player.getPacketSender()
			.sendEnterInputPrompt("Enter the name of the player you wish to Host Ban:");
			return true;
		case 31531:
			if (player.getRights().ordinal() < 3 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("unhostban"));
			player.getPacketSender()
			.sendEnterInputPrompt("Enter the name of the player you wish to Un-Host Ban:");
			return true;
		case 31532:
			if (player.getRights().ordinal() < 3 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("teletome"));
			player.getPacketSender()
			.sendEnterInputPrompt("Enter the name of the player you wish to teleport to you:");
			return true;
		case 31533:
			if (player.getRights().ordinal() < 3 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("movehome"));
			player.getPacketSender()
			.sendEnterInputPrompt("Enter the name of the player you wish to move to home:");
			return true;
		case 31534:
			if (player.getRights().ordinal() < 3 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("checkbank"));
			player.getPacketSender()
			.sendEnterInputPrompt("Enter the name of the player you wish to bank check:");
			return true;
		case 31535:
			if (player.getRights().ordinal() < 3 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("checkinv"));
			player.getPacketSender()
			.sendEnterInputPrompt("Enter the name of the player you wish to inventory check:");
			return true;
		case 31536:
			if (player.getRights().ordinal() < 3 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			Lottery.pickWinner();
			player.getPacketSender().sendMessage("<img=784> You have drawn the lottery and declared the winner!");
			return true;
		case 31538:
			if (player.getRights().ordinal() < 7 && player.getRights().isHighStaff()) {
				DialogueManager.sendStatement(player, "Command access has been restricted!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("wipeaccount"));
			player.getPacketSender()
			.sendEnterInputPrompt("Enter the name of the player you wish to wipe:");
			return true;
		case 31539:
			if (player.getRights().ordinal() < 4 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("pveteran"));
			player.getPacketSender()
			.sendEnterInputPrompt("Enter the name of the player to be given the Veteran's rank:");
			return true;
		case 31540:
			if (player.getRights().ordinal() < 4 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("prespected"));
			player.getPacketSender()
			.sendEnterInputPrompt("Enter the name of the player to be given the Respected rank:");
			return true;
		case 31541:
			if (player.getRights().ordinal() < 4 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("pes"));
			player.getPacketSender()
			.sendEnterInputPrompt("Enter the name of the player to be given the Ex-Staff rank:");
			return true;
		case 31542:
			if (player.getRights().ordinal() < 4 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("pmm"));
			player.getPacketSender()
			.sendEnterInputPrompt("Enter the name of the player to be given the Middleman's rank:");
			return true;
		case 31543:
			if (player.getRights().ordinal() < 4 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("peh"));
			player.getPacketSender()
			.sendEnterInputPrompt("Enter the name of the player to be given the Event Host rank:");
			return true;
		case 31544:
			if (player.getRights().ordinal() < 4 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("pss"));
			player.getPacketSender()
			.sendEnterInputPrompt("Enter the name of the player to be promoted to Server Supporter rank:");
			return true;
		case 31546:
			if (player.getRights().ordinal() < 5 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("pmod"));
			player.getPacketSender()
			.sendEnterInputPrompt("Enter the name of the player to be promoted to Moderator's rank:");
			return true;
		case 31547:
			if (player.getRights().ordinal() < 5 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("demote"));
			player.getPacketSender()
			.sendEnterInputPrompt("Enter the name of the player to be demoted:");
			return true;
		case 31548:
			if (player.getRights().ordinal() < 5 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("resetpass"));
			player.getPacketSender()
			.sendEnterInputPrompt("Enter the name of the player you wish to reset password:");
			return true;
		case 31549:
			if (player.getRights().ordinal() < 5 && player.getRights().isStaff()) {
				DialogueManager.sendStatement(player, "You're not eligible to use this command!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("resetpin"));
			player.getPacketSender()
			.sendEnterInputPrompt("Enter the name of the player you wish to reset bank PIN:");
			return true;
		case 31550:
			if (player.getRights().ordinal() < 7 && player.getRights().isHighStaff()) {
				DialogueManager.sendStatement(player, "Command access has been restricted!");
				return false;
			}
			player.setEnterSyntax(new StaffCommandSyntax("update"));
			player.getPacketSender()
			.sendEnterInputPrompt("Enter how many seconds before updating the server:");
			return true;
		}
		

		//if (button >= 31511 && button <= 31532) {
		//	player.getPacketSender().sendMessage("Coming soon.", 1000);
		//	return true;
		//}

		return false;
	}

}
