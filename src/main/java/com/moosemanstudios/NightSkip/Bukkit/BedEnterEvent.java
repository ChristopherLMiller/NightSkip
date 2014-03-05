package com.moosemanstudios.NightSkip.Bukkit;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

public class BedEnterEvent implements Listener {
	NightSkip plugin;
	
	public BedEnterEvent(NightSkip plugin) {
		this.plugin = plugin;
	}

	public void onBedEnter(PlayerBedEnterEvent event) {
		Bukkit.getServer().dispatchCommand(event.getPlayer(), "skip");
	}
}
