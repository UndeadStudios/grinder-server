package com.grinder.game.content.minigame.warriorsguild.npcs;

import com.grinder.game.content.minigame.warriorsguild.WarriorGuildNpc;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.NPCActions;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.util.ItemID;
import kotlin.Pair;

import java.util.ArrayList;
import java.util.function.Consumer;

import static com.grinder.util.NpcID.AJJAT;

/**
 * @author L E G E N D
 */
public final class Ajjat extends WarriorGuildNpc {

    public Ajjat(int id, Position position) {
        super(id, position);
    }

    static {
        NPCActions.INSTANCE.onClick(new int[]{AJJAT}, action -> {
            var player = action.getPlayer();
            var attackLevel = player.getSkills().getLevel(Skill.ATTACK);
            DialogueBuilder optionsDialogue = new DialogueBuilder(DialogueType.OPTION);
            var claimTokensDialogue = makePlayerDialogue(player.getWarriorsGuild().getTokens() > 0 ?
                            makeNpcDialogue(makePlayerDialogue(null,
                                    "Thanks!"),
                                    "Of course! Here you go, you've earned " + player.getWarriorsGuild().getTokens() + " tokens!").setAction($ -> {
                                ItemContainerUtil.addOrDrop(player.getInventory(), player, new Item(ItemID.WARRIOR_GUILD_TOKEN, player.getWarriorsGuild().getTokens()));
                                player.getWarriorsGuild().setTokens(0);
                            }) : makeNpcDialogue(makePlayerDialogue(null,
                    "Okay, I'll go see what I can find"),
                    "I'm afraid you have not earned any tokens yet. Try",
                    "some of the activities around the guild to earn some."),
                    "May I claim my tokens please?");
            if (action.getType() == NPCActions.ClickAction.Type.FIRST_OPTION) {
                var options = new ArrayList<Pair<String, Consumer<Player>>>();
                options.add(new Pair<>("Can you tell me about skillcapes, please?", $ -> makeNpcDialogue(optionsDialogue, "Unfortunately, I no longer sell the Cape of Attack.").start(player)));
                options.add(new Pair<>("Black Knight? Why are you here?", $ -> makePlayerDialogue(makeNpcDialogue(makePlayerDialogue(optionsDialogue,
                        "Hmm... well if Harrallak trusts you, I guess I can."),
                        "Indeed I was, however their... methods... did not match",
                        "with my ideals.. so I left. Harrallak, recognising my",
                        "talent as a warrior, took me in and offered me a job",
                        "here."),
                        "Black Knight? Why are you here?")
                        .start(player)));
                options.add(new Pair<>("What's the Dummy Room all about?", $ -> makePlayerDialogue(makeNpcDialogue(
                        makePlayerDialogue(makeNpcDialogue(makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(optionsDialogue,
                                "Indeed, you may find that you need several weapons to",
                                "to be successful 100% of the time, but keep trying. The",
                                "weapons shop upstairs may help you there."),
                                "That sounds ingenious!"),
                                "In the room you will find a poster on the wall which",
                                "can help you recognise each different dummy."),
                                "There are two different ways. One indication is their",
                                "colour, the other is the pose and weapons they are",
                                "holding, for instance, the one holding daggers you will",
                                "need to hit with a piercing attack."),
                                "So how do I tell which one is which?"),
                        "Ahh yes, the dummies. Another ingenious invention of",
                        "the noble Dwarf Gamfred. They're mechanical you see",
                        "and pop up out of the floor. You have to hit them with",
                        "the correct attack mode before they disappear again."),
                        "What's the Dummy Room all about?")
                        .start(player)));
                options.add(new Pair<>("May I claim my tokens please?", $ -> claimTokensDialogue.start(player)));
                options.add(new Pair<>("Bye!", $ -> makePlayerDialogue(makeNpcDialogue(null, "Farewell warrior. Stay away from the dark side."), "Bye!").start(player)));
                optionsDialogue.addOptions(options);
                DialogueBuilder initialDialogue = makeNpcDialogue(attackLevel >= 99 ? makeNpcDialogue(makeNpcDialogue(optionsDialogue,
                        "When wearing this cape, Kamfreena will recognize your",
                        "ability and allow you to access her Cyclopes without the ",
                        "need for guild tokens."),
                        "Ah, but I can see you are already a master in the fine",
                        "art of attacking, perhaps you have come to me to",
                        "purchase a Cape of Attack, and thus join the elite few",
                        "who have mastered this exacting skill?") : optionsDialogue,
                        "Greetings, fellow warrior. I am Ajjat, former black",
                        "knight and now training officer here in the warrior", "guild.");

                initialDialogue.start(player);
            } else if (action.getType() == NPCActions.ClickAction.Type.SECOND_OPTION) {
                claimTokensDialogue.start(player);
            }
            return true;
        });
    }
}
