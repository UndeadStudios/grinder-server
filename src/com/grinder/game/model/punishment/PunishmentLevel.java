package com.grinder.game.model.punishment;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.util.Misc;

import java.util.Optional;

/**
 * Represents the level at which a {@link Punishment} can be applied.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-02
 */
public enum PunishmentLevel {

    /**
     * Apply to a specific {@link Player} account.
     */
    ACCOUNT_LEVEL,

    /**
     * Apply to any {@link Player} that has the targeted ip-address.
     */
    IP_ADDRESS_LEVEL,

    /**
     * Apply to any {@link Player} that has the targeted mac-address.
     */
    MAC_ADDRESS_LEVEL,

    /**
     * Apply to any {@link Player} that has the targeted serial-number.
     */
    SERIAL_ADDRESS_LEVEL,

    /**
     * Apply to any {@link Player} that has the targeted hardware-serial-address.
     */
    HARDWARE_SERIAL_ADDRESS_LEVEL,

    /**
     * Apply to any {@link Player} that has the targeted UID.
     */
    UID_LEVEL;

    /**
     * Finds the field in the player save file that
     * is the targeted string for this level of punishment.
     *
     * @return {@link Optional<String>} containing the field name,
     *          {@link Optional#empty()} if none was found.
     */
    public Optional<String> findPlayerFileKey(){
        switch (this){
            case IP_ADDRESS_LEVEL:
                return Optional.of("connected-from");
            case MAC_ADDRESS_LEVEL:
                return Optional.of("mac-address");
            case SERIAL_ADDRESS_LEVEL:
                return Optional.of("sn-address");
            case HARDWARE_SERIAL_ADDRESS_LEVEL:
                return Optional.of("hd-serial-number");
            case UID_LEVEL:
                return Optional.of("UID");
        }
        return Optional.empty();
    }

    /**
     * Finds the field value in the argued {@link Player} instance that
     * is the targeted string for this level of punishment.
     *
     * @return {@link Optional<String>} containing the field name,
     *          {@link Optional#empty()} if none was found.
     */
    public Optional<String> findPlayerKey(Player player){
        switch (this){
            case ACCOUNT_LEVEL:
                return Optional.of(Misc.formatPlayerName(player.getUsername().toLowerCase()));
            case IP_ADDRESS_LEVEL:
                return Optional.of(player.getHostAddress());
            case MAC_ADDRESS_LEVEL:
                return Optional.of(player.getMacAddress());
            case SERIAL_ADDRESS_LEVEL:
                return Optional.of(player.getSnAddress());
            case HARDWARE_SERIAL_ADDRESS_LEVEL:
                return Optional.of(player.getHdSerialNumber());
            case UID_LEVEL:
                return Optional.of(player.getUID());
        }
        return Optional.empty();
    }

    /**
     * Check if the to-be targeted string is valid.
     *
     * @param identifier the targeted string.
     * @return {@code true} if the string can be targeted,
     *          {@code false} if not.
     */
    public boolean isValid(String identifier){
        if (this == PunishmentLevel.MAC_ADDRESS_LEVEL) {
            return PlayerUtil.isValidMacAddress(identifier);
        }
        return !identifier.isBlank();
    }
}
