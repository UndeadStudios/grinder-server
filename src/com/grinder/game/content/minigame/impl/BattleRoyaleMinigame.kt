package com.grinder.game.content.minigame.impl

import com.grinder.game.content.minigame.Minigame
import com.grinder.game.content.minigame.MinigameManager
import com.grinder.game.content.minigame.MinigameRestriction
import com.grinder.game.content.minigame.PublicMinigameHandler
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.grounditem.ItemOnGroundManager
import com.grinder.game.model.Boundary
import com.grinder.game.model.Position
import com.grinder.game.model.item.Item
import com.grinder.util.ItemID
import com.grinder.util.Misc
import java.util.*

/**
 * Represents the Battle Royale Minigame
 *
 * @author 2012
 */
class BattleRoyaleMinigame(vararg boundaries: Boundary) : Minigame(*boundaries) {

    override fun getName() = "Battle Royale"
    override fun getRestriction() = MinigameRestriction.NO_ITEMS
    override fun getUnuseablePrayer(): IntArray? = PrayerHandler.OVERHEAD_PRAYERS
    override fun hasRequirements(player: Player) = true
    override fun canUsePresets() = false
    override fun removeItems() = true
    override fun canUnEquip() = true
    override fun isSafeForHardcore() = true
    override fun isMulti(agent: Agent) = true
    override fun canTrade(player: Player, target: Player) = false
    override fun canEat(player: Player, itemId: Int) = true
    override fun canDrink(player: Player, itemId: Int) = true
    override fun handleDeath(npc: NPC) = false
    override fun handleDeath(player: Player, killer: Optional<Player>) = true
    override fun handleObjectClick(player: Player, obj: GameObject, actionType: Int): Boolean {

        if (MinigameManager.publicMinigame != this)
            return false

        if (obj.id == 27096) {
            PublicMinigameHandler.handleExitPortal(this, player)
            return true
        }
        return false
    }
    override fun dropItemsOnDeath(player: Player, killer: Optional<Player>) = true
    override fun onPlayerRightClick(player: Player, rightClicked: Player, option: Int) {}

    override fun getTimeUntilDangerous() = SAFE_ZONE_DURATION

    override fun enter(agent: Agent?) {
        super.enter(agent)
        PublicMinigameHandler.onEnterMinigameArea(this, agent?:return)
    }

    override fun leave(agent: Agent) {

        super.leave(agent)
        PublicMinigameHandler.onLeaveMinigameArea(this, agent)
    }

    override fun process(agent: Agent) {
       PublicMinigameHandler.onProcessMinigameArea(this, agent)
    }

    override fun canTeleport(player: Player) =
            PublicMinigameHandler.canTeleport(this, player)

    override fun canAttack(attacker: Agent?, target: Agent?) =
            PublicMinigameHandler.canAttack(this, attacker, target)

    override fun defeated(player: Player, agent: Agent) {

        if (MinigameManager.publicMinigame != this)
            return

        if (agent is Player) {
            if (players.contains(player)) {

                agent.message("You have been eliminated from the minigame by your opponent, better luck next time!")

                MinigameManager.rewardForParticipation(this, agent)
                MinigameManager.leaveMinigame(agent, this)

                if (players.size == 1) {
                    MinigameManager.wonMinigame(player, this)
                }
            }
        }
    }

    override fun start(player: Player) {

        player.inventory.addItems(TOOLS, true)
        player.packetSender.sendInteractionOption("null", 2, true)

        player.moveTo(START_POSITIONS.random())

        /*
        The amount of players determines how much items will be dropped on the floor upon starting.
         - the first spawn location is a big wide area counted as a square
         - the second spawn location is a smaller box
         - the third spawn location is a smaller box positioned vertically
           to spawn items in + 3X and +8Y coordinates
         The points are taken and calculated and should work perfectly fine like this.
         */
        for (i in 0 until (player.minigame.players.size * 2.75).toInt() + 5) {
            ItemOnGroundManager.registerGlobalDefaultDelay(player,
                    GROUND_ITEMS_SPAWNS[Misc.getRandomInclusive(GROUND_ITEMS_SPAWNS.size - 1)],
                    ITEM_SPAWNS_LOCATION_1.clone().add(Misc.getRandomInclusive(25), Misc.getRandomInclusive(9)))
        }
        for (i in 0 until (player.minigame.players.size * 2.5).toInt() + 5) {
            ItemOnGroundManager.registerGlobalDefaultDelay(player,
                    GROUND_ITEMS_SPAWNS[Misc.getRandomInclusive(GROUND_ITEMS_SPAWNS.size - 1)],
                    ITEM_SPAWNS_LOCATION_2.clone().add(Misc.getRandomInclusive(8), Misc.getRandomInclusive(5)))
        }
        for (i in 0 until (player.minigame.players.size * 2.5).toInt() + 5) {
            ItemOnGroundManager.registerGlobalDefaultDelay(player,
                    GROUND_ITEMS_SPAWNS[Misc.getRandomInclusive(GROUND_ITEMS_SPAWNS.size - 1)],
                    ITEM_SPAWNS_LOCATION_3.clone().add(Misc.getRandomInclusive(5), Misc.getRandomInclusive(8)))
        }
        for (i in 0 until (player.minigame.players.size * 2.5).toInt() + 5) {
            ItemOnGroundManager.registerGlobalDefaultDelay(player,
                    GROUND_ITEMS_SPAWNS[Misc.getRandomInclusive(GROUND_ITEMS_SPAWNS.size - 1)],
                    ITEM_SPAWNS_LOCATION_4.clone().add(Misc.getRandomInclusive(9), Misc.getRandomInclusive(4)))
        }
        for (i in 0 until (player.minigame.players.size * 2.5).toInt() + 5) {
            ItemOnGroundManager.registerGlobalDefaultDelay(player,
                    GROUND_ITEMS_SPAWNS[Misc.getRandomInclusive(GROUND_ITEMS_SPAWNS.size - 1)],
                    ITEM_SPAWNS_LOCATION_5.clone().add(Misc.getRandomInclusive(11), Misc.getRandomInclusive(6)))
        }
        player.message("You have 30 seconds to gather as much resources before it becomes a danger zone!")
        player.message("@red@ATTENTION: @bla@The items have been spawned randomly across the whole island!")
    }


    companion object {

        /**
         * The start position
         */
        val START_POSITIONS = arrayOf(
                Position(1757, 3432), Position(1741, 3416),
                Position(1770, 3448), Position(1779, 3433),
                Position(1787, 3406), Position(1763, 3403)
        )

        /**
         * The item spawns
         */
        private val ITEM_SPAWNS_LOCATION_1 = Position(1752, 3403, 0)
        private val ITEM_SPAWNS_LOCATION_2 = Position(1782, 3405, 0)
        private val ITEM_SPAWNS_LOCATION_3 = Position(1777, 3425, 0)
        private val ITEM_SPAWNS_LOCATION_4 = Position(1766, 3445, 0)
        private val ITEM_SPAWNS_LOCATION_5 = Position(1733, 3416, 0)

        /**
         * The waiting time
         */
        private const val SAFE_ZONE_DURATION = 60

        /**
         * The initial tools
         */
        private val TOOLS = arrayOf(
                Item(ItemID.BRONZE_SWORD), Item(ItemID.WOODEN_SHIELD),
            Item(ItemID.AIR_RUNE, 2500), Item(ItemID.WATER_RUNE, 2500),
            Item(ItemID.EARTH_RUNE, 2500),
            Item(ItemID.FIRE_RUNE, 2500),
            Item(ItemID.CHAOS_RUNE, 2500),
            Item(ItemID.DEATH_RUNE, 2500),
            Item(ItemID.BLOOD_RUNE, 2500),
            Item(ItemID.WRATH_RUNE, 2500),
            Item(ItemID.SOUL_RUNE, 2500),
            Item(ItemID.NATURE_RUNE, 2500),
            Item(ItemID.REVENANT_ETHER, 2500),
            )

        /**
         * The ground items spawned
         */
        private val GROUND_ITEMS_SPAWNS = arrayOf(



            // New items
            Item(ItemID.TORVA_FULL_HELM),
            Item(ItemID.TORVA_PLATEBODY),
            Item(ItemID.TORVA_PLATELEGS),
            Item(ItemID.TORVA_WHIP),
            Item(ItemID.ZARYTE_CROSSBOW),
            Item(ItemID.ZARYTE_VAMBRACES),
            Item(ItemID.VOLATILE_NIGHTMARE_STAFF),
            Item(ItemID.HARMONISED_NIGHTMARE_STAFF),
            Item(ItemID.ELDRITCH_NIGHTMARE_STAFF),
            Item(ItemID.ANCIENT_GODSWORD),
            Item(ItemID.DRAGON_CLAWS_OR),
            Item(ItemID.DRAGON_WARHAMMER_OR),
            Item(ItemID.HEAVY_BALLISTA_OR),
            Item(ItemID.TZKAL_SLAYER_HELMET_I),
            Item(ItemID.TZTOK_SLAYER_HELMET_I),
            Item(ItemID.VAMPYRIC_SLAYER_HELMET_I),
            Item(ItemID.INFERNAL_CAPE),
            Item(ItemID.DRAGON_BOOTS),
            Item(ItemID.VERACS_ARMOUR_SET),
            Item(ItemID.KARILS_ARMOUR_SET),
            Item(ItemID.AHRIMS_ARMOUR_SET),
            Item(ItemID.TORAGS_ARMOUR_SET),
            Item(15210),
            Item(15211),
            Item(15212),
            Item(15216),
            Item(15217),
            Item(15218),
            Item(15219),
            Item(15155),
            Item(15153),
            Item(15156),
            Item(15158),


            // End of new items
                Item(ItemID.AMULET_OF_STRENGTH),
                Item(ItemID.CLIMBING_BOOTS),
                Item(7462),
                Item(4587),
                Item(4151),
                Item(1089),
                Item(1127),
                Item(565, 100),
                Item(560, 100),
                Item(555, 300),
                Item(15890, 1),
                Item(ItemID.MONKFISH, 1),
            Item(ItemID.SHARK, 1),
            Item(ItemID.MONKFISH, 1),
            Item(ItemID.SHARK, 1),
            Item(ItemID.MONKFISH, 1),
            Item(ItemID.SHARK, 1),
            Item(ItemID.MONKFISH, 1),
            Item(ItemID.SHARK, 1),
            Item(ItemID.MONKFISH, 1),
                Item(2440),
                Item(ItemID.BASS, 1),
            Item(ItemID.BASS, 1),
            Item(ItemID.BASS, 1),
            Item(ItemID.BASS, 1),
            Item(ItemID.BASS, 1),
            Item(15890, 1),
            Item(15890, 1),
            Item(15890, 1),
            Item(15890, 1),
            Item(15890, 1),
            Item(15890, 1),
            Item(15890, 1),
            Item(15890, 1),

                Item(ItemID.MONKFISH, 1),
            Item(ItemID.MONKFISH, 1),
            Item(ItemID.MONKFISH, 1),
            Item(ItemID.MONKFISH, 1),
                Item(ItemID.AIR_RUNE, 20),
                Item(ItemID.LOBSTER, 1),
                Item(ItemID.LOBSTER, 1),
                Item(ItemID.LOBSTER, 1),
                Item(ItemID.LOBSTER, 1),
                Item(ItemID.LOBSTER, 1),
                Item(ItemID.AMULET_OF_STRENGTH),
                Item(ItemID.FIRE_CAPE),
                Item(ItemID.CLIMBING_BOOTS),
                Item(7462),
                Item(ItemID.MANTA_RAY, 1),
            Item(ItemID.MANTA_RAY, 1),
            Item(ItemID.MANTA_RAY, 1),
            Item(ItemID.MANTA_RAY, 1),
            Item(ItemID.MANTA_RAY, 1),
                Item(3140, 1),
                Item(ItemID.MONKFISH, 1),
                Item(ItemID.MONKFISH, 1),
                Item(ItemID.MONKFISH, 1),
            Item(ItemID.MONKFISH, 1),
            Item(ItemID.MONKFISH, 1),
            Item(ItemID.MONKFISH, 1),
            Item(ItemID.MONKFISH, 1),
            Item(ItemID.MONKFISH, 1),
            Item(ItemID.MONKFISH, 1),
                Item(ItemID.COOKED_KARAMBWAN, 1),
                Item(ItemID.COOKED_KARAMBWAN, 1),
            Item(ItemID.COOKED_KARAMBWAN, 1),
            Item(ItemID.COOKED_KARAMBWAN, 1),
            Item(ItemID.COOKED_KARAMBWAN, 1),
            Item(ItemID.COOKED_KARAMBWAN, 1),
            Item(ItemID.COOKED_KARAMBWAN, 1),
            Item(ItemID.COOKED_KARAMBWAN, 1),
            Item(ItemID.COOKED_KARAMBWAN, 1),
            Item(ItemID.COOKED_KARAMBWAN, 1),
            Item(ItemID.COOKED_KARAMBWAN, 1),
            Item(ItemID.COOKED_KARAMBWAN, 1),
            Item(ItemID.COOKED_KARAMBWAN, 1),
            Item(ItemID.COOKED_KARAMBWAN, 1),
            Item(ItemID.COOKED_KARAMBWAN, 1),
            Item(ItemID.COOKED_KARAMBWAN, 1),
                Item(ItemID.DHAROKS_GREATAXE),
                Item(ItemID.IRON_2H_SWORD),
                Item(ItemID.STEEL_AXE),
                Item(ItemID.BONE_DAGGER),
                Item(ItemID.BLACK_BATTLEAXE),
                Item(ItemID.MITHRIL_LONGSWORD),
                Item(ItemID.MITHRIL_WARHAMMER),
                Item(ItemID.ADAMANT_MACE),
                Item(ItemID.RUNE_SCIMITAR),
                Item(ItemID.RUNE_HALBERD),
                Item(ItemID.DRAGON_AXE),
                Item(ItemID.DRAGON_2H_SWORD),
                Item(ItemID.DRAGON_DAGGER),
                Item(ItemID.DRAGON_MACE),
                Item(ItemID.DRAGON_LONGSWORD),
                Item(ItemID.DRAGON_WHIP),
                Item(ItemID.DRAGON_GODSWORD),
                Item(ItemID.DRAGON_SCIMITAR),
                Item(ItemID.GUTHANS_WARSPEAR),
                Item(ItemID.TZHAAR_KET_OM),
                Item(ItemID.TORAGS_HAMMERS),
                Item(ItemID.GRANITE_MAUL),
                Item(ItemID.BARRELCHEST_ANCHOR),
                Item(ItemID.LAVA_BLADE),
                Item(3140, 1),
                Item(22284, 1),
                Item(2497, 1),
                Item(10828, 1),
                Item(12831, 1),
                Item(19994, 1),
                Item(12610, 1),
                Item(11284, 1),
                Item(6737, 1),
                Item(12825, 1),
                Item(1187, 1),
                Item(1149, 1),
                Item(4087, 1),
                Item(21892, 1),
                Item(ItemID.SARADOMIN_BREW_4_, 1),
                Item(ItemID.SARADOMIN_BREW_4_, 1),
                Item(ItemID.SARADOMIN_BREW_4_, 1),
            Item(ItemID.SARADOMIN_BREW_4_, 1),
            Item(ItemID.SARADOMIN_BREW_4_, 1),
            Item(ItemID.SARADOMIN_BREW_4_, 1),
            Item(ItemID.SARADOMIN_BREW_4_, 1),
                Item(ItemID.DHAROKS_GREATAXE),
                Item(ItemID.IRON_2H_SWORD),
                Item(ItemID.STEEL_AXE),
                Item(ItemID.BONE_DAGGER),
                Item(ItemID.BLACK_BATTLEAXE),
                Item(ItemID.MITHRIL_LONGSWORD),
                Item(ItemID.MITHRIL_WARHAMMER),
                Item(ItemID.ADAMANT_MACE),
                Item(ItemID.RUNE_SCIMITAR),
                Item(ItemID.RUNE_HALBERD),
                Item(ItemID.DRAGON_AXE),
                Item(ItemID.DRAGON_2H_SWORD),
                Item(ItemID.DRAGON_DAGGER),
                Item(ItemID.DRAGON_MACE),
                Item(ItemID.DRAGON_LONGSWORD),
                Item(ItemID.DRAGON_WHIP),
                Item(ItemID.DRAGON_GODSWORD),
                Item(ItemID.DRAGON_SCIMITAR),
                Item(ItemID.GUTHANS_WARSPEAR),
                Item(ItemID.EXCALIBUR),
                Item(ItemID.SARADOMIN_BREW_4_, 1),
                Item(ItemID.SARADOMIN_BREW_4_, 1),
                Item(ItemID.SUPER_RESTORE_4_, 1),
                Item(ItemID.SUPER_RESTORE_4_, 1),
            Item(ItemID.SUPER_RESTORE_4_, 1),
            Item(ItemID.SUPER_RESTORE_4_, 1),
            Item(ItemID.SUPER_RESTORE_4_, 1),
            Item(ItemID.SUPER_RESTORE_4_, 1),
            Item(ItemID.SUPER_RESTORE_4_, 1),
            Item(ItemID.SUPER_RESTORE_4_, 1),
            Item(ItemID.SUPER_RESTORE_4_, 1),
            Item(ItemID.SUPER_RESTORE_4_, 1),
            Item(ItemID.SUPER_RESTORE_4_, 1),
            Item(ItemID.SUPER_RESTORE_4_, 1),
            Item(ItemID.SUPER_RESTORE_4_, 1),
            Item(ItemID.SUPER_RESTORE_4_, 1),
            Item(ItemID.SUPER_RESTORE_4_, 1),
            Item(ItemID.SUPER_RESTORE_4_, 1),
                Item(ItemID.TZHAAR_KET_OM),
                Item(ItemID.TORAGS_HAMMERS),
                Item(ItemID.GRANITE_MAUL),
                Item(ItemID.GHRAZI_RAPIER),
                Item(22486),  // Scythe vitur
                Item(ItemID.DRAGON_CLAWS),
                Item(21003),  // Elder maul
                Item(ItemID.BANDOS_GODSWORD),
                Item(ItemID.SARADOMIN_SWORD),
                Item(ItemID.SARADOMIN_GODSWORD),
                Item(ItemID.ZAMORAK_GODSWORD),
                Item(ItemID.DRAGON_SWORD),
                Item(ItemID.DRAGON_BATTLEAXE),
                Item(ItemID.ARCLIGHT),
                Item(ItemID.DRAGON_WARHAMMER),
                Item(ItemID._3RD_AGE_LONGSWORD),
                Item(ItemID.ABYSSAL_BLUDGEON),
                Item(15153),  // Indigo whip
                Item(15156),  // Gold whip
                Item(ItemID.LAVA_BLADE),  // Ranged weapons
                Item(ItemID.MAGIC_SHORTBOW),
                Item(ItemID.RUNE_CROSSBOW),
                Item(ItemID.ARMADYL_CROSSBOW),
                Item(ItemID.DRAGON_HUNTER_CROSSBOW),
                Item(ItemID.SEERCULL),
                Item(11235),  // Dark bow
                Item(20997),  // Twisted bow
                Item(ItemID.HUNTERS_CROSSBOW),
                Item(ItemID._3RD_AGE_BOW),
                Item(ItemID.KARILS_CROSSBOW),
                Item(ItemID.SUPER_RESTORE_4_, 1),
                Item(ItemID.SUPER_RESTORE_4_, 1),
                Item(ItemID.SUPER_RESTORE_4_, 1),
                Item(ItemID.TOXIC_BLOWPIPE),
                Item(19481),  // Heavy ballista
                Item(ItemID.NEW_CRYSTAL_BOW),  // Magic weapons
                Item(ItemID._3RD_AGE_AMULET),
                Item(15021),  // Kodai wand
                Item(ItemID.SLAYERS_STAFF),
                Item(ItemID.IBANS_STAFF),
                Item(ItemID.ANCIENT_STAFF),
                Item(ItemID.STAFF_OF_LIGHT),
                Item(ItemID.STAFF_OF_THE_DEAD),
                Item(ItemID.MASTER_WAND),  //new Item(22323), // Sanguinesti staff
                //new Item(22555),
                Item(ItemID.LUNAR_STAFF),
                Item(ItemID.MYSTIC_MUD_STAFF),
                Item(ItemID.ZAMORAK_STAFF),
                Item(ItemID.AIR_BATTLESTAFF),
                Item(ItemID.AMULET_OF_STRENGTH),
                Item(ItemID.FIRE_CAPE),
                Item(ItemID.CLIMBING_BOOTS),
                Item(ItemID.BARROWS_GLOVES),
                Item(ItemID.DRAGON_CHAINBODY_2),
                Item(ItemID.BLACK_D_HIDE_SHIELD),
                Item(ItemID.BLACK_DHIDE_CHAPS),
                Item(ItemID.HELM_OF_NEITIZNOT),
                Item(ItemID.BLESSED_SPIRIT_SHIELD),
                Item(ItemID.RANGER_GLOVES),
                Item(ItemID.BERSERKER_NECKLACE),
                Item(ItemID.BOOK_OF_LAW),
                Item(ItemID.UNHOLY_BOOK),
                Item(ItemID.BOOK_OF_BALANCE),
                Item(ItemID.DRAGONFIRE_SHIELD_2),
                Item(ItemID.BERSERKER_RING),
                Item(ItemID.ARCHERS_RING),
                Item(ItemID.SEERS_RING),
                Item(ItemID.DRAGON_DEFENDER),
                Item(12821),  // Spectral spirit shield
                Item(12825),  // Arcane spirit shield
                //new Item(ItemIdentifiers.ELYSIAN_SPIRIT_SHIELD),
                Item(ItemID.SHARK, 1),
                Item(ItemID.SHARK, 1),
                Item(ItemID.SHARK, 1),
                Item(ItemID.SHARK, 1),
                Item(ItemID.DRAGON_SQ_SHIELD),
                Item(ItemID.DRAGON_MED_HELM),
                Item(ItemID.DRAGON_PLATELEGS),
                Item(ItemID.DRAGON_PLATEBODY),
                Item(ItemID.VIGGORA_CHAINMACE),
                Item(ItemID.DRAGON_HUNTER_LANCE),  // Magic sets
                Item(ItemID.RUNE_FULL_HELM),
                Item(ItemID.RUNE_PLATEBODY),
                Item(ItemID.RUNE_KITESHIELD),
                Item(ItemID.RUNE_PLATELEGS),
                Item(ItemID.DRAGON_PLATESKIRT),
                Item(ItemID.PROSELYTE_CUISSE),
                Item(ItemID.PROSELYTE_HAUBERK),
                Item(ItemID.PROSELYTE_SALLET),
                Item(ItemID.BRONZE_BOOTS),
                Item(ItemID.STEEL_BOOTS),
                Item(ItemID.MITHRIL_BOOTS),
                Item(ItemID.RUNE_BOOTS),
                Item(ItemID.DRAGON_BOOTS),
                Item(ItemID.BRONZE_GLOVES),
                Item(ItemID.STEEL_GLOVES),
                Item(ItemID.BLACK_GLOVES),
                Item(ItemID.RUNE_GLOVES),
                Item(ItemID.AMULET_OF_STRENGTH),
                Item(ItemID.AMULET_OF_DEFENCE),
                Item(ItemID.AMULET_OF_ACCURACY),
                Item(ItemID.AMULET_OF_POWER),
                Item(ItemID.AMULET_OF_FURY),
                Item(ItemID.FISHBOWL_HELMET),
                Item(ItemID.DIVING_APPARATUS),
                Item(ItemID.WARRIOR_HELM),
                Item(ItemID.ARCHER_HELM),
                Item(ItemID.BERSERKER_HELM),
                Item(ItemID.AVAS_ACCUMULATOR),
                Item(ItemID.DRAGON_HUNTER_LANCE),
                Item(ItemID.KARILS_ARMOUR_SET),
                Item(ItemID.DHAROKS_ARMOUR_SET),
                Item(ItemID.AHRIMS_ARMOUR_SET),
                Item(ItemID.VERACS_ARMOUR_SET),
                Item(ItemID.VOID_MAGE_HELM),
                Item(ItemID.VOID_MELEE_HELM),
                Item(ItemID.VOID_RANGER_HELM),
                Item(ItemID.VOID_KNIGHT_GLOVES),
                Item(ItemID.VOID_KNIGHT_ROBE),
                Item(ItemID.VOID_KNIGHT_TOP),
                Item(ItemID.MYSTIC_HAT),
                Item(ItemID.MYSTIC_GLOVES),
                Item(ItemID.MYSTIC_BOOTS),
                Item(ItemID.MYSTIC_ROBE_BOTTOM),
                Item(ItemID.MYSTIC_ROBE_TOP),
                Item(ItemID.SARADOMIN_CAPE),
                Item(ItemID.ZAMORAK_CAPE),
                Item(ItemID.GUTHIX_CAPE),
                Item(ItemID.ELEMENTAL_SHIELD),
                Item(ItemID.FARSEER_HELM),
                Item(ItemID.XERICIAN_HAT),
                Item(ItemID.XERICIAN_ROBE),
                Item(ItemID.XERICIAN_TOP),
                Item(ItemID.MAGES_BOOK),
                Item(ItemID.ZAMORAK_ROBE_3),
                Item(ItemID.ZAMORAK_ROBE),
                Item(ItemID.GREEN_DHIDE_BODY),
                Item(ItemID.GREEN_DHIDE_CHAPS),
                Item(ItemID.GREEN_DHIDE_VAMB),
                Item(ItemID.BLACK_DHIDE_BODY),
                Item(ItemID.BLACK_DHIDE_VAMB),
                Item(ItemID.ZAMORAK_HALO),
                Item(ItemID.SARADOMIN_HALO),
                Item(ItemID.GUTHIX_HALO),
                Item(ItemID.HAM_BOOTS),
                Item(ItemID.HAM_HOOD),
                Item(ItemID.HAM_ROBE),
                Item(ItemID.HAM_SHIRT),
                Item(ItemID.HAM_CLOAK),
                Item(ItemID.HAM_GLOVES),
                Item(ItemID.BROODOO_SHIELD),
                Item(ItemID.BROODOO_SHIELD_2),
                Item(ItemID.BROODOO_SHIELD_3),
                Item(ItemID.BROODOO_SHIELD_4),
                Item(ItemID.BROODOO_SHIELD_5),
                Item(ItemID.GRAAHK_HEADDRESS),
                Item(ItemID.GRAAHK_LEGS),
                Item(ItemID.GRAAHK_TOP),
                Item(ItemID.KYATT_HAT),
                Item(ItemID.KYATT_LEGS),
                Item(ItemID.KYATT_TOP),
                Item(ItemID.OBSIDIAN_HELMET),
                Item(ItemID.OBSIDIAN_PLATEBODY),
                Item(ItemID.OBSIDIAN_PLATELEGS),
                Item(ItemID.TWISTED_BUCKLER),
                Item(ItemID.WIZARD_BOOTS),
                Item(ItemID.RANGER_BOOTS),
                Item(ItemID.FIGHTER_TORSO),
                Item(ItemID.BANDOS_TASSETS),
                Item(ItemID.BANDOS_CHESTPLATE),
                Item(ItemID.ARMADYL_HELMET),
                Item(ItemID.ARMADYL_PLATEBODY),
                Item(ItemID.ARMADYL_PLATELEGS),
                Item(ItemID.PRIMORDIAL_BOOTS),
                Item(ItemID.ETERNAL_BOOTS),
                Item(ItemID.PEGASIAN_BOOTS),
                Item(ItemID.ANCESTRAL_HAT),
                Item(ItemID.ANCESTRAL_ROBE_TOP),
                Item(ItemID.ANCESTRAL_ROBE_BOTTOM),
                Item(ItemID.INFINITY_BOOTS),
                Item(ItemID.INFINITY_BOTTOMS),
                Item(ItemID.INFINITY_GLOVES),
                Item(ItemID.INFINITY_HAT),
                Item(ItemID.INFINITY_TOP),
                Item(ItemID.SHARK, 5),
                Item(ItemID.NEW_CRYSTAL_SHIELD),
                Item(ItemID.IMBUED_HEART),
                Item(ItemID.OCCULT_NECKLACE),
                Item(ItemID.SHARK, 5),
                Item(ItemID.TREASONOUS_RING),
                Item(ItemID.TYRANNICAL_RING),
                Item(ItemID.RING_OF_THE_GODS),
                Item(ItemID.SHARK, 3),
                Item(ItemID.OBSIDIAN_HELMET),
                Item(ItemID.OBSIDIAN_PLATEBODY),
                Item(ItemID.OBSIDIAN_PLATELEGS),
                Item(ItemID.RUNE_ARROW, 100),
                Item(ItemID.RUNE_ARROW, 200),
                Item(ItemID.AMETHYST_ARROW, 50),
                Item(ItemID.DRAGON_ARROW, 100),
                Item(ItemID.DRAGON_ARROW, 150),
                Item(ItemID.DRAGON_DART, 100),
                Item(ItemID.DRAGON_BOLTS_E_, 50),
                Item(ItemID.ONYX_BOLTS_E_, 50),
                Item(ItemID.EMERALD_BOLTS_E_, 250),
                Item(ItemID.DIAMOND_BOLTS_E_, 150),
                Item(ItemID.DRAGON_JAVELIN, 150),
                Item(ItemID.DRAGON_THROWNAXE, 100),
            Item(ItemID.AMETHYST_ARROW_P_PLUS_PLUS_, 100),
                Item(ItemID.BOLT_RACK, 205),
                Item(ItemID.RUNE_KNIFE, 205),
                Item(ItemID.TOKTZ_XIL_UL, 150),
                Item(22634, 50),  // Morrigan's throwing axe
                Item(ItemID.AMETHYST_JAVELIN, 100),  // RUNES
                Item(ItemID.AIR_RUNE, 250),
                Item(ItemID.WATER_RUNE, 250),
                Item(ItemID.EARTH_RUNE, 250),
                Item(ItemID.FIRE_RUNE, 250),
                Item(ItemID.CHAOS_RUNE, 135),
                Item(ItemID.DEATH_RUNE, 130),
                Item(ItemID.BLOOD_RUNE, 120),
                Item(ItemID.ASTRAL_RUNE, 110),
                Item(ItemID.NATURE_RUNE, 250),
                Item(ItemID.WRATH_RUNE, 50),  // Wrath runes
                Item(ItemID.SHARK, 1),
                Item(ItemID.SHARK, 1),
                Item(ItemID.SHARK, 1),
                Item(22978, 1))
    }
}