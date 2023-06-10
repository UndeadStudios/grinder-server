package com.grinder.net.session;

import com.grinder.game.World;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.LogoutPolicy;
import com.grinder.game.entity.agent.player.bot.BotPlayer;
import com.grinder.game.message.Message;
import com.grinder.game.message.impl.LogoutMessage;
import com.grinder.net.NetworkConstants;
import com.grinder.net.packet.*;
import com.grinder.util.Logging;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * The session handler dedicated to a player that will handle input and output
 * operations.
 *
 * @author Lare96
 * @author Swiffy
 * @author Professor Oak
 * @author Stan van der Bend
 */
public class PlayerSession extends Session {

    private Player player;

    private final Channel channel;
    private final PacketConfiguration packetConfiguration;
    private final BlockingQueue<Packet> priorityPackets = new ArrayBlockingQueue<>(NetworkConstants.PACKET_PROCESS_LIMIT);
    private final BlockingQueue<Packet> packets = new ArrayBlockingQueue<>(NetworkConstants.PACKET_PROCESS_LIMIT);
    private final Logger logger = LogManager.getLogger(PlayerSession.class.getSimpleName());

    /**
     * Creates a new {@link PlayerSession}.
     *
     * @param channel The SocketChannel.
     */
    public PlayerSession(Channel channel) {
        super(channel);
        this.channel = channel;
        this.player = new Player(this);
        this.packetConfiguration = World.getPacketMetaData();
    }

    @Override
    public void receiveMessage(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) {
        if(msg instanceof Packet){
            queuePacket((Packet)msg);
        } else
            logger.info(player+" received unhandled message {"+msg+"}!");
    }

    /**
     * Clear all packets and release their buffers from memory
     */
    public void releaseBuffers() {
        for (int i = 0; i < packets.size(); i++) {
            Packet packet = packets.poll();

            if(packet != null) {
                packet.getPayload().release();
            }
        }

        for (int i = 0; i < priorityPackets.size(); i++) {
            Packet packet = priorityPackets.poll();

            if(packet != null) {
                packet.getPayload().release();
            }
        }
    }

    /**
     * Queues a recently decoded packet received from the channel.
     *
     * @param packet The packet that should be queued.
     */
    private void queuePacket(Packet packet) {
        try {
            final int opcode = packet.getOpcode();

            if (packetConfiguration.isUnusedPacket(opcode))
                return;

            final BlockingQueue<Packet> queue;

            if (packetConfiguration.isPriority(opcode))
                queue = priorityPackets;
            else
                queue = packets;

            if (!queue.offer(packet)) {
                logger.warn("Failed to add packet due to capacity reached for " + player + "");
                player.logout(LogoutPolicy.IMMEDIATE);
                Logging.log("packetissues", "[" + player.getUsername() + "] has " + packets.size() + " packets queued -> could not queue packet " + packet.getOpcode() + " of size " + packet.getLength());
            }
        } catch (Exception e) {
            logger.error("[" + player.getUsername() + "] has " + packets.size() + " packets queued -> could not queue packet " + packet.getOpcode() + " of size " + packet.getLength(), e);
        }
    }


    /**
     * Handle all queued packets.
     */
    public void handlePendingPackets() {

        if(player instanceof BotPlayer)
            return;

        if(channel == null || !channel.isActive())
            return;

        for (int i = 0; i < NetworkConstants.PACKET_PROCESS_LIMIT; i++) {

            Packet packet = priorityPackets.poll();

            if (packet == null)
                break;

            processPacket(packet);
        }

        for (int i = 0; i < NetworkConstants.PACKET_PROCESS_LIMIT; i++) {

            Packet packet = packets.poll();

            if (packet == null)
                break;

            processPacket(packet);
        }
    }

    /**
     * Handles an incoming packet.
     *
     * @param packet The packet to handle.
     */
    private void processPacket(Packet packet) {
        try {

            final int opcode = packet.getOpcode();
            final PacketListener listener = packetConfiguration.getListeners()[opcode];
            if(listener != null) {

                if (!packetConfiguration.isIdlePacket(opcode))
                    player.stopLogout();

                final long start = System.nanoTime();

                final PacketReader reader = new PacketReader(packet);

                listener.handleMessage(player, reader, opcode);

                final long dur = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

                if(dur > 75)
                    logger.warn("Reading of "+packet+" for "+player+" took "+dur+" ms!");
            }
        } catch (Exception e) {
            logger.error("Failed to parse "+packet+" for "+player+" (position = "+player.getPosition().compactString()+", area = "+player.getArea()+")", e);
        } finally {
            if(!packet.getPayload().release()){
                if(packet.getLength() != 0)
                    logger.error(packet+" failed to release!");
            }
        }
    }

    /**
     * Encodes and dispatches the specified message.
     *
     * @param message The message.
     */
    public void dispatchMessage(Message message) {

        if(player instanceof BotPlayer)
            return;

        final Channel channel = getChannel();

        if (channel.isActive() && channel.isOpen()) {

            final ChannelFuture future = channel.writeAndFlush(message);

            if (message.getClass() == LogoutMessage.class) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    /**
     * Queues the {@code msg} for this session to be encoded and sent to the client.
     *
     * @param builder the packet to queue.
     */
    public void write(PacketBuilder builder) {
        write(builder.toPacket());
    }

    /**
     * Queues the {@code msg} for this session to be encoded and sent to the client.
     *
     * @param packet the packet to queue.
     */
    public void write(Packet packet) {

        if (player instanceof BotPlayer)
            return;

        try {

            if(channel.isActive() && channel.isOpen()) {
                final long start = System.nanoTime();
                channel.write(packet);
                final long dur = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

                if(dur > 5){
                    logger.warn("Writing "+packet+" for "+player+" took "+dur+" ms!");
                }
            }

        } catch (Exception ex) {
            logger.error("Failed to write "+packet+" for "+player, ex);
        }
    }

    /**
     * Flushes this channel.
     */
    public void flush() {

        if(player instanceof BotPlayer)
            return;

        if(channel.isActive()) {
            channel.flush();
        } else {
            logger.debug("Did not flush to inactive channel "+channel+" for "+player);
        }
    }

    @Override
    public void onChannelInactive() {
        player.logout(LogoutPolicy.SAFE);
    }

    public boolean isDead(){
        return !channel.isActive() || !channel.isOpen() || !channel.isRegistered();
    }

    public int packetsInQueue(){
        return packets.size();
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public final Channel getChannel() {
        return channel;
    }

    public PacketConfiguration getPacketMetaData() {
        return packetConfiguration;
    }


}
