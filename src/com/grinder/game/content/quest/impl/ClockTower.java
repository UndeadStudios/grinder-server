package com.grinder.game.content.quest.impl;

import com.grinder.game.content.quest.Quest;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.model.item.Item;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/dexter+morgan/>
 */
public class ClockTower extends Quest {

    private static final Item WHITE_COG = new Item(20);

    private static final Item BLACK_COG = new Item(21);

    private static final Item BLUE_COG = new Item(22);

    private static final Item RED_COG = new Item(23);

    private static final Item[] COGS = {
      WHITE_COG, BLACK_COG, BLUE_COG, RED_COG
    };

    public ClockTower() {
        super("Clock Tower", false, 1, 2);
    }

    @Override
    public String[][] getDescription(Player player) {
        return new String[][]{
                {
                        "",
                        "Help the confused Brother Kojo find the missing",
                        "cogs and fix his watch tower.",
                },
                {
                        "",
                        "Brother Kojo said that the watch tower is broken",
                        "and that he needs four different coloured cogs",
                        "to fix it. He said to bring back to him:",
                        "",
                        QuestManager.hasItem(player, RED_COG, "Red cog"),
                        QuestManager.hasItem(player, WHITE_COG, "White cog"),
                        QuestManager.hasItem(player, BLACK_COG, "Black cog"),
                        QuestManager.hasItem(player, BLUE_COG, "Blue cog"),

                },
                {
                        "",
                }
        };
    }

    @Override
    public int[] getQuestNpcs() {
        return new int[]{3606};
    }

    @Override
    public void getEndDialogue(Player player, int npcId) {
        int id = player.getDialogue().id();

        if(id == 3) {
            increaseStage(player);
            sendDialogue(player, 5);
        } else if(id == 7) {
            boolean hasCogs = player.getInventory().contains(COGS);

            sendDialogue(player, hasCogs ? 10 : 9);
        } else if(id == 11) {
            player.getInventory().delete(COGS);
            increaseStage(player);
            QuestManager.complete(player, quest, new String[]{"6,000,000 coins."}, 995);
            player.getInventory().add(new Item(995, 6_000_000));
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
