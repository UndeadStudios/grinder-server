package com.grinder.net.codec.database.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.net.codec.database.QueryBuilder
import com.grinder.net.codec.database.SQLDataSource
import com.grinder.net.codec.database.SQLManager
import com.grinder.net.codec.database.SQLTask
import java.sql.ResultSet
import java.util.*
import java.util.function.Consumer

class DatabaseLoginLogs(
    manager: SQLManager,
    private val playerName: String,
    private var hostAddress: String,
    private var macAddress: String
) : SQLTask(manager, SQLDataSource.STAFF_PANEL) {

    override fun canExecute() = true

    override fun sqlDisabledAction() {}

    override fun execute(): Optional<Consumer<Player>> {

        val query = QueryBuilder()
            .command(QueryBuilder.SELECT)
            .k("playerName")
            .k("hostAddress")
            .k("macAddress")
            .table("login_logs")
            .limit(25)
            .where(String.format("playerName = '%s' AND hostAddress = '%s' AND macAddress = '%s'", playerName, hostAddress, macAddress))

        val statement = connection!!.createStatement(
            ResultSet.TYPE_SCROLL_SENSITIVE,
            ResultSet.CONCUR_UPDATABLE)

        val results = statement.executeQuery(query.toString())


        if(results.next()){
            results.updateInt("loginCount", results.getInt("loginCount").plus(1))
            results.updateRow()
        } else {
            connection!!.createStatement().executeUpdate(QueryBuilder()
                .table("login_logs")
                .command(QueryBuilder.INSERT)
                .kv("playerName", playerName)
                .kv("hostAddress", hostAddress)
                .kv("macAddress", macAddress)
                .kv("loginCount", 0
                ).toString()
            )
        }
        return Optional.empty()
    }
}