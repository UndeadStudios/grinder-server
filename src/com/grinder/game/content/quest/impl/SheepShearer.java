package com.grinder.game.content.quest.impl;


import com.grinder.game.content.quest.Quest;
import com.grinder.game.content.quest.QuestDialogueLoader;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.interfaces.dialogue.DialogueOptions;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

/**
 *
 * @author Dexter Morgan <https://www.rune-server.ee/members/dexter+morgan/>
 *
 */
public class SheepShearer extends Quest {

	public SheepShearer() {
		super("Sheep Shearer", false, 1, 2);
	}

	private static final Item BALL_OF_WOOL = new Item(1759);

	private static final Item WOOL = new Item(1737);

	private static final Item SHEARS = new Item(1735);

	private static final Animation SHAVING = new Animation(893);

	public static void shaveSheep(Player p, NPC npc) {
		if(!p.getInventory().contains(SHEARS)) {
			p.sendMessage("You need shears to shave a sheep.");
			return;
		}

		if(p.getInventory().countFreeSlots() == 0) {
			p.sendMessage("You don't have any inventory space.");
			p.getPacketSender().sendSound(Sounds.INVENTORY_FULL_SOUND);
			return;
		}

		final int npcId = npc.getId();

		p.getMotion().clearSteps();

		p.performAnimation(SHAVING);

		p.getPacketSender().sendSound(Sounds.SHEAR_SHEEP);

		p.getInventory().add(WOOL);

		npc.setNpcTransformationId(2692);

		npc.resetEntityInteraction();

		npc.getMotion().clearSteps();

		TaskManager.submit(new Task(15) {
			@Override
			protected void execute() {
				npc.setNpcTransformationId(npcId);
				stop();
			}
		});
	}

	@Override
	public String[][] getDescription(Player player) {

		boolean ready = player.getInventory().getAmount(BALL_OF_WOOL.getId()) == 20;

		int toCollect = 20 - player.getInventory().getAmount(BALL_OF_WOOL.getId());

		return new String[][] {

				{ "<col=010080>I can start this quest by speaking to @dre@Farmer Fred<col=010080> at his",
						"@dre@farm <col=010080>just a little way @dre@North West of Lumbridge</col>.", "" },

				{ "Fred farmer asked me to collect 20 balls of wool",
						ready ? "I have enough balls of wool to give Fred and get my reward!"
								: "I need to collect " + toCollect + " more balls of wool." },

				{ "", "I have given Fred the wool and he has rewarded me." },

		};
	}

	@Override
	public int[] getQuestNpcs() {
		return new int[] { 732 };
	}

	@Override
	public boolean hasRequirements(Player player) {
		return true;
	}

	@Override
	public DialogueOptions getDialogueOptions(Player player) {
		DialogueOptions option = null;

		int dialogue = player.getDialogue().id();

		switch (getStage(player)) {
		case 0:
			option = new DialogueOptions() {

				@Override
				public void handleOption(Player player, int option) {
					switch (option) {
					case 1:
						if (quest.getStage(player) == 0) {
							switch (dialogue) {
							case 0:
								QuestDialogueLoader.sendDialogue(player, quest, 6);
								break;
							case 6:
								QuestManager.increaseStage(player, quest);
								QuestDialogueLoader.sendDialogue(player, quest, 23);
								break;
							}
						}
						break;
					case 2:
						if (quest.getStage(player) == 0) {
							switch (dialogue) {
							case 0:
								QuestDialogueLoader.sendDialogue(player, quest, 4);
								break;
							case 6:
								QuestDialogueLoader.sendDialogue(player, quest, 19);
								break;
							case 11:
								QuestDialogueLoader.sendDialogue(player, quest, 16);
								break;
							case 19:
								QuestDialogueLoader.sendDialogue(player, quest, 22);
								break;
							}
						}
						break;
					case 3:
						if (quest.getStage(player) == 0) {
							switch (dialogue) {
							case 0:
								QuestDialogueLoader.sendDialogue(player, quest, 2);
								break;
							case 6:
								QuestDialogueLoader.sendDialogue(player, quest, 11);
								break;
							}
						}
						break;
					}
				}
			};
			break;
		}
		return option;
	}

	@Override
	public boolean hasStartDialogue(Player player, int npcId) {
		return false;
	}

	@Override
	public void getEndDialogue(Player player, int npcId) {
		int id = player.getDialogue().id();

		if(id == 9) {
			increaseStage(player);
			sendDialogue(player, 23);
		}

		if (quest.getStage(player) == 1 && npcId == 732) {
			if (player.getDialogue().id() == 25) {
				QuestDialogueLoader.sendDialogue(player, quest,
						player.getInventory().contains(new Item(BALL_OF_WOOL.getId(), 20)) ? 28 : 27);
			} else if (player.getDialogue().id() == 30) {
				if (player.getInventory().contains(new Item(BALL_OF_WOOL.getId(), 20))) {
					player.getInventory().delete(new Item(BALL_OF_WOOL.getId(), 20));
					QuestManager.increaseStage(player, quest);
					QuestManager.complete(player, quest, new String[] { "15,000 Crafting XP", "5,000,000 coins." },
							1735);
					player.getInventory().add(new Item(995, 5_000_000));
					player.getSkillManager().addFixedDelayedExperience(Skill.CRAFTING, 15_000);
				}
			}
		}
	}
	
	@Override
	public Position getTeleport() {
		return null;
	}
}
