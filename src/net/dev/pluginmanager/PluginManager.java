package net.dev.pluginmanager;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import net.dev.pluginmanager.commands.PluginManagerCommand;
import net.dev.pluginmanager.listeners.InventoryClickListener;
import net.dev.pluginmanager.listeners.InventoryCloseListener;
import net.dev.pluginmanager.utilities.SetupFileManager;
import net.dev.pluginmanager.utilities.Utilities;

public class PluginManager extends JavaPlugin {

	private static PluginManager instance;
	
	public static PluginManager getInstance() {
		return instance;
	}
	
	private Utilities utilities;
	private SetupFileManager setupFileManager;
	
	@Override
	public void onEnable() {
		instance = this;
		
		utilities = new Utilities();
		setupFileManager = new SetupFileManager();
		
		getCommand("pluginmanager").setTabCompleter(new TabCompleter() {
			
			@Override
			public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
				List<String> tabCompletions = new ArrayList<>();

				if(cmd.getName().equalsIgnoreCase("pluginmanager")) {
					if(sender.hasPermission("pluginmanager.use")) {
						if(args.length <= 1) {
							tabCompletions.add("help");
							
							if(sender.hasPermission("pluginmanager.enable") || sender.hasPermission("pluginmanager.enable.all"))
								tabCompletions.add("enable");

							if(sender.hasPermission("pluginmanager.disable") || sender.hasPermission("pluginmanager.disable.all"))
								tabCompletions.add("disable");
							
							if(sender.hasPermission("pluginmanager.restart") || sender.hasPermission("pluginmanager.restart.all"))
								tabCompletions.add("restart");
							
							if(sender.hasPermission("pluginmanager.load") || sender.hasPermission("pluginmanager.load.all"))
								tabCompletions.add("load");
							
							if(sender.hasPermission("pluginmanager.unload") || sender.hasPermission("pluginmanager.unload.all"))
								tabCompletions.add("unload");
							
							if(sender.hasPermission("pluginmanager.reload") || sender.hasPermission("pluginmanager.reload.all"))
								tabCompletions.add("reload");
							
							if(sender.hasPermission("pluginmanager.cmdlookup"))
								tabCompletions.add("cmdlookup");
							
							if(sender.hasPermission("pluginmanager.info"))
								tabCompletions.add("info");
							
							if(sender.hasPermission("pluginmanager.deletedirectory"))
								tabCompletions.add("deldir");
							
							if(sender.hasPermission("pluginmanager.delete"))
								tabCompletions.add("del");
							
							if(sender.hasPermission("pluginmanager.commands"))
								tabCompletions.add("commands");
							
							if(sender.hasPermission("pluginmanager.list"))
								tabCompletions.add("list");
						} else if(args.length >= 2) {
							if(args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("disable") || args[0].equalsIgnoreCase("restart") || args[0].equalsIgnoreCase("load") || args[0].equalsIgnoreCase("unload") || args[0].equalsIgnoreCase("reload")) {
								if(sender.hasPermission("pluginmanager." + args[0].toLowerCase() + ".all"))
									tabCompletions.add("all");
								
								for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
									if(!(plugin.getName().equalsIgnoreCase(getDescription().getName())))
										if(sender.hasPermission("pluginmanager." + args[0].toLowerCase()) || sender.hasPermission("pluginmanager." + args[0].toLowerCase() + "." + plugin.getName().toLowerCase()))
											tabCompletions.add(plugin.getName());
								}
							} else if(args[0].equalsIgnoreCase("cmdlookup")) {
								for (Command command : utilities.getCommands().getCommands()) {
									if(sender.hasPermission("pluginmanager.cmdlookup") || sender.hasPermission("pluginmanager.cmdlookup." + command.getName().toLowerCase()))
										tabCompletions.add(command.getName());
								}
							} else if(args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("commands")) {
								for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
									if(sender.hasPermission("pluginmanager." + args[0].toLowerCase()) || sender.hasPermission("pluginmanager." + args[0].toLowerCase() + "." + plugin.getName().toLowerCase()))
										tabCompletions.add(plugin.getName());
								}
							} else if(args[0].equalsIgnoreCase("deldir")) {
								for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
									if(sender.hasPermission("pluginmanager.deletedirectory") || sender.hasPermission("pluginmanager.deletedirectory." + plugin.getName().toLowerCase()))
										tabCompletions.add(plugin.getName());
								}
							} else if(args[0].equalsIgnoreCase("del")) {
								for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
									if(sender.hasPermission("pluginmanager.delete") || sender.hasPermission("pluginmanager.delete." + plugin.getName().toLowerCase()))
										tabCompletions.add(plugin.getName());
								}
							}
						}
						
						tabCompletions.removeIf(tabCompletion -> !(StringUtil.copyPartialMatches(args[args.length - 1], tabCompletions, new ArrayList<String>()).contains(tabCompletion)));
						Collections.sort(tabCompletions);
					}
				}
				
				return tabCompletions;
			}
		});
		
		getCommand("pluginmanager").setExecutor(new PluginManagerCommand());
		
		org.bukkit.plugin.PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new InventoryClickListener(), this);
		pm.registerEvents(new InventoryCloseListener(), this);
		
		utilities.sendConsole("§7The plugin has been §aenabled§7!");
	}

	@Override
	public void onDisable() {
		utilities.sendConsole("§7The plugin has been §cdisabled§7!");
	}
	
	public Utilities getUtils() {
		return utilities;
	}
	
	public SetupFileManager getFileUtils() {
		return setupFileManager;
	}
	
}
