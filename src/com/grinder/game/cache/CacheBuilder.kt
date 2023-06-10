package com.grinder.game.cache

import com.grinder.game.WorldConstants
import com.grinder.game.cache.progress.AbstractProgressListener
import com.grinder.game.cache.progress.ProgressListener
import com.grinder.game.cache.store.index.OSRSIndices
import java.nio.file.Files
import java.nio.file.Paths

/**
 * An utility for packing custom files to the oldschool cache.
 *
 * @author Blaketon
 */
object CacheBuilder {

    private lateinit var cache: CacheLibrary
    private val modelsPath = Paths.get("./data", "cache", "idx7")

    @JvmStatic
    fun main(args: Array<String>) {
        cache = CacheLibrary.createUncached(WorldConstants.OLDSCHOOL_STORE_PATH.toString())
        packModels()
    }

    private fun packModels() {
        val modelDir = modelsPath.toFile()
        if (modelDir.exists()) {
            val index = cache.getIndex(OSRSIndices.MODELS)
            val files = modelDir.listFiles()
            var completed = 0.0
            files?.forEach { file ->
                if (file.extension != "dat") return@forEach
                var data = Files.readAllBytes(file.toPath())

                val id = file.nameWithoutExtension.toInt()
                val archive = index.getArchive(id)
                if (archive == null) {
                    index.addArchive(id).addFile(0, data)
                } else {
                    archive.getFile(0).data = data
                    archive.flag()
                }
                progressListener.notify(++completed / files.size, "Packing model ${file.name}")
            }

            index.update(progressListener)
        }
    }

    private val progressListener: ProgressListener = object : AbstractProgressListener() {
        override fun finish(title: String, message: String) {}
        override fun change(progress: Double, message: String) {}
    }

}