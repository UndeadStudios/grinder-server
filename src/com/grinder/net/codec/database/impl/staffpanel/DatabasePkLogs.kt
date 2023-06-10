package com.grinder.net.codec.database.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.net.codec.database.QueryBuilder
import com.grinder.net.codec.database.SQLDataSource
import com.grinder.net.codec.database.SQLManager
import com.grinder.net.codec.database.SQLTask
import java.sql.Timestamp
import java.util.*
import java.util.function.Consumer


class DatabasePkLogs(manager: SQLManager,
                        private val playerName: String,
                        private val victimName: String,
                        private val playerhostAddress: String,
                        private val victimHostAddress: String,
                        private val playerMacAddress: String,
                        private val victimMacAddress: String
) : SQLTask(manager, SQLDataSource.STAFF_PANEL) {

    override fun canExecute() = true

    override fun sqlDisabledAction() {}

    override fun execute(): Optional<Consumer<Player>> {

        connection!!.createStatement().executeUpdate(
            QueryBuilder()
                .command(QueryBuilder.INSERT)
                .table("pk_logs") // Table Name
                .kv("playerName", playerName)
                .kv("victimName", victimName)
                .kv("playerhostAddress", playerhostAddress)
                .kv("victimHostAddress", victimHostAddress)
                .kv("playerMacAddress", playerMacAddress)
                .kv("victimMacAddress", victimMacAddress)
                .kv("Date", Timestamp(System.currentTimeMillis())).toString()
        )

        return Optional.empty()
    }
}