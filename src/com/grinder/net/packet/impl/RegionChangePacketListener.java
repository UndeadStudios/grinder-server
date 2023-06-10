package com.grinder.net.packet.impl;

import com.grinder.game.collision.CollisionManager;
import com.grinder.game.content.skill.skillable.impl.hunter.birdhouse.BirdHouseActions;
import com.grinder.game.entity.agent.AgentUtil;
import com.grinder.game.entity.agent.npc.monster.aggression.MonsterAggressionProcess;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.grounditem.ItemOnGroundManager;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.model.Boundaries;
import com.grinder.game.model.areas.AreaManager;
import com.grinder.game.model.attribute.Attribute;
import com.grinder.game.model.sound.Sounds;
import com.grinder.net.packet.PacketListener;
import com.grinder.net.packet.PacketReader;
import com.grinder.util.Misc;


public class RegionChangePacketListener implements PacketListener {

        @Override
        public void handleMessage(Player player, PacketReader packetReader, int packetOpcode) {
            //if (player.isAllowRegionChangePacket()) {
            CollisionManager.tryLazyLoadRegionAt(player.getPosition().getX(), player.getPosition().getY());

            if (AreaManager.inside(player.getPosition(), Boundaries.HOME_AREAS) && Misc.getRandomInclusive(3) == 1) {
                player.getPacketSender().sendSound(Misc.randomInt(Sounds.RANDOM_SOUND_WHILE_WALKING));
            }

            if (AgentUtil.getRegionID(player) != player.getAttributes().numInt(Attribute.LAST_REGION_ID)) {
                player.getAttributes().numAttr(Attribute.LAST_REGION_ID, 0).setValue(AgentUtil.getRegionID(player));
                player.getMusic().playAreaRandomMusic(player);
            }
            player.getAggressionTolerance().start(MonsterAggressionProcess.Companion.getSecondsUntilTolerant()); //Every 4 minutes, reset aggression for npcs in the region.
            // player.setAllowRegionChangePacket(false);
            BirdHouseActions.refreshRegional(player);
            player.updateEquippedItemColors(); // So it updates colorful items when they change colors
            player.updateAppearance();
        }
}
