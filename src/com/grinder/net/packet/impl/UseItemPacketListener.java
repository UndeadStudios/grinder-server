package com.grinder.net.packet.impl;

import com.grinder.Server;
import com.grinder.game.World;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.item.*;
import com.grinder.game.content.item.charging.Chargeables;
import com.grinder.game.content.item.degrading.DegradableType;
import com.grinder.game.content.minigame.castlewars.BarricadeManager;
import com.grinder.game.content.minigame.castlewars.CastleWars;
import com.grinder.game.content.minigame.warriorsguild.WarriorsGuild;
import com.grinder.game.content.minigame.warriorsguild.drops.Defender;
import com.grinder.game.content.miscellaneous.christmas._2022.Christmas2022;
import com.grinder.game.content.miscellaneous.cleanherb.HerbCleaning;
import com.grinder.game.content.miscellaneous.cleanherb.HerbCleaningDialogue;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.content.skill.skillable.ItemCreationSkillable;
import com.grinder.game.content.skill.skillable.impl.*;
import com.grinder.game.content.skill.skillable.impl.Firemaking.LightableLog;
import com.grinder.game.content.skill.skillable.impl.Prayer.AltarOffering;
import com.grinder.game.content.skill.skillable.impl.Prayer.BuriableBone;
import com.grinder.game.content.skill.skillable.impl.cooking.*;
import com.grinder.game.content.skill.skillable.impl.crafting.Crafting;
import com.grinder.game.content.skill.skillable.impl.crafting.GlassBlowing;
import com.grinder.game.content.skill.skillable.impl.crafting.JewelryMaking;
import com.grinder.game.content.skill.skillable.impl.crafting.Pottery;
import com.grinder.game.content.skill.skillable.impl.fishing.Fishing;
import com.grinder.game.content.skill.skillable.impl.fletching.Fletching;
import com.grinder.game.content.skill.skillable.impl.herblore.PotionDecanting;
import com.grinder.game.content.skill.skillable.impl.hunter.birdhouse.BirdHouseActions;
import com.grinder.game.content.skill.skillable.impl.mining.RockType;
import com.grinder.game.content.skill.skillable.impl.runecrafting.AltarRunecrafting;
import com.grinder.game.content.skill.skillable.impl.runecrafting.TiaraCrafting;
import com.grinder.game.content.skill.skillable.impl.runecrafting.pouch.PouchType;
import com.grinder.game.content.skill.skillable.impl.woodcutting.TreeType;
import com.grinder.game.content.task_new.DailyTask;
import com.grinder.game.content.task_new.PlayerTaskManager;
import com.grinder.game.content.task_new.WeeklyTask;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.definition.ItemValueDefinition;
import com.grinder.game.definition.ItemValueType;
import com.grinder.game.definition.ObjectDefinition;
import com.grinder.game.entity.Entity;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.entity.agent.movement.task.WalkToAction;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.slayer.DesertLizard;
import com.grinder.game.entity.agent.npc.slayer.Gargoyle;
import com.grinder.game.entity.agent.npc.slayer.Rockslug;
import com.grinder.game.entity.agent.player.*;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;
import com.grinder.game.entity.agent.player.event.PlayerEvents;
import com.grinder.game.entity.grounditem.ItemOnGround;
import com.grinder.game.entity.grounditem.ItemOnGroundManager;
import com.grinder.game.entity.object.ClippedMapObjects;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.message.Message;
import com.grinder.game.message.decoder.*;
import com.grinder.game.message.impl.*;
import com.grinder.game.model.*;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.interfaces.dialogue.*;
import com.grinder.game.model.interfaces.menu.CreationMenu;
import com.grinder.game.model.interfaces.menu.CreationMenu.CreationMenuAction;
import com.grinder.game.model.interfaces.menu.impl.SingleItemCreationMenu;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.ItemRepairUtil;
import com.grinder.game.model.item.RequiredItem;
import com.grinder.game.model.item.container.bank.BankUtil;
import com.grinder.game.model.item.container.bank.PilesResourceArea;
import com.grinder.game.model.item.container.player.Inventory;
import com.grinder.game.model.projectile.Projectile;
import com.grinder.game.model.projectile.ProjectileTemplate;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.model.sound.SoundLoop;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.game.task.impl.TimedObjectReplacementTask;
import com.grinder.net.packet.Packet;
import com.grinder.net.packet.PacketConstants;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;
import com.grinder.net.packet.interaction.PacketInteractionManager;
import com.grinder.util.*;
import com.grinder.util.debug.DebugManager;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.grinder.util.ItemID.*;
import static com.grinder.util.NpcID.*;
import static com.grinder.util.ObjectID.ALTAR;
import static com.grinder.util.ObjectID.*;

public class UseItemPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {

        final Packet packet = packetReader.getPacket();
        Message message = null;

        switch (packetOpcode) {
            case PacketConstants.ITEM_ON_ITEM:
                message = new ItemOnItemMessageDecoder().decode(packet);
                break;
            case PacketConstants.ITEM_ON_OBJECT:
                message = new ItemOnObjectMessageDecoder().decode(packet);
                break;
            case PacketConstants.ITEM_ON_GROUND_ITEM:
                message = new ItemOnGroundItemMessageDecoder().decode(packet);
                break;
            case PacketConstants.ITEM_ON_NPC:
                message = new ItemOnNpcMessageDecoder().decode(packet);
                break;
            case PacketConstants.ITEM_ON_PLAYER:
                message = new ItemOnPlayerMessageDecoder().decode(packet);
                break;
        }

        if (message == null)
            return;

        if (player == null || player.getHitpoints() <= 0)
            return;

        SkillUtil.stopSkillable(player);

        if (player.isTeleporting() && player.getTeleportingType() == TeleportType.HOME)
            player.stopTeleporting();

        if (player.busy()) {
            player.sendMessage("You can't do that right now.");
            return;
        }

        if (player.BLOCK_ALL_BUT_TALKING)
            return;
        if (player.isInTutorial())
            return;
        if (EntityExtKt.getBoolean(player, Attribute.HAS_TRIGGER_RANDOM_EVENT, false)) {
            player.sendMessage("Please finish your random event before doing anything else.");
            return;
        }
        if (player.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD)
            return;
        if (EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(player, Attribute.HAS_PENDING_RANDOM_EVENT2, false) )
            return;
        if (!MorphItems.INSTANCE.notTransformed(player, "", false, true))
            return;
        if (player.isTeleporting())
            return;

        player.getCombat().reset(false);

        if (player.getMotion().hasFollowTarget())
            player.getMotion().resetTargetFollowing();

        player.getPacketSender().sendInterfaceRemoval();

        player.setEntityInteraction(null);

        switch (packetOpcode) {
            case PacketConstants.ITEM_ON_ITEM:
                itemOnItem(player, (ItemOnItemMessage) message);
                break;
            case PacketConstants.ITEM_ON_OBJECT:
                itemOnObject(player, (ItemOnObjectMessage) message);
                break;
            case PacketConstants.ITEM_ON_GROUND_ITEM:
                itemOnItemOnGround(player, (ItemOnGroundItemMessage) message);
                break;
            case PacketConstants.ITEM_ON_NPC:
                itemOnNpc(player, (ItemOnNpcMessage) message);
                break;
            case PacketConstants.ITEM_ON_PLAYER:
                itemOnPlayer(player, (ItemOnPlayerMessage) message);
                break;
        }
    }

    private static void itemOnItem(Player player, ItemOnItemMessage message) {

        final int targetSlot = message.getTargetSlot();
        final int usedSlot = message.getSlot();

        // TODO: add support for non-inventory item on item actions
        if (message.getInterfaceId() != Inventory.INTERFACE_ID || message.getInterfaceId() != message.getTargetInterfaceId()) {
            player.sendDevelopersMessage("Item On Item is only supported within inventory.");
            return;
        }

        if (targetSlot < 0 || usedSlot < 0 || usedSlot >= player.getInventory().capacity()
                || targetSlot >= player.getInventory().capacity()) {
            return;
        }

        Item used = player.getInventory().getItems()[usedSlot];
        Item usedWith = player.getInventory().getItems()[targetSlot];

        if (!player.getInventory().contains(used)) {
            return;
        }
        if (!player.getInventory().contains(usedWith)) {
            return;
        }
        if (used.getAmount() <= 0 || usedWith.getAmount() <= 0) {
            return;
        }

        DebugManager.debug(player, "item-on-item", "used: "+used.getId()+" on "+usedWith.getId()+", usedSlot: "+usedSlot+", targetSlot: "+targetSlot);

        if(PacketInteractionManager.handleItemOnItemInteraction(player, used, usedWith)) {
            return;
        }

        player.getMotion().followTarget(null);
        player.setEntityInteraction(null);
        player.getMotion().clearSteps();
        player.getPacketSender().sendMinimapFlagRemoval();
        player.getPacketSender().sendInterfaceRemoval();

        int usedValue = (int) ItemValueDefinition.Companion.getValue(used.getId(), ItemValueType.PRICE_CHECKER);
        int usedWithValue = (int) ItemValueDefinition.Companion.getValue(usedWith.getId(), ItemValueType.PRICE_CHECKER);
        if (usedValue >= 1_000_000 || usedWithValue >= 1_000_000) {
            StaffLogRelay.INSTANCE.save(StaffLogRelay.StaffLogType.ITEM_ON_ITEM, player.getUsername(), "used: @red@" + used.getDefinition().getName() + "@bla@ with @red@" + usedWith.getDefinition().getName());
            Logging.log("itemOnItem", "[ItemonItem > 1M value]: " + player.getUsername() + " used: " + used.getDefinition().getName() + " with " + usedWith.getDefinition().getName() + ".");
        }
        if (ItemActions.INSTANCE.handleItemOnItem(player, message))
            return;

        if (player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId() == 4084) {
            player.getPacketSender().sendMessage("You can't use items while your on sled!", 1000);
            return;
        }

        if (used.getId() == LootingBag.LOOTING_BAG) {
            LootingBag.add(player, usedWith);
            return;
        }

        if (usedWith.getId() == LootingBag.LOOTING_BAG) {
            LootingBag.add(player, used);
            return;
        }

        if (Christmas2022.INSTANCE.handleItemOnItem(player))
            return;

        // Herblore
        if (PotionDecanting.decant(player, used, usedWith, usedSlot, targetSlot))
            return;

        // Fletching
        if (Fletching.fletchLog(player, used.getId(), usedWith.getId())
                || Fletching.stringBow(player, used.getId(), usedWith.getId())
                || Fletching.fletchAmmo(player, used.getId(), usedWith.getId())
                || Fletching.fletchBoltTips(player, used.getId(), usedWith.getId())
                || Fletching.fletchCrossbow(player, used.getId(), usedWith.getId())) {
            if (PlayerExtKt.tryRandomEventTrigger(player, 1.3F))
                return;

            return;
        }

        if (Pottery.makeSoftClay(player, used.getId(), usedWith.getId())) {
            return;
        }

		// Glass Blowing
		if (GlassBlowing.Companion.openInterface(player, used.getId(), usedWith.getId())) {
			return;
		}

        // Fishing; Gutting barbarian leaping fish
        if (Fishing.gutFish(player, usedSlot, targetSlot) || Fishing.fillKarambwanVessel(player, usedSlot, targetSlot)) {
            return;
        }

        // Crafting
        if (Crafting.itemOnItem(player, used.getId(), usedWith.getId())) {
            return;
        }

        // Firemaking
        if (Firemaking.init(player, used.getId(), usedWith.getId())) {
            return;
        }
        if(PouchType.Companion.itemIsPouch(usedWith.getId()) || PouchType.Companion.itemIsPouch(used.getId())) {
            player.pouches.get(PouchType.Companion.getPouchForItem(usedWith.getId())).addEssence(player);
            return;
        }
        // Cooking
        if (Recipe.makeRecipe(player, usedSlot, targetSlot) || Dough.mixDough(player, usedSlot, targetSlot)
                || Toppings.addToppings(player, usedSlot, targetSlot) || Drinks.squeezeGrapes(player, usedSlot, targetSlot)
                || Drinks.cutFruit(player, usedSlot, targetSlot) || Gnome.garnishDish(player, usedSlot, targetSlot))
            return;

        if ((used.getId() == INFERNAL_EEL && usedWith.getId() == ItemID.HAMMER)
                || (used.getId() == ItemID.HAMMER && usedWith.getId() == INFERNAL_EEL)) {
            if (player.busy() || player.getCombat().isInCombat()) {
                player.getPacketSender().sendMessage("You can't do that right now.", 1000);
                return;
            }
            if(player.getInventory().countFreeSlots() < 3) {
                player.sendMessage("You need atleast 3 free inventory slots in order to do this!");
                return;
            }
            if (player.getInventory().contains(INFERNAL_EEL)) {
                player.getInventory().delete(INFERNAL_EEL, 1);
                player.getInventory().add(TOKKUL, Misc.inclusive(14, 20));

                if(Misc.getRandomInclusive(12) == 1) {
                    player.getInventory().add(LAVA_SCALE_SHARD, Misc.inclusive(1, 5));
                }
                if(Misc.getRandomInclusive(16) == 1) {
                    player.getInventory().add(ONYX_BOLT_TIPS, 1);
                }
            }
        } else if ((used.getId() == GRANITE_CLAMP || usedWith.getId() == GRANITE_CLAMP)
                && (used.getId() == GRANITE_MAUL || usedWith.getId() == GRANITE_MAUL)) {
            if (player.busy() || player.getCombat().isInCombat()) {
                player.getPacketSender().sendMessage("You can't do that right now.", 1000);
                return;
            }
            if (player.getInventory().contains(GRANITE_MAUL)) {
                player.getInventory().delete(GRANITE_MAUL, 1).delete(GRANITE_CLAMP, 1).add(GRANITE_MAUL_3, 1);
                //player.getPacketSender().sendMessage("@dre@You attach your Granite clamp onto the maul..");
                new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(GRANITE_MAUL_3, 200)
                        .setText("You attach your Granite clamp onto the maul..").start(player);
            }
        } else if ((used.getId() == 13274 || used.getId() == 13275 || used.getId() == 13276)
                && (used.getId() == 13274 || usedWith.getId() == 13275 || usedWith.getId() == 13276)) {
            if (!player.getInventory().contains(13274) || !player.getInventory().contains(13275)
                    || !player.getInventory().contains(13276)) {
                player.getPacketSender()
                        .sendMessage("You don't have all the required pieces to create an Abyssal bludgeon");
            } else {
                player.getInventory().delete(13274, 1);
                player.getInventory().delete(13275, 1);
                player.getInventory().delete(13276, 1);
                player.getInventory().add(13263, 1);
                //player.getPacketSender().sendMessage("You combine all the pieces together to form a @dre@Abyssal bludgeon</col>.");
                new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13263, 200)
                        .setText("You combine all the pieces together to form", "an @dre@Abyssal bludgeon</col>.").start(player);
            }
        } else if ((used.getId() == 11931 || used.getId() == 11932 || used.getId() == 11933)
                && (used.getId() == 11931 || usedWith.getId() == 11932 || usedWith.getId() == 11933)) {
            if (!player.getInventory().contains(11933) || !player.getInventory().contains(11932)
                    || !player.getInventory().contains(11931)) {
                player.getPacketSender()
                        .sendMessage("You don't have all the required shards to forge a ward.");
            } else {
                player.getPacketSender().sendMessage("You must use all the shards on the forge in the Wilderness.");
            }
        } else if ((used.getId() == 11928 || used.getId() == 11929 || used.getId() == 11930)
                && (used.getId() == 11928 || usedWith.getId() == 11929 || usedWith.getId() == 11930)) {
            if (!player.getInventory().contains(11928) || !player.getInventory().contains(11929)
                    || !player.getInventory().contains(11930)) {
                player.getPacketSender()
                        .sendMessage("You don't have all the required shards to forge a ward.");
            } else {
                player.getPacketSender().sendMessage("You must use all the shards on the forge in the Wilderness.");
            }
        } else if ((used.getId() == 13233 || used.getId() == 12797)
                && (usedWith.getId() == 13233 || usedWith.getId() == 12797)) {
            player.getPacketSender()
                    .sendMessage("The cosmetically upgraded pickaxe is too beautiful to convert into an infernal pickaxe.", 1000);
        } else if (Arrays.stream(GoldenGodswordSpecialAttacks.values()).anyMatch(gs -> gs.getKitId() == used.getId())) {
            GoldenGodswordSpecialAttacks.Handler.redeem(player, used.getId(), usedWith.getId());
        } else if ((used.getId() == ItemID.SACRED_EEL && usedWith.getId() == ItemID.KNIFE)
                || (used.getId() == ItemID.KNIFE && usedWith.getId() == ItemID.SACRED_EEL)) {
            if (player.getSkillManager().getMaxLevel(Skill.COOKING) < 72) {
                DialogueManager.sendStatement(player, "You need a Cooking level of at least 72 to dissected the eel.");
                return;
            }
            if (Misc.random(3) == 1) {
                if (PlayerExtKt.tryRandomEventTrigger(player, 1.5F)) {
                    return;
                }
            }
            int amountToMake = 3 + Misc.random(3);
            player.performAnimation(new Animation(885));
            player.getInventory().delete(SACRED_EEL, 1);
            player.getInventory().add(ZULRAHS_SCALES, amountToMake);
            player.getSkillManager().addExperience(Skill.COOKING, 100);
            player.sendMessage("You dissect the eel carcass and extract " + amountToMake + " scales.");
/*            Optional<CreationMenu> menu;
            CreationMenu.CreationMenuAction action = (index, item, amount) -> {
                SkillUtil.startSkillable(
                        player, new ItemCreationSkillable(
                                Arrays.asList(new RequiredItem((SACRED_EEL), true)),
                                new Item(ZULRAHS_SCALES, amountToMake),
                                amount,
                                new AnimationLoop(new Animation(885), 3),
                                72,
                                100,
                                Skill.COOKING, "You dissect the eel carcass and extract " + amountToMake + " scales.", 2));
                player.getPacketSender().sendInterfaceRemoval();
            };

            menu = Optional.of(new SingleItemCreationMenu(player,
                    ZULRAHS_SCALES,
                    "How many eels would you like to dissect?", action));


            if (menu.isPresent()) {
                player.setCreationMenu(menu);
                menu.get().open();
            }*/
        } else if ((used.getId() == KNIFE && (usedWith.getId() == RED_LOGS || usedWith.getId() == BLUE_LOGS || usedWith.getId() == WHITE_LOGS || usedWith.getId() == PURPLE_LOGS || usedWith.getId() == GREEN_LOGS))
                || (used.getId() == RED_LOGS || used.getId() == BLUE_LOGS || used.getId() == WHITE_LOGS || used.getId() == PURPLE_LOGS || used.getId() == GREEN_LOGS) && usedWith.getId() == KNIFE) {
                    player.sendMessage("You cannot fletch these logs any more.");
        } else if ((used.getDefinition().getName().toLowerCase().endsWith("logs") && usedWith.getDefinition().getName().toLowerCase().contains(" firelighter"))
                || (used.getDefinition().getName().toLowerCase().contains(" firelighter") && usedWith.getDefinition().getName().toLowerCase().endsWith("logs"))) {

            if ((usedWith.getId() == OAK_LOGS || usedWith.getId() == ACHEY_TREE_LOGS || usedWith.getId() == WILLOW_LOGS || usedWith.getId() == MAPLE_LOGS || usedWith.getId() == YEW_LOGS ||
                    usedWith.getId() == MAGIC_LOGS || usedWith.getId() == REDWOOD_PYRE_LOGS || usedWith.getId() == TEAK_LOGS || usedWith.getId() == MAHOGANY_LOGS || usedWith.getId() == REDWOOD_LOGS ||
                    usedWith.getId() == GNOMISH_FIRELIGHTER || usedWith.getId() == GNOMISH_FIRELIGHTER_2 || usedWith.getId() == GNOMISH_FIRELIGHTER_3)
                    ||
            (used.getId() == OAK_LOGS || used.getId() == ACHEY_TREE_LOGS || used.getId() == WILLOW_LOGS || used.getId() == MAPLE_LOGS || used.getId() == YEW_LOGS ||
                    used.getId() == MAGIC_LOGS || used.getId() == REDWOOD_PYRE_LOGS || used.getId() == TEAK_LOGS || used.getId() == MAHOGANY_LOGS || used.getId() == REDWOOD_LOGS ||
                    used.getId() == GNOMISH_FIRELIGHTER || used.getId() == GNOMISH_FIRELIGHTER_2 || used.getId() == GNOMISH_FIRELIGHTER_3)) {
                player.sendMessage("The firelighters will not work with these logs.");
                return;
            } else if ((usedWith.getId() == BLUE_LOGS || usedWith.getId() == WHITE_LOGS || usedWith.getId() == RED_LOGS || usedWith.getId() == GREEN_LOGS || usedWith.getId() == PURPLE_LOGS)
                    ||
                    (used.getId() == BLUE_LOGS || used.getId() == WHITE_LOGS || used.getId() == RED_LOGS || used.getId() == GREEN_LOGS || used.getId() == PURPLE_LOGS)) {
                player.sendMessage("These logs have already been coloured.");
                return;
            }

            player.getInventory().delete(ItemID.LOGS, 1);

            if (usedWith.getDefinition().getName().toLowerCase().contains("blue firelighter") || used.getDefinition().getName().toLowerCase().contains("blue firelighter")) {
                player.getInventory().delete(ItemID.BLUE_FIRELIGHTER, 1);
                player.getInventory().add(BLUE_LOGS, 1);
                player.sendMessage("You coat the logs with the blue chemicals.");
            } else if (usedWith.getDefinition().getName().toLowerCase().contains("white firelighter") || used.getDefinition().getName().toLowerCase().contains("white firelighter")) {
                player.getInventory().delete(WHITE_FIRELIGHTER, 1);
                player.getInventory().add(WHITE_LOGS, 1);
                player.sendMessage("You coat the logs with the white chemicals.");
            } else if (usedWith.getDefinition().getName().toLowerCase().contains("red firelighter") || used.getDefinition().getName().toLowerCase().contains("red firelighter")) {
                player.getInventory().delete(RED_FIRELIGHTER, 1);
                player.getInventory().add(RED_LOGS, 1);
                player.sendMessage("You coat the logs with the red chemicals.");
            } else if (usedWith.getDefinition().getName().toLowerCase().contains("green firelighter") || used.getDefinition().getName().toLowerCase().contains("green firelighter")) {
                player.getInventory().delete(GREEN_FIRELIGHTER, 1);
                player.getInventory().add(GREEN_LOGS, 1);
                player.sendMessage("You coat the logs with the green chemicals.");
            } else if (usedWith.getDefinition().getName().toLowerCase().contains("purple firelighter") || used.getDefinition().getName().toLowerCase().contains("purple firelighter")) {
                player.getInventory().delete(PURPLE_FIRELIGHTER, 1);
                player.getInventory().add(PURPLE_LOGS, 1);
                player.sendMessage("You coat the logs with the purple chemicals.");
            }

        } else if ((used.getId() == ItemID.KNIFE && usedWith.getId() == ItemID.KBD_HEADS)
                || (used.getId() == ItemID.KBD_HEADS && usedWith.getId() == ItemID.KNIFE)) {
            new DialogueBuilder(DialogueType.OPTION)
                    .setOptionTitle("Separate heads? (non-reversible)")
                    .firstOption("Yes, I want to separate the KBD head into three dragon heads.", player2 -> {
                        if (player.getInventory().countFreeSlots() < 3) {
                            player.sendMessage("You must have 3 free inventory slots to do this.");
                            return;
                        }
                        player.getInventory().delete(7980, 1);
                        player.getInventory().add(13510, 1);
                        player.getInventory().add(13510, 1);
                        player.getInventory().add(13510, 1);
                        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(13510, 200)
                                .setText("You separate the KBD head into three ensouled", "dragon heads.").start(player);
                    })
                    .addCancel("No, leave it as it is!").start(player);
        } else if ((used.getId() == STEEL_STUDS && usedWith.getId() == LEATHER_BODY)
                || (used.getId() == LEATHER_BODY && used.getId() == ItemID.STEEL_STUDS)) {
            new DialogueBuilder(DialogueType.OPTION)
                    .setOptionTitle("Infuse studs to the leather? (non-reversible)")
                    .firstOption("Yes, infuse to make studded leather.", player2 -> {

                        player.getInventory().delete(LEATHER_BODY, 1);
                        player.getInventory().delete(STEEL_STUDS, 1);
                        player.getInventory().add(STUDDED_BODY, 1);
                        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(STUDDED_BODY, 200)
                                .setText("You infused the metal studs in the leather.").start(player);
                    })
                    .addCancel("Nevermind.").start(player);
        } else if ((used.getId() == STEEL_STUDS && usedWith.getId() == LEATHER_CHAPS)
                || (used.getId() == LEATHER_CHAPS && used.getId() == ItemID.STEEL_STUDS)) {
            new DialogueBuilder(DialogueType.OPTION)
                    .setOptionTitle("Infuse studs to the leather? (non-reversible)")
                    .firstOption("Yes, infuse to make studded leather.", player2 -> {

                        player.getInventory().delete(LEATHER_CHAPS, 1);
                        player.getInventory().delete(STEEL_STUDS, 1);
                        player.getInventory().add(STUDDED_CHAPS, 1);
                        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(LEATHER_CHAPS, 200)
                                .setText("You infused the metal studs in the leather.").start(player);
                    })
                    .addCancel("Nevermind.").start(player);
        } else if ((used.getId() == UNPOWERED_SYMBOL && usedWith.getId() == UNHOLY_BOOK)
                || (used.getId() == UNHOLY_BOOK && used.getId() == ItemID.UNPOWERED_SYMBOL)) {
            new DialogueBuilder(DialogueType.OPTION)
                    .setOptionTitle("Infuse symbol? (non-reversible)")
                    .firstOption("Yes, infuse symbol with the unholy power.", player2 -> {
                        if (player.getSkillManager().getMaxLevel(Skill.PRAYER) < 50) {
                            DialogueManager.sendStatement(player, "You need a Prayer level of at least 50 to infuse the symbol.");
                            return;
                        }
                        if (player.getInventory().countFreeSlots() < 1) {
                            player.sendMessage("You must have one free inventory slots to do this.");
                            return;
                        }
                        player.getInventory().delete(UNPOWERED_SYMBOL, 1);
                        player.getInventory().delete(UNHOLY_BOOK, 1);
                        player.getInventory().add(UNHOLY_SYMBOL, 1);
                        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(UNHOLY_SYMBOL, 200)
                                .setText("You infused the symbol with unholy power.").start(player);
                    })
                    .addCancel("Don't touch it.").start(player);
        } else if ((used.getId() == INFERNAL_EEL && usedWith.getId() == ItemID.HAMMER)
                || (used.getId() == ItemID.HAMMER && used.getId() == ItemID.INFERNAL_EEL)) {
                        if (player.getInventory().countFreeSlots() < 1) {
                            player.sendMessage("You must have one free inventory slots to do this.");
                            return;
                        }
                        player.getInventory().delete(INFERNAL_EEL, 1);
                        if (Misc.random(3) == 1) {
                            player.getInventory().add(ONYX_BOLT_TIPS, 1 + Misc.random(3));
                        } else if (Misc.random(3) == 1) {
                            player.getInventory().add(TOKKUL, 14 + Misc.random(6));
                        } else {
                            player.getInventory().add(LAVA_SCALE_SHARD, 1 + Misc.random(4));
                        }
        } else if ((used.getId() == UNPOWERED_SYMBOL && usedWith.getId() == BOOK_OF_BALANCE)
                || (used.getId() == BOOK_OF_BALANCE || used.getId() == UNHOLY_BOOK && used.getId() == ItemID.UNPOWERED_SYMBOL)) {
            new DialogueBuilder(DialogueType.OPTION)
                    .setOptionTitle("Infuse symbol? (non-reversible)")
                    .firstOption("Yes, infuse symbol with the unholy power.", player2 -> {
                        if (player.getSkillManager().getMaxLevel(Skill.PRAYER) < 50) {
                            DialogueManager.sendStatement(player, "You need a Prayer level of at least 50 to infuse the symbol.");
                            return;
                        }
                        if (player.getInventory().countFreeSlots() < 1) {
                            player.sendMessage("You must have one free inventory slots to do this.");
                            return;
                        }
                        player.getInventory().delete(UNPOWERED_SYMBOL, 1);
                        player.getInventory().delete(BOOK_OF_BALANCE, 1);
                        player.getInventory().add(UNHOLY_SYMBOL, 1);
                        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(UNHOLY_SYMBOL, 200)
                                .setText("You infused the symbol with unholy power.").start(player);
                    })
                    .addCancel("Don't touch it.").start(player);
        } else if (Chargeables.INSTANCE.handleItemOnItem(player, used.getId(), usedWith.getId(), targetSlot)
                || Chargeables.INSTANCE.handleItemOnItem(player, usedWith.getId(), used.getId(), usedSlot)) {

        } else if ((used.getId() == 2436 || used.getId() == 2440 || used.getId() == 2442) && usedWith.getId() == 269
                || (used.getId() == 269 && (usedWith.getId() == 2436 || usedWith.getId() == 2440 || usedWith.getId() == 2442))) {
            if (player.getInventory().getAmount(ItemID.SUPER_ATTACK_4_) < 1) {
                player.getPacketSender().sendMessage("You don't have enough @dre@"
                        + ItemDefinition.forId(ItemID.SUPER_ATTACK_4_).getName() + " to make Super combat potion(4).");
                return;
            }
            if (player.getInventory().getAmount(ItemID.SUPER_STRENGTH_4_) < 1) {
                player.getPacketSender().sendMessage("You don't have enough @dre@"
                        + ItemDefinition.forId(ItemID.SUPER_STRENGTH_4_).getName() + " to make Super combat potion(4).");
                return;
            }
            if (player.getInventory().getAmount(ItemID.SUPER_DEFENCE_4_) < 1) {
                player.getPacketSender().sendMessage("You don't have enough @dre@"
                        + ItemDefinition.forId(ItemID.SUPER_DEFENCE_4_).getName() + " to make Super combat potion(4).");
                return;
            }
            if (player.getSkillManager().getCurrentLevel(Skill.HERBLORE) < 90) {
                player.getPacketSender().sendMessage(
                        "You need a Herblore level of at least 90 to create Super combat potion(4).");
                return;
            }
            if (PlayerExtKt.tryRandomEventTrigger(player, 1.3F))
                return;
            CreationMenu menu = new SingleItemCreationMenu(player, 12695,
                    "How many potions would you like to make?", (index, item, amount) -> {
                ItemCreationSkillable skillable = new ItemCreationSkillable(
                        Arrays.asList(
                                new RequiredItem(new Item(ItemID.SUPER_STRENGTH_4_), true),
                                new RequiredItem(new Item(ItemID.SUPER_ATTACK_4_), true),
                                new RequiredItem(new Item(ItemID.SUPER_DEFENCE_4_), true),
                                new RequiredItem(new Item(ItemID.TORSTOL), true)
                        ),
                        new Item(12695), amount,
                        new AnimationLoop(new Animation(363), 4), new SoundLoop(new Sound(2608, 0), 4),
                        90,
                        195,
                        Skill.HERBLORE, "You mix all the super potions together with the tortsol.", 2);
                SkillUtil.startSkillable(player, skillable);
            }).open();
            player.setCreationMenu(Optional.of(menu));

        } else if (
                (used.getId() == 6668 && usedWith.getId() == 401) ||
                        (usedWith.getId() == 6668 && used.getId() == 401)
        ) {
            player.getInventory().delete(401, 1);
            player.getInventory().delete(6668, 1);
            player.getInventory().add(6669, 1);
            player.sendMessage("You place the seaweed in the bowl.");
        } else if (
                (used.getId() == 6683 && usedWith.getId() == 6675) ||
                        (usedWith.getId() == 6683 && used.getId() == 6675)) {
            player.getInventory().delete(6675, 1);
            player.getInventory().delete(6683, 1);
            player.getInventory().add(6679, 1);
            player.sendMessage("You put the ground Seaweed into the box.");
        } else if (
                (used.getId() == 6679 && usedWith.getId() == 6681) ||
                        (usedWith.getId() == 6679 && used.getId() == 6681)) {
            player.getInventory().delete(6679, 1);
            player.getInventory().delete(6681, 1);
            player.getInventory().add(272, 1);
            player.sendMessage("You put the ground Guam Leaf into the box and make Fish Food.");
        } else if (
                (used.getId() == 6670 && usedWith.getId() == 272) ||
                        (usedWith.getId() == 6670 && used.getId() == 272)) {
            player.getInventory().delete(272, 1);
            player.performAnimation(new Animation(2781));
            player.sendMessage("You feed your fish.");
        } else if (
                (used.getId() == 6671 && usedWith.getId() == 272) ||
                        (usedWith.getId() == 6671 && used.getId() == 272)) {
            player.getInventory().delete(272, 1);
            player.performAnimation(new Animation(2784));
            player.sendMessage("You feed your fish.");
        } else if (
                (used.getId() == 6672 && usedWith.getId() == 272) ||
                        (usedWith.getId() == 6672 && used.getId() == 272)) {
            player.getInventory().delete(272, 1);
            player.performAnimation(new Animation(2787));
            player.sendMessage("You feed your fish.");
        } else if ((used.getId() == ItemID.MITH_GRAPPLE_TIP && usedWith.getId() == MITHRIL_BOLTS) ||  (usedWith.getId() == ItemID.MITH_GRAPPLE_TIP && used.getId() == ItemID.MITHRIL_BOLTS)) {
            if (player.getSkillManager().getMaxLevel(Skill.FLETCHING) < 59) {
                player.sendMessage("You need a Fletching level of at least 59 to do that.");
                return;
            }
            player.getInventory().delete(ItemID.MITH_GRAPPLE_TIP, 1);
            player.getInventory().delete(ItemID.MITHRIL_BOLTS, 1);
            player.getInventory().add(ItemID.MITH_GRAPPLE, 1);

            player.getSkillManager().addExperience(Skill.FLETCHING, 11);
            player.sendMessage("You fletch a bolt.");
        } else if ((used.getId() == ItemID.MITH_GRAPPLE && usedWith.getId() == ItemID.ROPE) ||  (usedWith.getId() == ItemID.MITH_GRAPPLE && used.getId() == ItemID.ROPE)) {
            player.getInventory().delete(ItemID.MITH_GRAPPLE, 1);
            player.getInventory().delete(ItemID.ROPE, 1);
            player.getInventory().add(ItemID.MITH_GRAPPLE_2, 1);
            player.sendMessage("You attach a rope to the mithril grapple.");
        } else if ((used.getId() == ItemID.COCONUT && usedWith.getId() == ItemID.HAMMER) ||  (usedWith.getId() == ItemID.COCONUT && used.getId() == ItemID.HAMMER)) {
            player.getInventory().delete(ItemID.COCONUT, 1);
            player.getInventory().add(ItemID.HALF_COCONUT, 2);
            player.sendMessage("You break the coconut with the hammer.");
        } else if ((used.getId() == ItemID.HALF_COCONUT && usedWith.getId() == ItemID.HAMMER) ||  (usedWith.getId() == ItemID.HALF_COCONUT && used.getId() == ItemID.HAMMER)) {
            player.sendMessage("This coconut has already been broken.");
        } else if ((used.getId() == ItemID.HALF_COCONUT && usedWith.getId() == ItemID.EMPTY_VIAL) ||  (usedWith.getId() == ItemID.HALF_COCONUT && used.getId() == ItemID.EMPTY_VIAL)) {
            player.getInventory().replace(new Item(ItemID.HALF_COCONUT), new Item(ItemID.COCONUT_SHELL));
            player.getInventory().replace(new Item(ItemID.EMPTY_VIAL), new Item(ItemID.COCONUT_MILK));
            player.getInventory().refreshItems();
            player.sendMessage("You fill the vial with coconut milk.");
        } else if (
                (used.getId() == 21726 && usedWith.getId() == ItemID.CANNONBALL) ||
                        (usedWith.getId() == 21726 && used.getId() == ItemID.CANNONBALL)) {
            if (player.getSkillManager().getCurrentLevel(Skill.SMITHING) < 50) {
                player.sendMessage("You need a Smithing level of 50 to apply coating to the cannonballs.");
                return;
            }
            int balls = player.getInventory().getAmount(ItemID.CANNONBALL);
            int dust = player.getInventory().getAmount(21726);
            int amt = Math.min(Math.min(balls, dust), 50);

            player.getInventory().delete(ItemID.CANNONBALL, amt);
            player.getInventory().delete(21726, amt);
            player.getInventory().add(new Item(21728, amt));
            player.sendMessage("You apply a thick coating of granite dust to your cannonballs.");
        } else if ((used.getId() == BANDOSIAN_COMPONENTS && usedWith.getId() == TORVA_FULL_HELM_DAMAGED) ||
                        (usedWith.getId() == BANDOSIAN_COMPONENTS && used.getId() == ItemID.TORVA_FULL_HELM_DAMAGED)) {
            if (player.getSkillManager().getCurrentLevel(Skill.SMITHING) < 90) {
                player.sendMessage("You need at least a Smithing level of 90 to be able to repair the helmet.");
                return;
            }
            if (player.getInventory().getAmount(BANDOSIAN_COMPONENTS) < 8) {
                new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(BANDOSIAN_COMPONENTS, 200)
                        .setText("You need to have at least 8 Bandosian components to", "permanently repair the helmet.").start(player);
                return;
            }

            new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                    .setItem(TORVA_FULL_HELM_DAMAGED, 200)
                    .setText("Are you sure you want to repair @dre@" + ItemDefinition.forId(TORVA_FULL_HELM_DAMAGED).getName() +"</col>?", "This action is irreversible.")
                    .add(DialogueType.OPTION)
                    .setOptionTitle("Repair the " + ItemDefinition.forId(TORVA_FULL_HELM_DAMAGED).getName() +"?")
                    .firstOption("Confirm.", player2 -> {
                        if (!player.getInventory().contains(TORVA_FULL_HELM_DAMAGED)) {
                            return;
                        }
                        if (!player.getInventory().contains(BANDOSIAN_COMPONENTS)) {
                            return;
                        }


                        player.getPacketSender().sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND);
                        AchievementManager.processFor(AchievementType.COMBINATION, player);
                        player.getSkillManager().addExperience(Skill.SMITHING, 2250);

                        player.getPacketSender().sendInterfaceRemoval();
                        player.getInventory().delete(ItemID.TORVA_FULL_HELM_DAMAGED, 1);
                        player.getInventory().delete(BANDOSIAN_COMPONENTS, 8);
                        player.getInventory().add(new Item(TORVA_FULL_HELM, 1));
                        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(TORVA_FULL_HELM, 200)
                                .setText("You carefully attach the components to the helmet ", "and have it permanently repaired.").start(player);
                    })
                    .addCancel()
                    .start(player);
        } else if ((used.getId() == BANDOSIAN_COMPONENTS && usedWith.getId() == TORVA_PLATEBODY_DAMAGED) ||
                (usedWith.getId() == BANDOSIAN_COMPONENTS && used.getId() == ItemID.TORVA_PLATEBODY_DAMAGED)) {
            if (player.getSkillManager().getCurrentLevel(Skill.SMITHING) < 90) {
                player.sendMessage("You need at least a Smithing level of 90 to be able to repair the platebody.");
                return;
            }
            if (player.getInventory().getAmount(BANDOSIAN_COMPONENTS) < 12) {
                new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(BANDOSIAN_COMPONENTS, 200)
                        .setText("You need to have at least 12 Bandosian components to", "permanently repair the platebody.").start(player);
                return;
            }

            new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                    .setItem(TORVA_PLATEBODY_DAMAGED, 200)
                    .setText("Are you sure you want to repair @dre@" + ItemDefinition.forId(TORVA_PLATEBODY_DAMAGED).getName() +"</col>?", "This action is irreversible.")
                    .add(DialogueType.OPTION)
                    .setOptionTitle("Repair the " + ItemDefinition.forId(TORVA_PLATEBODY_DAMAGED).getName() +"?")
                    .firstOption("Confirm.", player2 -> {
                        if (!player.getInventory().contains(TORVA_PLATEBODY_DAMAGED)) {
                            return;
                        }
                        if (!player.getInventory().contains(BANDOSIAN_COMPONENTS)) {
                            return;
                        }


                        player.getPacketSender().sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND);
                        AchievementManager.processFor(AchievementType.COMBINATION, player);
                        player.getSkillManager().addExperience(Skill.SMITHING, 2250);

                        player.getPacketSender().sendInterfaceRemoval();
                        player.getInventory().delete(ItemID.TORVA_PLATEBODY_DAMAGED, 1);
                        player.getInventory().delete(BANDOSIAN_COMPONENTS, 12);
                        player.getInventory().add(new Item(TORVA_PLATEBODY, 1));
                        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(TORVA_PLATEBODY, 200)
                                .setText("You carefully attach the components to the platebody ", "and have it permanently repaired.").start(player);
                    })
                    .addCancel()
                    .start(player);
        } else if ((used.getId() == BANDOSIAN_COMPONENTS && usedWith.getId() == TORVA_PLATELEGS_DAMAGED) ||
                (usedWith.getId() == BANDOSIAN_COMPONENTS && used.getId() == ItemID.TORVA_PLATELEGS_DAMAGED)) {
            if (player.getSkillManager().getCurrentLevel(Skill.SMITHING) < 90) {
                player.sendMessage("You need at least a Smithing level of 90 to be able to repair the platelegs.");
                return;
            }
            if (player.getInventory().getAmount(BANDOSIAN_COMPONENTS) < 10) {
                new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(BANDOSIAN_COMPONENTS, 200)
                        .setText("You need to have at least 10 Bandosian components to", "permanently repair the platelegs.").start(player);
                return;
            }

            new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                    .setItem(TORVA_PLATELEGS_DAMAGED, 200)
                    .setText("Are you sure you want to repair @dre@" + ItemDefinition.forId(TORVA_PLATELEGS_DAMAGED).getName() +"</col>?", "This action is irreversible.")
                    .add(DialogueType.OPTION)
                    .setOptionTitle("Repair the " + ItemDefinition.forId(TORVA_PLATELEGS_DAMAGED).getName() +"?")
                    .firstOption("Confirm.", player2 -> {
                        if (!player.getInventory().contains(TORVA_PLATELEGS_DAMAGED)) {
                            return;
                        }
                        if (!player.getInventory().contains(BANDOSIAN_COMPONENTS)) {
                            return;
                        }


                        player.getPacketSender().sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND);
                        AchievementManager.processFor(AchievementType.COMBINATION, player);
                        player.getSkillManager().addExperience(Skill.SMITHING, 2250);

                        player.getPacketSender().sendInterfaceRemoval();
                        player.getInventory().delete(ItemID.TORVA_PLATELEGS_DAMAGED, 1);
                        player.getInventory().delete(BANDOSIAN_COMPONENTS, 10);
                        player.getInventory().add(new Item(TORVA_PLATELEGS, 1));
                        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(TORVA_PLATELEGS, 200)
                                .setText("You carefully attach the components to the platelegs ", "and have it permanently repaired.").start(player);
                    })
                    .addCancel()
                    .start(player);
        } else if ((used.getId() == 15898 && usedWith.getId() == 15895) ||
                (usedWith.getId() == 15898 && used.getId() == 15895)) {
            if (player.getSkillManager().getCurrentLevel(Skill.SMITHING) < 90) {
                player.sendMessage("You need at least a Smithing level of 90 to be able to repair the cowl.");
                return;
            }
            if (player.getInventory().getAmount(15898) < 10) {
                new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(15898, 200)
                        .setText("You need to have at least 10 Armadyl components to", "permanently repair the cowl.").start(player);
                return;
            }

            new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                    .setItem(15895, 200)
                    .setText("Are you sure you want to repair @dre@" + ItemDefinition.forId(15895).getName() +"</col>?", "This action is irreversible.")
                    .add(DialogueType.OPTION)
                    .setOptionTitle("Repair the " + ItemDefinition.forId(15895).getName() +"?")
                    .firstOption("Confirm.", player2 -> {
                        if (!player.getInventory().contains(15895)) {
                            return;
                        }
                        if (!player.getInventory().contains(15898)) {
                            return;
                        }


                        player.getPacketSender().sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND);
                        AchievementManager.processFor(AchievementType.COMBINATION, player);
                        player.getSkillManager().addExperience(Skill.SMITHING, 2250);

                        player.getPacketSender().sendInterfaceRemoval();
                        player.getInventory().delete(15895, 1);
                        player.getInventory().delete(15898, 8);
                        player.getInventory().add(new Item(15883, 1));
                        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(15883, 200)
                                .setText("You carefully attach the components to the cowl ", "and have it permanently repaired.").start(player);
                    })
                    .addCancel()
                    .start(player);
        } else if ((used.getId() == 15898 && usedWith.getId() == 15896) ||
                (usedWith.getId() == 15898 && used.getId() == 15896)) {
            if (player.getSkillManager().getCurrentLevel(Skill.SMITHING) < 90) {
                player.sendMessage("You need at least a Smithing level of 90 to be able to repair the body.");
                return;
            }
            if (player.getInventory().getAmount(15898) < 12) {
                new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(15898, 200)
                        .setText("You need to have at least 12 Armadyl components to", "permanently repair the body.").start(player);
                return;
            }

            new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                    .setItem(15896, 200)
                    .setText("Are you sure you want to repair @dre@" + ItemDefinition.forId(15896).getName() +"</col>?", "This action is irreversible.")
                    .add(DialogueType.OPTION)
                    .setOptionTitle("Repair the " + ItemDefinition.forId(15896).getName() +"?")
                    .firstOption("Confirm.", player2 -> {
                        if (!player.getInventory().contains(15896)) {
                            return;
                        }
                        if (!player.getInventory().contains(15898)) {
                            return;
                        }


                        player.getPacketSender().sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND);
                        AchievementManager.processFor(AchievementType.COMBINATION, player);
                        player.getSkillManager().addExperience(Skill.SMITHING, 2250);

                        player.getPacketSender().sendInterfaceRemoval();
                        player.getInventory().delete(15896, 1);
                        player.getInventory().delete(15898, 12);
                        player.getInventory().add(new Item(15885, 1));
                        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(15885, 200)
                                .setText("You carefully attach the components to the body ", "and have it permanently repaired.").start(player);
                    })
                    .addCancel()
                    .start(player);
        } else if ((used.getId() == 15898 && usedWith.getId() == 15897) ||
                (usedWith.getId() == 15898 && used.getId() == 15897)) {
            if (player.getSkillManager().getCurrentLevel(Skill.SMITHING) < 90) {
                player.sendMessage("You need at least a Smithing level of 90 to be able to repair the chaps.");
                return;
            }
            if (player.getInventory().getAmount(15898) < 10) {
                new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(15898, 200)
                        .setText("You need to have at least 10 Armadyl components to", "permanently repair the chaps.").start(player);
                return;
            }

            new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                    .setItem(15897, 200)
                    .setText("Are you sure you want to repair @dre@" + ItemDefinition.forId(15897).getName() +"</col>?", "This action is irreversible.")
                    .add(DialogueType.OPTION)
                    .setOptionTitle("Repair the " + ItemDefinition.forId(15897).getName() +"?")
                    .firstOption("Confirm.", player2 -> {
                        if (!player.getInventory().contains(15897)) {
                            return;
                        }
                        if (!player.getInventory().contains(15898)) {
                            return;
                        }


                        player.getPacketSender().sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND);
                        AchievementManager.processFor(AchievementType.COMBINATION, player);
                        player.getSkillManager().addExperience(Skill.SMITHING, 2250);

                        player.getPacketSender().sendInterfaceRemoval();
                        player.getInventory().delete(15897, 1);
                        player.getInventory().delete(15898, 10);
                        player.getInventory().add(new Item(15887, 1));
                        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(15887, 200)
                                .setText("You carefully attach the components to the chaps ", "and have it permanently repaired.").start(player);
                    })
                    .addCancel()
                    .start(player);
        } else if ((used.getId() == 15899 && usedWith.getId() == 15892) ||
                (usedWith.getId() == 15899 && used.getId() == 15892)) {
            if (player.getSkillManager().getCurrentLevel(Skill.SMITHING) < 90) {
                player.sendMessage("You need at least a Smithing level of 90 to be able to repair the mask.");
                return;
            }
            if (player.getInventory().getAmount(15899) < 8) {
                new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(15899, 200)
                        .setText("You need to have at least 8 Magical components to", "permanently repair the mask.").start(player);
                return;
            }

            new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                    .setItem(15892, 200)
                    .setText("Are you sure you want to repair @dre@" + ItemDefinition.forId(15892).getName() +"</col>?", "This action is irreversible.")
                    .add(DialogueType.OPTION)
                    .setOptionTitle("Repair the " + ItemDefinition.forId(15892).getName() +"?")
                    .firstOption("Confirm.", player2 -> {
                        if (!player.getInventory().contains(15892)) {
                            return;
                        }
                        if (!player.getInventory().contains(15899)) {
                            return;
                        }


                        player.getPacketSender().sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND);
                        AchievementManager.processFor(AchievementType.COMBINATION, player);
                        player.getSkillManager().addExperience(Skill.SMITHING, 2250);

                        player.getPacketSender().sendInterfaceRemoval();
                        player.getInventory().delete(15892, 1);
                        player.getInventory().delete(15899, 8);
                        player.getInventory().add(new Item(15877, 1));
                        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(15877, 200)
                                .setText("You carefully attach the components to the mask ", "and have it permanently repaired.").start(player);
                    })
                    .addCancel()
                    .start(player);
        } else if ((used.getId() == 15899 && usedWith.getId() == 15893) ||
                (usedWith.getId() == 15899 && used.getId() == 15893)) {
            if (player.getSkillManager().getCurrentLevel(Skill.SMITHING) < 90) {
                player.sendMessage("You need at least a Smithing level of 90 to be able to repair the robe top.");
                return;
            }
            if (player.getInventory().getAmount(15899) < 12) {
                new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(15899, 200)
                        .setText("You need to have at least 12 Magical components to", "permanently repair the robe top.").start(player);
                return;
            }

            new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                    .setItem(15893, 200)
                    .setText("Are you sure you want to repair @dre@" + ItemDefinition.forId(15893).getName() +"</col>?", "This action is irreversible.")
                    .add(DialogueType.OPTION)
                    .setOptionTitle("Repair the " + ItemDefinition.forId(15893).getName() +"?")
                    .firstOption("Confirm.", player2 -> {
                        if (!player.getInventory().contains(15893)) {
                            return;
                        }
                        if (!player.getInventory().contains(15899)) {
                            return;
                        }


                        player.getPacketSender().sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND);
                        AchievementManager.processFor(AchievementType.COMBINATION, player);
                        player.getSkillManager().addExperience(Skill.SMITHING, 2250);

                        player.getPacketSender().sendInterfaceRemoval();
                        player.getInventory().delete(15893, 1);
                        player.getInventory().delete(15899, 12);
                        player.getInventory().add(new Item(15879, 1));
                        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(15879, 200)
                                .setText("You carefully attach the components to the robe top ", "and have it permanently repaired.").start(player);
                    })
                    .addCancel()
                    .start(player);
        } else if ((used.getId() == 15899 && usedWith.getId() == 15894) ||
                (usedWith.getId() == 15899 && used.getId() == 15894)) {
            if (player.getSkillManager().getCurrentLevel(Skill.SMITHING) < 90) {
                player.sendMessage("You need at least a Smithing level of 90 to be able to repair the robe bottoms.");
                return;
            }
            if (player.getInventory().getAmount(15899) < 10) {
                new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(15899, 200)
                        .setText("You need to have at least 10 Magical components to", "permanently repair the robe bottoms.").start(player);
                return;
            }

            new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                    .setItem(15894, 200)
                    .setText("Are you sure you want to repair @dre@" + ItemDefinition.forId(15894).getName() +"</col>?", "This action is irreversible.")
                    .add(DialogueType.OPTION)
                    .setOptionTitle("Repair the " + ItemDefinition.forId(15894).getName() +"?")
                    .firstOption("Confirm.", player2 -> {
                        if (!player.getInventory().contains(15894)) {
                            return;
                        }
                        if (!player.getInventory().contains(15899)) {
                            return;
                        }


                        player.getPacketSender().sendSound(Sounds.RECHARGE_AND_UNCHARGE_ITEMS_SOUND);
                        AchievementManager.processFor(AchievementType.COMBINATION, player);
                        player.getSkillManager().addExperience(Skill.SMITHING, 2250);

                        player.getPacketSender().sendInterfaceRemoval();
                        player.getInventory().delete(15894, 1);
                        player.getInventory().delete(15899, 10);
                        player.getInventory().add(new Item(15881, 1));
                        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(15881, 200)
                                .setText("You carefully attach the components to the robe bottoms ", "and have it permanently repaired.").start(player);
                    })
                    .addCancel()
                    .start(player);
        }

        else {
            player.getPacketSender().sendMessage("Nothing interesting happens.", 1000);
        }
    }

    private static void itemOnObject(Player player, ItemOnObjectMessage message) {

        int interfaceType = message.getInterfaceId();
        final int objectId = message.getObjectId();
        final int objectY = message.getPosition().getY();
        final int itemSlot = message.getSlot();
        final int objectX = message.getPosition().getX();
        final int itemId = message.getId();

        if (itemSlot < 0 || itemSlot >= player.getInventory().capacity())
            return;

        final Item item = player.getInventory().getItems()[itemSlot];

        if (item == null || item.getId() != itemId)
            return;

        final Position position = new Position(objectX, objectY, player.getPosition().getZ());
        final Optional<GameObject> object = World.findObject(player, objectId, position);

        if (!player.getInventory().contains(item)) {
            return;
        }

        DebugManager.debug(player, "item-on-object", "used: "+item.getId()+" on: "+objectId+" "+position.toString());



        // Make sure the object actually exists in the region...
        if (object.isEmpty()) {
            Server.getLogger().info("Object with id " + objectId + " does not exist!");
            return;
        }

        final GameObject gameObject = object.get();
        final ObjectDefinition objectDefinition = gameObject.getDefinition();


        if (player.getLocalObject(objectId, new Position(objectX, objectY, player.getPosition().getZ())).isEmpty() && (!ClippedMapObjects.exists(gameObject) || !ObjectManager.existsAt(gameObject.getId(), gameObject.getPosition()))) {
            return;
        }

        if (objectDefinition == null) {
            Server.getLogger().info("ObjectDefinition for object " + objectId + " is null.");
            return;
        }

        if (objectDefinition.getName() != null && item.getDefinition().getName() != null)
            Logging.log("itemOnNPC", "[ItemOnObject]: " + player.getUsername() + " used item: " + item.getDefinition().getName() + " with object " + objectDefinition.getName() + ".");

        // Face object..
        player.setWalkToTask(
                new WalkToAction<>(player, gameObject, 1, () -> {

                    player.setPositionToFace(position);
                    if (!EntityExtKt.passedTime(player, Attribute.LAST_ACTION_BUTTON, 600, TimeUnit.MILLISECONDS, false, true)) {
                        return;
                    }

                    if (Math.abs(player.getPosition().getX() - objectX) > 15 || Math.abs(player.getPosition().getY() - objectY) > 15) {
                        player.getMotion().clearSteps();
                        player.getPacketSender().sendMinimapFlagRemoval();
                        return;
                    }

                    if (ItemActions.INSTANCE.handleItemOnObject(player, object.get(), message))
                        return;

                    if(PacketInteractionManager.handleItemOnObjectInteraction(player, item, object.get())) {
                        return;
                    }

                    if (AltarRunecrafting.INSTANCE.handleItemOnObject(player, item.getId(), objectId)) {
                        return;
                    }

                    if (CastleWars.processItemOnObject(player, object.orElse(null), item.getId())) {
                        return;
                    }

                    TiaraCrafting.INSTANCE.makeTiara(player, item.getId(), objectId);

                    if (BirdHouseActions.INSTANCE.useItemOnBirdHouse(player, item.getId(), position, objectId)) {
                        return;
                    }

                    if ((object.get().getId() == ObjectID.FIRE_5)
                            || object.get().getId() == ObjectID.FIRE_23
                            || object.get().getId() == ObjectID.FIRE_16
                            || object.get().getId() == ObjectID.FIRE_17
                            || object.get().getId() == ObjectID.FIRE_24
                            || object.get().getId() == ObjectID.FIRE_25
                            || object.get().getId() == ObjectID.FIRE_26) {
                        Optional<LightableLog> log = LightableLog.find(item.getId());
                        if (log.isPresent()) {
                            CreationMenu fmMenu = new SingleItemCreationMenu(player, log.get().getLogId(),
                                    "How many would you like to burn?", new CreationMenuAction() {
                                @Override
                                public void execute(int index, int item12, int amount) {
                                    SkillUtil.startSkillable(player, new Firemaking(log.get(), object.get(), amount));
                                }
                            }).open();
                            player.setCreationMenu(Optional.of(fmMenu));
                            return;
                        }
                    }

                    if (Cooking.itemOnCookingObject(player, item, object))
                        return;

                    if (Firemaking.handleBonfire(item, player, object))
                        return;

					if (objectId == MAGICAL_ANIMATOR) {
					    WarriorsGuild.handleAnimator(player, itemId);
						return;
					}
                    // Handle object..
                    switch (object.get().getId()) {
                        case POTTERS_WHEEL:
                        case POTTERS_WHEEL_2:
                        case POTTERS_WHEEL_3:
                            Pottery.openPottersWheelInterface(player);
                            break;
                        case DWARF_MULTICANNON:
                            if (player.cannon == null || player.cannon.getObj() != object.get()) {
                                player.getPacketSender().sendMessage("That isn't your cannon.");
                                return;
                            }

                            if (item.getId() == ItemID.CANNONBALL
                                    || item.getId() == ItemID.GRANITE_CANNONBALL) {
                                player.cannon.load();
                            }

                            break;
                        case 23609:

                            if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, true))
                                return;

                            EntityExtKt.markTime(player, Attribute.LAST_ACTION_BUTTON);
                            player.getMotion().clearSteps();
                            player.getPacketSender().sendMinimapFlagRemoval();
                            if (item.getId() == ItemID.ROPE) {
                                player.getInventory().delete(ItemID.ROPE, 1);
                                player.performAnimation(new Animation(535));
                                final GameObject current = object.get();
                                final DynamicGameObject copy = DynamicGameObject.createPublic(10230, current.getPosition().clone(), current.getObjectType(), current.getFace());
                                TaskManager.submit(new TimedObjectReplacementTask(current, copy, 30));
                            } else {
                                player.sendMessage("You need a rope to climb down this hole!");
                            }
                            break;
                        case ObjectID.WEB:
                        case WEB_2:
                        case WEB_3:
                        case WEB_4:
                            if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, false)) {
                                return;
                            }
                            if (item.getId() == 946 || item.getId() == 7447 || item.getId() == 20779
                                    || item.getId() == 20781 || item.getId() == 21059) {
                                player.performAnimation(new Animation(451));
                                player.getPacketSender().sendSound(Sounds.SLASH_WEB);
                                TaskManager.submit(new Task(1) {
                                    @Override
                                    protected void execute() {
                                        if (Misc.random(2) == 1) {
                                            TaskManager.submit(new TimedObjectReplacementTask(
                                                    object.get(),
                                                    DynamicGameObject.createPublic(734, object.get().getPosition(), object.get().getObjectType(), object.get().getFace()),
                                                    30));
                                            player.sendMessage("You slash through the web!");
                                        } else {
                                            player.sendMessage("You fail to cut through the web.");
                                        }
                                        stop();
                                    }
                                });
                            } else {
                                player.sendMessage("Nothing interesting happens.");
                            }
                            EntityExtKt.markTime(player, Attribute.LAST_PRAY);
                            break;
                        case ObjectID.FURNACE:
                        case ObjectID.FURNACE_2:
                        case ObjectID.FURNACE_3:
                        case ObjectID.FURNACE_4:
                        case ObjectID.FURNACE_5:
                        case ObjectID.FURNACE_6:
                        case ObjectID.FURNACE_7:
                        case ObjectID.FURNACE_8:
                        case ObjectID.FURNACE_9:
                        case ObjectID.FURNACE_10:
                        case ObjectID.FURNACE_11:
                        case ObjectID.FURNACE_12:
                        case ObjectID.FURNACE_13:
                        case ObjectID.FURNACE_14:
                        case ObjectID.FURNACE_15:
                        case ObjectID.FURNACE_16:
                        case ObjectID.FURNACE_17:
                        case ObjectID.FURNACE_18:
                        case ObjectID.FURNACE_19:
                        case 11978:
                            if (item.getId() == ItemID.GOLD_BAR) {
                                if (player.getSkillManager().getCurrentLevel(Skill.SMITHING) < 40) {
                                    DialogueManager.sendStatement(player, "You need a Smithing level of at least 40 to work gold.");
                                    return;
                                }
                                JewelryMaking.openInterface(player);
                                return;
                            } else if (item.getId() == ItemID.SILVER_BAR) {
                                JewelryMaking.openSilverInterface(player);
                                return;
                            } else {
                                if (item.getId() == 436 || item.getId() == 438 || item.getId() == 440 ||
                                        item.getId() == 442 || item.getId() == 444 || item.getId() == 447 ||
                                        item.getId() == 449 || item.getId() == 451) {
                                    for (Smithing.Bar bar : Smithing.Bar.values()) {
                                        player.getPacketSender().sendInterfaceModel(bar.getFrame(), bar.getBar(), 150);
                                    }
                                    player.getPacketSender().sendChatboxInterface(2400);
                                } else {
                                    player.sendMessage("Nothing interesting happens.");
                                }
                            }
                            break;

                        case ANVIL:
                        case ANVIL_2:
                        case ANVIL_3:
                        case ANVIL_4:
                        case ANVIL_5:
                        case ANVIL_6:
                            if (item.getId() == 2357 || item.getId() == 2355) {
                                new DialogueBuilder(DialogueType.PLAYER_STATEMENT)
                                        .setText("Perhaps I should use this in a furnace instead.").start(player);
                                //player.getPacketSender().sendMessage("Gold bars cannot be made into weapons and armour as other bars can.");
                                //player.getPacketSender().sendMessage("To craft gold bars into jewellery, you must use a gold bar on a furnace with the proper mould in your inventory.");
                                return;
                            } else if (item.getId() == 2351 && player.getSkillManager().getMaxLevel(Skill.SMITHING) < 15) {
                                DialogueManager.sendStatement(player, "You need a Smithing level of at least 15 to work iron bars.");
                                return;
                            } else if (item.getId() == 2353 && player.getSkillManager().getMaxLevel(Skill.SMITHING) < 30) {
                                DialogueManager.sendStatement(player, "You need a Smithing level of at least 30 to work steel bars.");
                                return;
                            } else if (item.getId() == 2359 && player.getSkillManager().getMaxLevel(Skill.SMITHING) < 50) {
                                DialogueManager.sendStatement(player, "You need a Smithing level of at least 5 to work mithril bars.");
                                return;
                            } else if (item.getId() == 2361 && player.getSkillManager().getMaxLevel(Skill.SMITHING) < 70) {
                                DialogueManager.sendStatement(player, "You need a Smithing level of at least 70 to work adamantite bars.");
                                return;
                            } else if (item.getId() == 2363 && player.getSkillManager().getMaxLevel(Skill.SMITHING) < 85) {
                                DialogueManager.sendStatement(player, "You need a Smithing level of at least 85 to work runite bars.");
                                return;
                            }
                            if (item.getId() == 436 || item.getId() == 438 || item.getId() == 440 ||
                                    item.getId() == 442 || item.getId() == 444 || item.getId() == 447 || item.getId() == 453 ||
                                    item.getId() == 449 || item.getId() == 451) {
                                DialogueManager.sendStatement(player, "You need to purify the ore first, by smelting it in a furnace.");
                                return;
                            }
                            if (item.getId() == 2349 || item.getId() == 2351 || item.getId() == 2353 ||
                                    item.getId() == 2355 || item.getId() == 2359 ||
                                    item.getId() == 2361 || item.getId() == 2363) {
                                if ((object.get().getId() == ANVIL)) {
                                    if (!QuestManager.hasCompletedQuest(player, "Doric's Quest")) {
                                        player.sendMessage("You must complete the quest 'Doric's Quest' to be able to use this anvil.");
                                        return;
                                    }
                                }
                                Smithing.EquipmentMaking.openInterface(player);
                            } else {
                                player.sendMessage("Nothing interesting happens.");
                            }
                            break;
                        case 1276: // All choppable trees
                        case 1277:
                        case 1278:
                        case 3037:
                        case 1279:
                        case 1280:
                        case 1282:
                        case 1283:
                        case 1284:
                        case 1285:
                        case 1286:
                        case 1289:
                        case 1290:
                        case 1291:
                        case 1315:
                        case 1316:
                        case 1318:
                        case 1319:
                        case 1330:
                        case 1331:
                        case 1332:
                        case 1365:
                        case 1383:
                        case 1384:
                        case 2091:
                        case 2092:
                        case 3033:
                        case 3034:
                        case 3035:
                        case 3036:
                        case 3881:
                        case 3882:
                        case 3883:
                        case 5902:
                        case 5903:
                        case 5904:
                        case 2023:
                        case 1281:
                        case 1751:
                        case 10820:
                        case 1308:
                        case 1750:
                        case 1760:
                        case 1756:
                        case 1758:
                        case 5551:
                        case 5552:
                        case 5553:
                        case 10819:
                        case 10829:
                        case 10831:
                        case 10833:
                        case 1936:
                        case 1292:
                        case 21731:
                        case 21732:
                        case 21733:
                        case 21734:
                        case 21735:
                        case 9237:
                        case 9238:
                        case 9239:
                        case 9240:
                        case 1307:
                        case 4677:
                        case 1759:
                        case 1309:
                        case 1753:
                        case 10822:
                        case 1306:
                        case 1761:
                        case 10834:
                            if (item.getId() == 1351 || item.getId() == 1349 || item.getId() == 1353 || item.getId() == 1361 || item.getId() == 1355
                                    || item.getId() == 1357 || item.getId() == 1359 || item.getId() == 15222 || item.getId() == 6739
                                    || item.getId() == 13241 || item.getId() == 13242 || item.getId() == 20011 || item.getId() == 23279
                                    || item.getId() == 23673 || item.getId() == 23821 || item.getId() == 23862) { // Up to Crystal axe
                                Optional<TreeType> tree = TreeType.forObjectId(objectId);
                                SkillUtil.startSkillable(player, new Woodcutting(object.get(), tree.get()));
                                return;
                            } else {
                                player.sendMessage("Nothing interesting happens.");
                            }
                            break;
                        case 7454: // All mineable ores
                        case 9711:
                        case 9712:
                        case 9713:
                        case 15503:
                        case 15504:
                        case 10943:
                        case 15505:
                        case 11387:
                        case 7484:
                        case 9708:
                        case 9709:
                        case 9710:
                        case 11161:
                        case 11936:
                        case 11960:
                        case 11961:
                        case 11962:
                        case 11189:
                        case 11190:
                        case 11191:
                        case 29231:
                        case 29230:
                        case 11362:
                        case 11363:
                        case 11386:
                        case 2090:
                        case 7485:
                        case 7486:
                        case 9714:
                        case 9715:
                        case 9716:
                        case 11933:
                        case 11957:
                        case 11958:
                        case 11959:
                        case 11186:
                        case 11187:
                        case 11188:
                        case 2094:
                        case 29227:
                        case 11360:
                        case 11361:
                        case 29229:
                        case 7488:
                        case 7455:
                        case 9717:
                        case 9718:
                        case 9719:
                        case 2093:
                        case 11954:
                        case 11955:
                        case 11956:
                        case 11364:
                        case 11365:
                        case 29221:
                        case 29222:
                        case 29223:
                        case 2100:
                        case 7457:
                        case 7490:
                        case 2101:
                        case 29226:
                        case 29225:
                        case 11948:
                        case 11949:
                        case 11368:
                        case 11369:
                        case 7489:
                        case 7456:
                        case 5770:
                        case 29216:
                        case 29215:
                        case 29217:
                        case 11965:
                        case 11964:
                        case 11963:
                        case 11930:
                        case 11931:
                        case 11932:
                        case 11366:
                        case 11367:
                        case 7458:
                        case 7491:
                        case 9720:
                        case 9721:
                        case 9722:
                        case 11951:
                        case 11183:
                        case 11184:
                        case 11185:
                        case 11370:
                        case 11371:
                        case 2099:
                        case 7459:
                        case 7492:
                        case 25370:
                        case 25368:
                        case 5786:
                        case 5784:
                        case 11944:
                        case 11945:
                        case 11946:
                        case 29236:
                        case 11947:
                        case 11372:
                        case 11373:
                        case 11942:
                        case 11943:
                        case 7493:
                        case 7460:
                        case 7060:
                        case 11374:
                        case 11375:
                        case 11941:
                        case 11939:
                        case 29233:
                        case 29235:
                        case 7494:
                        case 7461:
                        case 14859:
                        case 4860:
                        case 2106:
                        case 2107:
                        case 11376:
                        case 11377:
                        case 13388:
                        case 13389:
                            if (item.getId() == 1265 || item.getId() == 1267 || item.getId() == 1269 || item.getId() == 1271 || item.getId() == 1273
                                    || item.getId() == 1275 || item.getId() == 15221 || item.getId() == 13243 || item.getId() == 13244
                                    || item.getId() == 11920 || item.getId() == 12797 || item.getId() == 20014 || item.getId() == 23276
                                    || item.getId() == 23677 || item.getId() == 23680 || item.getId() == 23863) { // Up to Crystal pickaxe
                                Optional<RockType> rock = RockType.forObjectId(objectId);
                                SkillUtil.startSkillable(player, new Mining(object.get(), rock.get()));
                                return;
                            } else {
                                player.sendMessage("Nothing interesting happens.");
                            }
                            break;
                        case 26502:
//                            if (item.getId() == 11942) {
//                                if (player.getInventory().contains(11942)) {
//                                    player.getInventory().delete(11942, 1);
//                                    player.getPacketSender().sendMessage("You used the key to enter to the boss chamber.");
//                                }
//                            } else {
//                                return;
//                            }
//                            if (player.getPosition().getY() == 5294) {
//                                player.moveTo(new Position(2839, 5295, 2));
//                            } else {
//                                player.moveTo(new Position(2839, 5294, 2));
//                            }
                            break;
                        case 26503:
//                            if (item.getId() == 11942) {
//                                if (player.getInventory().contains(11942)) {
//                                    player.getInventory().delete(11942, 1);
//                                    player.getPacketSender().sendMessage("You used the key to enter to the boss chamber.");
//                                }
//                            } else {
//                                player.getPacketSender().sendMessage("You need an ecumenical key to enter to the boss chamber.");
//                                return;
//                            }
//                            if (player.getPosition().getX() == 2862) {
//                                player.moveTo(new Position(2863, 5354, 2));
//                            } else {
//                                player.moveTo(new Position(2861, 5354, 2));
//                            }
                            break;
                        case 26505:
//                            if (item.getId() == 11942) {
//                                if (player.getInventory().contains(11942)) {
//                                    player.getInventory().delete(11942, 1);
//                                    player.getPacketSender().sendMessage("You used the key to enter to the boss chamber.");
//                                }
//                            } else {
//                                player.getPacketSender().sendMessage("You need an ecumenical key to enter to the boss chamber.");
//                                return;
//                            }
//                            if (player.getPosition().getY() == 5333 || player.getPosition().getY() == 5334) {
//                                player.moveTo(new Position(2925, 5332, 2));
//                            } else {
//                                player.moveTo(new Position(2925, 5333, 2));
//                            }
                            break;
                        case 26504:
//                            if (item.getId() == 11942) {
//                                if (player.getInventory().contains(11942)) {
//                                    player.getInventory().delete(11942, 1);
//                                    player.getPacketSender().sendMessage("You used the key to enter to the boss chamber.");
//                                }
//                            } else {
//                                player.getPacketSender().sendMessage("You need an ecumenical key to enter to the boss chamber.");
//                                return;
//                            }
//                            if (player.getPosition().getX() == 2909) {
//                                player.moveTo(new Position(2908, 5265, 0));
//                            } else {
//                                player.moveTo(new Position(2909, 5265, 0));
//                            }
                            break;
                        case ObjectID.BANK_BOOTH_2:
                        case ObjectID.BANK_BOOTH:
                        case ObjectID.BAKERS_STALL:
                        case ObjectID.BANK_BOOTH_4:
                        case ObjectID.BANK_BOOTH_5:
                        case ObjectID.BANK_BOOTH_7:
                        case ObjectID.BANK_BOOTH_8:
                        case ObjectID.BANK_BOOTH_9:
                        case ObjectID.BANK_BOOTH_10:
                        case ObjectID.BANK_BOOTH_11:
                        case ObjectID.BANK_BOOTH_12:
                        case ObjectID.BANK_BOOTH_13:
                        case ObjectID.BANK_BOOTH_14:
                        case ObjectID.BANK_BOOTH_15:
                        case ObjectID.BANK_BOOTH_16:
                        case ObjectID.BANK_BOOTH_17:
                        case ObjectID.BANK_BOOTH_18:
                        case ObjectID.BANK_BOOTH_19:
                        case ObjectID.BANK_BOOTH_20:
                        case ObjectID.BANK_BOOTH_21:
                        case ObjectID.BANK_BOOTH_22:
                        case ObjectID.BANK_BOOTH_23:
                        case ObjectID.BANK_BOOTH_24:
                        case ObjectID.BANK_BOOTH_25:
                        case ObjectID.BANK_BOOTH_26:
                        case ObjectID.BANK_BOOTH_27:
                        case ObjectID.BANK_BOOTH_28:
                        case ObjectID.BANK_BOOTH_29:
                        case ObjectID.BANK_BOOTH_30:
                        case ObjectID.BANK_BOOTH_31:
                        case ObjectID.BANK_BOOTH_32:
                        case ObjectID.BANK_BOOTH_33:
                        case ObjectID.BANK_BOOTH_34:
                        case ObjectID.BANK_BOOTH_35:
                        case ObjectID.BANK_BOOTH_36:
                        case ObjectID.BANK_BOOTH_37:
                        case ObjectID.BANK_BOOTH_38:
                        case ObjectID.BANK_BOOTH_39:
                        case ObjectID.BANK_BOOTH_40:
                        case ObjectID.BANK_BOOTH_41:
                        case ObjectID.BANK_BOOTH_42:
                        case ObjectID.BANK_BOOTH_43:
                        case ObjectID.BANK_BOOTH_44:
                        case ObjectID.BANK_BOOTH_45:
                        case ObjectID.BANK_BOOTH_10355:
                        case ObjectID.BANK_CHEST:
                        case ObjectID.BANK_CHEST_2:
                        case ObjectID.BANK_CHEST_3:
                        case ObjectID.BANK_CHEST_4:
                        case ObjectID.BANK_CHEST_5:
                        case ObjectID.OPEN_CHEST_8:
                        case ObjectID.OPEN_CHEST_9:
                        case ObjectID.OPEN_CHEST_10:
                        case ObjectID.OPEN_CHEST_11:
                        case ObjectID.OPEN_CHEST_12:
                        case ObjectID.OPEN_CHEST_13:
                        case ObjectID.OPEN_CHEST_14:
                        case ObjectID.OPEN_CHEST_15:
                        case ObjectID.OPEN_CHEST_16:
                        case ObjectID.OPEN_CHEST_17:
                        case ObjectID.OPEN_CHEST_18:
                        case ObjectID.BANK_CHEST_10:
                        case ObjectID.BANK_CHEST_11:
                        case ObjectID.BANK_CHEST_12:
                        case ObjectID.BANK_CHEST_13:
                        case ObjectID.BANK_CHEST_14:
                        case ObjectID.BANK_CHEST_15:
                        case ObjectID.BANK_CHEST_16:
                        case ObjectID.BANK_CHEST_17:
                        case ObjectID.BANK_CHEST_18:
                        case ObjectID.BANK_CHEST_9:
                        case ObjectID.BANK_CHEST_8:
                        case ObjectID.BANK_CHEST_7:
                        case ObjectID.BANK_CHEST_6:
                        case 34343:
                        case 27291:
                        case 7478:
                        case 27253:
                        case ObjectID.TIGHTROPE_4: // Varrock bank booths (W big bank)
                            ObjectDefinition object2 = ObjectDefinition.forId(object.get().getId());
                            if (item.getId() == 995 || item.getId() == 13204) {
                                if (item.getId() == 995 && object2.getName().contains("Bank")) {
                                    PlatinumToken.convertCoinsToTokens(player);
                                } else if (item.getId() == 13204 && object2.getName().contains("Bank")) {
                                    PlatinumToken.convertTokensToCoins(player);
                                }
                            } else {
                                  BankUtil.createNotingDialogue(object2, item, player);
                            }
                            break;
                        case CLOSED_CHEST_2: // Muddy chest
                            if (item.getId() == 991) {
                                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 2, TimeUnit.SECONDS, false, false)) {
                                    return;
                                }
                                if (!player.getInventory().contains(991)) {
                                    player.getPacketSender().sendMessage("The chest is locked.");
                                    player.getPacketSender().sendSound(Sounds.USE_KEY_ON_LOCKED_DOOR);
                                    return;
                                }
                                if (player.getInventory().countFreeSlots() < 3) {
                                    player.getPacketSender().sendMessage("You need at least 3 free inventory slots to open this chest.");
                                    return;
                                }
                                EntityExtKt.markTime(player, Attribute.LAST_PRAY);
                                player.getInventory().delete(991, 1);
                                player.performAnimation(new Animation(536, 15));
                                player.getPacketSender().sendSound(Sounds.OPEN_BANK_BOOTH);
                                player.BLOCK_ALL_BUT_TALKING = true;
                                TaskManager.submit(new Task(2) {
                                    @Override
                                    public void execute() {
                                        int luckyItem = -1;
                                        int pkResources = Thieving.PVP_RESOURCES_ITEMS[Misc.getRandomInclusive(Thieving.PVP_RESOURCES_ITEMS.length - 1)];
                                        int resourcesRandom = 25 + Misc.getRandomInclusive(35);

                                        int bmRandom = 2500 + Misc.getRandomInclusive(2500); // 3rd item is always cash bonus reward
                                        player.getPacketSender().sendItemOnInterface(6963, BLOOD_MONEY, 0, bmRandom);
                                        player.getPacketSender().sendItemOnInterface(6963, pkResources, 1, resourcesRandom);
                                        player.getPacketSender().sendItemOnInterface(6963, -1, 2, -1);
                                        if (Misc.getRandomInclusive(6) == 1) {
                                            luckyItem = Thieving.MUDDY_CHEST_ITEMS[Misc.getRandomInclusive(Thieving.MUDDY_CHEST_ITEMS.length - 1)];
                                            PlayerUtil.broadcastMessage("<img=754> @whi@" + PlayerUtil.getImages(player) + "" + player.getUsername() +" has just unlocked the Muddy chest in the Wilderness, and received a rare bonus item. Go get em!");
                                            player.getPacketSender().sendItemOnInterface(6963, luckyItem, 2, -1);
                                        }
                                        player.getPacketSender().sendItemOnInterface(6963, -1, 3, -1);
                                        player.getPacketSender().sendItemOnInterface(6963, -1, 4, -1);
                                        player.getPacketSender().sendItemOnInterface(6963, -1, 5, -1);
                                        player.getPacketSender().sendItemOnInterface(6963, -1, 6, -1);
                                        player.getPacketSender().sendInterface(6960);
                                        player.getInventory().add(BLOOD_MONEY, bmRandom);
                                        player.getInventory().add(pkResources, resourcesRandom);
                                        player.getInventory().add(luckyItem, 1);
                                        player.getPacketSender().sendMessage("You have unlocked the chest with your muddy key.");
                                        AchievementManager.processFor(AchievementType.ALL_MUD, player);
                                        AchievementManager.processFor(AchievementType.MUDDY_WORK, player);
                                        player.getPoints().increase(AttributeManager.Points.MUDDY_CHESTS_OPENED, 1); // Increase points
                                        PlayerTaskManager.progressTask(player, DailyTask.MUDDY_CHEST);
                                        PlayerTaskManager.progressTask(player, WeeklyTask.MUDDY_CHEST);
                                        player.getCollectionLog().createOrUpdateEntry(player,  "Muddy Chest", new Item(luckyItem)); // Collection Log
                                        player.BLOCK_ALL_BUT_TALKING = false;
                                        stop();
                                    }
                                });
                            } else {
                                player.getPacketSender().sendMessage("You need a muddy key to open this chest.", 1000);
                                return;
                            }
                            break;
                        case CLOSED_CHEST_3: // Crystal chest
                            if (item.getId() == 989) {
                                if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 2, TimeUnit.SECONDS, false, false)) {
                                    return;
                                }
                                if (!player.getInventory().contains(989)) {
                                    player.getPacketSender().sendMessage("The crystal chest is locked.");
                                    player.getPacketSender().sendSound(Sounds.USE_KEY_ON_LOCKED_DOOR);
                                    return;
                                }
                                if (player.getInventory().countFreeSlots() < 4) {
                                    player.getPacketSender().sendMessage("You need at least 4 free inventory slots to open the crystal chest.");
                                    return;
                                }
                                EntityExtKt.markTime(player, Attribute.LAST_PRAY);
                                player.getInventory().delete(989, 1);
                                player.performAnimation(new Animation(536, 15));
                                player.getPacketSender().sendSound(Sounds.OPEN_BANK_BOOTH);
                                player.BLOCK_ALL_BUT_TALKING = true;
                                TaskManager.submit(new Task(2) {
                                    @Override
                                    public void execute() {
                                        int item2;
                                        int item1 = Thieving.CRYSTAL_CHEST_LOOT[Misc.getRandomInclusive(Thieving.CRYSTAL_CHEST_LOOT.length - 1)];
                                        if (Misc.getRandomInclusive(4) == 1) { // You only have 1/4 chance of getting a second good item from the chest
                                            item2 = Thieving.CRYSTAL_CHEST_LOOT[Misc.getRandomInclusive(Thieving.CRYSTAL_CHEST_LOOT.length - 1)];
                                        } else {
                                            item2 = Thieving.NOOBISH_ITEMS[Misc.getRandomInclusive(Thieving.NOOBISH_ITEMS.length - 1)];
										}
                                        int moneyRandom = 150_000 + Misc.getRandomInclusive(500000); // 3rd item is always cash bonus reward
                                        player.getPacketSender().sendItemOnInterface(6963, item1, 0, 1);
                                        player.getPacketSender().sendItemOnInterface(6963, item2, 1, 1);
                                        player.getPacketSender().sendItemOnInterface(6963, 995, 2, moneyRandom);
                                        player.getPacketSender().sendItemOnInterface(6963, -1, 3, -1);
                                        player.getPacketSender().sendItemOnInterface(6963, -1, 4, -1);
                                        player.getPacketSender().sendItemOnInterface(6963, -1, 5, -1);
                                        player.getPacketSender().sendItemOnInterface(6963, -1, 6, -1);
                                        player.getPacketSender().sendInterface(6960);
                                        player.getInventory().add(item1, 1);
                                        player.getInventory().add(item2, 1);
                                        player.getInventory().add(995, moneyRandom);
                                        player.getPacketSender().sendMessage("You have unlocked the chest with your crystal key.");
                                        AchievementManager.processFor(AchievementType.CRYSTAL_MASTER, player);
                                        AchievementManager.processFor(AchievementType.CRYSTAL_EXPERT, player);
                                        AchievementManager.processFor(AchievementType.INITIAL_CRYSTAL, player);
                                        player.getPoints().increase(AttributeManager.Points.CRYSTAL_CHESTS_OPENED, 1); // Increase points
                                        PlayerTaskManager.progressTask(player, DailyTask.CRYSTAL_CHEST);
                                        PlayerTaskManager.progressTask(player, WeeklyTask.CRYSTAL_CHEST);
                                        player.getCollectionLog().createOrUpdateEntry(player,  "Crystal Chest", new Item(item1));
                                        player.getCollectionLog().createOrUpdateEntry(player,  "Crystal Chest", new Item(item2));
                                        player.BLOCK_ALL_BUT_TALKING = false;
                                        stop();
                                    }
                                });
                            } else {
                                player.getPacketSender().sendMessage("You need a crystal key to open this chest.", 1000);
                                return;
                            }
                            break;

                        case ObjectID.VOLCANIC_FORGE:
                            if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, false)) {
                                return;
                            }
                            if (!player.getInventory().contains(11933) && !player.getInventory().contains(11932)
                                    && !player.getInventory().contains(11931) && !player.getInventory().contains(11930)
                                    && !player.getInventory().contains(11929)
                                    && !player.getInventory().contains(11928)) {
                                player.getPacketSender().sendMessage("Nothing interesting happens.");
                                return;
                            }
                            if (item.getId() == 11933 || item.getId() == 11932 || item.getId() == 11931) {
                                if (player.getInventory().contains(11933) && player.getInventory().contains(11932)
                                        && player.getInventory().contains(11931)) {
                                    player.getInventory().delete(11933, 1);
                                    player.getInventory().delete(11932, 1);
                                    player.getInventory().delete(11931, 1);
                                    player.getInventory().add(11924, 1);
                                } else {
                                    player.getPacketSender()
                                            .sendMessage("You don't have all the required shards to forge a ward.");
                                    return;
                                }
                            } else if (item.getId() == 11930 || item.getId() == 11929 || item.getId() == 11928) {
                                if (player.getInventory().contains(11930) && player.getInventory().contains(11929)
                                        && player.getInventory().contains(11928)) {
                                    player.getInventory().delete(11930, 1);
                                    player.getInventory().delete(11929, 1);
                                    player.getInventory().delete(11928, 1);
                                    player.getInventory().add(11926, 1);
                                } else {
                                    player.getPacketSender()
                                            .sendMessage("You don't have all the required shards to forge a ward.");
                                    return;
                                }
                            }
                            EntityExtKt.markTime(player, Attribute.LAST_PRAY);
                            player.getMotion().update(MovementStatus.DISABLED);
                            player.getPacketSender().sendMessage(
                                    "You forge the shield pieces together in the chambers of fire and are blown back by the intense heat.");
                            player.getPacketSender().sendSound(Sounds.FIRE_EXPLODING_SOUND);
                            player.performAnimation(new Animation(899));
                            TaskManager.submit(new Task(5) {
                                @Override
                                public void execute() {
                                    player.getMotion().update(MovementStatus.NONE);
                                    player.performAnimation(new Animation(846));
                                    player.say("Ouch! that was hot!");
                                    player.getPacketSender()
                                            .sendMessage("The intensive heat pulls your character backwards!");
                                    stop();
                                }
                            });
                            break;
                        case ObjectID.FOUNTAIN_OF_RUNE:
                            int[] amulets = {1710, 1708, 1706, 1704, 1712, ItemID.AMULET_OF_GLORY_5_};
                            int[] bracelets = {11118, 11120, 11122, 11124, 11126, 11970};
                            int[] necklaces = {11105, 11107, 11109, 11111, 11113, 11970};
                            int[] rings = {2572, 11988, 11986, 11984, 11982};
                            int[] imbued_rings = {12785, 20790, 20789, 20788, 20787};

                            if (Arrays.stream(amulets).anyMatch(glory -> item.getId() == glory) || Arrays.stream(bracelets).anyMatch(bracelet -> item.getId() == bracelet)
                            || Arrays.stream(necklaces).anyMatch(necklace -> item.getId() == necklace)
                            || Arrays.stream(rings).anyMatch(ring -> item.getId() == ring)
                            || Arrays.stream(imbued_rings).anyMatch(imbued_ring -> item.getId() == imbued_ring)) {
                                player.performAnimation(new Animation(832));
                                DialogueManager.sendStatement(player,
                                        "You feel a power emanating from the fountain as it recharges your jewellery.");
                            } else {
                                player.sendMessage("Nothing interesting happens.");
                                break;
                            }
                            if (Arrays.stream(amulets).anyMatch(glory -> item.getId() == glory)) {
                                int amount = 0;
                                for (int i : amulets) {
                                    amount += player.getInventory().getAmount(i);
                                    player.getInventory().delete(i, player.getInventory().getAmount(i));
                                }
                                if (Misc.getRandomInclusive(1250) < amount) {

                                    player.getInventory().add(ItemID.AMULET_OF_ETERNAL_GLORY, 1);
                                    DialogueManager.sendStatement(player,
                                            "The power of the fountain is transferred into an amulet of eternal glory. It will now have unlimited charges.");
                                } else {
                                    player.getInventory().add(ItemID.AMULET_OF_GLORY_6_, amount);
                                }
                            }
                            if (Arrays.stream(bracelets).anyMatch(bracelet -> item.getId() == bracelet)) {
                                int amount = 0;
                                for (int i : bracelets) {
                                    amount += player.getInventory().getAmount(i);
                                    player.getInventory().delete(i, player.getInventory().getAmount(i));
                                }
                                player.getInventory().add(ItemID.COMBAT_BRACELET_6_, amount);
                            }
                            if (Arrays.stream(necklaces).anyMatch(necklace -> item.getId() == necklace)) {
                                int amount = 0;
                                for (int i : necklaces) {
                                    amount += player.getInventory().getAmount(i);
                                    player.getInventory().delete(i, player.getInventory().getAmount(i));
                                }
                                player.performAnimation(new Animation(832));
                                player.getInventory().add(ItemID.SKILLS_NECKLACE_6_, amount);
                            }
                            if (Arrays.stream(rings).anyMatch(ring -> item.getId() == ring)) {
                                int amount = 0;
                                for (int i : rings) {
                                    amount += player.getInventory().getAmount(i);
                                    player.getInventory().delete(i, player.getInventory().getAmount(i));
                                }
                                player.getInventory().add(ItemID.RING_OF_WEALTH_5_, amount);
                            }
                            if (Arrays.stream(imbued_rings).anyMatch(imbued_ring -> item.getId() == imbued_ring)) {
                                int amount = 0;
                                for (int i : imbued_rings) {
                                    amount += player.getInventory().getAmount(i);
                                    player.getInventory().delete(i, player.getInventory().getAmount(i));
                                }
                                player.getInventory().add(ItemID.RING_OF_WEALTH_I5_, amount);
                            }
                            break;
                        case ALTAR: // Bone on Altar
                        case ALTAR_2:
                        case ALTAR_3:
                        case ALTAR_4:
                        case ALTAR_5:
                        case ALTAR_6:
                        case ALTAR_7:
                        case ALTAR_8:
                        case ALTAR_9:
                        case ALTAR_10:
                        case ALTAR_11:
                        case ALTAR_12:
                        case ALTAR_13:
                        case ALTAR_14:
                        case ALTAR_15:
                        case ALTAR_16:
                        case ALTAR_17:
                        case ALTAR_18:
                        case ALTAR_19:
                        case ALTAR_20:
                        case ALTAR_21:
                        case ALTAR_22:
                        case ALTAR_23:
                        case ALTAR_24:
                        case ALTAR_25:
                        case ALTAR_26:
                        case ALTAR_27:
                        case ALTAR_28:
                        case ALTAR_29:
                        case ALTAR_30:
                        case ALTAR_31:
                        case ALTAR_32:
                        case ALTAR_33:
                        case ALTAR_34:
                        case ALTAR_35:
                        case ALTAR_36:
                        case ALTAR_37:
                        case ALTAR_38:
                        case ALTAR_39:
                        case ALTAR_40:
                        case ALTAR_41:
                        case ALTAR_42:
                        case ALTAR_44:
                        case ALTAR_45:
                        case CHAOS_ALTAR_2:
                        case SHRINE:
                        case 18258:
                        case 16648:
                        case 29941:
                            Optional<BuriableBone> b = BuriableBone.forId(item.getId());
                            if (b.isPresent()) {
                                CreationMenu menu = new SingleItemCreationMenu(player, itemId,
                                        "How many would you like to offer?", (index, item12, amount) -> SkillUtil.startSkillable(
                                        player, new AltarOffering(b.get(), object.get(), amount))).open();
                                player.setCreationMenu(Optional.of(menu));
                            } else {
                                DialogueManager.sendStatement(player, "The gods are only pleased with some bones.");
                            }
                            break;
                        case ObjectID.MOUNTED_MAX_CAPE_2:
                            if (item.getId() == 13342 || item.getId() == 13281) {
                                DialogueManager.start(player, 2706);
                                player.setDialogueOptions(new DialogueOptions() {
                                    @Override
                                    public void handleOption(Player player1, int option) {
                                        if (option == 1) {
                                            ExchangeMaxCape.gamble(player1);
                                        }
                                        player1.getPacketSender().sendInterfaceRemoval();
                                    }
                                });
                            } else {
                                player.getPacketSender().sendMessage("Nothing interesting happens.", 1000);
                                return;
                            }
                        default:
                            player.getPacketSender().sendMessage("Nothing interesting happens.", 1000);
                            break;
                    }
                }));
    }

    private static void itemOnItemOnGround(Player player, ItemOnGroundItemMessage message) {

        int interfaceType = message.getInterfaceId();
        int usedItemId = message.getId();
        int groundItemId = message.getGroundItemId();
        int y = message.getPosition().getY();
        int unknown = message.getSlot();
        int x = message.getPosition().getX();

		if (!player.getLastItemPickup().elapsed(300))
            return;

        if (!player.getInventory().contains(usedItemId))
            return;

        // Verify ground item..
        Optional<ItemOnGround> groundItem = ItemOnGroundManager
                .getItemOnGround(Optional.of(player.getUsername()), groundItemId, new Position(x, y));

        if (groundItem.isEmpty())
            return;


        player.setWalkToTask(new WalkToAction<>(player, groundItem.get(), 0, () -> {
            if (Math.abs(player.getPosition().getX() - x) > 20 || Math.abs(player.getPosition().getY() - y) > 20) {
                player.getMotion().clearSteps();
                player.getPacketSender().sendMinimapFlagRemoval();
                return;
            }

            player.getLastItemPickup().reset();

            // Handle used item..
            if (usedItemId == TINDERBOX) {

                Optional<LightableLog> log = LightableLog.find(groundItemId);

                if (log.isEmpty()) {
                    player.sendMessage("Nothing interesting happens.");
                    return;
                }
                if (Firemaking.initItemOnGround(player, TINDERBOX, groundItemId)) {
                    return;
                }

                SkillUtil.startSkillable(player, new Firemaking(log.get(), groundItem.get()));
            } else {
                player.sendMessage("Nothing interesting happens.", 1000);
            }
        }));
    }

    private static void itemOnNpc(final Player player, ItemOnNpcMessage message) {

        final int id = message.getId();
        final int index = message.getIndex();
        final int slot = message.getSlot();

        if (index < 0 || index > World.getNpcs().capacity())
            return;

        if (slot < 0 || slot > player.getInventory().getItems().length)
            return;

        final Item item = player.getInventory().getItems()[slot];

        if (item == null || item.getId() != id)
            return;

        final NPC npc = World.getNpcs().get(index);

        if (npc == null)
            return;

        if (player.getInventory().getItems()[slot].getId() != id)
            return;

        // Logging
        if (id != -1 && npc.fetchDefinition().getName() != null)
            Logging.log("itemOnNPC", "[ItemOnNpc]: " + player.getUsername() + " used item: " + ItemDefinition.forId(id).getName() + " with NPC " + npc.fetchDefinition().getName() + "@.");

       player.setEntityInteraction(npc);

        final Executable action = () -> {
            if (Math.abs(player.getPosition().getX() - npc.getPosition().getX()) > 20 || Math.abs(player.getPosition().getY() - npc.getPosition().getY()) > 20) {
                player.getMotion().clearSteps();
                player.getPacketSender().sendMinimapFlagRemoval();
                return;
            }

            player.subscribe(event -> {

                if (event == PlayerEvents.MOVED || event == PlayerEvents.LOGGED_OUT) {
                    npc.resetEntityInteraction();
                    return true;
                }

                final Entity playerInteracting = player.getInteractingEntity();
                final Entity npcInteracting = npc.getInteractingEntity();

                if (playerInteracting instanceof NPC && playerInteracting != npc) {
                    npc.resetEntityInteraction();
                    return true;
                }

                if (npcInteracting instanceof Player && npcInteracting != player) {
                    npc.resetEntityInteraction();
                    return true;
                }

                if (npc.getPosition().getDistance(player.getPosition()) > 1) {
                    npc.debug("player too far away, resetting...");
                    npc.resetEntityInteraction();
                    return true;
                }
                return false;
            });
            if (npc != null && npc.fetchDefinition().getName() != null) {
                if (!npc.fetchDefinition().getName().toLowerCase().equals("fishing spot")
                        && npc.getId() != PUMPY && npc.getId() != DUMPY && npc.getId() != DUMPY_7387 && npc.getId() != STUMPY
                        && npc.getId() != NUMPTY && npc.getId() != THUMPY
                        && npc.getId() != DWARVEN_MINER
                        && npc.getId() != DWARVEN_MINER_2435
                        && npc.getId() != DWARVEN_MINER_2436
                        && npc.getId() != DWARVEN_MINER_2437
                        && npc.getId() != DWARVEN_MINER_2438
                        && npc.getId() != DWARVEN_MINER_2439
                        && npc.getId() != DWARVEN_MINER_2440
                        && npc.getId() != DWARVEN_MINER_2441
                        && npc.getId() != DWARVEN_MINER_2442
                        && npc.getId() != DWARVEN_MINER_2443
                        && npc.getId() != DWARVEN_MINER_2444
                        && npc.getId() != DWARVEN_MINER_2445
                        && npc.getId() != DWARVEN_MINER_2446
                        && npc.getId() != DWARVEN_MINER_2447
                        && npc.getId() != DWARVEN_MINER_2448) {
                    npc.setEntityInteraction(player);
                }
            }

            if(PacketInteractionManager.handleItemOnEntity(player, item, npc)) {
                return;
            }

            switch (npc.getId()) {
                case NpcID.BARRICADE:
                    if (item.getId() == 590) {
                        BarricadeManager.fireBarricade(player, slot, npc);
                    } else if (item.getId() == 4045) {
                        BarricadeManager.explodeBarricade(player, slot, npc);
                    } else if (item.getId() == 1929) {
                        BarricadeManager.extinguishBarricade(player, slot, npc);
                    }
                    break;
                case NpcID.GARGOYLE:
                    if (item.getId() != 4162 && item.getId() != 21754) {
                        player.sendMessage("Nothing interesting happens.");
                        return;
                    }
                    Gargoyle.Companion.breakGargoyle(player, npc, npc.getHitpoints(), false, player.getEquipment().contains(21742));
                    break;
                case GAMER_1012:
                    AngelicCapeGamble.Companion.gambleAngelicByItem(player, item.getId());
                    break;
                case NpcID.ROCKSLUG:
                case NpcID.ROCKSLUG_422:
                    if (item.getId() != 4161) {
                        player.sendMessage("Nothing interesting happens.");
                        return;
                    }
                    Rockslug.Companion.saltSlug(player, npc, npc.getHitpoints(), false, player.getEquipment().contains(ItemID.BRINE_SABRE));
                    break;
                case NpcID.DESERT_LIZARD:
                case DESERT_LIZARD_460:
                case DESERT_LIZARD_461:
                    if (item.getId() != ICE_COOLER) {
                        player.sendMessage("Nothing interesting happens.");
                        return;
                    }
                    DesertLizard.Companion.finishOff(player,npc,npc.getHitpoints());
                    break;
                case NpcID.PERDU:
                    if (item.getDefinition().getName().contains("(c)")) {
                        new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                .setNpcChatHead(npc.getId())
                                .setExpression(DialogueExpression.SAD_HEAD_BOW)
                                .setText("Unfortunately I can't repair this adventurer!", "Only barrows items are viable for my service.")
                                .start(player);
                        return;
                    }

                    final int costForRepair = ItemRepairUtil.getRepairCost(id) * 15;
                    int repairedItemId = -1;
                    final ItemDefinition definition = ItemDefinition.forId(id);
                    final String name = definition.getName().toLowerCase();
                    final DegradableType degradableType = DegradableType.forItem(id);

                    if (!name.startsWith("ahrim") && !name.startsWith("dharok") && !name.startsWith("verac") && !name.startsWith("torag") && !name.startsWith("karil") && !name.startsWith("guthan")) {
                        if (!name.endsWith("0")) {
                            new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                    .setNpcChatHead(npc.getId())
                                    .setExpression(DialogueExpression.SAD_HEAD_BOW)
                                    .setText("Unfortunately I can't repair this adventurer!", "Only barrows items are viable for my service.")
                                    .start(player);
                            return;
                        }
                    }

                    if (degradableType == null) {

                        if (definition != null) {


                            if (name.startsWith("ahrim") || name.startsWith("dharok") || name.startsWith("verac") || name.startsWith("torag") || name.startsWith("karil") || name.startsWith("guthan")) {
                                if (name.endsWith("0")) {
                                    repairedItemId = DegradableType.brokenBarrows.get(id);
                                }
                            }

                        } else {
                            new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                    .setNpcChatHead(npc.getId())
                                    .setExpression(DialogueExpression.SAD_HEAD_BOW)
                                    .setText("Unfortunately I can't repair this adventurer!", "Only barrows items are viable for my service.")
                                    .start(player);
                            return;
                        }

                    } else {
                        if (degradableType.name().endsWith("_FULL")) {
                            new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                    .setNpcChatHead(npc.getId())
                                    .setExpression(DialogueExpression.LAUGHING_3)
                                    .setText("What do you expect me to do with this adventurer?", "This item is already fully repaired!")
                                    .start(player);
                            return;
                        }

                        if (costForRepair <= ItemRepairUtil.MINIMUM_REPAIR_COST) {
                            new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                    .setNpcChatHead(npc.getId())
                                    .setExpression(DialogueExpression.ANNOYED)
                                    .setText("You don't have any broken barrows pieces in your inventory.")
                                    .start(player);
                            return;
                        }

                        repairedItemId = degradableType.getRepairedBarrowsVersion().getItemId();
                    }

                    if (repairedItemId == -1) {
                        player.getPacketSender().sendMessage("Nothing interesting happens.");
                        return;
                    }

                    final Inventory inventory = player.getInventory();
                    final int coinsInInventory = inventory.getAmount(ItemID.COINS);

                    if (coinsInInventory >= costForRepair) {

                        if (degradableType == null || player.getItemDegradationManager().repair(id)) {

                            final ItemDefinition brokenDefinition = ItemDefinition.forId(id);
                            final String repaireMessage = brokenDefinition == null ? "You repaired the item." : "You repaired the " + brokenDefinition.getName() + ".";

                            inventory.delete(ItemID.COINS, costForRepair);
                            inventory.replaceFirst(id, repairedItemId);
                            inventory.refreshItems();

                            new DialogueBuilder(DialogueType.ITEM_STATEMENT)
                                    .setItem(repairedItemId, 170)
                                    .setText(repaireMessage)
                                    .add(DialogueType.NPC_STATEMENT)
                                    .setNpcChatHead(npc.getId())
                                    .setExpression(DialogueExpression.HAPPY)
                                    .setText("Thank you kindly " + (player.getAppearance().isMale() ? "sir" : "lady") + ",", "it was a pleasure doing business with you!")
                                    .start(player);
                        }

                    } else {

                        new DialogueBuilder(DialogueType.NPC_STATEMENT)
                                .setNpcChatHead(npc.getId())
                                .setExpression(DialogueExpression.ANNOYED)
                                .setText("You don't have enough coins in your inventory", "to repair your armour.")
                                .start(player);

                    }
                case NpcID.PILES:
                    PilesResourceArea.createNotingDialogue2(npc.getId(), item.getId(), item.getAmount(), player);
                    break;

                case NpcID.ZAHUR:
                    if (item.getDefinition().isNoted() && HerbCleaning.INSTANCE.isGrimyHerb(item.getDefinition().unNote()))
                        HerbCleaningDialogue.Companion.getDialogue().start(player);
                    else
                        player.sendMessage("Nothing interesting happens.");
                    break;
                case NpcID.LORELAI:
                case NpcID.KAMFREENA:
                    var defender = Defender.forId(item.getId());
                    if (defender == null) {
                        player.sendMessage("Nothing interesting happens.");
                        return;
                    }
                        WarriorsGuild.showDefender(player,defender);
                    break;
                default:
                    player.sendMessage("Nothing interesting happens.");
                    break;
            }
        };
        player.setWalkToTask(new WalkToAction(player, npc, action, WalkToAction.Policy.NO_RESET_ENTITY_INTERACTION_ON_EXECUTION, WalkToAction.Policy.RECALCULATE_IF_TARGET_MOVES));

    }

    private static void itemOnPlayer(Player player, ItemOnPlayerMessage message) {

        int interfaceId = message.getInterfaceId();
        final int targetIndex = message.getIndex();
        int itemId = message.getId();
        int slot = message.getSlot();

        final Player targetPlayer = World.getPlayers().get(targetIndex);
        if (slot < 0 || slot >= player.getInventory().capacity() || targetIndex >= World.getPlayers().capacity())
            return;

        if (targetPlayer == null || targetPlayer.getHitpoints() <= 0) {
            return;
        }

        if (!player.getInventory().contains(itemId)) {
            return;
        }
        if (!targetPlayer.isActive()) {
            return;
        }
        if (targetPlayer.isInTutorial()) {
            player.sendMessage("The other player is currently busy at the moment.");
            return;
        }
        if (!player.isRegistered() || !targetPlayer.isRegistered()) {
            return;
        }
        if (targetPlayer.getStatus() == PlayerStatus.AWAY_FROM_KEYBOARD) {
            targetPlayer.getPacketSender().sendQuickChat("I'm afk bitch, leave me alone..");
            return;
        }
        if (targetPlayer.equals(player)) {
            player.getMotion().clearSteps();
            player.getPacketSender().sendMinimapFlagRemoval();
            return;
        }
        switch (itemId) {
            case BANDAGES:
                CastleWars.useBandage(player, targetPlayer, slot);
                break;

            case ENCHANTED_GEM:
                player.sendMessage("Slayer partner feature is temporarily disabled.");
                break;
            case OLD_SCHOOL_BOND:

                player.setEntityInteraction(targetPlayer);
                player.setWalkToTask(new WalkToAction<>(player, targetPlayer, 8, 0, new Executable() {
                    @Override
                    public void execute() {
                        if (player.getMotion().isFollowing(targetPlayer)) {
                            player.getMotion().followTarget(null);
                            player.setEntityInteraction(null);
                        }
                        player.getMotion().reset();

                        if (Math.abs(player.getPosition().getX() - targetPlayer.getPosition().getX()) > 20 || Math.abs(player.getPosition().getY() - targetPlayer.getPosition().getY()) > 20) {
                            player.getMotion().clearSteps();
                            player.getPacketSender().sendMinimapFlagRemoval();
                            return;
                        }
                        if (!player.getInventory().contains(OLD_SCHOOL_BOND) || player.getInventory().getAmount(OLD_SCHOOL_BOND) <= 0) {
                            return;
                        }
                        if (player.getGameMode().isSpawn()) {
                            player.sendMessage("You cannot use bonds on other players in spawn game mode.");
                            return;
                        }

                        if (player.busy()) {
                            player.getPacketSender().sendMessage("You can't do that when you're busy.", 1000);
                            return;
                        }
                        if (player.getGameMode().isSpawn()) {
                            player.sendMessage("You cannot do that in spawn game mode.");
                            return;
                        }
                        if (targetPlayer.getGameMode().isSpawn()) {
                            player.sendMessage("You cannot use that on spawn game mode players.");
                            return;
                        }

                        if (targetPlayer.busy()) {
                            String msg = "That player is currently busy at the moment.";

                            if (targetPlayer.getStatus() == PlayerStatus.TRADING) {
                                //msg = "The other player is currently in trade with someone else.";
                                msg = "The other player is busy at the moment.";
                            }

                            player.getPacketSender().sendMessage(msg);
                            return;
                        }
                        if (player.getLocalPlayers().contains(targetPlayer)) {
                            DialogueManager.start(player, 2810);
                            player.setDialogueOptions(new DialogueOptions() {
                                @Override
                                public void handleOption(Player player, int option) {
                                    switch (option) {
                                        case 1:
                                            player.setEntityInteraction(targetPlayer);
                                            targetPlayer.setPositionToFace(player.getPosition());
                                            player.setPositionToFace(targetPlayer.getPosition());
                                            if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, false)) {
                                                return;
                                            }
                                            if (!player.getInventory().contains(OLD_SCHOOL_BOND)) {
                                                return;
                                            }
                                            if (player.getInventory().getAmount(OLD_SCHOOL_BOND) <= 0) {
                                                return;
                                            }
                                            player.setEntityInteraction(targetPlayer);
                                            player.getInventory().delete(OLD_SCHOOL_BOND, 1);
                                            EntityExtKt.markTime(player, Attribute.LAST_PRAY);

                                            targetPlayer.sendMessage("You have been gifted a @dre@$50.00 member's rank bond</col> from " + player.getUsername() + "!");
                                            targetPlayer.getPacketSender().sendMessage("<img=749> @yel@You have successfully bonded the member's rank on your account! $50.00 balance was added to your account.");
                                            targetPlayer.getAttributes().numAttr(Attribute.AMOUNT_PAID, 0).setValue(50 + targetPlayer.getAttributes().numInt(Attribute.AMOUNT_PAID));
                                            targetPlayer.getAttributes().numAttr(Attribute.FIFTY_DOLLAR_BOND, 0).setValue(1 + targetPlayer.getAttributes().numInt(Attribute.FIFTY_DOLLAR_BOND));
                                            EntityExtKt.setInt(targetPlayer, Attribute.TIMES_PAID, EntityExtKt.getInt(targetPlayer, Attribute.TIMES_PAID, 0) + 1, 0);

                                            targetPlayer.getPacketSender().sendRights();
                                            targetPlayer.sendMessage("Congratulations, you're now a @dre@" + targetPlayer.getRights().toString() + "</col>.");
                                            targetPlayer.getPacketSender().sendJinglebitMusic(134, 0);
                                            if (targetPlayer.getRights() == PlayerRights.RUBY_MEMBER || targetPlayer.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 49 || targetPlayer.getAttributes().containsKey(Attribute.FREE_RUBY_MEMBER_RANK)) {
                                                AchievementManager.processFor(AchievementType.SPREAD_LOVE, targetPlayer);
                                            } else if (targetPlayer.getRights() == PlayerRights.TOPAZ_MEMBER || targetPlayer.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 99) {
                                                AchievementManager.processFor(AchievementType.SUPERIOR_SUPPORT, targetPlayer);
                                            } else if (targetPlayer.getRights() == PlayerRights.AMETHYST_MEMBER || targetPlayer.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 150) {
                                                AchievementManager.processFor(AchievementType.EXTREME_SUPPORT, targetPlayer);
                                            } else if (targetPlayer.getRights() == PlayerRights.LEGENDARY_MEMBER || targetPlayer.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 249) {
                                                AchievementManager.processFor(AchievementType.LEGENDARY_SUPPORT, targetPlayer);
                                            } else if (targetPlayer.getRights() == PlayerRights.PLATINUM_MEMBER || player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 499) {
                                                AchievementManager.processFor(AchievementType.PLATINUM_SUPPORT, targetPlayer);
                                            } else if (targetPlayer.getRights() == PlayerRights.TITANIUM_MEMBER || player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 749) {
                                                AchievementManager.processFor(AchievementType.TITANIUM_SUPPORT, targetPlayer);
                                            } else if (targetPlayer.getRights() == PlayerRights.DIAMOND_MEMBER || player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 999) {
                                                AchievementManager.processFor(AchievementType.DIAMOND_SUPPORT, targetPlayer);
                                            }
                                            PlayerUtil.broadcastMessage("<img=749> @yel@Congratulations! " + PlayerUtil.getImages(targetPlayer) + "" + targetPlayer.getUsername() + " has been gifted a $50.00 member's rank bond by " + PlayerUtil.getImages(player) + "" + player.getUsername() +"!");
                                            player.getPacketSender().sendInterfaceRemoval();
                                            break;
                                        case 2:
                                            player.getPacketSender().sendInterfaceRemoval();
                                            break;
                                    }
                                    player.setEntityInteraction(null);
                                }
                            });
                        }
                    }
                }, WalkToAction.Policy.RECALCULATE_IF_TARGET_MOVES));
                break;

            case 15828:

                player.setEntityInteraction(targetPlayer);
                player.setWalkToTask(new WalkToAction<>(player, targetPlayer, 8, 0, new Executable() {
                    @Override
                    public void execute() {
                        if (player.getMotion().isFollowing(targetPlayer)) {
                            player.getMotion().followTarget(null);
                            player.setEntityInteraction(null);
                        }
                        player.getMotion().reset();

                        if (Math.abs(player.getPosition().getX() - targetPlayer.getPosition().getX()) > 20 || Math.abs(player.getPosition().getY() - targetPlayer.getPosition().getY()) > 20) {
                            player.getMotion().clearSteps();
                            player.getPacketSender().sendMinimapFlagRemoval();
                            return;
                        }
                        if (!player.getInventory().contains(15828) || player.getInventory().getAmount(15828) <= 0) {
                            return;
                        }
                        if (player.getGameMode().isSpawn()) {
                            player.sendMessage("You cannot use bonds on other players in spawn game mode.");
                            return;
                        }

                        if (player.busy()) {
                            player.getPacketSender().sendMessage("You can't do that when you're busy.", 1000);
                            return;
                        }
                        if (player.getGameMode().isSpawn()) {
                            player.sendMessage("You cannot do that in spawn game mode.");
                            return;
                        }
                        if (targetPlayer.getGameMode().isSpawn()) {
                            player.sendMessage("You cannot use that on spawn game mode players.");
                            return;
                        }

                        if (targetPlayer.busy()) {
                            String msg = "That player is currently busy at the moment.";

                            if (targetPlayer.getStatus() == PlayerStatus.TRADING) {
                                //msg = "The other player is currently in trade with someone else.";
                                msg = "The other player is busy at the moment.";
                            }

                            player.getPacketSender().sendMessage(msg);
                            return;
                        }
                        if (player.getLocalPlayers().contains(targetPlayer)) {
                            DialogueManager.start(player, 2810);
                            player.setDialogueOptions(new DialogueOptions() {
                                @Override
                                public void handleOption(Player player, int option) {
                                    switch (option) {
                                        case 1:
                                            player.setEntityInteraction(targetPlayer);
                                            targetPlayer.setPositionToFace(player.getPosition());
                                            player.setPositionToFace(targetPlayer.getPosition());
                                            if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, false)) {
                                                return;
                                            }
                                            if (!player.getInventory().contains(15828)) {
                                                return;
                                            }
                                            if (player.getInventory().getAmount(15828) <= 0) {
                                                return;
                                            }
                                            player.setEntityInteraction(targetPlayer);
                                            player.getInventory().delete(15828, 1);
                                            EntityExtKt.markTime(player, Attribute.LAST_PRAY);

                                            targetPlayer.sendMessage("You have been gifted a @dre@$10.00 member's rank bond</col> from " + player.getUsername() + "!");
                                            targetPlayer.getPacketSender().sendMessage("<img=749> @yel@You have successfully bonded the member's rank on your account! $10.00 balance was added to your account.");
                                            targetPlayer.getAttributes().numAttr(Attribute.AMOUNT_PAID, 0).setValue(10 + targetPlayer.getAttributes().numInt(Attribute.AMOUNT_PAID));
                                            targetPlayer.getAttributes().numAttr(Attribute.TEN_DOLLAR_BOND, 0).setValue(1 + targetPlayer.getAttributes().numInt(Attribute.TEN_DOLLAR_BOND));
                                            EntityExtKt.setInt(targetPlayer, Attribute.TIMES_PAID, EntityExtKt.getInt(targetPlayer, Attribute.TIMES_PAID, 0) + 1, 0);

                                            targetPlayer.getPacketSender().sendRights();
                                            targetPlayer.sendMessage("Congratulations, you're now a @dre@" + targetPlayer.getRights().toString() + "</col>.");
                                            targetPlayer.getPacketSender().sendJinglebitMusic(134, 0);
                                            if (targetPlayer.getRights() == PlayerRights.RUBY_MEMBER || targetPlayer.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 49 || targetPlayer.getAttributes().containsKey(Attribute.FREE_RUBY_MEMBER_RANK)) {
                                                AchievementManager.processFor(AchievementType.SPREAD_LOVE, targetPlayer);
                                            } else if (targetPlayer.getRights() == PlayerRights.TOPAZ_MEMBER || targetPlayer.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 99) {
                                                AchievementManager.processFor(AchievementType.SUPERIOR_SUPPORT, targetPlayer);
                                            } else if (targetPlayer.getRights() == PlayerRights.AMETHYST_MEMBER || targetPlayer.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 150) {
                                                AchievementManager.processFor(AchievementType.EXTREME_SUPPORT, targetPlayer);
                                            } else if (targetPlayer.getRights() == PlayerRights.LEGENDARY_MEMBER || targetPlayer.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 249) {
                                                AchievementManager.processFor(AchievementType.LEGENDARY_SUPPORT, targetPlayer);
                                            } else if (targetPlayer.getRights() == PlayerRights.PLATINUM_MEMBER || player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 499) {
                                                AchievementManager.processFor(AchievementType.PLATINUM_SUPPORT, targetPlayer);
                                            } else if (targetPlayer.getRights() == PlayerRights.TITANIUM_MEMBER || player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 749) {
                                                AchievementManager.processFor(AchievementType.TITANIUM_SUPPORT, targetPlayer);
                                            } else if (targetPlayer.getRights() == PlayerRights.DIAMOND_MEMBER || player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 999) {
                                                AchievementManager.processFor(AchievementType.DIAMOND_SUPPORT, targetPlayer);
                                            }
                                            PlayerUtil.broadcastMessage("<img=749> @yel@Congratulations! " + PlayerUtil.getImages(targetPlayer) + "" + targetPlayer.getUsername() + " has been gifted a $10.00 member's rank bond by " + PlayerUtil.getImages(player) + "" + player.getUsername() +"!");
                                            player.getPacketSender().sendInterfaceRemoval();
                                            break;
                                        case 2:
                                            player.getPacketSender().sendInterfaceRemoval();
                                            break;
                                    }
                                    player.setEntityInteraction(null);
                                }
                            });
                        }
                    }
                }, WalkToAction.Policy.RECALCULATE_IF_TARGET_MOVES));
                break;
            case 15829:

                player.setEntityInteraction(targetPlayer);
                player.setWalkToTask(new WalkToAction<>(player, targetPlayer, 8, 0, new Executable() {
                    @Override
                    public void execute() {
                        if (player.getMotion().isFollowing(targetPlayer)) {
                            player.getMotion().followTarget(null);
                            player.setEntityInteraction(null);
                        }
                        player.getMotion().reset();

                        if (Math.abs(player.getPosition().getX() - targetPlayer.getPosition().getX()) > 20 || Math.abs(player.getPosition().getY() - targetPlayer.getPosition().getY()) > 20) {
                            player.getMotion().clearSteps();
                            player.getPacketSender().sendMinimapFlagRemoval();
                            return;
                        }
                        if (!player.getInventory().contains(15829) || player.getInventory().getAmount(15829) <= 0) {
                            return;
                        }
                        if (player.getGameMode().isSpawn()) {
                            player.sendMessage("You cannot use bonds on other players in spawn game mode.");
                            return;
                        }

                        if (player.busy()) {
                            player.getPacketSender().sendMessage("You can't do that when you're busy.", 1000);
                            return;
                        }
                        if (player.getGameMode().isSpawn()) {
                            player.sendMessage("You cannot do that in spawn game mode.");
                            return;
                        }
                        if (targetPlayer.getGameMode().isSpawn()) {
                            player.sendMessage("You cannot use that on spawn game mode players.");
                            return;
                        }

                        if (targetPlayer.busy()) {
                            String msg = "That player is currently busy at the moment.";

                            if (targetPlayer.getStatus() == PlayerStatus.TRADING) {
                                //msg = "The other player is currently in trade with someone else.";
                                msg = "The other player is busy at the moment.";
                            }

                            player.getPacketSender().sendMessage(msg);
                            return;
                        }
                        if (player.getLocalPlayers().contains(targetPlayer)) {
                            DialogueManager.start(player, 2810);
                            player.setDialogueOptions(new DialogueOptions() {
                                @Override
                                public void handleOption(Player player, int option) {
                                    switch (option) {
                                        case 1:
                                            player.setEntityInteraction(targetPlayer);
                                            targetPlayer.setPositionToFace(player.getPosition());
                                            player.setPositionToFace(targetPlayer.getPosition());
                                            if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, false)) {
                                                return;
                                            }
                                            if (!player.getInventory().contains(15829)) {
                                                return;
                                            }
                                            if (player.getInventory().getAmount(15829) <= 0) {
                                                return;
                                            }
                                            player.setEntityInteraction(targetPlayer);
                                            player.getInventory().delete(15829, 1);
                                            EntityExtKt.markTime(player, Attribute.LAST_PRAY);

                                            targetPlayer.sendMessage("You have been gifted a @dre@$25.00 member's rank bond</col> from " + player.getUsername() + "!");
                                            targetPlayer.getPacketSender().sendMessage("<img=749> @yel@You have successfully bonded the member's rank on your account! $25.00 balance was added to your account.");
                                            targetPlayer.getAttributes().numAttr(Attribute.AMOUNT_PAID, 0).setValue(25 + targetPlayer.getAttributes().numInt(Attribute.AMOUNT_PAID));
                                            targetPlayer.getAttributes().numAttr(Attribute.TWENTY_FIVE_DOLLAR_BOND, 0).setValue(1 + targetPlayer.getAttributes().numInt(Attribute.TWENTY_FIVE_DOLLAR_BOND));
                                            EntityExtKt.setInt(targetPlayer, Attribute.TIMES_PAID, EntityExtKt.getInt(targetPlayer, Attribute.TIMES_PAID, 0) + 1, 0);

                                            targetPlayer.getPacketSender().sendRights();
                                            targetPlayer.sendMessage("Congratulations, you're now a @dre@" + targetPlayer.getRights().toString() + "</col>.");
                                            targetPlayer.getPacketSender().sendJinglebitMusic(134, 0);
                                            if (targetPlayer.getRights() == PlayerRights.RUBY_MEMBER || targetPlayer.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 49 || targetPlayer.getAttributes().containsKey(Attribute.FREE_RUBY_MEMBER_RANK)) {
                                                AchievementManager.processFor(AchievementType.SPREAD_LOVE, targetPlayer);
                                            } else if (targetPlayer.getRights() == PlayerRights.TOPAZ_MEMBER || targetPlayer.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 99) {
                                                AchievementManager.processFor(AchievementType.SUPERIOR_SUPPORT, targetPlayer);
                                            } else if (targetPlayer.getRights() == PlayerRights.AMETHYST_MEMBER || targetPlayer.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 150) {
                                                AchievementManager.processFor(AchievementType.EXTREME_SUPPORT, targetPlayer);
                                            } else if (targetPlayer.getRights() == PlayerRights.LEGENDARY_MEMBER || targetPlayer.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 249) {
                                                AchievementManager.processFor(AchievementType.LEGENDARY_SUPPORT, targetPlayer);
                                            } else if (targetPlayer.getRights() == PlayerRights.PLATINUM_MEMBER || player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 499) {
                                                AchievementManager.processFor(AchievementType.PLATINUM_SUPPORT, targetPlayer);
                                            } else if (targetPlayer.getRights() == PlayerRights.TITANIUM_MEMBER || player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 749) {
                                                AchievementManager.processFor(AchievementType.TITANIUM_SUPPORT, targetPlayer);
                                            } else if (targetPlayer.getRights() == PlayerRights.DIAMOND_MEMBER || player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 999) {
                                                AchievementManager.processFor(AchievementType.DIAMOND_SUPPORT, targetPlayer);
                                            }
                                            PlayerUtil.broadcastMessage("<img=749> @yel@Congratulations! " + PlayerUtil.getImages(targetPlayer) + "" + targetPlayer.getUsername() + " has been gifted a $25.00 member's rank bond by " + PlayerUtil.getImages(player) + "" + player.getUsername() +"!");
                                            player.getPacketSender().sendInterfaceRemoval();
                                            break;
                                        case 2:
                                            player.getPacketSender().sendInterfaceRemoval();
                                            break;
                                    }
                                    player.setEntityInteraction(null);
                                }
                            });
                        }
                    }
                }, WalkToAction.Policy.RECALCULATE_IF_TARGET_MOVES));
                break;
            case 15830:

                player.setEntityInteraction(targetPlayer);
                player.setWalkToTask(new WalkToAction<>(player, targetPlayer, 8, 0, new Executable() {
                    @Override
                    public void execute() {
                        if (player.getMotion().isFollowing(targetPlayer)) {
                            player.getMotion().followTarget(null);
                            player.setEntityInteraction(null);
                        }
                        player.getMotion().reset();

                        if (Math.abs(player.getPosition().getX() - targetPlayer.getPosition().getX()) > 20 || Math.abs(player.getPosition().getY() - targetPlayer.getPosition().getY()) > 20) {
                            player.getMotion().clearSteps();
                            player.getPacketSender().sendMinimapFlagRemoval();
                            return;
                        }
                        if (!player.getInventory().contains(15830) || player.getInventory().getAmount(15830) <= 0) {
                            return;
                        }
                        if (player.getGameMode().isSpawn()) {
                            player.sendMessage("You cannot use bonds on other players in spawn game mode.");
                            return;
                        }

                        if (player.busy()) {
                            player.getPacketSender().sendMessage("You can't do that when you're busy.", 1000);
                            return;
                        }
                        if (player.getGameMode().isSpawn()) {
                            player.sendMessage("You cannot do that in spawn game mode.");
                            return;
                        }
                        if (targetPlayer.getGameMode().isSpawn()) {
                            player.sendMessage("You cannot use that on spawn game mode players.");
                            return;
                        }

                        if (targetPlayer.busy()) {
                            String msg = "That player is currently busy at the moment.";

                            if (targetPlayer.getStatus() == PlayerStatus.TRADING) {
                                //msg = "The other player is currently in trade with someone else.";
                                msg = "The other player is busy at the moment.";
                            }

                            player.getPacketSender().sendMessage(msg);
                            return;
                        }
                        if (player.getLocalPlayers().contains(targetPlayer)) {
                            DialogueManager.start(player, 2810);
                            player.setDialogueOptions(new DialogueOptions() {
                                @Override
                                public void handleOption(Player player, int option) {
                                    switch (option) {
                                        case 1:
                                            player.setEntityInteraction(targetPlayer);
                                            targetPlayer.setPositionToFace(player.getPosition());
                                            player.setPositionToFace(targetPlayer.getPosition());
                                            if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, false)) {
                                                return;
                                            }
                                            if (!player.getInventory().contains(15830)) {
                                                return;
                                            }
                                            if (player.getInventory().getAmount(15830) <= 0) {
                                                return;
                                            }
                                            player.setEntityInteraction(targetPlayer);
                                            player.getInventory().delete(15830, 1);
                                            EntityExtKt.markTime(player, Attribute.LAST_PRAY);

                                            targetPlayer.sendMessage("You have been gifted a @dre@$100.00 member's rank bond</col> from " + player.getUsername() + "!");
                                            targetPlayer.getPacketSender().sendMessage("<img=749> @yel@You have successfully bonded the member's rank on your account! $100.00 balance was added to your account.");
                                            targetPlayer.getAttributes().numAttr(Attribute.AMOUNT_PAID, 0).setValue(100 + targetPlayer.getAttributes().numInt(Attribute.AMOUNT_PAID));
                                            targetPlayer.getAttributes().numAttr(Attribute.HUNDRED_DOLLAR_BOND, 0).setValue(1 + targetPlayer.getAttributes().numInt(Attribute.HUNDRED_DOLLAR_BOND));
                                            EntityExtKt.setInt(targetPlayer, Attribute.TIMES_PAID, EntityExtKt.getInt(targetPlayer, Attribute.TIMES_PAID, 0) + 1, 0);

                                            targetPlayer.getPacketSender().sendRights();
                                            targetPlayer.sendMessage("Congratulations, you're now a @dre@" + targetPlayer.getRights().toString() + "</col>.");
                                            targetPlayer.getPacketSender().sendJinglebitMusic(134, 0);
                                            if (targetPlayer.getRights() == PlayerRights.RUBY_MEMBER || targetPlayer.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 49 || targetPlayer.getAttributes().containsKey(Attribute.FREE_RUBY_MEMBER_RANK)) {
                                                AchievementManager.processFor(AchievementType.SPREAD_LOVE, targetPlayer);
                                            } else if (targetPlayer.getRights() == PlayerRights.TOPAZ_MEMBER || targetPlayer.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 99) {
                                                AchievementManager.processFor(AchievementType.SUPERIOR_SUPPORT, targetPlayer);
                                            } else if (targetPlayer.getRights() == PlayerRights.AMETHYST_MEMBER || targetPlayer.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 150) {
                                                AchievementManager.processFor(AchievementType.EXTREME_SUPPORT, targetPlayer);
                                            } else if (targetPlayer.getRights() == PlayerRights.LEGENDARY_MEMBER || targetPlayer.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 249) {
                                                AchievementManager.processFor(AchievementType.LEGENDARY_SUPPORT, targetPlayer);
                                            } else if (targetPlayer.getRights() == PlayerRights.PLATINUM_MEMBER || player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 499) {
                                                AchievementManager.processFor(AchievementType.PLATINUM_SUPPORT, targetPlayer);
                                            } else if (targetPlayer.getRights() == PlayerRights.TITANIUM_MEMBER || player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 749) {
                                                AchievementManager.processFor(AchievementType.TITANIUM_SUPPORT, targetPlayer);
                                            } else if (targetPlayer.getRights() == PlayerRights.DIAMOND_MEMBER || player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 999) {
                                                AchievementManager.processFor(AchievementType.DIAMOND_SUPPORT, targetPlayer);
                                            }
                                            PlayerUtil.broadcastMessage("<img=749> @yel@Congratulations! " + PlayerUtil.getImages(targetPlayer) + "" + targetPlayer.getUsername() + " has been gifted a $100.00 member's rank bond by " + PlayerUtil.getImages(player) + "" + player.getUsername() +"!");
                                            player.getPacketSender().sendInterfaceRemoval();
                                            break;
                                        case 2:
                                            player.getPacketSender().sendInterfaceRemoval();
                                            break;
                                    }
                                    player.setEntityInteraction(null);
                                }
                            });
                        }
                    }
                }, WalkToAction.Policy.RECALCULATE_IF_TARGET_MOVES));
                break;
            case 15831:

                player.setEntityInteraction(targetPlayer);
                player.setWalkToTask(new WalkToAction<>(player, targetPlayer, 8, 0, new Executable() {
                    @Override
                    public void execute() {
                        if (player.getMotion().isFollowing(targetPlayer)) {
                            player.getMotion().followTarget(null);
                            player.setEntityInteraction(null);
                        }
                        player.getMotion().reset();

                        if (Math.abs(player.getPosition().getX() - targetPlayer.getPosition().getX()) > 20 || Math.abs(player.getPosition().getY() - targetPlayer.getPosition().getY()) > 20) {
                            player.getMotion().clearSteps();
                            player.getPacketSender().sendMinimapFlagRemoval();
                            return;
                        }
                        if (!player.getInventory().contains(15831) || player.getInventory().getAmount(15831) <= 0) {
                            return;
                        }
                        if (player.getGameMode().isSpawn()) {
                            player.sendMessage("You cannot use bonds on other players in spawn game mode.");
                            return;
                        }

                        if (player.busy()) {
                            player.getPacketSender().sendMessage("You can't do that when you're busy.", 1000);
                            return;
                        }
                        if (player.getGameMode().isSpawn()) {
                            player.sendMessage("You cannot do that in spawn game mode.");
                            return;
                        }
                        if (targetPlayer.getGameMode().isSpawn()) {
                            player.sendMessage("You cannot use that on spawn game mode players.");
                            return;
                        }

                        if (targetPlayer.busy()) {
                            String msg = "That player is currently busy at the moment.";

                            if (targetPlayer.getStatus() == PlayerStatus.TRADING) {
                                //msg = "The other player is currently in trade with someone else.";
                                msg = "The other player is busy at the moment.";
                            }

                            player.getPacketSender().sendMessage(msg);
                            return;
                        }
                        if (player.getLocalPlayers().contains(targetPlayer)) {
                            DialogueManager.start(player, 2810);
                            player.setDialogueOptions(new DialogueOptions() {
                                @Override
                                public void handleOption(Player player, int option) {
                                    switch (option) {
                                        case 1:
                                            player.setEntityInteraction(targetPlayer);
                                            targetPlayer.setPositionToFace(player.getPosition());
                                            player.setPositionToFace(targetPlayer.getPosition());
                                            if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 1, TimeUnit.SECONDS, false, false)) {
                                                return;
                                            }
                                            if (!player.getInventory().contains(15831)) {
                                                return;
                                            }
                                            if (player.getInventory().getAmount(15831) <= 0) {
                                                return;
                                            }
                                            player.setEntityInteraction(targetPlayer);
                                            player.getInventory().delete(15831, 1);
                                            EntityExtKt.markTime(player, Attribute.LAST_PRAY);

                                            targetPlayer.sendMessage("You have been gifted a @dre@$250.00 member's rank bond</col> from " + player.getUsername() + "!");
                                            targetPlayer.getPacketSender().sendMessage("<img=749> @yel@You have successfully bonded the member's rank on your account! $250.00 balance was added to your account.");
                                            targetPlayer.getAttributes().numAttr(Attribute.AMOUNT_PAID, 0).setValue(250 + targetPlayer.getAttributes().numInt(Attribute.AMOUNT_PAID));
                                            targetPlayer.getAttributes().numAttr(Attribute.TWO_HUNDRED_FIFTY_DOLLAR_BOND, 0).setValue(1 + targetPlayer.getAttributes().numInt(Attribute.TWO_HUNDRED_FIFTY_DOLLAR_BOND));
                                            EntityExtKt.setInt(targetPlayer, Attribute.TIMES_PAID, EntityExtKt.getInt(targetPlayer, Attribute.TIMES_PAID, 0) + 1, 0);

                                            targetPlayer.getPacketSender().sendRights();
                                            targetPlayer.sendMessage("Congratulations, you're now a @dre@" + targetPlayer.getRights().toString() + "</col>.");
                                            targetPlayer.getPacketSender().sendJinglebitMusic(134, 0);
                                            if (targetPlayer.getRights() == PlayerRights.RUBY_MEMBER || targetPlayer.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 49 || targetPlayer.getAttributes().containsKey(Attribute.FREE_RUBY_MEMBER_RANK)) {
                                                AchievementManager.processFor(AchievementType.SPREAD_LOVE, targetPlayer);
                                            } else if (targetPlayer.getRights() == PlayerRights.TOPAZ_MEMBER || targetPlayer.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 99) {
                                                AchievementManager.processFor(AchievementType.SUPERIOR_SUPPORT, targetPlayer);
                                            } else if (targetPlayer.getRights() == PlayerRights.AMETHYST_MEMBER || targetPlayer.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 150) {
                                                AchievementManager.processFor(AchievementType.EXTREME_SUPPORT, targetPlayer);
                                            } else if (targetPlayer.getRights() == PlayerRights.LEGENDARY_MEMBER || targetPlayer.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 249) {
                                                AchievementManager.processFor(AchievementType.LEGENDARY_SUPPORT, targetPlayer);
                                            } else if (targetPlayer.getRights() == PlayerRights.PLATINUM_MEMBER || player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 499) {
                                                AchievementManager.processFor(AchievementType.PLATINUM_SUPPORT, targetPlayer);
                                            } else if (targetPlayer.getRights() == PlayerRights.TITANIUM_MEMBER || player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 749) {
                                                AchievementManager.processFor(AchievementType.TITANIUM_SUPPORT, targetPlayer);
                                            } else if (targetPlayer.getRights() == PlayerRights.DIAMOND_MEMBER || player.getAttributes().numInt(Attribute.AMOUNT_PAID) >= 999) {
                                                AchievementManager.processFor(AchievementType.DIAMOND_SUPPORT, targetPlayer);
                                            }
                                            PlayerUtil.broadcastMessage("<img=749> @yel@Congratulations! " + PlayerUtil.getImages(targetPlayer) + "" + targetPlayer.getUsername() + " has been gifted a $250.00 member's rank bond by " + PlayerUtil.getImages(player) + "" + player.getUsername() +"!");
                                            player.getPacketSender().sendInterfaceRemoval();
                                            break;
                                        case 2:
                                            player.getPacketSender().sendInterfaceRemoval();
                                            break;
                                    }
                                    player.setEntityInteraction(null);
                                }
                            });
                        }
                    }
                }, WalkToAction.Policy.RECALCULATE_IF_TARGET_MOVES));
                break;

            case CHRISTMAS_CRACKER: // Christmas Cracker
                player.setEntityInteraction(targetPlayer);
                player.setWalkToTask(new WalkToAction(player, targetPlayer, () -> {

                    if (!targetPlayer.getMotion().isMoving())
                        targetPlayer.setPositionToFace(player.getPosition());


                    if (player.getMotion().isFollowing(targetPlayer)) {
                        player.getMotion().followTarget(null);
                        player.setEntityInteraction(null);
                    }

                    player.getMotion().reset();

                    if (Math.abs(player.getPosition().getX() - targetPlayer.getPosition().getX()) > 20 || Math.abs(player.getPosition().getY() - targetPlayer.getPosition().getY()) > 20) {
                        player.getMotion().clearSteps();
                        player.getPacketSender().sendMinimapFlagRemoval();
                        return;
                    }

                    if (player.busy()) {
                        player.getPacketSender().sendMessage("You cannot do that right now.");
                        return;
                    }

                    if (targetPlayer.busy()) {
                        String msg = "That player is currently busy at the moment.";
                        if (targetPlayer.getStatus() == PlayerStatus.TRADING) {
                            //msg = "The other player is currently in trade with someone else.";
                            msg = "The other player is busy at the moment.";
                        }
                        player.getPacketSender().sendMessage(msg);
                        return;
                    }
                    if (player.getLocalPlayers().contains(targetPlayer)) {
                        if (targetPlayer.getInventory().countFreeSlots() < 1) {
                            player.sendMessage("The player's inventory is currently full.");
                            return;
                        }
                        if (!player.getInventory().contains(CHRISTMAS_CRACKER) || player.getInventory().getAmount(CHRISTMAS_CRACKER) <= 0) {
                            return;
                        }
                        player.performGraphic(new Graphic(176, GraphicHeight.HIGH));
                        player.performAnimation(new Animation(451));
                        player.setEntityInteraction(targetPlayer);
                        player.sendMessage("You pull the Christmas Cracker...");
                        targetPlayer.sendMessage("You pull the Christmas Cracker...");
                        int partyHat = (1038 + Misc.random(5) * 2);
                        player.getInventory().delete(CHRISTMAS_CRACKER, 1);
                        TaskManager.submit(new Task(2) {
                            @Override
                            public void execute() {
                                if (Misc.random(3) == 1) {
                                    targetPlayer.say("Yay!! I've got the Cracker!");
                                    targetPlayer.getInventory().add(partyHat, 1);
                                } else {
                                    player.say("Yay!! I've got the Cracker!");
                                    player.getInventory().add(partyHat, 1);
                                }
                                player.setEntityInteraction(null);
                                stop();
                            }
                        });
                    }
                }, WalkToAction.Policy.RECALCULATE_IF_TARGET_MOVES));
                break;
            case ROTTEN_TOMATO: // Rotten tomato
                player.setEntityInteraction(targetPlayer);
                player.setWalkToTask(new WalkToAction<>(player, targetPlayer, 8, 0, () -> {
                    if (player.getMotion().isFollowing(targetPlayer)) {
                        player.getMotion().followTarget(null);
                        player.setEntityInteraction(null);
                    }
                    player.getMotion().reset();

                    if (Math.abs(player.getPosition().getX() - targetPlayer.getPosition().getX()) > 20 || Math.abs(player.getPosition().getY() - targetPlayer.getPosition().getY()) > 20) {
                        player.getMotion().clearSteps();
                        player.getPacketSender().sendMinimapFlagRemoval();
                        return;
                    }

                    if (player.busy()) {
                        player.getPacketSender().sendMessage("You cannot do that right now.");
                        return;
                    }

                    if (targetPlayer.busy()) {
                        String msg = "That player is currently busy at the moment.";

                        if (targetPlayer.getStatus() == PlayerStatus.TRADING) {
                            //msg = "The other player is currently in trade with someone else.";
                            msg = "The other player is busy at the moment.";
                        }

                        player.getPacketSender().sendMessage(msg);
                        return;
                    }
                    if (player.getLocalPlayers().contains(targetPlayer)) {
                        final int rottenTomatos = player.getInventory().getAmount(ROTTEN_TOMATO);

                        if (rottenTomatos <= 0)
                            return;
                        player.getInventory().delete(ROTTEN_TOMATO, 1);
                        player.setEntityInteraction(targetPlayer);
                        player.BLOCK_ALL_BUT_TALKING = true;
                        player.performAnimation(new Animation(385));
                        player.sendMessage("You throw the rotten tomato on " + targetPlayer.getUsername() + "!");
                        final ProjectileTemplate rottenTomatoProjectileTemplate = ProjectileTemplate.builder(29).setCurve(12).setStartHeight(43)
                                .setEndHeight(0)
                                .setSpeed(8)
                                .setDelay(35).build();
                        final Projectile rottenTomatoProjectile = new Projectile(player, targetPlayer, rottenTomatoProjectileTemplate);
                        rottenTomatoProjectile.sendProjectile();
                        rottenTomatoProjectile.onArrival(() -> {
                            player.setEntityInteraction(null);
                            player.BLOCK_ALL_BUT_TALKING = false;
                            targetPlayer.performGraphic(new Graphic(31));
                            targetPlayer.sendMessage("" + PlayerUtil.getImages(player) + "" + player.getUsername() + " threw a rotten tomato at you!");
                        });
                    }
                }, WalkToAction.Policy.EXECUTE_ON_LINE_OF_SIGHT, WalkToAction.Policy.RECALCULATE_IF_TARGET_MOVES));
                break;
            default:
                player.setEntityInteraction(targetPlayer);
                player.setWalkToTask(new WalkToAction(player, targetPlayer, () -> {
                    player.setPositionToFace(targetPlayer.getPosition());
                    if (player.getMotion().isFollowing(targetPlayer)) {
                        player.getMotion().followTarget(null);
                        player.setEntityInteraction(null);
                    }
                    player.getMotion().reset();
                    if (Math.abs(player.getPosition().getX() - targetPlayer.getPosition().getX()) > 20 || Math.abs(player.getPosition().getY() - targetPlayer.getPosition().getY()) > 20) {
                        player.getMotion().clearSteps();
                        player.getPacketSender().sendMinimapFlagRemoval();
                        return;
                    }
                    player.getPacketSender().sendMessage("Nothing interesting happens.", 1000);
                }, WalkToAction.Policy.RECALCULATE_IF_TARGET_MOVES));
                break;
        }
    }
}