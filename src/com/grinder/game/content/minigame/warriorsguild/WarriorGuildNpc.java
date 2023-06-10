package com.grinder.game.content.minigame.warriorsguild;

import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.util.Misc;
import kotlin.Pair;

import java.util.function.Consumer;

/**
 * @author L E G E N D
 */
public abstract class WarriorGuildNpc extends NPC {

    private String[] overheadChats;
    private int previousOverheadChat;
    private int overheadChatInterval;
    private int timer;

    public WarriorGuildNpc(int id, Position position) {
        super(id, position);
        overheadChatInterval = 5;
        previousOverheadChat = -1;
    }

    public void process() {
        if (overheadChats == null) {
            return;
        }
        if (++timer % overheadChatInterval == 0) {
            sayRandomChat();
        }
    }

    public void sayRandomChat() {
        int index;
        do {
            index = Misc.getRandomExclusive(overheadChats.length);
        } while (index == previousOverheadChat);
        say(overheadChats[index]);
        previousOverheadChat = index;
    }

    public void setupOverheadChat(int overheadChatInterval, String... overheadChats) {
        this.overheadChatInterval = overheadChatInterval;
        this.overheadChats = overheadChats;
    }

    public static String getGenderName(Player player) {
        return player.getAppearance().isMale() ? "Sir" : "Lady";
    }

    @SafeVarargs
    public static DialogueBuilder makeOptionsDialogue(Pair<String, Consumer<Player>>... dialogues) {
        var optionsDialogue = new DialogueBuilder(DialogueType.OPTION);
        for (int i = 0; i < dialogues.length; i++)
            optionsDialogue.option(i, dialogues[i].getFirst(), dialogues[i].getSecond());
        return optionsDialogue;
    }

    public static DialogueBuilder makePlayerDialogue(DialogueBuilder next, String... text) {
        return new DialogueBuilder(DialogueType.PLAYER_STATEMENT).setText(text).setNext(next);
    }

    public static DialogueBuilder makeNpcDialogue(DialogueBuilder next, String... text) {
        return new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(-1).setText(text).setNext(next);
    }

    public static DialogueBuilder makeItemDialogue(int itemId, DialogueBuilder next, String... text) {
        return new DialogueBuilder(DialogueType.ITEM_STATEMENT).setItem(itemId, 250).setText(text).setNext(next);
    }

    public static DialogueBuilder makeMessageDialogue(DialogueBuilder next, String... text) {
        return new DialogueBuilder(DialogueType.STATEMENT).setText(text).setNext(next);
    }
}
