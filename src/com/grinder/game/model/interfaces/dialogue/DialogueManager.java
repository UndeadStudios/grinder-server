package com.grinder.game.model.interfaces.dialogue;

import com.grinder.game.content.miscellaneous.WelcomeManager;
import com.grinder.game.content.miscellaneous.WelcomeManager.WelcomeStage;
import com.grinder.game.content.quest.Quest;
import com.grinder.game.content.quest.QuestDialogue;
import com.grinder.game.content.quest.QuestDialogueLoader;
import com.grinder.game.definition.NpcDefinition;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.net.packet.PacketSender;
import org.apache.commons.lang.WordUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the loading and start of dialogues.
 *
 * @author relex lawl
 */

public class DialogueManager {

	/**
	 * A value representing the interface id for a dialogue.
	 */
	public static final int CHATBOX_INTERFACE_ID = 50;
	/**
	 * This array contains the child id where the dialogue statement starts for npc
	 * and item dialogues.
	 */
	private static final int[] NPC_DIALOGUE_ID = { 4885, 4890, 4896, 4903 };
	/**
	 * This array contains the child id where the dialogue statement starts for
	 * player dialogues.
	 */
	private static final int[] PLAYER_DIALOGUE_ID = { 971, 976, 982, 989 };
	/**
	 * This array contains the child id where the dialogue statement starts for
	 * option dialogues.
	 */
	private static final int[] OPTION_DIALOGUE_ID = { 13760, 2461, 2471, 2482, 2494 };
	/**
	 * A {@link Map} containing all of our {@link Dialogue}s.
	 */
	public static Map<Integer, Dialogue> dialogues = new HashMap<Integer, Dialogue>();

	/**
	 * Starts a dialogue gotten from the dialogues map.
	 *
	 * @param player
	 *            The player to dialogue with.
	 * @param id
	 *            The id of the dialogue to retrieve from dialogues map.
	 */
	public static void start(Player player, int id) {
		Dialogue dialogue = dialogues.get(id);
		start(player, dialogue);
	}

	/**
	 * Starts a dialogue.
	 *
	 * @param player
	 *            The player to dialogue with.
	 * @param dialogue
	 *            The dialogue to show the player.
	 */
	public static void start(Player player, Dialogue dialogue) {

		// If player isn't currently in a dialogue and they are busy,
		// simply send interface removal.
		if (player.getDialogue() == null) {
			if (player.busy()) {
				player.getPacketSender().sendInterfaceRemoval();
			}
		}

		// Update our dialogue state
		player.setDialogue(dialogue);

		// If dialogue is null, send interface removal.
		// Otherwise, show the dialogue!
		if (dialogue == null || dialogue.id() < 0) {
			player.getPacketSender().sendInterfaceRemoval();
		} else {
			dialogue.preAction(player);
			showDialogue(player, dialogue);
			dialogue.postAction(player);
		}
	}

	public static void startSelected(Player player, int selected) {
		startSelected(player, selected, DialogueExpression.HAPPY);
	}

	public static void startSelected(Player player, int selected, DialogueExpression animation) {
		String[] text = new String[] { player.getDialogue().dialogue()[selected - 1] };
		Dialogue dialogue = new Dialogue() {
			@Override
			public DialogueType type() {
				return DialogueType.PLAYER_STATEMENT;
			}

			@Override
			public DialogueExpression animation() {
				//System.out.println(animation.getAnimation().getId());
				return animation;
			}

			@Override
			public String[] dialogue() {
				return text;
			}
		};

		start(player, dialogue);
	}

	/**
	 * Handles the clicking of 'click here to continue', option1, option2 and so on.
	 *
	 * @param player
	 *            The player who will continue the dialogue.
	 */
	public static void next(Player player) {
		//System.out.println(player.tutorialStage);
		if (player.isInTutorial()) {
		if (EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 6) {
			WelcomeManager.welcome(player, WelcomeStage.TUTORIAL6);
			return;
		} else if (EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 7) {
			WelcomeManager.welcome(player, WelcomeStage.TUTORIAL7);
			return;
		} else if (EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 8) {
			WelcomeManager.welcome(player, WelcomeStage.TUTORIAL8);
			return;
		} else if (EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 9) {
			WelcomeManager.welcome(player, WelcomeStage.TUTORIAL9);
			return;
/*		} else if (player.tutorialStage == 10) {
			WelcomeManager.welcome(player, WelcomeStage.TUTORIAL10);
			return;*/
		} else if (EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 11) {
			WelcomeManager.welcome(player, WelcomeStage.TUTORIAL11);
			return;
		} else if (EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 12) {
			WelcomeManager.welcome(player, WelcomeStage.TUTORIAL12);
			return;
		} else if (EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 13) {
			WelcomeManager.welcome(player, WelcomeStage.TUTORIAL13);
			return;
		} else if (EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 14) {
			WelcomeManager.welcome(player, WelcomeStage.TUTORIAL14);
			return;
		} else if (EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 15) {
			WelcomeManager.welcome(player, WelcomeStage.TUTORIAL15);
			return;
		} else if (EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 16) {
			WelcomeManager.welcome(player, WelcomeStage.TUTORIAL16);
			return;
		} else if (EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 17) {
			WelcomeManager.welcome(player, WelcomeStage.TUTORIAL17);
			return;
		} else if (EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 18) {
			WelcomeManager.welcome(player, WelcomeStage.TUTORIAL18);
			return;
		} else if (EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 19) {
			WelcomeManager.welcome(player, WelcomeStage.TUTORIAL19);
			return;
		} else if (EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 20) {
			WelcomeManager.welcome(player, WelcomeStage.TUTORIAL20);
			return;
		} else if (EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 21) {
			WelcomeManager.welcome(player, WelcomeStage.TUTORIAL21);
			return;
		} else if (EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 22) {
			WelcomeManager.welcome(player, WelcomeStage.TUTORIAL22);
			return;
		} else if (EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 23) {
			WelcomeManager.welcome(player, WelcomeStage.TUTORIAL23);
			return;
		} else if (EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 24) {
			WelcomeManager.welcome(player, WelcomeStage.TUTORIAL24);
			return;
		} else if (EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 25) {
			WelcomeManager.welcome(player, WelcomeStage.TUTORIAL25);
			return;
		} else if (EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 26) {
			WelcomeManager.welcome(player, WelcomeStage.TUTORIAL27);
			return;
/*		} else if (player.tutorialStage == 27) {
			WelcomeManager.welcome(player, WelcomeStage.TUTORIAL27);
			return;*/
		} else if (EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 28) {
			WelcomeManager.welcome(player, WelcomeStage.TUTORIAL28);
			return;
		} else if (EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 29) {
			WelcomeManager.welcome(player, WelcomeStage.TUTORIAL29);
			return;
		} else if (EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 30) {
			WelcomeManager.welcome(player, WelcomeStage.TUTORIAL30);
			return;
		} else if (EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 31) {
			WelcomeManager.welcome(player, WelcomeStage.TUTORIAL31);
			return;
		}
		}
		
		// Handle custom actions..
		if (player.getDialogueContinueAction() != null) {
			player.getDialogueContinueAction().execute();
			player.setDialogueContinueAction(null);
			return;
		}
		
		// Make sure we are currently in a dialogue..
		if (player.getDialogue() == null) {
			player.getPacketSender().sendInterfaceRemoval();
			return;
		}
		Dialogue next = player.getDialogue().nextDialogue();

		if (player.getDialogue() instanceof QuestDialogue) {
			Quest quest = player.getQuest().getDialogue();
			if (quest != null) {
				next = QuestDialogueLoader.getDialogueForId(quest, player.getDialogue().nextDialogueId());
				if (next != null) {
					if (next.type() == DialogueType.QUEST_STAGE) {
						quest.getEndDialogue(player, player.getDialogue().npcId());
						return;
					}
				}
			}
		} else {
			// Fetch next dialogue..
			if (next == null) {
				next = dialogues.get(player.getDialogue().nextDialogueId());
			}
		}
		
		// Make sure the next dialogue is valid..
		if (next == null || next.id() < 0) {
			player.getPacketSender().sendInterfaceRemoval();
			return;
		}

		// Start the next dialogue.
		start(player, next);

	}

	/**
	 * Configures the dialogue's type and shows the dialogue interface and sets its
	 * child id's.
	 *
	 * @param player
	 *            The player to show dialogue for.
	 * @param dialogue
	 *            The dialogue to show.
	 */
	private static void showDialogue(Player player, Dialogue dialogue) {

		final PacketSender sender = player.getPacketSender();

		String title = dialogue.title();
		final String[] lines = dialogue.dialogue();
		final int lineIndex = lines.length > 0 ? lines.length-1 : 0;

		switch (dialogue.type()) {
		case NPC_STATEMENT:
			int startDialogueChildId = NPC_DIALOGUE_ID[lineIndex];
			int headChildId = startDialogueChildId - 2;

			final NpcDefinition npcDefinition = NpcDefinition.forId(dialogue.npcId());
			String name = npcDefinition != null ? npcDefinition.getName() : "";
			if(name == null)
				name = "";

			player.getPacketSender().sendNpcHeadOnInterface(dialogue.npcId(), headChildId);
			player.getPacketSender().sendInterfaceAnimation(headChildId, dialogue.animation().getAnimation());
			player.getPacketSender().sendString(startDialogueChildId - 1, name.replaceAll("_", " "));
			for (int i = 0; i < lines.length; i++) {
				player.getPacketSender().sendString(startDialogueChildId + i, lines[i]);
			}
			player.getPacketSender().sendChatboxInterface(startDialogueChildId - 3);
			break;
		case PLAYER_STATEMENT:
			startDialogueChildId = PLAYER_DIALOGUE_ID[lineIndex];
			headChildId = startDialogueChildId - 2;
			player.getPacketSender().sendPlayerHeadOnInterface(headChildId);
			player.getPacketSender().sendInterfaceAnimation(headChildId, dialogue.animation().getAnimation());
			player.getPacketSender().sendString(startDialogueChildId - 1, player.getUsername());
			for (int i = 0; i < lines.length; i++) {
				player.getPacketSender().sendString(startDialogueChildId + i, lines[i]);
			}
			player.getPacketSender().sendChatboxInterface(startDialogueChildId - 3);
			break;
		case ITEM_STATEMENT:
			startDialogueChildId = NPC_DIALOGUE_ID[lineIndex];
			headChildId = startDialogueChildId - 2;
			
			player.getPacketSender().sendInterfaceModel(headChildId, Integer.valueOf(dialogue.item()[0]), Integer.valueOf(dialogue.item()[1]));
			player.getPacketSender().sendString(startDialogueChildId - 1, dialogue.item()[2]);
			
			for (int i = 0; i < lines.length; i++) {
				player.getPacketSender().sendString(startDialogueChildId + i, lines[i]);
			}
			player.getPacketSender().sendChatboxInterface(startDialogueChildId - 3);
			break;
		case ITEM_STATEMENT_NO_HEADER:
			startDialogueChildId = NPC_DIALOGUE_ID[lineIndex];
			headChildId = startDialogueChildId - 2;

			player.getPacketSender().sendInterfaceModel(headChildId, Integer.valueOf(dialogue.item()[0]), Integer.valueOf(dialogue.item()[1]));
			player.getPacketSender().sendString(startDialogueChildId - 1, "");

			for (int i = 0; i < lines.length; i++) {
				player.getPacketSender().sendString(startDialogueChildId + i, lines[i]);
			}
			player.getPacketSender().sendChatboxInterface(startDialogueChildId - 3);
			break;
		case STATEMENT:
			int chatboxInterface = STATEMENT_DIALOGUE_ID[lineIndex];
			for (int i = 0; i < lines.length; i++) {
				player.getPacketSender().sendString((chatboxInterface + 1) + i, lines[i]);
			}
			player.getPacketSender().sendChatboxInterface(chatboxInterface);
			break;
		case TITLED_STATEMENT_NO_CONTINUE:
			sender.sendChatboxInterface(6179);
			sender.sendString(6180, title);
			for (int i = 0; i < 4; i++) {
				final String string = i >= lines.length ? "" : lines[i];
				sender.sendString(6181 + i, string);
			}
			break;
		case OPTION:
			int firstChildId = OPTION_DIALOGUE_ID[lineIndex];
			if(title.isEmpty())
				title = "Choose an option";

			sender.sendString(firstChildId - 1, title);
			for (int i = 0; i < lines.length; i++) {
				sender.sendString(firstChildId + i, lines[i]);
			}
			sender.sendChatboxInterface(firstChildId - 2);
			break;
		}
	}

	/**
	 * Sends a statement to the given player.
	 * 
	 * @param player
	 * @param statement
	 */
	public static void sendStatement(Player player, String statement) {
		String[] wrapped = WordUtils.wrap(statement, 62, System.lineSeparator(), false).split(System.lineSeparator());
		if (wrapped.length >= STATEMENT_DIALOGUE_ID.length) {
			throw new UnsupportedOperationException("Cannot wrap dialogue, statement too long!");
		}
		showDialogue(player, new Dialogue() {
			@Override
			public DialogueType type() {
				return DialogueType.STATEMENT;
			}

			@Override
			public String[] dialogue() {
				return wrapped;
			}

			@Override
			public DialogueExpression animation() {
				return null;
			}
		});
	}

	/**
	 * Gets an empty id for a dialogue.
	 *
	 * @return An empty index from the map or the map's size itself.
	 */
	public static int getDefaultId() {
		int id = dialogues.size();
		for (int i = 0; i < dialogues.size(); i++) {
			if (dialogues.get(i) == null) {
				id = i;
				break;
			}
		}
		return id;
	}

	/**
	 * Retrieves the dialogues map.
	 *
	 * @return dialogues.
	 */
	public static Map<Integer, Dialogue> getDialogues() {
		return dialogues;
	}

	/**
	 * This array contains the chatbox interfaces for statements.
	 */
	private static final int[] STATEMENT_DIALOGUE_ID = { 356, 359, 363, 368, 374, };

}
