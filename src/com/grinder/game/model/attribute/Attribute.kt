package com.grinder.game.model.attribute

import com.grinder.game.content.minigame.aquaisneige.AquaisNeigeValueHolder
import com.grinder.game.content.minigame.blastfurnace.BlastFurnaceValueHolder
import com.grinder.game.content.minigame.fightcave.FightCaveValueHolder
import com.grinder.game.content.minigame.motherlodemine.MotherlodeMineValueHolder
import com.grinder.game.content.minigame.warriorsguild.WarriorsGuildValueHolder
import com.grinder.game.content.skill.skillable.impl.farming.attribute.CompostBinValueHolder
import com.grinder.game.content.skill.skillable.impl.farming.attribute.PatchValueHolder
import com.grinder.game.model.Position
import com.grinder.game.model.attribute.value.*

/**
 * Represents an [Attribute] of type [T].
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   2018-12-19
 * @version 1.0
 */
open class Attribute<T : AttributeValueHolderTemplate<*>>(val valueHolder: T) {

    /**
     * Any attribute key annotated with [Value] will be serialised to the
     * player file iff [AttributeValueHolderTemplate.save] is 'true',
     * which in most cases just means the value of the attribute
     * is not equal to the default value.
     */
    companion object {

        /*
        Numerical attributes
         */

        @Value(NumericalValueHolder::class) const val CRAWS_BOW_CHARGES = "craws_bow_charges"
        @Value(NumericalValueHolder::class) const val VIGORA_CHAINMACE_CHARGES = "vigora_chain_charges"
        @Value(NumericalValueHolder::class) const val SKILL_RESET_LAMP = "reset_lamp_selected_skill"
        @Value(NumericalValueHolder::class) const val SKILL_BOOK_OF_KNOWLEDGE = "book_of_knowledge_selected_skill"
        @Value(NumericalValueHolder::class) const val MAX_XP_LAMP = "max_xp_lamp_selected_skill"
        @Value(NumericalValueHolder::class) const val ANTIQUE_LAMP = "antique_lamp_selected_skill"
        @Value(NumericalValueHolder::class) const val VOTE_STREAK_POINTS = "vote_streak_points"
        @Value(NumericalValueHolder::class) const val VOTE_PENALTY_POINTS = "vote_penalty_points"
        @Value(NumericalValueHolder::class) const val EXPEDITIOUS_BRACELET_CHARGES = "expeditious_bracelet_charges"
        @Value(NumericalValueHolder::class) const val BRACELET_OF_SLAUGHTER_CHARGES = "bracelet_of_slaughter_charges"
        @Value(NumericalValueHolder::class) const val GAMBLE_TYPE = "gamble_type"
        @Value(NumericalValueHolder::class) const val YELL_CREDITS = "yell_credits"
        @Value(NumericalValueHolder::class) const val BONECRUSHER_CHARGES = "bonecrusher_charges"
        @Value(NumericalValueHolder::class) const val BONECRUSHER_NECKLACE_CHARGES = "bonecrusher_necklace_charges"
        @Value(NumericalValueHolder::class) const val AMULET_OF_CHEMISTRY_CHARGES = "amulet_of_chemistry_charges"
        @Value(NumericalValueHolder::class) const val BOW_OF_FAERDHINEN_CHARGES = "bow_of_faerdhinen_charges"
        @Value(NumericalValueHolder::class) const val COAL_BAG_TOTAL = "coal_bag_total"
        @Value(NumericalValueHolder::class) const val DOING_PYRAMID_STEP = "doing_pyramid_step"
        @Value(NumericalValueHolder::class) const val RUNNING_ENERGY = "running_energy"
        @Value(NumericalValueHolder::class) const val DAILY_LOGIN_STREAK = "daily_login_streak"
        @Value(NumericalValueHolder::class) const val TOTAL_LOGIN_STREAK = "total_login_streak"
        @Value(NumericalValueHolder::class) const val AMOUNT_PAID = "total_amount_paid"
        @Value(NumericalValueHolder::class) const val TRIDENT_OF_SWAMP_CHARGES = "trident_of_swamp_charges"
        @Value(NumericalValueHolder::class) const val TRIDENT_OF_SEAS_CHARGES = "trident_of_seas_charges"
        @Value(NumericalValueHolder::class) const val SANGUINESTI_CHARGES = "sanguinesti_charges"
        @Value(NumericalValueHolder::class) const val SCEPTRE_CHARGES = "sceptre_charges"
        @Value(NumericalValueHolder::class) const val LAVA_BLADE_CHARGES = "lava_blade_charges"
        @Value(NumericalValueHolder::class) const val INFERNAL_BLADE_CHARGES = "infernal_blade_charges"
        @Value(NumericalValueHolder::class) const val TIMES_PAID = "times_paid"
        @Value(NumericalValueHolder::class) const val MODIFIABLE_X_VALUE = "modifiable_x_value"
        @Value(NumericalValueHolder::class) const val BANK_QUANTITY_CONFIG = "bank_quantity_config"
        @Value(NumericalValueHolder::class) const val TAB_DISPLAY_CONFIG = "tab_display_config"
        @Value(NumericalValueHolder::class) const val SKULL_TIMER = "skull_timer"
        @Value(NumericalValueHolder::class) const val PEST_CONTROL_POINTS = "pest_control_points"
        @Value(NumericalValueHolder::class) const val MAGIC_CAPE_CHARGES = "magic_cape_charges"
        @Value(NumericalValueHolder::class) const val DENSE_ESSENCE_BLOCK_CHARGES = "dense_essence_block_charges"
        @Value(NumericalValueHolder::class) const val RING_OF_RECOIL_CHARGES = "ring_of_recoil_charges"
        @Value(NumericalValueHolder::class) const val RING_OF_FORGING_CHARGES = "ring_of_forging_charges"
        @Value(NumericalValueHolder::class) const val BRACELET_OF_CLAY_CHARGES = "braclet_of_clay_charges"
        @Value(NumericalValueHolder::class) const val TEN_DOLLAR_BOND = "ten_dollar_bond"
        @Value(NumericalValueHolder::class) const val TWENTY_FIVE_DOLLAR_BOND = "twenty_five_dollar_bond"
        @Value(NumericalValueHolder::class) const val FIFTY_DOLLAR_BOND = "fifty_dollar_bond"
        @Value(NumericalValueHolder::class) const val HUNDRED_DOLLAR_BOND = "hundred_dollar_bond"
        @Value(NumericalValueHolder::class) const val TWO_HUNDRED_FIFTY_DOLLAR_BOND = "two_hundred_fifty_dollar_bond"

        const val INTERFACE_ID = "interface_id"
        const val WALKABLE_INTERFACE_ID = "walkable_interface_id"
        const val MULTI_ICON = "multi_icon"
        const val TUTORIAL_STAGE = "tutorial_stage"
        const val CURRENT_BANK_TAB = "current_bank_tab"
        const val DOING_BRIMHAVEN_DAMAGE = "doing_brimhaven_damage"
        const val SESSION_SKILLING_POINTS = "session_skilling_points"
        const val LAST_REGION_ID = "last_regionid"
        const val STREAK_PENALTY = "streak_penalty"
        const val WILDERNESS_LEVEL = "wilderness_level"
        const val KAMIL_MINION_KILL_COUNT = "kamil_minion_kill_count"
        const val ARMADYL_KILL_COUNT = "armadyl_kill_count"
        const val BANDOS_KILL_COUNT = "bandos_kill_count"
        const val ZAMORAK_KILL_COUNT = "zamorak_kill_count"
        const val SARADOMIN_KILL_COUNT = "saradomin_kill_count"
        const val REVENANT_HEAL_COUNT = "can_revenant_heal"
        const val RANDOM_EVENT_PUZZLE = "random_event_puzzle"
        const val DICE_SCORE = "dice_score"
        const val DICE_ROLL = "dice_roll"
        const val SEQUENCE_COUNT = "sequence_count"

        /*
        Boolean attributes
         */

        @Value(BooleanValueHolder::class) const val SKILL_BOOK_OPEN = "skill_book_open"
        @Value(BooleanValueHolder::class) const val RESET_LAMP_OPEN = "reset_lamp_open"
        @Value(BooleanValueHolder::class) const val MAX_XP_LAMP_OPEN = "max_xp_lamp_open"
        @Value(BooleanValueHolder::class) const val ANTIQUE_LAMP_OPEN = "antique_lamp_open"
        @Value(BooleanValueHolder::class) const val CAN_LOOKUP_MAX_HIT = "can_lookup_max_hit"
        @Value(BooleanValueHolder::class) const val SHATTER_EFFECT = "shatter_effect"
        @Value(BooleanValueHolder::class) const val CANNON_RECLAIM_STATUS = "cannon_reclaim_status"
        @Value(BooleanValueHolder::class) const val DOING_FOOD_PUZZLE = "doing_food_puzzle"
        @Value(BooleanValueHolder::class) const val EXPERIENCED_LOCKED = "experience_locked"
        @Value(BooleanValueHolder::class) const val MULTIPLY_XP_DROPS = "multiple_xp_drops"
        @Value(BooleanValueHolder::class) const val DISABLE_RING_OF_SUFFERING_EFFECT = "disable_ring_of_suffering_effect"
        @Value(BooleanValueHolder::class) const val DISABLE_RING_OF_SUFFERING_I_EFFECT = "disable_ring_of_suffering_i_effect"
        @Value(BooleanValueHolder::class) const val BONECRUSHER_ACTIVE = "bonecrusher_active"
        @Value(BooleanValueHolder::class) const val BONECRUSHER_NECKLACE_ACTIVE = "bonecrusher_necklace_active"
        @Value(BooleanValueHolder::class) const val CONSUMED_SARADOMIN_LIGHT = "consumed_saradomin_light"
        @Value(BooleanValueHolder::class) const val GRABBED_PYRAMID_TOP = "grabbed_pyramid_top"
        @Value(BooleanValueHolder::class) const val DOING_PYRAMID_JUMP = "doing_pyramid_jump"
        @Value(BooleanValueHolder::class) const val DOING_PYRAMID_DAMAGE = "doing_pyramid_damage"
        @Value(BooleanValueHolder::class) const val DID_FAIL_AGILITY_OBSTACLE = "did_fail_agility_obstacle"
        @Value(BooleanValueHolder::class) const val HAS_PENDING_RANDOM_EVENT = "has_pending_random_event"
        @Value(BooleanValueHolder::class) const val HAS_PENDING_RANDOM_EVENT2 = "has_pending_random_event2"
        @Value(BooleanValueHolder::class) const val HAS_TRIGGER_RANDOM_EVENT = "has_trigger_random_event"
        @Value(BooleanValueHolder::class) const val HAS_PENDING_EXPERIENCE_DELAY = "has_pending_experience_delay"
        @Value(BooleanValueHolder::class) const val IS_RUNNING = "is_running"
        @Value(BooleanValueHolder::class) const val CHANGED_PASS = "changed_pass"
        @Value(BooleanValueHolder::class) const val DICER = "dicer"
        @Value(BooleanValueHolder::class) const val YOUTUBER = "youtuber"
        @Value(BooleanValueHolder::class) const val WIKI_EDITOR = "wiki_editor"
        @Value(BooleanValueHolder::class) const val DESIGNER = "designer"
        @Value(BooleanValueHolder::class) const val MIDDLEMAN = "middleman"
        @Value(BooleanValueHolder::class) const val EVENT_HOST = "event_host"
        @Value(BooleanValueHolder::class) const val VETERAN = "veteran"
        @Value(BooleanValueHolder::class) const val EX_STAFF = "ex_staff"
        @Value(BooleanValueHolder::class) const val RESPECTED = "respected"
        @Value(BooleanValueHolder::class) const val CONTRIBUTOR = "contributor"
        @Value(BooleanValueHolder::class) const val MOTM = "member_of_the_month"
        @Value(BooleanValueHolder::class) const val RING_OF_WEALTH_ACTIVATED = "ring_of_wealth_activated"
        @Value(BooleanValueHolder::class) const val IS_ON_TUTORIAL_MODE = "is_on_tutorial_mode"
        @Value(BooleanValueHolder::class) const val RECEIVED_STARTER = "received_starter"
        @Value(BooleanValueHolder::class) const val OPEN_PRESETS_ON_DEATH = "open_presets_on_death"
        @Value(BooleanValueHolder::class) const val ACCOUNT_FLAGGED = "account_flagged"
        @Value(BooleanValueHolder::class) const val FALLEN_ONELIFE_GAMEMODE = "fallen_onelife_gamemode"
        @Value(BooleanValueHolder::class) const val IS_JAILED = "is_jailed"
        @Value(BooleanValueHolder::class) const val PRESERVE_UNLOCKED = "preserve_unlocked"
        @Value(BooleanValueHolder::class) const val RIGOUR_UNLOCKED = "rigour_unlocked"
        @Value(BooleanValueHolder::class) const val AUGURY_UNLOCKED = "augury_unlocked"
        @Value(BooleanValueHolder::class) const val INSERT_MODE = "insert_mode"
        @Value(BooleanValueHolder::class) const val FIXED_BANK_WIDTH = "fixed_bank_width"
        @Value(BooleanValueHolder::class) const val SHOW_DEPOSIT_WORN_ITEMS = "show_deposit_worn_items"
        @Value(BooleanValueHolder::class) const val UNLOCKED_VIAL_CRUSHING = "unlocked_vial_crushing"
        @Value(BooleanValueHolder::class) const val VIAL_CRUSHING_TOGGLED = "vial_crushing_toggled"
        @Value(BooleanValueHolder::class) const val GODWARS_ROCK_ROPE = "godwars_rock_rope"
        @Value(BooleanValueHolder::class) const val GODWARS_ROCK_ROPE_2 = "godwars_rock_rope_2"
        @Value(BooleanValueHolder::class) const val CAN_ENTER_MOR_UL_REK = "can_enter_mor_ul_rek"
        @Value(BooleanValueHolder::class) const val GOLDEN_AGS = "golden_ags"
        @Value(BooleanValueHolder::class) const val GOLDEN_BGS = "golden_bgs"
        @Value(BooleanValueHolder::class) const val GOLDEN_SGS = "golden_sgs"
        @Value(BooleanValueHolder::class) const val GOLDEN_ZGS = "golden_zgs"
        @Value(BooleanValueHolder::class) const val CLAIMED_GAMEMODE_CAPE = "claimed_gamemode_cape"


        const val CAMPAIGN_DEVELOPER = "campaign_developer"
        const val FIRE_TOME_ACTIVATED = "tome_activated"
        const val WATER_TOME_ACTIVATED = "water_tome_activated"
        const val INVISIBLE = "invisible"
        const val PLACE_HOLDERS = "place_holders"
        const val SHOW_DROP_WARNING = "show_drop_warning"
        const val SHOW_EMPTY_WARNING = "show_empty_warning"
        const val STAFF_PVP_TOGGLED = "staff_pvp_toggled"
        const val PACKETS_BLOCKED = "packets_blocked"
        const val UPDATE_COLORFUL_ITEM = "update_colorful_item"
        const val PASSING_OBSTACLE = "passing_obstacle"
        const val NEW_PLAYER = "new_player"
        const val NOTE_WITHDRAWAL = "note_withdrawal"
        const val HAS_AUTO_TALKER_MESSAGE_ACTIVE = "has_auto_talker_message_active"
        const val IS_DYING = "is_dying"
        const val NEX_COUGH = "nex_cough";
        const val HAS_COMMUNE_EFFECT = "has_commune_effect"
        const val IS_MUTED = "is_muted"
        const val INVULNERABLE = "invulnerable"
        const val RANDOM_FORFEIT = "random_forfeit"
        const val IS_COOKING = "is_cooking"
        const val STALL_HITS = "stall_damage"
        const val IS_DRUNK = "is_drunk"
        const val IS_DEMON = "is_demon"
        const val IS_CRAB = "is_crab"
        const val IS_FLYING = "is_flying"
        const val IS_ANY_NPC = "is_any_npc"
        const val MUSIC_PLAYING = "music_playing"
        const val SMOKE_BOSS_WARNING = "smokeboss_warning"
        const val ZARYTE_CROSSBOW = "zaryte_crossbow_spec"

        const val FILLING_WATER_CONTAINERS = "filling_water_containers"
        const val PAID_BRIMHAVEN_FEE = "paid_brimhaven_fee"
        const val PAID_BRIMHAVEN_AGILITY_FEE = "paid_brimhaven_agility_fee"
        const val SEEN_WILDERNESS_WARNING = "seen_wilderness_warning"

        /*
        Instant attributes
         */

        @Value(InstantValueHolder::class) const val SQL_ACTION = "sql_action"
        @Value(InstantValueHolder::class) const val FREE_RUBY_MEMBER_RANK = "free_ruby_members_rank"
        @Value(InstantValueHolder::class) const val LAST_VOTE = "last_vote"
        @Value(InstantValueHolder::class) const val SEVER_EFFECT = "sever_effect"
        @Value(InstantValueHolder::class) const val FEAR_EFFECT = "fear_effect"
        @Value(InstantValueHolder::class) const val SPEAR_WALL_EFFECT = "spear_wall_effect"
        @Value(InstantValueHolder::class) const val SOTD_SPEC_EFFECT = "staff-of-dead-effect"
        @Value(InstantValueHolder::class) const val ANCIENT_WYVERN_SHIELD_EFFECT = "ancient_wyvern_shield_effect"
        @Value(InstantValueHolder::class) const val DRAGONFIRE_SHIELD_EFFECT = "dragonfire_shield_effect"
        @Value(InstantValueHolder::class) const val DRAGONFIRE_WARD_EFFECT = "dragonfire_ward_effect"
        @Value(InstantValueHolder::class) const val ROCK_CAKE_WAIT = "rock_cake_wait"
        @Value(InstantValueHolder::class) const val CORPOREAL_BEAST_ENTRANCE_TIMER = "corporeal_beast_entrance_timer"
        @Value(InstantValueHolder::class) const val BONECRUSHER_NECKLACE_WEAR_TIMER = "bonecrush_necklace_wear_timer"
        @Value(InstantValueHolder::class) const val DRAGONBONE_NECKLACE_WEAR_TIMER = "dragonbone_necklace_wear_timer"
        @Value(InstantValueHolder::class) const val IMBUED_HEART_TIMER = "imbued_heart_timer"
        @Value(InstantValueHolder::class) const val LAST_RANDOM_EVENT = "last_random_event"
        @Value(InstantValueHolder::class) const val LAST_ACHIEVMENT_COMPLETION = "last_achievment_completion"
        @Value(InstantValueHolder::class) const val LAST_LOGIN = "last_login"
        @Value(InstantValueHolder::class) const val LAST_CHAT = "last_chat"
        @Value(InstantValueHolder::class) const val LAST_NEWS = "last_news"
        @Value(InstantValueHolder::class) const val LAST_STUCK = "last_stuck"
        @Value(InstantValueHolder::class) const val LAST_LEVEL_UP = "last_level_up"
        @Value(InstantValueHolder::class) const val LAST_GODZILLA_ENTRY = "last_godzilla_entry"
        @Value(InstantValueHolder::class) const val LAST_GOD_ALTAR = "last_god_altar"
        @Value(InstantValueHolder::class) const val STREAK_HOURS_ELAPSED = "streak_hours_elapsed"
        @Value(InstantValueHolder::class) const val LAST_DAILY_FLAX = "last_daily_flax"
        @Value(InstantValueHolder::class) const val LAST_INFO_BROADCAST = "last_info_broadcast"
        @Value(InstantValueHolder::class) const val LAST_MAGIC_CAPE_RESET = "last_magic_cape_reset"
        @Value(InstantValueHolder::class) const val LAST_DEFENCE_CAPE_TELEPORT = "last_defence_cape_teleport"
        @Value(InstantValueHolder::class) const val LAST_RANDOM_MOVEMENT = "last_random_movement"

        const val DELAYED_MESSAGE_TIMER = "delayed_message_timer"
        const val LAST_DUPE_TIME = "last_dupe_time"
        const val LAST_PRAY = "last_pray"
        const val LAST_BUSH_PICKUP = "last_bush_pickup"
        const val LAST_COFFIN_USE = "last_coffin_use"
        const val LAST_REFRESH = "last_refresh"
        const val LAST_COMMAND = "last_command"
        const val LAST_ACTION_BUTTON = "last_action_button"
        const val LAST_MYSTERY_BOX_OPENING = "last_mystery_box_opening"
        const val LAST_DICE_BUTTON = "last_dice_button"
        const val MYSTERY_BOX_LAST_SPIN = "mystery_box_last_spin"
        const val CANNON_DEGRADE_TIMER = "cannon_degrade_timer"
        const val ENCHANT_SPELL_TIMER = "enchant_spell_timer"
        const val TRAVEL_ACTION = "travel_action"
        const val GENERIC_ACTION = "generic_action"
        const val TRADE_DELAYS_ALL = "trade_delays_all"
        const val DFS_CHARGE_TIMER = "dfs_charge_timer"
        const val WILDERNESS_BOSS_TELEPORT_TIMER = "wilderness_boss_teleport_timer"
        const val PORAZDIR_BOSS_TELEPORT_TIMER = "porazdir_boss_teleport_timer"
        const val LAST_SHOVE_STUN = "last_shove_stun"
        const val YELL_TIMER = "yell_timer"
        const val SPAWN_TIEMR = "spawn_timer"

        /*
         String attributes
         */
        @Value(StringValueHolder::class) const val DEBUG_TYPE = "debug_type"
        @Value(StringValueHolder::class) const val SELECTED_RANK = "selected_rank"
        @Value(StringValueHolder::class) const val XMAS_2020 = "christmas_2020"
        @Value(StringValueHolder::class) const val XMAS_2021 = "christmas_2021"
        @Value(StringValueHolder::class) const val XMAS_2022 = "christmas_2022"

        const val LAST_DELAYED_MESSAGE = "last_delayed_message"
        const val MESSAGE_TO_AUTO_TALK = "message_to_auto_talk"
        const val TEMP_MESSAGE_TO_AUTO_TALK = "temp_message_to_auto_talk"
        const val SKILL_GUIDE_SELECTED_SKILL = "skill_guide_selected_skill"
        const val COMBAT_LAMP_SELECTED_SKILL = "combat_lamp_selected_skill"

        /*
        Daily Tasks attributes
         */
        const val TASK_TYPE_ONE = "task_type_one"
        const val TASK_TYPE_TWO = "task_type_two"
        const val TASK_TYPE_THREE = "task_type_three"
        const val TASK_TYPE_FOUR = "task_type_four"
        const val TASK_ID_ONE = "task_id_one"
        const val TASK_ID_TWO = "task_id_two"
        const val TASK_ID_THREE = "task_id_three"
        const val TASK_ID_FOUR = "task_id_four"

        @Value(StringValueHolder::class) const val TASK_ID_ONE_T = "task_id_one_t"
        @Value(StringValueHolder::class) const val TASK_ID_TWO_T = "task_id_two_t"
        @Value(StringValueHolder::class) const val TASK_ID_THREE_T = "task_id_three_t"
        @Value(StringValueHolder::class) const val TASK_ID_FOUR_T = "task_id_four_t"

        const val TASK_AMOUNT_DONE_ONE = "task_amount_done_one"
        const val TASK_AMOUNT_DONE_TWO = "task_amount_done_two"
        const val TASK_AMOUNT_DONE_THREE = "task_amount_done_three"
        const val TASK_AMOUNT_DONE_FOUR = "task_amount_done_four"

        const val TASK_REWARD_ITEM_ID_ONE = "task_reward_item_id_one"
        const val TASK_REWARD_ITEM_ID_TWO = "task_reward_item_id_two"
        const val TASK_REWARD_ITEM_ID_THREE = "task_reward_item_id_three"
        const val TASK_REWARD_ITEM_ID_FOUR = "task_reward_item_id_four"

        const val TASK_REWARD_ITEM_AMOUNT_ONE = "task_reward_item_amount_one"
        const val TASK_REWARD_ITEM_AMOUNT_TWO = "task_reward_item_amount_two"
        const val TASK_REWARD_ITEM_AMOUNT_THREE = "task_reward_item_amount_three"
        const val TASK_REWARD_ITEM_AMOUNT_FOUR = "task_reward_item_amount_four"

        //@Value(BooleanValueHolder::class) const val GOLDEN_ZGS = "golden_zgs"
        @Value(BooleanValueHolder::class) const val TASK_CLAIMED_ONE = "task_claimed_one"
        @Value(BooleanValueHolder::class) const val TASK_CLAIMED_TWO = "task_claimed_two"
        @Value(BooleanValueHolder::class) const val TASK_CLAIMED_THREE = "task_claimed_three"
        @Value(BooleanValueHolder::class) const val TASK_CLAIMED_FOUR = "task_claimed_four"

        @Value(InstantValueHolder::class) const val TASK_DAILY_TIMER = "task_daily_timer"
        @Value(InstantValueHolder::class) const val TASK_WEEKLY_TIMER = "task_weekly_timer"

        @Value(InstantValueHolder::class) const val TASK_DAILY_TIMER_T = "task_daily_timer_t"
        @Value(InstantValueHolder::class) const val TASK_WEEKLY_TIMER_T = "task_weekly_timer_t"

        /*
         Card list attribute
         */
        @Value(CardsValueHolder::class) const val BLACKJACK_HAND = "blackjack_hand"

        /*
        Bird House List attribute
         */
        @Value(BirdHouseValueHolder::class) const val BIRDHOUSE_DATA = "birdhouse_data"

        /*
        Farming patches attribute
         */
        @Value(PatchValueHolder::class) const val FARMING_PATCHES = "farming_patches"

        /*
        Farming compost bins attribute
         */
        @Value(CompostBinValueHolder::class) const val FARMING_COMPOST_BINS = "farming_compost_bins"

        /*
        Weapon fight type configuration attribute
        */
        @Value(WeaponFightTypeMapValueHolder::class) const val WEAPON_FIGHT_TYPE_CONFIG = "weapon_fight_type_config"

        /*
         Motherlode mine attribute
         */
        @Value(MotherlodeMineValueHolder::class) const val MOTHERLODE_MINE = "motherlode_mine"

        /*
          Warriors guild attribute
         */
        @Value(WarriorsGuildValueHolder::class) const val WARRIORS_GUILD = "warriors_guild"

        /*
        Fight Cave attribute
         */
        @Value(FightCaveValueHolder::class) const val FIGHT_CAVE = "fight_cave"

        /*
         Blast Furnace attribute
        */
        @Value(BlastFurnaceValueHolder::class) const val BLAST_FURNACE = "blast_furnace"

        /*
        Aquais Neige attribute
         */
        @Value(AquaisNeigeValueHolder::class) const val AQUAIS_NEIGE = "aquais_neige"


    }
}
