package com.grinder.game.model.punishment;

import com.grinder.game.GameConstants;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Position;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.interfaces.syntax.EnterSyntax;
import com.grinder.game.task.TaskManager;
import com.grinder.util.DiscordBot;
import com.grinder.util.Misc;

import java.util.Arrays;
import java.util.List;

/**
 * Helper class for applying {@link Punishment punishments}.
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-05-03
 */
class PunishmentUtil {

    static void jailTarget(final Player targetPlayer) {
        targetPlayer.setJailed(true);
        targetPlayer.sendMessage("You have been jailed!");
        targetPlayer.performAnimation(new Animation(735));
        targetPlayer.performGraphic(new Graphic(572));

        if(targetPlayer.getDueling().inDuel())
            targetPlayer.getDueling().loseDuel();

        TaskManager.submit(2, () -> {
            targetPlayer.getMotion().update(MovementStatus.DISABLED);
            targetPlayer.performAnimation(new Animation(804));
            targetPlayer.performGraphic(new Graphic(576));
            SkillUtil.stopSkillable(targetPlayer);
            PlayerExtKt.removeInterfaces(targetPlayer);
            PlayerExtKt.resetInteractions(targetPlayer, true, true);
        });

        TaskManager.submit(4, () -> {
            targetPlayer.getMotion().update(MovementStatus.NONE);
            targetPlayer.moveTo(new Position(3233 + Misc.getRandomInclusive(5), 9799 + Misc.getRandomInclusive(2), 0));
            targetPlayer.getPacketSender().sendJinglebitMusic(194, 25);
            targetPlayer.performAnimation(new Animation(-1));
        });
    }

    static void unjailTarget(Player targetPlayer){
        targetPlayer.setJailed(false);
        targetPlayer.sendMessage("Your jail has been lifted!");
        targetPlayer.moveTo(GameConstants.DEFAULT_DEATH_POSITION);
    }

    static void promptWarning(Player player, String targetName, Player targetPlayer) {
        player.setEnterSyntax(new EnterSyntax() {
            @Override
            public void handleSyntax(Player player, String input) {
                targetPlayer.sendMessage("@red@<img=742> [Staff Warning]: "+input);
                if(DiscordBot.ENABLED){
                    DiscordBot.INSTANCE.sendModMessage(
                            "Staff member '"+player.getUsername()+"' warned player '"+targetName+"'\n" +
                            "with the following message: \n"+input);
                }
            }

            @Override
            public void handleSyntax(Player player, int input) {

            }
        });
        player.getPacketSender().sendEnterInputPrompt("Enter a warning to send to player '"+targetName+"'");
    }

    static void showIpsDialogue(Player player, PunishmentType type, String[] ips, String finalTargetName) {
        final DialogueBuilder builder = new DialogueBuilder(DialogueType.OPTION);

        builder.setOptionTitle("Revoke "+type);

        DialogueBuilder temp = builder;

        int j = 0;

        for(int i = 0; i < ips.length; i++){
            if(i == 3){
                j = 0;
                final DialogueBuilder next = new DialogueBuilder(DialogueType.OPTION);
                next.setOptionTitle("Unban IP");
                temp.option(3, "Next", next::start);
                temp = next;
            }

            int finalJ = j;

            temp.option(j, ips[j], futurePlayer -> {

                PunishmentManager.revoke(futurePlayer, finalTargetName, type, ips[finalJ]);

                futurePlayer.getPacketSender().sendInterfaceRemoval();

                final List<String> list = Arrays.asList(ips);
                list.remove(ips[finalJ]);

                showIpsDialogue(futurePlayer, type, list.toArray(new String[]{}), finalTargetName);
            });
            j++;
        }

        temp.addCancel();

        builder.start(player);
    }
}
