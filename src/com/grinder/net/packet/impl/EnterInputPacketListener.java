package com.grinder.net.packet.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.interfaces.syntax.impl.BankQuantityX;
import com.grinder.game.model.interfaces.syntax.impl.BankX;
import com.grinder.game.model.interfaces.syntax.impl.WithdrawBankX;
import com.grinder.net.packet.PacketConstants;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;

import java.util.function.Consumer;

/**
 * This packet manages the input taken from chat box interfaces that allow
 * input, such as withdraw x, bank x, enter name of friend, etc.
 *
 * @author Gabriel Hannason
 */

public class EnterInputPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {



        switch (packetOpcode) {
            case PacketConstants.ENTER_SYNTAX_OPCODE:
                String name = packetReader.readString();
                if (player == null || player.getHitpoints() <= 0) {
                    return;
                }

                if (name == null)
                    return;

                if(player.getInputHandler() != null) {
                    Consumer<String> inputHandler = (Consumer<String>) player.getInputHandler();
                    inputHandler.accept(name);
                    player.removeInputHandler();
                } else {
                    // use old String input handling
                    if (player.getEnterSyntax() != null) {
                        player.getEnterSyntax().handleSyntax(player, name);
                    }
                }

                break;
            case PacketConstants.ENTER_AMOUNT_OPCODE:
                int amount = packetReader.readInt();
                if (player == null || player.getHitpoints() <= 0) {
                    return;
                }
                boolean canEnterZero = player.getEnterSyntax() instanceof BankX || player.getEnterSyntax() instanceof BankQuantityX || player.getEnterSyntax() instanceof WithdrawBankX;
                if (amount <= 0 && !canEnterZero)
                    return;

                if(player.getInputHandler() != null) {
                    Consumer<Integer> inputHandler = (Consumer<Integer>) player.getInputHandler();
                    inputHandler.accept(amount);
                    player.removeInputHandler();
                } else {
                    // use old integer input handling
                    if (player.getEnterSyntax() != null) {
                        player.getEnterSyntax().handleSyntax(player, amount);
                    }

                    player.setEnterSyntax(null);
                }

                break;
        }
    }
}
