package com.grinder.game.content.minigame.blastfurnace.npcs;

import com.grinder.game.World;
import com.grinder.game.content.minigame.blastfurnace.BlastFurnace;
import com.grinder.game.model.Animation;
import com.grinder.game.model.FacingDirection;
import com.grinder.game.model.Position;
import com.grinder.game.task.TaskManager;

/**
 * @author L E G E N D
 * @date 2/17/2021
 * @time 9:11 AM
 * @discord L E G E N D#4380
 */
public final class Dumpy extends BlastFurnaceNpc {

    private final BlastFurnaceNpc secondNPC;
    private boolean busy;

    public Dumpy(int id, Position position) {
        super(id, position);
        secondNPC = new BlastFurnaceNpc(7387, getPosition());
        World.getNpcAddQueue().add(secondNPC);
    }

    public void addCoke() {
        if (busy) {
            return;
        }
        busy = true;
        setFace(FacingDirection.NORTH);
        secondNPC.performAnimation(new Animation(2441));
        performAnimation(new Animation(2441));
        TaskManager.submit(2, () -> {
            moveTo(getPosition().transform(0, 0, 4));
            if(!getPosition().equals(secondNPC.getPosition())){
                secondNPC.moveTo(getPosition());
            }
            secondNPC.setFace(FacingDirection.NORTH);
        });
        TaskManager.submit(4, () -> {
            secondNPC.setFace(FacingDirection.WEST);
            secondNPC.performAnimation(new Animation(2442));
        });
        TaskManager.submit(6, () -> {
            moveTo(secondNPC.getPosition());
            setFace(secondNPC.getFace());
            secondNPC.moveTo(getPosition().transform(0, 0, 4));
            performAnimation(new Animation(2443));
            BlastFurnace.getStove().addCoal(75);
        });
        TaskManager.submit(10, () -> busy = false);
    }
}

