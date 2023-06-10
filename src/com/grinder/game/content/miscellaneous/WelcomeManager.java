package com.grinder.game.content.miscellaneous;

import com.grinder.Config;
import com.grinder.game.content.GameMode;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.gambling.GambleConstants;
import com.grinder.game.content.skill.skillable.impl.magic.Teleporting;
import com.grinder.game.content.trading.TradeConstants;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterfaces;
import com.grinder.game.entity.agent.player.Appearance;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerSettings;
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses;
import com.grinder.game.model.Position;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueOptions;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.shop.ShopManager;
import com.grinder.net.packet.impl.EquipPacketListener;
import com.grinder.util.Executable;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;
import com.grinder.util.ShopIdentifiers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Handles welcoming new players to the server
 *
 * @author 2012
 */
public class WelcomeManager {

    /**
     * The rules interface
     */
    private static final int RULES_INTERFACE = 23344;

    /**
     * The welcome
     */
    private Welcome welcome;

    /**
     * The welcome stages
     */
    public enum WelcomeStage {
        /*
         * Welcome dialogue and introduction
         */
        WELCOME,
        /*
         * Player must accept rules before continuing
         */
        RULES,
        /*
         * Player receives starter kit
         */
        STARTER,
        /*
         * Player presets interface is open to start pking
         */
        //PRESETS,
        /*
         * Player has an option to have a tutorial of the server
         */
        TUTORIAL1,
        TUTORIAL2,
        TUTORIAL3,
        TUTORIAL4,
        TUTORIAL5,
        TUTORIAL6,
        TUTORIAL7,
        TUTORIAL8,
        TUTORIAL9,
        TUTORIAL10,
        TUTORIAL11,
        TUTORIAL12,
        TUTORIAL13,
        TUTORIAL14,
        TUTORIAL15,
        TUTORIAL16,
        TUTORIAL17,
        TUTORIAL18,
        TUTORIAL19,
        TUTORIAL20,
        TUTORIAL21,
        TUTORIAL22,
        TUTORIAL23,
        TUTORIAL24,
        TUTORIAL25,
        TUTORIAL26,
        TUTORIAL27,
        TUTORIAL28,
        TUTORIAL29,
        TUTORIAL30,
        TUTORIAL31,
        ;
    }

    /**
     * Welcoming new player
     *
     * @param player the player
     * @param stage  the stage
     */
    public static void welcome(Player player, WelcomeStage stage) {
        switch (stage) {
            case WELCOME:
                player.getPacketSender().sendMessage("@blu@[Server] Welcome the newcomer " + player.getUsername() + "!");
                player.BLOCK_ALL_BUT_TALKING = true;
                player.getWelcome().setWelcome(new Welcome());
                player.getPacketSender().sendMessage("<img=779>Join date: " + player.getWelcome().getWelcome().getFullDate());
                DialogueManager.start(player, 2521);

                player.setDialogueContinueAction(new Executable() {
                    @Override
                    public void execute() {
                        Rules.open(player);
                    }
                });
                break;
            case RULES:
                DialogueManager.start(player, 2522);
                player.setDialogueContinueAction(new Executable() {
                    @Override
                    public void execute() {

                        // Pre setup
                        player.getPacketSender().sendAppearanceConfig(304, player.getAppearance().getLook()[Appearance.CHEST]);
                        player.getPacketSender().sendAppearanceConfig(306, player.getAppearance().getLook()[Appearance.ARMS]);
                        player.getPacketSender().sendAppearanceConfig(308, player.getAppearance().getLook()[Appearance.HANDS]);
                        player.getPacketSender().sendAppearanceConfig(310, player.getAppearance().getLook()[Appearance.LEGS]);
                        player.getPacketSender().sendAppearanceConfig(312, player.getAppearance().getLook()[Appearance.FEET]);
                        player.getPacketSender().sendAppearanceConfig(314, player.getAppearance().getLook()[Appearance.HAIR_COLOUR]);
                        player.getPacketSender().sendAppearanceConfig(316, player.getAppearance().getLook()[Appearance.TORSO_COLOUR]);
                        player.getPacketSender().sendAppearanceConfig(318, player.getAppearance().getLook()[Appearance.LEG_COLOUR]);
                        player.getPacketSender().sendAppearanceConfig(322, player.getAppearance().getLook()[Appearance.SKIN_COLOUR]);

                        // Send makeover mage interface
                        player.getPacketSender().sendInterface(3559);
                        player.getAppearance().setCanChangeAppearance(true);
                        player.getPacketSender().sendTabs();
                        player.setOnTutorialMode(true);
                        player.getEquipment().refreshItems();
                        EquipPacketListener.resetWeapon(player);
                        WeaponInterfaces.INSTANCE.assign(player);
                        player.getCombat().reset(false);
                        EquipmentBonuses.update(player);

                        // Refresh item containers..
                        player.getInventory().refreshItems();
                        player.getEquipment().refreshItems();
                        player.getRunePouch().refreshItems();
                        player.setUpdateInventory(true);
                        player.getPacketSender().sendConfig(player.getCombat().getFightType().getParentId(), player.getCombat().getFightType().getChildId())
                                .sendConfig(172, player.getCombat().retaliateAutomatically() ? 1 : 0).updateSpecialAttackOrb();
                        player.updateAppearance();
                    }
                });
                break;
            case STARTER:
                DialogueManager.start(player, 2578);
                player.setDialogueOptions(new DialogueOptions() {
                    @Override
                    public void handleOption(Player player, int option) {
                        switch (option) {
                            case 1:
                                player.moveTo(new Position(3082, 3512, 0));
                                DialogueManager.start(player, 2582); // Lets get started, when its pressed it will do whats inside {
                                player.setDialogueContinueAction(new Executable() {
                                    @Override
                                    public void execute() {
                                        player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(1);
                                        player.setOnTutorialMode(true);
                                        ShopManager.open(player, 11);
                                    }
                                });
                                break;
                            case 2:
                                if (player.isNewPlayer()) {
                                    player.getPacketSender().sendInterface(51200);
                                    return;
                                }
                                DialogueManager.start(player, 2724);
                                break;
                        }
                    }
                });
                break;
            case TUTORIAL1:
                player.moveTo(new Position(3096, 3512, 0));
                player.setPositionToFace(new Position(3096, 3506, 0));
                DialogueManager.start(player, 2583);
                player.setDialogueContinueAction(new Executable() {
                    @Override
                    public void execute() {
                        player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(2);
                        ShopManager.open(player, 28);

                    }
                });
                break;
            case TUTORIAL2:
                player.moveTo(new Position(3091, 3508, 0));
                player.setPositionToFace(new Position(3091, 3507, 0));
                DialogueManager.start(player, 2584);
                player.setDialogueContinueAction(new Executable() {
                    @Override
                    public void execute() {
                        player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(3);
                        ShopManager.open(player, ShopIdentifiers.SKILLING_POINTS_STORE);

                    }
                });
                break;
            case TUTORIAL3:
                player.moveTo(new Position(2446, 5176, 0));
                DialogueManager.start(player, 2585);
                player.setDialogueContinueAction(new Executable() {
                    @Override
                    public void execute() {
                        player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(4);
                        ShopManager.open(player, 22);
                    }
                });
                break;
            case TUTORIAL4:
                player.getPacketSender().sendTab(6);
                player.getPacketSender().sendTabInterface(6, player.getSpellbook().getInterfaceId());
                player.moveTo(new Position(3562, 9943, 0));
                DialogueManager.start(player, 2586);
                player.setDialogueContinueAction(new Executable() {
                    @Override
                    public void execute() {
                        Teleporting.handleButton(player, 55555);
                        player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(5);
                    }
                });
                break;
            case TUTORIAL5: // Slayer cave
                player.moveTo(new Position(2793, 9999, 0));
                player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(6);
                DialogueManager.start(player, 2587);
                break;
            case TUTORIAL6: // Catherby Fishing/Cooking
                player.moveTo(new Position(2837, 3435, 0));
                player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(7);
                DialogueManager.start(player, 2588);
                break;
            case TUTORIAL7:
                player.moveTo(new Position(2327, 3832, 0));
                player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(8);
                DialogueManager.start(player, 2589);
                break;
            case TUTORIAL8:
                player.moveTo(new Position(3366, 3275, 0));
                player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(9);
                DialogueManager.start(player, 2590);
                break;
            case TUTORIAL9:
                player.moveTo(new Position(2845 + Misc.random(4), 2597 + Misc.random(1), 0));
                player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(10);
                DialogueManager.start(player, 2808);
                player.setDialogueContinueAction(new Executable() {
                    @Override
                    public void execute() {

                        player.getPacketSender().sendString(GambleConstants.STATUS_STRING_ID, "");
                        player.getPacketSender().sendString(GambleConstants.GAMBLING_WITH_STRING_ID, "Gambling with: Zezima");
                        player.getPacketSender().sendString(GambleConstants.TAX_AMOUNT_LEFT_CHILD_ID, "");
                        player.getPacketSender().sendString(GambleConstants.TAX_AMOUNT_RIGHT_CHILD_ID, "");
                        player.getPacketSender().sendInterfaceSet(GambleConstants.INTERFACE_ID, TradeConstants.CONTAINER_INVENTORY_INTERFACE);
                    }
                });
                break;
            case TUTORIAL10:
                player.moveTo(new Position(2438, 5168, 0));
                player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(11);
                DialogueManager.start(player, 2591);
                break;
            case TUTORIAL11:
                player.moveTo(new Position(2843, 3541, 2));
                player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(12);
                DialogueManager.start(player, 2592);
                break;
            case TUTORIAL12:
                player.moveTo(new Position(3323, 4969, 0));
                player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(13);
                DialogueManager.start(player, 2593);
                break;
            case TUTORIAL13:
                player.moveTo(new Position(2853, 2597, 0));
                //player.setPositionToFace(new Position(3085, 3482, 0));
                player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(16); // event below removed
                DialogueManager.start(player, 2594);
                break;
//            case TUTORIAL14:
//                player.moveTo(new Position(3086, 3478, 0));
//                player.setPositionToFace(new Position(3085, 3478, 0));
//                player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(15);
//                DialogueManager.start(player, 2595);
//                break;
            case TUTORIAL15:
                player.moveTo(new Position(2671, 3992, 1));
                player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(16);
                DialogueManager.start(player, 2596);
                break;
            case TUTORIAL16:
                player.moveTo(new Position(2271, 4692, 0));
                player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(17);
                DialogueManager.start(player, 2597);
                break;
            case TUTORIAL17:
                player.moveTo(new Position(3242, 10344, 0));
                player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(18);
                DialogueManager.start(player, 2598);
                break;
            case TUTORIAL18:
                player.moveTo(new Position(2870, 5362, 2));
                player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(19);
                DialogueManager.start(player, 2599);
                break;
            case TUTORIAL19:
                player.moveTo(new Position(3203, 3844, 0));
                player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(20);
                DialogueManager.start(player, 2600);
                break;
            case TUTORIAL20: // Hydra's Area
                player.moveTo(new Position(1344, 10239, 0));
                player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(21);
                DialogueManager.start(player, 2694);
                break;
            case TUTORIAL21: // BKT
                player.moveTo(new Position(2565, 9507, 0));
                player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(22);
                DialogueManager.start(player, 2695);
                break;
            case TUTORIAL22: // Hydra's Boss
                player.moveTo(new Position(1364, 10266, 0));
                player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(23);
                DialogueManager.start(player, 2696);
                break;
            case TUTORIAL23: // Tarn
                player.moveTo(new Position(3148, 4653, 0));
                player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(24);
                DialogueManager.start(player, 2697);
                break;
            case TUTORIAL24: // Rune dragons
                player.moveTo(new Position(1583, 5074, 0));
                player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(25);
                DialogueManager.start(player, 2698);
                break;
            case TUTORIAL25: // Ancient cave
                player.moveTo(new Position(1775, 5354, 0));
                player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(26);
                DialogueManager.start(player, 2699);
                break;
/*            case TUTORIAL26: // OSRS Shop
                player.moveTo(new Position(3069, 3516, 0));
                player.setPositionToFace(new Position(3069, 3517, 0));
                player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(27);
                DialogueManager.start(player, 2700);
                player.setDialogueContinueAction(new Executable() {
                    @Override
                    public void execute() {
                        ShopManager.open(player, 55);
                    }
                });
                break;*/
            case TUTORIAL27:
                player.moveTo(new Position(3071, 9562, 0));
                player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(28);
                DialogueManager.start(player, 2601);
                break;
            case TUTORIAL28:
                player.moveTo(new Position(2680, 9436, 0));
                player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(29);
                DialogueManager.start(player, 2602);
                break;
            case TUTORIAL29:
                player.moveTo(new Position(3089, 3488, 0));
                player.getPacketSender().sendTab(2);
                player.getPacketSender().sendTabInterface(2, 31000);
                player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(30);
                DialogueManager.start(player, 2603);
                break;
            case TUTORIAL30:
                player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(31);
                DialogueManager.start(player, 2604);
                player.getPacketSender().sendTab(3);
                break;
            case TUTORIAL31:
                if (player.isNewPlayer()) {
                    player.getPacketSender().sendInterface(51200);
                    return;
                }
                // Anything below is not for new players (Replaying the tutorial)

                // Reset tutorial (end)
                player.getAttributes().numAttr(Attribute.TUTORIAL_STAGE, 0).setValue(0);

                // Send dialogue for finishing the tutorial
                DialogueManager.start(player, 2605);

                // End tutorial mode bind
                player.setOnTutorialMode(false);

                // Process achievement if not completed already (if its a very old player possibly)
                if (player.getAchievements().getProgress()[AchievementType.GETTING_READY.ordinal()] == 0) {
                    AchievementManager.processFor(AchievementType.GETTING_READY, player);
                }

                // Send jinglebit for finishing the tutorial replay
                player.getPacketSender().sendJinglebitMusic(269, 0);
                player.getPacketSender().sendMusic(553, 4, 0);

                // Remove interfaces
                //player.setDialogueContinueAction(() -> player.getPacketSender().sendInterfaceRemoval());
                break;
            default:
                break;

        }

    }

    public static boolean handleButton(Player player, int button) {

        if (!player.isInTutorial())
            return false;
        if (!EntityExtKt.passedTime(player, Attribute.LAST_PRAY, 2, TimeUnit.SECONDS, false, false)) {
            return false;
        }

        switch (button) {
            case 52257:

                if (!player.isNewPlayer())
                    return false;

                player.setGameMode(GameMode.NORMAL);
                player.setTitle("");
                player.updateAppearance();
                return true;
            case 52264:
                if (!player.isNewPlayer())
                    return false;

                player.setGameMode(GameMode.ONE_LIFE);
                player.setTitle("<col=cf2e02>One Life</col>");
                player.updateAppearance();
                return true;
            case 52271:
                if (!player.isNewPlayer())
                    return false;

                player.setGameMode(GameMode.REALISM);
                player.setTitle("<col=e69202>Realism</col>");
                player.updateAppearance();
                return true;
            case 52278:
                if (!player.isNewPlayer())
                    return false;

                player.setGameMode(GameMode.CLASSIC);
                player.setTitle("<col=7a7874>Classic</col>");
                player.updateAppearance();
                return true;
            case 52285:
                if (!player.isNewPlayer())
                    return false;

                player.setGameMode(GameMode.PURE);
                player.setTitle("<col=911313>Pure</col>");
                player.updateAppearance();
                return true;
            case 52292:
                if (!player.isNewPlayer())
                    return false;

                player.setGameMode(GameMode.MASTER);
                player.setTitle("<col=b015d6>Master</col>");
                player.updateAppearance();
                return true;
            case 52299:
                if (!player.isNewPlayer())
                    return false;
                if (!Config.spawn_game_mode_enabled) {
                    player.sendMessage("The @red@[SPAWN]</col>game mode system has been switched @red@OFF</col> by the server administrator. Check back later.");
                    return false;
                }

                player.setGameMode(GameMode.SPAWN);
                player.setTitle("<col=fcbd00>Spawn</col>");
                player.updateAppearance();
                return true;
            case 52306:
                if (!player.isNewPlayer())
                    return false;


                player.setGameMode(GameMode.IRONMAN);
                player.setTitle("@bla@Ironman</col>");
                player.updateAppearance();
                return true;
            case 52313:
                if (!player.isNewPlayer())
                    return false;

                player.setGameMode(GameMode.HARDCORE_IRONMAN);
                player.setTitle("@bla@HCIM</col>");
                player.updateAppearance();
                return true;
            case 52320:
                if (!player.isNewPlayer())
                    return false;

                player.setGameMode(GameMode.ULTIMATE_IRONMAN);
                player.setTitle("@bla@UIM</col>");
                player.updateAppearance();
                return true;
            case 51209: // Confirm game mode button
                GameMode.select(player);

                PlayerSettings.INSTANCE.toggleMultiplyXPDropsStateNoMessage(player); // Enable XP multiply by default
                return true;
        }
        EntityExtKt.markTime(player, Attribute.LAST_PRAY);

        return false;
    }

    /**
     * Sets the welcome
     *
     * @return the welcome
     */
    public Welcome getWelcome() {
        return welcome;
    }

    /**
     * Sets the welcome
     *
     * @param welcome the welcome
     */
    public void setWelcome(Welcome welcome) {
        this.welcome = welcome;
    }

    /**
     * Represents welcome details
     */
    public static class Welcome {

        /**
         * The date format
         */
        private static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyyy hh:mm:ss");

        /**
         * The welcome date
         */
        private String date;

        /**
         * The full date
         */
        private String fullDate;

        /**
         * New welcome details
         */
        public Welcome() {
            this.setFullDate(new Date().toString());
            this.setDate(format.format(new Date()));
        }

        /**
         * Sets the date
         *
         * @return the date
         */
        public String getDate() {
            return date;
        }

        /**
         * Sets the date
         *
         * @param date the date
         */
        public void setDate(String date) {
            this.date = date;
        }

        /**
         * Sets the fullDate
         *
         * @return the fullDate
         */
        public String getFullDate() {
            return fullDate;
        }

        /**
         * Sets the fullDate
         *
         * @param fullDate the fullDate
         */
        public void setFullDate(String fullDate) {
            this.fullDate = fullDate;
        }

        /**
         * Gets the join date in dd/MM/yyyy
         */
        public String getShortDate() {
            SimpleDateFormat shortFormat = new SimpleDateFormat("dd/MM/yyyy");
            String shortDate;
            try {
                shortDate = shortFormat.format(format.parse(date));
            } catch (ParseException e) {
                Date today = new Date();
                date = format.format(today);
                fullDate = today.toString();
                shortDate = shortFormat.format(today);
            }
            return shortDate;
        }
    }
}
