package com.grinder.game.model.commands.impl;

import java.util.Optional;

import com.grinder.game.World;
import com.grinder.game.content.miscellaneous.MysteriousManEvent;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.NPCFactory;
import com.grinder.game.entity.agent.player.*;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.GraphicHeight;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.areas.instanced.AquaisNeigeArea;
import com.grinder.game.model.areas.instanced.FightCaveArea;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.commands.Command;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Logging;
import com.grinder.util.Misc;
import com.grinder.util.NpcID;

public class TriggerRandomForPlayer implements Command {

	@Override
	public String getSyntax() {
		return "[playerName]";
	}

	@Override
	public String getDescription() {
		return "Triggers a random event for the player.";
	}

    @Override
    public void execute(Player player, String command, String[] parts) {
    	if (command.length() <= 8) {
			player.sendMessage("Wrong usage of the command!");
    		return;
    	}
        String player2 = command.substring(parts[0].length() + 1);
        Optional<Player> plr = World.findPlayerByName(player2);
        player2 = Misc.capitalize(player2);
        if (!PlayerSaving.playerExists(player2) && !plr.isPresent()) {
            player.getPacketSender().sendMessage(player2 + " is not a valid online player.");
            return;
        }
        /*if (plr.get().busy()) {
        	player.sendMessage("<img=779> " + player2 + " is currently busy and can't be promoted.");
        	return;
        }*/
		if (plr.get().getStatus() == PlayerStatus.TRADING
				|| plr.get().getStatus() == PlayerStatus.DUELING
				|| plr.get().getStatus() == PlayerStatus.DICING) {
			player.getPacketSender().sendMessage("<img=779> You can't use this command because " + player2 +" is in a busy state!");
			return;
		}
        if (plr.get().BLOCK_ALL_BUT_TALKING) {
			player.getPacketSender().sendMessage("<img=779> You can't use this command because " + player2 +" is having actions blocked!");
        	return;
        }
        if (plr.get().getMinigame() != null) {
			player.getPacketSender().sendMessage("<img=779> You can't use this command because " + player2 +" is in a minigame!");
        	return;
		}
        if (plr.get().isInTutorial()) {
			player.getPacketSender().sendMessage("<img=779> You can't use this command because " + player2 +" is in a tutorial!");
        	return;
        }
		if (EntityExtKt.getBoolean(plr.get(), Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(plr.get(), Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
			player.getPacketSender().sendMessage("<img=779> You can't use this command because " + player2 +" has random event triggered!");
			return;
		}
		if (EntityExtKt.getBoolean(plr.get(), Attribute.HAS_TRIGGER_RANDOM_EVENT, false)) {
			player.getPacketSender().sendMessage("<img=779> You can't use this command because " + player2 +" has random event triggered!");
			return;
		}
		if (AreaManager.DUEL_ARENA.contains(player)) {
			player.getPacketSender().sendMessage("<img=779> You can't use this command because " + player2 +" is in the Duel Arena!");
			return;
		}
		if (AreaManager.DuelFightArena.contains(player)) {
			player.getPacketSender().sendMessage("<img=779> You can't use this command because " + player2 +" is in the Duel Arena!");
			return;
		}
		if (AreaManager.MINIGAME_LOBBY.contains(player)) {
			player.getPacketSender().sendMessage("<img=779> You can't use this command because " + player2 +" is in Minigame Lobby!");
			return;
		}
		if (player.isJailed()) {
			player.getPacketSender().sendMessage("<img=779> You can't use this command because " + player2 +" is Jailed!");
			return;
		}
		if (player.getArea() != null && player.getArea() instanceof FightCaveArea) {
			player.getPacketSender().sendMessage("<img=779> You can't use this command because " + player2 +" is in Fight Caves!");
			return;
		}
		if (player.getArea() != null && player.getArea() instanceof AquaisNeigeArea) {
			player.getPacketSender().sendMessage("<img=779> You can't use this command because " + player2 +" is in Aquais Neige!");
			return;
		}
    	if (plr.get().getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
			player.getPacketSender().sendMessage("<img=779> You can't use this command because " + player2 +" is AFK!");
    		return;
    	}

    	// Trigger the event
		MysteriousManEvent.INSTANCE.trigger(plr.get());

		// Action completed succesfully
		player.getPacketSender().sendMessage("<img=779> " + player2 + " has been successfully triggered to a random event!");

    }

    @Override
    public boolean canUse(Player player) {
    	return player.getUsername().equals("3lou 55") || player.getRights().equals(PlayerRights.DEVELOPER) ||
				player.getRights().equals(PlayerRights.ADMINISTRATOR) || player.getRights().equals(PlayerRights.GLOBAL_MODERATOR) || player.getRights().equals(PlayerRights.MODERATOR) || player.getRights().equals(PlayerRights.SERVER_SUPPORTER) ||
				player.getRights().equals(PlayerRights.CO_OWNER) || player.getRights().equals(PlayerRights.OWNER);
    }

}
