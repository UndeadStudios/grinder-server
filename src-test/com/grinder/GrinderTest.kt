package com.grinder

import com.grinder.game.collision.CollisionManager
import com.grinder.game.definition.loader.NpcStatsDefinitionLoader
import com.grinder.game.definition.loader.impl.ItemDefinitionLoader
import com.grinder.game.definition.loader.impl.ItemValueDefinitionLoader
import com.grinder.game.definition.loader.impl.NpcDefinitionLoader
import com.grinder.game.definition.loader.impl.ShopDefinitionLoader
import com.grinder.game.model.areas.AreaManager

open class GrinderTest {

    init {
        if (!setupAlready) {
            setupAlready = true
            ItemDefinitionLoader().load()
            ItemValueDefinitionLoader().load()
            ShopDefinitionLoader().load()
            CollisionManager.init()
            NpcDefinitionLoader().load()
            NpcStatsDefinitionLoader().load()
            AreaManager.load()
        }
    }

    companion object {
        private var setupAlready = false
    }
}