package com.grinder.util.debug;

import com.grinder.game.entity.agent.player.Player;

import java.util.Optional;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-27
 */
public interface DebugProvider {

    Optional<DebugListener> provide(final Player player);

}
