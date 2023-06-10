package com.grinder.game.content.skill.skillable.impl.magic;

import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;
import com.grinder.game.model.*;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.item.Item;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.util.ItemID;
import com.grinder.util.timing.TimerKey;
import com.grinder.util.timing.TimerRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Stan van der Bend
 * @since 5-4-19
 */
public enum InteractiveSpell {

    VARROCK_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 1164;
        }

        @Override
        public int levelRequired() {
            return 25;
        }

        @Override
        public int baseExperience() {
            return 35;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(563), new Item(556, 3), new Item(554, 1)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.NORMAL;
        }

    }),
    LUMBRIDGE_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 1167;
        }

        @Override
        public int levelRequired() {
            return 31;
        }

        @Override
        public int baseExperience() {
            return 41;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.LAW_RUNE), new Item(ItemID.AIR_RUNE, 3), new Item(ItemID.EARTH_RUNE, 1)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.NORMAL;
        }

    }),
    FALADOR_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 1170;
        }

        @Override
        public int levelRequired() {
            return 37;
        }

        @Override
        public int baseExperience() {
            return 48;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.LAW_RUNE), new Item(ItemID.AIR_RUNE, 3), new Item(ItemID.WATER_RUNE, 1)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.NORMAL;
        }

    }),
    TELEPORT_HOUSE(new Spell() {

        @Override
        public int spellId() {
            return 19208;
        }

        @Override
        public int levelRequired() {
            return 40;
        }

        @Override
        public int baseExperience() {
            return 30;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.LAW_RUNE), new Item(ItemID.AIR_RUNE, 1), new Item(ItemID.EARTH_RUNE, 1)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.NORMAL;
        }

    }),
    CAMELOT_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 1174;
        }

        @Override
        public int levelRequired() {
            return 45;
        }

        @Override
        public int baseExperience() {
            return 55;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.LAW_RUNE), new Item(ItemID.AIR_RUNE, 5)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.NORMAL;
        }

    }),
    ARDOUGNE_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 1540;
        }

        @Override
        public int levelRequired() {
            return 51;
        }

        @Override
        public int baseExperience() {
            return 61;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.LAW_RUNE, 2), new Item(ItemID.WATER_RUNE, 2)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.NORMAL;
        }

    }),
    WATCHTOWER_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 1541;
        }

        @Override
        public int levelRequired() {
            return 58;
        }

        @Override
        public int baseExperience() {
            return 68;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.LAW_RUNE), new Item(ItemID.EARTH_RUNE, 2)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.NORMAL;
        }

    }),
    TROLLHEIM_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 7455;
        }

        @Override
        public int levelRequired() {
            return 61;
        }

        @Override
        public int baseExperience() {
            return 68;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.LAW_RUNE, 2), new Item(ItemID.FIRE_RUNE, 2)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.NORMAL;
        }

    }),
    PADDEWWA_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 13035;
        }

        @Override
        public int levelRequired() {
            return 54;
        }

        @Override
        public int baseExperience() {
            return 64;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.LAW_RUNE, 2), new Item(ItemID.AIR_RUNE, 1), new Item(ItemID.FIRE_RUNE)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.ANCIENT;
        }

    }),

    SENNTISTEN_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 13045;
        }

        @Override
        public int levelRequired() {
            return 60;
        }

        @Override
        public int baseExperience() {
            return 70;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.LAW_RUNE, 2), new Item(ItemID.SOUL_RUNE, 1)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.ANCIENT;
        }

    }),

    KHARYRLL_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 13053;
        }

        @Override
        public int levelRequired() {
            return 66;
        }

        @Override
        public int baseExperience() {
            return 76;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.LAW_RUNE, 2), new Item(ItemID.BLOOD_RUNE, 1)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.ANCIENT;
        }

    }),

    LASSAR_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 13061;
        }

        @Override
        public int levelRequired() {
            return 72;
        }

        @Override
        public int baseExperience() {
            return 82;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.LAW_RUNE, 2), new Item(ItemID.WATER_RUNE, 4)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.ANCIENT;
        }

    }),

    DAREEYAK_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 13069;
        }

        @Override
        public int levelRequired() {
            return 78;
        }

        @Override
        public int baseExperience() {
            return 88;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.LAW_RUNE, 2), new Item(ItemID.AIR_RUNE, 2), new Item(ItemID.FIRE_RUNE, 3)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.ANCIENT;
        }

    }),

    CARRALLANGAR_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 13079;
        }

        @Override
        public int levelRequired() {
            return 84;
        }

        @Override
        public int baseExperience() {
            return 94;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.LAW_RUNE), new Item(ItemID.SOUL_RUNE, 2)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.ANCIENT;
        }

    }),

    ANNAKARL_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 13087;
        }

        @Override
        public int levelRequired() {
            return 90;
        }

        @Override
        public int baseExperience() {
            return 100;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.LAW_RUNE, 2), new Item(ItemID.BLOOD_RUNE, 2)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.ANCIENT;
        }

    }),

    BONES_TO_BANANAS(new Spell() {

        @Override
        public int spellId() {
            return 1159;
        }

        @Override
        public int levelRequired() {
            return 15;
        }

        @Override
        public int baseExperience() {
            return 357;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(561), new Item(555, 2), new Item(557, 2)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {


        }

    }),
    LOW_ALCHEMY(new Spell() {

        @Override
        public int spellId() {
            return 1162;
        }

        @Override
        public int levelRequired() {
            return 21;
        }

        @Override
        public int baseExperience() {
            return 1017;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(554, 3), new Item(561)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {

        }

    }),
    TELEKINETIC_GRAB(new Spell() {

        @Override
        public int spellId() {
            return 1168;
        }

        @Override
        public int levelRequired() {
            return 33;
        }

        @Override
        public int baseExperience() {
            return 2988;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(563, 1), new Item(556, 1)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {


        }

    }),
    HOME_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 19208;
        }

        @Override
        public int levelRequired() {
            return 40;
        }

        @Override
        public int baseExperience() {
            return 30;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.LAW_RUNE), new Item(ItemID.EARTH_RUNE), new Item(ItemID.AIR_RUNE)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.NORMAL;
        }

    }),
    SUPERHEAT_ITEM(new Spell() {

        @Override
        public int spellId() {
            return 1173;
        }

        @Override
        public int levelRequired() {
            return 43;
        }

        @Override
        public int baseExperience() {
            return 2416;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(554, 4), new Item(561)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

    }),
    HIGH_ALCHEMY(new Spell() {

        @Override
        public int spellId() {
            return 1178;
        }

        @Override
        public int levelRequired() {
            return 55;
        }

        @Override
        public int baseExperience() {
            return 5421;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(554, 5), new Item(561)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {


        }

    }),
    BONES_TO_PEACHES(new Spell() {

        @Override
        public int spellId() {
            return 15877;
        }

        @Override
        public int levelRequired() {
            return 60;
        }

        @Override
        public int baseExperience() {
            return 2121;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(561, 2), new Item(555, 4), new Item(557, 4)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {


        }

    }),
    APE_ATOLL_TELPEORT(new Spell() {

        @Override
        public int spellId() {
            return 18470;
        }

        @Override
        public int levelRequired() {
            return 64;
        }

        @Override
        public int baseExperience() {
            return 74;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(554, 2), new Item(555, 2), new Item(563, 2), new Item(1963)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {

        }

    }),
    BAKE_PIE(new Spell() {

        @Override
        public int spellId() {
            return 30017;
        }

        @Override
        public int levelRequired() {
            return 65;
        }

        @Override
        public int baseExperience() {
            return 5121;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(9075, 1), new Item(554, 5), new Item(555, 4)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {

            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {


        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.LUNAR;
        }
    }),
    KOUREND_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 30330;
        }

        @Override
        public int levelRequired() {
            return 69;
        }

        @Override
        public int baseExperience() {
            return 81;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
        	return Optional.of(new Item[]{new Item(563, 2), new Item(566, 2), new Item(555, 4), new Item(554, 5)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {

        }

    }),
    TELEPORT_OTHER_LUMBRIDGE(new Spell() {

        @Override
        public int spellId() {
            return 12425;
        }

        @Override
        public int levelRequired() {
            return 74;
        }

        @Override
        public int baseExperience() {
            return 8200;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(566), new Item(563), new Item(557)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {

        }

    }),
    OURANIA_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 30065;
        }

        @Override
        public int levelRequired() {
            return 78;
        }

        @Override
        public int baseExperience() {
            return 69;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(9075), new Item(563), new Item(555)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {


        }
        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.LUNAR;
        }

    }),
    TELEPORT_OTHER_FALADOR(new Spell() {

        @Override
        public int spellId() {
            return 12435;
        }

        @Override
        public int levelRequired() {
            return 82;
        }

        @Override
        public int baseExperience() {
            return 8800;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(566), new Item(563), new Item(555)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {

        }

    }),
    TARGET_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 30331;
        }

        @Override
        public int levelRequired() {
            return 85;
        }

        @Override
        public int baseExperience() {
            return 70;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(563), new Item(560), new Item(562)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {

        }

    }),
    TARGET_TELEPORT_ANCIENT(new Spell() {

        @Override
        public int spellId() {
            return 30333;
        }

        @Override
        public int levelRequired() {
            return 85;
        }

        @Override
        public int baseExperience() {
            return 70;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(563), new Item(560), new Item(562)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {

        }
        
        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.ANCIENT;
        }

    }),
    TARGET_TELEPORT_LUNAR(new Spell() {

        @Override
        public int spellId() {
            return 30334;
        }

        @Override
        public int levelRequired() {
            return 85;
        }

        @Override
        public int baseExperience() {
            return 70;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(563), new Item(560), new Item(562)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {

        }
        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.LUNAR;
        }

    }),
    VENGEANCE_OTHER(new Spell() {

        @Override
        public int spellId() {
            return 30298;
        }

        @Override
        public int levelRequired() {
            return 93;
        }

        @Override
        public int baseExperience() {
            return 7500;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(9075, 3), new Item(557, 10), new Item(560, 2)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {


        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.LUNAR;
        }
    }),
    GHORROCK_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 13095;
        }

        @Override
        public int levelRequired() {
            return 96;
        }

        @Override
        public int baseExperience() {
            return 106;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(563, 2), new Item(555, 2)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {

        }
        
        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.ANCIENT;
        }

    }),
    MOONCLAN_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 30064;
        }

        @Override
        public int levelRequired() {
            return 69;
        }

        @Override
        public int baseExperience() {
            return 66;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.ASTRAL_RUNE), new Item(ItemID.LAW_RUNE), new Item(ItemID.EARTH_RUNE, 2)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.LUNAR;
        }

    }),
    WATERBIRTH_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 30106;
        }

        @Override
        public int levelRequired() {
            return 72;
        }

        @Override
        public int baseExperience() {
            return 71;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.ASTRAL_RUNE, 2), new Item(ItemID.LAW_RUNE), new Item(ItemID.WATER_RUNE)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.LUNAR;
        }

    }),
    BARBARIAN_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 30114;
        }

        @Override
        public int levelRequired() {
            return 75;
        }

        @Override
        public int baseExperience() {
            return 76;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.ASTRAL_RUNE, 2), new Item(ItemID.LAW_RUNE, 2), new Item(ItemID.FIRE_RUNE, 3)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.LUNAR;
        }

    }),
    KHAZARD_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 30138;
        }

        @Override
        public int levelRequired() {
            return 78;
        }

        @Override
        public int baseExperience() {
            return 80;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.ASTRAL_RUNE, 2), new Item(ItemID.LAW_RUNE, 2), new Item(ItemID.WATER_RUNE, 4)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.LUNAR;
        }

    }),

    FISHING_GUILD_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 30146;
        }

        @Override
        public int levelRequired() {
            return 85;
        }

        @Override
        public int baseExperience() {
            return 89;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.ASTRAL_RUNE, 3), new Item(ItemID.LAW_RUNE, 3), new Item(ItemID.WATER_RUNE, 10)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.LUNAR;
        }

    }),
    CATHERBY_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 30169;
        }

        @Override
        public int levelRequired() {
            return 87;
        }

        @Override
        public int baseExperience() {
            return 92;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.ASTRAL_RUNE, 3), new Item(ItemID.LAW_RUNE, 3), new Item(ItemID.WATER_RUNE, 10)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.LUNAR;
        }

    }),
    ICE_PLATEAU_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 30163;
        }

        @Override
        public int levelRequired() {
            return 89;
        }

        @Override
        public int baseExperience() {
            return 96;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.ASTRAL_RUNE, 3), new Item(ItemID.LAW_RUNE, 3), new Item(ItemID.WATER_RUNE, 8)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.LUNAR;
        }

    }),
    LUMBRIDGE_GRAVEYARD_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 32150;
        }

        @Override
        public int levelRequired() {
            return 6;
        }

        @Override
        public int baseExperience() {
            return 10;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.LAW_RUNE), new Item(ItemID.EARTH_RUNE, 2)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.ARCEEUS;
        }

    }),
    DRAYNOR_MANOR_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 32151;
        }

        @Override
        public int levelRequired() {
            return 17;
        }

        @Override
        public int baseExperience() {
            return 16;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.LAW_RUNE), new Item(ItemID.EARTH_RUNE, 1), new Item(ItemID.WATER_RUNE, 1)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.ARCEEUS;
        }

    }),
    BATTLEFRONT_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 32152;
        }

        @Override
        public int levelRequired() {
            return 23;
        }

        @Override
        public int baseExperience() {
            return 19;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.LAW_RUNE), new Item(ItemID.EARTH_RUNE, 1), new Item(ItemID.FIRE_RUNE, 1)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.ARCEEUS;
        }

    }),
    MIND_ALTAR_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 32153;
        }

        @Override
        public int levelRequired() {
            return 28;
        }

        @Override
        public int baseExperience() {
            return 22;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.LAW_RUNE), new Item(ItemID.MIND_RUNE, 1)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.ARCEEUS;
        }

    }),
    RESPAWN_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 32154;
        }

        @Override
        public int levelRequired() {
            return 34;
        }

        @Override
        public int baseExperience() {
            return 27;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.LAW_RUNE), new Item(ItemID.SOUL_RUNE, 1)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.ARCEEUS;
        }

    }),
    SALVE_GRAVEYARD_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 32155;
        }

        @Override
        public int levelRequired() {
            return 40;
        }

        @Override
        public int baseExperience() {
            return 30;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.LAW_RUNE), new Item(ItemID.SOUL_RUNE, 2)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.ARCEEUS;
        }

    }),
    FENKENSTRAINS_CASTLE_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 32156;
        }

        @Override
        public int levelRequired() {
            return 48;
        }

        @Override
        public int baseExperience() {
            return 50;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.LAW_RUNE), new Item(ItemID.SOUL_RUNE, 1), new Item(ItemID.EARTH_RUNE, 1)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.ARCEEUS;
        }

    }),
    WEST_ARDOUGNE_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 32157;
        }

        @Override
        public int levelRequired() {
            return 61;
        }

        @Override
        public int baseExperience() {
            return 68;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.LAW_RUNE), new Item(ItemID.SOUL_RUNE, 2)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.ARCEEUS;
        }

    }),
    HARMONY_ISLAND_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 32158;
        }

        @Override
        public int levelRequired() {
            return 65;
        }

        @Override
        public int baseExperience() {
            return 74;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.LAW_RUNE), new Item(ItemID.NATURE_RUNE, 1), new Item(ItemID.SOUL_RUNE, 1)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.ARCEEUS;
        }

    }),
    CEMETERY_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 32159;
        }

        @Override
        public int levelRequired() {
            return 71;
        }

        @Override
        public int baseExperience() {
            return 82;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.LAW_RUNE), new Item(ItemID.BLOOD_RUNE, 1), new Item(ItemID.SOUL_RUNE, 1)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.ARCEEUS;
        }

    }),
    BARROWS_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 32160;
        }

        @Override
        public int levelRequired() {
            return 83;
        }

        @Override
        public int baseExperience() {
            return 90;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.LAW_RUNE, 2), new Item(ItemID.BLOOD_RUNE, 1), new Item(ItemID.SOUL_RUNE, 2)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.ARCEEUS;
        }

    }),
    APE_ATOLL_TELEPORT(new Spell() {

        @Override
        public int spellId() {
            return 32161;
        }

        @Override
        public int levelRequired() {
            return 90;
        }

        @Override
        public int baseExperience() {
            return 100;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(ItemID.LAW_RUNE, 2), new Item(ItemID.BLOOD_RUNE, 2), new Item(ItemID.SOUL_RUNE, 2)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {
        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.ARCEEUS;
        }

    }),
    VENGEANCE(new Spell() {

        @Override
        public int spellId() {
            return 30306;
        }

        @Override
        public int levelRequired() {
            return 94;
        }

        @Override
        public int baseExperience() {
            return 14000;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(9075, 4), new Item(557, 10), new Item(560, 2)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {


        }

        @Override
        public MagicSpellbook getSpellbook() {
            return MagicSpellbook.LUNAR;
        }
    }),
    TELEPORT_OTHER_CAMELOT(new Spell() {

        @Override
        public int spellId() {
            return 12455;
        }

        @Override
        public int levelRequired() {
            return 90;
        }

        @Override
        public int baseExperience() {
            return 9500;
        }

        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(566, 2), new Item(563)});
        }

        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }

        @Override
        public void startCast(Agent cast, Agent castOn) {

        }

    }),
    CHARGE(new Spell(){

        @Override
        public int spellId() {
            return 1193;
        }

        @Override
        public int levelRequired() {
            return 80;
        }

        @Override
        public int baseExperience() {
            return 180;
        }
        @Override
        public void startCast(Agent cast, Agent castOn) {

            final TimerRepository timerRepository = cast.getTimerRepository();

            timerRepository.register(TimerKey.CHARGE_SPELL_CAST, 100);

            cast.performAnimation(CastSpellAnimation.EMPOWERING.getAnimation(cast));
            TaskManager.submit(new Task(2) {
                @Override
                public void execute() {
                    stop();
                    cast.performGraphic(new Graphic(308, GraphicHeight.HIGH));
            }
            });
            cast.getMotion().clearSteps();

            if(cast instanceof Player) {
                final Player player = (Player) cast;
                player.sendMessage("You imbued your god spells with more power!");
                player.getSkillManager().addExperience(Skill.MAGIC, baseExperience());
                player.getPacketSender().sendMinimapFlagRemoval();

                // Increase points
                player.getPoints().increase(AttributeManager.Points.CHARGE_SPELL_CASTED); // Increase points
                player.getPoints().increase(AttributeManager.Points.SPELLS_CASTED); // Increase points


                player.getPacketSender().sendEffectTimer(360, EffectTimer.CHARGE);
            }

            final int effectDuration = 600;// in ticks

            if(timerRepository.has(TimerKey.CHARGE_SPELL_EFFECT)){

                final int ticksLeft = timerRepository.left(TimerKey.CHARGE_SPELL_EFFECT);
                final int tickToAdd = effectDuration - ticksLeft;

                timerRepository.replaceIfLongerOrRegister(TimerKey.CHARGE_SPELL_EFFECT, tickToAdd);

            } else {
                timerRepository.register(TimerKey.CHARGE_SPELL_EFFECT, effectDuration);
            }

        }

        @Override
        public boolean canCast(Player player, Agent target, boolean deleteRunes) {

            if(player.getTimerRepository().has(TimerKey.CHARGE_SPELL_CAST)) {
                player.sendMessage("You can only cast this spell once every minute!");
                return false;
            }

            return super.canCast(player, target, true);
        }

        @Override
        public boolean hasEquipmentRequired(Player player) {
            final Item cape = player.getEquipment().atSlot(EquipmentConstants.CAPE_SLOT);
            if(cape == null)
                return false;
            return cape.getId() == ItemID.SARADOMIN_CAPE || cape.getId() == ItemID.ZAMORAK_CAPE || cape.getId() == ItemID.GUTHIX_CAPE ||
            		cape.getId() == ItemID.IMBUED_SARADOMIN_CAPE || cape.getId() == ItemID.IMBUED_ZAMORAK_CAPE || cape.getId() == ItemID.IMBUED_GUTHIX_CAPE ||
                    cape.getId() == ItemID.SARADOMIN_MAX_CAPE || cape.getId() == ItemID.ZAMORAK_MAX_CAPE || cape.getId() == ItemID.GUTHIX_MAX_CAPE ||
                    cape.getId() == 21176 || cape.getId() == 21780 || cape.getId() == 21784; // Imbued max capes
        }
        @Override
        public Optional<Item[]> itemsRequired(Player player) {
            return Optional.of(new Item[]{new Item(FIRE, 3), new Item(BLOOD, 3), new Item(AIR, 3)});
        }
        @Override
        public Optional<Item[]> equipmentRequired(Player player) {
            return Optional.empty();
        }
    });

    private final static int ASTRAL = 9075;
    private final static int DEATH = 560;
    private final static int BLOOD = 565;
    private final static int WRATH = 51880;
    private final static int WATER = 555;
    private final static int AIR = 556;
    private final static int EARTH = 557;
    private final static int FIRE = 554;
    private final static int MIND = 558;

    private final static int NATURE = 561;
    private final static int COSMIC = 564;
    private final static int CHAOS = 562;
    private final static int LAW = 563;
    private final static int SOUL = 566;

    private static final Map<Integer, InteractiveSpell> map = new HashMap<Integer, InteractiveSpell>();

    static {
        for (InteractiveSpell spell : InteractiveSpell.values()) {
            map.put(spell.getSpell().spellId(), spell);
        }
    }

    private Spell spell;

    InteractiveSpell(Spell spell) {
        this.spell = spell;
    }

    public static Optional<InteractiveSpell> forSpellId(int spellId) {
        InteractiveSpell spell = map.get(spellId);
        if (spell != null) {
            return Optional.of(spell);
        }
        return Optional.empty();
    }

    public boolean isTeleOther(){
        return this == TELEPORT_OTHER_CAMELOT
                || this == TELEPORT_OTHER_FALADOR
                || this == TELEPORT_OTHER_LUMBRIDGE;
    }

    public Spell getSpell() {
        return spell;
    }
}
