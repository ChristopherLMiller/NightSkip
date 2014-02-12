package com.moosemanstudios.NightSkip.Bukkit;

import net.gravitydevelopment.updater.Updater;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdaterPlayerListener implements Listener {

	private NightSkip plugin;

	UpdaterPlayerListener(NightSkip plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		// Note: this listener is never registered if updaterEnabled and updaterNotify are false
		Player player = event.getPlayer();

		if (player.hasPermission("nightskip.admin")) {
			Updater updater = new Updater(plugin, 64667, plugin.getFileFolder(), Updater.UpdateType.NO_DOWNLOAD, false);

			if (updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE && plugin.updaterNotify && plugin.updaterEnabled) {
				player.sendMessage(ChatColor.AQUA + "An update is avaiable for NightSkip: " + updater.getLatestName());
				player.sendMessage(ChatColor.AQUA + "Type " + ChatColor.WHITE + "/nightskip update" + ChatColor.AQUA + " to update");
			}
		}
	}
}