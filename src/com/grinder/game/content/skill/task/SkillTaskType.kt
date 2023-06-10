package com.grinder.game.content.skill.task

import com.grinder.game.content.skill.skillable.impl.woodcutting.TreeType
import com.grinder.util.ItemID
import com.grinder.util.ObjectID

/**
 * Handles skill tasks
 */
enum class SkillTaskType(
        /**
         * The skilling tasks
         */
        var task: Array<SkillingTask>, location: String) {
    THIEVING(arrayOf<SkillingTask>(SkillingTask(intArrayOf(ObjectID.BAKERS_STALL), 1, "steal from the Bakery stall"),
            SkillingTask(intArrayOf(ObjectID.SILK_STALL_3), 20, "steal from the Silk stall"),
            SkillingTask(intArrayOf(ObjectID.FUR_STALL_4, ObjectID.FUR_STALL, ObjectID.FUR_STALL_2, ObjectID.FUR_STALL_3), 42, "steal from the Fur stall"),
            SkillingTask(intArrayOf(ObjectID.SILVER_STALL_3), 65, "steal from the Silver stall"),
            SkillingTask(intArrayOf(ObjectID.GEM_STALL_4), 75, "steal from the Gem stall"),
            SkillingTask(intArrayOf(ObjectID.CRAFTING_STALL), 99, "steal from the Miscellaneous stall")),
            "Right over there, west of the bank"),
    WOODCUTTING(arrayOf<SkillingTask>(SkillingTask(intArrayOf(TreeType.NORMAL.logId), 1, "cut logs from Regular trees"),
            SkillingTask(intArrayOf(TreeType.OAK.logId), 15, "cut logs from Oak trees"),
            SkillingTask(intArrayOf(TreeType.WILLOW.logId), 30, "cut logs from Willow trees"),
            SkillingTask(intArrayOf(TreeType.MAPLE.logId), 45, "cut logs from Maples trees"),
            SkillingTask(intArrayOf(TreeType.YEW.logId), 60, "cut logs from Yew trees"),
            SkillingTask(intArrayOf(TreeType.MAGIC.logId), 75, "cut logs from Magic trees"),
            SkillingTask(intArrayOf(TreeType.REDWOOD.logId), 90, "cut logs from Redwood trees")), "Camelot, Draynor Village, or Woodcutting Guild"),
    MINING(arrayOf<SkillingTask>(SkillingTask(intArrayOf(436), 1, "mine some copper ore"),
            SkillingTask(intArrayOf(438), 1, "mine some Tin ore"), SkillingTask(intArrayOf(440), 15, "mine some Iron ore"),
            SkillingTask(intArrayOf(453), 30, "mine some Coal"), SkillingTask(intArrayOf(444), 40, "mine some Gold ore"),
            SkillingTask(intArrayOf(447), 55, "mine some Mithril ore"),
            SkillingTask(intArrayOf(449), 70, "mine some Adamantite ore"),
            SkillingTask(intArrayOf(451), 85, "mine some Runite ore")), "Neitznot or Falador Mine"),
    SMITHING(arrayOf<SkillingTask>(SkillingTask(intArrayOf(1205), 1, "smith some Bronze daggers"),
            SkillingTask(intArrayOf(1321), 5, "smith some Bronze scimitars"),
            SkillingTask(intArrayOf(1117), 16, "smith some Bronze platebodies"),
            SkillingTask(intArrayOf(1153), 22, "make some Iron full helms"),
            SkillingTask(intArrayOf(1081), 31, "smith some Iron plateskirts"),
            SkillingTask(intArrayOf(1424), 32, "smith some Steel maces"),
            SkillingTask(intArrayOf(1105), 41, "smith some Steel chainbodies"),
            SkillingTask(intArrayOf(1119), 48, "smith some Steel platebodies"),
            SkillingTask(intArrayOf(1285), 54, "smith some Mithril swords"),
            SkillingTask(intArrayOf(1197), 61, "smith some Mithril kiteshields"),
            SkillingTask(intArrayOf(1121), 68, "smith some Mithril platebodies"),
            SkillingTask(intArrayOf(823), 74, "smith some Adamant dart tips"),
            SkillingTask(intArrayOf(1371), 80, "smith some Adamant battleaxe"),
            SkillingTask(intArrayOf(1073), 86, "smith some Adamant platelegs"),
            SkillingTask(intArrayOf(1073), 86, "smith some Adamant platelegs"),
            SkillingTask(intArrayOf(1163), 92, "smith some Rune full helm"),
            SkillingTask(intArrayOf(1113), 96, "smith some Rune chainbody"),
            SkillingTask(intArrayOf(1201), 97, "smith some Rune kiteshield"),
            SkillingTask(intArrayOf(1319), 99, "smith some Rune 2h"),
            SkillingTask(intArrayOf(1093), 99, "smith some Rune plateskirt"),
            SkillingTask(intArrayOf(1079), 99, "smith some Rune platelegs"),
            SkillingTask(intArrayOf(1127), 99, "smith some Rune platebody")), "Neitznot or Falador Mine"),
    FISHING(arrayOf<SkillingTask>(SkillingTask(intArrayOf(317), 1, "small net some shrimp"),
            SkillingTask(intArrayOf(327), 5, "bait some Sardine"), SkillingTask(intArrayOf(345), 10, "bait some Herring"),
            SkillingTask(intArrayOf(341), 23, "bait some Cod"), SkillingTask(intArrayOf(349), 25, "bait some Pike"),
            SkillingTask(intArrayOf(3379), 28, "bait some Slimey eel"), SkillingTask(intArrayOf(331), 30, "lure some Salmon"),
            SkillingTask(intArrayOf(359), 40, "harpoon some Tuna"), SkillingTask(intArrayOf(377), 40, "cage some Lobsters"),
            SkillingTask(intArrayOf(371), 50, "harpoon some Swordfish"), SkillingTask(intArrayOf(2148), 54, "bait some Lava eel"),
            SkillingTask(intArrayOf(383), 76, "harpoon some Sharks"), SkillingTask(intArrayOf(13439), 82, "bait some Anglerfish")), "Catherby or Wilderness Resource Area"),
    CRAFTING(
        arrayOf<SkillingTask>(SkillingTask(intArrayOf(1059), 1, "craft Leather gloves"),
            SkillingTask(intArrayOf(1609), 1, "cut opal"),
            SkillingTask(intArrayOf(1607), 20, "cut sapphire"),
            SkillingTask(intArrayOf(1605), 27, "cut emerald"),
        SkillingTask(intArrayOf(1131), 28, "craft Hardleather body"),
            SkillingTask(intArrayOf(1603), 34, "cut ruby"),
        //SkillingTask(intArrayOf(1133), 41, "craft Studded body"),
        //SkillingTask(intArrayOf(22269), 41, "craft Leather shield"),
            SkillingTask(intArrayOf(1601), 43, "cut diamond"),
       // SkillingTask(intArrayOf(1097), 44, "craft Studded chaps"),
        SkillingTask(intArrayOf(6324), 51, "craft Snakeskin chaps"),
        SkillingTask(intArrayOf(6322), 53, "craft Snakeskin body"),
            SkillingTask(intArrayOf(1615), 55, "cut dragonstone"),
        SkillingTask(intArrayOf(1099), 60, "craft Green d'hide chaps"),
        //SkillingTask(intArrayOf(22275), 62, "craft Green dragonhide shield"),
        SkillingTask(intArrayOf(1135), 63, "craft Green d'hide body"),
            SkillingTask(intArrayOf(6573), 67, "cut onyx"),
        SkillingTask(intArrayOf(2493), 68, "craft Blue d'hide chaps"),
        //SkillingTask(intArrayOf(22278), 69, "craft Blue dragonhide shield"),
        SkillingTask(intArrayOf(2499), 71, "craft Blue d'hide body"),
        SkillingTask(intArrayOf(2495), 75, "craft Red d'hide chaps"),
        //SkillingTask(intArrayOf(22281), 76, "craft Red dragonhide shield"),
        SkillingTask(intArrayOf(2501), 77, "craft Red d'hide body"),
        SkillingTask(intArrayOf(2497), 82, "craft Black d'hide chaps"),
        //SkillingTask(intArrayOf(22284), 83, "craft Black dragonhide shield"),
        SkillingTask(intArrayOf(2503), 84, "craft Black d'hide body"),
        SkillingTask(intArrayOf(19493), 89, "cut zenyte")), "Anywhere"),
    FIREMAKING(
        arrayOf<SkillingTask>(SkillingTask(intArrayOf(1511), 1, "burn Logs"),
            SkillingTask(intArrayOf(1521), 15, "burn Oak logs"),
            SkillingTask(intArrayOf(1519), 30, "burn Willow logs"),
            SkillingTask(intArrayOf(6333), 35, "burn Teak logs"),
            SkillingTask(intArrayOf(1517), 45, "burn Maple logs"),
            SkillingTask(intArrayOf(6332), 50, "burn Mahogany logs"),
            SkillingTask(intArrayOf(1515), 60, "burn Yew logs"),
            SkillingTask(intArrayOf(1513), 75, "burn Magic logs"),
            SkillingTask(intArrayOf(19669), 90, "burn Redwood logs")), "Anywhere"),
    HERBLORE(
        arrayOf<SkillingTask>(SkillingTask(intArrayOf(ItemID.GUAM_LEAF), 1, "clean Guam leaf"),
            SkillingTask(intArrayOf(ItemID.ATTACK_POTION_3_), 3, "create Attack potion"),
            SkillingTask(intArrayOf(ItemID.RANARR_WEED), 25, "clean Ranarr weed"),
            SkillingTask(intArrayOf(ItemID.DEFENCE_POTION_3_), 30, "create Defence potion"),
            SkillingTask(intArrayOf(ItemID.AGILITY_POTION_3_), 34, "create Agility potion"),
            SkillingTask(intArrayOf(ItemID.PRAYER_POTION_3_), 38, "create Prayer potion"),
            SkillingTask(intArrayOf(ItemID.AVANTOE), 48, "clean Avantoe"),
            SkillingTask(intArrayOf(ItemID.FISHING_POTION_3_), 50, "create Fishing potion"),
            SkillingTask(intArrayOf(ItemID.SUPER_ENERGY_3_), 52, "create Super energy potion"),
            SkillingTask(intArrayOf(ItemID.KWUARM), 54, "clean Kwuarm"),
            SkillingTask(intArrayOf(ItemID.SUPER_RESTORE_3_), 63, "create Super restore potion"),
            SkillingTask(intArrayOf(ItemID.LANTADYME), 67, "clean Lantadyme"),
            SkillingTask(intArrayOf(ItemID.RANGING_POTION_3_), 73, "create Ranging potion"),
            SkillingTask(intArrayOf(ItemID.TORSTOL), 75, "clean Torstol"),
            SkillingTask(intArrayOf(ItemID.MAGIC_POTION_3_), 76, "create Magic potion"),
            SkillingTask(intArrayOf(ItemID.SARADOMIN_BREW_3_), 81, "create Saradomin brew")), "Anywhere"),
    PRAYER(
        arrayOf<SkillingTask>(SkillingTask(intArrayOf(ItemID.BONES), 1, "bury bones"),
            SkillingTask(intArrayOf(ItemID.BURNT_BONES), 25, "bury Burnt bones"),
            SkillingTask(intArrayOf(ItemID.DRAGON_BONES), 50, "bury Dragon bones"),
            SkillingTask(intArrayOf(ItemID.JOGRE_BONES), 57, "bury Jogre bones"),
            //SkillingTask(intArrayOf(ItemID.ZOGRE_BONES), 65, "bury Zogre bones"),
            SkillingTask(intArrayOf(ItemID.OURG_BONES), 78, "bury Ourg bones"),
            SkillingTask(intArrayOf(ItemID.DAGANNOTH_BONES), 85, "bury Dagannoth bones"),
            SkillingTask(intArrayOf(ItemID.WYVERN_BONES), 92, "bury Wyvern bones"),
            SkillingTask(intArrayOf(ItemID.LAVA_DRAGON_BONES), 92, "bury Lava dragon bones"),
            SkillingTask(intArrayOf(22124), 95, "bury Superior dragon bones")), "Anywhere"),
    COOKING(
        arrayOf<SkillingTask>(SkillingTask(intArrayOf(ItemID.COOKED_MEAT), 1, "cook meat"),
            SkillingTask(intArrayOf(ItemID.TROUT), 25, "cook Trout"),
            SkillingTask(intArrayOf(ItemID.PIKE), 20, "cook Pike"),
            SkillingTask(intArrayOf(ItemID.SALMON), 25, "cook Salmon"),
            SkillingTask(intArrayOf(ItemID.TUNA), 30, "cook Tuna"),
            SkillingTask(intArrayOf(ItemID.LOBSTER), 40, "cook Lobster"),
            SkillingTask(intArrayOf(ItemID.SWORDFISH), 45, "cook Swordfish"),
            SkillingTask(intArrayOf(ItemID.LAVA_EEL), 53, "cook Lava eel"),
            SkillingTask(intArrayOf(ItemID.MONKFISH), 62, "cook Monkfish"),
            SkillingTask(intArrayOf(ItemID.SHARK), 80, "cook Shark"),
            SkillingTask(intArrayOf(ItemID.SEA_TURTLE), 82, "cook Sea turtle"),
            SkillingTask(intArrayOf(ItemID.ANGLERFISH), 84, "cook Anglerfish"),
            SkillingTask(intArrayOf(ItemID.DARK_CRAB), 90, "cook Dark crab"),
            SkillingTask(intArrayOf(ItemID.MANTA_RAY), 92, "cook Manta ray")), "Anywhere"),
    RUNECRAFTING(
        arrayOf<SkillingTask>(SkillingTask(intArrayOf(ItemID.AIR_RUNE), 1, "Air runes"),
            SkillingTask(intArrayOf(ItemID.FIRE_RUNE), 14, "Fire runes"),
            SkillingTask(intArrayOf(ItemID.BODY_RUNE), 20, "Body runes"),
            SkillingTask(intArrayOf(ItemID.COSMIC_RUNE), 27, "Cosmic runes"),
            SkillingTask(intArrayOf(ItemID.CHAOS_RUNE), 35, "Chaos runes"),
            SkillingTask(intArrayOf(ItemID.ASTRAL_RUNE), 40, "Astral runes"),
            SkillingTask(intArrayOf(ItemID.NATURE_RUNE), 44, "Nature runes"),
            SkillingTask(intArrayOf(ItemID.LAW_RUNE), 54, "Law runes"),
            SkillingTask(intArrayOf(ItemID.DEATH_RUNE), 65, "Death runes"),
            SkillingTask(intArrayOf(ItemID.BLOOD_RUNE), 77, "Blood runes"),
            //SkillingTask(intArrayOf(ItemID.SOUL_RUNE), 90, "Soul runes"),
            SkillingTask(intArrayOf(ItemID.WRATH_RUNE), 95, "Wrath runes")), "Runecrafting altar"),
    FLETCHING(
        arrayOf<SkillingTask>(SkillingTask(intArrayOf(ItemID.ARROW_SHAFT), 1, "Arrow shafts"),
            SkillingTask(intArrayOf(ItemID.BRONZE_ARROW), 1, "Bronze arrows"),
            SkillingTask(intArrayOf(ItemID.MAPLE_SHORTBOW), 5, "Maple shortbow"),
            SkillingTask(intArrayOf(ItemID.SHORTBOW), 5, "Shortbow"),
            SkillingTask(intArrayOf(ItemID.IRON_ARROW), 15, "Iron arrows"),

            SkillingTask(intArrayOf(ItemID.IRON_CROSSBOW), 39, "Iron crossbow"),
            SkillingTask(intArrayOf(ItemID.MITHRIL_ARROW), 44, "Mithril arrows"),
            SkillingTask(intArrayOf(ItemID.STEEL_BOLTS), 46, "Steel bolts"),
            SkillingTask(intArrayOf(ItemID.MITHRIL_DART), 52, "Mithril darts"),
            SkillingTask(intArrayOf(ItemID.ADAMANT_ARROW), 60, "Adamant arrows"),
            //SkillingTask(intArrayOf(ItemID.ADAMANT_JAVELIN), 62, "Adamant javelin"),
            SkillingTask(intArrayOf(ItemID.RUNE_CROSSBOW), 69, "Rune crossbow"),
            SkillingTask(intArrayOf(ItemID.RUNE_ARROW), 75, "Rune arrows"),
            SkillingTask(intArrayOf(ItemID.MAGIC_SHORTBOW), 80, "Magic shortbow"),
            SkillingTask(intArrayOf(ItemID.RUNE_DART), 81, "Rune darts"),
            SkillingTask(intArrayOf(ItemID.MAGIC_LONGBOW), 85, "Magic longbow"),
            SkillingTask(intArrayOf(22266), 95, "Redwood shield")), "Anywhere"),
    AGILITY(
        arrayOf<SkillingTask>(SkillingTask(intArrayOf(ItemID.MARK_OF_GRACE), 1, "Mark of grace"),
        SkillingTask(intArrayOf(ItemID.AGILITY_ARENA_TICKET), 50, "Agility arena tickets"),
        SkillingTask(intArrayOf(ItemID.MARK_OF_GRACE), 25, "Mark of grace"),
        SkillingTask(intArrayOf(ItemID.MARK_OF_GRACE), 50, "Mark of grace"),
        SkillingTask(intArrayOf(ItemID.MARK_OF_GRACE), 75, "Mark of grace"),
    SkillingTask(intArrayOf(ItemID.AGILITY_ARENA_TICKET), 70, "Agility arena tickets")), "Agility courses/rooftops")

    ;
    /**
     * Sets the task
     *
     * @return the task
     */
    /**
     * Sets the task
     *
     * @param task the task
     */
    /**
     * Sets the location
     *
     * @return the location
     */
    /**
     * Sets the location
     *
     * @param location the location
     */
    /**
     * The location
     */
    var location: String? = null

    /**
     * Represents the skill tasks
     *
     * @param task     the task
     * @param location the location
     */
    init {
        this.location = location
    }
}