package com.grinder.game.content.skill.skillable.impl.farming

import com.grinder.game.World
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Position
import com.grinder.game.model.area.RegionCoordinates
import java.util.function.Consumer

enum class BinType(var position: Position, var face: Int) {
    FALADOR_BIN(Position(3056, 3312), 0),
    PHASMATYS_BIN(Position(3603, 3530), 2),
    CATHERBY_BIN(Position(2804, 3464), 3),
    ARDOUGNE_BIN(Position(2662, 3374), 3),
    CATHERBY_BIN_2(Position(2862, 3431), 1),
    RUBY_MEMBER_BIN(Position(3688, 2963), 2);

    companion object {
        init {

            //I guess we need to convert those fruit tree patches into herb patches?
            //World.addObject(DynamicGameObject.createPublic(7837, PatchType.MEMBERS_ZONE_HERB.position, 10, 0))


            for (binType in BinType.values()) {
                var loader = World.regions
                var coord = RegionCoordinates.fromPosition(binType.position)
                var region = loader.get(coord)

                val consumer = Consumer<Player> { player ->
                    val patch = player.farming.getBin(binType.position)?.reloadConfig()
                }

                region.addRegionLoadAction(consumer)
            }
        }
    }
}