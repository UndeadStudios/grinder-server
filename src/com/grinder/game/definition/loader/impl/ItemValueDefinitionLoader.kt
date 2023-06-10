package com.grinder.game.definition.loader.impl

import com.google.gson.GsonBuilder
import com.grinder.game.GameConstants
import com.grinder.game.definition.ItemValueDefinition
import com.grinder.game.definition.loader.DefinitionLoader
import org.apache.logging.log4j.LogManager
import java.io.File
import java.lang.Exception

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   19/01/2020
 * @version 1.0
 */
class ItemValueDefinitionLoader : DefinitionLoader() {

    private val logger = LogManager.getLogger(ItemValueDefinitionLoader::class.java)
    private val gson = GsonBuilder().create()!!

    override fun file() =  GameConstants.DEFINITIONS_DIRECTORY + "item_values.json"

    override fun load() {
        try {
            val values = gson.fromJson<HashMap<Int, ItemValueDefinition>>(File(file()).bufferedReader(), ItemValueDefinition.itemValueMapType)
            ItemValueDefinition.itemValueMap.clear()
            ItemValueDefinition.itemValueMap.putAll(values)
        } catch (e: Exception) {
            logger.error("Failed to load item value definitions!", e)
        }
    }
}