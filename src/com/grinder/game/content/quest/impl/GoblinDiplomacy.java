package com.grinder.game.content.quest.impl;


import com.grinder.game.content.quest.Quest;
import com.grinder.game.content.quest.QuestDialogueLoader;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.interfaces.dialogue.DialogueOptions;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.sound.Sounds;

/**
 *
 * @author Dexter Morgan <https://www.rune-server.ee/members/dexter+morgan/>
 *
 */
public class GoblinDiplomacy extends Quest {

	public GoblinDiplomacy() {
		super("Goblin Diplomacy", false, 3, 4);
	}

	private static final Item GOBLIN_MAIL = new Item(288);

	private static final Item RED_DYE = new Item(1763);

	private static final Item BLUE_DYE = new Item(1767);

	private static final Item YELLOW_DYE = new Item(1765);

	private static final Item ORANGE_DYE = new Item(1769);

	private static final Item RED_GOBLIN_MAIL = new Item(9054);

	private static final Item BLUE_GOBLIN_MAIL = new Item(287);

	private static final Item YELLOW_GOBLIN_MAIL = new Item(9056);

	private static final Item ORANGE_GOBLIN_MAIL = new Item(286);

	private static final Item[] ALL_MAILS = {
		RED_GOBLIN_MAIL, BLUE_GOBLIN_MAIL, YELLOW_GOBLIN_MAIL, ORANGE_GOBLIN_MAIL
	};

	private static void dyeGoblinMail(Player player, Item dye, Item mail) {
		player.getPacketSender().sendMessage("You pour the dye over the goblin mail..");
		player.getInventory().delete(dye);
		player.getInventory().replace(GOBLIN_MAIL, mail);
		player.getInventory().refreshItems();
		player.getPacketSender().sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND);
	}

	private void sendMailScene(Player player, int dialogue) {
		QuestDialogueLoader.sendDialogue(player, quest, dialogue);
	}

	@Override
	public String[][] getDescription(Player player) {
		return new String[][] {

				{ "", "<col=010080>There's a disturbance in the @dre@Goblin Village.<col=010080> Help",
						"<col=010080>the @dre@goblins <col=010080>solve their dispute so the world",
						"<col=010080>doesn't have to worry about rioting goblins." },

				{ "", "I have agreed to help General Wartface and Bentnoze.",
						"They have requested that I bring a new goblin mail", "in orange colour.", "",
						QuestManager.hasItem(player, ORANGE_GOBLIN_MAIL,
								" I should dye a normal goblin mail with an orange dye."),

				},

				{ "", "I have brought the generals orange goblin armour",
						"but they didn't like it so instead they have requested",
						"that I bring the a blue goblin mail.", "", QuestManager.hasItem(player, BLUE_GOBLIN_MAIL,
								" I should dye a normal goblin mail with an blue dye."), },

				{ "", "They didn't like neither of the new colour goblin mails.",
						"They have requested that I bring a brown, normal", "goblin mail. ", "",
						QuestManager.hasItem(player, GOBLIN_MAIL, "1x goblin mail."),

				},
				
				{"", "I have helped the goblins solve their dilemma, and they",
					"have rewarded me."
				}

		};
	}

	@Override
	public int[] getQuestNpcs() {
		return new int[] { 669, 670 };
	}

	@Override
	public boolean hasRequirements(Player player) {
		return true;
	}

	@Override
	public DialogueOptions getDialogueOptions(Player player) {
		DialogueOptions option = null;
		int dialogue = player.getDialogue().id();
		option = new DialogueOptions() {
			@Override
			public void handleOption(Player player, int option) {
				switch (option) {
				case 3:
					if (quest.getStage(player) == 0) {
						QuestDialogueLoader.sendDialogue(player, quest, 11);
					if (dialogue == 0) {
					} else if (dialogue == 11) {
						QuestDialogueLoader.sendDialogue(player, quest, 15);
					}
				} else if (quest.getStage(player) == 1) {
					if (dialogue == 28) {
						QuestDialogueLoader.sendDialogue(player, quest, 29);
					}
				} else if (quest.getStage(player) == 2) {
					if (dialogue == 45) {
						QuestDialogueLoader.sendDialogue(player, quest, 46);
					}
				}else if (quest.getStage(player) == 3) {
					if (dialogue == 61) {
						QuestDialogueLoader.sendDialogue(player, quest, 62);
					}
				}
					break;
				}
			}
		};
		return option;
	}

	@Override
	public boolean hasStartDialogue(Player player, int npcId) {
		return false;
	}

	@Override
	public void getEndDialogue(Player player, int npcId) {
		int dialogue = player.getDialogue().id();
		if (quest.getStage(player) == 0) {
			if (dialogue == 21) {
				player.getPacketSender().sendInterfaceRemoval();
				QuestManager.increaseStage(player, quest);
			}
		} else if (quest.getStage(player) == 1) {
			if (dialogue == 25) {
				QuestDialogueLoader.sendDialogue(player, quest,
						player.getInventory().contains(ORANGE_GOBLIN_MAIL) ? 28 : 27);
			} else if (dialogue == 32) {
				sendMailScene(player, 34);
			} else if (dialogue == 39) {
				player.getPacketSender().sendInterfaceRemoval();
				QuestManager.increaseStage(player, quest);
			}
		} else if (quest.getStage(player) == 2) {
			if (dialogue == 43) {
				QuestDialogueLoader.sendDialogue(player, quest,
						player.getInventory().contains(BLUE_GOBLIN_MAIL) ? 46 : 27);
			} else if (dialogue == 55) {
				player.getPacketSender().sendInterfaceRemoval();
				QuestManager.increaseStage(player, quest);
			}
		} else if (quest.getStage(player) == 3) {
			if (dialogue == 59) {
				QuestDialogueLoader.sendDialogue(player, quest,
						player.getInventory().contains(GOBLIN_MAIL) ? 62 : 27);
			} else if (dialogue == 69) {
				player.getInventory().delete(ALL_MAILS);
				QuestManager.increaseStage(player, quest);
				QuestManager.complete(player, quest,
						new String[] { "150,000 Attack XP" }, 2357);
				player.getSkillManager().addFixedDelayedExperience(Skill.ATTACK, 150_000);
			}
		}
	}

	@Override
	public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
		if (usedWith.getId() == GOBLIN_MAIL.getId()) {
			if (use.getId() == RED_DYE.getId()) {
				dyeGoblinMail(player, RED_DYE, RED_GOBLIN_MAIL);
				return true;
			} else if (use.getId() == YELLOW_DYE.getId()) {
				dyeGoblinMail(player, YELLOW_DYE, YELLOW_GOBLIN_MAIL);
				return true;
			} else if (use.getId() == BLUE_DYE.getId()) {
				dyeGoblinMail(player, BLUE_DYE, BLUE_GOBLIN_MAIL);
				return true;
			} else if (use.getId() == ORANGE_DYE.getId()) {
				dyeGoblinMail(player, ORANGE_DYE, ORANGE_GOBLIN_MAIL);
				return true;
			}
			else if (use.getId() == GOBLIN_MAIL.getId()) {
				if (usedWith.getId() == RED_DYE.getId()) {
					dyeGoblinMail(player, RED_DYE, RED_GOBLIN_MAIL);
					return true;
				} else if (usedWith.getId() == YELLOW_DYE.getId()) {
					dyeGoblinMail(player, YELLOW_DYE, YELLOW_GOBLIN_MAIL);
					return true;
				} else if (usedWith.getId() == BLUE_DYE.getId()) {
					dyeGoblinMail(player, BLUE_DYE, BLUE_GOBLIN_MAIL);
					return true;
				} else if (usedWith.getId() == ORANGE_DYE.getId()) {
					dyeGoblinMail(player, ORANGE_DYE, ORANGE_GOBLIN_MAIL);
					return true;
				}
			}
		} else if (use.getId() == YELLOW_DYE.getId() && usedWith.getId() == RED_DYE.getId()
		|| usedWith.getId() == YELLOW_DYE.getId() && use.getId() == RED_DYE.getId()) {
			player.getInventory().delete(usedWith);
			player.getInventory().replace(use, ORANGE_DYE);
			player.getInventory().refreshItems();
			player.getPacketSender().sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND);
			player.getPacketSender().sendMessage("You mix the yellow and red dye and make an orange dye.");
			return true;
		}
		return false;
	}
	
	@Override
	public Position getTeleport() {
		return null;
	}
}
