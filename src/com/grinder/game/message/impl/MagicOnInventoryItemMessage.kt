package com.grinder.game.message.impl

import com.grinder.game.message.Message

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
data class MagicOnInventoryItemMessage(var slot: Int, var itemId: Int, var childId: Int, var spellId: Int) : Message
