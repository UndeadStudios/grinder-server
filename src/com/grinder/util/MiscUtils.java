package com.grinder.util;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;

import java.awt.*;

/**
 * @version 1.0
 * @since 28/12/2019
 */
public class MiscUtils {

    private static int[] abovePointsX = {2944, 3392, 3392, 2944};
    private static int[] abovePointsY = {3525, 3525, 3971, 3971};
    private static int[] belowPointsX = {2944, 2944, 3264, 3264};
    private static int[] belowPointsY = {9918, 10360, 10360, 9918};

    private static Polygon abovePoly = new Polygon(abovePointsX, abovePointsY, abovePointsX.length);
    private static Polygon belowPoly = new Polygon(belowPointsX, belowPointsY, belowPointsX.length);

    //test replacement so private for now
    public static boolean inWildy(Position position)
    {
        if (position == null)
        {
            return false;
        }

        return abovePoly.contains(position.getX(), position.getY()) || belowPoly.contains(position.getX(), position.getY());
    }

    public static int getWildernessLevelFrom(Player client, Position point)
    {
        if (client == null)
        {
            return 0;
        }

        if (point == null)
        {
            return 0;
        }

        int x = point.getX();

        int y = point.getY();
        //v underground        //v above ground
        int wildernessLevel = clamp(y > 6400 ? ((y - 9920) / 8) + 1 : ((y - 3520) / 8) + 1, 0, 99);

        /*if (point.getZ() > 0)
        {
            if (y < 9920)
            {
                wildernessLevel = 0;
            }
        }*/
        //client.setWildernessLevel(wildernessLevel);
        return Math.max(0, wildernessLevel);
    }

    public static int clamp(int val, int min, int max)
    {
        return Math.max(min, Math.min(max, val));
    }

    public static float clamp(float val, float min, float max)
    {
        return Math.max(min, Math.min(max, val));
    }
}
