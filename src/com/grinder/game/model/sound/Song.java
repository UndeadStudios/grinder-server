package com.grinder.game.model.sound;

/**
 * An enumerated type that represents a song.
 * 
 * @author Blake
 */
public enum Song {

	ADVENTURE(177),
	BARBARIANISM(141),
	BOOK_OF_SPELLS(64),
	DUEL_ARENA(47),
	FOREVER(98),
	FORBIDDEN(121),
	GARDEN(125),
	GREATNESS(116),
	HARMONY(76),
	RELLEKKA(289),
	SHINE(122),
	YESTERYEAR(145);
	
	/**
	 * The song id.
	 */
	private final int id;

	/**
	 * Constructs a new {@link Song}.
	 * 
	 * @param id the song id
	 */
	Song(int id) {
		this.id = id;
	}

	/**
	 * Gets the song id.
	 * 
	 * @return the song id
	 */
	public int getId() {
		return id;
	}
}
