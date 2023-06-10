package com.grinder.game.content.minigame.chamberoxeric.skills.farming;

import com.grinder.game.content.minigame.chamberoxeric.COXManager;
import com.grinder.game.content.skill.skillable.impl.farming.FarmingConstants;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Skill;
import com.grinder.game.model.item.Item;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.net.packet.interaction.PacketInteraction;
import com.grinder.util.ItemID;

import java.util.HashMap;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class COXFarming extends PacketInteraction {

    private enum COXSeed {

        GOLPAR_SEED(ItemID.GOLPAR_SEED, ItemID.GRIMY_GOLPAR, 27, 4, 10),

        BUCHU_SEED(ItemID.BUCHU_SEED, ItemID.GRIMY_BUCHU_LEAF, 39, 6, 15),

        NOXIFER_SEED(ItemID.NOXIFER_SEED, ItemID.GRIMY_NOXIFER, 55, 12, 30),

        ;

        private int id;

        private int product;

        private int levelRequired;

        private int plantingXP;

        private int harvestXP;

        COXSeed(int id, int product, int levelRequired, int plantingXP, int harvestXP) {
            this.id = id;
            this.product = product;
            this.levelRequired = levelRequired;
            this.plantingXP = plantingXP;
            this.harvestXP = harvestXP;
        }

        private static final HashMap<Integer, COXSeed> FOR_ID = new HashMap<>();

        static {
            for(COXSeed s : values()) {
                FOR_ID.put(s.id, s);
                COXManager.RAID_ITEMS.add(s.id);
                COXManager.RAID_ITEMS.add(s.product);
            }
        }
    }

    private static void inspect(Player p) {
        COXHerbPatch patch = p.getCOX().getParty().herbPatch;

        if(patch == COXHerbPatch.NEEDS_RAKING) {
            if(!p.getInventory().contains(ItemID.RAKE)) {
                p.getPacketSender().sendMessage("The patch needs raking.");
            } else {
                p.performAnimation(new Animation(FarmingConstants.RAKING_ANIM));
                p.getCOX().getParty().herbPatch = COXHerbPatch.READY_FOR_SEED;
                p.getPacketSender().sendMessage("You rake the herb patch, you can now plant a seed.");
            }
        } else if(patch == COXHerbPatch.READY_FOR_SEED) {
            if(!p.getInventory().contains(ItemID.SEED_DIBBER)) {
                p.getPacketSender().sendMessage("You need a seed dibber to plant a seed.");
                return;
            }

            p.getPacketSender().sendMessage("Use any seed on herb patch to plant it.");
        }
    }

    private static void useSeedOnPatch(Player p, int id) {
        if(p.getCOX().getParty().herbPatch != COXHerbPatch.READY_FOR_SEED) {
            inspect(p);
            return;
        }

        COXSeed seed = COXSeed.FOR_ID.getOrDefault(id, null);

        if(seed == null) {
            p.getPacketSender().sendMessage("You can only use seeds on the herb patch.");
            return;
        }

        if(!Skill.hasCorrectLevel(p, Skill.FARMING, seed.levelRequired)) {
            return;
        }

        if(!p.getInventory().contains(seed.id)) {
            return;
        }

        if(p.getInventory().countFreeSlots() < 5) {
            p.getPacketSender().sendMessage("You need at least 5 inventory space to plant the seed.");
            return;
        }

        p.getCOX().getParty().herbPatch = COXHerbPatch.SEED_PLANTED;

        p.getInventory().delete(seed.id, 1);

        p.performAnimation(new Animation(FarmingConstants.SEED_DIBBING));

        p.getSkillManager().addExperience(Skill.FARMING, seed.plantingXP);

        p.getPacketSender().sendMessage("You plant the seed..");

        TaskManager.submit(new Task(5) {
            @Override
            protected void execute() {

                p.getInventory().add(seed.product, 5);

                p.getSkillManager().addExperience(Skill.FARMING, seed.harvestXP);

                p.getPacketSender().sendMessage("You harvest 5 grimy herbs.");

                p.getCOX().getParty().herbPatch = COXHerbPatch.READY_FOR_SEED;
                stop();
            }
        });
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int type) {
        switch (object.getId()) {
            case 29765:
                inspect(player);
                return true;
        }
        return false;
    }

    @Override
    public boolean handleItemOnObjectInteraction(Player player, Item item, GameObject object) {
        if(object.getId() == 29765) {
            useSeedOnPatch(player, item.getId());
            return true;
        }
        return false;
    }
}
