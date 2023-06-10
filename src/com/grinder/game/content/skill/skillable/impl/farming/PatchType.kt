package com.grinder.game.content.skill.skillable.impl.farming

import com.grinder.game.World
import com.grinder.game.entity.`object`.DynamicGameObject
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Position
import com.grinder.game.model.area.RegionCoordinates
import java.util.function.Consumer

/**
 * This enum contains the data for all farming patches.
 *
 * @author Austin
 */
enum class PatchType(var idx: Int,
                     var position: Position,
                     var plantType: PlantType, var varbit: Int
) {
    FALADOR_HERB(0, Position(3058, 3311), PlantType.HERB, 4774), //8150
    PHASMATYS_HERB(1, Position(3605, 3529), PlantType.HERB, 4774), //8153
    CATHERBY_HERB(2, Position(2813, 3463), PlantType.HERB, 4774), //8151
    CATHERBY_HERB2(2, Position(2860, 3433), PlantType.FRUIT_TREE, 4771), //7965 // fruit tree XD
    ARDOUGNE_HERB(3, Position(2670, 3374), PlantType.HERB, 4774), //8152
    RUBY_MEMBER_HERB(4, Position(3694, 2968), PlantType.HERB/*PlantType.FRUIT_TREE*/, 4774); //7965 //these are fruit trees XD


    companion object {
        fun getPatchForPosition(position: Position): PatchType? {
            for (patchType in values()) if (patchType.position == position) return patchType
            return null
        }

        init {

            //I guess we need to convert those fruit tree patches into herb patches?
            World.addObject(DynamicGameObject.createPublic(8150, RUBY_MEMBER_HERB.position, 10, 0))


            for (patchType in values()) {
                var loader = World.regions
                var coord = RegionCoordinates.fromPosition(patchType.position)
                var region = loader.get(coord)

                val consumer = Consumer<Player> { player ->
                    val patch = player.farming.getPatch(patchType.position)?.reloadConfig()
                }

                region.addRegionLoadAction(consumer)
            }
        }
    }


}