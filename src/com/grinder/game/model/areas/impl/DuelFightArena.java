package com.grinder.game.model.areas.impl;

import com.grinder.game.content.dueling.DuelRule;
import com.grinder.game.content.dueling.DuelState;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.areas.Area;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;

import java.util.Optional;

public class DuelFightArena extends Area {

    public DuelFightArena() {
    	super(new Boundary(3333, 3358, 3244, 3258), new Boundary(3364, 3389, 3245, 3257),
				new Boundary(3363, 3389, 3227, 3238), new Boundary(3363, 3389, 3206, 3219),
                new Boundary(3332, 3357, 3206, 3219),
				new Boundary(3332, 3357, 3225, 3238), new Boundary(3332, 3357, 3245, 3257));
    }

	@Override
	public void enter(Agent agent) {
		if (agent != null && agent.isPlayer()) {
		super.enter(agent);
		}
        if (agent != null && agent.isPlayer()) {
            Player player = agent.getAsPlayer();
            player.getPacketSender().sendInteractionOption("Challenge", 1, false);
            player.getPacketSender().sendInteractionOption("null", 2, true);
        }
    }

    @Override
    public void leave(Agent agent) {
    	if (agent != null && agent.isPlayer()) {
        super.leave(agent);
    	}
        if (agent != null && agent.isPlayer()) {
            Player player = agent.getAsPlayer();
            if (player.getDueling().inDuel()) {
                player.getDueling().loseDuel();
            }
            player.getPacketSender().sendInteractionOption("null", 2, true);
            player.getPacketSender().sendInteractionOption("null", 1, false);
        }
    }

    @Override
    public void process(Agent agent) {
    }

    @Override
    public boolean canTeleport(Player player) {
        if (player.getDueling().inDuel()) {
            DialogueManager.sendStatement(player, "You can't teleport out of a duel!");
            return false;
        }
        return true;
    }

    @Override
    public boolean canAttack(Agent agent, Agent target) {
        if (agent.isPlayer() && target.isPlayer()) {
            Player a = agent.getAsPlayer();
            Player t = target.getAsPlayer();
            if (a.getDueling().getState() == DuelState.IN_DUEL && t.getDueling().getState() == DuelState.IN_DUEL) {
                return true;
            } else if (a.getDueling().getState() == DuelState.STARTING_DUEL
                    || t.getDueling().getState() == DuelState.STARTING_DUEL) {
                DialogueManager.sendStatement(a, "The duel hasn't started yet!");
                return false;
            }
            if (!(t.getArea() instanceof DuelFightArena)) {
                a.sendMessage("That isn't your opponent.");
                a.setPositionToFace(target.getPosition());
                PlayerExtKt.resetInteractions(a, true, true);
                return false;
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean canTrade(Player player, Player target) {
        if (player.getDueling().inDuel()) {
            DialogueManager.sendStatement(player, "You can't trade during a duel!");
            return false;
        }
        return true;
    }

    @Override
    public boolean isMulti(Agent agent) {
        return true;
    }

    @Override
    public boolean canEat(Player player, int itemId) {
        if (player.getDueling().inDuel() && player.getDueling().getRules()[DuelRule.NO_FOOD.ordinal()]) {
            DialogueManager.sendStatement(player, "Food has been disabled in this duel!");
            return true;
        }
        return true;
    }

    @Override
    public boolean canDrink(Player player, int itemId) {
        if (player.getDueling().inDuel() && player.getDueling().getRules()[DuelRule.NO_POTIONS.ordinal()]) {
            DialogueManager.sendStatement(player, "Potions have been disabled in this duel!");
            return true;
        }
        return true;
    }

    @Override
    public boolean dropItemsOnDeath(Player player, Optional<Player> killer) {
        if (player.getDueling().inDuel()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean handleDeath(Player player, Optional<Player> killer) {
        if (player.getDueling().inDuel()) {
            player.getDueling().loseDuel();
            return true;
        }
        return false;
    }

    @Override
    public void onPlayerRightClick(Player player, Player rightClicked, int option) {
        if (option == 1) {
            if (player.busy()) {
                player.getPacketSender().sendMessage("You can't do that right now.");
                return;
            }
            if (rightClicked.busy()) {
                player.getPacketSender().sendMessage("That player is currently busy at the moment.");
                return;
            }
            player.getDueling().requestDuel(rightClicked);
        }
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
	
	@Override
	public boolean isSafeForHardcore() {
		return true;
	}

    @Override
    public boolean isCannonProhibited() {
        return true;
    }
}
