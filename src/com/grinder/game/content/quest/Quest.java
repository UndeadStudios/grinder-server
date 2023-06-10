package com.grinder.game.content.quest;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.model.interfaces.dialogue.DialogueOptions;
import com.grinder.net.packet.interaction.PacketInteraction;

import java.util.ArrayList;

/**
 * 
 * @author Dexter Morgan <https://www.rune-server.ee/members/dexter+morgan/>
 *
 */
public abstract class Quest extends PacketInteraction {

	public ArrayList<QuestDialogue> dialogue = new ArrayList<QuestDialogue>();

	public String name;

	public String dialogueName;

	public int questPoints;

	public boolean members;

	public Quest quest;

	public int finalStage;

	public QuestType type;

	public Quest(String name, boolean members, int questPointReward, int finalStage) {
		this.name = name;
		this.members = members;
		this.questPoints = questPointReward;
		this.dialogueName = name.toLowerCase().replaceAll("'", "").replaceAll(" ", "_");
		this.quest = this;
		this.finalStage = finalStage;
		this.type = members ? QuestType.MEMBER : QuestType.FREE;
	}

	public Quest(String name, QuestType type, int questPointReward, int finalStage) {
		this(name, false, questPointReward, finalStage);
		this.type = type;
	}

	public int getStage(Player player) {
		return player.getQuest().tracker.getProgress(name);
	}
	
	public boolean isCompleted(Player player) {
		return player.getQuest().tracker.getProgress(name) >= finalStage;
	}

	public abstract String[][] getDescription(Player player);

	public abstract int[] getQuestNpcs();

	public abstract boolean hasRequirements(Player player);

	public DialogueOptions getDialogueOptions(Player player) {
		return null;
	}

	public boolean hasStartDialogue(Player player, int npcId) {
		return false;
	}

	public void getEndDialogue(Player player, int npcId) {

	}
	
	public abstract Position getTeleport();

	public void increaseStage(Player p) {
		QuestManager.increaseStage(p, this);
	}

	public void sendDialogue(Player p, int id) {
		QuestDialogueLoader.sendDialogue(p, this, id);
	}

	public String hasCompletedQuest(Player p, String name) {
		boolean completedQuest = QuestManager.hasCompletedQuest(p, name);
		return (completedQuest ? "<str=0>" : "") + name;
	}
}