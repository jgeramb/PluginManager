package net.dev.pluginmanager.commands;

import java.io.File;
import java.lang.reflect.Field;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;

import net.dev.pluginmanager.PluginManager;
import net.dev.pluginmanager.utilities.SetupFileManager;
import net.dev.pluginmanager.utilities.Utilities;

public class PluginManagerCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		PluginManager pluginManager = PluginManager.getInstance();
		Utilities utilities = pluginManager.getUtils();
		SetupFileManager setupFileManager = pluginManager.getFileUtils();
		
		boolean showHelp = false;
		String prefix = utilities.getPrefix();
		
		if(args.length == 0) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				
				if(sender.hasPermission("pluginmanager.gui")) {
					utilities.getCurrentPages().put(player.getUniqueId(), 0);
					utilities.openInventory(player);
				} else
					sender.sendMessage(utilities.getNoPerm());
			} else
				utilities.sendConsole(utilities.getNotPlayer());
		} else if(args.length >= 2) {
			if(args[0].equalsIgnoreCase("enable")) {
				if(sender.hasPermission("pluginmanager.enable") || sender.hasPermission("pluginmanager.enable." + args[1].toLowerCase())) {
					if(args[1].equalsIgnoreCase("all")) {
						if(sender.hasPermission("pluginmanager.enable.all")) {
							for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
								if(!(plugin.getName().equalsIgnoreCase(pluginManager.getDescription().getName()))) {
									if(!(plugin.isEnabled()))
										Bukkit.getPluginManager().enablePlugin(plugin);
								}
							}
							
							sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.AllPluginsEnabled"));
						} else
							sender.sendMessage(utilities.getNoPerm());
					} else {
						Plugin plugin = Bukkit.getPluginManager().getPlugin(args[1]);
						
						if(plugin != null) {
							if(!(plugin.getName().equalsIgnoreCase(pluginManager.getDescription().getName()))) {
								if(!(plugin.isEnabled()))
									Bukkit.getPluginManager().enablePlugin(plugin);
								
								sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginEnabled").replace("%plugin%", args[1]));
							} else
								sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginCanNotBeToggled").replace("%plugin%", args[1]));
						} else
							sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginNotFound").replace("%plugin%", args[1]));
					}
				} else
					sender.sendMessage(utilities.getNoPerm());
			} else if(args[0].equalsIgnoreCase("disable")) {
				if(sender.hasPermission("pluginmanager.disable") || sender.hasPermission("pluginmanager.disable." + args[1].toLowerCase())) {
					if(args[1].equalsIgnoreCase("all")) {
						if(sender.hasPermission("pluginmanager.disable.all")) {
							for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
								if(!(plugin.getName().equalsIgnoreCase(pluginManager.getDescription().getName()))) {
									if(plugin.isEnabled())
										Bukkit.getPluginManager().disablePlugin(plugin);
								}
							}
							
							sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.AllPluginsDisabled"));
						} else
							sender.sendMessage(utilities.getNoPerm());
					} else {
						Plugin plugin = Bukkit.getPluginManager().getPlugin(args[1]);
						
						if(plugin != null) {
							if(!(plugin.getName().equalsIgnoreCase(pluginManager.getDescription().getName()))) {
								if(plugin.isEnabled())
									Bukkit.getPluginManager().disablePlugin(plugin);
								
								sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginDisabled").replace("%plugin%", args[1]));
							} else
								sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginCanNotBeToggled").replace("%plugin%", args[1]));
						} else
							sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginNotFound").replace("%plugin%", args[1]));
					}
				} else
					sender.sendMessage(utilities.getNoPerm());
			} else if(args[0].equalsIgnoreCase("restart")) {
				if(sender.hasPermission("pluginmanager.restart") || sender.hasPermission("pluginmanager.restart." + args[1].toLowerCase()) || (sender.hasPermission("pluginmanager.enable") && sender.hasPermission("pluginmanager.disable")) || (sender.hasPermission("pluginmanager.enable." + args[1].toLowerCase()) && sender.hasPermission("pluginmanager.disable." + args[1].toLowerCase()))) {
					if(args[1].equalsIgnoreCase("all")) {
						if(sender.hasPermission("pluginmanager.restart.all") || (sender.hasPermission("pluginmanager.enable.all") && sender.hasPermission("pluginmanager.disable.all"))) {
							for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
								if(!(plugin.getName().equalsIgnoreCase(pluginManager.getDescription().getName()))) {
									if(plugin.isEnabled())
										Bukkit.getPluginManager().disablePlugin(plugin);
									
									Bukkit.getPluginManager().enablePlugin(plugin);
								}
							}
						
							sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.AllPluginsRestarted"));
						} else
							sender.sendMessage(utilities.getNoPerm());
					} else {
						Plugin plugin = Bukkit.getPluginManager().getPlugin(args[1]);
						
						if(plugin != null) {
							if(!(plugin.getName().equalsIgnoreCase(pluginManager.getDescription().getName()))) {
								if(plugin.isEnabled())
									Bukkit.getPluginManager().disablePlugin(plugin);
							
								Bukkit.getPluginManager().enablePlugin(plugin);
								
								sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginRestarted").replace("%plugin%", args[1]));
							} else
								sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginCanNotBeToggled").replace("%plugin%", args[1]));
						} else
							sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginNotFound").replace("%plugin%", args[1]));
					}
				} else
					sender.sendMessage(utilities.getNoPerm());
			} else if(args[0].equalsIgnoreCase("load")) {
				if(sender.hasPermission("pluginmanager.load") || sender.hasPermission("pluginmanager.load." + args[1].toLowerCase())) {
					if(args[1].equalsIgnoreCase("all")) {
						if(sender.hasPermission("pluginmanager.load.all")) {
							for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
								if(!(plugin.getName().equalsIgnoreCase(pluginManager.getDescription().getName()))) {
									if(plugin.isEnabled())
										Bukkit.getPluginManager().disablePlugin(plugin);
									
									utilities.unloadPlugin(plugin);
									
									File file = new File("plugins", "PluginManager.jar");
									
									for (File tmpFile : new File("plugins").listFiles()) {
										if(tmpFile.getName().endsWith(".jar")) {
											if(tmpFile.getName().toLowerCase().contains(plugin.getName().toLowerCase()))
												file = new File("plugins", tmpFile.getName());
										}
									}
									
									if(file.exists()) {
										try {
											plugin = Bukkit.getPluginManager().loadPlugin(file);
											
											if(plugin != null) {
												if(plugin.isEnabled())
													Bukkit.getPluginManager().disablePlugin(plugin);
												
												Bukkit.getConsoleSender().sendMessage("[" + plugin.getName() + "] Loading " + plugin.getName() + " v" + plugin.getDescription().getVersion());
												
												plugin.onLoad();
												Bukkit.getPluginManager().enablePlugin(plugin);
											} else
												sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginCanNotBeLoaded").replace("%plugin%", file.getName()));
										} catch (UnknownDependencyException | InvalidPluginException | InvalidDescriptionException ex) {
											ex.printStackTrace();
											
											sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginCanNotBeLoaded").replace("%plugin%", plugin.getName()));
										}
									}
								}
							}
							
							sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.AllPluginsLoaded"));
						} else
							sender.sendMessage(utilities.getNoPerm());
					} else {
						if(!(args[1].equalsIgnoreCase(pluginManager.getDescription().getName()))) {
							File file = new File("plugins", "PluginManager.jar");
							
							for (File tmpFile : new File("plugins").listFiles()) {
								if(tmpFile.getName().endsWith(".jar")) {
									if(tmpFile.getName().toLowerCase().contains(args[1].toLowerCase()))
										file = new File("plugins", tmpFile.getName());
								}
							}
							
							if(file.exists()) {
								if(Bukkit.getPluginManager().getPlugin(args[1]) == null) {
									try {
										Plugin plugin = Bukkit.getPluginManager().loadPlugin(file);
										
										if(plugin != null) {
											Bukkit.getConsoleSender().sendMessage("[" + plugin.getName() + "] Loading " + plugin.getName() + " v" + plugin.getDescription().getVersion());
											
											plugin.onLoad();
											Bukkit.getPluginManager().enablePlugin(plugin);
											
											sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginLoaded").replace("%plugin%", args[1]));
										} else
											sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginNotFound").replace("%plugin%", args[1]));
									} catch (UnknownDependencyException | InvalidPluginException | InvalidDescriptionException ex) {
										ex.printStackTrace();
										
										sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginCanNotBeLoaded").replace("%plugin%", args[1]));
									}
								} else
									sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginAlreadyLoaded").replace("%plugin%", args[1]));
							} else {
								sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginNotFound").replace("%plugin%", args[1]));
								sender.sendMessage(prefix + "§7All files§8:");
								
								for (File content : new File("plugins").listFiles()) {
									if(content.isFile() && content.getName().endsWith(".jar"))
										sender.sendMessage(prefix + "§e" + content.getName());
								}
							}
						} else
							sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginCanNotBeToggled").replace("%plugin%", args[1]));
					}
				} else
					sender.sendMessage(utilities.getNoPerm());
			} else if(args[0].equalsIgnoreCase("unload")) {
				if(sender.hasPermission("pluginmanager.unload") || sender.hasPermission("pluginmanager.unload." + args[1].toLowerCase())) {
					if(args[1].equalsIgnoreCase("all")) {
						if(sender.hasPermission("pluginmanager.unload.all")) {
							for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
								if(!(plugin.getName().equalsIgnoreCase(pluginManager.getDescription().getName()))) {
									if(plugin.isEnabled())
										Bukkit.getPluginManager().disablePlugin(plugin);
									
									utilities.unloadPlugin(plugin);
								}
							}
							
							sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.AllPluginsUnloaded"));
						} else
							sender.sendMessage(utilities.getNoPerm());
					} else {
						Plugin plugin = Bukkit.getPluginManager().getPlugin(args[1]);
							
						if(plugin != null) {
							if(!(plugin.getName().equalsIgnoreCase(pluginManager.getDescription().getName()))) {
								if(plugin.isEnabled())
									Bukkit.getPluginManager().disablePlugin(plugin);
	
								utilities.unloadPlugin(plugin);
								
								sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginUnloaded").replace("%plugin%", args[1]));
							} else
								sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginCanNotBeToggled").replace("%plugin%", args[1]));
						} else
							sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginNotFound").replace("%plugin%", args[1]));
					}
				} else
					sender.sendMessage(utilities.getNoPerm());
			} else if(args[0].equalsIgnoreCase("reload")) {
				if(sender.hasPermission("pluginmanager.reload") || sender.hasPermission("pluginmanager.reload." + args[1].toLowerCase()) || (sender.hasPermission("pluginmanager.load") && sender.hasPermission("pluginmanager.unload")) || (sender.hasPermission("pluginmanager.load." + args[1].toLowerCase()) && sender.hasPermission("pluginmanager.unload." + args[1].toLowerCase()))) {
					if(args[1].equalsIgnoreCase("all")) {
						if(sender.hasPermission("pluginmanager.reload.all") || (sender.hasPermission("pluginmanager.load.all") && sender.hasPermission("pluginmanager.unload.all"))) {
							for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
								if(!(plugin.getName().equalsIgnoreCase(pluginManager.getDescription().getName()))) {
									if(plugin.isEnabled())
										Bukkit.getPluginManager().disablePlugin(plugin);
									
									utilities.unloadPlugin(plugin);
									
									File file = new File("plugins", "PluginManager.jar");
									
									for (File tmpFile : new File("plugins").listFiles()) {
										if(tmpFile.getName().endsWith(".jar")) {
											if(tmpFile.getName().toLowerCase().contains(plugin.getName().toLowerCase()))
												file = new File("plugins", tmpFile.getName());
										}
									}
									
									if(file.exists()) {
										try {
											plugin = Bukkit.getPluginManager().loadPlugin(file);
											
											if(plugin != null) {
												if(plugin.isEnabled())
													Bukkit.getPluginManager().disablePlugin(plugin);
													
												Bukkit.getConsoleSender().sendMessage("[" + plugin.getName() + "] Loading " + plugin.getName() + " v" + plugin.getDescription().getVersion());
												
												plugin.onLoad();
												Bukkit.getPluginManager().enablePlugin(plugin);
											} else
												sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginCanNotBeLoaded").replace("%plugin%", file.getName()));
										} catch (UnknownDependencyException | InvalidPluginException | InvalidDescriptionException ex) {
											ex.printStackTrace();
											
											sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginCanNotBeLoaded").replace("%plugin%", plugin.getName()));
										}
									}
								}
							}
							
							sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.AllPluginsReloaded"));
						} else
							sender.sendMessage(utilities.getNoPerm());
					} else {
						if(!(args[1].equalsIgnoreCase(pluginManager.getDescription().getName()))) {
							File file = new File("plugins", "PluginManager.jar");
							
							for (File tmpFile : new File("plugins").listFiles()) {
								if(tmpFile.getName().endsWith(".jar")) {
									if(tmpFile.getName().toLowerCase().contains(args[1].toLowerCase()))
										file = new File("plugins", tmpFile.getName());
								}
							}
							
							if(file.exists()) {
								Plugin plugin = Bukkit.getPluginManager().getPlugin(args[1]);
								
								if(plugin != null) {
									if(plugin.isEnabled())
										Bukkit.getPluginManager().disablePlugin(plugin);
									
									utilities.unloadPlugin(plugin);
									
									try {
										plugin = Bukkit.getPluginManager().loadPlugin(file);
		
										if(plugin != null) {
											Bukkit.getConsoleSender().sendMessage("[" + plugin.getName() + "] Loading " + plugin.getName() + " v" + plugin.getDescription().getVersion());
	
											plugin.onLoad();
											Bukkit.getPluginManager().enablePlugin(plugin);
											
											sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginReloaded").replace("%plugin%", args[1]));
										} else
											sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginCanNotBeLoaded").replace("%plugin%", args[1]));
									} catch (UnknownDependencyException | InvalidPluginException | InvalidDescriptionException ex) {
										ex.printStackTrace();
										
										sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginCanNotBeLoaded").replace("%plugin%", plugin.getName()));
									}
								} else
									sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginNotFound").replace("%plugin%", args[1]));
							} else {
								sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginNotFound").replace("%plugin%", args[1]));
								sender.sendMessage(prefix + "§7All files§8:");
								
								for (File content : new File("plugins").listFiles()) {
									if(content.isFile() && content.getName().endsWith(".jar")) {
										sender.sendMessage(prefix + "§e" + content.getName());
									}
								}
							}
						} else
							sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginCanNotBeToggled").replace("%plugin%", args[1]));
					}
				} else
					sender.sendMessage(utilities.getNoPerm());
			} else if(args[0].equalsIgnoreCase("info")) {
				if(sender.hasPermission("pluginmanager.info") || sender.hasPermission("pluginmanager.info." + args[1].toLowerCase())) {
					Plugin plugin = Bukkit.getPluginManager().getPlugin(args[1]);
					
					if(plugin != null) {
						PluginDescriptionFile description = plugin.getDescription();
						
						for (String line : setupFileManager.getConfig().getStringList("Messages.PluginInfo"))
							sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', line.replace("%name%", description.getName()).replace("%version%", description.getVersion()).replace("%state%", plugin.isEnabled() ? setupFileManager.getConfigString("Messages.State.Enabled") : setupFileManager.getConfigString("Messages.State.Disabled")).replace("%authors%", description.getAuthors().toString().replace("[", "").replace("]", ""))));
					} else
						sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginNotFound").replace("%plugin%", args[1]));
				} else
					sender.sendMessage(utilities.getNoPerm());
			} else if(args[0].equalsIgnoreCase("deldir")) {
				if(sender.hasPermission("pluginmanager.deletedirectory") || sender.hasPermission("pluginmanager.deletedirectory." + args[1].toLowerCase())) {
					Plugin plugin = Bukkit.getPluginManager().getPlugin(args[1]);
					
					if((plugin != null) && (plugin instanceof JavaPlugin)) {
						try {
							File pluginDirectory = new File("plugins/" + plugin.getName() + "/");
							
							if(pluginDirectory.exists())
								utilities.deleteFile(pluginDirectory);
							
							sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginDirectoryDeleted").replace("%plugin%", plugin.getName()));
						} catch (SecurityException ex) {
							ex.printStackTrace();
							
							sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginDirectoryCanNotBeDeleted").replace("%plugin%", plugin.getName()));
						}
					} else
						sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginNotFound").replace("%plugin%", args[1]));
				} else
					sender.sendMessage(utilities.getNoPerm());
			} else if(args[0].equalsIgnoreCase("del")) {
				if(sender.hasPermission("pluginmanager.delete") || sender.hasPermission("pluginmanager.delete." + args[1].toLowerCase())) {
					Plugin plugin = Bukkit.getPluginManager().getPlugin(args[1]);
					
					if((plugin != null) && (plugin instanceof JavaPlugin)) {
						try {
							if(System.getProperty("os.name").contains("Windows"))
								utilities.unloadPlugin(plugin);
							
							Field fileField = JavaPlugin.class.getDeclaredField("file");
							fileField.setAccessible(true);
							
							((File) fileField.get(plugin)).delete();
							
							sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginDeleted").replace("%plugin%", plugin.getName()));
						} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
							ex.printStackTrace();
							
							sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginCanNotBeDeleted").replace("%plugin%", plugin.getName()));
						}
					} else
						sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginNotFound").replace("%plugin%", args[1]));
				} else
					sender.sendMessage(utilities.getNoPerm());
			} else if(args[0].equalsIgnoreCase("commands")) {
				if(sender.hasPermission("pluginmanager.commands") || sender.hasPermission("pluginmanager.commands." + args[1].toLowerCase())) {
					Plugin plugin = Bukkit.getPluginManager().getPlugin(args[1]);
					
					if(plugin != null) {
						ArrayList<String> alreadyShown = new ArrayList<>();
						
						sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginCommands.Header").replace("%plugin%", plugin.getName()));
						
						for (Command command : utilities.getCommands().getCommands()) {
							if(!(alreadyShown.contains(command.getName()))) {
								alreadyShown.add(command.getName());
								
								if(command instanceof PluginCommand) {
									PluginCommand pluginCommand = (PluginCommand) command;
									
									if(pluginCommand.getPlugin() == plugin) {
										if(!(pluginCommand.getAliases().isEmpty()))
											pluginCommand.getAliases().forEach(alias -> sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginCommands.Command").replace("%name%", alias).replace("%usage%", command.getUsage().equals("") ? "/" + alias : command.getUsage())));
										
										sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginCommands.Command").replace("%name%", pluginCommand.getName()).replace("%usage%", command.getUsage().equals("") ? "/" + command.getName() : command.getUsage()));
									}
								}
							}
						}
						
						sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginCommands.Footer").replace("%plugin%", plugin.getName()));
					} else
						sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginNotFound").replace("%plugin%", args[1]));
				} else
					sender.sendMessage(utilities.getNoPerm());
			} else if(args[0].equalsIgnoreCase("cmdlookup")) {
				if(sender.hasPermission("pluginmanager.cmdlookup") || sender.hasPermission("pluginmanager.cmdlookup." + args[1].toLowerCase())) {
					PluginCommand command = Bukkit.getPluginCommand(args[1]);
					
					if(command != null)
						sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.CommandBelongsToPlugin").replace("%command%", command.getName()).replace("%plugin%", command.getPlugin().getName()));
					else
						sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.CommandNotFound").replace("%command%", args[1]));
				} else
					sender.sendMessage(utilities.getNoPerm());
			} else
				showHelp = true;
		} else if(args.length == 1) {
			if(args[0].equalsIgnoreCase("list")) {
				if(sender.hasPermission("pluginmanager.list")) {
					ArrayList<String> plugins = new ArrayList<>();
					
					for (Plugin plugin : Bukkit.getPluginManager().getPlugins())
						plugins.add(plugin.getName());
					
					sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.AllPlugins.Header").replace("%count%", String.valueOf(plugins.size())));
					
					Collections.sort(plugins, Collator.getInstance());
					
					String spacer = setupFileManager.getConfigString("Messages.AllPlugins.Spacer"), s = "";
					
					for (String pluginName : plugins) {
						Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
						
						s += setupFileManager.getConfigString("Messages.AllPlugins.Plugin").replace("%state%", plugin.isEnabled() ? "§a" : "§c").replace("%name%", plugin.getDescription().getName()).replace("%version%", plugin.getDescription().getVersion()) + spacer;
					}
					
					sender.sendMessage(prefix + ((plugins.size() < 2) ? s : s.substring(0, s.length() - spacer.length())));
					sender.sendMessage(prefix + setupFileManager.getConfigString("Messages.AllPlugins.Footer"));
				} else
					sender.sendMessage(utilities.getNoPerm());
			} else
				showHelp = true;
		} else
			showHelp = true;
		
		if(showHelp) {
			PluginDescriptionFile description = pluginManager.getDescription();
			
			if((args.length >= 1) && args[0].equalsIgnoreCase("about")) {
				sender.sendMessage(prefix + "§8⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ §a" + description.getName() + " §8⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯");
				sender.sendMessage(prefix + "§7Plugin made by §b" + description.getAuthors().toString().replace("[", "").replace("]", ""));
				sender.sendMessage(prefix + "§7Version§8: §d" + description.getVersion());
				sender.sendMessage(prefix + "§8⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ §a" + description.getName() + " §8⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯");
			} else {
				if(sender.hasPermission("pluginmanager.use")) {
					sender.sendMessage(prefix + "§8⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ §a" + description.getName() + " §8⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯");
					sender.sendMessage(prefix + "§a/pman §8| §7Opens the gui");
					sender.sendMessage(prefix + "§a/pman enable «plugin | all» §8| §7Enables all plugins/the given plugin");
					sender.sendMessage(prefix + "§a/pman disable «plugin | all» §8| §7Disables all plugins/the given plugin");
					sender.sendMessage(prefix + "§a/pman restart «plugin | all» §8| §7Restarts all plugins/the given plugin");
					sender.sendMessage(prefix + "§a/pman load «plugin | all» §8| §7Loads all plugins/the given plugin");
					sender.sendMessage(prefix + "§a/pman unload «plugin | all» §8| §7Unloads all plugins/the given plugin");
					sender.sendMessage(prefix + "§a/pman reload «plugin | all» §8| §7Reloads all plugins/the given plugin");
					sender.sendMessage(prefix + "§a/pman cmdlookup «command» §8| §7Shows the plugin which the given command belongs to");
					sender.sendMessage(prefix + "§a/pman info «plugin» §8| §7Shows information about the given plugin");
					sender.sendMessage(prefix + "§a/pman deldir «plugin» §8| §7Deletes the direcoty of the given plugin");
					sender.sendMessage(prefix + "§a/pman del «plugin» §8| §7Deletes the file of the given plugin");
					sender.sendMessage(prefix + "§a/pman commands «plugin» §8| §7Shows the commands of the given plugin");
					sender.sendMessage(prefix + "§a/pman list §8| §7Shows all plugins");
					sender.sendMessage(prefix + "§8⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ §a" + description.getName() + " §8⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯");
				} else
					sender.sendMessage(utilities.getNoPerm());
			}
		}
		
		return true;
	}

}
