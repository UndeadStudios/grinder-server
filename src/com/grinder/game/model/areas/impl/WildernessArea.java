package com.grinder.game.model.areas.impl;

import com.grinder.game.content.object.Obelisks;
import com.grinder.game.content.pvp.bountyhunter.PlayerKillRewardManager;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.AgentExtKt;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.Area;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.interfaces.IntefaceID;
import com.grinder.net.packet.PacketSender;
import com.grinder.util.MiscUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class WildernessArea extends Area {

	public static final List<Player> PLAYERS_IN_WILD = new CopyOnWriteArrayList<>();

	private static final boolean DISPLAY_WARNING_INTERFACE = true;

	public static int getLevel(int y) {
		int wildernessLevel = ((((y > 6400 ? y - 6400 : y) - 3520) / 8) + 1);
		if (wildernessLevel > 99)
			wildernessLevel = 99;
		return wildernessLevel;
	}

	public static boolean multi(int x, int y) {
		return x >= 3155 && y >= 3798
				|| x >= 3135 && x <= 3327 && y >= 3523 && y <= 3647
				|| x >= 3198 && x <= 3327 && y >= 3648 && y <= 3775
				|| x >= 3124 && x <= 3159 && y >= 3839 && y <= 3903
				|| x >= 3159 && x <= 3327 && y >= 3775
				|| x >= 2125 && x <= 2164 && y >= 4678 && y <= 4717 // Merodach Zone
				|| x >= 3008 && x <= 3135 && y >= 3856 && y <= 3903
				|| x >= 3008 && x <= 3071 && y >= 3600 && y <= 3711
				|| x >= 2972 && x <= 3007 && y >= 3911 && y <= 3930
				|| x >= 2944 && x <= 2960 && y >= 3814 && y <= 3827
				|| x >= 3072 && x <= 3135 && y >= 3609 && y <= 3647
				|| x >= 3136 && x <= 3263 && y >= 10048 && y <= 10239
				|| x >= 3064 && x <= 3071 && y >= 10253 && y <= 10261
				|| x >= 2253 && x <= 2290 && y >= 4676 && y <= 4715;
	}

	/**
	 * Calculates the combat level difference in Wilderness PVP combat.
	 *
	 * @param combatLevel      the combat level of the first actor.
	 * @param otherCombatLevel the combat level of the other actor.
	 * @return the difference in combat level.
	 */
	public static int getDifferenceInCombatLevel(final int combatLevel, final int otherCombatLevel) {
		if (combatLevel > otherCombatLevel)
			return (combatLevel - otherCombatLevel);
		else if (otherCombatLevel > combatLevel)
			return (otherCombatLevel - combatLevel);
		else
			return 0;
	}

	public static int getDifferenceInCombatLevel(final Agent agent, final Agent other) {
		final int combatAgent = agent instanceof Player ? ((Player) agent).getSkillManager().calculateCombatLevel() : ((NPC) agent).fetchDefinition().getCombatLevel();
		final int combatOther = other instanceof Player ? ((Player) other).getSkillManager().calculateCombatLevel() : ((NPC) other).fetchDefinition().getCombatLevel();
		return getDifferenceInCombatLevel(combatAgent, combatOther);
	}

	public WildernessArea(Boundary... boundaries){
		super(boundaries);
	}
	public WildernessArea() {
		super(new Boundary(2940, 3392, 3525, 3968),
				new Boundary(2986, 3012, 10338, 10366),
				//new Boundary(3653, 3720, 3441, 3538),
				new Boundary(3650, 3653, 3457, 3472),
				new Boundary(3220, 3248, 10332, 10352),
				new Boundary(3150, 3199, 3796, 3869),
				new Boundary(3078, 3133, 9923, 10003),
				new Boundary(2994, 3041, 3733, 3790),
				new Boundary(3064, 3071, 10253, 10261),
				new Boundary(2250, 2290, 4676, 4716), // KBD Zone
				new Boundary(2125, 2164, 4678, 4717), // Merodach Zone
				new Boundary(3393, 3445, 4672, 4795), // Keelow tournament zone
				new Boundary(3446, 3455, 4671, 4718), // Keelow tournament zone1
				new Boundary(3452, 3455, 4719, 4742), // Keelow tournament zone2
				new Boundary(3446, 3455, 4753, 4799), // Keelow tournament zone3
				new Boundary(3136, 3263, 10048, 10239));
	}


	public static void onCrossDitch(Player player){

		if(DISPLAY_WARNING_INTERFACE) {
			if (!EntityExtKt.getBoolean(player, Attribute.SEEN_WILDERNESS_WARNING, false)){
				EntityExtKt.setBoolean(player, Attribute.SEEN_WILDERNESS_WARNING, true, false);
				PlayerExtKt.openInterface(player, IntefaceID.WILDERNESS_WARNING_INTERFACE);
				PlayerExtKt.resetInteractions(player, true, false);
			}
		}
	}

	@Override
	public void enter(Agent agent) {

		super.enter(agent);
		if (agent instanceof Player) {

			final Player player = agent.getAsPlayer();
			final PacketSender packetSender = player.getPacketSender();

			packetSender.sendInteractionOption("Attack", 2, true);
			packetSender.sendWalkableInterface(IntefaceID.WILDERNESS_WALKABLE_INTERFACE);

			if (!PLAYERS_IN_WILD.contains(player))
				PLAYERS_IN_WILD.add(player);

		}
	}

	@Override
	public void leave(Agent agent) {
		super.leave(agent);
		if (agent != null && agent.isPlayer()) {
			Player player = agent.getAsPlayer();
			player.getPacketSender().sendWalkableInterface(-1);
			player.getPacketSender().sendInteractionOption("null", 2, true);
			player.getPacketSender().sendWalkableInterface(-1);
			player.setWildernessLevel(0);
			PLAYERS_IN_WILD.remove(player);
		}
	}

	@Override
	public void process(Agent agent) {
		if (agent.isPlayer()) {
			Player player = agent.getAsPlayer();
			final int level = MiscUtils.getWildernessLevelFrom(player, player.getPosition());
			player.setWildernessLevel(level);
			if (level > 0 && level < 6)
				player.setWildernessLevel(level + 5);
			else
				player.setWildernessLevel(level);
			player.getPacketSender().sendString(199, "Level: " + player.getWildernessLevel());
		}
	}

	@Override
	public boolean canTeleport(Player player) {
		return true;
	}

	@Override
	public boolean canAttack(Agent attacker, Agent target) {
		final boolean inWilderness = AgentExtKt.inWilderness(attacker);
		final boolean targetInWilderness = AgentExtKt.inWilderness(target);
		if (attacker.isPlayer()) {
			if (target.isPlayer()) {
				 if (inWilderness && !targetInWilderness) {
					attacker.getAsPlayer().sendMessage("That player can't be attacked, because they aren't in the Wilderness.");
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public boolean canTrade(Player player, Player target) {
		return true;
	}

	@Override
	public boolean isMulti(Agent agent) {
		int x = agent.getPosition().getX();
		int y = agent.getPosition().getY();
		return multi(x, y);
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
		if (agent.isPlayer()) {
			PlayerKillRewardManager.killedOpponent(player, agent.getAsPlayer());
		}
	}

	@Override
	public boolean handleObjectClick(Player player, GameObject obj, int actionType) {
		return Obelisks.activate(player, obj.getId());
	}

	@Override
	public boolean handleDeath(NPC npc) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		return "Wilderness";
	}


	@NotNull
	public static String getWarningMessage(int wildernessLevel, Position position) {
		final StringBuilder warning = new StringBuilder();
		warning.append("Are you sure you want to teleport there? ");
		if (wildernessLevel > 0) {
			warning.append("It's in level @red@").append(wildernessLevel).append(" @bla@Wilderness! ");
			if (WildernessArea.multi(position.getX(), position.getY())) {
				warning.append(
						"Additionally, @red@it's a multi zone@bla@. Other players may attack you simultaneously.");
			} else {
				warning.append("Other players will be able to attack you.");
			}
		} else {
			warning.append("Other players will be able to attack you.");
		}
		return warning.toString();
	}
}
