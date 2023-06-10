package com.grinder.game.content.minigame.warriorsguild.npcs;

import com.grinder.game.content.minigame.warriorsguild.WarriorGuildNpc;
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

import static com.grinder.util.NpcID.GAMFRED;

/**
 * @author L E G E N D
 */
public final class Gamfred extends WarriorGuildNpc {

    private static final int DEFENSIVE_SHIELD = 8856;

    public Gamfred(int id, Position position) {
        super(id, position);
    }

    static {
        NPCActions.INSTANCE.onClick(new int[]{GAMFRED}, action -> {
            var player = action.getPlayer();
            DialogueBuilder mainDialogue = new DialogueBuilder(DialogueType.OPTION);
            var claimTokensDialogue = makePlayerDialogue(player.getWarriorsGuild().getTokens() > 0 ?
                            makeNpcDialogue(makePlayerDialogue(null,
                                    "Thanks!"),
                                    "Of course! Here you go, you've earned " + player.getWarriorsGuild().getTokens() + " tokens!").setAction($ -> {
                                ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(ItemID.WARRIOR_GUILD_TOKEN, player.getWarriorsGuild().getTokens()));
                                player.getWarriorsGuild().setTokens(0);
                            })
                            : makeNpcDialogue(makePlayerDialogue(null,
                    "Okay, I'll go see what I can find."),
                    "I'm afraid you have not earned any tokens yet. Try",
                    "some of the activities around the guild to earn some."),
                    "May I claim my tokens please?");
            var claimShieldDialogue = makePlayerDialogue(player.getInventory().contains(DEFENSIVE_SHIELD) ? makeNpcDialogue(null,
                    "Silly muffin, you have one already!") : makeNpcDialogue(makeItemDialogue(DEFENSIVE_SHIELD, null,
                    "The dwarf hands you a large shield.").setAction($ -> ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(DEFENSIVE_SHIELD))),
                    "Of course!"),
                    "May I have shield please?");
            if (action.getType() == NPCActions.ClickAction.Type.FIRST_OPTION) {
                var options = new ArrayList<Pair<String, Consumer<Player>>>();
                options.add(new Pair<>("That's not a catapult, it's a large crossbow.", $ -> makePlayerDialogue(makeNpcDialogue(null,
                        "WHAT!? I'll have you know that is the finest piece of",
                        "dwarven engineering for miles around! How DARE",
                        "you insult my work!"),
                        "That's not a catapult, it's a large crossbow.").start(player)));
                options.add(new Pair<>("Yes, beautiful piece of engineering.", $ -> makePlayerDialogue(makeNpcDialogue(
                        makeOptionsDialogue(new Pair<>("Yes", $$ -> makePlayerDialogue(makeNpcDialogue(makeOptionsDialogue(
                                new Pair<>("It was ok I guess.", $$$ -> makePlayerDialogue(makeNpcDialogue(null, "Well I guess not everyone will like it."), "It was ok I guess.").start(player)),
                                new Pair<>("It was fun!", $$$ -> makePlayerDialogue(makeNpcDialogue(null, "Glad ta hear it. Try it again sometime. We have more", "tests to run."), "It was fun!").start(player)),
                                new Pair<>("I didn't like it.", $$$ -> makePlayerDialogue(makeNpcDialogue(null, "Well I guess not everyone will like it. But give", "it another chance before you go."), "I didn't like it.").start(player)),
                                new Pair<>("May I have a shield please?", $$$ -> claimShieldDialogue.start(player))
                                ),
                                "What did you think?"),
                                "Yes.").start(player)),
                                new Pair<>("No, how do I do that?", $$ ->
                                        makePlayerDialogue(makeNpcDialogue(makeOptionsDialogue(
                                                new Pair<>("May I have a shield please?", $$$ -> claimShieldDialogue.start(player)),
                                                new Pair<>("Sounds boring.", $$$$ -> makePlayerDialogue(makeNpcDialogue(null,
                                                        "Your loss..."),
                                                        "Sounds boring.").start(player))),
                                                "Well ye take the big defence shield in both hands and",
                                                "watch the catapult. My assistant will fire different things",
                                                "at you and you need to defend against them."),
                                                "No, how do I do that?").start(player))),
                        "Nice to meet someone who appreciates fine work, have",
                        "you tried it out yet?"),
                        "Yes, beautiful piece of engineering.").start(player)));
                options.add(new Pair<>("No, where is it?", $ -> makePlayerDialogue(makeNpcDialogue(null,
                        "Are ye blind lad? Tis over there in the next room with",
                        "me assistant working it!"),
                        "No, where is it?").start(player)));
                options.add(new Pair<>("May I claim my tokens please?", $ -> claimTokensDialogue.start(player)));
                options.add(new Pair<>("Bye!", $ -> makePlayerDialogue(makeNpcDialogue(null,
                        "Come back soon! My catapult needs more test subjects."),
                        "Bye!").start(player)));
                mainDialogue.addOptions(options);
                makeNpcDialogue(mainDialogue,
                        "Ello there. I'm Gamfred the engineer in this here guild.", "Have you seen my catapult?")
                        .start(player);
            } else if (action.getType() == NPCActions.ClickAction.Type.SECOND_OPTION) {
                claimTokensDialogue.start(player);
            } else if (action.getType() == NPCActions.ClickAction.Type.THIRD_OPTION) {
                claimShieldDialogue.start(player);
            }
            return true;
        });
    }
}
