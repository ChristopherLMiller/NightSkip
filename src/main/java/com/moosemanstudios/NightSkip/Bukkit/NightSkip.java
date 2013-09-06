package com.moosemanstudios.NightSkip.Bukkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
	
	@Override 
	public void onEnable() {
		pdfFile = this.getDescription();
		
		// create and load the config
		loadConfig();
		
		// register the command executor
		getCommand("skip").setExecutor(new NightSkipCommandExecutor(this));
		getCommand("noskip").setExecutor(new NightSkipCommandExecutor(this));
		getCommand("nightskip").setExecutor(new NightSkipCommandExecutor(this));
		
		// enable metrics
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		log.info(prefix + " has been enabled");
	}
	
	@Override 
	public void onDisable() {
		log.info(prefix + "has been disabled");
	}
	
	public void loadConfig() {
		FileConfiguration cfg = getConfig();
		cfg.options().copyDefaults(true);
		this.saveConfig();
		
		debug = getConfig().getBoolean("debug");
		delay = getConfig().getInt("delay");
		timeToSkipTo = getConfig().getInt("time-to-skip-to");
		nightStart = getConfig().getInt("night-start");
		nightEnd = getConfig().getInt("night-end");
	}
	
	public void setConfigValue(String key, Object value) {
		getConfig().set(key, value);
		saveConfig();
	}
}
