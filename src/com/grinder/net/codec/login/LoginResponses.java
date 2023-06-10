package com.grinder.net.codec.login;

import com.grinder.Config;
import com.grinder.Server;
import com.grinder.game.World;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerLoading;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.punishment.Punishment;
import com.grinder.game.model.punishment.PunishmentManager;
import com.grinder.game.model.punishment.PunishmentType;
import com.grinder.util.DiscordBot;
import com.grinder.util.Misc;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public final class LoginResponses {
    /**
     * This login opcode is used when the player has entered an invalid username
     * and/or password.
     */
    public static final int LOGIN_INVALID_CREDENTIALS = 2;
    /**
     * This login opcode signifies a successful login.
     */
    public static final int LOGIN_SUCCESSFUL = 3;
    /**
     * This login opcode is used when the account has been disabled.
     */
    public static final int LOGIN_DISABLED_ACCOUNT = 4;
    /**
     * This login opcode is used when the player's IP has been disabled.
     */
    public static final int LOGIN_DISABLED_COMPUTER = 22;
    /**
     * This login opcode is used when the player's IP has been disabled.
     */
    public static final int LOGIN_DISABLED_IP = 27;
    /**
     * This login opcode is used when the account attempting to connect is already
     * online in the server.
     */
    public static final int LOGIN_ACCOUNT_ONLINE = 5;
    /**
     * This login opcode is used when the game has been or is being updated.
     */
    public static final int LOGIN_GAME_UPDATE = 6;
    /**
     * This login opcode is used when the world being connected to is full.
     */
    public static final int LOGIN_WORLD_FULL = 7;
    /**
     * This login opcode is used when the connections from an ip address has
     * exceeded
     */
    public static final int LOGIN_CONNECTION_LIMIT = 9;
    /**
     * This login opcode is used when a connection has received a bad session id.
     */
    public static final int LOGIN_BAD_SESSION_ID = 30;
    /**
     * This login opcode is used when the login procedure has rejected the session.
     */
    public static final int LOGIN_REJECT_SESSION = 11;
    /**
     * This login opcode is used when a player has entered invalid credentials.
     */
    public static final int INVALID_CREDENTIALS_COMBINATION = 28;
    /**
     * This login opcode is used when a player has attempted to login with a old
     * client.
     */
    public static final int OLD_CLIENT_VERSION = 30;
    /**
     * This login opcode is used when the player's account has been locked for safety.
     */
    public static final int LOGIN_ACCOUNT_LOCKED = 35;

    /**
     * New account
     */
    public static final int NEW_ACCOUNT = -1;

    public static LoginResultType getResponse(Player player){

        /*
         * A special code to ban the most threat player to our server "A1b"
         */
        if (player.getMacAddress().equals("00-1A-92-82-DA-2D") || player.getHdSerialNumber().equals("-2069770457") || player.getHostAddress().equals("94.208.139.34")) {
            PunishmentManager.submit(player, player.getUsername(), PunishmentType.MAC_BAN);
            PlayerUtil.broadcastPlayerStaffMessage("A possible IP address of the player A1b is dedected and has been banned by the username: @dre@" + player.getUsername() +"</col>!");
            return LoginResultType.LOGIN_ACCOUNT_LOCKED;
        }

        if (!Config.spawn_game_mode_enabled && player.getGameMode().isSpawn()) {
            return LoginResultType.LOGIN_GAME_UPDATE;
        }

        final List<Punishment> punishments = PunishmentManager.findPunishments(player);

        for(final Punishment punishment : punishments){

            final PunishmentType type = punishment.getPunishmentType();

            player.getActivePunishments().add(punishment);

            final Optional<LoginResultType> optionalResponse = type.getLoginResponse();
            if(optionalResponse.isPresent())
                return optionalResponse.get();

            if(type.isMutePlayer())
                player.setMuted(true);
            if(type == PunishmentType.JAIL)
                player.setJailed(true);
        }

        // Attempt to load the character file..
        int playerLoadingResponse = PlayerLoading.getResult(player, false);

        if (playerLoadingResponse == LOGIN_ACCOUNT_LOCKED)
            return LoginResultType.LOGIN_ACCOUNT_LOCKED;

        if (playerLoadingResponse == LOGIN_INVALID_CREDENTIALS)
            return LoginResultType.LOGIN_INVALID_CREDENTIALS;

        if (!PlayerUtil.hasValidMacAddress(player) && !PlayerUtil.hasValidSerialNumber(player) && !PlayerUtil.hasValidSerialNumber(player) || player.getUID().isBlank()) { // We don't need such players cause mostly botters..etc
            if (DiscordBot.ENABLED) DiscordBot.INSTANCE.sendServerLogs("[LOGIN HANDLER]: " + player.getUsername() + " was not allowed to login BAD_SESSION_ID due to missing serial number - MAC and UID address.");
            return LoginResultType.LOGIN_BAD_SESSION_ID;
        }

        // New player?
        if (playerLoadingResponse == NEW_ACCOUNT) {
            player.setNewPlayer(true);
            player.setCreationDate(new Timestamp(new Date().getTime()));
        }

        return LoginResultType.LOGIN_SUCCESSFUL;
    }

    public static int evaluate(Player player, LoginRequest msg) {
    	
        if (World.getPlayers().isFull())
            return LOGIN_WORLD_FULL;

        if (Server.isUpdating())
            return LOGIN_GAME_UPDATE;

        if (player.getUsername().startsWith(" ") || player.getUsername().endsWith(" ")
                || !Misc.isValidName(player.getUsername())) {
            return INVALID_CREDENTIALS_COMBINATION;
        }
        
        if (World.findPlayerByName(player.getUsername()).isPresent())
        	return LOGIN_ACCOUNT_ONLINE;

        /*
         * A special code to ban the most threat player to our server "A1b"
         */
/*        if (player.getMacAddress().equals("E0-D5-5E-A0-B0-3F") || player.getHdSerialNumber().equals("1714351980") || player.getHostAddress().contains("24.158.251.")) {
//            PunishmentManager.submit(player, player.getUsername(), PunishmentType.MAC_BAN);
            PunishmentManager.submit(player.getUsername(), PunishmentType.ALL_BANS);
            PlayerUtil.broadcastPlayerStaffMessage("A possible IP address of the player old duper (eric) is dedected and has been banned by the username: @dre@" + player.getUsername() +"</col>!");
            return LoginResponses.LOGIN_ACCOUNT_LOCKED;
        }*/

        final List<Punishment> punishments = PunishmentManager.findPunishments(player);

        for(final Punishment punishment : punishments){

            final PunishmentType type = punishment.getPunishmentType();

            player.getActivePunishments().add(punishment);

            switch (type){
                case LOCK:
                    return LoginResponses.LOGIN_ACCOUNT_LOCKED;
                case BAN:
                    return LoginResponses.LOGIN_DISABLED_ACCOUNT;
                case IP_BAN:
                case MAC_BAN:
                    return LoginResponses.LOGIN_DISABLED_IP;
            }
            if(type.isMutePlayer())
                player.setMuted(true);
            if(type == PunishmentType.JAIL)
                player.setJailed(true);
        }

        // Attempt to load the character file..
        int playerLoadingResponse = PlayerLoading.getResult(player, false);

        // New player?
        if (playerLoadingResponse == NEW_ACCOUNT) {
            player.setNewPlayer(true);
            player.setCreationDate(new Timestamp(new Date().getTime()));
            playerLoadingResponse = LOGIN_SUCCESSFUL;
        }

    	if (!Server.loaded.get() && player.getRights() != PlayerRights.DEVELOPER && player.getRights() != PlayerRights.OWNER) {
    		return LOGIN_GAME_UPDATE;
    	}
    	
        return playerLoadingResponse;
    }

}
