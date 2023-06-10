package com.grinder.game.content.tasks;

import com.grinder.util.Misc;
import com.grinder.util.NpcID;

public class DailyTasks {

    //Type 1
    public enum CombatTasks
    {
        ROCK_CRAB(100, "Rocky Day", "Kill 100 Rock Crabs"),
        GREEN_DRAGON(100, "Mind the Fire", "Kill 100 Green Dragons"),
        HILL_GIANT(100, "How's the weather up there?", "Kill 100 Hill Giants"),
        ;

        private final int amount;
        private final String title;
        private final String description;

        private CombatTasks(int amount, String title, String description)
        {
            this.amount = amount;
            this.title = title;
            this.description = description;
        }

        public String getName() {
            return toString().replaceAll("_", " ");
        }

        public int GetAmount()
        {
            return amount;
        }

        public String GetTitle() {
            return title;
        }

        public String GetDescription() {
            return description;
        }
    }

    //Type 2
    public enum WoodcuttingTasks {

        LOGS(250, "Wood Chopper", "Chop 250 Normal Trees"),
        WILLOW_LOGS(100, "Junk in the trunk", "Chop 100 Willow Trees"),
        MAPLE_LOGS(100, "I'd tap that", "Chop 100 Maple Trees"),
        YEW_LOGS(50, "Just for yew", "Chop 50 Yew Trees"),
        MAGIC_LOGS(25, "Where the magic happens", "Chop 25 Magic Trees");

        private final int amount;
        private final String title;
        private final String description;

        private WoodcuttingTasks(int amount, String title, String description)
        {
            this.amount = amount;
            this.title = title;
            this.description = description;
        }

        public int GetAmount() {
            return amount;
        }

        public String GetTitle() {
            return title;
        }

        public String GetDescription() {
            return description;
        }
    }

    //Type 3
    public enum FishingTasks {

        RAW_LOBSTER(100, "Don't be shellfish", "Catch 100 Lobsters"),
        RAW_SHARK(50, "Pleased to eat you", "Catch 50 Sharks");

        private final int amount;
        private final String title;
        private final String description;

        private FishingTasks(int amount, String title, String description)
        {
            this.amount = amount;
            this.title = title;
            this.description = description;
        }

        public int GetAmount() {
            return amount;
        }

        public String GetTitle() {
            return title;
        }

        public String GetDescription() {
            return description;
        }
    }

    //Type 4
    public enum MiningTasks {

        IRON_ORE(100, "Ironman", "Mine 100 Iron ore"),
        COAL(100, "Santa's gift", "Mine 100 Coal");

        private final int amount;
        private final String title;
        private final String description;

        private MiningTasks(int amount, String title, String description)
        {
            this.amount = amount;
            this.title = title;
            this.description = description;
        }

        public int GetAmount() {
            return amount;
        }

        public String GetTitle() {
            return title;
        }

        public String GetDescription() {
            return description;
        }
    }

    //Type 5
    public enum FiremakingTasks {

        OAK_LOGS(100, "Oak so exciting", "Light 100 Oak Logs"),
        WILLOW_LOGS(100, "Burn, burn, burn", "Light 100 Willow Logs"),
        YEW_LOGS(50, "Yew light my fire", "Light 50 Yew Logs");

        private final int amount;
        private final String title;
        private final String description;

        private FiremakingTasks(int amount, String title, String description)
        {
            this.amount = amount;
            this.title = title;
            this.description = description;
        }

        public int GetAmount() {
            return amount;
        }

        public String GetTitle() {
            return title;
        }

        public String GetDescription() {
            return description;
        }
    }

    //Type 6
    public enum SmithingTasks {

        IRON_BAR(100, "Oh the irony", "Make 100 Iron Bars"),
        STEEL_BAR(50, "What a steel", "Make 50 Steel Bars");

        private final int amount;
        private final String title;
        private final String description;

        private SmithingTasks(int amount, String title, String description)
        {
            this.amount = amount;
            this.title = title;
            this.description = description;
        }

        public int GetAmount() {
            return amount;
        }

        public String GetTitle() {
            return title;
        }

        public String GetDescription() {
            return description;
        }
    }

    //Type 7
    public enum FletchingTasks {

        OAK_SHORTBOW(100, "Oak, so simple", "String 100 Oak Shortbows"),
        WILLOW_SHORTBOW(50, "Shoot for the stars", "String 50 Willow Shortbows");

        private final int amount;
        private final String title;
        private final String description;

        private FletchingTasks(int amount, String title, String description)
        {
            this.amount = amount;
            this.title = title;
            this.description = description;
        }

        public int GetAmount() {
            return amount;
        }

        public String GetTitle() {
            return title;
        }

        public String GetDescription() {
            return description;
        }
    }

    //Type 8
    public enum PrayerTasks {

        BIG_BONES(100, "Mortician", "Bury 100 Big Bones"),
        DRAGON_BONES(50, "Dragon yard", "Bury 50 Dragon Bones"),
        OURG_BONES(25, "Ooki Dookie", "Bury 25 Ourg Bones");

        private final int amount;
        private final String title;
        private final String description;

        private PrayerTasks(int amount, String title, String description)
        {
            this.amount = amount;
            this.title = title;
            this.description = description;
        }

        public int GetAmount() {
            return amount;
        }

        public String GetTitle() {
            return title;
        }

        public String GetDescription() {
            return description;
        }
    }

    //Type 9
    public enum OtherTasks {

        PEST_CONTROL(3, "Rats!", "Win 3 games of Pest Control"), // task 0#
        SKILLING_TASK(3, "Very skillful", "Complete 3 Skilling tasks"),
        CASTLE_WARS(1, "Great Knight life", "Complete 1 Castle Wars game"),
        AQUAIS_NEIGE(1, "Dark winter!", "Win the Aquais Neige minigame"),
        BARROWS_CHESTS(3, "Barrows brothers!", "Open up 3 Barrows chests"),
        CUTTING_GEMS(100, "Gems Uncut!", "Cut 100 Gems"), // task #5
        AGILITY_LAPS(5, "Like a Feather!", "Complete 5 Agility Course Laps"),
        PICKPOCKET(100, "Dirty Hands!", "Pickpocket 100 succesful times"),
        CRAFT_RUNES(250, "Wizardos Magisto!", "Craft 250 Runes"),
        HIGH_ALCHEMY(100, "Abra Kadabra!", "Cast 100 High alch spell"),
        POTIONS_CREATOR(50, "Ancient Brews!", "Herblore 50 potions"), // task #10
        FISHES_CAUGHT(150, "Fisherman!", "Catch 150 fishes"),
        CLUE_SCROLLS(3, "Curious Mind!", "Complete any 3 clue scrolls"),
        BONES_ALTAR(100, "Gods Pleased!", "Sacrifice 50 bones on altar"),
        MUDDY_CHEST(5, "Mud Mud!", "Open 5 Muddy chests"),
        CRYSTAL_CHEST(10, "Crystal Shine!", "Open 10 Crystal chests"), // task #15
        RARE_DROPS(5, "Mighty Chances!", "Receive 5 rare drops from NPC's"),
        RDT_DROPS(5, "What is RDT!", "Receive 5 RDT drops from NPC's"),
        FIGHT_CAVES(1, "Almighty Jad!", "Defeat the Fightcaves minigame"),
        SLAYER_KILLS(100, "Slay'em!", "Slay 100 Slayers NPC's on task"), // task #19
        ;

        private final int amount;
        private final String title;
        private final String description;

        private OtherTasks(int amount, String title, String description)
        {
            this.amount = amount;
            this.title = title;
            this.description = description;
        }

        public int GetAmount() {
            return amount;
        }

        public String GetTitle() {
            return title;
        }

        public String GetDescription() {
            return description;
        }
    }
}
