package com.moosemanstudios.NightSkip;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NightSkipCommandExecutor implements CommandExecutor {
	private NightSkip plugin;
	private static Boolean countdownStarted = false;
	
	public NightSkipCommandExecutor(NightSkip plugin) {
		this.plugin = plugin;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// handle skip
		if (cmd.getName().equalsIgnoreCase("skip")) {
			if (sender instanceof Player) {
				if (sender.hasPermission("nightskip.skip")) {
					// make sure we are actually in the "night" time
					// first get the world the player is on
					Player player = (Player) sender;
					World world = player.getWorld();
					Long time = world.getTime();
					
					if ((time >= plugin.nightStart) && (time <= plugin.nightEnd)) {
						// at this point verify countdown hasn't started already
						if (!countdownStarted) {
							// alert all players on the current world that time is about to be skipped
							List<Player> players = world.getPlayers();
							
							for (Player playerA : players) {
								playerA.sendMessage(ChatColor.YELLOW + player.getName() + " has requested to skip the night.");
								playerA.sendMessage(ChatColor.YELLOW + "Issue the command " + ChatColor.RED + "/noskip" + ChatColor.YELLOW + " within " + Integer.toString(plugin.delay/20) + " seconds to keep the night");
							}
							
							// at this point we can schedule the task!
							countdownStarted = true;
							// TODO: schedule the task
						} else {
							player.sendMessage(ChatColor.RED + "Countdown has already been started");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Its not night! Current time is: " + Long.toString(time) + " Night time is between: " + plugin.nightStart + "-" + plugin.nightEnd);
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Missing required permission node: " + ChatColor.WHITE + "nightskip.skip");
				}
			} else {
				sender.sendMessage("Command can only be issued by player");
			}
			return true;
			
		}
		
		// cancell the skip
		if (cmd.getName().equalsIgnoreCase("noskip") || cmd.getName().equalsIgnoreCase("nskp")) {
			if (sender instanceof Player) {
				if (sender.hasPermission("nightskip.skip")) {
					// see if the countdown has even begun yet
					if (countdownStarted) {
						countdownStarted = false;
						
						// alert all players on the world it was cancelled
						Player player = (Player) sender;
						World world = player.getWorld();
						List<Player> players = world.getPlayers();
						for (Player playerA: players) {
							playerA.sendMessage(ChatColor.YELLOW + player.getName() + " has cancelled the vote to skip night");
						}
						// TODO: cancel the countdown
						
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
				sender.sendMessage(ChatColor.RED + "Type " + ChatColor.WHITE + "/nightskip help " + ChatColor.RED + " for help");
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
					sender.sendMessage(ChatColor.GOLD + plugin.pdfFile.getName() + " Version: " + ChatColor.WHITE + plugin.pdfFile.getVersion());
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
