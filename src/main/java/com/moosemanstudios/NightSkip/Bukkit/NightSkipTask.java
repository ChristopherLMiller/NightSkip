package com.moosemanstudios.NightSkip.Bukkit;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class NightSkipTask extends BukkitRunnable {
	private final World world;
	private final Long time;
	
	public NightSkipTask(World world, Long time) {
		this.world = world;
		this.time = time;
	}

	@Override
	public void run() {	
		world.setTime(time);
	}

}
