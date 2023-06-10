package com.grinder.net.packet.impl;

import com.grinder.Config;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.clan.GlobalClanChatManager;
import com.grinder.game.content.gambling.blackjack.BlackJack;
import com.grinder.game.content.item.MorphItems;
import com.grinder.game.content.miscellaneous.*;
import com.grinder.game.content.miscellaneous.presets.Presetables;
import com.grinder.game.content.pvm.BossDropTables;
import com.grinder.game.content.pvm.ItemDropFinderInterface;
import com.grinder.game.content.pvm.MonsterKillTracker;
import com.grinder.game.content.pvp.WildernessScoreBoard;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.content.skill.skillable.impl.Smithing;
import com.grinder.game.content.skill.skillable.impl.crafting.Crafting;
import com.grinder.game.content.skill.skillable.impl.crafting.GlassBlowing;
import com.grinder.game.content.skill.skillable.impl.crafting.HideTanData;
import com.grinder.game.content.skill.skillable.impl.magic.InteractiveSpellCasting;
import com.grinder.game.content.skill.skillable.impl.magic.SpellCasting;
import com.grinder.game.content.skill.skillable.impl.magic.Teleporting;
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler;
import com.grinder.game.content.skill.skillable.impl.slayer.SlayerManager;
import com.grinder.game.content.skill.skillable.impl.slayer.SlayerRewards;
import com.grinder.game.content.task_new.PlayerTaskManager;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterfaces;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.message.decoder.ButtonClickMessageDecoder;
import com.grinder.game.message.impl.ButtonClickMessage;
import com.grinder.game.model.ButtonActions;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.item.container.bank.Banking;
import com.grinder.game.model.item.container.bank.presets.PresetsManager;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;
import com.grinder.net.packet.interaction.PacketInteractionManager;
import com.grinder.util.debug.DebugManager;

/**
 * This packet listener manages a button that the player has clicked upon.
 *
 * @author Gabriel Hannason
 */

public class ButtonClickPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {

        final ButtonClickMessageDecoder decoder = new ButtonClickMessageDecoder();
        final ButtonClickMessage message = decoder.decode(packetReader.getPacket());
        final int buttonId = message.getButtonId();

        if (player.isTeleporting()) {
            if (player.getTeleportingType() == TeleportType.HOME)
                player.stopTeleporting();
            if (!PrayerHandler.negateButton(buttonId))
                return;
        }

        if (player.getHitpoints() <= 0)
            return;

        player.sendDevelopersMessage("Button clicked: " + buttonId + ".");

        DebugManager.debug(player, "button", "button: "+buttonId);

        if(PacketInteractionManager.handleButtonInteraction(player, buttonId)) {
            return;
        }

        PlayerTaskManager.claimReward(player, buttonId);

        // This is handled by itself only for when the player is in the tutorial and showing the gamble interface
        // So if they click any button it will ask them to close the interface.
        // More reference in WelcomeManager, DialogueManager, ExitInterfacePacket
        if (tutorialButtonClick(player, buttonId)) {
            player.sendMessage("You must exit the interface to continue.");
            return;
        }

        if (player.presetInterfaceOpen)
        {
            if (PresetsManager.ClickButton(player, buttonId))
            {
                return;
            }
        }

        if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
            player.getPacketSender().sendMessage("You can't do that while AFK!", 2000);
            return;
        }

        if (ButtonActions.INSTANCE.handleClick(player, buttonId))
            return;

        if (BlackJack.negateButton(player, buttonId))
            return;

        AchievementManager.handleButton(player, buttonId);

        if (handlers(player, buttonId))
            return;

        // TODO: find what this button is for
        if (buttonId == 10162)
            player.getPacketSender().sendInterfaceRemoval();
    }

    private static boolean tutorialButtonClick(Player player, int buttonId) {
        return player.isInTutorial() && EntityExtKt.getInt(player, Attribute.TUTORIAL_STAGE, 0) == 10
                && (buttonId == 60021 || buttonId == 60022 || buttonId == 60038 ||
                buttonId == 60039 || buttonId == 60040 || buttonId == 60041 || buttonId == 60042);
    }

    public static boolean handlers(Player player, int buttonId) {

        if(QuestManager.display(player, buttonId)) {
            return true;
        }

        if(!player.BLOCK_ALL_BUT_TALKING){
            if (Teleporting.handleButton(player, buttonId)) return true;
            if(!player.isInTutorial()) {
                if (InteractiveSpellCasting.handleSpell(player, buttonId)) return true;
                if (Banking.handleButton(player, buttonId, 0)) return true;
                if (Emotes.doEmote(player, buttonId)) return true;
                if (GlobalClanChatManager.handleButton(player, buttonId, 0)) return true;
            }
            if(!player.isInTutorial()) {
                if (StaffCommandsTab.handleButton(player, buttonId)) return true;
                if (SlayerManager.handleButton(player, buttonId)) return true;
                if (SpellCasting.toggleAutomaticCasting(player, buttonId)) return true;
            }
        }

        if (player.getBankpin().handleDigitButton(buttonId)) return true;
        if(!player.isInTutorial()) {
            if (WeaponInterfaces.INSTANCE.handleButton(player, buttonId)) return true;
            if (SlayerRewards.purchase(player, buttonId)) return true;
        }
        if (WelcomeManager.handleButton(player, buttonId)) return true;
        if (Rules.click(player, buttonId)) return true;
        if (!MorphItems.INSTANCE.notTransformed(player, "", false, true))
            return false;
        if (player.isInTutorial()) return false;
        if(!player.BLOCK_ALL_BUT_TALKING) {
            if (player.getQuickPrayers().handleButton(buttonId)) return true;
            if (QuestTab.click(player, buttonId)) return true;
        }
        if (player.getCreationMenu().isPresent() && player.getCreationMenu().get().handleButton(buttonId)) return true;
        if (Smithing.handleButton(player, buttonId)) return true;
        if (!Config.PRODUCTION && Crafting.clickButton(player, buttonId)) return true;
        if (HideTanData.clickButton(player, buttonId)) return true;
        if (Notes.handleButton(player, buttonId)) return true;
        if (RecentUpdates.handleButton(player, buttonId)) return true;
        if (MonsterKillTracker.handleButton2(player, buttonId)) return true;
        if (AttributeManager.handleButton(player, buttonId)) return true;
        if (MonsterKillTracker.handleButton(player, buttonId)) return true;
        if (Broadcast.handleButton(player, buttonId)) return true;
        if (PlayerTitles.handleButton(player, buttonId)) return true;
        if (BossDropTables.handleButton(player, buttonId)) return true;
        if (MyCommandsInterface.handleButton(player, buttonId)) return true;
        if (ItemDropFinderInterface.handleButton(player, buttonId)) return true;
        if (player.getGambling().handleButton(buttonId)) return true;
        if (Presetables.handleButton(player, buttonId)) return true;
        if (GlassBlowing.Companion.handleButton(player, buttonId))
            return true;
        if(player.getCollectionLog().handleButton(buttonId)) return true;
        return WildernessScoreBoard.handleButtonInteraction(player, buttonId);
    }

}
