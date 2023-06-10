package com.grinder.game.entity.agent.player

import com.grinder.game.World
import com.grinder.game.content.achievement.AchievementManager
import com.grinder.game.content.achievement.AchievementType
import com.grinder.game.entity.getBoolean
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.message.MessageFilterManager
import com.grinder.game.model.message.MessageType
import com.grinder.util.Logging
import com.grinder.util.Misc
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * This file represents a player's relation with other world entities, this
 * manages adding and removing friends who we can chat with and also adding and
 * removing ignored players who won't be able to message us or see us online.
 *
 * @author relex lawl Redone a bit by Gabbe
 */
class PlayerRelations
/**
 * The PlayerRelations constructor.
 *
 * @param player The associated-player.
 */(private val player: Player) {

    /**
     * This map contains the player's friends list.
     */
    val friendList: MutableList<Long> = ArrayList(200)

    /**
     * This map contains the player's ignore list.
     */
    val ignoreList: MutableList<Long> = ArrayList(100)

    /**
     * Gets the current private message index.
     *
     * @return The current private message index + 1.
     */
    val privateMessageId: Int
        get() = messageCounter.incrementAndGet()
    val isPrivateOff: Boolean
        get() = privateStatus == 2

    fun setPrivateStatus(status: Int, update: Boolean): PlayerRelations {
        player.chatSettings.modes[2] = status
        if (update) {
            updateLists(true)
        }
        return this
    }

    val privateStatus: Int
        get() = player.chatSettings.modes[2]

    /**
     * Updates the player's friend list.
     *
     * @param sendNotification If `true`, the players who have this player added, will
     * be sent the notification this player has logged in.
     */
    fun updateLists(sendNotification: Boolean) {

        player.packetSender.sendFriendStatus(2)

        val sendOnlineNotification = if (isPrivateOff) false else sendNotification

        World.playerStream().forEach { friend: Player ->
            if (friend.relations.friendList.contains(player.longUsername)) {
                val friendsOnlyAndIsNotAFriend = privateStatus == 1 && !friendList.contains(friend.longUsername)
                val isHidden = isPrivateOff
                val isIgnored = ignoreList.contains(friend.longUsername)
                val temporaryOnlineStatus = !friendsOnlyAndIsNotAFriend && !isHidden && !isIgnored && sendOnlineNotification
                friend.packetSender.sendFriend(player.longUsername, if (temporaryOnlineStatus) 1 else 0)
            }
            if (player.relations.friendList.contains(friend.longUsername)) {
                val relations = friend.relations
                val friendsOnlyAndIsNotAFriend = relations.privateStatus == 1 && !relations.friendList.contains(player.longUsername)
                val isHidden = relations.isPrivateOff
                val isIgnored = relations.ignoreList.contains(player.longUsername)
                val temporaryOnlineStatus = !friendsOnlyAndIsNotAFriend && !isHidden && !isIgnored && sendOnlineNotification
                player.packetSender.sendFriend(friend.longUsername, if (temporaryOnlineStatus) 1 else 0)
            }
        }
    }

    fun sendFriends() {
        for (l in friendList) {
            player.packetSender.sendFriend(l, 0)
        }
    }

    fun sendIgnores() {
        for (l in ignoreList) {
            player.packetSender.sendAddIgnore(l)
        }
    }

    fun sendAddFriend(name: Long) {
        player.packetSender.sendFriend(name, 0)
    }

    fun sendDeleteFriend(name: Long) {
        player.packetSender.sendDeleteFriend(name)
    }

    fun sendAddIgnore(name: Long) {
        player.packetSender.sendAddIgnore(name)
    }

    fun sendDeleteIgnore(name: Long) {
        player.packetSender.sendDeleteIgnore(name)
    }

    fun onLogin(): PlayerRelations {
        sendIgnores()
        sendFriends()
        return this
    }

    /**
     * Adds a player to the associated-player's friend list.
     *
     * @param username The user name of the player to add to friend list.
     */
    fun addFriend(username: Long) {

        val name = Misc.formatName(Misc.longToString(username))
        if (name == player.username)
            return

        if (friendList.size >= 200) {
            player.message("Your friend list is full!")
            return
        }

        if (ignoreList.contains(username)) {
            player.message("Please remove $name from your ignore list first.")
            return
        }

        if (friendList.contains(username)) {
            player.message("$name is already on your friends list!")
        } else {
            friendList.add(username)
            sendAddFriend(username)
            updateLists(true)
            val friend = World.findPlayerByName(name)
            friend.ifPresent { value: Player -> value.relations.updateLists(true) }
            AchievementManager.processFor(AchievementType.FRIENDLY, player)
        }
    }

    /**
     * Checks if a player is friend with someone.
     */
    fun isFriendWith(player: String): Boolean {
        return friendList.contains(Misc.stringToLong(player))
    }

    /**
     * Deletes a friend from the associated-player's friends list.
     *
     * @param username The user name of the friend to delete.
     */
    fun deleteFriend(username: Long) {
        val name = Misc.formatName(Misc.longToString(username))
        if (name == player.username) {
            return
        }
        if (friendList.contains(username)) {
            friendList.remove(username)
            sendDeleteFriend(username)
            if (privateStatus > 0) {
                World.findPlayerByName(name).ifPresent { removedFriend: Player -> removedFriend.relations.updateLists(false) }
            }
        } else {
            player.message("This player is not on your friends list!", 1000)
        }
    }

    /**
     * Adds a player to the associated-player's ignore list.
     *
     * @param username The user name of the player to add to ignore list.
     */
    fun addIgnore(username: Long) {
        val name = Misc.formatName(Misc.longToString(username))
        if (name == player.username)
            return

        if (ignoreList.size >= 100) {
            player.message("Your ignore list is full!")
            return
        }
        if (friendList.contains(username)) {
            player.message("Please remove $name from your friend list first.")
            return
        }
        if (ignoreList.contains(username)) {
            player.message("$name is already on your ignore list!")
        } else {
            ignoreList.add(username)
            sendAddIgnore(username)
            updateLists(true)
            val ignored = World.findPlayerByName(name)
            ignored.ifPresent { value: Player -> value.relations.updateLists(true) }
        }
    }

    /**
     * Deletes an ignored player from the associated-player's ignore list.
     *
     * @param username The user name of the ignored player to delete from ignore list.
     */
    fun deleteIgnore(username: Long) {
        val name = Misc.formatName(Misc.longToString(username))
        if (name == player.username) {
            return
        }
        if (ignoreList.contains(username)) {
            ignoreList.remove(username)
            sendDeleteIgnore(username)
            updateLists(true)
            if (privateStatus == 0) {
                val ignored = World.findPlayerByName(name)
                ignored.ifPresent { value: Player -> value.relations.updateLists(true) }
            }
        } else {
            player.sendMessage("This player is not on your ignore list!", 1000)
        }
    }

    /**
     * Sends a private message to `friend`.
     *
     * @param friend  The player to private message.
     * @param message The message being sent in bytes.
     */
    fun message(friend: Player, message: String, recompressed: ByteArray?) {

        if ((friend.relations.privateStatus == 1
                        && !friend.relations.friendList.contains(player.longUsername)) || friend.relations.isPrivateOff || player.getBoolean(Attribute.INVISIBLE)) {
            player.message("This player is currently offline.")
            return
        }

        if (isPrivateOff)
            setPrivateStatus(1, true)


        if (message.toLowerCase().contains("<img")) {
            return;
        }

        if (Misc.blockedWord(message)) {
            player.statement("A word was blocked in your sentence.", " Please do not repeat it!")
            return
        }

        MessageFilterManager.reportMessage(player, message, MessageType.PRIVATE_CHAT, friend.username)

        if (player.isAccountFlagged)
            return

        Logging.log("pms", "" + player.username + " sent a pm to: " + friend.username + " message: " + message + "")
        friend.packetSender.sendPrivateMessage(player, recompressed)
    }

    companion object {
        /**
         * The amount of messages sent globally, offset by a random variable x, `0 <= x < 100,000,000`.
         */
        @JvmField
        var messageCounter = AtomicInteger((Math.random() * 100000000).toInt())
    }
}