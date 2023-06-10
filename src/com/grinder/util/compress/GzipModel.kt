package com.grinder.util.compress

import java.nio.file.Paths

object GzipModel {
    @JvmStatic
    fun main(args: Array<String>) {
        val path = Paths.get("/Users/stanvanderbend/GrinderScapeCache/32598.dat")
        val file = path.toFile()
        file.writeBytes(GZip.compress(file.readBytes()))
        file.renameTo(Paths.get("/Users/stanvanderbend/GrinderScapeCache/32598.gz").toFile())
    }
}