//package com.grinder.game.content.tasks;
//
//import com.grinder.game.entity.Entity;
//import com.grinder.game.entity.EntityExtKt;
//import com.grinder.game.entity.agent.npc.NPC;
//import com.grinder.game.entity.agent.player.Player;
//import com.grinder.game.model.Skill;
//import com.grinder.game.model.attribute.Attribute;
//import com.grinder.game.model.item.Item;
//import com.grinder.game.model.item.container.ItemContainerUtil;
//import com.grinder.util.Misc;
//import org.w3c.dom.Attr;
//
//import java.util.Random;
//import java.util.concurrent.TimeUnit;
//
///**
// * @author  Minoroin / TealWool#0873 (https://www.rune-server.ee/members/minoroin/)
// * @since   07/07/2022
// * @version 1.0
// */
//
//public class TaskList {
//
//    private static Random random = new Random();
//
//    public static void ShowInterface(Player player)
//    {
//        GenerateTasks(player);
//
//        SetClaimColour(player);
//        //send interface
//        player.getPacketSender().sendInterface(24500);
//
//    }
//
//    public static void GenerateTasks(Player player)
//    {
//        //needs saving...
//        //needs to grab task
//        Item dailyTasksItems[] = {GetRandomDailyItem(player), GetRandomDailyItem(player), GetRandomDailyItem(player)};
//
//        Item weeklyItem = GetRandomWeeklyItem(player);
//
//        if (dailyTasksItems[0] == null)
//        {
//            dailyTasksItems[0] = new Item(EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_ID_ONE, 0), EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_ONE, 0));
//            dailyTasksItems[1] = new Item(EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_ID_TWO, 0), EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_TWO, 0));
//            dailyTasksItems[2] = new Item(EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_ID_THREE, 0), EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_THREE, 0));
//        }
//        else
//        {
//            EntityExtKt.setInt(player, Attribute.TASK_REWARD_ITEM_ID_ONE, dailyTasksItems[0].getId(), 0);
//            EntityExtKt.setInt(player, Attribute.TASK_REWARD_ITEM_ID_TWO, dailyTasksItems[1].getId(), 0);
//            EntityExtKt.setInt(player, Attribute.TASK_REWARD_ITEM_ID_THREE, dailyTasksItems[2].getId(), 0);
//            EntityExtKt.setInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_ONE, dailyTasksItems[0].getAmount(), 0);
//            EntityExtKt.setInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_TWO, dailyTasksItems[1].getAmount(), 0);
//            EntityExtKt.setInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_THREE, dailyTasksItems[2].getAmount(), 0);
//        }
//
//        if (weeklyItem == null)
//        {
//            weeklyItem = new Item(EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_ID_FOUR, 0), EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_FOUR, 0));
//        }
//        else
//        {
//            EntityExtKt.setInt(player, Attribute.TASK_REWARD_ITEM_ID_FOUR, weeklyItem.getId(), 0);
//            EntityExtKt.setInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_FOUR, weeklyItem.getAmount(), 0);
//        }
//
//        //DailyTasks dailyTasks[] = {GetRandomDailyTask(), GetRandomDailyTask(), GetRandomDailyTask()};
//        GetRandomDailyTask(player);
//        GetRandomWeeklyTask(player);
//
//        //Daily Reward
//        for (int i = 0; i < dailyTasksItems.length; i++)
//        {
//            SetRewardImage(player, i, dailyTasksItems[i]);
//
//            int taskID = EntityExtKt.getInt(player, Attribute.TASK_ID_ONE, 0);
//            int taskAmountDone = EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_ONE, 0);
//
//            int taskType = EntityExtKt.getInt(player, Attribute.TASK_TYPE_ONE, 0);
//
//            switch(i)
//            {
//                case 1:
//                    taskID = EntityExtKt.getInt(player, Attribute.TASK_ID_TWO, 0);
//                    taskAmountDone = EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_TWO, 0);
//                    taskType = EntityExtKt.getInt(player, Attribute.TASK_TYPE_TWO, 0);
//                    break;
//                case 2:
//                    taskID = EntityExtKt.getInt(player, Attribute.TASK_ID_THREE, 0);
//                    taskAmountDone = EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_THREE, 0);
//                    taskType = EntityExtKt.getInt(player, Attribute.TASK_TYPE_THREE, 0);
//                    break;
//            }
//
//            switch (taskType)
//            {
//                case 1:
//                    SetText(player, i, DailyTasks.CombatTasks.values()[taskID].GetTitle(), DailyTasks.CombatTasks.values()[taskID].GetDescription());
//                    SetAmount(player, i, taskAmountDone, DailyTasks.CombatTasks.values()[taskID].GetAmount());
//                    break;
//
//                case 2:
//                    SetText(player, i, DailyTasks.WoodcuttingTasks.values()[taskID].GetTitle(), DailyTasks.WoodcuttingTasks.values()[taskID].GetDescription());
//                    SetAmount(player, i, taskAmountDone, DailyTasks.WoodcuttingTasks.values()[taskID].GetAmount());
//                    break;
//
//                case 3:
//                    SetText(player, i, DailyTasks.FishingTasks.values()[taskID].GetTitle(), DailyTasks.FishingTasks.values()[taskID].GetDescription());
//                    SetAmount(player, i, taskAmountDone, DailyTasks.FishingTasks.values()[taskID].GetAmount());
//                    break;
//
//                case 4:
//                    SetText(player, i, DailyTasks.MiningTasks.values()[taskID].GetTitle(), DailyTasks.MiningTasks.values()[taskID].GetDescription());
//                    SetAmount(player, i, taskAmountDone, DailyTasks.MiningTasks.values()[taskID].GetAmount());
//                    break;
//
//                case 5:
//                    SetText(player, i, DailyTasks.FiremakingTasks.values()[taskID].GetTitle(), DailyTasks.FiremakingTasks.values()[taskID].GetDescription());
//                    SetAmount(player, i, taskAmountDone, DailyTasks.FiremakingTasks.values()[taskID].GetAmount());
//                    break;
//
//                case 6:
//                    SetText(player, i, DailyTasks.SmithingTasks.values()[taskID].GetTitle(), DailyTasks.SmithingTasks.values()[taskID].GetDescription());
//                    SetAmount(player, i, taskAmountDone, DailyTasks.SmithingTasks.values()[taskID].GetAmount());
//                    break;
//
//                case 7:
//                    SetText(player, i, DailyTasks.FletchingTasks.values()[taskID].GetTitle(), DailyTasks.FletchingTasks.values()[taskID].GetDescription());
//                    SetAmount(player, i, taskAmountDone, DailyTasks.FletchingTasks.values()[taskID].GetAmount());
//                    break;
//
//                case 8:
//                    SetText(player, i, DailyTasks.PrayerTasks.values()[taskID].GetTitle(), DailyTasks.PrayerTasks.values()[taskID].GetDescription());
//                    SetAmount(player, i, taskAmountDone, DailyTasks.PrayerTasks.values()[taskID].GetAmount());
//                    break;
//
//                case 9:
//                    SetText(player, i, DailyTasks.OtherTasks.values()[taskID].GetTitle(), DailyTasks.OtherTasks.values()[taskID].GetDescription());
//                    SetAmount(player, i, taskAmountDone, DailyTasks.OtherTasks.values()[taskID].GetAmount());
//                    break;
//            }
//        }
//
//        //Weekly Reward
//        int taskID = EntityExtKt.getInt(player, Attribute.TASK_ID_FOUR, 0);
//        int taskAmountDone = EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_FOUR, 0);
//        int taskType = EntityExtKt.getInt(player, Attribute.TASK_TYPE_FOUR, 0);
//
//        switch (taskType)
//        {
//            case 1:
//            case 2:
//            case 5:
//                SetText(player, 3, WeeklyTasks.CombatTasks.values()[taskID].GetTitle(), WeeklyTasks.CombatTasks.values()[taskID].GetDescription());
//                SetAmount(player, 3, taskAmountDone, WeeklyTasks.CombatTasks.values()[taskID].GetAmount());
//                break;
//
//            case 3:
//            case 6:
//                SetText(player, 3, WeeklyTasks.FishingTasks.values()[taskID].GetTitle(), WeeklyTasks.FishingTasks.values()[taskID].GetDescription());
//                SetAmount(player, 3, taskAmountDone, WeeklyTasks.FishingTasks.values()[taskID].GetAmount());
//                break;
//
//            case 4:
//            case 7:
//                SetText(player, 3, WeeklyTasks.MiningTasks.values()[taskID].GetTitle(), WeeklyTasks.MiningTasks.values()[taskID].GetDescription());
//                SetAmount(player, 3, taskAmountDone, WeeklyTasks.MiningTasks.values()[taskID].GetAmount());
//                break;
//
//            case 9:
//            case 8:
//                SetText(player, 3, WeeklyTasks.OtherTasks.values()[taskID].GetTitle(), WeeklyTasks.OtherTasks.values()[taskID].GetDescription());
//                SetAmount(player, 3, taskAmountDone, WeeklyTasks.OtherTasks.values()[taskID].GetAmount());
//                break;
//        }
//
//        SetRewardImage(player, 3, weeklyItem);
//    }
//
//    private static void GetRandomDailyTask(Player player) {
//        if (EntityExtKt.passedTime(player, Attribute.TASK_DAILY_TIMER, 1, TimeUnit.DAYS, false, true)) {
//
//            int combatTaskLength = DailyTasks.CombatTasks.values().length;
//            int woodcuttingTaskLength = DailyTasks.WoodcuttingTasks.values().length;
//            int fishingTaskLength = DailyTasks.FishingTasks.values().length;
//            int miningTaskLength = DailyTasks.MiningTasks.values().length;
//            int firemakingTaskLength = DailyTasks.FiremakingTasks.values().length;
//            int smithingTaskLength = DailyTasks.SmithingTasks.values().length;
//            int fletchingTaskLength = DailyTasks.SmithingTasks.values().length;
//            int prayerTaskLength = DailyTasks.SmithingTasks.values().length;
//            int otherTaskLength = DailyTasks.SmithingTasks.values().length;
//
//            int taskTypeOne = random.nextInt(9) + 1;
//            int taskTypeTwo = random.nextInt(9) + 1;
//            int taskTypeThree = random.nextInt(9) + 1;
//
//            EntityExtKt.setInt(player, Attribute.TASK_TYPE_ONE, taskTypeOne, 0);
//            EntityExtKt.setInt(player, Attribute.TASK_TYPE_TWO, taskTypeTwo, 0);
//            EntityExtKt.setInt(player, Attribute.TASK_TYPE_THREE, taskTypeThree, 0);
//
//            int randomTaskIdOne = 0;
//            int randomTaskIdTwo = 0;
//            int randomTaskIdThree = 0;
//
//            switch (taskTypeOne)
//            {
//                case 1:
//                    randomTaskIdOne = random.nextInt(combatTaskLength);
//                    break;
//
//                case 2:
//                    randomTaskIdOne = random.nextInt(woodcuttingTaskLength);
//                    break;
//
//                case 3:
//                    randomTaskIdOne = random.nextInt(fishingTaskLength);
//                    break;
//
//                case 4:
//                    randomTaskIdOne = random.nextInt(miningTaskLength);
//                    break;
//
//                case 5:
//                    randomTaskIdOne = random.nextInt(firemakingTaskLength);
//                    break;
//
//                case 6:
//                    randomTaskIdOne = random.nextInt(smithingTaskLength);
//                    break;
//
//                case 7:
//                    randomTaskIdOne = random.nextInt(fletchingTaskLength);
//                    break;
//
//                case 8:
//                    randomTaskIdOne = random.nextInt(prayerTaskLength);
//                    break;
//
//                case 9:
//                    randomTaskIdOne = random.nextInt(otherTaskLength);
//                    break;
//            }
//
//            switch (taskTypeTwo)
//            {
//                case 1:
//                    randomTaskIdTwo = random.nextInt(combatTaskLength);
//                    break;
//
//                case 2:
//                    randomTaskIdTwo = random.nextInt(woodcuttingTaskLength);
//                    break;
//
//                case 3:
//                    randomTaskIdTwo = random.nextInt(fishingTaskLength);
//                    break;
//
//                case 4:
//                    randomTaskIdTwo = random.nextInt(miningTaskLength);
//                    break;
//
//                case 5:
//                    randomTaskIdTwo = random.nextInt(firemakingTaskLength);
//                    break;
//
//                case 6:
//                    randomTaskIdTwo = random.nextInt(smithingTaskLength);
//                    break;
//
//                case 7:
//                    randomTaskIdTwo = random.nextInt(fletchingTaskLength);
//                    break;
//
//                case 8:
//                    randomTaskIdTwo = random.nextInt(prayerTaskLength);
//                    break;
//
//                case 9:
//                    randomTaskIdTwo = random.nextInt(otherTaskLength);
//                    break;
//            }
//
//            switch (taskTypeThree)
//            {
//                case 1:
//                    randomTaskIdThree = random.nextInt(combatTaskLength);
//                    break;
//
//                case 2:
//                    randomTaskIdThree = random.nextInt(woodcuttingTaskLength);
//                    break;
//
//                case 3:
//                    randomTaskIdThree = random.nextInt(fishingTaskLength);
//                    break;
//
//                case 4:
//                    randomTaskIdThree = random.nextInt(miningTaskLength);
//                    break;
//
//                case 5:
//                    randomTaskIdThree = random.nextInt(firemakingTaskLength);
//                    break;
//
//                case 6:
//                    randomTaskIdThree = random.nextInt(smithingTaskLength);
//                    break;
//
//                case 7:
//                    randomTaskIdThree = random.nextInt(fletchingTaskLength);
//                    break;
//
//                case 8:
//                    randomTaskIdThree = random.nextInt(prayerTaskLength);
//                    break;
//
//                case 9:
//                    randomTaskIdThree = random.nextInt(otherTaskLength);
//                    break;
//            }
//
//            EntityExtKt.setInt(player, Attribute.TASK_ID_ONE, randomTaskIdOne, 0);
//            EntityExtKt.setInt(player, Attribute.TASK_ID_TWO, randomTaskIdTwo, 0);
//            EntityExtKt.setInt(player, Attribute.TASK_ID_THREE, randomTaskIdThree, 0);
//
//            EntityExtKt.setInt(player, Attribute.TASK_AMOUNT_DONE_ONE, 0, 0);
//            EntityExtKt.setInt(player, Attribute.TASK_AMOUNT_DONE_TWO, 0, 0);
//            EntityExtKt.setInt(player, Attribute.TASK_AMOUNT_DONE_THREE, 0, 0);
//
//            EntityExtKt.setBoolean(player, Attribute.TASK_CLAIMED_ONE, false, false);
//            EntityExtKt.setBoolean(player, Attribute.TASK_CLAIMED_TWO, false, false);
//            EntityExtKt.setBoolean(player, Attribute.TASK_CLAIMED_THREE, false, false);
//
//        }
//    }
//
//    private static void GetRandomWeeklyTask(Player player) {
//        if (EntityExtKt.passedTime(player, Attribute.TASK_WEEKLY_TIMER, 7, TimeUnit.DAYS, false, true)) {
//
//            int combatTaskLength = DailyTasks.CombatTasks.values().length;
//            int fishingTaskLength = DailyTasks.FishingTasks.values().length;
//            int miningTaskLength = DailyTasks.MiningTasks.values().length;
//            int otherTaskLength = DailyTasks.SmithingTasks.values().length;
//
//            int taskTypeFour = random.nextInt(9) + 1;
//
//            if (taskTypeFour == 8)
//                taskTypeFour = 9;
//
//            EntityExtKt.setInt(player, Attribute.TASK_TYPE_FOUR, taskTypeFour, 0);
//            int randomTaskIdFour = 0;
//
//            switch (taskTypeFour)
//            {
//                case 1:
//                case 2:
//                case 5:
//                    randomTaskIdFour = random.nextInt(combatTaskLength);
//                    break;
//
//                case 3:
//                case 6:
//                    randomTaskIdFour = random.nextInt(fishingTaskLength);
//                    break;
//
//                case 4:
//                case 7:
//                    randomTaskIdFour = random.nextInt(miningTaskLength);
//                    break;
//
//                case 9:
//                case 8:
//                    randomTaskIdFour = random.nextInt(otherTaskLength);
//                    break;
//            }
//
//            EntityExtKt.setInt(player, Attribute.TASK_ID_FOUR, randomTaskIdFour, 0);
//
//            EntityExtKt.setInt(player, Attribute.TASK_AMOUNT_DONE_FOUR, 0, 0);
//
//            EntityExtKt.setBoolean(player, Attribute.TASK_CLAIMED_FOUR, false, false);
//
//        }
//    }
//
//    public static void ProgressCombatTask(Player player, NPC npc) {
//        //check if combat task...
//        int taskIdOne = EntityExtKt.getInt(player, Attribute.TASK_ID_ONE, 0);
//        int taskIdTwo = EntityExtKt.getInt(player, Attribute.TASK_ID_TWO, 0);
//        int taskIdThree = EntityExtKt.getInt(player, Attribute.TASK_ID_THREE, 0);
//        int taskIdFour = EntityExtKt.getInt(player, Attribute.TASK_ID_FOUR, 0);
//
//        if (EntityExtKt.getInt(player, Attribute.TASK_TYPE_ONE, 0) == 1) {
//            if (DailyTasks.CombatTasks.values()[taskIdOne].getName().equalsIgnoreCase(npc.fetchDefinition().getName())) {
//                if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_ONE, 0)+1 == DailyTasks.CombatTasks.values()[taskIdOne].GetAmount())
//                {
//                    player.sendMessage("@dre@Your daily task is now complete!");
//                }
//                //Increase task 1
//                EntityExtKt.incInt(player, Attribute.TASK_AMOUNT_DONE_ONE, 1, DailyTasks.CombatTasks.values()[taskIdOne].GetAmount(), 0);
//            }
//        }
//        if (EntityExtKt.getInt(player, Attribute.TASK_TYPE_TWO, 0) == 1) {
//            if (DailyTasks.CombatTasks.values()[taskIdTwo].getName().equalsIgnoreCase(npc.fetchDefinition().getName())) {
//                if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_TWO, 0)+1 == DailyTasks.CombatTasks.values()[taskIdTwo].GetAmount())
//                {
//                    player.sendMessage("@dre@Your daily task is now complete!");
//                }
//                //Increase task 1
//                EntityExtKt.incInt(player, Attribute.TASK_AMOUNT_DONE_TWO, 1, DailyTasks.CombatTasks.values()[taskIdTwo].GetAmount(), 0);
//            }
//        }
//        if (EntityExtKt.getInt(player, Attribute.TASK_TYPE_THREE, 0) == 1) {
//            if (DailyTasks.CombatTasks.values()[taskIdThree].getName().equalsIgnoreCase(npc.fetchDefinition().getName())) {
//                if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_THREE, 0)+1 == DailyTasks.CombatTasks.values()[taskIdThree].GetAmount())
//                {
//                    player.sendMessage("@dre@Your daily task is now complete!");
//                }
//                //Increase task 1
//                EntityExtKt.incInt(player, Attribute.TASK_AMOUNT_DONE_THREE, 1, DailyTasks.CombatTasks.values()[taskIdThree].GetAmount(), 0);
//            }
//        }
//        if (EntityExtKt.getInt(player, Attribute.TASK_TYPE_FOUR, 0) == 1) {
//            if (WeeklyTasks.CombatTasks.values()[taskIdFour].getName().equalsIgnoreCase(npc.fetchDefinition().getName())) {
//                if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_FOUR, 0)+1 == WeeklyTasks.CombatTasks.values()[taskIdFour].GetAmount())
//                {
//                    player.sendMessage("@dre@Your weekly task is now complete!");
//                }
//                //Increase task 1
//                EntityExtKt.incInt(player, Attribute.TASK_AMOUNT_DONE_FOUR, 1, WeeklyTasks.CombatTasks.values()[taskIdFour].GetAmount(), 0);
//            }
//        }
//        /*
//        if (DailyTasks.CombatTasks.values()[taskIdOne].getName().equalsIgnoreCase(npc.fetchDefinition().getName())) {
//            //Increase task 1
//            EntityExtKt.incInt(player, Attribute.TASK_AMOUNT_DONE_ONE, 1, DailyTasks.CombatTasks.values()[taskIdOne].GetAmount(), 0);
//        }*/
//
//    }
//
//    public static void ProgressTask(Player player, int taskType, int taskId)
//    {
//        int taskIdOne = EntityExtKt.getInt(player, Attribute.TASK_ID_ONE, 0);
//        int taskIdTwo = EntityExtKt.getInt(player, Attribute.TASK_ID_TWO, 0);
//        int taskIdThree = EntityExtKt.getInt(player, Attribute.TASK_ID_THREE, 0);
//        int taskIdFour = EntityExtKt.getInt(player, Attribute.TASK_ID_FOUR, 0);
//
//        //add messages
//        /*if (taskName.equalsIgnoreCase(taskItemString)) {
//                if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_FOUR, 0)+1 == maxAmount)
//                {
//                    player.sendMessage("@dre@Your daily task is complete!");
//                }
//
//                EntityExtKt.incInt(player, Attribute.TASK_AMOUNT_DONE_FOUR, 1, DailyTasks.CombatTasks.values()[taskIdFour].GetAmount(), 0);
//            }*/
//        if (EntityExtKt.getInt(player, Attribute.TASK_TYPE_ONE, 0) == taskType) {
//            if (taskIdOne == taskId) {
//                if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_ONE, 0)+1 == DailyTasks.CombatTasks.values()[taskIdOne].GetAmount())
//                {
//                    player.sendMessage("@dre@Your daily task is now complete!");
//                }
//                //Increase task 1
//                EntityExtKt.incInt(player, Attribute.TASK_AMOUNT_DONE_ONE, 1, DailyTasks.CombatTasks.values()[taskIdOne].GetAmount(), 0);
//            }
//        }
//        if (EntityExtKt.getInt(player, Attribute.TASK_TYPE_TWO, 0) == taskType) {
//            if (taskIdTwo == taskId) {
//                if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_TWO, 0)+1 == DailyTasks.CombatTasks.values()[taskIdTwo].GetAmount())
//                {
//                    player.sendMessage("@dre@Your daily task is now complete!");
//                }
//                //Increase task 1
//                EntityExtKt.incInt(player, Attribute.TASK_AMOUNT_DONE_TWO, 1, DailyTasks.CombatTasks.values()[taskIdTwo].GetAmount(), 0);
//            }
//        }
//        if (EntityExtKt.getInt(player, Attribute.TASK_TYPE_THREE, 0) == taskType) {
//            if (taskIdThree == taskId) {
//                if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_THREE, 0)+1 == DailyTasks.CombatTasks.values()[taskIdThree].GetAmount())
//                {
//                    player.sendMessage("@dre@Your daily task is now complete!");
//                }
//
//                //Increase task 1
//                EntityExtKt.incInt(player, Attribute.TASK_AMOUNT_DONE_THREE, 1, DailyTasks.CombatTasks.values()[taskIdThree].GetAmount(), 0);
//            }
//        }
//        if (EntityExtKt.getInt(player, Attribute.TASK_TYPE_FOUR, 0) == taskType) {
//            if (taskIdFour == taskId) {
//                if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_FOUR, 0)+1 == WeeklyTasks.CombatTasks.values()[taskIdFour].GetAmount())
//                {
//                    player.sendMessage("@dre@Your weekly task is now complete!");
//                }
//                //Increase task 1
//                EntityExtKt.incInt(player, Attribute.TASK_AMOUNT_DONE_FOUR, 1, WeeklyTasks.CombatTasks.values()[taskIdFour].GetAmount(), 0);
//            }
//        }
//    }
//
//    public static void ProgressTask(Player player, int taskType, int taskId, int amount)
//    {
//        int taskIdOne = EntityExtKt.getInt(player, Attribute.TASK_ID_ONE, 0);
//        int taskIdTwo = EntityExtKt.getInt(player, Attribute.TASK_ID_TWO, 0);
//        int taskIdThree = EntityExtKt.getInt(player, Attribute.TASK_ID_THREE, 0);
//        int taskIdFour = EntityExtKt.getInt(player, Attribute.TASK_ID_FOUR, 0);
//
//        //add messages
//        /*if (taskName.equalsIgnoreCase(taskItemString)) {
//                if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_FOUR, 0)+1 == maxAmount)
//                {
//                    player.sendMessage("@dre@Your daily task is complete!");
//                }
//
//                EntityExtKt.incInt(player, Attribute.TASK_AMOUNT_DONE_FOUR, 1, DailyTasks.CombatTasks.values()[taskIdFour].GetAmount(), 0);
//            }*/
//        if (EntityExtKt.getInt(player, Attribute.TASK_TYPE_ONE, 0) == taskType) {
//            if (taskIdOne == taskId) {
//                if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_ONE, 0)+amount >= DailyTasks.CombatTasks.values()[taskIdOne].GetAmount())
//                {
//                    player.sendMessage("@dre@Your daily task is now complete!");
//                }
//                //Increase task 1
//                EntityExtKt.incInt(player, Attribute.TASK_AMOUNT_DONE_ONE, amount, DailyTasks.CombatTasks.values()[taskIdOne].GetAmount(), 0);
//            }
//        }
//        if (EntityExtKt.getInt(player, Attribute.TASK_TYPE_TWO, 0) == taskType) {
//            if (taskIdTwo == taskId) {
//                if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_TWO, 0)+amount >= DailyTasks.CombatTasks.values()[taskIdTwo].GetAmount())
//                {
//                    player.sendMessage("@dre@Your daily task is now complete!");
//                }
//                //Increase task 1
//                EntityExtKt.incInt(player, Attribute.TASK_AMOUNT_DONE_TWO, amount, DailyTasks.CombatTasks.values()[taskIdTwo].GetAmount(), 0);
//            }
//        }
//        if (EntityExtKt.getInt(player, Attribute.TASK_TYPE_THREE, 0) == taskType) {
//            if (taskIdThree == taskId) {
//                if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_THREE, 0)+amount >= DailyTasks.CombatTasks.values()[taskIdThree].GetAmount())
//                {
//                    player.sendMessage("@dre@Your daily task is now complete!");
//                }
//
//                //Increase task 1
//                EntityExtKt.incInt(player, Attribute.TASK_AMOUNT_DONE_THREE, amount, DailyTasks.CombatTasks.values()[taskIdThree].GetAmount(), 0);
//            }
//        }
//        if (EntityExtKt.getInt(player, Attribute.TASK_TYPE_FOUR, 0) == taskType) {
//            if (taskIdFour == taskId) {
//                if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_FOUR, 0)+amount >= WeeklyTasks.CombatTasks.values()[taskIdFour].GetAmount())
//                {
//                    player.sendMessage("@dre@Your weekly task is now complete!");
//                }
//                //Increase task 1
//                EntityExtKt.incInt(player, Attribute.TASK_AMOUNT_DONE_FOUR, amount, WeeklyTasks.CombatTasks.values()[taskIdFour].GetAmount(), 0);
//            }
//        }
//    }
//
//    public static void ProgressTask(Player player, int taskType, String taskItemString)
//    {
//        int taskIdOne = EntityExtKt.getInt(player, Attribute.TASK_ID_ONE, 0);
//        int taskIdTwo = EntityExtKt.getInt(player, Attribute.TASK_ID_TWO, 0);
//        int taskIdThree = EntityExtKt.getInt(player, Attribute.TASK_ID_THREE, 0);
//        int taskIdFour = EntityExtKt.getInt(player, Attribute.TASK_ID_FOUR, 0);
//
//        taskItemString = taskItemString.replaceAll("_", " ");
//        //System.out.println(taskItemString + " - " + taskType);
//
//        if (EntityExtKt.getInt(player, Attribute.TASK_TYPE_ONE, 0) == taskType) {
//            String taskName = "";
//            int maxAmount = 100;
//
//            switch(taskType)
//            {
//                case 2:
//                    taskName = DailyTasks.WoodcuttingTasks.values()[taskIdOne].toString();
//                    maxAmount = DailyTasks.WoodcuttingTasks.values()[taskIdOne].GetAmount();
//                    break;
//
//                case 3:
//                    taskName = DailyTasks.FishingTasks.values()[taskIdOne].toString();
//                    maxAmount = DailyTasks.FishingTasks.values()[taskIdOne].GetAmount();
//                    break;
//
//                case 4:
//                    taskName = DailyTasks.MiningTasks.values()[taskIdOne].toString();
//                    maxAmount = DailyTasks.MiningTasks.values()[taskIdOne].GetAmount();
//                    break;
//
//                case 5:
//                    taskName = DailyTasks.FiremakingTasks.values()[taskIdOne].toString();
//                    maxAmount = DailyTasks.FiremakingTasks.values()[taskIdOne].GetAmount();
//                    break;
//
//                case 6:
//                    taskName = DailyTasks.SmithingTasks.values()[taskIdOne].toString();
//                    maxAmount = DailyTasks.SmithingTasks.values()[taskIdOne].GetAmount();
//                    break;
//
//                case 7:
//                    taskName = DailyTasks.FletchingTasks.values()[taskIdOne].toString();
//                    maxAmount = DailyTasks.FletchingTasks.values()[taskIdOne].GetAmount();
//                    break;
//
//                case 8:
//                    taskName = DailyTasks.PrayerTasks.values()[taskIdOne].toString();
//                    maxAmount = DailyTasks.PrayerTasks.values()[taskIdOne].GetAmount();
//                    break;
//            }
//            taskName = taskName.replaceAll("_", " ");
//
//            //Increase task 1
//            if (taskName.equalsIgnoreCase(taskItemString)) {
//                if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_ONE, 0)+1 == maxAmount)
//                {
//                    player.sendMessage("@dre@Your daily task is now complete!");
//                }
//                EntityExtKt.incInt(player, Attribute.TASK_AMOUNT_DONE_ONE, 1, maxAmount, 0);
//            }
//        }
//        if (EntityExtKt.getInt(player, Attribute.TASK_TYPE_TWO, 0) == taskType) {
//            String taskName = "";
//            int maxAmount = 100;
//
//            switch(taskType)
//            {
//                case 2:
//                    taskName = DailyTasks.WoodcuttingTasks.values()[taskIdTwo].toString();
//                    maxAmount = DailyTasks.WoodcuttingTasks.values()[taskIdTwo].GetAmount();
//                    break;
//
//                case 3:
//                    taskName = DailyTasks.FishingTasks.values()[taskIdTwo].toString();
//                    maxAmount = DailyTasks.FishingTasks.values()[taskIdTwo].GetAmount();
//                    break;
//
//                case 4:
//                    taskName = DailyTasks.MiningTasks.values()[taskIdTwo].toString();
//                    maxAmount = DailyTasks.MiningTasks.values()[taskIdTwo].GetAmount();
//                    break;
//
//                case 5:
//                    taskName = DailyTasks.FiremakingTasks.values()[taskIdTwo].toString();
//                    maxAmount = DailyTasks.FiremakingTasks.values()[taskIdTwo].GetAmount();
//                    break;
//
//                case 6:
//                    taskName = DailyTasks.SmithingTasks.values()[taskIdTwo].toString();
//                    maxAmount = DailyTasks.SmithingTasks.values()[taskIdTwo].GetAmount();
//                    break;
//
//                case 7:
//                    taskName = DailyTasks.FletchingTasks.values()[taskIdTwo].toString();
//                    maxAmount = DailyTasks.FletchingTasks.values()[taskIdTwo].GetAmount();
//                    break;
//
//                case 8:
//                    taskName = DailyTasks.PrayerTasks.values()[taskIdTwo].toString();
//                    maxAmount = DailyTasks.PrayerTasks.values()[taskIdTwo].GetAmount();
//                    break;
//            }
//
//            taskName = taskName.replaceAll("_", " ");
//
//            //Increase task 2
//            if (taskName.equalsIgnoreCase(taskItemString)) {
//                if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_TWO, 0)+1 == maxAmount)
//                {
//                    player.sendMessage("@dre@Your daily task is now complete!");
//                }
//                EntityExtKt.incInt(player, Attribute.TASK_AMOUNT_DONE_TWO, 1, maxAmount, 0);
//
//
//            }
//        }
//        if (EntityExtKt.getInt(player, Attribute.TASK_TYPE_THREE, 0) == taskType) {
//
//            String taskName = "";
//            int maxAmount = 100;
//
//            switch(taskType)
//            {
//                case 2:
//                    taskName = DailyTasks.WoodcuttingTasks.values()[taskIdThree].toString();
//                    maxAmount = DailyTasks.WoodcuttingTasks.values()[taskIdThree].GetAmount();
//                    break;
//
//                case 3:
//                    taskName = DailyTasks.FishingTasks.values()[taskIdThree].toString();
//                    maxAmount = DailyTasks.FishingTasks.values()[taskIdThree].GetAmount();
//                    break;
//
//                case 4:
//                    taskName = DailyTasks.MiningTasks.values()[taskIdThree].toString();
//                    maxAmount = DailyTasks.MiningTasks.values()[taskIdThree].GetAmount();
//                    break;
//
//                case 5:
//                    taskName = DailyTasks.FiremakingTasks.values()[taskIdThree].toString();
//                    maxAmount = DailyTasks.FiremakingTasks.values()[taskIdThree].GetAmount();
//                    break;
//
//                case 6:
//                    taskName = DailyTasks.SmithingTasks.values()[taskIdThree].toString();
//                    maxAmount = DailyTasks.SmithingTasks.values()[taskIdThree].GetAmount();
//                    break;
//
//                case 7:
//                    taskName = DailyTasks.FletchingTasks.values()[taskIdThree].toString();
//                    maxAmount = DailyTasks.FletchingTasks.values()[taskIdThree].GetAmount();
//                    break;
//
//                case 8:
//                    taskName = DailyTasks.PrayerTasks.values()[taskIdThree].toString();
//                    maxAmount = DailyTasks.PrayerTasks.values()[taskIdThree].GetAmount();
//                    break;
//            }
//            taskName = taskName.replaceAll("_", " ");
//
//            //Increase task 1
//            if (taskName.equalsIgnoreCase(taskItemString)) {
//                if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_THREE, 0)+1 == maxAmount)
//                {
//                    player.sendMessage("@dre@Your daily task is now complete!");
//                }
//
//                EntityExtKt.incInt(player, Attribute.TASK_AMOUNT_DONE_THREE, 1, maxAmount, 0);
//            }
//        }
//        if (EntityExtKt.getInt(player, Attribute.TASK_TYPE_FOUR, 0) == taskType) {
//
//            String taskName = "";
//            int maxAmount = 100;
//
//            switch(taskType)
//            {
//                case 3:
//                case 6:
//                    taskName = WeeklyTasks.FishingTasks.values()[taskIdFour].toString();
//                    maxAmount = WeeklyTasks.FishingTasks.values()[taskIdFour].GetAmount();
//                    break;
//
//                case 4:
//                case 7:
//                    taskName = WeeklyTasks.MiningTasks.values()[taskIdFour].toString();
//                    maxAmount = WeeklyTasks.MiningTasks.values()[taskIdFour].GetAmount();
//                    break;
//            }
//            taskName = taskName.replaceAll("_", " ");
//
//            //Increase task 1
//            if (taskName.equalsIgnoreCase(taskItemString)) {
//                if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_FOUR, 0)+1 == maxAmount)
//                {
//                    player.sendMessage("@dre@Your weekly task is now complete!");
//                }
//
//                EntityExtKt.incInt(player, Attribute.TASK_AMOUNT_DONE_FOUR, 1, maxAmount, 0);
//            }
//        }
//    }
//
//    public static void ClaimReward(Player player, int buttonId)
//    {
//        int taskIdOne = EntityExtKt.getInt(player, Attribute.TASK_ID_ONE, 0);
//        int taskIdTwo = EntityExtKt.getInt(player, Attribute.TASK_ID_TWO, 0);
//        int taskIdThree = EntityExtKt.getInt(player, Attribute.TASK_ID_THREE, 0);
//        int taskIdFour = EntityExtKt.getInt(player, Attribute.TASK_ID_FOUR, 0);
//
//        int taskTypeOne = EntityExtKt.getInt(player, Attribute.TASK_TYPE_ONE, 0);
//        int taskTypeTwo = EntityExtKt.getInt(player, Attribute.TASK_TYPE_TWO, 0);
//        int taskTypeThree = EntityExtKt.getInt(player, Attribute.TASK_TYPE_THREE, 0);
//        int taskTypeFour = EntityExtKt.getInt(player, Attribute.TASK_TYPE_FOUR, 0);
//
//        int maxAmount = 100;
//
//        switch (buttonId)
//        {
//            case 24517:
//                switch(taskTypeOne)
//                {
//                    case 1:
//                        maxAmount = DailyTasks.CombatTasks.values()[taskIdOne].GetAmount();
//                        break;
//
//                    case 2:
//                        maxAmount = DailyTasks.WoodcuttingTasks.values()[taskIdOne].GetAmount();
//                        break;
//
//                    case 3:
//                        maxAmount = DailyTasks.FishingTasks.values()[taskIdOne].GetAmount();
//                        break;
//
//                    case 4:
//                        maxAmount = DailyTasks.MiningTasks.values()[taskIdOne].GetAmount();
//                        break;
//
//                    case 5:
//                        maxAmount = DailyTasks.FiremakingTasks.values()[taskIdOne].GetAmount();
//                        break;
//
//                    case 6:
//                        maxAmount = DailyTasks.SmithingTasks.values()[taskIdOne].GetAmount();
//                        break;
//
//                    case 7:
//                        maxAmount = DailyTasks.FletchingTasks.values()[taskIdOne].GetAmount();
//                        break;
//
//                    case 8:
//                        maxAmount = DailyTasks.PrayerTasks.values()[taskIdOne].GetAmount();
//                        break;
//
//                    case 9:
//                        maxAmount = DailyTasks.OtherTasks.values()[taskIdOne].GetAmount();
//                        break;
//                }
//
//                if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_ONE, 0) == maxAmount) {
//                    if (!EntityExtKt.getBoolean(player, Attribute.TASK_CLAIMED_ONE, false)) {
//                        Item item = new Item(EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_ID_ONE, 0), EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_ONE, 0));
//
//                        ItemContainerUtil.addOrDrop(player.getInventory(), player, item);
//                        player.sendMessage("@dre@You have been awarded " + Misc.format(item.getAmount()) + " " + item.getDefinition().getName() + " for completing the daily task.");
//                        EntityExtKt.setBoolean(player, Attribute.TASK_CLAIMED_ONE, true, false);
//                        player.getPacketSender().sendString(24537, "@gre@Claimed");
//                    } else {
//                        player.sendMessage("@dre@You have already claimed your daily task reward!");
//                    }
//                } else {
//                    player.sendMessage("@dre@You haven't complete this task yet!");
//                }
//                break;
//
//            case 24520:
//                switch(taskTypeTwo)
//                {
//                    case 1:
//                        maxAmount = DailyTasks.CombatTasks.values()[taskIdTwo].GetAmount();
//                        break;
//
//                    case 2:
//                        maxAmount = DailyTasks.WoodcuttingTasks.values()[taskIdTwo].GetAmount();
//                        break;
//
//                    case 3:
//                        maxAmount = DailyTasks.FishingTasks.values()[taskIdTwo].GetAmount();
//                        break;
//
//                    case 4:
//                        maxAmount = DailyTasks.MiningTasks.values()[taskIdTwo].GetAmount();
//                        break;
//
//                    case 5:
//                        maxAmount = DailyTasks.FiremakingTasks.values()[taskIdTwo].GetAmount();
//                        break;
//
//                    case 6:
//                        maxAmount = DailyTasks.SmithingTasks.values()[taskIdTwo].GetAmount();
//                        break;
//
//                    case 7:
//                        maxAmount = DailyTasks.FletchingTasks.values()[taskIdTwo].GetAmount();
//                        break;
//
//                    case 8:
//                        maxAmount = DailyTasks.PrayerTasks.values()[taskIdTwo].GetAmount();
//                        break;
//
//                    case 9:
//                        maxAmount = DailyTasks.OtherTasks.values()[taskIdTwo].GetAmount();
//                        break;
//                }
//
//                if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_TWO, 0) == maxAmount) {
//                    if (!EntityExtKt.getBoolean(player, Attribute.TASK_CLAIMED_TWO, false)) {
//                        Item item = new Item(EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_ID_TWO, 0), EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_TWO, 0));
//
//                        ItemContainerUtil.addOrDrop(player.getInventory(), player, item);
//                        player.sendMessage("@dre@You have been awarded " + Misc.format(item.getAmount()) + " " + item.getDefinition().getName() + " for completing the daily task.");
//                        EntityExtKt.setBoolean(player, Attribute.TASK_CLAIMED_TWO, true, false);
//                        player.getPacketSender().sendString(24538, "@gre@Claimed");
//                    } else {
//                        player.sendMessage("@dre@You have already claimed your daily task reward!");
//                    }
//                } else {
//                    player.sendMessage("@dre@You haven't complete this task yet!");
//                }
//                break;
//
//            case 24523:
//                switch(taskTypeThree)
//                {
//                    case 1:
//                        maxAmount = DailyTasks.CombatTasks.values()[taskIdThree].GetAmount();
//                        break;
//
//                    case 2:
//                        maxAmount = DailyTasks.WoodcuttingTasks.values()[taskIdThree].GetAmount();
//                        break;
//
//                    case 3:
//                        maxAmount = DailyTasks.FishingTasks.values()[taskIdThree].GetAmount();
//                        break;
//
//                    case 4:
//                        maxAmount = DailyTasks.MiningTasks.values()[taskIdThree].GetAmount();
//                        break;
//
//                    case 5:
//                        maxAmount = DailyTasks.FiremakingTasks.values()[taskIdThree].GetAmount();
//                        break;
//
//                    case 6:
//                        maxAmount = DailyTasks.SmithingTasks.values()[taskIdThree].GetAmount();
//                        break;
//
//                    case 7:
//                        maxAmount = DailyTasks.FletchingTasks.values()[taskIdThree].GetAmount();
//                        break;
//
//                    case 8:
//                        maxAmount = DailyTasks.PrayerTasks.values()[taskIdThree].GetAmount();
//                        break;
//
//                    case 9:
//                        maxAmount = DailyTasks.OtherTasks.values()[taskIdThree].GetAmount();
//                        break;
//                }
//
//                if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_THREE, 0) == maxAmount) {
//                    if (!EntityExtKt.getBoolean(player, Attribute.TASK_CLAIMED_THREE, false)) {
//                        Item item = new Item(EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_ID_THREE, 0), EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_THREE, 0));
//
//                        ItemContainerUtil.addOrDrop(player.getInventory(), player, item);
//                        player.sendMessage("@dre@You have been awarded " + Misc.format(item.getAmount()) + " " + item.getDefinition().getName() + " for completing the daily task.");
//                        EntityExtKt.setBoolean(player, Attribute.TASK_CLAIMED_THREE, true, false);
//                        player.getPacketSender().sendString(24539, "@gre@Claimed");
//                    } else {
//                        player.sendMessage("@dre@You have already claimed your daily task reward!");
//                    }
//                } else {
//                    player.sendMessage("@dre@You haven't complete this task yet!");
//                }
//                break;
//
//            case 24526:
//                switch(taskTypeFour)
//                {
//                    case 1:
//                    case 2:
//                    case 5:
//                        maxAmount = WeeklyTasks.CombatTasks.values()[taskIdFour].GetAmount();
//                        break;
//
//                    case 3:
//                    case 6:
//                        maxAmount = WeeklyTasks.FishingTasks.values()[taskIdFour].GetAmount();
//                        break;
//
//                    case 4:
//                    case 7:
//                        maxAmount = WeeklyTasks.MiningTasks.values()[taskIdFour].GetAmount();
//                        break;
//
//                    case 9:
//                    case 8:
//                        maxAmount = WeeklyTasks.OtherTasks.values()[taskIdFour].GetAmount();
//                        break;
//                }
//
//                if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_FOUR, 0) == maxAmount) {
//                    if (!EntityExtKt.getBoolean(player, Attribute.TASK_CLAIMED_FOUR, false)) {
//                        Item item = new Item(EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_ID_FOUR, 0), EntityExtKt.getInt(player, Attribute.TASK_REWARD_ITEM_AMOUNT_FOUR, 0));
//
//                        ItemContainerUtil.addOrDrop(player.getInventory(), player, item);
//                        player.sendMessage("@dre@You have been awarded " + Misc.format(item.getAmount()) + " " + item.getDefinition().getName() + " for completing the daily task.");
//                        EntityExtKt.setBoolean(player, Attribute.TASK_CLAIMED_FOUR, true, false);
//                        player.getPacketSender().sendString(24540, "@gre@Claimed");
//                    } else {
//                        player.sendMessage("@dre@You have already claimed your daily task reward!");
//                    }
//                } else {
//                    player.sendMessage("@dre@You haven't complete this task yet!");
//                }
//                break;
//        }
//    }
//
//    public static void SetClaimColour(Player player)
//    {
//        int taskIdOne = EntityExtKt.getInt(player, Attribute.TASK_ID_ONE, 0);
//        int taskIdTwo = EntityExtKt.getInt(player, Attribute.TASK_ID_TWO, 0);
//        int taskIdThree = EntityExtKt.getInt(player, Attribute.TASK_ID_THREE, 0);
//        int taskIdFour = EntityExtKt.getInt(player, Attribute.TASK_ID_FOUR, 0);
//
//        int taskTypeOne = EntityExtKt.getInt(player, Attribute.TASK_TYPE_ONE, 0);
//        int taskTypeTwo = EntityExtKt.getInt(player, Attribute.TASK_TYPE_TWO, 0);
//        int taskTypeThree = EntityExtKt.getInt(player, Attribute.TASK_TYPE_THREE, 0);
//        int taskTypeFour = EntityExtKt.getInt(player, Attribute.TASK_TYPE_FOUR, 0);
//
//        int maxAmount = 100;
//
//        switch(taskTypeOne)
//        {
//            case 1:
//                maxAmount = DailyTasks.CombatTasks.values()[taskIdOne].GetAmount();
//                break;
//
//            case 2:
//                maxAmount = DailyTasks.WoodcuttingTasks.values()[taskIdOne].GetAmount();
//                break;
//
//            case 3:
//                maxAmount = DailyTasks.FishingTasks.values()[taskIdOne].GetAmount();
//                break;
//
//            case 4:
//                maxAmount = DailyTasks.MiningTasks.values()[taskIdOne].GetAmount();
//                break;
//
//            case 5:
//                maxAmount = DailyTasks.FiremakingTasks.values()[taskIdOne].GetAmount();
//                break;
//
//            case 6:
//                maxAmount = DailyTasks.SmithingTasks.values()[taskIdOne].GetAmount();
//                break;
//
//            case 7:
//                maxAmount = DailyTasks.FletchingTasks.values()[taskIdOne].GetAmount();
//                break;
//
//            case 8:
//                maxAmount = DailyTasks.PrayerTasks.values()[taskIdOne].GetAmount();
//                break;
//
//            case 9:
//                maxAmount = DailyTasks.OtherTasks.values()[taskIdOne].GetAmount();
//                break;
//        }
//
//        if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_ONE, 0) > 0 && EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_ONE, 0) < maxAmount) {
//            player.getPacketSender().sendString(24537, "@or1@Claim");
//        } else if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_ONE, 0) == maxAmount) {
//            if (!EntityExtKt.getBoolean(player, Attribute.TASK_CLAIMED_ONE, false)) {
//                player.getPacketSender().sendString(24537, "@gre@Claim");
//            } else {
//                player.getPacketSender().sendString(24537, "@gre@Claimed");
//            }
//        } else {
//            player.getPacketSender().sendString(24537, "@red@Claim");
//        }
//
//
//        switch(taskTypeTwo)
//        {
//            case 1:
//                maxAmount = DailyTasks.CombatTasks.values()[taskIdTwo].GetAmount();
//                break;
//
//            case 2:
//                maxAmount = DailyTasks.WoodcuttingTasks.values()[taskIdTwo].GetAmount();
//                break;
//
//            case 3:
//                maxAmount = DailyTasks.FishingTasks.values()[taskIdTwo].GetAmount();
//                break;
//
//            case 4:
//                maxAmount = DailyTasks.MiningTasks.values()[taskIdTwo].GetAmount();
//                break;
//
//            case 5:
//                maxAmount = DailyTasks.FiremakingTasks.values()[taskIdTwo].GetAmount();
//                break;
//
//            case 6:
//                maxAmount = DailyTasks.SmithingTasks.values()[taskIdTwo].GetAmount();
//                break;
//
//            case 7:
//                maxAmount = DailyTasks.FletchingTasks.values()[taskIdTwo].GetAmount();
//                break;
//
//            case 8:
//                maxAmount = DailyTasks.PrayerTasks.values()[taskIdTwo].GetAmount();
//                break;
//
//            case 9:
//                maxAmount = DailyTasks.OtherTasks.values()[taskIdTwo].GetAmount();
//                break;
//        }
//
//        if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_TWO, 0) > 0 && EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_TWO, 0) < maxAmount) {
//            player.getPacketSender().sendString(24538, "@or1@Claim");
//        } else if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_TWO, 0) == maxAmount) {
//            if (!EntityExtKt.getBoolean(player, Attribute.TASK_CLAIMED_TWO, false)) {
//                player.getPacketSender().sendString(24538, "@gre@Claim");
//            } else {
//                player.getPacketSender().sendString(24538, "@gre@Claimed");
//            }
//        } else {
//            player.getPacketSender().sendString(24538, "@red@Claim");
//        }
//
//        switch(taskTypeThree)
//        {
//            case 1:
//                maxAmount = DailyTasks.CombatTasks.values()[taskIdThree].GetAmount();
//                break;
//
//            case 2:
//                maxAmount = DailyTasks.WoodcuttingTasks.values()[taskIdThree].GetAmount();
//                break;
//
//            case 3:
//                maxAmount = DailyTasks.FishingTasks.values()[taskIdThree].GetAmount();
//                break;
//
//            case 4:
//                maxAmount = DailyTasks.MiningTasks.values()[taskIdThree].GetAmount();
//                break;
//
//            case 5:
//                maxAmount = DailyTasks.FiremakingTasks.values()[taskIdThree].GetAmount();
//                break;
//
//            case 6:
//                maxAmount = DailyTasks.SmithingTasks.values()[taskIdThree].GetAmount();
//                break;
//
//            case 7:
//                maxAmount = DailyTasks.FletchingTasks.values()[taskIdThree].GetAmount();
//                break;
//
//            case 8:
//                maxAmount = DailyTasks.PrayerTasks.values()[taskIdThree].GetAmount();
//                break;
//
//            case 9:
//                maxAmount = DailyTasks.OtherTasks.values()[taskIdThree].GetAmount();
//                break;
//        }
//
//        if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_THREE, 0) > 0 && EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_THREE, 0) < maxAmount) {
//            player.getPacketSender().sendString(24539, "@or1@Claim");
//        } else if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_THREE, 0) == maxAmount) {
//            if (!EntityExtKt.getBoolean(player, Attribute.TASK_CLAIMED_THREE, false)) {
//                player.getPacketSender().sendString(24539, "@gre@Claim");
//            } else {
//                player.getPacketSender().sendString(24539, "@gre@Claimed");
//            }
//        } else {
//            player.getPacketSender().sendString(24539, "@red@Claim");
//        }
//
//        switch(taskTypeFour)
//        {
//            case 1:
//            case 2:
//            case 5:
//                maxAmount = WeeklyTasks.CombatTasks.values()[taskIdFour].GetAmount();
//                break;
//
//            case 3:
//            case 6:
//                maxAmount = WeeklyTasks.FishingTasks.values()[taskIdFour].GetAmount();
//                break;
//
//            case 4:
//            case 7:
//                maxAmount = WeeklyTasks.MiningTasks.values()[taskIdFour].GetAmount();
//                break;
//
//            case 9:
//            case 8:
//                maxAmount = WeeklyTasks.OtherTasks.values()[taskIdFour].GetAmount();
//                break;
//        }
//
//        if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_FOUR, 0) > 0 && EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_FOUR, 0) < maxAmount) {
//            player.getPacketSender().sendString(24540, "@or1@Claim");
//        } else if (EntityExtKt.getInt(player, Attribute.TASK_AMOUNT_DONE_FOUR, 0) == maxAmount) {
//            if (!EntityExtKt.getBoolean(player, Attribute.TASK_CLAIMED_FOUR, false)) {
//
//                player.getPacketSender().sendString(24540, "@gre@Claim");
//            } else {
//                player.getPacketSender().sendString(24540, "@gre@Claimed");
//            }
//        } else {
//            player.getPacketSender().sendString(24540, "@red@Claim");
//        }
//    }
//
//    private static Item GetRandomDailyItem(Player player)
//    {
//        if (EntityExtKt.passedTime(player, Attribute.TASK_DAILY_TIMER, 1, TimeUnit.DAYS, false, false)) {
//            int dailyLength = DailyTaskRewards.values().length;
//
//            int rewardId = -1;
//            while (rewardId == -1) {
//                int tryId = random.nextInt(dailyLength);
//                if (Misc.getRandomDouble(100) <= DailyTaskRewards.values()[tryId].GetChance()) {
//                    return DailyTaskRewards.values()[tryId].GetItem();
//                }
//            }
//        }
//
//        return null;
//    }
//
//    private static Item GetRandomWeeklyItem(Player player)
//    {
//        if (EntityExtKt.passedTime(player, Attribute.TASK_WEEKLY_TIMER, 7, TimeUnit.DAYS, false, false)) {
//            int weeklyLength = WeeklyTaskRewards.values().length;
//
//            int rewardId = -1;
//            while (rewardId == -1) {
//                int tryId = random.nextInt(weeklyLength);
//                if (Misc.getRandomDouble(100) <= WeeklyTaskRewards.values()[tryId].GetChance()) {
//                    return WeeklyTaskRewards.values()[tryId].GetItem();
//                }
//            }
//        }
//
//        return null;
//    }
//
//    public static void SetRewardImage(Player player, int id, Item item)
//    {
//        if (item.getDefinition().isNoted())
//        {
//            item = new Item(item.getDefinition().unNote(), item.getAmount());
//        }
//        player.getPacketSender().sendItemOnInterface(24505+id, item, 1);
//    }
//
//    public static void SetText(Player player, int id, String title, String description)
//    {
//        String titleStart = "Daily Task - ";
//        if (id == 3)
//        {
//            titleStart = "Weekly Task - ";
//        }
//
//        player.getPacketSender().sendString(24509+id, titleStart+title);
//        player.getPacketSender().sendString(24513+id, description);
//    }
//
//    public static void SetAmount(Player player, int id, int currentAmount, int neededAmount)
//    {
//        player.getPacketSender().sendString(24529+id, currentAmount+"/"+neededAmount);
//    }
//
//}
