package com.grinder.game.content.pvp.bountyhunter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.pvp.bountyhunter.reward.Artifact;
import com.grinder.game.content.pvp.bountyhunter.reward.Emblem;
import com.grinder.game.content.skill.skillable.impl.magic.InteractiveSpell;
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.model.Skill;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.areas.impl.WildernessArea;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.item.container.player.Inventory;
import com.grinder.util.ItemID;

/**
 * Handles the "Bounty Hunter" minigame. Includes Emblems, Wealthtypes, etc.
 *
 * @author Professor Oak
 * @author Stam van der Bend.
 */
public class BountyHunterManager {

	private static final List<BountyHuntPair> TARGET_PAIRS = new CopyOnWriteArrayList<>();
	private static final int WILDERNESS_LEVEL_ADDITION = 5;

	static final int TARGET_ABANDON_DELAY_SECONDS = 120;
	static final int TARGET_SEARCH_DELAY_SECONDS = 80;

	/**
	 * Processes the bounty hunter system for the specified player.
	 *
	 * @param player the {@link Player} to sequence the {@link BountyHunterManager} for.
	 */
	public static void sequence(final Player player) {

		final Optional<Player> optionalTarget = findTargetFor(player);

		final BountyHuntController controller = player.getCombat().getBountyHuntController();

		if (AreaManager.inWilderness(player)) {

			if (optionalTarget.isEmpty())
				searchNewBountyTarget(player);

		} else if (optionalTarget.isPresent()) {

			final Player target = optionalTarget.get();
			final BountyHuntController targetController = target.getCombat().getBountyHuntController();

			final int safeTimer = player.decrementAndGetSafeTimer();

			if (safeTimer == 180 || safeTimer == 120 || safeTimer == 60) {
				player.sendMessage("You have " + safeTimer + " seconds to get back to the Wilderness before you lose your target.");
				target.sendMessage("Your target has " + safeTimer + " seconds to get back to the Wilderness before they lose you as");
				target.sendMessage("target.");
			}

			if (safeTimer == 0) {

				disassemblePairIfPresent(player);

				controller.restartTargetAbandonTimer();
				targetController.restartTargetLostTimer();

				player.sendMessage("You have lost your target.");
				target.sendMessage("You have lost your target and will be given a new one shortly.");
			}
		}
	}

	/**
	 * Search a new bounty hunter target for the argued {@link Player}.
	 *
	 * Only search for a new player after {@link #TARGET_SEARCH_DELAY_SECONDS} have passed.
	 *
	 * @param player the {@link Player} in need of a target.
	 */
	private static void searchNewBountyTarget(Player player) {

		final BountyHuntController controller = player.getCombat().getBountyHuntController();

		if (controller.readyToSearchNewTarget()) {

			if (!validTargetContender(player))
				return;

			for (final Player other : WildernessArea.PLAYERS_IN_WILD) {

				if (validTargetContender(other)) {

					if (player.equals(other))
						continue;

					if (controller.hasRecentlyKilled(other))
						continue;

					final int differenceInCombatLevel = WildernessArea.getDifferenceInCombatLevel(player, other);
					final int playerWildernessCombatLevel = player.getWildernessLevel() + WILDERNESS_LEVEL_ADDITION;
					final int otherWildernessCombatLevel = other.getWildernessLevel() + WILDERNESS_LEVEL_ADDITION;

					if (differenceInCombatLevel < playerWildernessCombatLevel && differenceInCombatLevel < otherWildernessCombatLevel) {
						assign(player, other);
						break;
					}
				}
			}
			controller.restartTargetSearchTimer();
		}
	}

	/**
	 * Create a new {@link BountyHuntPair} of the two argued {@link Player} agents.
	 *
	 * @param player the first {@link Player}.
	 * @param other the second {@link Player}.
	 */
	public static void assign(final Player player, final Player other) {

		if (player.getGameMode().isSpawn() || other.getGameMode().isSpawn()) {
			return;
		}
		if (!findPairFor(player).isPresent() && !findPairFor(other).isPresent()) {

			// Create a new pair..
			final BountyHuntPair pair = new BountyHuntPair(player, other);

			// Add the pair to our list..
			TARGET_PAIRS.add(pair);


			AchievementManager.processFor(AchievementType.PANIC, player);

			player.sendMessage("You've been assigned " + other.getUsername() + " as your target!");
			other.sendMessage("You've been assigned " + player.getUsername() + " as your target!");

			// Jinglebits
			player.getPacketSender().sendJinglebitMusic(271, 0);
			other.getPacketSender().sendJinglebitMusic(271, 0);

			player.getPacketSender().sendEntityHint(other);
			other.getPacketSender().sendEntityHint(player);

			player.resetSafingTimer();
			other.resetSafingTimer();
		}
	}
	
	/**
	 * Teleports you to the current active bounty hunter target.
	 *
	 * @param player the first {@link Player}.
	 */
	public static boolean canTeleportToTarget(final Player player) {

		final Optional<Player> optionalTarget = findTargetFor(player);

		if (optionalTarget.isEmpty()) {
			player.sendMessage("You don't have an active target!");
			return false;
		}

		/*if (!player.getBountyTeleportTimer().finished()) {
			player.sendMessage("You can only cast teleport to bounty target once every 5 minutes.");
			return false;
		}*/

		final Player target = optionalTarget.get();

		if (target.getWildernessLevel() <= 0) {
			player.sendMessage("Your target is currently not in the Wilderness");
			return false;
		}

/*		if (AreaManager.RESOURCE_AREA.contains(target)) {
			player.sendMessage("You can't teleport when your target is currently in the Wilderness resource area.");
			return false;
		}
		if (AreaManager.WILDERNESS_AGILITY.contains(target)) {
			player.sendMessage("You can't teleport when your target is currently in the Wilderness agility area.");
			return false;
		}*/

		return true;
	}

	/**
	 * Teleports you to the current active bounty hunter target.
	 *
	 * @param player the first {@link Player}.
	 * @param interactiveSpell
	 */
	public static void teleportToBountyTarget(final Player player, InteractiveSpell interactiveSpell) {

		final Optional<Player> optionalTarget = findTargetFor(player);

		if (optionalTarget.isEmpty()) {
			player.sendMessage("You don't have an active target!");
			return;
		}
		final Player target = optionalTarget.get();

		TeleportHandler.teleportBountyTarget(player, target.getPosition(), player.getSpellbook().getTeleportType(), true, true, interactiveSpell);
	}


	/**
	 * Disassembles the {@link BountyHuntPair} with the argued {@link Player} if such is present.
	 *
	 * @param player the {@link Player} whose {@link BountyHuntPair} should be disassembled.
	 */
	public static void disassemblePairIfPresent(final Player player) {

		final Optional<BountyHuntPair> pairOptional = findPairFor(player);

		if (pairOptional.isPresent()) {

			final BountyHuntPair pair = pairOptional.get();

			TARGET_PAIRS.remove(pair);

			final Player p1 = pair.player1;
			final Player p2 = pair.player2;

			// Reset hints..
			p1.getPacketSender().sendEntityHintRemoval(true);
			p2.getPacketSender().sendEntityHintRemoval(true);

			p1.getCombat().getBountyHuntController().restartTargetSearchTimer();
			p2.getCombat().getBountyHuntController().restartTargetSearchTimer();

			p1.getBountyTeleportTimer().stop();
			p2.getBountyTeleportTimer().stop();
		}
	}

	/**
	 * Attempts to find a target for the argued {@link Player}.
	 *
	 * @param player the {@link Player} to find a target for.
	 *
	 * @return a {@link Optional} that may contain a {@link Player}.
	 */
	public static Optional<Player> findTargetFor(Player player) {

		final Optional<BountyHuntPair> pairOptional = findPairFor(player);

		if (pairOptional.isPresent()) {

			final BountyHuntPair pair = pairOptional.get();

			if (pair.player1.equals(player))
				return Optional.of(pair.player2);

			if (pair.player2.equals(player))
				return Optional.of(pair.player1);
		}
		return Optional.empty();
	}

	/**
	 * Attempts to find an existing {@link BountyHuntPair} for the argued {@link Player}.
	 *
	 * @see #TARGET_PAIRS
	 *
	 * @return a {@link Optional} that may contain a {@link BountyHuntPair}.
	 */
	private static Optional<BountyHuntPair> findPairFor(final Player player) {

		for (final BountyHuntPair pair : TARGET_PAIRS) {
			if (player.equals(pair.player1) || player.equals(pair.player2)) {
				return Optional.of(pair);
			}
		}

		return Optional.empty();
	}


	/**
	 * Determines the total value of emblems contained in the {@link Inventory} of the argued {@link Player}.
	 *
	 * @see Emblem
	 *
	 * @param player the {@link Player} to calculate the value for.
	 * @param convertIntoBloodMoney set to true if the value should be converted into blood money.
	 *
	 * @return the total value of all emblems held by the argued {@link Player}.
	 */
	public static int getValueForEmblems(final Player player, boolean convertIntoBloodMoney) {

		final ArrayList<Emblem> emblems = new ArrayList<>();
		
		for (final Emblem emblem : Emblem.values())
			if (player.getInventory().contains(emblem.getId()))
				emblems.add(emblem);

		if (emblems.isEmpty())
			return 0;

		int value = 0;

		for (final Emblem emblem : emblems) {
			value = getValue(player.getInventory(), convertIntoBloodMoney, emblem.getId(), value, emblem.getValue());
		}

		return value;
	}

	/**
	 * Determines the total value of artifacts contained in the {@link Inventory} of the argued {@link Player}.
	 *
	 * @see Artifact
	 *
	 * @param player the {@link Player} to calculate the value for.
	 * @param convertIntoBloodMoney set to true if the value should be converted into blood money.
	 *
	 * @return the total value of all artifacts held by the argued {@link Player}.
	 */
	public static int getValueForArtifacts(Player player, boolean convertIntoBloodMoney) {

		final ArrayList<Artifact> artifacts = new ArrayList<>();
		
		for (Artifact artifact : Artifact.values())
			if (player.getInventory().contains(artifact.getId()))
				artifacts.add(artifact);

		if (artifacts.isEmpty())
			return 0;

		int value = 0;
		
		for (final Artifact artifact : artifacts)
			value = getValue(player.getInventory(), convertIntoBloodMoney, artifact.getId(), value, artifact.getValue());

		return value;
	}

	/***
	 * Check whether the argued {@link Player} is applicable for assigning a {@link BountyHuntPair}.
	 *
	 * @param player the {@link Player} to check.
	 *
	 * @return {@code true} if the player is {@link BountyHuntPair} ready.
	 *         {@code false} otherwise
	 */
	private static boolean validTargetContender(final Player player) {
		return !(player == null || !player.isRegistered()
				|| !AreaManager.inWilderness(player)
				|| player.getWildernessLevel() <= 0 || player.getWildernessLevel() > 20 || player.isUntargetable() || player.getHitpoints() <= 0 || player.hasPendingTeleportUpdate()
				|| findPairFor(player).isPresent());
	}

	private static int getValue(final Inventory inventory, boolean convertIntoBloodMoney, int id, int stackedValue, int additionalValue) {

		final int amount = inventory.getAmount(id);

		if (amount > 0) {

			if (convertIntoBloodMoney) {
				inventory.delete(id, amount);
				inventory.add(ItemID.BLOOD_MONEY, (additionalValue * amount));
			}

			stackedValue += (additionalValue * amount);
		}
		return stackedValue;
	}
}
