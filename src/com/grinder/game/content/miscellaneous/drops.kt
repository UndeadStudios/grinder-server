package com.grinder.game.content.miscellaneous

import com.grinder.game.definition.ItemDefinition
import com.grinder.game.definition.NpcDefinition
import com.grinder.game.definition.NpcDropDefinition
import com.grinder.game.definition.loader.impl.ItemDefinitionLoader
import com.grinder.game.definition.loader.impl.NpcDefinitionLoader
import com.grinder.game.definition.loader.impl.NpcDropDefinitionLoader
import java.nio.file.Paths

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   01/12/2019
 * @version 1.0
 */

fun main(){
    ItemDefinitionLoader().load()
    NpcDefinitionLoader().load()
    NpcDropDefinitionLoader().load()
    val file = Paths.get("npc_drops_dump.txt").toFile()
    file.createNewFile()
    val writer = file.printWriter()

    NpcDefinition.getDefinitions().forEach { (id, definition) ->

        if(definition.name != null && definition.name.isNotEmpty()) {

            val optionalDropDefinition = NpcDropDefinition.get(id)

            if(optionalDropDefinition.isPresent) {
                val dropDefinition = optionalDropDefinition.get()
                writer.println("----------------------------------------------------------------")
                writer.println("Table for ${definition.name}")
                writer.println("Always:")
                dropDefinition.alwaysDrops?.forEach {
                    val itemDefinition = ItemDefinition.forId(it.itemId)
                    val amountString = if(it.minAmount == it.maxAmount) "${it.minAmount}" else "${it.minAmount}..${it.maxAmount}"
                    writer.format("%30s\t%16s\n", itemDefinition.name, amountString)
                }
                writer.println("Common:")
                dropDefinition.commonDrops?.forEach {
                    val itemDefinition = ItemDefinition.forId(it.itemId)
                    val amountString = if(it.minAmount == it.maxAmount) "${it.minAmount}" else "${it.minAmount}..${it.maxAmount}"
                    val chanceString = if(it.chance == 0) "5" else it.chance.toString()
                    writer.format("%30s\t%16s\t%s\n", itemDefinition.name, amountString, chanceString)
                }
                writer.println("Uncommon:")
                dropDefinition.commonDrops?.forEach {
                    val itemDefinition = ItemDefinition.forId(it.itemId)
                    val amountString = if(it.minAmount == it.maxAmount) "${it.minAmount}" else "${it.minAmount}..${it.maxAmount}"
                    val chanceString = if(it.chance == 0) "10" else it.chance.toString()
                    writer.format("%30s\t%16s\t%s\n", itemDefinition.name, amountString, chanceString)
                }
                writer.println("Rare:")
                dropDefinition.rareDrops?.forEach {
                    val itemDefinition = ItemDefinition.forId(it.itemId)
                    val amountString = if(it.minAmount == it.maxAmount) "${it.minAmount}" else "${it.minAmount}..${it.maxAmount}"
                    val chanceString = if(it.chance == 0) "25" else it.chance.toString()
                    writer.format("%30s\t%16s\t%s\n", itemDefinition.name, amountString, chanceString)
                }
                writer.println("Very Rare:")
                dropDefinition.veryRareDrops?.forEach {
                    val itemDefinition = ItemDefinition.forId(it.itemId)
                    val amountString = if(it.minAmount == it.maxAmount) "${it.minAmount}" else "${it.minAmount}..${it.maxAmount}"
                    val chanceString = if(it.chance == 0) "50" else it.chance.toString()
                    writer.format("%30s\t%16s\t%s\n", itemDefinition.name, amountString, chanceString)
                }
                writer.println("Special:")
                dropDefinition.specialDrops?.forEach {

                    if(it != null)
                    {
                        val itemDefinition = ItemDefinition.forId(it.itemId)
                        val amountString = if(it.minAmount == it.maxAmount) "${it.minAmount}" else "${it.minAmount}..${it.maxAmount}"
                        val chanceString = if(it.chance >= 64) "128" else it.chance.times(2).toString()
                        writer.format("%30s\t%16s\t%s\n", itemDefinition.name, amountString, chanceString)
                    }

                }
            }
        }

    }
    writer.flush()
    writer.close()
}