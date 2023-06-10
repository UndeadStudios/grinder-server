package com.grinder.net.codec.database.impl

import com.grinder.game.content.GameMode
import com.grinder.game.content.skill.SkillManager
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Skill
import com.grinder.net.codec.database.QueryBuilder
import com.grinder.net.codec.database.SQLDataSource
import com.grinder.net.codec.database.SQLManager
import com.grinder.net.codec.database.SQLTask
import java.sql.Date
import java.sql.ResultSet
import java.sql.Time
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.function.Consumer


class DatabaseEmptyLogs(manager: SQLManager,
                             private val playerName: String,
                             private val itemName: String,
                             private val itemAmount: Int
) : SQLTask(manager, SQLDataSource.STAFF_PANEL) {

    override fun canExecute() = true

    override fun sqlDisabledAction() {}

    override fun execute(): Optional<Consumer<Player>> {

        connection!!.createStatement().executeUpdate(
            QueryBuilder()
                .command(QueryBuilder.INSERT)
                .table("empty_logs") // Table Name
                .kv("playerName", playerName)
                .kv("itemName", itemName)
                .kv("itemAmount", itemAmount)
                .kv("Date", Timestamp(System.currentTimeMillis())).toString()
        )

        return Optional.empty()
    }
}