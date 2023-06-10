package com.grinder.game.content.pvp.bountyhunter.kill;

import com.grinder.game.entity.agent.player.Player;

public class Kill {

	private final int killerID;
	private final int killedID;

	private final String killerSerial;
	private final String killedSerial;

	private final String killerMac;
	private final String killedMac;

	private final String killerName;
	private final String killedName;

	private final String killerAddress;
	private final String killedAddress;

	private long killTime;

	public Kill(int killerID, int killedID, String killerSerial, String killedSerial, String killerMac, String killedMac, String killerName, String killedName, String killerAddress, String killedAddress, long killTime) {
		this.killerID = killerID;
		this.killedID = killedID;
		this.killerSerial = killerSerial;
		this.killedSerial = killedSerial;
		this.killerMac = killerMac;
		this.killedMac = killedMac;
		this.killerName = killerName;
		this.killedName = killedName;
		this.killerAddress = killerAddress;
		this.killedAddress = killedAddress;
		this.killTime = killTime;
	}

	public long getKillTime() {
		return killTime;
	}

	public void setKillTime(long killTime) {
		this.killTime = killTime;
	}

	public int getKillerID() {
		return killerID;
	}

	public int getKilledID() {
		return killedID;
	}

	public String getKillerSerial() {
		return killerSerial;
	}

	public String getKilledSerial() {
		return killedSerial;
	}

	public String getKillerMac() {
		return killerMac;
	}

	public String getKilledMac() {
		return killedMac;
	}

	public String getKillerName() {
		return killerName;
	}

	public String getKilledName() {
		return killedName;
	}

	public String getKillerAddress() {
		return killerAddress;
	}

	public String getKilledAddress() {
		return killedAddress;
	}
	
	public boolean hasElapsed(long time) {
		return System.currentTimeMillis() - getKillTime() > time;
	}
	public boolean isEqualKiller(Player client) {
		if(client != null) {
			return getKillerID() == client.getIndex();
		}
		return false;
	}
}
