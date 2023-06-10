package com.grinder.game.model.areas.instanced;

import com.grinder.game.content.minigame.blastfurnace.BlastFurnace;
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.GraphicHeight;
import com.grinder.game.model.areas.Area;
import com.grinder.game.model.areas.AreaListener;
import com.grinder.game.model.sound.Sound;
import com.grinder.util.Misc;

import java.util.Optional;

public final class ApeAtollDungeon extends Area implements AreaListener {

    private int timer;
    public static final int ROCKFALL_INTERVAL = 25;

    public ApeAtollDungeon() {
        super(
                new Boundary(2686, 2815, 9089, 9155)
        );
    }


    @Override
    public void process(Agent agent) {
        if (agent instanceof Player) {
            var player = agent.getAsPlayer();
            if (++timer % ROCKFALL_INTERVAL == 0) {
                rockFallDamage(player);
            }
        }
    }

    @Override
    public void enter(Agent agent) {
            super.enter(agent);
    }

    @Override
    public void leave(Agent agent) {
            super.leave(agent);
    }

    public void rockFallDamage(Player player) {
        player.getPacketSender().sendSound(1670, 5);
        player.say("Ouch!");
/*        if (!player.hasActivePrayer(PrayerHandler.PROTECT_FROM_MELEE)) {
            player.getCombat().queue(Damage.create(Misc.random(5)));
        } else {
            player.getCombat().queue(Damage.create(Misc.random(1)));
        }*/
        player.getCombat().queue(Damage.create(Misc.random(5)));
        player.getPacketSender().sendGlobalGraphic(new Graphic(60, GraphicHeight.LOW), player.getPosition());
        if (player.getAppearance().isMale()) {
            player.playSound(new Sound(Misc.random(2) % 2 == 0 ? 521 : 519));
        } else {
            player.playSound(new Sound(Misc.random(2) % 2 == 0 ? 509 : 510));
        }

    }

    @Override
    public void defeated(Player player, Agent agent) {

    }

    @Override
    public void onPlayerRightClick(Player player, Player rightClicked, int option) {

    }

    @Override
    public boolean isMulti(Agent agent) {
        return true;
    }

    @Override
    public boolean canTeleport(Player player) {
        return true;
    }

    @Override
    public boolean canAttack(Agent attacker, Agent target) {
        return !attacker.isPlayer() || !target.isPlayer();
    }

    @Override
    public boolean canTrade(Player player, Player target) {
        return true;
    }

    @Override
    public boolean canDrink(Player player, int itemId) {
        return true;
    }

    @Override
    public boolean canEat(Player player, int itemId) {
        return true;
    }

    @Override
    public boolean dropItemsOnDeath(Player player, Optional<Player> killer) {
        return true;
    }

    @Override
    public boolean handleObjectClick(Player player, GameObject obj, int actionType) {
        return false;
    }

    @Override
    public boolean handleDeath(Player player, Optional<Player> killer) {
        return false;
    }

    @Override
    public boolean handleDeath(NPC npc) {
        return false;
    }
}