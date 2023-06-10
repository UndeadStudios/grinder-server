package com.grinder.game.model;

public class Boundaries {

	/**
	 * The edgeville area boundaries.
	 */
	public static final Boundary EDGEVILLE_AREA = new Boundary(3070, 3117, 3456, 3520);

	/**
	 * The boundaries for the green dragons near the Dark Warriors' Fortress.
	 */
	public static final Boundary GREEN_DRAGONS_BOUNDARY = new Boundary(2954, 3014, 3595, 3653);

	/*
	* Game map in green areas (forests)
	 */
	public static final Boundary[] GREEN_TREE_AREAS = new Boundary[] {
			new Boundary(1175, 3891, 2495, 4143)
	};

	/**
	 * The home areas boundaries.
	 */
	public static final Boundary[] HOME_AREAS = new Boundary[] {
			new Boundary(2720, 3150, 2882, 3199), new Boundary(2094, 3135, 2690, 3519),
			new Boundary(3135, 3263, 3137, 3520), new Boundary(3264, 3777, 3333, 3524),
			new Boundary(2303, 2432, 3776, 3839), new Boundary(2493, 3199, 3841, 3869)
	};

}
