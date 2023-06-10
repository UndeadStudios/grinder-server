package com.grinder.util

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.nio.file.Paths
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   30/11/2019
 * @version 1.0
 */
class SmartLogger(directory: String) {

    val path = LOGS_PATH.resolve(directory)
    var file : File
    var lastFlush = 0L

    val strings = ArrayList<String>()

    lateinit var bw : BufferedWriter

    init {
        path.toFile().mkdir()
        file = path.resolve(DATE_FORMAT.format(Calendar.getInstance().time) + SUFFIX).toFile()
        try {
            val fw = FileWriter(file, true)
            bw = BufferedWriter(fw)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun write(string: String){

        strings.add("[${TIME_FORMAT.format(Calendar.getInstance().time)}] $string")

        val time = System.currentTimeMillis()

        if(time-lastFlush > TimeUnit.MINUTES.toMillis(2)){
            flush()
        }
    }

    fun close(){
        if(this::bw.isInitialized){
            bw.flush()
            bw.close()
        }
    }

    fun flush(){

        val name = DATE_FORMAT.format(Calendar.getInstance().time) + SUFFIX

        if(file.name != name || !file.exists()){
            file = path.resolve(name).toFile()
            bw.flush()
            bw.close()
            try {
                val fw = FileWriter(file, true)
                bw = BufferedWriter(fw)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if(this::bw.isInitialized){
            for(string in strings){
                bw.write(string)
                bw.newLine()
            }
            bw.flush()
        }
        strings.clear()
        lastFlush = System.currentTimeMillis()
    }

    companion object {
        private val LOGS_PATH = Paths.get("data/logs")
        val DATE_FORMAT: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        private val TIME_FORMAT: DateFormat = SimpleDateFormat("HH:mm:ss")
        private var SUFFIX = ".log"
    }
}