package com.grinder.game.model.commands.impl;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import com.grinder.game.World;
import com.grinder.game.entity.agent.npc.NPC;
import com.grinder.game.entity.agent.player.Player;
import com.grinder.game.model.commands.Command;
import com.grinder.game.entity.agent.player.PlayerRights;
import com.grinder.game.task.Task;
import com.grinder.game.task.TaskManager;

public class StatsCommand implements Command {

	@Override
	public String getSyntax() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Displays current server stats.";
	}
	
	public static final String SERVER_STATS_ATTR = "server_stats";
	
	private NumberFormat nf = new DecimalFormat("###,##0.0");
	
    @Override
    public void execute(Player player, String command, String[] parts) {

    	final Object object = new Object();
    	
    	player.getPacketSender().sendInterface(8134);
    	player.getPacketSender().sendString(8144, "@gre@Server - Stats");

    	player.subscribe(event ->  {
    		TaskManager.cancelTasks(object);
    		return true;
		});

    	TaskManager.submit(new Task(2, object, false) {

			@Override
			protected void execute() {
				if (!player.isActive()){
					stop();
					return;
				}

		    	player.getPacketSender().sendString(8145, "Mem: " + nf.format(getUsedMemory()) + "MB" + "/" + nf.format((double) Runtime.getRuntime().totalMemory() / (double) (1024 * 1024)) + "MB");
		        player.getPacketSender().sendString(8147, "Thread count - " + Thread.activeCount());
				player.getPacketSender().sendString(8148, "Task count - " + TaskManager.getTaskAmount());
				//player.getPacketSender().sendString(8149, "Objects - " + World.getObjects().size());
				player.getPacketSender().sendString(8150, "Npcs - " + World.getNpcs().size());
				player.getPacketSender().sendString(8151, "Players - " + World.getPlayers().size());
				player.getPacketSender().sendString(8152, "Ground items - " + World.getGroundItems().size());
			}
    		
    	});
    	
    	player.getPacketSender().clearInterfaceText(8153, 8200);

    	//printNpcCount(player);
		System.out.println("Pending tasks: count: "+  TaskManager.pendingTasks.size() + " info:" + TaskManager.printPendingTasks());
		System.out.println("Active tasks: count: "+  TaskManager.activeTasks.size() + " info:" + TaskManager.printActiveTasks());
    }

	private static void printNpcCount(Player player) {
    	Map<Integer, Integer> map = new HashMap<>();

    	for (NPC n : World.getNpcs()) {
    		if (n == null) continue;

    		int oldAmount = map.getOrDefault(n.npcId(), 0);

    		int newAmount = oldAmount + 1;

    		map.put(n.npcId(), newAmount);
		}

    	for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
    		int npcId = entry.getKey();
    		int count = entry.getValue();

    		if (count > 100) {
    			player.sendMessage("NPC[" + Integer.toString(npcId) + "] count:" + Integer.toString(count));
			}
			System.out.println("NPC[" + Integer.toString(npcId) + "] count:" + Integer.toString(count));
		}
	}
    
	public double getUsedMemory() {
		long total = Runtime.getRuntime().totalMemory();

		long free = Runtime.getRuntime().freeMemory();

		return (double) (total - free) / (double) (1024 * 1024);
	}
    	

    @Override
    public boolean canUse(Player player) {
    	return player.getUsername().equals("3lou 55") || player.getRights().equals(PlayerRights.DEVELOPER);
    }

}
