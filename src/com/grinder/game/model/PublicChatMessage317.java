package com.grinder.game.model;

import com.grinder.game.model.message.ChatMessage317;
import com.grinder.util.Misc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a ChatMessage's values, such as their color, effect and text
 * bytes.
 *
 * @author relex lawl
 */
public class PublicChatMessage317 extends ChatMessage317 {

	private final int color;
	private final int effects;

	/**
	 * Creates a new chat message.
	 *
	 * @param message The message.
	 * @param compressedMessage The compressed message.
	 * @param color The text color.
	 * @param effects The text effects.
	 */
	public PublicChatMessage317(String message, byte[] compressedMessage, int color, int effects) {
		super(message, compressedMessage);
		this.color = color;
		this.effects = effects;
	}

	/**
	 * Gets the message's chat color.
	 *
	 * @return colour.
	 */
	public int getColour() {
		return color;
	}

	/**
	 * Gets the message's chat effect.
	 *
	 * @return effects.
	 */
	public int getEffects() {
		return effects;
	}

	/**
	 * Pattern that matches "i" in a message where it should be capital
	 */
	private static final Pattern I_PATTERN = Pattern.compile("(^|\\s)i(?=$|\\s)");

	/**
	 * Formats a chat message.
	 *
	 * @param message
	 *            The string to format.
	 * @return The formatted string.
	 */
	public static String format(String message) {
		message = Misc.capitalize(message);
		StringBuffer buf = new StringBuffer();
		Matcher matcher = I_PATTERN.matcher(message);
		while (matcher.find()) {
			matcher.appendReplacement(buf, matcher.group().toUpperCase());
		}
		matcher.appendTail(buf);
		return buf.toString();
	}
}