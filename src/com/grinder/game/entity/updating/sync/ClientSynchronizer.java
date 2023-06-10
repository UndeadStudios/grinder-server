package com.grinder.game.entity.updating.sync;

import com.grinder.game.entity.agent.AgentList;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.util.benchmark.SimpleBenchMarker;

/**
 * The {@link ClientSynchronizer} manages the update sequence which keeps clients synchronized with the in-game world.
 * There are two implementations distributed with Apollo: {@link SequentialClientSynchronizer} which is optimized for a
 * single-core/single-processor machine and {@link ParallelClientSynchronizer} which is optimized for a multi-processor/
 * multi-core machines.
 * <p>
 * To switch between the two synchronizer implementations, edit the {@code synchronizers.xml} configuration file. The
 * default implementation is currently {@link ParallelClientSynchronizer} as the vast majority of machines today have
 * two or more cores.
 *
 * @author Graham
 */
public abstract class ClientSynchronizer {

	/**
	 * Synchronizes the state of the clients with the state of the server.
	 *
	 * @param players The {@link AgentList} containing the {@link Player}s.
	 * @param npcs The {@link AgentList} containing the {@link NPC}s.
	 */
	public abstract void synchronize(SimpleBenchMarker benchMarker, AgentList<Player> players, AgentList<NPC> npcs);

}