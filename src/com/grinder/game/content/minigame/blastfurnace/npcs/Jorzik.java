package com.grinder.game.content.minigame.blastfurnace.npcs;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.NPCActions;
import com.grinder.game.model.Position;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.container.shop.ShopManager;
import com.grinder.util.ShopIdentifiers;
import kotlin.Pair;

import java.util.function.Consumer;

import static com.grinder.util.NpcID.JORZIK;

/**
 * @author L E G E N D
 * @date 2/22/2021
 * @time 10:52 AM
 * @discord L E G E N D#4380
 */
public class Jorzik extends BlastFurnaceNpc {

    public Jorzik(int id, Position position) {
        super(id, position);
    }

    static {
        NPCActions.INSTANCE.onClick(new int[]{JORZIK}, action -> {
            var player = action.getPlayer();
            if (action.getType() == NPCActions.ClickAction.Type.FIRST_OPTION) {

                var declineOption = new Pair<String, Consumer<Player>>("No thanks.", $ -> {
                    new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                            .setText("No thanks.")
                            .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                    .setText("You just don't appreciate the beauty of the fine metalwork."))
                            .start(player);
                });

                new DialogueBuilder(DialogueType.NPC_STATEMENT)
                        .setText("Do you want to trade?")
                        .setNext(new DialogueBuilder(DialogueType.OPTION)
                                .firstOption("What are you selling?", $$ ->
                                        new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                .setText("What are you selling?")
                                                .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                        .setText("The finest smiths from all over Gielinor come here to",
                                                                "work, and I buy the fruit of their craft. Armour made",
                                                                "from the higher metals!")
                                                        .setNext(new DialogueBuilder(DialogueType.OPTION)
                                                                .firstOption("Let's have a look then.", $ ->
                                                                        new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                                                .setText("Let's have a look then.")
                                                                                .setNext(new DialogueBuilder()
                                                                                        .setPostAction(Jorzik::openShop))
                                                                                .start(player)
                                                                )
                                                                .secondOption(declineOption.getFirst(), declineOption.getSecond())
                                                        )
                                                ).start(player))
                                .secondOption(declineOption.getFirst(), declineOption.getSecond())
                        ).start(player);
            } else if (action.getType() == NPCActions.ClickAction.Type.SECOND_OPTION) {
                openShop(player);
            }
            return true;
        });
    }

    public static void openShop(Player player) {
        ShopManager.open(player, ShopIdentifiers.ARMOUR_STORE);
    }
}
