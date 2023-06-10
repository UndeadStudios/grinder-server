package com.grinder.net.codec.login;


import com.grinder.net.packet.Packet;
import com.grinder.net.security.IsaacRandom;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * The {@link Packet} implementation that contains data used for the final
 * portion of the login protocol.
 *
 * @author lare96 <http://github.org/lare96>
 */
public final class LoginRequest {

    /**
     * The context to which this player is going through.
     */
    private final Channel context;

    /**
     * The username of the player.
     */
    private final String username;

    /**
     * The password of the player.
     */
    private final String password;

    /**
     * The player's host address
     */
    private final String host;
    
    private final String macAddress;
    private final String snAddress;
    private final String hdSerialNumber;
    private final long UID;
    
    /**
     * The encrypting isaac
     */
    private final IsaacRandom encryptor;

    /**
     * The decrypting isaac
     */
    private final IsaacRandom decryptor;

    /**
     * Creates a new {@link LoginRequest}.
     *
     * @param ctx       the {@link ChannelHandlerContext} that holds our
     *                  {@link Channel} instance.
     * @param username  the username of the player.
     * @param password  the password of the player.
     * @param encryptor the encryptor for encrypting messages.
     * @param decryptor the decryptor for decrypting messages.
     */
    public LoginRequest(Channel context, String username, String password, String host,
                        String macAddress, String snAddress, String hdSerialNumber,
                        long UID,
                        IsaacRandom encryptor, IsaacRandom decryptor) {
        this.context = context;
        this.username = username;
        this.password = password;
        this.host = host;
        this.UID = UID;
        this.encryptor = encryptor;
        this.decryptor = decryptor;
        this.macAddress = macAddress;
        this.snAddress = snAddress;
        this.hdSerialNumber = hdSerialNumber;
    }


    public Channel getChannel() {
        return context;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public long getUID() {
        return UID;
    }

    public IsaacRandom getEncryptor() {
        return encryptor;
    }

    public IsaacRandom getDecryptor() {
        return decryptor;
    }


	/**
	 * Sets the macAddress
	 *
	 * @return the macAddress
	 */
	public String getMacAddress() {
		return macAddress;
	}


	/**
	 * Sets the snAddress
	 *
	 * @return the snAddress
	 */
	public String getSnAddress() {
		return snAddress;
	}


	/**
	 * Sets the hdSerialNumber
	 *
	 * @return the hdSerialNumber
	 */
	public String getHdSerialNumber() {
		return hdSerialNumber;
	}

    @Override
    public String toString() {
        return "LoginDetailsMessage{" +
                "context=" + context +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", host='" + host + '\'' +
                ", macAddress='" + macAddress + '\'' +
                ", snAddress='" + snAddress + '\'' +
                ", hdSerialNumber='" + hdSerialNumber + '\'' +
                ", encryptor=" + encryptor +
                ", decryptor=" + decryptor +
                '}';
    }
}
