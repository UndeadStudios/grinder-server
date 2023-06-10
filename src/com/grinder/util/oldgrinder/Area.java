package com.grinder.util.oldgrinder;

import com.grinder.game.collision.CollisionManager;
import com.grinder.game.entity.Entity;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.LineOfSight;
import com.grinder.game.model.Direction;
import com.grinder.game.model.Position;
import com.grinder.game.model.PositionUtil;
import com.grinder.util.DistanceUtil;
import com.grinder.util.Misc;
import kotlin.random.Random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

public class Area {

	private int west;
	private int south;
	private int east;
	private int north;

	private Area() {}

	/**
	 * 
	 * @param x1 x1 position
	 * @param y1 y1 position
	 * @param x2 x2 position
	 * @param y2 y2 position
	 */
	public Area(int x1, int y1, int x2, int y2) {
		this.north = y2;
		this.east = x2;
		this.south = y1;
		this.west = x1;
	}

	public Area(int radius) {
		this.north = radius;
		this.east = radius;
		this.south = radius;
		this.west = radius;
	}

	public int getWest() {
		return west;
	}

	public void setWest(int west) {
		this.west = west;
	}

	public int getSouth() {
		return south;
	}

	public void setSouth(int south) {
		this.south = south;
	}

	public int getEast() {
		return east;
	}

	public void setEast(int east) {
		this.east = east;
	}

	public int getNorth() {
		return north;
	}

	public void setNorth(int north) {
		this.north = north;
	}

	@Override
	public String toString() {
		return "Area [west=" + west + ", south=" + south + ", east=" + east + ", north=" + north + "]";
	}
	
	public boolean hasArea() {
		return west + south + east + north != 0;
	}

	public boolean contains(Entity entity){
		return contains(entity.getCenterPosition());
	}

	public boolean contains(Position position){
		final int x = position.getX();
		final int y = position.getY();
		return x >= west && x <= east && y >= south && y <= north;
	}

	public boolean contains(Position position, int reach){
		final int x = position.getX();
		final int y = position.getY();
		return x >= west + reach
				&& x <= east - reach
				&& y >= south - reach
				&& y <= north + reach;
	}

	public Position getCenter(){
		return new Position(west + (east - west) / 2, south + (north - south) / 2 );
	}

	public Position getRandomPosition() {
		return new Position(Misc.random(west, east), Misc.random(south, north), 0);
	}

	public Stream<Position> findPositions(int plane){

		final Stream.Builder<Position> builder = Stream.builder();

		for(int x = west; x <= east; x++){

			for(int y = south; y <= north; y++){

				final Position position = new Position(x, y, plane);

				builder.accept(position);
			}
		}

		return builder.build();
	}

	public Stream<Position> findOpenPositions(final int plane){
		return findPositions(plane).filter(CollisionManager::open);
	}

	public Optional<Position> findRandomOpenPosition(int plane, int minOpenDirections){
		return findRandomOpenPosition(plane, minOpenDirections, null);
	}

	public Optional<Position> findRandomOpenPosition(int plane, int minOpenDirections, Position reachableFrom){
		ArrayList<Position> spots = new ArrayList<>();

		for(int x = west; x <= east; x++){

			for(int y = south; y <= north; y++){

				final Position position = new Position(x, y, plane);

				if(CollisionManager.blocked(position))
					continue;

				int openDirections = 0;

				for(Direction direction : Direction.values()){
					if(!CollisionManager.blocked(position, direction))
						openDirections++;
				}

				if(openDirections >= minOpenDirections)
					spots.add(position);
			}
		}

		if (reachableFrom != null && !spots.isEmpty()){
			Collections.shuffle(spots);
			for (Position position : spots) {
				if (position.sameAs(reachableFrom))
					continue;
				if (LineOfSight.withinSight(reachableFrom, position))
					return Optional.of(position);
			}
			return Optional.empty();
		}

		return spots.size() > 0 ? Optional.of(spots.get(Random.Default.nextInt(spots.size()))): Optional.empty();
	}

	public Position getRandomPosition(int maxDistance) {
		int x = west + Misc.clamp(Misc.getRandomInclusive(east - west), 0, maxDistance);
		int y = south + Misc.clamp(Misc.getRandomInclusive(north - south), 0, maxDistance);
		return new Position(x, y, 0);
	}

	public Area getAbsolute(Position p) {
		Area area = new Area();
		area.west = p.getX() - west;
		area.north = p.getY() + north;
		area.south = p.getY() - south;
		area.east = p.getX() + east;
		return area;
	}
	
	public static Area fromAbsolute(Position position, Area walkingArea) {
		int north = walkingArea.getNorth() - position.getY();
		int east = walkingArea.getEast() - position.getX();
		int south = position.getY() - walkingArea.getSouth();
		int west = position.getX() - walkingArea.getWest();
		return new Area(west, south, east, north);
	}

	public static Area of(int west, int south, int east, int north) {
		return new Area(west, south, east, north);
	}

	public static Area of(Entity entity) {
		return new Area(entity.getSize()).getAbsolute(entity.getCenterPosition());
	}

	public int getVerticalLength() {
		return  (north - south) / 2;
	}

	public int getHorizontalLength() {
		return  (west - east) / 2;
	}

	public boolean isSquare(){
		return getVerticalLength() == getHorizontalLength();
	}

	public int closestDistanceTo(Agent agent) {

		if(contains(agent))
			return 0;

		final Position center = getCenter();
		final Position outside = agent.getPosition();

		final int size = agent.getSize();
		final int height = getVerticalLength();
		final int width = getHorizontalLength();

		final Direction rotation = Direction.getDirection(outside, center);

		final Position[] firstBorder = PositionUtil.getOutlineNoCorner(center, rotation, height, width);
		final Position[] secondBorder = PositionUtil.getTilesEnclosing(outside, size);

		int minDistance = Integer.MAX_VALUE;

		for (final Position tileInside : firstBorder) {
			for (final Position tileOutside : secondBorder) {
				minDistance = Math.min(minDistance, DistanceUtil.getManhattanDistance(tileInside, tileOutside));
			}
		}

		return minDistance;
	}

}