package com.grinder.game.content.minigame.warriorsguild.npcs;

import com.grinder.game.content.minigame.warriorsguild.WarriorGuildNpc;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.NPCActions;
import com.grinder.game.model.Position;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import kotlin.Pair;

import java.util.ArrayList;
import java.util.function.Consumer;

import static com.grinder.util.NpcID.HARRALLAK_MENAROUS;

/**
 * @author L E G E N D
 */
public final class Harrallak extends WarriorGuildNpc {

    public Harrallak(int id, Position position) {
        super(id, position);
    }

    static {
        NPCActions.INSTANCE.onClick(new int[]{HARRALLAK_MENAROUS}, action -> {
            var player = action.getPlayer();
            if (action.getType() == NPCActions.ClickAction.Type.FIRST_OPTION) {
                var activitiesOptions = new ArrayList<Pair<String, Consumer<Player>>>();
                var activitiesDialogue = new DialogueBuilder(DialogueType.OPTION);
                activitiesOptions.add(strOption(player));
                activitiesOptions.add(attOption(player));
                activitiesOptions.add(defOption(player));
                activitiesOptions.add(comOption(player));
                activitiesOptions.add(tokensOption(player));
                var mainOptions = new ArrayList<Pair<String, Consumer<Player>>>();
                mainOptions.add(new Pair<>("Quite a place you've got here.", $ -> makePlayerDialogue(makeNpcDialogue(activitiesDialogue, "Indeed we do. Would you like to know more about it?"), "Quite a place you've got here.").start(player)));
                mainOptions.add(new Pair<>("You any good with a sword?", $ -> makePlayerDialogue(makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(makeNpcDialogue(activitiesDialogue,
                        "have. You might even collect enough tokens to be",
                        "allowed in to kill the strange beasts from the east!"),
                        "My dear " + getGenderName(player) + ", I couldn't possibly duel you, I might",
                        "hurt you and then what would happen to my",
                        "reputation! Besides, I have this wonderful guild to run.",
                        "Why don't you take a look at the various activities we"),
                        "Try me then!"),
                        "Why I could best any person alive in a rapier duel!"),
                        "Not really... no."),
                        "Am I any good with...a sword... Have you any clue", "who I am?"),
                        "You any good with a sword?").start(player)));
                mainOptions.add(byeOption(player));

                var mainDialogue = new DialogueBuilder(DialogueType.OPTION);
                mainDialogue.addOptions(mainOptions);

                activitiesDialogue.addOptions(activitiesOptions);
                makeNpcDialogue(mainDialogue, "Welcome to my humble guild " + player.getUsername() + ".").start(player);
            }
            return true;
        });
    }

    private static Pair<String, Consumer<Player>> comOption(Player player) {
        return new Pair<>("Tell me about the combat training area.", $ ->
                makePlayerDialogue(makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(
                        makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(makePlayerDialogue(makeOptionsDialogue(strOption(player), attOption(player), defOption(player), tokensOption(player), byeOption(player)),
                                "Ok, thanks for the warning."),
                                "Indeed we have had a few fatalities from warriors over",
                                "stretching themselves and not knowing their limits. Start",
                                "small and work up is my motto! That and go see Lidio",
                                "for some food if you need it."),
                                "Fighting my own armour, that sounds... weird. I could",
                                "be killed by it..."),
                                "the armour, the harder it is to 'kill'."),
                        "So far that's not happened. You need to speak to",
                        "Shanomi about the specifics of the process, but as I",
                        "understand it, putting a suit of armour in one of these",
                        "devices will make it come to life some how. The better"),
                        "That sounds dangerous. What if I'm wearing it at the",
                        "time?"),
                        "Ahh yes, our resident magician from foreign lands",
                        "created a most amazing gadget which can turn your",
                        "own armour against you! It's really quite intriguing."),
                        "Tell me about the combat training area.").start(player));
    }

    private static Pair<String, Consumer<Player>> defOption(Player player) {
        return new Pair<>("Tell me about the defence training area.", $ ->
                makePlayerDialogue(makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(
                        makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(makePlayerDialogue(
                                makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(
                                        makePlayerDialogue(makeNpcDialogue(makeOptionsDialogue(strOption(player), attOption(player), comOption(player), tokensOption(player), byeOption(player)),
                                                "No need to be afraid, it's all under very controlled",
                                                "conditions! You can find it just up the stairs behind the", "bank."),
                                                "ANVILS!?"),
                                        "... and even anvils."), "Ummm...")
                                        , "....spinning slashing blades...."),
                                "Err...."),
                                "....spiked iron balls....."),
                                "Mmmm?"),
                                "He's an inventor you see and has built a marvellous",
                                "contraption that can throw all sorts of things at you",
                                "including magic missiles..."),
                        "Oh my no! I think Gamfred would object to that most", "strongly."),
                        "You're going to throw dwarves at me?"),
                        "To polish your defensive skills to the very highest level",
                        "we've employed a most inventive dwarf and a catapult."),
                        "Tell me about the defence training area.").start(player));
    }

    private static Pair<String, Consumer<Player>> attOption(Player player) {
        return new Pair<>("Tell me about the attack training area.", $ ->
                makePlayerDialogue(makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(makeNpcDialogue(makePlayerDialogue(
                        makeNpcDialogue(makeOptionsDialogue(strOption(player), defOption(player), comOption(player), tokensOption(player), byeOption(player)),
                                "You can find it just down the corridor and on your", "right."),
                        "Oh, ok, I'll have to try it out!"),
                        "to do there."),
                        "Oh no my dear " + getGenderName(player) + ", I did not mean you at all! The",
                        "raining area has mechanical dummies which pop up out",
                        "of holes in the floor. The noble dwarf Gamfred invented",
                        "the mechanism and Ajjat can explain more about what"),
                        "I'm no dummy, I just want to know what is there!"),
                        "Ahhh, dummies."),
                        "Tell me about the attack training area.").start(player));
    }

    private static Pair<String, Consumer<Player>> strOption(Player player) {
        return new Pair<>("Tell me about the strength training area.", $ -> makePlayerDialogue(
                makeNpcDialogue(makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(makeOptionsDialogue(attOption(player), defOption(player), comOption(player), tokensOption(player), byeOption(player)),
                        "Well you see my dear [boy/lady], the games there really do",
                        "sap your energy quite a bit. You can find it just up",
                        "the stairs behind the bank."),
                        " Oh? Why?"),
                        "may find yourself panting for breath though."),
                        "Ahh the mighty warrior Sloane guards the strength",
                        "training area. This intriguing little area consists of two",
                        "shotput lanes for different weights of shot. It's fairly",
                        "simple, the Referee or Sloane can explain more. You"),
                "Tell me about the strength training area.").start(player));
    }

    private static Pair<String, Consumer<Player>> tokensOption(Player player) {
        return new Pair<>("Tell me about tokens.", $ ->
                makePlayerDialogue(makeNpcDialogue(makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(makePlayerDialogue(
                        makeNpcDialogue(makeNpcDialogue(makePlayerDialogue(makeOptionsDialogue(strOption(player), attOption(player), defOption(player), comOption(player), tokensOption(player)), "Sounds easy enough."),
                                "myself."),
                                "You can earn them simply by using the training",
                                "exercises around the guild, the staff will then enter this",
                                "into a ledger as you play. You can claim them by",
                                "simply asking any of the staff at the training areas, or"),
                        "Ok ok... so how do I earn and claim these tokens?"),
                        "If I told you it would spoil the surprise!"),
                        "So what's up there?"),
                        "amount of tokens you collect from the various activities",
                        "around the guild will dictate how long Kamfreena will",
                        "allow you in the enclosure on the very top floor. More",
                        "tokens equals more time."),
                        "Ahh, yes, our token system is designed to allow you an",
                        "appropriate amount of time with my discovery in the",
                        "very top floor of the guild. I won't spoil the surprise as",
                        "to what that is. Go up and see for yourself. Now, the"),
                        "Tell me about tokens.").start(player));
    }

    private static Pair<String, Consumer<Player>> byeOption(Player player) {
        return new Pair<>("Bye!", $ -> makePlayerDialogue(makeNpcDialogue(null,
                "Farewell brave warrior, I do hope you enjoy my guild."),
                "Bye!").start(player));
    }
}
