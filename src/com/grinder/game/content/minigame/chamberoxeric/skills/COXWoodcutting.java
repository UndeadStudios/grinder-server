package com.grinder.game.content.minigame.chamberoxeric.skills;

import com.grinder.game.content.skill.skillable.impl.woodcutting.AxeType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.Skill;
import com.grinder.net.packet.interaction.PacketInteraction;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class COXWoodcutting extends PacketInteraction {

    public static AxeType getAxe(Player p) {
        for (AxeType a : AxeType.values()) {
            if ((p.getInventory().contains(a.getId()) || p.getEquipment().contains(a.getId())) &&
                    p.getSkillManager().getCurrentLevel(Skill.WOODCUTTING) >= a.getRequiredLevel()) {
                return a;
            }
        }
        return null;
    }
}
