package com.grinder.util.tools

import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.PlayerLoading
import com.grinder.game.model.Skill
import com.grinder.util.Misc
import java.nio.file.Paths

val preRollBackPath = Paths.get("")
val postRollBackPath = Paths.get("")
val currentPath = Paths.get("")

fun main() {

    val preRollBackPlayers = preRollBackPath.toFile().listFiles().map {
        val player = Player()
        player.username = Misc.formatPlayerName(it.nameWithoutExtension)
        PlayerLoading.getResult(player, true, true)
        Pair(player.username!!, player)
    }.toMap()
    println("Loaded pre roll back player accounts")

    val postRollBackPlayers = postRollBackPath.toFile().listFiles().map {
        val player = Player()
        player.username = Misc.formatPlayerName(it.nameWithoutExtension)
        PlayerLoading.getResult(player, true, true)
        Pair(player.username!!, player)
    }.toMap()
    println("Loaded post roll back player accounts")

    val currentPlayers = currentPath.toFile().listFiles().map {
        val player = Player()
        player.username = Misc.formatPlayerName(it.nameWithoutExtension)
        PlayerLoading.getResult(player, true, true)
        Pair(player.username!!, player)
    }.toMap()
    println("Loaded current player accounts")

    var totalRestored = 0

    for(username in postRollBackPlayers.keys){
        val pre = preRollBackPlayers[username]?:continue
        val post = postRollBackPlayers[username]?:continue
        val current = currentPlayers[username]?:continue
        for(skill in Skill.values()){
            val preExperience = pre.skillManager.getExperience(skill)
            val postExperience = post.skillManager.getExperience(skill)
            val difference = postExperience-preExperience
            if(difference > 0){
                current.skillManager.skills.experiences[skill.ordinal] += difference
                totalRestored += difference
            }
        }
    }

    println("Restored a total of $totalRestored experience.")
}