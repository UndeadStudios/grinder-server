package com.grinder.game.content.minigame.motherlodemine.npcs;

import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.NPCActions;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import kotlin.Pair;

import java.util.Arrays;
import java.util.function.Consumer;

import static com.grinder.util.NpcID.DWARF_7721;

/**
 * @author L E G E N D
 * @date 2/14/2021
 * @time 6:11 AM
 * @discord L E G E N D#4380
 */
public final class Dwarf extends NPC {

    public Dwarf(int id, Position position) {
        super(id, position);
        setArea(AreaManager.MOTHERLODE_MINE_AREA);
    }

    static {
        NPCActions.INSTANCE.onClick(new int[]{DWARF_7721}, action -> {
            var player = action.getPlayer();
            new DialogueBuilder(DialogueType.NPC_STATEMENT)
                    .setNpcChatHead(DWARF_7721)
                    .setText("Hello there. Do you need anything?")
                    .setNext(new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                            .setText("What's through this cave?")
                            .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                    .setText("This cave leads to the Mining guild, home to the finest",
                                            "mining site around.", "Is there anything else I can help you with?")
                                    .setNext(new DialogueBuilder(DialogueType.OPTION)
                                            .addOptions(Arrays.asList(getFirstOption(player), getSecondOption(player), getThirdOption(player))))

                            )).start(player);
            return true;
        });
    }

    private static Pair<String, Consumer<Player>> getFirstOption(Player player) {
        return new Pair<>("What have you got in the Guild?", $ ->
                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                        .setText("What have you got in the guild?")
                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                .setText("All sort of things!",
                                        "There's plenty of coal rocks along with some iron,",
                                        "mithril and adamantite as well.")
                                .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                        .setText("Deeper in the guild you'll find even more rocks",
                                                "including some runite!",
                                                "The best bit though is our amethyst mine, the only one",
                                                "in the land!")
                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                .setText("There's no better mining site anywhere!")
                                                .setNext(new DialogueBuilder(DialogueType.OPTION)
                                                        .addOptions(Arrays.asList(getSecondOption(player), getThirdOption(player))))
                                        ))).start(player));
    }

    private static Pair<String, Consumer<Player>> getSecondOption(Player player) {
        return new Pair<>("What do you dwarves do with the ores you mine?", $ ->
                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                        .setText("What do you dwarves do with the ores you mine?")
                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                .setText("What do you think? We smelt it into bars, smith the",
                                        "metal to make armour and weapons, then we exchange",
                                        "them for goods and services.")
                                .setNext(new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                        .setText("I don't see many dwarves", "selling armour or weapons here.")
                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                .setText("No, this is only for mining outpost. We dwarves don't",
                                                        "much like to settle in human cities. Most of the ore is",
                                                        "carted off to Keldagrim, the great dwarven city.",
                                                        "They've got a special blast furnace up there ّ' it makes")
                                                .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                        .setText("smelting the ore so much easier. There are plenty of",
                                                                "dwarven traders working in Keldagrim. Anyway, can I",
                                                                "help you with something else?")
                                                        .setNext(new DialogueBuilder(DialogueType.OPTION)
                                                                .addOptions(Arrays.asList(getFirstOption(player), getThirdOption(player))))
                                                )))).start(player));
    }

    private static Pair<String, Consumer<Player>> getThirdOption(Player player) {
        return new Pair<>("No thanks, I'm fine.", $ ->
                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                        .setText("No thanks, I'm fine.")
                        .setNext(null)
                        .start(player));
    }
}
