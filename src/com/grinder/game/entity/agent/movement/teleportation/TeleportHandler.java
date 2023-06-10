package com.grinder.game.entity.agent.movement.teleportation;

import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.item.MorphItems;
import com.grinder.game.content.minigame.pestcontrol.PestControl;
import com.grinder.game.content.miscellaneous.QuestTab;
import com.grinder.game.content.miscellaneous.QuestTab.Tab;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.content.skill.skillable.impl.magic.InteractiveSpell;
import com.grinder.game.definition.NpcDefinition;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.movement.PlayerMotion;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.agent.player.event.PlayerEvents;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.*;
import com.grinder.game.model.areas.Area;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.areas.impl.WildernessArea;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueOptions;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.model.projectile.ProjectileTemplate;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.game.task.impl.TimedObjectReplacementTask;
import com.grinder.util.*;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TeleportHandler {

	/**
	 * Teleports a player to the target location.
	 *
	 * @param player
	 *            The player teleporting.
	 * @param targetLocation
	 *            The location to teleport to.
	 * @param requestType
	 *            The requested type of teleport.
	 */
	public static void teleport(
			Player player,
			Position targetLocation,
			TeleportType requestType,
			boolean wildernessWarning,
			boolean wildyCheck) {
		teleport(player, targetLocation, requestType, wildernessWarning, wildyCheck, () -> {});
	}

	public static void teleportNoReq(
			Player player,
			Position targetLocation,
			TeleportType requestType,
			boolean wildernessWarning,
			boolean wildyCheck) {
		teleportNoReqs(player, targetLocation, requestType, wildernessWarning, wildyCheck, () -> {});
	}

	public static void teleportNoCombatChecks(
			Player player,
			Position targetLocation,
			TeleportType requestType,
			boolean wildernessWarning,
			boolean wildyCheck) {
		teleportNoCombatReqs(player, targetLocation, requestType, wildernessWarning, wildyCheck, () -> {});
	}

	public static void teleport(Player player, Position targetLocation, TeleportType requestType, boolean wildernessWarning, boolean wildyCheck, Runnable onArrival) {

		final boolean ignoreReqs = requestType.ignoreTeleportRequirements();
		final int wildernessLevel = wildyCheck ? WildernessArea.getLevel(targetLocation.getY()) : 0;

		if (checkReqs(player, targetLocation, wildyCheck, ignoreReqs, requestType)) {

			if (wildernessWarning && checkWildernessWarning(player, targetLocation, requestType, wildyCheck, wildernessLevel))
				return;

			Optional<NPC> wizard = findTeleportWizard(player, NpcID.ANCIENT_WIZARD);
			if(wizard.isEmpty() && requestType == TeleportType.AUBURY && !PlayerUtil.isStaff(player)) {
				Logging.log("teleportbug", "" + player.getUsername() +" teleported from coords " + player.getPosition() +"");
			}

			if(wizard.isPresent() && player.getPosition().isWithinDistance(wizard.get().getPosition(), 3) && requestType == TeleportType.AUBURY) {

				NPC wizardNpc = wizard.get();

				if (Misc.random(2) == 1) {
					wizardNpc.say("Sallamander doe ing!");
				} else if (Misc.random(2) == 2) {
					wizardNpc.say("Sallamander kazaak ita!");
				} else if (Misc.random(3) == 2) {
					wizardNpc.say("Sallamander vira eek!");
				} else if (Misc.random(2) == 1) {
					wizardNpc.say("Sallamander fenkstertain trio!");
				}
				player.getPacketSender().sendAreaPlayerSound(127, 10, 1, 0);
				player.getPoints().increase(AttributeManager.Points.WIZARD_TELEPORTS, 1); // Increase points
				wizardNpc.performAnimation(new Animation(1166));
				wizardNpc.performGraphic(new Graphic(108, GraphicHeight.MIDDLE, Priority.HIGH));
			}

			if (player.inPestControl()) {
				PestControl.removePlayerFromGame(player);
			}


			final TeleportType teleportType = requestType;
			final PlayerMotion motion = player.getMotion();

			motion.clearSteps();

			if(teleportType != TeleportType.HOME)
				motion.update(MovementStatus.DISABLED);

			player.getPacketSender().sendMinimapFlagRemoval();

			onTeleporting(player);
			SkillUtil.stopSkillable(player);

			player.performAnimation(teleportType.getAnimation(0));
			player.performGraphic(teleportType.getGraphic(0));

			teleportType
					.findTeleportSound()
					.ifPresent(player.getPacketSender()::sendAreaPlayerSound);

			if(teleportType != TeleportType.HOME) {
				player.setUntargetable(true);
			}
			player.setTeleporting(teleportType);

			final int duration = teleportType.getTick(0);

			TaskManager.submit(new Task(1, player, true) {
				int tick = 0;
				int index = 1;
				@Override
				public void execute() {
					player.setTeleportingTask(this);
					int teleportTypeTick = teleportType.getTick(index);

					if(teleportTypeTick != -1) {
						if(tick == teleportTypeTick) {
							player.performAnimation(teleportType.getAnimation(index));
							player.performGraphic(teleportType.getGraphic(index));
							if(teleportType == TeleportType.HOME && index < Sounds.HOME_TELEPORT.length) {
								player.getPacketSender().sendAreaPlayerSound(Sounds.HOME_TELEPORT[index]);
							}
							index++;
						}
					}

					if (tick == duration) {
						onTeleporting(player);
						player.moveTo(targetLocation);
						player.notify(PlayerEvents.TELEPORTED);
						onArrival.run();
					} else if (tick == duration + 2) {
						player.getMotion().update(MovementStatus.NONE).clearSteps();
						if (wildernessLevel > 0 && AreaManager.inWilderness(player)) {
							player.getPacketSender().sendMessage("@red@You're in level " + wildernessLevel + " Wilderness!");
						}
						if (player.getPacketSender().sideTabs[2] == 31102) {
							QuestTab.refresh(player, Tab.WILDERNESS);
						}
						stop();
						return;
					}
					tick++;
				}

				@Override
				public void stop() {
					super.stop();

					player.getClickDelay().reset(0);
					player.setUntargetable(false);
					player.setTeleporting(null);
					player.setTeleportingTask(null);
				}
			});
			player.getClickDelay().reset();
		}
	}

	public static void teleportNoCombatReqs(Player player, Position targetLocation, TeleportType requestType, boolean wildernessWarning, boolean wildyCheck, Runnable onArrival) {

		final boolean ignoreReqs = requestType.ignoreTeleportRequirements();
		final int wildernessLevel = wildyCheck ? WildernessArea.getLevel(targetLocation.getY()) : 0;

		if (checkReqsNoCombat(player, targetLocation, wildyCheck, ignoreReqs, requestType)) {

			if (wildernessWarning && checkWildernessWarning(player, targetLocation, requestType, wildyCheck, wildernessLevel))
				return;

			Optional<NPC> wizard = findTeleportWizard(player, NpcID.ANCIENT_WIZARD);
			if(wizard.isEmpty() && requestType == TeleportType.AUBURY && !PlayerUtil.isStaff(player)) {
				Logging.log("teleportbug", "" + player.getUsername() +" teleported from coords " + player.getPosition() +"");
			}

			if(wizard.isPresent() && player.getPosition().isWithinDistance(wizard.get().getPosition(), 3) && requestType == TeleportType.AUBURY) {

				NPC wizardNpc = wizard.get();

				if (Misc.random(2) == 1) {
					wizardNpc.say("Sallamander doe ing!");
				} else if (Misc.random(2) == 2) {
					wizardNpc.say("Sallamander kazaak ita!");
				} else if (Misc.random(3) == 2) {
					wizardNpc.say("Sallamander vira eek!");
				} else if (Misc.random(2) == 1) {
					wizardNpc.say("Sallamander fenkstertain trio!");
				}
				player.getPacketSender().sendAreaPlayerSound(127, 10, 1, 0);
				player.getPoints().increase(AttributeManager.Points.WIZARD_TELEPORTS, 1); // Increase points
				wizardNpc.performAnimation(new Animation(1166));
				wizardNpc.performGraphic(new Graphic(108, GraphicHeight.MIDDLE, Priority.HIGH));
			}


			final TeleportType teleportType = requestType;
			final PlayerMotion motion = player.getMotion();

			motion.clearSteps();

			if(teleportType != TeleportType.HOME)
				motion.update(MovementStatus.DISABLED);

			player.getPacketSender().sendMinimapFlagRemoval();

			onTeleporting(player);
			SkillUtil.stopSkillable(player);

			player.performAnimation(teleportType.getAnimation(0));
			player.performGraphic(teleportType.getGraphic(0));

			teleportType
					.findTeleportSound()
					.ifPresent(player.getPacketSender()::sendAreaPlayerSound);

			if(teleportType != TeleportType.HOME) {
				player.setUntargetable(true);
			}
			player.setTeleporting(teleportType);

			final int duration = teleportType.getTick(0);

			TaskManager.submit(new Task(1, player, true) {
				int tick = 0;
				int index = 1;
				@Override
				public void execute() {
					player.setTeleportingTask(this);
					int teleportTypeTick = teleportType.getTick(index);

					if(teleportTypeTick != -1) {
						if(tick == teleportTypeTick) {
							player.performAnimation(teleportType.getAnimation(index));
							player.performGraphic(teleportType.getGraphic(index));
							if(teleportType == TeleportType.HOME && index < Sounds.HOME_TELEPORT.length) {
								player.getPacketSender().sendAreaPlayerSound(Sounds.HOME_TELEPORT[index]);
							}
							index++;
						}
					}

					if (tick == duration) {
						onTeleporting(player);
						player.moveTo(targetLocation);
						player.notify(PlayerEvents.TELEPORTED);
						onArrival.run();
					} else if (tick == duration + 2) {
						player.getMotion().update(MovementStatus.NONE).clearSteps();
						if (wildernessLevel > 0 && AreaManager.inWilderness(player)) {
							player.getPacketSender().sendMessage("@red@You're in level " + wildernessLevel + " Wilderness!");
						}
						if (player.getPacketSender().sideTabs[2] == 31102) {
							QuestTab.refresh(player, Tab.WILDERNESS);
						}
						stop();
						return;
					}
					tick++;
				}

				@Override
				public void stop() {
					super.stop();

					player.getClickDelay().reset(0);
					player.setUntargetable(false);
					player.setTeleporting(null);
					player.setTeleportingTask(null);
				}
			});
			player.getClickDelay().reset();
		}
	}

	public static void teleportNoReqs(Player player, Position targetLocation, TeleportType requestType, boolean wildernessWarning, boolean wildyCheck, Runnable onArrival) {
			final TeleportType teleportType = requestType;
			final PlayerMotion motion = player.getMotion();

			motion.clearSteps();

			if(teleportType != TeleportType.HOME)
				motion.update(MovementStatus.DISABLED);

			player.getPacketSender().sendMinimapFlagRemoval();

			onTeleporting(player);
			SkillUtil.stopSkillable(player);

			player.performAnimation(teleportType.getAnimation(0));
			player.performGraphic(teleportType.getGraphic(0));

			teleportType
					.findTeleportSound()
					.ifPresent(player.getPacketSender()::sendAreaPlayerSound);

			if(teleportType != TeleportType.HOME) {
				player.setUntargetable(true);
			}
			player.setTeleporting(teleportType);

			final int duration = teleportType.getTick(0);

			TaskManager.submit(new Task(1, player, true) {
				int tick = 0;
				int index = 1;
				@Override
				public void execute() {
					player.setTeleportingTask(this);
					int teleportTypeTick = teleportType.getTick(index);

					if(teleportTypeTick != -1) {
						if(tick == teleportTypeTick) {
							player.performAnimation(teleportType.getAnimation(index));
							player.performGraphic(teleportType.getGraphic(index));
							if(teleportType == TeleportType.HOME && index < Sounds.HOME_TELEPORT.length) {
								player.getPacketSender().sendAreaPlayerSound(Sounds.HOME_TELEPORT[index]);
							}
							index++;
						}
					}

					if (tick == duration) {
						onTeleporting(player);
						player.moveTo(targetLocation);
						player.notify(PlayerEvents.TELEPORTED);
						onArrival.run();
					} else if (tick == duration + 2) {
						player.getMotion().update(MovementStatus.NONE).clearSteps();
						if (player.getPacketSender().sideTabs[2] == 31102) {
							QuestTab.refresh(player, Tab.WILDERNESS);
						}
						stop();
						return;
					}
					tick++;
				}

				@Override
				public void stop() {
					super.stop();

					player.getClickDelay().reset(0);
					player.setUntargetable(false);
					player.setTeleporting(null);
					player.setTeleportingTask(null);
				}
			});
			player.getClickDelay().reset();
	}

	@NotNull
	private static Optional<NPC> findTeleportWizard(Player player, int ancientWizard) {
		return player.getLocalNpcs().stream().filter(n -> n.getId() == ancientWizard).findFirst();
	}

	private static boolean checkWildernessWarning(Player player, Position targetLocation, TeleportType requestType, boolean wildyCheck, int wildernessLevel) {
		StringBuilder warning = new StringBuilder();
		Area area = AreaManager.get(targetLocation);
		boolean wilderness = (area instanceof WildernessArea);
		if (wilderness) {
			warning.append("Are you sure you want to teleport there? ");
			buildWildernessWarning(targetLocation, wildernessLevel, warning);
			player.setDialogueContinueAction(() -> {

				DialogueManager.start(player, 2523);
				player.setDialogueOptions(new DialogueOptions() {
					@Override
					public void handleOption(Player player1, int option) {
						player1.getPacketSender().sendInterfaceRemoval();
						if (option == 1) {
							teleport(player1, targetLocation, requestType, false, wildyCheck);
						}
					}
				});
			});
			DialogueManager.sendStatement(player, warning.toString());
			return true;
		}
		return false;
	}

	/**
	 * Teleports a player to the target location.
	 *
	 * @param player
	 *            The player teleporting.
	 * @param targetLocation
	 *            The location to teleport to.
	 * @param requestType
	 *            The requested type of teleport.
	 */
	public static void wildernessSpellTeleport(Player player, Position targetLocation, TeleportType requestType,
								boolean wildernessWarning, boolean wildyCheck, int button) {
		boolean ignoreReqs = false;
		int wildernessLevel = wildyCheck ? WildernessArea.getLevel(targetLocation.getY()) : 0;
		if (checkReqs(player, targetLocation, wildyCheck, ignoreReqs, requestType)) {
			if (wildernessWarning) {
				StringBuilder warning = new StringBuilder();
				Area area = AreaManager.get(targetLocation);
				boolean wilderness = (area instanceof WildernessArea);
				if (wilderness) {
					warning.append("Are you sure you want to teleport there? ");
					buildWildernessWarning(targetLocation, wildernessLevel, warning);
					player.setDialogueContinueAction(() -> {

						DialogueManager.start(player, 2523);
						player.setDialogueOptions(new DialogueOptions() {
							@Override
							public void handleOption(Player player1, int option) {

								player1.getPacketSender().sendInterfaceRemoval();

								if (option == 1) {

									/*
									 * This is to fix it so you only earn XP when you accept to teleport into the wilderness
									 * from the dialogue. If we remove this, then players can spam click it and gain XP without
									 * teleporting.
									 */
									Optional<InteractiveSpell> spell = InteractiveSpell.forSpellId(button);

									if (spell.isEmpty())
										return;

									if (spell.get() == InteractiveSpell.DAREEYAK_TELEPORT
											|| spell.get() == InteractiveSpell.CARRALLANGAR_TELEPORT
											|| spell.get() == InteractiveSpell.ANNAKARL_TELEPORT
											|| spell.get() == InteractiveSpell.GHORROCK_TELEPORT
											|| spell.get() == InteractiveSpell.ICE_PLATEAU_TELEPORT)
									{
										player1.getSkillManager().addExperience(Skill.MAGIC, spell.get().getSpell().baseExperience());

										// Increase points
										player.getPoints().increase(AttributeManager.Points.WILDERNESS_BOOK_TELEPORTS); // Increase points
										player.getPoints().increase(AttributeManager.Points.SPELLS_CASTED); // Increase points

										spell.get().getSpell().deleteItemsRequired(player1);
									}
									teleport(player1, targetLocation, requestType, false, wildyCheck);
								}
							}
						});
					});
					DialogueManager.sendStatement(player, warning.toString());
				}
			}
		}
	}

	public static void teleportByLever(Player player, GameObject object, Position targetLocation,
									   boolean wildernessWarning, boolean wildyCheck) {
		boolean ignoreReqs = true;
		int wildernessLevel = wildyCheck ? WildernessArea.getLevel(targetLocation.getY()) : 0;


		if (checkReqs(player, targetLocation, wildyCheck, ignoreReqs, TeleportType.LEVER)) {
			if (wildernessWarning) {
				StringBuilder warning = new StringBuilder();
				Area area = AreaManager.get(targetLocation);
				boolean wilderness = (area instanceof WildernessArea);
				if (wilderness) {
					warning.append("Are you sure you want to use this lever teleport? ");
					buildWildernessWarning(targetLocation, wildernessLevel, warning);
					player.setDialogueContinueAction(() -> {
						DialogueManager.start(player, 2523);
						player.setDialogueOptions(new DialogueOptions() {
							@Override
							public void handleOption(Player player1, int option) {
								player1.getPacketSender().sendInterfaceRemoval();
								if (option == 1) {
									teleportByLever(player1, object, targetLocation, false, wildyCheck);
								}
							}
						});
					});
					DialogueManager.sendStatement(player, warning.toString());
					return;
				}
			}
			player.getMotion().clearSteps();
			player.getMotion().update(MovementStatus.DISABLED).clearSteps();
			player.getPacketSender().sendMinimapFlagRemoval();
			onTeleporting(player);
			SkillUtil.stopSkillable(player);
			player.performAnimation(TeleportType.LEVER.getAnimation(0));
			player.performGraphic(TeleportType.LEVER.getGraphic(0));
			player.getPacketSender().sendMessage("You pull the lever..");
			player.getPacketSender().sendAreaPlayerSound(Sounds.PULL_LEVEL);
			player.setUntargetable(true);
			player.setTeleporting(TeleportType.LEVER);

			if (object.getId() != 9706 && object.getId() != 9707) {
				TaskManager.submit(new TimedObjectReplacementTask(object,
						DynamicGameObject.createPublic(5961, object.getPosition(),
								object.getObjectType(), object.getFace()), 4));
			}

			int duration = TeleportType.LEVER.getTick(0);
			TaskManager.submit(new Task(1, player, true) {
				int tick = 0;
				int index = 1;
				@Override
				public void execute() {
					player.setTeleportingTask(this);
					int teleportTypeTick = TeleportType.LEVER.getTick(index);

					if(teleportTypeTick != -1) {
						if(tick == teleportTypeTick) {
							player.performAnimation(TeleportType.LEVER.getAnimation(index));
							player.performGraphic(TeleportType.LEVER.getGraphic(index));
							index++;
						}
					}

					if (tick == duration - 2) {
						player.getPacketSender().sendAreaPlayerSound(Sounds.NORMAL_TELEPORT);
					} else if (tick == duration) {
						onTeleporting(player);
						player.moveTo(targetLocation);
						player.notify(PlayerEvents.TELEPORTED);
					} else if (tick == duration + 2) {
						player.getMotion().update(MovementStatus.NONE).clearSteps();
						if (wildernessLevel > 0 && AreaManager.inWilderness(player)) {
							player.getPacketSender().sendMessage("... and teleport into the Wilderness.");
							player.getPacketSender().sendMessage("@red@You're in level " + wildernessLevel + " Wilderness!");
						}
						if (player.getPacketSender().sideTabs[2] == 31102) {
							QuestTab.refresh(player, Tab.WILDERNESS);
						}
						stop();
						return;
					}
					tick++;
				}

				@Override
				public void stop() {
					super.stop();

					player.getClickDelay().reset(0);
					player.setUntargetable(false);
					player.setTeleporting(null);
					player.setTeleportingTask(null);
				}
			});
			player.getClickDelay().reset();
		}
	}

	public static void buildWildernessWarning(Position targetLocation, int wildernessLevel, StringBuilder warning) {
		if (wildernessLevel > 0) {
			warning.append("It's in level @red@")
					.append(wildernessLevel)
					.append(" @bla@Wilderness! ");
			if (WildernessArea.multi(targetLocation.getX(), targetLocation.getY())) {
				warning.append(
						"Additionally, @red@it's a multi zone@bla@. Other players may attack you simultaneously.");
			} else {
				warning.append("Other players will be able to attack you.");
			}
		} else {
			warning.append("Other players will be able to attack you.");
		}
	}

	public static void dragonStoneJewerlyTeleport(
			Player player,
			Position targetLocation,
			TeleportType teleportType,
			boolean wildernessWarning,
			boolean wildyCheck
	) {

		final boolean ignoreReqs = teleportType.ignoreTeleportRequirements();
		final int wildernessLevel = wildyCheck ? WildernessArea.getLevel(targetLocation.getY()) : 0;

		if (checkReqs2(player, targetLocation, wildyCheck, ignoreReqs, teleportType)) {

			if (checkWildernessPrompt(player, targetLocation, wildernessWarning, wildernessLevel, teleportType, wildyCheck, teleportType, wildyCheck))
				return;

			player.getMotion().clearSteps();
			player.getMotion().update(MovementStatus.DISABLED).clearSteps();
			player.getPacketSender().sendMinimapFlagRemoval();
			onTeleporting(player);
			SkillUtil.stopSkillable(player);
			player.performAnimation(teleportType.getAnimation(0));
			player.performGraphic(teleportType.getGraphic(0));

			teleportType
					.findTeleportSound()
					.ifPresent(player.getPacketSender()::sendAreaPlayerSound);

			player.setUntargetable(true);
			player.setTeleporting(teleportType);

			final int duration = teleportType.getTick(0);

			TaskManager.submit(new Task(1, player, true) {
				int tick = 0;
				int index = 1;
				@Override
				public void execute() {
					player.setTeleportingTask(this);
					int teleportTypeTick = teleportType.getTick(index);

					if(teleportTypeTick != -1) {
						if(tick == teleportTypeTick) {
							player.performAnimation(teleportType.getAnimation(index));
							player.performGraphic(teleportType.getGraphic(index));
							index++;
						}
					}

					if (tick == duration) {
						onTeleporting(player);
						player.moveTo(targetLocation);
						player.notify(PlayerEvents.TELEPORTED);
					} else if (tick == duration + 2) {
						player.getMotion().update(MovementStatus.NONE).clearSteps();
						if (wildernessLevel > 0 && AreaManager.inWilderness(player)) {
							player.getPacketSender().sendMessage("@red@You're in level " + wildernessLevel + " Wilderness!");
						}
						if (player.getPacketSender().sideTabs[2] == 31102) {
							QuestTab.refresh(player, Tab.WILDERNESS);
						}
						stop();
						return;
					}
					tick++;
				}

				@Override
				public void stop() {
					super.stop();
					player.getClickDelay().reset(0);
					player.setUntargetable(false);
					player.setTeleporting(null);
					player.setTeleportingTask(null);
				}
			});
			player.getClickDelay().reset();
		}
	}
	public static void teleportBountyTarget(Player player, Position targetLocation, TeleportType teleportType,
											boolean wildernessWarning, boolean wildyCheck, InteractiveSpell interactiveSpell) {

		final int wildernessLevel = wildyCheck
				? WildernessArea.getLevel(targetLocation.getY())
				: 0;

		if (checkReqs3(player, wildyCheck, false, teleportType)) {
			if (wildernessWarning) {
				final StringBuilder warning = new StringBuilder();
				final Area area = AreaManager.get(targetLocation);
				final boolean wilderness = (area instanceof WildernessArea);
				if (wilderness) {

					warning.append("Are you sure you want to teleport there? ");
					buildWildernessWarning(targetLocation, wildernessLevel, warning);

					player.setDialogueContinueAction(() -> {

						DialogueManager.start(player, 2523);
						player.setDialogueOptions(new DialogueOptions() {
							@Override
							public void handleOption(Player player1, int option) {
								player1.getPacketSender().sendInterfaceRemoval();
								if (option == 1) {
									teleportBountyTarget(player1, targetLocation, teleportType, false, false, interactiveSpell);
								}
							}
						});
					});
					DialogueManager.sendStatement(player, warning.toString());
					return;
				}
			}
			AchievementManager.processFor(AchievementType.LOOKING_FOR_TROUBLE, player);
			// Increase points
			player.getPoints().increase(AttributeManager.Points.SPELL_BOOK_TELEPORTS); // Increase points
			player.getPoints().increase(AttributeManager.Points.SPELLS_CASTED); // Increase points

			// Add XP
			player.getSkillManager().addExperience(Skill.MAGIC, interactiveSpell.getSpell().baseExperience());
			// Delete runes
			interactiveSpell.getSpell().deleteItemsRequired(player);

			PlayerExtKt.resetInteractions(player, true, false);
			PlayerExtKt.removeInterfaces(player);

			player.getMotion().clearSteps();
			player.getMotion().update(MovementStatus.DISABLED).clearSteps();
			player.getPacketSender().sendMinimapFlagRemoval();
			onTeleporting(player);
			SkillUtil.stopSkillable(player);
			player.performAnimation(teleportType.getAnimation(0));
			player.performGraphic(teleportType.getGraphic(0));
			player.setUntargetable(true);
			player.setTeleporting(teleportType);
	        //player.getBountyTeleportTimer().extendOrStart(300);

			int duration = teleportType.getTick(0);
			TaskManager.submit(new Task(1, player, true) {
				int tick = 0;
				int index = 1;
				@Override
				public void execute() {
					player.setTeleportingTask(this);
					int teleportTypeTick = teleportType.getTick(index);

					if(teleportTypeTick != -1) {
						if(tick == teleportTypeTick) {
							player.performAnimation(teleportType.getAnimation(index));
							player.performGraphic(teleportType.getGraphic(index));
							index++;
						}
					}

					if (tick == duration) {
						onTeleporting(player);
						player.moveTo(targetLocation);
						player.notify(PlayerEvents.TELEPORTED);
					} else if (tick == duration + 2) {
						player.getMotion().update(MovementStatus.NONE).clearSteps();
						if (wildernessLevel > 0 && AreaManager.inWilderness(player)) {
							player.getPacketSender().sendMessage("@red@You're in level " + wildernessLevel + " Wilderness!");
						}
						if (player.getPacketSender().sideTabs[2] == 31102) {
							QuestTab.refresh(player, Tab.WILDERNESS);
						}
						stop();
						return;
					}
					tick++;
				}

				@Override
				public void stop() {
					super.stop();
					player.getClickDelay().reset(0);
					player.setUntargetable(false);
					player.setTeleporting(null);
					player.setTeleportingTask(null);
				}
			});
			player.getClickDelay().reset();
		}
	}

	private static boolean checkWilderness(Player player, Position targetLocation, int wildernessLevel, StringBuilder warning, TeleportType teleportType2, boolean wildyCheck2, TeleportType teleportType, boolean wildyCheck) {
		final Area area = AreaManager.get(targetLocation);
		final boolean wilderness = (area instanceof WildernessArea);
		if (wilderness) {

			warning.append("Are you sure you want to teleport there? ");
			buildWildernessWarning(targetLocation, wildernessLevel, warning);

			player.setDialogueContinueAction(() -> {

				DialogueManager.start(player, 2523);
				player.setDialogueOptions(new DialogueOptions() {
					@Override
					public void handleOption(Player player1, int option) {
						player1.getPacketSender().sendInterfaceRemoval();
						if (option == 1) {
							teleport(player1, targetLocation, teleportType2, false, wildyCheck2);
						}
					}
				});
			});
			DialogueManager.sendStatement(player, warning.toString());
			return true;
		}
		return false;
	}

	private static void onTeleporting(Player player) {
		SkillUtil.stopSkillable(player);
		player.getPacketSender().sendInterfaceRemoval();
		player.setWalkToTask(null);
		player.setEntityInteraction(null);
		player.getMotion().followTarget(null);
		if (player.getCombat().hasTarget() || player.getCombat().isAttacking())
			player.getCombat().reset(true);
		player.getCombat().clearDamages(player);
		if(AreaManager.FREE_PVP_ARENA.contains(player)) {
			player.resetAttributes();
		}
	}

	/**
	 * Teleports a player to the target location with NPC graphics being played.
	 *
	 * @param player
	 *            The player teleporting.
	 * @param targetLocation
	 *            The location to teleport to.
	 * @param requestType
	 *            The requested type of teleport.
	 */
	public static void teleportFromNPC(Player player, Position targetLocation, TeleportType requestType,
								boolean wildernessWarning, boolean wildyCheck, int npcId) {
		boolean ignoreReqs = false;
		int wildernessLevel = wildyCheck ? WildernessArea.getLevel(targetLocation.getY()) : 0;

		if (checkReqs(player, targetLocation, wildyCheck, ignoreReqs, requestType)) {

			if (checkWildernessPrompt(player, targetLocation, wildernessWarning, wildernessLevel, requestType, wildyCheck, requestType, wildyCheck))
				return;


			Optional<NPC> teleporter = findTeleportWizard(player, npcId);
			if(teleporter.isPresent() && player.getPosition().isWithinDistance(teleporter.get().getPosition(), 2) && requestType == TeleportType.ANCIENT_WIZARD) {
				NPC wizardNpc = teleporter.get();
				wizardNpc.say("Veniens! Sallakar! Rinnesset!");
				wizardNpc.performAnimation(new Animation(1818));
				wizardNpc.performGraphic(new Graphic(343));
				player.getPacketSender().sendAreaPlayerSound(Sounds.TELEOTHER_CAMELOT_SPELL, 4, 1, 0);
			} else if(teleporter.isPresent() && player.getPosition().isWithinDistance(teleporter.get().getPosition(), 2) && requestType == TeleportType.MAGE_OF_ZAMORAK) {
				NPC wizardNpc = teleporter.get();
				wizardNpc.performAnimation(new Animation(1161));
				ProjectileTemplate temp = ProjectileTemplate
						.builder(109)
						.setStartHeight(38)
						.setEndHeight(38)
						.setCurve(1)
						.setDelay(50)
						.setSourceSize(1)
						.setSourceOffset(0)
						.setSpeed(75)
						.build();
				Projectile proj = new Projectile(wizardNpc.getPosition(), player.getPosition(), temp);
				proj.sendProjectile();
				wizardNpc.say("Senventior Disthine Molenko!");
				//wizardNpc.performAnimation(new Animation(1818));
				// cast projectile 109 to player
				wizardNpc.performGraphic(new Graphic(108));
				player.getPacketSender().sendAreaPlayerSound(Sounds.AUBURY_TELEPORT_FULL, 4, 1, 0);
			}
			final TeleportType teleportType = requestType;
			player.getMotion().clearSteps();
			player.getMotion().update(MovementStatus.DISABLED).clearSteps();
			player.getPacketSender().sendMinimapFlagRemoval();
			onTeleporting(player);
			SkillUtil.stopSkillable(player);
			player.performAnimation(teleportType.getAnimation(0));
			player.performGraphic(teleportType.getGraphic(0));

			if (teleportType.equals(TeleportType.AUBURY)) {
				player.getPacketSender().sendAreaPlayerSound(Sounds.AUBURY_TELEPORT, 75);
			} else if (teleportType.equals(TeleportType.ANCIENT_WIZARD)) {
				player.getPacketSender().sendAreaPlayerSound(Sounds.TELEOTHER_TELEPORTING, 75);
			}

			player.setUntargetable(true);
			player.setTeleporting(teleportType);
			int duration = teleportType.getTick(0);
			TaskManager.submit(new Task(1, player, true) {
				int tick = 0;
				int index = 1;
				@Override
				public void execute() {
					player.setTeleportingTask(this);
					int teleportTypeTick = teleportType.getTick(index);

					if(teleportTypeTick != -1) {
						if(tick == teleportTypeTick) {
							player.performAnimation(teleportType.getAnimation(index));
							player.performGraphic(teleportType.getGraphic(index));
							if(teleportType == TeleportType.HOME && index < Sounds.HOME_TELEPORT.length) {
								player.getPacketSender().sendAreaPlayerSound(Sounds.HOME_TELEPORT[index]);
							}
							index++;
						}
					}

					if (tick == duration) {
						onTeleporting(player);
						player.moveTo(targetLocation);
						player.notify(PlayerEvents.TELEPORTED);
					} else if (tick == duration + 2) {
						player.getMotion().update(MovementStatus.NONE).clearSteps();
						if (wildernessLevel > 0 && AreaManager.inWilderness(player)) {
							player.getPacketSender().sendMessage("@red@You're in level " + wildernessLevel + " Wilderness!");
						}
						if (player.getPacketSender().sideTabs[2] == 31102) {
							QuestTab.refresh(player, Tab.WILDERNESS);
						}
						stop();
						return;
					}
					tick++;
				}

				@Override
				public void stop() {
					super.stop();

					player.getClickDelay().reset(0);
					player.setUntargetable(false);
					player.setTeleporting(null);
					player.setTeleportingTask(null);
				}
			});
			player.getClickDelay().reset();
		}
	}

	private static boolean checkWildernessPrompt(Player player, Position targetLocation, boolean wildernessWarning, int wildernessLevel, TeleportType requestType2, boolean wildyCheck2, TeleportType requestType, boolean wildyCheck) {
		if (wildernessWarning) {
			StringBuilder warning = new StringBuilder();
			Area area = AreaManager.get(targetLocation);
			boolean wilderness = (area instanceof WildernessArea);
			if (wilderness) {
				warning.append("Are you sure you want to teleport there? ");
				buildWildernessWarning(targetLocation, wildernessLevel, warning);
				player.setDialogueContinueAction(() -> {
					DialogueManager.start(player, 2523);
					player.setDialogueOptions(new DialogueOptions() {
						@Override
						public void handleOption(Player player1, int option) {
							player1.getPacketSender().sendInterfaceRemoval();
							if (option == 1) {
								teleport(player1, targetLocation, requestType2, false, wildyCheck2);
							}
						}
					});
				});
				DialogueManager.sendStatement(player, warning.toString());
				return true;
			}
		}
		return false;
	}

	/**
	 * Makes the NPC offer a teleport prompt to the player
	 *
	 * @param player
	 *            The player teleporting.
	 * @param targetLocation
	 *            The location to teleport to.
	 * @param requestType
	 *            The requested type of teleport.
	 */
	public static void offerTeleportFromNPC(Player player,
											Position targetLocation,
											TeleportType requestType,
											boolean wildernessWarning,
											boolean wildyCheck,
											int npcId,
											String areaName) {

		int wildernessLevel = wildyCheck ? WildernessArea.getLevel(targetLocation.getY()) : 0;

		if (checkReqs(player, targetLocation, wildyCheck, false, requestType)) {

			if (wildernessWarning) {
				StringBuilder warning = new StringBuilder();
				if (checkWilderness(player, targetLocation, wildernessLevel, warning, requestType, wildyCheck, requestType, wildyCheck))
					return;
			}

			Optional<NPC> teleporterNPC = findTeleportWizard(player, npcId);
			if (teleporterNPC.isPresent() && player.getPosition().isWithinDistance(teleporterNPC.get().getPosition(), 2)) {
				NPC wizardNpc = teleporterNPC.get();
				if (npcId != 2183) // Tzhaar npc skip animation
				wizardNpc.performAnimation(new Animation(1818));
				wizardNpc.performGraphic(new Graphic(343));
				player.getPacketSender().sendAreaPlayerSound(Sounds.TELEPORT_REQUEST_SOUND, 5, 1, 0);
				player.BLOCK_ALL_BUT_TALKING = true;
				player.getPacketSender().sendString(areaName, 12560);
				player.getPacketSender().sendString("" + NpcDefinition.forId(npcId).getName() +"", 12558);
				player.setTeleportDestination(targetLocation);
			}
			TaskManager.submit(2, () -> player.getPacketSender().sendInterface(12468));
			TaskManager.submit(3, () -> player.BLOCK_ALL_BUT_TALKING = false);
		}
	}

	public static boolean checkReqsNoCombat(Player player, Position targetLocation, boolean wildyCheck, boolean ignoreReqs, TeleportType t) {
		if (player.getArea() != null) {
			if (!(AreaManager.inWilderness(player) && ignoreReqs)) {
				if (!player.getArea().canTeleport(player) && !(AreaManager.inWilderness(player) && !wildyCheck)) {
					return false;
				}
			}
		}
		if(player.instance!= null) {
			if(!player.instance.canTeleport(player)) {
				player.getPacketSender().sendMessage("You can't teleport out of here!");
				return false;
			}
		}
		if (player.getHitpoints() <= 0)
			return false;
		if (player.BLOCK_ALL_BUT_TALKING) {
			player.getPacketSender().sendMessage("You can't that right now.", 1000);
			return false;
		}
		if (player.isInTutorial()) {
			player.getPacketSender().sendMessage("You can't do this while in a tutorial.", 1000);
			return false;
		}
		if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
			player.getPacketSender().sendMessage("You can't this that when you're busy.", 1000);
			return false;
		}
		if (EntityExtKt.getBoolean(player, Attribute.HAS_TRIGGER_RANDOM_EVENT, false)) {
			player.getPacketSender().sendMessage("You can't use this teleport right now.", 1000);
			return false;
		}
		if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
			player.getPacketSender().sendMessage("You can't teleport when you're AFK!", 1000);
			return false;
		}
		if (!MorphItems.INSTANCE.notTransformed(player, "teleport", true, false))
			return false;

		if (player.getStatus() == PlayerStatus.TRADING) {
			player.getPacketSender().sendMessage("You can't teleport while trading!", 1000);
			return false;
		}
		if (player.getStatus() == PlayerStatus.BANKING) {
			player.getPacketSender().sendMessage("You can't teleport while banking!", 1000);
			return false;
		}
		if (player.getStatus() == PlayerStatus.PRICE_CHECKING) {
			player.getPacketSender().sendMessage("You can't teleport while price checking!", 1000);
			return false;
		}
		if (player.getStatus() == PlayerStatus.DUELING) {
			player.getPacketSender().sendMessage("You can't teleport while dueling!", 1000);
			return false;
		}
		if (player.isJailed()) {
			player.getPacketSender().sendMessage("You're not allowed to teleport when you're jailed!", 1000);
			return false;
		}
		if (!player.getCombat().getTeleBlockTimer().finished()) {
			player.getPacketSender().sendMessage("A magical spell is blocking you from teleporting.", 1000);
			return false;
		}
		int wildernessLevel = WildernessArea.getLevel(player.getPosition().getY());
		if (wildernessLevel > 20 && AreaManager.inWilderness(player) && wildyCheck) {
			player.getPacketSender().sendMessage("You can't teleport above level 20 Wilderness.", 1000);
			player.getPacketSender().sendInterfaceRemoval();
			return false;
		}
		if (ignoreReqs) {
			return true;
		}
		if (player.blockTeleportation() || EntityExtKt.getBoolean(player, Attribute.STALL_HITS, false)) {
			player.getPacketSender().sendMessage("You can't do that right now.", 1000);
			return false;
		}
		/* Causes issues in wildy
		if (!player.getMotion().canMove()) {
			return false;
		}*/
		return true;
	}


	public static boolean checkReqs(Player player, Position targetLocation, boolean wildyCheck, boolean ignoreReqs, TeleportType t) {
		if (player.getArea() != null) {
			if (!(AreaManager.inWilderness(player) && ignoreReqs)) {
				if (!player.getArea().canTeleport(player) && !(AreaManager.inWilderness(player) && !wildyCheck)) {
					return false;
				}
			}
		}
		if(player.instance!= null) {
			if(!player.instance.canTeleport(player)) {
				player.getPacketSender().sendMessage("You can't teleport out of here!");
				return false;
			}
		}
		if (player.getHitpoints() <= 0)
			return false;
		if (player.BLOCK_ALL_BUT_TALKING) {
			player.getPacketSender().sendMessage("You can't that right now.", 1000);
			return false;
		}
        if (player.isInTutorial()) {
			player.getPacketSender().sendMessage("You can't do this while in a tutorial.", 1000);
        	return false;
        }
		if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
			player.getPacketSender().sendMessage("You can't this that when you're busy.", 1000);
			return false;
		}
		if (EntityExtKt.getBoolean(player, Attribute.HAS_TRIGGER_RANDOM_EVENT, false)) {
			player.getPacketSender().sendMessage("You can't use this teleport right now.", 1000);
			return false;
		}
    	if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
    		player.getPacketSender().sendMessage("You can't teleport when you're AFK!", 1000);
    		return false;
    	}
		if (!MorphItems.INSTANCE.notTransformed(player, "teleport", true, false))
			return false;

		if (player.getStatus() == PlayerStatus.TRADING) {
			player.getPacketSender().sendMessage("You can't teleport while trading!", 1000);
			return false;
		}
		if (player.getStatus() == PlayerStatus.BANKING) {
			player.getPacketSender().sendMessage("You can't teleport while banking!", 1000);
			return false;
		}
		if (player.getStatus() == PlayerStatus.PRICE_CHECKING) {
			player.getPacketSender().sendMessage("You can't teleport while price checking!", 1000);
			return false;
		}
		if (player.getStatus() == PlayerStatus.DUELING) {
			player.getPacketSender().sendMessage("You can't teleport while dueling!", 1000);
			return false;
		}
        if (player.isJailed()) {
        	player.getPacketSender().sendMessage("You're not allowed to teleport when you're jailed!", 1000);
            return false;
        }
		if (!player.getCombat().getTeleBlockTimer().finished()) {
			player.getPacketSender().sendMessage("A magical spell is blocking you from teleporting.", 1000);
			return false;
		}
		int wildernessLevel = WildernessArea.getLevel(player.getPosition().getY());
		if (wildernessLevel > 20 && AreaManager.inWilderness(player) && wildyCheck) {
			player.getPacketSender().sendMessage("You can't teleport above level 20 Wilderness", 1000);
			player.getPacketSender().sendInterfaceRemoval();
			return false;
		}
		if (ignoreReqs) {
			return true;
		}
		if (player.blockTeleportation()) {
			player.getPacketSender().sendMessage("You can't do that right now.", 1000);
			return false;
		}
		boolean skipCombat = false;
		if (t != null) {
			if (t.equals(TeleportType.TELE_TAB)) {
				skipCombat = true;
			} else if (t.equals(TeleportType.JEWELRY_RUB)) {
				skipCombat = true;
			} else if (t.equals(TeleportType.AUBURY)) {
				skipCombat = true;
			} else if (t.equals(TeleportType.ECTOPHIAL)) {
				skipCombat = true;
			} else if (t.equals(TeleportType.LEVER)) {
				skipCombat = true;
			} else if (t.equals(TeleportType.PURO_PURO)) {
				skipCombat = true;
			} else if (t.equals(TeleportType.ROYAL_SEED_POT)) {
				skipCombat = true;
			} else if (t.equals(TeleportType.MAGE_OF_ZAMORAK)) {
				skipCombat = true;
			} else if (t.equals(TeleportType.ROYAL_SEED_POT)) {
				skipCombat = true;
			} else if (t.equals(TeleportType.SCROLL)) {
				skipCombat = true;
			}
		}
		if (player.getCombat().isInCombat() && !skipCombat) {
			player.getPacketSender().sendMessage("You must wait a few seconds after being out of combat to teleport!", 1000);
			return false;
		}
		/*if (player.getCombat().getOpponent() != null
						&& player.getCombat().getOpponent().isPlayer()
						&& player.getCombat().isInCombat()
						&& !skipCombat
						&& wildernessLevel < 20 && AreaManager.inWilderness(player)) {
			player.getPacketSender().sendMessage("You must wait a few seconds after being out of combat to teleport!", 1000);
			return false;
		}*/

		return true;
	}
	
	public static boolean checkReqs2(Player player, Position targetLocation, boolean wildyCheck, boolean ignoreReqs, TeleportType t) {
		if (player.getArea() != null) {
			if (!(AreaManager.inWilderness(player) && ignoreReqs)) {
				
				if (!player.getArea().canTeleport(player) && !(AreaManager.inWilderness(player) && !wildyCheck)) {
					return false;
				}
			}
		}
		if (player.BLOCK_ALL_BUT_TALKING) {
			return false;
		}
        if (player.isInTutorial()) {
        	return false;
        }
		if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
			return false;
		}
    	if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
    		player.getPacketSender().sendMessage("You can't teleport when you're AFK!", 1000);
    		return false;
    	}
		if (player.getStatus() == PlayerStatus.TRADING) {
			player.getPacketSender().sendMessage("You can't teleport while trading!", 1000);
			return false;
		}
		if (player.getStatus() == PlayerStatus.BANKING) {
			player.getPacketSender().sendMessage("You can't teleport while banking!", 1000);
			return false;
		}
		if (player.getStatus() == PlayerStatus.PRICE_CHECKING) {
			player.getPacketSender().sendMessage("You can't teleport while price checking!", 1000);
			return false;
		}
		if (player.getStatus() == PlayerStatus.DUELING) {
			player.getPacketSender().sendMessage("You can't teleport while dueling!", 1000);
			return false;
		}

		if (!MorphItems.INSTANCE.notTransformed(player, "teleport", true, false))
			return false;

        if (player.isJailed()) {
        	player.sendMessage("You're not allowed to teleport when you're jailed!");
            return false;
        }
		if (!player.getCombat().getTeleBlockTimer().finished()) {
			if (AreaManager.inWilderness(player)) {
				player.getPacketSender().sendMessage("A magical spell is blocking you from teleporting.", 1000);
				return false;
			} else {
				player.getCombat().getTeleBlockTimer().stop();
				player.getPacketSender().sendEffectTimer(0, EffectTimer.TELE_BLOCK);
			}
		}
		if (ignoreReqs) {
			return true;
		}
		int wildernessLevel = WildernessArea.getLevel(player.getPosition().getY());
		if (wildernessLevel > 30 && AreaManager.inWilderness(player) && wildyCheck) {
			player.getPacketSender().sendMessage("You can't teleport above level 30 Wilderness.", 1000);
			player.getPacketSender().sendInterfaceRemoval();
			return false;
		}
		if (player.blockTeleportation()) {
			player.getPacketSender().sendMessage("You can't do that right now.", 1000);
			return false;
		}

		return true;
	}
	public static boolean checkReqs3(Player player, boolean wildyCheck, boolean ignoreReqs, TeleportType t) {
		if (player.getArea() != null) {
			if (!(AreaManager.inWilderness(player)) || !ignoreReqs) {
				
				if (!(player.getArea().canTeleport(player) || AreaManager.inWilderness(player) && !wildyCheck)) {
					return false;
				}
			}
		}
		if (player.BLOCK_ALL_BUT_TALKING) {
			return false;
		}
		if (player.getStatus() == PlayerStatus.TRADING) {
			player.getPacketSender().sendMessage("You can't teleport while trading!", 1000);
			return false;
		}
		if (player.getStatus() == PlayerStatus.BANKING) {
			player.getPacketSender().sendMessage("You can't teleport while banking!", 1000);
			return false;
		}
		if (player.getStatus() == PlayerStatus.PRICE_CHECKING) {
			player.getPacketSender().sendMessage("You can't teleport while price checking!", 1000);
			return false;
		}
		if (player.getStatus() == PlayerStatus.DUELING) {
			player.getPacketSender().sendMessage("You can't teleport while dueling!", 1000);
			return false;
		}
        if (player.isInTutorial()) {
        	return false;
        }
		if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
			return false;
		}
    	if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
    		player.getPacketSender().sendMessage("You can't teleport when you're AFK!", 1000);
    		return false;
    	}

    	if (!MorphItems.INSTANCE.notTransformed(player, "teleport", true, false))
			return false;

        if (player.isJailed()) {
        	player.sendMessage("You're not allowed to teleport when you're jailed!");
            return false;
        }
		if (!player.getCombat().getTeleBlockTimer().finished()) {
			if (AreaManager.inWilderness(player)) {
				player.getPacketSender().sendMessage("A magical spell is blocking you from teleporting.", 1000);
				return false;
			} else {
				player.getCombat().getTeleBlockTimer().stop();
				player.getPacketSender().sendEffectTimer(0, EffectTimer.TELE_BLOCK);
			}
		}
		if (ignoreReqs) {
			return true;
		}
		if (player.blockTeleportation()) {
			player.getPacketSender().sendMessage("You can't do that right now.", 1000);
			return false;
		}
		return true;
	}
}
