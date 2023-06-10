package com.grinder.game.content.achievement;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;

/**
 * The achievements
 */
public enum AchievementType {
    /*
     * Easy
     */
    COMMUNICATING(1, "Say something.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.COINS, 100_000)}, AchievementDifficulty.EASY),

    VAINGLORIOUS(1, "Change your character appearance.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.COINS, 100_000)}, AchievementDifficulty.EASY),

    DUEL_ICIOUS(1, "Win a duel against a player.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.COINS, 350_000)}, AchievementDifficulty.EASY),

    FEARLESS(1, "Defeat a player in the deep Wilderness", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.BLOOD_MONEY, 3000), new Item(ItemID.COINS, 500_000), new Item(24384, 1), new Item(24207, 1)}, AchievementDifficulty.EASY),

    FRIENDLY(25, "Add 25 friends to your friends list.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.COINS, 750_000)}, AchievementDifficulty.EASY),

    GETTING_READY(1, "Complete the starter guide tutorial.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.COINS, 750_000)}, AchievementDifficulty.EASY),

    HAT_TRICK(1, "Equip the Top hat.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.COINS, 250_000)}, AchievementDifficulty.EASY),

    AMMO_CONSERVATION(1, "Defeat a player with one arrow remaining.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.COINS, 3_000_000)}, AchievementDifficulty.EASY),

    ASSISTANCE_REQUIRED(1, "Defeat a player while in multi area Wilderness", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.COINS, 2_500_000)}, AchievementDifficulty.EASY),

    BETRAYED(1, "Be killed by someone from your clan.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.COINS, 2_000_000)}, AchievementDifficulty.EASY),

    CLAN_WARFARE(1, "Defeat a player while you're in a clan chat.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.COINS, 2_000_000)}, AchievementDifficulty.EASY),

    COLLECTOR(1, "Ask Piles to note an item for you at the Wilderness resource area.", "1 achievement point.", 1,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.EASY),

    COMBAT_READY(1, "Get skulled.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.COINS, 250_000)}, AchievementDifficulty.EASY),

    SAFE_KILL(1, "Defeat a player with higher hit points than your hitpoints.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.COINS, 2_500_000)}, AchievementDifficulty.EASY),

    DEAD_MAN_STALKING(1, "Defeat a player when you're at one hp.", "1 achievement point.", 1,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.EASY),

    HIDING(1, "Appear as off-line to all your friends.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.COINS, 100_000)}, AchievementDifficulty.EASY),

    SELF_SECURE(1, "Add a bank PIN to your account.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.COINS, 100_000)}, AchievementDifficulty.EASY),

    TROUBLE_BLOCKED(1, "Get teleblocked by any player.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.COINS, 500_000)}, AchievementDifficulty.EASY),

    ON_THE_ALERT(1, "Get an item saved by protect item prayer on death.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.COINS, 350_000)}, AchievementDifficulty.EASY),

    MYSTERY_MIND(1, "Change your magic spell book.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.COINS, 100_000)}, AchievementDifficulty.EASY),

    OWING_A_LIFE(1, "Be saved by a ring of life with a combat level above 100.", "1 achievement point.", 1,
            new Item[]{new Item(6199, 1), new Item(2571, 1)}, AchievementDifficulty.EASY),

    PANIC(1, "Be assigned as someone's target at the Wilderness", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.COINS, 100_000)}, AchievementDifficulty.EASY),

    TREASURE_HUNTER(1, "Complete a Clue Scroll.", "1 achievement point.", 1,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.EASY),

    COOKING_APPRENTICE(1, "Burn a food while attempting to cook it.", "1 achievement point.", 1,
            new Item[]{new Item(321, 50), new Item(317, 50)}, AchievementDifficulty.EASY),

    JEWELLERY_ENCHANTER(1, "Enchant a jewellery.", "1 achievement point.", 1,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.EASY),

    MINOR_ALCHEMIST(10, "Cast High alchemy on items 10 times.", "1 achievement point.", 1,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.EASY),

    VENGEANCE(10, "Cast Vengeance 10 times.", "1 achievement point.", 1,
            new Item[]{new Item(6199, 1), new Item(9075, 100), new Item(560, 50), new Item(557, 250)}, AchievementDifficulty.EASY),

    NEED_ENERGY(100, "Restore 100% run energy from potions.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.COINS, 1_000_000)}, AchievementDifficulty.EASY),

    QUICK_SPIRIT(1, "Setup your quick prayers.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.COINS, 100_000)}, AchievementDifficulty.EASY),

    CRUSHING_THEM(1, "Crush bones using Bonecrusher.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.COINS, 1_500_000)}, AchievementDifficulty.EASY),

    COMBINATION(1, "Combine two items together.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.COINS, 350_000)}, AchievementDifficulty.EASY),

    DECANTER(1, "Decant your potions from decanter.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.COINS, 100_000)}, AchievementDifficulty.EASY),

    IDEA_NOTED(1, "Write a note.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.COINS, 50_000)}, AchievementDifficulty.EASY),

    POISON_WOUND(50, "Take 50 poison damage from enemies.", "1 achievement point.", 1,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.EASY),

    DEPRECIATION(1, "Use a degrading item until it vanishes.", "1 achievement point.", 1,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.EASY),

    TRANSMOGRIFIED(1, "Transform into an ice demon.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.COINS, 3_000_000)}, AchievementDifficulty.EASY),

    HARD_AS_ROCK(1, "Transform into a rock crab.", "1 achievement point.", 1,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.EASY),

    LOOKING_FOR_TROUBLE(1, "Teleport to a target in the Wilderness", "1 achievement point.", 1,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.EASY),

    MASTER_AT_ARMS(1, "Defeat a player without wearing anything.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.COINS, 1_000_000), new Item(24382, 1), new Item(24215, 1)}, AchievementDifficulty.EASY),

    LOCATING_EYE(1, "Use a talisman altar teleport.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.COINS, 150_000), new Item(1437, 1000)}, AchievementDifficulty.EASY),

    BRAND_NEW(1, "Repair a piece of armour.", "1 achievement point.", 1,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.EASY),

    MONEY_POUCH(10000000, "Get 10,000,000 coins from drops.", "1 achievement point.", 1,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.EASY),

    NEED_FOOD(50, "Steal from the Bakery stall 50 times.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.COINS, 5_000_000), new Item(1892, 50), new Item(1898, 50)}, AchievementDifficulty.EASY),

    MONSTER_KILLER(250, "Slay 250 monsters.", "1 achievement point.", 1,
            new Item[]{new Item(20405, 1), new Item(20408, 1), new Item(ItemID.COINS, 25_000_000)}, AchievementDifficulty.EASY),

    CHOP_CHOP(100, "Chop 100 regular trees.", "1 achievement point.", 1,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.EASY),

    OBSTACLE_FREE(10, "Complete 10 agility courses.", "1 achievement point.", 1,
            new Item[]{new Item(2996, 25), new Item(3033, 25)}, AchievementDifficulty.EASY),

    GEM_CUTTER(100, "Cut 100 uncut gems.", "1 achievement point.", 1,
            new Item[]{new Item(1632, 50), new Item(1618, 50), new Item(1620, 50), new Item(1622, 50), new Item(1624, 25), new Item(1630, 25)}, AchievementDifficulty.EASY),

    SLAYER_NOVICE(5, "Complete 5 Slayer tasks.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.BLOOD_MONEY, 25_000), new Item(8921, 1)}, AchievementDifficulty.EASY),

    GREENLAND(50, "Plant 50 seeds.", "1 achievement point.", 1,
            new Item[]{new Item(5291, 10), new Item(5292, 10), new Item(5293, 10), new Item(5294, 10), new Item(5295, 10), new Item(5296, 10),
                    new Item(5297, 10), new Item(5298, 10), new Item(5299, 10)}, AchievementDifficulty.EASY),

    LUCKY_TRIP(1, "Get an RDT drop.", "1 achievement point.", 1,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.EASY),

    OVER_DICE(1, "Roll exactly 100 while rolling a dice.", "1 achievement point.", 1,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.EASY),

    INITIAL_CRYSTAL(1, "Open the crystal chest.", "1 achievement point.", 1,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.EASY),

    MUDDY_WORK(1, "Open the muddy chest.", "1 achievement point.", 1,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.EASY),

    SAD_DEATH(1, "Die in the Wilderness.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.COINS, 50_000)}, AchievementDifficulty.EASY),

    CURIOSITY(10, "Open 10 caskets or mystery boxes.", "2 achievement points.", 1,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.EASY),

    /*
     * Medium
     */

    PET_COLLECTOR(1, "Aquire a cute pet from skilling.", "2 achievement points.", 2,
            new Item[]{new Item(6199, 1), new Item(ItemID.COINS, 5_000_000)}, AchievementDifficulty.MEDIUM),

    BARROWS_ENEMY(1, "Slay all the Barrows brothers.", "2 achievement points.", 2,
            new Item[]{new Item(7462), new Item(ItemID.COINS, 5_000_000)}, AchievementDifficulty.MEDIUM),

    SKILLING_ADDICT(1000, "Exchange some skilling points.", "2 achievement points.", 2,
            new Item[]{new Item(ItemID.COINS, 25_000_000)}, AchievementDifficulty.MEDIUM),

    DARK_AND_HOLLOW(1, "Equip the Darkbow.", "2 achievement points.", 2,
            new Item[]{new Item(ItemID.COINS, 1_000_000)}, AchievementDifficulty.MEDIUM),

    DAILY_FEED(5, "Receive the daily login reward 5 times.", "2 achievement points.", 2,
            new Item[]{new Item(ItemID.COINS, 500_000)}, AchievementDifficulty.MEDIUM),

    IMPORTANT_PERSON(1, "Be promoted to a recruit rank in a clan chat.", "2 achievement points.", 2,
            new Item[]{new Item(ItemID.COINS, 1_000_000)}, AchievementDifficulty.MEDIUM),

    KILLING_THEM(25, "Defeat 25 players in the Wilderness", "2 achievement points.", 2,
            new Item[]{new Item(ItemID.COINS, 50_000_000), new Item(24380, 1), new Item(24209, 1)}, AchievementDifficulty.MEDIUM),

    ALL_MUD(10, "Open the muddy chest 10 times.", "2 achievement points.", 2,
            new Item[]{new Item(ItemID.BLOOD_MONEY, 10000)}, AchievementDifficulty.MEDIUM),

    QUICK_RECOVERY(100, "Recover 100 Hit points.", "2 achievement points.", 2,
            new Item[]{new Item(ItemID.COINS, 5_000_000)}, AchievementDifficulty.MEDIUM),

    APPRENTICE_ALCHEMIST(50, "Cast High alchemy on items 50 times.", "2 achievement points.", 2,
            new Item[]{new Item(65, 250)}, AchievementDifficulty.MEDIUM),

    HARD_DAMAGE(1, "Deal 30 or more damage in one hit.", "2 achievement points.", 2,
            new Item[]{new Item(ItemID.COINS, 1_000_000)}, AchievementDifficulty.MEDIUM),

    BLOWING_HITS(25, "Use special attack 25 times", "2 achievement points.", 2,
            new Item[]{new Item(ItemID.COINS, 1_000_000)}, AchievementDifficulty.MEDIUM),

    LUCKY_DRAW(1, "Win the lottery.", "2 achievement points.", 2,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.MEDIUM),

    CONTRACT_JUNIOR(1, "Complete a boss contract.", "2 achievement points.", 2,
            new Item[]{new Item(ItemID.COINS, 5_000_000)}, AchievementDifficulty.MEDIUM),

    SLAYER_MAJOR(10, "Complete 10 Slayer tasks.", "2 achievement points.", 2,
            new Item[]{new Item(ItemID.BLOOD_MONEY, 35000), new Item(8901)}, AchievementDifficulty.MEDIUM),

    NEED_FUR(50, "Steal from the Fur stall 50 times.", "2 achievement points.", 2,
            new Item[]{new Item(ItemID.COINS, 5_000_000)}, AchievementDifficulty.MEDIUM),

    PROFESSIONAL_THIEF(250, "Steal from an NPC 250 times.", "2 achievement points.", 2,
            new Item[]{new Item(ItemID.COINS, 25_000_000)}, AchievementDifficulty.MEDIUM),

    NEED_GEM(100, "Steal from the Gem stall 100 times.", "2 achievement points.", 2,
            new Item[]{new Item(ItemID.COINS, 5_000_000)}, AchievementDifficulty.MEDIUM),

    PARTY_UP(10, "Claim the command trivia 10 times.", "2 achievement points.", 2,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.MEDIUM),

    THE_CHARGER(50, "Charge your shield 50 times from dragons.", "2 achievement points.", 2,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.MEDIUM),

    DEEP_FOCUS(1, "Unlock Rigour, Augury, and Preserve prayers.", "2 achievement points.", 2,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.MEDIUM),

    MONEY_CHEST(100000000, "Get 100,000,000 coins from drops.", "2 achievement points.", 2,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.MEDIUM),

    YELL_MANIA(50, "Yell 50 times.", "2 achievement points.", 2,
            new Item[]{new Item(7774, 1)}, AchievementDifficulty.MEDIUM),

    POISON_SPREE(250, "Take 250 poison damage from enemies.", "2 achievement points.", 2,
            new Item[]{new Item(2449, 25)}, AchievementDifficulty.MEDIUM),

    STRAIGHT_UP(250, "Obtain 250 participation points.", "2 achievement points.", 2,
            new Item[]{new Item(15023, 1)}, AchievementDifficulty.MEDIUM),

    RAMPAGE(1, "Reach a killstreak count of 10.", "2 achievement points.", 2,
            new Item[]{new Item(15205, 1)}, AchievementDifficulty.MEDIUM),

    CANT_STOP_ME(1, "Block a Slayer task.", "2 achievement points.", 2,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.MEDIUM),

    MISTAKES_HAPPEN(1, "Cancel your current Slayer task.", "2 achievement points.", 2,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.MEDIUM),

    DOUBLE_LUCK(1, "Win a bonus item from thieving stalls.", "2 achievement points.", 2,
            new Item[]{new Item(ItemID.COINS, 5_000_000)}, AchievementDifficulty.MEDIUM),

    TREASURE_FOUNDER(5, "Complete 5 Clue Scrolls.", "2 achievement points.", 2,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.MEDIUM),

    KEEP_SMITING(250, "Smite your opponent from 250 prayer points.", "2 achievement points.", 2,
            new Item[]{new Item(ItemID.COINS, 5_000_000)}, AchievementDifficulty.MEDIUM),

    MONSTER_MURDERER(1000, "Slay 1,000 Monsters.", "2 achievement points.", 2,
            new Item[]{new Item(ItemID.COINS, 750_000)}, AchievementDifficulty.MEDIUM),

    CHOPPING_AWAY(250, "Chop 250 willow trees.", "2 achievement points.", 2,
            new Item[]{new Item(1520, 250)}, AchievementDifficulty.MEDIUM),

    CHEFS_TASTE(250, "Cook 250 fish.", "2 achievement points.", 2,
            new Item[]{new Item(3148, 250)}, AchievementDifficulty.MEDIUM),

    ORE_AND_FURNACE(250, "Smelt 250 bars.", "2 achievement points.", 2,
            new Item[]{new Item(ItemID.COINS, 5_000_000)}, AchievementDifficulty.MEDIUM),

    LIGHT_UP(250, "Start 250 fires.", "2 achievement points.", 2,
            new Item[]{new Item(ItemID.COINS, 500_000)}, AchievementDifficulty.MEDIUM),

    GRAVE_DIGGER(500, "Bury 500 bones.", "2 achievement points.", 2,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.MEDIUM),

    HERBLORER(250, "Herblore 250 potions.", "2 achievement points.", 2,
            new Item[]{new Item(ItemID.COINS, 5_000_000)}, AchievementDifficulty.MEDIUM),

    WET_WORK(200, "Mine 200 Iron ores.", "2 achievement points.", 2,
            new Item[]{new Item(441, 100)}, AchievementDifficulty.MEDIUM),

    MINING_THE_ESSENTIALS(300, "Mine 300 coal ores.", "2 achievement points.", 2,
            new Item[]{new Item(454, 100)}, AchievementDifficulty.MEDIUM),

    /*
     * Hard
     */

    OVER_ACHIEVER(50, "Complete 50 tasks.", "3 achievement points..", 3,
            new Item[]{new Item(ItemID.BLOOD_MONEY, 25000)}, AchievementDifficulty.HARD),

    PRECISE_DETAILS(1, "Reach max level in Attack skill.", "3 achievement points.", 3,
            new Item[]{new Item(11037, 1)}, AchievementDifficulty.HARD),

    GOD_OF_WAR(1, "Reach max level in Strength skill.", "3 achievement points.", 3,
            new Item[]{new Item(21646, 1)}, AchievementDifficulty.HARD),

    BRUTAL_ARMOR(1, "Reach max level in Defence skill.", "3 achievement points.", 3,
            new Item[]{new Item(10589, 1), new Item(10564), new Item(6809), new Item(3122)}, AchievementDifficulty.HARD),

    EXPERT_MARKSMAN(1, "Reach max level in Ranging skill.", "3 achievement points.", 3,
            new Item[]{new Item(5627, 100), new Item(5667, 100), new Item(9305, 100), new Item(5641, 100)}, AchievementDifficulty.HARD),

    MAGIC_ARTIST(1, "Reach max level in Magic skill.", "3 achievement points.", 3,
            new Item[]{new Item(9762), new Item(9764), new Item(6563, 1), new Item(4675)}, AchievementDifficulty.HARD),

    BLESSED_SPIRIT(1, "Reach max level in Prayer skill.", "3 achievement points.", 3,
            new Item[]{new Item(20220, 1)}, AchievementDifficulty.HARD),

    SEVEN_LIVES(1, "Reach max level in Hitpoints skill.", "3 achievement points.", 3,
            new Item[]{new Item(6686, 25)}, AchievementDifficulty.HARD),

    MAKE_THE_CUT(1, "Reach max level in Woodcutting skill.", "3 achievement points.", 3,
            new Item[]{new Item(1514, 100)}, AchievementDifficulty.HARD),

    AGILE_TASKS(1, "Reach max level in Agility skill.", "3 achievement points.", 3,
            new Item[]{new Item(2996, 50)}, AchievementDifficulty.HARD),

    STAR_CHEF(1, "Reach max level in Cooking skill.", "3 achievement points.", 3,
            new Item[]{new Item(13442, 50)}, AchievementDifficulty.HARD),

    SURF_N_TURF(1, "Reach max level in Fishing skill.", "3 achievement points.", 3,
            new Item[]{new Item(13440, 50)}, AchievementDifficulty.HARD),

    NIGHT_DIPLOMACY(1, "Reach max level in Slayer skill.", "3 achievement points.", 3,
            new Item[]{new Item(ItemID.BLOOD_MONEY, 50000)}, AchievementDifficulty.HARD),

    HERBALISM(1, "Reach max level in Herblore skill.", "3 achievement points.", 3,
            new Item[]{new Item(2486, 100)}, AchievementDifficulty.HARD),

    NEVER_GIVE_UP(1, "Reach max level in Mining skill.", "3 achievement points.", 3,
            new Item[]{new Item(ItemID.COAL_2, 250), new Item(ItemID.RUNITE_ORE_2, 50)}, AchievementDifficulty.HARD),

    STAND_THE_HEAT(1, "Reach max level in Smithing skill.", "3 achievement points.", 3,
            new Item[]{new Item(2364, 100)}, AchievementDifficulty.HARD),

    ESSENCE_ARTIST(1, "Reach max level in Runecrafting skill.", "3 achievement points.", 3,
            new Item[]{new Item(9737, 250)}, AchievementDifficulty.HARD),

    CRAFTSMANSHIP(1, "Reach max level in Crafting skill.", "3 achievement points.", 3,
            new Item[]{new Item(1632, 50)}, AchievementDifficulty.HARD),

    FEATHER_ARTISAN(1, "Reach max level in Fletching skill.", "3 achievement points.", 3,
            new Item[]{new Item(314, 50), new Item(1778, 50)}, AchievementDifficulty.HARD),

    NATURE_PRESERVED(1, "Reach max level in Farming skill.", "3 achievement points.", 3,
            new Item[]{new Item(5304, 50)}, AchievementDifficulty.HARD),

    BLAZING_HOT(1, "Equip the Infernal cape.", "3 achievement points.", 3,
            new Item[]{new Item(6199, 1), new Item(ItemID.COINS, 1_000_000)}, AchievementDifficulty.HARD),

    SLIDER_STATION(1, "Equip a waxed Sled.", "3 achievement points.", 3,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.HARD),

    ARMADYL_RECRUIT(1, "Equip full Armadyl armour.", "3 achievement points.", 3,
            new Item[]{new Item(20593, 1)}, AchievementDifficulty.HARD),

    ULTIMATE_MAX(1, "Reach a total level of 1,500.", "3 achievement points.", 3,
            new Item[]{new Item(11738, 1)}, AchievementDifficulty.HARD),

    CONTAGIOUS_VENOM(400, "Take 400 poison damage from enemies.", "3 achievement points.", 3,
            new Item[]{new Item(5953, 50)}, AchievementDifficulty.HARD),

    ANCIENT_SURVIVOR(1, "Accumulate login time of more than three days on your account.", "3 achievement points.", 3,
            new Item[]{new Item(ItemID.COINS, 10_000_000)}, AchievementDifficulty.HARD),

    MAJOR_ALCHEMIST(250, "Cast High alchemy on items 250 times.", "3 achievement points.", 3,
            new Item[]{new Item(561, 100)}, AchievementDifficulty.HARD),

    EXTREME_DAMAGE(1, "Deal 50 or more damage in one hit.", "3 achievement points.", 3,
            new Item[]{new Item(ItemID.COINS, 25_000_000)}, AchievementDifficulty.HARD),

    CONSECUTIVE_BLOWS(50, "Use special attack 50 times", "3 achievement points.", 3,
            new Item[]{new Item(ItemID.COINS, 15_000_000)}, AchievementDifficulty.HARD),

    SUPER_CURIOUS(100, "Open 100 caskets or mystery boxes.", "3 achievement points.", 3,
            new Item[]{new Item(15206, 1)}, AchievementDifficulty.HARD),

    TREASURE_HOLDER(25, "Complete 25 Clue Scrolls.", "3 achievement points.", 3,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.HARD),

    DEDICATED_SLAYER(400, "Slay over 400 monsters of Slayer tasks.", "3 achievement points.", 3,
            new Item[]{new Item(7789, 1)}, AchievementDifficulty.HARD),

    BRAVE_WARRIOR(10, "Slay the General Graardor 10 times.", "3 achievement points.", 3,
            new Item[]{new Item(ItemID.COINS, 7_500_000)}, AchievementDifficulty.HARD),

    THE_ENCHANTER(1000, "Enchant 1,000 bolts.", "3 achievement points.", 3,
            new Item[]{new Item(21948, 50)}, AchievementDifficulty.HARD),

    COWBOW_DIPLOMACY(10, "Vote 10 times to the server.", "3 achievement points.", 3,
            new Item[]{new Item(15031, 25)}, AchievementDifficulty.HARD),

    ENLIGHTMENT(500, "Consume 500 potion sips.", "3 achievement points.", 3,
            new Item[]{new Item(2441, 10), new Item(2437, 10), new Item(2443, 10), new Item(2445, 10), new Item(3041, 10), new Item(2435, 10),
                    new Item(22462, 5), new Item(22450, 5)}, AchievementDifficulty.HARD),

    SKILLING_CHAMPION(15, "Complete 15 skilling tasks.", "3 achievement points.", 3,
            new Item[]{new Item(ItemID.COINS, 50_000_000)}, AchievementDifficulty.HARD),

    CRYSTAL_EXPERT(25, "Open the crystal chest 25 times.", "3 achievement points.", 3,
            new Item[]{new Item(6199, 2)}, AchievementDifficulty.HARD),

    FIRE_WARRIOR(1, "Defeat the TzTok-Jad.", "3 achievement points.", 3,
            new Item[]{new Item(6529, 2500)}, AchievementDifficulty.HARD),

    THE_ROAD_TO_HELL(5000000, "Obtain more than 5M XP in the Resource area.", "3 achievement points.", 3,
            new Item[]{new Item(10586, 1)}, AchievementDifficulty.HARD),

    BANDOS_TREASURE(1, "Get a rare drop from General Graador.", "3 achievement points.", 3,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.HARD),

    BEAST_SLAYER(1, "Defeat the Corporal Beast while you're at full health.", "3 achievement points.", 3,
            new Item[]{new Item(15024, 1)}, AchievementDifficulty.HARD),

    FOCUSED_SLAYER(1, "Unlock the ability to craft Slayer Helmets.", "3 achievement points.", 3,
            new Item[]{new Item(4160, 500)}, AchievementDifficulty.HARD),

    MONEY_FOUNTAIN(250_000_000, "Get 250,000,000 coins from drops.", "3 achievement points.", 3,
            new Item[]{new Item(6199, 10)}, AchievementDifficulty.HARD),

    AXE_DOES_IT(250, "Chop 250 Magic trees.", "3 achievement points.", 3,
            new Item[]{new Item(1513, 100)}, AchievementDifficulty.HARD),

    RANDOM_STUFF(250, "Steal from the Miscellaneous stall 250 times.", "3 achievement points.", 3,
            new Item[]{new Item(2631, 1)}, AchievementDifficulty.HARD),

    NO_OBSTALCES(25, "Complete 25 agility courses.", "3 achievement points.", 3,
            new Item[]{new Item(2996, 100)}, AchievementDifficulty.HARD),

    CLEARING_THEM(100, "Defeat 100 players in the Wilderness", "3 achievement points.", 3,
            new Item[]{new Item(15202, 1), new Item(ItemID.BLOOD_MONEY, 50000), new Item(24378, 1), new Item(24211, 1), new Item(24213, 1)}, AchievementDifficulty.HARD),

    GEM_FISSURE(250, "Cut 250 uncut gems.", "3 achievement points.", 3,
            new Item[]{new Item(1632, 50), new Item(1618, 50), new Item(1620, 50), new Item(1622, 50), new Item(1624, 50), new Item(1630, 50)}, AchievementDifficulty.HARD),

    OVER_GREEN(100, "Plant 100 seeds.", "3 achievement points.", 3,
            new Item[]{new Item(5300, 25), new Item(5301, 25), new Item(5302, 25), new Item(5303, 25)}, AchievementDifficulty.HARD),

    RECLAIMING_THE_RUINS(30, "Slay the Crazy Archaeologist 30 times.", "3 achievement points.", 3,
            new Item[]{new Item(ItemID.BLOOD_MONEY, 5000)}, AchievementDifficulty.HARD),

    SPIDER_HUNTER(30, "Slay the Venenatis 30 times.", "3 achievement points.", 3,
            new Item[]{new Item(ItemID.BLOOD_MONEY, 5000)}, AchievementDifficulty.HARD),

    SURVIVING_THE_PIT(30, "Slay Scorpia 30 times.", "3 achievement points.", 3,
            new Item[]{new Item(ItemID.BLOOD_MONEY, 5000)}, AchievementDifficulty.HARD),

    BEAR_HUNTER(30, "Slay Callisto 30 times.", "3 achievement points.", 3,
            new Item[]{new Item(ItemID.BLOOD_MONEY, 5000)}, AchievementDifficulty.HARD),

    DAILY_SAVIOUR(10, "Receive the daily login reward 10 times.", "3 achievement points.", 3,
            new Item[]{new Item(15267, 1)}, AchievementDifficulty.HARD),

    BORN_TO_DIE(50, "Die 50 times in the Wilderness", "3 achievement points.", 3,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.HARD),

    /*
     * Elite
     */

    LUCKY_BEAST(1, "Get a rare drop from the Corporal beast.", "4 achievement points.", 4,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.ELITE),

    INSURGENT(1, "Reach a combat level of 126.", "4 achievement points.", 4,
            new Item[]{new Item(ItemID.COINS, 100_000_000)}, AchievementDifficulty.ELITE),

    END_OF_JOURNEY(1, "Reach all skills level 99.", "4 achievement points.", 4,
            new Item[]{new Item(10586, 1), new Item(11738, 1)}, AchievementDifficulty.ELITE),

    DWARF_ASSISTANCE(2000, "Assist Mining Expert to obtain 2,000 mining points.", "4 achievement points.", 4,
            new Item[]{new Item(451, 500)}, AchievementDifficulty.ELITE),

    GRAVE_MASTER(1000, "Bury or sacrifice 1,000 bones.", "4 achievement points.", 4,
            new Item[]{new Item(4835, 50)}, AchievementDifficulty.ELITE),

    SPRAY_AND_PRAY(500, "Bury 500 bones while you're 99 Prayer.", "4 achievement points.", 4,
            new Item[]{new Item(3124, 50)}, AchievementDifficulty.ELITE),

    CONTRACT_EXPERT(10, "Complete 10 boss contracts.", "4 achievement points.", 4,
            new Item[]{new Item(ItemID.COINS, 50_000_000)}, AchievementDifficulty.ELITE),

    GOLDEN_MOUNTAIN(500000000, "Get 500,000,000 coins from drops.", "4 achievement points.", 4,
            new Item[]{new Item(6199, 15)}, AchievementDifficulty.ELITE),

    COMMAND_AND_CONQUER(250, "Defeat 250 players in the Wilderness", "4 achievement points.", 4,
            new Item[]{new Item(22542, 1), new Item(22547, 1), new Item(22552, 1), new Item(24376, 1), new Item(24374, 1)}, AchievementDifficulty.ELITE),

    DREAM_MENTOR(1, "Defeat the Mutant Tarn while no prayer points.", "4 achievement points.", 4,
            new Item[]{new Item(15022, 1)}, AchievementDifficulty.ELITE),

    JAD_HEAD(10, "Defeat the TzTok-Jad 10 times.", "4 achievement points.", 4,
            new Item[]{new Item(6529, 5000)}, AchievementDifficulty.ELITE),

    LUCKY_JOURNEY(250, "Get 250 rare drops.", "4 achievement points.", 4,
            new Item[]{new Item(15206, 1)}, AchievementDifficulty.ELITE),

    FIRE_GAMBLING(1, "Gamble your Fire cape to win a mini TzTok-Jad pet.", "4 achievement points.", 4,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.ELITE),

    DEVASTATING_DAMAGE(1, "Deal 75 or more damage in one hit.", "4 achievement points.", 4,
            new Item[]{new Item(ItemID.COINS, 50_000_000)}, AchievementDifficulty.ELITE),

    ALWAYS_SPECIAL(250, "Use special attack 250 times", "4 achievement points.", 4,
            new Item[]{new Item(ItemID.COINS, 25_000_000)}, AchievementDifficulty.ELITE),

    SLAYER_ELITE(25, "Complete 25 Slayer tasks.", "4 achievement points.", 4,
            new Item[]{new Item(ItemID.BLOOD_MONEY, 35000), new Item(11784, 1)}, AchievementDifficulty.ELITE),

    MASTER_ALCHEMIST(1000, "Cast High alchemy on items 1000 times.", "4 achievement points.", 4,
            new Item[]{new Item(6040, 1)}, AchievementDifficulty.ELITE),

    TREASURE_MASTER(50, "Complete 50 Clue Scrolls.", "4 achievement points.", 4,
            new Item[]{new Item(6199, 10)}, AchievementDifficulty.ELITE),

    HEAVY_WORK(1250, "Mine 1,250 ores.", "4 achievement points.", 4,
            new Item[]{new Item(452, 500)}, AchievementDifficulty.ELITE),

    HEAT_UP(300, "Use the anvil to smith 300 items.", "4 achievement points.", 4,
            new Item[]{new Item(2364, 250)}, AchievementDifficulty.ELITE),

    RISK_ACTIVITY(500, "Steal from the Wilderness stall 500 times.", "4 achievement points.", 4,
            new Item[]{new Item(4627, 1)}, AchievementDifficulty.ELITE),

    ELITE_PRECISION(1, "Reach 50M XP in Attack skill.", "4 achievement points.", 4,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.ELITE),

    ELITE_WAR(1, "Reach 50M XP in Strength skill.", "4 achievement points.", 4,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.ELITE),

    ELITE_ARMOR(1, "Reach 50M XP in Defence skill.", "4 achievement points.", 4,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.ELITE),

    ELITE_MARKSMAN(1, "Reach 50M XP in Ranging skill.", "4 achievement points.", 4,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.ELITE),

    ELITE_MAGE(1, "Reach 50M XP in Magic skill.", "4 achievement points.", 4,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.ELITE),

    ELITE_SPIRIT(1, "Reach 50M XP in Prayer skill.", "4 achievement points.", 4,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.ELITE),

    ELITE_AGE(1, "Reach 50M XP in Hitpoints skill.", "4 achievement points.", 4,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.ELITE),

    ELITE_CUTTING(1, "Reach 50M XP in Woodcutting skill.", "4 achievement points.", 4,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.ELITE),

    ELITE_AGILIY(1, "Reach 50M XP in Agility skill.", "4 achievement points.", 4,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.ELITE),

    ELITE_CHEF(1, "Reach 50M XP in Cooking skill.", "4 achievement points.", 4,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.ELITE),

    DEEP_FISHING(1, "Reach 50M XP in Fishing skill.", "4 achievement points.", 4,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.ELITE),

    ELITE_DIPLOMACY(1, "Reach 50M XP in Slayer skill.", "4 achievement points.", 4,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.ELITE),

    ELITE_HERBALISM(1, "Reach 50M XP in Herblore skill.", "4 achievement points.", 4,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.ELITE),

    NOT_GIVING_UP(1, "Reach 50M XP in Mining skill.", "4 achievement points.", 4,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.ELITE),

    SURVIVING_THE_HEAT(1, "Reach 50M XP in Smithing skill.", "4 achievement points.", 4,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.ELITE),

    ELITE_ARTIST(1, "Reach 50M XP in Runecrafting skill.", "4 achievement points.", 4,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.ELITE),

    ELITE_CRAFTSMANSHIP(1, "Reach 50M XP in Crafting skill.", "4 achievement points.", 4,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.ELITE),

    SHAFTS_ARTISAN(1, "Reach 50M XP in Fletching skill.", "4 achievement points.", 4,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.ELITE),

    EVER_GREEN_AGAIN(1, "Reach 50M XP in Farming skill.", "4 achievement points.", 4,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.ELITE),

    /*
     * Master
     */

    I_DID_IT_MY_SELF(5_000, "Slay 5,000 Monsters.", "5 achievement points.", 5,
            new Item[]{new Item(ItemID.COINS, 250_000_000)}, AchievementDifficulty.MASTER),

    BOSS_OBLITERATOR(500, "Slay 500 bosses.", "5 achievement points.", 5,
            new Item[]{new Item(15201, 1), new Item(15201), new Item(ItemID.COINS, 250_000_000)}, AchievementDifficulty.MASTER),

    COLOR_MAX(1, "Acquire the Max cape.", "5 achievement points.", 5,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.MASTER),

    ROGUE_SETUP(1, "Equip the Rogue's set.", "5 achievement points.", 5,
            new Item[]{new Item(4627, 1)}, AchievementDifficulty.MASTER),

    PROSPECTOR_SETUP(1, "Equip the Prospector's set.", "5 achievement points.", 5,
            new Item[]{new Item(2364, 100)}, AchievementDifficulty.MASTER),

    ANGLER_SETUP(1, "Equip the Angler's set.", "5 achievement points.", 5,
            new Item[]{new Item(ItemID.BLOOD_MONEY, 50000)}, AchievementDifficulty.MASTER),

    ANKOU_SETUP(1, "Equip the Ankou's set.", "5 achievement points.", 5,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.MASTER),

    MUMMY_SETUP(1, "Equip the Mummy's set.", "5 achievement points.", 5,
            new Item[]{new Item(13283, 1)}, AchievementDifficulty.MASTER),

    LUMBERJACK_SETUP(1, "Equip the Lumberjack's set.", "5 achievement points.", 5,
            new Item[]{new Item(7789, 1)}, AchievementDifficulty.MASTER),

    COLOR_ARTIST(1, "Gamble and win the Colorful max cape.", "5 achievement points.", 5,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.MASTER),

    SAFETY_FIRST(8000, "Heal a total of 8,000 life hit points.", "5 achievement points.", 5,
            new Item[]{new Item(3144, 500)}, AchievementDifficulty.MASTER),

    CRYSTAL_MASTER(100, "Open the crystal chest 100 times.", "5 achievement points.", 5,
            new Item[]{new Item(990, 10)}, AchievementDifficulty.MASTER),

    A_WORLD_OF_PAIN(50000, "Inflict 50,000 total hits.", "5 achievement points.", 5,
            new Item[]{new Item(ItemID.COINS, 250_000_000)}, AchievementDifficulty.MASTER),

    DAILY_ACTIVIST(30, "Receive the daily login reward 30 times.", "5 achievement points.", 5,
            new Item[]{new Item(15267, 5)}, AchievementDifficulty.MASTER),

    SLAYER_CONTROL(50, "Complete 50 Slayer tasks.", "5 achievement points.", 5,
            new Item[]{new Item(ItemID.BLOOD_MONEY, 50000), new Item(12931, 1)}, AchievementDifficulty.MASTER),

    CONTRACT_MASTER(50, "Complete 50 boss contracts.", "5 achievement points.", 5,
            new Item[]{new Item(ItemID.COINS, 50_000_000)}, AchievementDifficulty.MASTER),

    THE_SAVIOUR(500, "Defeat 500 players in the Wilderness", "5 achievement points.", 5,
            new Item[]{new Item(ItemID.BLOOD_MONEY, 1_000_000), new Item(24373, 1), new Item(24215, 1)}, AchievementDifficulty.MASTER),

    GRINDERSCAPE_IDOL(1, "Reach a total XP of 500,000,000.", "5 achievement points.", 5,
            new Item[]{new Item(15158, 1)}, AchievementDifficulty.MASTER),

    TREES_ARE_LIFE(2000, "Chop 2,000 Magic trees.", "5 achievement points.", 5,
            new Item[]{new Item(13241, 1)}, AchievementDifficulty.MASTER),

    MASTER_PRECISION(1, "Reach 100M XP in Attack skill.", "5 achievement points.", 5,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.MASTER),

    MASTER_OF_WAR(1, "Reach 100M XP in Strength skill.", "5 achievement points.", 5,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.MASTER),

    UNBREAKABLE_ARMOR(1, "Reach 100M XP in Defence skill.", "5 achievement points.", 5,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.MASTER),

    MASTER_MARKSMAN(1, "Reach 100M XP in Ranging skill.", "5 achievement points.", 5,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.MASTER),

    MAGIC_MASTER(1, "Reach 100M XP in Magic skill.", "5 achievement points.", 5,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.MASTER),

    ENCHANTED_SPIRIT(1, "Reach 100M XP in Prayer skill.", "5 achievement points.", 5,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.MASTER),

    IMMORTALITY(1, "Reach 100M XP in Hitpoints skill.", "5 achievement points.", 5,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.MASTER),

    MASTER_CUTTER(1, "Reach 100M XP in Woodcutting skill.", "5 achievement points.", 5,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.MASTER),

    MASTER_NINJA(1, "Reach 100M XP in Agility skill.", "5 achievement points.", 5,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.MASTER),

    MASTER_CHEF(1, "Reach 100M XP in Cooking skill.", "5 achievement points.", 5,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.MASTER),

    MARIANA_TRENCH(1, "Reach 100M XP in Fishing skill.", "5 achievement points.", 5,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.MASTER),

    MASTER_DIPLOMACY(1, "Reach 100M XP in Slayer skill.", "5 achievement points.", 5,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.MASTER),

    MASTER_HERBALISM(1, "Reach 100M XP in Herblore skill.", "5 achievement points.", 5,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.MASTER),

    NEVER_GIVING_UP(1, "Reach 100M XP in Mining skill.", "5 achievement points.", 5,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.MASTER),

    HEAT_ROUTINE(1, "Reach 100M XP in Smithing skill.", "5 achievement points.", 5,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.MASTER),

    MASTER_ARTIST(1, "Reach 100M XP in Runecrafting skill.", "5 achievement points.", 5,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.MASTER),

    MASTER_CRAFTSMANSHIP(1, "Reach 100M XP in Crafting skill.", "5 achievement points.", 5,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.MASTER),

    MASTER_FLETCHER(1, "Reach 100M XP in Fletching skill.", "5 achievement points.", 5,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.MASTER),

    FARM_MASTER(1, "Reach 100M XP in Farming skill.", "5 achievement points.", 5,
            new Item[]{new Item(6199, 5)}, AchievementDifficulty.MASTER),

    /*
     * Other
     */

    FRIENDLY_ATTIRE(1, "Shop for at least one time from the store.", "1 achievement point.", 1,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.OTHER),

    FAN_OF_COLORS(1, "Unlock and change your yell title.", "1 achievement point.", 1,
            new Item[]{new Item(1907, 1)}, AchievementDifficulty.OTHER),

    HIDDEN(1, "Change your primary rank from the rank chooser.", "1 achievement point.", 1,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.OTHER),

    TRUSTED_MEMBER(1, "Become a trusted dicer.", "1 achievement point.", 1,
            new Item[]{new Item(1907, 1)}, AchievementDifficulty.OTHER),

    SPREAD_LOVE(1, "Aquire the Ruby member's rank from rank chooser.", "1 achievement point.", 1,
            new Item[]{new Item(1907, 1)}, AchievementDifficulty.OTHER),

    SUPERIOR_SUPPORT(1, "Aquire the Topaz member's rank from rank chooser.", "1 achievement point.", 1,
            new Item[]{new Item(1907, 5)}, AchievementDifficulty.OTHER),

    EXTREME_SUPPORT(1, "Aquire the Amethyst member's rank from rank chooser.", "1 achievement point.", 1,
            new Item[]{new Item(1907, 10)}, AchievementDifficulty.OTHER),

    LEGENDARY_SUPPORT(1, "Aquire the Legendary member's rank from rank chooser.", "1 achievement point.", 1,
            new Item[]{new Item(1907, 10)}, AchievementDifficulty.OTHER),

    PLATINUM_SUPPORT(1, "Aquire the Platinum member's rank from rank chooser.", "1 achievement point.", 1,
            new Item[]{new Item(1907, 12)}, AchievementDifficulty.OTHER),

    TITANIUM_SUPPORT(1, "Aquire the Titanium member's rank from rank chooser.", "1 achievement point.", 1,
            new Item[]{new Item(1907, 15)}, AchievementDifficulty.OTHER),

    DIAMOND_SUPPORT(1, "Aquire the Diamond member's rank from rank chooser.", "1 achievement point.", 1,
            new Item[]{new Item(1907, 25)}, AchievementDifficulty.OTHER),

    GIFT_OF_PEACE(1, "Search the Gift of peace in Stronghold security.", "1 achievement point.", 1,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.OTHER),

    GRAIN_OF_PLENTY(1, "Search the Grain of plenty in Stronghold security.", "1 achievement point.", 1,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.OTHER),

    BOX_OF_HEALTH(1, "Search the Box of health in Stronghold security.", "1 achievement point.", 1,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.OTHER),

    CRADLE_OF_LIFE(1, "Search the Cradle of life in Stronghold security.", "1 achievement point.", 1,
            new Item[]{new Item(6199, 1)}, AchievementDifficulty.OTHER),

    CHRISTMAS_2019(1, "Complete 2019 Christmas Event.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.NULL_3058, 1)}, AchievementDifficulty.OTHER),

    CHRISTMAS_2020(1, "Complete 2020 Christmas Event.", "1 achievement point.", 1,
                           new Item[]{new Item(ItemID.MYSTERY_BOX, 3)}, AchievementDifficulty.OTHER),

    CHRISTMAS_2021(1, "Complete 2021 Christmas Event.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.MYSTERY_BOX, 3)}, AchievementDifficulty.OTHER),

    CHRISTMAS_2022(1, "Complete 2022 Christmas Event.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.MYSTERY_BOX, 3),  new Item(15276, 1),  new Item(26316, 1)}, AchievementDifficulty.OTHER),

    HALLOWEEN_2022(1, "Complete 2022 Halloween Event.", "1 achievement point.", 1,
            new Item[]{new Item(ItemID.MYSTERY_BOX, 3), new Item(22684, 1)}, AchievementDifficulty.OTHER),




    ;

    /**
     * The amount of times to complete
     */
    private int amount;

    /**
     * The description
     */
    private String description;

    /**
     * The reward
     */
    private String reward;

    /**
     * The points reward
     */
    private int pointsReward;

    /**
     * The item reward
     */
    private Item[] itemReward;

    /**
     * The difficulty
     */
    private AchievementDifficulty difficulty;

    /**
     * Represents an achievement
     *
     * @param amount       the amount
     * @param description  the description
     * @param reward       the reward
     * @param pointsReward the points reward
     * @param itemReward   the item reward
     * @param difficulty   the difficulty
     */
    AchievementType(int amount, String description, String reward, int pointsReward, Item[] itemReward,
                    AchievementDifficulty difficulty) {
        this.setAmount(amount);
        this.setDescription(description);
        this.setReward(reward);
        this.setPointsReward(pointsReward);
        this.setItemReward(itemReward);
        this.setDifficulty(difficulty);
    }


    /**
     * Any other time of reward
     *
     * @param player the player
     */
    public void reward(Player player) {

    }

    /**
     * Sets the amount
     *
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Sets the amount
     *
     * @param amount the amount
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * Sets the description
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the reward
     *
     * @return the reward
     */
    public String getReward() {
        return reward;
    }

    /**
     * Sets the reward
     *
     * @param reward the reward
     */
    public void setReward(String reward) {
        this.reward = reward;
    }

    /**
     * Sets the pointsReward
     *
     * @return the pointsReward
     */
    public int getPointsReward() {
        return pointsReward;
    }

    /**
     * Sets the pointsReward
     *
     * @param pointsReward the pointsReward
     */
    public void setPointsReward(int pointsReward) {
        this.pointsReward = pointsReward;
    }

    /**
     * Sets the itemReward
     *
     * @return the itemReward
     */
    public Item[] getItemReward() {
        return itemReward;
    }

    /**
     * Sets the itemReward
     *
     * @param itemReward the itemReward
     */
    public void setItemReward(Item[] itemReward) {
        this.itemReward = itemReward;
    }

    /**
     * Sets the difficulty
     *
     * @return the difficulty
     */
    public AchievementDifficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Sets the difficulty
     *
     * @param difficulty the difficulty
     */
    public void setDifficulty(AchievementDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public static final AchievementType[] VALUES = values();

    public String getName() {
        return Misc.ucFirst(name()).replaceAll("_", " ");
    }
}
