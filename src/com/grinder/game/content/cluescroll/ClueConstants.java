package com.grinder.game.content.cluescroll;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grinder.game.model.Direction;
import com.grinder.game.model.Graphic;
import com.grinder.game.content.cluescroll.scroll.reward.ScrollReward;
import com.grinder.game.content.cluescroll.task.ClueTaskState;
import com.grinder.util.ItemID;

import java.lang.reflect.Modifier;

public class ClueConstants {
	
	public static final ScrollReward[] GLOBAL_REWARDS = new ScrollReward[] {
			// All types of pages, purple sweets, amethyst arrows
			new ScrollReward(3827), new ScrollReward(3828),
			new ScrollReward(3829), new ScrollReward(3830),
			new ScrollReward(3835), new ScrollReward(3836),
			new ScrollReward(3837), new ScrollReward(3838),
			new ScrollReward(3831), new ScrollReward(3832),
			new ScrollReward(3833), new ScrollReward(3834),
			new ScrollReward(12613), new ScrollReward(12614), new ScrollReward(12615),
			new ScrollReward(12938, 3, 5),
			new ScrollReward(21802, 3, 5),
			//new ScrollReward(989), new ScrollReward(991),
			new ScrollReward(12616), new ScrollReward(12617),
			new ScrollReward(12618), new ScrollReward(12619),
			new ScrollReward(12620), new ScrollReward(12621),
			new ScrollReward(12622), new ScrollReward(12623),
			new ScrollReward(12624),
			new ScrollReward(ItemID.ANCIENT_BLESSING),
			new ScrollReward(ItemID.HONOURABLE_BLESSING),
			new ScrollReward(ItemID.WAR_BLESSING),
			new ScrollReward(ItemID.PEACEFUL_BLESSING),
			new ScrollReward(ItemID.UNHOLY_BLESSING),
			new ScrollReward(ItemID.HOLY_BLESSING),
			new ScrollReward(ItemID.NARDAH_TELEPORT, 5, 10),
			new ScrollReward(ItemID.MOS_LEHARMLESS_TELEPORT, 5, 10),
			new ScrollReward(ItemID.MORTTON_TELEPORT, 5, 10),
			new ScrollReward(ItemID.FELDIP_HILLS_TELEPORT, 5, 10),
			new ScrollReward(ItemID.LUNAR_ISLE_TELEPORT, 5, 10),
			new ScrollReward(ItemID.DIGSITE_TELEPORT, 5, 10),
			new ScrollReward(ItemID.PISCATORIS_TELEPORT, 5, 10),
			new ScrollReward(ItemID.PEST_CONTROL_TELEPORT, 5, 10),
			new ScrollReward(ItemID.TAI_BWO_WANNAI_TELEPORT, 5, 10),
			new ScrollReward(ItemID.LUMBERYARD_TELEPORT, 5, 10),
			new ScrollReward(ItemID.ELF_CAMP_TELEPORT, 5, 10),
			new ScrollReward(ItemID.NARDAH_TELEPORT, 5, 10),
			new ScrollReward(ItemID.RED_FIRELIGHTER, 5, 10),
			new ScrollReward(ItemID.GREEN_FIRELIGHTER, 5, 10),
			new ScrollReward(ItemID.BLUE_FIRELIGHTER, 5, 10),
			new ScrollReward(ItemID.PURPLE_FIRELIGHTER, 5, 10),
			new ScrollReward(ItemID.PURPLE_SWEETS, 10, 100),
			new ScrollReward(24982, 10, 100),
			new ScrollReward(24984, 10, 50),
			new ScrollReward(24986, 10, 25),
			//new ScrollReward(21326, 50, 250)
	};

	public static final Direction[] PUZZLE_POSSIBLE_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

	public static final ClueTaskState DEFAULT_ACTION_PROGRESS = new ClueTaskState(true, false);

	public static final Graphic AGENT_SPAWN_GRAPHIC = new Graphic(188, 0);

	public static final Graphic AGENT_SPAWN_GRAPHIC2 = new Graphic(188, 5 * 20);

	private static Gson gson;

	public static Gson getGson() {
        if (gson == null)
            gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC).create();
        return gson;
    }
}
