package com.grinder.game.model.area.update

import com.grinder.game.message.impl.RegionUpdateMessage
import com.grinder.game.message.impl.SendTileGraphicMessage
import com.grinder.game.model.TileGraphic
import com.grinder.game.model.area.EntityUpdateType
import com.grinder.game.model.area.Region

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   04/04/2020
 * @version 1.0
 */
class GraphicUpdateOperation(region: Region, type: EntityUpdateType, entity: TileGraphic)
    : UpdateOperation<TileGraphic>(region, type, entity) {

    override fun add(offset: Int) = SendTileGraphicMessage(entity, offset)

    override fun remove(offset: Int): RegionUpdateMessage? {
        throw IllegalStateException("Graphics cannot be removed.")
    }
}