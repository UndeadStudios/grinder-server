package com.grinder.net.packet.impl;

import com.grinder.game.World;
import com.grinder.game.content.item.MorphItems;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.entity.agent.combat.PlayerCombat;
import com.grinder.game.entity.agent.combat.attack.weapon.Weapon;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterface;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterfaces;
import com.grinder.game.entity.agent.movement.task.WalkToAction;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses;
import com.grinder.game.model.GraphicHeight;
import com.grinder.game.model.sound.Sounds;
import com.grinder.util.Executable;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.model.projectile.ProjectileTemplate;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;


public class FifthPlayerOptionPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {

        final int index = packetReader.readLEShort();

        if (index > World.getPlayers().capacity() || index < 0)
            return;

        final Player targetPlayer = World.getPlayers().get(index);

        if (player == null
                || player.getHitpoints() <= 0
                || !player.isRegistered()
                || targetPlayer == null || targetPlayer.getHitpoints() <= 0
                || !targetPlayer.isRegistered()) {
            player.getMotion().clearSteps();
            return;
        }
        if (player.BLOCK_ALL_BUT_TALKING) {
        	return;
        }
        if (player.isInTutorial()) {
        	return;
        }

        if (!MorphItems.INSTANCE.notTransformed(player, "do this", true, true))
            return;

    	if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
    		player.getPacketSender().sendMessage("You can't do this trades when you're AFK!", 1000);
    		return;
    	}

        SkillUtil.stopSkillable(player);
        player.getCombat().reset(false);
		player.setPositionToFace(targetPlayer.getPosition());
//		if(!RegionManager.canReachAttack(player, targetPlayer, 8)) {
//		    player.getMotion().reset();
//		    player.sendMessage("You can't reach "+targetPlayer.getUsername()+" from here!");
//            return;
//        }

        player.setEntityInteraction(targetPlayer);
        player.setWalkToTask(new WalkToAction<>(player, targetPlayer, 8, 0, () -> {
            if (player.getMotion().isFollowing(targetPlayer)) {
                player.getMotion().followTarget(null);
                player.setEntityInteraction(null);
            }
            player.getMotion().reset();

            if (player.busy()) {
                player.getPacketSender().sendMessage("You can't do that right now.");
                return;
            }

            if (targetPlayer.busy()) {
                String msg = "That player is currently busy at the moment.";

                if (targetPlayer.getStatus() == PlayerStatus.TRADING) {
                    //msg = "The other player is currently in trade with someone else.";
                    msg = "The other player is busy at the moment.";
                }

                player.getPacketSender().sendMessage(msg);
                return;
            }

            if (player.getLocalPlayers().contains(targetPlayer)) {
                final PlayerCombat combat = player.getCombat();
                final Weapon weapon  = combat.getWeapon();
                if(weapon.getWeaponInterface() == WeaponInterface.SNOWBALL){

                    final int snowBallCount = player.getEquipment().getAmount(ItemID.SNOWBALL);

                    if(snowBallCount <= 0)
                        return;

                    if(snowBallCount - 1 == 0) {
                        EquipPacketListener.resetWeapon(player);
                        player.getCombat().reset(false);
                        EquipmentBonuses.update(player);
                        player.getEquipment().refreshItems();
                        WeaponInterfaces.INSTANCE.assign(player);
                        player.updateAppearance();
                        player.sendMessage("You have run out of snow balls.");
                    }

                    player.getEquipment().delete(ItemID.SNOWBALL, 1);


                    //player.sendMessage("some message perhaps");
                    player.setEntityInteraction(targetPlayer);
                    player.BLOCK_ALL_BUT_TALKING = true;
                    targetPlayer.getPacketSender().sendAreaPlayerSound(2708);
                    player.performGraphic(new Graphic(860, GraphicHeight.LOW));
                    player.performAnimation(new Animation(5063));
                    final ProjectileTemplate snowBallProjectileTemplate = ProjectileTemplate.builder(861).setCurve(12).setStartHeight(44)
                            .setEndHeight(11)
                            .setSpeed(10)
                            .setDelay(60).build();
                    final Projectile snowBallProjectile = new Projectile(player, targetPlayer, snowBallProjectileTemplate);
                    snowBallProjectile.sendProjectile();
                    snowBallProjectile.onArrival(() -> {
                        player.setEntityInteraction(null);
                        player.BLOCK_ALL_BUT_TALKING = false;
                        targetPlayer.performAnimation(new Animation(targetPlayer.getBlockAnim()));
                        if (Misc.random(3) == 1) {
                            targetPlayer.getPacketSender().sendSound(Sounds.getBlockSound(targetPlayer, 0));
                        } else {
                            targetPlayer.say("Ouch!");
                            targetPlayer.getPacketSender().sendSound(3292);
                        }
                        targetPlayer.performGraphic(new Graphic(862));
                    });
                }
            }
        }, WalkToAction.Policy.EXECUTE_ON_LINE_OF_SIGHT, WalkToAction.Policy.RECALCULATE_IF_TARGET_MOVES));

    }
}
