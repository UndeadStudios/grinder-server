package com.grinder.net.packet.impl;

import com.grinder.game.World;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.entity.agent.movement.task.WalkToAction;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;
import com.grinder.util.Executable;


public class TradeRequestPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {
        int index = packetReader.readLEShort();
        if (index > World.getPlayers().capacity() || index < 0) {
            return;
        }
       // System.out.println("index: " + index);
        Player player2 = World.getPlayers().get(index);
        //if (player != null)
        //System.out.println(player2.getUsername());
        if (player == null
                || player.getHitpoints() <= 0
                || !player.isRegistered()
                || player2 == null || player2.getHitpoints() <= 0
                || !player2.isRegistered()) {
            player.getMotion().clearSteps();
            return;
        }
        if (player.BLOCK_ALL_BUT_TALKING) {
        	return;
        }
        if (player.isInTutorial()) {
        	return;
        }
    	if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
    		player.getPacketSender().sendMessage("You can't initiate trades when you're AFK!", 1000);
    		return;
    	}
    	if (player2.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
    		player2.getPacketSender().sendQuickChat("I'm afk bitch, leave me alone..");
    		return;
    	}
        //PlayerRights rights = player.getRights();
        /*if (rights == PlayerRights.ADMINISTRATOR) {
        	player.getPacketSender().sendMessage("<img=742> Administrators aren't allowed to trade!");
        	return;
        }
        if (player2.getRights().equals(PlayerRights.ADMINISTRATOR)) {
        	player.getPacketSender().sendMessage("<img=742> Administrators aren't allowed to trade!");
        	return;
        }*/
        SkillUtil.stopSkillable(player);
        player.getCombat().reset(false);
        player.setEntityInteraction(player2);
        player.setWalkToTask(new WalkToAction(player, player2, new Executable() {
            @Override
            public void execute() {
                player.setPositionToFace(player2.getPosition());
                if (player.getMotion().isFollowing(player2)) {
            		player.getMotion().followTarget(null);
                    player.setEntityInteraction(null);
                }

                if (player.busy()) {
                    player.getPacketSender().sendMessage("You can't do that right now.");
                    return;
                }
                if (player.getMinigame() != null) {
                    player.sendMessage("You cannot trade players while in a minigame.");
                    return;
                }
                if (player.getArea() == AreaManager.MINIGAME_LOBBY) {
                    player.sendMessage("You cannot trade players while in a minigame.");
                    return;
                }

                if (player2.busy()) {
                    String msg = "That player is currently busy at the moment.";

                    if (player2.getStatus() == PlayerStatus.TRADING) {
                        //msg = "The other player is currently in trade with someone else.";
                        msg = "The other player is busy at the moment.";
                    }

                    player.getPacketSender().sendMessage(msg);
                    return;
                }

                if (player.getArea() != null) {
                    if (!player.getArea().canTrade(player, player2)) {
                        player.getPacketSender().sendMessage("You can't trade here.");
                        return;
                    }
                }

                if (player.getLocalPlayers().contains(player2)) {
                    player.getTrading().requestTrade(player2);
                }
            }
        }, WalkToAction.Policy.EXECUTE_WHEN_IN_DISTANCE, WalkToAction.Policy.RECALCULATE_IF_TARGET_MOVES));

    }
}
