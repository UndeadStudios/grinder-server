package com.grinder.net.packet.impl

import java.nio.file.Paths

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   08/02/2020
 * @version 1.0
 */
fun main() {

    val map = HashMap<Int, String>()
    for(line in Paths.get("/Users/stanbend/IdeaProjects/GrinderScapeSource/src/com/grinder/util/ObjectIdentifiers.java").toFile().readLines()){
        if(line.contains("public static final int")){
            val nameId = line.trim().removePrefix("public static final int ").split(" = ")
            val name = nameId[0]
            val id = nameId[1].drop(1).toInt()
            map[id] = name
        }
    }

    for(line in Paths.get("ObjectActionPacketListener.java").toFile().readLines()){
        if(line.contains("case")){
            val id = line.trim().substring(4).drop(1).toInt()
            val name = map[id]
            println("$id -> $name")
        }
    }
}