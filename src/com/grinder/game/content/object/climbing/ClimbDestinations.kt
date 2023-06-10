package com.grinder.game.content.`object`.climbing

import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Animation
import com.grinder.game.model.Position
import com.grinder.game.task.Task
import com.grinder.game.task.TaskManager
import com.grinder.util.ObjectID
import java.util.*
import java.util.function.BiFunction

/**
 * Can be used to configure custom position transforms upon climbing an object.
 *
 * @author Lou Grinder
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since 19/01/2021
 */
internal object ClimbDestinations {

    @JvmField
    val DESTINATIONS: MutableMap<Int, BiFunction<Int, Position, Position?>> = HashMap()

    init {
//        DESTINATIONS[ObjectID.LADDER_192] = BiFunction { _, playerPosition: Position ->
//            when {
//                playerPosition.x in 2858..2901 && playerPosition.y in 3518..3521 -> Position(2860, 9919, 0)
//                playerPosition.x in 2855..2858 && playerPosition.y in 3515..3518 -> Position(2858, 9917, 0)
//                playerPosition.x in 2847..2850 && playerPosition.y in 3512..3515 -> Position(2847, 9913, 0)
//                playerPosition.x in 2844..2847 && playerPosition.y in 3514..3517 -> Position(2844, 9916, 0)
//                playerPosition.x in 2847..2850 && playerPosition.y in 3518..3521 -> Position(2847, 9919, 0)
//                else -> null
//            }
//        }
//        DESTINATIONS[ObjectID.LADDER_218] = staticDestination(3578, 3526, 0)
        DESTINATIONS[ObjectID.MEMORIAL] = staticDestination(3577, 9927, 0)
        DESTINATIONS[ObjectID.OBSTACLE_PIPE_5] = BiFunction { _, playerPosition: Position ->
            when (playerPosition.x) {
                2892 -> Position(2886, 9799, 0)
                2886 -> Position(2892, 9799, 0)
                else -> null
            }
        }
        DESTINATIONS[31558] = staticDestination(3126, 3832, 0)
        DESTINATIONS[132] = staticDestination(3092, 3361, 0)
        DESTINATIONS[133] = staticDestination(3117, 9753, 0)
        DESTINATIONS[ObjectID.CLIMBING_ROPE_13] = staticDestination(2834, 3254, 0)
        DESTINATIONS[34477] = BiFunction { _, playerPosition: Position ->  // farming guild
            when (playerPosition.x) {
                in 1222..1224 -> Position(1225, 3755, 1)
                else -> Position(1232, 3755, 1)
            }
        }
        DESTINATIONS[34478] = BiFunction { _, playerPosition: Position ->  // farming guild
            when (playerPosition.x) {
                in 1222..1225 -> Position(1224, 3755, 0)
                else -> Position(1233, 3755, 0)
            }
        }
        DESTINATIONS[ObjectID.ROPE_31] = staticDestination(1563, 3792, 0) // brutal black dragon
        DESTINATIONS[ObjectID.ROPE_32] = staticDestination(1696, 3866, 0) // deviant sceptres
        DESTINATIONS[ObjectID.ROPE_33] = staticDestination(1670, 3569, 0) // king sand crabs
        DESTINATIONS[ObjectID.ROPE_34] = staticDestination(1696, 3866, 0) // greater demons
        DESTINATIONS[ObjectID.STAIRS_43868] = staticDestination(3124, 3805, 0)
        DESTINATIONS[ObjectID.HOLE_11] = staticDestination(3229, 9904, 0)
        DESTINATIONS[ObjectID.STAIRCASE_100] = staticDestination(2516, 3413, 0)
        DESTINATIONS[ObjectID.STAIRCASE_101] = staticDestination(2517, 3426, 1)
        DESTINATIONS[ObjectID.STAIRS_88] = staticDestination(2636, 9510, 2)
        DESTINATIONS[ObjectID.LADDER_30] = staticDestination(2805, 9589, 3)
        DESTINATIONS[ObjectID.CLIMBING_ROPE_8] = staticDestination(2762, 2768, 0)
        DESTINATIONS[ObjectID.LADDER_31] = staticDestination(2809, 3193, 0)
        DESTINATIONS[ObjectID.STAIRS_89] = staticDestination(2636, 9517, 0)
        DESTINATIONS[ObjectID.LADDER_68] = staticDestination(2547, 3420, 0)
        DESTINATIONS[ObjectID.STAIRS_85] = staticDestination(2643, 9594, 2)
        DESTINATIONS[ObjectID.STAIRS_87] = staticDestination(2649, 9591, 0)
        DESTINATIONS[ObjectID.STAIRCASE_135] = BiFunction { _, playerPosition: Position ->
            when (playerPosition.x) {
                in 3035..3038 -> Position(3036, 3363, 1)
                3050 -> Position(3049, 3354, 1)
                else -> Position(2972, 3385, 1)
            }
        }
        DESTINATIONS[ObjectID.STAIRCASE_26] = BiFunction { _, playerPosition: Position ->
            when (playerPosition.x) {
                in 3827..3832 -> Position(3829, 3064, 0)
                else -> Position(3814, 3061, 0)
            }
        }
        DESTINATIONS[ObjectID.STAIRCASE_50] = BiFunction { _, playerPosition: Position ->
            when (playerPosition.x) {
                3219 -> Position(3220, 3496, 1)
                else -> null
            }
        }
        DESTINATIONS[ObjectID.LADDER_154] = BiFunction { _, playerPosition: Position ->
            when (playerPosition.x) {
                3234 -> Position(3232, 3401, 0)
                else -> null
            }
        }
        DESTINATIONS[ObjectID.LADDER_15] = BiFunction { _, playerPosition: Position ->
            when (playerPosition.x) {
                2819 -> Position(2821, 9772, 0)
                2820 -> Position(2821, 9772, 0)
                2821 -> Position(2821, 9772, 0)
                else -> null
            }
        }
        DESTINATIONS[ObjectID.STAIRCASE_51] = BiFunction { _, playerPosition: Position ->
            when (playerPosition.x) {
                3203 -> Position(3204, 3497, 1)
                3222 -> Position(3223, 3472, 3)
                3257, 3258 -> Position(3258, 3486, 1)
                else -> null
            }
        }
        DESTINATIONS[ObjectID.LADDER_216] = BiFunction { _, playerPosition: Position ->
                Position(playerPosition.x, playerPosition.y - 6400, 0) // Falador Mining Guild Bottom
        }
        DESTINATIONS[ObjectID.LADDER_354] = BiFunction { _, playerPosition: Position ->
            Position(playerPosition.x, playerPosition.y + 6400, 0) // Falador Mining Guild Top
        }
        DESTINATIONS[ObjectID.STAIRCASE_53] = BiFunction { height, playerPosition: Position ->
            when (playerPosition.x) {
                3204 -> Position(3204, 3497, height)
                3257, 3258 -> Position(3258, 3486, 0)
                else -> null
            }
        }
        DESTINATIONS[ObjectID.STAIRCASE_54] = BiFunction { _, playerPosition: Position ->
            when (playerPosition.x) {
                3223 -> Position(3222, 3471, 2)
                3257 -> Position(3256, 3420, 0)
                3220 -> Position(3219, 3496, 0)
                3204 -> Position(3204, 3497, 1)
                else -> null
            }
        }
        DESTINATIONS[ObjectID.LADDER_315] = BiFunction { _, playerPosition: Position ->
            when (playerPosition.x) {
                3050 -> Position(3050, 3354, 2)
                else -> null
            }
        }
        DESTINATIONS[ObjectID.STAIRCASE_104] = BiFunction { _, playerPosition: Position ->
            when (playerPosition.x) {
                2574 -> Position(2574, 3325, 1)
                2597 -> Position(2597, 3208, 1)
                in 3205..3206 -> Position(playerPosition.x, playerPosition.y, 1) // Lumbridge Right Staircase
                else -> null
            }
        }
        DESTINATIONS[ObjectID.LADDER_10] = staticDestination(3104, 9576, 0) //mage tower ladder down
        DESTINATIONS[ObjectID.LADDER_11] = staticDestination(3105, 3162, 0) //mage tower ladder up
        DESTINATIONS[ObjectID.LADDER_158] = staticDestination(2981, 9915, 0)
        DESTINATIONS[ObjectID.LADDER_159] = staticDestination(2960, 3506, 0)
        DESTINATIONS[ObjectID.STAIRCASE_7] = staticDestination(3144, 3449, 1) // Cooking Guild Staircase Up
        DESTINATIONS[ObjectID.STAIRCASE_9] = staticDestination(3144, 3449, 1) // Cooking Guild Staircase Down
        DESTINATIONS[ObjectID.STAIRCASE_143] = staticDestination(3038, 3374, 2)
        DESTINATIONS[ObjectID.STAIRCASE_144] = staticDestination(3053, 3374, 2)
        DESTINATIONS[ObjectID.STAIRCASE_145] = staticDestination(3038, 3382, 0)
        DESTINATIONS[ObjectID.STAIRCASE_146] = staticDestination(3039, 3373, 1)
        DESTINATIONS[ObjectID.STAIRCASE_147] = staticDestination(3053, 3382, 0)
        DESTINATIONS[ObjectID.STAIRCASE_148] = staticDestination(3052, 3373, 1)
        DESTINATIONS[ObjectID.STAIRCASE_131] = staticDestination(2968, 3348, 1)
        DESTINATIONS[ObjectID.STAIRCASE_132] = staticDestination(2971, 3347, 0)
        DESTINATIONS[ObjectID.STAIRCASE_130] = offsetDestination(3, -6400, 0)
        DESTINATIONS[ObjectID.STAIRCASE_59] = staticDestination(3190, 9833, 0)
        DESTINATIONS[ObjectID.STAIRCASE_60] = staticDestination(3186, 3434, 0)
        DESTINATIONS[ObjectID.STAIRCASE_38] = BiFunction { _, playerPosition: Position ->
            when (playerPosition.y) {
                3248 -> Position(3026, 3247, 1)
                3260 -> Position(3025, 3261, 1)
                else -> null
            }
        }
        DESTINATIONS[ObjectID.STAIRCASE_138] = BiFunction { _, playerPosition: Position ->
            when (playerPosition.y) {
                3340 -> Position(2984, 3336, 0)
                else -> null
            }
        }
        DESTINATIONS[ObjectID.STAIRCASE_133] = BiFunction { _, playerPosition: Position ->
            when (playerPosition.x) {
                2960 -> Position(2959, 3339, 2)
                2958 -> Position(2959, 3338, 3)
                3010 -> Position(3011, 3337, 1)
                else -> null
            }
        }
        DESTINATIONS[ObjectID.STAIRCASE_50] = BiFunction { _, playerPosition: Position ->
            when (playerPosition.x) {
                in 3254..3257 -> Position(3257, 3421, 1)
                else -> null
            }
        }
        DESTINATIONS[ObjectID.STAIRCASE_108] = BiFunction { _, playerPosition: Position ->
            Position(playerPosition.x, playerPosition.y, if (playerPosition.z == 0) 1 else 0)
        }
        DESTINATIONS[ObjectID.STAIRCASE_109] = BiFunction { _, playerPosition: Position ->
            Position(playerPosition.x, playerPosition.y, if (playerPosition.z == 0) 1 else 0)
        }
        DESTINATIONS[31627] = BiFunction { _, playerPosition: Position ->
            Position(playerPosition.x, playerPosition.y, if (playerPosition.z == 0) 1 else 0)
        }
        DESTINATIONS[ObjectID.STAIRCASE_98] = staticDestination(2606, 3079, 0)

        /*
        Slayer tower staircases
         */
        DESTINATIONS[ObjectID.STAIRCASE] = BiFunction { _, playerPosition: Position ->
            Position(3433, playerPosition.y, 1)
        }
        DESTINATIONS[ObjectID.STAIRCASE_2] = BiFunction { _, playerPosition: Position ->
            Position(3438, playerPosition.y, 0)
        }
        DESTINATIONS[ObjectID.STAIRCASE_3] = BiFunction { _, playerPosition: Position ->
            Position(3417, playerPosition.y, 2)
        }
        DESTINATIONS[ObjectID.STAIRCASE_4] = BiFunction { _, playerPosition: Position ->
            Position(3412, playerPosition.y, 1)
        }
        DESTINATIONS[ObjectID.VINE_24] = staticDestination(2670, 9583, 2)
        DESTINATIONS[ObjectID.LADDER_275] = offsetDestination(2, 0, 2)
        DESTINATIONS[ObjectID.LADDER_276] = offsetDestination(-2, 0, -2)
        DESTINATIONS[ObjectID.LADDER_195] = conditionalYOffsetDestination(0, 0, 1, 3513, 3512)
        DESTINATIONS[ObjectID.LADDER_273] = offsetDestination(0, 0, 2)
        DESTINATIONS[ObjectID.LADDER_91] = BiFunction { _, playerPosition: Position ->
            when (playerPosition.x) {
                in 2901..2907 -> Position(2834, 3542, 0)
                else -> null
            }
        }
        DESTINATIONS[ObjectID.LADDER_274] = offsetDestination(0, 0, -2)
        DESTINATIONS[ObjectID.LADDER_245] = BiFunction<Int, Position, Position?> { up: Int?, playerPosition: Position  ->
            when (playerPosition.x) {
                3069 -> Position(3017, 10250, 0)
                3017 -> Position(3069, 10255, 0)
                else -> null
            }
        }
        DESTINATIONS[ObjectID.LADDER_246] = BiFunction { _, playerPosition: Position ->
            when (playerPosition.x) {
                in 3016..3018 -> Position(3069, 3857, 0)
                else -> null
            }
        }
        DESTINATIONS[ObjectID.LADDER_94] = BiFunction<Int, Position, Position?> { up: Int?, playerPosition: Position  ->
            when {
                playerPosition.x in 2831..2835 -> Position(2907, 9968, 0)
                playerPosition.y in 3541..3543 -> Position(3081, 3421, 0)
                else -> null
            }
        }
        DESTINATIONS[ObjectID.ROPE_LADDER_2] = BiFunction<Int, Position, Position?> { up: Int?, playerPosition: Position  ->
            when {
                playerPosition.y in 3699..3701 -> Position(2003, 3700, 1)
                playerPosition.y in 3709..3711 -> Position(2003, 3710, 1)
                else -> null
            }
        }
        DESTINATIONS[ObjectID.ROPE_LADDER_3] = BiFunction<Int, Position, Position?> { down: Int?, playerPosition: Position  ->
            when {
                playerPosition.y in 3699..3701 -> Position(2004, 3700, 0)
                playerPosition.y in 3709..3711 -> Position(2004, 3710, 0)
                playerPosition.z == 2 -> Position(2004, playerPosition.y, 0)
                else -> null
            }
        }
        DESTINATIONS[ObjectID.VINE_25] = changePlaneDestination(0)
        DESTINATIONS[ObjectID.LADDER_277] = BiFunction { _, playerPosition: Position ->
            Position(2329, playerPosition.y, 1)
        }
        DESTINATIONS[ObjectID.ROPE_5] = staticDestination(3509, 9499, 2)
        DESTINATIONS[ObjectID.IRON_LADDER_7] = staticDestination(1798, 4407, 3)
        DESTINATIONS[ObjectID.LADDER_271] = staticDestination(3081, 3421, 0)
        DESTINATIONS[ObjectID.CLIMBING_ROPE_9] = staticDestination(3170, 3172, 0)
        DESTINATIONS[ObjectID.LADDER_32] = staticDestination(2665, 10038, 0)
        DESTINATIONS[ObjectID.LADDER_34] = staticDestination(2644, 3658, 0)
        DESTINATIONS[ObjectID.BONEY_LADDER] = staticDestination(2148, 5283, 0)
        DESTINATIONS[ObjectID.LADDER_69] = staticDestination(3233, 9293, 0)
        DESTINATIONS[ObjectID.SPIKEY_CHAIN_3] = staticDestination(1861, 5242, 0)
        DESTINATIONS[ObjectID.LADDER_272] = staticDestination(2042, 5245, 0)
        DESTINATIONS[ObjectID.LADDER_247] = staticDestination(1902, 5221, 0)
        DESTINATIONS[ObjectID.ROPE_22] = staticDestination(2042, 5243, 0)
        DESTINATIONS[ObjectID.LADDER_248] = staticDestination(2122, 5251, 0)
        DESTINATIONS[ObjectID.DRIPPING_VINE] = staticDestination(2026, 5219, 0)
        DESTINATIONS[ObjectID.GOO_COVERED_VINE] = staticDestination(2122, 5254, 0)
        DESTINATIONS[ObjectID.BONE_CHAIN] = staticDestination(3081, 3421, 0)
        DESTINATIONS[ObjectID.ROPE_25] = staticDestination(3094, 3469, 0)
        DESTINATIONS[ObjectID.ROPE_7] = staticDestination(3372, 2904, 0)
        DESTINATIONS[ObjectID.CLIMBING_ROPE_12] = staticDestination(2856, 3167, 0)
        DESTINATIONS[32205] = staticDestination(2457, 2849, 0)
        DESTINATIONS[ObjectID.ROPE_4] = staticDestination(3227, 3111, 0)
        DESTINATIONS[ObjectID.DRIPPING_VINE_2] = staticDestination(2358, 5215, 0)
        DESTINATIONS[ObjectID.LADDER_118] = BiFunction { _, playerPosition: Position ->
            Position(playerPosition.x, playerPosition.y - 3, 2)
        }
        DESTINATIONS[ObjectID.LADDER_117] = BiFunction { _, playerPosition: Position ->
            Position(playerPosition.x, playerPosition.y + 3, 1)
        }
        DESTINATIONS[ObjectID.SHIPS_LADDER] = offsetDestination(0, +2)
        DESTINATIONS[ObjectID.SHIPS_LADDER_2] = offsetDestination(0, -2)
        DESTINATIONS[ObjectID.LADDER_116] = offsetDestination(0, -3)
        DESTINATIONS[ObjectID.LADDER_115] = offsetDestination(0, +3)
        DESTINATIONS[ObjectID.LADDER_114] = offsetDestination(-2, 0)
        DESTINATIONS[ObjectID.LADDER_113] = offsetDestination(+2, 0)
        DESTINATIONS[ObjectID.LADDER_108] = offsetDestination(0, +3)
        DESTINATIONS[ObjectID.LADDER_107] = offsetDestination(0, -3)
        DESTINATIONS[ObjectID.LADDER_103] = offsetDestination(0, -3)
        DESTINATIONS[ObjectID.LADDER_123] = offsetDestination(0, -3)
        DESTINATIONS[ObjectID.LADDER_124] = offsetDestination(0, +3)
        DESTINATIONS[ObjectID.LADDER_125] = offsetDestination(0, -3)
        DESTINATIONS[ObjectID.LADDER_126] = offsetDestination(0, +3)
        DESTINATIONS[ObjectID.LADDER_101] = offsetDestination(-3, 0)
        DESTINATIONS[ObjectID.LADDER_102] = offsetDestination(+3, 0)
        DESTINATIONS[ObjectID.LADDER_100] = offsetDestination(-3, 0)
        DESTINATIONS[ObjectID.CLIMBING_ROPE_3] = offsetDestination(0, 0, 3)
        DESTINATIONS[ObjectID.LADDER_99] = offsetDestination(+3, 0)
        DESTINATIONS[ObjectID.LADDER_97] = staticDestination(2545, 10143, 0)
        DESTINATIONS[ObjectID.KINGS_LADDER] = staticDestination(1912, 4367, 0)
        DESTINATIONS[ObjectID.KINGS_LADDER_2] = staticDestination(2900, 4449, 0)
        DESTINATIONS[ObjectID.LADDER_98] = BiFunction { _, playerPosition: Position ->
            if (playerPosition.x in 1975..1977) 
                Position(2510, 3644, 0) 
            else 
                Position(1890, 4409, 0) 
        }
        DESTINATIONS[ObjectID.LADDER_119] = staticDestination(1890, 4407, 0)

        // Slayer tower basement ladder (DOWN)
        DESTINATIONS[ObjectID.LADDER_352] = staticDestination(3412, 9932, 3)
        // Slayer tower basement ladder (UP)
        DESTINATIONS[ObjectID.LADDER_353] = staticDestination(3417, 3536, 0)
        DESTINATIONS[ObjectID.LADDER_278] = BiFunction { _, playerPosition: Position ->
            Position(2331, playerPosition.y, 0)
        }
        DESTINATIONS[ObjectID.LADDER_121] = offsetDestination(0, +3)
        DESTINATIONS[ObjectID.LADDER_122] = offsetDestination(0, -3)
        DESTINATIONS[ObjectID.IRON_LADDER_2] = staticDestination(1975, 4408, 3)
        DESTINATIONS[ObjectID.LADDER_255] = staticDestination(2593, 5260, 0)
        DESTINATIONS[ObjectID.ROPE_38] = staticDestination(1435, 3671, 0)
        DESTINATIONS[ObjectID.ROPE_29] = staticDestination(2026, 5611, 0)
        DESTINATIONS[ObjectID.LADDER_256] = staticDestination(2535, 3572, 0)
    }

    private fun conditionalYOffsetDestination(dx: Int, dy: Int, dz: Int, vararg yValues: Int): BiFunction<Int, Position, Position?> {
        return BiFunction { _, playerPosition: Position ->
            when {
                !anyMatch(playerPosition.y, yValues) -> null
                else -> Position(playerPosition.x + dx, playerPosition.y + dy, playerPosition.z + dz)
            }
        }
    }

    private fun anyMatch(coord: Int, coordValues: IntArray): Boolean {
        var found = false
        for (xOrY in coordValues) {
            if (coord == xOrY) {
                found = true
                break
            }
        }
        return found
    }

    private fun offsetDestination(dx: Int, dy: Int): BiFunction<Int, Position, Position?> {
        return BiFunction { newHeight: Int?, playerPosition: Position -> Position(playerPosition.x + dx, playerPosition.y + dy, newHeight!!) }
    }

    private fun offsetDestination(dx: Int, dy: Int, dz: Int): BiFunction<Int, Position, Position?> {
        return BiFunction { up: Int?, playerPosition: Position -> Position(playerPosition.x + dx, playerPosition.y + dy, playerPosition.z + dz) }
    }

    private fun staticDestination(x: Int, y: Int, z: Int): BiFunction<Int, Position, Position?> {
        return BiFunction { up: Int?, playerPosition: Position? -> Position(x, y, z) }
    }

    private fun changePlaneDestination(z: Int): BiFunction<Int, Position, Position?> {
        return BiFunction { up: Int?, playerPosition: Position -> Position(playerPosition.x, playerPosition.y, z) }
    }

}