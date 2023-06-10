package com.grinder.game.content.minigame.blastfurnace.npcs;

import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.NPCActions;
import com.grinder.game.model.Position;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.util.Misc;
import kotlin.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

import static com.grinder.util.NpcID.*;

/**
 * @author L E G E N D
 * @date 2/22/2021
 * @time 4:38 AM
 * @discord L E G E N D#4380
 */
public class BlastFurnaceNpc extends NPC {

    private String[] overheadChats;
    private int previousOverheadChat;
    private int overheadChatInterval;
    private int timer;

    static {
        NPCActions.INSTANCE.onClick(new int[]{PUMPY, NUMPTY, DUMPY, DUMPY_7387, STUMPY, THUMPY}, action -> {
            var player = action.getPlayer();
            new DialogueBuilder(DialogueType.NPC_STATEMENT)
                    .setText("What is it human? I'm working.")
                    .setNext(constructAll(player)).start(player);
            return true;
        });
    }

    public BlastFurnaceNpc(int id, Position position) {
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

    private static DialogueBuilder constructAllBut(Player player, int exclusion) {
        var options = new ArrayList<Pair<String, Consumer<Player>>>();
        options.add(getFirstOption(player));
        options.add(getSecondOption(player));
        options.add(getThirdOption(player));
        options.remove(exclusion - 1);
        return new DialogueBuilder(DialogueType.OPTION).addOptions(options);
    }

    private static DialogueBuilder constructAll(Player player) {
        return new DialogueBuilder(DialogueType.OPTION).addOptions(Arrays.asList(getFirstOption(player), getSecondOption(player),
                getThirdOption(player)));
    }

    private static Pair<String, Consumer<Player>> getFirstOption(Player player) {
        return new Pair<>("How do I use the furnace?", $ ->
                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                        .setText("How do I use the furnace?")
                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                .setText("Put your ore on the conveyor belt to get it smelted.",
                                        "we'll stoke the boiler and operate the rest of the",
                                        "machinery for you.")
                                .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                        .setText("You'll have to help repair the furnace whenever a pipe blows",
                                                "though. Just hit it with a hammer - that usually",
                                                "works.")
                                        .setText("You'll have to put money in the coffer to pay for our",
                                                "services. we charge 2,520,000 coins per hour; money is",
                                                "deducted as long as you're in this room with us.")
                                        .setNext(constructAllBut(player, 1)

                                        ))).start(player));
    }

    private static Pair<String, Consumer<Player>> getSecondOption(Player player) {
        return new Pair<>("Gosh you're remarkably big dwarf.", $ ->
                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                        .setText("Gosh you're remarkably big dwarf.")
                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                .setText("My brothers and I have a rare genetic condition",
                                        "affecting our growth. Rather than treating us as freaks,",
                                        "you might like to appreciate how useful it is that we can",
                                        "operate the machinery.")
                                .setNext(new DialogueBuilder(DialogueType.OPTION)
                                        .firstOption("Sorry.", $$ ->
                                                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                        .setText("Sorry.")
                                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                .setText("That's okay, human. You're not the first to comment",
                                                                        "on it. Now, did you need anything? I've got work to do",
                                                                        "here.")
                                                                .setNext(constructAllBut(player, 2))
                                                        ).start(player))
                                        .secondOption("Are you sure you're really a dwarf?", $$ ->
                                                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                        .setText("Are you sure you're really a dwarf?")
                                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                .setText("... look, do you want help with something, or did you",
                                                                        "just come here to be rude? I've got work to do.")
                                                                .setNext(constructAllBut(player, 2))
                                                        ).start(player))
                                )).start(player));
    }

    private static Pair<String, Consumer<Player>> getThirdOption(Player player) {
        return new Pair<>("I'll let you get on with it.", $ ->
                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                        .setText("I'll let you get on with it.")
                        .start(player));
    }
}
