package com.grinder.net.packet.impl;

import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.content.skill.skillable.impl.magic.EnchantSpellCasting;
import com.grinder.game.content.skill.skillable.impl.magic.InteractiveSpell;
import com.grinder.game.content.task_new.DailyTask;
import com.grinder.game.content.task_new.PlayerTaskManager;
import com.grinder.game.content.task_new.WeeklyTask;
import com.grinder.game.definition.ItemValueType;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.entity.agent.player.Color;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.message.decoder.MagicOnInventoryItemMessageDecoder;
import com.grinder.game.message.impl.MagicOnInventoryItemMessage;
import com.grinder.game.model.*;
import com.grinder.game.model.areas.instanced.PestControlArea;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.sound.Sounds;
import com.grinder.net.codec.database.SQLManager;
import com.grinder.net.codec.database.impl.DatabaseAlchLogs;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;
import com.grinder.util.DiscordBot;
import com.grinder.util.ItemID;
import com.grinder.util.Logging;
import com.grinder.util.Misc;

import java.text.NumberFormat;
import java.util.Optional;

/**
 * Handles the packet for using magic spells on items ingame.
 *
 * @author Professor Oak
 */
public class MagicOnInventoryItemPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {

        final MagicOnInventoryItemMessage message = new MagicOnInventoryItemMessageDecoder().decode(packetReader.getPacket());

        final int itemId = message.getItemId();
        final int slot = message.getSlot();
        final int spellId = message.getSpellId();

        if (slot < 0 || slot >= player.getInventory().capacity())
            return;

        if (player.getInventory().getItems()[slot].getId() != itemId)
            return;

        if (player.getInventory().getItems()[slot].getAmount() <= 0)
            return;

        Optional<InteractiveSpell> spell = InteractiveSpell.forSpellId(spellId);
        if (player.busy()) {
            player.sendMessage("You can't do that right now.");
            return;
        }
        if (player.BLOCK_ALL_BUT_TALKING) {
            return;
        }
        if (player.isInTutorial()) {
            return;
        }
        if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
            return;
        }
        if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
            return;
        }
        if (player.getHitpoints() <= 0) {
            return;
        }
        if (player.isTeleporting() && player.getTeleportingType() == TeleportType.HOME) {
            player.stopTeleporting();
        }
        if (player.isTeleporting()) {
            return;
        }

        if (!EntityExtKt.passedTimeGenericAction(player, 1, false)){
            PlayerExtKt.message(player, "You must wait a few seconds before you can cast the next spell.", Color.NONE);
            return;
        }

        SkillUtil.stopSkillable(player);
        player.getCombat().reset(false);

        final Item item = new Item(itemId, 1);

        if (spellId == 1155 || spellId == 1165 || spellId == 1176 || spellId == 1180 || spellId == 1187 || spellId == 6003 || spellId == 30332) {
            if (EnchantSpellCasting.enchant(player, spellId, itemId)) {
                return;
            }
        }

        if (spell.isEmpty())
            return;

        if (!spell.get().getSpell().canCast(player, null, false))
            return;

        switch (spell.get()) {
            case LOW_ALCHEMY:
                if (itemId != item.getDefinition().getId()) {
                    return;
                }
                if (player.busy()) {
                    player.sendMessage("You cannot do that when you are busy.");
                    return;
                }
                if (item.getAmount() <= 0) {
                    return;
                }
                if (item.hasAttributes()) {
                    player.sendMessage("You cannot alch this item.");
                    return;
                }
                if (player.isJailed()) {
                    player.getPacketSender().sendMessage("You can't alch items when you're in jail.");
                    return;
                }
                if (player.getArea() instanceof PestControlArea) {
                    player.sendMessage("You cannot use alchemy spells while in the Pest control minigame.");
                    return;
                }
                if (!item.getDefinition().isTradeable() || !item.getDefinition().isSellable() || item.getId() == 995
                        || item.getValue(ItemValueType.HIGH_ALCHEMY) <= 0 || item.getValue(ItemValueType.LOW_ALCHEMY) <= 0) {
                    player.getPacketSender().sendMessage("This spell can't be cast on this item.");
                    return;
                }
                if (AreaManager.CASTLE_WARS.contains(player)) {
                    player.getPacketSender().sendMessage("You are not allowed to use alchemy spells here.");
                    return;
                }
                if (PlayerExtKt.tryRandomEventTrigger(player, 20F))
                    return;
                player.getInventory().delete(itemId, 1);
                spell.get().getSpell().deleteItemsRequired(player);
                player.performAnimation(new Animation(712));
                player.getInventory().add(995, Math.toIntExact(item.getValue(ItemValueType.LOW_ALCHEMY) * 5));
                player.getAsPlayer().getPacketSender().sendSound(Sounds.LOW_LVL_ALCH_SPELL);
                player.performGraphic(new Graphic(112, GraphicHeight.HIGH));
                player.getSkillManager().addExperience(Skill.MAGIC, spell.get().getSpell().baseExperience() / 400);

                // Increase points
                player.getPoints().increase(AttributeManager.Points.LOW_ALCHEMY_CASTS); // Increase points
                player.getPoints().increase(AttributeManager.Points.SPELLS_CASTED); // Increase points

                player.getPacketSender().sendTab(6);
                player.getClickDelay().reset();
                break;
            case HIGH_ALCHEMY:
                if (itemId != item.getDefinition().getId()) {
                    DiscordBot.INSTANCE.sendModMessage("[SERVER]: Tried to alch a non existing item. Player: " + player.getUsername() + ".");
                    return;
                }
                if (player.isJailed()) {
                    player.getPacketSender().sendMessage("You can't alch items when you're in jail.");
                    return;
                }
                if (player.busy()) {
                    player.sendMessage("You cannot do that when you are busy.");
                    return;
                }
                if (item.hasAttributes()) {
                    player.sendMessage("You cannot alch this item.");
                    DiscordBot.INSTANCE.sendModMessage("[SERVER]: Tried to alch an item with attributes. Player: " + player.getUsername() + ".");
                    return;
                }

                if (item.getAmount() <= 0) {
                    DiscordBot.INSTANCE.sendModMessage("[SERVER]: Tried to alch an item with zero amount. Player: " + player.getUsername() + ".");
                    return;
                }
                if (player.getArea() instanceof PestControlArea) {
                    player.sendMessage("You cannot use alchemy spells while in the Pest control minigame.");
                    return;
                }
                if (!item.getDefinition().isTradeable() || !item.getDefinition().isSellable() || item.getId() == 995
                        || item.getValue(ItemValueType.PRICE_CHECKER) <= 0) {
                    player.getPacketSender().sendMessage("This spell can't be cast on this item.");
                    return;
                }
                if (AreaManager.CASTLE_WARS.contains(player)) {
                    player.getPacketSender().sendMessage("You are not allowed to use alchemy spells here.");
                    return;
                }
                if (PlayerExtKt.tryRandomEventTrigger(player, 20F))
                    return;
                player.getInventory().delete(itemId, 1);
                player.performAnimation(new Animation(713));
                //System.out.println("Item ID : "+ itemId + " on slot " + slot + " itemdef ID " + item.getDefinition().getId());
                double value = item.getValue(ItemValueType.PRICE_CHECKER) / 7D;
                if (value >= 200000) {
                    value /= 2.5;
                }
                if (item.getId() == 15160) {
                    value /= 3;
                }
                if (item.getId() == 21802) {
                    value /= 15;
                }
                if (item.getId() == 2503 || item.getId() == 2504) {
                    value /= 3;
                }
                if (item.getId() == ItemID.DRAGON_AXE || item.getId() == ItemID.DRAGON_CHAINBODY
                        || item.getId() == ItemID.DRAGON_CHAINBODY_2 || item.getId() == ItemID.DRAGON_PLATEBODY
                        || item.getId() == ItemID.SKELETAL_BOTTOMS || item.getId() == ItemID.SKELETAL_TOP
                        || item.getId() == ItemID.SKELETAL_BOOTS || item.getId() == ItemID.SKELETAL_GLOVES
                        || item.getId() == ItemID.INFINITY_BOOTS || item.getId() == ItemID.INFINITY_TOP
                        || item.getId() == ItemID.INFINITY_BOTTOMS || item.getId() == ItemID.DRAGONBONE_NECKLACE
                        || item.getId() == ItemID.MONKEY_TAIL
                        || item.getId() == ItemID.DRAGON_CHAINBODY_2 || item.getId() == 21893
                        || item.getId() == ItemID.SKELETAL_BOTTOMS_2 || item.getId() == ItemID.SKELETAL_TOP_2
                        || item.getId() == ItemID.SKELETAL_BOOTS_2 || item.getId() == ItemID.SKELETAL_GLOVES_2
                        || item.getId() == ItemID.INFINITY_BOOTS_2 || item.getId() == ItemID.INFINITY_TOP_2
                        || item.getId() == ItemID.INFINITY_BOTTOMS_2 || item.getId() == 22112
                        || item.getId() == ItemID.MONKEY_TAIL_2
                        || item.getId() == ItemID.DRAGON_AXE_2
                ) {
                    value /= 2;
                }
                if (item.getId() == ItemID.WRATH_RUNE || item.getId() == ItemID.MAGIC_SEED) {
                    value /= 5;
                }

                if (item.getId() == 2572 || item.getId() == 2573 || item.getId() == 6522) {
                    value /= 17;
                } else if (item.getId() == 861 || item.getId() == 862 || item.getId() == 2497 || item.getId() == 2498 || item.getId() == 2503 || item.getId() == 2504 || (item.getId() >= 8901 && item.getId() <= 8922)) {
                    value /= 4;
                }
                value *= 2;
                if (value >= 60000000) {
                    value /= 4.5;
                }
                if (item.getId() == ItemID.MONKS_ROBE_G_ || item.getId() == ItemID.MONKS_ROBE_TOP_G_ || item.getId() == ItemID.MONKS_ROBE_TOP_G_2 || item.getId() == ItemID.MONKS_ROBE_G_2) {
                    value /= 5;
                }
                if (item.getId() == 22481 || item.getId() == 22482) {
                    value /= 3;
/*                    DiscordBot.INSTANCE.sendModMessage("[SERVER]: @everyone Just a warning that " + player.getUsername() + " has just tried to alch a sangunesti staff and it has been prevented. Better get on and see what they're doing.");
                    return;*/
                }
                if (item.getId() == ItemID.AMETHYST_JAVELIN || item.getId() == ItemID.AMETHYST_JAVELIN_P_ || item.getId() == ItemID.AMETHYST_JAVELIN_P_PLUS_ || item.getId() == ItemID.AMETHYST_JAVELIN_P_PLUS_PLUS_) {
                    value /= 3;
                }
                if (value >= 100000) {
                    if (!player.getGameMode().isSpawn()) {
                        StaffLogRelay.INSTANCE.save(StaffLogRelay.StaffLogType.ALCH, player.getUsername(), item);
                        Logging.log("alch", "" + player.getUsername() + " alched: " + item.getDefinition().getName() + " with value of " + Misc.format((int) value) + "");
                        if (DiscordBot.ENABLED)
                            DiscordBot.INSTANCE.sendServerLogs("[HIGH ALCH]: " + player.getUsername() + " alched: " + item.getDefinition().getName() + " with value of " + Misc.format((int) value) + "");

                        // Databasse logging
                        new DatabaseAlchLogs(
                                SQLManager.Companion.getINSTANCE(),
                                player.getUsername(),
                                item.getDefinition().getName(),
                                (int) value
                        ).schedule(player);
                    }
                }
                spell.get().getSpell().deleteItemsRequired(player);
                player.getInventory().add(995, (int) value);
                player.getAsPlayer().getPacketSender().sendSound(Sounds.HIGH_LVL_ALCH_SPELL);
                player.performGraphic(new Graphic(113, GraphicHeight.HIGH));
                player.getSkillManager().addExperience(Skill.MAGIC, spell.get().getSpell().baseExperience() / 150);
                player.getPacketSender().sendTab(6);
                AchievementManager.processFor(AchievementType.MASTER_ALCHEMIST, player);
                AchievementManager.processFor(AchievementType.MAJOR_ALCHEMIST, player);
                AchievementManager.processFor(AchievementType.APPRENTICE_ALCHEMIST, player);
                AchievementManager.processFor(AchievementType.MINOR_ALCHEMIST, player);

                // Increase points
                player.getPoints().increase(AttributeManager.Points.HIGH_ALCHEMY_CASTS); // Increase points
                player.getPoints().increase(AttributeManager.Points.SPELLS_CASTED); // Increase points
                PlayerTaskManager.progressTask(player, DailyTask.HIGH_ALCHEMY);
                PlayerTaskManager.progressTask(player, WeeklyTask.HIGH_ALCHEMY);
                break;
            case SUPERHEAT_ITEM:
                if (itemId != item.getDefinition().getId()) {
                    return;
                }
                if (itemId != ItemID.COPPER_ORE && itemId != ItemID.TIN_ORE && itemId != ItemID.IRON_ORE && itemId != ItemID.GOLD_ORE && itemId != ItemID.MITHRIL_ORE
                        && itemId != ItemID.ADAMANTITE_ORE && itemId != ItemID.RUNITE_ORE && itemId != ItemID.LOVAKITE_ORE && itemId != ItemID.SILVER_ORE) {
                    player.getPacketSender()
                            .sendMessage("You need to cast superheat item on ore, and a splash animation will appear on the player.");
                    return;
                }
                if (PlayerExtKt.tryRandomEventTrigger(player, 20F))
                    return;

                switch (itemId) {
                    case ItemID.COPPER_ORE: // Copper ore
                    case ItemID.TIN_ORE: // Tin ore
                        if (!player.getInventory().contains(ItemID.COPPER_ORE) || !player.getInventory().contains(ItemID.TIN_ORE)) {
                            player.getPacketSender().sendMessage("You don't have all the required ores in your inventory to cast Superheat.");
                            return;
                        }
                        player.getInventory().delete(ItemID.COPPER_ORE, 1);
                        player.getInventory().delete(ItemID.TIN_ORE, 1);
                        player.getInventory().add(ItemID.BRONZE_BAR, 1);
                        player.getSkillManager().addExperience(Skill.SMITHING, 6);
                        break;
                    case ItemID.IRON_ORE: // Iron ore
                        if (player.getInventory().contains(new Item(ItemID.COAL, 2))) {
                            player.getInventory().delete(ItemID.IRON_ORE, 1);
                            player.getInventory().delete(ItemID.COAL, 2);
                            player.getInventory().add(ItemID.STEEL_BAR, 1);
                            player.getSkillManager().addExperience(Skill.SMITHING, 28);
                        } else {
                            player.getInventory().delete(ItemID.IRON_ORE, 1);
                            player.getInventory().add(ItemID.IRON_BAR, 1);
                            player.getSkillManager().addExperience(Skill.SMITHING, 19);
                        }
                        break;
                    case ItemID.SILVER_ORE: // Silver ore
                        player.getInventory().delete(ItemID.SILVER_ORE, 1);
                        player.getInventory().add(ItemID.SILVER_BAR, 1);
                        player.getSkillManager().addExperience(Skill.SMITHING, 22);
                        break;
                    case ItemID.GOLD_ORE: // Gold ore
                        player.getInventory().delete(ItemID.GOLD_ORE, 1);
                        player.getInventory().add(ItemID.GOLD_BAR, 1);
                        player.getSkillManager().addExperience(Skill.SMITHING, 70);
                        break;
                    case ItemID.LOVAKITE_ORE: // Adamantite ore
                        if (player.getInventory().contains(new Item(ItemID.COAL, 2))) {
                            player.getInventory().delete(ItemID.LOVAKITE_ORE, 1);
                            player.getInventory().delete(ItemID.COAL, 2);
                            player.getInventory().add(ItemID.LOVAKITE_BAR, 1);
                            player.getSkillManager().addExperience(Skill.SMITHING, 65);
                        } else {
                            player.getPacketSender().sendMessage("You don't have all the required ores in your inventory to cast Superheat.");
                            return;
                        }
                        break;
                    case ItemID.MITHRIL_ORE: // Mithril ore
                        if (player.getInventory().contains(new Item(ItemID.COAL, 4))) {
                            player.getInventory().delete(ItemID.MITHRIL_ORE, 1);
                            player.getInventory().delete(ItemID.COAL, 4);
                            player.getInventory().add(ItemID.MITHRIL_BAR, 1);
                            player.getSkillManager().addExperience(Skill.SMITHING, 65);
                        } else {
                            player.getPacketSender().sendMessage("You don't have all the required ores in your inventory to cast Superheat.");
                            return;
                        }
                        break;
                    case ItemID.ADAMANTITE_ORE: // Adamantite ore
                        if (player.getInventory().contains(new Item(ItemID.COAL, 6))) {
                            player.getInventory().delete(ItemID.ADAMANTITE_ORE, 1);
                            player.getInventory().delete(ItemID.COAL, 6);
                            player.getInventory().add(ItemID.ADAMANTITE_BAR, 1);
                            player.getSkillManager().addExperience(Skill.SMITHING, 82);
                        } else {
                            player.getPacketSender().sendMessage("You don't have all the required ores in your inventory to cast Superheat.");
                            return;
                        }
                        break;
                    case ItemID.RUNITE_ORE: // Runite ore
                        if (player.getInventory().contains(new Item(ItemID.COAL, 8))) {
                            player.getInventory().delete(ItemID.RUNITE_ORE, 1);
                            player.getInventory().delete(ItemID.COAL, 8);
                            player.getInventory().add(ItemID.RUNITE_BAR, 1);
                            player.getSkillManager().addExperience(Skill.SMITHING, 110);
                        } else {
                            player.getPacketSender().sendMessage("You don't have all the required ores in your inventory to cast Superheat.");
                            return;
                        }
                        break;
                }
                player.getMotion().reset();
                SkillUtil.stopSkillable(player);
                spell.get().getSpell().deleteItemsRequired(player);
                player.getPacketSender().sendMinimapFlagRemoval();
                player.performAnimation(new Animation(793));
                player.performGraphic(new Graphic(148, GraphicHeight.HIGH));
                player.getAsPlayer().getPacketSender().sendSound(Sounds.SUPERHEAT_ITEM_SPELL);
                player.getSkillManager().addExperience(Skill.MAGIC, spell.get().getSpell().baseExperience() / 72);

                // Increase points
                player.getPoints().increase(AttributeManager.Points.SUPERHEAT_SPELL_CASTS); // Increase points
                player.getPoints().increase(AttributeManager.Points.SPELLS_CASTED); // Increase points

                player.getPacketSender().sendTab(6);
                break;
            default:
                player.getPacketSender().sendMessage("Nothing interesting happens.", 1000);
                break;
        }
    }
}