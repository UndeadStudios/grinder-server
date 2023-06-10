package com.grinder.util.area;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.grinder.game.World;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;

public abstract class Shape {

	private Position[] areas;
	private ShapeType type;

	public abstract boolean inside(Position Position);

	public Position[] areas() {
		return areas;
	}

	public Shape areas(Position[] areas) {
		this.areas = areas;
		return this;
	}

	public ShapeType type() {
		return type;
	}

	public Shape type(ShapeType type) {
		this.type = type;
		return this;
	}

	public enum ShapeType {
		RECTENGLE,
		POLYGON;
	}

	public int getNumberOfPlayers() {
		return (int) World.playerStream()
				.filter(player -> inside(player.getPosition()))
				.count();
	}
	
	public List<Player> getPlayers() {
		return World.playerStream()
				.filter(player -> inside(player.getPosition()))
				.collect(Collectors.toList());
	}

}
