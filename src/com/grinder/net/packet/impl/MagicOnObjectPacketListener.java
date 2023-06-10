package com.grinder.net.packet.impl;

import com.grinder.Server;
import com.grinder.game.World;
import com.grinder.game.content.item.MorphItems;
import com.grinder.game.content.skill.SkillUtil;
import com.grinder.game.content.skill.skillable.impl.magic.ChargeOrbSpellCasting;
import com.grinder.game.content.skill.skillable.impl.magic.InteractiveSpell;
import com.grinder.game.content.skill.skillable.impl.magic.Spell;
import com.grinder.game.entity.EntityExtKt;
import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpell;
import com.grinder.game.entity.agent.combat.attack.weapon.magic.CombatSpellType;
import com.grinder.game.entity.agent.movement.task.WalkToAction;
import com.grinder.game.entity.agent.movement.teleportation.TeleportType;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerStatus;
import com.grinder.game.entity.agent.player.PlayerUtil;
import com.grinder.game.entity.agent.player.bot.BotPlayer;
import com.grinder.game.entity.agent.player.equipment.EquipmentConstants;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Animation;
import com.grinder.game.model.Graphic;
import com.grinder.game.model.Position;
import com.grinder.game.model.Skill;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.task.TaskManager;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;
import com.grinder.util.Misc;
import com.grinder.util.time.TimeUtil;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * TODO: remove all duplicate code junk
 */
public class MagicOnObjectPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {
        int objectIndex = packetReader.readLEShort() & 0xFFFF;
        int objectX = packetReader.readInt();
        int objectY = packetReader.readInt();
        int spellId = packetReader.readLEShort() & 0xFFFF;

        //System.out.println("Object ID: " + objectIndex + " - object X: " + objectX + " - object Y: " + objectY + " - spell ID: " + spellId);

        //Obelisk
        if (objectIndex == 55092)
        {
            Position pos = new Position(player.getPosition().getBaseLocalX() + objectX, player.getPosition().getBaseLocalY() + objectY, player.getPosition().getZ());
            System.out.println(pos);
            final Optional<GameObject> object = World.findObject(player, 2151, pos);
            if (object.isEmpty()) {
                Server.getLogger().info("Object with id " + 2151 + " does not exist!");
                return;
            }

            ChargeOrbSpellCasting.enchant(player, spellId);
        }
    }
}
