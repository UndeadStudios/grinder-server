package com.grinder.game.content.skill.skillable.impl.farming

import com.grinder.game.content.miscellaneous.PetHandler
import com.grinder.game.content.skill.skillable.impl.farming.attribute.CompostBinValueHolder
import com.grinder.game.content.skill.skillable.impl.farming.attribute.PatchValueHolder
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.checkLevel
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants
import com.grinder.game.entity.agent.player.event.impl.PlayerLoginEvent
import com.grinder.game.entity.agent.player.getLevel
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.model.ItemActions
import com.grinder.game.model.PlayerActions
import com.grinder.game.model.Position
import com.grinder.game.model.Skill
import com.grinder.game.model.area.Region
import com.grinder.game.model.attribute.Attribute
import com.grinder.util.Misc
import java.util.*

/**
 * This class is used to track and manage all farming patches
 *
 * @author Austin
 * @author Stan van der Bend
 */
class Farming(private val player: Player) {

    companion object {
        init {
            PlayerActions.onEvent(PlayerLoginEvent::class) {
                for (patch in player.getPatches())
                    patch.init(player)
                for (bin in player.getBins())
                    bin.init(player)
            }
            val plantSeedItemIds = Plant.getHerbs().map { it.seed.id }
            ItemActions.onItemOnObjectByItemId(
                    *(FarmingConstants.HERBS
                            + FarmingConstants.COMPOST_POTIONS
                            + FarmingConstants.WEEDS
                            + FarmingConstants.BUCKET
                            + FarmingConstants.SPADE
                            + plantSeedItemIds
                            + FarmingConstants.COMPOST
                            + FarmingConstants.SUPER_COMPOST
                            + FarmingConstants.RAKE
                            + FarmingConstants.CURE
                            + FarmingConstants.SECATEURS
                            + FarmingConstants.MAGIC_SECATEURS)
            ) {
                val farming = player.farming?:return@onItemOnObjectByItemId false
                val itemId = getItemId()
                val position = gameObject.position
                val bin = farming.getBin(position)
                if (bin != null){
                    return@onItemOnObjectByItemId when {
                        FarmingConstants.WEEDS == itemId -> bin.addToBin().returnTrue()
                        FarmingConstants.BUCKET == itemId -> bin.takeCompost().returnTrue()
                        FarmingConstants.HERBS.contains(itemId) -> bin.addSuperToBin().returnTrue()
                        FarmingConstants.COMPOST_POTIONS.contains(itemId) -> bin.compostPotion(getItem()).returnTrue()
                        else -> false
                    }
                }
                val patch = farming.getPatch(position)
                if (patch != null) {
                    return@onItemOnObjectByItemId when {
                        FarmingConstants.SPADE == itemId -> patch.digPlant().returnTrue()
                        plantSeedItemIds.contains(itemId) -> {
                            if (farming.playerHasDibber()) {
                                val plant = Plant.getHerbs().find { it.seed.id == itemId }
                                if (plant != null)
                                    if (player.checkLevel(Skill.FARMING, plant.getLevelReq()))
                                        patch.plantSeed(plant)
                            } else
                                player.message("You must have a Seed dibber to plant the seed.")
                            true
                        }
                        else -> farming.handleObjectOptions(position)
                    }
                }
                return@onItemOnObjectByItemId false
            }
        }

        fun Player.getPatches() = attributes.getValue(Attribute.FARMING_PATCHES) {PatchValueHolder(generatePatches())}
        fun Player.getBins() = attributes.getValue(Attribute.FARMING_COMPOST_BINS) {CompostBinValueHolder(generateBins())}

        private fun generatePatches() = PatchType.values().map { FarmingPatch(it) }.toTypedArray()
        private fun generateBins() = BinType.values().map { CompostBin(it) }.toTypedArray()

        private fun <T> T.returnTrue() = let { true }
    }


    fun process() {
        for (patch in player.getPatches()) {
            patch.process()
        }
        for (bin in player.getBins()) {
            bin.process()
        }
    }

    fun reloadConfig(patch: PatchType) {

    }

    fun reloadPlants() {
        for (patch in player.getPatches()) {
            if (Misc.getDistance(patch.patchType.position, player.position) <= Region.VIEWPORT_WIDTH) {
                //patch.reloadObject()
            }
        }
        for (bin in player.getBins()) {
            if (Misc.getDistance(bin.binType.position, player.position) <= Region.VIEWPORT_WIDTH) {
                //bin.reloadObject()
            }
        }
    }

    fun findFarmingObject(position: Position) : Optional<GameObject> {
        return Optional.empty();
//        return Optional.ofNullable(
//                getPatch(position)?.patchObject
//                ?:getBin(position)?.binObject)
    }

    fun handleObjectOptions(position: Position): Boolean {
        val patch = getPatch(position)
        val bin = getBin(position)
        if (bin != null) {
            if (bin.binState == BinState.FILLED || bin.binState == BinState.FILLED_SUPER)
                bin.closeBin()
            else if (bin.binState == BinState.CLOSED || bin.binState == BinState.CLOSED_SUPER) {
                if (bin.isCanOpen)
                    bin.openBin()
                else
                    player.message("Your weeds aren't finished composting.")
            }
            return true
        }
        if (patch != null) {
            player.positionToFace = position
            when (patch.state) {
                PatchState.HERB_SEEDED -> player.sendMessage("Your herbs are 0% grown.")
                PatchState.HERB_GROWING_1 -> player.sendMessage("Your herbs are 20% grown.")
                PatchState.HERB_GROWING_2 -> player.sendMessage("Your herbs are 40% grown.")
                PatchState.HERB_GROWING_3 -> player.sendMessage("Your herbs are 60% grown.")
                PatchState.HERB_GROWING_4 -> player.sendMessage("Your herbs are 80% grown.")
                PatchState.HERB_GRASS_2, PatchState.HERB_GRASS_3, PatchState.HERB_GRASS_4 -> if (playerHasRake()) patch.rakeGrass() else player.sendMessage("You must have a Rake to clear the weeds.")
                PatchState.HERB_DISEASED_1, PatchState.HERB_DISEASED_2, PatchState.HERB_DISEASED_3 -> if (playerHasCure()) patch.curePlant() else player.sendMessage("You must have a Plant cure to cure the plant.")
                PatchState.HERB_RAKED -> if (playerHasSuperCompost()) {
                    patch.addCompost(CompostType.SUPERCOMPOST)
                } else if (playerHasCompost()) {
                    patch.addCompost(CompostType.COMPOST)
                } else if (playerHasDibber()) {
                    val plant = getFirstPlant()
                    if (plant == null)
                        player.message("You must have a seed to plant.")
                    else
                        patch.plantSeed(plant)
                } else
                    player.message("You must have a Seed dibber to plant the seed.")
                PatchState.HERB_COMPOST, PatchState.HERB_SUPERCOMPOST -> if (playerHasDibber()) {
                    val plant = getFirstPlant()
                    if (plant == null)
                        player.message("You must have a seed to plant.")
                    else
                        patch.plantSeed(plant)
                } else player.sendMessage("You must have a Seed dibber to plant the seed.")
                PatchState.HERB_DEAD_1, PatchState.HERB_DEAD_2, PatchState.HERB_DEAD_3 ->
                    if (playerHasSpade())
                        patch.digPlant()
                    else
                        player.message("You must have a Spade to dig up the dead plant.")
                PatchState.HERB_GROWN -> patch.pick()
                else -> { }
            }
            return true
        }
        return false
    }

    private fun getFirstPlant() : Plant? {
        for (item in player.inventory.items)
            if (item != null)
                for (plant in Plant.getHerbs())
                    if (item.id == plant.getSeed().id)
                        if (player.getLevel(Skill.FARMING) >= plant.getLevelReq())
                            return plant
        // Roll for pet
        PetHandler.onSkill(player, Skill.FARMING);
        return null
    }

    public fun getPatch(position: Position): FarmingPatch? {
        return  player.getPatches().find {
            val patchPosition = it.patchType?.position
            patchPosition != null && patchPosition.sameAs(position)
        }
    }

    public fun getBin(position: Position): CompostBin? {
        return player.getBins()
            .find {
                val binPosition = it.binType?.position
                binPosition != null && binPosition.sameAs(position)
            }
    }

    private fun playerHasCure(): Boolean {
        return player.inventory.contains(FarmingConstants.CURE)
    }

    private fun playerHasRake(): Boolean {
        return player.inventory.contains(FarmingConstants.RAKE)
    }

    private fun playerHasSpade(): Boolean {
        return player.inventory.contains(FarmingConstants.SPADE)
    }

    private fun playerHasDibber(): Boolean {
        return player.inventory.contains(FarmingConstants.SEED_DIBBER)
    }

    fun playerHasTrowel(): Boolean {
        return player.inventory.contains(FarmingConstants.TROWEL)
    }

    fun playerHasSecateurs(): Boolean {
        return player.inventory.contains(FarmingConstants.SECATEURS) || player.inventory.contains(FarmingConstants.MAGIC_SECATEURS) || player.equipment[EquipmentConstants.WEAPON_SLOT].id == FarmingConstants.MAGIC_SECATEURS
    }

    fun playerHasMagicSecateurs(): Boolean {
        if (player.inventory.contains(FarmingConstants.MAGIC_SECATEURS)) return true
        val weapon = player.equipment.atSlot(EquipmentConstants.WEAPON_SLOT) ?: return false
        return weapon.id == FarmingConstants.MAGIC_SECATEURS
    }

    fun playerHasAndDecrementWateringCan(): Boolean {
        for (i in FarmingConstants.WATERING_CANS.indices) if (player.inventory.getById(FarmingConstants.WATERING_CANS[i]) != null) {
            if (i + 1 < FarmingConstants.WATERING_CANS.size) player.inventory.getById(FarmingConstants.WATERING_CANS[i]).id = FarmingConstants.WATERING_CANS[i + 1] else player.inventory.getById(FarmingConstants.WATERING_CANS[i]).id = FarmingConstants.EMPTY_WATERING_CAN
            return true
        }
        return false
    }

    fun playerHasBucket(): Boolean {
        return player.inventory.contains(FarmingConstants.BUCKET)
    }

    private fun playerHasCompost(): Boolean {
        return player.inventory.contains(FarmingConstants.COMPOST)
    }

    private fun playerHasSuperCompost(): Boolean {
        return player.inventory.contains(FarmingConstants.SUPER_COMPOST)
    }

}