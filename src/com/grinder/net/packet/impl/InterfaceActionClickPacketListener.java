package com.grinder.net.packet.impl;

import com.grinder.game.content.clan.GlobalClanChatManager;
import com.grinder.game.content.miscellaneous.presets.Presetables;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerSettings;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.item.container.bank.Banking;
import com.grinder.game.model.item.container.bank.presets.PresetsManager;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;

public class InterfaceActionClickPacketListener implements PacketListener {

	public static final int MULTIPLY_EXP_DROPS = 55233;

	@Override
	public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {
		int interfaceId = packetReader.readInt();
		int action = packetReader.readByte();

		if(player == null) {
			return;
		}
		if(player.isTeleporting() && player.getTeleportingType() == TeleportType.HOME) {
			player.stopTeleporting();
		}
		if (player.getHitpoints() <= 0 || player.isTeleporting()) {
			return;
		}

		if (PresetsManager.InterfaceButtonClick(player, interfaceId, action)) {
			return;
		}

		if (Banking.handleButton(player, interfaceId, action)) {
			return;
		}
		
		if (GlobalClanChatManager.handleButton(player, interfaceId, action)) {
			return;
		}
		
		if (Presetables.handleButton(player, interfaceId)) {
			return;
		}



		switch (interfaceId) {
			case MULTIPLY_EXP_DROPS:
				player.getAttributes()
						.boolAttr(Attribute.MULTIPLY_XP_DROPS, false)
						.setValue(action == 1);
				PlayerSettings.INSTANCE.updateMultiplyXPDropsState(player);
				break;
		}
	}
}
