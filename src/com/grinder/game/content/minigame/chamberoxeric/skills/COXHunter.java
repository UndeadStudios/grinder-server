package com.grinder.game.content.minigame.chamberoxeric.skills;

import com.grinder.game.World;
import com.grinder.game.content.minigame.chamberoxeric.COXManager;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Skill;
import com.grinder.net.packet.interaction.PacketInteraction;
import com.grinder.util.ItemID;
import com.grinder.util.NpcID;

import java.util.HashMap;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class COXHunter extends PacketInteraction {

    private enum COXBat {

        GUANIC_BAT(NpcID.GUANIC_BAT, ItemID.RAW_GUANIC_BAT_0_, 1, 5),

        PRAEL_BAT(NpcID.PRAEL_BAT, ItemID.RAW_PRAEL_BAT_1_, 15, 9),

        GIRAL_BAT(NpcID.GIRAL_BAT, ItemID.RAW_GIRAL_BAT_2_, 30, 13),

        PHLUXIA_BAT(NpcID.PHLUXIA_BAT, ItemID.PHLUXIA_BAT_3_, 45, 17),

        KRYKET_BAT(NpcID.KRYKET_BAT, ItemID.RAW_KRYKET_BAT_4_, 60, 21),

        MURNG_BAT(NpcID.MURNG_BAT, ItemID.MURNG_BAT_5_, 75, 25),

        PSYKK_BAT(NpcID.PSYKK_BAT, ItemID.RAW_PSYKK_BAT_6_, 90, 29),


        ;

        private int npcId;

        private int itemId;

        private int levelRequired;

        private int experience;

        COXBat(int npcId, int itemId, int levelRequired, int experience) {
            this.npcId = npcId;
            this.itemId = itemId;
            this.levelRequired = levelRequired;
            this.experience = experience;
        }

        private static final HashMap<Integer, COXBat> FOR_NPC = new HashMap<>();

        static {
            for (COXBat b : values()) {
                FOR_NPC.put(b.npcId, b);
                COXManager.RAID_ITEMS.add(b.itemId);
            }
        }

        private static boolean isBat(int id) {
            return FOR_NPC.get(id) != null;
        }
    }

    private static boolean catchBat(Player p, NPC npc) {
        if (!COXBat.isBat(npc.getId())) {
            return false;
        }

        COXBat bat = COXBat.FOR_NPC.get(npc.getId());

        if (!Skill.hasCorrectLevel(p, Skill.HUNTER, bat.levelRequired)) {
            return true;
        }

        if (!p.getEquipment().contains(ItemID.BUTTERFLY_NET) && p.getInventory().contains(ItemID.BUTTERFLY_NET)) {
            p.getPacketSender().sendMessage("You need a butterfly net to catch bats.");
            return true;
        }

        if (p.getInventory().countFreeSlots() < 3) {
            p.getPacketSender().sendMessage("You don't have enough inventory space to catch this bat.");
            return true;
        }

        p.performAnimation(new Animation(6605));

        World.remove(npc);

        p.getInventory().add(bat.itemId, 3);

        p.getSkillManager().addExperience(Skill.HUNTER, bat.experience);

        return true;
    }

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int type) {
        if (catchBat(player, npc)) {
            return true;
        }
        return false;
    }
}
