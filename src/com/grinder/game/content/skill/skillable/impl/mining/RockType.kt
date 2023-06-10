package com.grinder.game.content.skill.skillable.impl.mining

import com.grinder.util.ItemID
import com.grinder.util.ObjectID
import java.util.*

/**
 * @author Professor Oak
 * @author Zach (zach@findzach.com)
 * @since 12/17/2020
 * Holds data related to the rocks which can be used to train this skill.
 */
enum class RockType(val objects: IntArray, val requiredLevel: Int, val xpReward: Int, val oreId: Int, val cycles: Int, val respawnTimer: Int, val empty: Int = ObjectID.ROCKS_5) {

    NO_ORES(intArrayOf(2704, 11391, 11385, 11393), 1, -1, -1, -1, -1, ObjectID.ROCKS_5),
    CLAY(intArrayOf(7454, 9711, 9712, 11362, 15503, 15504, 11363, 15505), 1, 5, ItemID.CLAY, 11, 11, ObjectID.ROCKS_5),
    SOFT_CLAY(intArrayOf(34956, 34957), 1, 5, ItemID.SOFT_CLAY, 11, 11, ObjectID.ROCKS_5),
    SOFT_CLAY_2(intArrayOf(36210), 1, 5, ItemID.SOFT_CLAY, 11, 11, ObjectID.ROCKS_36202),
    LIMESTONE(intArrayOf(11382, 11383, 11384), 10, 26, ItemID.LIMESTONE, 11, 13, 11385),
    ESSENCE(intArrayOf(34773), 1, 12, -1, 11, -1, ObjectID.ROCKS_5),
    COPPER(intArrayOf(7484, 9708, 9709, 9710, 10943, 10079, 11161, 11936, 11960, 11961, 11962, 11189, 11190, 11191, 29231, 29230, 2090), 1, 18, 436, 12, 8, ObjectID.ROCKS_5),
    TIN(intArrayOf(7485, 7486, 9714, 9715, 9716, 11933, 11957, 11958, 11959, 11186, 11187, 11188, 2094, 11361, 29227, 11360, 29229), 1, 18, 438, 12, 8, ObjectID.ROCKS_5),
    BLURITE(intArrayOf(11379, 11378), 10, 38, 668, 10, 8, ObjectID.ROCKS_5),
    IRON(intArrayOf(7488, 7455, 9717, 9718, 9719, 2093, 11954, 11955, 11956, 11364, 11365, 29221, 29222, 29223, 36203), 15, 35,440, 13, 15, ObjectID.ROCKS_5),
    SILVER(intArrayOf(2100, 7457, 7490, 2101, 29226, 29225, 11368, 11369), 20, 40, 442, 14, 11, ObjectID.ROCKS_5),
    COAL(intArrayOf(7489, 7456, 5770, 29216, 29215, 29217, 11965, 11964, 11963, 11930, 11931, 11932, 11366, 11367), 30, 50, 453,15, 20, ObjectID.ROCKS_5),
    SANDSTONE(intArrayOf(11386), 35, 45, ItemID.SANDSTONE_1KG_,15, 15, ObjectID.ROCKS_5),
    GRANITE(intArrayOf(11387), 45, 65, ItemID.GRANITE_500G_,20, 15, ObjectID.ROCKS_5),
    GEM(intArrayOf(11381, 11380), 40, 65, 1629, 20, 18, ObjectID.ROCKS_5),
    GOLD(intArrayOf(7458, 7491, 9720, 9721, 9722, 11951, 11183, 11184, 11185, 11370, 11371, 2099), 40, 65, 444, 15, 25, ObjectID.ROCKS_5),
    LOVAKITE(intArrayOf(9999), 65, 10, ItemID.LOVAKITE_ORE, 19, 35, ObjectID.ROCKS_5),
    MITHRIL(intArrayOf(7459, 7492, 25370, 25368, 5786, 5784, 11942, 11943, 11944, 11945, 11946, 29236, 11947, 11372, 11373), 55, 80, 447, 17, 30, ObjectID.ROCKS_5),
    ADAMANTITE(intArrayOf(7493, 7460, 7060, 11374, 11375, 11941, 11939, 29233, 29235), 70, 95, 449, 18, 35, ObjectID.ROCKS_5),
    EFH_SALT(intArrayOf(33255), 72, 5, 22595,15, 15, 33253),
    URT_SALT(intArrayOf(33254), 72, 5, 22597,15, 15, 33253),
    TE_SALT(intArrayOf(33256), 72, 5, 22593,15, 15, 33253),
    BASALT(intArrayOf(33257), 72, 5, 22603,15, 15, 33253),
    RUNITE(intArrayOf(7494, 7461, 14859, 4860, 2106, 2107, 11376, 11377), 85, 125, 451, 23, 45, ObjectID.ROCKS_5),

    /*
    * Traehaern mine
     */
    IRON_2(intArrayOf(36203), 15, 35,440, 13, 15, ObjectID.ROCKS_36202),
    COAL_2(intArrayOf(36204), 30, 50, 453,15, 20, ObjectID.ROCKS_36202),
    SILVER_2(intArrayOf(36205), 20, 40, 442, 14, 11, ObjectID.ROCKS_36202),
    GOLD_2(intArrayOf(36206), 40, 65, 444, 15, 25, ObjectID.ROCKS_36202),
    MITHRIL_2(intArrayOf(36207), 55, 80, 447, 17, 30, ObjectID.ROCKS_36202),
    ADAMANTITE_2(intArrayOf(36208), 70, 95, 449, 18, 35, ObjectID.ROCKS_36202),
    RUNITE_2(intArrayOf(36209), 85, 125, 451, 23, 45, ObjectID.ROCKS_36202),




    AMETHYST(intArrayOf(11388, 11389), 92, 240, 21347, 23, 75, 11393); //30368

    companion object {
        private val rocks: MutableMap<Int, RockType> = HashMap()

        @JvmStatic
        fun forObjectId(objectId: Int): Optional<RockType> {
            val rock = rocks[objectId]
            return if (rock != null) {
                Optional.of(rock)
            } else Optional.empty()
        }

        init {
            for (t in RockType.values()) {
                for (obj in t.objects) {
                    rocks[obj] = t
                }
                //rocks[t.oreId] = t
            }
        }
    }

}