package com.grinder.game.content.minigame.chamberoxeric.room.mutadiles.npc;

import com.grinder.game.content.minigame.chamberoxeric.room.mutadiles.MeatTree;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.model.Position;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class MutadileNPC extends NPC {

    private static final Position TREE_POSITION = new Position(3305, 5323);

    public MutadileState state = MutadileState.ATTACKING;

    private MeatTree meatTree;

    private int healedAmount;

    private int eatTick;

    public MutadileNPC(int id, Position position, MeatTree meatTree) {
        super(id, position);
        this.meatTree = meatTree;
    }

    @Override
    public void pulse() {
        if(state == MutadileState.ATTACKING && healedAmount < 3 && meatTree.health > 0) {
            int healingHP = (int) (getMaxHitpoints() * 0.60);

            if (getHitpoints() <= healingHP) {
                state = MutadileState.HEALING;
                healedAmount++;
            }
        } else if(state == MutadileState.HEALING) {
            if(getPosition().getDistance(TREE_POSITION) > 2) {
                getMotion().traceTo(TREE_POSITION);
            } else {
                if(eatTick++ % 10 == 0) {
                    if (meatTree.health > 0) {
                        performAnimation(LargeMutadileCombat.MELEE_ATTACK);
                        meatTree.health -= 10;
                        heal(10);
                    } else {
                        state = MutadileState.ATTACKING;
                    }

                    if (getHitpoints() > getMaxHitpoints() * 0.90) {
                        state = MutadileState.ATTACKING;
                    }
                }
            }
        }
    }
}
