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

import static com.grinder.util.NpcID.SHANOMI;

/**
 * @author L E G E N D
 */
public final class Shanomi extends WarriorGuildNpc {

    public Shanomi(int id, Position position) {
        super(id, position);
        setupOverheadChat(10,
                "Those things which cannot be seen, perceive them.",
                "Do nothing which is of no use.",
                "Think not dishonestly.",
                "The Way in training is.",
                "Gain and loss between you must distinguish.",
                "Trifles pay attention even to.",
                "Way of the warrior this is.",
                "Acquainted with every art become.",
                "Ways of all professions know you.",
                "Judgment and understanding for everything develop you must."
        );
    }

    static {
        NPCActions.INSTANCE.onClick(new int[]{SHANOMI}, action -> {
            var player = action.getPlayer();
            var mainDialogue = new DialogueBuilder(DialogueType.OPTION);
            var claimTokensDialogue = makePlayerDialogue(player.getWarriorsGuild().getTokens() > 0 ?
                            makeNpcDialogue(makePlayerDialogue(null,
                                    "Thanks!"),
                                    "Yes yes! Earned you have " + player.getWarriorsGuild().getTokens() + " tokens!").setAction($ -> {
                                ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(ItemID.WARRIOR_GUILD_TOKEN, player.getWarriorsGuild().getTokens()));
                                player.getWarriorsGuild().setTokens(0);
                            }) : makeNpcDialogue(
                    makePlayerDialogue(null,
                            "Okay, I'll go see what I can find to do around here to", "earn some tokens"),
                    "No tokens earned have you. In training activities", "participate you must."),
                    "May I claim my tokens please?");

            if (action.getType() == NPCActions.ClickAction.Type.FIRST_OPTION) {
                var options = new ArrayList<Pair<String, Consumer<Player>>>();
                if (player.getWarriorsGuild().getLostArmour() != null) {
                    options.add(new Pair<>("My armour disappeared!", $ -> makePlayerDialogue(
                            makeNpcDialogue(
                                    makePlayerDialogue(
                                            makeNpcDialogue(
                                                    makePlayerDialogue(null, "Thanks."),
                                                    "It is as you say.").setAction($$ -> {
                                                var items = player.getWarriorsGuild().getLostArmour().getArmour();
                                                for (var item : items) {
                                                    ItemContainerUtil.addOrDrop(player.getInventory(), player, item);
                                                }
                                                player.getWarriorsGuild().setLostArmour(null);
                                            }),
                                            "Excellent may I have it so I can try the machine",
                                            "again?"),
                                    "Correct you are. Not killed it was. Collected your",
                                    "armour I did."),
                            "My armour disappeared!").start(player)));
                }

                options.add(new Pair<>("What do I do here?", $ -> makePlayerDialogue(
                        makeNpcDialogue(
                                makeNpcDialogue(
                                        makePlayerDialogue(
                                                makeNpcDialogue(
                                                        makePlayerDialogue(
                                                                makeNpcDialogue(
                                                                        makePlayerDialogue(
                                                                                makeNpcDialogue(
                                                                                        makePlayerDialogue(makeNpcDialogue(mainDialogue, "It is as you say."),
                                                                                                "So, occasionally I might lose a bit because it's being",
                                                                                                "bashed about and I'll obviously lose it if I die... that it?"),
                                                                                        "Lose armour you will if damaged too much it becomes.",
                                                                                        "Rare this is, but still possible. If kill you armour",
                                                                                        "does, also lose armour you will."),
                                                                                "So will I lose my armour?"),
                                                                        "It is as you say."),
                                                                "You're not from around here are you...?")
                                                        ,
                                                        "It is as you are saying. For this earn tokens you",
                                                        "will. Also gain experience in combat you will. Trained",
                                                        "long and hard here have I."
                                                ),
                                                "So I use a full set of plate armour on the centre plate",
                                                "of the machines and it will animate it? Then I have to",
                                                "kill my own armour... how bizarre!"
                                        ),
                                        "alive! Kill it you must, yes."),
                                "A spare suit of plate armour need you will. Full Helm",
                                "plate leggings and platebody yes? Placing it in the",
                                "centre of the magical machines you will be doing. KA-",
                                "POOF! The armour, it attacks most furiously as if")
                        , "What do I do here?").start(player)));
                options.add(new Pair<>("Where do the machines come from?", $ ->
                        makePlayerDialogue(
                                makeNpcDialogue(
                                        makePlayerDialogue(
                                                makeNpcDialogue(
                                                        makePlayerDialogue(makeNpcDialogue(mainDialogue, "It is as you say."), "I see, so you made the magical machines and Harrallak",
                                                                "Saw how they could be used in the guild to train",
                                                                "warrior's combat... interesting. Harrallak certainly is an",
                                                                "intelligent guy."),
                                                        "A skilled warrior also am I. Harrallak mistakes does not",
                                                        "make. Potential in my invention he sees and",
                                                        "opportunity grasps."),
                                                "Magic, in the Warrior's Guild?"),
                                        "Make them I did, with magics."),
                                "Where do the machines come from?").start(player)));
                options.add(new Pair<>("May I claim my tokens please?", $ -> claimTokensDialogue.start(player)));
                options.add(new Pair<>("Bye!", $ -> Shanomi.makePlayerDialogue(makeNpcDialogue(null, "Health be with you travelling."), "Bye!").start(player)));
                mainDialogue.addOptions(options);
                makeNpcDialogue(mainDialogue, "Greetings " + player.getUsername() + ". Welcome you are in the", "test of combat.").start(player);
            } else if (action.getType() == NPCActions.ClickAction.Type.SECOND_OPTION) {
                claimTokensDialogue.start(player);
            }
            return true;
        });
    }
}
