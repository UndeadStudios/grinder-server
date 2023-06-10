package com.grinder.game.content.minigame.barrows;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public enum BarrowsBrother {

    DHAROK(1673, "You dare disturb my rest!", 20720, 20668,
            new Position(3574, 3298),
            new Position(3554, 9717, 3),
            player -> inSquare(player, new Position(3571, 3294), new Position(3579, 3301)), new Position(3556, 9718, 3)),
    AHRIM(1672, "You dare disturb my rest!", 20770, 20667,
            new Position(3565, 3291),
            new Position(3553, 9702, 3),
            player -> inSquare(player, new Position(3560, 3284), new Position(3569, 3293)), new Position(3557, 9703, 3)),
    VERAC(1677, "You dare disturb my rest!", 20772, 20672,
            new Position(3557, 3298),
            new Position(3577, 9707, 3),
            player -> inSquare(player, new Position(3553, 3292), new Position(3562, 3301)), new Position(3578, 9706, 3)),
    TORAG(1676, "You dare disturb my rest!", 20721, 20671,
            new Position(3553, 3283),
            new Position(3571, 9683, 3),
            player -> inSquare(player, new Position(3550, 3278), new Position(3557, 3286)), new Position(3568, 9683, 3)),
    KARIL(1675, "You dare disturb my rest!", 20771, 20670,
            new Position(3565, 3275),
            new Position(3547, 9683, 3),
            player -> inSquare(player, new Position(3561, 3271), new Position(3570, 3280)), new Position(3546, 9684, 3)),
    GUTHAN(1674, "You dare disturb my rest!", 20722, 20669,
            new Position(3577, 3282),
            new Position(3539, 9706, 3),
            player -> inSquare(player, new Position(3573, 3278), new Position(3581, 3286)), new Position(3534, 9704, 3));

    private final int npcId;

    private final String spawnMessage;

    private final int coffinId;

    private final int stairsId;

    private final Position hillPosition;
    private final Position spawnPosition;

    private final BarrowsDig dig;

    private final Position cavePosition;

    private final static EnumSet<BarrowsBrother> barrowsBrothers = EnumSet.allOf(BarrowsBrother.class);

    BarrowsBrother(int npcId, String spawnMessage, int coffinId, int stairsId, Position hillPosition, Position spawnPosition, BarrowsDig dig, Position cavePosition) {
        this.npcId = npcId;
        this.spawnMessage = spawnMessage;
        this.coffinId = coffinId;
        this.stairsId = stairsId;
        this.hillPosition = hillPosition;
        this.spawnPosition = spawnPosition;
        this.dig = dig;
        this.cavePosition = cavePosition;
    }

    public int getNpcId() {
        return npcId;
    }

    public String getSpawnMessage() {
        return spawnMessage;
    }

    public int getCoffinId() {
        return coffinId;
    }

    public int getStairsId() {
        return stairsId;
    }

    public Position getSpawnPosition() {
        return spawnPosition;
    }

    public Position getHillPosition() {
        return hillPosition;
    }

    public BarrowsDig getDig() {
        return dig;
    }

    public Position getCavePosition() {
        return cavePosition;
    }

    public static EnumSet<BarrowsBrother> getBarrowsBrothers() {
        return barrowsBrothers;
    }

    public static Optional<BarrowsBrother> getByNpcId(int npcId) {
        return barrowsBrothers.stream().filter(barrow -> barrow.getNpcId() == npcId).findFirst();
    }

    public static Optional<BarrowsBrother> getByCoffinId(int objectId) {
        return barrowsBrothers.stream().filter(barrow -> barrow.getCoffinId() == objectId).findFirst();
    }

    public static Optional<BarrowsBrother> getByStairsId(int objectId) {
        return barrowsBrothers.stream().filter(barrow -> barrow.getStairsId() == objectId).findFirst();
    }

    public static List<BarrowsBrother> getBrothersList() {
        return barrowsBrothers.stream().collect(Collectors.toList());
    }

    private static boolean inSquare(Player player, Position southWest, Position northEast) {
        return (player.getPosition().getX() >= southWest.getX() && player.getPosition().getX() <= northEast.getX())
                && (player.getPosition().getY() >= southWest.getY() && player.getPosition().getY() <= northEast.getY());
    }
}
