package com.grinder.game.content.minigame.chamberoxeric;

import com.grinder.game.model.Position;
/**
 *
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 *
 */
public enum COXMob {

	RUNTS(new int[] {7546}, 4, new Position[] {new Position(3276, 5163),
			new Position(3279, 5167), new Position(3284, 5170),}),

	;

	public int[] id;
	
	public int spawnAmount;
	
	public Position[] spawns;

	COXMob(int[] id, int spawnAmount, Position[] spawns) {
		this.id = id;
		this.spawnAmount = spawnAmount;
		this.spawns = spawns;
	}

	public static final COXMob[] VALUES = values();
}
