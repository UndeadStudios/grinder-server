package com.grinder.game.content.tasks;

public class WeeklyTasks {

    //Type 1
    public enum CombatTasks
    {
        //TZTOK_JAD(10, "Fire in your heart", "Kill Jad 10 times"),
        KALPHITE_QUEEN(25, "Royal pardon", "Kill Kalphite Queen 25 times"),
        GIANT_MOLE(25, "Mole-itically incorrect", "Kill Giant Mole 25 times"),
        MUTANT_TARN(25, "All vines", "Kill Mutant Tarn 25 times"),
        ZULRAH(25, "One true boss", "Kill Zulrah 25 times"),
        BLACK_KNIGHT_TITAN(25, "The Titan Slayer", "Kill Black Knight Titan 25 times"),
        CHAOS_ELEMENTAL(25, "True chaos", "Kill the Chaos Elemental 25 times"),
        ENT(100, "Evil trees", "Kill 100 ents"),
        FIRE_GIANTS(250, "Giants on Fire", "Kill 250 Fire giants"),
        BLACK_DEMON(100, "Satanic", "Kill 100 Black demons"),
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

    //Type 3
    public enum FishingTasks {

        RAW_SHARK(500, "Pleased to eat you", "Catch 500 Sharks"),
        RAW_MONKFISH(500, "For a true Monk", "Catch 500 Monkfish");

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

        ADAMANTITE_ORE(500, "This will take awhile", "Mine 500 Adamantite ore"),
        RUNITE_ORE(250, "Shiny", "Mine 250 Runite Ore");

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

    //Type 9
    public enum OtherTasks {

        PEST_CONTROL(10, "Rats!", "Win 10 games of Pest Control"),
        SKILLING_TASK(5, "Very skillful", "Complete 5 Skilling tasks"),
        CASTLE_WARS(5, "Great Knight life", "Complete 5 Castle Wars game"),
        AQUAIS_NEIGE(1, "Dark winter!", "Win the Aquais Neige minigame"),
        BARROWS_CHESTS(8, "Barrows brothers!", "Open up 8 Barrows chests"),
        CUTTING_GEMS(500, "Gems Uncut!", "Cut 500 Gems"), // task #5
        AGILITY_LAPS(25, "Like a Feather!", "Complete 25 Agility Course Laps"),
        PICKPOCKET(250, "Dirty Hands!", "Pickpocket 250 succesful times"),
        CRAFT_RUNES(1000, "Wizardos Magisto!", "Craft 1000 Runes"),
        HIGH_ALCHEMY(500, "Abra Kadabra!", "Cast 500 High alch spell"),
        POTIONS_CREATOR(250, "Ancient Brews!", "Herblore 250 potions"), // task #10
        FISHES_CAUGHT(500, "Fisherman!", "Catch 500 fishes"),
        CLUE_SCROLLS(10, "Curious Mind!", "Complete any 10 clue scrolls"),
        BONES_ALTAR(500, "Gods Pleased!", "Sacrifice 500 bones on altar"),
        MUDDY_CHEST(25, "Mud Mud!", "Open 25 Muddy chests"),
        CRYSTAL_CHEST(25, "Crystal Shine!", "Open 25 Crystal chests"), // task #15
        RARE_DROPS(25, "Mighty Chances!", "Receive 25 rare drops from NPC's"),
        RDT_DROPS(25, "What is RDT!", "Receive 25 RDT drops from NPC's"),
        FIGHT_CAVES(5, "Almighty Jad!", "Defeat the Fightcaves minigame 5 times"),
        SLAYER_KILLS(500, "Slay'em!", "Slay 500 Slayers NPC's on task"); // task #19

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
