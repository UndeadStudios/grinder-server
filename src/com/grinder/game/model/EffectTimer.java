package com.grinder.game.model;

public enum EffectTimer {

    VENGEANCE(157),
    FREEZE(158),
    ANTIFIRE(159),
    OVERLOAD(160),
    TELE_BLOCK(161),
    CHARGE(950),
    DEATH_ITEMS_DESPAWN(951),
    VOTING_BONUS(1030),
    AGGRESSIVITY(1031),
    ANTIPOISON(1029),
    ANTI_VENOM(1029); //TODO venom sprite

    private int clientSprite;

    EffectTimer(int clientSprite) {
        this.clientSprite = clientSprite;
    }

    public int getClientSprite() {
        return clientSprite;
    }

    public void setClientSprite(int sprite) {
        this.clientSprite = sprite;
    }
}
