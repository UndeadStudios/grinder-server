package com.grinder.game.content.minigame.castlewars;

import java.util.concurrent.TimeUnit;

public enum CastleWarsEffect {
    RESTLESS("Restless",TimeUnit.MINUTES.toMillis(2)),
    HARM("Harm", TimeUnit.MINUTES.toMillis(2));

    private final String name;
    private final long duration;

    private CastleWarsEffect(String name, long duration) {
        this.name = name;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public long getDuration() {
        return duration;
    }
}
