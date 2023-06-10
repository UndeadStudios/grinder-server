package com.grinder.game.entity.agent.npc.monster

import com.grinder.game.entity.getInt
import com.grinder.game.model.attribute.Attribute

fun Monster.onEvent(event: MonsterEvent, function: (MonsterEvents) -> Unit) {
    onEvent {
        if (it == event) {
            function.invoke(it)
        }
    }
}

inline fun<reified T : MonsterEvents> Monster.onEvent(crossinline function: (T) -> Unit) {
    onEvent {
        if (it is T) {
            function.invoke(it)
        }
    }
}


fun Monster.onEventEvery(tickRange: IntRange, event: MonsterEvent, function: (MonsterEvent) -> Unit) {
    val executeTick = tickRange.random()
    onEvent {
        if (it == event) {
            val sequenceCount = getInt(Attribute.SEQUENCE_COUNT, 0)
            if (sequenceCount > 0 && sequenceCount % executeTick == 0)
                function.invoke(it)
        }
    }
}

fun Monster.scheduleRandomSpeech(frequencyRange: IntRange, vararg messages: String, function: (Int) -> Unit = {}) {
    require(messages.isNotEmpty())
    val lastMessageIndex : Int? = null
    onEventEvery(frequencyRange, MonsterEvents.PRE_SEQUENCE) {
        val nextMessageIndex = if (messages.size > 1) {
            var messageIndex: Int
            do {
                messageIndex = messages.indices.random()
            }
            while (messageIndex == lastMessageIndex)
            messageIndex
        } else
            0
        val nextMessage = messages[nextMessageIndex]
        if (nextMessage.isNotBlank())
            say(nextMessage)
        function.invoke(nextMessageIndex)
    }
}