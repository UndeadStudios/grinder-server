package com.grinder.game.content.skill.task;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.grinder.game.content.points.ParticipationPoints;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.task_new.DailyTask;
import com.grinder.game.content.task_new.PlayerTaskManager;
import com.grinder.game.content.task_new.WeeklyTask;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.interfaces.dialogue.*;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.Skill;
import com.grinder.game.model.item.container.shop.ShopManager;
import com.grinder.util.Misc;

/**
 * Handles a skilling task given to a player
 * 
 * @author 2012
 *
 */
public class SkillTaskManager {

	private Map<SkillMasterType, SkillTask> tasks = new HashMap<>();
	private int[] points = new int[24];

	public static boolean sendSkillMasterDialogue(Player player, int id) {

		final SkillMasterType master = SkillMasterType.forId(id);

		if (master == null)
			return false;

		/*
		 * Sends the start dialogue
		 */
		if (checkSkillTaskCompletion(player, master) == true) {
			checkCompletion(player, master);
			return true;
		}
			new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
					.setText("Hello there!").add(DialogueType.NPC_STATEMENT).setNpcChatHead(master.getMaster())
					.setText("Hi there @dre@" + player.getUsername() + "</col>! What can I do for you?").add(DialogueType.OPTION).setOptionTitle("Select an Option")
/*					.firstOption("Talk about Skillcapes.", player1 -> {
						if (player.getSkillManager().getCurrentLevel(master.getSkill()) == 99) {
							DialogueManager.start(player, master.getMasterCapeDialogue());
							player.setDialogueOptions(new DialogueOptions() {
								@Override
								public void handleOption(Player player, int option) {
									switch (option) {
										case 1:
											getCape(player, master);
											break;
										case 2:
											new DialogueBuilder(DialogueType.PLAYER_STATEMENT).setText("No thanks. Perhaps another time.").start(player);
											break;
									}
								}
							});
						} else {
							DialogueManager.start(player, master.getCapeDialogue());
						}
					})*/
					.firstOption("Ask about the Skilling tasks.", player1 -> {

						if (player.getSkillTaskManager().getTask(master) != null) {
							checkCompletion(player, master);
						} else {
							DialogueManager.start(player, master.getDialogueId());
							player.setDialogueOptions(new DialogueOptions() {
								@Override
								public void handleOption(Player player, int option) {
									switch (option) {
										case 1:
											getTask(player, master);
											break;
										case 2:
											DialogueManager.start(player, 2514);
											break;
										case 3:
											sendTaskInfomrationDialogue(player, master.getMaster());
											break;
									}
								}
							});
						}
					})
					.secondOption("View Master's shop.", player1 -> {
						ShopManager.open(player1, master.getShop());
					})
					.addCancel()
					.start(player);
			return true;
		}

	private static void sendTaskInfomrationDialogue(Player player, int id) {
		final SkillMasterType master = SkillMasterType.forId(id);

		DialogueManager.start(player, master.getExplainDialogue());
		player.setDialogueOptions(new DialogueOptions() {
			@Override
			public void handleOption(Player player, int option) {
				switch (option) {
					case 1:
						getTask(player, master);
						break;
					case 2:
						DialogueManager.start(player, 2514);
						break;
					case 3:
						sendTaskInfomrationDialogue(player, master.getMaster());
						break;
				}
			}
		});
	}

	/**
	 * Gets a {@link SkillTask task} from the {@link SkillMasterType master}.
	 */
	public static void getTask(Player player, SkillMasterType master) {
		player.getPacketSender().sendInterfaceRemoval();
		/*
		 * Already existing
		 */
		if (player.getSkillTaskManager().getTask(master) != null) {
			if (master.getSkill() == Skill.AGILITY) {
				player.getPacketSender().sendMessage(
						"The master has given you a task to collect " + Misc.format(player.getSkillTaskManager().getTask(master).getAmount()) +" " + player.getSkillTaskManager().getTask(master).getDescription() + ".");
				player.sendMessage("You can collect Marks of grace from Brimhaven agility arena, or Agility arena tickets from courses and rooftops.");
				new DialogueBuilder(DialogueType.NPC_STATEMENT)
						.setNpcChatHead(master.getMaster())
						.setExpression(DialogueExpression.SLEEPY)
						.setText("Your task is to collect " + Misc.format(player.getSkillTaskManager().getTask(master).getAmount()) +" " + player.getSkillTaskManager().getTask(master).getDescription() + ".")
						.add(DialogueType.PLAYER_STATEMENT)
						.setText("Thanks master!")
						.start(player);
			} else if (master.getSkill() == Skill.FLETCHING) {
				player.getPacketSender().sendMessage(
						"The master has given you a task to fletch " + Misc.format(player.getSkillTaskManager().getTask(master).getAmount()) +" " + player.getSkillTaskManager().getTask(master).getDescription() + ".");
				new DialogueBuilder(DialogueType.NPC_STATEMENT)
						.setNpcChatHead(master.getMaster())
						.setExpression(DialogueExpression.SLEEPY)
						.setText("Your task is to fletch " + Misc.format(player.getSkillTaskManager().getTask(master).getAmount()) +" " + player.getSkillTaskManager().getTask(master).getDescription() + ".")
						.add(DialogueType.PLAYER_STATEMENT)
						.setText("Thanks master!")
						.start(player);
			} else if (master.getSkill() == Skill.RUNECRAFTING) {
				player.getPacketSender().sendMessage(
						"The master has given you a task to bind " + Misc.format(player.getSkillTaskManager().getTask(master).getAmount()) + " " + player.getSkillTaskManager().getTask(master).getDescription()+ ".");
				new DialogueBuilder(DialogueType.NPC_STATEMENT)
						.setNpcChatHead(master.getMaster())
						.setExpression(DialogueExpression.SLEEPY)
						.setText("Your task is to bind " + Misc.format(player.getSkillTaskManager().getTask(master).getAmount()) + " " + player.getSkillTaskManager().getTask(master).getDescription() + ".")
						.add(DialogueType.PLAYER_STATEMENT)
						.setText("Thanks master!")
						.start(player);
			} else {
				player.getPacketSender().sendMessage(
						"The master has given you a task to " + player.getSkillTaskManager().getTask(master).getDescription() + " x" + Misc.format(player.getSkillTaskManager().getTask(master).getAmount()) + " times.");
				new DialogueBuilder(DialogueType.NPC_STATEMENT)
						.setNpcChatHead(master.getMaster())
						.setExpression(DialogueExpression.SLEEPY)
						.setText("Your task is to " + player.getSkillTaskManager().getTask(master).getDescription() + " x" + Misc.format(player.getSkillTaskManager().getTask(master).getAmount()) + " times.")
						.add(DialogueType.PLAYER_STATEMENT)
						.setText("Thanks master!")
						.start(player);
			}
			return;
		}
		/*
		 * Possible tasks
		 */
		ArrayList<SkillingTask> tasks = new ArrayList<SkillingTask>();
		/*
		 * Gets the tasks
		 */
		for (SkillingTask task : master.getTasks().getTask()) {
			if (task == null) {
				new DialogueBuilder(DialogueType.NPC_STATEMENT)
						.setNpcChatHead(master.getMaster())
						.setExpression(DialogueExpression.SLEEPY)
						.setText("I don't have any task for you at the moment.")
						.add(DialogueType.PLAYER_STATEMENT)
						.setText("Okay I will come back later!")
						.start(player);
				continue;
			}
/*			if (task.getLevelRequired() * 2 < player.getSkillManager().getCurrentLevel(master.getSkill())) {
				new DialogueBuilder(DialogueType.NPC_STATEMENT)
						.setNpcChatHead(master.getMaster())
						.setExpression(DialogueExpression.SLEEPY)
						.setText("Your skill level is too low for a skill task at the moment.")
						.add(DialogueType.PLAYER_STATEMENT)
						.setText("Okay I will come back later!")
						.start(player);
				continue;
			}*/
			if (player.getSkillManager().getCurrentLevel(master.getSkill()) >= task.getLevelRequired()) {
				tasks.add(task);
			}
		}
		/*
		 * Shuffle task
		 */
		Collections.shuffle(tasks);

		/*
		 * The random id
		 */
		final SkillingTask skillTask = Misc.random(tasks);

		if (skillTask == null)
			return;

		/*
		 * The current level
		 */
		int level = player.getSkillManager().getCurrentLevel(master.getSkill());
		/*
		 * The skill tasks
		 */
		if (level > 50) {
			level /= 3;
		}

		/**
		 * Skill task amount
		 */
		int times = Misc.randomInclusive(20, (level) * (1 + skillTask.getLevelRequired() / 10));

		if (level <= 10) {
			times /= 2;
		}
		if (master.getSkill() == Skill.HERBLORE) {
			times /= 2;
		}
		if (master.getSkill() == Skill.RUNECRAFTING) {
			times *= 5;
		}
		if (master.getSkill() == Skill.FLETCHING) {
			times *= 3;
		}
		/*
		 * The skill tasks
		 */
		SkillTask task = new SkillTask(skillTask.getInteraction(),
				times, master.getSkill(),
				skillTask.getDescription());


			if (master.getSkill() == Skill.AGILITY) {
				player.getPacketSender().sendMessage(
						"The master has given you a task to collect " + Misc.format(task.getAmount()) +" " + task.getDescription() + ".");
				player.sendMessage("You can collect Marks of grace from Brimhaven agility arena, or Agility arena tickets from courses and rooftops.");
				new DialogueBuilder(DialogueType.NPC_STATEMENT)
						.setNpcChatHead(master.getMaster())
						.setExpression(DialogueExpression.SLEEPY)
						.setText("Your task is to collect " + Misc.format(task.getAmount()) +" " + task.getDescription() + ".")
						.add(DialogueType.PLAYER_STATEMENT)
						.setText("Thanks master!")
						.start(player);
			} else if (master.getSkill() == Skill.FLETCHING) {
				player.getPacketSender().sendMessage(
						"The master has given you a task to fletch " + Misc.format(task.getAmount()) +" " + task.getDescription() + ".");
				new DialogueBuilder(DialogueType.NPC_STATEMENT)
						.setNpcChatHead(master.getMaster())
						.setExpression(DialogueExpression.SLEEPY)
						.setText("Your task is to fletch " + Misc.format(task.getAmount()) +" " + task.getDescription() + ".")
						.add(DialogueType.PLAYER_STATEMENT)
						.setText("Thanks master!")
						.start(player);
			} else if (master.getSkill() == Skill.RUNECRAFTING) {
				player.getPacketSender().sendMessage(
						"The master has given you a task to bind " + Misc.format(task.getAmount()) + " " + task.getDescription() + ".");
				new DialogueBuilder(DialogueType.NPC_STATEMENT)
						.setNpcChatHead(master.getMaster())
						.setExpression(DialogueExpression.SLEEPY)
						.setText("Your task is to bind " + Misc.format(task.getAmount()) + " " + task.getDescription() + ".")
						.add(DialogueType.PLAYER_STATEMENT)
						.setText("Thanks master!")
						.start(player);
			} else {
			player.getPacketSender().sendMessage(
					"The master has given you a task to " + task.getDescription() + " x" + Misc.format(task.getAmount()) + " times.");
				new DialogueBuilder(DialogueType.NPC_STATEMENT)
						.setNpcChatHead(master.getMaster())
						.setExpression(DialogueExpression.SLEEPY)
						.setText("Your task is to " + task.getDescription() + " x" + Misc.format(task.getAmount()) + " times.")
						.add(DialogueType.PLAYER_STATEMENT)
						.setText("Thanks master!")
						.start(player);
		}

		player.getSkillTaskManager().setTask(master, task);
	}

	/**
	 * Attempting to perform a skill task
	 * 
	 * @param player
	 *            the player
	 * @param interaction
	 *            the interaction
	 */
	public static void perform(Player player, int interaction, int amount, SkillMasterType master) {
		/*
		 * No task
		 */
		if (player.getSkillTaskManager().getTask(master) == null) {
			return;
		}
		/*
		 * The skilling task
		 */
		SkillTask task = player.getSkillTaskManager().getTask(master);
		/*
		 * Interaction
		 */
		if (task.hasInteraction(interaction)) {
			/*
			 * Decreases amount
			 */
			if (task.getAmount() > 0) {
				/*
				 * Thieving extra
				 */
			/*	if (task.getSkill() == Skill.MINING) {
					task.setAmount(task.getAmount() - (EquipmentUtil.isWearingMiningSet(player) ? 2 : amount));
				} else if (task.getSkill() == Skill.THIEVING) {
					task.setAmount(task.getAmount() - (EquipmentUtil.isWearingThievingSet(player) ? 2 : amount));
				} else if (task.getSkill() == Skill.WOODCUTTING) {
					task.setAmount(task.getAmount() - (EquipmentUtil.isWearingWoodcuttingSet(player) ? 2 : amount));
				} else if (task.getSkill() == Skill.FISHING) {
					task.setAmount(task.getAmount() - (EquipmentUtil.isWearingFishingSet(player) ? 2 : amount));
				} else {
					task.setAmount(task.getAmount() - amount);
				}*/
				task.setAmount(task.getAmount() - amount);
				/*
				 * Fix task
				 */
				if (task.getAmount() < 0) {
					task.setAmount(0);
				}
				/*
				 * Completed
				 */
				if (task.getAmount() == 0) {
					player.sendMessage("@red@You've completed your skilling task. Please return to the " + task.getSkill().getName() + " master for a reward.");
					player.getPacketSender().sendJinglebitMusic(96, 0);
				} else if (task.getAmount() % 5 == 0) {
					player.sendMessage("@red@Skilling task left for " + task.getSkill().getName() + " skill: " + Misc.format(task.getAmount()));
				}
			}
		}
	}


	/**
	 * Checking completion
	 * 
	 * @param player
	 *            the player
	 * @param master
	 *            the master
	 */
	public static void checkCompletion(Player player, SkillMasterType master) {
		/*
		 * No task
		 */
		if (player.getSkillTaskManager().getTask(master) == null) {
			return;
		}
		/*
		 * The task
		 */
		SkillTask task = player.getSkillTaskManager().getTask(master);
		/*
		 * The dialogue
		 */
		Dialogue dialogue = null;
		/*
		 * More to do
		 */
		if (task.getAmount() > 0) {
			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.ANGRY;
				}

				@Override
				public String[] dialogue() {
					if (master.getSkill() == Skill.AGILITY) {
						return new String[]{

								"You currently have a skilling task. Your task",
								"is to collect " + Misc.format(task.getAmount()) + " " + task.getDescription() + ".",
								"Come back to me when you have finished.",};
					} else if (master.getSkill() == Skill.FLETCHING) {
						return new String[]{
								"You currently have a skilling task. Your task",
								"is to fletch " + Misc.format(task.getAmount()) +" " + task.getDescription() + ".",
								"Come back to me when you have finished.",};
					} else if (master.getSkill() == Skill.RUNECRAFTING) {
						return new String[]{
								"You currently have a skilling task. Your task",
								"is to bind " + Misc.format(task.getAmount()) +" " + task.getDescription() + ".",
								"Come back to me when you have finished.",};
					} else {
						return new String[]{

								"You currently have a skilling task. Your task",
								"is to " + task.getDescription() + " " + Misc.format(task.getAmount()) + " more times.",
								"Come back to me when you have finished.",};
					}
				}

				@Override
				public int npcId() {
					return master.getMaster();
				}

				@Override
				public Dialogue nextDialogue() {

					int cancelation = task.getInitialAmount() * SkillTaskConstants.CANCELATION_FEE;

					player.setDialogueOptions(new DialogueOptions() {
						@Override
						public void handleOption(Player player, int option) {
							switch (option) {
								case 1:
									cancelTask(player, cancelation, master);
									break;
								case 2:
									new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
											.setText("I'll be back later. Bye!")
											.start(player);
									break;
							}
						}
					});
					return new Dialogue() {

						@Override
						public DialogueType type() {
							return DialogueType.OPTION;
						}

						@Override
						public DialogueExpression animation() {
							return null;
						}

						@Override
						public String[] dialogue() {
							return new String[] { "Cancel skilling task for " + (NumberFormat.getInstance().format(cancelation))
									+ " coins.", "I'll be back later.", };
						}
					};
				}
			};
			/*
			 * Completed
			 */
		} else if (task.getAmount() <= 0) {

			/*
			 * The skill level
			 */
			int lvl = player.getSkillManager().getCurrentLevel(task.getSkill()) + 1;
			/*
			 * The money reward
			 */
			int moneyReward = (int) ((int) ((((task.getInitialAmount() * lvl) * (SkillTaskConstants.TIME_REWARD)) / 2) * 25) * 55.89);
			/*
			 * The points
			 */
			int points = (int) (task.getInitialAmount() * SkillTaskConstants.POINTS_REWARD);

			PlayerTaskManager.progressTask(player, DailyTask.SKILLING_TASK);
			PlayerTaskManager.progressTask(player, WeeklyTask.SKILLING_TASK);

			// Custom variables temporarily to make it match properly the economy
			if (task.getSkill() == Skill.RUNECRAFTING) {
				points /= 5;
				moneyReward /= 5;
			}
			if (task.getSkill() == Skill.AGILITY) {
				points *= 0.65;
				moneyReward *= 2;
			}
			if (task.getSkill() == Skill.FLETCHING) {
				points /= 4;
				moneyReward /= 4;
			}

			int finalMoneyReward = moneyReward;
			int finalPoints = points;

			dialogue = new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.HAPPY;
				}

				@Override
				public String[] dialogue() {
					return new String[] {

							"You have completed your skilling task. Choose",
							"your reward. " + NumberFormat.getInstance().format(finalMoneyReward) + " coins or " + NumberFormat.getInstance().format(finalPoints) + " " + Misc.ucFirst(task.getSkill().name())
									+ " points?"};
				}

				@Override
				public int npcId() {
					return master.getMaster();
				}

				@Override
				public Dialogue nextDialogue() {

					player.setDialogueOptions(new DialogueOptions() {
						@Override
						public void handleOption(Player player, int option) {
							switch (option) {
							case 1:
								selectReward(player, 1, finalMoneyReward, master);
								break;
							case 2:
								selectReward(player, 2, finalPoints, master);
								break;
							}
						}
					});
					return new Dialogue() {

						@Override
						public DialogueType type() {
							return DialogueType.OPTION;
						}

						@Override
						public DialogueExpression animation() {
							return null;
						}

						@Override
						public String[] dialogue() {
							return new String[] { NumberFormat.getInstance().format(finalMoneyReward) + " gp coins.",
									NumberFormat.getInstance().format(finalPoints) + " "
											+ Misc.ucFirst(task.getSkill().name()) + " Points", };
						}
					};
				}
			};
		}
		DialogueManager.start(player, dialogue);
	}

	/**
	 * Checking completion of task
	 * @return true if completed the skill task
	 * @param player
	 *            the player
	 * @param master
	 *            the master
	 */
	private static boolean checkSkillTaskCompletion(Player player, SkillMasterType master) {
		SkillTask task = player.getSkillTaskManager().getTask(master);
		if (task != null) {
			if (task.getAmount() <= 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Player selecting reward
	 * 
	 * @param player
	 *            the player
	 * @param type
	 *            the type
	 * @param amount
	 *            the amount
	 */
	private static void selectReward(Player player, int type, int amount, SkillMasterType master) {
		/*
		 * No task
		 */
		if (player.getSkillTaskManager().getTask(master) == null) {
			return;
		}
		/*
		 * The skilling task
		 */
		SkillTask task = player.getSkillTaskManager().getTask(master);
		/*
		 * Task not completed
		 */
		if (task.getAmount() > 0) {
			return;
		}
		switch (type) {
		case SkillTaskConstants.MONEY_REWARD:
			/*
			 * No space
			 */
			if (player.getInventory().countFreeSlots() == 0 && !player.getInventory().contains(995)) {
				DialogueManager.sendStatement(player, "You don't have enough inventory space left to collect your reward.");
				return;
			}
			/*
			 * Adds the reward
			 */
			player.getInventory().add(new Item(995, amount));
			player.getPacketSender().sendMessage("You have chosen the @dre@" + NumberFormat.getInstance().format(amount)
					+ "</col> coins as your reward.");
			/*
			 * Announcement
			 */
			if (amount > SkillTaskConstants.MONEY_ANNOUNCEMENT) {
				PlayerUtil.broadcastMessage("<img=760> " + PlayerUtil.getImages(player) + "" + player.getUsername() +" has just claimed @dre@"
						+ NumberFormat.getInstance().format(amount) + "</col> coins from @dre@" + task.getSkill().getName()
						+ " </col>skilling task!");
			}
			break;
		case SkillTaskConstants.SKILL_POINT_REWARD:
			player.getSkillTaskManager().getPoints()[task.getSkill().ordinal()] += amount;
			player.getPacketSender().sendMessage("You have chosen the @dre@" + NumberFormat.getInstance().format(amount)
					+ " Skilling points</col> as your reward.");
			if (task.getSkill().getName().toLowerCase().contains("mining")) {
				AchievementManager.processFor(AchievementType.DWARF_ASSISTANCE, amount, player);
			}
			/*
			 * Announcement
			 */
			if (amount > SkillTaskConstants.POINTS_ANNOUNCEMENT) {
				PlayerUtil.broadcastMessage("<img=760> " + PlayerUtil.getImages(player) + "" + player.getUsername() +" has just claimed @dre@"
						+ NumberFormat.getInstance().format(amount) + " " + task.getSkill().getName()
						+ " skilling points</col> from @dre@" + task.getSkill().getName() + "</col> skilling task!");
			}
			break;
		}
		int partiPoints = player.getSkillTaskManager().getTask(master).getInitialAmount() / 40;
		if (partiPoints < 1) {
			partiPoints = 1;
		}
		if (task.getSkill() == Skill.RUNECRAFTING) {
			partiPoints /= 5;
		}
		//player.getPoints().increase(Points.PARTICIPATION_POINTS,partiPoints);
		ParticipationPoints.addPoints(player, partiPoints, "@dre@from skilling tasks</col>.");
		AchievementManager.processFor(AchievementType.SKILLING_CHAMPION, player);
		player.setDialogueOptions(null);
		player.getPacketSender().sendInterfaceRemoval();
		player.getSkillTaskManager().setTask(master, null);
	}

	private static void cancelTask(Player player, int amount, SkillMasterType master) {

		if (player.getSkillTaskManager().getTask(master) == null) {
			return;
		}

		SkillTask task = player.getSkillTaskManager().getTask(master);

		if (task.getAmount() == 0) {
			return;
		}

		if (!player.getInventory().contains(new Item(995, amount))) {
			DialogueManager.sendStatement(player, "You don't have enough money to cancel the task.");
			return;
		}
		player.getInventory().delete(new Item(995, amount));
		player.getSkillTaskManager().setTask(master, null);
		DialogueManager.sendStatement(player, "You have successfully cancelled your skilling task.");
	}

	private static void getCape(Player player, SkillMasterType master) {

		if (player.getSkillManager().getCurrentLevel(master.getSkill()) < 99) {
			DialogueManager.sendStatement(player,
					"You need have a level of 99 " + Misc.ucFirst(master.getSkill().name()) + " to claim the cape.");
			return;
		}

		if (player.getInventory().countFreeSlots() < 2) {
			DialogueManager.sendStatement(player, "You need at least two inventory slots to claim the cape.");
			return;
		}

		int mastery = player.getSkillManager().countSkillsMastered();

		new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
				.setText("I'd like to receive a cape please.").add(DialogueType.NPC_STATEMENT)
				.setText("You are surely eligible to get a cape from me!", "This cape is only worn by the true masters", "of the " + Misc.ucFirst(master.getSkill().name()) + " skill. Each skill cape has different", "perks when equipped including Boosted skill XP.").add(DialogueType.NPC_STATEMENT)
				.setText("You can read our @dre@Wiki</col> page for more information", "about each cape and its different perks.").add(DialogueType.NPC_STATEMENT)
				.setText("Enough for the talk and let me hand you the cape.").add(DialogueType.OPTION).firstOption("Take the skill cape.", player1 -> {
				if (mastery > 1) {
					player.getInventory().add(new Item(master.getCape() + 1));
				} else {
					player.getInventory().add(new Item(master.getCape()));
				}
				player.getInventory().add(new Item(master.getCape() + 2));
				DialogueManager.sendStatement(player,
						"You claim your " + Misc.ucFirst(master.getSkill().name()) + " skill cape and hood from your master!");

		}).secondOption("I'll take it later.", player1 -> {
			new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
					.setText("I'll take it later.").add(DialogueType.NPC_STATEMENT)
					.setText("You are welcome to take it at anytime!").setExpression(DialogueExpression.HAPPY).start(player);
		}).start(player);
	}

	/**
	 * Gets the points
	 * 
	 * @param player
	 *            the player
	 * @param skill
	 *            the skill
	 * @return the skill points
	 */
	public static int getPoints(Player player, Skill skill) {
		return player.getSkillTaskManager().getPoints()[skill.ordinal()];
	}

	/**
	 * Sets the task
	 *
	 * @return the task
	 */
	public SkillTask getTask(SkillMasterType master) {
		return tasks.get(master);
	}

	/**
	 * Returns the map of the skill task for each master.
	 */
	public Map<SkillMasterType, SkillTask> getAllTasks() {
		return tasks;
	}

	/**
	 * Sets the task map.
	 */
	public void setTasks(Map<SkillMasterType, SkillTask> tasks) {
		this.tasks = tasks;
	}

	/**
	 * Returns a set containing all SkillMasters for which the player has a task.
	 */
	public Set<SkillMasterType> getTasksManagers() {
		return tasks.entrySet().stream()
				.filter(it -> it.getValue() != null)
				.map(Map.Entry::getKey)
				.collect(Collectors.toSet());
	}

	/**
	 * Finds the first task the player has, or null
	 */
	public SkillTask getFirstTask() {
		Set<SkillMasterType> current = getTasksManagers();

		if(current.isEmpty()) return null;
		return tasks.get(current.iterator().next());
	}

	/**
	 * Sets the task
	 * 
	 * @param task
	 *            the task
	 */
	public void setTask(SkillMasterType master, SkillTask task) {
		tasks.put(master, task);
	}

	/**
	 * Set the tasks to null for all masters.
	 */
	public void resetTasks() {
		tasks.forEach((key, value) -> tasks.replace(key, null));
	}

	/**
	 * Sets the points
	 *
	 * @return the points
	 */
	public int[] getPoints() {
		return points;
	}

	/**
	 * Sets the points
	 *
	 * @param points
	 *            the points
	 */
	public void setPoints(int[] points) {
		this.points = points == null || points.length == 0
				? new int[24]
				: Arrays.copyOf(points, 24);
	}
}
