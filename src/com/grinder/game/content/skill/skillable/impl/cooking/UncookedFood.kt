@file:JvmName("UncookedFood")
package com.grinder.game.content.skill.skillable.impl.cooking

import com.grinder.game.definition.ItemDefinition
import com.grinder.util.ItemID
import java.util.HashMap

/**
 * Hashmap of all food that is ready to be cooked on a range, or a fire.
 */
val cookReadyFood = HashMap<Int, Uncooked>()

/**
 * Holds information about food that is ready to be prepared.
 *
 * @param uncooked itemId of uncooked food.
 * @param cookId itemId of cooked food.
 * @param burnID itemId of burned item, -1 for never burn.
 * @param req_level Required cooking level.
 * @param exp Amount of exp given upon success.
 * @param burnLevel Level of which burning stops.
 * @param message The message upon a successful cook.
 */
enum class Uncooked(val uncooked: Int, val cookId: Int, val burnID: Int, val req_level: Int, val exp: Int,val burnLevel:Int=req_level+33,
    val message:String="You manage to cook a ${ItemDefinition.forId(cookId).name}") {

    // if the burnt version id == -1 then that means it never gets burnt no matter what.
    // make sure to add support for cooking cape / cooking gauntlets / never burn limit
    MEAT(ItemID.RAW_BEEF, ItemID.COOKED_MEAT, ItemID.BURNT_MEAT, 1, 30),
    BURNT_MEAT(ItemID.COOKED_MEAT, ItemID.BURNT_MEAT, ItemID.BURNT_MEAT, 1, 0),
    RAT_MEAT(ItemID.RAW_RAT_MEAT, ItemID.COOKED_MEAT, ItemID.BURNT_MEAT, 1, 30),
    SHRIMP(ItemID.RAW_SHRIMPS, ItemID.SHRIMPS, ItemID.BURNT_FISH, 1, 30),
    CHICKEN(ItemID.RAW_CHICKEN, ItemID.COOKED_CHICKEN, ItemID.BURNT_CHICKEN, 1, 30),
    RABBIT(ItemID.RAW_RABBIT, ItemID.COOKED_RABBIT, ItemID.BURNT_RABBIT, 1, 30),
    ANCHOVIES(ItemID.RAW_ANCHOVIES, ItemID.ANCHOVIES, ItemID.BURNT_FISH, 1, 30),
    SARDINE(ItemID.RAW_SARDINE, ItemID.SARDINE, ItemID.BURNT_FISH_9, 1, 40),
    BREAD(2307, 2309, 2311, 1, 40),
    //TODO KARBWAM has 2 burn ids.
    POISON_KARABWAM(ItemID.RAW_KARAMBWAN, ItemID.COOKED_KARAMBWAN_3, ItemID.POISON_KARAMBWAN, 1, 80),
    UGTHANKI(ItemID.RAW_UGTHANKI_MEAT, ItemID.UGTHANKI_MEAT, ItemID.BURNT_MEAT, 1, 40),
    HERRING(ItemID.RAW_HERRING, ItemID.HERRING, ItemID.BURNT_FISH_5, 5, 50),
    POTATO(1942,6701,6699,7,15),
    MACKEREL(ItemID.RAW_MACKEREL, ItemID.MACKEREL, ItemID.BURNT_FISH_5, 10, 60),
    REDBERRY_PIE(2321, 2325, 2329,10, 78),
    CRUNCHIES(ItemID.RAW_CRUNCHIES, ItemID.HALF_BAKED_CRUNCHY, ItemID.BURNT_CRUNCHIES, 10, 30,
            message="You half-cook the crunchies."),
    TOAD_CRUNCHIES(9581, 9582, ItemID.BURNT_CRUNCHIES, 10, 0,
            message="You bake the crunchies."),
    BIRD(ItemID.SKEWERED_BIRD_MEAT, ItemID.ROAST_BIRD_MEAT, ItemID.BURNT_BIRD_MEAT, 11, 60), //TODO: REQUIRES TO ADD RAW BIRD MEAT TO BE USED WITH IRON SPIT ITEM
    THIN_SNAIL(ItemID.THIN_SNAIL, ItemID.THIN_SNAIL_MEAT, ItemID.BURNT_SNAIL, 12, 70),
    SCRAMBLE_EGG(1944,7078,7090,13,50),
    SPICY_CRUNCHIES(9579, 9580, ItemID.BURNT_CRUNCHIES, 12, 0,
            message="You bake the crunchies."),
    WORM_CRUNCHIES(9583, 9584, ItemID.BURNT_CRUNCHIES, 14, 0,
            message="You bake the crunchies."),
    TROUT(ItemID.RAW_TROUT, ItemID.TROUT, ItemID.BURNT_FISH_3, 15, 70),
    CHOC_CRUNCHIES(9577, 9578, ItemID.BURNT_CRUNCHIES, 16, 0,
            message="You bake the crunchies."),
    SPIDER_STICK(ItemID.SPIDER_ON_STICK, ItemID.SPIDER_ON_STICK_3, -1, 16, 80), // No burnt version found
    SPIDER_SHAFT(ItemID.SPIDER_ON_SHAFT, ItemID.SPIDER_ON_SHAFT_3, -1, 16, 80),
    ROAST_RABBIT(ItemID.RAW_RABBIT, ItemID.ROAST_RABBIT, ItemID.BURNT_RABBIT, 16, 70),
    LEAN_SNAIL(ItemID.LEAN_SNAIL, ItemID.LEAN_SNAIL_MEAT, ItemID.BURNT_SNAIL, 17, 80),
    COD(ItemID.RAW_COD, ItemID.COD, ItemID.BURNT_FISH_3, 18, 75),
    PIKE(ItemID.RAW_PIKE, ItemID.PIKE, ItemID.BURNT_FISH_3, 20, 80),
    MEAT_PIE(2319, 2327, 2329,20, 110),
    ROAST_BEAST(ItemID.RAW_BEAST_MEAT, ItemID.ROAST_BEAST_MEAT, ItemID.BURNT_BEAST_MEAT, 21, 83),
    CRAB(ItemID.CRAB_MEAT, ItemID.COOKED_CRAB_MEAT, ItemID.BURNT_CRAB_MEAT, 21, 100),
    FAT_SNAIL(ItemID.FAT_SNAIL, ItemID.FAT_SNAIL_MEAT, ItemID.BURNT_SNAIL, 22, 95),
    BATTA(ItemID.RAW_BATTA, ItemID.HALF_BAKED_BATTA, ItemID.BURNT_BATTA, 25, 0,
            message="You half-cook the batta."),
    SALMON(ItemID.RAW_SALMON, ItemID.SALMON, ItemID.BURNT_FISH_3, 25, 90),
    STEW(1997,2001, ItemID.BURNT_STEW,25,117),
    FRUIT_BATTA(9478, 9479, ItemID.BURNT_BATTA, 25, 0,
            message = "You bake the batta."),
    TOAD_BATTA(9482, 2258, ItemID.BURNT_BATTA, 26, 82,
            message = "You bake the batta."),
    WORM_BATTA(9480, 9481, ItemID.BURNT_BATTA, 27, 0,
            message = "You bake the batta."),
    SLIMY_EEL(ItemID.RAW_SLIMY_EEL, ItemID.COOKED_SLIMY_EEL, ItemID.BURNT_EEL, 28, 95),
    SWEETCORN(5986,5988,5990,28,104),
    VEGETABLE_BATTA(9485, 9486, ItemID.BURNT_BATTA, 28, 0,
            message = "You bake the batta."),
    CHEESE_BATTA(9483, 9484, ItemID.BURNT_BATTA, 29, 0,
        message = "You bake the batta."),
    MUD_PIE(19177,7170,2329,29,128),
    TUNA(ItemID.RAW_TUNA, ItemID.TUNA, ItemID.BURNT_FISH_7, 30, 100),
    GNOMEBOWL(ItemID.RAW_GNOMEBOWL, ItemID.HALF_BAKED_BOWL, ItemID.BURNT_GNOMEBOWL, 30, 0,
            message="You half-cook the gnome bowl."),
    WORM_HOLE(9559, 9560, ItemID.BURNT_GNOMEBOWL, 30, 0,
            message="You bake the gnome bowl."),
    APPLE_PIE(2317,2323,2329,30,130),
    KARABWAM(ItemID.RAW_KARAMBWAN, ItemID.COOKED_KARAMBWAN, ItemID.BURNT_KARAMBWAN, 30, 190),
    CHOMPY(ItemID.RAW_CHOMPY, ItemID.COOKED_CHOMPY, ItemID.BURNT_CHOMPY, 30, 100),
    FISHCAKE(ItemID.RAW_FISHCAKE, ItemID.COOKED_FISHCAKE, ItemID.BURNT_FISHCAKE, 31, 100),
    GARDEN_PIE(7176,7178,2329,34,138),
    RAINBOW_FISH(ItemID.RAW_RAINBOW_FISH, ItemID.RAINBOW_FISH, ItemID.BURNT_RAINBOW_FISH, 35, 110, 63),
    PIZZA(2287,2289,2305,35,143),
    VEGETABLE_BALL(9561, 9562, ItemID.BURNT_GNOMEBOWL, 35, 0,
            message="You bake the gnome bowl."),
    CAVE_EEL(ItemID.RAW_CAVE_EEL, ItemID.CAVE_EEL, ItemID.BURNT_CAVE_EEL, 38, 115),
    LOBSTER(ItemID.RAW_LOBSTER, ItemID.LOBSTER, ItemID.BURNT_LOBSTER, 40, 120, 70),
    CAKE(ItemID.UNCOOKED_CAKE, ItemID.CAKE, ItemID.BURNT_CAKE, 40,180),
    TANGLED_TOADS_LEG(9558, 2187, ItemID.BURNT_GNOMEBOWL, 42, 105,
            message="You bake the gnome bowl."),
    JUBBLY(ItemID.RAW_JUBBLY, ItemID.COOKED_JUBBLY, ItemID.BURNT_JUBBLY, 41, 160),
    CHOPPED_ONION(1957,1871,7092,42,60),
    CHOCLATE_BOMB(9563, 9564, ItemID.BURNT_GNOMEBOWL, 42, 0,
            message="You bake the gnome bowl."),
    BASS(ItemID.RAW_BASS, ItemID.BASS, ItemID.BURNT_FISH_7, 43, 130),
    SWORDFISH(ItemID.RAW_SWORDFISH, ItemID.SWORDFISH, ItemID.BURNT_SWORDFISH, 45, 140, 86),
    MUSHROOMS(6004,7082,7094,46,60), //TODO: https://oldschool.runescape.wiki/w/Sliced_mushrooms
    FISH_PIE(7186,7188,2329,47,164),
    BOTANICAL_PIE(19656,19662,2329,52,180),
    LAVA_EEL(ItemID.RAW_LAVA_EEL, ItemID.LAVA_EEL, -1, 53, 30), // They never burn
    PITTA(1863, 1865, 1867, 58, 40),
    MUSHROOM_PIE(21684,21690,2329,60,200),
    CURRY(2009,2011,2013,60,280),
    MONKFISH(ItemID.RAW_MONKFISH, ItemID.MONKFISH, ItemID.BURNT_MONKFISH, 62,150, 92),
    ADMIRAL_PIE(19184,19185,2329,70,210),
    DRAGONFRUIT_PIE(22789,22795,2329,73,220),
    SHARK(ItemID.RAW_SHARK, ItemID.SHARK, ItemID.BURNT_SHARK, 80, 210, 100),
    SEA_TURTLE(ItemID.RAW_SEA_TURTLE, ItemID.SEA_TURTLE, ItemID.BURNT_SEA_TURTLE, 82, 211),
    ANGLERFISH(ItemID.RAW_ANGLERFISH, ItemID.ANGLERFISH, ItemID.BURNT_ANGLERFISH,84,230, 104),
    WILD_PIE(7206,7208,2329,85,240),
    DARK_CRAB(ItemID.RAW_DARK_CRAB, ItemID.DARK_CRAB, ItemID.BURNT_CRAB_MEAT,90,215),
    MANTA_RAY(ItemID.RAW_MANTA_RAY, ItemID.MANTA_RAY, ItemID.BURNT_MANTA_RAY,91,216),
    BIG_SHARK(15701, 15890, 15703, 92, 280, 120),
    PADDLE_FISH(15708, 15706, 23873, 96, 248, 125),
    SUMMER_PIE(7216,7218,2329,95,260),

    // RAIDS
    PYSK_FISH(20855, 20856, 20854,1,10),
    SUPHI_FISH(20857, 20858, 20854,15,13),
    LECKISH_FISH(20859, 20860, 20854,30,16),
    BRAWK_FISH(20861, 20862, 20854,45,19),
    MYCIL_FISH(20863,20864, 20854,60,22),
    ROQED_FISH(20865, 20866, 20854,75,25),
    KYREN_FISH(20867, 20868, 20854,90,28),

    GUANIC_BAT(20870, 20871,20869,10, 10),
    PRAEL_BAT(20872, 20873, 20869,15,13),
    GIRAL_BAT(20874, 20875, 20869,30,16),
    PHLUXIA_BAT(20876, 20877, 20869,45,19),
    KRYKET_BAT(20878, 20879, 20869,60,22),
    MURNG_BAT(20880, 20881, 20869,75,25),
    PSYKK_BAT(20882, 20883, 20869,90,28);

    companion object {
        init {
            for (meat in values()) {
                cookReadyFood[meat.uncooked] = meat
            }
        }
    }
}