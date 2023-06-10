package com.grinder.game.content.quest.impl;

import com.grinder.game.content.quest.Quest;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.model.item.Item;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/dexter+morgan/>
 */
public class MonksFriend extends Quest {

    private static final Item CHILDS_BLANKET = new Item(90);

    private static final Item JUG_OF_WATER = new Item(1937);

    private static final Item LOGS = new Item(1511);

    public MonksFriend() {
        super("Monk's Friend", false, 1, 7);
    }

    @Override
    public String[][] getDescription(Player player) {
        return new String[][]{
                {
                        "",
                        "A monk's child has had their blanket stolen. Find",
                        "the thieves' den and return the blanket, then",
                        "help Brother Omad organise the drinks for the",
                        "child's birthday party."

                },
                {
                        "",
                        "Brother Omad wants me to retrieve a child's",
                        "blanket from some thieves found in a secret",
                        "cave hidden under a ring of stones, west of",
                        "the monastery.",
                },
                {
                        "",
                        "I retrieved and returned the blanket to",
                        "brother Omad. I should speak to him again.",
                },
                {
                        "",
                        "He now wants to organise",
                        "a party for the child's birthday. He",
                        "said to find Brother Cedric who was",
                        "lost in the forest south of Ardougne.",
                },
                {
                        "",
                        "I found brother Cedric who is very drunk.",
                        "He asked me to bring him a jug of water.."
                },
                {
                        "",
                        "I gave brother Cedric some water and he",
                        "asked me to bring him some logs..",
                },
                {
                        "",
                        "I found Brother Cedric who was drunk.",
                        "I managed to sober him up and he said",
                        "that I should inform Brother Omad that",
                        "he will return soon.."
                },
                {""},
        };
    }

    @Override
    public int[] getQuestNpcs() {
        return new int[]{4244, 4245};
    }

    @Override
    public void getEndDialogue(Player player, int npcId) {
        int id = player.getDialogue().id();

        if (id == 7) {
            increaseStage(player);
            sendDialogue(player, 9);
        } else if (id == 12) {
            boolean hasBlanket = player.getInventory().contains(CHILDS_BLANKET);

            sendDialogue(player, hasBlanket ? 15 : 14);
        } else if (id == 15) {
            player.getInventory().delete(CHILDS_BLANKET);
            increaseStage(player);
            sendDialogue(player, 17);
        } else if (id == 32 || id == 37) {
            increaseStage(player);
            player.getPacketSender().sendInterfaceRemoval();
        } else if(id == 39) {
            boolean hasWater = player.getInventory().contains(JUG_OF_WATER);

            sendDialogue(player, hasWater ? 42 : 41);
        } else if(id == 43) {
            player.getInventory().delete(JUG_OF_WATER);
            increaseStage(player);
            sendDialogue(player, 45);
        } else if(id == 50) {
            boolean hasLogs = player.getInventory().contains(LOGS);

            if(hasLogs) {
                sendDialogue(player, 53);
                player.getInventory().delete(LOGS);
                increaseStage(player);
            } else {
                sendDialogue(player, 52);
            }
        } else if(id == 55) {
            increaseStage(player);
            QuestManager.complete(player, quest, new String[]{"1,000 Law runes."}, 563);
            player.getInventory().add(new Item(563, 1_000));
        }
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
