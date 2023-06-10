package com.grinder.net.codec.database.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerRights
import com.grinder.net.codec.database.QueryBuilder
import com.grinder.net.codec.database.SQLDataSource
import com.grinder.net.codec.database.SQLManager
import com.grinder.net.codec.database.SQLTask
import java.sql.ResultSet
import java.util.*
import java.util.function.Consumer

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   06/04/2020
 * @version 1.0
 */
class UpdateOnlinePlayers(manager: SQLManager,
                          private val playerName: String,
                          private var rank: String,
                          private var online: Boolean,
                          private var staffRank: PlayerRights
) : SQLTask(manager, SQLDataSource.WEBSITE) {

    override fun canExecute() = true

    override fun sqlDisabledAction() {}

    override fun execute(): Optional<Consumer<Player>> {

        val query = QueryBuilder()
                .command(QueryBuilder.SELECT)
                .table("online_users")
                .limit(1)
                .where(String.format("username = '%s'", playerName))

        val statement = connection!!.createStatement(
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE)

        val results = statement.executeQuery(query.toString())

        if(results.next()){
            results.updateString("rank", rank)
            results.updateBoolean("is_online", online)
            results.updateInt("staff_rank", staffRank.ordinal)
            results.updateRow()
        } else {
            connection!!.createStatement().executeUpdate(QueryBuilder()
                    .table("online_users")
                    .command(QueryBuilder.INSERT)
                        .kv("username", playerName)
                        .kv("rank", rank)
                        .kv("is_online", online)
                        .kv("staff_rank", staffRank.ordinal
                    ).toString()
            )
        }
        return Optional.empty()
    }
}