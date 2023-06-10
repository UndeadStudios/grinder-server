package com.grinder.game.content.skill.skillable.impl.woodcutting

import com.grinder.util.ItemID
import java.util.*

/**
 * Holds data related to the trees which can be used to train this skill.
 */
enum class TreeType(val requiredLevel: Int, val xpReward: Int, val logId: Int, val objects: IntArray, val stumpId: Int, val cycles: Int, val respawnTimer: Int, val burnXpReward: Int, val isMulti: Boolean) {

    NORMAL(1, 25, ItemID.LOGS, intArrayOf(1276, 1277, 1278, 3037, 1279, 1280, 1282, 1283, 1284, 1285, 1286, 1289, 1290, 1291, 1315, 1316, 1318, 1319, 1330, 1331, 1332, 1365, 1383, 1384, 2091, 2092, 3033, 3034, 3035, 3036, 3881, 3882, 3883, 5902, 5903, 5904), 1343,10, 8, 20, false),
    PEST_CONTROL_TREE(1, 25, ItemID.LOGS, intArrayOf(14308, 14309), 9661,10, 15, 20, false),
    ACHEY(1, 25, ItemID.ACHEY_TREE_LOGS, intArrayOf(2023), 3371,13, 9, 20, false),

    OAK(15, 38, ItemID.OAK_LOGS, intArrayOf(1281, 1751, 10820),1356, 12, 11, 30, true),
    WILLOW(30, 68, ItemID.WILLOW_LOGS, intArrayOf(1308, 1750, 1760, 1756, 1758, 5551, 5552, 10819, 10829, 10831, 10833),9471, 15, 14, 45, true),
    TEAK(35, 85, ItemID.TEAK_LOGS, intArrayOf(9036), 9037, 16, 16, 52, true),
    DRAMEN(36, 88, ItemID.DRAMEN_BRANCH, intArrayOf(1292), 1343, 16, 17, -1, true),
    VINES(1, 32, ItemID.DRAMEN_BRANCH, intArrayOf(21731, 21732, 21733, 21734, 21735, 9237, 9238, 9239, 9240), 1343, 6, 2, -1, false),
    // Mature juniper tree (sand crabs area)
    MAPLE(45, 100, ItemID.MAPLE_LOGS, intArrayOf(1307, 4677, 4674, 10832, 1759), 9712, 15, 18, 67, true),
    // Hollow tree
    MAHOGANY(50, 125, ItemID.MAHOGANY_LOGS, intArrayOf(9034), 9035,17, 20, 78, true),
    // Artic pine tree
    YEW(60, 175, ItemID.YEW_LOGS, intArrayOf(1309, 1753, 10822, 10823), 9714, 18, 28, 115, true),
    // Blisterwood
    MAGIC(75, 250, ItemID.MAGIC_LOGS, intArrayOf(1306, 1761, 10834), 9713,25, 40, 160, true),
    REDWOOD(90, 380, ItemID.REDWOOD_LOGS, intArrayOf(29670, 29668), 29669, 22, 43, 195, true);

    companion object {
        private val trees: MutableMap<Int, TreeType> = HashMap()

        @JvmStatic
        fun forObjectId(objectId: Int): Optional<TreeType> {
            val tree = trees[objectId]
            return if (tree != null) {
                Optional.of(tree)
            } else Optional.empty()
        }

        init {
            for (t in values()) {
                for (obj in t.objects) {
                    trees[obj] = t
                }
                //trees[t.logId] = t
            }
        }
    }

}