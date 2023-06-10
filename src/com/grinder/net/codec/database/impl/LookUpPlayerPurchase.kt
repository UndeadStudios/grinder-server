package com.grinder.net.codec.database.impl

import com.grinder.game.GameConstants
import com.grinder.game.content.miscellaneous.Broadcast
import com.grinder.game.content.miscellaneous.donating.PurchaseRewards
import com.grinder.game.content.miscellaneous.donating.Store
import com.grinder.game.entity.agent.player.*
import com.grinder.game.entity.getInt
import com.grinder.game.entity.setInt
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.commands.impl.ClaimOrderCommand
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder
import com.grinder.game.model.interfaces.dialogue.DialogueExpression
import com.grinder.game.model.interfaces.dialogue.DialogueType
import com.grinder.game.model.interfaces.dialogue.firstOption
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.ItemContainerUtil
import com.grinder.net.codec.database.QueryBuilder
import com.grinder.net.codec.database.SQLDataSource
import com.grinder.net.codec.database.SQLManager
import com.grinder.net.codec.database.SQLTask
import com.grinder.util.DiscordBot
import com.grinder.util.ItemID
import com.grinder.util.Logging
import com.grinder.util.Misc
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Timestamp
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Consumer
import kotlin.collections.HashSet
import kotlin.math.ceil
import kotlin.math.roundToInt

/**
 * An [SQLTask] to verify a [Player] purchase.
 *
 * @see ClaimOrderCommand for trigger
 *
 * @author Harrison, Alias: Hc747, Contact: harrisoncole05@gmail.com
 * @author Stan van der Bend
 * @version 2.0
 * @since 6/9/17
 */
class LookUpPlayerPurchase(manager: SQLManager,
                           private val promptTeleport: Boolean,
                           private val playerName: String,
                           private val playerIp: String,
                           private val playerMac: String
) : SQLTask(manager, SQLDataSource.WEBSITE) {

    private var dialogueBuilder = DialogueBuilder(DialogueType.NPC_STATEMENT)
            .setNpcChatHead(5792)
            .setExpression(DialogueExpression.HAPPY)
    private var totalProductPrice = 0.0
    private var totalPaidPrice = 0.0
    private var rewards = PurchaseRewards()
    private val ids = HashSet<Long>()

    override fun sqlDisabledAction() {}

    override fun canExecute() = ENABLED.get()

    @Throws(SQLException::class)
    override fun execute() : Optional<Consumer<Player>> {

        var orderCount = 0
        val orderResults = fetchOrders()

        if(orderResults != null) {

            while (orderResults.next()) {

                parseOrder(orderResults)

                orderCount++
            }
        } else {
            logger.error("Failed to fetch purchases for $this")
        }

        if(ids.isNotEmpty())
            updateOrderColumns()

        val roundedPrice = totalProductPrice.roundToInt()

        rewards.premiumPoints += rewards.extraPremiumPoints

        finaliseDialogue(orderCount)

        return Optional.of(Consumer {
            when {
                orderCount == 0 -> {
                    dialogueBuilder.setExpression(DialogueExpression.SAD_HEAD_BOW)
                    dialogueBuilder.setText(
                            "You don't have any purchases to redeem on your account.")
                    dialogueBuilder.start(it)
                }
                totalPaidPrice >= totalProductPrice -> {
                    try {
                        Store.handleRewards(rewards, it, roundedPrice)
                        dialogueBuilder.start(it)
                        it.setInt(Attribute.TIMES_PAID, it.getInt(Attribute.TIMES_PAID) + orderCount)
                        it.setInt(Attribute.AMOUNT_PAID, it.getInt(Attribute.AMOUNT_PAID) + ceil(totalPaidPrice).toInt())
                        Logging.log("redeemdonations", "$playerName has donated " + Misc.format(roundedPrice) + "$ for: " + Misc.format(rewards.premiumPoints) + " premium points")
                    } catch (e: Exception){
                        logger.error("Failed to redeem donation $rewards of $it", e)
                        it.message("Something went wrong while redeeming your purchase, please contact a staff member and screenshot this message.", Color.RED)
                    }
                }
                else -> {
                    it.statement(
                            "Your purchase claim was @red@denied</col>!",
                            "If you think this is a mistake,",
                            "please open a ticket on Discord support."
                    )
                    val message = "Server denied purchase claim from '$playerName' \t (cost = $totalProductPrice$, paid = $totalPaidPrice$)"
                    PlayerUtil.broadcastPlayerStaffMessage(message)
                    Logging.log("denieddonations", "$message\t| $rewards")
                }
            }
        })
    }

    private fun finaliseDialogue(orderCount: Int) {

        if(orderCount > 0){

            val mysteryBoxes = rewards.mysteryBoxes
            val participationPoints = rewards.participationPoints

            val premiumPoints = rewards.premiumPoints
            val extraPremiumPoints = rewards.extraPremiumPoints

            if(premiumPoints > 0) {
                if(extraPremiumPoints > 0){
                    dialogueBuilder.setExpression(DialogueExpression.HAPPY)
                    dialogueBuilder.setText(
                            "Congratulations! You have received ${Misc.formatWithAbbreviation(rewards.premiumPoints.toLong())} @cya@Premium Points</col>",
                            "of which ${Misc.formatWithAbbreviation(rewards.extraPremiumPoints.toLong())} are bonus points!")
                } else
                    dialogueBuilder.setText(
                        "You have claimed ${Misc.formatWithAbbreviation(rewards.premiumPoints.toLong())} @cya@Premium Points</col>!")

                dialogueBuilder.setExpression(DialogueExpression.HAPPY)
                dialogueBuilder.add()
                        .setText("Thank you for your purchase!",
                                if(promptTeleport) "Would you like to visit my shop?"
                                else "Would you like to view my shop?")
                        .add(DialogueType.OPTION)
                        .firstOption("Yes.") {
                            if(promptTeleport)
                                Store.teleportToPremiumStoreOwner(it)
                            else
                                Store.openPremiumStore(it)
                        }
                        .addCancel("No.")
            }

            if(mysteryBoxes > 0) {
                dialogueBuilder = dialogueBuilder.add(DialogueType.ITEM_STATEMENT_NO_HEADER)
                        .setItem(ItemID.MYSTERY_BOX, 200)
                        .setText("You have received $mysteryBoxes @cya@Mystery Box" + (if (mysteryBoxes > 1) "es" else ""))
            }
            
            if(participationPoints > 0){
               dialogueBuilder = dialogueBuilder
                        .add(DialogueType.NPC_STATEMENT)
                        .setNpcChatHead(5792)
                       .setText("You have received $participationPoints @cya@Participation Points")
            }

            dialogueBuilder
                    .add(DialogueType.NPC_STATEMENT)
                    .setNpcChatHead(5792)
                    .setExpression(DialogueExpression.HAPPY)
                    .setText("Thank you for donating!",
                            if(promptTeleport) "Would you like to visit my shop?"
                            else "Would you like to view my shop?")
                    .add(DialogueType.OPTION)
                    .firstOption("Yes.") {
                        if(promptTeleport)
                            Store.teleportToPremiumStoreOwner(it)
                        else
                            Store.openPremiumStore(it)
                    }
                    .addCancel("No.")
        }
    }

    private fun parseOrder(orderResults: ResultSet) {
        if(orderResults.getInt("is_dice_order") == 1){
            rewards.giveDiceRank = true
            rewards.extraPremiumPoints += 2_500
        }

        val totalPaid = orderResults.getDouble("total_payment_amount")

        Store.checkExtraRewards(rewards, totalPaid.roundToInt())

        totalPaidPrice += totalPaid

        val orderId = orderResults.getLong("id")
        val productId = orderResults.getLong("product_id")
        val productResult = fetchProducts(productId)

        if (productResult != null) {
            ids.add(orderId)
            if (productResult.next())
                parseProduct(productResult)
            else {
                System.err.println("Could not find product {$productId} for $this")
                dialogueBuilder.setText(
                        "Did not find product @cya@$productId</col>",
                        "Please ask for help on our Discord.")
                dialogueBuilder = dialogueBuilder.add()
            }
        } else
            logger.error("Something went wrong with claiming product {$productId} for $this")
    }

    private fun parseProduct(productResult: ResultSet) {
        val productName = productResult.getString("name")
        val productPrice = productResult.getDouble("price")

        totalProductPrice += productPrice

        if (productName.contains("Premium Points"))
            rewards.premiumPoints += productResult.getInt("amount")
        else {
            dialogueBuilder.setText("You have claimed @cya@$productName</col>")
            dialogueBuilder = dialogueBuilder.add()
        }
    }

    private fun fetchOrders(): ResultSet? {
        val query = QueryBuilder()
                .command(QueryBuilder.SELECT)
                .k("player_name")
                .k("claimed_at")
                .k("total_payment_amount")
                .k("id")
                .table(ORDERS_TABLE)
                .limit(28)
                .where(String.format("username = '%s' AND status = 'Approved' AND claimed_at IS NULL", playerName))

        val statement = connection?.createStatement()

        return statement?.executeQuery(query.toString())
    }

    private fun fetchProducts(productId: Long): ResultSet? {

        val resultsQuery = QueryBuilder()
                .command(QueryBuilder.SELECT)
                .k("id")
                .table(PRODUCTS_TABLE)
                .limit(1)
                .where(String.format("id = '%d'", productId))

        return connection
                ?.createStatement()
                ?.executeQuery(resultsQuery.toString())
    }

    private fun updateOrderColumns() {

        val updateQuery = QueryBuilder()
                .command(QueryBuilder.UPDATE)
                .kv("claimed_at", Timestamp(System.currentTimeMillis()))
                .kv("claimed_ip", playerIp)
                .kv("claimed_mac", playerMac)
                .kv("status", "Redeemed")
                .table(ORDERS_TABLE)
                .where(String.format("id IN (" + ids.joinToString(",") + ")"))

        connection
                ?.createStatement()
                ?.executeUpdate(updateQuery.build())
    }

    override fun toString(): String {
        return "LookUpPlayerPurchase(playerName='$playerName', playerIp='$playerIp', playerMac='$playerMac')"
    }

    companion object {

        val ENABLED = AtomicBoolean(true)

        private const val ORDERS_TABLE = "orders"
        private const val PRODUCTS_TABLE = "products"
    }

}
