package com.grinder.game.model.areas.impl;

import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.areas.Area;

import java.util.Optional;

public class LaIslaEbana extends Area {

    public LaIslaEbana() {
        super(new Boundary(3631, 3853, 2935, 3051));
    }

    @Override
    public void process(Agent agent) {
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
		// TODO Auto-generated method stub
		return false;
	}
}