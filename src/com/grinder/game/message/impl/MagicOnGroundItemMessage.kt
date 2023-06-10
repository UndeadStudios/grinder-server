package com.grinder.game.message.impl

import com.grinder.game.message.Message

data class MagicOnGroundItemMessage(val itemId: Int, val spellId: Int, val itemX: Int, val itemY: Int) : Message