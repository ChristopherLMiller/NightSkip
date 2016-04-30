package com.moosemanstudios.NightSkip.Bukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
		if ((getPlayersOnline() > 1) && (event.getPlayer().hasPermission("nightskip.skip")) && plugin.skipBedEnter) {
			Bukkit.getServer().dispatchCommand((CommandSender) event.getPlayer(), "skip");
			event.getPlayer().sendMessage(ChatColor.YELLOW + "You've requested the jump to day");
			event.setCancelled(true);

			if (event.getPlayer().getBedSpawnLocation() == null) {
				event.getPlayer().setBedSpawnLocation(event.getBed().getLocation());
				event.getPlayer().sendMessage("Bed spawn location set");
			}
		}
	}

	private int getPlayersOnline() {
		int playersOnline = 0;
		try {
			if (Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).getReturnType() == Collection.class)
				playersOnline = ((Collection<?>)Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).invoke(null, new Object[0])).size();
			else
				playersOnline = ((Player[])Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).invoke(null, new Object[0])).length;
		}
		catch (NoSuchMethodException ex){} // can never happen
		catch (InvocationTargetException ex){} // can also never happen
		catch (IllegalAccessException ex){} // can still never happen

        return playersOnline;
	}


}
