package com.grinder.game.entity.agent.player;

import com.grinder.Config;
import com.grinder.Server;
import com.grinder.ServerIO;
import com.grinder.game.GameConstants;
import com.grinder.game.World;
import com.grinder.game.WorldConstants;
import com.grinder.game.content.GameMode;
import com.grinder.game.content.achievement.AchievementManager;
import com.grinder.game.content.achievement.AchievementType;
import com.grinder.game.content.clan.ClanChat;
import com.grinder.game.content.clan.GlobalClanChatManager;
import com.grinder.game.content.cluescroll.ClueScrollManager;
import com.grinder.game.content.collectionLog.CollectionLog;
import com.grinder.game.content.dueling.DuelController;
import com.grinder.game.content.gambling.GambleController;
import com.grinder.game.content.gambling.lottery.Lottery;
import com.grinder.game.content.item.LootingBag;
import com.grinder.game.content.item.VotingTicket;
import com.grinder.game.content.item.coloring.ItemColorCustomizer;
import com.grinder.game.content.item.coloring.ItemColorCustomizer.ColorfulItem;
import com.grinder.game.content.item.degrading.ItemDegradationManager;
import com.grinder.game.content.item.mysterybox.SpinMysteryBoxType;
import com.grinder.game.content.minigame.Minigame;
import com.grinder.game.content.minigame.MinigameManager;
import com.grinder.game.content.minigame.Party;
import com.grinder.game.content.minigame.aquaisneige.AquaisNeige;
import com.grinder.game.content.minigame.aquaisneige.AquaisNeigeValueHolder;
import com.grinder.game.content.minigame.barrows.BarrowsBrother;
import com.grinder.game.content.minigame.barrows.BarrowsManager;
import com.grinder.game.content.minigame.blastfurnace.BlastFurnace;
import com.grinder.game.content.minigame.blastfurnace.BlastFurnaceValueHolder;
import com.grinder.game.content.minigame.castlewars.CastleWars;
import com.grinder.game.content.minigame.castlewars.LobbyManager;
import com.grinder.game.content.minigame.chamberoxeric.COXManager;
import com.grinder.game.content.minigame.fightcave.FightCave;
import com.grinder.game.content.minigame.fightcave.FightCaveValueHolder;
import com.grinder.game.content.minigame.motherlodemine.MotherlodeMine;
import com.grinder.game.content.minigame.motherlodemine.MotherlodeMineValueHolder;
import com.grinder.game.content.minigame.pestcontrol.PestControl;
import com.grinder.game.content.minigame.warriorsguild.WarriorsGuild;
import com.grinder.game.content.minigame.warriorsguild.WarriorsGuildValueHolder;
import com.grinder.game.content.miscellaneous.*;
import com.grinder.game.content.miscellaneous.WelcomeManager.Welcome;
import com.grinder.game.content.miscellaneous.WelcomeManager.WelcomeStage;
import com.grinder.game.content.miscellaneous.daily.DailyLoginRewardManager;
import com.grinder.game.content.miscellaneous.presets.Presetable;
import com.grinder.game.content.miscellaneous.presets.Presetables;
import com.grinder.game.content.miscellaneous.randomevent.RandomEvent;
import com.grinder.game.content.miscellaneous.randomevent.RandomEventRefreshment;
import com.grinder.game.content.miscellaneous.rugmerchant.RugMerchant;
import com.grinder.game.content.miscellaneous.voting.Voting;
import com.grinder.game.content.object.DwarfCannon;
import com.grinder.game.content.pvm.MonsterKillTracker;
import com.grinder.game.content.pvm.NpcInformation;
import com.grinder.game.content.pvm.contract.MonsterHunting;
import com.grinder.game.content.pvp.WildernessScoreBoard;
import com.grinder.game.content.pvp.bountyhunter.BountyHunterManager;
import com.grinder.game.content.pvp.bountyhunter.kill.KillClearanceTask;
import com.grinder.game.content.quest.QuestManager;
import com.grinder.game.content.skill.SkillManager;
import com.grinder.game.content.skill.SkillStats;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.content.skill.Skills;
import com.grinder.game.content.skill.skillable.SkillActionTask;
import com.grinder.game.content.skill.skillable.Skillable;
import com.grinder.game.content.skill.skillable.impl.agility.Agility;
import com.grinder.game.content.skill.skillable.impl.cons.House;
import com.grinder.game.content.skill.skillable.impl.cons.HouseFurniture;
import com.grinder.game.content.skill.skillable.impl.cons.HouseRoom;
import com.grinder.game.content.skill.skillable.impl.cons.seating.Seat;
import com.grinder.game.content.skill.skillable.impl.farming.Farming;
import com.grinder.game.content.skill.skillable.impl.hunter.birdhouse.BirdHouseActions;
import com.grinder.game.content.skill.skillable.impl.hunter_new.Hunter;
import com.grinder.game.content.skill.skillable.impl.hunter_new.traps.HunterTrap;
import com.grinder.game.content.skill.skillable.impl.magic.SpellCasting;
import com.grinder.game.content.skill.skillable.impl.magic.Teleporting;
import com.grinder.game.content.skill.skillable.impl.prayer.Prayer;
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler;
import com.grinder.game.content.skill.skillable.impl.prayer.PrayerHandler.PrayerType;
import com.grinder.game.content.skill.skillable.impl.prayer.QuickPrayers;
import com.grinder.game.content.skill.skillable.impl.runecrafting.pouch.EssencePouch;
import com.grinder.game.content.skill.skillable.impl.runecrafting.pouch.PouchType;
import com.grinder.game.content.skill.skillable.impl.slayer.SlayerManager;
import com.grinder.game.content.skill.task.SkillTaskManager;
import com.grinder.game.content.trading.TradeController;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.EntityType;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.combat.PlayerCombat;
import com.grinder.game.entity.agent.combat.attack.AttackType;
import com.grinder.game.entity.agent.combat.attack.special.SpecialAttackType;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponFightType;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterface;
import com.grinder.game.entity.agent.combat.attack.weapon.WeaponInterfaces;
import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpellType;
import com.grinder.game.entity.agent.combat.attack.weapon.poison.PoisonEffectTask;
import com.grinder.game.entity.agent.combat.attack.weapon.poison.VenomEffectTask;
import com.grinder.game.entity.agent.combat.attack.weapon.ranged.RangedWeapon;
import com.grinder.game.entity.agent.combat.misc.CombatPrayer;
import com.grinder.game.entity.agent.movement.MovementStatus;
import com.grinder.game.entity.agent.movement.PlayerMotion;
import com.grinder.game.entity.agent.movement.pathfinding.target.FollowTargetStrategy;
import com.grinder.game.entity.agent.movement.pathfinding.target.RectangleTargetStrategy;
import com.grinder.game.entity.agent.movement.pathfinding.target.TargetStrategy;
import com.grinder.game.entity.agent.movement.pathfinding.traverse.SmallTraversal;
import com.grinder.game.entity.agent.movement.pathfinding.traverse.TraversalType;
import com.grinder.game.entity.agent.movement.task.WalkToAction;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.bot.BotPlayer;
import com.grinder.game.entity.agent.player.death.PlayerDeathTask;
import com.grinder.game.entity.agent.player.equipment.EquipmentBonuses;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;
import com.grinder.game.entity.agent.player.equipment.EquipmentUtil;
import com.grinder.game.entity.agent.player.event.PlayerEvent;
import com.grinder.game.entity.agent.player.event.PlayerEventListener;
import com.grinder.game.entity.agent.player.event.PlayerEvents;
import com.grinder.game.entity.agent.player.event.impl.PlayerLoginEvent;
import com.grinder.game.entity.agent.player.event.impl.PlayerLogoutEvent;
import com.grinder.game.entity.grounditem.ItemOnGroundManager;
import com.grinder.game.entity.object.DynamicGameObject;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.entity.updating.UpdateBlock;
import com.grinder.game.entity.updating.block.BasicAnimationSet;
import com.grinder.game.entity.updating.sync.SequentialClientSynchronizer;
import com.grinder.game.message.Message;
import com.grinder.game.model.*;
import com.grinder.game.model.area.Region;
import com.grinder.game.model.area.RegionCoordinates;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.areas.InstanceManager;
import com.grinder.game.model.areas.instanced.HouseInstance;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.attribute.AttributeManager;
import com.grinder.game.model.attribute.AttributeManager.Points;
import com.grinder.game.model.commands.impl.LoginControlCommand;
import com.grinder.game.model.instance.PlayerInstance;
import com.grinder.game.model.interfaces.dialogue.Dialogue;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueOptions;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.interfaces.menu.CreationMenu;
import com.grinder.game.model.interfaces.syntax.EnterSyntax;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.bank.*;
import com.grinder.game.model.item.container.player.*;
import com.grinder.game.model.item.container.shop.Shop;
import com.grinder.game.model.message.ChatMessage;
import com.grinder.game.model.punishment.Punishment;
import com.grinder.game.model.punishment.PunishmentManager;
import com.grinder.game.model.punishment.PunishmentType;
import com.grinder.game.model.sound.AreaSound;
import com.grinder.game.model.sound.Music;
import com.grinder.game.model.sound.Sound;
import com.grinder.game.model.sound.Sounds;
import com.grinder.game.service.search.droptable.SearchDropTableWorker;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.game.task.impl.OverloadTask;
import com.grinder.game.task.impl.RestoreSpecialAttackTask;
import com.grinder.game.task.impl.SavePlayerTask;
import com.grinder.net.channel.GrinderHandler;
import com.grinder.net.codec.database.SQLManager;
import com.grinder.net.codec.database.impl.DatabaseLoginLogs;
import com.grinder.net.codec.database.impl.UpdateHighscorePlayers;
import com.grinder.net.codec.database.impl.UpdateOnlinePlayers;
import com.grinder.net.packet.PacketSender;
import com.grinder.net.packet.impl.EquipPacketListener;
import com.grinder.net.session.PlayerSession;
import com.grinder.util.*;
import com.grinder.util.benchmark.SimpleBenchMarker;
import com.grinder.util.collection.CollectionUtil;
import com.grinder.util.debug.DebugTab;
import com.grinder.util.debug.DebugType;
import com.grinder.util.oldgrinder.EquipSlot;
import com.grinder.util.oldgrinder.InterfaceCloseListener;
import com.grinder.util.time.SecondsTimer;
import com.grinder.util.time.TimeUnits;
import com.grinder.util.time.TimeUtil;
import com.grinder.util.timing.Stopwatch;
import com.grinder.util.timing.TimerKey;
import com.grinder.util.tools.DupeDetector;
import io.netty.channel.Channel;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

public class Player extends Agent {

    public PlayerInstance instance;

    public COXManager getCOX() {
        return raid;
    }

    public COXManager raid = new COXManager(this);


    public QuestManager getQuest() {
        return quest;
    }

    private final QuestManager quest = new QuestManager(this);

    /**
     * How many tiles a player can 'see' at a time, normally.
     */
    public final static int NORMAL_VIEW_DISTANCE = 15;

    /**
     * How many tiles a player can 'see' at a time when in a 'large' viewport.
     */
    public final static int LARGE_VIEW_DISTANCE = 127;

    public static final int FORCE_DISCONNECT_TICKS = 100;

    /**
     * The current amount of appearance tickets.
     */
    private static final AtomicInteger appearanceTicketCounter = new AtomicInteger(0);

    public final SecondsTimer increaseStats = new SecondsTimer();
    public final SecondsTimer decreaseStats = new SecondsTimer();

    public final SecondsTimer divineAttack = new SecondsTimer();
    public final SecondsTimer divineStrength = new SecondsTimer();
    public final SecondsTimer divineDefence = new SecondsTimer();
    public final SecondsTimer divineRange = new SecondsTimer();
    public final SecondsTimer divineMagic = new SecondsTimer();

    private final WildernessScoreBoard wildernessScoreBoard = new WildernessScoreBoard();
    private final Prayer prayer = new Prayer();

    public CollectionLog collectionLog = new CollectionLog(this);

    /**
     * The Set of DynamicGameObjects that are visible to this Player.
     */
    private final Set<DynamicGameObject> localObjects = new HashSet<>();

    /**
     * A temporary queue of messages sent during the login process.
     */
    private final Deque<Message> queuedMessages = new ArrayDeque<>();

    /**
     * Castle Wars variables
     **/

    public int castleWarsScore;
    public int castleWarsGamesPlayed;
    public long castleWarsIddleTimer = -1;
    public boolean hasBraceletEffects;
    /* Catapult variables */
    public long castleWarsCatapultDamage;
    public int cwGameCatapultDamage;
    public int totalShotCatapultDamage;
    /* Kill count variables */
    public int cwGameKills;
    public int castleWarsKills;
    /* Capture count variables */
    public int cwGameCaptures;
    public int castleWarsCaptures;

    public boolean usingCatapult;
    public long lastPresetLoad;

    /**
     * This Players appearance ticket.
     */
    private int appearanceTicket = nextAppearanceTicket();
    /**
     * Used for minigames
     */
    private Party currentParty = null;
    /**
     * This appearance tickets for this Player.
     * <p>
     * Added + 1 offset because indices in {@link com.grinder.game.entity.agent.AgentList} start at 1,
     * might change this later to start at 0, not sure if this breaks any updating.
     */
    private final int[] appearanceTickets = new int[WorldConstants.MAXIMUM_PLAYERS + 1];

    private final HashMap<WeaponInterface, WeaponFightType> weaponFightTypeHashMap = new HashMap<>();
    private final ArrayList<PlayerEventListener> listeners = new ArrayList<>();
    private final MessageCache messageCache = new MessageCache();
    private final PlayerMotion motion = new PlayerMotion(this);
    private final PlayerCombat combat = new PlayerCombat(this);
    private final AtomicBoolean firstSequence = new AtomicBoolean(false);
    private final SecondsTimer bountyTeleportTimer = new SecondsTimer();
    private final SecondsTimer chatTimer = new SecondsTimer();
    private final SecondsTimer dragonFireShieldTimer = new SecondsTimer();
    private final List<Punishment> activePunishments = new ArrayList<>();
    private final Queue<ChatMessage> chatMessageQueue = new ConcurrentLinkedQueue<>();
    private final Agility agility = new Agility();
    private final SlayerManager slayer = new SlayerManager();
    private final ItemDegradationManager itemDegradationManager = new ItemDegradationManager(this);
    private final ClueScrollManager clueScrollManager = new ClueScrollManager(this);
    private final MinigameManager minigameManager = new MinigameManager();
    private final SecondsTimer staminaPot = new SecondsTimer();
    private final Farming farming = new Farming(this);
    private final Rules rules = new Rules();
    private final AttributeManager points = new AttributeManager(this);
    private final WelcomeManager welcome = new WelcomeManager();
    private final Teleporting teleport = new Teleporting();
    private final LootingBag lootingBag = new LootingBag(this);
    private final AchievementManager achievements = new AchievementManager();
    private final MonsterKillTracker killTracker = new MonsterKillTracker();
    private final Notes notes = new Notes();
    private final NpcInformation npcInfo = new NpcInformation();
    private final SkillTaskManager skillingTask = new SkillTaskManager();
    private final Stopwatch doorTimer = new Stopwatch();
    private final List<Player> localPlayers = new LinkedList<Player>();
    private final List<NPC> localNpcs = new LinkedList<>();
    private final PacketSender packetSender = new PacketSender(this);
    private final Music music = new Music();
    private final Appearance appearance = new Appearance(this);
    private final SkillManager skillManager = new SkillManager(this);
    private final PlayerRelations relations = new PlayerRelations(this);
    private final PlayerChatSettings chatSettings = new PlayerChatSettings();
    private final EquipmentBonuses bonusManager = new EquipmentBonuses();
    private final QuickPrayers quickPrayers = new QuickPrayers(this);
    private final Inventory inventory = new Inventory(this);
    private final Equipment equipment = new Equipment(this);
    private final PriceChecker priceChecker = new PriceChecker(this);
    private final RunePouch runePouch = new RunePouch(this);
    private final SafeDeposit safeDeposit = new SafeDeposit(this);

    private final Stopwatch clickDelay = new Stopwatch();
    private final Stopwatch lastItemPickup = new Stopwatch();

    private final SecondsTimer yellDelay = new SecondsTimer();
    private final SecondsTimer aggressionTolerance = new SecondsTimer();
    private final SecondsTimer specialAttackRestore = new SecondsTimer();
    private final SecondsTimer forcedLogoutTimer = new SecondsTimer();
    private final SecondsTimer vengeanceEffectTimer = new SecondsTimer();
    private final SecondsTimer vengeanceDelayTimer = new SecondsTimer();
    private final SecondsTimer cursedVaultDelayTimer = new SecondsTimer();
    private final SecondsTimer restoreBoxDelayTimer = new SecondsTimer();
    private final SecondsTimer autoChatBreakTimer = new SecondsTimer();

    private final List<String> recentIPS = new ArrayList<>();
    private final TradeController trading = new TradeController(this);
    private final DuelController dueling = new DuelController(this);
    private final GambleController gambling = new GambleController(this);
    private final BankPin bankpin = new BankPin(this);

    private final Bank[] banks = new Bank[BankConstants.TOTAL_BANK_TABS];

    private final Map<ColorfulItem, Integer[]> colorfulItemMap = ColorfulItem.getDefaultColorfulItemMap();
    private final SecondsTimer votingBonus = new SecondsTimer();
    private final BarrowsManager barrowsManager = new BarrowsManager(this);
    private final Set<BarrowsBrother> killedBarrows = new HashSet<>();

    public final Set<HunterTrap> hunterTraps = new HashSet<>();

    public HunterTrap findHunterTrap(Position p) {
        if (hunterTraps.isEmpty()) {
            return null;
        }

        for (HunterTrap trap : hunterTraps) {
            if (trap.getObject().getPosition().equals(p)) {
                return trap;
            }
        }

        return null;
    }

    public Seat seat;
    public boolean updateColorfulItem = false;
    public boolean wasRunningAgility;
    public boolean shouldSetRunningBack = false;

    public Map<PouchType, EssencePouch> pouches = Arrays
            .stream(PouchType.values())
            .collect(Collectors.toMap(it -> it, PouchType::emptyPouch));

    public TargetStrategy followTarget = new FollowTargetStrategy(this);

    public boolean passingObstacle;
    public int pestControlZeal;
    public int pestControlTotalHit;


    public final Inventory[] presetInventory = {new Inventory(this), new Inventory(this), new Inventory(this), new Inventory(this), new Inventory(this), new Inventory(this), new Inventory(this), new Inventory(this), new Inventory(this), new Inventory(this)};
    public final Equipment[] presetEquipment = {new Equipment(this), new Equipment(this), new Equipment(this), new Equipment(this), new Equipment(this), new Equipment(this), new Equipment(this), new Equipment(this), new Equipment(this), new Equipment(this)};
    public int presetsAmount = 0;


    public boolean presetInterfaceOpen = false;
    public int selectedPresetId = 0;
    public String[] presetNames = new String[]{"Preset 1", "Preset 2", "Preset 3", "Preset 4", "Preset 5", "Preset 6", "Preset 7", "Preset 8", "Preset 9", "Preset 10"};
    public MagicSpellbook[] presetMagicBooks = {MagicSpellbook.NORMAL, MagicSpellbook.NORMAL, MagicSpellbook.NORMAL, MagicSpellbook.NORMAL, MagicSpellbook.NORMAL, MagicSpellbook.NORMAL, MagicSpellbook.NORMAL, MagicSpellbook.NORMAL, MagicSpellbook.NORMAL, MagicSpellbook.NORMAL};

    public int tutorialStage = 0;
    public int pin = -1;
    public int carriedWeight = 0;
    public boolean BLOCK_ALL_BUT_TALKING;
    public Direction cameFromDirection;
    public Position agilityFailGoToLocation;
    public int packetOpcode = -1;
    public int packetSize = -1;
    public Position teleportDestination = null;
    public RandomEventRefreshment selectedRefreshment;
    public int accountID;
    public Agent teleportToCaster = null;
    public DwarfCannon cannon = null;
    public boolean isLoggedIn = false;
    public Position pohPortalReturnPosition = GameConstants.DEFAULT_POSITION.clone();
    public Position brimhavenPillarPosition = null;
    private int hostScore;
    private int otherScore;
    public boolean crossAgilityReady = false;
    private boolean blockLogout = false;
    private boolean hasLogoutTimer = false;
    private volatile boolean pendingLogout = false;
    private volatile boolean setForceLogoutTimer = false;
    private volatile LogoutPolicy logoutPolicy;
    /**
     * Some areas have a 'large' viewport. Which means the player's client is
     * able to render more entities in a larger radius than normal.
     */
    private boolean largeViewport = false;

    private int blockAnimationTicks;

    /**
     * A flag indicating if the region changed in the last cycle.
     */
    private boolean regionChanged;

    /**
     * The centre of the last region the client has loaded.
     */
    private Position lastKnownRegion;

    /**
     * The start tick to identify if player's constructed map or not.
     */
    public int instancedMapTick;

    /**
     * A 13x13 set of region chunks that are rendered for the player. Used for updating new floors
     */
    private Set<RegionCoordinates> renderedRegions = new HashSet<>(13 * 13);
    /**
     * List based on height. Used for resetting certain chunks when altering objects/grounditems and the player is on different height level from the object/grounditem.
     */
    private final List<Set<RegionCoordinates>> resetRegions = new ArrayList<>(Position.HEIGHT_LEVELS);

    /**
     * Used to check if the floor has been loaded for the player.
     */
    public final boolean[] LOADED_FLOOR = new boolean[Position.HEIGHT_LEVELS];

    public List<Set<RegionCoordinates>> getResetRegions() {
        return resetRegions;
    }

    /**
     * A set of region chunk coordinates to tell us which region chunk we must send updates to when spawning an object/grounditem/gfx/projectile
     */
    private final Set<RegionCoordinates> updateRegionChunks = new HashSet<>(13 * 13);

    public Set<RegionCoordinates> getUpdateRegionChunks() {
        return updateRegionChunks;
    }

    private DebugTab tab;
    private ChatMessage currentChatMessage;
    /**
     * The player's game mode.
     */
    private GameMode gameMode = GameMode.NORMAL;
    private SkillActionTask skillAction;
    private Minigame minigame;
    private Item randomItemReward;
    private final Set<String> purchasedTitles = new HashSet<>();
    private int failedBankPinTries;
    private String UID;
    private String macAddress;
    private String snAddress;
    private String hdSerialNumber;
    private InterfaceCloseListener interfaceCloseListener;
    private InstanceDeadItemCollectionBox instanceDeadItemCollectionBox;
    private Presetable currentPreset;
    private Presetable[] presets = new Presetable[Presetables.MAX_PRESETS];
    private String username;
    private String password;
    private String hostAddress;
    private Long longUsername;
    private PlayerSession session;
    private PlayerStatus status = PlayerStatus.NONE;
    private ClanChat currentClanChat;
    private String clanChatName;
    private Dialogue dialogue;
    private Shop shop;
    public int teleGrabCount = 0;
    private boolean allowRegionChangePacket;
    private NPC currentPet;
    private int prayerDrainCounter;
    private EnterSyntax enterSyntax;
    private MagicSpellbook spellbook = MagicSpellbook.NORMAL;
    private boolean teleportInterfaceOpen;
    private DialogueOptions dialogueOptions;
    private Executable dialogueContinueAction;
    private int destroyItem = -1;
    private boolean updateInventory; // Updates inventory on next tick
    private int regionHeight;
    private Optional<Skillable> skill = Optional.empty();
    private Optional<CreationMenu> creationMenu = Optional.empty();
    private Timestamp creationDate;
    private SkullType skullType = SkullType.WHITE_SKULL;
    private SpecialAttackType specialAttackType;
    private Date lastDeath;
    private int blowpipeScales;
    private int safeTimer = 180;
    private NPC currentBrother;
    private boolean targetTeleportUnlocked;
    private long timePlayed;
    private int[] yellColors = new int[]{0x0094ff, 0, 0x0094ff, 0, 0x13c400, 0, 0x13c400, 0};
    private String yellTitle = "Yell";
    /**
     * TODO: convert to attributes system
     */
    private final List[] mysteryBoxItemsList = new List[SpinMysteryBoxType.values().length];
    private AchievementType lastCompletedAchievement;
    private String lastDropTableInputText;
    private List<SearchDropTableWorker.NpcDropTable> dropTableResults = new ArrayList<>();
    private boolean dropTablesItemSearch;
    private PlayerRights rights = PlayerRights.NONE;
    private Map<String, Long> attackedBy = new HashMap<>();
    /**
     * The crown that the player has chosen to be displayed.
     */
    private int crown;
    private String title = "";
    private Position oldPosition = null;
    private BarrowsBrother finalBarrow = Misc.randomTypeOfList(BarrowsBrother.getBrothersList());
    private boolean spawnedBarrows;
    private transient Object inputPromise;
    private transient Consumer<?> inputHandler;
    private House poh = new House();
    private boolean inHouseDungeon = false;

    /**
     * Create a new {@link Player} connected with a client.
     *
     * @param session the {@link PlayerSession} that manages this player.
     */
    public Player(PlayerSession session) {
        this();
        this.session = session;
        traversal = new SmallTraversal(TraversalType.Land, false);
        interactTarget = new RectangleTargetStrategy(this);
        motion.updateCollision();
        subscribe(event -> {
            PlayerActions.INSTANCE.handleEvent(this, event);
            return false;
        });
    }

    /**
     * Create a new {@link Player}.
     */
    public Player() {
        super(EntityType.PLAYER, World.getStartPosition().clone());

        int renderedSize = 13 * 13;
        for (int height = 0; height < Position.HEIGHT_LEVELS; height++) {
            resetRegions.add(new HashSet<>(renderedSize));
        }
    }

    /**
     * Generates the next appearance ticket.
     *
     * @return The next available appearance ticket.
     */
    private static int nextAppearanceTicket() {
        if (appearanceTicketCounter.incrementAndGet() == 0) {
            appearanceTicketCounter.set(1);
        }
        return appearanceTicketCounter.get();
    }

    public WildernessScoreBoard getWildernessScoreBoard() {
        return wildernessScoreBoard;
    }

    public Prayer getPrayer() {
        return prayer;
    }

    public boolean isLargeViewport() {
        return largeViewport;
    }

    public void setLargeViewport(boolean largeViewport) {
        this.largeViewport = largeViewport;
    }

    public void setBlockAnimationTicks(int blockAnimationTicks) {
        this.blockAnimationTicks = blockAnimationTicks;
    }

    /**
     * Sends a {@link Message} to this player.
     *
     * @param message The message..
     */
    public void send(Message message) {
        if (!isActive()) {
            queuedMessages.add(message);
            return;
        }

        if (!queuedMessages.isEmpty()) {
            CollectionUtil.pollAll(queuedMessages, session::dispatchMessage);
        }

        session.dispatchMessage(message);
    }

    /**
     * Adds the specified {@link DynamicGameObject} to this Player's {@link Set}
     * of visible objects.
     *
     * @param object The DynamicGameObject.
     */
    public void addObject(DynamicGameObject object) {
        localObjects.add(object);
        object.addTo(this);
    }

    /**
     * Sets the region changed flag.
     *
     * @param regionChanged Whether or not the region has changed.
     */
    public void setRegionChanged(boolean regionChanged) {
        this.regionChanged = regionChanged;
    }

    /**
     * Checks if the region has changed.
     *
     * @return {@code true} if so, {@code false} if not.
     */
    public boolean hasRegionChanged() {
        return regionChanged;
    }

    /**
     * Checks if this player has ever known a region.
     *
     * @return {@code true} if so, {@code false} if not.
     */
    public boolean hasLastKnownRegion() {
        return lastKnownRegion != null;
    }

    public Set<RegionCoordinates> getRenderedRegions() {
        return renderedRegions;
    }

    public void setRenderedRegions(Set<RegionCoordinates> rendered) {
        this.renderedRegions = rendered;
    }

    /**
     * Gets this Players appearance ticket.
     *
     * @return This Players appearance ticket.
     */
    public int getAppearanceTicket() {
        return appearanceTicket;
    }

    /**
     * Gets all of this Players appearance tickets.
     *
     * @return All of this Players appearance tickets.
     */
    public int[] getAppearanceTickets() {
        return appearanceTickets;
    }

    @Override
    public void setVenomDamage(int venomDamage) {
        super.setVenomDamage(venomDamage);
        if (venomDamage > 0) {
            packetSender.sendOrbConfig();
        }
    }

    @Override
    public void setPoisonDamage(int poisonDamage) {
        super.setPoisonDamage(poisonDamage);
        if (poisonDamage > 0) {
            packetSender.sendOrbConfig();
        }
    }

    public void drainEnergy() {
        if (isRidingCarpet()) // Skip if you are on a carpet
            return;
        if (isRunning()) {
            double clamped_weight = Math.max(0, Math.min(64, carriedWeight));
            int energyLoss = (int) (67 + Math.floor((67 * clamped_weight) / 64));

            if (staminaPot.secondsRemaining() > 0) {
                energyLoss *= 0.3;
            }
            int runEnergy = getRunEnergy();
            runEnergy = runEnergy > energyLoss ? runEnergy - energyLoss : 0;
            setRunEnergy(runEnergy);
            getPacketSender().sendOrbConfig();
            if (runEnergy <= 0) {
                getPacketSender().sendRunStatus();
            }
        }
    }

    public boolean isRunning() {
        return EntityExtKt.getBoolean(this, Attribute.IS_RUNNING, false);
    }

    public PacketSender getPacketSender() {
        return packetSender;
    }

    public Player setRunning(boolean isRunning) {
        EntityExtKt.setBoolean(this, Attribute.IS_RUNNING, isRunning, false);
        return this;
    }

    public ClueScrollManager getClueScrollManager() {
        return clueScrollManager;
    }

    /**
     * Sets the lootingBag
     *
     * @return the lootingBag
     */
    public LootingBag getLootingBag() {
        return lootingBag;
    }

    public void sendMessage(String privateMessage) {
        getPacketSender().sendMessage(privateMessage, 800);
    }

    /**
     * Actions that should be done when this character is added to the world.
     */
    @Override
    public void onAdd() {
    }

    /**
     * Actions that should be done when this character is removed from the
     * world.
     */
    @Override
    public void onRemove() {
        getMotion().removeCollision();
        notify(PlayerEvents.LOGGED_OUT);
    }

    /**
     * Updates the appearance for this Player.
     */
    @Override
    public void updateAppearance() {
        appearanceTicket = nextAppearanceTicket();
        blockSet.add(UpdateBlock.Companion.createAppearanceBlock(this, true));
    }

    @Override
    public PlayerMotion getMotion() {
        return motion;
    }

    @Override
    public PlayerCombat getCombat() {
        return combat;
    }

    @Override
    public void debug(String message) {
        if (Config.enable_debug_messages)
            sendMessage("@red@" + message);
    }

    @Override
    public int getHitpoints() {
        return skillManager.getCurrentLevel(Skill.HITPOINTS);
    }

    @Override
    public int getMaxHitpoints() {
        return skillManager.getMaxLevel(Skill.HITPOINTS);
    }

    @Override
    public void heal(int amount) {
        int currentHp = skillManager.getCurrentLevel(Skill.HITPOINTS);
        int maxHp = skillManager.getMaxLevel(Skill.HITPOINTS);

        // If using Amulet of the Damned and Guthan's set, your max HP increases by 10.
        if (EquipmentUtil.hasAnyAmuletOfTheDamned(equipment)) {
            if (EquipmentUtil.isWearingGuthanSet(equipment)) {
                maxHp += 10;
            }
        }

        if (amount + currentHp > maxHp) {
            amount = maxHp - currentHp;
        }

        setHitpoints(getHitpoints() + amount);
    }

    @Override
    public Agent setHitpoints(int hitpoints) {
        if (isDying()) {
            return this;
        }

        if (EntityExtKt.getBoolean(this, Attribute.INVULNERABLE, false)) {
            if (skillManager.getCurrentLevel(Skill.HITPOINTS) > hitpoints) {
                return this;
            }
        }

        skillManager.setCurrentLevel(Skill.HITPOINTS, hitpoints, true);
        packetSender.sendSkill(Skill.HITPOINTS);
        if (getHitpoints() <= 0 && !isDying())
            appendDeath();
        return this;
    }

    @Override
    public Skills getSkills() {
        return skillManager.getSkills();
    }

    @Override
    public void appendDeath() {
        if (!isDying()) {
            TaskManager.submit(new PlayerDeathTask(this));
            setIsDying(true);
        }
    }

    @Override
    public int getBaseAttackSpeed() {

        int speed = getCombat().getWeapon().getSpeed();

        var fightType = getCombat().getFightType();
        if (fightType.toString().toLowerCase().contains("rapid")
                || fightType.equals(WeaponFightType.CHIN_MED)
                || fightType.equals(WeaponFightType.LIZARD_FLARE)
                || fightType.equals(WeaponFightType.SALAMANDER_FLARE)) {
            speed--;
        }

        return speed;
    }

    @Override
    public int getAttackAnim() {
        int anim = getCombat().getFightType().getAnimation();
        if (this.getCombat().getTarget() != null && this.getCombat().getTarget().isPlayer()) {
            this.getPacketSender().sendAreaPlayerSound(getCombat().getFightType().getSound());
            if (this.isPlayer() && this.getCombat().getRangedWeapon() == RangedWeapon.DARK_BOW) {
                this.getPacketSender().sendAreaPlayerSound(getCombat().getFightType().getSound(), 20);
            }
        } else {
            this.getPacketSender().sendSound(getCombat().getFightType().getSound());
            if (this.isPlayer() && this.getCombat().getRangedWeapon() == RangedWeapon.DARK_BOW) {
                this.getPacketSender().sendSound(getCombat().getFightType().getSound(), 20);
            }

        }
        // Get player attack sound
        return anim;
    }

    @Override
    public int getBlockAnim() {
        final Item shield = getEquipment().getItems()[EquipmentConstants.SHIELD_SLOT];
        final Item weapon = getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT];
        final ItemDefinition definition = shield.getId() > 0 ? shield.getDefinition() : weapon.getDefinition();
        return definition.getBlockAnim();
    }

    @Override
    public double protectionPrayerReductionMultiplier(AttackType type) {
        return CombatPrayer.PRAYER_DAMAGE_REDUCTION_RATIO_VS_PLAYER;
    }

    @Override
    public void performAnimation(Animation animation) {
        if (animation == null)
            return;
        if (blockAnimationTicks > 0)
            return;

        blockAnimationTicks = animation.getBlockNextAnimationTicks();

        super.performAnimation(animation);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Player)) {
            return false;
        }
        Player p = (Player) o;
        return p.getUsername().equals(username);
    }

    @Override
    public String toString() {
        return "Player{" +
                "index=" + getIndex() +
                ", name=" + username +
                '}';
    }

    public void notify(PlayerEvent event) {
        try {
            if (event != null) {
                if (listeners != null)
                    listeners.removeIf(playerEventListener -> playerEventListener.on(event));
            }
        } catch (Exception ex) {
            System.out.println("Error in notify in player.java " + ex);
        }
    }

    /**
     * This method is invoked at the start of the {@link SequentialClientSynchronizer}.
     */
    public void preSequence() {

        setLastPosition(getPosition());

        motion.sequenceMovement();

        if (blockAnimationTicks > 0)
            blockAnimationTicks--;

        incrementPlayTime();

        BirdHouseActions.pulseBirdHouses(this);

        farming.process();
        timerRepository.process();

        prayer.sequence(this);
        Hunter.sequence(this);
    }

    private void incrementPlayTime() {
        timePlayed += TimeUnits.TICK.getMilisecondValue();
    }

    /**
     * This method is invoked after {@link NPC#preSequence() npc presequencing} in {@link SequentialClientSynchronizer}.
     *
     * @param benchMarker a {@link SimpleBenchMarker} for performance monitoring.
     */
    public void sequence(SimpleBenchMarker benchMarker) {

        if (pendingLogout && successfulLogout())
            return;

        if (firstSequence.get()) {
            sendLoginPackets();
            firstSequence.set(false);
        }

        motion.sequence();
        benchMarker.mark("motion");

        combat.sequence();
        combat.sequenceHitsAndDamages();

        benchMarker.mark("combat");

        AreaManager.sequence(this);
        benchMarker.mark("area");

        BountyHunterManager.sequence(this);
        benchMarker.mark("bountyhunter");

        PestControl.sequence(this);

        updateChatMessage();
        updateInventory();
        benchMarker.mark("chat/inventory");

        sequenceMiscellaneousProcesses();
        SkillStats.sequenceTemporaryStats(this);
        sequenceRunEnergy();
        benchMarker.mark("misc/stats/energy");

        sequenceAttackedBy();
    }

    private boolean successfulLogout() {

        final boolean canLogoutSafely = canLogout();
        final boolean preventCombatLogout = logoutPolicy != LogoutPolicy.IMMEDIATE && combat.isInCombat();
        final boolean delayLogout = logoutPolicy == LogoutPolicy.IDLE
                || blockLogout
                || preventCombatLogout;

        if (setForceLogoutTimer) {
            timerRepository.register(TimerKey.FORCE_LOGOUT_TIMER, FORCE_DISCONNECT_TICKS);
            setForceLogoutTimer = false;
        }

        if (logoutPolicy == LogoutPolicy.IDLE) {
            final int ticksTillLogout = timerRepository.left(TimerKey.FORCE_LOGOUT_TIMER);
            final int secondTillLogout = TimeUtil.GAME_CYCLES.toSeconds(ticksTillLogout);
            if (secondTillLogout % 10 != 0)
                sendMessage("Your account will automatically logout in " + secondTillLogout + " seconds.");
        }

        final boolean forceTimerFinished = !timerRepository.has(TimerKey.FORCE_LOGOUT_TIMER);
        final boolean forceLogout = logoutPolicy == LogoutPolicy.IMMEDIATE || forceTimerFinished;

        if (rights == PlayerRights.DEVELOPER) {
            Server.getLogger().info("[" + logoutPolicy + "]: sequencing developer logout " + this + " " +
                    "preventCombatLogout = " + preventCombatLogout + ", " +
                    "delayLogout = " + delayLogout + ", " +
                    "forceLogout = " + forceLogout + " " + (!forceTimerFinished ? "| forced in " + timerRepository.left(TimerKey.FORCE_LOGOUT_TIMER) : ""));
        }

        if (!delayLogout || forceLogout) {
            if (forceLogout || canLogoutSafely) {
                forcedLogoutTimer.stop();
                if (!World.getPlayerRemovalQueue().contains(this))
                    World.getPlayerRemovalQueue().add(this);
                return true;
            }
        }
        return false;
    }

    private void sendLoginPackets() {
        relations.onLogin();
        relations.updateLists(true);
        chatSettings.onLogin(this);
    }

    /**
     * Send queued chat messages
     */
    private void updateChatMessage() {
        if (!chatMessageQueue.isEmpty()) {
            currentChatMessage = chatMessageQueue.poll();
            blockSet.add(UpdateBlock.Companion.createChatBlock(this));
        } else
            currentChatMessage = null;
    }

    /**
     * Update the inventory in case of an update being requested.
     */
    private void updateInventory() {
        if (isUpdateInventory()) {
            getInventory().refreshItems();
            setUpdateInventory(false);
        }
    }

    private void sequenceMiscellaneousProcesses() {

        if (status == PlayerStatus.AWAY_FROM_KEYBOARD)
            performAnimation(Animation.AFK_ANIMATION);

        if (timePlayed % (TimeUnits.TICK.getMilisecondValue() * 2) == 0)
            itemDegradationManager.sequence();

        Optional.ofNullable(tab).ifPresent(DebugTab::update);
    }

    /**
     * Increase run energy
     */
    private void sequenceRunEnergy() {
        int runEnergy = EntityExtKt.getInt(this, Attribute.RUNNING_ENERGY, 10000);
        if (runEnergy < PlayerMotion.MAX_RUN_ENERGY && (motion.completed() || !isRunning())) {
            double increasePerTick = Math.floor(skillManager.getCurrentLevel(Skill.AGILITY) / 6.0) + 8;

            increasePerTick *= energyRegenIncreasePerRank();

            if (EquipmentUtil.isWearingAnyGracefulSet(this)) {
                increasePerTick *= 1.3; // 30% faster
            }

            int newRunEnergy = (int) (runEnergy + increasePerTick);
            runEnergy = Math.min(newRunEnergy, PlayerMotion.MAX_RUN_ENERGY);
            setRunEnergy(runEnergy);
            packetSender.sendOrbConfig();
        }
    }

    private double energyRegenIncreasePerRank() {
        if (PlayerUtil.isBronzeMember(this)) {
            return 1.1;
        } else if (PlayerUtil.isRubyMember(this)) {
            return 1.2;
        } else if (PlayerUtil.isTopazMember(this)) {
            return 1.3;
        } else if (PlayerUtil.isAmethystMember(this)) {
            return 1.4;
        } else if (PlayerUtil.isLegendaryMember(this) || PlayerUtil.isPlatinumMember(this) || PlayerUtil.isDiamondMember(this) || PlayerUtil.isTitaniumMember(this)) {
            return 1.5;
        }
        return 1;
    }

    /**
     * Called by the world's login queue!
     */
    public void onLogin() {

        //AttributeableItemRemovalKt.checkAccount(this);

        try {

            if (PlayerUtil.isStaff(this))
                System.out.println("[Player][" + username + "][" + hostAddress + "]: logged in as staff member.");
            else
                System.out.println("[Player][" + username + "][" + hostAddress + "]: logged in as player.");

            getPacketSender().sendDetails();
            getPacketSender().sendMessage("Welcome to " + GameConstants.NAME + ".");
            //updateLocalFaces(); // Do we need to enable this?
            long totalExp = 0;
            for (Skill skill : Skill.values()) {
                int expectedMaxLevel = SkillUtil.calculateLevelForExperience(skillManager.getExperience(skill.ordinal()));
                if (skillManager.getMaxLevel(skill) != expectedMaxLevel) {
                    skillManager.setCurrentLevel(skill, expectedMaxLevel, false);
                    skillManager.setMaxLevel(skill, expectedMaxLevel, false);
                }
                getSkillManager().updateSkill(skill);
                totalExp += getSkillManager().getExperience(skill);
            }
            getPacketSender().sendTotalExp(totalExp);
            getPacketSender().sendTabs();

            // Reset prayer configs...
            PrayerHandler.resetAll(this);
            getPacketSender().sendConfig(709, PrayerHandler.canUse(this, PrayerType.PRESERVE, false) ? 1 : 0);
            getPacketSender().sendConfig(711, PrayerHandler.canUse(this, PrayerType.RIGOUR, false) ? 1 : 0);
            getPacketSender().sendConfig(713, PrayerHandler.canUse(this, PrayerType.AUGURY, false) ? 1 : 0);


            // Send preset config
            //getPacketSender().sendConfig(987, isOpenPresetsOnDeath() ? 0 : 1);
            ItemColorCustomizer.onLogin(this);
            getEquipment().refreshItems();
            EquipPacketListener.resetWeapon(this);
            WeaponInterfaces.INSTANCE.assign(this);
            getCombat().reset(false);
            EquipmentBonuses.update(this);

            // Refresh item containers..
            getInventory().refreshItems();
            getEquipment().refreshItems();
            getRunePouch().refreshItems();


            // Interaction options on right click...
            getPacketSender().sendInteractionOption("Follow", 3, false);
            getPacketSender().sendInteractionOption("Trade With", 4, false);


            // Sending run energy attributes...
            getPacketSender().sendRunStatus();
            getPacketSender().sendOrbConfig();
            //updateLocalFaces();
            // Sending player's rights..
            getPacketSender().sendRights();

            // Close all interfaces, just in case...
            getPacketSender().sendInterfaceRemoval();

            // Update weapon interface configs
            getPacketSender().sendConfig(getCombat().getFightType().getParentId(), getCombat().getFightType().getChildId())
                    .sendConfig(172, getCombat().retaliateAutomatically() ? 1 : 0).updateSpecialAttackOrb();

            // Reset autocasting
            SpellCasting.setSpellToCastAutomatically(this, null);

            Item weapon = getEquipment().getItems()[EquipmentConstants.WEAPON_SLOT];
            if (weapon.getId() == ItemID.TRIDENT_OF_THE_SEAS_FULL_ || weapon.getId() == ItemID.TRIDENT_OF_THE_SEAS || weapon.getId() == 22288) {
                SpellCasting.setSpellToCastAutomatically(this, CombatSpellType.TRIDENT_OF_THE_SEAS);
            } else if (weapon.getId() == ItemID.TRIDENT_OF_THE_SWAMP || weapon.getId() == 22292) {
                SpellCasting.setSpellToCastAutomatically(this, CombatSpellType.TRIDENT_OF_THE_SWAMP);
            } else if (weapon.getId() == 22323) {
                SpellCasting.setSpellToCastAutomatically(this, CombatSpellType.SANGUINESTI_STAFF);
            } else if (weapon.getId() == ItemID.HOLY_SANGUINESTI_STAFF) {
                SpellCasting.setSpellToCastAutomatically(this, CombatSpellType.HOLY_SANGUINESTI_STAFF);
            } else if (weapon.getId() == 22555) {
                SpellCasting.setSpellToCastAutomatically(this, CombatSpellType.THAMMARON_SCEPTRE);
            }

            // Handle timerRepository and run tasks
            if (isVenomed()) {
                TaskManager.submit(new VenomEffectTask(this));
            } else if (isPoisoned()) {
                TaskManager.submit(new PoisonEffectTask(this));
            }

            if (getSpecialPercentage() < 100) {
                TaskManager.submit(new RestoreSpecialAttackTask(this));
            }
            TaskManager.submit(new RestoreSpecialAttackTask(this));
            if (!getVengeanceEffect().finished()) {
                getPacketSender().sendEffectTimer(getVengeanceEffect().secondsRemaining(), EffectTimer.VENGEANCE);
            }
            if (!getCombat().getFireImmunityTimer().finished()) {
                getPacketSender().sendEffectTimer(getCombat().getFireImmunityTimer().secondsRemaining(),
                        EffectTimer.ANTIFIRE);
            }
            if (!getCombat().getSuperFireImmunityTimer().finished()) {
                getPacketSender().sendEffectTimer(getCombat().getSuperFireImmunityTimer().secondsRemaining(),
                        EffectTimer.ANTIFIRE);
            }
            if (!getCombat().getTeleBlockTimer().finished()) {
                getPacketSender().sendEffectTimer(getCombat().getTeleBlockTimer().secondsRemaining(),
                        EffectTimer.TELE_BLOCK);
            }
            if (!getVotingBonusTimer().finished()) {
                getPacketSender().sendEffectTimer(getVotingBonusTimer().secondsRemaining(),
                        EffectTimer.VOTING_BONUS);
            }
            if (!getCombat().getPoisonImmunityTimer().finished()) {
                getPacketSender().sendEffectTimer(getCombat().getPoisonImmunityTimer().secondsRemaining(),
                        EffectTimer.ANTIPOISON);
            }
            if (!getCombat().getVenomImmunityTimer().finished()) {
                getPacketSender().sendEffectTimer(getCombat().getVenomImmunityTimer().secondsRemaining(),
                        EffectTimer.ANTI_VENOM);
            }
            if (!getCombat().getAggressivityTimer().finished()) {
                getPacketSender().sendEffectTimer(getCombat().getAggressivityTimer().secondsRemaining(),
                        EffectTimer.AGGRESSIVITY);
            }
            if (getLastDeath() != null) {
                int timerSeconds = (int) ((double) ItemOnGroundManager.STATE_UPDATE_DELAY_ON_DEATH * 0.6);
                long secondsDiff = (new Date().getTime() - getLastDeath().getTime()) / 1000;
                int secondsLeft = timerSeconds - (int) secondsDiff;
                if (secondsLeft > 0) {
                    getPacketSender().sendEffectTimer(secondsLeft, EffectTimer.DEATH_ITEMS_DESPAWN);
                }
            }

            GlobalClanChatManager.onLogin(this);

            if (isInTutorial()) {
                setOnTutorialMode(false);
                moveTo(new Position(3088, 3501, 0));
                sendMessage("You are no longer on the tutorial. Speak to Grinderscape guide to start again.");
            }

            Notes.display(this);

            if (isNewPlayer()) {

                if (LoginControlCommand.Companion.getLOCK_ALL_NEW_PLAYERS())
                    PunishmentManager.submit(username, PunishmentType.LOCK);

                EntityExtKt.setBoolean(this, Attribute.CHANGED_PASS, true, true);
                WelcomeManager.welcome(this, WelcomeStage.WELCOME);
            }

            if (World.countPlayersOnline() > ServerIO.MAX_PLAYERS_ONLINE) {
                ServerIO.MAX_PLAYERS_ONLINE = World.countPlayersOnline();
                ServerIO.save();
            }

            if (getWelcome().getWelcome() == null) {
                getWelcome().setWelcome(new Welcome());
            }

            // Send quest tab information
            QuestTab.onLogin(this);

            AchievementManager.sendTab(this);

            AttributeManager.onLogin(this);

            Broadcast.onLogin(this);

            MonsterHunting.INSTANCE.onLogin(this);

/*            if (username.equals("Mod Hellmage")) {
                setInvulnerable(true);
            }*/

            if (Voting.INSTANCE.hasPendingVote(getAsPlayer()) && GameConstants.MYSQL_ENABLED && !isNewPlayer() && !getGameMode().isSpawn()
                    && EntityExtKt.passedTime(this, Attribute.LAST_VOTE, 11, TimeUnit.HOURS, false, false)) {
                TaskManager.submit(new Task(15) {
                    @Override
                    public void execute() {
                        stop();
                        // Send broadcast
                        Broadcast.broadcastSingle(getAsPlayer(), 250, "You're eligible to vote for the server for great rewards. Click here!", "https://www.grinderscape.org/vote/");
//                    if (!busy() && !getCombat().isInCombat() && !isAccountFlagged() && getMinigame() == null && getInterfaceId() == -1 && getDialogue() == null && !isInTutorial()) {
//                        new DialogueBuilder(DialogueType.NPC_STATEMENT).setNpcChatHead(NpcID.QUARTERMASTER)
//                                .setExpression(DialogueExpression.SAD_HEAD_BOW)
//                                .setText("Sorry to bother you, but you haven't voted for today.", "This is just a friendly reminder, " + getAsPlayer().getUsername() +"!").start(getAsPlayer());
//                    }
                    }
                });
            }

            if (getGameMode().isSpawn()) {
                TaskManager.submit(new Task(10) {
                    @Override
                    public void execute() {
                        stop();
                        Broadcast.broadcastSingle(getAsPlayer(), 250, "You can use ::presets ::spawn ItemName ::bank and ::shops", "");
                        new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER)
                                .setText("You can load your custom preset with custom setup even", "if you don't have the gear in the bank. Use @red@::presets.")
                                .add(DialogueType.ITEM_STATEMENT_NO_HEADER)
                                .setText("After you die you can retype ::presets to re-load everything in a single click.")
                                .start(getAsPlayer());
//                        sendMessage("@red@ You can load your custom preset with custom setup even if you don't have the gear in the bank. Use ::presets.");
//                        sendMessage("@red@ After you die you can type ::presets to re-load your last set even if its custom in one click.");
                    }
                });
            }

            if (getPoints().get(Points.PET) > 0) {
                PetHandler.drop(this, getPoints().get(Points.PET), false);
            }
            if (AreaManager.DuelFightArena.contains(this)) {
                if (getStatus() == PlayerStatus.DUELING) {
                    setStatus(PlayerStatus.NONE);
                }
                if (getDueling().inDuel()) {
                    getDueling().loseDuel();
                }
                sendMessage("You will be teleported out of here in 5 seconds!");
                BLOCK_ALL_BUT_TALKING = true;
                getCombat().reset(true);
                TaskManager.submit(new Task(9) {
                    @Override
                    public void execute() {
                        BLOCK_ALL_BUT_TALKING = false;
                        moveTo(new Position(3361 + Misc.getRandomInclusive(10), 3275, 0));
                        sendMessage("You have been teleported outside the Duel arena!");
                        stop();
                    }
                });
            }

            if (getHitpoints() <= 0)
                setHitpoints(1);

            RecentUpdates.onLogin(this);

            TaskManager.submit(new SavePlayerTask(this));
            TaskManager.submit(new KillClearanceTask(this));

            decreaseStats.start(60);
            increaseStats.start(60);

            updateCarriedWeight();

            Lottery.checkUncollectedWinnings(this);

            if ((!recentIPS.contains(getHostAddress()) && getBankpin().requireBankPin()) || isAccountFlagged()) {
                // moveTo(GameConstants.DEFAULT_POSITION);
                flagAccount();
            }

            if (getGameMode().isOneLife() && fallenOneLifeGameMode()) {
                BLOCK_ALL_BUT_TALKING = true;
                setFallenOneLifeGameMode(true);
                packetSender.sendInterfaceRemoval();
                sendMessage("Your account has fallen as a One life game mode and can no longer do any actions.");
            }

            if (getTitle() == "@red@Ruby member" && !PlayerUtil.isRubyMember(this)) {
                setTitle("");
            }
            if (getCrown() == 9 && !PlayerUtil.isRubyMember(this)) {
                setCrown(0);
            }

            if (getTimePlayed(TimeUnits.HOUR) >= 72) {
                AchievementManager.processFor(AchievementType.ANCIENT_SURVIVOR, this);
            }
            if (getGameMode().isUltimate()) {
                if (!getBank(0).isEmpty()) {
                    Banking.wipe(this);
                    Logging.log("UIMWIPE", "" + getUsername() + " bank has been wiped on login due to its not empty.");
                }
            }
            if (!isNewPlayer()) {
                //LoginTips.sendLoginTip(this);
                getPacketSender().sendJinglebitMusic(23, 0); // XMAS LOGIN (REMOVE AFTER XMAS)
            }
            if (EntityExtKt.getBoolean(this, Attribute.HAS_PENDING_RANDOM_EVENT, false)) {
                RandomEvent.FOOD_PUZZLE.triggeronLogin(this);
            }
            if (EntityExtKt.getBoolean(this, Attribute.HAS_TRIGGER_RANDOM_EVENT, false)) {
                moveTo(new Position(2640 + Misc.random(2), 10024 + Misc.random(2), 0));
            }
            if (EntityExtKt.getBoolean(this, Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
                RandomEvent.REFRESHMENTS_PUZZLE.triggeronLogin(this);
            }

        } catch (Exception e) {
            e.printStackTrace();
            requestClientLogout();
        }

        DailyLoginRewardManager.onLogin(this);

        if (GameConstants.BONUS_DOUBLE_EXP_WEEKEND || GameConstants.BONUS_DOUBLE_REWARDS) {
            //sendMessage("<img=779><col=12353176> [Weekend event]: +25% combat experience, 0% tax rates, and 2x cash from thieving stalls!");
            Broadcast.broadcastSingle(getAsPlayer(), 120, "[Weekend]: +25% combat experience, 0% tax rates, and 2x thieving cash!", "");
        }
        if (GameConstants.TRIPLE_VOTING_POINTS_EVENT) {
            // sendMessage("<img=779><col=12353176> [First 3 Days of The Month]: TRIPLE 3x rewards from voting!");
            Broadcast.broadcastSingle(getAsPlayer(), 120, "[First 3 Days of The Month]: TRIPLE 3x rewards from voting!", "https://www.grinderscape.org/vote/");
        }

        this.isLoggedIn = true;
        EntityExtKt.markTime(this, Attribute.LAST_LOGIN);

        if (!isNewPlayer()) {
            BankUtil.validateBank(username, banks);
            DupeDetector.INSTANCE.check(this);
            VotingTicket.checkVotingTickets(this);
        }

        final Attribute attribute = getAttributes().get(Attribute.DEBUG_TYPE);
        if (attribute != null) {
            final String type = (String) attribute.getValueHolder().getValue();
            toggleDebugging(DebugType.valueOf(type));
        }

        // Update website logging on homepgae
        new UpdateOnlinePlayers(
                SQLManager.Companion.getINSTANCE(),
                username,
                PlayerUtil.isStaff(this) ? "staff" : EntityExtKt.getBoolean(this, Attribute.MIDDLEMAN, false) ? "middleman" : "",
                true,
                rights
        ).schedule(this);

        if (!getMacAddress().equals("50-46-5D-06-7E-5F") && !PlayerUtil.isDeveloper(this)
                && !getUsername().contains("3lou ")) {
            // Database Logging
            new DatabaseLoginLogs(
                    SQLManager.Companion.getINSTANCE(),
                    username,
                    getHostAddress(),
                    getMacAddress()
            ).schedule(this);
        }

        InstanceManager.onLogin(this);


        //fixPlayerItems();

        FightCave.onLogin(this);
        AquaisNeige.onLogin(this);
        TaskManager.submit(2, () -> {
            if (CastleWars.isInCastleWars(this) || CastleWars.isInCastleWarsLobby(this)) {
                CastleWars.loginPlayer(this);
            }

            if (!PestControl.PEST_CONTROL_INSTANCE.outsideMapInstance(this)) {
                moveTo(new Position(2657, 2639, 0));
            }

            //PestControl.removePlayerFromGame(this);
            if (getPosition().inside(2660, 2638, 2663, 2643)) {
                moveTo(new Position(2657, 2639, 0));
            }
        });

        notify(new PlayerLoginEvent(this));

    }

    private void fixPlayerItems() {

        if (!getGameMode().isOneLife()) {
            if (getInventory().contains(ItemID.GROUP_IRON_HELM))
                getInventory().delete(new Item(ItemID.GROUP_IRON_HELM));

            if (getInventory().contains(ItemID.GROUP_IRON_PLATEBODY))
                getInventory().delete(new Item(ItemID.GROUP_IRON_PLATEBODY));

            if (getInventory().contains(ItemID.GROUP_IRON_PLATELEGS))
                getInventory().delete(new Item(ItemID.GROUP_IRON_PLATELEGS));

            if (getInventory().contains(ItemID.GROUP_IRON_BRACERS))
                getInventory().delete(new Item(ItemID.GROUP_IRON_BRACERS));

            if (getInventory().contains(13319))
                getInventory().delete(new Item(13319));

            if (getInventory().contains(15716))
                getInventory().delete(new Item(15716));


            // equipment
            if (getEquipment().contains(ItemID.GROUP_IRON_HELM))
                getEquipment().delete(getEquipment().atSlot(EquipSlot.HAT), true);

            if (getInventory().contains(ItemID.GROUP_IRON_PLATEBODY))
                getEquipment().delete(getEquipment().atSlot(EquipSlot.CHEST), true);

            if (getInventory().contains(ItemID.GROUP_IRON_PLATELEGS))
                getEquipment().delete(getEquipment().atSlot(EquipSlot.LEGS), true);

            if (getInventory().contains(ItemID.GROUP_IRON_BRACERS))
                getEquipment().delete(getEquipment().atSlot(EquipSlot.HANDS), true);

            if (getInventory().contains(15716))
                getEquipment().delete(getEquipment().atSlot(EquipSlot.WEAPON), true);
        }

        if (!getGameMode().isRealism()) {
            if (getInventory().contains(ItemID.HARDCORE_GROUP_IRON_HELM))
                getInventory().delete(new Item(ItemID.HARDCORE_GROUP_IRON_HELM));

            if (getInventory().contains(ItemID.HARDCORE_GROUP_IRON_PLATEBODY))
                getInventory().delete(new Item(ItemID.HARDCORE_GROUP_IRON_PLATEBODY));

            if (getInventory().contains(ItemID.HARDCORE_GROUP_IRON_PLATELEGS))
                getInventory().delete(new Item(ItemID.HARDCORE_GROUP_IRON_PLATELEGS));

            if (getInventory().contains(ItemID.HARDCORE_GROUP_IRON_BRACERS))
                getInventory().delete(new Item(ItemID.HARDCORE_GROUP_IRON_BRACERS));

            if (getInventory().contains(13319))
                getInventory().delete(new Item(13319));

            if (getInventory().contains(15717))
                getInventory().delete(new Item(15717));

            if (getInventory().contains(15718))
                getInventory().delete(new Item(15718));

            if (getInventory().contains(15719))
                getInventory().delete(new Item(15719));

            // equipment
            if (getEquipment().contains(ItemID.HARDCORE_GROUP_IRON_HELM))
                getEquipment().delete(getEquipment().atSlot(EquipSlot.HAT), true);

            if (getInventory().contains(ItemID.HARDCORE_GROUP_IRON_PLATEBODY))
                getEquipment().delete(getEquipment().atSlot(EquipSlot.CHEST), true);

            if (getInventory().contains(ItemID.HARDCORE_GROUP_IRON_PLATELEGS))
                getEquipment().delete(getEquipment().atSlot(EquipSlot.LEGS), true);

            if (getInventory().contains(ItemID.HARDCORE_GROUP_IRON_BRACERS))
                getEquipment().delete(getEquipment().atSlot(EquipSlot.HANDS), true);

            if (getInventory().contains(15717))
                getEquipment().delete(getEquipment().atSlot(EquipSlot.WEAPON), true);

            if (getInventory().contains(15718))
                getEquipment().delete(getEquipment().atSlot(EquipSlot.WEAPON), true);

            if (getInventory().contains(15719))
                getEquipment().delete(getEquipment().atSlot(EquipSlot.WEAPON), true);
        }

    }

    public void stopTeleporting() {
        setTeleporting(null);
        performAnimation(Animation.DEFAULT_RESET_ANIMATION);
        performGraphic(Graphic.DEFAULT_RESET_GRAPHIC);
        Task t = getTeleportingTask();
        if (t != null)
            t.stop();
    }

    public void flagAccount() {
        BLOCK_ALL_BUT_TALKING = true;
        setAccountFlagged(true);
        packetSender.sendInterfaceRemoval();
        sendMessage("You have logged in from a new IP address, to verify your account please enter your bank PIN.");
        sendMessage("You won't be allowed to do any actions until you have successfully entered your bank PIN.");
        bankpin.requestPin(() -> {

            boolean validPin = bankpin.checkEnteredPin();

            if (validPin) {

                getPacketSender().sendSound(Sounds.BANK_PIN_SUCESSFULLY);
                sendMessage("<img=750> You have correctly entered your PIN.");
                sendMessage("You have successfully verified your new IP address.");
                sendMessage("Your current IP address has been whitelisted on your account!");

                recentIPS.add(getHostAddress());
                if (recentIPS.size() >= 5)
                    recentIPS.remove(0);

                packetSender.sendInterfaceRemoval();

                failedBankPinTries = 0;
                BLOCK_ALL_BUT_TALKING = false;
                bankpin.pinEntered = true;
                setAccountFlagged(false);

            } else {

                bankpin.requestPinAgain();

                getPacketSender().sendSound(Sounds.BANK_PIN_WRONG);
                sendMessage("<img=750> @red@You have entered the wrong PIN.");
                sendMessage("You have submitted " + getFailedBankPinTries() + "/10 incorrect bank PIN tries!");

                failedBankPinTries++;

                if (failedBankPinTries == 7)
                    sendMessage("@red@You have 3 more incorrect bank PIN tries before your account gets locked.");
                else if (failedBankPinTries == 10) {
                    PunishmentManager.submit(username, PunishmentType.LOCK);
                    BankUtil.logAccountLock(getUsername());
                }
            }
        });
    }

    /**
     * Removes the specified {@link DynamicGameObject} from this Player's
     * {@link Set} of visible objects.
     *
     * @param object The DynamicGameObject.
     */
    public void removeObject(DynamicGameObject object) {
        localObjects.remove(object);
        object.removeFrom(this);
    }

    /**
     * Note this method can be invoked from outside the game thread.
     *
     * @param policy determines the priority of the request to be handled.
     */
    public void logout(LogoutPolicy policy) {
        if (isActive()) {
            if (pendingLogout) {
                if (logoutPolicy != policy) {
                    Server.getLogger().info("[" + logoutPolicy + " -> " + policy + "]: changed logout policy for {" + this + "}.");
                    logoutPolicy = policy;
                }
            } else {
                logoutPolicy = policy;
                pendingLogout = true;
                setForceLogoutTimer = true;
                Server.getLogger().info("[" + logoutPolicy + "]: logout request for " + this + ".");
            }
        }
    }

    public void stopLogout() {
        if (logoutPolicy != null && logoutPolicy.canBeCanceled()) {
            Server.getLogger().info("[" + logoutPolicy + "]: canceled logout for " + this + ".");
            pendingLogout = false;
            logoutPolicy = null;
        }
    }

    /**
     * Check whether this {@link Player} is able to logout from the game.
     *
     * @return {@code true} if the player can logout
     * {@code false} otherwise
     */
    public boolean canLogout() {
            /*
            allows the player to force or request logout while in combat
             */

        if (combat.isInCombat()) {
            packetSender.sendMessage("You must wait a few seconds after being out of combat before doing this.", 1000);
            return false;
        }

        if (busy()) {
            packetSender.sendMessage("You can't log out at the moment.", 1000);
            return false;
        }

        // Logout instantly if not in the Wilderness, otherwise setup a 10 seconds timer which resets upon any action.
        if (getArea() != null) {
            if (AreaManager.inWilderness(this)) {
                setHasLogoutTimer(true); // Enable count down timer
                TaskManager.submit(new Task(1, false) {
                    int logoutDelay = 10;

                    @Override
                    protected void execute() {

                        // Logout timer in the Wilderness
                        if (!hasLogoutTimer()) {
                            stop();
                        }

                        --logoutDelay; // Reduce delay

                        // Stop task if more than 10 seconds has passed
                        if (logoutDelay <= 0 && hasLogoutTimer()) {
                            setHasLogoutTimer(false);
                            packetSender.sendInterfaceRemoval();
                            stop();
                            logout(LogoutPolicy.IMMEDIATE);
                        }

                        // Send dialogue message
                        if (hasLogoutTimer()) {
                            new DialogueBuilder(DialogueType.TITLED_STATEMENT_NO_CONTINUE)
                                    .setText("You will automatically get logged out after " + logoutDelay + " more seconds.")
                                    .start(getAsPlayer());
                        }
                    }
                });
                return false;
            }
        }
        if (isRidingCarpet()) {
            sendMessage("You can't log out at the moment.");
            return false;
        }

        if (EntityExtKt.getBoolean(this, Attribute.HAS_PENDING_RANDOM_EVENT, false) || EntityExtKt.getBoolean(this, Attribute.HAS_PENDING_RANDOM_EVENT2, false)) {
            packetSender.sendMessage("You can't log out at the moment.", 1000);
            return false;
        }

        if (getFightCave().isPaused()) {
            return true;
        }
        if (getAquaisNeige().isPaused()) {
            return true;
        }
        return true;
    }

    public boolean isRidingCarpet() {
        if (getAppearance().getBas() == null) {
            return false;
        }
        return getAppearance().getBas().getIdle() == RugMerchant.BAS_ID;
    }

    /**
     * Handles the actual logging out from the game.
     */
    public void onLogout() {

        packetSender.sendInterfaceRemoval();

        Region.removePlayerFromChunks(this, getRenderedRegions());

        if (status == PlayerStatus.DUELING || dueling.inDuel()) {
            status = PlayerStatus.NONE;
            dueling.loseDuel();
        }

        if (timerRepository.has(TimerKey.OVERLOAD_POTION))
            OverloadTask.resetOverload(this);
        if (timerRepository.has(TimerKey.WINE_FERMENT))
            timerRepository.cancel(TimerKey.WINE_FERMENT); // force cancel.

        if (cannon != null) {
            cannon.pickup(true);
        }

        if (!hunterTraps.isEmpty()) {
            Hunter.clearTraps(this, true);
        }
//        if (HunterTraps.INSTANCE.getPLAYER_TRAPS().containsKey(getUsername())) {
//            HunterActions.INSTANCE.clearTraps(this);
//        }

        PestControl.removePlayerFromGame(this);
        if (CastleWars.isInCastleWars(this) || CastleWars.isInCastleWarsLobby(this)) {
            CastleWars.logoutPlayer(this);
        }
        if (CastleWars.isInCastleWarsLobby(this)) {
            LobbyManager.logoutPlayer(this);
        }

        if (isRidingCarpet()) {
            // TODO: Move to start destination of your ride
            moveTo(new Position(3309, 3109, 0));
        }

        notify(new PlayerLogoutEvent(this));

        Optional.ofNullable(getArea()).ifPresent(area -> area.leave(this));
        PetHandler.onLogout(this, getCurrentPet());
        getRelations().updateLists(false);
        BountyHunterManager.disassemblePairIfPresent(this);
        GlobalClanChatManager.leave(this, false);
        TaskManager.cancelTasks(this);

        PlayerSaving.save(this);

        logoutPolicy = null;

        final Channel channel = session.getChannel();

        if (channel.isActive())
            packetSender.sendLogout();

        session.flush();
        session.releaseBuffers();

        if (instance != null) {
            instance.onLogout(this);
            instance.destroy();
            instance = null;
        }

        if (channel.isOpen())
            channel.disconnect();

        new UpdateOnlinePlayers(
                SQLManager.Companion.getINSTANCE(),
                username,
                PlayerUtil.isStaff(this) ? "staff" : EntityExtKt.getBoolean(this, Attribute.MIDDLEMAN, false) ? "middleman" : "",
                false,
                rights
        ).schedule(this);

        if (!PlayerUtil.isDeveloper(this)) {
            new UpdateHighscorePlayers(
                    SQLManager.Companion.getINSTANCE(),
                    username,
                    gameMode,
                    skillManager
            ).schedule(this);
        }
    }

    /**
     * Requests a logout by sending the logout packet to the client. This leads
     * to the connection being closed. The {@link GrinderHandler} will then
     * add the player to the remove characters queue.
     */
    public void requestClientLogout() {
        if (!FightCave.onLogout(this)) {
            return;
        }
        if (!AquaisNeige.onLogout(this)) {
            return;
        }
        WarriorsGuild.onLogout(this);
        PestControl.removePlayerFromGame(this);
        packetSender.sendLogout();
        localObjects.forEach(object -> object.removeFrom(this));
        localObjects.clear();
    }

    public void resetAnimation() {
        performAnimation(new Animation(65535));
    }

    /**
     * Resets the player's attributes to default.
     */
    public void resetAttributes() {
        if (isInTutorial()) {
            return;
        }
        if (getHitpoints() <= 0) {
            setHitpoints(1);
        }

        getAttributes().remove(Attribute.SHATTER_EFFECT);
        getAttributes().remove(Attribute.FEAR_EFFECT);
        getAttributes().remove(Attribute.SEVER_EFFECT);

        resetAnimation();
        setSpecialActivatedAndSendState(false);
        SpecialAttackType.updateBar(this, true);
        getCombat().getFireImmunityTimer().stop();
        getCombat().getSpellBlockTimer().stop();
        getVengeanceEffect().stop();
        getVengeanceDelayTimer().stop();
        getCombat().getPoisonImmunityTimer().stop();
        getCombat().getVenomImmunityTimer().stop();
        getCombat().getAggressivityTimer().stop();
        getCombat().getTeleBlockTimer().stop();
        //getVotingBonusTimer().stop();
        getTimerRepository().cancel(TimerKey.FREEZE);
        setPoisonDamage(0);
        setVenomDamage(0);
        setWildernessLevel(0);
        getAttributes().reset(Attribute.RING_OF_RECOIL_CHARGES);
        setSkullTimer(0);
        setTeleportToCaster(null);
        setTeleportDestination(null);
        setSpecialPercentage(100);
        setSkullType(SkullType.WHITE_SKULL);
        getAttackedBy().clear();
        getAttributes().remove(Attribute.IS_FLYING);
        getAttributes().remove(Attribute.IS_DRUNK);
        getAttributes().remove(Attribute.IS_DEMON);
        getAttributes().remove(Attribute.IS_CRAB);
        getAttributes().remove(Attribute.IS_ANY_NPC);
        WeaponInterfaces.INSTANCE.assign(this);
        EquipmentBonuses.update(this);
        PrayerHandler.deactivatePrayers(this);
        getEquipment().refreshItems();
        getInventory().refreshItems();
        for (Skill skill : Skill.values())
            getSkillManager().setCurrentLevel(skill, getSkillManager().getMaxLevel(skill), true);
        setRunEnergy(10000);
        updateCarriedWeight();
        getPacketSender().sendOrbConfig();
        getMotion().update(MovementStatus.NONE).clearSteps();
        getPacketSender().sendMinimapFlagRemoval();
        for (EffectTimer effectTimer : EffectTimer.values()) {
            if (effectTimer.equals(EffectTimer.DEATH_ITEMS_DESPAWN)
                    || effectTimer.equals(EffectTimer.VOTING_BONUS)) {
                continue;
            }
            getPacketSender().sendEffectTimer(0, effectTimer);
        }
        setUntargetable(false);
        setIsDying(false);
        getTimerRepository().cancel(TimerKey.FREEZE);
        updateAppearance();
    }

    public void restoreRegularAttributes() {
        if (getHitpoints() <= 0) {
            setHitpoints(1);
        }
        setPoisonDamage(0);
        setVenomDamage(0);
        WeaponInterfaces.INSTANCE.assign(this);
        EquipmentBonuses.update(this);
        getEquipment().refreshItems();
        getInventory().refreshItems();
        setSpecialPercentage(100);
        SpecialAttackType.updateBar(this, true);
        for (Skill skill : Skill.values())
            getSkillManager().setCurrentLevel(skill, getSkillManager().getMaxLevel(skill), true);
        setRunEnergy(10000);
        for (EffectTimer effectTimer : EffectTimer.values()) {
            if (effectTimer.equals(EffectTimer.DEATH_ITEMS_DESPAWN)
                    || effectTimer.equals(EffectTimer.VOTING_BONUS)
                    || effectTimer.equals(EffectTimer.ANTIFIRE)
                    || effectTimer.equals(EffectTimer.VENGEANCE)
                    || effectTimer.equals(EffectTimer.AGGRESSIVITY)
                    || effectTimer.equals(EffectTimer.ANTIPOISON)
                    || effectTimer.equals(EffectTimer.ANTI_VENOM)
            ) {
                continue;
            }
            getPacketSender().sendEffectTimer(0, effectTimer);
        }
        getCombat().getSpellBlockTimer().stop();
        updateCarriedWeight();
        getPacketSender().sendOrbConfig();
        getPacketSender().sendMinimapFlagRemoval();
    }

    public final void updateEquippedItemColors() {
        EntityExtKt.setBoolean(this, Attribute.UPDATE_COLORFUL_ITEM, true, true);
        blockSet.add(UpdateBlock.Companion.createAppearanceBlock(this, true));
    }

    /**
     * Checks if a player is busy.
     */
    public boolean busy() {
        if (getHitpoints() <= 0) {
            return true;
        }
        if (hasPendingTeleportUpdate() || isTeleporting() || getTeleportPosition() != null) {
            return true;
        }
        if (getAgility().getObstacle() != null) {
            return true;
        }
        if (status != PlayerStatus.NONE && status != PlayerStatus.AWAY_FROM_KEYBOARD) {
            return true;
        }
        if (forceMovement != null)
            return true;
        return blockLogout;
    }

    public void playSound(Sound sound) {
        if (sound instanceof AreaSound)
            packetSender.sendAreaPlayerSound((AreaSound) sound);
        else
            packetSender.sendSound(sound);
    }

    public void dispatchInterfaceClose() {
        if (this.interfaceCloseListener != null) {
            this.interfaceCloseListener.processInterfaceClose();
            this.interfaceCloseListener = null;
        }
    }

    public SecondsTimer getVengeanceEffect() {
        return vengeanceEffectTimer;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public String getUsername() {
        return username;
    }

    public Player setUsername(String username) {
        this.username = username;
        return this;
    }

    public void setDialogueBuilder(DialogueBuilder dialogueBuilder) {
    }

    public Stopwatch getDoorTimer() {
        return doorTimer;
    }

    public SecondsTimer getBountyTeleportTimer() {
        return bountyTeleportTimer;
    }

    public SecondsTimer getDragonFireShieldTimer() {
        return dragonFireShieldTimer;
    }

    public SecondsTimer getChatTimer() {
        return chatTimer;
    }

    public long getTimePlayed(TimeUnits timeUnit) {
        return timePlayed / timeUnit.getMilisecondValue();
    }

    void setTimePlayed(long time) {
        timePlayed = time * TimeUnits.MILLISECOND.getMilisecondValue();
    }

    public Position getOldPosition() {
        return oldPosition;
    }

    public NPC getCurrentPet() {
        return currentPet;
    }

    public PlayerRelations getRelations() {
        return relations;
    }

    public boolean isUpdateInventory() {
        return updateInventory;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setUpdateInventory(boolean updateInventory) {
        this.updateInventory = updateInventory;
    }

    public void setCurrentPet(NPC currentPet) {
        this.currentPet = currentPet;
    }

    public void setOldPosition(Position oldPosition) {
        this.oldPosition = oldPosition;
    }

    public boolean isInTutorial() {
        return EntityExtKt.getBoolean(this, Attribute.IS_ON_TUTORIAL_MODE, false);
    }

    /**
     * Sets special attack and sends orb state.
     * Feel free to refactor this, couldn't think of a non confusing name.
     */
    public void setSpecialActivatedAndSendState(boolean activated) {
        setSpecialActivated(activated);
        getPacketSender().sendSpecialAttackState(isSpecialActivated());
    }

    public SecondsTimer getVengeanceDelayTimer() {
        return vengeanceDelayTimer;
    }

    public SecondsTimer getTheCursedVaultDelayTimer() {
        return cursedVaultDelayTimer;
    }

    public SecondsTimer getRestoreBoxDelayTimer() {
        return restoreBoxDelayTimer;
    }

    public SecondsTimer getAutoChatBreakTimer() {
        return autoChatBreakTimer;
    }

    public SecondsTimer getVotingBonusTimer() {
        return votingBonus;
    }

    public SkillManager getSkillManager() {
        return skillManager;
    }

    public int updateCarriedWeight() {
        carriedWeight = 0;
        for (Item equipped : getEquipment().getItems()) {
            ItemDefinition definition = ItemDefinition.forId(equipped.getId());
            if (definition.getWeight() != 0) {
                carriedWeight += definition.getWeight();
            }
        }
        for (Item inventory : getInventory().getItems()) {
            ItemDefinition definition = ItemDefinition.forId(inventory.getId());
            if (definition.getWeight() != 0 && definition.getWeight() > 0) { // prevents carrying items like grace in inventory to reduce your weight
                carriedWeight += definition.getWeight();
            }
        }
        return carriedWeight;
    }

    /**
     * Checks if a player has pending movement check or force movement.
     */
    public boolean hasPendingMovement() {
        if (getHitpoints() <= 0) {
            return true;
        }
        if (hasPendingTeleportUpdate()) {
            return true;
        }
        return forceMovement != null;
    }

    public boolean blockTeleportation() {
        if (!isAlive())
            return true;
        if (hasPendingTeleportUpdate() || isTeleporting())
            return true;
        if (status != PlayerStatus.NONE)
            return true;
        return forceMovement != null;
    }

    public boolean isPacketsBlocked() {
        return EntityExtKt.getBoolean(this, Attribute.PACKETS_BLOCKED, false);
    }

    public void setPacketsBlocked(boolean blocked) {
        EntityExtKt.setBoolean(this, Attribute.PACKETS_BLOCKED, blocked, false);
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp timestamp) {
        creationDate = timestamp;
    }

    public PlayerSession getSession() {
        return session;
    }

    public Long getLongUsername() {
        return longUsername;
    }

    public Player setLongUsername(Long longUsername) {
        this.longUsername = longUsername;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Player setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public Player setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
        return this;
    }

    public boolean isRingofWealthActivated() {
        return EntityExtKt.getBoolean(this, Attribute.RING_OF_WEALTH_ACTIVATED, false);
    }

    public void setRingofWealthActivated(boolean ringofWealthActivated) {
        EntityExtKt.setBoolean(this, Attribute.RING_OF_WEALTH_ACTIVATED, ringofWealthActivated, false);
    }

    public int getCrown() {
        return crown;
    }

    public void setCrown(int crown) {
        this.crown = crown;
    }

    public Music getMusic() {
        return music;
    }

    public Appearance getAppearance() {
        return appearance;
    }

    public SecondsTimer getForcedLogoutTimer() {
        return forcedLogoutTimer;
    }

    public boolean isDying() {
        return EntityExtKt.getBoolean(this, Attribute.IS_DYING, false);
    }

    public void setIsDying(boolean isDying) {
        EntityExtKt.setBoolean(this, Attribute.IS_DYING, isDying, false);
    }

    public List<Player> getLocalPlayers() {
        return localPlayers;
    }

    public List<NPC> getLocalNpcs() {
        return localNpcs;
    }

    public int getInterfaceId() {
        return EntityExtKt.getInt(this, Attribute.INTERFACE_ID, -1);
    }

    public Player setInterfaceId(int interfaceId) {
        EntityExtKt.setInt(this, Attribute.INTERFACE_ID, interfaceId, -1);
        return this;
    }

    public BarrowsManager getBarrowsManager() {
        return barrowsManager;
    }

    public BarrowsBrother getFinalBarrow() {
        return finalBarrow;
    }

    public void setFinalBarrow(BarrowsBrother brother) {
        this.finalBarrow = brother;
    }

    public boolean isSpawnedBarrows() {
        return spawnedBarrows;
    }

    public void setSpawnedBarrows(boolean spawnedBarrows) {
        this.spawnedBarrows = spawnedBarrows;
    }

    public Set<BarrowsBrother> getKilledBarrows() {
        return killedBarrows;
    }

    public boolean hasCommuneEffect() {
        return EntityExtKt.getBoolean(this, Attribute.HAS_COMMUNE_EFFECT, false);
    }

    public void setHasCommuneEffect(boolean hasCommuneEffect) {
        EntityExtKt.setBoolean(this, Attribute.HAS_COMMUNE_EFFECT, hasCommuneEffect, false);
    }

    public boolean isJailed() {
        return EntityExtKt.getBoolean(this, Attribute.IS_JAILED, false);
    }

    public void setJailed(boolean isJailed) {
        EntityExtKt.setBoolean(this, Attribute.IS_JAILED, isJailed, false);
    }

    public boolean hasAutoTalkerMessageActive() {
        return EntityExtKt.getBoolean(this, Attribute.HAS_AUTO_TALKER_MESSAGE_ACTIVE, false);
    }

    public void setHasAutoTalkerMessageActive(boolean hasAutoTalkerMessageActive) {
        EntityExtKt.setBoolean(this, Attribute.HAS_AUTO_TALKER_MESSAGE_ACTIVE, hasAutoTalkerMessageActive, false);
    }

    public boolean hasLogoutTimer() {
        return hasLogoutTimer;
    }

    public void setHasLogoutTimer(boolean hasLogoutTimer) {
        this.hasLogoutTimer = hasLogoutTimer;
    }

    public String getMessageToAutoTalk() {
        return EntityExtKt.getString(this, Attribute.MESSAGE_TO_AUTO_TALK, "");
    }

    public void setMessageToAutoTalk(String messageToAutoTalk) {
        EntityExtKt.setString(this, Attribute.MESSAGE_TO_AUTO_TALK, messageToAutoTalk, "");
    }

    public String getTempMessageToAutoTalk() {
        return EntityExtKt.getString(this, Attribute.TEMP_MESSAGE_TO_AUTO_TALK, "");
    }

    public void setTempMessageToAutoTalk(String tempMessageToAutoTalk) {
        EntityExtKt.setString(this, Attribute.TEMP_MESSAGE_TO_AUTO_TALK, tempMessageToAutoTalk, "");
    }

    public int getTeleGrabCount() {
        return teleGrabCount;
    }

    public void setTeleGrabCount(int teleGrabCount) {
        this.teleGrabCount = teleGrabCount;
    }

    public boolean receivedStarter() {
        return EntityExtKt.getBoolean(this, Attribute.RECEIVED_STARTER, false);
    }

    public void setReceivedStarter(boolean receivedStarter) {
        EntityExtKt.setBoolean(this, Attribute.RECEIVED_STARTER, receivedStarter, false);
    }

    public PlayerChatSettings getChatSettings() {
        return chatSettings;
    }

    public Dialogue getDialogue() {
        return this.dialogue;
    }

    public void setDialogue(Dialogue dialogue) {
        this.dialogue = dialogue;
    }

    public DialogueOptions getDialogueOptions() {
        return dialogueOptions;
    }

    public void setDialogueOptions(DialogueOptions dialogueOptions) {
        this.dialogueOptions = dialogueOptions;
    }

    public boolean isAllowRegionChangePacket() {
        return allowRegionChangePacket;
    }

    public void setAllowRegionChangePacket(boolean allowRegionChangePacket) {
        this.allowRegionChangePacket = allowRegionChangePacket;
    }

    public int getWalkableInterfaceId() {
        return EntityExtKt.getInt(this, Attribute.WALKABLE_INTERFACE_ID, -1);
    }

    public void setWalkableInterfaceId(int interfaceId2) {
        EntityExtKt.setInt(this, Attribute.WALKABLE_INTERFACE_ID, interfaceId2, -1);
    }

    public void setPlayerInteractingOption(PlayerInteractingOption playerInteractingOption) {
    }

    public EquipmentBonuses getBonusManager() {
        return bonusManager;
    }

    public int getMultiIcon() {
        return EntityExtKt.getInt(this, Attribute.MULTI_ICON, 0);
    }

    public void setMultiIcon(int multiIcon) {
        EntityExtKt.setInt(this, Attribute.MULTI_ICON, multiIcon, 0);
    }

    public ForceMovement getForceMovement() {
        return forceMovement;
    }

    @Override
    public Agent setForceMovement(ForceMovement forceMovement) {
        return super.setForceMovement(forceMovement).getAsPlayer();
    }

    /**
     * Gets the last known region.
     *
     * @return The last known region, or {@code null} if the player has never
     * known a region.
     */
    public Position getLastKnownRegion() {
        return lastKnownRegion;
    }

    /**
     * Sets the last known region.
     *
     * @param lastKnownRegion The last known region.
     */
    public void setLastKnownRegion(Position lastKnownRegion) {
        this.lastKnownRegion = lastKnownRegion;
    }

    public Player setBas(BasicAnimationSet set) {
        appearance.setBas(set);
        return this;
    }

    public Player setBas(int animation) {
        setBas(new BasicAnimationSet(animation, animation, animation, animation, animation, animation, animation));
        return this;
    }

    public Player resetBas() {
        appearance.setBas(null);
        return this;
    }

    public int getRunEnergy() {
        return EntityExtKt.getInt(this, Attribute.RUNNING_ENERGY, 10000);
    }

    public void setRunEnergy(int runEnergy) {
        this.getAttributes().numAttr(Attribute.RUNNING_ENERGY, 10000).setValue(runEnergy);
    }

    public Stopwatch getLastItemPickup() {
        return lastItemPickup;
    }

    public void setWalkToTask(WalkToAction walkToTask) {
        if (walkToTask == null) {
            this.motion.cancelTask();
        } else {
            this.motion.start(walkToTask, false);
        }
    }

    public boolean isVialCrushingToggled() {
        return EntityExtKt.getBoolean(this, Attribute.VIAL_CRUSHING_TOGGLED, false);
    }

    public void setVialCrushingToggled(boolean vialCrushingToggled) {
        EntityExtKt.setBoolean(this, Attribute.VIAL_CRUSHING_TOGGLED, vialCrushingToggled, false);
    }

    public boolean unlockedVialCrushing() {
        return EntityExtKt.getBoolean(this, Attribute.UNLOCKED_VIAL_CRUSHING, false);
    }

    public void setUnlockedVialCrushing(boolean unlockedVialCrushing) {
        EntityExtKt.setBoolean(this, Attribute.UNLOCKED_VIAL_CRUSHING, unlockedVialCrushing, false);
    }

    public SpecialAttackType getSpecialAttackType() {
        return specialAttackType;
    }

    public void setSpecialAttackType(SpecialAttackType specialAttackType) {
        this.specialAttackType = specialAttackType;
    }

    public MagicSpellbook getSpellbook() {
        return spellbook;
    }

    public void setSpellbook(MagicSpellbook spellbook) {
        this.spellbook = spellbook;
    }

    public int getWildernessLevel() {
        return getAttributes().numInt(Attribute.WILDERNESS_LEVEL);
    }

    public void setWildernessLevel(int wildernessLevel) {
        getAttributes().numAttr(Attribute.WILDERNESS_LEVEL, 0).setValue(wildernessLevel);
    }

    public int getDestroyItem() {
        return destroyItem;
    }

    public void setDestroyItem(int destroyItem) {
        this.destroyItem = destroyItem;
    }

    public boolean isSkulled() {
        return getSkullTimer() > 0;
    }

    public int getAndDecrementSkullTimer() {
        int value = getSkullTimer();
        setSkullTimer(getSkullTimer() - 1);
        return value;
    }

    public int getSkullTimer() {
        return EntityExtKt.getInt(this, Attribute.SKULL_TIMER, 0);
    }

    public void setSkullTimer(int skullTimer) {
        EntityExtKt.setInt(this, Attribute.SKULL_TIMER, skullTimer, 0);
    }

    public Date getLastDeath() {
        return lastDeath;
    }

    public void setLastDeath(Date date) {
        lastDeath = date;
    }

    /**
     * @deprecated please use the [EntityExt.passedTime] method.
     */
    @Deprecated
    public Stopwatch getClickDelay() {
        return clickDelay;
    }

    public Shop getShop() {
        return shop;
    }

    public Player setShop(Shop shop) {
        this.shop = shop;
        return this;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public Player setStatus(PlayerStatus status) {
        this.status = status;
        return this;
    }

    public EnterSyntax getEnterSyntax() {
        return enterSyntax;
    }

    /**
     * @deprecated Please use requestInput
     */
    @Deprecated
    public void setEnterSyntax(EnterSyntax enterSyntax) {
        this.enterSyntax = enterSyntax;
    }

    public <T> T promiseInput(Class<T> clazz, String question) {
        requestInput(clazz, question, (v) -> {
            inputPromise = v;
            removeInputHandler();
            getPacketSender().sendInterfaceRemoval();
        });
        return (T) inputPromise;
    }

    public <T> void requestInput(Class<T> type, String query, Consumer<T> handler) {
        checkArgument(type == Integer.class || type == String.class,
                "Input may only be Integer or String");

        if (type == String.class) {
            getPacketSender().sendEnterInputPrompt(query);
        } else {
            getPacketSender().sendEnterAmountPrompt(query);
        }

        inputHandler = handler;
    }

    public void removeInputHandler() {
        this.inputHandler = null;
    }

    public Consumer<?> getInputHandler() {
        return inputHandler;
    }

    public int getCurrentBankTab() {
        return EntityExtKt.getInt(this, Attribute.CURRENT_BANK_TAB, 0);
    }

    public Player setCurrentBankTab(int tab) {
        EntityExtKt.setInt(this, Attribute.CURRENT_BANK_TAB, tab, 0);
        return this;
    }

    public int getModifiableXValue() {
        return EntityExtKt.getInt(this, Attribute.MODIFIABLE_X_VALUE, 1);
    }

    public void setModifiableXValue(int value) {
        EntityExtKt.setInt(this, Attribute.MODIFIABLE_X_VALUE, value, 1);
    }

    public int getBankQuantityConfig() {
        return EntityExtKt.getInt(this, Attribute.BANK_QUANTITY_CONFIG, 1);
    }

    public void setBankQuantityConfig(int config) {
        EntityExtKt.setInt(this, Attribute.BANK_QUANTITY_CONFIG, config, 1);
    }

    public int getTabDisplayConfig() {
        return EntityExtKt.getInt(this, Attribute.TAB_DISPLAY_CONFIG, 1);
    }

    public void setTabDisplayConfig(int config) {
        EntityExtKt.setInt(this, Attribute.TAB_DISPLAY_CONFIG, config, 1);
    }

    public void setNoteWithdrawal(boolean noteWithdrawal) {
        EntityExtKt.setBoolean(this, Attribute.NOTE_WITHDRAWAL, noteWithdrawal, false);
    }

    public boolean withdrawAsNote() {
        return EntityExtKt.getBoolean(this, Attribute.NOTE_WITHDRAWAL, false);
    }

    public void setInsertMode(boolean insertMode) {
        EntityExtKt.setBoolean(this, Attribute.INSERT_MODE, insertMode, false);
    }

    public boolean insertMode() {
        return EntityExtKt.getBoolean(this, Attribute.INSERT_MODE, false);
    }

    public boolean isFixedBankWidth() {
        return EntityExtKt.getBoolean(this, Attribute.FIXED_BANK_WIDTH, false);
    }

    public void setFixedBankWidth(boolean fixed) {
        EntityExtKt.setBoolean(this, Attribute.FIXED_BANK_WIDTH, fixed, false);
    }

    public void setShowDepositWornItems(boolean show) {
        EntityExtKt.setBoolean(this, Attribute.SHOW_DEPOSIT_WORN_ITEMS, show, false);
    }

    public boolean showDepositWornItems() {
        return EntityExtKt.getBoolean(this, Attribute.SHOW_DEPOSIT_WORN_ITEMS, false);
    }

    public Bank[] getBanks() {
        return banks;
    }

    public Bank getBank(int index) {
        if (banks[index] == null) {
            banks[index] = new Bank(this);
        }
        return banks[index];
    }

    public Player setBank(int index, Bank bank) {
        this.banks[index] = bank;
        return this;
    }


    public boolean isShowDropWarning() {
        return EntityExtKt.getBoolean(this, Attribute.SHOW_DROP_WARNING, true);
    }

    public void setShowDropWarning(boolean showDropWarning) {
        EntityExtKt.setBoolean(this, Attribute.SHOW_DROP_WARNING, showDropWarning, true);
    }

    public boolean isShowEmptyWarning() {
        return EntityExtKt.getBoolean(this, Attribute.SHOW_EMPTY_WARNING, true);
    }

    public void setShowEmptyWarning(boolean showEmptyWarning) {
        EntityExtKt.setBoolean(this, Attribute.SHOW_EMPTY_WARNING, showEmptyWarning, true);
    }

    public int[] getYellColors() {
        return yellColors;
    }

    public void setYellColors(int[] colors) {
        this.yellColors = colors;
    }

    public String getYellTitle() {
        return yellTitle;
    }

    public void setYellTitle(String title) {
        this.yellTitle = title;
    }

    public Map<ColorfulItem, Integer[]> getColorfulItemMap() {
        return colorfulItemMap;
    }

    public List[] getMysteryBoxItemsLists() {
        return mysteryBoxItemsList;
    }

    public AchievementType getLastCompletedAchievement() {
        return lastCompletedAchievement;
    }

    public void setLastCompletedAchievement(AchievementType lastCompletedAchievement) {
        this.lastCompletedAchievement = lastCompletedAchievement;
    }

    public boolean isNewPlayer() {
        return EntityExtKt.getBoolean(this, Attribute.NEW_PLAYER, false);
    }

    public void setNewPlayer(boolean newPlayer) {
        EntityExtKt.setBoolean(this, Attribute.NEW_PLAYER, newPlayer, false);
    }

    public boolean isPreserveUnlocked() {
        return EntityExtKt.getBoolean(this, Attribute.PRESERVE_UNLOCKED, false);
    }

    public void setPreserveUnlocked(boolean preserveUnlocked) {
        EntityExtKt.setBoolean(this, Attribute.PRESERVE_UNLOCKED, preserveUnlocked, false);
    }

    public boolean isRigourUnlocked() {
        return EntityExtKt.getBoolean(this, Attribute.RIGOUR_UNLOCKED, false);
    }

    public void setRigourUnlocked(boolean rigourUnlocked) {
        EntityExtKt.setBoolean(this, Attribute.RIGOUR_UNLOCKED, rigourUnlocked, false);
    }

    public boolean isAuguryUnlocked() {
        return EntityExtKt.getBoolean(this, Attribute.AUGURY_UNLOCKED, false);
    }

    public void setAuguryUnlocked(boolean auguryUnlocked) {
        EntityExtKt.setBoolean(this, Attribute.AUGURY_UNLOCKED, auguryUnlocked, false);
    }

    public PriceChecker getPriceChecker() {
        return priceChecker;
    }

    public RunePouch getRunePouch() {
        return runePouch;
    }

    public SafeDeposit getSafeDeposit() {
        return safeDeposit;
    }

    public ClanChat getCurrentClanChat() {
        return currentClanChat;
    }

    public void setCurrentClanChat(ClanChat currentClanChat) {
        this.currentClanChat = currentClanChat;
    }

    public String getClanChatName() {
        return clanChatName;
    }

    public void setClanChatName(String clanChatName) {
        this.clanChatName = clanChatName;
    }

    public TradeController getTrading() {
        return trading;
    }

    public QuickPrayers getQuickPrayers() {
        return quickPrayers;
    }

    public boolean isTargetTeleportUnlocked() {
        return targetTeleportUnlocked;
    }

    public void setTargetTeleportUnlocked(boolean targetTeleportUnlocked) {
        this.targetTeleportUnlocked = targetTeleportUnlocked;
    }

    public SecondsTimer getYellDelay() {
        return yellDelay;
    }

    public void resetSafingTimer() {
        this.setSafeTimer(180);
    }

    public void setSafeTimer(int safeTimer) {
        this.safeTimer = safeTimer;
    }

    public List<String> getRecentIPS() {
        return recentIPS;
    }

    public int decrementAndGetSafeTimer() {
        return this.safeTimer--;
    }

    public SecondsTimer getSpecialAttackRestore() {
        return specialAttackRestore;
    }

    public SkullType getSkullType() {
        return skullType;
    }

    public void setSkullType(SkullType skullType) {
        this.skullType = skullType;
    }

    public DuelController getDueling() {
        return dueling;
    }

    public GambleController getGambling() {
        return gambling;
    }

    public int getBlowpipeScales() {
        return blowpipeScales;
    }

    public void setBlowpipeScales(int blowpipeScales) {
        this.blowpipeScales = blowpipeScales;
    }

    public boolean staffPvpToggled() {
        return EntityExtKt.getBoolean(this, Attribute.STAFF_PVP_TOGGLED, false);
    }

    public void setStaffPvPToggled(boolean staffPvPToggled) {
        EntityExtKt.setBoolean(this, Attribute.STAFF_PVP_TOGGLED, staffPvPToggled, false);
    }

    public int pin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public void incrementBlowpipeScales(int blowpipeScales) {
        this.blowpipeScales += blowpipeScales;
    }

    public int decrementAndGetBlowpipeScales() {
        return this.blowpipeScales--;
    }

    public SecondsTimer getAggressionTolerance() {
        return aggressionTolerance;
    }

    public int getRegionHeight() {
        return regionHeight;
    }

    public void setRegionHeight(int regionHeight) {
        this.regionHeight = regionHeight;
    }

    public boolean isBlockingDisconnect() {
        return blockLogout;
    }

    public void setBlockLogout(boolean blockLogout) {
        this.blockLogout = blockLogout;
    }

    public Optional<Skillable> getSkill() {
        return skill;
    }

    public void setSkill(Optional<Skillable> skill) {
        this.skill = skill;
    }

    public Optional<CreationMenu> getCreationMenu() {
        return creationMenu;
    }

    public void setCreationMenu(Optional<CreationMenu> creationMenu) {
        this.creationMenu = creationMenu;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean hasTitle() {
        return title != null && !title.isEmpty();
    }

    public boolean isInvulnerable() {
        return EntityExtKt.getBoolean(this, Attribute.INVULNERABLE, false);
    }

    public void setInvulnerable(boolean invulnerable) {
        EntityExtKt.setBoolean(this, Attribute.INVULNERABLE, invulnerable, false);
    }

    public void setOnTutorialMode(boolean isOnTutorialMode) {
        EntityExtKt.setBoolean(this, Attribute.IS_ON_TUTORIAL_MODE, isOnTutorialMode, false);
    }

    public NPC getCurrentBrother() {
        return currentBrother;
    }

    public void setCurrentBrother(NPC brother) {
        this.currentBrother = brother;
    }

    public int getFailedBankPinTries() {
        return failedBankPinTries;
    }

    public void setFailedBankPinTries(int failedBankPinTries) {
        this.failedBankPinTries = failedBankPinTries;
    }

    public boolean hasPlaceHoldersEnabled() {
        return EntityExtKt.getBoolean(this, Attribute.PLACE_HOLDERS, true);
    }

    public void setPlaceholders(boolean placeholders) {
        EntityExtKt.setBoolean(this, Attribute.PLACE_HOLDERS, placeholders, true);
    }

    public Presetable[] getPresets() {
        return presets;
    }

    public void setPresets(Presetable[] sets) {
        this.presets = sets;
    }

    public boolean isOpenPresetsOnDeath() {
        return EntityExtKt.getBoolean(this, Attribute.OPEN_PRESETS_ON_DEATH, true);
    }

    public void setOpenPresetsOnDeath(boolean openPresetsOnDeath) {
        EntityExtKt.setBoolean(this, Attribute.OPEN_PRESETS_ON_DEATH, openPresetsOnDeath, true);
    }

    public Presetable getCurrentPreset() {
        return currentPreset;
    }

    public void setCurrentPreset(Presetable currentPreset) {
        this.currentPreset = currentPreset;
    }

    public boolean isAccountFlagged() {
        return EntityExtKt.getBoolean(this, Attribute.ACCOUNT_FLAGGED, false);
    }

    public void setAccountFlagged(boolean accountFlagged) {
        EntityExtKt.setBoolean(this, Attribute.ACCOUNT_FLAGGED, accountFlagged, false);
    }

    public boolean fallenOneLifeGameMode() {
        return EntityExtKt.getBoolean(this, Attribute.FALLEN_ONELIFE_GAMEMODE, false);
    }

    public void setFallenOneLifeGameMode(boolean fallenOneLifeGameMode) {
        EntityExtKt.setBoolean(this, Attribute.FALLEN_ONELIFE_GAMEMODE, fallenOneLifeGameMode, false);
    }

    public Queue<ChatMessage> getChatMessageQueue() {
        return chatMessageQueue;
    }

    public ChatMessage getCurrentChatMessage() {
        return currentChatMessage;
    }

    public Executable getDialogueContinueAction() {
        return dialogueContinueAction;
    }

    public void setDialogueContinueAction(Executable dialogueContinueAction) {
        this.dialogueContinueAction = dialogueContinueAction;
    }

    public boolean isTeleportInterfaceOpen() {
        return teleportInterfaceOpen;
    }

    public void setTeleportInterfaceOpen(boolean teleportInterfaceOpen) {
        this.teleportInterfaceOpen = teleportInterfaceOpen;
    }

    public long getLastDailyFlax() {
        return EntityExtKt.getLong(this, Attribute.LAST_DAILY_FLAX, 0);
    }

    public void setLastDailyFlax(long time) {
        EntityExtKt.setLong(this, Attribute.LAST_DAILY_FLAX, time, 0);
    }

    /**
     * Sets the skillingTask
     *
     * @return the skillingTask
     */
    public SkillTaskManager getSkillTaskManager() {
        return skillingTask;
    }

    /**
     * Sets the npcInfo
     *
     * @return the npcInfo
     */
    public NpcInformation getNpcInfo() {
        return npcInfo;
    }

    /**
     * Sets the notes
     *
     * @return the notes
     */
    public Notes getNotes() {
        return notes;
    }

    public Farming getFarming() {
        return farming;
    }

    public MonsterKillTracker getKillTracker() {
        return killTracker;
    }

    public FightCave getFightCave() {
        if (!getAttributes().containsKey(Attribute.FIGHT_CAVE)) {
            getAttributes().set(Attribute.FIGHT_CAVE, new Attribute<>(new FightCaveValueHolder()));
        }
        return (FightCave) getAttributes().get(Attribute.FIGHT_CAVE).getValueHolder().getValue();
    }

    public AquaisNeige getAquaisNeige() {
        if (!getAttributes().containsKey(Attribute.AQUAIS_NEIGE)) {
            getAttributes().set(Attribute.AQUAIS_NEIGE, new Attribute<>(new AquaisNeigeValueHolder()));
        }
        return (AquaisNeige) getAttributes().get(Attribute.AQUAIS_NEIGE).getValueHolder().getValue();
    }

    public WarriorsGuild getWarriorsGuild() {
        if (!getAttributes().containsKey(Attribute.WARRIORS_GUILD)) {
            getAttributes().set(Attribute.WARRIORS_GUILD, new Attribute<>(new WarriorsGuildValueHolder()));
        }
        return (WarriorsGuild) getAttributes().get(Attribute.WARRIORS_GUILD).getValueHolder().getValue();
    }

    public MotherlodeMine getMotherlodeMine() {
        if (!getAttributes().containsKey(Attribute.MOTHERLODE_MINE)) {
            getAttributes().set(Attribute.MOTHERLODE_MINE, new Attribute<>(new MotherlodeMineValueHolder()));
        }
        return (MotherlodeMine) getAttributes().get(Attribute.MOTHERLODE_MINE).getValueHolder().getValue();
    }

    public BlastFurnace getBlastFurnace() {
        if (!getAttributes().containsKey(Attribute.BLAST_FURNACE)) {
            getAttributes().set(Attribute.BLAST_FURNACE, new Attribute<>(new BlastFurnaceValueHolder()));
        }
        return (BlastFurnace) getAttributes().get(Attribute.BLAST_FURNACE).getValueHolder().getValue();
    }

    public AchievementManager getAchievements() {
        return achievements;
    }

    public void sendMessage(String privateMessage, int delay) {
        getPacketSender().sendMessage(privateMessage, delay);
    }

    public void sendDevelopersMessage(String privateMessage) {
        if (getRights() == PlayerRights.DEVELOPER || getRights() == PlayerRights.OWNER)
            getPacketSender().sendMessage(privateMessage);
    }

    public PlayerRights getRights() {
        return rights;
    }

    public int getRightsValue() {
        final PlayerRights member = PlayerUtil.getMemberRights(this);
        final int primaryRights = rights.isMember() ? 0 : rights.ordinal();
        int donatorRights = 0;

        if (member.isMember()) {
            donatorRights = member.ordinal() - PlayerRights.Companion.getMEMBER_ORDINAL() + 2;
        } else if (isDicer()) {
            donatorRights = 1;
        }

        //System.out.println("Primary rights(" + (rights.ordinal() << 24) + "): " + rights + " donor: " + donatorRights + " gm: " + getGameMode().ordinal()); // rank debugging
        return (primaryRights << 24) | (donatorRights << 16) | getGameMode().ordinal();
    }

    public Player setRights(PlayerRights rights) {
        if (isActive() && rights.isStaff()) {
            if (this.rights != null && this.rights.isStaff())
                World.getStaffMembers().get(this.rights).remove(this);
            World.getStaffMembers().putIfAbsent(rights, new HashSet<>());
            World.getStaffMembers().get(rights).add(this);
        }
        this.rights = rights;
        return this;
    }

    public void sendSpecialMessage(String name, int type, String message) {
        getPacketSender().sendSpecialMessage(name, type, message, 1200);
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getSnAddress() {
        return snAddress;
    }

    public void setSnAddress(String snAddress) {
        this.snAddress = snAddress;
    }

    public String getHdSerialNumber() {
        return hdSerialNumber;
    }

    public void setHdSerialNumber(String hdSerialNumber) {
        this.hdSerialNumber = hdSerialNumber;
    }

    public Set<String> getPurchasedTitles() {
        return purchasedTitles;
    }

    public Teleporting getTeleport() {
        return teleport;
    }

    public Item getRandomItemReward() {
        return randomItemReward;
    }

    public void setRandomItemReward(Item randomItemReward) {
        this.randomItemReward = randomItemReward;
    }

    public WelcomeManager getWelcome() {
        return welcome;
    }

    public AttributeManager getPoints() {
        return points;
    }

    public Rules getRules() {
        return rules;
    }

    public SecondsTimer getStaminaPot() {
        return staminaPot;
    }

    public Minigame getMinigame() {
        return minigame;
    }

    public void setMinigame(Minigame minigame) {
        this.minigame = minigame;
    }

    public MinigameManager getMinigameManager() {
        return minigameManager;
    }

    public SkillActionTask getSkillAction() {
        return skillAction;
    }

    public void setSkillAction(SkillActionTask skillAction) {
        this.skillAction = skillAction;
    }

    public SlayerManager getSlayer() {
        return slayer;
    }

    public Agility getAgility() {
        return agility;
    }

    public boolean isDicer() {
        return EntityExtKt.getBoolean(this, Attribute.DICER, false);
    }

    public void setDicer(boolean dicer) {
        EntityExtKt.setBoolean(this, Attribute.DICER, dicer, false);
    }

    /**
     * Checks if the player is a bot.
     *
     * @return <code>true</code> if the player is a bot
     */
    public boolean isBotPlayer() {
        return this instanceof BotPlayer;
    }

    /**
     * Gets the the game mode.
     *
     * @return the the game mode.
     */
    public GameMode getGameMode() {
        return gameMode;
    }

    /**
     * Sets the the game mode.
     *
     * @param gameMode the game mode.
     */
    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public BankPin getBankpin() {
        return bankpin;
    }

    public ItemDegradationManager getItemDegradationManager() {
        return itemDegradationManager;
    }

    public boolean isMuted() {
        return EntityExtKt.getBoolean(this, Attribute.IS_MUTED, false);
    }

    public void setMuted(boolean muted) {
        EntityExtKt.setBoolean(this, Attribute.IS_MUTED, muted, false);
    }

    public List<Punishment> getActivePunishments() {
        return activePunishments;
    }

    public LogoutPolicy getRemovePlayerContract() {
        return logoutPolicy;
    }

    public void setRemovePlayerContract(LogoutPolicy logoutPolicy) {
        this.logoutPolicy = logoutPolicy;
    }

    public void toggleDebugging(final DebugType type) {

        if (type == DebugType.NONE) {
            getAttributes().remove(Attribute.DEBUG_TYPE);
            if (tab != null) {
                tab = null;
                packetSender.sendWalkableInterface(-1);
                sendMessage("Toggled off debugging!");
                packetSender.sendInterfaceRemoval();
                return;
            }
        }

        getAttributes().stringAttr(Attribute.DEBUG_TYPE, DebugType.NONE.name()).setValue(type.name());

        tab = new DebugTab(this, type);

        packetSender.sendInterfaceRemoval();
    }

    public Position getTeleportDestination() {
        return teleportDestination;
    }

    public void setTeleportDestination(Position teleportDestination) {
        this.teleportDestination = teleportDestination;
    }

    public String getLastDropTableInputText() {
        return lastDropTableInputText;
    }

    public void setLastDropTableInputText(String lastDropTableInputText) {
        this.lastDropTableInputText = lastDropTableInputText;
    }

    public boolean isDropTablesItemSearch() {
        return dropTablesItemSearch;
    }

    public void setDropTablesItemSearch(boolean dropTablesItemSearch) {
        this.dropTablesItemSearch = dropTablesItemSearch;
    }

    public Agent getTeleportToCaster() {
        return teleportToCaster;
    }

    public void setTeleportToCaster(Agent teleportToCaster) {
        this.teleportToCaster = teleportToCaster;
    }

    public void dropUnder(Item item) {
        ItemOnGroundManager.register(this, item, getPosition().copy());
    }

    public void subscribe(PlayerEventListener eventListener) {
        listeners.add(eventListener);
    }

    public InstanceDeadItemCollectionBox getInstanceDeadItemCollectionBox() {
        return instanceDeadItemCollectionBox;
    }

    public void setInstanceDeadItemCollectionBox(InstanceDeadItemCollectionBox instanceDeadItemCollectionBox) {
        this.instanceDeadItemCollectionBox = instanceDeadItemCollectionBox;
    }

    public boolean register() {

        if (World.getPlayers().add(this)) {
            World.getUsernameMap().put(getUsername().toLowerCase(), this);
            Region region = World.getRegions().fromPosition(this.getPosition());
            region.addEntity(this);

            if (rights.isStaff()) {
                World.getStaffMembers().putIfAbsent(rights, new HashSet<>());
                World.getStaffMembers().get(rights).add(this);
            }
            return true;
        }
        return false;
    }

    public void login() {
        onLogin();
        firstSequence.set(true);
    }

    /**
     * @deprecated see {@link WeaponInterfaces#getFightTypeMap(Player)}
     * <p>
     * TODO: remove
     */
    @Deprecated
    public HashMap<WeaponInterface, WeaponFightType> getWeaponFightTypeHashMap() {
        return weaponFightTypeHashMap;
    }

    public MessageCache getMessageCache() {
        return messageCache;
    }

    public List<SearchDropTableWorker.NpcDropTable> getDropTableSearchResults() {
        return dropTableResults;
    }

    public void setDropTableResults(List<SearchDropTableWorker.NpcDropTable> dropTableResults) {
        this.dropTableResults = dropTableResults;
    }

    public Map<String, Long> getAttackedBy() {
        return attackedBy;
    }

    public boolean containsAttackedBy(String username) {
        for (Map.Entry<String, Long> entry : attackedBy.entrySet()) {
            String key = entry.getKey();
            if (key.equals(username)) {
                return true;
            }
        }
        return false;
    }

    private void sequenceAttackedBy() {
        if (!attackedBy.isEmpty()) {
            for (Map.Entry<String, Long> entry : attackedBy.entrySet()) {
                String key = entry.getKey();
                long time = entry.getValue();
                final long timeToRemove = System.currentTimeMillis() - (10 * (1000 * 60));
                if (timeToRemove >= time) {
                    attackedBy.remove(key);
                }
            }
        }
    }

    public void setAttackedBy(Map<String, Long> attackedBy) {
        this.attackedBy = attackedBy;
    }

    public void addToAttackedBy(String attackedBy) {
        this.attackedBy.put(attackedBy, System.currentTimeMillis());
    }

    public int getPrayerDrainCounter() {
        return prayerDrainCounter;
    }

    public void setPrayerDrainCounter(int prayerDrainCounter) {
        this.prayerDrainCounter = prayerDrainCounter;
    }

    public int getX() {
        return this.getPosition().getX();
    }

    public int getY() {
        return this.getPosition().getY();
    }

    @Override
    public int getInteractionIndex() {
        return getIndex() | 0x8000; // TODO magic constant
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.PLAYER;
    }

    public int getZ() {
        return this.getPosition().getZ();
    }

    public boolean inBuildingMode() {
        return getArea() != null
                && getArea() instanceof HouseInstance
                && ((HouseInstance) getArea()).isBuildMode();
    }

    /**
     * Get either the surface or dungeon palette for the POH.
     *
     * @return Room set from the owner of the {@link HouseInstance}.
     */
    public HouseRoom[][][] getCurrentRoomSet() {
        return this.isInHouseDungeon()
                ? ((HouseInstance) getArea()).getHouseOwner().getHouse().getDungeonRooms()
                : ((HouseInstance) getArea()).getHouseOwner().getHouse().getHouseHouseRooms();
    }

    public boolean isInHouseDungeon() {
        return inHouseDungeon;
    }

    public House getHouse() {
        return poh;
    }

    public void setHouse(House house) {
        this.poh = house;
    }

    public void setInHouseDungeon(boolean inHouseDungeon) {
        this.inHouseDungeon = inHouseDungeon;
    }

    public ArrayList<HouseFurniture> getCurrentFurnitureSet() {
        return inHouseDungeon ? getHouse().getDungeonFurniture() : getHouse().getSurfaceFurniture();
    }

    public Set<DynamicGameObject> getLocalObjects() {
        return localObjects;
    }

    public Optional<GameObject> getLocalObject(int id, Position pos) {
        if (localObjects.isEmpty()) {
            return Optional.empty();
        }

        for (DynamicGameObject object : localObjects) {
            if (object.getId() == id && object.getPosition().equals(pos)) {
                return Optional.of(object);
            }
        }

        return Optional.empty();
    }

    public Party getCurrentParty() {
        return currentParty;
    }

    public void setCurrentParty(Party currentParty) {
        this.currentParty = currentParty;
    }

    public final boolean inSaradominLobby() {
        return getPosition().getX() >= 2370 && getPosition().getY() >= 9482 && getPosition().getX() <= 2392 && getPosition().getY() <= 9497;
    }

    public final boolean inZamorakLobby() {
        return getPosition().getX() >= 2411 && getPosition().getY() >= 9514 && getPosition().getX() <= 2429 && getPosition().getY() <= 9533;
    }

    public final boolean inCastleWars() {
        return getPosition().getX() >= 2368 && getPosition().getX() <= 2431 && ((getPosition().getY() >= 3072 && getPosition().getY() <= 3135) || (getPosition().getY() >= 9480 && getPosition().getY() <= 9534)) && !inSaradominLobby() && !inZamorakLobby();
    }

    public final boolean inCastleWarsRegion() {
        return getPosition().getX() >= 2368 && getPosition().getX() <= 2431 && ((getPosition().getY() >= 3072 && getPosition().getY() <= 3135) || (getPosition().getY() >= 9480 && getPosition().getY() <= 9534));
    }

    public final boolean inCastleWarsFront() {
        return (getPosition().getX() >= 2437 && getPosition().getX() <= 3099 && getPosition().getY() >= 3080 && getPosition().getY() <= 3099);
    }

    public CollectionLog getCollectionLog() {
        return collectionLog;
    }
}