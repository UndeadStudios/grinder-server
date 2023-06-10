package com.grinder.game.service.captcha

import com.grinder.net.codec.captcha.CaptchaResponse

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   07/12/2019
 * @version 1.0
 */
data class CaptchaServiceRequest(val captcha: CaptchaResponse) {

    var creationTime = System.currentTimeMillis()

    fun timeSinceCreation() = System.currentTimeMillis() - creationTime
}