package com.grinder.game.model.commands.impl;

import com.grinder.game.World;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.pvm.MonsterKillTracker;
import com.grinder.game.entity.agent.npc.NPCDropGenerator;
import com.grinder.game.entity.agent.npc.monster.boss.impl.god.zaros.Nex;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.model.Direction;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.GraphicHeight;
import com.grinder.game.model.TileGraphic;
import com.grinder.game.model.areas.Area;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.commands.Command;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.bank.BankUtil;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;
import com.grinder.util.NpcID;

public class TestCommand implements Command {

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Used for testing purposes.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
            //player.getPacketSender().sendJinglebitMusic(Integer.parseInt(parts[1]), 0);


            player.sendMessage("Test!");
          //  player.sendMessage("Your opponent coords" + player.getCombat().getOpponent().getPosition() +" and " + player.getCombat().getOpponent().isAlive() + " and " + player.getCombat().getOpponent().getAsNpc().getPosition());
         //    AreaManager.checkAreaChanged(player.getCombat().getOpponent().getAsNpc());
    //    AreaManager.sequence(player.getCombat().getOpponent().getAsNpc());
            // TODO: Print npc count within 3 tiles
/*        for (int i = 0; i < Integer.parseInt(parts[1]); i++){
            NPCDropGenerator.start(player, new Nex(Integer.parseInt(parts[2])));
            MonsterKillTracker.track(player, new Nex(Integer.parseInt(parts[2])));
        }*/
//            World.spawn(new TileGraphic(player.getPosition(), new Graphic(721, 20, GraphicHeight.MIDDLE)));


//        new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(Integer.parseInt(parts[1]))
//                .setText("Hey, you've only got four legs. How do you manage?", "Don't you fall over?")
//                .add(DialogueType.PLAYER_STATEMENT).start(player);
        //TaskManager.cancelTasks(this);
		}

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER);
    }
}
