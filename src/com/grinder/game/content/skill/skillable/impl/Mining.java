package com.grinder.game.content.skill.skillable.impl;

import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.item.degrading.DegradingType;
import com.grinder.game.content.item.jewerly.BraceletOfClay;
import com.grinder.game.content.miscellaneous.PetHandler;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.content.skill.skillable.DefaultSkillable;
import com.grinder.game.content.skill.skillable.impl.mining.*;
import com.grinder.game.content.skill.task.SkillMasterType;
import com.grinder.game.content.skill.task.SkillTaskManager;
import com.grinder.game.content.task_new.DailyTask;
import com.grinder.game.content.task_new.PlayerTaskManager;
import com.grinder.game.content.task_new.WeeklyTask;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerExtKt;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;
import com.grinder.game.entity.object.*;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Skill;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.game.task.impl.TimedObjectReplacementTask;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;

import java.util.Optional;

/**
 * Represents the Mining skill.
 *
 * @author Professor Oak
 */
public class Mining extends DefaultSkillable {

    /*
    * Messages that are sent to the player while training Mining skill
     */
    private static final String[][] MINING_MESSAGES = {
            { "@whi@You can train in the Mining guild after reaching level 60 Mining!" },
            { "@whi@Every equipped Prospector gear piece increases your experience gain in Mining skill!" },
            { "@whi@You can take a Mining skill task from your master for bonus rewards." },
            { "@whi@You can train Mining in the Motherlode Mine!" },
            { "@whi@Mining in the Wilderness Resource Area provides 20% bonus experience gain!" },
            { "@whi@Mining with the skillcape equipped will give you 20% bonus experience gain!" },
            { "@whi@Players with Members rank can train in unique skilling zones with a lot of ores." },
    };

    public static String currentMessage;

    public static void sendSkillRandomMessages(Player player) {
        currentMessage = MINING_MESSAGES[Misc.getRandomInclusive(MINING_MESSAGES.length - 1)][0];
        player.getPacketSender().sendMessage("<img=779> " + currentMessage);
    }

    /**
     * The {@link GameObject} to mine.
     */
    private final GameObject rockObject;
    /**
     * The {@code rock} as an enumerated type which contains information about
     * it, such as required level.
     */
    private final RockType rock;
    /**
     * The pickaxe we're using to mine.
     */
    private Optional<PickaxeType> pickaxe = Optional.empty();

    /**
     * Constructs a new {@link Mining}.
     *
     * @param rockObject The rock to mine.
     * @param rock       The rock's data
     */
    public Mining(GameObject rockObject, RockType rock) {
        this.rockObject = rockObject;
        this.rock = rock;
    }

    @Override
    public void start(Player player) {
        player.getPacketSender().sendMessage("You swing your pickaxe at the rock..");
        super.start(player);
    }

    @Override
    public void startAnimationLoop(Player player) {
        Task animLoop = new Task(9, player, true) {
            @Override
            protected void execute() {
                player.setPositionToFace(rockObject.getPosition());
                if (rockObject.getId() == 34773 || rock == RockType.AMETHYST) {
                    player.performAnimation(pickaxe.get().getSecondAnimation());
                } else {
                    player.performAnimation(pickaxe.get().getAnimaion());
                }
                //player.getPacketSender().sendSound(Sounds.MINING_SOUND);
            }
        };
        TaskManager.submit(animLoop);
        getTasks().add(animLoop);
    }

    @Override
    public void startGraphicsLoop(Player player) {}

    @Override
    public void startSoundLoop(Player player) {
		/*Task soundLoop = new Task(2, player, true) {
			@Override
			protected void execute() {
				player.getPacketSender().sendSound(Sounds.MINING_SOUND);
			}
		};
		TaskManager.submit(soundLoop);
		getTasks().add(soundLoop);*/
    }

    @Override
    public void onCycle(Player player) {
    }

    @Override
    public void finishedCycle(Player player) {

        // Random event processing
        if (PlayerExtKt.tryRandomEventTrigger(player, 0.5F)) {
            cancel(player);
            return;
        }

        // Add ores..
        if (rockObject.getId() == 34773) {
            if (player.getSkillManager().getMaxLevel(Skill.MINING) < 30) {
                player.getInventory().add(ItemID.RUNE_ESSENCE, 1);
            } else {
                player.getInventory().add(ItemID.PURE_ESSENCE, 1);
            }
            player.getPacketSender().sendAreaPlayerSound(Sounds.ROCK_MINED_SOUND);
            player.getSkillManager().addExperience(Skill.MINING, (int) (rock.getXpReward()));
            return;
        }

        // Send message
        if (rock == RockType.SANDSTONE) {
            player.getPacketSender().sendMessage("You manage to get some sandstones.");
        } else if (rock == RockType.GRANITE) {
            player.getPacketSender().sendMessage("You manage to get some granite.");
        } else if (rock == RockType.CLAY) {
            player.getPacketSender().sendMessage("You manage to get some clay.");
        } else if(rock == RockType.EFH_SALT || rock == RockType.URT_SALT || rock == RockType.TE_SALT || rock == RockType.BASALT) {
            player.getPacketSender().sendMessage("You manage to get some salts.");
        } else {
            player.getPacketSender().sendMessage("You get some ores.");
        }

        // Roll pet
        PetHandler.onSkill(player, Skill.MINING);

        // Skill random messages while skilling
        if (Misc.getRandomInclusive(5) == Misc.getRandomInclusive(5) && player.getSkillManager().getMaxLevel(Skill.MINING) < SkillUtil.maximumAchievableLevel()) {
            sendSkillRandomMessages(player);
        }

        player.getPoints().increase(AttributeManager.Points.ORES_MINED, 1); // Increase points

        if (rock.getOreId() == ItemID.RUNITE_ORE) {
            player.getPoints().increase(AttributeManager.Points.RUNE_ORES_MINED, 1); // Increase points
        }

        // Tasks
        AchievementManager.processFor(AchievementType.HEAVY_WORK, player);
        if (rock.getOreId() == ItemID.IRON_ORE) {
            AchievementManager.processFor(AchievementType.WET_WORK, player);
        }
        if (rock.getOreId() == ItemID.COAL) {
            AchievementManager.processFor(AchievementType.MINING_THE_ESSENTIALS, player);
        }

        // Send sound
        player.getPacketSender().sendAreaPlayerSound(Sounds.ROCK_MINED_SOUND);

//        TaskList.ProgressTask(player, 4, ItemDefinition.forId(rock.getOreId()).getName());

        handleTasks(player);

        // Process infernal pickaxe
        boolean infernalPickaxe = (player.getEquipment().contains(ItemID.INFERNAL_PICKAXE) || player.getInventory().contains(ItemID.INFERNAL_PICKAXE)) && Misc.getRandomInclusive(4) == 1;
        if (infernalPickaxe && pickaxe.get().getId() == ItemID.INFERNAL_PICKAXE & rock.getOreId() != ItemID.COAL && rock != RockType.GEM && rock != RockType.EFH_SALT && rock != RockType.URT_SALT && rock != RockType.TE_SALT && rock != RockType.BASALT) {
            switch (rock.getOreId()) {
                case ItemID.COPPER_ORE: // Copper ore
                case ItemID.TIN_ORE: // Tin ore
                    player.getInventory().add(ItemID.BRONZE_BAR, 1);
                    player.getSkillManager().addExperience(Skill.SMITHING, 6);
                    break;
                case ItemID.BLURITE_ORE: // Blurite ore
                    player.getInventory().add(9467, 1);
                    player.getSkillManager().addExperience(Skill.SMITHING, 16);
                    break;
                case ItemID.IRON_ORE: // Iron ore
                    player.getInventory().add(ItemID.IRON_BAR, 1);
                    player.getSkillManager().addExperience(Skill.SMITHING, 19);
                    break;
                case ItemID.SILVER_ORE: // Silver ore
                    player.getInventory().add(ItemID.SILVER_BAR, 1);
                    player.getSkillManager().addExperience(Skill.SMITHING, 22);
                    break;
                case ItemID.GOLD_ORE: // Gold ore
                    player.getInventory().add(ItemID.GOLD_BAR, 1);
                    player.getSkillManager().addExperience(Skill.SMITHING, 70);
                    break;
                case ItemID.MITHRIL_ORE: // Mithril ore
                    player.getInventory().add(ItemID.MITHRIL_BAR, 1);
                    player.getSkillManager().addExperience(Skill.SMITHING, 65);
                    break;
                case ItemID.ADAMANTITE_ORE: // Adamantite ore
                    player.getInventory().add(ItemID.ADAMANTITE_BAR, 1);
                    player.getSkillManager().addExperience(Skill.SMITHING, 82);
                    break;
                case ItemID.RUNITE_ORE: // Runite ore
                    player.getInventory().add(ItemID.RUNITE_BAR, 1);
                    player.getSkillManager().addExperience(Skill.SMITHING, 110);
                    break;
            }
            if (player.getEquipment().contains(ItemID.INFERNAL_PICKAXE)) {
                player.getItemDegradationManager().degrade(DegradingType.SKILLING, -1);
            } else {
                player.getItemDegradationManager().degradeInventoryItems(DegradingType.SKILLING, -1, 13243);
            }
            player.performGraphic(new Graphic(86));
        } else {

            if (rock == RockType.GEM) { // Gem rock handling
                player.getInventory().add(GemType.generateGemType().getUncutId(), 1);
            } else if (rock == RockType.CLAY && player.getEquipment().contains(ItemID.BRACELET_OF_CLAY)) { // Bracelet of clay handling
                player.getInventory().add(ItemID.SOFT_CLAY, 1);
                BraceletOfClay.INSTANCE.handleBraceletOfClay(player.getEquipment(), 1, player);
            } else if (rock == RockType.SANDSTONE) { // Sandstone handling
                int sandstoneId = SandStone.generateSandStoneType().getSandstoneId();
                player.getInventory().add(sandstoneId, 1);
                if (sandstoneId == ItemID.SANDSTONE_1KG_) {
                    player.getSkillManager().addExperience(Skill.MINING, 30);
                } else if (sandstoneId == ItemID.SANDSTONE_2KG_) {
                    player.getSkillManager().addExperience(Skill.MINING, 40);
                } else if (sandstoneId == ItemID.SANDSTONE_5KG_) {
                    player.getSkillManager().addExperience(Skill.MINING, 50);
                } else { // Sandstone 10KG
                    player.getSkillManager().addExperience(Skill.MINING, 60);
                }
            } else if (rock == RockType.GRANITE) { // Granite rock handling
                int graniteRockId = Granite.generateGraniteRockType().getGraniteId();
                player.getInventory().add(graniteRockId, 1);
                if (graniteRockId == ItemID.GRANITE_500G_) {
                    player.getSkillManager().addExperience(Skill.MINING, 50);
                } else if (graniteRockId == ItemID.GRANITE_2KG_) {
                    player.getSkillManager().addExperience(Skill.MINING, 60);
                } else { // Granite 5KG
                    player.getSkillManager().addExperience(Skill.MINING, 75);
                }
            } else if (rock == RockType.EFH_SALT || rock == RockType.URT_SALT || rock == RockType.TE_SALT) {
                player.getInventory().add(rock.getOreId(), 2 + Misc.random(5));
            } else {
                // Add ore
                player.getInventory().add(rock.getOreId(), 1);

                // Amulet of glory increases the chances of randomly finding gems while mining from 1/256 to 1/86
                // if it has a minimum of 1 charge remaining. It also significantly boosts mining gem rocks.
                if (player.getEquipment().containsAny(ItemID.AMULET_OF_ETERNAL_GLORY, ItemID.AMULET_OF_GLORY_1_, ItemID.AMULET_OF_GLORY_2, ItemID.AMULET_OF_GLORY_3, ItemID.AMULET_OF_GLORY_4, ItemID.AMULET_OF_GLORY_5_,
                        ItemID.AMULET_OF_GLORY_6_, ItemID.AMULET_OF_GLORY_T1_, ItemID.AMULET_OF_GLORY_T_2, ItemID.AMULET_OF_GLORY_T3_, ItemID.AMULET_OF_GLORY_T4_, ItemID.AMULET_OF_GLORY_T5_, ItemID.AMULET_OF_GLORY_T6_) && Misc.random(256) <= 5) {
                    player.getInventory().add(GemType.generateGemType().getUncutId(), 1);
                    player.sendMessage("Your glory amulet shiny effect spotted hidden gems in the ore.");
                    player.getPacketSender().sendSound(Sounds.PROSPECT_ORE);
                }

                // Mining cape 5% chance of getting an extra ore from adamantite rocks
                if (rock != RockType.RUNITE && player.getEquipment().containsAny(ItemID.MINING_CAPE, ItemID.MINING_CAPE_T_) && Misc.random(100) <= 5) {
                player.getInventory().add(rock.getOreId(), 1);
                player.sendMessage("Your mining cape effect lets you mine one extra ore instantly.");
                }

                // Varrock armour 1,2,3,4
                if (rock != RockType.RUNITE && rock != RockType.ADAMANTITE && rock != RockType.MITHRIL && player.getEquipment().containsAny(ItemID.VARROCK_ARMOUR_1) && Misc.random(100) <= 10) {
                    player.getInventory().add(rock.getOreId(), 1);
                    player.getSkillManager().addExperience(Skill.MINING, (int) (rock.getXpReward()));
                }  else if (rock != RockType.RUNITE && rock != RockType.ADAMANTITE && player.getEquipment().containsAny(ItemID.VARROCK_ARMOUR_2) && Misc.random(100) <= 10) {
                    player.getInventory().add(rock.getOreId(), 1);
                    player.getSkillManager().addExperience(Skill.MINING, (int) (rock.getXpReward()));
                }  else if (rock != RockType.RUNITE && player.getEquipment().containsAny(ItemID.VARROCK_ARMOUR_3) && Misc.random(100) <= 10) {
                    player.getInventory().add(rock.getOreId(), 1);
                    player.getSkillManager().addExperience(Skill.MINING, (int) (rock.getXpReward()));
                }  else if (player.getEquipment().containsAny(ItemID.VARROCK_ARMOUR_4) && Misc.random(100) <= 10) {
                    player.getInventory().add(rock.getOreId(), 1);
                    player.getSkillManager().addExperience(Skill.MINING, (int) (rock.getXpReward()));
                }

            }
        }

        // Add exp..
        if (rock != RockType.SANDSTONE && rock != RockType.GRANITE) {
            player.getSkillManager().addExperience(Skill.MINING, rock.getXpReward());
        }

        // Process skilling task
        SkillTaskManager.perform(player, rock.getOreId(), 1, SkillMasterType.MINING);

        if (Misc.getRandomInclusive(rock.getCycles() + 5) > 2) {
            return;
        }
        if (player.getEquipment().contains(new Item(ItemID.MINING_GLOVES)) && Misc.random(10) == 1) { // 1/10 chance to skip rock depletion
            return;
        } else if (player.getEquipment().contains(new Item(ItemID.SUPERIOR_MINING_GLOVES)) && Misc.random(8) == 1) { // 1/8 chance to skip rock depletion
            return;
        }
        if (player.getEquipment().contains(new Item(ItemID.EXPERT_MINING_GLOVES)) && Misc.random(5) == 1) { // 1/5 chance to skip rock depletion
            return;
        }

        // Despawn object and respawn it after a short period of time.
/*        if (rock.equals(RockType.AMETHYST)) {
            DialogueManager.sendStatement(player, "The rocks crumble over your pickaxe and you were interrupted.");
        }*/

        TaskManager.submit(new TimedObjectReplacementTask(rockObject,
        DynamicGameObject.createPublic(rock.getEmpty(), rockObject.getPosition(), rockObject.getObjectType(), rockObject.getFace()),
        rock.getRespawnTimer()));
        // Stop skilling..
        cancel(player);
    }

    private void handleTasks(Player player) {
        if(rock.getOreId() == ItemID.COAL) {
            PlayerTaskManager.progressTask(player, DailyTask.MINE_COAL);
        } else if(rock.getOreId() == ItemID.IRON_ORE) {
            PlayerTaskManager.progressTask(player, DailyTask.MINE_IRON_ORE);
        } else if(rock.getOreId() == ItemID.ADAMANTITE_ORE) {
            PlayerTaskManager.progressTask(player, WeeklyTask.ADAMANTITE_ORE);
        } else if(rock.getOreId() == ItemID.RUNITE_ORE) {
            PlayerTaskManager.progressTask(player, WeeklyTask.RUNITE_ORE);
        }
    }

    @Override
    public int cyclesRequired(Player player) {
        int cycles = rock.getCycles() + Misc.getRandomInclusive(5);
        int miningLevel = player.getSkillManager().getMaxLevel(Skill.MINING);

        // Celestial item effects: Gives an invisible +4 boost to Mining; stacks with both visible and invisible boosts,
        // though visible boosts are capped to level 99. When charged, gives the Varrock armour 3's mining effect.
        if (player.getEquipment().containsAny(25539, 22541, 25543, 25545))
            miningLevel += 4;

        cycles -= miningLevel * 0.1;
        cycles -= cycles * pickaxe.get().getSpeed();
        if(cycles < 3) {
            cycles = 3;
        }
        return cycles;
    }

    public static Optional<PickaxeType> findPickaxe(Player player) {
        Optional<PickaxeType> pickaxe = Optional.empty();
        for (PickaxeType a : PickaxeType.values()) {
            if (player.getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT].getId() == a.getId()
                    || player.getInventory().contains(a.getId())) {

                // If we have already found a pickaxe,
                // don't select others that are worse or can't be used
                if (pickaxe.isPresent()) {
                    if (player.getSkillManager().getMaxLevel(Skill.MINING) < a.getRequiredLevel()) {
                        continue;
                    }
                    if (a.getRequiredLevel() < pickaxe.get().getRequiredLevel()) {
                        continue;
                    }
                }

                pickaxe = Optional.of(a);
            }
        }

        return pickaxe;
    }

    @Override
    public boolean hasRequirements(Player player) {
        // Attempt to find a pickaxe..
        pickaxe = findPickaxe(player);

        // Check if we found one..
        if (!pickaxe.isPresent()) {
            player.getPacketSender().sendMessage("You don't have a pickaxe which you can use.", 1000);
            return false;
        }

        // Check if we have the required level to mine this {@code rock} using
        // the {@link Pickaxe} we found..
        if (player.getSkillManager().getCurrentLevel(Skill.MINING) < pickaxe.get().getRequiredLevel()) {
            player.getPacketSender()
                    .sendMessage("You don't have a pickaxe which you have the required Mining level to use.", 1000);
            return false;
        }

        // Check if we have the required level to mine this {@code rock}..
        if (player.getSkillManager().getCurrentLevel(Skill.MINING) < rock.getRequiredLevel()) {
            player.getPacketSender().sendMessage(
                    "You need a Mining level of at least " + rock.getRequiredLevel() + " to mine this rock.", 1000);
            return false;
        }

        // Finally, check if the rock object remains there.
        // Another player may have mined it already.
        if (!ClippedMapObjects.exists(rockObject) || ObjectManager.existsAt(rock.getEmpty(), rockObject.getPosition()) || rock.equals(RockType.NO_ORES)) {
            player.getPacketSender().sendSound(Sounds.PROSPECT_ORE);
            player.sendMessage(ObjectManager.existsAt(11385, rockObject.getPosition()) ? "There are no minerals currently available in this rock." : "This rock contains no ores.");
            return false;
        }

        return super.hasRequirements(player);
    }

    @Override
    public boolean loopRequirements() {
        return true;
    }

    @Override
    public boolean allowFullInventory() {
        return false;
    }

    public GameObject getTreeObject() {
        return rockObject;
    }
}
