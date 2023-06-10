package com.grinder.game.model.commands.impl;

import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.entity.object.ObjectManager;
import com.grinder.game.entity.object.ObjectType;
import com.grinder.game.entity.object.StaticGameObjectFactory;
import com.grinder.game.model.Direction;
import com.grinder.game.model.commands.Command;

public class SpawnObjectCommand implements Command {

    @Override
    public String getSyntax() {
        return "[id] [face] [type]";
    }

    @Override
    public String getDescription() {
        return "Adds object spawn to data dump.";
    }

    @Override
    public void execute(Player player, String command, String[] parts) {

        int id = Integer.parseInt(parts[1]);


        Direction direction = Direction.EAST;
        if (parts.length > 2) {
            direction = Direction.valueOf(parts[2].toUpperCase());
        }

        ObjectType objectType = ObjectType.INTERACTABLE;
        if (parts.length > 3) {
            objectType = ObjectType.Companion.forId(Integer.parseInt(parts[3]));
        }
        int x = player.getPosition().getX();
        int y = player.getPosition().getY();
        int z = player.getPosition().getZ();
        final int face = direction.getId();
        final int type = objectType.getValue();

        String line = System.getProperty("line.separator");

        String data = "" + line + "\t{" + line + "";
        data += "\t\t\"face\": " + face + "," + line + "";
        data += "\t\t\"type\": " + type + "," + line + "";
        data += "\t\t\"id\": " + id + "," + line + "";
        data += "\t\t\"position\": {" + line + "";
        data += "\t\t\t\"x\": " + x + "," + line + "";
        data += "\t\t\t\"y\": " + y + "," + line + "";
        data += "\t\t\t\"z\": " + z + "" + line + "";
        data += "\t\t}" + line + "";
        data += "\t}," + line + "";

        ObjectManager.add(StaticGameObjectFactory.produce(id, player.getPosition(), type, face), true);
//		try {
//			Files.write(Paths.get("./data/object_spawns.cfg"), data.getBytes(), StandardOpenOption.APPEND);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
    }

    @Override
    public boolean canUse(Player player) {
        PlayerRights rights = player.getRights();
        return (rights == PlayerRights.OWNER || rights == PlayerRights.DEVELOPER || rights == PlayerRights.CO_OWNER);
    }

}
