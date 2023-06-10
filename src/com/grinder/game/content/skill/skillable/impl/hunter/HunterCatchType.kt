package com.grinder.game.content.skill.skillable.impl.hunter

import com.grinder.game.content.skill.skillable.impl.hunter.HunterTechniqueType.*
import com.grinder.game.content.skill.skillable.impl.hunter.HunterToolType.*
import com.grinder.game.model.Animation
import com.grinder.game.model.item.Item
import com.grinder.util.ItemID
import com.grinder.util.NpcID

/**
 * The enumerated type contains the types of npcs that can be hunted.
 *
 * @param requiredLevel     the hunter level required to make a catch
 * @param experienceGain    the xp gained from succeeding in making a catch
 * @param npcId             the id of the npc belonging to this type
 * @param loot              an optional [Item] given when made a successful catch
 * @param investigate       an optional [Animation] for npcs investigating a trap
 * @param escaped           an optional [Animation] for npcs escaping a trap
 * @param tool              an optional [HunterToolType] specifying pre-requisites for making a catch
 * @param technique         the [HunterTechniqueType] required to make a catch
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   29/11/2019
 * @version 1.0
 */
enum class HunterCatchType(
        val requiredLevel: Int,
        val experienceGain: Double,
        val npcId: Int,
        val loot: Item? = null,
        val investigate: Animation? = null,
        val caught: Animation? = null,
        val escaped: Animation? = null,
        val tool: HunterToolType = NONE,
        val technique: HunterTechniqueType
) {

    /**
     * Kebbits
     */

    SPOTTED_KEBBIT(43, 104.0,
            NpcID.SPOTTED_KEBBIT,
            technique = FALCONRY),
    DARK_KEBBIT(57, 132.0,
            NpcID.DARK_KEBBIT,
            technique = FALCONRY),
    DASHING_KEBBIT(69, 156.0,
            NpcID.DASHING_KEBBIT,
            technique = FALCONRY),

    /**
     * Implings
     */

    BABY_IMPLING(17, 20.0,
            NpcID.BABY_IMPLING,
            tool = BABY_IMPLING_JAR,
            technique = IMPLINGS),
    YOUNG_IMPLING(22, 224.0,
            NpcID.YOUNG_IMPLING,
            tool = YOUNG_IMPLING_JAR,
            technique = IMPLINGS),
    GOURMET_IMPLING(28, 244.0,
            NpcID.GOURMET_IMPLING,
            tool = GOURMET_IMPLING_JAR,
            technique = IMPLINGS),
    EARTH_IMPLING(36, 274.0,
            NpcID.EARTH_IMPLING,
            tool = EARTH_IMPLING_JAR,
            technique = IMPLINGS),
    ESSENCE_IMPLING(42, 294.0,
            NpcID.ESSENCE_IMPLING,
            tool = ESSENCE_IMPLING_JAR,
            technique = IMPLINGS),
    ECLECTIC_IMPLING(50, 324.0,
            NpcID.ECLECTIC_IMPLING,
            tool = ECLECTIC_IMPLING_JAR,
            technique = IMPLINGS),
    NATURE_IMPLING(58, 364.0,
            NpcID.NATURE_IMPLING,
            tool = NATURE_IMPLING_JAR,
            technique = IMPLINGS),
    MAGPIE_IMPLING(65, 544.0,
            NpcID.MAGPIE_IMPLING,
            tool = MAGPIE_IMPLING_JAR,
            technique = IMPLINGS),
    NINJA_IMPLING(74, 604.0,
            NpcID.NINJA_IMPLING,
            tool = NINJA_IMPLING_JAR,
            technique = IMPLINGS),
    DRAGON_IMPLING(83, 754.0,
            NpcID.DRAGON_IMPLING,
            tool = DRAGON_IMPLING_JAR,
            technique = IMPLINGS),

    /**
     * Butterflies
     */

    RUBY_HARVEST(15, 244.0,
            NpcID.RUBY_HARVEST,
            tool = RUBY_HARVEST_HARVEST,
            technique = BUTTERFLY_NETTING),
    SAPPHIRE_GLACIALIS(25, 344.0,
            NpcID.SAPPHIRE_GLACIALIS,
            tool = SAPPHIRE_GLACIALIS_HARVEST,
            technique = BUTTERFLY_NETTING),
    SNOWY_KNIGHT(35, 444.0,
            NpcID.SNOWY_KNIGHT,
            tool = SNOWY_KNIGHT_HARVEST,
            technique = BUTTERFLY_NETTING),
    BLACK_WARLOCK(45, 54.0,
            NpcID.BLACK_WARLOCK,
            tool = BLACK_WARLOCK_HARVEST,
            technique = BUTTERFLY_NETTING),

    /**
     * Birds
     */

    CRIMSON_SWIFT(1, 39.0,
            NpcID.CRIMSON_SWIFT,
            investigate = Animation(5172),
            caught = Animation(5171),
            escaped = Animation(5173),
            tool = BIRD_SNARE,
            technique = TRAP),
    GOLDEN_WARBLER(5, 47.0,
            NpcID.GOLDEN_WARBLER,
            investigate = Animation(5172),
            caught = Animation(5171),
            escaped = Animation(5173),
            tool = BIRD_SNARE,
            technique = TRAP),
    COPPER_LONGTAIL(9, 61.0,
            NpcID.COPPER_LONGTAIL,
            investigate = Animation(5172),
            caught = Animation(5171),
            escaped = Animation(5173),
            tool = BIRD_SNARE,
            technique = TRAP),
    CERULEAN_TWITCH(11, 64.0,
            NpcID.CERULEAN_TWITCH,
            investigate = Animation(5172),
            caught = Animation(5171),
            escaped = Animation(5173),
            tool = BIRD_SNARE,
            technique = TRAP),
    TROPICAL_WAGTAIL(19, 95.0,
            NpcID.TROPICAL_WAGTAIL,
            investigate = Animation(5172),
            caught = Animation(5171),
            escaped = Animation(5173),
            tool = BIRD_SNARE,
            technique = TRAP),

    /**
     * Chinchompas
     */

    CHINCHOMPA(53, 198.4,
            NpcID.CHINCHOMPA,
            loot = Item(ItemID.BLACK_CHINCHOMPA),
            tool = BOX_TRAP,
            technique = TRAP),
    RED_CHINCHOMPA(63, 265.0,
            NpcID.CARNIVOROUS_CHINCHOMPA,
            loot = Item(ItemID.BLACK_CHINCHOMPA),
            tool = BOX_TRAP,
            technique = TRAP),
    BLACK_CHINCHOMPA(73, 315.0,
            NpcID.BLACK_CHINCHOMPA,
            loot = Item(ItemID.BLACK_CHINCHOMPA),
            tool = BOX_TRAP,
            technique = TRAP),

    /**
     * Ferret
     */

    FERRET(27, 155.0,
            NpcID.FERRET,
            loot = Item(ItemID.FERRET),
            tool = BOX_TRAP,
            technique = TRAP),

    /**
     * Rabbits
     */

    WHITE_RABBIT(27, 144.0,
            NpcID.RABBIT,
            tool = RABBIT_SNARE,
            technique = TRAP),
    KID_RABBIT(34, 210.0,
            NpcID.RABBIT_3421,
            tool = RABBIT_SNARE,
            technique = TRAP),
    LIME_RABBIT(54, 180.0,
            NpcID.RABBIT_1853,
            tool = RABBIT_SNARE,
            technique = TRAP),
    MOTHER_RABBIT(64, 290.0,
            NpcID.RABBIT_3422,
            tool = RABBIT_SNARE,
            technique = TRAP),
    DAD_RABBIT(74, 380.0,
            NpcID.RABBIT_3420,
            tool = RABBIT_SNARE,
            technique = TRAP)


    ;

    companion object {
        val map = values().map { Pair(it.npcId, it) }.toMap()
    }
}