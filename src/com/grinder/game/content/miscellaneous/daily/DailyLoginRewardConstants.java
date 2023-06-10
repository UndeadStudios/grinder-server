package com.grinder.game.content.miscellaneous.daily;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-01
 */
class DailyLoginRewardConstants {

    /**
     * The minimum amount of days to be eligible for a daily reward.
     */
    static final int DAYS_REQUIRED = 2;

    /**
     * The save file location.
     */
    static final Path PATH = Paths.get("data", "saves", "daily_login_rewards.json");

    static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyyy hh:mm:ss");
}
