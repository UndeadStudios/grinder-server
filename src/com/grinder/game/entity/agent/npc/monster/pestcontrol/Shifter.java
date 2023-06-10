package com.grinder.game.entity.agent.npc.monster.pestcontrol;

import com.grinder.game.World;
import com.grinder.game.content.minigame.pestcontrol.PestControlDoorState;
import com.grinder.game.content.minigame.pestcontrol.PestControlDoorsManager;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.monster.Monster;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.GraphicHeight;
import com.grinder.game.model.Position;
import com.grinder.game.model.TileGraphic;
import com.grinder.game.task.TaskManager;
import com.grinder.util.DistanceUtil;
import com.grinder.util.Misc;
import org.jetbrains.annotations.NotNull;

public class Shifter extends Monster {
    public Shifter(int id, @NotNull Position position) {
        super(id, position);

    }

    public void process(Position base, PestControlDoorsManager doorsManager, NPC voidKnight) {
        if (getHitpoints() <= 0)
            return;

        if (Misc.randomChance(45)) {
            Position position = getPosition();

            if (position.isWithinDistance(base.transform(32, 32, 0), 5)) {
                //Teleport around void knight and attack..
                //getMotion().update(MovementStatus.NONE);

                if (Misc.randomChance(2)) {
                    teleportAroundVoidKnight(base);
                }
                if (getMotion().isMoving()) {
                    return;
                }
                if (getCombat().isUnderAttack()) {
                    getMotion().clearSteps();
                    resetEntityInteraction();
                    getCombat().resetTarget();
                    return;
                }
                if (voidKnight != null && !getCombat().isUnderAttack()) {
                    //getMotion().update(MovementStatus.MOVING);

                    if (getCombat().getTarget() != voidKnight)
                    getCombat().initiateCombat(voidKnight);

                    if (!getCombat().canReach(voidKnight, false))
                    getMotion().traceTo(voidKnight.getPosition());
                }
                return;
            }
            //West Side
            else if (position.isWithinDistance(base.transform(18, 33, 0), 3) ) {
                teleportAroundVoidKnight(base);
            }
            else if (position.isWithinDistance(base.transform(12, 33, 0), 4)) {
                teleport(base.transform(18-Misc.random(3), 34-Misc.random(3), 0));
            }
            else if (position.isWithinDistance(base.transform(7, 32, 0), 5)) {
                teleport(base.transform(13-Misc.random(3), 34-Misc.random(3), 0));
            }
            //East Side
            else if (position.isWithinDistance(base.transform(48, 32, 0), 4) ) {
                teleportAroundVoidKnight(base);
            }
            else if (position.isWithinDistance(base.transform(55, 29, 0), 4)) {
                teleport(base.transform(47+Misc.random(1), 31+Misc.random(3), 0));
            }
            //South door
            else if (position.isWithinDistance(base.transform(30, 22, 0), 4) ) {
                teleportAroundVoidKnight(base);
            }
            //South east side
            else if (position.isWithinDistance(base.transform(46, 13, 0), 4)) {
                teleport(base.transform(36+Misc.random(3), 13+Misc.random(3), 0));
            }
            else if (position.isWithinDistance(base.transform(37, 14, 0), 4)) {
                teleport(base.transform(31-Misc.random(3), 24-Misc.random(3), 0));
            }
            //South west side
            else if (position.isWithinDistance(base.transform(23, 12, 0), 4)) {
                teleport(base.transform(27+Misc.random(3), 15+Misc.random(3), 0));
            }
            else if (position.isWithinDistance(base.transform(27, 14, 0), 4)) {
                teleport(base.transform(31-Misc.random(3), 24-Misc.random(3), 0));
            }
        }
    }

    private void teleport(Position teleportPosition) {
        Position position = getPosition();
        if (position.sameAs(teleportPosition))
            return;

        if (!position.isWithinDistance(teleportPosition, 1)) {
            World.spawn(new TileGraphic(position, new Graphic(654, GraphicHeight.MIDDLE)));
            World.spawn(new TileGraphic(teleportPosition, new Graphic(654, GraphicHeight.MIDDLE)));
/*            if (getCombat().hasTarget() && getCombat().getTarget() instanceof Player) {
                Player targetPlayer = (Player) getCombat().getTarget();
                targetPlayer.getPacketSender().sendGraphic(new Graphic(654, GraphicHeight.MIDDLE), position);
                targetPlayer.getPacketSender().sendGraphic(new Graphic(654, GraphicHeight.MIDDLE), teleportPosition);
            }*/
        }
        getCombat().reset(true);
        if (getCombat().hasTarget())
        getCombat().getTarget().getCombat().reset(true, true);

        setTeleportPosition(teleportPosition);
    }

    private void teleportAroundVoidKnight(Position base) {
        Position teleportTo = base.transform(30+Misc.random(5), 30+Misc.random(5), 0);
        if (teleportTo.sameAs(base.transform(32, 32, 0))) {
            teleportTo = base.transform(32, 33, 0);
        }
        teleport(teleportTo);
    }

    private boolean ifAnyDoorsOpen(PestControlDoorsManager doorsManager) {
        return doorsManager.getWestDoorState() == PestControlDoorState.BROKEN ||
                doorsManager.getWestDoorState() == PestControlDoorState.HALF_BROKEN ||
                doorsManager.getWestDoorState() == PestControlDoorState.OPENED ||
                doorsManager.getEastDoorState() == PestControlDoorState.BROKEN ||
                doorsManager.getEastDoorState() == PestControlDoorState.HALF_BROKEN ||
                doorsManager.getEastDoorState() == PestControlDoorState.OPENED ||
                doorsManager.getSouthDoorState() == PestControlDoorState.BROKEN ||
                doorsManager.getSouthDoorState() == PestControlDoorState.HALF_BROKEN ||
                doorsManager.getSouthDoorState() == PestControlDoorState.OPENED;
    }

    @Override
    public int attackRange(@NotNull AttackType type) {
        return 1;
    }
}
