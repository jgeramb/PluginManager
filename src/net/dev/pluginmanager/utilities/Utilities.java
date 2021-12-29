package net.dev.pluginmanager.utilities;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

import net.dev.pluginmanager.PluginManager;

public class Utilities {

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

	public void openInventory(Player player) {
		PluginManager pluginManager = PluginManager.getInstance();
		SetupFileManager setupFileManager = pluginManager.getFileUtils();
		
		if(!(currentPages.containsKey(player.getUniqueId())))
			currentPages.put(player.getUniqueId(), 0);
		
		Inventory inv = Bukkit.createInventory(null, 54, setupFileManager.getConfigString("Settings.PluginsInventory.Title"));
		List<Plugin> plugins = Arrays.asList(Bukkit.getPluginManager().getPlugins()).stream().filter(plugin -> !(plugin.getName().equalsIgnoreCase(pluginManager.getDescription().getName()))).sorted(Comparator.comparing(plugin -> plugin.getName())).collect(Collectors.toList());
		int currentPage = currentPages.get(player.getUniqueId()), i = 0, count = 0;

		for (Plugin plugin : plugins) {
			count++;
			
			if(count <= (currentPage * 45))
				continue;
			
			if(i < 45) {
				i++;
				int slot = i - 1;
				
				Bukkit.getScheduler().runTaskLater(pluginManager, () -> inv.setItem(slot, createItem(Material.getMaterial(setupFileManager.getConfigString(plugin.isEnabled() ? "Settings.PluginsInventory.Plugin.Type.Enabled" : "Settings.PluginsInventory.Plugin.Type.Disabled")), 1, plugin.isEnabled() ? setupFileManager.getConfig().getInt("Settings.PluginsInventory.Plugin.MetaData.Enabled") : setupFileManager.getConfig().getInt("Settings.PluginsInventory.Plugin.MetaData.Disabled"), setupFileManager.getConfigString("Settings.PluginsInventory.Plugin.DisplayName").replace("%plugin%", plugin.getDescription().getName()), replaceInList(setupFileManager.getStringList("Settings.PluginsInventory.Plugin.Lore"), "%plugin%", plugin.getDescription().getVersion()))), i);
			}
		}
		
		inv.setItem(45, createItem(Material.getMaterial(setupFileManager.getConfigString("Settings.PluginsInventory.Info.Type")), 1, 0, setupFileManager.getConfigString("Settings.PluginsInventory.Info.DisplayName"), setupFileManager.getStringList("Settings.PluginsInventory.Info.Lore")));

		if(currentPage != 0)
			inv.setItem(52, createItem(Material.getMaterial(setupFileManager.getConfigString("Settings.PluginsInventory.Back.Type")), 1, 0, setupFileManager.getConfigString("Settings.PluginsInventory.Back.DisplayName"), setupFileManager.getStringList("Settings.PluginsInventory.Back.Lore")));
		
		if(plugins.size() > (45 * (currentPage + 1)))
			inv.setItem(53, createItem(Material.getMaterial(setupFileManager.getConfigString("Settings.PluginsInventory.Next.Type")), 1, 0, setupFileManager.getConfigString("Settings.PluginsInventory.Next.DisplayName"), setupFileManager.getStringList("Settings.PluginsInventory.Next.Lore")));
		
		player.openInventory(inv);
	}
	
	public void unloadPlugin(Plugin plugin) {
		org.bukkit.plugin.PluginManager pluginManager = Bukkit.getPluginManager();
		
		if(pluginManager != null) {
			try {
				//Unregister plugin
				Field pluginsField = pluginManager.getClass().getDeclaredField("plugins");
				pluginsField.setAccessible(true);
				List<Plugin> plugins = (List<Plugin>) pluginsField.get(pluginManager);
				
				Field lookupNamesField = pluginManager.getClass().getDeclaredField("lookupNames");
				lookupNamesField.setAccessible(true);
				Map<String, Plugin> lookupNames = (Map<String, Plugin>) lookupNamesField.get(pluginManager);
				
				if(plugins.contains(plugin))
					plugins.remove(plugin);
				
				if(lookupNames.containsKey(plugin.getName()))
					lookupNames.remove(plugin.getName());
				
				pluginsField.set(pluginManager, plugins);
				lookupNamesField.set(pluginManager, lookupNames);
				
				try {
					//Remove listeners
					try {
						Field listenersField = pluginManager.getClass().getDeclaredField("listeners");
						listenersField.setAccessible(true);
						Map<Event, SortedSet<RegisteredListener>> listeners = (Map<Event, SortedSet<RegisteredListener>>) listenersField.get(pluginManager);

						ArrayList<Event> toRemoveFromListeners = new ArrayList<>();
						
						for (Event event : listeners.keySet()) {
							for (RegisteredListener listener : listeners.get(event)) {
								if(listener.getPlugin() == plugin)
									toRemoveFromListeners.add(event);
							}
						}
						
						toRemoveFromListeners.forEach(event -> listeners.remove(event));
						
						listenersField.set(pluginManager, listeners);
					} catch (Exception ex) {
					}

					//Unregister commands
					Field commandMapField = pluginManager.getClass().getDeclaredField("commandMap");
					commandMapField.setAccessible(true);
					SimpleCommandMap commandMap = (SimpleCommandMap) commandMapField.get(pluginManager);
					
					Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
					knownCommandsField.setAccessible(true);
					Map<String, Command> knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);
						
					if(commandMap != null) {
						new HashSet<>(knownCommands.entrySet()).stream().filter(knownCommand -> (knownCommand.getValue() instanceof PluginCommand)).filter(knownCommand -> ((PluginCommand) knownCommand.getValue()).getPlugin().equals(plugin)).forEach(knownCommand -> {
							knownCommand.getValue().unregister(commandMap);
							knownCommands.remove(knownCommand.getKey());
						});
						
						knownCommandsField.set(commandMap, knownCommands);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
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
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public SimpleCommandMap getCommands() {
		try {
			org.bukkit.plugin.PluginManager pm = Bukkit.getPluginManager();
			Field commandMapField = pm.getClass().getDeclaredField("commandMap");
			commandMapField.setAccessible(true);
			
			return (SimpleCommandMap) commandMapField.get(pm);
		} catch (Exception ex) {
			return new SimpleCommandMap(Bukkit.getServer());
		}
	}
	
	public boolean deleteFile(File file) {
		if(file.isDirectory()) {
	        for (File subFile : file.listFiles()) {
	            if (!(deleteFile(subFile)))
	                return false;
	        }
		}
		
		return file.delete();
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