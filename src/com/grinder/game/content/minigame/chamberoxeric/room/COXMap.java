package com.grinder.game.content.minigame.chamberoxeric.room;

import com.grinder.game.model.Position;
/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public enum COXMap {

	START(new Position(3331, 5188)),
	SKILLING_AREA(new Position(3280, 5440)),

	SKELETAL_MYSTIC(new Position(3343, 5257, 1)),

	VANGUARD(new Position(3342, 5250)),

	VASA_NISTIRIO(new Position(3280, 5280)),

	ICE_DEMON(new Position(3282, 5348)),

	TEKTON(new Position(3296, 5280, 1)),

	SHAMAN(new Position(3312, 5275)),
	MUTADILES(new Position(3311, 5309, 1)),

	VESPULA(new Position(3280, 5280, 2)),

	OLM(new Position(3232, 5720)),

	;

	public Position position;

	COXMap(Position position) {
		this.position = position;
	}

}