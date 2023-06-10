package com.grinder.net.codec.database.impl

import com.grinder.game.entity.agent.player.Player
import com.grinder.net.codec.database.QueryBuilder
import com.grinder.net.codec.database.SQLDataSource
import com.grinder.net.codec.database.SQLManager
import com.grinder.net.codec.database.SQLTask
import java.sql.Timestamp
import java.util.*
import java.util.function.Consumer


class DatabaseStakeLogs(manager: SQLManager,
                        private val winnerName: String,
                        private val loserName: String,
                        private val itemName: String,
                        private val itemAmount: Int,
                        private val details: String
) : SQLTask(manager, SQLDataSource.STAFF_PANEL) {

    override fun canExecute() = true

    override fun sqlDisabledAction() {}

    override fun execute(): Optional<Consumer<Player>> {

        connection!!.createStatement().executeUpdate(
            QueryBuilder()
                .command(QueryBuilder.INSERT)
                .table("stake_logs") // Table Name
                .kv("winnerName", winnerName)
                .kv("loserName", loserName)
                .kv("itemName", itemName)
                .kv("itemAmount", itemAmount)
                .kv("details", details)
                .kv("Date", Timestamp(System.currentTimeMillis())).toString()
        )

        return Optional.empty()
    }
}