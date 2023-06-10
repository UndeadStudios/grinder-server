package com.grinder.game.content.minigame.warriorsguild.npcs.shops;

import com.grinder.game.content.minigame.warriorsguild.WarriorGuildNpc;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.NPCActions;
import com.grinder.game.model.Position;
import com.grinder.game.model.item.container.shop.ShopManager;
import com.grinder.util.ShopIdentifiers;

import static com.grinder.util.NpcID.ANTON;

/**
 * @author L E G E N D
 */
public final class Anton extends WarriorGuildNpc {

    public Anton(int id, Position position) {
        super(id, position);
        setupOverheadChat(10,
                "A fine selection of blades for you to peruse, come take a look!",
                "Armours and axes to suit your needs.",
                "Imported weapons from the finest smithys around the lands!",
                "Ow my toe! That armour is heavy."
        );
    }

    static {
        NPCActions.INSTANCE.onClick(new int[]{ANTON}, action -> {
            var player = action.getPlayer();
            if (action.getType() == NPCActions.ClickAction.Type.FIRST_OPTION) {
                makeNpcDialogue(makePlayerDialogue(makeNpcDialogue(makeMessageDialogue(null, "").setPostAction(Anton::openShop),
                        "Indeed so, specially imported from the finest smiths",
                        "around the lands, take a look at my wares."),
                        "Looks like you have a good selection of weapons around", "here..."),
                        "Ahh hello there. How can I help?")
                        .start(player);
            } else if (action.getType() == NPCActions.ClickAction.Type.SECOND_OPTION) {
                openShop(player);
            }
            return true;
        });
    }

    private static void openShop(Player player) {
        ShopManager.open(player, ShopIdentifiers.WARRIOR_GUILD_ARMOURY);
    }
}