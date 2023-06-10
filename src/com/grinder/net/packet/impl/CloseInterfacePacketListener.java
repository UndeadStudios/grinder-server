package com.grinder.net.packet.impl;

import com.grinder.game.content.miscellaneous.WelcomeManager;
import com.grinder.game.content.miscellaneous.WelcomeManager.WelcomeStage;
import com.grinder.game.content.gambling.flower_poker.PlantFlowerTask;
import com.grinder.game.content.gambling.blackjack.BlackJack;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;

public class CloseInterfacePacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {

    	if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false) )
    		return;

		if (player.getInterfaceId() == 12140 && EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_EXPERIENCE_DELAY, false)) {
			EntityExtKt.setBoolean(player, Attribute.HAS_PENDING_EXPERIENCE_DELAY, false, false);
			player.BLOCK_ALL_BUT_TALKING = false;
		}

    	if(player.isBlockingDisconnect()) {
    		if(player.getInterfaceId() == BlackJack.INTERFACE_ID || player.getInterfaceId() == PlantFlowerTask.INTERFACE_ID){
				player.sendMessage("You can't close the interface during a gamble session!");
				player.getPacketSender().sendInterface(player.getInterfaceId());
				return;
			}
		}

    	if (player.getTeleportToCaster() != null) {
    		player.getPacketSender().sendInterfaceRemoval();
    		player.dispatchInterfaceClose();
    		player.setTeleportToCaster(null);
    		player.setTeleportDestination(null);
    		return;
    	}
    	if (player.isInTutorial() && player.getAppearance().canChangeAppearance()) {
            player.getPacketSender().sendInterface(3559);
    		return;
    	}
    	if (player.isInTutorial()) {
    		player.getPacketSender().sendInterfaceRemoval();
    		if (EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 1) {
    			WelcomeManager.welcome(player, WelcomeStage.TUTORIAL1);
			return;
    		} else if (EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 2) {
    			WelcomeManager.welcome(player, WelcomeStage.TUTORIAL2);
    			return;
			} else if (EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 3) {
				WelcomeManager.welcome(player, WelcomeStage.TUTORIAL3);
				return;
			} else if (EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 4) {
				WelcomeManager.welcome(player, WelcomeStage.TUTORIAL4);
				return;
			} else if (EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 5) {
				WelcomeManager.welcome(player, WelcomeStage.TUTORIAL5);
				return;
			} else if (EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 10) {
				WelcomeManager.welcome(player, WelcomeStage.TUTORIAL10);
				return;
/*			} else if (player.tutorialStage == 26) {
				WelcomeManager.welcome(player, WelcomeStage.TUTORIAL26);
				return;*/
			} else if (EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 27) {
				WelcomeManager.welcome(player, WelcomeStage.TUTORIAL27);
				return;
			}
    		
    	}
        player.getPacketSender().sendInterfaceRemoval();
		player.dispatchInterfaceClose();

    }
}
