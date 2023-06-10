package com.grinder.game.content.skill.skillable.impl.fishing

import com.grinder.game.content.skill.ResourceSkillReq
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.getLevel
import com.grinder.game.model.Skill
import kotlin.math.floor
import kotlin.random.Random

/**
 * Fish is data associated with the Fishing skill. [These could also be setup as a SEALED class in kotlin]
 * @param itemID The item identifier associated to the fish.
 * @param fishLevel The level required to receive.
 * @param fishExp The experience given upon receiving.
 */
enum class Fish(val itemID: Int, val fishReq: ResourceSkillReq, private val catchChance: Pair<Int, Int>,
                val baits: IntArray? = null, val addReq: Array<ResourceSkillReq>? = null) {
    SHRIMP(317, ResourceSkillReq(Skill.FISHING, 1, 10), Pair(48, 256)),
    SARDINE(327, ResourceSkillReq(Skill.FISHING, 5, 20), Pair(24, 126), intArrayOf(313)),
    KARAMBWANJI(3150, ResourceSkillReq(Skill.FISHING, 5, 5), Pair(100, 250)),
    HERRING(345, ResourceSkillReq(Skill.FISHING, 10, 30), Pair(24, 128), intArrayOf(313)),
    ANCHOVIES(321, ResourceSkillReq(Skill.FISHING, 15, 40), Pair(24, 128)),
    OYSTER(407, ResourceSkillReq(Skill.FISHING, 16, 10), Pair(3, 7)),
    CASKET(403, ResourceSkillReq(Skill.FISHING, 16, 100), Pair(1, 2)),
    MACKEREL(353, ResourceSkillReq(Skill.FISHING, 16, 20), Pair(5, 65)),
    TROUT(335, ResourceSkillReq(Skill.FISHING, 20, 50), Pair(32, 192), intArrayOf(314)),
    COD(341, ResourceSkillReq(Skill.FISHING, 23, 45), Pair(4, 55), intArrayOf(314)),
    PIKE(349, ResourceSkillReq(Skill.FISHING, 25, 60), Pair(16, 96), intArrayOf(313)),
    SLIMY_EEL(3379, ResourceSkillReq(Skill.FISHING, 28, 65), Pair(10, 80), intArrayOf(313)),
    SALMON(331, ResourceSkillReq(Skill.FISHING, 30, 70), Pair(16, 96), intArrayOf(314)),
    FROG_SPAWN(5004, ResourceSkillReq(Skill.FISHING, 33, 75), Pair(16, 96)),
    TUNA(359, ResourceSkillReq(Skill.FISHING, 35, 80), Pair(8, 64),
            addReq = arrayOf(
                    ResourceSkillReq(Skill.FISHING, 55, 80),
                    ResourceSkillReq(Skill.STRENGTH, 35, 8)
            )),
    RAINBOW_FISH(10138, ResourceSkillReq(Skill.FISHING, 38, 80), Pair(8, 64), intArrayOf(10087)),
    CAVE_EEL(5001, ResourceSkillReq(Skill.FISHING, 38, 80), Pair(10, 80), intArrayOf(313)),
    LOBSTER(377, ResourceSkillReq(Skill.FISHING, 40, 90), Pair(6, 95)),
    BASS(363, ResourceSkillReq(Skill.FISHING, 46, 100), Pair(3, 40)),
    LEAPING_TROUT(11328, ResourceSkillReq(Skill.FISHING, 48, 50), Pair(32, 192), intArrayOf(313, 314, 11334, 11324, 11326),
            arrayOf(ResourceSkillReq(Skill.STRENGTH, 15, 5), ResourceSkillReq(Skill.AGILITY, 15, 5))),
    SWORDFISH(371, ResourceSkillReq(Skill.FISHING, 50, 100), Pair(4, 48),
            addReq = arrayOf(
                    ResourceSkillReq(Skill.FISHING, 70, 100),
                    ResourceSkillReq(Skill.STRENGTH, 50, 10)
            )),
    LEAPING_SALMON(11330, ResourceSkillReq(Skill.FISHING, 58, 70), Pair(16, 96), intArrayOf(313, 314, 11334, 11324, 11326),
            arrayOf(ResourceSkillReq(Skill.STRENGTH, 30, 6), ResourceSkillReq(Skill.AGILITY, 30, 6))),
    MONKFISH(7944, ResourceSkillReq(Skill.FISHING, 62, 120), Pair(48, 90)),
    KARAMBWAN(3142, ResourceSkillReq(Skill.FISHING, 65, 50), Pair(48, 90), intArrayOf(3150)) {
        override fun catch(player: Player, boost: Double): Boolean {
            val success = super.catch(player, boost)
            if (!success) {
                //player.inventory.replaceFirst(3159, 3157)
                player.inventory.delete(3150, 1)
                player.sendMessage("A Karambwan deftly snatches the Karambwanji from your vessel!")
            }
            return success
        }
    },
    LEAPING_STURGEON(11332, ResourceSkillReq(Skill.FISHING, 70, 80), Pair(8, 64), intArrayOf(313, 314, 11334, 11324, 11326),
            arrayOf(ResourceSkillReq(Skill.STRENGTH, 45, 7), ResourceSkillReq(Skill.AGILITY, 45, 7))),
    SHARK(383, ResourceSkillReq(Skill.FISHING, 76, 110), Pair(3, 40),
            addReq = arrayOf(
                    ResourceSkillReq(Skill.FISHING, 96, 110),
                    ResourceSkillReq(Skill.STRENGTH, 76, 11)
            )),
    BIG_SHARK(15701, ResourceSkillReq(Skill.FISHING, 76, 165), Pair(1, 8),
            addReq = arrayOf(
                    ResourceSkillReq(Skill.FISHING, 92, 165),
                    ResourceSkillReq(Skill.STRENGTH, 76, 11)
            )),
    SEA_TURTLE(395, ResourceSkillReq(Skill.FISHING, 79, 38), Pair(-11, 59)),
    INFERNAL_EEL(21293, ResourceSkillReq(Skill.FISHING, 80, 95), Pair(4, 48), intArrayOf(313)),
    PADDLEFISH(15708, ResourceSkillReq(Skill.FISHING, 96, 196), Pair(1, 48), intArrayOf(313)),
    LAVA_EEL(2148, ResourceSkillReq(Skill.FISHING, 53, 30), Pair(16, 96), intArrayOf(313)),
    SACRED_EEL(13339, ResourceSkillReq(Skill.FISHING, 87, 105), Pair(16, 96), intArrayOf(313)),
    MANTA_RAY(389, ResourceSkillReq(Skill.FISHING, 81, 46), Pair(-11, 59)),
    ANGLER_FISH(13439, ResourceSkillReq(Skill.FISHING, 82, 120), Pair(1, 32), intArrayOf(13431)),
    DARK_CRAB(11934, ResourceSkillReq(Skill.FISHING, 85, 130), Pair(1, 32), intArrayOf(11940)),

    // chambers of XERIC
    PYSK0(20855, ResourceSkillReq(Skill.FISHING, 1, 20), Pair(48, 256), intArrayOf(20853)),
    SUPHI1(20857, ResourceSkillReq(Skill.FISHING, 15, 23), Pair(5, 65), intArrayOf(20853)),
    LECKISH2(20859, ResourceSkillReq(Skill.FISHING, 30, 26), Pair(16, 96), intArrayOf(20853)),
    BRAWK3(20861, ResourceSkillReq(Skill.FISHING, 45, 29), Pair(16, 95), intArrayOf(20853)),
    MYCIL4(20863, ResourceSkillReq(Skill.FISHING, 60, 32), Pair(16, 96), intArrayOf(20853)),
    ROQED5(20865, ResourceSkillReq(Skill.FISHING, 75, 35), Pair(8, 64), intArrayOf(20853)),
    KYREN6(20867, ResourceSkillReq(Skill.FISHING, 90, 38), Pair(8, 64), intArrayOf(20853));

    /**
     * catch determines if the player was sucessful in catching the fish.
     *
     * @param player The player fishing.
     * @param boost Any applicable boosts affecting catch rate.
     * @return Success/Failure
     */
    open fun catch(player:Player, boost: Double=0.0): Boolean {
        val fishLv = player.getLevel(Skill.FISHING)
        val modLow = floor(this.catchChance.first * (boost +1))
        val modHigh = floor(this.catchChance.second * (boost +1))
        val rawRoll = floor(((99 - fishLv) * modLow + (fishLv - 1) * modHigh) / 98).toInt()
        return Random.nextInt(256) + 1 <= rawRoll
    }

    /**
     * Gets the first bait available bait in the players inventory.
     * @param player Player being checked
     * @return Bait ID associated
     */
    fun firstBait(player: Player): Int {
        val bait = this.baits?.filter { player.inventory.contains(it) }
        return if (bait.isNullOrEmpty()) 0 else bait.first()
    }
}

/**
 * FishReward is a possible reward given when fishing.
 * @param fish The type of fish to be rewarded.
 * @param amt The number of fish to give.
 * @param chance The 'roll' chance of successfully fishing this reward.
 */
data class FishReward(val fish: Fish, val amt: (Player) -> Int = fun(p: Player): Int {
    if (p.equipment.contains(22941) && Random.nextInt(50) == 0
            || p.equipment.contains(22943) && Random.nextInt(25) == 0
            || p.equipment.contains(22945) && Random.nextInt(50) < 3
            || p.equipment.contains(22947) && Random.nextInt(25) < 2)
        return 2
    return 1
})