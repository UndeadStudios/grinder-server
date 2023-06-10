package com.grinder.game.content.minigame.chamberoxeric.storage;

import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 *
 */
public enum StorageUnit {

	NONE(-1, -1, -1, -1, -1, -1),

	TINY(29_769, 21_040, 50, 5, 1, 1),

	SMALL(29_770, 21_037, 250, 30, 30, 4),

	MEDIUM(29_779, 21_038, 500, 60, 60, 8),

	LARGE(29_780, 21_039, 1000, 90, 90, 15),

	;

	public int id;

	public int item;

	public int capacity;

	public int personal;

	public int level;

	public int planks;

	StorageUnit(int id, int item, int capacity, int personal, int level, int planks) {
		this.id = id;
		this.item = item;
		this.capacity = capacity;
		this.personal = personal;
		this.level = level;
		this.planks = planks;
	}

	public static final Map<Integer, StorageUnit> FOR_OBJECT = new HashMap<Integer, StorageUnit>();

	public static final Map<Integer, StorageUnit> FOR_ITEM = new HashMap<Integer, StorageUnit>();

	static {
		for (StorageUnit map : StorageUnit.values()) {
			FOR_OBJECT.put(map.id, map);
			FOR_ITEM.put(map.item, map);
		}
	}
}