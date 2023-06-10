package com.grinder.game.content.minigame.warriorsguild.npcs.shops;

import com.grinder.game.content.minigame.warriorsguild.WarriorGuildNpc;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.NPCActions;
import com.grinder.game.model.Position;
import com.grinder.game.model.item.container.shop.ShopManager;
import com.grinder.util.ShopIdentifiers;

import static com.grinder.util.NpcID.LIDIO;

/**
 * @author L E G E N D
 */
public final class Lidio extends WarriorGuildNpc {

    public Lidio(int id, Position position) {
        super(id, position);
        setupOverheadChat(41,
                "Come try my lovely pizza or maybe some fish!",
                "Potatoes are filling and healthy too!",
                "Stew to fill the belly, on sale here!");
    }

    static {
        NPCActions.INSTANCE.onClick(new int[]{LIDIO}, action -> {
            var player = action.getPlayer();
            if (action.getType() == NPCActions.ClickAction.Type.FIRST_OPTION) {
                makeNpcDialogue(makePlayerDialogue(makeMessageDialogue(null, "").setPostAction(Lidio::openShop),
                        "With food preferably."),
                        "Greetings warrior, how can i fill your stomach today?")
                        .start(player);
            } else if (action.getType() == NPCActions.ClickAction.Type.SECOND_OPTION) {
                openShop(player);
            }
            return true;
        });
    }

    private static void openShop(Player player) {
        ShopManager.open(player, ShopIdentifiers.WARRIOR_GUILD_FOOD_SHOP);
    }
}
