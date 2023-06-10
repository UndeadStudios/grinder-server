package com.grinder.game.entity.agent.player.event.impl

import com.grinder.game.entity.agent.player.event.PlayerEvent
import com.grinder.game.model.Position

class PositionChangedEvent(val previousPosition: Position, val currentPosition: Position)
    : PlayerEvent