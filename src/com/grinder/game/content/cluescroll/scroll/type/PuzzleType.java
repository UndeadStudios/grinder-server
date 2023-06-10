package com.grinder.game.content.cluescroll.scroll.type;

import com.grinder.util.Misc;

import java.util.ArrayList;
import java.util.stream.Stream;

public enum PuzzleType {

	TREE(3619, 1, 3565),
	TROLL(3643, 1, 3569),
	CASTLE(2749, 1, 2798),
	PLANE(3904, 2, 3567),
	DRAGON(20283, 1, -1),
	GENERAL_GRAARDOR(18865, 1, -1),
	DARK_BEAST(18889, 1, -1),
	BRIDGE(18913, 1, -1);

	private static final ArrayList<PuzzleType> randomPuzzles = new ArrayList<>();
	
	static {
		Stream.of(values()).filter(p -> p.puzzleBox == -1).forEach(randomPuzzles::add);
	}

	PuzzleType(int itemOffset, int nextOffset, int puzzleBox) {
		this.solution = new int[5][5];
		this.puzzleBox = puzzleBox;

		for (int y = 0; y < getSolution().length; y++) {
			for (int x = 0; x < getSolution()[y].length; x++) {
				getSolution()[x][y] = itemOffset;
				itemOffset += nextOffset;
			}
		}
		this.getSolution()[4][4] = -1;
	}

	private final int[][] solution;
	private final int puzzleBox;

	public static PuzzleType forName(String puzzleType) {
		for (PuzzleType type : values()) {
			if (type.name().equalsIgnoreCase(puzzleType)) {
				return type;
			}
		}
		return null;
	}

	public static PuzzleType forPuzzleID(int itemId) {
		for (PuzzleType type : values()) {
			if (type.puzzleBox == itemId) {
				return type;
			}
		}
		return Misc.random(randomPuzzles);
	}

	public int[][] getSolution() {
		return solution;
	}

	public int getPuzzleBox() {
		return puzzleBox;
	}
}