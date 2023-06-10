package com.grinder.net.update

import io.netty.buffer.ByteBuf

/**
 * Represents a single 'on-demand' response.
 *
 * @author Graham
 *
 * @param fs        The file descriptor.
 * @param chunkData The chunk data.
 */
class OnDemandResponse(
        val fs: Int,
        val folder: Int,
        val priority: OnDemandRequest.Priority,
        val chunkData: ByteBuf)
