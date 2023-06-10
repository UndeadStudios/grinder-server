package com.grinder.game.service.captcha

import com.grinder.game.service.Service
import com.grinder.net.ByteBufUtils
import io.netty.channel.Channel
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   07/12/2019
 * @version 1.0
 */
class CaptchaService : Service {

    val requests = LinkedBlockingQueue<CaptchaServiceRequest>()

    override fun init() {}

    override fun postLoad() {}

    override fun bindNet() {}

    override fun terminate() {}

    fun addCaptchaResponse(captchaResponse: CaptchaServiceRequest){
        requests.offer(captchaResponse)
    }

    fun hasCompletedCaptcha(channel: Channel): Boolean{

        val hostAddress =  ByteBufUtils.getHost(channel)
        val iterator = requests.iterator()

        while (iterator.hasNext()){

            val request = iterator.next()

            if(request == null){
                iterator.remove()
                continue
            }

            val minutesSinceCreation = TimeUnit.MILLISECONDS.toMinutes(request.timeSinceCreation())

            if(minutesSinceCreation >= 2) {
                iterator.remove()
                continue
            }

            val captcha = request.captcha

            if(captcha.remoteIp == hostAddress)
                return captcha.success
        }

        return false
    }
}