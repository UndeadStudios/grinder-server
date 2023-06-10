package com.grinder.game.content.minigame.motherlodemine.sack;

import com.google.gson.annotations.Expose;
import com.grinder.game.World;
import com.grinder.game.content.minigame.motherlodemine.MotherlodeMine;
import com.grinder.game.content.minigame.motherlodemine.vien.Vien;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Skill;
import com.grinder.game.model.area.RegionCoordinates;
import com.grinder.game.model.interfaces.dialogue.DialogueBuilder;
import com.grinder.game.model.interfaces.dialogue.DialogueType;
import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;

/**
 * @author L E G E N D
 * @date 2/9/2021
 * @time 4:50 AM
 * @discord L E G E N D#4380
 */
public final class Sack {

    public static final int VARBIT = 5558;

    @Expose
    private SackType type;
    @Expose
    private int nuggets;
    @Expose
    private int coal;
    @Expose
    private int gold;
    @Expose
    private int mithril;
    @Expose
    private int adamantite;
    @Expose
    private int runite;

    public Sack() {
        this.type = SackType.NORMAL;
    }

    static {
        // update varbit every time visiting this region
        World.getRegions().get(new RegionCoordinates(3748 >> 3, 5659 >> 3)).addRegionLoadAction(player -> Sack.updateVarbit(player));
    }

    public static void collectOres(Player player) {
        if (player.getMotherlodeMine().getSack().isSackEmpty()) {
            new DialogueBuilder(DialogueType.STATEMENT).setText("The sack is empty.").start(player);
            return;
        }
        while (player.getInventory().countFreeSlots() > 0) {
            if (player.getMotherlodeMine().getSack().getAmountInSack() <= 0) {
                break;
            }
            collect(player, Vien.NUGGET);
            collect(player, Vien.RUNITE);
            collect(player, Vien.ADAMANTITE);
            collect(player, Vien.MITHRIL);
            collect(player, Vien.GOLD);
            collect(player, Vien.COAL);
        }
        MotherlodeMine.updateInterface(player);
        updateVarbit(player);
        if (player.getMotherlodeMine().getSack().isSackEmpty()) {
            new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(ItemID.PAY_DIRT, 200)
                    .setText("You collect your ore from the sack.", "The sack is now empty.")
                    .start(player);
        } else {
            new DialogueBuilder(DialogueType.ITEM_STATEMENT_NO_HEADER).setItem(ItemID.PAY_DIRT, 200)
                    .setText("You collect your ore from the sack.")
                    .start(player);
        }
        player.getInventory().refreshItems();
    }

    private static void updateVarbit(Player player) {
        player.getPacketSender().sendVarbit(VARBIT, Math.min(254, player.getMotherlodeMine().getSack().getAmountInSack()));
    }

    public static void collect(Player player, Vien vien) {
        final var freeSlots = player.getInventory().countFreeSlots();
        var amount = player.getMotherlodeMine().getSack().getAmountInSack(vien);
        if (freeSlots < amount) {
            amount = freeSlots;
        }
        if (amount == 0) {
            return;
        }
        player.getInventory().add(new Item(vien.getItemId(), amount), true);
        var sack = player.getMotherlodeMine().getSack();
        switch (vien) {
            case NUGGET:
                sack.nuggets -= amount;
                break;
            case COAL:
                sack.coal -= amount;
                break;
            case GOLD:
                sack.gold -= amount;
                break;
            case MITHRIL:
                sack.mithril -= amount;
                break;
            case ADAMANTITE:
                sack.adamantite -= amount;
                break;
            case RUNITE:
                sack.runite -= amount;
                break;
        }
    }

    public static void fromMachineToSack(Player player) {
        int ores = player.getMotherlodeMine().getOresInMachine();
        int totalXp = 0;
        while (ores > 0) {
            var vien = Vien.roll(player.getSkillManager().getCurrentLevel(Skill.MINING));
            //System.out.println(vien);
            var xpToAdd = vien.getExperience();
            player.getMotherlodeMine().getSack().addVienToSack(vien);
            totalXp += xpToAdd;
            ores--;
        }
        MotherlodeMine.removeNpc(player);
        MotherlodeMine.resetOresInMachine(player);
        MotherlodeMine.updateInterface(player);
        player.getSkillManager().addExperience(Skill.MINING, totalXp);
        updateVarbit(player);
        if (player.getMotherlodeMine().getSack().isSackFull()) {
            player.sendMessage("Some ore is ready to be collected from the sack. It's getting full.");
        } else {
            player.sendMessage("Some ore is ready to be collected from the sack.");
        }
    }

    public void addVienToSack(Vien... viens) {
        for (var vien : viens) {
            switch (vien) {
                case NUGGET:
                    nuggets++;
                    break;
                case COAL:
                    coal++;
                    break;
                case GOLD:
                    gold++;
                    break;
                case MITHRIL:
                    mithril++;
                    break;
                case ADAMANTITE:
                    adamantite++;
                    break;
                case RUNITE:
                    runite++;
                    break;
            }
        }
    }

    public int getAmountInSack(Vien vien) {
        switch (vien) {
            case NUGGET:
                return nuggets;
            case COAL:
                return coal;
            case GOLD:
                return gold;
            case MITHRIL:
                return mithril;
            case ADAMANTITE:
                return adamantite;
            case RUNITE:
                return runite;
            default:
                return 0;
        }
    }

    public int getAmountInSack() {
        return nuggets + coal + gold + mithril + adamantite + runite;
    }

    public SackType getSackType() {
        return type;
    }

    public void setSackType(SackType type) {
        this.type = type;
    }

    public int getEmptySlots() {
        return getSackType().getSize() - getAmountInSack();
    }

    public boolean isSackFull() {
        return getAmountInSack() >= getSackType().getSize();
    }

    public boolean isSackEmpty() {
        return getAmountInSack() == 0;
    }
}