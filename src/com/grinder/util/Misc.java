package com.grinder.util;

import com.google.common.cache.CacheLoader;
import com.grinder.game.entity.Entity;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Direction;
import com.grinder.game.model.Position;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainer;
import com.grinder.game.model.item.container.StackType;
import com.grinder.util.oldgrinder.MapEntry;
import com.grinder.util.random.RandomGen;
import org.apache.commons.lang.WordUtils;

import java.io.*;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.zip.GZIPInputStream;

import static com.grinder.game.model.Direction.*;

public class Misc {

	public static final int BROWN = 0xA52A2A;
	public static final int WHITE = 0xFFFFFF;

	public static final int RED = 0xFF0000;
	public static final int YELLOW = 0xFFFF00;

	public static final int GREEN = 0x04C404;

	public static final int OR1 = 0xFFB000;
	public static final long DAY_LENGTH = 86400000;

    public static int getTicks(int seconds) {
        return (int) (seconds / 0.6);
    }

    public static int getSeconds(int ticks) {
        return (int) (ticks * 0.6);
    }

    public static final String[] INVALID_PASS_CHARACTERS = new String[]{"[", "]", "/", "-", " ", "~", "{", "}", ",", "_", "@", "#", "$", "%", "^", "&",
            "*", "(", ")", "\"", "`", ".", "<", ">", "?",};

    // Our formatter
    public static final DecimalFormat FORMATTER = new DecimalFormat("0.#");
    public static final int HALF_A_DAY_IN_MILLIS = 43200000;
    /**
     * An array containing valid player name characters.
     */
    public static final char[] VALID_PLAYER_CHARACTERS = {'_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
            'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', '[', ']', '/', '-', ' '};
    /**
     * An array containing valid characters that may be used on the server.
     */
    public static final char[] VALID_CHARACTERS = {'_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '+', '=', ':', ';', '.', '>', '<',
            ',', '"', '[', ']', '|', '?', '/', '`'};

    /**
     * A hash collection of vowels.
     */
    private static final Set<Character> VOWELS = new HashSet<>(Arrays.asList(
            'A', 'E', 'I', 'O', 'U'
    ));

    /**
     * Random instance, used to generate pseudo-random primitive types.
     */
    private static final RandomGen RANDOM = new RandomGen();

    /**
     * A hash collection of domain extensions.
     */
    private static final Set<String> DOMAIN_EXTENSION = new HashSet<>(Arrays.asList(".com", ".org", ".net", ".info", ".io"));

    /**
     * An array that contains words that should be blocked.
     */
    private static final String[] BLOCKED_WORDS = new String[]{
            "www", "(c)om", "etherum", "@cr", "<img=", "<col=", "<shad=", ":tradereq:", ":duelreq:",
            "fuck", "f u c k", "fucker", "bitch", "blowjob", "dildo",
            "penis", "vagina", "pussy", "pu$$sy", "b i t c h", "cunt", "c u n t", "nigger", "nigga",
            "ballsack", "fking", "niggger", "nigggerr", "n i g g e r", "niggar", "neggar", "niggggar",
            "nigggggggar", "nigarrr", "nigarrrr", "nigarrrrrr,"
    };

    @SafeVarargs
    public static <T, O> Map<T, O> buildMap(MapEntry... entries) {
        Map<T, O> map = new HashMap<T, O>();
        for (MapEntry entry : entries) {
            map.put((T) entry.getKey(), (O) entry.getValue());
        }
        return map;
    }

    public static byte[] directionDeltaX = new byte[]{0, 1, 1, 1, 0, -1, -1, -1};
    public static byte[] directionDeltaY = new byte[]{1, 1, 0, -1, -1, -1, 0, 1};
    public static byte[] xlateDirectionToClient = new byte[]{1, 2, 4, 7, 6, 5, 3, 0};
    public static char[] xlateTable = {' ', 'e', 't', 'a', 'o', 'i', 'h', 'n', 's', 'r', 'd', 'l', 'u', 'm', 'w', 'c',
            'y', 'f', 'g', 'p', 'b', 'v', 'k', 'x', 'j', 'q', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            ' ', '!', '?', '.', ',', ':', ';', '(', ')', '-', '&', '*', '\\', '\'', '@', '#', '+', '=', '\243', '$',
            '%', '"', '[', ']'};

	public static final int[] OBJECT_SLOTS = { 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3 };

	public static final boolean objectTypesCollide(int type1, int type2) {
		return OBJECT_SLOTS[type1] == OBJECT_SLOTS[type2];
	}

    private static ZonedDateTime zonedDateTime;
    private static final Random random = new Random();

    public static int getRandomInclusive(int length) {
        return RANDOM.get().nextInt(length + 1);
    }

    public static int getRandomExclusive(int length) {
        return RANDOM.get().nextInt(length);
    }

    public static int randomInt(int... values) {
        int range = values.length;
        int random = (int) (Math.random() * range);
        return values[random];
    }

    public static int random(final int min, final int max) {
        return min + (random.nextInt(max - min + 1));
    }

    public static Optional<String> onRandom(String[] spawnMessages) {
        return Optional.ofNullable(randomString(spawnMessages));
    }

    public static String randomString(String... strings) {
        return random(strings);
    }

    public static <T> T random(T... possibleRewards) {
        return possibleRewards[(int) (Math.random() * possibleRewards.length)];
    }

    public static int random(int range) {
        return (int) (java.lang.Math.random() * (range + 1));
    }

    public static <T> T random(final List<T> availableSales) {
        if (availableSales == null || availableSales.size() <= 0) {
            return null;
        }
        return availableSales.get((int) (Math.random() * availableSales.size()));
    }

    public static boolean randomChance(float chances) {
        return randomFloat(100) < chances;
    }

    public static boolean randomChances(double chances) {
        return randomDouble(100) < chances;
    }


    public static Object roll(Map<Object, Double> map) {
        return new Object();
    }

	public static <T extends Rollable> T rollRarest(T[] elements) {
		var minimumChance = Double.MAX_VALUE;
		var minimumElement = (T)null;
		var roll = Misc.getRandomDouble();
		for (var element : elements) {
			var elementChance = element.getRoll();
			if (roll <= elementChance && elementChance <= minimumChance) {
				minimumElement = element;
				minimumChance = elementChance;
			}
		}
		return minimumElement;
	}
    public static <T extends Rollable> T rollShared(T[] elements) {
		var possibleElements = new ArrayList<T>();
		var roll = getRandomDouble();
		for (var element : elements) {
			if (roll <= element.getRoll()) {
				possibleElements.add(element);
			}
		}
		return possibleElements.isEmpty() ? null : randomTypeOfList(possibleElements);
	}

    private static float randomFloat(float range) {
        return (float) (java.lang.Math.random() * range);
    }

    private static double randomDouble(int range) {
        return (java.lang.Math.random() * range);
    }

    public static int distanceBetween(final Entity a1, final Entity a2) {
        return distanceBetween(a1.getPosition(), a2.getPosition());
    }

    public static int distanceBetween(final Position p1, final Position p2) {
        final int x = (int) Math.pow(p1.getX() - p2.getX(), 2);
        final int y = (int) Math.pow(p1.getY() - p2.getY(), 2);
        return (int) Math.floor(Math.sqrt((x + y)));
    }

    public static int getDistance(int x2, int x3, int y2, int y3) {
        final int x = (int) Math.pow(x2 - x3, 2);
        final int y = (int) Math.pow(y2 - y3, 2);
        return (int) Math.floor(Math.sqrt((x + y)));
    }

    public static int[] clone(int[] array) {
        int[] cloned = new int[array.length];
        for (int i = 0; i < cloned.length; i++) {
            cloned[i] = array[i];
        }
        return cloned;
    }

    public static int[][] clone(int[][] array) {
        int[][] cloned = new int[array.length][];

        for (int x = 0; x < cloned.length; x++) {
            cloned[x] = new int[array[x].length];

            for (int y = 0; y < cloned[x].length; y++) {
                cloned[x][y] = array[x][y];
            }
        }
        return cloned;
    }

	private static final String[] tensNames = {
			"",
			" ten",
			" twenty",
			" thirty",
			" forty",
			" fifty",
			" sixty",
			" seventy",
			" eighty",
			" ninety"
	};

	private static final String[] numNames = {
			"",
			" one",
			" two",
			" three",
			" four",
			" five",
			" six",
			" seven",
			" eight",
			" nine",
			" ten",
			" eleven",
			" twelve",
			" thirteen",
			" fourteen",
			" fifteen",
			" sixteen",
			" seventeen",
			" eighteen",
			" nineteen"
	};

	public static String convertLessThanOneThousand(int number) {
		String soFar;

		if (number % 100 < 20){
			soFar = numNames[number % 100];
			number /= 100;
		}
		else {
			soFar = numNames[number % 10];
			number /= 10;

			soFar = tensNames[number % 10] + soFar;
			number /= 10;
		}
		if (number == 0) return soFar;
		return numNames[number] + " hundred" + soFar;
	}


	public static String convert(long number) {
		// 0 to 999 999 999 999
		if (number == 0) { return "zero"; }

		String snumber = Long.toString(number);

		// pad with "0"
		String mask = "000000000000";
		DecimalFormat df = new DecimalFormat(mask);
		snumber = df.format(number);

		// XXXnnnnnnnnn
		int billions = Integer.parseInt(snumber.substring(0,3));
		// nnnXXXnnnnnn
		int millions  = Integer.parseInt(snumber.substring(3,6));
		// nnnnnnXXXnnn
		int hundredThousands = Integer.parseInt(snumber.substring(6,9));
		// nnnnnnnnnXXX
		int thousands = Integer.parseInt(snumber.substring(9,12));

		String tradBillions;
		switch (billions) {
			case 0:
				tradBillions = "";
				break;
			case 1 :
				tradBillions = convertLessThanOneThousand(billions)
						+ " billion ";
				break;
			default :
				tradBillions = convertLessThanOneThousand(billions)
						+ " billion ";
		}
		String result =  tradBillions;

		String tradMillions;
		switch (millions) {
			case 0:
				tradMillions = "";
				break;
			case 1 :
				tradMillions = convertLessThanOneThousand(millions)
						+ " million ";
				break;
			default :
				tradMillions = convertLessThanOneThousand(millions)
						+ " million ";
		}
		result =  result + tradMillions;

		String tradHundredThousands;
		switch (hundredThousands) {
			case 0:
				tradHundredThousands = "";
				break;
			case 1 :
				tradHundredThousands = "one thousand ";
				break;
			default :
				tradHundredThousands = convertLessThanOneThousand(hundredThousands)
						+ " thousand ";
		}
		result =  result + tradHundredThousands;

		String tradThousand;
		tradThousand = convertLessThanOneThousand(thousands);
		result =  result + tradThousand;

		// remove extra spaces!
		return result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");
	}

    /**
     * Convert long time into String with day-hours-minutes-seconds.
     *
     * @param time time as long
     * @return String with time converted.
     */
    public static String convertTime(long time) {
        int secs = (int) Math.floor(time / 1000);
        int mins = (int) Math.floor(secs / 60);
        secs = secs % 60;
        int hrs = (int) Math.floor(mins / 60);
        mins = mins % 60;
        int days = (int) Math.floor(hrs / 24);
        hrs = hrs % 24;
        if (days > 0) {
            String postFix = hrs > 0 ? (hrs > 1 ? "hours" : "hour") : mins > 0 ? (mins > 1 ? "minutes" : "minute") : secs > 0 ? "seconds" : "second";
            return String.format("%d " + (days > 1 ? "days and " : " day and ") + "%02d:%02d:%02d " + postFix, days, hrs, mins, secs);
        } else if (hrs > 0) {
            return String.format("%02d:%02d:%02d " + (hrs > 1 ? "hours" : "hour"), hrs, mins, secs);
        } else if (mins > 0) {
            return String.format("%02d:%02d " + (mins > 1 ? "minutes" : "minute"), mins, secs);
        } else {
            return String.format("%02d " + (secs > 1 ? "seconds" : "second"), secs);
        }
    }
	public static Random getRandomInclusive() {
		return random;
	}

	public static double getRandomDouble(double length) {
		return RANDOM.get().nextDouble(length);
	}

	public static double getRandomDouble() {
		return RANDOM.get().nextDouble();
	}

	public static int getRandomInt() {
		return RANDOM.get().nextInt();
	}

	public static int inclusive(int min, int max) {
		return RANDOM.inclusive(min, max);
	}

	public static String getCurrentServerTime() {
		zonedDateTime = ZonedDateTime.now();
		int hour = zonedDateTime.getHour();
		String hourPrefix = hour < 10 ? "0" + hour + "" : "" + hour + "";
		int minute = zonedDateTime.getMinute();
		String minutePrefix = minute < 10 ? "0" + minute + "" : "" + minute + "";
		return "" + hourPrefix + ":" + minutePrefix + "";
	}

	public static String getTimePlayed(long totalPlayTime) {
		final int sec = (int) (totalPlayTime / 1000), h = sec / 3600, m = sec / 60 % 60, s = sec % 60;
		return (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m) + ":" + (s < 10 ? "0" + s : s);
	}

	public static String getHoursPlayed(long totalPlayTime) {
		final int sec = (int) (totalPlayTime / 1000), h = sec / 3600;
		return (h < 10 ? "0" + h : h) + "h";
	}

	public static int getMinutesPassed(long t) {
		int seconds = (int) ((t / 1000) % 60);
		int minutes = (int) (((t - seconds) / 1000) / 60);
		return minutes;
	}

	public static Item[] concat(Item[] a, Item[] b) {
		int aLen = a.length;
		int bLen = b.length;
		Item[] c = new Item[aLen + bLen];
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);
		return c;
	}
	
	public static ArrayList<Item> concat(List<Item> a, List<Item> b) {
		ArrayList<Item> c = new ArrayList<Item>(a.size() + b.size());
		
		for (Item itemA : a) {
			c.add(itemA);
		}

		for (Item itemB : b) {
			c.add(itemB);
		}
		
		return c;
	}

	public static ItemContainer concat(ItemContainer a, ItemContainer b) {
		ItemContainer c = new ItemContainer() {
			@Override
			public StackType stackType() {
				return a.stackType();
			}

			@Override
			public ItemContainer refreshItems() {
				return this;
			}

			@Override
			public ItemContainer full() {
				return this;
			}

			@Override
			public int capacity() {
				return a.capacity() + b.capacity();
			}
		};

		for (Item itemA : a.getValidItems()) {
			c.add(itemA);
		}

		for (Item itemB : b.getValidItems()) {
			c.add(itemB);
		}

		return c;
	}

	public static Player getCloseRandomPlayer(List<Player> plrs) {
		int index = Misc.getRandomInclusive(plrs.size() - 1);
		if (index > 0)
			return plrs.get(index);
		return null;
	}

	public static int direction(int srcX, int srcY, int x, int y) {
		final double dx = (double) x - srcX, dy = (double) y - srcY;
		double angle = Math.atan(dy / dx);
		angle = Math.toDegrees(angle);
		if (Double.isNaN(angle))
			return -1;
		if (Math.signum(dx) < 0)
			angle += 180.0;
		return (int) (((90 - angle) / 22.5 + 16) % 16);
		/*
		 * int changeX = x - srcX; int changeY = y - srcY; for (int j = 0; j <
		 * directionDeltaX.length; j++) { if (changeX == directionDeltaX[j] &&
		 * changeY == directionDeltaY[j]) return j; } return -1;
		 */
	}

	public static String uppercaseWords(String string) {
		string.replaceAll("_", " ");
		String[] s = string.split("_");
		String word = "";
		for (String w : s) {
			word += " " + ucFirst(w);
		}
		return word;
	}

	public static String ucFirst(String str) {
		str = str.toLowerCase();
		if (str.length() > 1) {
			str = str.substring(0, 1).toUpperCase() + str.substring(1);
		} else {
			return str.toUpperCase();
		}
		return str;
	}

	public static String format(int num) {
		return NumberFormat.getInstance().format(num);
	}

	public static String formatWithAbbreviation(long num) {
    	String prefix = "@yel@";
    	String suffix = "</col>";
    	String appendix = "";
    	double remainder = 0.0;
		if(num > 1_000_000_000_000L){
			appendix = "T";
			remainder = ((double) num % 1_000_000_000_000L) / 1_000_000_000_000L;
			num /= 1_000_000_000_000L;
		} else if(num > 1_000_000_000){
    		appendix = "B";
            remainder =  ((double) num % 1_000_000_000) / 1_000_000_000;
    		num /= 1_000_000_000;
		} else if(num > 1_000_000){
			appendix = "M";
            remainder = ((double) num % 1_000_000) / 1_000_000;
			num /= 1_000_000;
		} else if(num > 1_000){
			appendix = "K";
            remainder =  ((double) num % 1_000) / 1_000;
			num /= 1_000;
		}
//        NumberFormat.getNumberInstance().setMaximumFractionDigits(3);
//		NumberFormat.getNumberInstance().setMinimumFractionDigits(1);
        NumberFormat.getNumberInstance().setRoundingMode(RoundingMode.UNNECESSARY);
		return prefix + NumberFormat.getNumberInstance().format((double) num + remainder) + appendix + suffix;
	}

	public static String formatWithAbbreviationCustomPrefix(long num, String prefix) {
		String suffix = "</col>";
		String appendix = "";
		double remainder = 0.0;
		DecimalFormat numberFormat = new DecimalFormat("#.00");
		if(num > 1_000_000_000_000L){
			appendix = "T";
			remainder = ((double) num % 1_000_000_000_000L) / 1_000_000_000_000L;
			num /= 1_000_000_000_000L;
		} else if(num > 1_000_000_000){
			appendix = "B";
			remainder =  ((double) num % 1_000_000_000) / 1_000_000_000;
			num /= 1_000_000_000;
		} else if(num > 1_000_000){
			appendix = "M";
			remainder = ((float) num % 1_000_000) / 1_000_000;
			num /= 1_000_000;
		} else if(num > 1_000){
			appendix = "K";
			remainder =  ((int) num % 1_000) / 1_000;
			num /= 1_000;
		}
//        NumberFormat.getNumberInstance().setMaximumFractionDigits(3);
//		NumberFormat.getNumberInstance().setMaximumFractionDigits(1);
//		NumberFormat.getNumberInstance().setMinimumFractionDigits(1);
		NumberFormat.getNumberInstance().setRoundingMode(RoundingMode.UNNECESSARY);
		if (prefix == null) {
			return NumberFormat.getNumberInstance().format((double) num + remainder) + appendix + suffix;
		}
		return prefix + NumberFormat.getNumberInstance().format((double) num + remainder) + appendix + suffix;
	}

	public static String formatWithAbbreviation2(long num) {
		String prefix = "@dre@";
		String suffix = "</col>";
		String appendix = "";
		double remainder = 0.0;
		if(num > 1_000_000_000_000L){
			appendix = "T";
			remainder = ((double) num % 1_000_000_000_000L) / 1_000_000_000_000L;
			num /= 1_000_000_000_000L;
		} else if(num > 1_000_000_000){
			appendix = "B";
			remainder =  ((double) num % 1_000_000_000) / 1_000_000_000;
			num /= 1_000_000_000;
		} else if(num > 1_000_000){
			appendix = "M";
			remainder = ((double) num % 1_000_000) / 1_000_000;
			num /= 1_000_000;
		} else if(num > 1_000){
			appendix = "K";
			remainder =  ((double) num % 1_000) / 1_000;
			num /= 1_000;
		}
		NumberFormat.getNumberInstance().setRoundingMode(RoundingMode.UNNECESSARY);
		return prefix + NumberFormat.getNumberInstance().format((double) num + remainder) + appendix + suffix;
	}

	public static String formatText(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (i == 0) {
				s = String.format("%s%s", Character.toUpperCase(s.charAt(0)), s.substring(1));
			}
			if (!Character.isLetterOrDigit(s.charAt(i))) {
				if (i + 1 < s.length()) {
					s = String.format("%s%s%s", s.subSequence(0, i + 1), Character.toUpperCase(s.charAt(i + 1)),
							s.substring(i + 2));
				}
			}
		}
		return s.replace("_", " ");
	}
	public static String splitText(String s, int size) {
		String string = "";
		int length = 0;
		for(String str : s.split(" ")) {
			string += str+ " ";
			length += str.length();
			if(length >= size) {
				string +="\\n";
				length = 0;
			}
		}
		return string;
	}
	public static String getTotalAmount(int j) {
		if (j >= 10000 && j < 1000000) {
			return j / 1000 + "K";
		} else if (j >= 1000000 && j <= Integer.MAX_VALUE) {
			return j / 1000000 + "M";
		} else {
			return "" + j;
		}
	}

	public static String formatPlayerName(String str) {
		return formatText(str);
	}

	public static String insertCommasToNumber(String number) {
		return number.length() < 4 ? number
				: insertCommasToNumber(number.substring(0, number.length() - 3)) + ","
						+ number.substring(number.length() - 3, number.length());
	}
	
	public static String insertCommasToNumber(int number) {
		return insertCommasToNumber(number+"");
	}

	// assumes no empty slots in container
	public static int getContainerRows(int itemsAmt, int columns) {
		int rows = 0;
		for (int i = 0; i < itemsAmt; i += columns) {
			if (i % columns == 0) {
				rows++;
			}
		}
		return rows;
	}

	// assumes no empty slots in container
	public static int getContainerScrollMax(int itemsAmt, int columns, int spritePaddingY, int offsetY, int height, boolean lastRowPadding) {
		int rows = getContainerRows(itemsAmt, columns);
		return Math.max((32 + spritePaddingY) * rows - (!lastRowPadding ? spritePaddingY : 0) + (offsetY / 2), height);
	}

	public static ItemContainer convertToItemContainer(List<Item> items, StackType stackType) {
		ItemContainer container = new ItemContainer() {
			@Override
			public StackType stackType() {
				return stackType;
			}

			@Override
			public ItemContainer refreshItems() {
				return this;
			}

			@Override
			public ItemContainer full() {
				return this;
			}

			@Override
			public int capacity() {
				return items.size();
			}
		};

		for (Item item : items) {
			container.add(item);
		}

		return container;
	}

	public static ItemContainer convertToItemContainer(Item[] items, StackType stackType) {
		ItemContainer container = new ItemContainer() {
			@Override
			public StackType stackType() {
				return stackType;
			}

			@Override
			public ItemContainer refreshItems() {
				return this;
			}

			@Override
			public ItemContainer full() {
				return this;
			}

			@Override
			public int capacity() {
				return items.length;
			}
		};

		for (Item item : items) {
			container.add(item);
		}

		return container;
	}

	public static String textUnpack(byte packedData[], int size) {
		byte[] decodeBuf = new byte[4096];
		int idx = 0, highNibble = -1;
		for (int i = 0; i < size * 2; i++) {
			int val = packedData[i / 2] >> (4 - 4 * (i % 2)) & 0xf;
			if (highNibble == -1) {
				if (val < 13)
					decodeBuf[idx++] = (byte) xlateTable[val];
				else
					highNibble = val;
			} else {
				decodeBuf[idx++] = (byte) xlateTable[((highNibble << 4) + val) - 195];
				highNibble = -1;
			}
		}

		return new String(decodeBuf, 0, idx);
	}

	/**
	 * Packs text.
	 *
	 * @param packedData
	 *            The destination of the packed text.
	 * @param text
	 *            The unpacked text.
	 */
	public static void textPack(byte packedData[], String text) {
		if (text.length() > 80) {
			text = text.substring(0, 80);
		}
		text = text.toLowerCase();
		int carryOverNibble = -1;
		int ofs = 0;
		for (int idx = 0; idx < text.length(); idx++) {
			char c = text.charAt(idx);
			int tableIdx = 0;
			for (int i = 0; i < xlateTable.length; i++) {
				if (c == (byte) xlateTable[i]) {
					tableIdx = i;
					break;
				}
			}
			if (tableIdx > 12) {
				tableIdx += 195;
			}
			if (carryOverNibble == -1) {
				if (tableIdx < 13) {
					carryOverNibble = tableIdx;
				} else {
					packedData[ofs++] = (byte) tableIdx;
				}
			} else if (tableIdx < 13) {
				packedData[ofs++] = (byte) ((carryOverNibble << 4) + tableIdx);
				carryOverNibble = -1;
			} else {
				packedData[ofs++] = (byte) ((carryOverNibble << 4) + (tableIdx >> 4));
				carryOverNibble = tableIdx & 0xf;
			}
		}
		if (carryOverNibble != -1) {
			packedData[ofs++] = (byte) (carryOverNibble << 4);
		}
	}

	public static String anOrA(String s) {
		s = s.toLowerCase();
		if (s.equalsIgnoreCase("anchovies") || s.equalsIgnoreCase("soft clay") || s.equalsIgnoreCase("cheese")
				|| s.equalsIgnoreCase("ball of wool") || s.equalsIgnoreCase("spice")
				|| s.equalsIgnoreCase("steel nails") || s.equalsIgnoreCase("snape grass") || s.equalsIgnoreCase("coal"))
			return "some";
		if (s.startsWith("a") || s.startsWith("e") || s.startsWith("i") || s.startsWith("o") || s.startsWith("u"))
			return "an";
		return "a";
	}

	@SuppressWarnings("rawtypes")
	public static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		ArrayList<Class> classes = new ArrayList<Class>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes.toArray(new Class[classes.size()]);
	}

	@SuppressWarnings("rawtypes")
	private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(
						Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
			}
		}
		return classes;
	}

	public static String removeSpaces(String s) {
		return s.replace(" ", "");
	}

	public static int getMinutesElapsed(int minute, int hour, int day, int year) {
		Calendar i = Calendar.getInstance();

		if (i.get(1) == year) {
			if (i.get(6) == day) {
				if (hour == i.get(11)) {
					return i.get(12) - minute;
				}
				return (i.get(11) - hour) * 60 + (59 - i.get(12));
			}

			int ela = (i.get(6) - day) * 24 * 60 * 60;
			return ela > 2147483647 ? 2147483647 : ela;
		}

		int ela = getElapsed(day, year) * 24 * 60 * 60;

		return ela > 2147483647 ? 2147483647 : ela;
	}

	public static int getDayOfYear() {
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int days = 0;
		int[] daysOfTheMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		if ((year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0)) {
			daysOfTheMonth[1] = 29;
		}
		days += c.get(Calendar.DAY_OF_MONTH);
		for (int i = 0; i < daysOfTheMonth.length; i++) {
			if (i < month) {
				days += daysOfTheMonth[i];
			}
		}
		return days;
	}

	public static int getYear() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.YEAR);
	}

    public static boolean elapsed(Long time, Long difference) {
        return System.currentTimeMillis() - time > difference;
    }
    public static boolean elapsed(Long time, int difference) {
        return System.currentTimeMillis() - time > difference;
    }
    
	public static int getElapsed(int day, int year) {
		if (year < 2013) {
			return 0;
		}

		int elapsed = 0;
		int currentYear = Misc.getYear();
		int currentDay = Misc.getDayOfYear();

		if (currentYear == year) {
			elapsed = currentDay - day;
		} else {
			elapsed = currentDay;

			for (int i = 1; i < 5; i++) {
				if (currentYear - i == year) {
					elapsed += 365 - day;
					break;
				} else {
					elapsed += 365;
				}
			}
		}

		return elapsed;
	}

	public static boolean isWeekend() {
		int day = Calendar.getInstance().get(7);
		return (day == 1) || (day == 6) || (day == 7);
	}

	public static byte[] readFile(File s) {
		try {
			FileInputStream fis = new FileInputStream(s);
			FileChannel fc = fis.getChannel();
			ByteBuffer buf = ByteBuffer.allocate((int) fc.size());
			fc.read(buf);
			buf.flip();
			fis.close();
			return buf.array();
		} catch (Exception e) {
			System.out.println("FILE : " + s.getName() + " missing.");
			return null;
		}
	}

	public static <T> T randomTypeOfList(List<T> list) {
		return list.get(RANDOM.get().nextInt(list.size()));
	}

	public static int randomInclusive(int min, int max) {
		return Math.min(min, max) + RANDOM.get().nextInt(Math.max(min, max) - Math.min(min, max) + 1);
	}

	public static RandomGen getRANDOM() {
		return RANDOM;
	}

	public static byte[] getBuffer(File f) throws Exception {
		if (!f.exists())
			return null;
		byte[] buffer = new byte[(int) f.length()];
		DataInputStream dis = new DataInputStream(new FileInputStream(f));
		dis.readFully(buffer);
		dis.close();
		byte[] gzipInputBuffer = new byte[999999];
		int bufferlength = 0;
		GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(buffer));
		do {
			if (bufferlength == gzipInputBuffer.length) {
				System.out.println("Error inflating data.\nGZIP buffer overflow.");
				break;
			}
			int readByte = gzip.read(gzipInputBuffer, bufferlength, gzipInputBuffer.length - bufferlength);
			if (readByte == -1)
				break;
			bufferlength += readByte;
		} while (true);
		byte[] inflated = new byte[bufferlength];
		System.arraycopy(gzipInputBuffer, 0, inflated, 0, bufferlength);
		buffer = inflated;
		if (buffer.length < 10)
			return null;
		return buffer;
	}

	public static int getTimeLeft(long start, int timeAmount, TimeUnit timeUnit) {
		start -= timeUnit.toMillis(timeAmount);
		long elapsed = System.currentTimeMillis() - start;
		int toReturn = timeUnit == TimeUnit.SECONDS ? (int) ((elapsed / 1000) % 60) - timeAmount
				: (int) ((elapsed / 1000) / 60) - timeAmount;
		if (toReturn <= 0)
			toReturn = 1;
		return timeAmount - toReturn;
	}

	/**
	 * Gets the formatted time played.
	 *
	 * @return The time played formatted as a string.
	 */
	public static String getFormattedPlayTime(Player player) {
		long different = System.currentTimeMillis() - player.getCreationDate().getTime();

		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;

		long elapsedDays = different / daysInMilli;
		different = different % daysInMilli;

		long elapsedHours = different / hoursInMilli;
		different = different % hoursInMilli;

		long elapsedMinutes = different / minutesInMilli;
		different = different % minutesInMilli;

		long elapsedSeconds = different / secondsInMilli;

		/*
		 * long days = (long) elapsedJoinDate / 86400; // 86,400 long
		 * daysRemainder = (long) elapsedJoinDate - (days * 86400); long hours =
		 * (long) daysRemainder / 3600; // 3,600 long hoursRemainder = (long)
		 * daysRemainder - (hours * 3600); long minutes = (long) hoursRemainder
		 * / 60; // 60 long seconds = (long) hoursRemainder - (minutes * 60); //
		 * remainder
		 */

		return elapsedDays + " day(s) : " + elapsedHours + " hour(s) : " + elapsedMinutes + " minute(s) : "
				+ elapsedSeconds + " second(s)";

	}

	/**
	 * Converts an array of bytes to an integer.
	 *
	 * @param data
	 *            the array of bytes.
	 * @return the newly constructed integer.
	 */
	public static int hexToInt(byte[] data) {
		int value = 0;
		int n = 1000;
		for (int i = 0; i < data.length; i++) {
			int num = (data[i] & 0xFF) * n;
			value += num;
			if (n > 1) {
				n = n / 1000;
			}
		}
		return value;
	}

	public static Position delta(Position a, Position b) {
		return new Position(b.getX() - a.getX(), b.getY() - a.getY());
	}

	/**
	 * Picks a random element out of any array type.
	 *
	 * @param array
	 *            the array to pick the element from.
	 * @return the element chosen.
	 */
	public static <T> T randomElement(T[] array) {
		return array[(int) (RANDOM.get().nextDouble() * array.length)];
	}

	/**
	 * Picks a random element out of any list type.
	 *
	 * @param list
	 *            the list to pick the element from.
	 * @return the element chosen.
	 */
	public static <T> T randomElement(List<T> list) {
		return list.get((int) (RANDOM.get().nextDouble() * list.size()));
	}

	public static <T> T randomElement(ArrayList<T> list) {
		return list.get((int) (RANDOM.get().nextInt() * (list.size())));
	}
	
	/**
	 * Filters characters and symbols from the word.
	 * 
	 * @param word
	 *            The word.
	 * @return The filtered word.
	 */
	public static String replaceHiddenSynonyms(String word) {
		word = word.toLowerCase().replaceAll("\\s+", " ");
		word = word.replaceAll(",", ".");
		word = word.replaceAll("_", "");
		word = word.replaceAll(" ", "");
		word = word.replaceAll("[.]\\s+", ".");
		word = word.replaceAll("0", "o");
		word = word.replaceAll("1", "i");
		word = word.replaceAll("!", "i");
		word = word.replaceAll("3", "e");
		word = word.replaceAll("5", "s");
		word = word.replaceAll("-", "");
		
		return word;
	}

	/**
	 * Checks if the specified word is blocked.
	 *
	 *
	 * TODO: improve performance and accuracy (e.g. ", information" is flagged as ".info
	 * @param text
	 *            The word.
	 * @return <code>true</code> if the word is blocked.
	 */
	public static boolean blockedWord(String text) {

		text = replaceHiddenSynonyms(text);
		
		for (String bw : BLOCKED_WORDS) {
			if (text.contains(bw)) {
				return true;
			}
		}
		
		for (String ext : DOMAIN_EXTENSION) {
			if (text.contains(ext) && !text.contains(".come")) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Capitalized all words split by a space char.
	 *
	 * @param string
	 *            The string to format.
	 */
	public static String capitalizeWords(String string) {
		return WordUtils.capitalize(string);
	}

	/**
	 * Capitalizes the first letter in said string.
	 *
	 * @param name
	 *            The string to capitalize.
	 * @return The string with the first char capitalized.
	 */
	public static String capitalize(String name) {
		if (name.length() < 1)
			return "";
		StringBuilder builder = new StringBuilder(name.length());
		char first = Character.toUpperCase(name.charAt(0));
		builder.append(first).append(name.toLowerCase().substring(1));
		return builder.toString();
	}

	/**
	 * Formats the name by checking if it starts with a vowel.
	 *
	 * @param name
	 *            The string to format.
	 */
	public static String getVowelFormat(String name) {
		char letter = name.charAt(0);
		boolean vowel = letter == 'a' || letter == 'e' || letter == 'i' || letter == 'o' || letter == 'u';
		String other = vowel ? "an" : "a";
		return other + " " + name;
	}

	/**
	 * Checks if a name is valid according to the
	 * {@code VALID_PLAYER_CHARACTERS} array.
	 *
	 * @param name
	 *            The name to check.
	 * @return The name is valid.
	 */
	public static boolean isValidName(String name) {
		return formatNameForProtocol(name).matches("[a-z0-9_]+");
	}

	/**
	 * Converts a name to a long value.
	 *
	 * @param string
	 *            The string to convert to long.
	 * @return The long value of the string.
	 */
	public static long stringToLong(String string) {
		long l = 0L;
		for (int i = 0; i < string.length() && i < 12; i++) {
			char c = string.charAt(i);
			l *= 37L;
			if (c >= 'A' && c <= 'Z')
				l += (1 + c) - 65;
			else if (c >= 'a' && c <= 'z')
				l += (1 + c) - 97;
			else if (c >= '0' && c <= '9')
				l += (27 + c) - 48;
		}
		while (l % 37L == 0L && l != 0L)
			l /= 37L;
		return l;
	}

	/**
	 * Converts a long to a string.
	 *
	 * @param encodedName
	 *            The long value to convert to a string.
	 * @return The string value.
	 */
	public static String longToString(long encodedName) {
		int i = 0;
		char[] ac = new char[12];
		while (encodedName != 0L) {
			long l1 = encodedName;
			encodedName /= 37L;
			final int charIndex = (int) (l1 - encodedName * 37L);
			if(charIndex < 0 || charIndex > VALID_CHARACTERS.length)
				return "";
			final int nameCharIndex = 11 - i++;
			if(nameCharIndex < 0 || nameCharIndex > ac.length)
				return "";
			ac[nameCharIndex] = VALID_CHARACTERS[charIndex];
		}
		return new String(ac, 12 - i, i);
	}

	public static byte[] getBuffer(String file) {
		try {
			java.io.File f = new java.io.File(file);
			if (!f.exists())
				return null;
			byte[] buffer = new byte[(int) f.length()];
			java.io.DataInputStream dis = new java.io.DataInputStream(new java.io.FileInputStream(f));
			dis.readFully(buffer);
			dis.close();
			return buffer;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Formats a name for use in the protocol.
	 *
	 * @param name
	 *            The name to format.
	 * @return The formatted name.
	 */
	public static String formatNameForProtocol(String name) {
		return name.toLowerCase().replace(" ", "_");
	}

	/**
	 * Formats a name for in-game display.
	 *
	 * @param name
	 *            The name to format.
	 * @return The formatted name.
	 */
	public static String formatName(String name) {
		return fixName(name.replace(" ", "_"));
	}

	/**
	 * Formats a player's name, i.e sets upper case letters after a space.
	 *
	 * @param name
	 *            The name to format.
	 * @return The formatted name.
	 */
	private static String fixName(String name) {
		if (name.length() > 0) {
			final char ac[] = name.toCharArray();
			for (int j = 0; j < ac.length; j++)
				if (ac[j] == '_') {
					ac[j] = ' ';
					if ((j + 1 < ac.length) && (ac[j + 1] >= 'a') && (ac[j + 1] <= 'z')) {
						ac[j + 1] = (char) ((ac[j + 1] + 65) - 97);
					}
				}

			if ((ac[0] >= 'a') && (ac[0] <= 'z')) {
				ac[0] = (char) ((ac[0] + 65) - 97);
			}
			return new String(ac);
		} else {
			return name;
		}
	}

	/**
	 * Hashes a {@code String} using Jagex's algorithm, this method should be
	 * used to convert actual names to hashed names to lookup files within the
	 * {@link CacheLoader}.
	 *
	 * @param string
	 *            The string to hash.
	 * @return The hashed string.
	 */
	public static int hash(String string) {
		return _hash(string.toUpperCase());
	}

	/**
	 * Hashes a {@code String} using Jagex's algorithm, this method should be
	 * used to convert actual names to hashed names to lookup files within the
	 * {@link CacheLoader}.
	 * <p>
	 * <p>
	 * This method should <i>only</i> be used internally, it is marked
	 * deprecated as it does not properly hash the specified {@code String}. The
	 * functionality of this method is used to create a proper {@code String}
	 * {@link #hash(String) <i>hashing method</i>}. The scope of this method has
	 * been marked as {@code private} to prevent confusion.
	 * </p>
	 *
	 * @param string
	 *            The string to hash.
	 * @return The hashed string.
	 * @deprecated This method should only be used internally as it does not
	 *             correctly hash the specified {@code String}. See the note
	 *             below for more information.
	 */
	@Deprecated
	private static int _hash(String string) {
		return IntStream.range(0, string.length()).reduce(0, (hash, index) -> hash * 61 + string.charAt(index) - 32);
	}

	public static int randomElement(int[] array) {
		return array[getRandomInclusive(array.length - 1)];
	}
	
	/**
	 * Picks a random element out of an enum.
	 * 
	 * @param clazz
	 *            the enum to pick the element from.
	 * @return the element
	 */
	public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        return clazz.getEnumConstants()[RANDOM.get().nextInt(clazz.getEnumConstants().length)];
    }

	public static int clamp(int value, int min, int max) {
		if (value < min) {
			return min;
		}
		if (value > max) {
			return max;
		}
		return value;
	}

	public static long getTimeGMT() {
		TimeZone currentTimeZone = TimeZone.getDefault();
		return System.currentTimeMillis() - currentTimeZone.getRawOffset();
	}

	public static final int[][] getCoordOffsetsNear(int size) {
		int[] xs = new int[4 + (4 * size)];
		int[] xy = new int[xs.length];
		xs[0] = -size;
		xy[0] = 1;
		xs[1] = 1;
		xy[1] = 1;
		xs[2] = -size;
		xy[2] = -size;
		xs[3] = 1;
		xy[2] = -size;
		for (int fakeSize = size; fakeSize > 0; fakeSize--) {
			xs[(4 + ((size - fakeSize) * 4))] = -fakeSize + 1;
			xy[(4 + ((size - fakeSize) * 4))] = 1;
			xs[(4 + ((size - fakeSize) * 4)) + 1] = -size;
			xy[(4 + ((size - fakeSize) * 4)) + 1] = -fakeSize + 1;
			xs[(4 + ((size - fakeSize) * 4)) + 2] = 1;
			xy[(4 + ((size - fakeSize) * 4)) + 2] = -fakeSize + 1;
			xs[(4 + ((size - fakeSize) * 4)) + 3] = -fakeSize + 1;
			xy[(4 + ((size - fakeSize) * 4)) + 3] = -size;
		}
		return new int[][] { xs, xy };
	}

	public static Map<TimeUnit, Long> computeDiff(Date date1, Date date2) {
		long diffInMilliSeconds = date2.getTime() - date1.getTime();
		List<TimeUnit> units = new ArrayList<TimeUnit>(EnumSet.allOf(TimeUnit.class));
		Collections.reverse(units);
		Map<TimeUnit, Long> result = new LinkedHashMap<TimeUnit, Long>();
		long milliSecondsRest = diffInMilliSeconds;
		for (TimeUnit unit : units) {
			long diff = unit.convert(milliSecondsRest, TimeUnit.MILLISECONDS);
			long diffInMilliSecondsForUnit = unit.toMillis(diff);
			milliSecondsRest = milliSecondsRest - diffInMilliSecondsForUnit;
			result.put(unit, diff);
		}
		return result;
	}


	/*public static String getTimeElapsed(long different, boolean flag) {
		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;

		long days = different / daysInMilli;
		different = different % daysInMilli;

		long hours = different / hoursInMilli;
		different = different % hoursInMilli;

		long minutes = different / minutesInMilli;
		different = different % minutesInMilli;

		long seconds = different / secondsInMilli;

		if (days > 0 && hours > 0) {
			return days + " days, " + hours + "h, " + minutes + "m, " + seconds + "s";
		}
		if (days > 0 && hours == 0) {
			return days + " days, " + minutes + "m, " + seconds + "s";
		}
		if (hours > 0) {
			return hours + "h, " + minutes + "m, " + seconds + "s";
		}
		if (minutes > 0) {
			return minutes + " min, " + seconds + " sec";
		}
		if (seconds > 0) {
			return seconds + " sec";
		}
		return "Under 1 second";
	}*/

	public static String getTimeElapsed(long time, boolean flag) {
		long different = System.currentTimeMillis() - time;
		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;

		long days = different / daysInMilli;
		different = different % daysInMilli;

		long hours = different / hoursInMilli;
		different = different % hoursInMilli;

		long minutes = different / minutesInMilli;
		different = different % minutesInMilli;

		long seconds = different / secondsInMilli;

		if (days > 0 && hours > 0) {
			return days + " days, " + hours + "h, " + minutes + (flag ? "m" : "m, " + seconds + "s");
		}
		if (days > 0 && hours == 0) {
			return days + " days, " + minutes + (flag ? "m" : "m, " + seconds + "s");
		}
		if (hours > 0) {
			return hours + "h, " + minutes + (flag ? "m" : "m, " + seconds + "s");
		}
		if (minutes > 0) {
			return minutes + " min, " + seconds + " sec";
		}
		if (seconds > 0) {
			return seconds + " sec";
		}
		return "Just Started!";
	}

	public static String getTimeElapsed(long time) {
		long different = System.currentTimeMillis() - time;
		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;

		long days = different / daysInMilli;
		different = different % daysInMilli;

		long hours = different / hoursInMilli;
		different = different % hoursInMilli;

		long minutes = different / minutesInMilli;
		different = different % minutesInMilli;

		long seconds = different / secondsInMilli;

		if (days > 0 && hours > 0) {
			return days + " days, " + hours + "h, " + minutes + "m, " + seconds + "s";
		}
		if (days > 0 && hours == 0) {
			return days + " days, " + minutes + "m, " + seconds + "s";
		}
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
	public static String sendTimeFormat(long time) {
		long different = time;
		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;

		long days = different / daysInMilli;
		different = different % daysInMilli;

		long hours = different / hoursInMilli;
		different = different % hoursInMilli;

		long minutes = different / minutesInMilli;
		different = different % minutesInMilli;

		long seconds = different / secondsInMilli;

		if (days > 0 && hours > 0) {
			return days + " days, " + hours + "h, " + minutes + "m, " + seconds + "s";
		}
		if (days > 0 && hours == 0) {
			return days + " days, " + minutes + "m, " + seconds + "s";
		}
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

	public static int getManhattanDistance(int x, int y, int x2, int y2) {
		return Math.abs(x - x2) + Math.abs(y - y2);
	}
	
	public static int getDistance(Position pos1, Position pos2) {
		return Math.abs(pos1.getX() - pos2.getX()) + Math.abs(pos1.getY() - pos2.getY());
	}

	public static boolean startsWithVowel(String word) {
		return VOWELS.contains(Character.toUpperCase(word.charAt(0)));
	}
	
	public static String getAOrAn(String s) {
		return startsWithVowel(s) ? "an" : "a";
	}

	public static String getRankIcon(Player player) {
		switch (player.getRights()) {
			case ADMINISTRATOR:
				return "<img=742> ";
			case DEVELOPER:
				return "<img=744> ";
			case CO_OWNER:
				return "<img=788> ";
			case GLOBAL_MODERATOR:
				return "<img=741> ";
			case MODERATOR:
				return "<img=740> ";
			case NONE:
				return "";
			case OWNER:
				return "<img=743> ";
			case DICER:
				return "<img=770> ";
			case YOUTUBER:
				return "<img=748> ";
			case SERVER_SUPPORTER:
				return "<img=785> ";
			case WIKI_EDITOR:
				return "<img=796> ";
			case DESIGNER:
				return "<img=751> ";
			case BRONZE_MEMBER:
				return "<img=1025> ";
			case RUBY_MEMBER:
				return "<img=745> ";
			case TOPAZ_MEMBER:
				return "<img=746> ";
			case AMETHYST_MEMBER:
				return "<img=747> ";
			case LEGENDARY_MEMBER:
				return "<img=1026> ";
			case PLATINUM_MEMBER:
				return "<img=1027> ";
			case TITANIUM_MEMBER:
				return "<img=1227> ";
			case DIAMOND_MEMBER:
				return "<img=1228> ";
			case RESPECTED:
				return "<img=943> ";
			case EX_STAFF:
				return "<img=942> ";
			case VETERAN:
				return "<img=941> ";
			case EVENT_HOST:
				return "<img=940> ";
			case MIDDLEMAN:
				return "<img=939> ";
			case CAMPAIGN_DEVELOPER:
				return "<img=1028> ";
			case CONTRIBUTOR:
				return "<img=1229> ";
			case MOTM:
				return "<img=1241> ";
			default:
				break;
		}
		return "";
	}

	public static void shuffleArray(int[] array) {
		int index, temp;
		for (int i = array.length - 1; i > 0; i--) {
			index = random.nextInt(i + 1);
			temp = array[index];
			array[index] = array[i];
			array[i] = temp;
		}
	}
	
	/**
	 * Checks if the sum of two integers is an integer.
	 * 
	 * @param base
	 *            The base value.
	 * @param value
	 *            The value to be added.
	 * @return <code>true</code> if can
	 */
	public static boolean canAddInteger(int base, int value) {
		long total = (long) base + (long) value;

		return total <= Integer.MAX_VALUE && total > 0;
	}

	/**
	 * Gets the amount that can be added to the base integer.
	 * 
	 * @param base
	 *            The base value.
	 * @param value
	 *            The value.
	 * @return The amount.
	 */
	public static int getAddInteger(int base, int value) {
		if (base >= Integer.MAX_VALUE) {
			return 0;
		}
		
		long total = (long) base + (long) value;
		
		if (total < 1) {
			return 0;
		}
		
		if (total > Integer.MAX_VALUE) {
			return Integer.MAX_VALUE - base;
		}

		return value;
	}

	public static int getDirection(Position p1, Position p2) {
		return calculateDirection(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}

	public static int calculateDirection(int x1, int y1, int x2, int y2) {
		if (x1 == x2) {
			if (y1 == y2) {
				return -1;
			}

			if (y1 < y2) {
				return 0;
			}

			return 4;
		} else if (y1 == y2) {
			if (x1 < x2) {
				return 2;
			}

			return 6;
		} else if (x1 < x2) {
			if (y1 < y2) {
				return 1;
			}

			return 3;
		} else if (y1 < y2) {
			return 7;
		} else if (x1 > x2) {
			return 5;
		} else {
			throw new RuntimeException();
		}

	}
	public static int maxDelta(Position position, Position position2) {
		int deltaX = abs(position.getX() - position2.getX());
		int deltaY = abs(position.getY() - position2.getY());
		return deltaX > deltaY ? deltaX : deltaY;
	}
	public static int abs(int a) {
		return (a < 0) ? -a : a;
	}

    public static String friendlyTimeDiff(long timeDifferenceMilliseconds) {
        float diffSeconds = timeDifferenceMilliseconds / 1000f;
        float diffMinutes = timeDifferenceMilliseconds / (60f * 1000f);
        float diffHours = timeDifferenceMilliseconds / (60f * 60f * 1000f);
        float diffDays = timeDifferenceMilliseconds / (60f * 60f * 1000f * 24f);
        long diffWeeks = timeDifferenceMilliseconds / (60 * 60 * 1000 * 24 * 7);
        long diffMonths = (long) (timeDifferenceMilliseconds / (60 * 60 * 1000 * 24 * 30.41666666));
        long diffYears = timeDifferenceMilliseconds / ((long)60 * 60 * 1000 * 24 * 365);

        if (diffSeconds < 1) {
            return "less than a second";
        } else if (diffMinutes < 1) {
            return String.format("%.2f",diffSeconds) + " seconds";
        } else if (diffHours < 1) {
            return String.format("%.2f",diffMinutes) + " minutes";
        } else if (diffDays < 1) {
            return String.format("%.2f",diffHours) + " hours";
        } else if (diffWeeks < 1) {
            return String.format("%.2f",diffDays) + " days";
        } else if (diffMonths < 1) {
            return diffWeeks + " weeks";
        } else if (diffYears < 1) {
            return diffMonths + " months";
        } else {
            return diffYears + " years";
        }
    }

	public static boolean randomBoolean() {
		return randomChance(50.0F);
	}

    public static Direction getDirectionBetween(Player player, GameObject object) {
        var ox = object.getX();
        var oy = object.getY();
        var ox2 = ox + object.getRotatedWidth() - 1;
        var oy2 = oy + object.getRotatedLength() - 1;
        var px = player.getX();
        var py = player.getY();
        var px2 = px + player.getSize() - 1;
        var py2 = py + player.getSize() - 1;
        boolean east = ox2 < px;
        boolean west = px2 < ox;
        boolean north = oy2 < py;
        boolean south = py2 < oy;
        if (south && east) {
            return SOUTH_EAST;
        } else if (north && east) {
            return NORTH_EAST;
        } else if (north && west) {
            return NORTH_WEST;
        } else if (south && west) {
            return SOUTH_EAST;
        } else if (east) {
            return EAST;
        } else if (west) {
            return WEST;
        } else if (north) {
            return NORTH;
        } else if (south) {
            return SOUTH;
        } else {
            switch (object.getFace()) {
                case 0:
                    return EAST;
                case 1:
                    return SOUTH;
                case 2:
                    return WEST;
                case 3:
                default:
                    return NORTH;
            }
        }
    }

	public static void printStackTrace() {
		for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
			System.out.println(ste);
		}
	}

}
