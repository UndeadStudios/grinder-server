package com.grinder.game.model.areas.impl;

import com.grinder.game.content.minigame.barrows.BarrowsBrother;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.areas.Area;
import com.grinder.util.ObjectID;

import java.util.Optional;

public class BarrowsArea extends Area {

    public BarrowsArea() {
        super(new Boundary(3521, 3582, 9662, 9724), new Boundary(3545, 3583, 3265, 3306));
    }

	@Override
	public void enter(Agent agent) {
		if (agent != null && agent.isPlayer()) {
		super.enter(agent);
		}
        if (agent != null && agent.isPlayer()) {
            Player player = agent.getAsPlayer();
            player.getPacketSender().sendWalkableInterface(4535);
            player.getBarrowsManager().updateInterface();
        }
    }

    @Override
    public void leave(Agent agent) {
        if (agent != null && agent.isPlayer()) {
            super.leave(agent);
            agent.getAsPlayer().getPacketSender().sendWalkableInterface(-1);
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
    }

    @Override
    public void defeated(Player player, Agent agent) {

    }

    @Override
    public boolean handleObjectClick(Player player, GameObject obj, int actionType) {
        if (obj.getId() == ObjectID.BARROWS_REWARD_CHEST) {
            player.getBarrowsManager().handleChest(player.getFinalBarrow());
            return true;
        }
        BarrowsBrother.getByStairsId(obj.getId()).ifPresent(barrow -> {
            player.getBarrowsManager().handleStairs(barrow);
        });
        BarrowsBrother.getByCoffinId(obj.getId()).ifPresent(barrow -> {
            player.getBarrowsManager().spawn(barrow, false);
        });
        return true;
    }

	@Override
	public boolean handleDeath(NPC npc) {
		// TODO Auto-generated method stub
		return false;
	}
}
