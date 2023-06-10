package com.grinder.game.model.commands;

import com.grinder.Config;
import com.grinder.ServerIO;
import com.grinder.game.content.cluescroll.test.ResetScrollsHandlerCommand;
import com.grinder.game.content.cluescroll.test.SetScrollTaskCommand;
import com.grinder.game.content.cluescroll.test.TestClueTaskInterfaces;
import com.grinder.game.content.skill.skillable.impl.cons.Construction;
import com.grinder.game.content.skill.skillable.impl.cons.ConstructionUtils;
import com.grinder.game.content.skill.skillable.impl.cons.HouseRoom;
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonEffect;
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonType;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.model.EffectTimer;
import com.grinder.game.model.areas.constructed.Palette;
import com.grinder.game.model.areas.instanced.HouseInstance;
import com.grinder.game.model.commands.impl.*;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.bank.presets.PresetsManager;
import com.grinder.game.model.item.container.player.InstanceDeadItemCollectionBox;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {

    public static final Map<String, Command> commands = new HashMap<>();

    static {
        addPlayerCommands();
        addMemberCommands();
        addServerSupportCommands();
        addModeratorCommands();
        addGlobalModeratorCommands();
        addAdministratorCommands();
        AddOwnerCommands();
        addDeveloperCommands();
    }

    private static void addDeveloperCommands() {
        commands.put("toggle-superior-slayer", new ToggleSuperiorSlayerCommand());
        commands.put("p", new ProjectileCommand());
        commands.put("test-rob", new TestObjectRemovalCommand());
        commands.put("combot", new CombatBotCommand());
        commands.put("toggle", new ToggleCommand());
        commands.put("toggle-weapons-game", new ToggleWeaponsGame());
        commands.put("toggle-p-benchmarking", new DeveloperCommand() {
            @Override
            public void execute(Player player, String command, String[] parts) {
                Config.enable_player_benchmarking = !Config.enable_player_benchmarking;
                player.sendMessage("Set enable_player_benchmarking to "+Config.enable_player_benchmarking);
            }
        });
        commands.put("toggle-n-benchmarking", new DeveloperCommand() {
            @Override
            public void execute(Player player, String command, String[] parts) {
                Config.enable_npc_benchmarking = !Config.enable_npc_benchmarking;
                player.sendMessage("Set enable_npc_benchmarking to "+Config.enable_npc_benchmarking);
            }
        });
        commands.put("toggle-debugging", new DeveloperCommand() {
            @Override
            public void execute(Player player, String command, String[] parts) {
                Config.enable_debug_messages = !Config.enable_debug_messages;
                ServerIO.save();
                player.sendMessage("Set enable_npc_debug_messages to "+Config.enable_debug_messages);
            }
        });

        commands.put("test-box", new DeveloperCommand() {
            @Override
            public void execute(Player player, String command, String[] parts) {
                InstanceDeadItemCollectionBox box = new InstanceDeadItemCollectionBox(player, player.getInventory().cloneItems());
                player.setInstanceDeadItemCollectionBox(box);
            }
        });
        commands.put("xmas", new SetXmasState());
        commands.put("cam", new SetCameraCommand());
        commands.put("fade", new ScreenFadeCommand());
        commands.put("login", new LoginControlCommand());
        commands.put("telen", new TeleportToNpcName());
        commands.put("clue", new TestClueCommand());
        commands.put("teleni", new TeleportToNpcIndex());
        commands.put("finishslayer", new FinishSlayerTaskCommand());
        commands.put("cn", new ControlNPCCommand());
        commands.put("asound", new AreaSoundCommand());
        commands.put("sound", new EffectSoundCommand());
        commands.put("miscstall", new MiscStallTest());
        commands.put("face", new FaceNPCCommand());
        commands.put("boss", new BossCommand());
        commands.put("storevent", new StoreEventCommand());
        commands.put("broadcast", new BroadcastCommand());
        commands.put("spellbook", new ChangeSpellBookCommand());
        commands.put("wipeaccount", new WipeAccountCommand());
        commands.put("barrowschest", new TestBarrowsChest());
        commands.put("finishtask", new FinishSlayerTask());
        commands.put("dev", new SetRemoveDeveloperCommand());
        commands.put("nodev", new NoDevCommand());
        commands.put("setup", new CombatSetupCommand());
        commands.put("superban", new SuperBanCommand());
        ItemSpawn itemSpawnCommand = new ItemSpawn();
        commands.put("pickup", itemSpawnCommand);
        commands.put("item", itemSpawnCommand);
        commands.put("noclip", new NoclipCommand());
        commands.put("alltasks", new FinishAllTasksCommand());
        commands.put("unlockprayers", new UnlockPrayersCommands());
        commands.put("vorkath", new VorkathCommand());
        commands.put("givemember", new GiveMemberCommand());
        commands.put("fban", new ForceBanCommand());
        commands.put("reloaditems", new ReloadItemsCommand());
        commands.put("reloaddrops", new ReloadDropsCommand());
        commands.put("reloadshops", new ReloadShopsCommand());
        commands.put("reloadpunishments", new ReloadPunishmentsCommand());
        commands.put("reloadbans", new ReloadPunishmentsCommand());
        commands.put("checkip", new CheckIPAddress());
        commands.put("checkpin", new CheckBankPinCommand());
        commands.put("fkick", new ForceKickCommand());
        commands.put("clip", new ShowClipping());
        commands.put("obj", new SpawnObjectCommand());
        commands.put("snpc", new AddNpcCommand());
        commands.put("npcind", new NpcIndicesCommand());
        commands.put("stopnpc", new AddStopNpcCommand());
        commands.put("mgm", new MinigameMinuteCommand());
        commands.put("tourny", new TournamentCommand());
        commands.put("debughome", new DebugHome());
        commands.put("resetscrolls", new ResetScrollsHandlerCommand());
        commands.put("set-task", new SetScrollTaskCommand());
        commands.put("testclues", new TestClueTaskInterfaces());
        commands.put("addbot", new AddBotCommand());
        commands.put("randomizeapp", new RandomizeAppearance());
        commands.put("cluer", new ClaimClueReward());
        commands.put("killcon", new ForceChannelInactive());
        commands.put("massaddbot", new MassBot());
        commands.put("kickbots", new KickBotsCommand());
        commands.put("exit", new ExitClientCommand());
        commands.put("dialogue", new DialogueCommand());
        commands.put("flood", new FloodCommand());
        commands.put("hydra", new HydraCommand());
        commands.put("inferno", new InfernoCommand());
        commands.put("openbank", new OpenBank());
        commands.put("mysterytest", new MysteryTestCommand());
        commands.put("mysterytest2", new MysteryTest2Command());
        commands.put("addmusic", new IncrementMusicCommand());
        commands.put("bonusitems", new AddBonusItems());
        commands.put("muddytest", new MuddyTest());
        commands.put("muddytest2", new MuddyTest2());
        commands.put("crystaltest", new CrystalTestCommand());
        commands.put("crystaltest2", new TestCrystalChestCommand());
        commands.put("master", new MasterCommand());
        commands.put("randomizestats", new RandomizeStatsCommand());
        commands.put("reset", new ResetCommand());
        commands.put("fly", new FlyCommand());
        commands.put("human", new HumanCommand());
        commands.put("pnpc", new PNPCCommand());
        commands.put("npc", new SpawnNPCCommand());
        commands.put("pos", new POScommand());
        commands.put("setconfig", new SetConfigCommand());
        commands.put("setspec", new SpecCommand());
        commands.put("gfx", new GFXCommand());
        commands.put("gfxtest", new GFXTestCommand());
        commands.put("droptest", new DropTestCommand());
        commands.put("addpoints", new AddPointsCommand());
        commands.put("music", new MusicCommand());
        commands.put("jingle", new JinglebitCommand());
        commands.put("anim", new TestAnimationCommand());
        commands.put("emote", new TestAnimationCommand());
        commands.put("int", new InterfaceCommand());
        commands.put("ifvis", new InterfaceVisibilityCommand());
        commands.put("dint", new OpenDialogueInterfaceCommand());
        commands.put("shop", new Shop());
        commands.put("fdemote", new FDemoteCommand());
        commands.put("cint", new OpenChatboxInterfaceCommand());
        commands.put("areadebug", new AreaDebug());
        commands.put("invu", new Invulnerable());
        commands.put("itemattr", new AttributeItem());
        commands.put("minigame", new MinigameCommand());
        commands.put("test", new TestCommand());
        commands.put("varbit", new VarbitCommand());
        commands.put("getid", new GetIdCommand());
        commands.put("find", new FindCommand());
        commands.put("getnpc", new GetNpcCommand());
        commands.put("stats", new StatsCommand());
        commands.put("title", new Title());
        commands.put("runes", new RunesCommand());
        commands.put("barrage", new BarrageCommand());
        commands.put("morph", new MorphCommand());
        commands.put("iod", new IODCommand());
        commands.put("dtest", new DialogueTestCommand());
        commands.put("img", new ImageTestCommand());
        commands.put("richie", new RichieCommand());
        commands.put("die", new DieCommand());
        commands.put("copybank", new CopyBankCommand());
        commands.put("pkt", new SendPacketCommand());
        commands.put("cleargenstore", new ClearGeneralStoreCommand());
        commands.put("randomizebank", new RandomizeItemsInBank());
        commands.put("tb", new Command() {
            @Override
            public String getSyntax() {
                return "";
            }

            @Override
            public String getDescription() {
                return "Makes your account teleblocked for 150 seconds.";
            }

            @Override
            public void execute(Player player, String command, String[] parts) {
                if (player.getCombat().getTeleBlockTimer().finished()) {
                    player.getCombat().getTeleBlockTimer().start(150);
                    player.getPacketSender().sendEffectTimer(150, EffectTimer.TELE_BLOCK).sendMessage("You have just been teleblocked!");
                }
            }

            @Override
            public boolean canUse(Player player) {
                return PlayerUtil.isStaff(player);
            }
        });

        commands.put("poh", new Command() {
            @Override
            public String getSyntax() {
                return "";
            }

            @Override
            public String getDescription() {
                return "Enter HouseInstance";
            }

            @Override
            public void execute(Player player, String command, String[] parts) {
                if(player.getArea() != null && player.getArea() instanceof HouseInstance) {
                    player.sendMessage("House instance of: " + ((HouseInstance) player.getArea()).getHouseOwner().getUsername());
                    player.sendMessage("Rooms: " + Construction.countRooms(player));
                    player.sendMessage("Furniture: " + ((HouseInstance) player.getArea()).getHouseOwner().getHouse().getSurfaceFurniture().size());
                    return;
                }

                String[] args = command.split(" ");
                if(args.length > 0) {
                    if(args[0].equalsIgnoreCase("fill")) {
                        for(int i = 3; i < 11; i++) {
                            for (int j = 3; j < 11; j++) {
                                HouseRoom houseRoom = new HouseRoom(0, ConstructionUtils.PARLOUR, 0, i, j, 0);
                                Palette.PaletteTile tile = new Palette.PaletteTile(houseRoom.getX(), houseRoom.getY(), houseRoom.getZ(), houseRoom.getRotation());
                                player.getHouse().getHouseHouseRooms()[0][i][j]
                                        = houseRoom;
                            }
                        }
                    }
                }

                Construction.updateHouseInstance(player, ((HouseInstance) player.getArea()), true);
            }

            @Override
            public boolean canUse(Player player) {
                return PlayerUtil.isStaff(player);
            }
        });
        commands.put("resethouse", new Command() {
            @Override
            public String getSyntax() {
                return "";
            }

            @Override
            public String getDescription() {
                return "Resets house rooms & furniture";
            }

            @Override
            public void execute(Player player, String command, String[] parts) {
                player.getHouse().setHouseRooms(new HouseRoom[4][13][13]);
                player.getHouse().setDungeonHouseRooms(new HouseRoom[2][13][13]);
                player.getHouse().getSurfaceFurniture().clear();
                player.getHouse().getDungeonFurniture().clear();
                Construction.newHouse(player);

                Construction.updateHouseInstance(player, null, true);
            }

            @Override
            public boolean canUse(Player player) {
                return PlayerUtil.isStaff(player);
            }
        });
    }

    private static void AddOwnerCommands() {
        commands.put("pmod", new PromoteModerator());
        commands.put("demote", new DemoteCommand());
        commands.put("p", new ProjectileCommand());
        commands.put("resetpass", new ResetPasswordCommand());
        commands.put("update", new UpdateServerCommand());
        commands.put("resetpin", new ResetBankPinCommand());
        commands.put("tele", new TeleToLocation());
        commands.put("teler", new TeleToRegion());
        commands.put("cm", new CustomMapTeleCommand());
        commands.put("fteletome", new ForceTeleTomeCommand());
        commands.put("ftelehome", new ForceTeleHomeCommand());
        commands.put("ftelejail", new ForceTeleJailCommand());
        commands.put("padmin", new PromoteAdministrator());
        commands.put("pglobalmod", new PromoteGlobalModerator());
    }

    private static void addAdministratorCommands() {
        commands.put("toggletrading", new DisableTradingCommand());
        commands.put("togglestaking", new DisableStakingCommand());
        commands.put("togglegambling", new DisableGamblingCommand());
        commands.put("togglepickup", new DisablePickUpCommand());
        commands.put("toggledrop", new DisableDropCommand());
        commands.put("toggleshopping", new DisableShoppingCommand());
        commands.put("togglespawnmode", new DisableSpawnModeCommand());
        commands.put("pveteran", new PromoteVeteran());
        commands.put("pwiki", new PromoteWikiEditor());
        commands.put("prespected", new PromoteRespected());
        commands.put("pdesigner", new PromoteDesignerCommand());
        commands.put("pcontributor", new PromoteContributor());
        commands.put("pmotm", new PromoteMOTM());
        commands.put("pes", new PromoteExStaff());
        commands.put("pmm", new PromoteMiddleMan());
        commands.put("peh", new PromoteEventHost());
        commands.put("pss", new PromoteServerSupport());
        commands.put("admin", new AdminTeleportCommand());
        commands.put("invis", new InvisibleCommand());
        commands.put("togglevpn", new ToggleVPNCommand());
        commands.put("trigger", new TriggerRandomForPlayer());
        commands.put("trigger2", new TriggerRandomForPlayerV2());
        commands.put("tfrog", new TriggerFrogRandom());
        commands.put("event", new EventCommand());
    }

    private static void addGlobalModeratorCommands() {
        commands.put("bank", new BankCommand());
        commands.put("ipban", new IpBanPlayerCommand());
        commands.put("unipban", new UnIpBanPlayer());
        commands.put("hostban", new MacBanCommand());
        commands.put("unhostban", new UnMacBan());
        commands.put("macban", new MacBanCommand());
        commands.put("unmacban", new UnMacBan());
        commands.put("teletome", new TeleToMePlayer());
        commands.put("telehome", new TeleportOtherToHomeCommand());
        commands.put("movehome", new TeleportOtherToHomeCommand());
        commands.put("checkbank", new CheckBankCommand());
        commands.put("checkinv", new CheckInventoryCommand());
        commands.put("checkmove", new CheckMovementCommand());
        commands.put("showcol", new ShowCollisionCommand());
        commands.put("check", new CheckPlayerCommand());
        commands.put("checkdp", new CheckDepositCommand());
        commands.put("drawlottery", new DrawLottery());
        commands.put("sortlottery", new DrawLottery());
    }

    private static void addModeratorCommands() {
        commands.put("kick", new KickPlayerCommand());
        commands.put("ban", new BanPlayerCommand());
        commands.put("unban", new UnBanPlayer());
        commands.put("togglepvp", new ToggleStaffPvpCommand());
        commands.put("jail2", new Jail2PlayerCommand());
        commands.put("ipmute", new IpMutePlayerCommand());
        commands.put("unipmute", new UnIpMutePlayer());
        commands.put("lock", new LockAccountCommand());
        commands.put("verify", new VerifyCommand());
        commands.put("unlock", new VerifyCommand());
        commands.put("teleto", new TeleToPlayer());
        commands.put("cleartraps", new ClearTrapsCommand());
        commands.put("resettask", new ResetSlayerCommand());
        commands.put("resetquest", new ResetSlayerCommand());
    }

    private static void addServerSupportCommands() {
        commands.put("mycommands",new ShowCommandList());
        commands.put("mute", new MutePlayerCommand());
        commands.put("unmute", new UnMutePlayer());
        commands.put("telejail", new TeleportJailCommand());
        commands.put("jail", new JailPlayerCommand());
        commands.put("unjail", new UnJailPlayerCommand());
        commands.put("warn", new WarnPlayerCommand());
        commands.put("debug", new DebugCommand());
        commands.put("staffzone", new TeleportToStaffZoneCommand());
        commands.put("staffzone2", new StaffZoneCommand2());
        commands.put("sedge", new SEdgeCommand());
        commands.put("telemm", new TeleportMiddleManCommand());
    }

    private static void addMemberCommands() {
        commands.put("yell", new YellCommand());
        Skull skullCommand = new Skull();
        commands.put("skull", skullCommand);
        commands.put("redskull", skullCommand);
    }

    private static void addPlayerCommands() {
        commands.put("claimvote", new ClaimVoteCommand());
        commands.put("claimorder", new ClaimOrderCommand());
        commands.put("news", new CheckUpdatesCommand());
        commands.put("autotype", new AutoTypeCommand());
        commands.put("repeat", new RepeatAutoTalkCommand());
        commands.put("changepassword", new ChangePassword());
        commands.put("commands", new ShowCommandList());
        commands.put("staff", new StaffOnline());
        //commands.put("ironrank", new IronManRankCommand());
        commands.put("staffonline", new StaffOnline());
        commands.put("tasks", new FinishTasksCommand());
        commands.put("credits", new CheckYellCreditsCommand());
        commands.put("spawn", new SpawnGameModeFindCommand());
        commands.put("lockxp", new LockExperienceCommand());
        commands.put("afk", new AFKCommand());
        commands.put("back", new BackCommand());
        commands.put("claim", new ClaimOrderCommand());
        commands.put("stuck", new StuckCommand());
        commands.put("redeem", new ClaimOrderCommand());
        commands.put("redeemvote", new ClaimVoteCommand());
        commands.put("help", new HelpCommand());
        commands.put("creationdate", new CheckAccountCreationDate());
        commands.put("kdr", new CheckKillDeathRatio());
        commands.put("risk", new CheckRiskedBloodWorth());
        commands.put("players", new CheckPlayersOnline());
        commands.put("thread", new OpenThread());
        commands.put("timeplayed", new TimePlayed());
        commands.put("time", new TimePlayedForCommand());
        commands.put("benefits", new OpenWebMemberBenefits());
        commands.put("mm", new OnlineMiddlemenCommand());
        OpenWebStore storeCommand = new OpenWebStore();
        commands.put("donate", storeCommand);
        commands.put("store", storeCommand);
        commands.put("vote", new VoteCommand());
        commands.put("discord", new DiscordCommand());
        commands.put("osrs", new OsrsCommand());
        commands.put("unlock", new VerifyCommand());
        commands.put("highscores", new HighscoreCommand());
        commands.put("highscore", new HighscoreCommand());
        commands.put("updates", new CheckUpdatesCommand());
        commands.put("spec", new MemberSpecCommand());
        commands.put("hp", new MemberHealCommand());
        //commands.put("ge", new GECommand());
        commands.put("home", new HomeCommand());
        //commands.put("market", new GECommand());
        commands.put("wiki", new WikiCommand());
        commands.put("rules", new RulesCommand());
        commands.put("prices", new PricesCommand());
        commands.put("drops", new DropsCommand());
        commands.put("shops", new ShopsAreaCommand());
        commands.put("claimcape", new ClaimCapeCommand());
        commands.put("train", new TrainingCommand());
        commands.put("mb", new MageBankCommand());
        commands.put("teleport", new TeleportCommand());
        commands.put("t", new TeleportCommand());
        commands.put("duel", new TeleportToDuelArenaCommand());
        commands.put("dice", new DiceCommand());
        commands.put("casino", new DiceCommand());
        commands.put("gamble", new DiceCommand());
        commands.put("whipeffects", new WhipEffectsCommand());
        commands.put("newpvp", new NewPvPZoneCommand());
        commands.put("easts", new EastsCommand());
        commands.put("wests", new WestsCommand());
        commands.put("presets", new PresetsCommand());
        commands.put("preset", new PresetsCommand());
        commands.put("empty", new Empty());
        //commands.put("dz", new MemberZoneCommand());
        commands.put("1312zone", new To_1312_Zone());
        commands.put("tgarp", new GarpZoneCommand());

/*        commands.put("cl", new Command() {
            @Override
            public String getSyntax() {
                return "cl";
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public void execute(Player player, String command, String[] parts) {
                player.getCollectionLog().showInterface();
            }

            @Override
            public boolean canUse(Player player) {
                return true;
            }
        });*/

/*        commands.put("daily", new Command() {
            @Override
            public String getSyntax() {
                return "daily";
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public void execute(Player player, String command, String[] parts) {
                TaskList.ShowInterface(player);
            }

            @Override
            public boolean canUse(Player player) {
                return true;
            }
        });*/
    }

}
