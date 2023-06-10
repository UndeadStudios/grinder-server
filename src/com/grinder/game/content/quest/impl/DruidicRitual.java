package com.grinder.game.content.quest.impl;


import com.grinder.game.content.quest.Quest;
import com.grinder.game.content.quest.QuestDialogueLoader;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.interfaces.dialogue.DialogueOptions;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

/**
 * 
 * @author Dexter Morgan
 *         <https://www.rune-server.ee/members/102745-dexter-morgan/>
 *
 */
public class DruidicRitual extends Quest {

	private static final Item RAW_RAT_MEAT = new Item(2134);

	private static final Item RAW_BEAR_MEAT = new Item(2136);

	private static final Item RAW_BEEF = new Item(2132);

	private static final Item RAW_CHICKEN = new Item(2138);

	private static final Item ENCHANTED_BEEF = new Item(522);

	private static final Item ENCHANTED_RAT = new Item(523);

	private static final Item ENCHANTED_BEAR = new Item(524);

	private static final Item ENCHANTED_CHICKEN = new Item(525);

	private static final Item[] RAW_MEATS = {
			RAW_RAT_MEAT, RAW_BEAR_MEAT, RAW_BEEF, RAW_CHICKEN
	};

	private static final Item[] ENCHANTED_MEATS = { ENCHANTED_BEAR, ENCHANTED_BEEF, ENCHANTED_CHICKEN, ENCHANTED_RAT };

	public DruidicRitual() {
		super("Druidic Ritual", false, 4, 4);
	}

	@Override
	public String[][] getDescription(Player player) {
		return new String[][] {

				{ "", "I can begin this quest by talking to Kaqemeex!", "He is in trouble with the dark wizards." },

				{ "", "Kaqemeex said I need to speak to Sanfew,", "he will tell me more." },

				{ "", "Sanfew explained I need to collect these items:", "",
						QuestManager.hasItem(player, RAW_BEEF, "Raw beef"),
						QuestManager.hasItem(player, RAW_RAT_MEAT, "Raw rat meat"),
						QuestManager.hasItem(player, RAW_BEAR_MEAT, "Raw bear meat"),
						QuestManager.hasItem(player, RAW_CHICKEN, "Raw chicken"), "",
						"And take them to the Cauldron of Thunder in Taverley." },

				{ "", "I have given all the meats to Sanfew, I need to return", "to Kaqemeex!" },

				{ "" },

		};
	}

	@Override
	public int[] getQuestNpcs() {
		return new int[] { 5045, 5044 };
	}

	@Override
	public DialogueOptions getDialogueOptions(Player player) {
		DialogueOptions option = null;

		switch (getStage(player)) {
		case 0:
			option = new DialogueOptions() {
				@Override
				public void handleOption(Player player, int option) {
					switch (option) {
					case 1:
						QuestManager.increaseStage(player, quest);
						QuestDialogueLoader.sendDialogue(player, quest, 7);
						break;
					case 2:
						player.getPacketSender().sendInterfaceRemoval();
						break;
					}
				}
			};
			break;
		}
		return option;
	}

	@Override
	public void getEndDialogue(Player player, int npcId) {
		int id = player.getDialogue().id();

		switch (getStage(player)) {
		case 1:
			QuestManager.increaseStage(player, quest);
			player.getPacketSender().sendInterfaceRemoval();
			break;
		case 2:
			if (id == 15) {
				boolean hasAll = player.getInventory().contains(ENCHANTED_MEATS);
				QuestDialogueLoader.sendDialogue(player, quest, hasAll ? 18 : 17);
			} else if (id == 19) {

				player.getInventory().delete(ENCHANTED_MEATS);

				QuestManager.increaseStage(player, quest);
				QuestDialogueLoader.sendDialogue(player, quest, 21);
			}
			break;

		case 3:
			QuestManager.increaseStage(player, quest);
			QuestManager.complete(player, quest, new String[] { "1,000 Herblore XP", "Access to the Herblore skill." },
					525);
			player.getSkillManager().addFixedDelayedExperience(Skill.HERBLORE, 1000);
			break;
		}
	}

	@Override
	public boolean handleItemOnObjectInteraction(Player player, Item item, GameObject object) {
		if (item.getId() == RAW_BEAR_MEAT.getId() || item.getId() == RAW_BEEF.getId()
				|| item.getId() == RAW_CHICKEN.getId() || item.getId() == RAW_RAT_MEAT.getId()) {
			if (object.getId() == 2142) {
				if (!player.getInventory().contains(RAW_MEATS)) {
					player.getPacketSender().sendMessage("I should collect all the meats before trying to dip them in..");
					return true;
				}

				player.performAnimation(new Animation(896));
				player.playSound(new Sound(2577));
				player.BLOCK_ALL_BUT_TALKING = true;
				TaskManager.submit(new Task(1) {
					@Override
					protected void execute() {
						stop();
						player.getInventory().delete(RAW_MEATS);
						player.getInventory().addItemSet(ENCHANTED_MEATS);
						player.BLOCK_ALL_BUT_TALKING = false;
						player.getPacketSender().sendMessage("You dip all the raw meats into the cauldron..");
						player.getPacketSender().sendMessage("..and retrieve enchanted versions!");
					}
				});

				return true;
			}
		}

		return false;
	}

	@Override
	public boolean hasRequirements(Player player) {
		return true;
	}

	@Override
	public Position getTeleport() {
		return null;
	}
}
