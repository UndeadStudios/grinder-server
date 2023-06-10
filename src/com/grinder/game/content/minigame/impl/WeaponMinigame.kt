package com.grinder.game.content.minigame.impl

import com.grinder.game.content.minigame.Minigame
import com.grinder.game.content.minigame.MinigameManager
import com.grinder.game.content.minigame.MinigameRestriction
import com.grinder.game.content.minigame.PublicMinigameHandler
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler
import com.grinder.game.entity.`object`.GameObject
import com.grinder.game.entity.agent.Agent
import com.grinder.game.entity.agent.movement.MovementStatus
import com.grinder.game.entity.agent.npc.NPC
import com.grinder.game.entity.agent.player.Player
import com.grinder.game.entity.agent.player.addInventoryItem
import com.grinder.game.entity.agent.player.message
import com.grinder.game.entity.passedTime
import com.grinder.game.model.Animation
import com.grinder.game.model.Boundary
import com.grinder.game.model.EffectTimer
import com.grinder.game.model.Position
import com.grinder.game.model.attribute.Attribute
import com.grinder.game.model.attribute.AttributeManager.Points
import com.grinder.game.model.item.Item
import com.grinder.game.model.item.container.ItemContainerUtil
import com.grinder.game.task.TaskManager
import com.grinder.util.ItemID
import com.grinder.util.Misc
import java.util.*
import kotlin.random.Random

/**
 * Represents the weapons minigame.
 *
 * @author 2012
 */
class WeaponMinigame(vararg boundaries: Boundary) : Minigame(*boundaries) {

    override fun getName() = "Weapon Game"
    override fun hasRequirements(player: Player) = true
    override fun getRestriction() = MinigameRestriction.NO_ITEMS
    override fun getUnuseablePrayer(): IntArray? = PrayerHandler.PROTECTION_PRAYERS
    override fun canUsePresets() = false
    override fun removeItems() = true
    override fun canUnEquip() = true
    override fun handleDeath(npc: NPC) = false

    override fun start(player: Player) {

        player.packetSender.sendInteractionOption("Attack", 2, true)
        player.packetSender.sendWalkableInterface(197)

        player.moveTo(POSITIONS.random())
        player.addInventoryItem(Item(ItemID.IRON_DAGGER, 1), -1)
        player.addInventoryItem(Item(ItemID.SHORTBOW, 1), -1)
        player.addInventoryItem(WEAPONS.random(), 0)
        player.addInventoryItem(CRATE_SUPPLIES.random(), 0)
        player.addInventoryItem(SUPPLIES.random(), 0)

        // Bonus 10 minutes experience for anyone who plays the minigame! It can also be extended!
        player.votingBonusTimer.extendOrStart(600)
        player.packetSender.sendEffectTimer(player.votingBonusTimer.secondsRemaining(), EffectTimer.VOTING_BONUS)

        // Send jinglebit
        player.packetSender.sendJinglebitMusic(238, 0)

        upgradeWeapon(player)
    }

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

    override fun defeated(player: Player, agent: Agent) {}
    override fun canTrade(player: Player, target: Player) = false
    override fun isMulti(agent: Agent) = true
    override fun canEat(player: Player, itemId: Int) = true
    override fun canDrink(player: Player, itemId: Int) = true
    override fun dropItemsOnDeath(player: Player, killer: Optional<Player>) = false

    override fun handleDeath(player: Player, killer: Optional<Player>): Boolean {

        if (MinigameManager.publicMinigame != this)
            return false

        if (killer.isPresent) {

            val killerPlayer = killer.get()

            player.moveTo(POSITIONS.random())
            player.combat.reset(true)

            if (!players.contains(killerPlayer))
                return true

            killerPlayer.points.increase(Points.WEAPON_MINIGAME)

            if (killerPlayer.points[Points.WEAPON_MINIGAME] == WINNING_SCORE) {
                players ?.filterNotNull()
                        ?.filterNot { it !== killerPlayer  }
                        ?.forEach {
                            it.points.increase(Points.MINIGAME_POINTS, Random.nextInt(15, 25))
                        }
                MinigameManager.wonMinigame(killerPlayer, this)
                return true
            }

            upgradeWeapon(killerPlayer)

            val supplies = (0 until Random.nextInt(1, 3)).map { SUPPLIES.random() }
            for (supply in supplies)
                ItemContainerUtil.addOrDrop(killerPlayer.inventory, player, supply)

            ItemContainerUtil.addOrDrop(killerPlayer.inventory, player, CONSUMABLE_SUPPLIES.random())

            player.points.increase(Points.MINIGAME_DEATH_STREAK)

            killerPlayer.points[Points.MINIGAME_DEATH_STREAK] = 0
        }
        return true
    }

    override fun onPlayerRightClick(player: Player, rightClicked: Player, option: Int) {}

    override fun handleObjectClick(player: Player, obj: GameObject, actionType: Int): Boolean {

        if (MinigameManager.publicMinigame != this)
            return false

        when (obj.id) {
            32187, 32190, 32057, 33016 -> {

                if (!player.passedTime(Attribute.GENERIC_ACTION, 1, message = false)){
                    player.message("You must wait for one second before searching again.")
                    return false
                }

                if (player.inventory.countFreeSlots() < 1) {
                    player.message("You don't have enough inventory space.")
                    return false
                }

                player.motion.update(MovementStatus.DISABLED)
                player.performAnimation(Animation(832, 5))
                player.message("You open and search the crates...")
                TaskManager.submit(2) {
                    player.motion.update(MovementStatus.NONE)
                    if (player.minigame is WeaponMinigame) {
                        val crateSupplies = CRATE_SUPPLIES[Misc.getRandomInclusive(CRATE_SUPPLIES.size - 1)]
                        player.addInventoryItem(crateSupplies)
                        player.message("You've found " + crateSupplies.amount + " x " + crateSupplies.definition.name + ".")
                    } else {
                        player.message("You don't find anything valuable.")
                    }
                }
            }
            32144 -> player.message("Coming soon...")
            27096 -> PublicMinigameHandler.handleExitPortal(this, player)
        }
        return true
    }

    companion object {

        var ENABLED = true

        private const val WINNING_SCORE = 15

        /**
         * Adds or drops a random weapon from [WEAPONS].
         *
         * @param player the [Player] to upgrade the weapon for.
         */
        private fun upgradeWeapon(player: Player) {
            ItemContainerUtil.addOrDrop(player.inventory, player, WEAPONS.random())
            MinigameManager.displayWeapon(player)
        }

        /**
         * The weapons
         */
        private val WEAPONS = arrayOf( // Melee weapons


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

            Item(15155),
            Item(15153),
            Item(15156),
            Item(15158),

                Item(ItemID.DHAROKS_GREATAXE),
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
                //Item(ItemID.ABYSSAL_TENTACLE),
                Item(ItemID.TZHAAR_KET_OM),
                Item(ItemID.TORAGS_HAMMERS),
                Item(ItemID.GRANITE_MAUL),
                Item(ItemID.GHRAZI_RAPIER),
                Item(22486),  // Scythe vitur
                Item(ItemID.DRAGON_CLAWS),
                Item(15020),  // Elder maul
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
                //Item(22547),  // Craws bow
                Item(20408),  // Dark bow
                Item(15023),  // Twisted bow
                Item(ItemID.HUNTERS_CROSSBOW),
                Item(ItemID._3RD_AGE_BOW),
                Item(ItemID.KARILS_CROSSBOW),
                Item(ItemID.TOXIC_BLOWPIPE_EMPTY_),
                Item(15022),  // Heavy ballista
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
                Item(ItemID.AIR_BATTLESTAFF)
        )

        /**
         * The random supplies given after a kill
         */
        private val SUPPLIES = arrayOf(

                // New Items
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
                Item(ItemID.DRAGON_SQ_SHIELD),
                Item(ItemID.DRAGON_MED_HELM),
                Item(ItemID.DRAGON_PLATELEGS),
                Item(ItemID.DRAGON_PLATEBODY),
                //Item(ItemID.VIGGORA_CHAINMACE),
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
                Item(ItemID.NEW_CRYSTAL_SHIELD),
                Item(ItemID.IMBUED_HEART),
                Item(ItemID.OCCULT_NECKLACE),
                Item(ItemID.TREASONOUS_RING),
                Item(ItemID.TYRANNICAL_RING),
                Item(ItemID.RING_OF_THE_GODS),
                Item(ItemID.OBSIDIAN_HELMET),
                Item(ItemID.OBSIDIAN_PLATEBODY),
                Item(ItemID.OBSIDIAN_PLATELEGS)
        )

        /**
         * The random supplies given after a kill
         */
        private val CRATE_SUPPLIES = arrayOf( // RANGED AMMUNITION
                //new Item(ItemIdentifiers.RUNE_ARROW, 10),
                Item(ItemID.RUNE_ARROW_P_PLUS_PLUS_, 50),
                Item(ItemID.AMETHYST_ARROW, 40),  //new Item(ItemIdentifiers.DRAGON_ARROW, 10),
                Item(ItemID.AMETHYST_ARROW_P_PLUS_PLUS_, 25),  //new Item(ItemIdentifiers.DRAGON_ARROW, 10),
                Item(ItemID.DRAGON_ARROW_P_PLUS_PLUS_, 25),
                Item(ItemID.DRAGON_DART, 25),
                Item(ItemID.ZULRAHS_SCALES, 50),
                Item(ItemID.DRAGON_BOLTS_E_, 25),
                Item(ItemID.RUBY_DRAGONBOLTS_E_, 25),
                Item(ItemID.ONYX_BOLTS_E_, 25),
                Item(ItemID.EMERALD_BOLTS_E_, 25),
                Item(ItemID.DIAMOND_BOLTS_E_, 50),  //new Item(ItemIdentifiers.DRAGON_JAVELIN, 15),
                Item(ItemID.DRAGON_THROWNAXE, 25),
                Item(ItemID.BOLT_RACK, 50),
                Item(ItemID.RUNE_KNIFE_P_PLUS_PLUS_, 50),
                Item(ItemID.TOKTZ_XIL_UL, 50),
                Item(22634, 15),  // Morrigan's throwing axe
                Item(ItemID.AMETHYST_JAVELIN, 25),  // RUNES
                Item(ItemID.AIR_RUNE, 250),
                Item(ItemID.WATER_RUNE, 250),
                Item(ItemID.EARTH_RUNE, 250),
                Item(ItemID.FIRE_RUNE, 50),
                Item(ItemID.CHAOS_RUNE, 35),
                Item(ItemID.DEATH_RUNE, 30),
                Item(ItemID.BLOOD_RUNE, 25),
                Item(ItemID.ASTRAL_RUNE, 15),  //new Item(ItemIdentifiers.NATURE_RUNE, 25),
                Item(ItemID.WRATH_RUNE, 25) // Wrath runes
        )

        /**
         * The random supplies given after a kill
         */
        private val CONSUMABLE_SUPPLIES = arrayOf(
                Item(ItemID.SHRIMPS, 3),
                Item(ItemID.TUNA, 2),
                Item(ItemID.SALMON, 3),
                Item(ItemID.TROUT, 3),
                Item(ItemID.SEA_TURTLE, 3),
                Item(ItemID.ANGLERFISH, 2),
                Item(ItemID.SWORDFISH, 2),
                Item(ItemID.SHARK, 3),
                Item(ItemID.COOKED_KARAMBWAN, 2),
                Item(ItemID.MANTA_RAY, 2),
                Item(15890, 3), // Big shark
                /**
                 * Potions
                 */
                Item(ItemID.SUPER_STRENGTH_2_),
                Item(ItemID.SUPER_ATTACK_2_),
                Item(ItemID.SUPER_DEFENCE_2_),
                Item(ItemID.MAGIC_POTION_2_),
                Item(ItemID.RANGING_POTION_2_),
                Item(ItemID.SUPER_COMBAT_POTION_2_),
                Item(ItemID.OVERLOAD_1),
                Item(ItemID.ZAMORAK_BREW_1_),
                Item(ItemID.GUTHIX_REST_1_),
                Item(ItemID.CUP_OF_TEA),
        )

        /**
         * The positions
         */
        val POSITIONS = arrayOf(Position(2960, 4228), Position(2989, 4228),
                Position(2998, 4255), Position(2989, 4282), Position(2960, 4282), Position(2951, 4255))

    }
}