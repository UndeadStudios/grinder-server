package com.grinder.game.content.skill.skillable.impl.slayer;

import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.item.jewerly.BraceletOfSlaughter;
import com.grinder.game.content.item.jewerly.ExpeditiousBracelet;
import com.grinder.game.content.skill.skillable.impl.slayer.SlayerRewards.Rewards;
import com.grinder.game.content.skill.skillable.impl.slayer.superior.SuperiorSlayerManager;
import com.grinder.game.content.skill.skillable.impl.slayer.superior.SuperiorSlayerMonsters;
import com.grinder.game.content.task_new.DailyTask;
import com.grinder.game.content.task_new.PlayerTaskManager;
import com.grinder.game.content.task_new.WeeklyTask;
import com.grinder.game.definition.NpcDefinition;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager.Points;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueExpression;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.game.model.item.container.bank.BankUtil;
import com.grinder.game.model.item.container.shop.ShopManager;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;
import com.grinder.util.ShopIdentifiers;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Handles slayer
 * 
 * @author 2012
 *
 */
public class SlayerManager {

	public static boolean DISABLED_SUPERIOR_SLAYER = false;

	/**
	 * The coins rate
	 */
	private static final int MONEY_RATE = 28;

	/**
	 * The unlock interface
	 */
	public static final int UNLOCK = 60114;

	/**
	 * The extend interface
	 */
	public static final int EXTEND = 60101;

	/**
	 * The confirm interface
	 */
	public static final int CONFIRM = 60105;

	/**
	 * The shop interface
	 */
	public static final int SHOP = 60118;

	/**
	 * The shop container id
	 */
	public static final int SHOP_CONTAINER = 60121;

	/**
	 * The task interface
	 */
	private static final int TASK = 60159;

	/**
	 * The unlocked config
	 */
	private static final int UNLOCKED_CONFIG = 1100;

	/**
	 * The extended config
	 */
	private static final int EXTENDED_CONFIG = 1117;

	/**
	 * The blocked monsters
	 */
	private ArrayList<String> blockedMonsters = new ArrayList<String>();

	/**
	 * The slayer task
	 */
	private SlayerTask task;

	/**
	 * The last cancelled task
	 */
	private SlayerMasterTask lastCancelledTask;

	/**
	 * The confirm type
	 */
	private ConfirmType type;

	/**
	 * The unlockables
	 */
	private final boolean[] unlocked = new boolean[19];

	/**
	 * The extended
	 */
	private final boolean[] extended = new boolean[20];

	/**
	 * The reward
	 */
	private Rewards reward;

	/**
	 * The confirm type
	 */
	public enum ConfirmType {
		CANCEL, BLOCK, UNLOCK, EXTEND;

		public boolean requiresSlayerTask(){
			return this == CANCEL || this == BLOCK;
		}
	}

	/**
	 * Opening the interface
	 * 
	 * @param player
	 *            the player
	 * @param id
	 *            the id
	 */
	public static void open(Player player, int id) {
		switch (id) {
		case UNLOCK:
			int config = 0;
			for (boolean unlocked : player.getSlayer().getUnlocked()) {
				player.getPacketSender().sendConfig(UNLOCKED_CONFIG + config, unlocked ? 0 : 1);
				config++;
			}
			break;
		case EXTEND:
			config = 0;
			for (boolean unlocked : player.getSlayer().getExtended()) {
				player.getPacketSender().sendConfig(EXTENDED_CONFIG + config, unlocked ? 0 : 1);
				config++;
			}
			break;
		case TASK:
			player.getPacketSender().sendString(60162, "Empty");
			player.getPacketSender().sendString(60164, "Empty");
			player.getPacketSender().sendString(60166, "Empty");
			player.getPacketSender().sendString(60168, "Empty");
			player.getPacketSender().sendString(60170, "Empty");
			player.getPacketSender().sendString(60172, "Empty");

			int slot = 60162;

			for (String string : player.getSlayer().getBlockedMonsters()) {

				player.getPacketSender().sendString(slot, string);
				slot += 2;
			}

			if (player.getSlayer().getTask() == null) {
				player.getPacketSender().sendString(60176, "No current Slayer task");
			} else {
				player.getPacketSender().sendString(60176,
						player.getSlayer().getTask().getName() + " x" + player.getSlayer().getTask().getAmountLeft());
			}
			break;
		}
		player.getPacketSender().sendString(62101,
				"" + NumberFormat.getInstance().format(player.getPoints().get(Points.SLAYER_POINTS)));
		player.getPacketSender().sendSound(73, 5);
		player.getPacketSender().sendInterface(id);
	}

	/**
	 * Gets an assignment
	 * 
	 * @param player
	 *            the player
	 * @param id
	 *            the id
	 * @param dialogue
	 *            the dialogue
	 */
	public static void getAssignment(Player player, int id, boolean dialogue) {
		if (!dialogue) {
			getAssignment(player, id);
		}
	}
	
	/**
	 * Has no slayer task option
	 * 
	 * @param player
	 *            the player
	 */
	private static void sendNoTaskOption(Player player, int id) {

		SlayerMaster master = SlayerMaster.forMasterID(id);

		if (master == null)
			return;

		new DialogueBuilder(DialogueType.NPC_STATEMENT)
				.setNpcChatHead(master.getMasterID())
				.setExpression(DialogueExpression.EVIL)
				.setText("You don't have a Slayer task. Would you like", "me to you give one?").add(DialogueType.OPTION)
				.setOptionTitle("Choose an Option.").firstOption("Yes please.", player1 -> {
				getAssignment(player, id);
		}).addCancel("Maybe later!").start(player);

	}


	/**
	 * Starting the dialogue with Slayer master
	 *
	 * @param player
	 *            the player
	 */
	public static void talkToSlayerMaster(Player player, int id) {
		new DialogueBuilder(DialogueType.NPC_STATEMENT)
				.setNpcChatHead(id)
				.setExpression(DialogueExpression.CALM)
				.setText("Greetings @dre@" + player.getUsername() + "</col>,", "How can I help you?").add(DialogueType.OPTION)
				.setOptionTitle("Choose an option").firstOption("Get a Slayer assignment.", player1 -> {
					getAssignment(player, id);
		}).secondOption("What do you have for sale?", player1 -> {
			new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
					.setNpcChatHead(id)
					.setExpression(DialogueExpression.CALM)
					.setText("What do you have for sale?").add(DialogueType.NPC_STATEMENT)
					.setText("I have a though selection of Slayer equipments", " that you can use to slay different types of monsters", "as each one requires a different item to be used.").add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.HAPPY)
					.setText("Would you like to have a look?").add(DialogueType.OPTION).setOptionTitle("Choose an Option.")
					.firstOption("Yes please.", player2 -> {
						ShopManager.open(player, ShopIdentifiers.SLAYER_EQUIPMENTS_STORE);
					}).addCancel("No.")
					.start(player);
		}).thirdOption("Ask about Slayer rewards.", player1 -> {
			new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
					.setNpcChatHead(id)
					.setExpression(DialogueExpression.CALM)
					.setText("What are the Slayer rewards?").add(DialogueType.NPC_STATEMENT)
					.setText("You may choose between a variety of Slayer", "categories. First, you can @red@Unlock Perks</col> that", "will allow you to have extra privileges. Secondly,", "you can learn @red@How to Craft</col> a slayer headgear").add(DialogueType.NPC_STATEMENT)
					.setText("You may also @red@Purchase Items</col> that are recommended", "by the Slayer experts from my rewards store.").add(DialogueType.NPC_STATEMENT)
					.setText("You may spend points to @red@Cancel</col> or @red@Block</col>", "your current task. If you cancel it, you", "may be assigned that target again in future.", "If you block it, you won't get that assignment", "task again.").add(DialogueType.NPC_STATEMENT).setExpression(DialogueExpression.HAPPY)
					.setText("Would you like to view the selection of the rewards?").add(DialogueType.OPTION).setOptionTitle("Choose an Option.")
					.firstOption("Yes, I want to have a look.", player2 -> {
						SlayerManager.open(player, SlayerManager.UNLOCK);
					}).addCancel("Maybe next time.")
					.start(player);
		}).fourthOption("Tell me more about Slayer.", player1 -> {
			new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
					.setNpcChatHead(id)
					.setExpression(DialogueExpression.CALM)
					.setText("Can you tell me more about Slayer?").add(DialogueType.NPC_STATEMENT)
					.setText("Pfftt..Slayer should be one of your first skills to train", "upon starting your journey. This is because you get the", "chance to level up your Slayer level and your combat skills", "at the same time while Slayer tasks. Moreover, completing").add(DialogueType.NPC_STATEMENT)
					.setText("Slayer tasks will reward you with @dre@Blood money</col> that you", "can use to buy some of the best equipments around here.", "There are 7 Slayer master's and each master assigns a task", "with different difficulty, quantity, and requirements.").add(DialogueType.NPC_STATEMENT)
					.setText("For example, @dre@Nieve</col> assigns bosses task while others assign", "Wilderness only tasks. It's for you to decide to which master", "you want to go to. Keep in mind that some masters", "only assign tasks for the though and high combat players.").add(DialogueType.NPC_STATEMENT)
					.setText("Completion of tasks rewards you with Slayer points.", "You can use these points to @red@Unlock Perks</col> that will allow you", "to have extra privileges. Secondly,", "you can @red@Learn how to Craft</col> a slayer headgear.").add(DialogueType.NPC_STATEMENT)
					.setText("You may also @red@Purchase items</col> that are recommended", "by the Slayer experts. You may spend these points", "to @red@Cancel</col> or @red@Block</col> your current task.").add(DialogueType.NPC_STATEMENT)
					.setText("Superior slayer monsters are more powerful versions of", "normal slayer monsters that have a 1/200 chance to spawn", "upon the death of one of its normal counterparts.", "They can be encountered after purchasing..").add(DialogueType.NPC_STATEMENT)
					.setText("the unlock @dre@Bigger and Badder</col> for @dre@150 Slayer reward points</col>", "from any Slayer master, and will only occur while", "on a Slayer task. This can be toggled on and off afterwards", "When a superior slayer monster spawns, a message will appear..").add(DialogueType.NPC_STATEMENT)
					.setText("in the chatbox, stating @red@\"A superior foe has appeared...\"</col> ", "Killing a superior slayer monster will reward you with a", "generous amount of Slayer experience — for most,", "but not all of them, this is about ten times..").add(DialogueType.NPC_STATEMENT)
					.setText("the Hitpoints of the superior monster. This experience", "will even be granted if the superior monster is slain", "after the completion of the corresponding Slayer task.").add(DialogueType.NPC_STATEMENT)
					//.setText("", " ", "", "").add(DialogueType.NPC_STATEMENT)
					//.setText("", " ", "", "").add(DialogueType.NPC_STATEMENT)
					.setText("Finally, being a member can also reduce the", "quantity of the task you get.").add(DialogueType.NPC_STATEMENT)
					.setText("If you still need more information you can always", "visit our @dre@Wiki</col> page guides for more specific information.").add(DialogueType.NPC_STATEMENT)
					.setText("Now please leave me alone.").setExpression(DialogueExpression.DISTRESSED)
					.add(DialogueType.PLAYER_STATEMENT)
					.setText("..Okay goodbye..")
					.start(player);
		}).addCancel("Never mind.").start(player);
	}


	/**
	 * Checking the existing Slayer task
	 * 
	 * @param player
	 *            the player
	 */
	public static void check(Player player, int id) {
		if (player.getSlayer().getTask() == null) {
			sendNoTaskOption(player, id);
			return;
		}
		if (player.getSlayer().getTask().getAmountLeft() > 1) {
			new DialogueBuilder(DialogueType.NPC_STATEMENT)
					.setNpcChatHead(id)
					.setExpression(DialogueExpression.CALM)
					.setText("Your current Slayer task assignment is to",  "slay " + player.getSlayer().getTask().getAmountLeft() + " more @dre@" + player.getSlayer().getTask().getName() + "'s!").add()
					.setText("Good luck with that @dre@" + player.getUsername() + "</col>!")
					.start(player);
		} else {
			new DialogueBuilder(DialogueType.NPC_STATEMENT)
					.setNpcChatHead(id)
					.setExpression(DialogueExpression.CALM)
					.setText("Your current Slayer task assignment is to", "slay " + player.getSlayer().getTask().getAmountLeft() + " more @dre@" + player.getSlayer().getTask().getName() + "'!").add()
					.setText("Good luck with that @dre@" + player.getUsername() + "</col>!")
					.start(player);
		}
	}

	/**
	 * Gets an assignment
	 */
	private static void getAssignment(Player player, int id) {

		if (!EntityExtKt.passedTime(player, Attribute.GENERIC_ACTION, 1, TimeUnit.SECONDS, false, true)) {
            return;
        }

		SlayerMaster master = SlayerMaster.forMasterID(id);

		if (master == null)
			return;

		if (player.getSkillManager().calculateCombatLevel() < master.getRequiredCombat()) {
			new DialogueBuilder(DialogueType.NPC_STATEMENT)
					.setNpcChatHead(master.getMasterID())
					.setExpression(DialogueExpression.ANNOYED)
					.setText("I will start assigning you Slayer tasks once you", "haved reached a Combat level of @dre@" + master.getRequiredCombat() +"</col> or more!").add()
					.setText("Come back to me once you're strong enough.")
					.start(player);
			return;
		}

		if (player.getSkillManager().getMaxLevel(Skill.SLAYER) < master.getRequiredSlayer()) {
			new DialogueBuilder(DialogueType.NPC_STATEMENT)
					.setNpcChatHead(master.getMasterID())
					.setExpression(DialogueExpression.DISTRESSED)
					.setText("You must have a Slayer level of at ", "least @dre@" + master.getRequiredSlayer() +"</col> before I can assign you a Slayer task.").add()
					.setText("Come back when you have advanced your Slayer level.")
					.start(player);
			return;
		}
		/*
		 * Already has a task
		 */
		if (player.getSlayer().getTask() != null) {
			new DialogueBuilder(DialogueType.NPC_STATEMENT)
					.setNpcChatHead(master.getMasterID())
					.setExpression(DialogueExpression.DISTRESSED)
					.setText("You already have a Slayer task.", "Finish or cancel your existing task first.")
					.start(player);
			return;
		}
		/*
		 * The task
		 */
		SlayerTask task = master.getTask(player);
		if (task == null) {
			new DialogueBuilder(DialogueType.NPC_STATEMENT)
					.setNpcChatHead(master.getMasterID())
					.setExpression(DialogueExpression.CURIOUS)
					.setText("I don't have a task for you, perhaps someone else does?")
					.start(player);
			return;
		}
		/*
		 * Sets the task
		 */
		player.getSlayer().setTask(task);

		// Cheap fix just in case
		if (task.getAmountLeft() <= 0) {
			task.setAmountLeft(40 + Misc.random(20));
		}

		new DialogueBuilder(DialogueType.NPC_STATEMENT)
				.setNpcChatHead(master.getMasterID())
				.setExpression(DialogueExpression.EVIL_2)
				.setText("Your new Slayer task is to ", " slay @dre@" + task.getName() + "'s</col> " + task.getAmountLeft() + " times!")
				.start(player);
		player.sendMessage("You have been assigned a Slayer task to slay @dre@" + task.getName() + "</col> " + task.getAmountLeft() + " times!");
	}

	/**
	 * Determines if the monster being fought is a monster tasked for slayer.
	 * @param player Player in the combat.
	 * @return If monster in combat is tasked against the player.
	 */
	public static boolean isFightingTaskedMonster(Player player) {
		final SlayerTask task = player.getSlayer().getTask();

		if(task == null)
			return false;

		final Agent agent = player.getCombat().getTarget();
		if (!(agent instanceof NPC)) {
			return false;
		}
		return isMonsterPartOfTask(player, agent.getAsNpc().fetchDefinition());
	}

	public static boolean isMonsterPartOfTask(Player player, NpcDefinition npcDefinition) {


		final SlayerTask task = player.getSlayer().getTask();

		if (task == null)
			return false;

		final String taskName = task.getName().toLowerCase();
		final String npcName = npcDefinition.getName().toLowerCase();

		if (taskName.contains("monkey guard") && npcName.equals("guard"))
			return false;

		if (npcName.contains(taskName))
			return true;

		if (taskName.toLowerCase().startsWith("TzTok") || npcName.equals("TzTok-Jad"))
			return true;

		return isMonsterPartOfTask(task.getMonster(), npcName);
	}

	public static boolean isMonsterPartOfTask(SlayerMonsterType monsterType, String npcName) {
		final String[] otherNames = monsterType.getOtherNames();

		if (otherNames != null) {
			for (String names :otherNames) {
				if (npcName.contains(names)) {
					return true;
				}
			}
		}
		return monsterType.toString().toLowerCase().contains(npcName);
	}

	/**
	 * Progresses on task
	 * 
	 * @param player
	 *            the player
	 * @param npc
	 *            the npc
	 */
	public static void progress(Player player, NPC npc) {

		final SlayerManager slayerManager = player.getSlayer();
		final SlayerTask slayerTask = slayerManager.getTask();

		if (slayerTask == null)
			return;

		final NpcDefinition definition = npc.fetchDefinition();

		Optional<SuperiorSlayerMonsters> optionalSuperiorSlayerMonster = Optional.empty();

		boolean found = isMonsterPartOfTask(player, definition);

		if (!found) {
			optionalSuperiorSlayerMonster = SuperiorSlayerMonsters.forId(npc.getId());
		}

		boolean isSuperiorSlayerMonster = optionalSuperiorSlayerMonster.isPresent();

		if (found || isSuperiorSlayerMonster) {

			final Position npcPosition = npc.getSpawnPosition().clone();

			/*
			 * Rolls the chance to spawn the superior
			 * slayer monster
			 */
			if (!DISABLED_SUPERIOR_SLAYER && !isSuperiorSlayerMonster) {
				SuperiorSlayerManager.handleSuperiorMonster(player, slayerTask.getMonster(), npcPosition);
			}
			if (slayerTask.getAmountLeft() > 0) {

				if (player.getSlayer().getTask() != null && player.getSlayer().getTask().getMaster() != null) {
					if (slayerTask.getMaster().equals(SlayerMaster.KRYSTILIA)) {
						if (player.getSlayer().getTask().getMonster().isInWilderness() && player.getWildernessLevel() <= 0) { // Don't count if the task is wilderness type and the killed npc was not in the wilderness
							return;
						}
					}
				}

				if (!BraceletOfSlaughter.INSTANCE.handleSlaughterEffect(player))
					player.getSlayer().getTask().setAmountLeft(player.getSlayer().getTask().getAmountLeft() - 1);

				ExpeditiousBracelet.INSTANCE.handleExpeditiousBracelet(player);

				final int slayerXP = isSuperiorSlayerMonster
						? optionalSuperiorSlayerMonster.get().getExperienceReward()
						: definition.getHitpoints();

				if (slayerXP > 0)
					player.getSkillManager().addExperience(Skill.SLAYER, slayerXP);


				player.getPoints().increase(Points.SLAYER_NPC_KILLS);
				AchievementManager.processFor(AchievementType.DEDICATED_SLAYER, player);
				PlayerTaskManager.progressTask(player, DailyTask.SLAYER_KILLS);
				PlayerTaskManager.progressTask(player, WeeklyTask.SLAYER_KILLS);

				if (slayerTask.getAmountLeft() <= 0) {
					player.getPoints().increase(Points.SLAYER_STREAK);

					if (slayerTask.getMaster() == null) { // Fail-safe
						player.getSlayer().getTask().setMaster(SlayerMaster.TURAEL);
					}

					int taskDifficulty = player.getSlayer().getTask().getMaster().getDifficulty();
					int bonusReward = 0;
					int participationPointsReward = (taskDifficulty * 3) + Misc.getRandomInclusive(4);

					int slayerXPReward = (int) ((slayerTask.getInitialAmount() * 1.5) * (taskDifficulty <= 1 ? 2 : taskDifficulty) * (player.getSkillManager().getSkills().getMaxLevels()[Skill.SLAYER.ordinal()] / 9));

					// XP rewards
					if (slayerTask.getMonster().isBoss()) { // 10k xp for finishing a boss task
						player.getSkillManager().addExperience(Skill.SLAYER, 10_000);
					} else {
						player.getSkillManager().addExperience(Skill.SLAYER, slayerXPReward);
					}

					int cashMultiplier = (int) (((taskDifficulty * 60.5) * MONEY_RATE));

					if (taskDifficulty == 2) {
						cashMultiplier *= 1.2;
					} else if (taskDifficulty > 2 && taskDifficulty < 5) {
						cashMultiplier *= 1.5;
					} else if (taskDifficulty >= 5) {
						cashMultiplier *= 2.2;
					}

					if (player.getPoints().get(Points.SLAYER_STREAK) % 50 == 0) {
						bonusReward = (int) slayerTask.getMaster().getFiftyTaskBonus() / 2;
						participationPointsReward += 25 + Misc.getRandomInclusive(25);
						//cashMultiplier += 1_000_000;
					} else if (player.getPoints().get(Points.SLAYER_STREAK) % 10 == 0) {
						bonusReward = (int) slayerTask.getMaster().getTenTaskBonus() / 2;
						participationPointsReward += 5 + Misc.getRandomInclusive(5);
						//cashMultiplier += 550_000;
					}

					int rewardPoints = slayerTask.getMaster().getRewardPoints() + bonusReward;
					player.getPoints().increase(Points.SLAYER_POINTS, rewardPoints);
					player.getPoints().increase(Points.TOTAL_SLAYER_POINTS_RECEIVED, rewardPoints);

					player.getPoints().increase(Points.PARTICIPATION_POINTS, participationPointsReward);

					AchievementManager.processFor(AchievementType.SLAYER_CONTROL, player);
					AchievementManager.processFor(AchievementType.SLAYER_ELITE, player);
					AchievementManager.processFor(AchievementType.SLAYER_MAJOR, player);
					AchievementManager.processFor(AchievementType.SLAYER_NOVICE, player);

					cashMultiplier *= 500; // Change after fixing economy

					player.sendMessage("<img=91><col=006600> You have completed " + player.getPoints().get(Points.SLAYER_STREAK)
							+ " tasks in a row and gain " + rewardPoints
							+ " Slayer points! Return to a Slayer Master for more.");

					String currencyReward = NumberFormat.getInstance().format(cashMultiplier);

					if (player.getGameMode().isUltimate()) {
						if (player.getInventory().countFreeSlots() > 1) {
							player.getInventory().add(ItemID.COINS, cashMultiplier);
						} else {
							ItemContainerUtil.dropUnder(player, ItemID.COINS, cashMultiplier);
						}
						player.getPacketSender()
								.sendMessage("<img=91><col=006600> You have received " + currencyReward
										+ " coins for completing a Slayer task. The reward is dropped under you.");
					} else { // Not UIM
						if (player.getInventory().countFreeSlots() > 0) {
							ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(ItemID.COINS, cashMultiplier));
							player.getPacketSender()
									.sendMessage("<img=91><col=006600> You have received " + currencyReward + " coins for completing a Slayer task.");
						} else {
							player.getPacketSender()
									.sendMessage("<img=91><col=006600> You have received " + currencyReward
											+ " coins for completing a Slayer task. Your reward was sent to your bank.");
							BankUtil.addToBank(player, new Item(ItemID.COINS, cashMultiplier));
						}
					}
					//if (cashMultiplier > 6000) {
					//	PlayerUtil.broadcastMessage("<img=91>@red@ " + player.getUsername() + " has received " + currencyReward + " Blood money for completing a Slayer task.");
					//}
					if (cashMultiplier > 5_000_000) {
						PlayerUtil.broadcastMessage("<img=91><col=006600> " + PlayerUtil.getImages(player) + "" + player.getUsername() +" has received " + currencyReward + " coins for completing the Slayer task.");
					}

					slayerManager.setTask(null);
				}
			}
		}
	}

	/**
	 * Completes the task which is used via a command
	 *
	 * @param player the player
	 */
	public static void completeTaskByCommand(Player player) {


		final SlayerManager slayerManager = player.getSlayer();
		final SlayerTask slayerTask = slayerManager.getTask();

		if (slayerTask == null)
			return;

		AchievementManager.processFor(AchievementType.DEDICATED_SLAYER, player);
		player.getPoints().increase(Points.SLAYER_STREAK);
		//player.getPoints().increase(Points.SLAYER_NPC_KILLS);

		if (slayerTask.getMaster() == null) { // Fail-safe
			player.getSlayer().getTask().setMaster(SlayerMaster.TURAEL);
		}

		int taskDifficulty = player.getSlayer().getTask().getMaster().getDifficulty();
		int bonusReward = 0;
		int participationPointsReward = (taskDifficulty * 3) + Misc.getRandomInclusive(4);

		int slayerXPReward = (int) ((slayerTask.getInitialAmount() * 1.5) * (taskDifficulty <= 1 ? 2 : taskDifficulty) * (player.getSkillManager().getSkills().getMaxLevels()[Skill.SLAYER.ordinal()] / 9));

		// XP rewards
		if (slayerTask.getMonster().isBoss()) { // 10k xp for finishing a boss task
			player.getSkillManager().addExperience(Skill.SLAYER, 10_000);
		} else {
			player.getSkillManager().addExperience(Skill.SLAYER, slayerXPReward);
		}

		int cashMultiplier = (int) (((taskDifficulty * 60.5) * MONEY_RATE));

		if (taskDifficulty == 2) {
			cashMultiplier *= 1.2;
		} else if (taskDifficulty > 2 && taskDifficulty < 5) {
			cashMultiplier *= 1.5;
		} else if (taskDifficulty >= 5) {
			cashMultiplier *= 2.2;
		}

		if (player.getPoints().get(Points.SLAYER_STREAK) % 50 == 0) {
			bonusReward = (int) slayerTask.getMaster().getFiftyTaskBonus() / 2;
			participationPointsReward += 25 + Misc.getRandomInclusive(25);
			//cashMultiplier += 1_000_000;
		} else if (player.getPoints().get(Points.SLAYER_STREAK) % 10 == 0) {
			bonusReward = (int) slayerTask.getMaster().getTenTaskBonus() / 2;
			participationPointsReward += 5 + Misc.getRandomInclusive(5);
			//cashMultiplier += 550_000;
		}

		int rewardPoints = slayerTask.getMaster().getRewardPoints() + bonusReward;
		player.getPoints().increase(Points.SLAYER_POINTS, rewardPoints);
		player.getPoints().increase(Points.TOTAL_SLAYER_POINTS_RECEIVED, rewardPoints);

		player.getPoints().increase(Points.PARTICIPATION_POINTS, participationPointsReward);

		AchievementManager.processFor(AchievementType.SLAYER_CONTROL, player);
		AchievementManager.processFor(AchievementType.SLAYER_ELITE, player);
		AchievementManager.processFor(AchievementType.SLAYER_MAJOR, player);
		AchievementManager.processFor(AchievementType.SLAYER_NOVICE, player);


		player.sendMessage("<img=91><col=006600> You have completed " + player.getPoints().get(Points.SLAYER_STREAK)
				+ " tasks in a row and gain " + rewardPoints
				+ " Slayer points! Return to a Slayer Master for more.");

		cashMultiplier *= 500; // Change after fixing economy
		String currencyReward = NumberFormat.getInstance().format(cashMultiplier);


		if (player.getGameMode().isUltimate()) {
			if (player.getInventory().countFreeSlots() > 1) {
				player.getInventory().add(ItemID.COINS, cashMultiplier);
			} else {
				ItemContainerUtil.dropUnder(player, ItemID.COINS, cashMultiplier);
			}
			player.getPacketSender()
					.sendMessage("<img=91><col=006600> You have received " + currencyReward
							+ " coins for completing a Slayer task. The reward is dropped under you.");
		} else { // Not UIM
			if (player.getInventory().countFreeSlots() > 0) {
				ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(ItemID.COINS, cashMultiplier));
				player.getPacketSender()
						.sendMessage("<img=91><col=006600> You have received " + currencyReward + " coins for completing a Slayer task.");
			} else {
				player.getPacketSender()
						.sendMessage("<img=91><col=006600> You have received " + currencyReward
								+ " coins for completing a Slayer task. Your reward was sent to your bank.");
				BankUtil.addToBank(player, new Item(ItemID.COINS, cashMultiplier));
			}
		}

		if (cashMultiplier > 5_000_000) {
			PlayerUtil.broadcastMessage("<img=91><col=006600> " + PlayerUtil.getImages(player) + "" + player.getUsername() +" has received " + currencyReward + " coins for completing the Slayer task.");
		}

		slayerManager.setTask(null);
	}



	/**
	 * Cancelling current task
	 * 
	 * @param player
	 *            the player
	 */
	private static void cancelTask(Player player) {
		/*
		 * No task
		 */
		if (player.getSlayer().getTask() == null) {
			player.getPacketSender().sendMessage("You don't have a Slayer task to cancel.");
			return;
		}
		/*
		 * The task
		 */
		SlayerTask task = player.getSlayer().getTask();
		/*
		 * Display
		 */
		player.getPacketSender().sendString(60107, task.getName());
		player.getPacketSender().sendString(60108,
				"Your current task will be cancelled, and\\nthe Slayer Masters will be blocked from\\nassignning this category to you next task.");
		player.getPacketSender().sendString(60109, "@red@Cost: 30 Slayer Points");
		player.getPacketSender().sendString(60110, "This task will be available in the future tasks");
		player.getPacketSender().sendInterface(CONFIRM);
		player.getSlayer().setType(ConfirmType.CANCEL);
	}

	/**
	 * Blocking current task
	 * 
	 * @param player
	 *            the player
	 */
	private static void blockTask(Player player) {
		/*
		 * No task
		 */
		if (player.getSlayer().getTask() == null) {
			player.getPacketSender().sendMessage("You don't have a Slayer task to block.", 1000);
			return;
		}
		/*
		 * Checks blocked amount
		 */
		if (player.getSlayer().getBlockedMonsters().size() == 6) {
			player.getPacketSender().sendMessage("You've reached max blocked monsters. Unblock some to add this one.", 1000);
			return;
		}
		/*
		 * Already contains
		 */
		if (player.getSlayer().getBlockedMonsters().contains(player.getSlayer().getTask().getName())) {
			return;
		}

		/*
		 * The task
		 */
		SlayerTask task = player.getSlayer().getTask();
		/*
		 * Display
		 */
		player.getPacketSender().sendString(60107, task.getName());
		player.getPacketSender().sendString(60108,
				"This creature will be blocked and the\\nSlayer Masters won't assign you this task\\nuntil you unblock it");
		player.getPacketSender().sendString(60109, "@red@Cost: 100 points");
		player.getPacketSender().sendString(60110,
				"If you unblock this creature in future, you\\nwon't get your points back");
		player.getPacketSender().sendInterface(CONFIRM);
		player.getSlayer().setType(ConfirmType.BLOCK);
	}

	/**
	 * The confirm option
	 * 
	 * @param player
	 *            the player
	 */
	private static void confirm(Player player) {


		if (player.getSlayer().getType().requiresSlayerTask()) {
			if (player.getSlayer().getTask() == null) {
				player.getPacketSender().sendMessage("You must have a Slayer task before doing so.", 1000);
				return;
			}
		}

		/*
		 * Cancelling task
		 */
		if (player.getSlayer().getType().equals(ConfirmType.CANCEL)) {



			/*
			 * Checks points
			 */
			if (player.getPoints().get(Points.SLAYER_POINTS) < 30) {
				player.getPacketSender().sendMessage("You don't have enough Slayer points to cancel this task.", 1000);
				return;
			}
			/*
			 * Cancel task
			 */
			player.getPoints().decrease(Points.SLAYER_POINTS, 30);
			player.getSlayer().setTask(null);
			AchievementManager.processFor(AchievementType.MISTAKES_HAPPEN, player);
			open(player, TASK);
		} else if (player.getSlayer().getType().equals(ConfirmType.BLOCK)) {
			/*
			 * Checks points
			 */
			if (player.getPoints().get(Points.SLAYER_POINTS) < 100) {
				player.getPacketSender().sendMessage("You don't have enough Slayer points to block this creature.", 1000);
				return;
			}
			/*
			 * Cancel task
			 */
			player.getPoints().decrease(Points.SLAYER_POINTS, 100);
			player.getSlayer().getBlockedMonsters().add(player.getSlayer().getTask().getName());
			player.getSlayer().setTask(null);
			AchievementManager.processFor(AchievementType.CANT_STOP_ME, player);
			open(player, TASK);
		} else if (player.getSlayer().getType().equals(ConfirmType.UNLOCK)) {
			/*
			 * The reward
			 */
			Rewards reward = player.getSlayer().getReward();
			/*
			 * Unlocks reward
			 */
			if (reward == null) {
				return;
			}
			/*
			 * Purchase
			 */
			player.getPoints().decrease(Points.SLAYER_POINTS, reward.getCost());
			player.getSlayer().getUnlocked()[reward.getRewardIndex()] = true;
			if (reward.ordinal() == 5) {
				AchievementManager.processFor(AchievementType.FOCUSED_SLAYER, player);
			}
			open(player, UNLOCK);
		} else if (player.getSlayer().getType().equals(ConfirmType.EXTEND)) {
			/*
			 * The reward
			 */
			Rewards reward = player.getSlayer().getReward();
			/*
			 * Unlocks reward
			 */
			if (reward == null) {
				return;
			}

			System.out.println(reward.toString());
			System.out.println(reward.getRewardIndex());

			/*
			 * Purchase
			 */
			player.getPoints().decrease(Points.SLAYER_POINTS, reward.getCost());
			player.getSlayer().getExtended()[reward.getRewardIndex()] = true;
			open(player, EXTEND);
		}

	}

	/**
	 * Unblocking
	 * 
	 * @param player
	 *            the player
	 * @param id
	 *            the id
	 */
	private static void unblock(Player player, int id) {
		/*
		 * Higher id
		 */
		if (id >= player.getSlayer().getBlockedMonsters().size()) {
			player.sendMessage("You do not have any task to unblock.");
			return;
		}
		/*
		 * Unblocks
		 */
		player.getPacketSender().sendMessage(player.getSlayer().getBlockedMonsters().get(id)
				+ " has been unblocked. This creature can now be assigned to you.");
		player.getSlayer().getBlockedMonsters().remove(id);
		open(player, TASK);

	}

	/**
	 * Handles the buttons
	 * 
	 * @param player
	 *            the player
	 * @param button
	 *            the button
	 * @return the button
	 */
	public static boolean handleButton(Player player, int button) {
		switch (button) {
		case 60174:
			cancelTask(player);
			return true;
		case 62102:
			open(player, UNLOCK);
			return true;
		case 62103:
			open(player, EXTEND);
			return true;
		case 62104:
			//player.getPacketSender().sendMessage("This reward shop is currently unavailable for purchase.", 1000);
			ShopManager.open(player, 37);
			player.getPacketSender().sendSound(73, 5);
			return true;
		case 60111:
			if (player.getSlayer().getType().equals(ConfirmType.BLOCK)
					|| player.getSlayer().getType().equals(ConfirmType.CANCEL)) {
				open(player, TASK);
			} else if (player.getSlayer().getType().equals(ConfirmType.UNLOCK)) {
				open(player, UNLOCK);
			} else if (player.getSlayer().getType().equals(ConfirmType.EXTEND)) {
				open(player, EXTEND);
			}
			return true;
		case 62105:
			player.getPacketSender().sendString(62101,
					"" + NumberFormat.getInstance().format(player.getPoints().get(Points.SLAYER_POINTS)));
			open(player, TASK);
			return true;
		case 60112:
			confirm(player);
			return true;
		case 60175:
			blockTask(player);
			return true;
		case 60163:
			unblock(player, 0);
			return true;
		case 60165:
			unblock(player, 1);
			return true;
		case 60167:
			unblock(player, 2);
			return true;
		case 60169:
			unblock(player, 3);
			return true;
		case 60171:
			unblock(player, 4);
			return true;
		case 60173:
			unblock(player, 5);
			return true;
		}
		return false;
	}

	/**
	 * Sets the task
	 *
	 * @return the task
	 */
	public SlayerTask getTask() {
		return task;
	}

	/**
	 * Sets the task
	 * 
	 * @param task
	 *            the task
	 */
	public void setTask(SlayerTask task) {
		this.task = task;
	}

	/**
	 * Sets the blockedMonsters
	 *
	 * @return the blockedMonsters
	 */
	public ArrayList<String> getBlockedMonsters() {
		return blockedMonsters;
	}

	/**
	 * Sets the blockedMonsters
	 * 
	 * @param blockedMonsters
	 *            the blockedMonsters
	 */
	public void setBlockedMonsters(ArrayList<String> blockedMonsters) {
		this.blockedMonsters = blockedMonsters;
	}

	/**
	 * Sets the lastCancelledTask
	 *
	 * @return the lastCancelledTask
	 */
	public SlayerMasterTask getLastCancelledTask() {
		return lastCancelledTask;
	}

	/**
	 * Sets the lastCancelledTask
	 * 
	 * @param lastCancelledTask
	 *            the lastCancelledTask
	 */
	public void setLastCancelledTask(SlayerMasterTask lastCancelledTask) {
		this.lastCancelledTask = lastCancelledTask;
	}

	/**
	 * Sets the unlocked
	 *
	 * @return the unlocked
	 */
	public boolean[] getUnlocked() {
		return unlocked;
	}

	/**
	 * Sets the unlocked
	 * 
	 * @param unlocked
	 *            the unlocked
	 */
	public void setUnlocked(boolean[] unlocked) {
		System.arraycopy(unlocked, 0, this.unlocked, 0, unlocked.length);
	}

	/**
	 * Sets the type
	 *
	 * @return the type
	 */
	public ConfirmType getType() {
		return type;
	}

	/**
	 * Sets the type
	 * 
	 * @param type
	 *            the type
	 */
	public void setType(ConfirmType type) {
		this.type = type;
	}

	/**
	 * Sets the extended
	 *
	 * @return the extended
	 */
	public boolean[] getExtended() {
		return extended;
	}

	/**
	 * Sets the extended
	 * 
	 * @param extended
	 *            the extended
	 */
	public void setExtended(boolean[] extended) {
		System.arraycopy(extended, 0, this.extended, 0, extended.length);
	}

	/**
	 * Sets the reward
	 *
	 * @return the reward
	 */
	public Rewards getReward() {
		return reward;
	}

	/**
	 * Sets the reward
	 * 
	 * @param reward
	 *            the reward
	 */
	public void setReward(Rewards reward) {
		this.reward = reward;
	}
}
