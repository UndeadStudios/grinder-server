package com.grinder.net.codec.database

import com.grinder.Config
import com.grinder.game.GameConstants
import com.grinder.game.World
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import java.sql.SQLException
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * @author  Harrison, Alias: Hc747, Contact: harrisoncole05@gmail.com
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   04/09/2017
 * @version 1.0
 */
open class SQLManager private constructor() {

    private val contexts = EnumMap<SQLDataSource, HikariDataSource>(SQLDataSource::class.java)
    private val executor = Executors.newSingleThreadExecutor()

    fun init() {
        for (source in SQLDataSource.values())
            LOGGER.info(String.format("%s to %s", if (getPool(source) == null) "Could not connect" else "Connected", source))
    }

    private fun getPool(source: SQLDataSource): HikariDataSource? {
        return (contexts as MutableMap<SQLDataSource, HikariDataSource>)
                .computeIfAbsent(source) { create(it)!! }
    }

    @Throws(SQLException::class)
    fun getConnection(source: SQLDataSource): Connection {
        return getPool(source)!!.connection
    }

    fun execute(task: SQLTask, player: Player) {

        if(!GameConstants.MYSQL_ENABLED)
            return

        if (task.canExecute()) {

            if(executor.isShutdown){
                player.message("Could not parse request, please try again later!")
                return
            }

            executor.execute {

                try {
                    task.connect()

                    task.execute().ifPresent {
                        World.submitGameThreadJob {
                            it.accept(player)
                        }
                    }

                } catch (e: Exception) {

                    e.printStackTrace()
                } finally {

                    task.release()
                }
            }
        } else
            LOGGER.error("Could not execute sql task $task")
    }

    fun execute(task: Runnable) {

        if(!GameConstants.MYSQL_ENABLED)
            return


            if(executor.isShutdown){
                return
            }

            executor.execute {
                task.run()
            }
    }

    @Throws(InterruptedException::class)
    fun shutdown(timeout: Long, unit: TimeUnit) {
        try {
            executor.shutdown()
            executor.awaitTermination(timeout, unit)
        } finally {
            contexts.values.forEach{ it.close() }
        }
    }

    companion object {

        val LOGGER = org.apache.logging.log4j.LogManager.getLogger(SQLManager::class.java.simpleName)
        val INSTANCE = SQLManager()

        private fun create(source: SQLDataSource): HikariDataSource? {
            if (source.isEnabled) {
                try {
                    val config = HikariConfig(source.configFileLocation)
                    return HikariDataSource(config)
                } catch (e: Exception) {
                    LOGGER.error("Failed to create data source '$source'", e)
                }
            }
            return null
        }
    }

}
