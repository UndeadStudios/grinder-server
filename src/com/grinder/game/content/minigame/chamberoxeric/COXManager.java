package com.grinder.game.content.minigame.chamberoxeric;

import com.grinder.game.content.minigame.chamberoxeric.party.COXParty;
import com.grinder.game.content.minigame.chamberoxeric.room.COXMap;
import com.grinder.game.content.minigame.chamberoxeric.storage.StorageUnitManager;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainer;
import com.grinder.game.model.item.container.StackType;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.net.packet.interaction.PacketInteraction;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class COXManager extends PacketInteraction {

    public static final HashMap<String, COXParty> RAID_PARTIES = new HashMap<>();

    public static final ArrayList<Integer> RAID_ITEMS = new ArrayList<>();

    public static final Position EXIT = new Position(1238, 3559);

    public StorageUnitManager storage;

    private COXParty raidParty;

    public Task skillTask;

    public int points;

    public ItemContainer reward;

    public COXManager() {

    }

    public COXManager(Player p) {
        this.storage = new StorageUnitManager(p);
        this.reward = new ItemContainer(p) {
            @Override
            public ItemContainer full() {
                return null;
            }

            @Override
            public int capacity() {
                return 3;
            }

            @Override
            public StackType stackType() {
                return StackType.DEFAULT;
            }

            @Override
            public ItemContainer refreshItems() {
                getPlayer().getPacketSender().sendItemContainer(this, COXInterface.REWARD_WIDGET_CONTAINER);
                return null;
            }
        };
    }

    public static void makeParty(final Player player) {
        if (player.getCurrentClanChat() == null) {
            player.getPacketSender()
                    .sendMessage("You need to be in your own Clan Chat channel to create a Raid party.");
            return;
        }

        if (!player.getCurrentClanChat().getOwnerName().equalsIgnoreCase(player.getUsername())) {
            player.getPacketSender()
                    .sendMessage("You need to be in your own Clan Chat channel to create a Raid party.");
            return;
        }

        COXParty existingParty = getPartyByUsername(player.getUsername());

        if (player.getCOX().getParty() == null && existingParty != null) {
            player.getCOX().setRaidParty(existingParty);
        }

        if (getPartyByUsername(player.getUsername()) != null && player.getCOX().getParty() != null) {
            COXInterface.openMyParty(player);
            return;
        }

        final COXParty party = new COXParty(player);

        for (Player s : player.getCurrentClanChat().players()) {
            if (s == null) {
                continue;
            }
            s.getCOX().setRaidParty(party);
        }

        player.getCOX().setRaidParty(party);

        RAID_PARTIES.put(player.getUsername(), party);
    }

    public static void disbandParty(final Player player) {

        COXParty party = getPartyByUsername(player.getUsername());

        if (party == null) {
            player.getPacketSender().sendMessage("You don't have a Raid party.");
            return;
        }

        for (Player p : party.clanChat.players()) {
            if (p == null) {
                continue;
            }

            p.getPacketSender().sendMessage("The owner has disbanded the Raid Party.");
            p.getCOX().setRaidParty(null);
        }

        player.getCOX().setRaidParty(null);

        player.getPacketSender().sendMessage("You disband the Raid party.");

        RAID_PARTIES.remove(player.getUsername());
    }

    public static COXParty getPartyByUsername(String username) {
        if (RAID_PARTIES.get(username) == null) {
            return null;
        }
        return RAID_PARTIES.get(username);
    }

    public static void begin(Player p) {
        COXParty party = getPartyByUsername(p.getUsername());

        if (party == null) {
            p.getPacketSender().sendMessage("You need to have a party to start the raids.");
            return;
        }

        if (!party.owner.getUsername().equalsIgnoreCase(p.getUsername())) {
            p.getPacketSender().sendMessage("Only the party leader can start the raid.");
            return;
        }

        p.getPacketSender().sendFadeScreen("The raid has begun!", 2, 5);

        TaskManager.submit(new Task(3) {
            @Override
            protected void execute() {
                COXMinigame minigame = new COXMinigame();

                minigame.start(p);

                party.sendNpcSpawn(p);
                stop();
            }
        });
    }

    private static void leave(Player p) {
        new DialogueBuilder(DialogueType.OPTION)
                .firstOption("Leave", $ -> reset(p))
                .secondOption("Stay", $ -> p.getPacketSender().sendInterfaceRemoval())
                .start(p);
    }

    private static void reset(Player p) {
        if (p.instance != null) {
            p.instance.destroy();
            p.instance = null;
        }

        withdrawPrivateStorage(p);

        clearRaidItems(p);

        p.getPacketSender().sendFadeScreen("You have completed the raid!", 2, 5);

        p.delayedMoveTo(EXIT, 3);
    }

    public static void withdrawPrivateStorage(Player p) {
        ArrayList<Item> items = p.getCOX().storage.privateStorage.getValidItems();

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            p.getCOX().getParty().withdraw(p, p.getCOX().storage.privateStorage, item.getId(), item.getAmount(), i);
        }

        p.getCOX().storage.privateStorage.resetItems();
    }

    public static void clearRaidItems(Player p) {
        for (Item item : p.getInventory().getValidItems()) {
            if(RAID_ITEMS.contains(item.getId())) {
                p.getInventory().add(item);
            }
        }
    }

    public static int getAccumulativeLevel(Player p, Skill skill) {
        if (p.getCOX().getParty() == null) {
            return 1;
        }

        int total = 0;
        for (Player p1 : p.getCOX().getParty().clanChat.players()) {
            if (p1 == null) {
                continue;
            }
            total += p1.getSkillManager().getCurrentLevel(skill);
        }
        int size = p.getCOX().getParty().clanChat.players().size();
        int level = (total / size);
        return level;
    }

    public COXParty getParty() {
        return raidParty;
    }

    public void setRaidParty(final COXParty raidParty) {
        this.raidParty = raidParty;
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int type) {
        switch (object.getId()) {
            case 29777:
                begin(player);
                return true;
            case 29778:
                leave(player);
                return true;
            case 30028:
                player.getCOX().reward.refreshItems();
                player.getPacketSender().sendInterface(COXInterface.REWARD_WIDGET);
                return true;
        }
        return false;
    }

    @Override
    public boolean handleCommand(Player player, String command, String[] args) {
        if (!player.getRights().isStaff()) {
            return false;
        }
        if (command.startsWith("fsr1")) {
            if (player.instance != null) {
                player.instance.destroy();
            }
            makeParty(player);
            begin(player);
            player.getPacketSender().sendMessage("Force start Raids 1");
            return true;
        } else if (command.startsWith("test-olm")) {
            if (player.instance != null) {
                player.instance.destroy();
            }
            makeParty(player);
            begin(player);
            player.delayedMoveTo(COXMap.OLM.position, 4);
            player.getPacketSender().sendMessage("Testing olm");
            return true;
        } else if (command.startsWith("coxrewards")) {
            player.getPacketSender().sendInterface(COXInterface.REWARD_WIDGET);
            TaskManager.submit(new Task(1) {
                @Override
                protected void execute() {
                    if(player.getInterfaceId()!=COXInterface.REWARD_WIDGET) {
                        stop();
                        return;
                    }

                    COXRewards.grantReward(player);
                }
            });
            player.getPacketSender().sendMessage("Testing automated rewards");
            return true;
        }
        return false;
    }
}
