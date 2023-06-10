package com.grinder.game.content.skill.skillable.impl.farming;


/**
 * This enum is used for holding the data for all different patch states and their corresponding object ID's 
 * 
 * @author Austin
 *
 */
public enum PatchState {
	HERB_GRASS_4(0, 8135, 0),
	HERB_GRASS_3(0, 8134, 1),
	HERB_GRASS_2(0, 8133, 2),
	HERB_RAKED(0, 8132, 3),
	HERB_COMPOST(1, 8132, 3),
	HERB_SUPERCOMPOST(1, 8132, 3),
	HERB_SEEDED(2, 8132, 3),
	HERB_GROWING_1(3, 8139, 4),
	HERB_GROWING_2(4, 8140, 5),
	HERB_GROWING_3(5, 8141, 6),
	HERB_GROWING_4(6, 8142, 7),
	HERB_GROWN(7, 8143, 8),
	HERB_DISEASED_1(3, 8144, 128),
	HERB_DISEASED_2(4, 8145, 129),
	HERB_DISEASED_3(5, 8146, 130),
	HERB_DEAD_1(3, 8147, 170),
	HERB_DEAD_2(4, 8148, 171),
	HERB_DEAD_3(5, 8149, 172),
	
	NONE(0, 8138),
	RAKED(1),
	COMPOST(2),
	SEEDED(3),
	WATERED(4),
	GROWTH(5),
	HARVEST(6);
	
	private int id, objectId, childIndex;

	PatchState(int id) {
		this.id = id;
	}
	
	PatchState(int id, int objectId) {
		this.id = id;
		this.objectId = objectId;
	}

	PatchState(int id, int objectId, int child) {
		this.id = id;
		this.objectId = objectId;
		this.childIndex = child;
	}

	public int getId() {
		return id;
	}
	
	public int getObjectId() {
		return objectId;
	}

	public int getChildIndex() { return childIndex; }
}