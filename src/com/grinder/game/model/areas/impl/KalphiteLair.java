package com.grinder.game.model.areas.impl;

import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.monster.boss.impl.KalphiteQueenBoss;
import com.grinder.game.entity.agent.npc.monster.boss.minion.BossMinion;
import com.grinder.game.entity.agent.npc.monster.boss.minion.BossMinionPolicy;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.areas.Area;
import com.grinder.game.task.TaskManager;
import com.grinder.util.NpcID;
import com.grinder.util.ObjectID;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The kalphite lair
 *
 * @see KalphiteQueenBoss
 *
 * @author 2012
 */
public class KalphiteLair extends Area {

	private final List<Cocoon> cocoonList;

	/**
	 * Represents the kalphite lair
	 */
	public KalphiteLair() {
		super(new Boundary(3463, 3510, 9475, 9520));
		cocoonList = boundaries().get(0)
				.objectStream(0)
				.filter(object -> object.getId() == ObjectID.COCOON_2)
				.map(Cocoon::new)
				.collect(Collectors.toList());

	}

	public Optional<Cocoon> findNearbyCocoon(KalphiteQueenBoss boss){
		return cocoonList.stream().filter(cocoon -> cocoon.inProximity(boss))
				.findFirst();
	}

	@Override public void process(Agent agent) { }
	@Override public boolean canTeleport(Player player) {
		return true;
	}
	@Override public boolean canAttack(Agent attacker, Agent target) {
		return true;
	}
	@Override public void defeated(Player player, Agent agent) { }
	@Override public boolean canTrade(Player player, Player target) {
		return true;
	}
	@Override public boolean isMulti(Agent agent) {
		return true;
	}
	@Override public boolean canEat(Player player, int itemId) {
		return true;
	}
	@Override public boolean canDrink(Player player, int itemId) {
		return true;
	}
	@Override public boolean dropItemsOnDeath(Player player, Optional<Player> killer) {
		return true;
	}
	@Override public boolean handleDeath(Player player, Optional<Player> killer) {
		return false;
	}
	@Override public void onPlayerRightClick(Player player, Player rightClicked, int option) { }
	@Override public boolean handleObjectClick(Player player, GameObject obj, int actionType) {
		return false;
	}
	@Override public boolean handleDeath(NPC npc) { return false; }

	public final static class Cocoon {

		final GameObject gameObject;

		Cocoon(GameObject gameObject) {
			this.gameObject = gameObject;
		}

		public void spawnWorker(KalphiteQueenBoss boss){
			TaskManager.submit(1, () -> new BossMinion<>(boss,
					NpcID.KALPHITE_WORKER,
					gameObject.getPosition().copy().move(gameObject.getFacing()),
					BossMinionPolicy.NO_RESPAWN,
					BossMinionPolicy.REMOVE_WHEN_BOSS_REMOVED,
					BossMinionPolicy.ATTACK_PREFERRED_OPPONENT)
					.spawn());
		}

		boolean inProximity(KalphiteQueenBoss boss){
			return gameObject.getPosition().getDistance(boss.getPosition()) < 6;
		}
	}

	@Override
	public boolean isCannonProhibited() {
		return true;
	}
}