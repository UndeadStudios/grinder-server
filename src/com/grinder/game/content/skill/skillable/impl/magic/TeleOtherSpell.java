package com.grinder.game.content.skill.skillable.impl.magic;

import com.grinder.game.content.item.MorphItems;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.model.Animation;
import com.grinder.game.model.ButtonActions;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Position;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;



public class TeleOtherSpell {

	static {
		ButtonActions.INSTANCE.onClick(12566, clickAction -> {
			final Player player = clickAction.getPlayer();
			if (player.getInterfaceId() != 12468) {
				return;
			}
			if (player.getTeleportToCaster() != null) {
				if (player.getTeleportDestination() == null)
						TeleOtherSpell.teleportToLocation(player);
				else
					if (TeleportHandler.checkReqs(player, player.getTeleportDestination(), true, true, player.getSpellbook().getTeleportType())) {
						TeleOtherSpell.teleportToLocationMap(player, player.getTeleportDestination());
					}
			} else {
				if (player.getTeleportDestination() != null)
					if (TeleportHandler.checkReqs(player, player.getTeleportDestination(), true, true, player.getSpellbook().getTeleportType())) {
						TeleOtherSpell.teleportToLocationMap(player, player.getTeleportDestination());
					}
			}
		});
		ButtonActions.INSTANCE.onClick(12568, clickAction -> {
			final Player player = clickAction.getPlayer();
			player.getPacketSender().sendInterfaceRemoval();
		});
	}
	/**
	 * Teleporting the player using a command
	 * 
	 * @param player
	 *            the player
	 */
	public static void teleportToLocation(final Player player) {
		final Agent caster = player.getTeleportToCaster();

		if (caster == null)
			return;

		if(caster instanceof Player) {
			if (player.getRelations().getIgnoreList().contains(caster.getAsPlayer().getUsername())) {
				player.getPacketSender().sendMessage("You can't teleport to players that you have on your ignore list.");
				return;
			}
		}
        if (player.BLOCK_ALL_BUT_TALKING) {
			player.getPacketSender().sendMessage("You can't do this right now.", 1000);
        	return;
        }
        if (player.isInTutorial()) {
			player.getPacketSender().sendMessage("You can't do this while in a tutorial.", 1000);
        	return;
        }
		if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
			player.getPacketSender().sendMessage("You can't this that when you're busy.", 1000);
			return;
		}
		if (player.getHitpoints() <= 0)
			return;
		if (player.getStatus() == PlayerStatus.TRADING) {
			player.getPacketSender().sendMessage("You can't do this while in a trade!", 1000);
			return;
		}
		if (player.getStatus() == PlayerStatus.DUELING) {
			player.getPacketSender().sendMessage("You can't do this while dueling!", 1000);
			return;
		}
		if (player.getStatus() == PlayerStatus.BANKING) {
			player.getPacketSender().sendMessage("You can't use this while banking!", 1000);
			return;
		}
		if (player.getStatus() == PlayerStatus.PRICE_CHECKING) {
			player.getPacketSender().sendMessage("You can't use this while price checking!", 1000);
			return;
		}
		if (player.busy()) {
    		player.getPacketSender().sendMessage("You can't do this when you're busy.", 1000);
    		return;
    	}

		if (!MorphItems.INSTANCE.notTransformed(player, "do", true, false))
			return;
		
		// Perform animation
        player.performAnimation(new Animation(1816));
        
        // Perform graphic
        player.performGraphic(new Graphic(342));

        // Block movement
		player.setTeleporting(TeleportType.ANCIENT_WIZARD);
		player.BLOCK_ALL_BUT_TALKING = true;

		// Increase points
		player.getPoints().increase(AttributeManager.Points.TELE_OTHER_CASTED, 1); // Increase points
		player.getPoints().increase(AttributeManager.Points.SPELLS_CASTED); // Increase points
        

        final Position coords = player.getTeleportToCaster().getPosition();
        // Teleport
		TaskManager.submit(new Task(2) {
			@Override
			public void execute() {
		        player.moveTo(coords);
		        player.performAnimation(new Animation(-1));
				stop();
			}
		});

		TaskManager.submit(new Task(4) {
			@Override
			public void execute() {
				player.setTeleporting(null);
				player.BLOCK_ALL_BUT_TALKING = false;
				stop();
			}
		});
		
        
        // Send message
        player.sendMessage("You have successfully teleported to: @dre@" +
				(caster instanceof Player ? caster.getAsPlayer().getUsername()
						: caster.getAsNpc().fetchDefinition().getName()) +"</col>!");
        
        // Close interface
        player.getPacketSender().sendInterfaceRemoval();
        
        // Send Sound
        player.getPacketSender().sendAreaPlayerSound(201);
        
        // Set tele location null
        player.setTeleportDestination(null);
		player.setTeleportToCaster(null);
	}
	
	/**
	 * Casting tele other spell on a player.
	 * 
	 * @param player
	 *            the player
	 */
	public static void teleportToLocationMap(final Player player, final Position positionCoords) {



		if (player.getStatus() == PlayerStatus.TRADING) {
			player.getPacketSender().sendMessage("You can't do this while in a trade!", 1000);
			return;
		}
		if (player.getStatus() == PlayerStatus.DUELING) {
			player.getPacketSender().sendMessage("You can't do this while dueling!", 1000);
			return;
		}
		if (player.getStatus() == PlayerStatus.BANKING) {
			player.getPacketSender().sendMessage("You can't do this while banking!", 1000);
			return;
		}
		if (player.getStatus() == PlayerStatus.PRICE_CHECKING) {
			player.getPacketSender().sendMessage("You can't do this while price checking!", 1000);
			return;
		}
		if (player.busy()) {
			player.getPacketSender().sendMessage("You can't do this when you're busy.", 1000);
    		return;
    	}

		if (!MorphItems.INSTANCE.notTransformed(player, "do", true, false))
			return;

		
		// Perform animation
        player.performAnimation(new Animation(1816));
        
        // Perform graphic
        player.performGraphic(new Graphic(342));

		// Block movement
		player.setTeleporting(TeleportType.ANCIENT_WIZARD);
		player.BLOCK_ALL_BUT_TALKING = true;
        

        // Teleport
		TaskManager.submit(new Task(2) {
			@Override
			public void execute() {
		        player.moveTo(positionCoords);
		        player.performAnimation(new Animation(-1));
				stop();
			}
		});

		// Reset movement and set back to normal
		TaskManager.submit(new Task(4) {
			@Override
			public void execute() {
				player.setTeleporting(null);
				player.BLOCK_ALL_BUT_TALKING = false;
				stop();
			}
		});

        
        // Send Message
        player.sendMessage("You have been successfully teleported!");
        
        // Close interface
        player.getPacketSender().sendInterfaceRemoval();
        
        // Send Sound
        player.getPacketSender().sendAreaPlayerSound(201);
        
		// Cast sound 199 ID
        
        // Set tele location null
        player.setTeleportDestination(null);
		player.setTeleportToCaster(null);
	}
}
