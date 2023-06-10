package com.grinder.game.message.impl

import com.grinder.game.message.Message

/**
 * A [Message] sent by the client representing the clicking of an npc menu action. Note that the actual message
 * sent by the client is one of the three npc action messages, but this is the message that should be intercepted (and
 * the option verified).
 *
 * @author Major
 *
 * Creates an npc action message.
 *
 * @param opcode The option number.
 * @param index The index of the npc.
 */
open class NPCOptionMessage(val index: Int, val opcode: Int) : Message