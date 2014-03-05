package com.moosemanstudios.NightSkip.Bukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

public class BedEnterEvent implements Listener {
	NightSkip plugin;
	
	public BedEnterEvent(NightSkip plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBedEnter(PlayerBedEnterEvent event) {
		// see if there is more than just the one person online, if there isn't we can just let sleeping occur naturally.
		if ((Bukkit.getServer().getOnlinePlayers().length > 1) && (event.getPlayer().hasPermission("nightskip.skip")) && plugin.skipBedEnter) {
			Bukkit.getServer().dispatchCommand((CommandSender) event.getPlayer(), "skip");
			event.getPlayer().sendMessage(ChatColor.YELLOW + "You've requested the jump to day");
			event.setCancelled(true);
		}
	}
}
