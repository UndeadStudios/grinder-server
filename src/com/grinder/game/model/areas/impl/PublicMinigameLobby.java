package com.grinder.game.model.areas.impl;

import com.grinder.game.content.minigame.MinigameManager;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.areas.Area;

import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Handles the lobby for public minigame
 * 
 * @author 2012
 */
public class PublicMinigameLobby extends Area {

	/**
	 * A list of players currently in this area.
	 */
	public static Set<Player> lobby = new HashSet<>();

	/**
	 * The Battle royale games lobby
	 */
	public PublicMinigameLobby() {
		super(new Boundary(3105, 3113, 3493, 3501));
	}

	@Override
	public void enter(Agent agent) {
		if (agent instanceof Player) {

			super.enter(agent);

			final Player player = (Player) agent;

			player.getPacketSender().sendWalkableInterface(29343);
			lobby.add(player);

			player.sendMessage("You have been added to the lobby list. Player(s) waiting: " + lobby.size());
		}
	}

	@Override
	public void leave(Agent agent) {
		if (agent instanceof Player){

			super.leave(agent);

			final Player player = (Player) agent;
			lobby.remove(player);
			player.getPacketSender().sendWalkableInterface(-1);
		}
	}

	@Override
	public void process(Agent agent) {
		if (agent instanceof Player){
			final Player player = (Player) agent;
			player.getPacketSender().sendString(29346, "@or1@Next Game:\\n@whi@" + MinigameManager.publicMinigame.getName());
			player.getPacketSender().sendString(29347,
					"@or1@Player(s): " + lobby.size() + "\\n@or1@Time left: "
							+ (MinigameManager.MINUTES_TILL_NEXT_PUBLIC_GAME - MinigameManager.minutesSinceLastPublicGameEnded) + " Minutes");
//							+ " minutes\\n\\nPotential winning:\\n@red@"
//							+ (NumberFormat.getInstance().format(MinigameManager.getBloodMoneyReward())) + " Blood money");
		}
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
	public void defeated(Player player, Agent agent) { }

	@Override
	public boolean canTrade(Player player, Player target) {
		return false;
	}

	@Override
	public boolean isMulti(Agent agent) {
		return true;
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
		return true;
	}

	@Override
	public void onPlayerRightClick(Player player, Player rightClicked, int option) { }

	@Override
	public boolean handleObjectClick(Player player, GameObject obj, int actionType) {
		return false;
	}

	@Override
	public boolean handleDeath(NPC npc) {
		return false;
	}

}
