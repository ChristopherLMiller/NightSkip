package com.moosemanstudios.NightSkip.Bukkit;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bstats.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class NightSkip extends JavaPlugin
{
	// global variables to the plugin
	Logger log = Bukkit.getLogger();
	PluginDescriptionFile pdfFile;
	String prefix = "[NightSkip] ";
	int delay, timeToSkipTo;
	Boolean debug;
	int nightStart, nightEnd;
	public int mobRange;
	public Boolean mobEnabled, skipBedEnter;
	Map<String, BukkitTask> tasks = new HashMap<String, BukkitTask>();

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
		
		// register bed enter events
		this.getServer().getPluginManager().registerEvents(new BedEnterEvent(this), this);

		Metrics metrics = new Metrics(this);
		
		log.info(prefix + "has been enabled");
	}
	
	@Override 
	public void onDisable() {
		log.info(prefix + "has been disabled");
	}
	
	public void loadConfig() {
		
		// misc
		if (getConfig().contains("debug")) getConfig().set("misc.debug", false);
		getConfig().set("debug", null);
		if (!getConfig().contains("misc.debug")) getConfig().set("misc.debug", false);
		if (!getConfig().contains("misc.skip-on-bed-enter")) getConfig().set("misc.skip-on-bed-enter", true);
		
		// time settings
		if (!getConfig().contains("delay")) getConfig().set("delay", 300);
		if (!getConfig().contains("time-to-skip-to")) getConfig().set("time-to-skip-to", 22812);
		if (!getConfig().contains("night-start")) getConfig().set("night-start", 13187);
		if (!getConfig().contains("night-end")) getConfig().set("night-end", 22812);

		// mob range
		if (!getConfig().contains("mob-check.enabled")) getConfig().set("mob-check.enabled", false);
		if (!getConfig().contains("mob-check.range")) getConfig().set("mob-check.range", 20);
		
		saveConfig();
		
		debug = getConfig().getBoolean("misc.debug");
		if (debug)
			log.info(prefix + "debugging enabled");
		
		skipBedEnter = getConfig().getBoolean("misc.skip-on-bed-enter");
		if (debug && skipBedEnter) {
			log.info(prefix + "Skipping on bed enter");
		}
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

		
		mobEnabled = getConfig().getBoolean("mob-check.enabled");
		mobRange = getConfig().getInt("mob-check.range");
		if (debug && mobEnabled) {
			log.info(prefix + "Mob checking enabled - Mob-range: " + mobRange);
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
