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

import static com.grinder.util.NpcID.KAMFREENA;

/**
 * @author L E G E N D
 */
public final class Kamfreena extends WarriorGuildNpc {

    public Kamfreena(int id, Position position) {
        super(id, position);
        setupOverheadChat(10,
                "When you aim for perfection, you discover it's a moving target.",
                "Patience and persistence can bring down the tallest tree.",
                "Be master of mind rather than mastered by mind.",
                "A reflection on a pool of water does not reveal its depth.",
                "Life isn't fair, that doesn't mean you can't win!",
                "He who speaks in anger makes his anger heard, but his words forgotten."
        );
    }

    static {
        NPCActions.INSTANCE.onClick(new int[]{KAMFREENA}, action -> {
            var player = action.getPlayer();
            var claimTokensDialogue = makePlayerDialogue(player.getWarriorsGuild().getTokens() > 0 ?
                            makeNpcDialogue(makePlayerDialogue(null,
                                    "Thanks!"),
                                    "Of course! Here you go, you've earned " + player.getWarriorsGuild().getTokens() + " tokens!").setAction($ -> {
                                ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(ItemID.WARRIOR_GUILD_TOKEN, player.getWarriorsGuild().getTokens()));
                                player.getWarriorsGuild().setTokens(0);
                            }) : makeNpcDialogue(
                    makePlayerDialogue(null,
                            "Okay, I'll go see what I can find."),
                    "I'm afraid you have not earned any tokens yet. Try",
                    "some of the activities around the guild to earn some."),
                    "May I claim my tokens please?");
            if (action.getType() == NPCActions.ClickAction.Type.FIRST_OPTION) {
                var mainDialogue = new DialogueBuilder(DialogueType.OPTION);
                var options = new ArrayList<Pair<String, Consumer<Player>>>();
                options.add(new Pair<>("Where are they from?", $s ->
                        makePlayerDialogue(
                                makeNpcDialogue(mainDialogue, "They're from the far east lands."),
                                "Where are they from?").start(player)));
                options.add(new Pair<>("How did they get here?", $ ->
                        makePlayerDialogue(
                                makeNpcDialogue(mainDialogue,
                                        "Ahh... our guild master, Harrallak, went on an expedition",
                                        "there. He brought them back with him."),
                                "How did they get here?").start(player)));
                options.add(new Pair<>("Why are they here?", $ ->
                        makePlayerDialogue(makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(
                                makePlayerDialogue(makeNpcDialogue(makeNpcDialogue(makePlayerDialogue(
                                        makeNpcDialogue(makePlayerDialogue(mainDialogue,
                                                "Right, I'd better go player some games then."),
                                                "Oh, by the way, you'll need to earn 100 tokens before",
                                                "I'll let you in!"),
                                        "Okay!"),
                                        "to start at bronze again. I'd advise keeping a spare in",
                                        "your bank."),
                                        "For every 10 tokens you collect around the guild, you",
                                        "can spend one minute in with my pets. As you get",
                                        "defenders you can show them to me to earn even",
                                        "better ones... but remember if you lose them you'll have"),
                                        "Wow!"),
                                "It's a blade you can defend with using your shield",
                                "hand, like I have."), "Err what are they?"),
                                "Defenders."),
                                "Oh? What would that be?"),
                                "For the warriors to train on of course! They also drop",
                                "a rather nice blade."),
                                "Why are they here?").start(player)));
                options.add(new Pair<>("Bye!", $ -> makePlayerDialogue(makeNpcDialogue(null,
                        "See you back here soon I hope!"),
                        "Bye!").start(player)));
                mainDialogue.addOptions(options);
                makeNpcDialogue(
                        makePlayerDialogue(
                                makeNpcDialogue(
                                        makePlayerDialogue(
                                                makeNpcDialogue(
                                                        makePlayerDialogue(mainDialogue,
                                                                "Oh, right, thanks."),
                                                        "Nope! Cyclopes is the plural of cyclopes. One cyclops,",
                                                        "many cyclopes."),
                                                "Shouldn't that be cyclopes?"),
                                        "Sorry... I don't get to see the rest of the guild much,",
                                        "stuck up here. The Cyclopes don't talk much you see."),
                                "That was a really bad pun."),
                        "Why hello there! I'm Kamfreena. Like the look of my",
                        "pets? I think they're eying you up.").start(player);
            } else if (action.getType() == NPCActions.ClickAction.Type.SECOND_OPTION) {
                claimTokensDialogue.start(player);
            }
            return true;
        });
    }
}
