package com.grinder.game.content.minigame.castlewars;

import com.grinder.game.content.minigame.Party;
import com.grinder.game.content.minigame.blastfurnace.npcs.Ordan;
import com.grinder.game.content.miscellaneous.PetHandler;
import com.grinder.game.definition.ItemDefinition;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Boundary;
import com.grinder.game.model.Coordinate;
import com.grinder.game.model.Position;
import com.grinder.game.model.consumable.edible.Edible;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueManager;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.player.Equipment;
import com.grinder.util.ItemID;
import com.grinder.util.Misc;
import com.grinder.util.oldgrinder.EquipSlot;
import org.apache.commons.lang.mutable.MutableBoolean;

import java.util.stream.Stream;

import static com.grinder.game.content.minigame.castlewars.CastleWars.*;
import static com.grinder.game.content.minigame.castlewars.CastleWarsConstants.*;

public class LobbyManager {

    public static final int SARADOMIN_ITEMS[] = {1718, 2412, 2415, 2661, 2663, 2665, 2667, 3479, 3840, 6762, 10384, 10386, 10388, 10390, 10440, 10446, 10452, 10458, 10464, 10470, 10778, 10784, 10792, 11698, 11730, 18745, 19143, 19152};
    public static final int ZAMORAK_ITEMS[] = {538, 540, 2963, 1724, 11716, 11700, 2414, 2417, 2653, 2655, 2657, 2659, 3478, 3841, 3842, 3852, 6764, 10368, 10370, 10372, 10374, 10444, 10450, 10456, 10460, 10468, 10474, 10776, 10786, 10790, 11700, 11716, 18746, 19149, 19162};
    public static final int GUTHIX_ITEMS[] = {2413, 2416, 2669, 2671, 2673, 2675, 3480, 3843, 3844, 6760, 10376, 10378, 10380, 10382, 10442, 10448, 10454, 10462, 10466, 10472, 10720, 10780, 10788, 10794, 18744, 19146, 19157,};

    public static boolean joinLobbyTeam(Player player, Party team) {
        boolean guthixPortal = false;
        if (team == null) {// Guthix chooses suitable team.
            guthixPortal = true;
            team = getLowerMemberParty(gameState);
            if (team == null) {
                team = getWeakerGameParty(gameState);
                if (team == null) {
                    team = getRandomParty();
                }
            }
        }

        if (canEnterTeam(player, team)) {
            joinLobbyRoom(player, team, guthixPortal ? -1 : team.getTeamID());
            return true;
        }
        return false;
    }

    public static void transformPlayer(Player player, int joinTeam) {
        if (joinTeam == SARADOMIN_TEAM) {
            Equipment playerEquipment = player.getEquipment();

            if (playerEquipment.containsAny(ZAMORAK_ITEMS) || playerEquipment.containsAny(GUTHIX_ITEMS)) {
                player.setNpcTransformationId(NPC_SARADOMIN_RABBIT);
                new DialogueBuilder(DialogueType.STATEMENT)
                        .setText("You wear symbols of foolish and evil gods? Perhaps some", "time spent as the lowliest of forms will help you appreciate the", "gifts that I can bestow upon my followers.")
                        .start(player);
            }
        } else if (joinTeam == ZAMORAK_TEAM) {
            Equipment playerEquipment = player.getEquipment();

            if (playerEquipment.containsAny(SARADOMIN_ITEMS) || playerEquipment.containsAny(GUTHIX_ITEMS)) {
                player.setNpcTransformationId(NPC_ZAMORAK_IMP);
                new DialogueBuilder(DialogueType.STATEMENT)
                        .setText("You come to me wearing symbols of ignorant, weak-minded gods?", "Such treachery must be punished! Enjoy some time in the", "most mischievous of forms.")
                        .start(player);
            }
        } else {
            Equipment playerEquipment = player.getEquipment();
            if (playerEquipment.containsAny(SARADOMIN_ITEMS) || playerEquipment.containsAny(ZAMORAK_ITEMS)) {
                player.setNpcTransformationId(NPC_GUTHIX_SHEEP);
                new DialogueBuilder(DialogueType.STATEMENT)
                        .setText("I pity you wearing symbols of imbalance. I shall bless you with", "some time in the most holy of forms; maybe it wisdom will rub off", "on you and you'll see the error of your ways.")
                        .start(player);
            }
        }
    }

    public static boolean canEnterTeam(Player player, Party team) {
        if (player.getEquipment().get(EquipSlot.HAT).getId() != -1 || player.getEquipment().get(EquipSlot.CAPE).getId() != -1) {
            player.sendMessage("You can't wear hats, capes or helms in the arena.");
            return false;
        }
        /*
        if (!Config.DEVELOPER_MACHINE && isAlreadyPlaying(player)) {
            player.sendMessage("There is one account from your host playing already.");
            return false;
        }
        */

        Party currentParty = team == zamorakParty ? zamorakLobby : saradominLobby;
        Party opositeParty = team == zamorakParty ? saradominLobby : zamorakLobby;
        if (currentParty.memberCount() > opositeParty.memberCount()) {
            player.sendMessage("There isn't enough space on this team.");
            return false;
        }

        for (Item item : player.getInventory().getItems()) {
            int itemID = item.getId();

            for (Edible food : Edible.values()) {
                if (itemID == food.item.getId()) {
                    player.sendMessage("You can't bring food to the arena.");
                    return false;
                }
            }
            if (player.getCurrentPet() != null) {
                player.getPacketSender().sendMessage("You can't bring any pets to the arena.");
                return false;
            }
            for (PetHandler.Pet petIds : PetHandler.Pet.values()) {
                if (itemID == petIds.getItemId()) {
                    player.sendMessage("You can't bring " + ItemDefinition.forId(petIds.getId()).getName() + " to the arena.");
                    return false;
                }
            }
            for (int invalidItem : INVALID_ITEMS) {
                if (itemID == invalidItem) {
                    player.sendMessage("You can't bring " + ItemDefinition.forId(invalidItem).getName() + " to the arena.");
                    return false;
                }
            }
            if (item.getDefinition().getName().toLowerCase().contains(" helm") || item.getDefinition().getName().toLowerCase().contains(" hat") || item.getDefinition().getName().toLowerCase().contains(" hood")
                    || item.getDefinition().getName().toLowerCase().contains(" scarf") || item.getDefinition().getName().toLowerCase().contains(" cape") || item.getDefinition().getName().toLowerCase().contains(" head")
                    || item.getDefinition().getName().toLowerCase().contains(" faceguard") || item.getDefinition().getName().toLowerCase().contains(" coif") || item.getDefinition().getName().toLowerCase().contains("fez")
                    || item.getDefinition().getName().toLowerCase().contains(" halo") || item.getDefinition().getName().toLowerCase().contains(" mask") || item.getDefinition().getName().toLowerCase().contains("ava's ")
                    || item.getDefinition().getName().toLowerCase().contains("backpack") || item.getId() == ItemID.DIVING_APPARATUS || item.getId() == ItemID.GRAIN_3
                    || item.getId() == ItemID.SACK_OF_PRESENTS || item.getId() == 15432 || item.getId() == 22675 || item.getId() == ItemID.BONESACK
                    || item.getId() == 22838 || item.getId() == 23224) {
                    player.sendMessage("You can't bring " + ItemDefinition.forId(item.getDefinition().getId()).getName() + " to the arena.");
                    return false;
                }
            }

        return true;
    }

    private static boolean isAlreadyPlaying(Player player) {
        final MutableBoolean found = new MutableBoolean(false);
        Stream.of(zamorakParty.getPlayers(), saradominParty.getPlayers(), zamorakLobby.getPlayers(), saradominLobby.getPlayers()).parallel().forEach(partyPlayers -> {
            for (Player partyPlayer : partyPlayers) {
                if (partyPlayer != null) {
                    if (partyPlayer.getUID().equals(player.getUID())) {
                        found.setValue(true);
                    }
                }
            }
        });

        return found.booleanValue();
    }

    private static Party getLowerMemberParty(GameState gameState) {
        Party saraTeam = saradominLobby;
        Party zammyTeam = zamorakLobby;

        if (saraTeam.memberCount() < zammyTeam.memberCount()) {
            return saradominParty;
        }
        if (zammyTeam.memberCount() < saraTeam.memberCount()) {
            return zamorakParty;
        }
        return null;
    }

    public static Party getWeakerGameParty(GameState gameState) {
        Party saraTeam = gameState != GameState.RUNNING ? saradominLobby : saradominParty;
        Party zammyTeam = gameState != GameState.RUNNING ? zamorakLobby : zamorakParty;

        if (gameState == GameState.RUNNING) {
            if (CastleWars.teamPoints[ZAMORAK_TEAM] > CastleWars.teamPoints[SARADOMIN_TEAM]) {
                return saradominLobby;
            }

            if (CastleWars.teamPoints[SARADOMIN_TEAM] > CastleWars.teamPoints[ZAMORAK_TEAM]) {
                return zamorakLobby;
            }
        }

        if (saraTeam.memberCount() < zammyTeam.memberCount()) {
            return saradominLobby;
        }

        if (zammyTeam.memberCount() < saraTeam.memberCount()) {
            return zamorakLobby;
        }

        if (saraTeam.getTotalCombatLevel() < zammyTeam.getTotalCombatLevel()) {
            return saradominLobby;
        }
        if (zammyTeam.getTotalCombatLevel() < saraTeam.getTotalCombatLevel()) {
            return zamorakLobby;
        }

        if (gameState == GameState.RUNNING) {
            if (CastleWars.flagState[SARADOMIN_TEAM] != FlagState.SAFE && CastleWars.flagState[ZAMORAK_TEAM] == FlagState.SAFE) {
                return saradominLobby;
            }

            if (CastleWars.flagState[ZAMORAK_TEAM] != FlagState.SAFE && CastleWars.flagState[SARADOMIN_TEAM] == FlagState.SAFE) {
                return zamorakLobby;
            }
        }

        if (saraTeam.getTotalLevel() < zammyTeam.getTotalLevel()) {
            return saradominLobby;
        }

        if (zammyTeam.getTotalLevel() < saraTeam.getTotalLevel()) {
            return zamorakLobby;
        }

        return getRandomLobbyParty();
    }

    private static Party getRandomLobbyParty() {
        return Misc.random(1) == 0 ? saradominLobby : zamorakLobby;
    }

    private static Party getRandomParty() {
        return Misc.random(1) == 0 ? saradominParty : zamorakParty;
    }

    protected static void joinLobbyRoom(Player player, Party team, int joinTeam) {
        Position freeSpot = null;

        if (team.getTeamID() == SARADOMIN_TEAM) {
            freeSpot = findAvailableSpot(new Boundary(2375, 2387, 9485, 9493), 0);
        } else if (team.getTeamID() == ZAMORAK_TEAM) {
            freeSpot = findAvailableSpot(new Boundary(2417, 2427, 9517, 9531), 0);
        }

        if (freeSpot != null) {
            wearTeamItems(player, team);
            transformPlayer(player, joinTeam);

            if (team.getTeamID() == SARADOMIN_TEAM) {
                saradominLobby.addPlayer(player);
            } else if (team.getTeamID() == ZAMORAK_TEAM) {
                zamorakLobby.addPlayer(player);
            }

            if (zamorakLobby.memberCount() >= MIN_TEAM_MEMBERS && saradominLobby.memberCount() >= MIN_TEAM_MEMBERS) {
                int add = 0;
                if (gameState == GameState.RUNNING) {
                    add += 2;
                }
                player.getPacketSender().sendString(11480, "Time until next game starts: " + (getTimeLeft()+add));
            } else {
                player.getPacketSender().sendString(11480, "Waiting for players to join the other team.");
            }

            player.getPacketSender().sendWalkableInterface(11479);

            player.moveTo(new Position(freeSpot.getX(), freeSpot.getY(), 0));

        }

        if (zamorakLobby.memberCount() >= MIN_TEAM_MEMBERS && saradominLobby.memberCount() >= MIN_TEAM_MEMBERS) {
            Stream<Player> stream = Stream.concat(zamorakLobby.getPlayers().stream(), saradominLobby.getPlayers().stream());

            stream.forEach((p) -> {
                if (p != null) {
                    int add = 0;
                    if (gameState == GameState.RUNNING) {
                        add += 2;
                    }
                    p.getPacketSender().sendString(11480, "Time until next game starts: " + (getTimeLeft()+add));
                }
            });
        }
    }

    // Called when user logout from the game.
    protected static void teleportToLobby(Player player, Party team) {
        if (team == saradominParty) {
            player.moveTo(findAvailableSpot(new Boundary(2375, 2387, 9485, 9493), 0));
        } else if (team == zamorakParty) {
            player.moveTo(findAvailableSpot(new Boundary(2417, 2427, 9517, 9531), 0));
        }
    }

    protected static void wearTeamItems(Player player, Party team) {
        if (team == null) {
            return;
        }
        if (team.getTeamID() == SARADOMIN_TEAM) {
            player.getEquipment().set(EquipSlot.HAT, new Item(4513, 1));
            player.getEquipment().set(EquipSlot.CAPE, new Item(4514, 1));
        } else if (team.getTeamID() == ZAMORAK_TEAM) {
            player.getEquipment().set(EquipSlot.HAT, new Item(4515, 1));
            player.getEquipment().set(EquipSlot.CAPE, new Item(4516, 1));
        }
        player.getEquipment().refreshItems();
    }

    protected static void removeTeamEquips(Player player) {
        player.getEquipment().reset(EquipSlot.HAT);
        player.getEquipment().reset(EquipSlot.CAPE);
        player.getEquipment().refreshItems();
    }

    public static void leaveLobbyRoom(Player player, Party team) {

        if (player.getNpcTransformationId() != -1) {
            player.setNpcTransformationId(-1);
        }

        player.moveTo(findAvailableSpot(new Boundary(2439, 2445, 3082, 3097), 0));
        removeTeamEquips(player);
        player.getPacketSender().sendWalkableInterface(-1);
        if (team != null) {
            team.removePlayer(player);
        }
        if (zamorakLobby.memberCount() < MIN_TEAM_MEMBERS || saradominLobby.memberCount() < MIN_TEAM_MEMBERS) {
            Stream<Player> stream = Stream.concat(zamorakLobby.getPlayers().stream(), saradominLobby.getPlayers().stream());

            stream.forEach((p) -> {
                if (p != null) {
                    p.getPacketSender().sendString(11480, "Waiting for players to join the other team.");
                }
            });
        }
    }

    public static void logoutPlayer(Player player) {
        if (player != null) {
            Party party = player.getCurrentParty();
            removeTeamEquips(player);
            if (party == zamorakLobby || party == saradominLobby) {
                party.removePlayer(player);
            }
        }

        if (zamorakLobby.memberCount() < MIN_TEAM_MEMBERS || saradominLobby.memberCount() < MIN_TEAM_MEMBERS) {
            Stream<Player> stream = Stream.concat(zamorakLobby.getPlayers().stream(), saradominLobby.getPlayers().stream());

            stream.forEach((p) -> {
                if (p != null) {
                    p.getPacketSender().sendString(11480, "Waiting for players to join the other team.");
                }
            });
        }
    }

    public static boolean inWaitingRoom(Player player) {
        return player.inSaradominLobby() || player.inZamorakLobby();
    }

    public static void offerSpot(final Party lobbyParty) {

        for (Player player : lobbyParty.getPlayers()) {

            if (!inWaitingRoom(player) && player.getCurrentParty() == lobbyParty) {
                lobbyParty.removePlayer(player);
                continue;
            }
            if (lobbyParty.inParty(player)) {
                if (gameState == GameState.RUNNING) {
                    new DialogueBuilder(DialogueType.OPTION).setOptionTitle("Space Available. Join game?")
                            .firstOption("Yes please!", player2 -> {
                                if (lobbyParty == zamorakLobby) {
                                    player.moveTo(findAvailableSpot(new Boundary(2369, 2376, 3127, 3135), 1));
                                    if (ArrowPointerManager.hasActivePointers(zamorakParty)) {
                                        ArrowPointerManager.showPointer(player);
                                    }
                                    zamorakParty.addPlayer(player);
                                } else {
                                    player.moveTo(findAvailableSpot(new Boundary(2423, 2430, 3072, 3080), 1));
                                    if (ArrowPointerManager.hasActivePointers(saradominParty)) {
                                        ArrowPointerManager.showPointer(player);
                                    }
                                    saradominParty.addPlayer(player);
                                }
                                player.castleWarsIddleTimer = System.currentTimeMillis();
                                resetPlayerProgress(player);
                                player.getPacketSender().sendWalkableInterface(11146);
                                player.getPacketSender().sendInteractionOption("Attack", 2, true);
                                player.getPacketSender().sendString(11155, getTimeLeft() + " Min");

                                if (player.getNpcTransformationId() != -1) {
                                    player.setNpcTransformationId(-1);
                                }
                                playerEnteredBase(player);
                                CastleWars.updateInterface(player);
                                DialogueManager.start(player, -1);
                            })
                            .secondOption("No thanks.", $ -> DialogueManager.start(player, -1))
                            .start(player);
                }
            }
        }

    }
}
