package com.grinder.game.content.miscellaneous.voting

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.grinder.game.content.item.Book
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.getDouble
import com.grinder.game.entity.getTimePassed
import com.grinder.game.entity.markTime
import com.grinder.game.entity.setDouble
import com.grinder.game.model.attribute.Attribute
import com.grinder.util.ItemID
import org.apache.logging.log4j.LogManager
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import kotlin.math.min

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   13/05/2020
 * @version 1.0
 */
object VotingStreaks {

    private val rewards = VoteStreakType.values().toList()
    private val activeRewards = HashMap<String, HashSet<VoteStreakType>>()

    private val highTierRange = 12L..16L
    private val lowTierRange = 16L..24L

    private const val MAX_STREAK_PENALTY = 25.0

    private val GSON = GsonBuilder().setPrettyPrinting().create()!!
    private val PATH = Paths.get("data", "vote-streaks")
    private val LOGGER = LogManager.getLogger(VotingStreaks::class.java.simpleName)!!
    private val TYPE = object: TypeToken<HashSet<VoteStreakType>>(){}.type!!
    private val BOOK = Book(ItemID.BOOK, "Vote Streak Information")

    init {

        // load player streak information
        load()

        // configure the book item to display the available rewards
        configureBook()
    }

    /**
     * Check if there are any new [VoteStreakType]s available for the argued player.
     *
     * @param player    the [Player] to lookup new rewards for
     */
    fun checkNewStreakRewards(player: Player){

        val newRewards = getNewStreakRewards(player)

        player.markTime(Attribute.LAST_VOTE)

        handleRewardActions(newRewards, player)

        val rewards = getStreakRewards(player)

        rewards.addAll(newRewards)

        save(player.username, rewards)
    }

    /**
     * Handle the claiming of all argued [VoteStreakType]s for the argued player.
     *
     * @param rewards   the [Collection] of [VoteStreakType] instances
     * @param player    the [Player] used to invoke the action mapped to each reward
     */
    private fun handleRewardActions(rewards: Collection<VoteStreakType>, player: Player) {
        for (reward in rewards) {
            reward.onReceived(player)
            player.message("You have received " + reward.getFormattedName() +" from your vote streak!")
        }
    }

    /**
     * Retrieves all rewards mapped to the [Player.username] in [activeRewards].
     *
     * @param player    the [Player] to get the rewards for
     */
    private fun getStreakRewards(player: Player) : HashSet<VoteStreakType> {
        return activeRewards!!.getOrPut(player.username) { HashSet() }
    }

    /**
     * Obtains all new [VoteStreakReward] based on the time passed since
     * the argued [Player]'s last two consecutive votes.
     */
    private fun getNewStreakRewards(player: Player): List<VoteStreakType> {

        val details = checkStreakDetails(player)

        var gainedPoints = details!!.pointReward
        val gainedPenalty = details!!.penalty

        val oldPenalty = player.getDouble(Attribute.VOTE_PENALTY_POINTS)
        var newPenalty = min(oldPenalty + gainedPenalty, MAX_STREAK_PENALTY)

//        if(newPenalty > 0){
//
//            // reduce penalty with points gained during penalty
//            if(gainedPoints > 0){
//                val penaltyReduction = newPenalty.coerceAtMost(gainedPoints)
//                newPenalty -= penaltyReduction
//                gainedPoints -= penaltyReduction
//            }
//
//            getStreakRewards(player)!!.forEach {
//                if(newPenalty > 0)
//                    it.onPenaltyReceived(player)
//                else
//                    it.onPenaltyLifted(player)
//            }
//
//            player.setDouble(Attribute.VOTE_PENALTY_POINTS, newPenalty)
//        }

        val oldPoints = player.getDouble(Attribute.VOTE_STREAK_POINTS)
        val newPoints = oldPoints + gainedPoints

        if(newPoints > oldPoints) {
            player.setDouble(Attribute.VOTE_STREAK_POINTS, newPoints)
            return rewards.filter {
                it.applies(oldPoints, newPoints)
            }
        }

        return emptyList()
    }

    /**
     * TODO: see if the vote streak should be applied based
     *       on the date in database or based on time of claiming
     *
     * @return the streak point reward for the player based of the time
     *          that passed since the player's last vote
     */
    private fun checkStreakDetails(player: Player): VoteStreakDetails {

        val hourSinceLastVote = player.getTimePassed(Attribute.LAST_VOTE, TimeUnit.HOURS)

        if(hourSinceLastVote in highTierRange)
            return VoteStreakDetails(1.5, 0.0)

        if(hourSinceLastVote in lowTierRange)
            return VoteStreakDetails(1.0, 0.0)

        if(hourSinceLastVote > 32){
            val daysSinceLastVote = TimeUnit.HOURS.toDays(hourSinceLastVote).toInt()
            val penalty = when {
                daysSinceLastVote > 14 -> 2.0 * daysSinceLastVote
                daysSinceLastVote > 7 -> 1.5 * daysSinceLastVote
                daysSinceLastVote > 2 -> daysSinceLastVote.toDouble()
                else -> 0.5 * daysSinceLastVote
            }
            return VoteStreakDetails(0.0, penalty)
        }

        return VoteStreakDetails(0.0, 0.0)
    }

    private fun configureBook() {
        var page = 0
        var text = ArrayList<String>()

        var i = 0
        val map = HashMap<Int, List<String>>()
        var r = 0
        map[page] = text

        for(reward in rewards){

            val lines = reward.getText()

            var nextPages = i + lines.size > 20

            val index = text.lastIndex

            if(!nextPages) {
                val skipLines = i + lines.size > 10

                if(skipLines) {
                    for (u in i..10) {
                        text.add("")
                        i++
                    }
                }
                nextPages = i + lines.size > 20
            }

            if(!nextPages && index > 0 && index != 10) {
                text.add(index+1, "")
                i++
            }

            if (nextPages) {
                text = ArrayList()
                map[++page] = text
                i = 0
            }

            for(line in lines){
                text.add(line)
                i++
            }
        }

        for(entry in map){
            BOOK.addPage(entry.key, *entry.value.toTypedArray())
        }
    }

    private fun load(){
        val directory = PATH.toFile()

        if(!directory.exists()){
            if(directory.mkdir()){
                LOGGER.debug("Created directory for streak saving at $directory")
            }
        }

        val files = directory.listFiles()

        if(files == null){
            LOGGER.debug("Did not find any streak files")
            return
        }

        var count = 0
        for(file in files){
            if(file != null){
                val name = file.nameWithoutExtension
                val reader = file.reader()
                activeRewards[name] = GSON.fromJson(reader, TYPE)
                count++
            }
        }

        LOGGER.info("Loaded $count vote streak entries")
    }

    private fun save(name: String, rewards: Set<VoteStreakType>){

        val file = PATH.resolve("$name.json").toFile()

        if(file.createNewFile())
            LOGGER.debug("Created new file at $file")

        val writer = file.writer()

        GSON.toJson(rewards, writer)

        writer.flush()
        writer.close()
    }

}