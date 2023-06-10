package com.grinder.util.benchmark;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since 24/09/2020
 */
public interface Monitored {

    default void onStateChange(String newState) {
        Monitors.INSTANCE.onStateSwitch(this, newState);
    }

}
