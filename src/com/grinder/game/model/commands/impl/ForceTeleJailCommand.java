package com.grinder.game.model.commands.impl;

import java.util.Optional;

import com.grinder.game.World;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Graphic;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.Position;
import com.grinder.game.model.commands.Command;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueOptions;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.Misc;
import com.grinder.util.timing.TimerKey;

import static com.grinder.game.entity.agent.player.PlayerRights.*;

public class ForceTeleJailCommand implements Command {

    @Override
    public String getSyntax() {
        return "[playerName]";
    }

    @Override
    public String getDescription() {
        return "Force teleport a player's account to jail.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (command.length() <= 9) {
            player.sendMessage("Wrong usage of the command!");
            return;
        }
        final String targetName = command.substring(parts[0].length() + 1);

        Optional<Player> plr = World.findPlayerByName(targetName);

        if (!plr.isPresent()) {
            player.getPacketSender().sendMessage(command.substring(parts[0].length() + 1) + " appears to be offline.");
            return;
        }
        if (plr.get().getUsername() == null) {
            player.getPacketSender().sendMessage("Invalid player name.");
            return;
        }
        if (plr.get().isBlockingDisconnect()) {
            player.getPacketSender().sendMessage("<img=779> You can't use this command because " + plr.get().getUsername() +" is in a busy state!");
            return;
        }
        if (plr.get().isJailed()) {
            player.getPacketSender().sendMessage(plr.get().getUsername() + " is already jailed!");
            return;
        }
        if (plr.get().getStatus() == PlayerStatus.TRADING) {
            player.sendMessage("<img=742> The player that you're trying to teleport is in a busy state.");
            return;
        }
        if (plr.get().getStatus() == PlayerStatus.BANKING) {
            player.sendMessage("<img=742> The player that you're trying to teleport is in a busy state.");
            return;
        }
        if (plr.get().getStatus() == PlayerStatus.DICING) {
            player.sendMessage("<img=742> The player that you're trying to teleport is in a busy state.");
            return;
        }
        if (plr.get().getStatus() == PlayerStatus.SHOPPING) {
            player.sendMessage("<img=742> The player that you're trying to teleport is in a busy state.");
            return;
        }
        if (plr.get().getStatus() == PlayerStatus.DUELING) {
            player.sendMessage("<img=742> The player that you're trying to teleport is in a busy state.");
            return;
        }
        if (!PlayerUtil.isDeveloper(player) && plr.get().busy()) {
            player.sendMessage("<img=742> The player that you're trying to teleport is in a busy state.");
            return;
        }
        jail(player, plr);
    }

    private static void jail(Player player, Optional<Player> plr) {
        if (plr.get().getCombat().isInCombat() || plr.get().getWildernessLevel() > 0 || plr.get().getDueling().inDuel() || plr.get().getStatus() == PlayerStatus.TRADING
                || plr.get().getTimerRepository().has(TimerKey.COMBAT_COOLDOWN) || plr.get().getCombat().isBeingAttacked()) {


            player.setDialogue(DialogueManager.getDialogues().get(10));
            DialogueManager.sendStatement(player, "" + plr.get().getUsername() + " seems to be busy. Do you still want to teleport this player to jail?");
            player.setDialogueOptions(new DialogueOptions() {
                @Override
                public void handleOption(Player player, int option) {
                    switch (option) {
                        case 1:
                            player.getPacketSender().sendMessage("<img=742> You have teleported @dre@" + plr.get().getUsername() + "</col> to jail!");
                            player.getPacketSender().sendInterfaceRemoval();
                            plr.get().getPacketSender().sendMessage("<img=742> You have been teleported to jail by @dre@" + PlayerUtil.getImages(player) +"" + player.getUsername() + "</col>!");
                            plr.get().performAnimation(new Animation(735));
                            plr.get().performGraphic(new Graphic(572));
                            if (plr.get().getDueling().inDuel()) {
                                plr.get().getDueling().loseDuel();
                            }
                            TaskManager.submit(new Task(2) {
                                @Override
                                public void execute() {
                                    plr.get().performAnimation(new Animation(804));
                                    plr.get().performGraphic(new Graphic(576));
                                    SkillUtil.stopSkillable(plr.get());
                                    PlayerExtKt.resetInteractions(plr.get(), true, true);
                                    PlayerExtKt.removeInterfaces(plr.get());
                                    stop();
                                }
                            });
                            TaskManager.submit(new Task(4) {
                                @Override
                                public void execute() {
                                    plr.get().moveTo(new Position(3233 + Misc.getRandomInclusive(5), 9799 + Misc.getRandomInclusive(2), plr.get().getIndex() * 4));
                                    player.moveTo(new Position(3233 + Misc.getRandomInclusive(5), 9799 + Misc.getRandomInclusive(2), plr.get().getIndex() * 4));
                                    plr.get().performAnimation(new Animation(-1));
                                    stop();
                                }
                            });
                            break;
                        case 2:
                            PlayerExtKt.removeInterfaces(player);
                            break;
                    }
                }
            });
        } else {
            player.getPacketSender().sendMessage("<img=742> You have teleported @dre@" + plr.get().getUsername() + "</col> to jail!");
            if (plr.isPresent()) {
                plr.get().getPacketSender().sendMessage("<img=742> You have been teleported to jail by @dre@" + PlayerUtil.getImages(player) +"" + player.getUsername() + "</col>!");
                plr.get().performAnimation(new Animation(735));
                plr.get().performGraphic(new Graphic(572));
                if (plr.get().getDueling().inDuel()) {
                    plr.get().getDueling().loseDuel();
                }
                TaskManager.submit(new Task(2) {
                    @Override
                    public void execute() {
                        plr.get().performAnimation(new Animation(804));
                        plr.get().performGraphic(new Graphic(576));
                        SkillUtil.stopSkillable(plr.get());
                        plr.get().getCombat().reset(false);
                        plr.get().getPacketSender().sendInterfaceRemoval();
                        plr.get().getMotion().clearSteps();
                        plr.get().getPacketSender().sendMinimapFlagRemoval();
                        stop();
                    }
                });
                TaskManager.submit(new Task(4) {
                    @Override
                    public void execute() {
                        plr.get().moveTo(new Position(3233 + Misc.getRandomInclusive(5), 9799 + Misc.getRandomInclusive(2), plr.get().getIndex() * 4));
                        player.moveTo(new Position(3233 + Misc.getRandomInclusive(5), 9799 + Misc.getRandomInclusive(2), plr.get().getIndex() * 4));
                        plr.get().performAnimation(new Animation(-1));
                        stop();
                    }
                });
            }
        }
    }

    @Override
    public boolean canUse(Player player) {
        return player.getRights().anyMatch(SERVER_SUPPORTER, MODERATOR, GLOBAL_MODERATOR, ADMINISTRATOR, DEVELOPER, CO_OWNER, OWNER);
    }

}
