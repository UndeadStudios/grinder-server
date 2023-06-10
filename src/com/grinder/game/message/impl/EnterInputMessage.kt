package com.grinder.game.message.impl

import com.grinder.game.message.Message
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.model.interfaces.syntax.impl.BankQuantityX
import com.grinder.game.model.interfaces.syntax.impl.BankX
import com.grinder.game.model.interfaces.syntax.impl.WithdrawBankX
import com.grinder.net.packet.PacketConstants
import com.grinder.net.packet.PacketListener
import com.grinder.net.packet.PacketReader

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   27/11/2019
 * @version 1.0
 */
data class EnterInputMessage(var name: String, var amount: Int) : Message
