package com.grinder.game.model.punishment;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.net.codec.login.LoginResultType;

import java.util.Optional;

/**
 * Represents the type of {@link Punishment punishments} that
 * can be applied to players.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-02
 */
public enum PunishmentType {

    WARN(PlayerRights.SERVER_SUPPORTER, PunishmentLevel.ACCOUNT_LEVEL),
    KICK(PlayerRights.SERVER_SUPPORTER, PunishmentLevel.ACCOUNT_LEVEL, "kicks"),
    MUTE(PlayerRights.SERVER_SUPPORTER, PunishmentLevel.ACCOUNT_LEVEL, "mutes"),
    JAIL(PlayerRights.SERVER_SUPPORTER, PunishmentLevel.ACCOUNT_LEVEL, "jails"),
    LOCK(PlayerRights.MODERATOR, PunishmentLevel.ACCOUNT_LEVEL, "locks"),
    BAN(PlayerRights.MODERATOR, PunishmentLevel.ACCOUNT_LEVEL, "bans"),

    UID_MUTE(PlayerRights.MODERATOR, PunishmentLevel.UID_LEVEL, "UID_mutes"),
    UID_BAN(PlayerRights.MODERATOR, PunishmentLevel.UID_LEVEL, "UID_bans"),

    IP_MUTE(PlayerRights.MODERATOR, PunishmentLevel.IP_ADDRESS_LEVEL, "IPmutes"),
    IP_BAN(PlayerRights.MODERATOR, PunishmentLevel.IP_ADDRESS_LEVEL, "IPBans"),

    MAC_MUTE(PlayerRights.MODERATOR, PunishmentLevel.MAC_ADDRESS_LEVEL, "hostmutes"),
    MAC_BAN(PlayerRights.MODERATOR, PunishmentLevel.MAC_ADDRESS_LEVEL, "hostbans"),

    SERIAL_BAN(PlayerRights.ADMINISTRATOR, PunishmentLevel.SERIAL_ADDRESS_LEVEL, "sn_bans"),
    HD_SERIAL_BAN(PlayerRights.ADMINISTRATOR, PunishmentLevel.HARDWARE_SERIAL_ADDRESS_LEVEL, "hd_sn_bans");

    public static final PunishmentType[] DUPE_BANS = {MAC_BAN, IP_BAN, BAN};
    public static final PunishmentType[] ALL_BANS = {HD_SERIAL_BAN, SERIAL_BAN, MAC_BAN, IP_BAN, BAN};

    private final PlayerRights minimumRightsRequired;
    private final PunishmentLevel punishmentLevel;
    private final String logFolderName;

    PunishmentType(PlayerRights minimumRightsRequired, PunishmentLevel punishmentLevel) {
        this(minimumRightsRequired, punishmentLevel, "");
    }

    PunishmentType(PlayerRights minimumRightsRequired, PunishmentLevel punishmentLevel, String logFolderName) {
        this.minimumRightsRequired = minimumRightsRequired;
        this.punishmentLevel = punishmentLevel;
        this.logFolderName = logFolderName;
    }

    /**
     * Are the argued rights sufficient to apply a {@link Punishment} of this type.
     *
     * @param rights the {@link PlayerRights} to check.
     * @return {@code true} if the argued rights can apply punishments of this level,
     *          {@code false} if not.
     */
    public boolean hasSufficientRights(PlayerRights rights){
        return rights.isStaff(minimumRightsRequired);
    }

    /**
     * Should a confirmation be sent to the {@link Player}
     * applying the punishment if the target resides in the wilderness?
     *
     * @return {@code true} if a confirmation screen should be prompted,
     *          {@code false} if not.
     */
    public boolean askConfirmationIfTargetInWild(){
        return this == JAIL;
    }

    /**
     * Is there only an immediate action for this punishment,
     * e.g. disconnect the player, or should the punishment be kept track of.
     *
     * @return {@code true} if there is only an immediate action,
     *          {@code false} if this punishment should be saved.
     */
    public boolean onlyImmediateAction(){
        return this == KICK || this == WARN;
    }

    /**
     * Should the target be disconnected from the game
     * after applying a {@link Punishment} of this type?
     *
     * @return {@code true} if the target should be disconnected,
     *          {@code false} if not.
     */
    public boolean isDisconnectPlayer() {
        return this == KICK || this == LOCK || this == BAN || this == IP_BAN || this == MAC_BAN || this == SERIAL_BAN || this == HD_SERIAL_BAN;
    }

    /**
     * Should the targeted player be restricted from talking?
     *
     * @return {@code true} if the target is restricted from talking,
     *          {@code false} if not.
     */
    public boolean isMutePlayer(){
        return this == MUTE || this == IP_MUTE || this == MAC_MUTE;
    }

    /**
     * Should logs be written for {@link Punishment punishments} of this level?
     *
     * @return {@code true} if logs should be written,
     *          {@code false} if not.
     */
    public boolean writeLogs(){
        return !logFolderName.isEmpty();
    }

    /**
     * Gets the {@link LoginResultType} for a player to whom
     * a punishment of this type is applicable.
     *
     * @return {@link Optional<LoginResultType>} if this level is being used for validating logins,
     *          {@link Optional#empty()} if this level does not restricy logins.
     */
    public Optional<LoginResultType> getLoginResponse(){
        switch (this){
            case LOCK: return Optional.of(LoginResultType.LOGIN_ACCOUNT_LOCKED);
            case BAN: return Optional.of(LoginResultType.LOGIN_DISABLED_ACCOUNT);
            case IP_BAN: return Optional.of(LoginResultType.LOGIN_DISABLED_COMPUTER); // confuse em
            case UID_BAN:
            case SERIAL_BAN:
            case HD_SERIAL_BAN:
            case MAC_BAN: return Optional.of(LoginResultType.LOGIN_DISABLED_IP); // confuse em
        }
        return Optional.empty();
    }

    public String getLogFolderName(){
        return logFolderName;
    }

    public PunishmentLevel getPunishmentLevel() {
        return punishmentLevel;
    }
}
