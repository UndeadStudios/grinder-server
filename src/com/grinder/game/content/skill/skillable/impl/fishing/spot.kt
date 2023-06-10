package com.grinder.game.content.skill.skillable.impl.fishing

import com.grinder.game.content.skill.SkillUtil
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.getLevel
import com.grinder.game.entity.agent.player.hasItemInInventory
import com.grinder.game.model.NPCActions
import com.grinder.game.model.Skill
import com.grinder.game.model.item.Item

/**
 * SpotOption is a single option from a FishingSpot
 * @param tool FishingTool used to on this fishing spot.
 * @param rewards Rewards are a list of possible fish that can be caught in order by fishingLevel.
 */
data class FishOption(val tool: FishingTool, private val rewards: List<FishReward>) {
    /**
     * rewards filters the available rewards based on the players fishing level.
     * NOTE: This does not check for the additional requirements of the fish.
     *
     * @param player Player associated.
     * @return List of possible FishRewards.
     */
    fun rewards(player: Player): List<FishReward> {
        val lv = player.skills.getLevel(Skill.FISHING)
        return rewards.filter {
            lv >= it.fish.fishReq.lv
        }
    }

    /**
     * filters the rewards based on the additional requirements of the fishReward.
     *
     * @param player Player associated.
     * @return List of possible FishRewards
     */
    fun rewardsByReq(player: Player): List<FishReward> {
        return rewards(player).filter {
            if (it.fish.addReq != null)
                !it.fish.addReq.any { req -> player.getLevel(req.skill) < req.lv}
            else
                true
        }
    }

    /**
     * lowestLevel returns the lowest viable fishing level required for this FishOption.
     * @return Lowest fishing level required.
     */
    fun lowestLevel(): Int {
        return rewards.minByOrNull { it.fish.fishReq.lv }!!.fish.fishReq.lv
    }

    /**
     * Determines if the fishing spot requires bait.
     * @return Bait required.
     */
    fun requiresBait(): Boolean {
        return rewards.minByOrNull { it.fish.fishReq.lv }?.fish?.baits != null
    }
}

/**
 * FishingSpot is an 'npc' that is used to fish.
 * @param npcId NPCID associated with fishing spot.
 * @param options FishOption associate with the same indexed NPCOption.
 */
enum class FishingSpot(val npcId: Int, val options: Array<FishOption>) {
    RELLEKA_NET_BAIT(3913, arrayOf(
            FishOption(FishingTool.SMALL_FISHING_NET, listOf(
                    FishReward(Fish.SHRIMP),
                    FishReward(Fish.ANCHOVIES)
            )),
            FishOption(FishingTool.FISHING_ROD, listOf(
                    FishReward(Fish.SARDINE),
                    FishReward(Fish.HERRING)
            ))
    )),
    LUMBY_SHRIMP(1530, arrayOf(
            FishOption(FishingTool.SMALL_FISHING_NET, listOf(
                    FishReward(Fish.SHRIMP),
                    FishReward(Fish.ANCHOVIES)
            )),
            FishOption(FishingTool.FISHING_ROD, listOf(
                    FishReward(Fish.SARDINE),
                    FishReward(Fish.HERRING)
            ))
    )),
    LUMB_LURE_BAIT(1527, arrayOf(
            FishOption(FishingTool.FLY_FISHING_ROD, listOf(
                    FishReward(Fish.TROUT),
                    FishReward(Fish.SALMON),
                    FishReward(Fish.RAINBOW_FISH)
            )),
            FishOption(FishingTool.FISHING_ROD, listOf(
                    FishReward(Fish.SARDINE),
                    FishReward(Fish.HERRING)
            ))
    )),
    AL_KHARID_DRAYNOR_SPOT(1528, arrayOf(
            FishOption(FishingTool.SMALL_FISHING_NET, listOf(
                    FishReward(Fish.SHRIMP),
                    FishReward(Fish.ANCHOVIES)
            )),
            FishOption(FishingTool.FISHING_ROD, listOf(
                    FishReward(Fish.SARDINE),
                    FishReward(Fish.HERRING)
            ))
    )),
    PORT_SARIM_NET_BAIT(1523, arrayOf(
            FishOption(FishingTool.SMALL_FISHING_NET, listOf(
                    FishReward(Fish.SHRIMP),
                    FishReward(Fish.ANCHOVIES)
            )),
            FishOption(FishingTool.FISHING_ROD, listOf(
                    FishReward(Fish.SARDINE),
                    FishReward(Fish.HERRING)
            ))
    )),
    PORT_SARIM_NET_BAIT_2(1524, arrayOf(
            FishOption(FishingTool.SMALL_FISHING_NET, listOf(
                    FishReward(Fish.SHRIMP),
                    FishReward(Fish.ANCHOVIES)
            )),
            FishOption(FishingTool.FISHING_ROD, listOf(
                    FishReward(Fish.SARDINE),
                    FishReward(Fish.HERRING)
            ))
    )),
    CATHERBY_SHRIMP(1518, arrayOf(
            FishOption(FishingTool.SMALL_FISHING_NET, listOf(
                    FishReward(Fish.SHRIMP),
                    FishReward(Fish.ANCHOVIES)
            )),
            FishOption(FishingTool.FISHING_ROD, listOf(
                    FishReward(Fish.SARDINE),
                    FishReward(Fish.HERRING)
            ))
    )),
    KARAJ_SHRIMP(1521, arrayOf(
            FishOption(FishingTool.SMALL_FISHING_NET, listOf(
                    FishReward(Fish.SHRIMP),
                    FishReward(Fish.ANCHOVIES)
            )),
            FishOption(FishingTool.FISHING_ROD, listOf(
                    FishReward(Fish.SARDINE),
                    FishReward(Fish.HERRING)
            ))
    )),
    DRAYNOR_SHRIMP(1525, arrayOf(
            FishOption(FishingTool.SMALL_FISHING_NET, listOf(
                    FishReward(Fish.SHRIMP),
                    FishReward(Fish.ANCHOVIES)
            )),
            FishOption(FishingTool.FISHING_ROD, listOf(
                    FishReward(Fish.SARDINE),
                    FishReward(Fish.HERRING)
            ))
    )),
    FROGSPAWN(1497, arrayOf(
            FishOption(FishingTool.SMALL_FISHING_NET, listOf(FishReward(Fish.FROG_SPAWN))),
            FishOption(FishingTool.FISHING_ROD, listOf(
                    FishReward(Fish.SLIMY_EEL),
                    FishReward(Fish.CAVE_EEL)
            ))
    )),
    FROGSPAWN_2(1498, arrayOf(
            FishOption(FishingTool.SMALL_FISHING_NET, listOf(FishReward(Fish.FROG_SPAWN))),
            FishOption(FishingTool.FISHING_ROD, listOf(
                    FishReward(Fish.SLIMY_EEL),
                    FishReward(Fish.CAVE_EEL)
            ))
    )),
    KARAMBWAN(4714, arrayOf(
            FishOption(FishingTool.KARAMBWAN_VESSEL, listOf(FishReward(Fish.KARAMBWAN)))
    )),//TODO ADD IN SPOT SPAWN
    KARAMBWAN_2(4712, arrayOf(
            FishOption(FishingTool.KARAMBWAN_VESSEL, listOf(FishReward(Fish.KARAMBWAN)))
    )),//TODO ADD IN SPOT SPAWN
    KARAMBWANJI(4710, arrayOf(
            FishOption(FishingTool.SMALL_FISHING_NET, listOf(
                    FishReward(Fish.KARAMBWANJI) {
                        return@FishReward kotlin.math.floor(it.getLevel(Skill.FISHING) * .2).toInt() + 1
                    }))
    )),
    MONK_SHARK(5234, arrayOf(
            FishOption(FishingTool.HARPOON, listOf(FishReward(Fish.TUNA), FishReward(Fish.SWORDFISH))),
            FishOption(FishingTool.SMALL_FISHING_NET, listOf(FishReward(Fish.MONKFISH)))
    )),
    FISHING_GUILD_LOBSTER(1510, arrayOf(
            FishOption(FishingTool.LOBSTER_POT, listOf(FishReward(Fish.LOBSTER))),
            FishOption(FishingTool.HARPOON, listOf(
                    FishReward(Fish.TUNA),
                    FishReward(Fish.SWORDFISH)
            ))
    )),
    CATHERBY_LOBSTER(1519, arrayOf(
            FishOption(FishingTool.LOBSTER_POT, listOf(FishReward(Fish.LOBSTER))),
            FishOption(FishingTool.HARPOON, listOf(
                    FishReward(Fish.TUNA),
                    FishReward(Fish.SWORDFISH)
            ))
    )),
    RELLEKA_CAGE_HARPOON(3914, arrayOf(
            FishOption(FishingTool.LOBSTER_POT, listOf(FishReward(Fish.LOBSTER))),
            FishOption(FishingTool.HARPOON, listOf(
                    FishReward(Fish.TUNA),
                    FishReward(Fish.SWORDFISH)
            ))
    )),
    KARAJ_LOBBY(1522, arrayOf(
            FishOption(FishingTool.LOBSTER_POT, listOf(FishReward(Fish.LOBSTER))),
            FishOption(FishingTool.HARPOON, listOf(
                    FishReward(Fish.TUNA),
                    FishReward(Fish.SWORDFISH)
            ))
    )),
    FISHING_GUILD_SHARK(1511, arrayOf(
            FishOption(FishingTool.BIG_FISHING_NET, listOf(
                    FishReward(Fish.MACKEREL),
                    FishReward(Fish.COD),
                    FishReward(Fish.BASS)
            )),
            FishOption(FishingTool.HARPOON, listOf(FishReward(Fish.BIG_SHARK), FishReward(Fish.SHARK)))
    )),
    CATHERBY_SHARK(1520, arrayOf(
            FishOption(FishingTool.BIG_FISHING_NET, listOf(
                    FishReward(Fish.MACKEREL),
                    FishReward(Fish.OYSTER),
                    FishReward(Fish.COD),
                    FishReward(Fish.BASS),
                    FishReward(Fish.CASKET)
            )),
            FishOption(FishingTool.HARPOON, listOf(FishReward(Fish.SHARK)))
    )),
    RELLEKA_BIGNET_HARPOON(3915, arrayOf(
            FishOption(FishingTool.BIG_FISHING_NET, listOf(
                    FishReward(Fish.MACKEREL),
                    FishReward(Fish.OYSTER),
                    FishReward(Fish.COD),
                    FishReward(Fish.BASS),
                    FishReward(Fish.CASKET)
            )),
            FishOption(FishingTool.HARPOON, listOf(FishReward(Fish.SHARK)))
    )),
    LEAPING(1542, arrayOf(FishOption(FishingTool.BARBARIAN_ROD, listOf(
            FishReward(Fish.LEAPING_TROUT),
            FishReward(Fish.LEAPING_SALMON),
            FishReward(Fish.LEAPING_STURGEON)))
    )),
    TROUT_SALMON_EDGE(1526, arrayOf(
            FishOption(FishingTool.FLY_FISHING_ROD, listOf(
                    FishReward(Fish.TROUT),
                    FishReward(Fish.SALMON),
                    FishReward(Fish.RAINBOW_FISH)
            )),
            FishOption(FishingTool.FISHING_ROD, listOf(
                    FishReward(Fish.PIKE)
            ))
    )),
    SEERS_VILLAGE_LURE_BAIT(1513, arrayOf(
            FishOption(FishingTool.FLY_FISHING_ROD, listOf(
                    FishReward(Fish.TROUT),
                    FishReward(Fish.SALMON),
                    FishReward(Fish.RAINBOW_FISH)
            )),
            FishOption(FishingTool.FISHING_ROD, listOf(
                    FishReward(Fish.PIKE)
            ))
    )),
    ANGLER(6825, arrayOf(FishOption(FishingTool.FISHING_ROD, listOf(FishReward(Fish.ANGLER_FISH))))),
    INFERNAL_EEL(7676, arrayOf(FishOption(FishingTool.OILY_FISHING_ROD, listOf(FishReward(Fish.INFERNAL_EEL), FishReward(Fish.LAVA_EEL), FishReward(Fish.PADDLEFISH))))),
    //SACRED_EEL(2654, arrayOf(FishOption(FishingTool.FISHING_ROD, listOf(FishReward(Fish.SACRED_EEL))))),
    SACRED_EEL_2(6488, arrayOf(FishOption(FishingTool.FISHING_ROD, listOf(FishReward(Fish.SACRED_EEL))))),
    SACRED_EEL_3(6489, arrayOf(FishOption(FishingTool.FISHING_ROD, listOf(FishReward(Fish.SACRED_EEL))))),
    DARK_CRAB(1536 ,arrayOf(FishOption(FishingTool.LOBSTER_POT, listOf(FishReward(Fish.DARK_CRAB))))),
    MANTA_RAY_SEA_TURTLE(5233, arrayOf(FishOption(FishingTool.BIG_FISHING_NET, listOf(FishReward(Fish.MANTA_RAY), FishReward(Fish.SEA_TURTLE)))));

    companion object {
        init {
            // This can be initialized either here
            for (spot in values()) {
                NPCActions.onClick(spot.npcId) {
                    when (it.type) {
                        NPCActions.ClickAction.Type.FIRST_OPTION -> SkillUtil.startSkillable(it.player, FishingAction(spot.options[0], it.npc))
                        NPCActions.ClickAction.Type.SECOND_OPTION -> SkillUtil.startSkillable(it.player, FishingAction(spot.options[1], it.npc))
                        else -> return@onClick false
                    }
                    return@onClick true
                }
            }
        }
    }
}