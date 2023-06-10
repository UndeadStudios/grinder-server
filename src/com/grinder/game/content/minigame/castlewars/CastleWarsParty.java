package com.grinder.game.content.minigame.castlewars;

import com.grinder.game.content.minigame.Party;

import java.util.EnumMap;

public class CastleWarsParty extends Party {
    public CastleWarsParty(String name, int teamID) { super(name, teamID); }

    EnumMap<CastleWarsEffect, Long> stateMap = new EnumMap<CastleWarsEffect, Long>(CastleWarsEffect.class);

    public int bossesKilled;

    public void reset() {
        bossesKilled = 0;
    }

    public boolean hasEffect(CastleWarsEffect effect) {
        Long time = stateMap.get(effect);
        if (time != null) {
            return System.currentTimeMillis() - time < effect.getDuration();
        }
        return false;
    }

    public void setEffect(CastleWarsEffect effect) {
        stateMap.put(effect, System.currentTimeMillis());
    }

    public void resetEffects() {
        stateMap.clear();
    }
}
