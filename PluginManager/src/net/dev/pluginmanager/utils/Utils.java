package net.dev.pluginmanager.utils;

import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredListener;

import net.dev.pluginmanager.main.Main;

public class Utils {

	public static String prefix;
	public static String noPerm;
	public static String notPlayer;
	
	public static HashMap<UUID, Integer> currentPages = new HashMap<>();
	
	public static void sendConsole(String msg) {
		Bukkit.getConsoleSender().sendMessage(prefix + msg);
	}

	public static ItemStack createItem(Material mat, int amount, int metaData, String displayName, String... lore) {
		ItemStack item = new ItemStack(mat, amount, (byte) metaData);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		meta.setLore(Arrays.asList(lore));
		item.setItemMeta(meta);
		
		return item;
	}

	public static void openInventory(Player p) {
		if(!(currentPages.containsKey(p.getUniqueId())))
			currentPages.put(p.getUniqueId(), 0);
		
		Inventory inv = Bukkit.createInventory(null, 54, "§aPlugins");
		ArrayList<Plugin> plugins = new ArrayList<>();
		ArrayList<String> pluginNames = new ArrayList<>();
		
		for (Plugin plugin : Bukkit.getPluginManager().getPlugins())
			if(!(plugin.getName().equalsIgnoreCase(Main.getInstance().getDescription().getName())))
				pluginNames.add(plugin.getName());
		
		pluginNames.sort(Collator.getInstance());
		
		for (String name : pluginNames)
			plugins.add(Bukkit.getPluginManager().getPlugin(name));
			
		int i = 0;
		
		if(currentPages.get(p.getUniqueId()) == 0) {
			for (Plugin plugin : plugins) {
				if(i < 45) {
					i++;
					int slot = i - 1;
					
					Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
						
						@Override
						public void run() {
							inv.setItem(slot, createItem(Utils.getVersion().contains("1_13") ? Material.getMaterial("LEGACY_WOOL") : Material.getMaterial("WOOL"), 1, plugin.isEnabled() ? 5 : 14, "§e" + plugin.getDescription().getName(), "§7Version§8: §d" + plugin.getDescription().getVersion()));
						}
					}, i);
				}
			}
		} else {
			int count = 0;

			for (Plugin plugin : plugins) {
				count++;
				
				if(count <= (currentPages.get(p.getUniqueId()) * 45))
					continue;
				
				if(i < 45) {
					i++;
					int slot = i - 1;
					
					Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
						
						@Override
						public void run() {
							inv.setItem(slot, createItem(Utils.getVersion().contains("1_13") ? Material.getMaterial("LEGACY_WOOL") : Material.getMaterial("WOOL"), 1, plugin.isEnabled() ? 5 : 14, "§e" + plugin.getDescription().getName(), "§7Version§8: §d" + plugin.getDescription().getVersion()));
						}
					}, i);
				}
			}
		}
		
		inv.setItem(45, createItem(Material.ANVIL, 1, 0, "§d§lInfo", "§7Leftclick a plugin to open it's settings", "§7Rightclick a plugin to toggle it's status"));
		
		if(plugins.size() > (45 + 1)) {
			inv.setItem(52, createItem(Material.PAPER, 1, 0, "§7Back", "§7Click here to go to the previous page"));
			inv.setItem(53, createItem(Material.PAPER, 1, 0, "§7Next", "§7Click here to go to the next page"));
		}
		
		p.openInventory(inv);
	}

	@SuppressWarnings("unchecked")
	public static void unloadPlugin(Plugin plugin) {
		PluginManager pm = Bukkit.getPluginManager();
		
		if(pm != null) {
			try {
				//Unregister plugin
				Field pluginsField = pm.getClass().getDeclaredField("plugins");
				pluginsField.setAccessible(true);
				List<Plugin> plugins = (List<Plugin>) pluginsField.get(pm);
				
				Field lookupNamesField = pm.getClass().getDeclaredField("lookupNames");
				lookupNamesField.setAccessible(true);
				Map<String, Plugin> lookupNames = (Map<String, Plugin>) lookupNamesField.get(pm);
				
				if(plugins.contains(plugin))
					plugins.remove(plugin);
				
				if(lookupNames.containsKey(plugin.getName()))
					lookupNames.remove(plugin.getName());
				
				pluginsField.set(pm, plugins);
				lookupNamesField.set(pm, lookupNames);
				
				try {
					//Remove listeners
					try {
						Field listenersField = pm.getClass().getDeclaredField("listeners");
						listenersField.setAccessible(true);
						Map<Event, SortedSet<RegisteredListener>> listeners = (Map<Event, SortedSet<RegisteredListener>>) listenersField.get(pm);

						ArrayList<Event> toRemoveFromListeners = new ArrayList<>();
						
						for (Event event : listeners.keySet())
							for (RegisteredListener listener : listeners.get(event))
								if(listener.getPlugin() == plugin)
									toRemoveFromListeners.add(event);
						
						toRemoveFromListeners.forEach(event -> listeners.remove(event));
						
						listenersField.set(pm, listeners);
					} catch (Exception e) {
					}

					//Unregister commands
					Field commandMapField = pm.getClass().getDeclaredField("commandMap");
					commandMapField.setAccessible(true);
					SimpleCommandMap commandMap = (SimpleCommandMap) commandMapField.get(pm);
					
					Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
					knownCommandsField.setAccessible(true);
					Map<String, Command> knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);
						
					if(commandMap != null) {
						ArrayList<String> toRemoveFromKnownCommands = new ArrayList<>();
						
						for (String commandName : knownCommands.keySet()) {
							Command cmd = knownCommands.get(commandName);
								
							if(cmd instanceof PluginCommand) {
								PluginCommand command = (PluginCommand) cmd;
								
								if(command.getPlugin() == plugin) {
									command.unregister(commandMap);
									
									toRemoveFromKnownCommands.add(commandName);
								}
							}
						}
					
						toRemoveFromKnownCommands.forEach(commandName -> knownCommands.remove(commandName));
						
						knownCommandsField.set(commandMap, knownCommands);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
					
				//Disable ClassLoader
				ClassLoader classLoader = plugin.getClass().getClassLoader();

		        if (classLoader instanceof URLClassLoader) {
	                Field pluginField = classLoader.getClass().getDeclaredField("plugin");
	                pluginField.setAccessible(true);
	                pluginField.set(classLoader, null);

	                Field pluginInitField = classLoader.getClass().getDeclaredField("pluginInit");
	                pluginInitField.setAccessible(true);
	                pluginInitField.set(classLoader, null);

	                ((URLClassLoader) classLoader).close();
		        }
				
				System.gc();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static SimpleCommandMap getCommands() {
		try {
			PluginManager pm = Bukkit.getPluginManager();
			Field commandMapField = pm.getClass().getDeclaredField("commandMap");
			commandMapField.setAccessible(true);
			
			return (SimpleCommandMap) commandMapField.get(pm);
		} catch (Exception e) {
			return new SimpleCommandMap(Bukkit.getServer());
		}
	}
	
	public static String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	}

}
