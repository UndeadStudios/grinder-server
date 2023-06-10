package com.grinder.game.model.consumable.edible;

import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents all types of food currently available.
 *
 * @author relex lawl
 * @author Stan van der Bend
 */
public enum Edible {

    ADMIRAL_PIE(new Item(7198), 8),
    ANCHOVIES(new Item(319), 1),
    ONION(new Item(1957), 1),
    ANCHOVY_PIZZA(new Item(2297), 9),
    JUBBLY(new Item(7568), 5),
    ANCHOVY_PIZZA_HALF(new Item(2299), 9),
    ANGLERFISH(new Item(13441), 22),
    BIG_SHARK(new Item(15890), 23), // Customly added, not like on OSRS
    PADDLEFISH(new Item(15706), 24), // Customly added, not like on OSRS
    CAVE_EEL(new Item(5003), 8),
    APPLE_PIE(new Item(2323), 7),
    BAKED_POTATO(new Item(6701), 4),
    BANANA(new Item(1963), 2),
    RED_BANANA(new Item(ItemID.RED_BANANA), 2),
    BANANA_(new Item(18199), 2),
    BANDAGES(new Item(4049), 0),
    BASS(new Item(365), 13),
    BREAD(new Item(2309), 1),
    CABBAGE(new Item(1965), 2),
    CAKE(new Item(1891), 5),
    MINT_CAKE(new Item(ItemID.MINT_CAKE), 15),
    CHEESE(new Item(1985), 2),
    CHILLI_POTATO(new Item(7054), 14),
    CHOCOLATE_CAKE(new Item(1897), 5),
    CHOCOLATE_CAKE_SLICE(new Item(1899), 5),
    COD(new Item(339), 7),
    COOKED_CHICKEN(new Item(2140), 3),
    ROE(new Item(11324), 3),
    FROG_SPAWN(new Item(5004), 3),
    CAVIAR(new Item(11326), 5),
    COOKED_CHOMPY(new Item(2878), 5),
    COOKED_MEAT(new Item(2142), 3),
    MINCED_MEAT(new Item(7070), 2),
    SPICY_MINCED_MEAT(new Item(9996), 3),
    KEBAB_MIX(new Item(1881), 7),
    COOKED_RABBIT(new Item(3228), 4),
    DARK_CRAB(new Item(11936), 22),
    EASTER_EGG(new Item(1961), 50),
    EDIBLE_SEAWEED(new Item(403), 4),
    PUMPKIN(new Item(ItemID.PUMPKIN), 10),
    EGG_POTATO(new Item(7056), 16),
    FIELD_RATION(new Item(7934), 35),
    HALLOWEEN_PUMPKIN(new Item(ItemID.PUMPKIN_24979), 21),
    TRIANGLE_SANDWICH(new Item(ItemID.TRIANGLE_SANDWICH), 35),
    FISH_PIE(new Item(7188), 0),
    GARDEN_PIE(new Item(7178), 6),
    LAVA_EEL(new Item(2149), 11),
    GIANT_CARP(new Item(337), 30),
    HALF_ADMIRAL_PIE(new Item(7200), 5),
    HALF_APPLE_PIE(new Item(2335), 7),
    HALF_FISH_PIE(new Item(7180), 0),
    HALF_GARDEN_PIE(new Item(7180), 6),
    HALF_MEAT_PIE(new Item(2331), 6),
    HALF_REDBERRY_PIE(new Item(2333), 5),
    HALF_SUMMER_PIE(new Item(7220), 11),
    HALF_WILD_PIE(new Item(7210), 11),
    JANGERBERRIES(new Item(247), 2),
    KARAMBWAN(new Item(3144), 18),
    POISON_KARAMBWAN(new Item(3146), 0),
    KEBAB(new Item(1971), 4),
    HERRING(new Item(347), 5),
    LOBSTER(new Item(379), 12),
    MAKAREL(new Item(355), 6),
    MANTA_RAY(new Item(391), 22),
    MEAT_PIE(new Item(2327), 6),
    MEAT_PIZZA(new Item(2293), 14),
    MEAT_PIZZA_HALF(new Item(2295), 14),
    MONKFISH(new Item(7946), 16),
    MUSHROOM_POTATO(new Item(7058), 20),
    ORANGE(new Item(2108), 2),
    PEACH(new Item(6883), 8),
    PIKE(new Item(351), 8),
    PINEAPPLE_CHUNKS(new Item(2116), 2),
    PINEAPPLE_PIZZA(new Item(2301), 11),
    PINEAPPLE_RINGS(new Item(2118), 2),
    PLAIN_PIZA(new Item(2289), 7),
    PLAIN_PIZA_HALF(new Item(2291), 7),
    POTATO(new Item(1942), 1),
    POTATO_WITH_BUTTER(new Item(6703), 14),
    POTATO_WITH_CHEESE(new Item(6705), 16),
    PURPLE_SWEET(new Item(4561), 0),
    PURPLE_SWEETS(new Item(10476), 0),
    STRANGE_FRUIT(new Item(464), 3),
    REDBERRY_PIE(new Item(2325), 5),
    SALMON(new Item(329), 9),
    SARDINE(new Item(325), 4),
    SEA_TURTLE(new Item(397), 21),
    SECOND_CAKE_SLICE(new Item(1893), 5),
    SHARK(new Item(385), 20),
    SHRIMPS(new Item(315), 3),
    SLIMY_EEL(new Item(3381), 8),
    SPINACH_ROLL(new Item(1969), 2),
    SUMMER_PIE(new Item(7218), 11),
    SUPER_KEBAB(new Item(4608), 26),
    SWORDFISH(new Item(373), 14),
    TEA(new Item(712), 1),
    THIRD_CAKE_SLICE(new Item(1895), 5),
    THIRD_CHOCOLATE_CAKE_SLICE(new Item(1901), 5),
    TROUT(new Item(333), 7),
    TUNA(new Item(361), 10),
    CHOPPED_TUNA(new Item(7086), 10),
    CHOPPED_GARLIC(new Item(7074), 1),
    TUNA_POTATO(new Item(7060), 22),
    UGTHANKI_MEAT(new Item(1861), 3),
    CHOPPED_UGTHANKI(new Item(1873), 3),
    UGTHANKI_AND_TOMATO(new Item(1879), 3),
    UGTHANKI_AND_ONION(new Item(1877), 3),
    ONION_AND_TOMATO(new Item(1875), 2),
    CHOPPED_TOMATO(new Item(1869), 1),
    CHOPPED_ONION(new Item(1871), 1),
    MACKEREL(new Item(335), 6),
    BIRD_MEAT(new Item(9980), 6),
    BEAST_MEAT(new Item(9988), 8),
    THIN_SNAIL(new Item(3369), 7),
    CHOMPY_MEAT(new Item(2878), 10),
    STEW(new Item(2003), 10),
    BANANA_STEW(new Item(4016), 12),
    SPICY_STEW(new Item(7479), 14),
    CURRY(new Item(2011), 10),
    NETTLE_TEA(new Item(4239), 3),
    NETTLE_WATER(new Item(4237), 3),
    //JUG_OF_WINE(new Item(1993), 11),
    SPICY_SAUCE(new Item(7072), 10),
    POT_OF_CREAM(new Item(2130), 1),
    DWELLBERRIES(new Item(2126), 2),
    EQUA_LEAVES(new Item(2128), 1),
    TOAD_LEGS(new Item(2152), 3),
    EQUA_TOADS_LEGS(new Item(2154), 3),
    SPICY_TOAD_LEGS(new Item(2156), 5),
    SEASONED_LEGS(new Item(2158), 3),
    KING_WORMS(new Item(2162), 2),
    LIME_SLICES(new Item(2124), 1),
    LIME_CHUNKS(new Item(2122), 1),
    LIME(new Item(2120), 1),
    PINEAPPLE_RING(new Item(2118), 1),
    PINEAPPLE(new Item(2114), 1),
    ORANGE_SLICES(new Item(2112), 1),
    ORANGE_CHUNKS(new Item(2110), 1),
    LEMON_SLICES(new Item(2106), 1),
    LEMON_CHUNKS(new Item(2104), 1),
    CHILI_CON_CARNE(new Item(7062), 5),
    SCRAMBLED_EGG(new Item(7078), 5),
    EGG_AND_TOMATO(new Item(7064), 8),
    COOKED_STEW(new Item(2001), 7),
    COOKED_SWEETCORN(new Item(5988), 10),
    COOKED_SWEETCORN_BOWL(new Item(ItemID.SWEETCORN_3), 10),
    FRIED_ONIONS(new Item(7084), 5),
    FRIED_MUSHROOMS(new Item(7082), 5),
    MUSHROOM_AND_ONION(new Item(7066), 11),
    TUNA_AND_CORN(new Item(7068), 13),
    SPIDER_ON_STICK(new Item(6293), 10),
    SPIDER_ON_SHAFT(new Item(6295), 10),
    LEAN_SNAIL(new Item(3371), 10),
    FAT_SNAIL(new Item(33733), 10),
    COOKED_FISHCAKE(new Item(7530), 12),
    COOKED_JUBBLY(new Item(7570), 6),
    BOTANICAL_PIE(new Item(19662), 12),
    RAINBOW_FISH(new Item(10136), 11),
    HALF_BOTANICAL_PIE(new Item(19659), 12),
    DRAGONFRUIT_PIE(new Item(22795), 20),
    HALF_DRAGONFRUIT_PIE(new Item(22792), 20),
    MUSHROOM_PIE(new Item(21690), 16),
    HALF_MUSHROOM_PIE(new Item(21687), 16),

    // RAIDS
    PYSK_FISH(new Item(20856), 5),
    SUPHI_FISH(new Item(20858), 8),
    LECKISH_FISH(new Item(20860), 11),
    BRAWK_FISH(new Item(20862), 14),
    MYCIL_FISH(new Item(20864), 17),
    ROQED_FISH(new Item(20866), 20),
    KYREN_FISH(new Item(20868), 23),

    GUANIC_BAT(new Item(20871), 5),
    PRAEL_BAT(new Item(20873), 8),
    GIRAL_BAT(new Item(20875), 11),
    PHLUXIA_BAT(new Item(20877), 14),
    KRYKET_BAT(new Item(20879), 17),
    MURNG_BAT(new Item(20881), 20),
    PSYKK_BAT(new Item(20883), 23),




    /*
    TODO: Add system for those below
     */
    // TODO: BLIGHTED POTIONS SYSTEM
    // TODO: DIVINE POTIONS SYSTEM
    // TODO: ADD SUPPORT TO DRINK MIX POTIONS
    // TODO: STAMINA POTIONS SYSTEM (CLIENT SIDED TOO)
        /*
    // BREWING
    CIDER(new Item(5763), 10),
    MATURE_CIDER(new Item(5765), 10),
    DWARVEN_STOUT(new Item(1913), 10),
    DWARVEN_STOUT_M(new Item(5747), 10),
    ASGARNIAN_ALE(new Item(1905), 10),
    ASGARNIAN_ALE_M(new Item(5308), 10),
    GREENMANS_ALE(new Item(1909), 10),
    GREENMANS_ALE_M(new Item(5743), 10),
    WIZARD_MIND_BOMB(new Item(1907), 10),
    MATURE_WMB(new Item(5741), 10),
    DRAGON_BITTER(new Item(1911), 10),
    DRAGON_BITTER_M(new Item(5745), 10),
    MOONLIGHT_MEAD(new Item(2955), 10),
    MOONLIGHT_MEAD_M(new Item(5749), 10),
    AXEMANS_FOLLY(new Item(5751), 10),
    AXEMANS_FOLLY_M(new Item(5753), 10),
    CHEFS_DELIGHT(new Item(5755), 10),
    CHEFS_DELIGHT_M(new Item(5757), 10),
    SLAYERS_RESPITE(new Item(5759), 10),
    SLAYERS_RESPITE_M(new Item(5761), 10),

    VODKA(new Item(2015), 10),
    WHISKY(new Item(2017), 10),
    GIN(new Item(2019), 10),
    RUM(new Item(8940), 10),
    BRAINDEATH_RUM(new Item(7157), 10),
    AHABS_BEER(new Item(6561), 10),
    ASGOLDIAN_ALE(new Item(7508), 10),
    BOTTLE_OF_WINE(new Item(7919), 10),
    HALF_FULL_WINE_JUG(new Item(1989), 10),
    KEG_OF_BEER(new Item(3711), 10),
    KELDA_STOUT(new Item(6118), 10),
    BRANDY(new Item(2021), 10),
    BEER_TAKNDARD(new Item(3803), 10),
    BLOODY_BRACER(new Item(22430), 10),
    GROG(new Item(1915), 10),
    ELVEN_DAWN(new Item(23948), 10),
         */

    // GNOME COOKING
    /*FRUIT_BLAST(new Item(2084), 9),
    PINEAPPLE_PUNCH(new Item(2048), 9),
    WIZARD_BLIZZARD(new Item(2054), 5),
    SHORT_GREEN_GUY(new Item(2080), 5),
    DRUNK_DRAGON(new Item(2092), 5),
    CHOC_SATURDAY(new Item(2074), 5),
    BLURBERRY_SPECIAL(new Item(2064), 7),
    PREMADE_FRUIT_BLAST(new Item(2034), 9),
    PREMADE_PINEAPPLE_PUNCH(new Item(2036), 9),
    PREMADE_WIZARD_BLIZZARD(new Item(2040), 5),
    PREMADE_SGG(new Item(2038), 5), // Short green guy
    PREMADE_DRUNK_DRAGON(new Item(2032), 5),
    PREMADE_CHOCS_DY(new Item(2030), 5), // Chocolate saturday
    PREMADE_BLURBERRY_SPECIAL(new Item(2028), 7),
    */
    ODD_CRUNCHIES(new Item(2197), 7),
    TOAD_CRUNCHIES(new Item(2217), 8),
    SPICY_CRUNCHIES(new Item(2213), 7),
    WORM_CRUNCHIES(new Item(2205), 8),
    CHOCCHIP_CRUNCHIES(new Item(2209), 7),
    FRUIT_BATTA(new Item(2277), 11),
    TOAD_BATTA(new Item(2255), 11),
    WORM_BATTA(new Item(2253), 1110),
    VEGETABLE_BATTA(new Item(2281), 11),
    CHEESE_AND_TOMATO_BATTA(new Item(2259), 11),
    WORM_HOLE(new Item(2191), 12),
    CHOCOLATE_CHIP(new Item(2209), 12),
    VEG_BALL(new Item(21195), 12),
    TANGLED_TOADS_LEGS(new Item(2187), 15),
    CHOCOLATE_BOMB(new Item(1973), 15),
    PREMADE_TOAD_CRUNCHIES(new Item(2243), 8),
    PREMADE_SPICY_CRUNCHIES(new Item(2241), 7), //s'y crunch
    PREMADE_WORM_CRUNCHIES(new Item(2237), 8), // w'm crunh'
    PREMADE_CHOCCHIP_CRUNCHIES(new Item(2239), 7),
    PREMADE_FRUIT_BATTA(new Item(2225), 11),
    PREMADE_TOAD_BATTA(new Item(2221), 11),
    PREMADE_WORM_BATTA(new Item(2219), 1110),
    PREMADE_VEGETABLE_BATTA(new Item(2227), 11),
    PREMADE_CHEESE_AND_TOMATO_BATTA(new Item(2223), 11), // c+t batta
    PREMADE_WORM_HOLE(new Item(2233), 12),
    PREMADE_VEG_BALL(new Item(2235), 12),
    PREMADE_TANGLED_TOADS_LEGS(new Item(2231), 15), //ttl
    PREMADE_CHOCOLATE_BOMB(new Item(2229), 15),
    UGETHANKI_KEBAB(new Item(1883), 25),
    UGETHANKI_KEBAB_2(new Item(1885), 25),
    WILD_PIE(new Item(7208), 11),
    BROWN_CANDY(new Item(24980), 2),
    BLUE_CANDY(new Item(24981), 2),
    WHITE_CANDY(new Item(24982), 2),
    PURPLE_CANDY(new Item(24983), 2),
    RED_CANDY(new Item(24984), 2),
    GREEN_CANDY(new Item(24985), 2),
    BLACK_CANDY(new Item(24986), 2),
    ORANGE_CANDY(new Item(24987), 2),
    PINK_CANDY(new Item(24988), 2);

    public static Map<Integer, Edible> types = new HashMap<>();

    static {
        for (Edible type : Edible.values()) {
            types.put(type.item.getId(), type);
        }
    }

    public String name;
    public Item item;
    private int heal;

    Edible(Item item, int heal) {
        this.item = item;
        this.heal = heal;
        this.name = (toString().toLowerCase().replaceAll("__", "-").replaceAll("_", " "));
    }

    public int getHeal() {
        return heal;
    }
}
