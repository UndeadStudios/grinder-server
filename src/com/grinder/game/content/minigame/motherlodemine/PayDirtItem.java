package com.grinder.game.content.minigame.motherlodemine;

import com.grinder.game.content.minigame.motherlodemine.vien.Vien;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Skill;
import com.grinder.game.model.item.Item;
import com.grinder.util.ItemID;

/**
 * @author L E G E N D
 * @date 2/13/2021
 * @time 5:41 AM
 * @discord L E G E N D#4380
 */
public final class PayDirtItem extends Item {

    private Vien vien;

    public PayDirtItem(int id) {
        super(id);
    }

    public PayDirtItem(Player player) {
        super(ItemID.PAY_DIRT);
        roll(player);
    }

    public void roll(Player player) {
        this.vien = Vien.roll(player.getSkillManager().getCurrentLevel(Skill.MINING));
    }

    public Vien getVien() {
        return vien;
    }
}
