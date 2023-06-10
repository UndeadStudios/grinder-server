package com.grinder.game.content.minigame.chamberoxeric.room.vasanistirio;

import com.grinder.game.World;
import com.grinder.game.content.minigame.chamberoxeric.room.COXRoom;
import com.grinder.game.entity.agent.combat.hit.damage.Damage;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.object.GameObject;
import com.grinder.game.model.Position;

/**
 * @author Dexter Morgan <https://www.rune-server.ee/members/102745-dexter-morgan/>
 */
public class VasaNistirioCOXRoom extends COXRoom {

    private static final Position SPAWN = new Position(3278, 5293);

    private static final Position CROSS_FIRE = new Position(3280, 5283);

    private Player p;

    public VasaNistirioCOXRoom(Player p) {
        this.p = p;
        init();
    }

    public VasaNistirioCOXRoom() {

    }

    @Override
    public void init() {

        int height = p.getPosition().getZ();

        Position position = SPAWN.clone().transform(0, 0, height);

        VasaNistirioNPC vasa = new VasaNistirioNPC(position);

        World.getNpcAddQueue().add(vasa);

        p.instance.addAgent(vasa);
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int type) {
        switch (object.getId()) {
            case 30019:
                if(player.getPosition().getY() == 5280) {
                    player.moveTo(CROSS_FIRE);
                    player.getCombat().queue(Damage.create(20));
                    player.getPacketSender().sendMessage("You cross the burning flames and suffer a bit!");
                } else {
                    player.getPacketSender().sendMessage("I don't think it's wise to cross this fire..");
                }
                return true;
        }
        return false;
    }

}
