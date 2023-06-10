package com.grinder.net.codec.database

import com.grinder.game.GameConstants
import com.grinder.game.entity.agent.player.Player
import org.apache.logging.log4j.LogManager
import java.sql.Connection
import java.sql.SQLException
import java.util.*
import java.util.function.Consumer

/**
 * @author Harrison, Alias: Hc747, Contact: harrisoncole05@gmail.com
 * @author Stan van der Bend
 * @version 2.0
 * @since 4/9/17
 */
abstract class SQLTask protected constructor(manager: SQLManager, source: SQLDataSource) {

    internal val logger = LogManager.getLogger(this::class.java)!!
    private val manager: SQLManager = Objects.requireNonNull(manager)
    private val source: SQLDataSource = Objects.requireNonNull(source)
    private val connections = LinkedList<Connection>()

    protected var connection: Connection? = null

    abstract fun canExecute(): Boolean
    abstract fun sqlDisabledAction()

    @Throws(SQLException::class)
    abstract fun execute(): Optional<Consumer<Player>>

    fun schedule(player: Player) {

        if (GameConstants.MYSQL_ENABLED && source.isEnabled)
            manager.execute(this, player)
        else
            sqlDisabledAction()
    }


    @Throws(SQLException::class)
    internal fun connect() {

        if (connection == null || connection!!.isClosed) {
            val connection = manager.getConnection(source)
            connections.add(connection)
            this.connection = connection
            logger.debug("Established connection with $source")
        }
    }

    internal fun release() {
        logger.debug("Releasing ${connections.size} connections")
        while (!connections.isEmpty()) {
            closeConnection(connections.poll())
        }
    }

    private fun closeConnection(connection: Connection?) {
        Optional.ofNullable(connection).ifPresent { conn ->
            try {
                if (!conn.isClosed)
                    conn.close()
            } catch (e: Exception) {
                logger.error("Could not close connection $conn", e)
            }
        }
    }

}
