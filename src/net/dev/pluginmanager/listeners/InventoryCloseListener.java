package net.dev.pluginmanager.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.Plugin;

import net.dev.pluginmanager.PluginManager;
import net.dev.pluginmanager.utils.FileUtils;
import net.dev.pluginmanager.utils.Utils;

public class InventoryCloseListener implements Listener {

	@EventHandler
	public void onInvenoryClose(InventoryCloseEvent e) {
		PluginManager pluginManager = PluginManager.getInstance();
		Utils utils = pluginManager.getUtils();
		FileUtils fileUtils = pluginManager.getFileUtils();
		
		if(e.getPlayer() instanceof Player) {
			Player player = (Player) e.getPlayer();
			
			for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
				if(e.getView().getTitle().equalsIgnoreCase(fileUtils.getConfigString("Settings.PluginsInventory.Title").replace("%plugin%", plugin.getDescription().getName())) || e.getView().getTitle().equalsIgnoreCase(fileUtils.getConfigString("Settings.SettingsInventory.Title").replace("%plugin%", plugin.getDescription().getName()))) {
					Bukkit.getScheduler().runTaskLater(PluginManager.getInstance(), new Runnable() {
						
						@Override
						public void run() {
							if(player.isOnline()) {
								if((player.getOpenInventory() == null) || ((player.getOpenInventory() == null) && !(player.getOpenInventory().getTitle().equalsIgnoreCase(fileUtils.getConfigString("Settings.PluginsInventory.Title").replace("%plugin%", plugin.getDescription().getName())) || player.getOpenInventory().getTitle().equalsIgnoreCase(fileUtils.getConfigString("Settings.SettingsInventory.Title").replace("%plugin%", plugin.getDescription().getName()))))) {
									if(utils.getCurrentPages().containsKey(player.getUniqueId()))
										utils.getCurrentPages().remove(player.getUniqueId());
								}
							} else if(utils.getCurrentPages().containsKey(player.getUniqueId()))
								utils.getCurrentPages().remove(player.getUniqueId());
						}
					}, 20);
				}
			}
		}
	}
	
}
