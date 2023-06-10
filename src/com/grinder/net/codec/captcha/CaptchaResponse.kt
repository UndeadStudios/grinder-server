package com.grinder.net.codec.captcha

import java.util.*

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   06/12/2019
 * @version 1.0
 */
data class CaptchaResponse(
        val success: Boolean,
        val challengeTimeStamp: Date,
        val remoteIp: String,
        val errorCodes: Array<String>) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CaptchaResponse) return false

        if (success != other.success) return false
        if (challengeTimeStamp != other.challengeTimeStamp) return false
        if (remoteIp != other.remoteIp) return false
        if (!errorCodes.contentEquals(other.errorCodes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = success.hashCode()
        result = 31 * result + challengeTimeStamp.hashCode()
        result = 31 * result + remoteIp.hashCode()
        result = 31 * result + errorCodes.contentHashCode()
        return result
    }
}