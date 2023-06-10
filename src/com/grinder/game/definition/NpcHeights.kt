package com.grinder.game.definition

import com.google.gson.JsonParser
import com.grinder.game.entity.agent.npc.NPC
import java.nio.file.Paths

/**
 * This class contains the model heights of all npcs with a combat level of 1 or higher.
 *
 * The client contains a "dumpnpcheights" command to re-generate this file.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   09/04/2020
 * @version 1.0
 */
object NpcHeights {

    private val heightMap = HashMap<Int, Int>()

    init {

        val file = Paths.get("data", "definitions", "npc_model_heights.json").toFile()
        if(file.exists()){
            val array = JsonParser().parse(file.readText()).asJsonArray
            for(element in array){
                val obj = element.asJsonObject
                val npcId = obj.getAsJsonPrimitive("npc").asInt
                val height = obj.getAsJsonPrimitive("height").asInt
                heightMap[npcId] = height
            }
        }
    }

    fun getNpcHeight(npc: NPC)=  heightMap.getOrDefault(npc.id, 200)

}