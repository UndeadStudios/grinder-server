package com.grinder.game.content.skill.skillable.impl.cons;

/**
 * @author Simplex
 * @since Mar 27, 2020
 */
public enum ButlerType {
    JACK(4235, 20, 500, 6, 60),
    MAID(4237, 25, 1000, 10, 30),
    COOK(4239, 30, 3000, 16, 17),
    BUTLER(4241, 40, 5000, 20, 12),
    DEMON_BUTLER(4243, 50, 10000, 26, 7),
    ;
    private int npcId, consLevel, loanCost, inventory;
    private double tripSeconds;

    private ButlerType(int npcId, int consLevel, int loanCost, int inventory, double tripSeconds) {
        this.setNpcId(npcId);
        this.setConsLevel(consLevel);
        this.setLoanCost(loanCost);
        this.setInventory(inventory);
        this.setTripSeconds(tripSeconds * 1.4D);
    }

    public static ButlerType forId(int npcId) {
        for (ButlerType b : values())
            if (b.getNpcId() == npcId)
                return b;
        return null;
    }

    public double getTripSeconds() {
        return tripSeconds;
    }

    public void setTripSeconds(double tripSeconds) {
        this.tripSeconds = tripSeconds;
    }

    public int getConsLevel() {
        return consLevel;
    }

    public void setConsLevel(int consLevel) {
        this.consLevel = consLevel;
    }

    public int getLoanCost() {
        return loanCost;
    }

    public void setLoanCost(int loanCost) {
        this.loanCost = loanCost;
    }

    public int getInventory() {
        return inventory;
    }

    public void setInventory(int inventory) {
        this.inventory = inventory;
    }

    public int getNpcId() {
        return npcId;
    }

    public void setNpcId(int npcId) {
        this.npcId = npcId;
    }
}
