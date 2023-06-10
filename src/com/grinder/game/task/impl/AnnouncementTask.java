package com.grinder.game.task.impl;

import com.grinder.ServerIO;
import com.grinder.game.content.miscellaneous.Announcement;
import com.grinder.game.content.miscellaneous.CommandEvent;
import com.grinder.game.content.gambling.lottery.Lottery;
import com.grinder.game.content.miscellaneous.StoreBonusEvent;
import com.grinder.game.model.punishment.PunishmentManager;
import com.grinder.game.task.Task;
import com.grinder.util.Misc;

public class AnnouncementTask extends Task {

    /**
     * If announced lottery
     */
    public static boolean lotteryWinnerDeclared;

    public AnnouncementTask() {
		super(100);
	}

	private int minute = 0;

	@SuppressWarnings("deprecation")
	@Override
	protected void execute() {
		/*
		 * Announcements
		 */
		if (minute % 10 == 0) {
			if (Misc.getRandomInclusive(2) == 1) {
				Announcement.sequence();
			} else {
				Announcement.sequence2();
			}
		}
		/*
		 * Command event
		 */
		if (minute % 10 == 0) {
			CommandEvent.sendAnnouncement();
		}
		/*
		 * Lottery
		 */
		if (ServerIO.SERVER_TIME.getHours() == 19 && !lotteryWinnerDeclared) {
			Lottery.pickWinner();
		}


		/*if (Misc.random(250) == Misc.random(250)) {
			StoreBonusEvent.runRandomStoreEvent(player);
		}*/

		minute++;
	}
}
