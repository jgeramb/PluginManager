package net.dev.pluginmanager.utils;

import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.text.Collator;
import java.util.ArrayList;
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
import org.bukkit.plugin.RegisteredListener;

import net.dev.pluginmanager.PluginManager;

public class Utils {

	private String prefix, noPerm, notPlayer;
	
	private HashMap<UUID, Integer> currentPages = new HashMap<>();
	
	public void sendConsole(String msg) {
		Bukkit.getConsoleSender().sendMessage(prefix + msg);
	}

	public ItemStack createItem(Material mat, int amount, int metaData, String displayName) {
		ItemStack item = new ItemStack(mat, amount, (byte) metaData);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public ItemStack createItem(Material mat, int amount, int metaData, String displayName, List<String> list) {
		ItemStack item = new ItemStack(mat, amount, (byte) metaData);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		meta.setLore(list);
		item.setItemMeta(meta);
		
		return item;
	}

	public void openInventory(Player p) {
		PluginManager pluginManager = PluginManager.getInstance();
		FileUtils fileUtils = pluginManager.getFileUtils();
		
		if(!(currentPages.containsKey(p.getUniqueId())))
			currentPages.put(p.getUniqueId(), 0);
		
		Inventory inv = Bukkit.createInventory(null, 54, fileUtils.getConfigString("Settings.PluginsInventory.Title"));
		ArrayList<Plugin> plugins = new ArrayList<>();
		ArrayList<String> pluginNames = new ArrayList<>();
		
		for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
			if(!(plugin.getName().equalsIgnoreCase(pluginManager.getDescription().getName())))
				pluginNames.add(plugin.getName());
		}
		
		pluginNames.sort(Collator.getInstance());
		
		for (String name : pluginNames)
			plugins.add(Bukkit.getPluginManager().getPlugin(name));
			
		int currentPage = currentPages.get(p.getUniqueId()), i = 0, count = 0;

		for (Plugin plugin : plugins) {
			count++;
			
			if(count <= (currentPage * 45))
				continue;
			
			if(i < 45) {
				i++;
				int slot = i - 1;
				
				Bukkit.getScheduler().runTaskLater(pluginManager, new Runnable() {
					
					@Override
					public void run() {
						inv.setItem(slot, createItem(Material.getMaterial(fileUtils.getConfigString(plugin.isEnabled() ? "Settings.PluginsInventory.Plugin.Type.Enabled" : "Settings.PluginsInventory.Plugin.Type.Disabled")), 1, plugin.isEnabled() ? fileUtils.getConfig().getInt("Settings.PluginsInventory.Plugin.MetaData.Enabled") : fileUtils.getConfig().getInt("Settings.PluginsInventory.Plugin.MetaData.Disabled"), fileUtils.getConfigString("Settings.PluginsInventory.Plugin.DisplayName").replace("%plugin%", plugin.getDescription().getName()), replaceInList(fileUtils.getStringList("Settings.PluginsInventory.Plugin.Lore"), "%plugin%", plugin.getDescription().getVersion())));
					}
				}, i);
			}
		}
		
		inv.setItem(45, createItem(Material.getMaterial(fileUtils.getConfigString("Settings.PluginsInventory.Info.Type")), 1, 0, fileUtils.getConfigString("Settings.PluginsInventory.Info.DisplayName"), fileUtils.getStringList("Settings.PluginsInventory.Info.Lore")));

		if(currentPage != 0)
			inv.setItem(52, createItem(Material.getMaterial(fileUtils.getConfigString("Settings.PluginsInventory.Back.Type")), 1, 0, fileUtils.getConfigString("Settings.PluginsInventory.Back.DisplayName"), fileUtils.getStringList("Settings.PluginsInventory.Back.Lore")));
		
		if(plugins.size() > (45 * (currentPage + 1)))
			inv.setItem(53, createItem(Material.getMaterial(fileUtils.getConfigString("Settings.PluginsInventory.Next.Type")), 1, 0, fileUtils.getConfigString("Settings.PluginsInventory.Next.DisplayName"), fileUtils.getStringList("Settings.PluginsInventory.Next.Lore")));
		
		p.openInventory(inv);
	}
	
	public void unloadPlugin(Plugin plugin) {
		org.bukkit.plugin.PluginManager pm = Bukkit.getPluginManager();
		
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
						
						for (Event event : listeners.keySet()) {
							for (RegisteredListener listener : listeners.get(event)) {
								if(listener.getPlugin() == plugin)
									toRemoveFromListeners.add(event);
							}
						}
						
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

	public SimpleCommandMap getCommands() {
		try {
			org.bukkit.plugin.PluginManager pm = Bukkit.getPluginManager();
			Field commandMapField = pm.getClass().getDeclaredField("commandMap");
			commandMapField.setAccessible(true);
			
			return (SimpleCommandMap) commandMapField.get(pm);
		} catch (Exception e) {
			return new SimpleCommandMap(Bukkit.getServer());
		}
	}
	
	public String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	}
	
	public List<String> replaceInList(List<String> stringList, String target, String replacement) {
		List<String> list = new ArrayList<>();
		
		for (String string : stringList)
			list.add(string.replace(target, replacement));
		
		return list;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public String getNoPerm() {
		return noPerm;
	}
	
	public String getNotPlayer() {
		return notPlayer;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public void setNoPerm(String noPerm) {
		this.noPerm = noPerm;
	}
	
	public void setNotPlayer(String notPlayer) {
		this.notPlayer = notPlayer;
	}
	
	public HashMap<UUID, Integer> getCurrentPages() {
		return currentPages;
	}

}