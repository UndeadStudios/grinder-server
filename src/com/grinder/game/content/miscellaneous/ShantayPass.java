package com.grinder.game.content.miscellaneous;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.NPCActions;
import com.grinder.game.model.ObjectActions;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.shop.ShopManager;
import com.grinder.util.ItemID;
import com.grinder.util.ObjectID;
import com.grinder.util.ShopIdentifiers;

import static com.grinder.util.NpcID.SHANTAY;
import static com.grinder.util.NpcID.SHANTAY_GUARD_4648;

/**
 * @author L E G E N D
 * @date 2/16/2021
 * @time 2:42 AM
 * @discord L E G E N D#4380
 */
public class ShantayPass {

    static {
        ObjectActions.INSTANCE.onClick(new int[]{ObjectID.SHANTAY_PASS}, action -> {
            var player = action.getPlayer();
            if (action.isFirstOption()) {
                if (player.getInventory().contains(ItemID.SHANTAY_PASS) || player.getY() < 3117) {
                    pass(player);
                } else {
                    new DialogueBuilder(DialogueType.STATEMENT)
                            .setText("You need a Shantay Pass to go there.")
                            .start(player);
                }
            } else if (action.isSecondOption()) {
                new DialogueBuilder(DialogueType.STATEMENT).setText("You look at the huge stone gate.",
                        "Near the gate is a large billboard poster, it reads")
                        .setNext(new DialogueBuilder(DialogueType.STATEMENT)
                                .setText("<col=ff0000>The Desert is a VERY Dangerous place. Do not enter if you are",
                                        "<col=ff0000>afraid of dying. Beware of high temperatures, sand storms, robbers,",
                                        "<col=ff0000>and slavers. No responsibility is taken by Shantay if anything bad.",
                                        "<col=ff0000>should happen to you in any  circumstances whatsoever.")
                                .setNext(new DialogueBuilder(DialogueType.STATEMENT)
                                        .setText("Despite this warning lots of people seem to pass through the gate.")

                                )).start(player);
            }
            return true;
        });
        NPCActions.INSTANCE.onClick(new int[]{SHANTAY}, action -> {
            var player = action.getPlayer();
            if (action.getType() == NPCActions.ClickAction.Type.FIRST_OPTION) {
                new DialogueBuilder(DialogueType.NPC_STATEMENT)
                        .setNpcChatHead(SHANTAY)
                        .setText("Hello again friend. Please read the billboard poster",
                                "before going into the desert. It'll give yer details on the",
                                "dangers you can face.")
                        .setNext(new DialogueBuilder(DialogueType.OPTION)
                                .firstOption("What is this place?", $ ->
                                        new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                .setText("What is this place?")
                                                .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                        .setText("This is the pass of Shantay. I guard this area with my",
                                                                "men. I am responsible for keeping this pass open and",
                                                                "repaired")
                                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                .setText("My men and I prevent outlaws from getting out of the",
                                                                        "desert. And we stop the inexperienced from a dry death",
                                                                        "in the sands. Which would you say to were?"))

                                                ).start(player))
                                .secondOption("Can I see what you have to sell please?", $ ->
                                        new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                .setText("Can I see what you have to sell please?")
                                                .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                        .setText("Absolutely Effendi!")
                                                        .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                .setPostAction(ShantayPass::openShop))

                                                ).start(player))
                                .thirdOption("I must be going.", $ ->
                                        new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                .setText("I must be going.")
                                                .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                        .setText("So long...")).start(player))
                                .fourthOption("I want to buy a shantay pass for 5 gold coins.", $ ->
                                        new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                .setText("I want to buy a shantay pass for 5 gold coins.")
                                                .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                        .setPostAction(ShantayPass::buyShantayPass)
                                                ).start(player))

                        ).start(player);

            } else if (action.getType() == NPCActions.ClickAction.Type.SECOND_OPTION) {
                openShop(player);
            } else if (action.getType() == NPCActions.ClickAction.Type.THIRD_OPTION) {
                buyShantayPass(player);
            }
            return true;
        });
        NPCActions.INSTANCE.onClick(new int[]{SHANTAY_GUARD_4648}, action -> {
            var player = action.getPlayer();
            if (action.getType() == NPCActions.ClickAction.Type.FIRST_OPTION) {
                new DialogueBuilder(DialogueType.NPC_STATEMENT)
                        .setNpcChatHead(SHANTAY_GUARD_4648)
                        .setText("Hello there! What can I do for you?")
                        .setNext(new DialogueBuilder(DialogueType.OPTION)
                                .firstOption("I'd like to go into the desert please.", $ ->
                                        new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                .setText("I'd like to go into the desert please.")
                                                .setNext(new DialogueBuilder()
                                                        .setPostAction($$ -> {
                                                            if (!player.getInventory().contains(ItemID.SHANTAY_PASS)) {
                                                                new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                                        .setNpcChatHead(SHANTAY_GUARD_4648)
                                                                        .setText("You'll need a Shantay pass to go through the gate into",
                                                                                "the desert. See Shantay, he'll sell you one for a very",
                                                                                "reasonable price.").start(player);
                                                            } else {
                                                                pass(player);
                                                            }
                                                        })
                                                ).start(player))
                                .secondOption("Nothing thanks.", $ ->
                                        new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                                .setText("Nothing thanks.")
                                                .setNext(new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                                        .setText("Okay then, have a nice day."))
                                                .start(player))
                        ).start(player);

            } else if (action.getType() == NPCActions.ClickAction.Type.SECOND_OPTION) {
                if (!player.getInventory().contains(ItemID.SHANTAY_PASS)) {
                    new DialogueBuilder(DialogueType.NPC_STATEMENT)
                            .setNpcChatHead(SHANTAY_GUARD_4648)
                            .setText("You'll need a Shantay pass to go through the gate into",
                                    "the desert. See Shantay, he'll sell you one for a very",
                                    "reasonable price.").start(player);
                } else {
                    pass(player);
                }
            }
            return true;
        });
    }

    private static void buyShantayPass(Player player) {
        if (player.getInventory().contains(new Item(ItemID.COINS, 5))) {
            if (player.getInventory().countFreeSlots() > 0) {
                new DialogueBuilder(DialogueType.ITEM_STATEMENT)
                        .setItem(ItemID.SHANTAY_PASS, 200).
                        setText("You purchase a Shantay Pass.").start(player);
                player.getInventory().add(new Item(ItemID.SHANTAY_PASS, 1), true);
                player.getInventory().delete(ItemID.COINS, 5, true);
            } else {
                new DialogueBuilder(DialogueType.NPC_STATEMENT)
                        .setNpcChatHead(SHANTAY)
                        .setText("Sorry friend, you'll need more inventory space to buy a pass.")
                        .start(player);
            }
        } else {
            new DialogueBuilder(DialogueType.NPC_STATEMENT)
                    .setNpcChatHead(SHANTAY).setText("Sorry friend, the Shantay Pass is 5 gold coins. You",
                    "don't seem to have enough money!").start(player);
        }
    }

    private static void openShop(Player player) {
        ShopManager.open(player, ShopIdentifiers.SHANTAY_PASS_SHOP);
    }

    private static void pass(Player player) {
        var y = 3117;
        if (player.getY() >= 3117) {
            player.getInventory().delete(ItemID.SHANTAY_PASS, 1);
            y = 3115;
        }
        player.getMotion().enqueuePathToWithoutCollisionChecks(player.getX(), y);
    }
}
