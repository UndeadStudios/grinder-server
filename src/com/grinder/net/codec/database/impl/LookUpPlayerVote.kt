package com.grinder.net.codec.database.impl


import com.grinder.game.content.miscellaneous.voting.Voting
import com.grinder.game.content.miscellaneous.voting.VotingStreaks
import com.grinder.game.entity.agent.player.Color
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.agent.player.statement
import com.grinder.game.model.commands.impl.ClaimVoteCommand
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueExpression
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.interfaces.dialogue.firstOption
import com.grinder.net.codec.database.QueryBuilder
import com.grinder.net.codec.database.SQLDataSource
import com.grinder.net.codec.database.SQLManager
import com.grinder.net.codec.database.SQLTask
import com.grinder.util.Logging
import com.grinder.util.NpcID
import java.lang.Exception
import java.sql.Date
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Timestamp
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Consumer
import kotlin.collections.HashSet
import kotlin.math.log

/**
 * An [SQLTask] to verify a [Player] vote.
 *
 * @see ClaimVoteCommand for trigger
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   06/04/2020
 * @version 1.0
 */
class LookUpPlayerVote(
        manager: SQLManager,
        private val promptTeleport: Boolean,
        private val player: String
) : SQLTask(manager, SQLDataSource.WEBSITE) {

    var totalVotePoints = 0

    private val ids = HashSet<Long>()

    override fun sqlDisabledAction() {}

    override fun canExecute() = ENABLED.get()

    @Throws(SQLException::class)
    override fun execute() : Optional<Consumer<Player>> {

        val results = fetchVotes()
        var count = 0

        if(results != null) {
            while (results.next()) {
                parseVote(results)
                count++
            }
        } else {
            logger.error("Failed to fetch votes for $this")
        }

        if(ids.isNotEmpty())
            updateVoteColumns()

        return Optional.of(Consumer { player ->
            if(totalVotePoints > 0) {
                try {
                    Logging.log("redeemvotes", "${player.username} has redeemed $totalVotePoints voting tickets from IP: ${player.hostAddress} MAC: ${player.macAddress}")

                    Voting.giveRewards(player, count, totalVotePoints)
                    VotingStreaks.checkNewStreakRewards(player)

                    sendDialogue(count, player)
                } catch (e: Exception) {
                    logger.error("Failed to redeem $totalVotePoints voting tickets of $player", e)
                    player.message("Something went wrong while parsing your vote, please contact a staff member and screenshot this message.", Color.RED)
                }
            } else {
                DialogueBuilder(DialogueType.NPC_STATEMENT)
                        .setNpcChatHead(NpcID.QUARTERMASTER)
                        .setExpression(DialogueExpression.SAD_HEAD_BOW)
                        .setText("You don't have any votes to redeem on your account.")
                        .start(player)
            }
        })
    }

    private fun sendDialogue(count: Int, player: Player) {
        DialogueBuilder(DialogueType.ITEM_STATEMENT)
                .setText(
                        "You have received $totalVotePoints @cya@Voting ${if (totalVotePoints > 1) "Tickets" else "Tickets"}</col>!",
                        "and a voting Mystery Box!")
                .setItem(15031, 200, "You have voted on $count ${if (count > 1) "websites" else "website"}")
                .add(DialogueType.NPC_STATEMENT)
                .setNpcChatHead(2871)
                .setExpression(DialogueExpression.HAPPY)
                .setText("Hey, thanks for voting!", "You can use voting tickets within my store,",
                        if(promptTeleport) "would you like to visit my shop?"
                        else "Would you like to view my shop?")
                .add(DialogueType.OPTION)
                .firstOption("Yes.") {
                    if(promptTeleport)
                        Voting.teleportToVotePointStore(it)
                    else
                        Voting.openVotePointStore(it)
                }
                .addCancel("No.")
                .start(player)
    }

    private fun parseVote(results: ResultSet) {
        val voteId = results.getLong("id")
        ids.add(voteId)
        val siteId = results.getInt("site_id")
        val resultsQuery = QueryBuilder()
                .command(QueryBuilder.SELECT)
                .k("id")
                .table(VOTE_SITE_TABLE)
                .limit(1)
                .where(String.format("id = '%d'", siteId))
        val statement2 = connection!!.createStatement()
        val siteResults = statement2.executeQuery(resultsQuery.toString())
        if (siteResults.next())
            totalVotePoints += siteResults.getInt("points_given")
    }

    private fun updateVoteColumns() {
        val updateQuery = QueryBuilder()
                .command(QueryBuilder.UPDATE)
                .kv("date_claimed", Timestamp(System.currentTimeMillis()))
                .table(TABLE)
                .where(String.format("id IN (" + ids.joinToString(",") + ")"))

        connection
                ?.createStatement()
                ?.executeUpdate(updateQuery.build())
    }

    private fun fetchVotes(): ResultSet? {
        val query = QueryBuilder()
                .command(QueryBuilder.SELECT)
                .k("id")
                .k("username")
                .k("completed")
                .k("date_claimed")
                .table(TABLE)
                .limit(30)
                .where(String.format("username = '%s' AND completed = 1 AND date_claimed IS NULL", player))

        return connection
                ?.createStatement()
                ?.executeQuery(query.toString())
    }

    override fun toString(): String {
        return "LookUpPlayerVote(player='$player')"
    }

    companion object {

        val ENABLED = AtomicBoolean(true)

        private const val TABLE = "votes"
        private const val VOTE_SITE_TABLE = "vote_sites"
    }
}
