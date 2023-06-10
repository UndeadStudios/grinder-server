package com.grinder.util.area;

import java.util.ArrayList;
import java.util.List;

import com.grinder.game.World;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;

public class Rectangle extends Shape {

	private Position northEast;

	private Position southWest;

	public Rectangle(Position northEast, Position southWest) {
		areas(new Position[] { northEast, southWest }).type(ShapeType.RECTENGLE);
		this.northEast = northEast;
		this.southWest = southWest;
	}

	@Override
	public boolean inside(Position Position) {

		if (areas()[0].getX() <= Position.getX() || areas()[1].getX() >= Position.getX())
			return false;

		if (areas()[0].getY() <= Position.getY() || areas()[1].getY() >= Position.getY())
			return false;

		return true;
	}

	public List<Position> getBorder(Position Position) {
		List<Position> border = new ArrayList<>();
		if (areas()[0].getX() - 1 <= Position.getX() || areas()[1].getX() + 1 >= Position.getX())
			border.add(Position);

		if (areas()[0].getY() - 1 <= Position.getY() || areas()[1].getY() + 1 >= Position.getY())
			border.add(Position);
		return border;
	}

	public int getNumberOfPlayers() {
		return (int) World.playerStream().filter(player -> inside(player.getPosition())).count();
	}

	public Position getNorthEast() {
		return northEast;
	}

	public Position getSouthWest() {
		return southWest;
	}
}
