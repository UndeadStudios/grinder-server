package com.grinder.game.content.pvp.bountyhunter.kill;

import com.grinder.game.content.pvp.bountyhunter.PlayerKillRewardManager;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;

import java.util.Iterator;

public class KillVerification {

	private int serialMatchCount;
	private int macAdressMatchCount;
	private int addressMatchCount;
	private int accountMatchCount;

	/**
	 * Gets the match count
	 * 
	 * @param killer
	 *            the killer
	 * @param killed
	 *            the killed
	 * @param time
	 *            the time
	 * @param removeExpired
	 *            the remove time
	 * @return the count
	 */
	public static KillVerification getSerialMatchCount(Player killer, Player killed, long time, boolean removeExpired) {
		KillVerification killVerification = new KillVerification();
		for (Iterator<Kill> iterator = PlayerKillRewardManager.WILD_KILLS.iterator(); iterator.hasNext();) {
			Kill wildKill = iterator.next();

			if (wildKill.isEqualKiller(killer)) {
				if (!wildKill.hasElapsed(time)) {
					if (wildKill.getKilledID() == killed.getIndex()) {
						killVerification.accountMatchCount++;
					} else if (wildKill.getKilledSerial().equals(killed.getSnAddress())) {
						if(PlayerUtil.hasValidSerialAddress(killed))
							killVerification.serialMatchCount++;
					} else if (wildKill.getKilledMac().equals(killed.getMacAddress())) {
						if(PlayerUtil.hasValidMacAddress(killed))
							killVerification.macAdressMatchCount++;
					} else if (wildKill.getKillerAddress().equals(killed.getHostAddress())) {
						killVerification.addressMatchCount++;
					}
				} else {
					if (removeExpired) {
						iterator.remove();
					}
				}
			}

		}
		return killVerification;
	}

	public int getSerialMatchCount() {
		return serialMatchCount;
	}

	public int getMacAdressMatchCount() {
		return macAdressMatchCount;
	}

	public int getAddressMatchCount() {
		return addressMatchCount;
	}

	public int getAccountMatchCount() {
		return accountMatchCount;
	}

	public int getTotalMatches() {
		return serialMatchCount + macAdressMatchCount + addressMatchCount + accountMatchCount;
	}

	@Override
	public String toString() {
		return "KillVerification [serialMatchCount=" + serialMatchCount + ", macAdressMatchCount=" + macAdressMatchCount
				+ ", addressMatchCount=" + addressMatchCount + ", accountMatchCount=" + accountMatchCount + "]";
	}

}
