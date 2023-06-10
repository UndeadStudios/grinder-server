package com.grinder.game.model;

public class GraphicsLoop {

    private final Graphic gfx;
    private final int loopDelay;

    public GraphicsLoop(Graphic gfx, int loopDelay) {
        this.gfx = gfx;
        this.loopDelay = loopDelay;
    }

    public Graphic getGraphic() {
        return gfx;
    }

    public int getLoopDelay() {
        return loopDelay;
    }
}
