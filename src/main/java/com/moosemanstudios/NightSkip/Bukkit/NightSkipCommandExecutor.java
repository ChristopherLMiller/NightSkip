package com.moosemanstudios.NightSkip.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class NightSkipCommandExecutor implements CommandExecutor {
	private NightSkip plugin;
	private static List<String> countdownStarted = new ArrayList<String>();
	BukkitTask task;
	
	public NightSkipCommandExecutor(NightSkip plugin) {
		this.plugin = plugin;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		// handle skip
		if (cmd.getName().equalsIgnoreCase("skip")) {
			if (sender instanceof Player) {
				if (sender.hasPermission("nightskip.skip")) {
					// get some variables
					Player player = (Player) sender;
					World world = player.getWorld();
					Long time = world.getTime();
					Environment environment = world.getEnvironment();
					
					// firstly check the world type, if its nether or end move on
					if (environment.equals(Environment.NETHER) || environment.equals(Environment.THE_END)) {
						sender.sendMessage(ChatColor.RED + "Time doesn't exist here... its always dark");
					} else {
						
						// see if the time is between the configured times
						if ((time >= plugin.nightStart) && (time <= plugin.nightEnd)) {
							
							// see if the countdown has already been started on this world
							if (countdownStarted.contains(world.getName())) {
								player.sendMessage(ChatColor.RED + "Countdown has already been initiated on this world");
							} else {
								
								// at this point we can alert the rest of the players on this world that the countdown has been intiated
								List<Player> players = world.getPlayers();
								for(Player worldPlayer : players) {
									worldPlayer.sendMessage(ChatColor.YELLOW + player.getName() + " has requested to skip the night.");
									worldPlayer.sendMessage(ChatColor.YELLOW + "Issue the command " + ChatColor.WHITE + "/noskip" + ChatColor.YELLOW + " within " + Integer.toString(plugin.delay/20) + " seconds to keep the night");
								}
								
								// update the list to include this world now
								countdownStarted.add(world.getName());
								
								// we are ready to schedule the task at this point.
								task = new NightSkipTask(world, (long)plugin.timeToSkipTo).runTaskLater(plugin, (long)plugin.delay);
							}
						} else {
							sender.sendMessage(ChatColor.RED + "Current Time: " + world.getTime() + " - Time must be between " + plugin.nightStart + "-" + plugin.nightEnd + " to skip the night");
						}
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Missing required permission node: " + ChatColor.WHITE + "nightskip.skip");
				}
			} else {
				sender.sendMessage("Command can only be issued by player");
			}
			return true;
			
		}
		
		// cancel the skip
		if (cmd.getName().equalsIgnoreCase("noskip") || cmd.getName().equalsIgnoreCase("nskp")) {
			if (sender instanceof Player) {
				if (sender.hasPermission("nightskip.skip")) {
					Player player = (Player) sender;
					World world = player.getWorld();
					
					// see if the list contains the world name, if so then we can cancel it
					if (countdownStarted.contains(world.getName())) {
						
						// we do, so go ahead and alert the rest of the players its being cancelled
						List<Player> players = ((Player) sender).getWorld().getPlayers();
						for (Player playerWorld : players) {
							playerWorld.sendMessage(ChatColor.YELLOW + sender.getName() + " needs the night, sorry we can't skip");
						}
						
						countdownStarted.remove(((Player) sender).getWorld().getName());
						if (task != null) {
							task.cancel();
						}
					
					} else {
						sender.sendMessage(ChatColor.RED + "Night skipping hasn't been started");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Missing required permission node: " + ChatColor.WHITE + "nightskip.skip");
				}
			} else {
				sender.sendMessage("Command can only be issued by player");
			}
			
			return true;
		}
		
		// handle the rest of the commands
		if (cmd.getName().equalsIgnoreCase("nightskip") || cmd.getName().equalsIgnoreCase("ntskp")) {
			// general commands related to the plugin
			
			if (args.length == 0) {
				sender.sendMessage("/nightskip help" + ChatColor.RED + ": Display this help screen");
				sender.sendMessage("/nightskip version" + ChatColor.RED + ": Display version of the plugin");
				if (sender.hasPermission("nightskip.admin")) {
					sender.sendMessage("/nightskip reload" + ChatColor.RED + ": Reload the config file");
					sender.sendMessage("/nightskip view" + ChatColor.RED + ": View current values");
					sender.sendMessage("/nightskip delay <time>" + ChatColor.RED + ": Change the delay before night skips - in ticks");
					sender.sendMessage("/nightskip skipto <time>" + ChatColor.RED + ": Change the time to skip to (0-24000");
					sender.sendMessage("/nightskip start <time>" + ChatColor.RED + ": Change night start time (0-24000");
					sender.sendMessage("/nightskip end <time>" + ChatColor.RED + ": Change night end time (0-24000");
				}
				
				if (sender.hasPermission("nightskip.skip")) {
					sender.sendMessage("/skip" + ChatColor.RED + ": Skip the night, starts the delay");
					sender.sendMessage("/noskip" + ChatColor.RED + ": Cancels countdown, player required night");
				}
			} else {
				if (args[0].equalsIgnoreCase("help")) {
					sender.sendMessage("/nightskip help" + ChatColor.RED + ": Display this help screen");
					sender.sendMessage("/nightskip version" + ChatColor.RED + ": Display version of the plugin");
					if (sender.hasPermission("nightskip.admin")) {
						sender.sendMessage("/nightskip reload" + ChatColor.RED + ": Reload the config file");
						sender.sendMessage("/nightskip view" + ChatColor.RED + ": View current values");
						sender.sendMessage("/nightskip delay <time>" + ChatColor.RED + ": Change the delay before night skips - in ticks");
						sender.sendMessage("/nightskip skipto <time>" + ChatColor.RED + ": Change the time to skip to (0-24000");
						sender.sendMessage("/nightskip start <time>" + ChatColor.RED + ": Change night start time (0-24000");
						sender.sendMessage("/nightskip end <time>" + ChatColor.RED + ": Change night end time (0-24000");
					}
					
					if (sender.hasPermission("nightskip.skip")) {
						sender.sendMessage("/skip" + ChatColor.RED + ": Skip the night, starts the delay");
						sender.sendMessage("/noskip" + ChatColor.RED + ": Cancels countdown, player required night");
					}
				} else if (args[0].equalsIgnoreCase("version")) {
					sender.sendMessage(ChatColor.GOLD + plugin.pdfFile.getName() + " Version: " + ChatColor.WHITE + plugin.pdfFile.getVersion() + ChatColor.GOLD + " Author: moose517");
				} else if (args[0].equalsIgnoreCase("reload")) {
					if (sender.hasPermission("nightskip.admin")) {
						plugin.loadConfig();
						sender.sendMessage("Nightskip Config file reloaded");
					} else {
						sender.sendMessage(ChatColor.RED + "Missing required permission node: " + ChatColor.WHITE + "nightskip.admin");
					}
				} else if (args[0].equalsIgnoreCase("delay")) {
					if (sender.hasPermission("nightskip.admin")) {
						if (args.length == 2) {
							plugin.setConfigValue("delay", Integer.parseInt(args[1]));
							plugin.delay = plugin.getConfig().getInt("delay");
							sender.sendMessage("Delay set to: " + plugin.delay + "ticks");
						} else if (args.length < 2) {
							sender.sendMessage(ChatColor.RED + "Must specify a delay in ticks");
						} else {
							sender.sendMessage(ChatColor.RED + "Too many arguments, see help for more info");
						}
							
					} else {
						sender.sendMessage(ChatColor.RED + "Missing required permission node: " + ChatColor.WHITE + "nightskip.admin");
					}
				} else if (args[0].equalsIgnoreCase("skipto")) {
					if (sender.hasPermission("nightskip.admin")) {
						if (args.length == 2) {
							int time = Integer.parseInt(args[1]);
							
							if ((time >= 0) && (time <= 2400)) {
								plugin.setConfigValue("time-to-skip-to", time);
								plugin.timeToSkipTo = plugin.getConfig().getInt("time-to-skip-to");
								sender.sendMessage("Skip to time: " + plugin.timeToSkipTo + "ticks");					
							} else {
								sender.sendMessage(ChatColor.RED + "Invalid value, must be in the range of 0-24000");
							}
						} else if (args.length < 2) {
							sender.sendMessage(ChatColor.RED + "Must specify time to skip to");
						} else {
							sender.sendMessage(ChatColor.RED + "Too many arguments, see help for more info");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Missing required permission node: " + ChatColor.WHITE + "nightskip.admin");
					}
				} else if (args[0].equalsIgnoreCase("start")) {
					if (sender.hasPermission("nightskip.admin")) {
						if (args.length == 2) {
							int time = Integer.parseInt(args[1]);
							
							if ((time >= 0) && (time <= 24000)) {
								plugin.setConfigValue("night-start", time);
								plugin.nightStart = plugin.getConfig().getInt("night-start");
								sender.sendMessage("Night start: " + plugin.nightStart + "ticks");
							} else {
								sender.sendMessage(ChatColor.RED + "Invalid value, must be in the range of 0-24000");
							}
						} else if (args.length < 2) {
							sender.sendMessage(ChatColor.RED + "Must specify time for night start");
						} else {
							sender.sendMessage(ChatColor.RED + "Too many arguments, see help for more info");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Missing required permisson node: " + ChatColor.WHITE + "nightskip.admin");
					}
				} else if (args[0].equalsIgnoreCase("end")) {
					if (sender.hasPermission("nightskip.admin")) {
						if (args.length == 2) {
							int time = Integer.parseInt(args[1]);
							
							if ((time >= 0) && (time <= 24000)) {
								plugin.setConfigValue("night-end", time);
								plugin.nightEnd = plugin.getConfig().getInt("night-end");
								sender.sendMessage("Night end: " + plugin.nightEnd + "ticks");
							} else {
								sender.sendMessage(ChatColor.RED + "Invalid value, must be in the range of 0-24000");
							}
						} else if (args.length < 2) {
							sender.sendMessage(ChatColor.RED + "Must specify time for night end");
						} else {
							sender.sendMessage(ChatColor.RED + "Too many arguments, see help for more info");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Missing required permisson node: " + ChatColor.WHITE + "nightskip.admin");
					}
				} else if (args[0].equalsIgnoreCase("view")) {
					if (sender.hasPermission("nightskip.admin")) {
						sender.sendMessage(ChatColor.GOLD + "NightSkip" + ChatColor.WHITE + " Current values: ");
						sender.sendMessage(ChatColor.AQUA + "Delay (ticks): " + ChatColor.WHITE + plugin.delay);
						sender.sendMessage(ChatColor.AQUA + "Skip to (ticks): " + ChatColor.WHITE + plugin.timeToSkipTo);
						sender.sendMessage(ChatColor.AQUA + "Night start: " + ChatColor.WHITE + plugin.nightStart);
						sender.sendMessage(ChatColor.AQUA + "Night end: " + ChatColor.WHITE + plugin.nightEnd);
					}
				}
			}
			return true;
		}
		return false;
	}
}
