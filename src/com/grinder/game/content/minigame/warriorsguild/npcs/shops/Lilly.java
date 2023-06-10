package com.grinder.game.content.minigame.warriorsguild.npcs.shops;

import com.grinder.game.content.minigame.warriorsguild.WarriorGuildNpc;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.NPCActions;
import com.grinder.game.model.Position;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.container.shop.ShopManager;
import com.grinder.util.ShopIdentifiers;
import kotlin.Pair;

import java.util.ArrayList;
import java.util.function.Consumer;

import static com.grinder.util.NpcID.LILLY;

/**
 * @author L E G E N D
 */
public final class Lilly extends WarriorGuildNpc {

    public Lilly(int id, Position position) {
        super(id, position);
        setupOverheadChat(
                25,
                "Pink bananas.... mmmm",
                "Don't look back, they might be gaining on you.",
                "If you don't care where you are, then you ain't lost",
                "Madness takes its toll. Please have exact change.",
                "It's not an optical illusion, it just looks like one.",
                "Demons are a Ghoul's best Friend.");
    }

    static {
        NPCActions.INSTANCE.onClick(new int[]{LILLY}, action -> {
            var player = action.getPlayer();
            if (action.getType() == NPCActions.ClickAction.Type.FIRST_OPTION) {
                var optionsD = new DialogueBuilder(DialogueType.OPTION);
                var options = new ArrayList<Pair<String, Consumer<Player>>>();
                options.add(new Pair<>("I'd like to see what you have for sale.", $ ->
                        makePlayerDialogue(makeNpcDialogue(makeMessageDialogue(null, "").setPostAction(Lilly::openShop),
                                "Of course..."),
                                "I'd like to see what you have for sale.")
                                .start(player)));
                options.add(new Pair<>("That's a pretty wall hanging.", $ ->
                        makePlayerDialogue(
                                makeNpcDialogue(
                                        makePlayerDialogue(
                                                makeNpcDialogue(null,
                                                        "Yes, it's a hobby of mine when I'm.... relaxing."),
                                                "Really? Is that why there's all this cloth and dye", "around?"),
                                        "Do you think so? I made it myself."),
                                "That's a pretty wall hanging.").start(player)));
                options.add(new Pair<>("Bye!", $ ->
                        makePlayerDialogue(makeNpcDialogue(null, "Have fun and come back soon!"), "Bye!")
                                .start(player)));
                optionsD.addOptions(options);
                makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(optionsD,
                        "Erm... yes. When I'm not drinking them."),
                        "Umm... do you sell potions?"),
                        "Uh..... hi... didn't see you there. Can.... I help?")
                        .start(player);
            } else if (action.getType() == NPCActions.ClickAction.Type.SECOND_OPTION) {
                openShop(player);
            }
            return true;
        });
    }

    private static void openShop(Player player) {
        ShopManager.open(player, ShopIdentifiers.WARRIOR_GUILD_POTION_SHOP);
    }
}
