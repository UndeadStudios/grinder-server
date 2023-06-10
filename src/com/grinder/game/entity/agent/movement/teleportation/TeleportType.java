package com.grinder.game.entity.agent.movement.teleportation;

import com.grinder.game.model.Animation;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.GraphicHeight;
import com.grinder.game.model.sound.AreaSound;
import com.grinder.game.model.sound.Sounds;
import com.grinder.util.Priority;
import com.grinder.util.oldgrinder.Area;

import java.util.Optional;
import java.util.OptionalInt;

public enum TeleportType {

    HOME(
            new Animation[] {new Animation(4847, Priority.HIGH), new Animation(4850, Priority.HIGH), new Animation(4853, Priority.HIGH), new Animation(4855, Priority.HIGH), new Animation(4857, Priority.HIGH)},
            new Graphic[] {new Graphic(800, GraphicHeight.LOW), null, new Graphic(802, GraphicHeight.LOW), new Graphic(803, GraphicHeight.LOW), new Graphic(804, GraphicHeight.LOW)},
            19, 5, 8, 12, 16, 19),

    FAIRY_RING(
            new Animation[]{new Animation(3265, Priority.HIGH), null, Animation.DEFAULT_RESET_ANIMATION},
            new Graphic[]{new Graphic(569, 0)}, 3, 0, 3),

    ECTOPHIAL(
            new Animation[]{new Animation(878, Priority.HIGH), null, Animation.DEFAULT_RESET_ANIMATION},
            new Graphic[]{new Graphic(1273, 0)}, 4, 0, 4),

    XERIC_TALISMAN(
            new Animation[]{new Animation(3865, Priority.HIGH), null, Animation.DEFAULT_RESET_ANIMATION},
            new Graphic[]{new Graphic(1612, 0, GraphicHeight.HIGH)}, 3, 0, 3),

    // Spellbooks
    NORMAL(
            new Animation[]{new Animation(714, Priority.HIGH), null, new Animation(715, Priority.HIGH)},
            new Graphic[]{new Graphic(308, 50, GraphicHeight.HIGH)}, 3, 0, 3),
    ANCIENT(
            new Animation[]{new Animation(6303, Priority.HIGH), null, Animation.DEFAULT_RESET_ANIMATION},
            new Graphic[]{new Graphic(392, Priority.HIGH)}, 3, 0, 3),
    LUNAR(
            new Animation[]{new Animation(1816, Priority.HIGH), null, new Animation(715, Priority.HIGH)},
            new Graphic[]{new Graphic(747, 0, GraphicHeight.HIGH)}, 3, 0, 3),

    ARCEEUS(
            new Animation[]{new Animation(3865, Priority.HIGH), null, Animation.DEFAULT_RESET_ANIMATION},
            new Graphic[]{new Graphic(1296, 0, GraphicHeight.HIGH)}, 3, 0, 3),

    // Ladders
    LADDER_DOWN(
            new Animation[]{new Animation(827, Priority.HIGH), null, Animation.DEFAULT_RESET_ANIMATION},
            new Graphic[]{}, 1, 0, 1),
	LADDER_UP(
            new Animation[]{new Animation(828, Priority.HIGH), null, Animation.DEFAULT_RESET_ANIMATION},
            new Graphic[]{}, 1, 0, 1),

    //ancient wizard
    ANCIENT_WIZARD(
            new Animation[]{null, new Animation(1816, Priority.HIGH), Animation.DEFAULT_RESET_ANIMATION, null},
            new Graphic[]{null, new Graphic(342, Priority.HIGH), null}, 5, 3, 5),


    MAGE_OF_ZAMORAK(
            new Animation[]{null, null, null},
            new Graphic[]{new Graphic(110, 75, GraphicHeight.HIGH, Priority.HIGH), null, new Graphic(110, Priority.HIGH)}, 3, 0, 3),

    
    ROYAL_SEED_POT(
            new Animation[]{new Animation(4544, Priority.HIGH), null, new Animation(4546, Priority.HIGH)},
            new Graphic[]{new Graphic(767, Priority.HIGH), null, new Graphic(769, Priority.HIGH)}, 5, 2, 5),


    AUBURY(
            new Animation[]{null, null, null},
            new Graphic[]{new Graphic(110, GraphicHeight.HIGH, Priority.HIGH), null, new Graphic(110, GraphicHeight.HIGH, Priority.HIGH)}, 3, 0, 3),

    // Misc
	LEVER(
            new Animation[]{new Animation(2140, Priority.HIGH), new Animation(714), new Animation(715, Priority.HIGH)},
            new Graphic[]{null, new Graphic(308, 50, GraphicHeight.HIGH), null}, 4, 2, 4),
    TELE_TAB(
            new Animation[]{new Animation(4069, Priority.HIGH), new Animation(4071, Priority.HIGH), Animation.DEFAULT_RESET_ANIMATION},
            new Graphic[]{new Graphic(678, 55, 0, Priority.HIGH), null, null}, 4, 2, 4),

    TELE_TAB_NEW(
            new Animation[]{new Animation(3864, Priority.HIGH), null, Animation.DEFAULT_RESET_ANIMATION},
            new Graphic[]{new Graphic(1039, 0, 0, Priority.HIGH), null, null}, 3, 0, 3),

    SCROLL(
            new Animation[]{new Animation(4071, Priority.HIGH), null, Animation.DEFAULT_RESET_ANIMATION},
            new Graphic[]{new Graphic(1039, 15, 0, Priority.HIGH), null, null}, 3, 1, 3),
    PURO_PURO(
            new Animation[]{new Animation(6601, Priority.HIGH), null, Animation.DEFAULT_RESET_ANIMATION},
            new Graphic[]{new Graphic(1118, Priority.HIGH), null, null}, 9, 7, 9),
    JEWELRY_RUB(
            new Animation[]{new Animation(714, Priority.HIGH), null, new Animation(715, Priority.HIGH)},
            new Graphic[]{new Graphic(308, 50, GraphicHeight.HIGH), null, null}, 3, 1, 3),

    //TODO: ECTOPHIAL_TELEPORT()

    HOME_QUICK(
            new Animation[] {new Animation(4855, Priority.HIGH), null, new Animation(4857, Priority.HIGH), Animation.DEFAULT_RESET_ANIMATION},
            new Graphic[] {new Graphic(802, GraphicHeight.LOW), new Graphic(803, GraphicHeight.LOW), new Graphic(804, GraphicHeight.LOW)},
            5, 2, 4);

    private final Animation[] anims;
    private final Graphic[] graphics;
    private final int[] ticks;
    
    TeleportType(Animation[] anims, Graphic[] graphics, int... ticks) {
        this.ticks = ticks;
        this.anims = anims;
        this.graphics = graphics;
    }

    public boolean isNonMagical(){
        return this == LADDER_UP || this == LADDER_DOWN || this == LEVER;
    }

    public boolean isEffectiveInCombat(){
        return this == TELE_TAB || this == JEWELRY_RUB;
    }

    public boolean ignoreTeleportRequirements() {
        return this == LADDER_UP || this == LADDER_DOWN
                || this == LEVER || this == TELE_TAB || this == TELE_TAB_NEW;
    }

    public Optional<AreaSound> findTeleportSound(){
        if (equals(TELE_TAB)) return Optional.of(new AreaSound(Sounds.TELEPORT_TABLET, 0, 1, 12));
        else if (equals(ANCIENT)) return Optional.of(new AreaSound(Sounds.ANCIENT_TELEPORT, 0, 1, 12));
        else if (equals(FAIRY_RING)) return Optional.of(new AreaSound(Sounds.FAIRY_RING_TELEPORT, 25, 1, 12));
        else if (equals(AUBURY)) return Optional.of(new AreaSound(Sounds.AUBURY_TELEPORT, 75, 1, 12));
        else if (equals(ANCIENT_WIZARD)) return Optional.of(new AreaSound(Sounds.TELEOTHER_TELEPORTING, 75, 1, 12));
        else if (equals(ECTOPHIAL)) return Optional.of(new AreaSound(Sounds.ECTOPHIAL, 130, 1, 12));
        else if (equals(HOME)) return Optional.of(new AreaSound(Sounds.HOME_TELEPORT[0], 0, 1, 12));
        else if (equals(HOME_QUICK)) return Optional.of(new AreaSound(Sounds.HOME_TELEPORT_QUICK, 0, 1, 12));
        else if (!(this == LADDER_UP || this == LADDER_DOWN || this == ROYAL_SEED_POT))
            return Optional.of(new AreaSound(Sounds.NORMAL_TELEPORT, 0, 1, 12));
        return Optional.empty();
    }

    public Animation getAnimation(int index) {
        if(index < 0 || index >= anims.length)
            return null;
        return anims[index];
    }

    public Graphic getGraphic(int index) {
        if(index < 0 || index >= graphics.length)
            return null;
        return graphics[index];
    }

    public int getTick(int index) {
        if(index < 0 || index >= ticks.length)
            return -1;
        return ticks[index];
    }
}
