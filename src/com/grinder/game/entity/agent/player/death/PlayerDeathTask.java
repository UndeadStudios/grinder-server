package com.grinder.game.entity.agent.player.death;

import com.grinder.game.GameConstants;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.dueling.DuelController;
import com.grinder.game.content.item.charging.RevenantEtherChargeable;
import com.grinder.game.content.item.charging.impl.*;
import com.grinder.game.content.minigame.pestcontrol.PestControl;
import com.grinder.game.content.minigame.pestcontrol.PestControlInstance;
import com.grinder.game.content.miscellaneous.presets.Presetables;
import com.grinder.game.content.pvp.bountyhunter.BountyHunterManager;
import com.grinder.game.content.pvp.bountyhunter.reward.Emblem;
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.definition.ItemValueType;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterfaces;
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.Chinchompas;
import com.grinder.game.entity.agent.combat.event.impl.KilledTargetEvent;
import com.grinder.game.entity.agent.combat.misc.CombatPrayer;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.agent.player.death.ItemsKeptOnDeathGenerator.Result;
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses;
import com.grinder.game.entity.grounditem.ItemOnGroundManager;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.EffectTimer;
import com.grinder.game.model.Position;
import com.grinder.game.model.areas.Area;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.areas.InstancedArea;
import com.grinder.game.model.areas.impl.DuelArenaArea;
import com.grinder.game.model.areas.instanced.CerberusArea;
import com.grinder.game.model.areas.instanced.HydraArea;
import com.grinder.game.model.areas.instanced.VorkathArea;
import com.grinder.game.model.areas.instanced.ZulrahShrine;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.item.AttributeKey;
import com.grinder.game.model.item.BrokenItems;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.ItemUtil;
import com.grinder.game.model.item.container.bank.BankUtil;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.net.packet.impl.EquipPacketListener;
import com.grinder.util.ItemID;
import com.grinder.util.Logging;
import com.grinder.util.Misc;
import com.grinder.util.timing.TimerKey;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.grinder.util.ItemID.*;

/**
 * Represents a player's death task, through which the process of dying is
 * handled, the animation, dropping items, etc.
 *
 * @author Professor Oak
 * @author Stan van der Bend (cleaned this up)
 */
public class PlayerDeathTask extends Task {

    private final Player player;
    private final Agent killer;

    private final Area playerArea;
    private final Area killerArea;
    private boolean instanced;
    private boolean unsafe = true;
    private int ticks = 2;

    /**
     * The PlayerDeathTask constructor.
     *
     * @param player The player setting off the task.
     */
    public PlayerDeathTask(Player player) {
        super(2, player, false);
        this.player = player;
        killer = player.getCombat().findKiller(true).orElse(null);
        killerArea = Optional.ofNullable(killer).map(Agent::getArea).orElse(null);
        playerArea = player.getArea();
        instanced = playerArea instanceof InstancedArea;
        if (killerArea instanceof DuelArenaArea)
            killer.ifPlayer(Player::resetAttributes);
    }

    @Override
    public void execute() {

        if (player == null) {
            stop();
            return;
        }

        try {
            switch (ticks) {
                case 2:

                    if (PrayerHandler.isActivated(player, PrayerHandler.PROTECT_ITEM))
                        PlayerExtKt.progressAchievement(player, AchievementType.ON_THE_ALERT);

                    PlayerExtKt.removeInterfaces(player);
                    PlayerExtKt.resetInteractions(player, true, false);
                    player.getMotion().update(MovementStatus.DISABLED);
                    player.getCombat().reset(true);

                    // if (player.getMinigame() == null)
                    //     player.getPacketSender().sendMusic(146, 4, 350);

                    player.sendMessage("Oh dear, you are dead!");
                    TaskManager.submit(player, 3, () -> {
                        player.getPacketSender().sendJinglebitMusic(90, 0);
                    });

                    player.setUntargetable(true);

                    player.performAnimation(new Animation(836, 50));

                    if (!player.getGameMode().isSpawn()) // DONT LOG FOR SPAWN GAME MODE DEATH
                        Logging.log("playerDeath", "[playerDeath]: " + player.getUsername() + " died on coords " + player.getPosition());

                    if (killer != null) {
                        if (PrayerHandler.isActivated(player, PrayerHandler.RETRIBUTION))
                            CombatPrayer.handleRetribution(player, killer);
                    } else {
                        final DuelController duelController = player.getDueling();
                        if (duelController.inDuel()) {
                            final Player opponent = player.getDueling().getInteract();
                            if (opponent != null && opponent.isDying()) {
                                // when we die but are not killed, e.g. by poison, but opponent is dying first
                                stop();
                                return;
                            }
                        }
                    }
                    break;
                case 0:

                    if (killer != null)
                        killer.getCombat().submit(new KilledTargetEvent(killer, player));

                    final Optional<Player> optionalKiller = Optional.ofNullable(killer).flatMap(Agent::getAsOptionalPlayer);

                    if (killerArea != null) {
                        optionalKiller.ifPresent(p -> {
                            killerArea.defeated(p, player);
                            if (BountyHunterManager.findTargetFor(player).equals(player.getUsername())) { // Remove bounty hunter target for both pairs
                                BountyHunterManager.disassemblePairIfPresent(player);
                                BountyHunterManager.disassemblePairIfPresent(p);
                                player.getBountyTeleportTimer().stop();
                                p.getBountyTeleportTimer().stop();
                            }
                        });
                    }


                    if (playerArea != null)
                        unsafe = playerArea.dropItemsOnDeath(player, optionalKiller);

                    boolean pestControl = false;
                    PestControlInstance pestControlInstance = PestControl.PEST_CONTROL_INSTANCE;
                    if (pestControlInstance != null) {
                        if (pestControlInstance.hasPlayer(player)) {
                            unsafe = false;
                            pestControl = true;
                        }
                    }

                    if (player.instance != null) {
                        unsafe = !player.instance.isSafe(player);
                    }

                    if (unsafe) {
                        if (unsafe && player.getRights().loseItemsOnDeath() && !isJailedandInJail(player)/* && !player.getUsername().equals("Mod Hellmage")*/ && !player.getGameMode().isSpawn()) {

                            final boolean killedByIronman = optionalKiller.filter(p -> p.getGameMode().isAnyIronman()).isPresent();
                            final Position dropPosition = getDropPosition(player.getPosition().clone());

                            final ItemsKeptOnDeathGenerator generator = new ItemsKeptOnDeathGenerator(player, true);
                            final Result result = generator.generate();
                            final List<Item> itemsKept = result.getKeep();
                            final List<Item> itemsBroken = result.getBroken();
                            final List<Item> itemsDropped = result.getDropped();
                            final List<Item> itemsLost = result.getLost();

                            if (killer != null)
                                generator.getPlayerConsumer().accept(player);

                            // For items that are to be broken on death
                            for (final Item brokenItem : itemsBroken) {

                                final BrokenItems type = BrokenItems.get(brokenItem.getId());

                                if (killer != null) { // If the killer is a player they get rewarded for killing players with items that CAN break and that is not already broken.
                                    killer.ifPlayer(p -> {
                                        int bloodMoneyAmount = 0;
                                        // If wilderness level below 20 killer gets 50% of the repair value of the item, however if its above the broken item vanishes and the killer gets 75% of its repair value
                                        if (p.getWildernessLevel() > 0 && p.getWildernessLevel() < 20) {
                                            bloodMoneyAmount += type.getBloodMoneyValue() * 0.50;
                                        } else {
                                            bloodMoneyAmount += type.getBloodMoneyValue() * 0.75;
                                        }

                                        if (bloodMoneyAmount > 0) {
                                            if (!killedByIronman) {
                                                final Item bloodMoneyItem = new Item(BLOOD_MONEY, bloodMoneyAmount);
                                                ItemOnGroundManager.register(p, bloodMoneyItem, dropPosition.clone());
                                            }

                                        }
                                    });
                                }

                                brokenItem.setId(type.getBrokenItem());

                                if (player.getWildernessLevel() < 20) {
                                    player.sendMessage("Your " + ItemDefinition.definitions.get(type.getOriginalItem()).getName() + " has been broken. You can fix it by talking to Perdu.");
                                    player.getInventory().add(brokenItem);
                                } else {
                                    player.sendMessage("Your " + ItemDefinition.definitions.get(type.getOriginalItem()).getName() + " vanishes into dust for dying above level 20 Wilderness.");

                                    // Dying above level 20 to PVM
                                    if (killer != null) { // You only get BM if you died in PVM above 20 wilderness to PVM
                                        if (!killer.isPlayer()) {
                                            int bloodMoneyAmount = 0;
                                            bloodMoneyAmount += type.getBloodMoneyValue();
                                            if (bloodMoneyAmount > 0) {
                                                player.getInventory().add(new Item(BLOOD_MONEY, bloodMoneyAmount));
                                            }
                                        }
                                    }
                                }
                            } // End of items to be broken on death

                            // Items that are kept on death and are protected
                            for (final Item keptItem : itemsKept) {
                                if (Chinchompas.INSTANCE.isAnyChin(keptItem)) {
                                    player.sendMessage("Your chins have managed to escape and bound away.");
                                    player.getPacketSender().sendSound(Sounds.CHIN_DROP);
                                    continue;
                                }


                                if (keptItem.getDefinition().getName().contains("(broken)")) {
                                    continue;
                                }


                                // Revenant items even if kept on death they remain in your inventory and all charges are dropped even if protected.
                                // The code in ChargeableItem handling works perfect for when it is NOT protected on death so this extra is only for when you die while keeping items (not lost on death).
                                if (keptItem.getId() == 22550 && CrawsBow.INSTANCE.getCharges(keptItem) > 0) {
                                    itemsDropped.add(new Item(REVENANT_ETHER, CrawsBow.INSTANCE.getCharges(keptItem)));
                                    keptItem.setId(-1); // To remove attributes
                                    player.getInventory().add(new Item(22547));
                                }
                                if (keptItem.getId() == 22545 && ViggorasChainmace.INSTANCE.getCharges(keptItem) > 0) {
                                    itemsDropped.add(new Item(REVENANT_ETHER, ViggorasChainmace.INSTANCE.getCharges(keptItem)));
                                    keptItem.setId(-1); // To remove attributes
                                    player.getInventory().add(new Item(22542));
                                }
                                if (keptItem.getId() == 22555 && ThammaronsSceptre.INSTANCE.getCharges(keptItem) > 0) {
                                    itemsDropped.add(new Item(REVENANT_ETHER, ThammaronsSceptre.INSTANCE.getCharges(keptItem)));
                                    keptItem.setId(-1); // To remove attributes
                                    player.getInventory().add(new Item(22552));
                                }
                                if (keptItem.getId() == 21816 && EtherBracelet.INSTANCE.getCharges(keptItem) > 0) {
                                    itemsDropped.add(new Item(REVENANT_ETHER, EtherBracelet.INSTANCE.getCharges(keptItem)));
                                    keptItem.setId(-1); // To remove attributes
                                    player.getInventory().add(new Item(21817));
                                }

                                if (!player.getInventory().canHold(keptItem))
                                    BankUtil.addToBank(player, keptItem);
                                else
                                    player.getInventory().add(keptItem);
                            } // End of items kept on death (not lost)

                            itemsDropped.add(new Item(BONES));

                            List<Item> addonItems = new ArrayList<>();

                            // Items to be dropped (not protected on death)
                            for (final Item droppedItem : itemsDropped) {
                                PlayerDeathUtil.logItemIfValuable(player, droppedItem);

                                if (droppedItem != null && Chinchompas.INSTANCE.isAnyChin(droppedItem)) {
                                    if (droppedItem != null && Chinchompas.INSTANCE.isAnyChin(droppedItem)) {
                                        player.sendMessage("Your chins bound away after being on the ground.");
                                        player.getPacketSender().sendSound(Sounds.CHIN_DROP);
                                        continue;
                                    }
                                }

                                // Tentacle whip to drop as kraken on death drop example below (When not protected)
                                switch (droppedItem.getId()) {

                                    case SEERS_RING_I_:
                                        droppedItem.setId(SEERS_RING);
                                        break;
                                    case ARCHERS_RING_I_:
                                        droppedItem.setId(ARCHERS_RING);
                                        break;
                                    case BERSERKER_RING_I_:
                                        droppedItem.setId(BERSERKER_RING);
                                        break;
                                    case WARRIOR_RING_I_:
                                        droppedItem.setId(WARRIOR_RING);
                                        break;
                                    case TYRANNICAL_RING_I_:
                                        droppedItem.setId(TYRANNICAL_RING);
                                        break;
                                    case TREASONOUS_RING_I_:
                                        droppedItem.setId(TREASONOUS_RING);
                                        break;
                                    case RING_OF_SUFFERING_I_:
                                        droppedItem.setId(RING_OF_SUFFERING);
                                        break;
                                    case 22249: // NECKLACE_OF_ANGUISH_OR
                                        droppedItem.setId(NECKLACE_OF_ANGUISH);
                                        break;
                                    case RING_OF_THE_GODS_I_:
                                        droppedItem.setId(RING_OF_THE_GODS);
                                        break;
                                    case SALVE_AMULET_I_:
                                        droppedItem.setId(SALVE_AMULET);
                                        break;
                                    case SALVE_AMULET_EI_:
                                        droppedItem.setId(SALVE_AMULET_E_);
                                        break;
                                    case SLAYER_HELMET:
                                    case SLAYER_HELMET_I_:
                                    case BLACK_SLAYER_HELMET:
                                    case BLACK_SLAYER_HELMET_I_:
                                    case GREEN_SLAYER_HELMET:
                                    case GREEN_SLAYER_HELMET_I_:
                                    case RED_SLAYER_HELMET:
                                    case RED_SLAYER_HELMET_I_:
                                    case PURPLE_SLAYER_HELMET:
                                    case PURPLE_SLAYER_HELMET_I_:
                                    case 21888: // Turquise slayer helmet
                                    case 21890: // Turquise slayer helmet (i)
                                    case 23073: // Hydra slayer helmet
                                    case 23075: // Hydra slayer helmet (i)
                                    case 15910: // Maranami's Custom slayer helmet
                                    case ItemID.TWISTED_SLAYER_HELMET:
                                    case ItemID.TWISTED_SLAYER_HELMET_I:
                                    case ItemID.TZTOK_SLAYER_HELMET:
                                    case ItemID.TZTOK_SLAYER_HELMET_I:
                                    case ItemID.VAMPYRIC_SLAYER_HELMET:
                                    case ItemID.VAMPYRIC_SLAYER_HELMET_I:
                                    case ItemID.TZKAL_SLAYER_HELMET:
                                    case ItemID.TZKAL_SLAYER_HELMET_I:
                                        droppedItem.setId(BLACK_MASK);
                                        break;
                                    case MYSTERY_BOX:
                                        droppedItem.setId(-1);
                                        player.sendMessage("Your Mystery box vanishes into dust as it touches the ground.");
                                        break;
                                    case BLACK_MASK_1__I_:
                                        droppedItem.setId(BLACK_MASK_1_);
                                        break;
                                    case BLACK_MASK_2__I_:
                                        droppedItem.setId(BLACK_MASK_2_);
                                        break;
                                    case BLACK_MASK_3__I_:
                                        droppedItem.setId(BLACK_MASK_3_);
                                        break;
                                    case BLACK_MASK_4__I_:
                                        droppedItem.setId(BLACK_MASK_4_);
                                        break;
                                    case BLACK_MASK_5__I_:
                                        droppedItem.setId(BLACK_MASK_5_);
                                        break;
                                    case BLACK_MASK_6__I_:
                                        droppedItem.setId(BLACK_MASK_6_);
                                        break;
                                    case BLACK_MASK_7__I_:
                                        droppedItem.setId(BLACK_MASK_7_);
                                        break;
                                    case BLACK_MASK_8__I_:
                                        droppedItem.setId(BLACK_MASK_8_);
                                        break;
                                    case BLACK_MASK_9__I_:
                                        droppedItem.setId(BLACK_MASK_9_);
                                        break;
                                    case BLACK_MASK_10__I_:
                                        droppedItem.setId(BLACK_MASK_10_);
                                        break;
                                    /*case TRIDENT_OF_THE_SEAS:
                                    case 22288:  // Trident of seas (e)
                                        droppedItem.setId(UNCHARGED_TRIDENT);
                                        break;
                                    case TRIDENT_OF_THE_SWAMP:
                                        int scalesToDrop = 0;
                                        if (droppedItem.hasAttributes()) {
                                            scalesToDrop = droppedItem.getAsAttributable().getAttribute(new AttributeKey("trident-of-swamp-charges"));
                                        }
                                        droppedItem.setId(UNCHARGED_TOXIC_TRIDENT);
                                        if (scalesToDrop > 0)
                                            addonItems.add(new Item(ZULRAHS_SCALES, scalesToDrop));
                                        break;
                                    case 22292: // Toxic trident of swamp (e)
                                        int scalesToDrop_E = 0;
                                        if (droppedItem.hasAttributes()) {
                                            scalesToDrop_E = droppedItem.getAsAttributable().getAttribute(new AttributeKey("trident-of-swamp-charges"));
                                        }
                                        droppedItem.setId(22294); // Uncharged toxic trident (e)
                                        if (scalesToDrop_E > 0)
                                        addonItems.add(new Item(ZULRAHS_SCALES, scalesToDrop_E));
                                        break;
                                    case TOXIC_STAFF_OF_THE_DEAD:
                                        int toxicStaffScales = 0;
                                        if (droppedItem.hasAttributes()) {
                                            toxicStaffScales = droppedItem.getAsAttributable().getAttribute(new AttributeKey("toxic-staff-of-dead-charges"));
                                        }
                                        droppedItem.setId(TOXIC_STAFF_UNCHARGED_);
                                        if (toxicStaffScales > 0)
                                            addonItems.add(new Item(ZULRAHS_SCALES, toxicStaffScales));
                                        break;*/
                                    case GRANITE_MAUL_3:
                                    case 24225: // Granite maul
                                    case 24227: // Granite maul
                                        droppedItem.setId(GRANITE_MAUL);
                                        addonItems.add(new Item(COINS, 75_000)); // Add g maul
                                        break;
                                    case 23083: // Brimstone key on death to the killer gets cash reward instead
                                        addonItems.add(new Item(COINS, 50_000));
                                        break;
                                    case TORVA_FULL_HELM:
                                        droppedItem.setId(TORVA_FULL_HELM_DAMAGED);
                                        break;
                                    case TORVA_PLATEBODY:
                                        droppedItem.setId(TORVA_PLATEBODY_DAMAGED);
                                        break;
                                    case TORVA_PLATELEGS:
                                        droppedItem.setId(TORVA_PLATELEGS_DAMAGED);
                                        break;
                                    case 15877: // Pernix
                                        droppedItem.setId(15892);
                                        break;
                                    case 15879:
                                        droppedItem.setId(15893);
                                        break;
                                    case 15881:
                                        droppedItem.setId(15894);
                                        break;
                                    case 15883: // Virtus
                                        droppedItem.setId(15895);
                                        break;
                                    case 15885:
                                        droppedItem.setId(15896);
                                        break;
                                    case 15887:
                                        droppedItem.setId(15897);
                                        break;
                                    default:
                                        droppedItem.setId(ItemOnGroundManager.changeItem(droppedItem.getId())); // Barrows handling on drop (Switches the id from untradeable to tradeable)
                                        break;
                                }

                                final Player dropRecipient = killedByIronman
                                        ? player
                                        : optionalKiller.orElse(player);


                                if (droppedItem.getDefinition().isTradeable()) {
                                    ItemOnGroundManager.register(dropRecipient, droppedItem, dropPosition.clone());
                                } else {
                                    // Handle untradeable items on death
                                    // If you die with untradeable items above level 20 and it was not protected, you will lose it.
                                    if (player.getWildernessLevel() < 20) {
                                        if (droppedItem.getDefinition().getName().contains("(broken)")) { // Broken items are ALWAYS lost on death
                                            continue;
                                        }

                                        player.sendMessage("You have 10 minutes to pick up your " + droppedItem.getDefinition().getName() + " before it completely vanishes!");
                                        ItemOnGroundManager.registerNonGlobalOnDeath(player, droppedItem, dropPosition.clone());
                                    } else {
                                        /*int dropItemValue = (int) (droppedItem.getValue(ItemValueType.PRICE_CHECKER) / 10);
                                        if (killer != null) { // Untradeable items drop fraction of their price if lost on death above level 20 wilderness
                                            if (killer.isPlayer()) {
                                                itemsDropped.add(new Item(COINS, dropItemValue));
                                                }
                                        } else {
                                        player.getInventory().add(new Item(COINS, dropItemValue));
                                        }*/
                                        player.sendMessage("Your " + droppedItem.getDefinition().getName() + " vanishes into dust for dying above level 20 Wilderness.");
                                    }
                                }
                            }


                            // Handle addonItems items drop such as when you die with orante g maul it will drop it as a gmaul + coins so the coins here are extra drop.
                            for (final Item addonItemsDrop : addonItems) {
                                ItemOnGroundManager.register(killedByIronman ? player : optionalKiller.orElse(player), addonItemsDrop, dropPosition.clone());
                            }

                            // For items that are ALWAYS lost on death #PlayerDeathUtil.loseItem
                            for (final Item lostItem : itemsLost) {

                                // Chins for non-wilderness handling
                                PlayerDeathUtil.logItemIfValuable(player, lostItem);

                                if (lostItem != null && Chinchompas.INSTANCE.isAnyChin(lostItem)) {
                                    player.sendMessage("Your chins bound away after being on the ground.");
                                    player.getPacketSender().sendSound(Sounds.CHIN_DROP);
                                    continue;
                                }

                                if (PlayerDeathUtil.loseItem(lostItem) && !lostItem.getDefinition().getName().contains("(broken)")) {
                                    player.sendMessage("Your " + lostItem.getDefinition().getName() + " vanishes into dust after being on the ground.");
                                }

                                // Handle chins to killer (wilderness)
                                if (player.getArea() != null) {
                                    if (Chinchompas.INSTANCE.isDropable(lostItem, playerArea)) {
                                        final Player p = killedByIronman ? player : optionalKiller.orElse(player);
                                        ItemOnGroundManager.register(p, lostItem, dropPosition.clone());
                                        continue;
                                    }

                                }
                            }
                        } // If died unsafe end of code
                    }
                    if (player.getGameMode().isSpawn()) {
                        final ItemsKeptOnDeathGenerator generator = new ItemsKeptOnDeathGenerator(player, true);
                        final Result result = generator.generate();
                        final List<Item> itemsKept = result.getKeep();

                        player.getInventory().resetItems().refreshItems();
                        player.getEquipment().resetItems().refreshItems();
                        for (final Item keptItem : itemsKept) {
                            if (Chinchompas.INSTANCE.isAnyChin(keptItem)) {
                                player.sendMessage("Your chins have managed to escape and bound away.");
                                player.getPacketSender().sendSound(Sounds.CHIN_DROP);
                                continue;
                            }


                            if (keptItem.getDefinition().getName().contains("(broken)")) {
                                continue;
                            }


                            // Revenant items even if kept on death they remain in your inventory and all charges are dropped even if protected.
                            // The code in ChargeableItem handling works perfect for when it is NOT protected on death so this extra is only for when you die while keeping items (not lost on death).
                            if (keptItem.getId() == 22550 && CrawsBow.INSTANCE.getCharges(keptItem) > 0) {
                                keptItem.setId(-1); // To remove attributes
                                player.getInventory().add(new Item(22547));
                            }
                            if (keptItem.getId() == 22545 && ViggorasChainmace.INSTANCE.getCharges(keptItem) > 0) {
                                keptItem.setId(-1); // To remove attributes
                                player.getInventory().add(new Item(22542));
                            }
                            if (keptItem.getId() == 22555 && ThammaronsSceptre.INSTANCE.getCharges(keptItem) > 0) {
                                keptItem.setId(-1); // To remove attributes
                                player.getInventory().add(new Item(22552));
                            }
                            if (keptItem.getId() == 21816 && EtherBracelet.INSTANCE.getCharges(keptItem) > 0) {
                                keptItem.setId(-1); // To remove attributes
                                player.getInventory().add(new Item(21817));
                            }

                            if (!player.getInventory().canHold(keptItem))
                                BankUtil.addToBank(player, keptItem);
                            else
                                player.getInventory().add(keptItem);
                        } // End of items kept on death (not lost)
                    }

                    player.resetAttributes();
                    player.getInventory().refreshItems();
                    player.getEquipment().refreshItems();
                    player.getRunePouch().refreshItems();
                    player.getAttributes().remove(Attribute.IMBUED_HEART_TIMER);

                    optionalKiller.ifPresent(p -> {
                        if (player.getCurrentClanChat() != null && p.getCurrentClanChat() != null) {
                            if (player.getCurrentClanChat().equals(p.getCurrentClanChat())) {
                                AchievementManager.processFor(AchievementType.BETRAYED, player);
                            }
                        }
                    });

                    if (AreaManager.inWilderness(player))
                        AchievementManager.processFor(AchievementType.BORN_TO_DIE, player);

                    EquipPacketListener.resetWeapon(player);
                    WeaponInterfaces.INSTANCE.assign(player);
                    EquipmentBonuses.update(player);

                    final boolean handledDeath = playerArea != null && playerArea.handleDeath(player, optionalKiller);

                    if (player.getGameMode().isHardcore()) {
                        if (playerArea == null || !playerArea.isSafeForHardcore()
                                && !(player.getPosition().getX() > 2668 && player.getPosition().getX() <= 2644 && player.getPosition().getY() >= 2644 && player.getPosition().getY() <= 2670)
                                && !player.getPosition().equals(new Position(2657, 2639, 0))
                                && !player.getPosition().equals(new Position(2657, 2638, 0))
                                && !player.getPosition().equals(new Position(2657, 2640, 0))
                                && !player.getPosition().equals(new Position(2657, 2641, 0))
                                && !player.getPosition().equals(new Position(2657, 2642, 0))
                                && !player.getPosition().equals(new Position(2657, 2643, 0))
                                && !player.getPosition().equals(new Position(2657, 2644, 0))
                        )
                            if (!isJailedandInJail(player))
                                PlayerDeathUtil.onHardcoreIronmanDeath(player);
                    }
                    if (player.getGameMode().isOneLife()) {
                        if (playerArea == null || !playerArea.isSafeForHardcore()
                                && !(player.getPosition().getX() > 2668 && player.getPosition().getX() <= 2644 && player.getPosition().getY() >= 2644 && player.getPosition().getY() <= 2670)
                                && !player.getPosition().equals(new Position(2657, 2639, 0))
                                && !player.getPosition().equals(new Position(2657, 2638, 0))
                                && !player.getPosition().equals(new Position(2657, 2640, 0))
                                && !player.getPosition().equals(new Position(2657, 2641, 0))
                                && !player.getPosition().equals(new Position(2657, 2642, 0))
                                && !player.getPosition().equals(new Position(2657, 2643, 0))
                                && !player.getPosition().equals(new Position(2657, 2644, 0))
                        )
                            if (!isJailedandInJail(player))
                                PlayerDeathUtil.onOneLifeGameModeDeath(player);
                    }

                    if (pestControl) {
                        pestControlInstance.handlePlayerDeath(player);
                    } else if (!handledDeath) {
                        if (player.instance != null) {
                            player.instance.handlePlayerDeath(player);
                        } else if (isJailedandInJail(player)) {
                            player.moveTo(GameConstants.DEFAULT_JAIL_POSITION.clone());
                        } else if (player.getGameMode().isSpawn()) {
                            player.moveTo(GameConstants.DEFAULT_DEATH_POSITION.clone());
                            player.getTimerRepository().cancel(TimerKey.COMBAT_COOLDOWN);
                            player.setLastDeath(new Date());
                            if (unsafe) {
                                if (player.isOpenPresetsOnDeath()) {
                                    //player.BLOCK_ALL_BUT_TALKING = true;
                                    TaskManager.submit(player, 3, () -> {
                                        //player.BLOCK_ALL_BUT_TALKING = false;
                                        Presetables.INSTANCE.open(player);
                                    });
                                }
                            }
                        } else {
                            player.moveTo(GameConstants.DEFAULT_DEATH_POSITION.clone());
                            player.getTimerRepository().cancel(TimerKey.COMBAT_COOLDOWN);
                            player.setLastDeath(new Date());

                            //player.getPacketSender().sendEffectTimer((int) ((double) ItemOnGroundManager.STATE_UPDATE_DELAY_ON_DEATH * 0.6), EffectTimer.DEATH_ITEMS_DESPAWN);
                            if (killer != null) {
                                if (killer.isPlayer()) {
                                    player.getPacketSender().sendEffectTimer((int) (ItemOnGroundManager.STATE_UPDATE_DELAY * 0.6), EffectTimer.DEATH_ITEMS_DESPAWN);

                                } else {
                                    player.getPacketSender().sendEffectTimer((int) (ItemOnGroundManager.STATE_UPDATE_DELAY_ON_DEATH * 0.6), EffectTimer.DEATH_ITEMS_DESPAWN);
                                }
                            }
                            player.getRunePouch().refreshItems();
                        }
                    }


                    stop();
                    break;
            }
            ticks--;
        } catch (Exception e) {
            setEventRunning(false);
            e.printStackTrace();
            player.resetAttributes();
            if (isJailedandInJail(player)) {
                player.moveTo(new Position(3231 + Misc.random(7), 9799 + Misc.random(3)));
            } else {
                player.moveTo(GameConstants.DEFAULT_DEATH_POSITION.clone());
            }
        }
    }

    private boolean isJailedandInJail(Player player) {
        if (AreaManager.inside(player.getPosition(), new Boundary(3201, 3248, 9789, 9830))/* && player.isJailed()*/)
            return true;
        return false;
    }

    private Position getDropPosition(Position dropPosition) {
        if (instanced) {
            if (playerArea instanceof ZulrahShrine) return new Position(2213, 3056, 0);
            else if (playerArea instanceof VorkathArea) return new Position(2272, 4051, 0);
            else if (playerArea instanceof HydraArea) return new Position(1351, 10250, 0);
            else if (playerArea instanceof CerberusArea) return new Position(2873, 9847, 0);
        }
        return dropPosition;
    }
}
