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
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.UnknownDependencyException;

import net.dev.pluginmanager.PluginManager;
import net.dev.pluginmanager.utils.FileUtils;
import net.dev.pluginmanager.utils.Utils;

public class InventoryClickListener implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		PluginManager pluginManager = PluginManager.getInstance();
		Utils utils = pluginManager.getUtils();
		FileUtils fileUtils = pluginManager.getFileUtils();
		
		String prefix = utils.getPrefix();
		
		if(e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			
			if((e.getCurrentItem() != null) && (e.getCurrentItem().getType() != Material.AIR) && (e.getCurrentItem().getItemMeta() != null) && e.getCurrentItem().getItemMeta().hasDisplayName()) {
				if(e.getView().getTitle().equalsIgnoreCase(fileUtils.getConfigString("Settings.PluginsInventory.Title"))) {
					if(p.hasPermission("pluginmanager.gui")) {
						e.setCancelled(true);
						
						if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(fileUtils.getConfigString("Settings.PluginsInventory.Next.DisplayName"))) {
							utils.getCurrentPages().put(p.getUniqueId(), utils.getCurrentPages().get(p.getUniqueId()) + 1);
							utils.openInventory(p);
						} else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(fileUtils.getConfigString("Settings.PluginsInventory.Back.DisplayName"))) {
							utils.getCurrentPages().put(p.getUniqueId(), utils.getCurrentPages().get(p.getUniqueId()) - 1);
							utils.openInventory(p);
						} else {
							for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
								if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(fileUtils.getConfigString("Settings.PluginsInventory.Plugin.DisplayName").replace("%plugin%", plugin.getDescription().getName()))) {
									if(e.isRightClick()) {
										if(plugin.isEnabled()) {
											Bukkit.getPluginManager().disablePlugin(plugin);
											
											ItemStack newItem = new ItemStack(e.getCurrentItem().getType(), e.getCurrentItem().getAmount(), (byte) fileUtils.getConfig().getInt("Settings.PluginsInventory.Plugin.MetaData.Disabled"));
											newItem.setItemMeta(e.getCurrentItem().getItemMeta());
											
											e.getInventory().setItem(e.getSlot(), newItem);
											p.updateInventory();
										} else {
											Bukkit.getPluginManager().enablePlugin(plugin);
											
											ItemStack newItem = new ItemStack(e.getCurrentItem().getType(), e.getCurrentItem().getAmount(), (byte) fileUtils.getConfig().getInt("Settings.PluginsInventory.Plugin.MetaData.Enabled"));
											newItem.setItemMeta(e.getCurrentItem().getItemMeta());
											
											e.getInventory().setItem(e.getSlot(), newItem);
											p.updateInventory();
										}
									} else {
										p.closeInventory();
										
										Inventory inv = Bukkit.createInventory(null, 27, fileUtils.getConfigString("Settings.SettingsInventory.Title").replace("%plugin%", plugin.getDescription().getName()));
										
										if(fileUtils.getConfig().getBoolean("Settings.SettingsInventory.UsePlaceHolders")) {
											for (int i = 0; i < inv.getSize(); i++)
												inv.setItem(i, utils.createItem(Material.getMaterial(fileUtils.getConfigString("Settings.SettingsInventory.PlaceHolder.Type")), 1, 0, fileUtils.getConfigString("Settings.SettingsInventory.PlaceHolder.DisplayName")));
										}
											
										inv.setItem(10, utils.createItem(Material.getMaterial(fileUtils.getConfigString("Settings.SettingsInventory.Enable.Type")), 1, fileUtils.getConfig().getInt("Settings.SettingsInventory.Enable.MetaData"), fileUtils.getConfigString("Settings.SettingsInventory.Enable.DisplayName"), fileUtils.getStringList("Settings.SettingsInventory.Enable.Lore")));
										inv.setItem(11, utils.createItem(Material.getMaterial(fileUtils.getConfigString("Settings.SettingsInventory.Disable.Type")), 1, fileUtils.getConfig().getInt("Settings.SettingsInventory.Disable.MetaData"), fileUtils.getConfigString("Settings.SettingsInventory.Disable.DisplayName"), fileUtils.getStringList("Settings.SettingsInventory.Disable.Lore")));
										inv.setItem(13, utils.createItem(Material.getMaterial(fileUtils.getConfigString("Settings.SettingsInventory.Info.Type")), 1, 0, fileUtils.getConfigString("Settings.SettingsInventory.Info.DisplayName").replace("%plugin%", plugin.getDescription().getName()), utils.replaceInList(utils.replaceInList(utils.replaceInList(fileUtils.getStringList("Settings.SettingsInventory.Info.Lore"), "%version%", plugin.getDescription().getVersion()), "%authors%", plugin.getDescription().getAuthors().toString().replace("[", "").replace("]", "")), "%state%", plugin.isEnabled() ? fileUtils.getConfigString("Messages.State.Enabled") : fileUtils.getConfigString("Messages.State.Disabled"))));
										inv.setItem(15, utils.createItem(Material.getMaterial(fileUtils.getConfigString("Settings.SettingsInventory.Restart.Type")), 1, fileUtils.getConfig().getInt("Settings.SettingsInventory.Restart.MetaData"), fileUtils.getConfigString("Settings.SettingsInventory.Restart.DisplayName"), fileUtils.getStringList("Settings.SettingsInventory.Restart.Lore")));
										inv.setItem(16, utils.createItem(Material.getMaterial(fileUtils.getConfigString("Settings.SettingsInventory.Reload.Type")), 1, fileUtils.getConfig().getInt("Settings.SettingsInventory.Reload.MetaData"), fileUtils.getConfigString("Settings.SettingsInventory.Reload.DisplayName"), fileUtils.getStringList("Settings.SettingsInventory.Reload.Lore")));
										
										inv.setItem(18, utils.createItem(Material.getMaterial(fileUtils.getConfigString("Settings.SettingsInventory.Back.Type")), 1, 0, fileUtils.getConfigString("Settings.SettingsInventory.Back.DisplayName"), fileUtils.getStringList("Settings.SettingsInventory.Back.Lore")));
										
										p.openInventory(inv);
									}

									break;
								}
							}
						}
					}
				} else {
					for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
						if(e.getView().getTitle().equalsIgnoreCase(fileUtils.getConfigString("Settings.SettingsInventory.Title").replace("%plugin%", plugin.getDescription().getName()))) {
							if(p.hasPermission("pluginmanager.gui")) {
								e.setCancelled(true);
								
								if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(fileUtils.getConfigString("Settings.SettingsInventory.Enable.DisplayName"))) {
									if(!(plugin.isEnabled()))
										Bukkit.getPluginManager().enablePlugin(plugin);
									
									p.closeInventory();
									p.sendMessage(prefix + fileUtils.getConfigString("Messages.PluginEnabled").replace("%plugin%", plugin.getDescription().getName()));
								} else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(fileUtils.getConfigString("Settings.SettingsInventory.Disable.DisplayName"))) {
									if(plugin.isEnabled())
										Bukkit.getPluginManager().disablePlugin(plugin);
									
									p.closeInventory();
									p.sendMessage(prefix + fileUtils.getConfigString("Messages.PluginDisabled").replace("%plugin%", plugin.getDescription().getName()));
								} else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(fileUtils.getConfigString("Settings.SettingsInventory.Restart.DisplayName"))) {
									if(plugin.isEnabled())
										Bukkit.getPluginManager().disablePlugin(plugin);
									
									Bukkit.getPluginManager().enablePlugin(plugin);
									
									p.closeInventory();
									p.sendMessage(prefix + fileUtils.getConfigString("Messages.PluginRestarted").replace("%plugin%", plugin.getDescription().getName()));
								} else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(fileUtils.getConfigString("Settings.SettingsInventory.Reload.DisplayName"))) {
									String name = plugin.getDescription().getName();
									File file = new File("plugins", "PluginManager.jar");
									
									for (File tmpFile : new File("plugins").listFiles()) {
										if(tmpFile.getName().endsWith(".jar")) {
											if(tmpFile.getName().toLowerCase().contains(name.toLowerCase()))
												file = new File("plugins", tmpFile.getName());
										}
									}
									
									p.closeInventory();
									
									if(file.exists()) {
										if(plugin.isEnabled())
											Bukkit.getPluginManager().disablePlugin(plugin);
										
										utils.unloadPlugin(plugin);
										
										try {
											plugin = Bukkit.getPluginManager().loadPlugin(file);
			
											if(plugin != null) {
												Bukkit.getConsoleSender().sendMessage("[" + plugin.getName() + "] Loading " + plugin.getName() + " v" + plugin.getDescription().getVersion());
		
												plugin.onLoad();
												Bukkit.getPluginManager().enablePlugin(plugin);
												
												p.sendMessage(prefix + fileUtils.getConfigString("Messages.PluginReloaded").replace("%plugin%", name));
											} else
												p.sendMessage(prefix + fileUtils.getConfigString("Messages.PluginCanNotBeLoaded").replace("%plugin%", name));
										} catch (UnknownDependencyException | InvalidPluginException | InvalidDescriptionException ex) {
											ex.printStackTrace();
											
											p.sendMessage(prefix + fileUtils.getConfigString("Messages.PluginCanNotBeLoaded").replace("%plugin%", plugin.getName()));
										}
									} else
										p.sendMessage(prefix + fileUtils.getConfigString("Messages.PluginCanNotBeToggled").replace("%plugin%", name));
								} else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(fileUtils.getConfigString("Settings.SettingsInventory.Back.DisplayName"))) {
									p.closeInventory();
									
									utils.openInventory(p);
								}
							}
						}
					}
				}
			}
		}
	}
	
}
