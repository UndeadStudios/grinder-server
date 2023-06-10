package com.grinder.game.model.commands.impl;

import com.grinder.game.content.skill.skillable.impl.magic.Teleporting;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.movement.teleportation.TeleportHandler;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;

public class TrainingCommand implements Command {

	@Override
	public String getSyntax() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Teleports you to training area's.";
	}

    @Override
    public void execute(Player player, String command, String[] parts) {

		new DialogueBuilder(DialogueType.OPTION).setOptionTitle("Choose an option.")
				.firstOption("Low Level Training.", player2 -> {
					new DialogueBuilder(DialogueType.OPTION).setOptionTitle("Choose an option.")
							.firstOption("Lumbridge Chickens.", player3 -> {
								if (TeleportHandler.checkReqs(player, new Position(3238, 3295), true, false, player.getSpellbook().getTeleportType())) {
									TeleportHandler.teleport(player, new Position(3238, 3295), player.getSpellbook().getTeleportType(), false, true);
								}
							}).secondOption("Lumbridge Cows.", player3 -> {
								if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.LUMBRIDGE_FARM.getPosition(), true, false, player.getSpellbook().getTeleportType())) {
									TeleportHandler.teleport(player, Teleporting.TeleportLocation.LUMBRIDGE_FARM.getPosition().clone().add(Misc.random(5), Misc.random(5)), player.getSpellbook().getTeleportType(), false, true);
								}
							}).thirdOption("Mini Kalphite's.", player3 -> {
								if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.KALPHITE_LAIR.getPosition(), true, false, player.getSpellbook().getTeleportType())) {
									TeleportHandler.teleport(player, Teleporting.TeleportLocation.KALPHITE_LAIR.getPosition().clone().add(Misc.random(5), Misc.random(1)), player.getSpellbook().getTeleportType(), false, true);
								}
							}).fourthOption("Al-Kharid Warrior's.", player3 -> {
								if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.AL_KHARID_TEMPLE.getPosition(), true, false, player.getSpellbook().getTeleportType())) {
									TeleportHandler.teleport(player, Teleporting.TeleportLocation.AL_KHARID_TEMPLE.getPosition().clone().add(Misc.random(1), Misc.random(1)), player.getSpellbook().getTeleportType(), false, true);
								}
							}).fifthOption("Hosidius Sand Crabs.", player3 -> {
								if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.SOUTH_HOSIDIUS.getPosition(), true, false, player.getSpellbook().getTeleportType())) {
									TeleportHandler.teleport(player, Teleporting.TeleportLocation.SOUTH_HOSIDIUS.getPosition().clone().add(Misc.random(1), Misc.random(1)), player.getSpellbook().getTeleportType(), false, true);
								}
							}).start(player);


				}).secondOption("Intermediate Level Training.", player2 -> {
					new DialogueBuilder(DialogueType.OPTION).setOptionTitle("Choose an option.")
							.firstOption("Al-Kharid Bandits.", player3 -> {
								if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.BANDITS_CAMP.getPosition(), true, false, player.getSpellbook().getTeleportType())) {
									TeleportHandler.teleport(player, Teleporting.TeleportLocation.BANDITS_CAMP.getPosition().clone().add(Misc.random(3), Misc.random(3)), player.getSpellbook().getTeleportType(), false, true);
								}
							}).secondOption("Lighthouse Dagannoth's.", player3 -> {
								if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.DAGANNOTHS_LAIR.getPosition(), true, false, player.getSpellbook().getTeleportType())) {
									TeleportHandler.teleport(player, Teleporting.TeleportLocation.DAGANNOTHS_LAIR.getPosition().clone().add(Misc.random(3), Misc.random(3)), player.getSpellbook().getTeleportType(), false, true);
								}
							}).thirdOption("Ice Giants & Ice Warriors.", player3 -> {
								if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.ASGARNIAN_ICE_CAVE.getPosition(), true, false, player.getSpellbook().getTeleportType())) {
									TeleportHandler.teleport(player, Teleporting.TeleportLocation.ASGARNIAN_ICE_CAVE.getPosition(), player.getSpellbook().getTeleportType(), false, true);
								}
							}).fourthOption("Hill Giants & Moss Giants.", player3 -> {
								if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.EDGEVILLE_DUNGEON.getPosition(), true, false, player.getSpellbook().getTeleportType())) {
									TeleportHandler.teleport(player, Teleporting.TeleportLocation.EDGEVILLE_DUNGEON.getPosition(), player.getSpellbook().getTeleportType(), false, true);
								}
							}).fifthOption("Fire Giants & Hellhounds.", player3 -> {
								if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.BRIMHAVEN_DUNGEON.getPosition(), true, false, player.getSpellbook().getTeleportType())) {
									TeleportHandler.teleport(player, Teleporting.TeleportLocation.BRIMHAVEN_DUNGEON.getPosition(), player.getSpellbook().getTeleportType(), false, true);
								}
							}).start(player);



				}).thirdOption("High Level Training.", player2 -> {
					new DialogueBuilder(DialogueType.OPTION).setOptionTitle("Choose an option.")
							.firstOption("Lesser & Greater Demons.", player3 -> {
								if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.CHASM_OF_FIRE.getPosition(), true, false, player.getSpellbook().getTeleportType())) {
									TeleportHandler.teleport(player, Teleporting.TeleportLocation.CHASM_OF_FIRE.getPosition(), player.getSpellbook().getTeleportType(), false, true);
								}
							}).secondOption("Black Dragons & Demons.", player3 -> {
								if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.BRIMHAVEN_DUNGEON.getPosition(), true, false, player.getSpellbook().getTeleportType())) {
									TeleportHandler.teleport(player, Teleporting.TeleportLocation.BRIMHAVEN_DUNGEON.getPosition(), player.getSpellbook().getTeleportType(), false, true);
								}
							}).thirdOption("Catacombs of Kourend.", player3 -> {
								if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.CATACOMBS_OF_KOUREND.getPosition(), true, false, player.getSpellbook().getTeleportType())) {
									TeleportHandler.teleport(player, Teleporting.TeleportLocation.CATACOMBS_OF_KOUREND.getPosition(), player.getSpellbook().getTeleportType(), false, true);
								}
							}).fourthOption("Iron & Steel Dragons.", player3 -> {
								if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.BRIMHAVEN_DUNGEON.getPosition(), true, false, player.getSpellbook().getTeleportType())) {
									TeleportHandler.teleport(player, Teleporting.TeleportLocation.BRIMHAVEN_DUNGEON.getPosition(), player.getSpellbook().getTeleportType(), false, true);
								}
							}).fifthOption("Mor Ul Rek Tzhaar's.", player3 -> {
								if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.MOR_UI_REK.getPosition(), true, false, player.getSpellbook().getTeleportType())) {
									TeleportHandler.teleport(player, Teleporting.TeleportLocation.MOR_UI_REK.getPosition(), player.getSpellbook().getTeleportType(), false, true);
								}
							}).start(player);



				}).fourthOption("Elite Level Training.", player2 -> {
					new DialogueBuilder(DialogueType.OPTION).setOptionTitle("Choose an option.")
							.firstOption("Hydra's & Wyrms", player3 -> {
								if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.ALCHEMICAL_HYDRA.getPosition(), true, false, player.getSpellbook().getTeleportType())) {
									TeleportHandler.teleport(player, Teleporting.TeleportLocation.ALCHEMICAL_HYDRA.getPosition(), player.getSpellbook().getTeleportType(), false, true);
								}
							}).secondOption("Demonic Gorilla's.", player3 -> {
								if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.DEMONIC_GORILLAS.getPosition(), true, false, player.getSpellbook().getTeleportType())) {
									TeleportHandler.teleport(player, Teleporting.TeleportLocation.DEMONIC_GORILLAS.getPosition(), player.getSpellbook().getTeleportType(), false, true);
								}
							}).thirdOption("Adamant & Rune Dragons.", player3 -> {
								if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.LITHKREN_VAULT.getPosition(), true, false, player.getSpellbook().getTeleportType())) {
									TeleportHandler.teleport(player, Teleporting.TeleportLocation.LITHKREN_VAULT.getPosition(), player.getSpellbook().getTeleportType(), false, true);
								}
							}).fourthOption("Ancient Wyvern.", player3 -> {
								if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.ASGARNIAN_ICE_CAVE.getPosition(), true, false, player.getSpellbook().getTeleportType())) {
									TeleportHandler.teleport(player, Teleporting.TeleportLocation.ASGARNIAN_ICE_CAVE.getPosition(), player.getSpellbook().getTeleportType(), false, true);
								}
							}).fifthOption("Thermonuclear Smoke Devil.", player3 -> {
								if (TeleportHandler.checkReqs(player, Teleporting.TeleportLocation.SMOKE_DEVIL_DUNGEON.getPosition(), true, false, player.getSpellbook().getTeleportType())) {
									TeleportHandler.teleport(player, Teleporting.TeleportLocation.SMOKE_DEVIL_DUNGEON.getPosition(), player.getSpellbook().getTeleportType(), false, true);
								}
							}).start(player);



				}).addCancel("Nevermind.").start(player);

				player.sendMessage("Reminder: The most efficient way of training is through slayer tasks, bandits, cyclops (defenders), high alches, and cannon/chins!");
//		if (TeleportHandler.checkReqs(player, new Position(3080, 3507), true, false, player.getSpellbook().getTeleportType())) {
//			TeleportHandler.teleport(player, new Position(3080 + Misc.getRandomInclusive(2), 3507 + Misc.getRandomInclusive(3), 0), player.getSpellbook().getTeleportType(), false, true);
//		}
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
