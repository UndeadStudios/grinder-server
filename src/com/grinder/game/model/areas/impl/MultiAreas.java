package com.grinder.game.model.areas.impl;

import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.areas.Area;
import com.grinder.game.model.areas.AreaManager;

import java.util.ArrayList;
import java.util.Optional;

public class MultiAreas extends Area {

	private final ArrayList<Area> nested = new ArrayList<>();

/*	public static void main(String[] args) {

		new MultiAreas().boundaries().forEach(
				boundary -> {
					System.out.println("Area area = new Area("+boundary.getX()+", "+boundary.getY()+", "+boundary.getX2()+", "+boundary.getY2()+");");
				}
		);
	}*/
	public MultiAreas() {
		// NON WILDERNESS MULTI AREAS BELOW!
		super(
				new Boundary(2944, 3007, 3304, 3455), // Falador
				new Boundary(2253, 2290, 4676, 4715), // King black dragon area
				new Boundary(1856, 1983, 4352, 4415), // Dagannoth area
				new Boundary(2368, 2431, 5056, 5119), // Fight caves area
				new Boundary(2624, 2687, 2560, 2623), // Pest control area
				new Boundary(3140, 3154,  4644,4658), // Mutant Tarn New Zone
				new Boundary(1950, 2047, 5566, 5631), // Crash Site Cavern Outside
				new Boundary(2067, 2089, 5640, 5688), // Crash Site Cavern Inside 1
				new Boundary(2090, 2166, 5668, 5688), // Crash Site Cavern Inside 2
				new Boundary(1853, 1919,  5185,5248), // Stronghold 1st floor
				new Boundary(1985, 2046,  5185,5246), // Stronghold 2st floor
				new Boundary(2114, 2176,  5250,5311), // Stronghold 3rd floor
				new Boundary(2305, 2366,  5183,5248), // Stronghold 4th floor
				new Boundary(2304, 2328,  3779,3818), // YAKS
				new Boundary(2245, 2299,  2562,2621), // Legendary boss dungeon
				new Boundary(2650, 2725,  3712,3733), // Rock Crabs
				new Boundary(3260, 3340,  9475,9550), // Kalphites area
				new Boundary(3266, 3392,  2880,2992), // Crocodiles
				new Boundary(2439, 2490,  10120,10169), // Dagannoth Area
				new Boundary(2377, 2500, 9757, 9838), // Slayer Stronghold
				new Boundary(2949, 2969, 9771, 9797), // Taverly magic axe

				// NEW
				new Boundary(2872, 2901, 3717, 3766), // Kamil area
				new Boundary(2705, 2734, 4308, 4332), // Mimic area
				// STOP NEW

				//new Boundary(1732,  1798,5315, 5370), // Ancient cave
				new Boundary(3154, 3294, 9874, 9923), // Varrock Dungeon (moss giants)
				new Boundary(3131, 3327, 3467, 3524), // VarrockWilderness
				new Boundary(2374, 2422, 5126, 5176), new Boundary(2627, 2677, 4549, 4604),
				new Boundary(2846, 2864, 9626, 9649), new Boundary(2561, 2572, 9503, 9511),
				new Boundary(2824, 2879, 9544, 9599), new Boundary(2892, 2936, 4428, 4471),
				new Boundary(2497, 2510, 3891, 3907), new Boundary(3008, 3071, 4799, 4863),
				new Boundary(2944, 3007, 3304, 3455), new Boundary(2816, 2943, 5184, 5375),
				new Boundary(2892, 2929, 3596, 3623), new Boundary(2816, 2879, 3456, 3519),
				new Boundary(2651, 2685, 3411, 3446), new Boundary(3264, 3327, 3136, 3199),
				new Boundary(3094, 3124, 3145, 3176), new Boundary(3105, 3135, 3233, 2363),
				new Boundary(3049, 3135, 3392, 3406), new Boundary(3072, 3135, 3407, 3455),
				new Boundary(3061, 3071, 3418, 3448), new Boundary(2504, 2559, 3200, 3246),
				new Boundary(3198, 3327, 3648, 3775), new Boundary(3124, 3159, 3839, 3903),
				new Boundary(3159, 3327, 3775, 3903), new Boundary(3200, 3455, 3840, 3967),
				new Boundary(3008, 3135, 3856, 3903), new Boundary(2972, 3007, 3911, 3930),
				new Boundary(2368, 2431, 3072, 3135), new Boundary(1231, 1254, 1236, 1257),
				new Boundary(2639, 2653, 10421, 10428), new Boundary(3220, 3248, 10332, 10352),
				new Boundary(2880, 2943, 3690, 3775), new Boundary(2610, 2617, 9501, 9526),
				new Boundary(2560, 2623, 9472, 9535), new Boundary(2501, 2538, 4629, 4663),
				new Boundary(2498, 2547, 3023, 3061), new Boundary(3136, 3199, 2944, 3007),
				new Boundary(2690, 2712, 2703, 2715), new Boundary(2854, 2879, 9907, 9964),
				new Boundary(2962, 3002, 4370, 4399), new Boundary(3306, 3328, 9357, 9393),
				new Boundary(3305, 3305, 9373, 9378));
	}

	@Override
	public void process(Agent agent) {
		for(Area nest : nested){
			if(nest.contains(agent)){
				nest.process(agent);
			}
		}
	}

	@Override
	public void enter(Agent agent) {
		//if (agent != null && agent.isPlayer()) {
			super.enter(agent);
			for(Area nest : nested){
				if(nest.contains(agent)){
					nest.enter(agent);
				}
			}
	//	}

	}

	@Override
	public void leave(Agent agent) {
		//if (agent != null && agent.isPlayer()) {
			super.leave(agent);
			for(Area nest : nested){
				if(nest.contains(agent)){
					nest.leave(agent);
				}
			}
		//}
	}

	@Override
	public boolean canTeleport(Player player) {
		for(Area nest : nested){
			if(nest.contains(player)){
				return nest.canTeleport(player);
			}
		}
		return true;
	}

	@Override
	public boolean canAttack(Agent attacker, Agent target) {

		for(Area nest : nested){
			if(AreaManager.inside(attacker.getPosition(), nest)){
				return nest.canAttack(attacker, target);
			}
		}

		if(attacker instanceof Player && target instanceof Player){
			final boolean attackerInWild = AreaManager.inWilderness(attacker);
			final boolean targetInWild = AreaManager.inWilderness(target);
			return attackerInWild && targetInWild;
		}

		return true;
	}

	@Override
	public boolean canTrade(Player player, Player target) {
		for(Area nest : nested){
			if(nest.contains(player)){
				return nest.canTrade(player, target);
			}
		}
		return true;
	}

	@Override
	public boolean isMulti(Agent agent) {
		return true;
	}


	@Override
	public boolean canEat(Player player, int itemId) {
		for(Area nest : nested){
			if(nest.contains(player)){
				return nest.canEat(player, itemId);
			}
		}
		return true;
	}

	@Override
	public boolean canDrink(Player player, int itemId) {
		for(Area nest : nested){
			if(nest.contains(player)){
				return nest.canDrink(player, itemId);
			}
		}
		return true;
	}

	@Override
	public boolean dropItemsOnDeath(Player player, Optional<Player> killer) {
		for(Area nest : nested){
			if(nest.contains(player)){
				return nest.dropItemsOnDeath(player, killer);
			}
		}
		return true;
	}

	@Override
	public boolean handleDeath(Player player, Optional<Player> killer) {
		for(Area nest : nested){
			if(nest.contains(player)){
				return nest.handleDeath(player, killer);
			}
		}
		return false;
	}

	@Override
	public void onPlayerRightClick(Player player, Player rightClicked, int option) {
		for(Area nest : nested){
			if(nest.contains(player)){
				nest.onPlayerRightClick(player, rightClicked, option);
			}
		}
	}

	@Override
	public void defeated(Player player, Agent agent) {
		for(Area nest : nested){
			if(nest.contains(player)){
				nest.defeated(player, agent);
			}
		}
	}

	@Override
	public boolean handleObjectClick(Player player, GameObject obj, int actionType) {
		for(Area nest : nested){
			if(nest.contains(player)){
				return nest.handleObjectClick(player, obj, actionType);
			}
		}
		return false;
	}

	@Override
	public boolean handleDeath(NPC npc) {
		for(Area nest : nested){
			if(nest.contains(npc)){
				return nest.handleDeath(npc);
			}
		}
		return false;
	}

	public ArrayList<Area> getNested() {
		return nested;
	}
}
