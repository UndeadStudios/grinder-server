package com.grinder.game.model.areas.impl;

import com.grinder.game.World;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.NPCFactory;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.Area;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.game.task.impl.NPCRespawnTask;

import java.util.Optional;

import static com.grinder.util.NpcID.*;

/**
 * Handles the dagannoth kings area
 */
public class DagannothKings extends Area {

	private static final Position NORTH_KING = new Position(2913, 4458, 0);
	private static final Position EAST_KING = new Position(2913, 4448, 0);
	private static final Position WEST_KING = new Position(2907, 4448, 0);

	/**
	 * Represents the area
	 */
	public DagannothKings() {
		super(new Boundary(2899, 2926, 4438, 4461));
	}

	@Override
	public void process(Agent agent) { }
	
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
	public void onPlayerRightClick(Player player, Player rightClicked, int option) { }

	@Override
	public boolean handleObjectClick(Player player, GameObject obj, int actionType) {
		return false;
	}

	@Override
	public boolean handleDeath(NPC npc) {
		return false;
	}
	
	private boolean canExecute = true;
	
	public void reload() {
			Optional<NPC> boss = World.findNpcById(DAGANNOTH_PRIME);
			Optional<NPC> minion_one = World.findNpcById(DAGANNOTH_SUPREME);
			Optional<NPC> minion_two = World.findNpcById(DAGANNOTH_REX);

			if (boss.isEmpty() && minion_one.isEmpty() && minion_two.isEmpty()) {

				if (!canExecute)
					return;

				TaskManager.submit(new NPCRespawnTask(NPCFactory.INSTANCE.create(DAGANNOTH_PRIME, NORTH_KING), 30));
				TaskManager.submit(new NPCRespawnTask(NPCFactory.INSTANCE.create(DAGANNOTH_SUPREME, WEST_KING), 30));
				TaskManager.submit(new NPCRespawnTask(NPCFactory.INSTANCE.create(DAGANNOTH_REX, EAST_KING), 30));
				setCanExecute(false);

                TaskManager.submit(new Task(65) {
                    @Override
                    public void execute() {
                        setCanExecute(true);
                        stop();
                    }
                });
			}
		}
	
	public boolean canExecute() {
		return canExecute;
	}
	
	public boolean setCanExecute(boolean canExecute) {
		return this.canExecute = canExecute;
	}
}