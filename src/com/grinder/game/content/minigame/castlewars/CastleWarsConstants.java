package com.grinder.game.content.minigame.castlewars;

import com.grinder.game.content.skill.skillable.impl.mining.PickaxeType;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.entity.object.StaticGameObject;
import com.grinder.game.model.Position;
import com.grinder.util.ItemID;

import java.util.concurrent.TimeUnit;

public class CastleWarsConstants {

    public static final int SARADOMIN_TEAM = 0;
    public static final int ZAMORAK_TEAM = 1;

    public static final int MIN_TEAM_MEMBERS = 1;
    public static final int GAME_DURATION = 20; // 20
    public static final int GAME_AWAIT_DURATION = 5; // 5

    public static final long EFFECT_DURATION = TimeUnit.MINUTES.toMillis(2);

    public static final int ITEM_ROPE = 954;
    public static final int ITEM_EXPLOSIVE = 4045;
    public static final int ITEM_TINDERBOX = 590;
    public static final int ITEM_BUCKET = 1925;
    public static final int ITEM_BUCKET_OF_WATER = 1929;
    public static final int ITEM_ZAMORAK_FLAG = 4039;
    public static final int ITEM_SARADOMIN_FLAG = 4037;
    public static final int ITEM_BANDAGE = 4049;
    public static final int ITEM_BARRICADE = 4053;
    public static final int ITEM_TOOLKIT = 4051;
    public static final int ITEM_LOCKPICK = 13296;
    public static final int ITEM_SARADOMIN_HOOD = 4513;
    public static final int ITEM_SARADOMIN_CAPE = 4514;
    public static final int ITEM_ZAMORAK_HOOD = 4515;
    public static final int ITEM_ZAMORAK_CAPE = 4516;
    public static final int ITEM_ROCK = 4043;
    public static final int ITEM_SHEARS = 1735;
    public static final int ITEM_TICKETS = 4067;

    public static final int[] INVALID_ITEMS = {
            /*995, 13307, 8007, 8008, 8009, 8010, 8011, 8013,
            19475, 19476, 19477, 19478, 19479, 19480,
            19967, 19968, 189, 191, 193, 2450, 3385, 6685, 6687,
            6689, 6691*/
            ItemID.CANNON_BALL, ItemID.CANNON_BASE, ItemID.CANNON_STAND, ItemID.CANNON_BARRELS, ItemID.CANNON_FURNACE, ItemID.DWARF_CANNON_SET
    };

    public static final int[] GAME_ITEMS = {
            ITEM_SARADOMIN_HOOD, ITEM_SARADOMIN_CAPE, ITEM_ZAMORAK_HOOD, ITEM_ZAMORAK_CAPE,
            ITEM_BANDAGE, ITEM_BARRICADE, ITEM_BUCKET, ITEM_BUCKET_OF_WATER,
            ITEM_EXPLOSIVE, ITEM_TINDERBOX, ITEM_TOOLKIT, ITEM_ROPE, ITEM_ZAMORAK_FLAG,
            ITEM_SARADOMIN_FLAG, ITEM_ROCK, PickaxeType.BRONZE.getId()
    };

    public static final long BANDAGE_DELAY = 600L;
    public static final int BANDAGES_TABLE = 4458;
    public static final int SARADOMIN_ENERGY_BARRIER = 4469;
    public static final int ZAMORAK_ENERGY_BARRIER = 4470;

    public static final int OBJECT_SARADOMIN_FLAG = 4900;
    public static final int OBJECT_ZAMORAK_FLAG = 4901;

    public static final int OBJECT_SARADOMIN_FLAG_STAND = 4902;
    public static final int OBJECT_SARADOMIN_STAND = 4377;
    public static final int OBJECT_ZAMORAK_FLAG_STAND = 4903;
    public static final int OBJECT_ZAMORAK_STAND = 4378;
    public static final int OBJECT_ZAMORAK_JOIN_PORTAL = 4388;
    public static final int OBJECT_GUTHIX_JOIN_PORTAL = 4408;
    public static final int OBJECT_SARADOMIN_JOIN_PORTAL = 4387;
    public static final int OBJECT_SCOREBOARD = 4484;
    public static final int OBJECT_SARADOMIN_WAITING_LEAVE_PORTAL = 4389;
    public static final int OBJECT_ZAMORAK_WAITING_LEAVE_PORTAL = 4390;
    public static final int OBJECT_ZAMORAK_BASE_LEAVE_PORTAL = 4407;
    public static final int OBJECT_SARADOMIN_BASE_LEAVE_PORTAL = 4406;

    public static final int OBJECT_SARADOMIN_BATTLEMENTS = 4446;
    public static final int OBJECT_ZAMORAK_BATTLEMENTS = 4447;

    public static final int OBJECT_ZAMORAK_CATAPULT = 4381;
    public static final int OBJECT_SARADOMIN_CATAPULT = 4382;

    public static final int OBJECT_ZAMORAK_BROKEN_CATAPULT = 4385;
    public static final int OBJECT_SARADOMIN_BROKEN_CATAPULT = 4386;

    public static final int OBJECT_ZAMORAK_BURNING_CATAPULT = 4905;
    public static final int OBJECT_SARADOMIN_BURNING_CATAPULT = 4904;

    public static final int OBJECT_WALL_ROPE = 36312;
    public static final int OBJECT_SARADOMIN_WALL_ROPE = 36313;
    public static final int OBJECT_ZAMORAK_WALL_ROPE = 36314;

    public static final int OBJECT_SARADOMIN_ALTAR = 37990;
    public static final int OBJECT_ZAMORAK_ALTAR = 19145;

    public static GameObject ZAMORAK_WEST_ROCKS = ObjectManager.findStaticObjectAt(4437, new Position(2391, 9501, 0)).orElse(null);
    public static GameObject ZAMORAK_NORTH_ROCKS = ObjectManager.findStaticObjectAt(4437, new Position(2400, 9512, 0)).orElse(null);

    public static GameObject SARADOMIN_SOUTH_ROCKS = ObjectManager.findStaticObjectAt(4437, new Position(2401, 9494, 0)).orElse(null);
    public static GameObject SARADOMIN_EAST_ROCKS = ObjectManager.findStaticObjectAt(4437, new Position(2409, 9503, 0)).orElse(null);

    public static final int MAX_DOOR_HEALTH = 300;

    public static final int LIMIT_BARRICADES = 10;
    public static final int PICK_LOCK_CHANCE = 12;
    public static final int LOCK_PICK_BOOST = 40;

    public static final int NPC_GUTHIX_SHEEP = 5726;
    //5335-5334-0-0-5334-5334
    public static final int NPC_SARADOMIN_RABBIT = 5727;
    //6089-6088-0-0-6088-6088
    public static final int NPC_ZAMORAK_IMP = 5728;
    //171-168-4626-0-168-168


}
