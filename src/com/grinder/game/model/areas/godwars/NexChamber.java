package com.grinder.game.model.areas.godwars;

import com.grinder.game.World;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.zaros.Nex;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.areas.Area;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.NpcID;

import java.util.Optional;
import java.util.TimerTask;

/**
 * @author Savions.
 */
public class NexChamber extends Area {

	public static final int LOBBY_SOUND_TRACK = 709, FIGHT_SOUND_TRACK = 710;

	private boolean active = false;
	private Nex nex;

	public NexChamber(Boundary... boundaries) {
		super(new Boundary(2909, 2941, 5188, 5218));
	}

	@Override public void enter(Agent agent) {
		super.enter(agent);
		if (agent instanceof Player) {
			final Player player = (Player) agent;
			player.getPacketSender().sendMusic(FIGHT_SOUND_TRACK, 4, 25);
			if (!active && amountOfPlayers() == 1) {
				active = true;
				spawnNex();
			}
		}
	}

	private void spawnNex() {
		TaskManager.submit(new Task(10, false) {

			@Override protected void execute() {
				if (amountOfPlayers() > 0 && nex == null) {
					nex = new Nex(NpcID.NEX);
					nex.setArea(NexChamber.this);
					nex.spawn();
					nex.start();
				} else {
					active = false;
				}
				stop();
			}
		});
	}

	@Override public void leave(Agent agent) {
		super.leave(agent);
		if (agent instanceof Player && amountOfPlayers() < 1) {
			destroyNex();
		}
	}

	private void destroyNex() {
		active = false;
		if (nex != null) {
			if (nex.isActive())
				World.getNpcRemoveQueue().add(nex);
			nex.destroy();
			resetNex();
		}
	}

	public void resetNex() {
		nex = null;
	}

	@Override public void process(Agent agent) {

	}

	@Override public void defeated(Player player, Agent agent) {
		final int id = agent.getAsNpc().getId();
		switch(id) {
			case 11278:
			case 11279:
			case 11280:
			case 11281:
			case 11282:
				TaskManager.submit(new Task(60, false) {

					@Override protected void execute() {
						if (active = amountOfPlayers() > 0) {
							spawnNex();
						}
						stop();
					}
				});
				break;
		}
	}

	@Override public void onPlayerRightClick(Player player, Player rightClicked, int option) {

	}

	@Override public boolean isMulti(Agent agent) {
		return true;
	}

	@Override public boolean canTeleport(Player player) {
		return true;
	}

	@Override public boolean canAttack(Agent attacker, Agent target) {
		if (attacker.isPlayer() && target.isPlayer()) {
			return false;
		}
		if (target instanceof Nex && ((Nex) target).locked()) {
			return false;
		}
		return true;
	}

	@Override public boolean canTrade(Player player, Player target) {
		return false;
	}

	@Override public boolean canDrink(Player player, int itemId) {
		return true;
	}

	@Override public boolean canEat(Player player, int itemId) {
		return true;
	}

	@Override public boolean dropItemsOnDeath(Player player, Optional<Player> killer) {
		return true;
	}

	@Override public boolean handleObjectClick(Player player, GameObject obj, int actionType) {
		return false;
	}

	@Override public boolean handleDeath(Player player, Optional<Player> killer) {
		return false;
	}

	@Override public boolean handleDeath(NPC npc) {
		return false;
	} //TODO if npc is nex handle death
}
