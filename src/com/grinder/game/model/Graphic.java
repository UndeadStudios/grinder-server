package com.grinder.game.model;

import com.grinder.game.World;
import com.grinder.util.Priority;

/**
 * Represents a graphic an entity might perform.
 *
 * @author relex lawl
 */
public class Graphic {

    public static boolean USE_NEW_UPDATING = false;
    public static Graphic DEFAULT_RESET_GRAPHIC = new Graphic(65535);

    /**
     * The graphic's id.
     */
    private final int id;
    /**
     * The delay which the graphic must wait before being performed.
     */
    private final int delay;
    /**
     * The graphic's height level to display in.
     */
    private final int height;
    /**
     * The priority of the graphic.
     */
    private final Priority priority;

    public Graphic(int id, int delay, int height, Priority priority) {
        this.id = id;
        this.delay = delay;
        this.height = height;
        this.priority = priority;
    }
    public Graphic(int id, int delay, int height) {
        this(id, delay, height, Priority.LOW);
    }
    public Graphic(int id, int delay, GraphicHeight height, Priority priority) {
        this(id, delay, height.ordinal() * 50, priority);
    }
    public Graphic(int id, int delay, GraphicHeight height) {
        this(id, delay, height, Priority.LOW);
    }
    public Graphic(int id, int delay) {
        this(id, delay, GraphicHeight.LOW);
    }
    public Graphic(int id, GraphicHeight height) {
        this(id, 0, height.ordinal() * 50);
    }
    public Graphic(int id) {
        this(id, 0);
    }
    public Graphic(int id, Priority priority) {
        this(id, 0, GraphicHeight.LOW, priority);
    }
    public Graphic(int id, GraphicHeight height, Priority priority) {
        this(id, 0, height, priority);
    }

    public static void sendGlobal(Graphic graphic, Position position) {
        World.spawn(new TileGraphic(position, graphic));
    }

    /**
     * Gets the graphic's id.
     *
     * @return id.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the graphic's wait delay.
     *
     * @return delay.
     */
    public int getDelay() {
        return delay;
    }

    /**
     * Gets the priority of this graphic.
     *
     * @return the priority.
     */
    public Priority getPriority() {
        return priority;
    }

    public int getHeight() {
        return height;
    }

}
