package com.grinder.util

import com.grinder.net.packet.IncomingPacketDetails
import java.nio.file.Files
import java.nio.file.Paths

object JavaPacketDetailsParser {
    @JvmStatic
    fun main(args: Array<String>) {
        val details = ArrayList<IncomingPacketDetails>()
        Files.list(Paths.get("/Users/stanbend/IdeaProjects/GrinderScapeClient/src/main/java/com/runescape/io/packets/outgoing/impl"))
                .sorted().forEach {
                    val packetName = it.fileName.toString().dropLast(5) // drop .java
                    val text = it.toFile().readText().split("create()")[1]

                    val parts = text.split(".")

                    val onset = parts.indexOfFirst { !it.replace('{', ' ').trim().startsWith("//") }

                    val firstPart = parts[onset]


                    val opcode = firstPart.substring(firstPart.indexOf('(') + 1, firstPart.indexOf(')')).toInt()


                    if (parts.size == onset + 1) {
                        details.add(IncomingPacketDetails(opcode))
                        return@forEach
                    }

                    if(opcode == 247){
                        println("eh")
                    }
                    var bytes = 0
                    var shorts = 0
                    var ints = 0
                    var longs = 0
                    var strings = 0
                    for (i in onset + 1 until parts.size) {
                        val part = parts[i].trim()

                        if (part.startsWith("//"))
                            continue

                        when {
                            part.contains("putString") -> strings++
                            part.contains("byte", true) -> bytes++
                            part.contains("word", true) ||
                                    part.contains("short", true) -> shorts++
                            part.contains("int", true) -> ints++
                            part.contains("long", true) -> longs++

                        }
                    }
                    println("[$onset] parsing $packetName -> $opcode, $bytes, $shorts, $ints, $longs, $strings")
                    details.add(IncomingPacketDetails(opcode, bytes, shorts, ints, longs, strings))
                }

        details.add(IncomingPacketDetails(98))
        details.add(IncomingPacketDetails(164))
        details.add(IncomingPacketDetails(248))
//            details.sortBy { it.opcode }
        println("client:")
        details.map { it.exportLength() }.toSet().forEach {
            println(it)
        }
        println("server:")
        details.map { it.exportType() }.toSet().forEach {
            println(it)
        }
    }
}