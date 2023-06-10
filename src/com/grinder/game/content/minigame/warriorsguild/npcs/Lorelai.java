package com.grinder.game.content.minigame.warriorsguild.npcs;

import com.grinder.game.content.minigame.warriorsguild.WarriorGuildNpc;
import com.grinder.game.content.minigame.warriorsguild.drops.Defender;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.NPCActions;
import com.grinder.game.model.Position;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.util.ItemID;
import kotlin.Pair;

import java.util.ArrayList;
import java.util.function.Consumer;

import static com.grinder.util.NpcID.LORELAI;

/**
 * @author L E G E N D
 */
public final class Lorelai extends WarriorGuildNpc {

    public Lorelai(int id, Position position) {
        super(id, position);
    }

    static {
        NPCActions.INSTANCE.onClick(new int[]{LORELAI}, action -> {
            var player = action.getPlayer();
            var claimTokensDialogue = makePlayerDialogue(player.getWarriorsGuild().getTokens() > 0 ?
                            makeNpcDialogue(makePlayerDialogue(null,
                                    "Thanks!"),
                                    "Of course! Here you go, you've earned " + player.getWarriorsGuild().getTokens() + " tokens!").setAction($ -> {
                                ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(ItemID.WARRIOR_GUILD_TOKEN, player.getWarriorsGuild().getTokens()));
                                player.getWarriorsGuild().setTokens(0);
                            }) : makeNpcDialogue(makePlayerDialogue(null,
                    "Okay, I'll go see what I can find."),
                    "I'm afraid you have not earned any tokens yet. Try",
                    "some of the activities around the guild to earn some."),
                    "May I claim my tokens please?");
            if (action.getType() == NPCActions.ClickAction.Type.FIRST_OPTION) {
                var mainDialogue = new DialogueBuilder(DialogueType.OPTION);
                var options = new ArrayList<Pair<String, Consumer<Player>>>();
                options.add(new Pair<>("What's special about these Cyclopes?", $ ->
                        makePlayerDialogue(
                                makeNpcDialogue(mainDialogue,
                                        "They have a chance of dropping a new, more powerful",
                                        "defender. Kamfreena doesn't want just anyone getting",
                                        "heir hands on one so she moved them down here and",
                                        "tasked me with training them."),
                                "What's special about these Cyclopes?").start(player)));
                var fightDialogue = makePlayerDialogue((player.getWarriorsGuild().getLastDefender() == Defender.RUNE ? makeNpcDialogue(null, "Yes, you have already proved yourself to me, the door", "is unlocked.") :
                                makeNpcDialogue(null, "No you are not allowed in here."/*TODO:get the exact dialogue*/)),
                        "I am a might adventurer! Can I fight  them?");
                options.add(new Pair<>("How exactly are you training them?", $ ->
                        makePlayerDialogue(
                                makeNpcDialogue(
                                        makePlayerDialogue(
                                                makeNpcDialogue(fightDialogue,
                                                        "Of course not, that would just be cruel! Kamfreena and",
                                                        "I only allow the most worthy of adventurers to fight",
                                                        "these cyclopes."),
                                                "So, you just let them fight each other and hope they get",
                                                "stronger?"),
                                        "With a punishing routine of combat, fighting, and the", "odd death!")
                                ,
                                "How exactly are you training them?").start(player)));
                options.add(new Pair<>("Can I fight them?", $ -> fightDialogue.start(player)));
                options.add(new Pair<>("Bye!", $ -> makePlayerDialogue(makeNpcDialogue(null, "See you around."), "Bye!").start(player)));
                makeNpcDialogue(
                        makePlayerDialogue(makeNpcDialogue(mainDialogue,
                                "I'm training this group of special Cyclopes to be",
                                "stronger and more powerful for Kamfreena."),
                                "What are you doing down here?"),
                        "Hello! I'm Lorelai. Welcome to the Warrior's Guild",
                        "basement!").
                        start(player);
                mainDialogue.addOptions(options);
            } else if (action.getType() == NPCActions.ClickAction.Type.SECOND_OPTION) {
                claimTokensDialogue.start(player);
            }
            return true;
        });
    }
}
