package com.grinder.game.content.skill.skillable.impl.farming;

import java.util.ArrayList;

import com.grinder.game.model.item.Item;

/**
 * This enum holds the data for each and every possible plant.
 * 
 * @author Austin
 *
 */
public enum Plant {
	HERB_GUAM(			5291,	199,	1,	11,		12.5,	80),
	HERB_MARRENTILL(	5292,	201,	14,	13.5,	15,		80),
	HERB_TARROMIN(		5293,	203,	19,	16,		18,		80),
	HERB_HARRALANDER(	5294,	205,	26,	21.5,	24,		80),
	HERB_GOUT(			6311,	4182,	29,	105,	45,		80),
	HERB_RANNAR(		5295,	207,	32,	26.5,	30.5,	80),
	HERB_TOADFLAX(		5296,	3049,	38,	34,		38.5,	80),
	HERB_IRIT(			5297,	209,	44,	43,		48.5,	80),
	HERB_AVANTOE(		5298,	211,	50,	54.5,	61.5,	80),
	HERB_KWUARM(		5299,	213,	56,	69,		78,		80),
	HERB_SNAPDRAGON(	5300,	3051,	62,	87.5,	98.5,	80),
	HERB_CADANTINE(		5301,	215,	67,	106.5,	120,	80),
	HERB_LANTADYME(		5302,	2485,	73,	134.5,	151.5,	80),
	HERB_DWARF(			5303,	217,	79,	170.5,	192,	80),
	HERB_TORSTOL(		5304,	219,	85,	199.5,	244.5,	80),
	;
	
	int levelReq, time = 80/*minutes*/;
	
	Item seed, product;
	
	double plantXP, harvestXP;
	
	Plant(int seedId, int herbId, int levelReq, double plantXP, double harvestXP, int time) {
		this.seed = new Item(seedId);
		this.product = new Item(herbId);
		this.levelReq = levelReq;
		this.plantXP = plantXP;
		this.harvestXP = harvestXP;
		this.time = time;
	}

	public static ArrayList<Plant> getHerbs() {
		ArrayList<Plant> herbs = new ArrayList<Plant>();
		for (Plant plant : Plant.values())
			if (plant.name().startsWith("HERB"))
				herbs.add(plant);
		return herbs;
	}
	public static ArrayList<Plant> getFlowers() {
		ArrayList<Plant> flowers = new ArrayList<Plant>();
		for (Plant plant : Plant.values())
			if (plant.name().startsWith("FLOWER"))
				flowers.add(plant);
		return flowers;
	}
	public static ArrayList<Plant> getBushes() {
		ArrayList<Plant> bushes = new ArrayList<Plant>();
		for (Plant plant : Plant.values())
			if (plant.name().startsWith("BUSH"))
				bushes.add(plant);
		return bushes;
	}
	public static ArrayList<Plant> getFruits() {
		ArrayList<Plant> fruits = new ArrayList<Plant>();
		for (Plant plant : Plant.values())
			if (plant.name().startsWith("FRUIT"))
				fruits.add(plant);
		return fruits;
	}
	public static ArrayList<Plant> getTrees() {
		ArrayList<Plant> trees = new ArrayList<Plant>();
		for (Plant plant : Plant.values())
			if (plant.name().startsWith("TREE"))
				trees.add(plant);
		return trees;
	}
	public static ArrayList<Plant> getHops() {
		ArrayList<Plant> hops = new ArrayList<Plant>();
		for (Plant plant : Plant.values())
			if (plant.name().startsWith("HOP"))
				hops.add(plant);
		return hops;
	}
	public static ArrayList<Plant> getAllotments() {
		ArrayList<Plant> allotments = new ArrayList<Plant>();
		for (Plant plant : Plant.values())
			if (plant.name().startsWith("ALLOTMENT"))
				allotments.add(plant);
		return allotments;
	}
	
	
	public Item getSeed() {
		return seed;
	}
	public Item getProduct() {
		return product;
	}
	public int getLevelReq() {
		return levelReq;
	}
	public double getPlantXP() {
		return plantXP;
	}
	public double getHarvestXP() {
		return harvestXP;
	}
	public Long getTimeInMillis() {
		return (long) (time * 60_000);
	}
}