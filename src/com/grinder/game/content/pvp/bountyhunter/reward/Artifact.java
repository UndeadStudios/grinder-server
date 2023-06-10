package com.grinder.game.content.pvp.bountyhunter.reward;

public enum Artifact {
	
	EMBLEM(21807, 2000),
	TOTEM(21810, 2250),
	STATUETTE(21813, 2500),
	MEDALLION(22299, 5500),
	EFFIGY(22302, 6000),
	RELIC(22305, 7000);
	
	
	private int id;
	private int value;
	
	Artifact(int itemId, int value) {
		this.id = itemId;
		this.value = value;
	}
	
	public int getId() {
		return id;
	}
	
	public int getValue() {
		return value;
	}
	
}
