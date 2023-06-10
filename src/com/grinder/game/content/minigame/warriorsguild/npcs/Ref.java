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

import static com.grinder.util.NpcID.REF;
import static com.grinder.util.NpcID.REF_6074;

/**
 * @author L E G E N D
 */
public final class Ref extends WarriorGuildNpc {

    public Ref(int id, Position position) {
        super(id, position);
    }

    static {
        NPCActions.INSTANCE.onClick(new int[]{REF, REF_6074}, action -> {
            var player = action.getPlayer();
            var gender = getGenderName(player);
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
            DialogueBuilder mainDialogue = new DialogueBuilder(DialogueType.OPTION);
            if (action.getType() == NPCActions.ClickAction.Type.FIRST_OPTION) {
                var options = new ArrayList<Pair<String, Consumer<Player>>>();

                options.add(new Pair<>("Tell me about the Shot Put area.", $ -> makePlayerDialogue(makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(
                        makePlayerDialogue(makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(makePlayerDialogue(makeMessageDialogue(makeNpcDialogue(
                                makePlayerDialogue(makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(null,
                                        "You are welcome " + gender + "."),
                                        "Thanks for the help!"),
                                        "Crudely put " + gender + ", but yes. Some are more difficult than",
                                        "others. Experiment and see which you prefer."),
                                        "Oh! You mean the spinny round thing or the chuck it", "straight?"),
                                gender + ", the style in which you throw the shot, not the style", "of the iron."),
                                "The Referee sighs, rolls his eyes and continues...."),
                                "Iron has style??"),
                                "The style of the shot " + gender + "."),
                                "What's that then?"),
                                "Your strength " + gender + ". The stronger you are the further",
                                "you can throw the shot, but there are other factors of",
                                "course like your technique."),
                                "The... what?"),
                        "Yes " + gender + ". Shot. The iron spheres that are propelled by",
                        "the chemical energy stored in your body."),
                        "Shot?"),
                        "Of course " + gender + ". There are two different weights of shot..."),
                        "Tell me about the Shot Put area.").
                        start(player)));
                options.add(new Pair<>("May I claim my tokens please?", $ -> claimTokensDialogue.start(player)));
                options.add(new Pair<>("Do you have any tips for me?", $ -> makePlayerDialogue(makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(
                        makePlayerDialogue(makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(null, "You are welcome " + gender + "."),
                                "Thanks!"),
                                "Yes " + gender + "."),
                                "You mean if I grind something up and put dust on my",
                                "hands I'll check the ball further?"),
                        gender + ", may find that a fine powder applied to the hands",
                        "may give one an advantage when putting the shot."),
                        "Yes, like how can I do better than everyone else."),
                        "Tips " + gender + "?"),
                        "Do you have any tips for me?").
                        start(player)));
                options.add(new Pair<>("Bye!", $ -> makePlayerDialogue(makeNpcDialogue(null, "Good luck " + gender + "."), "Bye!")
                        .start(player)));
                mainDialogue.addOptions(options);
                makeNpcDialogue(mainDialogue, "Greetings " + gender + ".").start(player);
            } else if (action.getType() == NPCActions.ClickAction.Type.SECOND_OPTION) {
                claimTokensDialogue.start(player);
            }
            return true;
        });
    }
}
