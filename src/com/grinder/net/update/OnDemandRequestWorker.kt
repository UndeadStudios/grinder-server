package com.grinder.net.update

import com.google.common.primitives.Ints
import com.grinder.net.channel.ChannelRequest
import com.grinder.net.codec.filestore.OnDemandRequest
import com.grinder.net.codec.filestore.OnDemandResponse
import com.grinder.net.session.FilestoreSession
import io.netty.channel.Channel
import net.runelite.cache.fs.Container
import net.runelite.cache.fs.Store
import net.runelite.cache.fs.jagex.CompressionType
import net.runelite.cache.fs.jagex.DiskStorage
import java.io.IOException

/**
 * A worker which services 'on-demand' requests.
 *
 * @author  Graham
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 */
class OnDemandRequestWorker
/**
 * Creates the 'on-demand' request worker.
 *
 * @param dispatcher The dispatcher.
 * @param fs         The file system.
 */
(dispatcher: UpdateDispatcher, fs: Store) : RequestWorker<OnDemandRequest, Store>(dispatcher, fs) {

    @Throws(InterruptedException::class)
    override fun nextRequest(dispatcher: UpdateDispatcher): ChannelRequest<OnDemandRequest> {
        return dispatcher.nextOnDemandRequest()
    }

    @Throws(IOException::class)
    override fun service(cache: Store, channel: Channel, request: OnDemandRequest) {

        if(request.index == 255)
            encodeIndexData(cache, channel, request)
        else
            encodeFileData(cache, channel, request)
    }


    private fun encodeIndexData(store: Store, channel: Channel, req: OnDemandRequest) {
        val data: ByteArray

        if (req.archive == 255) {
            val buf = channel.alloc().heapBuffer(store.indexes.size * 8)

            store.indexes.forEach { index ->
                buf.writeInt(index.crc)
                buf.writeInt(index.revision)
            }

            val container = Container(CompressionType.NONE, -1)
            container.compress(buf.array().copyOf(buf.readableBytes()), null)
            data = container.data
            buf.release()
        } else {
            val storage = store.storage as DiskStorage
            data = storage.readIndex(req.archive)
        }

        val response = OnDemandResponse(index = req.index, archive = req.archive, data = data)
        channel.writeAndFlush(response)
    }

    private fun encodeFileData(store: Store, channel: Channel, req: OnDemandRequest) {
        val index = store.findIndex(req.index)!!
        val archive = index.getArchive(req.archive)!!
        var data = store.storage.loadArchive(archive)

        if (data != null) {
            val compression = data[0]
            val length = Ints.fromBytes(data[1], data[2], data[3], data[4])
            val expectedLength = length + (if (compression.toInt() != CompressionType.NONE) 9 else 5)
            if (expectedLength != length && data.size - expectedLength == 2) {
                data = data.copyOf(data.size - 2)
            }

            val response = OnDemandResponse(index = req.index, archive = req.archive, data = data)
            channel.writeAndFlush(response)
        } else {
            FilestoreSession.logger.warn("Data is missing from archive. index={${req.index}}, archive={${req.archive}}")
        }
    }
}