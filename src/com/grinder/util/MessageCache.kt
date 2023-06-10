package com.grinder.util

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
class MessageCache {

    private val frames = HashMap<Int, String>()

    fun isCached(id: Int, message: String) : Boolean {

        if(frames[id] == message)
            return true

        frames[id] = message
        return false
    }

    fun removeRange(range: IntRange){
        range.forEach {
            frames.remove(it)
        }
    }

}