package com.grinder.net.update

import com.grinder.net.channel.ChannelRequest
import io.netty.channel.Channel

/**
 * A [ChannelRequest] with a [Comparable] request type.
 *
 * @author Major
 *
 * @param T The type of request.
 * @param channel The [Channel] making the request.
 * @param request The request.
 **/
class ComparableChannelRequest<T : Comparable<T>?>(channel: Channel?, request: T) : ChannelRequest<T>(channel, request), Comparable<ComparableChannelRequest<T>> {
    override fun compareTo(other: ComparableChannelRequest<T>): Int {
        return request!!.compareTo(other.request)
    }
}