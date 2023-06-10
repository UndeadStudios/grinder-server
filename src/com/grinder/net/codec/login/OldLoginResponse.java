package com.grinder.net.codec.login;

import com.grinder.game.entity.agent.player.PlayerRights;

/**
 * The packet that contains information about a players login attempt.
 *
 * @author Vult-R
 */
public final class OldLoginResponse {

    /**
     * The login response that was indicated.
     */
    private final int response;

    /**
     * The rights of the player logging in.
     */
    private final PlayerRights rights;
    

    /**
     * Creates a new {@link LoginResponse}.
     *
     * @param response The response that was indicated.
     * @param rights   The rights of the player logging in.
     * @param flagged  The flag that indicates a player was flagged.
     */
    public OldLoginResponse(int response, PlayerRights rights) {
        this.response = response;
        this.rights = rights;
    }

    public OldLoginResponse(int response) {
        this.response = response;
        this.rights = PlayerRights.NONE;
    }

    public int getResponse() {
        return response;
    }

    public PlayerRights getRights() {
        return rights;
    }
}

