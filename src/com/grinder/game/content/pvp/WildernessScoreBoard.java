package com.grinder.game.content.pvp;

import java.lang.reflect.Type;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map.Entry;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.util.json.JsonIO;
import com.grinder.util.Misc;

public class WildernessScoreBoard {

	private static final int MAIN_INTERFACE = 32_110;

	private static final int CONFIG = 634;

	private static final String DIRECTORY = "./data/saves/highscores/";

	public static final HashMap<Integer, HashMap<String, WildernessRecord>> LIVE_HIGHSCORES = new HashMap<>();

	public static final HashMap<String, WildernessRecord> ALL_TIME = new HashMap<>();

	private static final Type LIVE_REFERENCE = new TypeToken<HashMap<Integer, HashMap<String, WildernessRecord>>>() {
	}.getType();

	private static final Type ALL_TIME_REFERENCE = new TypeToken<HashMap<String, WildernessRecord>>() {
	}.getType();

	private ScoreBoard board;

	public static final JsonIO IO = new JsonIO(DIRECTORY, "wilderness") {

		@Override
		public void init(String name, Gson builder, JsonObject reader) {
			if (reader.has("live-highscores")) {
				JsonElement e = reader.get("live-highscores");

				HashMap<Integer, HashMap<String, WildernessRecord>> result = GSON.fromJson(e.toString(),
						LIVE_REFERENCE);

				LIVE_HIGHSCORES.putAll(result);
			}

			if (reader.has("all-time-highscores")) {
				JsonElement e = reader.get("all-time-highscores");

				HashMap<String, WildernessRecord> result = GSON.fromJson(e.toString(), ALL_TIME_REFERENCE);

				ALL_TIME.putAll(result);
			}
		}

		@Override
		public JsonObject save(String name, Gson builder, JsonObject object) {
			object.add("live-highscores", builder.toJsonTree(LIVE_HIGHSCORES, LIVE_REFERENCE));
			object.add("all-time-highscores", builder.toJsonTree(ALL_TIME, ALL_TIME_REFERENCE));
			return object;
		}
	};

	public static void track(String name, int kills, int killStreak) {

		int today = getTodayDate();

		LIVE_HIGHSCORES.computeIfAbsent(today, k -> new HashMap<>());

		WildernessRecord record = new WildernessRecord(name, kills, killStreak);

		if (LIVE_HIGHSCORES.get(today).get(name) == null) {
			LIVE_HIGHSCORES.get(today).put(name, record);
		} else {
			LIVE_HIGHSCORES.get(today).get(name).kills = kills;
			LIVE_HIGHSCORES.get(today).get(name).killStreak = killStreak;
		}

		if (ALL_TIME.get(name) == null) {
			ALL_TIME.put(name, record);
		} else {
			WildernessRecord allTimeRecord = ALL_TIME.get(name);

			if (allTimeRecord.kills < kills) {
				allTimeRecord.kills = kills;
			}

			if (allTimeRecord.killStreak < killStreak) {
				allTimeRecord.killStreak = killStreak;
			}
		}

		IO.save();
	}

	public static void open(Player player, ScoreBoard board, int type) {

		player.getWildernessScoreBoard().board = board;

		HashMap<String, WildernessRecord> highscores = new HashMap<>();

		int today = getTodayDate();

		if (board == ScoreBoard.TODAY) {

			if (LIVE_HIGHSCORES.get(today) != null) {
				highscores.putAll(LIVE_HIGHSCORES.get(today));
			}

		} else if (board == ScoreBoard.THIS_WEEK) {

			for (int i = 0; i < 7; i++) {

				if (LIVE_HIGHSCORES.get(today - i) != null) {
					highscores.putAll(LIVE_HIGHSCORES.get(today - i));
				}
			}
		} else if (board == ScoreBoard.ALL_TIME) {

			if (ALL_TIME.size() > 0) {
				highscores.putAll(ALL_TIME);
			}
		}

		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		long howMany = (c.getTimeInMillis() - System.currentTimeMillis());

		Duration duration = Duration.between(LocalDateTime.now(), getWeekEnd(LocalDateTime.now()));

		player.getPacketSender().sendString(MAIN_INTERFACE + 21, "Day ends in: @whi@ " + getTimeElapsed(howMany) + "."
				+ " @or1@Week ends in: @whi@" + duration.toDays() + " days");

		int line = MAIN_INTERFACE + 25;

		player.getPacketSender().clearInterfaceText(line, MAIN_INTERFACE + 350);

		for (WildernessRecord r : sortList(highscores, 1)) {

			player.getPacketSender().sendString(line, Misc.formatName(r.username));

			player.getPacketSender().sendString(line + 1, r.kills + "");

			player.getPacketSender().sendString(line + 2, r.killStreak + "");

			line += 5;
		}

		player.getPacketSender().sendConfig(CONFIG, board.ordinal());

		player.getPacketSender().sendInterface(MAIN_INTERFACE);
	}

	private static LocalDateTime getWeekEnd(LocalDateTime d) {
		return d.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
	}

	private static int getTodayDate() {
		final Calendar cal = new GregorianCalendar();
		final int day = cal.get(Calendar.DAY_OF_MONTH);
		final int month = cal.get(Calendar.MONTH);
		final int year = cal.get(Calendar.YEAR);
		return year + month * 100 + day;
	}

	private static String getTimeElapsed(long different) {
		final long secondsInMilli = 1000;
		final long minutesInMilli = secondsInMilli * 60;
		final long hoursInMilli = minutesInMilli * 60;
		final long daysInMilli = hoursInMilli * 24;

		different = different % daysInMilli;

		final long hours = different / hoursInMilli;
		different = different % hoursInMilli;

		final long minutes = different / minutesInMilli;
		different = different % minutesInMilli;

		final long seconds = different / secondsInMilli;

		if (hours > 0) {
			return hours + "h, " + minutes + "m, " + seconds + "s";
		}
		if (minutes > 0) {
			return minutes + " min, " + seconds + " sec";
		}
		if (seconds > 0) {
			return seconds + " sec";
		}
		return "Just Started!";
	}

	private static ArrayList<WildernessRecord> sortList(HashMap<String, WildernessRecord> list, int type) {
		ArrayList<WildernessRecord> records = new ArrayList<>();

		for (Entry<String, WildernessRecord> l : list.entrySet()) {
			WildernessRecord r = l.getValue();

			records.add(r);
		}

		records.sort((record, record1) -> {

			if (type == 0) {
				return record.username.compareTo(record1.username);
			} else if (type == 1) {
				if (record.kills == record1.kills) {
					return Integer.compare(record1.killStreak, record.killStreak);
				} else if (record.kills > record1.kills) {
					return -1;
				} else {
					return 1;
				}
			} else {
				return Integer.compare(record1.killStreak, record.killStreak);
			}
		});

		return records;
	}

	public enum ScoreBoard {
		TODAY,

		THIS_WEEK,

		ALL_TIME,
	}

	public static final class WildernessRecord {
		public String username;

		public int kills;

		public int killStreak;

		public WildernessRecord(String username, int kills, int killStreak) {
			this.username = username;
			this.kills = kills;
			this.killStreak = killStreak;
		}
	}

	public static boolean handleButtonInteraction(Player player, int button) {
		switch (button) {
			case 32_644:
				open(player, player.getWildernessScoreBoard().board, 0);
				return true;

			case 32_639:
				open(player, player.getWildernessScoreBoard().board, 1);
				return true;

			case 32_634:
				open(player, player.getWildernessScoreBoard().board, 2);
				return true;

			case 32_117:
				open(player, ScoreBoard.TODAY, 1);
				return true;

			case 32_118:
				open(player, ScoreBoard.THIS_WEEK, 1);
				return true;

			case 32_119:
				open(player, ScoreBoard.ALL_TIME, 1);
				return true;
		}
		return false;
	}
}