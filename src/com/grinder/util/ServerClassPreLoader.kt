package com.grinder.util

import com.grinder.game.GameEngine
import com.grinder.game.content.`object`.*
import com.grinder.game.content.`object`.agilityshortcuts.OgrePitLooseRailing
import com.grinder.game.content.achievement.AchievementManager
import com.grinder.game.content.cluescroll.scroll.type.PuzzleType
import com.grinder.game.content.dueling.DuelController
import com.grinder.game.content.dueling.Scoreboard
import com.grinder.game.content.gambling.blackjack.card.Deck
import com.grinder.game.content.item.*
import com.grinder.game.content.item.building.ItemBuilding
import com.grinder.game.content.item.charging.Chargeables
import com.grinder.game.content.item.charging.ClearableChargedItems
import com.grinder.game.content.item.coloring.ItemColorCustomizer
import com.grinder.game.content.item.degrading.DegradableType
import com.grinder.game.content.item.jewerly.*
import com.grinder.game.content.item.mysterybox.MysteryBoxes
import com.grinder.game.content.item.mysterybox.SpinMysteryBox
import com.grinder.game.content.item.transforming.ItemTransforming
import com.grinder.game.content.minigame.PublicMinigameHandler
import com.grinder.game.content.minigame.blastfurnace.BlastFurnace
import com.grinder.game.content.minigame.motherlodemine.MotherlodeMine
import com.grinder.game.content.miscellaneous.*
import com.grinder.game.content.miscellaneous.christmas._2022.Christmas2022
import com.grinder.game.content.miscellaneous.cleanherb.HerbCleaningDialogue
import com.grinder.game.content.miscellaneous.pickables.Pickables
import com.grinder.game.content.miscellaneous.randomevent.RandomEventRefreshment
import com.grinder.game.content.miscellaneous.voting.VotingStreaks
import com.grinder.game.content.npc.*
import com.grinder.game.content.pvm.*
import com.grinder.game.content.pvm.contract.MonsterHunting
import com.grinder.game.content.skill.SkillGuides
import com.grinder.game.content.skill.skillable.impl.*
import com.grinder.game.content.skill.skillable.impl.cons.actions.*
import com.grinder.game.content.skill.skillable.impl.cooking.FoodTopping
import com.grinder.game.content.skill.skillable.impl.cooking.Recipes
import com.grinder.game.content.skill.skillable.impl.cooking.Uncooked
import com.grinder.game.content.skill.skillable.impl.crafting.AmuletStringing
import com.grinder.game.content.skill.skillable.impl.crafting.HideTanData
import com.grinder.game.content.skill.skillable.impl.crafting.Jewelry
import com.grinder.game.content.skill.skillable.impl.crafting.craftable.impl.Gem
import com.grinder.game.content.skill.skillable.impl.crafting.craftable.impl.Hide
import com.grinder.game.content.skill.skillable.impl.crafting.craftable.impl.LeatherShield
import com.grinder.game.content.skill.skillable.impl.crafting.craftable.impl.Snakeskin
import com.grinder.game.content.skill.skillable.impl.farming.Farming
import com.grinder.game.content.skill.skillable.impl.fishing.FishingSpot
import com.grinder.game.content.skill.skillable.impl.fletching.FletchableLog
import com.grinder.game.content.skill.skillable.impl.fletching.StringableBow
import com.grinder.game.content.skill.skillable.impl.herblore.*
import com.grinder.game.content.skill.skillable.impl.hunter.HunterActions
import com.grinder.game.content.skill.skillable.impl.hunter.HunterTraps
import com.grinder.game.content.skill.skillable.impl.hunter.birdhouse.BirdHouseActions
import com.grinder.game.content.skill.skillable.impl.magic.EnchantCrossbowBolt
import com.grinder.game.content.skill.skillable.impl.magic.EnchantSpellCasting
import com.grinder.game.content.skill.skillable.impl.magic.InteractiveSpell
import com.grinder.game.content.skill.skillable.impl.magic.TeleOtherSpell
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler
import com.grinder.game.content.skill.skillable.impl.runecrafting.RunecraftingEvents
import com.grinder.game.content.skill.skillable.impl.runecrafting.abyss.AbyssEvents
import com.grinder.game.content.skill.skillable.impl.runecrafting.abyss.DarkMage
import com.grinder.game.content.trading.TradeController
import com.grinder.game.definition.NpcHeights
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponFightType
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.Ammunition
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.RangedWeapon
import com.grinder.game.entity.agent.npc.monster.impl.Cows
import com.grinder.game.entity.agent.player.PlayerSettings
import com.grinder.game.entity.agent.player.bot.script.BotScriptRepository
import com.grinder.game.model.Skill
import com.grinder.game.model.Skillcape
import com.grinder.game.model.areas.MapBuilder
import com.grinder.game.model.areas.instanced.HydraArea
import com.grinder.game.model.areas.instanced.VorkathArea
import com.grinder.game.model.areas.instanced.ZulrahShrine
import com.grinder.game.model.commands.CommandManager
import com.grinder.game.model.commands.DeveloperCommands
import com.grinder.game.model.consumable.DwarvenRockCakePlugin
import com.grinder.game.model.consumable.edible.Edible
import com.grinder.game.model.item.BloodItemPrices
import com.grinder.game.model.item.BrokenItems
import com.grinder.game.model.item.ImbuedableItems
import com.grinder.game.model.item.ItemSetType
import com.grinder.game.model.item.container.player.SafeDeposit
import com.grinder.game.model.message.MessageFilterManager
import com.grinder.game.model.punishment.PunishmentManager
import com.grinder.net.channel.HttpStaticFileServerHandler
import com.grinder.net.packet.DataConstants
import com.grinder.util.tools.DupeDetector
import org.reflections.Reflections
import java.util.function.Consumer

/**
 * Force invokes static (or init in kotlin) blocks.
 *
 * Init classes here to reduce overhead of lazy loading
 * during word cycling.
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   01/10/2019
 * @version 1.0
 */
object ServerClassPreLoader {

    /**
     * TODO: sort this alphabetically or something
     */
    fun preloadClasses(){
        forceInit(Cows::class.java)
        forceInit(Tables::class.java)
        forceInit(AmuletOfChemistry::class.java)
        forceInit(PotionBrewing::class.java)
        forceInit(BarbarianPotion::class.java)
        forceInit(PoisonWeapon::class.java)
        forceInit(Snakeskin::class.java)
        forceInit(AmuletStringing::class.java)
        forceInit(AncientWizard::class.java)
        forceInit(Bardur::class.java)
        forceInit(EnchantmentGuardian::class.java)
        forceInit(Giles::class.java)
        forceInit(KingPercival::class.java)
        forceInit(LeonDcour::class.java)
        forceInit(SirLancelot::class.java)
        forceInit(Trufitus::class.java)
        forceInit(SlayerRing::class.java)
        forceInit(EnchantedGem::class.java)
        forceInit(Farming::class.java)
        forceInit(BankObjectActions::class.java)
        forceInit(Oyster::class.java)
        forceInit(SlayerHelmet::class.java)
        forceInit(SlayerEnchantment::class.java)
        forceInit(BirdHouseActions::class.java)
        forceInit(DicerRank::class.java)
        forceInit(Spade::class.java)
        forceInit(OldschoolBond::class.java)
        forceInit(QuestScrolls::class.java)
        forceInit(ItemPacks::class.java)
        forceInit(SkillingSupplies::class.java)
        forceInit(SkillingTomes::class.java)
        forceInit(CombatLamp::class.java)
        forceInit(ImbuedHeart::class.java)
        forceInit(PoisonChalice::class.java)
        forceInit(SaradominsLight::class.java)
        forceInit(BanditsBrew::class.java)
        forceInit(RingOfWealthScroll::class.java)
        forceInit(WaterSource::class.java)
        forceInit(CombatStarterPacks::class.java)
        forceInit(FishBowls::class.java)
        forceInit(Bonecrusher::class.java)
        forceInit(BonecrusherNecklace::class.java)
        forceInit(DragonboneNecklace::class.java)
        forceInit(ItemBuilding::class.java)
        forceInit(PrayerHandler::class.java)
        forceInit(ItemDestruction::class.java)
        forceInit(AchievementManager::class.java)
        forceInit(YellCustomizer::class.java)
        forceInit(TradeController::class.java)
        forceInit(DuelController::class.java)
        forceInit(RingOfRecoil::class.java)
        forceInit(Scoreboard::class.java)
        forceInit(LightSources::class.java)
        forceInit(MessageFilterManager::class.java)
        forceInit(YellCredits::class.java)
        forceInit(MaxCapeClaim::class.java)
        forceInit(VotingTicket::class.java)
        forceInit(PremiumTicket::class.java)
        forceInit(Deck::class.java)
        forceInit(RankChooser::class.java)
        forceInit(MysteriousManEvent::class.java)
        forceInit(FrogPrinceEvent::class.java)
        forceInit(Thessalia::class.java)
        forceInit(RunecraftingEvents::class.java)
        forceInit(AbyssEvents::class.java)
        forceInit(DarkMage::class.java)
        forceInit(Ammunition::class.java)
        forceInit(AmuletOfGlory::class.java)
        forceInit(TrimmedAmuletOfGlory::class.java)
        forceInit(ImbuedRingOfWealth::class.java)
        forceInit(RingOfWealth::class.java)
        forceInit(DoubleXPRing::class.java)
        forceInit(BurningAmulet::class.java)
        forceInit(NecklaceOfPassage::class.java)
        forceInit(CombatBracelet::class.java)
        forceInit(RingOfReturning::class.java)
        forceInit(GamesNecklace::class.java)
        forceInit(DigsitePedant::class.java)
        forceInit(AbyssalBracelet::class.java)
        forceInit(BloodItemPrices::class.java)
        forceInit(EnchantCrossbowBolt::class.java)
        forceInit(BossDropTables::class.java)
        forceInit(BotScriptRepository::class.java)
        forceInit(BrokenItems::class.java)
        forceInit(Christmas2022::class.java)
        forceInit(CommandManager::class.java)
        //forceInit(ConstructionUtils::class.java)
        forceInit(DataConstants::class.java)
        forceInit(DegradableType::class.java)
        forceInit(DupeDetector::class.java)
        forceInit(Edible::class.java)
        forceInit(Emotes::class.java)
        forceInit(EnchantSpellCasting::class.java)
        forceInit(Firemaking::class.java)
        forceInit(FletchableLog::class.java)
        forceInit(GameEngine::class.java)
        forceInit(Gem::class.java)
        forceInit(Hide::class.java)
        forceInit(LeatherShield::class.java)
        forceInit(HideTanData::class.java)
        //forceInit(HouseActions::class.java)
        //forceInit(HouseBuildingActions::class.java)
        //forceInit(HouseButtonActions::class.java)
        //forceInit(HouseFurnitureActions::class.java)
        //forceInit(HouseNPCActions::class.java)
        forceInit(HttpStaticFileServerHandler::class.java)
        forceInit(HunterActions::class.java)
        forceInit(HunterTraps::class.java)
        forceInit(InteractiveSpell::class.java)
        forceInit(ItemColorCustomizer::class.java)
        forceInit(MysteryBoxes::class.java)
        forceInit(ItemSetType::class.java)
        forceInit(ItemTransforming::class.java)
        forceInit(Jewelry::class.java)
        forceInit(Mining::class.java)
        forceInit(MonsterKilling::class.java)
        forceInit(MonsterCaskets::class.java)
        forceInit(MonsterHunting::class.java)
        forceInit(MorphItems::class.java)
        forceInit(SpinMysteryBox::class.java)
        forceInit(Notes::class.java)
        forceInit(NpcHeights::class.java)
        forceInit(PlayerSettings::class.java)
        forceInit(PotionDosageType::class.java)
        forceInit(PotionFinishedType::class.java)
        forceInit(PotionIngredientHerbType::class.java)
        forceInit(PotionUnfinishedType::class.java)
        forceInit(Prayer::class.java)
        forceInit(PrayerHandler::class.java)
        forceInit(PunishmentManager::class.java)
        forceInit(PuzzleType::class.java)
        forceInit(PvpPortalObject::class.java)
        forceInit(RandomEventRefreshment::class.java)
        forceInit(RangedWeapon::class.java)
        forceInit(RecentUpdates::class.java)
        forceInit(RingOfDueling::class.java)
        forceInit(RingOfWealth::class.java)
        forceInit(SafeDeposit::class.java)
        forceInit(Skill::class.java)
        forceInit(Skillcape::class.java)
        forceInit(BookOfKnowledge::class.java)
        forceInit(SkillsNecklace::class.java)
        forceInit(SkillGuides::class.java)
        forceInit(Smithing::class.java)
        forceInit(StringableBow::class.java)
        forceInit(TeleOtherSpell::class.java)
        forceInit(Thieving::class.java)
        forceInit(ClearableChargedItems::class.java)
        forceInit(ImbuedableItems::class.java)
        forceInit(VotingStreaks::class.java)
        forceInit(WeaponFightType::class.java)
        forceInit(WildernessBossSpirit::class.java)
        forceInit(PorazdirWildernessEvent::class.java)
        forceInit(ZulrahShrine::class.java)
        forceInit(HerbCleaningDialogue::class.java)
        // TODO: Lava blade attributes
        // TODO: serpentine effect
        // TODO: blade of sealdor
        // TODO: Pharoah scetpre, charging, teleporting, and all
        forceInit(FishingSpot::class.java)
        forceInit(Recipes::class.java)
        forceInit(Uncooked::class.java)
        forceInit(FoodTopping::class.java)
        forceInit(CannonBase::class.java)
        forceInit(DwarfMulticannon::class.java)
        forceInit(NulodionDialogue::class.java)
        forceInit(Chargeables::class.java)
        forceInit(BraceletOfSlaughter::class.java)
        forceInit(ExpeditiousBracelet::class.java)
        forceInit(ElementalShields::class.java)
        forceInit(DeveloperCommands::class.java)
        forceInit(Entrances::class.java)
        forceInit(ChasmOfFire::class.java)
        forceInit(Portals::class.java)
        forceInit(Chests::class.java)
        forceInit(HotVentDoor::class.java)
        forceInit(DwarvenRockCakePlugin::class.java)
        forceInit(PublicMinigameHandler::class.java)
        forceInit(MotherlodeMine::class.java)
        forceInit(Pickables::class.java)
        forceInit(ShantayPass::class.java)
        forceInit(BlastFurnace::class.java)
        forceInit(HydraArea::class.java)
        forceInit(VorkathArea::class.java)
        forceInit(MapBuilder::class.java)
        forceInit(OgrePitLooseRailing::class.java)

    }

    /**
     * Forces the initialization of the class pertaining to
     * the specified <tt>Class</tt> object.
     * This method does nothing if the class is already
     * initialized prior to invocation.
     *
     * @param klass the class for which to force initialization
     * @return <tt>klass</tt>
     */
    fun <T> forceInit(klass: Class<T>): Class<T> {
        try {
            Class.forName(klass.name, true, klass.classLoader)
        } catch (e: ClassNotFoundException) {
            throw AssertionError(e)  // Can't happen
        }

        return klass
    }

    fun<T> forceInitAllSubs(klass: Class<T>) {
        val reflections = Reflections(klass)
        val modules = reflections.getSubTypesOf(klass)
        modules.forEach(Consumer { subClass: Class<out T> ->
            forceInit(subClass)
        })
    }
}
