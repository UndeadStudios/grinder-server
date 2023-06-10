package com.grinder.game.model

import com.google.common.base.Preconditions
import com.grinder.game.entity.Entity
import com.grinder.game.entity.EntityType
import com.grinder.game.model.area.EntityUpdateType
import com.grinder.game.model.area.Region
import com.grinder.game.model.area.update.GraphicUpdateOperation
import com.grinder.game.model.area.update.GroupableEntity

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   04/04/2020
 * @version 1.0
 */
class TileGraphic(position: Position, val graphic: Graphic) : Entity(position), GroupableEntity {

    override fun getEntityType() = EntityType.GRAPHIC

    override fun toUpdateOperation(region: Region, type: EntityUpdateType): GraphicUpdateOperation? {
        Preconditions.checkArgument(type == EntityUpdateType.ADD, "Graphics cannot be removed from the client")
        return GraphicUpdateOperation(region, type, this)
    }
}