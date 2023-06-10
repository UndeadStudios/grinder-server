package com.grinder.game.content.minigame.chamberoxeric.skills;

import com.grinder.game.content.minigame.chamberoxeric.COXManager;
import com.grinder.game.content.skill.skillable.impl.fishing.FishingTool;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Skill;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;
import com.grinder.net.packet.interaction.PacketInteraction;
import com.grinder.util.ItemID;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class COXFishing extends PacketInteraction {

    public enum COXFish {

        PYSK_FISH(ItemID.RAW_PYSK_FISH_0_, ItemID.PYSK_FISH_0_, 1, 1, 20, 20, 5),

        SUPHI_FISH(ItemID.RAW_SUPHI_FISH_1_, ItemID.SUPHI_FISH_1_, 15, 15, 23, 13, 5),

        LECKISH_FISH(ItemID.RAW_LECKISH_FISH_2_, ItemID.LECKISH_FISH_2_, 30, 30, 26, 16, 11),

        BRAWK_FISH(ItemID.RAW_BRAWK_FISH_3_, ItemID.BRAWK_FISH_3_, 45, 45, 29, 19, 14),

        MYCIL_FISH(ItemID.RAW_MYCIL_FISH_4_, ItemID.MYCIL_FISH_4_, 60, 60, 32, 22, 17),

        ROQED_FISH(ItemID.RAW_ROQED_FISH_5_, ItemID.ROQED_FISH_5_, 75, 75, 35, 25, 20),

        KYREN_FISH(ItemID.RAW_KYREN_FISH_6_, ItemID.KYREN_FISH_6_, 90, 90, 38, 28, 23),

        ;

        private int rawId;

        private int cookedId;

        private int fishingLevel;

        private int cookingLevel;

        private int fishingExp;

        private int cookingExp;

        private int heals;

        COXFish(int rawId, int cookedId, int fishingLevel, int cookingLevel, int fishingExp, int cookingExp, int heals) {
            this.rawId = rawId;
            this.cookedId = cookedId;
            this.fishingLevel = fishingLevel;
            this.cookingLevel = cookingLevel;
            this.fishingExp = fishingExp;
            this.cookingExp = cookingExp;
            this.heals = heals;
        }

        private static final COXFish[] VALUES = values();

        private static COXFish getFish(Player p) {
            COXFish fish = null;
            for (COXFish f : VALUES) {
                if (COXManager.getAccumulativeLevel(p, Skill.FISHING) >= f.fishingLevel) {
                    fish = f;
                }
            }
            return fish;
        }

        static {
            for(COXFish f : VALUES) {
                COXManager.RAID_ITEMS.add(f.rawId);
                COXManager.RAID_ITEMS.add(f.cookedId);
            }
        }
    }


    private static void startFishing(Player p, GameObject object) {
        if (p.getCOX().skillTask != null) {
            return;
        }

        if (p.getInventory().countFreeSlots() == 0) {
            p.getPacketSender().sendMessage("You don't have any inventory space to fish.");
            return;
        }

        if(!p.getInventory().contains(ItemID.BIG_FISHING_NET)) {
            p.getPacketSender().sendMessage("You need a big fishing net to fish here.");
            return;
        }

        p.setPositionToFace(object.getPosition());

        COXFish fish = COXFish.getFish(p);

        FishingTool tool = FishingTool.BIG_FISHING_NET;

        p.performAnimation(tool.findTool(p).getStartAnim());

        Task task = new Task(5) {
            @Override
            protected void execute() {

                if (p.getPosition().getDistance(object.getPosition()) > 1) {
                    stop();
                    return;
                }

                if (p.getInventory().countFreeSlots() == 0) {
                    stop();
                    return;
                }

                p.performAnimation(tool.findTool(p).getStartAnim());

                p.getInventory().add(fish.rawId, 1);
                p.getSkillManager().addExperience(Skill.FISHING, fish.fishingExp);
            }

            @Override
            public void onStop() {
                p.getCOX().skillTask = null;
                p.resetAnimation();
            }
        };

        p.getCOX().skillTask = task;

        TaskManager.submit(task);
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int type) {
        switch (object.getId()) {
            case 29889:
                startFishing(player,object);
                return true;
        }
        return false;
    }
}
