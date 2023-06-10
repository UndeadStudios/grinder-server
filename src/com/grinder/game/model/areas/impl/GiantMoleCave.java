package com.grinder.game.model.areas.impl;

import com.grinder.game.content.item.LightSources;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.Area;
import com.grinder.game.model.item.Item;

import java.util.Optional;

/**
 * Handles the giant mole cave area
 * 
 * @author 2012
 *
 */
public class GiantMoleCave extends Area {

	/**
	 * The giant mole cave
	 */
	public GiantMoleCave() {
		super(new Boundary(1723, 1798, 5126, 5250));
	}

	/**
	 * The entry points
	 */
	private static final Position[] ENTRY_POINTS = {
			new Position(3005, 3376), new Position(2996, 3377),
			new Position(2999, 3375), new Position(2989, 3378),
			new Position(2987, 3387), new Position(2984, 3387)
	};

	/**
	 * Tinderbox
	 */
	private static final Item TINDERBOX = new Item(590);
	private static final Item GOLDEN_TINDERBOX = new Item(2946);

	/**
	 * The entry position
	 */
	private static final Position ENTRY_POINT = new Position(1753, 5237, 0);

	/**
	 * Digging for cave
	 * 
	 * @param player
	 *            the player
	 */
	public static boolean dig(Player player) {
		for (Position pos : ENTRY_POINTS) {
			if (player.getPosition().sameAs(pos)) {
				if (!player.getInventory().contains(TINDERBOX) && !player.getInventory().contains(GOLDEN_TINDERBOX)) {
					player.getPacketSender()
							.sendMessage("You should consider bringing a tinderbox before digging down..", 1000);
					return true;
				}
				player.moveTo(ENTRY_POINT);
				return true;
			}
		}
		return false;
	}

	
	@Override
	public void enter(Agent agent) {
		if (agent != null && agent.isPlayer()) {
		super.enter(agent);
		}
	}

	@Override
	public void leave(Agent agent) {
    	if (agent != null && agent.isPlayer()) {
            super.leave(agent);
		}
	}

	@Override
	public void process(Agent agent) {
		if(agent instanceof Player)
			LightSources.INSTANCE.updateLightInterface(agent.getAsPlayer());
	}

	@Override
	public boolean canTeleport(Player player) {
		return true;
	}

	@Override
	public boolean canAttack(Agent attacker, Agent target) {
		return true;
	}

	@Override
	public void defeated(Player player, Agent agent) {

	}

	@Override
	public boolean canTrade(Player player, Player target) {
		return true;
	}

	@Override
	public boolean isMulti(Agent agent) {
		return true;
	}

	@Override
	public boolean canEat(Player player, int itemId) {
		return true;
	}

	@Override
	public boolean canDrink(Player player, int itemId) {
		return true;
	}

	@Override
	public boolean dropItemsOnDeath(Player player, Optional<Player> killer) {
		return true;
	}

	@Override
	public boolean handleDeath(Player player, Optional<Player> killer) {
		return false;
	}

	@Override
	public void onPlayerRightClick(Player player, Player rightClicked, int option) {

	}

	@Override
	public boolean handleObjectClick(Player player, GameObject obj, int actionType) {
		return false;
	}

	@Override
	public boolean handleDeath(NPC npc) {
		// TODO Auto-generated method stub
		return false;
	}
}