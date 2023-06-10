package com.grinder.game.content.minigame.chamberoxeric.party;

import com.grinder.game.World;
import com.grinder.game.content.clan.ClanChat;
import com.grinder.game.content.minigame.chamberoxeric.COXMob;
import com.grinder.game.content.minigame.chamberoxeric.room.icedemon.IceDemonCOXRoom;
import com.grinder.game.content.minigame.chamberoxeric.room.mutadiles.MutadilesCOXRoom;
import com.grinder.game.content.minigame.chamberoxeric.room.mystics.MysticsCOXRoom;
import com.grinder.game.content.minigame.chamberoxeric.room.olm.OlmCOXRoom;
import com.grinder.game.content.minigame.chamberoxeric.room.shamans.ShamanCOXRoom;
import com.grinder.game.content.minigame.chamberoxeric.room.tekton.TektonCOXRoom;
import com.grinder.game.content.minigame.chamberoxeric.room.vanguard.VanguardCOXRoom;
import com.grinder.game.content.minigame.chamberoxeric.room.vasanistirio.VasaNistirioCOXRoom;
import com.grinder.game.content.minigame.chamberoxeric.room.vespula.VespulaCOXRoom;
import com.grinder.game.content.minigame.chamberoxeric.skills.farming.COXHerbPatch;
import com.grinder.game.content.minigame.chamberoxeric.storage.StorageUnit;
import com.grinder.game.content.minigame.chamberoxeric.storage.StorageUnitManager;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.npc.NPCFactory;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.item.Item;
import com.grinder.game.model.item.container.ItemContainer;
import com.grinder.game.model.item.container.StackType;
import com.grinder.game.model.progresstracker.ProgressTracker;
import com.grinder.util.Misc;
import com.grinder.util.timing.Stopwatch;

import java.util.ArrayList;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class COXParty {

    public static final int MAX_SIZE = 20;

    public Player owner;

    public ClanChat clanChat;

    public ProgressTracker tracker;

    public ItemContainer sharedStorage;

    public StorageUnit storage;

    public COXHerbPatch herbPatch;

    public MutadilesCOXRoom mutadiles;

    public TektonCOXRoom tekton;

    public IceDemonCOXRoom iceDemon;

    public ShamanCOXRoom shaman;

    public VanguardCOXRoom vanguard;

    public MysticsCOXRoom mystics;

    public VasaNistirioCOXRoom vasa;

    public VespulaCOXRoom vespula;

    public OlmCOXRoom olm;

    public Stopwatch time = new Stopwatch();

    public int preferSize;

    public int preferCombatlevel;

    public int preferSkillTotal;

    public COXParty(final Player owner) {
        this.owner = owner;
        this.clanChat = owner.getCurrentClanChat();
        this.tracker = new ProgressTracker();
        this.sharedStorage = new ItemContainer(owner) {

            @Override
            public StackType stackType() {
                return StackType.STACKS;
            }

            @Override
            public ItemContainer refreshItems() {
                return null;
            }

            @Override
            public ItemContainer full() {
                return null;
            }

            @Override
            public int capacity() {
                return 1000;
            }
        };
        this.storage = StorageUnit.TINY;
        this.herbPatch = COXHerbPatch.NEEDS_RAKING;
        this.preferSize = MAX_SIZE;
        this.preferCombatlevel = owner.getSkillManager().calculateCombatLevel();
        this.preferSkillTotal = owner.getSkillManager().countTotalLevel();

    }

    public ArrayList<NPC> sendNpcSpawn(Player player) {
        ArrayList<NPC> npcs = new ArrayList<NPC>();

        if (owner == null) {
            return npcs;
        }

        if (owner.getCurrentClanChat() == null) {
            return npcs;
        }

        int[] levels = new int[7];

        int size = 0;

        for (Player p : owner.getCurrentClanChat().players()) {
            if (p == null) {
                continue;
            }
            for (Skill skill : Skill.COMBAT_SKILLS) {
                levels[skill.ordinal()] += p.getSkillManager().getCurrentLevel(skill);
            }
            size++;
        }

        for (int i = 0; i < levels.length; i++) {
            levels[i] /= size;
        }

        for (COXMob m : COXMob.VALUES) {
            for (int i = 0; i < m.spawnAmount; i++) {
                int id = Misc.randomElement(m.id);
                Position pos = Misc.randomElement(m.spawns).transform(0, 0, player.getPosition().getZ());

                NPC npc = NPCFactory.INSTANCE.create(id, pos);

                int hp = npc.getHitpoints() + (levels[3] * 2);

                npc.setHitpoints(hp);

                //npc.getStatsDefinition().getCombatStats().getDefenceStats()[4] = levels[6] + 40; // magic
                //npc.getStatsDefinition().getCombatStats().getDefenceStats()[3] = levels[0] + 40; // melee
                //npc.getStatsDefinition().getCombatStats().getDefenceStats()[0] = levels[4] + 40; // range

                //npc.getStatsDefinition().getCombatStats().getDefenceStats()[13] = levels[1] + (levels[6] / 3); // magic
                //npc.getStatsDefinition().getCombatStats().getDefenceStats()[10] = levels[1] + (levels[0] / 3); // melee
                //npc.getStatsDefinition().getCombatStats().getDefenceStats()[14] = levels[1] + (levels[4] / 3); // range

                npcs.add(npc);

                World.getNpcAddQueue().add(npc);
            }
        }
        return npcs;
    }

    private void updateStorage(Player player) {
        int size = sharedStorage.getValidItems().size();
        int validItems = (size / 14) * 32;

        player.getPacketSender().sendItemContainer(sharedStorage, StorageUnitManager.SHARED_STORAGE_CONTAINER);
        player.getPacketSender().sendItemContainer(player.getInventory(), 5064);

        player.getPacketSender().sendScrollbarHeight(StorageUnitManager.SHARED_STORAGE_CONTAINER - 1, validItems);

        player.getPacketSender().sendString(28_017, "" + size);
        player.getPacketSender().sendString(28_018, "" + storage.capacity);

        player.getPacketSender().sendInterfaceSet(StorageUnitManager.SHARED_STORAGE_INTERFACE, 5063);
    }

    public void updateStorage() {
        for (Player p : clanChat.players()) {
            if (p == null) {
                continue;
            }
            if (p.getInterfaceId() != StorageUnitManager.SHARED_STORAGE_INTERFACE) {
                continue;
            }
            updateStorage(p);
        }
    }

    public void storeShared(Player player, int id, int amount, int slot) {
        store(player, sharedStorage, id, amount, slot);
    }

    public void withdrawShared(Player player, int id, int amount, int slot) {
        withdraw(player, sharedStorage, id, amount, slot);
    }

    public void store(Player player, ItemContainer container, int id, int amount, int slot) {
        int slotId = player.getInventory().get(slot).getId();
        if (slotId != id) {
            return;
        }

        int existing = container.getValidItems().size();

        int permitted = storage.capacity;

        int freeSlots = container.countFreeSlots();

        if (existing == permitted || freeSlots == 0) {
            player.getPacketSender().sendMessage("Your storage unit is full.");
            if (permitted != 1000) {
                player.getPacketSender().sendMessage("Upgrade your store unit for more space.");
            }
            return;
        }

        amount = player.getInventory().verifyItem(new Item(id, amount), slot);

        if (amount == 0) {
            return;
        }

        if (player.getInventory().contains(id)) {
            Item item = new Item(id, amount);

            player.getInventory().delete(item);

            container.add(item);

            updateStorage();
        }
    }

    public void withdraw(Player player, ItemContainer container, int id, int amount, int slot) {
        if (container.get(slot).getId() != id) {
            return;
        }

        amount = container.verifyItem(new Item(id, amount), slot);

        if (amount == 0) {
            return;
        }

        if (container.getAmount(id) >= amount) {

            container.get(slot).decrementAmountBy(amount);

            if(container.get(slot).getAmount() <= 0) {
                container.reset(slot);
            }

            player.getInventory().add(new Item(id, amount));

            updateStorage();
        }
    }

    public void sendMessage(String message) {
        for(Player p : clanChat.players()) {
            p.getPacketSender().sendMessage(message);
        }
    }
}