package net.dev.pluginmanager.listeners;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.*;

import net.dev.pluginmanager.PluginManager;
import net.dev.pluginmanager.utilities.SetupFileManager;
import net.dev.pluginmanager.utilities.Utilities;

public class InventoryClickListener implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		PluginManager pluginManager = PluginManager.getInstance();
		Utilities utilities = pluginManager.getUtils();
		SetupFileManager setupFileManager = pluginManager.getFileUtils();
		
		String prefix = utilities.getPrefix();
		
		if(e.getWhoClicked() instanceof Player) {
			Player player = (Player) e.getWhoClicked();
			
			if((e.getCurrentItem() != null) && (e.getCurrentItem().getType() != Material.AIR) && (e.getCurrentItem().getItemMeta() != null) && e.getCurrentItem().getItemMeta().hasDisplayName()) {
				if(e.getView().getTitle().equalsIgnoreCase(setupFileManager.getConfigString("Settings.PluginsInventory.Title"))) {
					if(player.hasPermission("pluginmanager.gui")) {
						e.setCancelled(true);
						
						if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(setupFileManager.getConfigString("Settings.PluginsInventory.Next.DisplayName"))) {
							utilities.getCurrentPages().put(player.getUniqueId(), utilities.getCurrentPages().get(player.getUniqueId()) + 1);
							utilities.openInventory(player);
						} else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(setupFileManager.getConfigString("Settings.PluginsInventory.Back.DisplayName"))) {
							utilities.getCurrentPages().put(player.getUniqueId(), utilities.getCurrentPages().get(player.getUniqueId()) - 1);
							utilities.openInventory(player);
						} else {
							for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
								if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(setupFileManager.getConfigString("Settings.PluginsInventory.Plugin.DisplayName").replace("%plugin%", plugin.getDescription().getName()))) {
									if(e.isRightClick()) {
										if(plugin.isEnabled()) {
											Bukkit.getPluginManager().disablePlugin(plugin);
											
											ItemStack newItem = new ItemStack(e.getCurrentItem().getType(), e.getCurrentItem().getAmount(), (byte) setupFileManager.getConfig().getInt("Settings.PluginsInventory.Plugin.MetaData.Disabled"));
											newItem.setItemMeta(e.getCurrentItem().getItemMeta());
											
											e.getInventory().setItem(e.getSlot(), newItem);
											player.updateInventory();
										} else {
											Bukkit.getPluginManager().enablePlugin(plugin);
											
											ItemStack newItem = new ItemStack(e.getCurrentItem().getType(), e.getCurrentItem().getAmount(), (byte) setupFileManager.getConfig().getInt("Settings.PluginsInventory.Plugin.MetaData.Enabled"));
											newItem.setItemMeta(e.getCurrentItem().getItemMeta());
											
											e.getInventory().setItem(e.getSlot(), newItem);
											player.updateInventory();
										}
									} else {
										player.closeInventory();
										
										Inventory inv = Bukkit.createInventory(null, 27, setupFileManager.getConfigString("Settings.SettingsInventory.Title").replace("%plugin%", plugin.getDescription().getName()));
										
										if(setupFileManager.getConfig().getBoolean("Settings.SettingsInventory.UsePlaceHolders")) {
											for (int i = 0; i < inv.getSize(); i++)
												inv.setItem(i, utilities.createItem(Material.getMaterial(setupFileManager.getConfigString("Settings.SettingsInventory.PlaceHolder.Type")), 1, 0, setupFileManager.getConfigString("Settings.SettingsInventory.PlaceHolder.DisplayName")));
										}
											
										inv.setItem(10, utilities.createItem(Material.getMaterial(setupFileManager.getConfigString("Settings.SettingsInventory.Enable.Type")), 1, setupFileManager.getConfig().getInt("Settings.SettingsInventory.Enable.MetaData"), setupFileManager.getConfigString("Settings.SettingsInventory.Enable.DisplayName"), setupFileManager.getStringList("Settings.SettingsInventory.Enable.Lore")));
										inv.setItem(11, utilities.createItem(Material.getMaterial(setupFileManager.getConfigString("Settings.SettingsInventory.Disable.Type")), 1, setupFileManager.getConfig().getInt("Settings.SettingsInventory.Disable.MetaData"), setupFileManager.getConfigString("Settings.SettingsInventory.Disable.DisplayName"), setupFileManager.getStringList("Settings.SettingsInventory.Disable.Lore")));
										inv.setItem(13, utilities.createItem(Material.getMaterial(setupFileManager.getConfigString("Settings.SettingsInventory.Info.Type")), 1, 0, setupFileManager.getConfigString("Settings.SettingsInventory.Info.DisplayName").replace("%plugin%", plugin.getDescription().getName()), utilities.replaceInList(utilities.replaceInList(utilities.replaceInList(setupFileManager.getStringList("Settings.SettingsInventory.Info.Lore"), "%version%", plugin.getDescription().getVersion()), "%authors%", plugin.getDescription().getAuthors().toString().replace("[", "").replace("]", "")), "%state%", plugin.isEnabled() ? setupFileManager.getConfigString("Messages.State.Enabled") : setupFileManager.getConfigString("Messages.State.Disabled"))));
										inv.setItem(15, utilities.createItem(Material.getMaterial(setupFileManager.getConfigString("Settings.SettingsInventory.Restart.Type")), 1, setupFileManager.getConfig().getInt("Settings.SettingsInventory.Restart.MetaData"), setupFileManager.getConfigString("Settings.SettingsInventory.Restart.DisplayName"), setupFileManager.getStringList("Settings.SettingsInventory.Restart.Lore")));
										inv.setItem(16, utilities.createItem(Material.getMaterial(setupFileManager.getConfigString("Settings.SettingsInventory.Reload.Type")), 1, setupFileManager.getConfig().getInt("Settings.SettingsInventory.Reload.MetaData"), setupFileManager.getConfigString("Settings.SettingsInventory.Reload.DisplayName"), setupFileManager.getStringList("Settings.SettingsInventory.Reload.Lore")));
										
										inv.setItem(18, utilities.createItem(Material.getMaterial(setupFileManager.getConfigString("Settings.SettingsInventory.Back.Type")), 1, 0, setupFileManager.getConfigString("Settings.SettingsInventory.Back.DisplayName"), setupFileManager.getStringList("Settings.SettingsInventory.Back.Lore")));
										
										player.openInventory(inv);
									}

									break;
								}
							}
						}
					}
				} else {
					for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
						if(e.getView().getTitle().equalsIgnoreCase(setupFileManager.getConfigString("Settings.SettingsInventory.Title").replace("%plugin%", plugin.getDescription().getName()))) {
							if(player.hasPermission("pluginmanager.gui")) {
								e.setCancelled(true);
								
								if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(setupFileManager.getConfigString("Settings.SettingsInventory.Enable.DisplayName"))) {
									if(!(plugin.isEnabled()))
										Bukkit.getPluginManager().enablePlugin(plugin);
									
									player.closeInventory();
									player.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginEnabled").replace("%plugin%", plugin.getDescription().getName()));
								} else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(setupFileManager.getConfigString("Settings.SettingsInventory.Disable.DisplayName"))) {
									if(plugin.isEnabled())
										Bukkit.getPluginManager().disablePlugin(plugin);
									
									player.closeInventory();
									player.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginDisabled").replace("%plugin%", plugin.getDescription().getName()));
								} else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(setupFileManager.getConfigString("Settings.SettingsInventory.Restart.DisplayName"))) {
									if(plugin.isEnabled())
										Bukkit.getPluginManager().disablePlugin(plugin);
									
									Bukkit.getPluginManager().enablePlugin(plugin);
									
									player.closeInventory();
									player.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginRestarted").replace("%plugin%", plugin.getDescription().getName()));
								} else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(setupFileManager.getConfigString("Settings.SettingsInventory.Reload.DisplayName"))) {
									String name = plugin.getDescription().getName();
									File file = new File("plugins", "PluginManager.jar");
									
									for (File tmpFile : new File("plugins").listFiles()) {
										if(tmpFile.getName().endsWith(".jar")) {
											if(tmpFile.getName().toLowerCase().contains(name.toLowerCase()))
												file = new File("plugins", tmpFile.getName());
										}
									}
									
									player.closeInventory();
									
									if(file.exists()) {
										if(plugin.isEnabled())
											Bukkit.getPluginManager().disablePlugin(plugin);
										
										utilities.unloadPlugin(plugin);
										
										try {
											plugin = Bukkit.getPluginManager().loadPlugin(file);
			
											if(plugin != null) {
												Bukkit.getConsoleSender().sendMessage("[" + plugin.getName() + "] Loading " + plugin.getName() + " v" + plugin.getDescription().getVersion());
		
												plugin.onLoad();
												Bukkit.getPluginManager().enablePlugin(plugin);
												
												player.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginReloaded").replace("%plugin%", name));
											} else
												player.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginCanNotBeLoaded").replace("%plugin%", name));
										} catch (UnknownDependencyException | InvalidPluginException | InvalidDescriptionException ex) {
											ex.printStackTrace();
											
											player.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginCanNotBeLoaded").replace("%plugin%", plugin.getName()));
										}
									} else
										player.sendMessage(prefix + setupFileManager.getConfigString("Messages.PluginCanNotBeToggled").replace("%plugin%", name));
								} else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(setupFileManager.getConfigString("Settings.SettingsInventory.Back.DisplayName"))) {
									player.closeInventory();
									
									utilities.openInventory(player);
								}
							}
						}
					}
				}
			}
		}
	}
	
}
