package com.grinder.game.content.minigame;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.areas.Area;

/**
 * Represents a minigame
 * 
 * @author 2012
 *
 */
public abstract class Minigame extends Area {

	/**
	 * The players in the minigame
	 */
	private ArrayList<Player> players = new ArrayList<>();

	/**
	 * Represents a minigame
	 * 
	 * @param boundaries
	 *            the boundaries of the minigame
	 */
	public Minigame(Boundary... boundaries) {
		super(boundaries);
	}

	@Override
	public void enter(Agent agent) {
		if (agent instanceof Player) {
			super.enter(agent);
		}
	}

	@Override
	public void leave(Agent agent) {
		if (agent instanceof Player) {
			super.leave(agent);
		}
	}

	@Override
	public void process(Agent agent) {

	}

	@Override
	public boolean canTeleport(Player player) {
		return false;
	}

	@Override
	public boolean canAttack(Agent attacker, Agent target) {
		return false;
	}

	@Override
	public void defeated(Player player, Agent agent) {

	}

	@Override
	public boolean canTrade(Player player, Player target) {
		return false;
	}

	@Override
	public boolean isMulti(Agent agent) {
		return false;
	}

	@Override
	public boolean canEat(Player player, int itemId) {
		return false;
	}

	@Override
	public boolean canDrink(Player player, int itemId) {
		return false;
	}

	@Override
	public boolean dropItemsOnDeath(Player player, Optional<Player> killer) {
		return false;
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

	public int getTimeUntilDangerous(){
		return -1;
	}

	/**
	 * Starting the minigame
	 * 
	 * @param player
	 *            the player
	 */
	public abstract void start(Player player);

	/**
	 * Checks for requirements
	 * 
	 * @param player
	 *            the player
	 * @return the requirements
	 */
	public abstract boolean hasRequirements(Player player);

	/**
	 * The item restriction policy
	 * 
	 * @return the policy
	 */
	public abstract MinigameRestriction getRestriction();

	/**
	 * The name of the minigame
	 * 
	 * @return the name
	 */
	public abstract String getName();

	/**
	 * The prayers that can't be used in minigame
	 * 
	 * @return the player
	 */
	public abstract int[] getUnuseablePrayer();

	/**
	 * Whether presets can be used
	 * 
	 * @return the presets
	 */
	public abstract boolean canUsePresets();

	/**
	 * Whether items are removed
	 * 
	 * @return items removed
	 */
	public abstract boolean removeItems();

	/**
	 * Whether items can be unequipped
	 * 
	 * @return unequipped
	 */
	public abstract boolean canUnEquip();

	/**
	 * Sets the players
	 *
	 * @return the players
	 */
	public ArrayList<Player> getPlayers() {
		return players;
	}

	@Override
	public boolean isSafeForHardcore() {
		return true;
	}
}
