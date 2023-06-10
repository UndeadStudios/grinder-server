package com.grinder.game.content.task_new;

import com.google.common.collect.ImmutableList;
import com.grinder.game.content.tasks.DailyTaskRewards;
import com.grinder.game.content.tasks.WeeklyTaskRewards;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainerUtil;
import com.grinder.util.Misc;
import com.grinder.util.random.RandomGen;

import java.util.concurrent.TimeUnit;

public final class PlayerTaskManager {

    private static final RandomGen RANDOM = new RandomGen();

    public static void openInterface(Player player) {
        generatePlayerTasks(player);
        setClaimColour(player);
        player.getPacketSender().sendInterface(24500);
    }

    private static void generatePlayerTasks(Player player) {

        Item[] dailyTaskItems = new Item[]{generateRandomDailyItem(player), generateRandomDailyItem(player), generateRandomDailyItem(player)};
        Item weeklyItem = generateRandomWeeklyItem(player);

        if (dailyTaskItems[0] == null) {
            dailyTaskItems[0] = new Item(EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_ID_ONE, 0), EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_ONE, 0));
            dailyTaskItems[1] = new Item(EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_ID_TWO, 0), EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_TWO, 0));
            dailyTaskItems[2] = new Item(EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_ID_THREE, 0), EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_THREE, 0));
        } else {
            EntityExtKt.setInt(player, Attribute.TASK_REWARD_ITEM_ID_ONE, dailyTaskItems[0].getId(), 0);
            EntityExtKt.setInt(player, Attribute.TASK_REWARD_ITEM_ID_TWO, dailyTaskItems[1].getId(), 0);
            EntityExtKt.setInt(player, Attribute.TASK_REWARD_ITEM_ID_THREE, dailyTaskItems[2].getId(), 0);
            EntityExtKt.setInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_ONE, dailyTaskItems[0].getAmount(), 0);
            EntityExtKt.setInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_TWO, dailyTaskItems[1].getAmount(), 0);
            EntityExtKt.setInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_THREE, dailyTaskItems[2].getAmount(), 0);
        }

        if (weeklyItem == null) {
            weeklyItem = new Item(EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_ID_FOUR, 0), EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_FOUR, 0));
        } else {
            EntityExtKt.setInt(player, Attribute.TASK_REWARD_ITEM_ID_FOUR, weeklyItem.getId(), 0);
            EntityExtKt.setInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_FOUR, weeklyItem.getAmount(), 0);
        }

        generateRandomDailyTask(player);
        generateRandomWeeklyTask(player);


        //Daily reward visuals
        for(int i = 0; i < dailyTaskItems.length; i++) {
            setRewardImage(player, i, dailyTaskItems[i]);
            String attributeId = i == 0 ? Attribute.TASK_ID_ONE_T : i == 1 ? Attribute.TASK_ID_TWO_T : Attribute.TASK_ID_THREE_T;
            String attributeDone = i == 0 ? Attribute.TASK_AMOUNT_DONE_ONE : i == 1 ? Attribute.TASK_AMOUNT_DONE_TWO : Attribute.TASK_AMOUNT_DONE_THREE;

            String taskID = EntityExtKt.getString(player, attributeId, "");
            int taskAmountDone = EntityExtKt.getInt(player, attributeDone, 0);

            setText(player, i, DailyTask.valueOf(taskID).title(), DailyTask.valueOf(taskID).description());
            setAmount(player, i, taskAmountDone, DailyTask.valueOf(taskID).amount());
        }

        String taskID = EntityExtKt.getString(player, Attribute.TASK_ID_FOUR_T, "");
        int taskAmountDone = EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_FOUR, 0);

        setText(player, 3, WeeklyTask.valueOf(taskID).title(), WeeklyTask.valueOf(taskID).description());
        setAmount(player, 3, taskAmountDone, WeeklyTask.valueOf(taskID).amount());
        setRewardImage(player, 3, weeklyItem);
    }

    private static void generateRandomDailyTask(Player player) {
        if (EntityExtKt.passedTime(player, Attribute.TASK_DAILY_TIMER_T, 1, TimeUnit.DAYS, false, true)) {
            ImmutableList<DailyTask> dailyTasksList = DailyTask.DAILY_TASKS.asList();
            PlayerTask[] tasks = new PlayerTask[]{RANDOM.random(dailyTasksList), RANDOM.random(dailyTasksList), RANDOM.random(dailyTasksList)};

            EntityExtKt.setString(player, Attribute.TASK_ID_ONE_T, tasks[0].id().name(), "");
            EntityExtKt.setString(player, Attribute.TASK_ID_TWO_T, tasks[1].id().name(), "");
            EntityExtKt.setString(player, Attribute.TASK_ID_THREE_T, tasks[2].id().name(), "");

            EntityExtKt.setInt(player, Attribute.TASK_AMOUNT_DONE_ONE, 0, 0);
            EntityExtKt.setInt(player, Attribute.TASK_AMOUNT_DONE_TWO, 0, 0);
            EntityExtKt.setInt(player, Attribute.TASK_AMOUNT_DONE_THREE, 0, 0);

            EntityExtKt.setBoolean(player, Attribute.TASK_CLAIMED_ONE, false, false);
            EntityExtKt.setBoolean(player, Attribute.TASK_CLAIMED_TWO, false, false);
            EntityExtKt.setBoolean(player, Attribute.TASK_CLAIMED_THREE, false, false);
        }
    }

    private static void generateRandomWeeklyTask(Player player) {
        if(EntityExtKt.passedTime(player, Attribute.TASK_WEEKLY_TIMER_T, 7, TimeUnit.DAYS, false, true)) {

            WeeklyTask weeklyTask = RANDOM.random(WeeklyTask.WEEKLY_TASKS.asList());
            EntityExtKt.setString(player, Attribute.TASK_ID_FOUR_T, weeklyTask.id().name(), "");

            EntityExtKt.setInt(player, Attribute.TASK_AMOUNT_DONE_FOUR, 0, 0);

            EntityExtKt.setBoolean(player, Attribute.TASK_CLAIMED_FOUR, false, false);
        }
    }

    private static Item generateRandomDailyItem(Player player) {
        if (EntityExtKt.passedTime(player, Attribute.TASK_DAILY_TIMER_T, 1, TimeUnit.DAYS, false, false)) {
            int dailyLength = DailyTaskRewards.values().length;

            while (true) {
                int tryId = RANDOM.get().nextInt(dailyLength);
                if (Misc.getRandomDouble(100) <= DailyTaskRewards.values()[tryId].GetChance()) {
                    return DailyTaskRewards.values()[tryId].GetItem();
                }
            }
        }

        return null;
    }

    private static Item generateRandomWeeklyItem(Player player) {
        if (EntityExtKt.passedTime(player, Attribute.TASK_WEEKLY_TIMER_T, 7, TimeUnit.DAYS, false, false)) {
            int weeklyLength = WeeklyTaskRewards.values().length;

            while (true) {
                int tryId = RANDOM.get().nextInt(weeklyLength);
                if (Misc.getRandomDouble(100) <= WeeklyTaskRewards.values()[tryId].GetChance()) {
                    return WeeklyTaskRewards.values()[tryId].GetItem();
                }
            }
        }

        return null;
    }

    public static void progressTask(Player player, PlayerTask task) {
        progressTask(player, task, 1);
    }

    public static void progressTask(Player player, PlayerTask task, int amount) {
        String taskIdOne = EntityExtKt.getString(player, Attribute.TASK_ID_ONE_T, "");
        String taskIdTwo = EntityExtKt.getString(player, Attribute.TASK_ID_TWO_T, "");
        String taskIdThree = EntityExtKt.getString(player, Attribute.TASK_ID_THREE_T, "");
        String taskIdFour = EntityExtKt.getString(player, Attribute.TASK_ID_FOUR_T, "");

        if(!taskIdOne.isBlank() && DailyTask.valueOf(taskIdOne).equals(task.id())) {
            if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_ONE, 0)+amount == task.amount()) {
                player.sendMessage("@dre@Your daily task is now complete!");
            }

            EntityExtKt.incInt(player, Attribute.TASK_AMOUNT_DONE_ONE, amount, task.amount(), 0);
        } else if(!taskIdTwo.isBlank() && DailyTask.valueOf(taskIdTwo).equals(task.id())) {
            if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_TWO, 0)+amount == task.amount()) {
                player.sendMessage("@dre@Your daily task is now complete!");
            }

            EntityExtKt.incInt(player, Attribute.TASK_AMOUNT_DONE_TWO, amount, task.amount(), 0);
        } else if(!taskIdThree.isBlank() && DailyTask.valueOf(taskIdThree).equals(task.id())) {
            if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_THREE, 0)+amount == task.amount()) {
                player.sendMessage("@dre@Your daily task is now complete!");
            }

            EntityExtKt.incInt(player, Attribute.TASK_AMOUNT_DONE_THREE, amount, task.amount(), 0);
        } else if(!taskIdFour.isBlank() && WeeklyTask.valueOf(taskIdFour).equals(task.id())) {
            if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_FOUR, 0)+amount == task.amount()) {
                player.sendMessage("@dre@Your weekly task is now complete!");
            }

            EntityExtKt.incInt(player, Attribute.TASK_AMOUNT_DONE_FOUR, amount, task.amount(), 0);
        }
    }

    public static void progressTask(Player player, String task) {
        String taskIdOne = EntityExtKt.getString(player, Attribute.TASK_ID_ONE_T, "");
        String taskIdTwo = EntityExtKt.getString(player, Attribute.TASK_ID_TWO_T, "");
        String taskIdThree = EntityExtKt.getString(player, Attribute.TASK_ID_THREE_T, "");
        String taskIdFour = EntityExtKt.getString(player, Attribute.TASK_ID_FOUR_T, "");

        if(!taskIdOne.isBlank() && DailyTask.valueOf(taskIdOne).getName().equalsIgnoreCase(task)) {
            if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_ONE, 0)+1 == DailyTask.valueOf(taskIdOne).amount()) {
                player.sendMessage("@dre@Your daily task is now complete!");
            }

            EntityExtKt.incInt(player, Attribute.TASK_AMOUNT_DONE_ONE, 1, DailyTask.valueOf(taskIdOne).amount(), 0);
        }
        if(!taskIdTwo.isBlank() && DailyTask.valueOf(taskIdTwo).getName().equalsIgnoreCase(task)) {
            if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_TWO, 0)+1 == DailyTask.valueOf(taskIdTwo).amount()) {
                player.sendMessage("@dre@Your daily task is now complete!");
            }

            EntityExtKt.incInt(player, Attribute.TASK_AMOUNT_DONE_TWO, 1, DailyTask.valueOf(taskIdTwo).amount(), 0);
        }
        if(!taskIdThree.isBlank() && DailyTask.valueOf(taskIdThree).getName().equalsIgnoreCase(task)) {
            if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_THREE, 0)+1 == DailyTask.valueOf(taskIdThree).amount()) {
                player.sendMessage("@dre@Your daily task is now complete!");
            }

            EntityExtKt.incInt(player, Attribute.TASK_AMOUNT_DONE_THREE, 1, DailyTask.valueOf(taskIdThree).amount(), 0);
        }
        if(!taskIdFour.isBlank() && WeeklyTask.valueOf(taskIdFour).getName().equalsIgnoreCase(task)) {
            if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_FOUR, 0)+1 == WeeklyTask.valueOf(taskIdFour).amount()) {
                player.sendMessage("@dre@Your weekly task is now complete!");
            }

            EntityExtKt.incInt(player, Attribute.TASK_AMOUNT_DONE_FOUR, 1, WeeklyTask.valueOf(taskIdFour).amount(), 0);
        }
    }

    public static void progressCombatTask(Player player, NPC npc) {
        progressTask(player, npc.fetchDefinition().getName());
    }

    private static void setRewardImage(Player player, int id, Item item) {
        if (item.getDefinition().isNoted()) {
            item = new Item(item.getDefinition().unNote(), item.getAmount());
        }
        player.getPacketSender().sendItemOnInterface(24505+id, item, 1);
    }

    private static void setText(Player player, int id, String title, String description) {
        String titleStart = "Daily Task - ";
        if (id == 3) {
            titleStart = "Weekly Task - ";
        }

        player.getPacketSender().sendString(24509+id, titleStart+title);
        player.getPacketSender().sendString(24513+id, description);
    }

    private static void setAmount(Player player, int id, int currentAmount, int neededAmount) {
        player.getPacketSender().sendString(24529+id, currentAmount+"/"+neededAmount);
    }

    public static void claimReward(Player player, int buttonId) {
        if(buttonId != 24517 && buttonId != 24520 && buttonId != 24523 && buttonId != 24526) {
            return;
        }

        String taskIdOne = EntityExtKt.getString(player, Attribute.TASK_ID_ONE_T, "");
        String taskIdTwo = EntityExtKt.getString(player, Attribute.TASK_ID_TWO_T, "");
        String taskIdThree = EntityExtKt.getString(player, Attribute.TASK_ID_THREE_T, "");
        String taskIdFour = EntityExtKt.getString(player, Attribute.TASK_ID_FOUR_T, "");

        switch(buttonId) {
            case 24517:
                if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_ONE, 0) == DailyTask.valueOf(taskIdOne).amount()) {
                    if (!EntityExtKt.getBoolean(player, Attribute.TASK_CLAIMED_ONE, false)) {
                        Item item = new Item(EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_ID_ONE, 0), EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_ONE, 0));

                        ItemContainerUtil.addOrDrop(player.getInventory(), player, item);
                        player.sendMessage("@dre@You have been awarded " + Misc.format(item.getAmount()) + " " + item.getDefinition().getName() + " for completing the daily task.");
                        EntityExtKt.setBoolean(player, Attribute.TASK_CLAIMED_ONE, true, false);
                        player.getPacketSender().sendString(24537, "@gre@Claimed");
                    } else {
                        player.sendMessage("@dre@You have already claimed your daily task reward!");
                    }
                } else {
                    player.sendMessage("@dre@You have not completed this task yet!");
                }
                break;
            case 24520:
                if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_TWO, 0) == DailyTask.valueOf(taskIdTwo).amount()) {
                    if (!EntityExtKt.getBoolean(player, Attribute.TASK_CLAIMED_TWO, false)) {
                        Item item = new Item(EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_ID_TWO, 0), EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_TWO, 0));

                        ItemContainerUtil.addOrDrop(player.getInventory(), player, item);
                        player.sendMessage("@dre@You have been awarded " + Misc.format(item.getAmount()) + " " + item.getDefinition().getName() + " for completing the daily task.");
                        EntityExtKt.setBoolean(player, Attribute.TASK_CLAIMED_TWO, true, false);
                        player.getPacketSender().sendString(24538, "@gre@Claimed");
                    } else {
                        player.sendMessage("@dre@You have already claimed your daily task reward!");
                    }
                } else {
                    player.sendMessage("@dre@You have not completed this task yet!");
                }
                break;
            case 24523:
                if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_THREE, 0) == DailyTask.valueOf(taskIdThree).amount()) {
                    if (!EntityExtKt.getBoolean(player, Attribute.TASK_CLAIMED_THREE, false)) {
                        Item item = new Item(EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_ID_THREE, 0), EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_THREE, 0));

                        ItemContainerUtil.addOrDrop(player.getInventory(), player, item);
                        player.sendMessage("@dre@You have been awarded " + Misc.format(item.getAmount()) + " " + item.getDefinition().getName() + " for completing the daily task.");
                        EntityExtKt.setBoolean(player, Attribute.TASK_CLAIMED_THREE, true, false);
                        player.getPacketSender().sendString(24539, "@gre@Claimed");
                    } else {
                        player.sendMessage("@dre@You have already claimed your daily task reward!");
                    }
                } else {
                    player.sendMessage("@dre@You have not completed this task yet!");
                }
                break;
            case 24526:
                if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_FOUR, 0) == WeeklyTask.valueOf(taskIdFour).amount()) {
                    if (!EntityExtKt.getBoolean(player, Attribute.TASK_CLAIMED_FOUR, false)) {
                        Item item = new Item(EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_ID_FOUR, 0), EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_FOUR, 0));

                        ItemContainerUtil.addOrDrop(player.getInventory(), player, item);
                        player.sendMessage("@dre@You have been awarded " + Misc.format(item.getAmount()) + " " + item.getDefinition().getName() + " for completing the daily task.");
                        EntityExtKt.setBoolean(player, Attribute.TASK_CLAIMED_FOUR, true, false);
                        player.getPacketSender().sendString(24540, "@gre@Claimed");
                    } else {
                        player.sendMessage("@dre@You have already claimed your daily task reward!");
                    }
                } else {
                    player.sendMessage("@dre@You have not completed this task yet!");
                }
                break;
        }
    }

    private static void setClaimColour(Player player) {
        String taskIdOne = EntityExtKt.getString(player, Attribute.TASK_ID_ONE_T, "");
        String taskIdTwo = EntityExtKt.getString(player, Attribute.TASK_ID_TWO_T, "");
        String taskIdThree = EntityExtKt.getString(player, Attribute.TASK_ID_THREE_T, "");
        String taskIdFour = EntityExtKt.getString(player, Attribute.TASK_ID_FOUR_T, "");

        DailyTask dailyTaskOne = DailyTask.valueOf(taskIdOne);

        if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_ONE, 0) > 0 && EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_ONE, 0) < dailyTaskOne.amount()) {
            player.getPacketSender().sendString(24537, "@or1@Claim");
        } else if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_ONE, 0) == dailyTaskOne.amount()) {
            if (!EntityExtKt.getBoolean(player, Attribute.TASK_CLAIMED_ONE, false)) {
                player.getPacketSender().sendString(24537, "@gre@Claim");
            } else {
                player.getPacketSender().sendString(24537, "@gre@Claimed");
            }
        } else {
            player.getPacketSender().sendString(24537, "@red@Claim");
        }

        DailyTask dailyTaskTwo = DailyTask.valueOf(taskIdTwo);

        if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_TWO, 0) > 0 && EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_TWO, 0) < dailyTaskTwo.amount()) {
            player.getPacketSender().sendString(24538, "@or1@Claim");
        } else if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_TWO, 0) == dailyTaskTwo.amount()) {
            if (!EntityExtKt.getBoolean(player, Attribute.TASK_CLAIMED_TWO, false)) {
                player.getPacketSender().sendString(24538, "@gre@Claim");
            } else {
                player.getPacketSender().sendString(24538, "@gre@Claimed");
            }
        } else {
            player.getPacketSender().sendString(24538, "@red@Claim");
        }

        DailyTask dailyTaskThree = DailyTask.valueOf(taskIdThree);

        if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_THREE, 0) > 0 && EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_THREE, 0) < dailyTaskThree.amount()) {
            player.getPacketSender().sendString(24539, "@or1@Claim");
        } else if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_THREE, 0) == dailyTaskThree.amount()) {
            if (!EntityExtKt.getBoolean(player, Attribute.TASK_CLAIMED_THREE, false)) {
                player.getPacketSender().sendString(24539, "@gre@Claim");
            } else {
                player.getPacketSender().sendString(24539, "@gre@Claimed");
            }
        } else {
            player.getPacketSender().sendString(24539, "@red@Claim");
        }

        WeeklyTask weeklyTaskFour = WeeklyTask.valueOf(taskIdFour);

        if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_FOUR, 0) > 0 && EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_FOUR, 0) < weeklyTaskFour.amount()) {
            player.getPacketSender().sendString(24540, "@or1@Claim");
        } else if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_FOUR, 0) == weeklyTaskFour.amount()) {
            if (!EntityExtKt.getBoolean(player, Attribute.TASK_CLAIMED_FOUR, false)) {

                player.getPacketSender().sendString(24540, "@gre@Claim");
            } else {
                player.getPacketSender().sendString(24540, "@gre@Claimed");
            }
        } else {
            player.getPacketSender().sendString(24540, "@red@Claim");
        }
    }
}
