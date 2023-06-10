package com.grinder.game.content.minigame.blastfurnace.npcs;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.NPCActions;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;
import kotlin.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

import static com.grinder.util.NpcID.BLAST_FURNACE_FOREMAN;

/**
 * @author L E G E N D
 * @date 2/22/2021
 * @time 4:36 AM
 * @discord L E G E N D#4380
 */
public final class BlastFurnaceForeman extends BlastFurnaceNpc {

    public BlastFurnaceForeman(int id, Position position) {
        super(id, position);

        setupOverheadChat(20,
                "Work Faster!",
                "That'll keep it going",
                "Keep the temperature controlled");
    }

    static {
        NPCActions.INSTANCE.onClick(new int[]{BLAST_FURNACE_FOREMAN}, action -> {
            var player = action.getPlayer();
            if (action.getType() == NPCActions.ClickAction.Type.FIRST_OPTION) {
                new DialogueBuilder(DialogueType.NPC_STATEMENT)
                        .setText("You!, Get to work!")
                        .setNext(new DialogueBuilder(DialogueType.OPTION)
                                .firstOption("What?", $ -> new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                        .setText("What?")
                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                .setText("You are here to help the blast furnace, aren't",
                                                        "you?")
                                                .setNext(constructAll(player))).start(player))
                                .secondOption("Okay.", $ -> new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                        .setText("Okay.")
                                        .start(player))
                        ).start(player);
            } else if (action.getType() == NPCActions.ClickAction.Type.SECOND_OPTION) {
                if (player.getSkillManager().getCurrentLevel(Skill.SMITHING) >= 60) {
                    new DialogueBuilder(DialogueType.NPC_STATEMENT)
                            .setText("You are an experienced smith, you may use the", "furnace free of charge!")
                            .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                    .setText("Remember, you only need half as much coal as with a", " regular furnace."))
                            .start(player);

                } else {
                    new DialogueBuilder(DialogueType.NPC_STATEMENT)
                            .setText("Get to work!")
                            .start(player);
                }
            }
            return true;
        });
    }

    private static DialogueBuilder constructAllBut(Player player, int exclusion) {
        var options = new ArrayList<Pair<String, Consumer<Player>>>();
        options.add(getFirstOption(player));
        options.add(getSecondOption(player));
        options.add(getThirdOption(player));
        options.add(getFourthOption(player));
        options.remove(exclusion - 1);
        return new DialogueBuilder(DialogueType.OPTION).addOptions(options);
    }

    private static DialogueBuilder constructAll(Player player) {
        return new DialogueBuilder(DialogueType.OPTION).addOptions(Arrays.asList(getFirstOption(player), getSecondOption(player),
                getThirdOption(player), getFourthOption(player)));
    }

    private static Pair<String, Consumer<Player>> getFirstOption(Player player) {
        return new Pair<>("What's the blast furnace?", $ ->
                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                .setText("What's the blast furnace?")
                .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                        .setText("The blast furnace is the pinnacle of dwarven metal-",
                                "processing! Ore goes in the top and comes out as metal",
                                "bars almost at once! And it's so efficient it only takes",
                                "half as much coal to purify it as a regular furnace.")
                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                .setText("But we've got a bit of labour shortage at the moment.",
                                        "I've got some workers from the city to operate the",
                                        "pump and pedal, and to stoke the boiler, but when the",
                                        "machine breaks it'll need a good whack with a hammer.")
                                .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                        .setText("to get it working again.")
                                        .setNext(constructAllBut(player, 1))))
                ).start(player));
    }

    private static Pair<String, Consumer<Player>> getSecondOption(Player player) {
        return new Pair<>("How can I help work the blast furnace?", $ ->
                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                        .setText("How can I help work the blast furnace?")
                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                .setText("The blast furnace will only work if there is a group of",
                                        "people keeping it going. Let me explain...")
                                .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                        .setText("Firstly, the stove needs to be kept filled with coal. If this",
                                                "runs ou the furnace will not work.")
                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                .setText("Secondly, someone needs to operate the pump that",
                                                        "keeps the hot air blast circulating through the furnace.")
                                                .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                        .setText("Thirdly, someone needs to keep an eye on the",
                                                                "temperature gauge. They should tell the pumper to",
                                                                "start or stop so that the temperature stays in the right",
                                                                "range.")
                                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                .setText("Lastly, someone needs to be on the pedal to power the",
                                                                        "conveyor belt that puts ore into the furnace.")
                                                                .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                        .setText("Someone will also need to be on standby with a hammer",
                                                                                "in case the machine breaks!")
                                                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                                .setText("While that's going, anyone else can put their ore on",
                                                                                        "the conveyor belt to have it smelted.")
                                                                                .setNext(constructAllBut(player, 2))
                                                                        ))))))).start(player));
    }

    private static Pair<String, Consumer<Player>> getThirdOption(Player player) {
        return new Pair<>("Can I use the furnace to smelt ore?", $ ->
                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                        .setText("Can I use the furnace to smelt ore?")
                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                .setText("You can, although you'll need to find some people to",
                                        "work it for you as well. But the furnace is very",
                                        "delicate so I charge a fee for anyone who doesn't have",
                                        "level 60 smithing.")
                                .setNext(new DialogueBuilder(DialogueType.OPTION)
                                        .firstOption("I have level 60!", $$ -> new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                .setText("I have level 60!")
                                                .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                        .setText("A human has level 60 smithing? How extraordinary!")
                                                        .setNext(
                                                                new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                        .setText("Feel free to use the furnace. Remember, you only",
                                                                                "need half as much coal as with a regular furnace.")))
                                                .start(player))
                                        .secondOption("I don't have level 60", $$ ->
                                                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                        .setText("I don't have level 60")
                                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                .setText("Hmm, well, I'll let you use the furnace if you want, but",
                                                                        "you must pay a fee of 2,500 coins.")
                                                                .setNext(new DialogueBuilder(DialogueType.OPTION)
                                                                        .firstOption("Okay.", $$$ -> {

                                                                            player.getInventory().delete(ItemID.COINS, 2500);
                                                                            if (player.getInventory().contains(new Item(ItemID.COINS, 2500))) {
                                                                                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                                                        .setText("Okay, here you are.")
                                                                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                                                .setText("Okay, you can use the furnace for ten minutes.",
                                                                                                        "Remember, you only need half as much coal as with the",
                                                                                                        "regular furnace.")
                                                                                        ).start(player);
                                                                            } else {
                                                                                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                                                        .setText("Okay, here you are.")
                                                                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                                                .setText("Where?")
                                                                                                .setNext(new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                                                                        .setText("Actually I don't have money on me.")
                                                                                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                                                                .setText("Well go get it then."))
                                                                                                )).start(player);
                                                                            }
                                                                        })
                                                                        .secondOption("Maybe some other time.", $$$ ->
                                                                                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                                                        .setText("Maybe some other time.")
                                                                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                                                .setText("Well if you're not here to work, go hang around",
                                                                                                        "somewhere else!"))
                                                                                        .start(player))
                                                                )).start(player))
                                )).start(player));
    }

    private static Pair<String, Consumer<Player>> getFourthOption(Player player) {
        return new Pair<>("Maybe some other time.", $ ->
                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                        .setText("Maybe some other time.")
                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                .setText("Well if you're not here to work, go hang around",
                                        "somewhere else!")).start(player));
    }
}
