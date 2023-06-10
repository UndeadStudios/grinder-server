package com.grinder.game.model.areas;

import com.grinder.game.World;
import com.grinder.game.content.minigame.MinigameManager;
import com.grinder.game.content.minigame.castlewars.CastleWars;
import com.grinder.game.content.minigame.impl.BattleRoyaleMinigame;
import com.grinder.game.content.minigame.impl.agility.AgilityArenaManager;
import com.grinder.game.content.minigame.impl.agility.AgilityPyramidManager;
import com.grinder.game.content.minigame.pestcontrol.PestControl;
import com.grinder.game.content.minigame.pestcontrol.PestControlInstance;
import com.grinder.game.entity.agent.Agent;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.event.impl.AreaChangedEvent;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.Position;
import com.grinder.game.model.area.RegionRepository;
import com.grinder.game.model.area.RegionSection;
import com.grinder.game.model.areas.godwars.GodChamber;
import com.grinder.game.model.areas.godwars.GodwarsArea;
import com.grinder.game.model.areas.godwars.NexChamber;
import com.grinder.game.model.areas.impl.*;
import com.grinder.game.model.areas.impl.slayer.*;
import com.grinder.game.model.areas.instanced.ApeAtollDungeon;
import com.grinder.game.model.areas.instanced.AquaisNeigeArea;
import com.grinder.game.model.areas.instanced.FightCaveArea;

import java.util.*;

public class AreaManager {

    public static List<Area> areas = new ArrayList<>();

    private static List<Area> wildernessAreas = new ArrayList<>();

    private static final MultiAreas MULTI_AREAS = new MultiAreas();

    /**
     * Note: do not use this for checking if an agent or position is in wild,
     * use {@link #inWilderness(Agent)} for that, there may me more {@link WildernessArea wilderness areas}.
     */
    public static final WildernessArea WILD = new WildernessArea();
    //public static final WildernessAgility WILDERNESS_AGILITY = new WildernessAgility();
    //public static final ResourceArea RESOURCE_AREA = new ResourceArea();

    public static final BarrowsArea BARROWS = new BarrowsArea();
    public static final DuelArenaArea DUEL_ARENA = new DuelArenaArea();
    public static final DuelFightArena DuelFightArena = new DuelFightArena();
    public static final GiantMoleCave GIANT_MOLE_CAVE = new GiantMoleCave();
    public static final GiantSeaSnakeSwamp GIANT_SEA_SNAKE_SWAMP = new GiantSeaSnakeSwamp();
    public static final DagannothKings DAGANNOTH_KINGS_AREA = new DagannothKings();
    public static final IceKingTrollMountain ICE_MOUNTAIN = new IceKingTrollMountain();

    public static final KalphiteLair KALPHITE_LAIR = new KalphiteLair();
    public static final NexChamber NEX_CHAMBER = new NexChamber();
    //public static final KamilArea KAMIL = new KamilArea();
    public static final GodwarsArea GOD_WARS_AREA = new GodwarsArea();
    public static final FreePvPArena FREE_PVP_ARENA = new FreePvPArena();
    public static final FreePvPSafeZone FREE_PVP_SAFEZONE = new FreePvPSafeZone();
    public static final LaIslaEbana LA_ISLA_EBANA = new LaIslaEbana();
    public static final BankAreas BANK_AREAS = new BankAreas();
    public static final DicingArea DICING_AREA = new DicingArea();
    public static final RevenantsCave REVENANTS_CAVE = new RevenantsCave();
    public static final SlayerCaveArea SLAYERCAVE_AREA = new SlayerCaveArea();
    public static final PublicMinigameLobby MINIGAME_LOBBY = new PublicMinigameLobby();
    //public static final GodZillaArea GOD_ZILLA_AREA = new GodZillaArea();
    //public static final BoxingRingArea BOXING_RING_AREA = new BoxingRingArea();
    public static final SlayerTowerArea SLAYER_TOWER_AREA = new SlayerTowerArea();
    public static final AncientDungeonArea ANCIENT_DUNGEON_AREA = new AncientDungeonArea();
    public static final LizardmanShamanArea LIZARDMAN_SHAMAN_AREA = new LizardmanShamanArea();
    public static final WarriorsGuildArea WARRIORS_GUILD_AREA = new WarriorsGuildArea();
    public static final CatacombsOfKourendArea CATACOMBS_OF_KOUREND_AREA = new CatacombsOfKourendArea();
    public static final WoodcuttingGuildArea WOODCUTTING_GUILD_AREA = new WoodcuttingGuildArea();
    public static final MotherlodeMineArea MOTHERLODE_MINE_AREA = new MotherlodeMineArea();
    public static final AlKharidianDesertArea AL_KHARIDIAN_DESERT_AREA = new AlKharidianDesertArea();
    public static final SmokeDungeonArea SMOKE_DUNGEON_AREA = new SmokeDungeonArea();
    public static final BlastFurnaceArea BLAST_FURNACE_AREA = new BlastFurnaceArea();
    public static final AgilityArenaManager BRIMHAVEN_AGILITY = new AgilityArenaManager();
    public static final AgilityPyramidManager AGILITY_PYRAMID = new AgilityPyramidManager();
    public static final ApeAtollDungeon APE_ATOLL_DUNGEON = new ApeAtollDungeon();
    public static final StrongholdSlayerCave STRONGHOLD_SLAYER_CAVE = new StrongholdSlayerCave();
    public static final LizardmanCave LIZARDMAN_CAVE = new LizardmanCave();
    public static final SmokeDevilDungeonArea SMOKE_DEVIL_DUNGEON_AREA = new SmokeDevilDungeonArea();
    public static final KaruulmDungeon KARUULM_DUNGEON = new KaruulmDungeon();
    public static final CastleWars CASTLE_WARS = new CastleWars();
    //public static final PestControlArea PEST_CONTROL_AREA = new PestControlArea();
    public static final AquaisNeigeArea AQUAIS_NEIGE_AREA = new AquaisNeigeArea();
    public static final FightCaveArea FIGHT_CAVE_AREA = new FightCaveArea();
    public static final BattleRoyaleMinigame BATTLE_ROYALE_MINIGAME = new BattleRoyaleMinigame();

	public static boolean inWilderness(Agent agent){
        return inWilderness(agent.getPosition());
    }

    public static boolean inWilderness(Position position) {
        for (Area area : wildernessAreas) {
            if (area.contains(position))
                return true;
        }
        return false;
    }


    public static void load() {
        areas.add(GodChamber.ARMADYL.getArea());
        areas.add(GodChamber.BANDOS.getArea());
        areas.add(GodChamber.SARADOMIN.getArea());
        areas.add(GodChamber.ZAMORAK.getArea());

        areas.add(GOD_WARS_AREA);
        areas.add(MinigameManager.WEAPON_GAME);
        areas.add(MinigameManager.BATTLE_ROYALE);
        areas.add(BARROWS);
        areas.add(DUEL_ARENA);
        areas.add(DuelFightArena);
        areas.add(GIANT_MOLE_CAVE);
        areas.add(GIANT_SEA_SNAKE_SWAMP);
        areas.add(DAGANNOTH_KINGS_AREA);
        areas.add(ICE_MOUNTAIN);
        //areas.add(WILDERNESS_AGILITY);
        areas.add(KALPHITE_LAIR);
        areas.add(NEX_CHAMBER);
        //areas.add(KAMIL);
        areas.add(FREE_PVP_ARENA);
        areas.add(FREE_PVP_SAFEZONE);
        areas.add(LA_ISLA_EBANA);
        areas.add(BANK_AREAS);
        areas.add(DICING_AREA);
        areas.add(REVENANTS_CAVE);
       // areas.add(RESOURCE_AREA);
        areas.add(MINIGAME_LOBBY);
        //areas.add(GOD_ZILLA_AREA);
        //areas.add(BOXING_RING_AREA);
        areas.add(SLAYER_TOWER_AREA);
        areas.add(SLAYERCAVE_AREA);
        areas.add(STRONGHOLD_SLAYER_CAVE);
        areas.add(LIZARDMAN_CAVE);
        areas.add(SMOKE_DEVIL_DUNGEON_AREA);
        areas.add(KARUULM_DUNGEON);
        areas.add(APE_ATOLL_DUNGEON);
        areas.add(ANCIENT_DUNGEON_AREA);
        areas.add(LIZARDMAN_SHAMAN_AREA);
        areas.add(WARRIORS_GUILD_AREA);
        areas.add(CATACOMBS_OF_KOUREND_AREA);
        areas.add(WOODCUTTING_GUILD_AREA);
        areas.add(MOTHERLODE_MINE_AREA);
        areas.add(AL_KHARIDIAN_DESERT_AREA);
        areas.add(SMOKE_DUNGEON_AREA);
        areas.add(BLAST_FURNACE_AREA);
        areas.add(BRIMHAVEN_AGILITY);
        areas.add(AGILITY_PYRAMID);
        areas.add(CASTLE_WARS);
        //areas.add(PEST_CONTROL_AREA);
        areas.add(AQUAIS_NEIGE_AREA);
        areas.add(FIGHT_CAVE_AREA);
        areas.add(BATTLE_ROYALE_MINIGAME);

        areas.add(WILD);
        areas.add(MULTI_AREAS);

        areas.add(new ShadowDungeonArea());
        areas.add(new DessourGraveArea());
        areas.add(new TrollheimIceMountain());
        //areas.add(new MultiAreas());

        //Check for wilderness areas and add them to the wilderness list to use for wilderness checks to reduce redundancy
        for (Area area : areas) {
            if (area instanceof WildernessArea) {
                wildernessAreas.add(area);
            }
        }

        mapWorldAreas();
    }

    /**
     * Add boundaries to a 64x64 region system to reference areas
     */
    private static void mapWorldAreas() {

        for (Area area : areas) {
            mapAreaToRegion(area);
        }
    }

    public static void mapAreaToRegion(Area area) {
        //Lesik code
        RegionRepository repo = World.getRegions();
        for (Boundary b : area.boundaries()) {
            int startX = b.getX() >> 6;
            int startY = b.getY() >> 6;
            int endX = b.getX2() >> 6;
            int endY = b.getY2() >> 6;

            for (int x = 0, xL = endX - startX; x <= xL; x++) {
                for (int y = 0, yL = endY - startY; y <= yL; y++) {
                    int regionId = (startX + x) << 8 | (startY + y);
                    RegionSection region = repo.getSection(regionId);
                    region.addArea(area, b);
                }
            }
        }
    }

    /**
     * Gets the total players in the area
     */
    public static int getPlayersInArena(Area area) {
        return getPlayersInBoundaries(area.boundaries());
    }

    /**
     * Gets the total players in the boundary
     */
    public static int getPlayersInBoundaries(Boundary... boundaries) {
        int total = 0;
        for (Player players : World.getPlayers()) {
            if (players == null) {
                continue;
            }
            if (inside(players.getPosition(), boundaries)) {
                total++;
            }
        }
        return total;
    }

    /**
     * Gets the total players in the boundaries
     */
    public static int getPlayersInBoundaries(List<Boundary> boundaries) {
        int total = 0;
        for (Player players : World.getPlayers()) {
            if (players == null) {
                continue;
            }
            if (inside(players.getPosition(), boundaries.toArray(new Boundary[0]))) {
                total++;
            }
        }
        return total;
    }

    /**
     * Gets the players in the area
     */
    public static Player[] getPlayers(Area area) {
        return getPlayers(area.boundaries());
    }

    /**
     * Gets the players in the boundary
     */
    public static Player[] getPlayers(Boundary... boundaries) {
        return getPlayers(Arrays.asList(boundaries));
    }

    /**
     * Gets the players in the boundaries
     */
    public static Player[] getPlayers(List<Boundary> boundaries) {
        final Boundary[] boundariesArray = boundaries.toArray(new Boundary[0]);
        final List<Player> players = new ArrayList<>();
        for (Player player : World.getPlayers()) {
            if (player == null) {
                continue;
            }
            if (inside(player.getPosition(), boundariesArray)) {
                players.add(player);
            }
        }
        return players.toArray(new Player[0]);
    }

    public static void checkAreaChanged(Agent agent) {

        final Position position = agent.getPosition();
        final Area oldArea = agent.getArea();
        Area area = oldArea;



        if (area != null && !area.contains(position)) {
            area.leave(agent);
            if(agent.instancedArea != null) {
                agent.instancedArea.destroy();
            }
            area = null;
        }

        if (area == null) {
            if (agent.isCachedAreaDirty()) {
                final Area newArea = get(position);
                if (newArea != null) {
                    newArea.enter(agent);
                }
                area = newArea;
                agent.setCachedArea(newArea);
                agent.setCachedAreaDirty(true);
            } else
                area = agent.getCachedArea();
        }

        //set multi boolean
        setMultiBoolean(agent);

        if (agent instanceof Player) {

            final Player player = (Player) agent;
            final int multiIcon = inMulti(player) ? 1 : 0;

            if (player.getMultiIcon() != multiIcon)
                player.getPacketSender().sendMultiIcon(multiIcon);

            if (oldArea != area)
                player.notify(new AreaChangedEvent(oldArea, area));
        }

        if (area != oldArea)
            agent.setArea(area);
    }

    /**
     * Processes areas for the given character.
     */
    public static void sequence(Agent agent) {

        final Area area = agent.getArea();

        if (area != null)
            area.process(agent);
    }

    /**
     * Checks if a {@link Agent} is in multi.
     */
    public static boolean inMulti(Agent c) {
        if(c.isPlayer()) {
            Player p = c.getAsPlayer();
            if(p.instance != null) {
                if(p.instance.inMulti(p)) {
                    return true;
                }
            }
        }
        return c.isInMulti();
    }

    public static void setMultiBoolean(Agent c) {
        if (c.getArea() != null && c.getArea().isMulti(c)) {
            if (!c.isInMulti())
                c.setInMulti(true);
            return;
        }

        PestControlInstance instance = PestControl.PEST_CONTROL_INSTANCE;
        if (instance != null && c.isPlayer()) {
            if (instance.hasPlayer(c.getAsPlayer())) {
                if (!c.isInMulti())
                    c.setInMulti(true);
            }
        }
        else if (instance != null && c.isNpc()) {
            if (PestControl.spawnedNpcs.contains(c)) {
                if (!c.isInMulti())
                    c.setInMulti(true);
            }
        }
        Position position = c.getPosition();
        RegionRepository repo = World.getRegions();
        RegionSection region = repo.getSection(position.getRegionId());
        Iterator<Map.Entry<Boundary, Area>> itr = region.areaMap.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<Boundary, Area> entry = itr.next();
            Boundary boundary = entry.getKey();
            Area area = entry.getValue();
            if (area.isMulti(c) && boundary.contains(position)) {
                if (!c.isInMulti())
                    c.setInMulti(true);
                return;
            }
        }
        if (c.isInMulti())
            c.setInMulti(false);
    }

    public static boolean allInMulti(Agent... agents) {
        for (Agent agent : agents) {
            if (!inMulti(agent))
                return false;
        }
        return true;
    }

    /**
     * Checks if a {@link Agent} can attack another one.
     */
    public static boolean canAttack(Agent attacker, Agent target) {

        if (attacker.getArea() != null)
            return attacker.getArea().canAttack(attacker, target);

        // Don't allow PvP by default
        return !attacker.isPlayer() || !target.isPlayer();
    }

	/**
	 * Gets a {@link Area} based on a given {@link Position}.
	 */
	public static Area get(Position position) {
        RegionRepository repo = World.getRegions();
        RegionSection region = repo.getSection(position.getRegionId());
        Iterator<Map.Entry<Boundary, Area>> itr = region.areaMap.entrySet().iterator();
        while(itr.hasNext()) {
            Map.Entry<Boundary, Area> entry = itr.next();
            Boundary boundary = entry.getKey();
            Area area = entry.getValue();
            if (area instanceof MultiAreas)
                continue;
            if (boundary.contains(position))
                return area;
        }
        return null;
    }

    /**
     * Checks if a position is inside of an area's boundaries.
     */
    public static boolean inside(Position position, Boundary... boundaries) {
        for (Boundary b : boundaries) {
            if (b.contains(position)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if a position is inside of an area's boundaries.
     */
    public static boolean inside(Position position, Area area) {
        for (Boundary b : area.boundaries()) {
            if (b.contains(position)) {
                return true;
            }
        }
        return false;
    }
}