package com.grinder.game.content.minigame.chamberoxeric.room.olm;

import com.grinder.game.model.Animation;
import com.grinder.game.model.Position;
import com.grinder.util.Misc;

import java.util.ArrayList;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class OlmConfiguration {
    public static final int GREAT_OLM_OBJECT = 29881;
    public static final int LEFT_HAND_OBJECT = 29884;
    public static final int RIGHT_HAND_OBJECT = 29887;
    public static final int LEFTOVER_CRYSTALS = 1338;
    public static final int DARK_GREEN_FLYING = 1339;
    public static final int GREEN_CRYSTAL_FLYING = 1340;
    public static final int PURPLE_ORB = 1341;
    public static final int GREEN_ORB = 1343;
    public static final int RED_ORB = 1345;
    public static final int FIRE_BLAST = 1347;
    public static final int SMALL_FIRE_BLAST = 1348;
    public static final int CRYSTAL = 1352;
    public static final int DARK_GREEN_SMALL_PROJECTILE = 1354;
    public static final int BLUE_SMALL_PROJECTILE = 1355;
    public static final int GREEN_LIGHTNING = 1356;
    public static final int FALLING_CRYSTAL = 1357;
    public static final int WHITE_CIRCLE = 1359;
    public static final int TELEPORT = 1039;

    public static final Animation GOING_DOWN = new Animation(7348);
    public static final Animation GOING_UP = new Animation(7335);
    public static final Animation FACE_MIDDLE = new Animation(7336);
    public static final Animation FACE_RIGHT = new Animation(7337);
    public static final Animation FACE_LEFT = new Animation(7338);
    public static final Animation MIDDLE_TO_RIGHT = new Animation(7339);
    public static final Animation RIGHT_TO_MIDDLE = new Animation(7340);
    public static final Animation MIDDLE_TO_LEFT = new Animation(7341);
    public static final Animation LEFT_TO_MIDDLE = new Animation(7342);
    public static final Animation LEFT_TO_RIGHT = new Animation(7343);
    public static final Animation RIGHT_TO_LEFT = new Animation(7344);
    public static final Animation SHOOT_MIDDLE = new Animation(7345);
    public static final Animation SHOOT_RIGHT = new Animation(7346);
    public static final Animation SHOOT_LEFT = new Animation(7347);
    public static final Animation FACE_MIDDLE_ENRAGED = new Animation(7374);
    public static final Animation FACE_RIGHT_ENRAGED = new Animation(7376);
    public static final Animation FACE_LEFT_ENRAGED = new Animation(7375);
    public static final Animation SHOOT_MIDDLE_ENRAGED = new Animation(7371);
    public static final Animation SHOOT_RIGHT_ENRAGED = new Animation(7373);
    public static final Animation SHOOT_LEFT_ENRAGED = new Animation(7372);
    public static final Animation GOING_DOWN_ENRAGED = new Animation(7348);
    public static final Animation GOING_UP_LEFT_HAND = new Animation(7354);
    public static final Animation LEFT_HAND = new Animation(7355);
    public static final Animation FLASHING_CRYSTAL_LEFT_HAND = new Animation(7356);
    public static final Animation FLASHING_INFINITY_LEFT_HAND = new Animation(7357);
    public static final Animation FLASHING_LIGHTNING_LEFT_HAND = new Animation(7358);
    public static final Animation FLASHING_CIRCLES_LEFT_HAND = new Animation(7359);
    public static final Animation CLINCHING_LEFT_HAND = new Animation(7360);
    public static final Animation CLENCHED_LEFT_HAND = new Animation(7361);
    public static final Animation BACK_TO_NORMAL_LEFT_HAND = new Animation(7362);
    public static final Animation GOING_UP_RIGHT_HAND = new Animation(7350);

    public static final Animation GOING_UP_ENRAGED = new Animation(7383);

    public static final Animation GOING_DOWN_LEFT_HAND = new Animation(7370);

    public static final Animation GOING_DOWN_RIGHT_HAND = new Animation(7352);

    public static final Animation DEAD_LEFT_HAND = new Animation(7353);




    public static Position randomLocation(int height) {
        ArrayList<Position> positions = new ArrayList<>();

        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 17; y++) {
                if (x != 3237 && y != 5748) {
                    positions.add(new Position(3228 + x, 5731 + y, height));
                }
            }
        }

        return positions.get(Misc.random(positions.size() - 1));
    }
}
