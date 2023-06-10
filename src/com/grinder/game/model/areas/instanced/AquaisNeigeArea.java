package com.grinder.game.model.areas.instanced;

import com.grinder.game.content.minigame.aquaisneige.AquaisNeige;
import com.grinder.game.content.minigame.aquaisneige.Wave;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.GraphicHeight;
import com.grinder.game.model.areas.InstancedArea;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;

import java.util.Optional;

import static com.grinder.util.ItemID.WATERSKIN_1_;
import static com.grinder.util.ItemID.WATERSKIN_4_;

public final class AquaisNeigeArea extends InstancedArea {

    private int timer;
    public static final int BLIZZARD_INTERVAL = 35;

    public AquaisNeigeArea(AquaisNeige handler) {
        super(new Boundary(1286, 1340, 3090, 3120));
    }

    public AquaisNeigeArea() {
        super();
    }

    @Override
    public void process(Agent agent) {
        if (agent instanceof Player) {
            var player = agent.getAsPlayer();
            if (++timer % BLIZZARD_INTERVAL == 0 && !player.getAquaisNeige().getCurrentWave().equals(Wave.INADEQUACY)) {
                decreaseOrHit(player);
            }
        }
    }

    public void decreaseOrHit(Player player) {

        var hasProtectiveGear = false;
        var baseBlizzardStat = 150;

        // Camera shake and stop
        player.getPacketSender().sendCameraShake(3, 6, 3, 10);
        player.getPacketSender().sendSound(1670, 5);
        TaskManager.submit(new Task(1) {
            @Override
            public void execute() {
                stop();
                player.getPacketSender().sendCameraNeutrality();
            }
        });

        // Protected
        if (Misc.random(170) + baseBlizzardStat < player.getBonusManager().getDefenceBonus()[4]) {
            player.sendMessage("Your protective gear reduces the chance getting hit by the harmful blizzards.");
            player.getCombat().queue(Damage.create(Misc.random(3)));
            player.getPacketSender().sendGlobalGraphic(new Graphic(1019, GraphicHeight.LOW), player.getPosition());
            hasProtectiveGear = true;
        }

        // Unprotected
        if (!hasProtectiveGear) {
            player.sendMessage("You should get a better ranged protective gear to protect you from the harmful blizzards.");
            player.sendMessage("The Aquais Neige blizzards damages you harshly.");
            player.getCombat().queue(Damage.create(Misc.random(14) + 1));
            player.getPacketSender().sendGlobalGraphic(new Graphic(1019, GraphicHeight.LOW), player.getPosition());
            if (player.getAppearance().isMale()) {
                player.playSound(new Sound(Misc.random(2) % 2 == 0 ? 521 : 519));
            } else {
                player.playSound(new Sound(Misc.random(2) % 2 == 0 ? 509 : 510));
            }
        }

    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void enter(Agent agent) {

        super.enter(agent);
    }

    @Override
    public void leave(Agent agent) {

        super.leave(agent);
    }

    @Override
    public boolean canTeleport(Player player) {
      AquaisNeige.setWave(player,1);
        player.getAquaisNeige().updateLastSession();
        player.getAquaisNeige().clear();
        AquaisNeige.reset(player);
        return true;
    }

    @Override
    public boolean canAttack(Agent attacker, Agent target) {
        return !attacker.isPlayer() || !target.isPlayer();
    }

    @Override
    public boolean canTrade(Player player, Player target) {
        return false;
    }

    @Override
    public boolean isMulti(Agent agent) {
        return true;
    }

    @Override
    public boolean canEat(Player player, int itemId) {
        return true;
    }

    @Override
    public boolean canDrink(Player player, int itemId) {
        return true;
    }

    @Override
    public boolean dropItemsOnDeath(Player player, Optional<Player> killer) {
        return false;
    }

    @Override
    public boolean handleDeath(Player player, Optional<Player> killer) {
        AquaisNeige.onPlayerDeath(player);
        return true;
    }

    @Override
    public void defeated(Player player, Agent agent) {
    }

    @Override
    public void onPlayerRightClick(Player player, Player rightClicked, int option) {
    }


    @Override
    public boolean handleObjectClick(Player player, GameObject obj, int actionType) {
        return false;
    }

    @Override
    public boolean handleDeath(NPC npc) {
        if (players.size() > 0)
            AquaisNeige.onNpcDeath(players.get(0), npc);
        return false;
    }

    @Override
    public boolean isSafeForHardcore() {
        return true;
    }

    @Override
    public boolean isCannonProhibited() {
        return true;
    }
}
