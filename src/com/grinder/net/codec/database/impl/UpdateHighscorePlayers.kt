package com.grinder.net.codec.database.impl

import com.grinder.game.content.GameMode
import com.grinder.game.content.skill.SkillManager
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.Skill
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
class UpdateHighscorePlayers(manager: SQLManager,
                             private val playerName: String,
                             private val gameMode: GameMode,
                             private val skills: SkillManager
) : SQLTask(manager, SQLDataSource.WEBSITE) {

    private val overallLevel = skills.countTotalLevel()
    private val overallExp = skills.countTotalExperience()
    private val map = Skill.values().map {
        Pair(it.name.toLowerCase().replace("_", " "),
                Pair(skills.getExperience(it), skills.getMaxLevel(it)))
    }.toMap()

    override fun canExecute() = true

    override fun sqlDisabledAction() {}

    override fun execute(): Optional<Consumer<Player>> {

        val results = fetchHighscoreEntry()

        if(results != null && results.next()){

            results.updateString("gameMode", getGameModeString())

            results.updateLong("overall_exp", overallExp)
            results.updateInt("overall_level", overallLevel)

            map.forEach { (skill, pair) ->
                results.updateInt("${skill}_exp", pair.first)
                results.updateInt("${skill}_level", pair.second)
            }

            results.updateRow()

        } else
            insertHighscoreEntry()


        return Optional.empty()
    }

    private fun insertHighscoreEntry() {
        val insertQuery = QueryBuilder()
                .command(QueryBuilder.INSERT)
                .table("highscores")
                .kv("username", playerName)
                .kv("gameMode", getGameModeString())
                .kv("overall_exp", overallExp)
                .kv("overall_level", overallLevel)

        map.forEach { (skill, pair) ->
            insertQuery.kv("${skill}_exp", pair.first)
            insertQuery.kv("${skill}_level", pair.second)
        }

        connection?.createStatement()?.executeUpdate(insertQuery.toString())
    }

    private fun fetchHighscoreEntry(): ResultSet? {
        val query = QueryBuilder()
                .command(QueryBuilder.SELECT)
                .k("username")
                .table("highscores")
                .limit(1)
                .where(String.format("username = '%s'", playerName))

        val statement = connection?.createStatement(
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE)

        return statement?.executeQuery(query.toString())
    }

    private fun getGameModeString(): String {
        return when (gameMode) {
            GameMode.ONE_LIFE -> "One Life"
            GameMode.REALISM -> "Realism"
            GameMode.CLASSIC -> "Classic"
            GameMode.NORMAL -> "Normal"
            GameMode.PURE -> "Pure"
            GameMode.MASTER -> "Master"
            GameMode.SPAWN -> "Spawn"
            GameMode.IRONMAN -> "Iron Man"
            GameMode.HARDCORE_IRONMAN -> "Hardcore Iron Man"
            GameMode.ULTIMATE_IRONMAN -> "Ultimate Iron Man"
        }
    }
}