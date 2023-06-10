package com.grinder.game.model.commands.impl;

import com.grinder.game.content.skill.skillable.impl.Thieving;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.item.container.bank.BankUtil;
import com.grinder.game.model.item.container.bank.Banking;
import com.grinder.util.Misc;

public class MiscStallTest implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Wipes bank and adds 1,000 misc stall rewards.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        Banking.wipe(player);
        player.getPacketSender().sendMessage("<img=742> Showing all the rewards from Miscellaneous stall!");
        for (int i = 24500; i <= 26700; i++) {
           // int items = Thieving.THIEVING_STALL_RANDOMS[Misc.getRandomInclusive(Thieving.CRYSTAL_CHEST_LOOT.length - 1)];
            BankUtil.addToBank(player, new Item(i, 1));
        }
    }


    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
    }

}
