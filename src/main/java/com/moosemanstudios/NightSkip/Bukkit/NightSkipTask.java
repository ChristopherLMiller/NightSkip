package com.moosemanstudios.NightSkip.Bukkit;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class NightSkipTask extends BukkitRunnable {
	private final World world;
	private final Long time;
	private NightSkip plugin;
	
	public NightSkipTask(NightSkip plugin, World world, Long time) {
		this.plugin = plugin;
		this.world = world;
		this.time = time;
	}

	@Override
	public void run() {

		// we are adding the amount of time to the current full time
		Long currentFullTime = world.getFullTime();

		// now set the time
		world.setFullTime(currentFullTime + time);
		plugin.tasks.remove(world.getName());
			
		for (Player player: world.getPlayers()) {
			player.sendMessage(ChatColor.YELLOW + "Time has been skipped ahead!");
		}
	}
}
