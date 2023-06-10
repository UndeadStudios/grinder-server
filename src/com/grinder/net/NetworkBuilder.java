package com.grinder.net;

import com.grinder.game.GameConstants;
import com.grinder.net.channel.ClientChannelInitializer;
import com.grinder.net.channel.GrinderHandler;
import com.grinder.net.channel.WebChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.BindException;
import java.net.SocketAddress;

/**
 * The network builder for the Runescape #317 protocol. This class is used to
 * start and configure the {@link ServerBootstrap} that will control and manage
 * the entire network.
 *
 * @author lare96 <http://github.com/lare96>
 * @author Graham
 * @author Stan van der Bend
 */
public final class NetworkBuilder {

    private final static Logger LOGGER = LogManager.getLogger(NetworkBuilder.class);

    private final ServerBootstrap webBootstrap = new ServerBootstrap();
    private final EventLoopGroup webGroup = new NioEventLoopGroup();

    /**
     * The bootstrap that will oversee the management of the entire network.
     */
    private final ServerBootstrap gameBootstrap = new ServerBootstrap();

    private final EventLoopGroup gameAcceptGroup = new NioEventLoopGroup(2);
    private final EventLoopGroup gameIOGroup = new NioEventLoopGroup(1);

    /**
     * Initializes this network handler effectively preparing the server to
     * listen for connections and handle network events.
     *
     */
    public void initialize() {

        ResourceLeakDetector.setLevel(Level.SIMPLE);

        LOGGER.info("Initialized " + GameConstants.NAME + ".");

        gameBootstrap.group(gameAcceptGroup, gameIOGroup);
        webBootstrap.group(webGroup);

        final GrinderHandler handler = new GrinderHandler();

        final ChannelInitializer<SocketChannel> game = new ClientChannelInitializer(handler, GameConstants.CLIENT_UID, NetworkConstants.RSA_EXPONENT, NetworkConstants.RSA_MODULUS);
        gameBootstrap.channel(NioServerSocketChannel.class);
        gameBootstrap.childHandler(game);
        gameBootstrap.childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        final ChannelInitializer<SocketChannel> web = new WebChannelInitializer();
        webBootstrap.channel(NioServerSocketChannel.class);
        webBootstrap.childHandler(web);
    }

    /**
     * Binds the server to the specified address.
     *
     * @param game The service address to bind to.
     * @param http The HTTP address to bind to.
     * @throws BindException If the ServerBootstrap fails to bind to the SocketAddress.
     */
    public void bind(SocketAddress game, SocketAddress http) throws IOException {
        LOGGER.info("Binding service listener to address: " + game + "...");
        bind(gameBootstrap, game);

        try {
            LOGGER.info("Binding HTTP listener to address: " + http + "...");
            bind(webBootstrap, http);
        } catch (IOException cause) {
            LOGGER.warn("Unable to bind to HTTP.", cause);
        }

        LOGGER.info("Ready for connections.");
    }

    /**
     * Attempts to bind the specified ServerBootstrap to the specified SocketAddress.
     *
     * @param bootstrap The ServerBootstrap.
     * @param address The SocketAddress.
     * @throws IOException If the ServerBootstrap fails to bind to the SocketAddress.
     */
    private void bind(ServerBootstrap bootstrap, SocketAddress address) throws IOException {
        try {
            bootstrap.bind(address).sync().awaitUninterruptibly();
        } catch (Exception cause) {
            throw new IOException("Failed to bind to " + address, cause);
        }
    }
}
