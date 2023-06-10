package com.grinder.net.packet

import io.netty.buffer.Unpooled
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   09/11/2019
 * @version 1.0
 */

val sourcesPath: Path = Paths.get("/Users/stanbend/IdeaProjects/GrinderScapeSource/src")
val packetListenerPath: Path  = sourcesPath.resolve("com/grinder/net/packet/impl")
val messagesPath: Path = sourcesPath.resolve("com/grinder/game/message/impl")
val decoderPath: Path = sourcesPath.resolve("com/grinder/game/message/decoder")
val encoderPath: Path = sourcesPath.resolve("com/grinder/game/message/encoder")

const val FILE_HEADER =
        "/**\n" +
        " * TODO: add documentation\n" +
        " *\n" +
        " * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)\n" +
        " * @since   27/11/2019\n" +
        " * @version 1.0\n" +
        " */\n"

fun main(){
//    writeDecoderMessages()

    val decoders = ArrayList<String>()

    sourcesPath.resolve("com/grinder/net/packet/PacketConfiguration.kt")
            .toFile()
            .readLines().forEach {
                if(it.trim().startsWith("listeners"))
                {
                    decoders.add(it.replace("PacketListener", "MessageDecoder"))
                }
            }
    for (decoder in decoders) {
        println(decoder)
    }
}

fun writeDecoderMessages(){

    Files.list(packetListenerPath)
            .forEach {
                val file = it.toFile()

                if(file.extension == "kt")
                    return@forEach

                try {

                    val reader = file.bufferedReader()

                    val messageName = file.nameWithoutExtension.removeSuffix("PacketListener") + "Message"
                    val messageFile = messagesPath.resolve("$messageName.kt").toFile()
                    messageFile.createNewFile()
                    val messageWriter = messageFile.printWriter()

                    val decoderName = messageName + "Decoder"
                    val decoderFile = decoderPath.resolve("$decoderName.kt").toFile()
                    decoderFile.createNewFile()
                    val decoderWriter = decoderFile.printWriter()

                    val importLines = ArrayList<String>()
                    var lineIndex = 0
                    var packetReaderParamName = ""
                    val variables = ArrayList<String>()
                    val params = ArrayList<String>()

                    while (true) {

                        val line = reader.readLine() ?: break

                        lineIndex++

                        if (line.trim().startsWith("import")) {
                            importLines.add(line)
                        }

                        if (line.trim().startsWith("public void handleMessage")) {
                            packetReaderParamName = line
                                    .substringAfter('(')
                                    .substringBefore(')')
                                    .split(',')
                                    .map { it.trim().removePrefix("final").trim() }
                                    .filter { it.isNotBlank() }
                                    .toList()[1].trim().split(' ')[1]
                        }
                        if (packetReaderParamName.isNotBlank() && line.contains("$packetReaderParamName.")) {
                            if (line.contains('=')) {
                                val sides = line.split('=')
                                val variable = sides[0]
                                val methodCall = sides[1].trim().removePrefix("$packetReaderParamName.").removeSuffix(";")
                                val prefix = when {
                                    variable.startsWith("final") -> "val"
                                    else -> "var"
                                }
                                val suffix = when {
                                    methodCall.contains("bytes", true) -> ""
                                    methodCall.contains("byte", true) ||
                                            methodCall.contains("short", true) -> ".toInt()"
                                    else -> ""
                                }
                                val var1 = variable.trim().removePrefix("final").trim().split(' ')
                                variables.add("$prefix ${var1[1]}: ${var1[0].capitalize()} = reader.$methodCall$suffix")
                                val paramSplit = sides[0].trim().removePrefix("final").trim().split(' ')
                                params.add("$prefix ${paramSplit[1]}: ${paramSplit[0].capitalize()}")
                            }
                        }
                    }
                    writeIncomingMessage(messageWriter, importLines, variables, messageName, params)
                    writeDecoder(decoderWriter, messageName, importLines, decoderName, variables)
                } catch (e: Exception) {
                    println("failed to parse $file")
                    e.printStackTrace()

                }
            }
}

private fun writeIncomingMessage(messageWriter: PrintWriter, importLines: ArrayList<String>, variables: ArrayList<String>, messageName: String, params: ArrayList<String>) {
    messageWriter.println("package com.grinder.game.message.impl")
    messageWriter.println()
    messageWriter.println("import com.grinder.game.message.Message")
    for (import in importLines) {
        messageWriter.println(import.removeSuffix(";"))
    }
    messageWriter.println()
    messageWriter.print(FILE_HEADER)
    if (variables.size > 0)
        messageWriter.print("data ")
    messageWriter.print("class $messageName(")
    for (param in params) {
        messageWriter.print(param)
        if (params.lastIndex != params.indexOf(param)) {
            messageWriter.print(", ")
        }
    }
    messageWriter.print(") : Message")
    messageWriter.println()
    messageWriter.flush()
    messageWriter.close()
}

private fun writeDecoder(decoderWriter: PrintWriter, messageName: String, importLines: ArrayList<String>, decoderName: String, variables: ArrayList<String>) {
    // DECODER FILE
    decoderWriter.println("package com.grinder.game.message.decoder")
    decoderWriter.println()
    decoderWriter.println("import com.grinder.game.message.MessageDecoder")
    decoderWriter.println("import com.grinder.net.packet.Packet")
    decoderWriter.println("import com.grinder.game.message.impl.$messageName")
    for (import in importLines) {
        decoderWriter.println(import.removeSuffix(";"))
    }
    decoderWriter.println()
    decoderWriter.print(FILE_HEADER)

    decoderWriter.println("class $decoderName: MessageDecoder<$messageName>() {")
    decoderWriter.println()
    decoderWriter.println("\toverride fun decode(packet: Packet) : $messageName {")
    decoderWriter.println("\t\tval reader = PacketReader(packet)")
    variables.forEach { variable ->
        decoderWriter.println("\t\t" + variable)
    }

    decoderWriter.print("\t\treturn $messageName(")
    variables.forEach { variable ->

        val var1 = variable.split(' ')[1].removeSuffix(":")
        decoderWriter.print(var1)
        if (variables.lastIndex != variables.indexOf(variable))
            decoderWriter.print(", ")
    }
    decoderWriter.println(')')
    decoderWriter.println("\t}")
    decoderWriter.println("}")
    decoderWriter.flush()
    decoderWriter.close()
}

fun writeEncoderMessages(){
    val file = sourcesPath.resolve("com/grinder/net/packet/PacketSender.java").toFile()

    val reader = file.bufferedReader()
    var i = 0
    val metaData = PacketConfiguration()
    val packets = HashMap<String, Packet>()
    val packetParams = HashMap<String, ArrayList<String>>()

    val methods = ArrayList<Method>()

    val imports = ArrayList<String>()

    var name = ""
    while(true){

        val line = reader.readLine() ?: break
        i++

        if(line.trim().startsWith("import")){
            imports.add(line)
        }

        if(line.trim().startsWith("public")){
            name = line.substringBefore('(').substringAfterLast(' ')
            methods.add(Method(name, line
                    .substringAfter('(')
                    .substringBefore(')')
                    .split(',')
                    .map { it.trim().removePrefix("final").trim() }
                    .filter { it.isNotBlank() }
                    .toList()))
        }

        if(line.contains("new PacketBuilder(")) {
            val partLine = line.substringAfter("new PacketBuilder")
            val opcode = partLine.subSequence(partLine.indexOf('(') + 1, partLine.indexOf(',')).toString().toInt()
            val type = metaData.outgoingPacketTypes[opcode]?:PacketType.RAW
            packets[name] = Packet(opcode, type, Unpooled.EMPTY_BUFFER)
            packetParams.putIfAbsent(name, ArrayList())
        }

    }
    PacketSender::class.java.methods.forEach {method ->
        var messageName = method.name!!
        val m = methods.find { it.name == messageName && it.params.size == method.parameterCount}
        val packet = packets[messageName]
        if(m == null){
            println("method for $messageName was not found!")
            return@forEach
        }
        if(packet == null){
            println("packet for $messageName was not found!")
            return@forEach
        }
        messageName = messageName.removePrefix("send").capitalize()
        messageName += "Message"


        val encoderName = messageName + "Decoder"
        val encoderFile = encoderPath.resolve("$encoderName.kt").toFile()
        encoderFile.createNewFile()
        val encoderWriter = encoderFile.printWriter()

        var messageFile = messagesPath.resolve("$messageName.kt").toFile()
        var n = 2
        while (messageFile.exists()){
            messageName+="${n++}"
            messageFile = messagesPath.resolve("$messageName.kt").toFile()
        }
        messageFile.createNewFile()
        val messageWriter = messageFile.printWriter()


        val usedImports = imports.filter {globalImport ->
            m.params.any {
                globalImport.substringAfterLast('.').substringBefore(';').trim() == it.split(' ')[0].trim()
            }
        }
        messageWriter.println("package com.grinder.game.message.impl")
        messageWriter.println()
        messageWriter.println("import com.grinder.game.message.Message")
        for(import in usedImports){
            messageWriter.println(import.removeSuffix(";"))
        }
        messageWriter.println()
        messageWriter.print(FILE_HEADER)
        if(method.parameterCount > 0)
            messageWriter.print("data ")
        messageWriter.print("class $messageName(")
        method.parameters.forEachIndexed { index, parameter ->
            val param = m.params[index].split(' ')

            val prefix = when {
                parameter.isVarArgs -> "val varargs"
                else -> "val"
            }
            messageWriter.print("$prefix ${param[1]}: ${param[0].capitalize()}")

            if(index < m.params.size-1)
                messageWriter.print(", ")
        }
        messageWriter.print(") : Message")
        messageWriter.println()
        messageWriter.flush()
        messageWriter.close()

        // DECODER FILE
//        encoderWriter.println("package com.grinder.game.message.encoder")
//        encoderWriter.println()
//        encoderWriter.println("import com.grinder.game.message.MessageEncoder")
//        encoderWriter.println("import com.grinder.net.packet.Packet")
//        encoderWriter.println("import com.grinder.game.message.impl.$messageName")
//
//        encoderWriter.println()
//        encoderWriter.print(FILE_HEADER)
//
//        encoderWriter.println("class $encoderName: MessageEncoder<$messageName>() {")
//        encoderWriter.println()
//        encoderWriter.println("\toverride fun encode(message: $messageName) : $messageName {")
//        encoderWriter.println("\t\tval builder = PacketBuilder(packet)")
//        variables.forEach { variable ->
//            encoderWriter.println("\t\t" + variable)
//        }
//        method.parameters.forEachIndexed { index, parameter ->
//            val param = m.params[index].split(' ')
//
//            val prefix = when {
//                parameter.isVarArgs -> "val varargs"
//                else -> "val"
//            }
//            messageWriter.print("$prefix ${param[1]}: ${param[0].capitalize()}")
//
//            if(index < m.params.size-1)
//                messageWriter.print(", ")
//        }
//        encoderWriter.print("\t\treturn $messageName(")
//        variables.forEach { variable ->
//
//            val var1 = variable.split(' ')[1].removeSuffix(":")
//            encoderWriter.print(var1)
//            if (variables.lastIndex != variables.indexOf(variable))
//                encoderWriter.print(", ")
//        }
//        encoderWriter.println(')')
//        encoderWriter.println("\t}")
//        encoderWriter.println("}")
//        encoderWriter.flush()
//        encoderWriter.close()
    }

}

class Method(val name: String, val params: List<String>)

fun writePacketTypes(){
    val file = Paths.get("/Users/stanbend/IdeaProjects/GrinderScapeSource/src/com/grinder/net/packet/PacketSender.java").toFile()

    val reader = file.bufferedReader()

    var i = 0
    val metaData = PacketConfiguration()

    val newFile = Paths.get("/Users/stanbend/IdeaProjects/GrinderScapeSource/src/com/grinder/net/packet/PacketSender2.java").toFile()
    val writer = newFile.bufferedWriter()

    while(true){
        var line = reader.readLine() ?: break
        i++
        if(line.contains("new PacketBuilder(")) {
            var partLine = line.substringAfter("new PacketBuilder")
            val opcode = partLine.subSequence(partLine.indexOf('(') + 1, partLine.indexOf(')')).toString().toInt()
            val type = metaData.outgoingPacketTypes[opcode]?:PacketType.RAW
            line = line.substringBefore(partLine) + "($opcode, PacketType.$type)" + partLine.substringAfter(')')
        }
        writer.write(line)
        writer.newLine()
    }
    writer.flush()
    writer.close()
}

fun main2(){

    val file = Paths.get("/Users/stanbend/IdeaProjects/GrinderScapeSource/src/com/grinder/net/packet/PacketSender.java").toFile()

    val reader = file.bufferedReader()

    var i = 0

    val details = ArrayList<OutgoingPacketDetails>()

    while(true){

        var line = reader.readLine() ?: break
        i++

        if(line.contains("new PacketBuilder(")){
            line = line.substringAfter("new PacketBuilder")
            val opcode = line.subSequence(line.indexOf('(')+1, line.indexOf(')')).toString().toInt()

            var strings = 0
            var bytes = 0
            var ints = 0
            var shorts = 0
            var longs = 0

            loop@ while(true){

                var next = reader.readLine()?.trim() ?: break
                i++

                if(next.isEmpty())
                    continue

                if(!next.endsWith(';')){
                    println("suspicious line for opcode($opcode) -> [$i]: $next")
                }

                next = next.substringAfter('.')
                when {
                    next.startsWith("putString(") -> strings++
                    next.startsWith("put(") -> bytes++
                    next.startsWith("putInt(") -> ints++
                    next.startsWith("putShort(") -> shorts++
                    next.startsWith("putLong(") -> longs++
                    else -> break@loop
                }
            }

            details.add(OutgoingPacketDetails(opcode, bytes, shorts, ints, longs, strings))
        }
    }

    details.add(OutgoingPacketDetails(65))
    details.add(OutgoingPacketDetails(81))
    details.sortBy { it.opcode }
    println("client:")
    details.map { it.exportLength() }.toSet().forEach {
        println(it)
    }
    println("server:")
    details.map { it.exportType() }.toSet().forEach {
        println(it)
    }
}

class OutgoingPacketDetails(
        opcode: Int,
        bytes: Int = 0,
        shorts: Int = 0,
        ints: Int = 0,
        longs: Int = 0,
        strings: Int = 0)
    : PacketDetails(opcode, bytes, shorts, ints, longs, strings) {

    override fun type() : PacketType {
        return when {
            opcode == 53 ||
                    opcode == 65 ||
                    opcode == 81 ||
                    opcode == 123 ||
                    opcode == 126 ||
                    opcode == 214 ||
                    opcode == 231 ||
                    opcode == 241 ||
                    opcode == 254 ||
                    strings > 1 ||
                    strings == 1 && fixedLength() > 0 -> PacketType.VARIABLE_SHORT
            opcode == 104 ||
                    opcode == 196 ||
                    opcode == 253 ||
                    strings == 1 -> PacketType.VARIABLE_BYTE
            else -> PacketType.FIXED
        }
    }
}
class IncomingPacketDetails(
        opcode: Int,
        bytes: Int = 0,
        shorts: Int = 0,
        ints: Int = 0,
        longs: Int = 0,
        strings: Int = 0)
    : PacketDetails(opcode, bytes, shorts, ints, longs, strings) {

    override fun type() : PacketType {
        return when {
            opcode == 250 ||
                    strings > 1 ||
                    strings == 1 && fixedLength() > 0 -> PacketType.VARIABLE_SHORT
            opcode == 98 ||
                    opcode == 164 ||
                    opcode == 248 ||
                    strings == 1 -> PacketType.VARIABLE_BYTE
            else -> PacketType.FIXED
        }
    }

    override fun fixedLength(): Int {
        return when(opcode) {
            11 -> 1 + 7 + 5
            else -> super.fixedLength()
        }
    }
}

abstract class PacketDetails(
        val opcode: Int,
        val bytes: Int = 0,
        val shorts: Int = 0,
        val ints: Int = 0,
        val longs: Int = 0,
        val strings: Int = 0
) {
    override fun toString(): String {
        return "PacketDetails(opcode=$opcode, bytes=$bytes, shorts=$shorts, ints=$ints, longs=$longs, strings=$strings)"
    }

    fun exportType() = "TYPE[$opcode] = ${type()};"
    fun exportLength() = "PACKET_LENGTHS[$opcode] = ${length()}; "

    abstract fun type(): PacketType

    fun length() = type().let { if(it == PacketType.FIXED) fixedLength().toString() else it.name }

    open fun fixedLength() = bytes + shorts.times(2) + ints.times(4) + longs.times(8)
}