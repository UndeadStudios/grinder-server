package com.grinder.game.content.quest.impl;

import com.grinder.game.content.quest.Quest;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueExpression;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ItemID;
import com.grinder.util.NpcID;

/**
 *
 * @author Dexter Morgan <https://www.rune-server.ee/members/dexter+morgan/>
 *
 */
public class PiratesTreasure extends Quest {

    private static final String QUEST = "Pirate's Treasure";

    private static final Item RUM = new Item(ItemID.KARAMJAN_RUM);

    private static final Item KEY = new Item(432);

    private static final Item PIRATES_MESSAGE = new Item(433);

    private static final Animation EMOTE = new Animation(832, 5);

    public PiratesTreasure() {
        super(QUEST, false, 2, 4);
    }

    public static boolean digForTreasure(Player p) {
        if(QuestManager.getStage(p, QUEST) == 3) {
            if(p.getPosition().sameAs(new Position(2999, 3383))) {
                QuestManager.increaseStage(p, QuestManager.PIRATES_TREASURE);
                p.getInventory().add(new Item(995, 10_000_000));
                QuestManager.complete(p, QuestManager.PIRATES_TREASURE, new String[]{"10,000,000 coins."}, 995);
                return true;
            }
        }
        return false;
    }

    @Override
    public String[][] getDescription(Player player) {
        return new String[][]{
                {
                    "",
                        "Redbeard Frank knows the location of pirate treasure,",
                        "but he'll only part with the knowledge for a bottle",
                        "of Karamjan rum."
                },
                {
                    "",
                        "I spoke to Redbeard Frank to told me to bring him",
                        "some Karamja rum in exchange for the location",
                        "of the treasure.."
                },
                {
                    "",
                        "I have given the Rum to Redbeard Frank and he",
                        "has given me a chest key. I should go visit",
                        "Varrock's Blue Moon Inn.",
                },
                {
                    "",
                        "I found the pirate message, I should go dig",
                        "in Falador park.."
                },
                {""},
        };
    }

    @Override
    public int[] getQuestNpcs() {
        return new int[]{3643};
    }

    @Override
    public void getEndDialogue(Player player, int npcId) {
        int id = player.getDialogue().id();

        if(id == 5) {
            increaseStage(player);
            sendDialogue(player, 7);
        } else if(id == 8) {
            boolean hasRum = player.getInventory().contains(RUM);

            sendDialogue(player, hasRum ? 11 : 10);
        } else if(id == 14) {
            player.getInventory().delete(RUM);
            player.getInventory().add(KEY);
            increaseStage(player);
            sendDialogue(player, 16);
        } else if(id == 21) {
            if (!player.getInventory().contains(KEY)) {
                player.getInventory().add(KEY);
                player.getPacketSender().sendInterfaceRemoval();
            } else {
                new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.REDBEARD_FRANK)
                        .setText("You already have a key I can see..!").setExpression(DialogueExpression.ANNOYED)
                        .start(player);
            }
        }
    }

    @Override
    public boolean handleItemOnObjectInteraction(Player player, Item item, GameObject object) {
        if(item.getId() == KEY.getId() && object.getId() == 2079 && getStage(player) == 2) {
            player.performAnimation(EMOTE);
            player.getPacketSender().sendSound(Sounds.OPEN_BANK_BOOTH);
            player.sendMessage("You use the key on the chest and it unlocks..");
            player.BLOCK_ALL_BUT_TALKING = true;
            player.getInventory().delete(KEY);
            TaskManager.submit(1, () -> {
                player.BLOCK_ALL_BUT_TALKING = false;
                player.getInventory().add(PIRATES_MESSAGE);
                player.getPacketSender().sendMessage("..you find a pirate's message!");
                new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(432, 250)
                        .setText("..you find a pirate's message!").start(player);
                increaseStage(player);
            });
            return true;
        }
        return false;
    }

    @Override
    public boolean handleItemInteraction(Player player, Item item, int type) {
        switch (item.getId()) {
            case 433:
                player.getPacketSender().sendMessage("Dig somewhere in Falador park..");
                return true;
        }
        return false;
    }

    @Override
    public boolean hasRequirements(Player player) {
        return true;
    }

    @Override
    public Position getTeleport() {
        return null;
    }
}
