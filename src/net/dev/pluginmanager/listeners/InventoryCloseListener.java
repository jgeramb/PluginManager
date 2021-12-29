package net.dev.pluginmanager.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.Plugin;

import net.dev.pluginmanager.PluginManager;
import net.dev.pluginmanager.utilities.SetupFileManager;
import net.dev.pluginmanager.utilities.Utilities;

public class InventoryCloseListener implements Listener {

	@EventHandler
	public void onInvenoryClose(InventoryCloseEvent e) {
		PluginManager pluginManager = PluginManager.getInstance();
		Utilities utilities = pluginManager.getUtils();
		SetupFileManager setupFileManager = pluginManager.getFileUtils();
		
		if(e.getPlayer() instanceof Player) {
			Player player = (Player) e.getPlayer();
			
			for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
				if(e.getView().getTitle().equalsIgnoreCase(setupFileManager.getConfigString("Settings.PluginsInventory.Title").replace("%plugin%", plugin.getDescription().getName())) || e.getView().getTitle().equalsIgnoreCase(setupFileManager.getConfigString("Settings.SettingsInventory.Title").replace("%plugin%", plugin.getDescription().getName()))) {
					Bukkit.getScheduler().runTaskLater(PluginManager.getInstance(), new Runnable() {
						
						@Override
						public void run() {
							if(player.isOnline()) {
								if((player.getOpenInventory() == null) || ((player.getOpenInventory() == null) && !(player.getOpenInventory().getTitle().equalsIgnoreCase(setupFileManager.getConfigString("Settings.PluginsInventory.Title").replace("%plugin%", plugin.getDescription().getName())) || player.getOpenInventory().getTitle().equalsIgnoreCase(setupFileManager.getConfigString("Settings.SettingsInventory.Title").replace("%plugin%", plugin.getDescription().getName()))))) {
									if(utilities.getCurrentPages().containsKey(player.getUniqueId()))
										utilities.getCurrentPages().remove(player.getUniqueId());
								}
							} else if(utilities.getCurrentPages().containsKey(player.getUniqueId()))
								utilities.getCurrentPages().remove(player.getUniqueId());
						}
					}, 20);
				}
			}
		}
	}
	
}
