package com.grinder.game.content.task_new;


import com.google.common.collect.ImmutableSet;

public enum DailyTask implements PlayerTask {
    //combat tasks
    ROCK_CRAB(100, "Rocky Day", "Kill 100 Rock Crabs"),
    GREEN_DRAGON(100, "Mind the Fire", "Kill 100 Green Dragons"),
    HILL_GIANT(100, "How's the weather up there?", "Kill 100 Hill Giants"),

    //woodcutting tasks
    CHOP_LOGS(250, "Wood Chopper", "Chop 250 Normal Trees"),
    CHOP_WILLOW_LOGS(100, "Junk in the trunk", "Chop 100 Willow Trees"),
    CHOP_MAPLE_LOGS(100, "I'd tap that", "Chop 100 Maple Trees"),
    CHOP_YEW_LOGS(50, "Just for yew", "Chop 50 Yew Trees"),
    CHOP_MAGIC_LOGS(25, "Where the magic happens", "Chop 25 Magic Trees"),

    //fishing tasks
    FISH_RAW_LOBSTER(100, "Don't be shellfish", "Catch 100 Lobsters"),
    FISH_RAW_SHARK(50, "Pleased to eat you", "Catch 50 Sharks"),

    //mining tasks
    MINE_IRON_ORE(100, "Ironman", "Mine 100 Iron ore"),
    MINE_COAL(100, "Santa's gift", "Mine 100 Coal"),

    //firemaking tasks
    BURN_OAK_LOGS(100, "Oak so exciting", "Light 100 Oak Logs"),
    BURN_WILLOW_LOGS(100, "Burn, burn, burn", "Light 100 Willow Logs"),
    BURN_YEW_LOGS(50, "Yew light my fire", "Light 50 Yew Logs"),

    //smithing tasks
    SMITH_IRON_BAR(100, "Oh the irony", "Make 100 Iron Bars"),
    SMITH_STEEL_BAR(50, "What a steel", "Make 50 Steel Bars"),

    //fletching tasks
    FLETCH_OAK_SHORTBOW(100, "Oak, so simple", "String 100 Oak Shortbows"),
    FLETCH_WILLOW_SHORTBOW(50, "Shoot for the stars", "String 50 Willow Shortbows"),

    //prayer tasks
    OFFER_BIG_BONES(100, "Mortician", "Bury 100 Big Bones"),
    OFFER_DRAGON_BONES(50, "Dragon yard", "Bury 50 Dragon Bones"),
    OFFER_OURG_BONES(25, "Ooki Dookie", "Bury 25 Ourg Bones"),

    //random tasks
    PEST_CONTROL(3, "Rats!", "Win 3 games of Pest Control"),
    SKILLING_TASK(3, "Very skillful", "Complete 3 Skilling tasks"),
    CASTLE_WARS(1, "Great Knight life", "Complete 1 Castle Wars game"),
    AQUAIS_NEIGE(1, "Dark winter!", "Win the Aquais Neige minigame"),
    BARROWS_CHESTS(3, "Barrows brothers!", "Open up 3 Barrows chests"),
    CUTTING_GEMS(100, "Gems Uncut!", "Cut 100 Gems"),
    AGILITY_LAPS(5, "Like a Feather!", "Complete 5 Agility Course Laps"),
    PICKPOCKET(100, "Dirty Hands!", "Pickpocket 100 succesful times"),
    CRAFT_RUNES(250, "Wizardos Magisto!", "Craft 250 Runes"),
    HIGH_ALCHEMY(100, "Abra Kadabra!", "Cast 100 High alch spell"),
    POTIONS_CREATOR(50, "Ancient Brews!", "Herblore 50 potions"),
    FISHES_CAUGHT(150, "Fisherman!", "Catch 150 fishes"),
    CLUE_SCROLLS(3, "Curious Mind!", "Complete any 3 clue scrolls"),
    BONES_ALTAR(100, "Gods Pleased!", "Sacrifice 50 bones on altar"),
    MUDDY_CHEST(5, "Mud Mud!", "Open 5 Muddy chests"),
    CRYSTAL_CHEST(10, "Crystal Shine!", "Open 10 Crystal chests"),
    RARE_DROPS(5, "Mighty Chances!", "Receive 5 rare drops from NPC's"),
    RDT_DROPS(5, "What is RDT!", "Receive 5 RDT drops from NPC's"),
    FIGHT_CAVES(1, "Almighty Jad!", "Defeat the Fightcaves minigame"),
    SLAYER_KILLS(100, "Slay'em!", "Slay 100 Slayers NPC's on task"),

    ;

    public static final DailyTask[] VALUES = values();
    public static final ImmutableSet<DailyTask> DAILY_TASKS = ImmutableSet.copyOf(VALUES);

    private final int amount;
    private final String title;
    private final String description;

    DailyTask(int amount, String title, String description) {
        this.amount = amount;
        this.title = title;
        this.description = description;
    }

    public String getName() {
        return toString().replaceAll("_", " ");
    }

    @Override
    public int amount() {
        return amount;
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public Enum<DailyTask> id() {
        return this;
    }
}
