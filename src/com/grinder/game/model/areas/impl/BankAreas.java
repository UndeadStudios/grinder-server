package com.grinder.game.model.areas.impl;


import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.areas.Area;

import java.util.Optional;

public class BankAreas extends Area {

    public BankAreas() {
		super(
		        new Boundary(3180, 3190, 3433, 3447),
                new Boundary(3145, 3180, 3473, 3506),
				new Boundary(3250, 3257, 3416, 3424),
                new Boundary(3091, 3101, 3483, 3499), // Edge bank
                new Boundary(3076, 3101, 3507, 3513), // Edge Shops
                //new Boundary(3064, 3065, 3505, 3508), // Edge Shops2
                //new Boundary(3064, 3071, 3514, 3517), // Edge Shops2
                //new Boundary(3085, 3101, 3468, 3482), // Edge
                new Boundary(2943, 2948, 3365, 3373),
				new Boundary(3009, 3019, 3353, 3358));
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
		return false;
	}

    @Override
    public boolean isCannonProhibited() {
        return true;
    }
}
