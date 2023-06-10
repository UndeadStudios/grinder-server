package com.grinder.game.model.areas.impl;

import com.grinder.game.definition.NpcDefinition;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.areas.Area;

import java.util.Optional;

public class DicingArea extends Area {

    public DicingArea() {
		super(
		        new Boundary(3037, 3054, 3372, 3384),
                new Boundary(2816,2879,2560,2623),
                new Boundary(2962, 2984, 9731, 9749));
    }

	@Override
	public void enter(Agent agent) {
		if (agent != null && agent.isPlayer()) {
		super.enter(agent);
		}
    	if (agent != null && agent.isPlayer()) {
            Player player = agent.getAsPlayer();
            player.getPacketSender().sendInteractionOption("Gamble", 1, false);
            player.getPacketSender().sendInteractionOption("null", 2, true);
        }
    }

    @Override
    public void leave(Agent agent) {
    	if (agent != null && agent.isPlayer()) {
    		super.leave(agent);
            Player player = agent.getAsPlayer();
            player.getPacketSender().sendInteractionOption("null", 2, true);
            player.getPacketSender().sendInteractionOption("null", 1, false);
        }
    }

    @Override
    public void process(Agent agent) {
    }

    @Override
    public boolean canTeleport(Player player) {
        return true;
    }

    @Override
    public boolean canAttack(Agent attacker, Agent target) {
        if (attacker.isPlayer() && target.isPlayer()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean canTrade(Player player, Player target) {
        return true;
    }

    @Override
    public boolean isMulti(Agent agent) {
		return false;
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
    	player.getGambling().request(rightClicked);
    }

    @Override
    public void defeated(Player player, Agent agent) {

    }

    @Override
    public boolean handleObjectClick(Player player, GameObject obj, int actionType) {
        return false;
    }

	@Override
	public boolean handleDeath(NPC npc) {
		return false;
	}
}
