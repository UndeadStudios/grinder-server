package com.grinder.game.entity.agent.player.event.impl

import com.grinder.game.entity.agent.player.event.PlayerEvent
import com.grinder.game.model.areas.Area

class AreaChangedEvent(val oldArea: Area?, val newArea: Area?) : PlayerEvent