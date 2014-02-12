package com.moosemanstudios.NightSkip.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.gravitydevelopment.updater.Updater;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

public class NightSkip extends JavaPlugin
{
	// global variables to the plugin
	Logger log = Bukkit.getLogger();
	PluginDescriptionFile pdfFile;
	String prefix = "[NightSkip] ";
	int delay, timeToSkipTo;
	Boolean debug;
	int nightStart, nightEnd;
	
	public boolean updaterEnabled, updaterAuto, updaterNotify;
	
	@Override 
	public void onEnable() {
		pdfFile = this.getDescription();
		
		// create and load the config
		loadConfig();
		
		// register the command executor
		CommandExecutor executor = new NightSkipCommandExecutor(this);
		getCommand("skip").setExecutor(executor);
		getCommand("noskip").setExecutor(executor);
		getCommand("nightskip").setExecutor(executor);
		
		// enable metrics
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Updater - Note, the values updaterEnabled, updaterAuto, updaterNotify are loaded from config file
		if (updaterEnabled) {
			if (updaterAuto) {
				Updater updater = new Updater(this, 64667 , this.getFile(), Updater.UpdateType.DEFAULT, true);
				if (updater.getResult() == Updater.UpdateResult.SUCCESS)
					log.info(prefix + "update downloaded successfully, restart server to apply update");
				}
			if (updaterNotify) {
					log.info(prefix + "Notifying admins as they login if update found");
						this.getServer().getPluginManager().registerEvents(new UpdaterPlayerListener(this), this);
					}
				}
		
		log.info(prefix + " has been enabled");
	}
	
	@Override 
	public void onDisable() {
		log.info(prefix + "has been disabled");
	}
	
	public void loadConfig() {
		
		// misc
		if (!getConfig().contains("debug")) getConfig().set("debug", true);
		
		// time settings
		if (!getConfig().contains("delay")) getConfig().set("delay", 300);
		if (!getConfig().contains("time-to-skip-to")) getConfig().set("time-to-skip-to", 22812);
		if (!getConfig().contains("night-start")) getConfig().set("night-start", 13187);
		if (!getConfig().contains("night-end")) getConfig().set("night-end", 22812);
		
		// updater
		if (!getConfig().contains("updater.enabled")) getConfig().set("updater.enabled", true);
		if (!getConfig().contains("updater.auto")) getConfig().set("updater.auto", true);
		if (!getConfig().contains("updater.notify")) getConfig().set("updater.notify", true);
		
		saveConfig();
		
		debug = getConfig().getBoolean("debug");
		if (debug)
			log.info(prefix + " debugging enabled");
		
		delay = getConfig().getInt("delay");
		timeToSkipTo = getConfig().getInt("time-to-skip-to");
		nightStart = getConfig().getInt("night-start");
		nightEnd = getConfig().getInt("night-end");
		if (debug) {
			log.info(prefix + "Delay: " + delay);
			log.info(prefix + "Time-to-skip-to: " + timeToSkipTo);
			log.info(prefix + "Night start: " + nightStart);
			log.info(prefix + "Night end: " + nightEnd);
		}
		
		updaterEnabled = getConfig().getBoolean("updater.enabled");
		updaterAuto = getConfig().getBoolean("updater.auto");
		updaterNotify = getConfig().getBoolean("updater.notify");
		if (debug) {
			if (updaterEnabled)
				log.info(prefix + "Updater enabled");
			if (updaterAuto)
				log.info(prefix + "Auto updating enabled");
			if (updaterNotify)
				log.info(prefix + "Notifying admins on update");
		}
	}
	
	public void setConfigValue(String key, Object value) {
		getConfig().set(key, value);
		saveConfig();
	}
	
	public File getFileFolder() {
		return this.getFile();
	}
}