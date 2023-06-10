package com.grinder.game.content.minigame.blastfurnace.npcs;

import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.NPCActions;
import com.grinder.game.model.Position;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.container.shop.ShopManager;
import com.grinder.util.ShopIdentifiers;

import java.util.function.Consumer;

import static com.grinder.util.NpcID.ORDAN;

/**
 * @author L E G E N D
 * @date 2/22/2021
 * @time 7:31 AM
 * @discord L E G E N D#4380
 */
public class Ordan extends NPC {

    public Ordan(int id, Position position) {
        super(id, position);
    }

    static {
        NPCActions.INSTANCE.onClick(new int[]{ORDAN}, action -> {
            var player = action.getPlayer();
            if (action.getType() == NPCActions.ClickAction.Type.FIRST_OPTION) {
                new DialogueBuilder(DialogueType.NPC_STATEMENT).setText("Are you here to smith? Do you want to buy some ore?")
                        .setNext(new DialogueBuilder(DialogueType.OPTION)
                                .firstOption("Yes please.", Ordan::openShop)
                                .secondOption("No thanks.", $ -> DialogueManager.start(player,-1))
                        ).start(player);
            } else if (action.getType() == NPCActions.ClickAction.Type.SECOND_OPTION) {
                openShop(player);
            }
            return true;
        });
    }

    public static void openShop(Player player) {
        ShopManager.open(player, ShopIdentifiers.ORE_SELLER);
    }
}
