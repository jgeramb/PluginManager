package net.dev.pluginmanager.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import net.dev.pluginmanager.main.Main;
import net.dev.pluginmanager.utils.FileUtils;
import net.dev.pluginmanager.utils.Utils;

public class InventoryListener implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if(e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			
			if((e.getCurrentItem() != null) && (e.getCurrentItem().getType() != Material.AIR) && (e.getCurrentItem().getItemMeta() != null) && e.getCurrentItem().getItemMeta().hasDisplayName()) {
				if(e.getView().getTitle().equalsIgnoreCase(FileUtils.getConfigString("Settings.PluginsInventory.Title"))) {
					if(p.hasPermission("pluginmanager.gui")) {
						e.setCancelled(true);
						
						if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(FileUtils.getConfigString("Settings.PluginsInventory.Next.DisplayName"))) {
							if(Bukkit.getPluginManager().getPlugins().length >= ((45 * (Utils.currentPages.get(p.getUniqueId()) + 1) + 1))) {
								Utils.currentPages.put(p.getUniqueId(), Utils.currentPages.get(p.getUniqueId()) + 1);
								
								Utils.openInventory(p);
							} else {
								p.sendMessage(Utils.prefix + FileUtils.getConfigString("Messages.NoMorePages"));
							}
						} else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(FileUtils.getConfigString("Settings.PluginsInventory.Back.DisplayName"))) {
							if(Utils.currentPages.get(p.getUniqueId()) != 0) {
								Utils.currentPages.put(p.getUniqueId(), Utils.currentPages.get(p.getUniqueId()) - 1);
								
								Utils.openInventory(p);
							} else {
								p.sendMessage(Utils.prefix + FileUtils.getConfigString("Messages.AlreadyFirstPage"));
							}
						} else {
							for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
								if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(FileUtils.getConfigString("Settings.PluginsInventory.Plugin.DisplayName").replace("%plugin%", plugin.getDescription().getName()))) {
									if(e.isRightClick()) {
										if(plugin.isEnabled()) {
											Bukkit.getPluginManager().disablePlugin(plugin);
											
											ItemStack newItem = new ItemStack(e.getCurrentItem().getType(), e.getCurrentItem().getAmount(), (byte) FileUtils.cfg.getInt("Settings.PluginsInventory.Plugin.MetaData.Disabled"));
											newItem.setItemMeta(e.getCurrentItem().getItemMeta());
											
											e.getInventory().setItem(e.getSlot(), newItem);
											p.updateInventory();
										} else {
											Bukkit.getPluginManager().enablePlugin(plugin);
											
											ItemStack newItem = new ItemStack(e.getCurrentItem().getType(), e.getCurrentItem().getAmount(), (byte) FileUtils.cfg.getInt("Settings.PluginsInventory.Plugin.MetaData.Enabled"));
											newItem.setItemMeta(e.getCurrentItem().getItemMeta());
											
											e.getInventory().setItem(e.getSlot(), newItem);
											p.updateInventory();
										}
									} else {
										p.closeInventory();
										
										Inventory inv = Bukkit.createInventory(null, 27, FileUtils.getConfigString("Settings.SettingsInventory.Title").replace("%plugin%", plugin.getDescription().getName()));
										
										if(FileUtils.cfg.getBoolean("Settings.SettingsInventory.UsePlaceHolders"))
											for (int i = 0; i < inv.getSize(); i++)
												inv.setItem(i, Utils.createItem(Material.getMaterial(FileUtils.getConfigString("Settings.SettingsInventory.PlaceHolder.Type")), 1, 0, FileUtils.getConfigString("Settings.SettingsInventory.PlaceHolder.DisplayName")));
											
										inv.setItem(10, Utils.createItem(Material.getMaterial(FileUtils.getConfigString("Settings.SettingsInventory.Enable.Type")), 1, FileUtils.cfg.getInt("Settings.SettingsInventory.Enable.MetaData"), FileUtils.getConfigString("Settings.SettingsInventory.Enable.DisplayName"), FileUtils.getStringList("Settings.SettingsInventory.Enable.Lore")));
										inv.setItem(13, Utils.createItem(Material.getMaterial(FileUtils.getConfigString("Settings.SettingsInventory.Info.Type")), 1, 0, FileUtils.getConfigString("Settings.SettingsInventory.Info.DisplayName").replace("%plugin%", plugin.getDescription().getName()), Utils.replaceInList(FileUtils.getStringList("Settings.SettingsInventory.Info.Lore"), "%version%", plugin.getDescription().getVersion())));
										inv.setItem(16, Utils.createItem(Material.getMaterial(FileUtils.getConfigString("Settings.SettingsInventory.Disable.Type")), 1, FileUtils.cfg.getInt("Settings.SettingsInventory.Disable.MetaData"), FileUtils.getConfigString("Settings.SettingsInventory.Disable.DisplayName"), FileUtils.getStringList("Settings.SettingsInventory.Disable.Lore")));
										
										inv.setItem(18, Utils.createItem(Material.getMaterial(FileUtils.getConfigString("Settings.SettingsInventory.Back.Type")), 1, 0, FileUtils.getConfigString("Settings.SettingsInventory.Back.DisplayName"), FileUtils.getStringList("Settings.SettingsInventory.Back.Lore")));
										
										p.openInventory(inv);
									}

									break;
								}
							}
						}
					}
				} else {
					for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
						if(e.getView().getTitle().equalsIgnoreCase(FileUtils.getConfigString("Settings.SettingsInventory.Title").replace("%plugin%", plugin.getDescription().getName()))) {
							if(p.hasPermission("pluginmanager.gui")) {
								e.setCancelled(true);
								
								if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(FileUtils.getConfigString("Settings.SettingsInventory.Enable.DisplayName"))) {
									if(!(plugin.isEnabled()))
										Bukkit.getPluginManager().enablePlugin(plugin);
									
									p.closeInventory();
									p.sendMessage(Utils.prefix + FileUtils.getConfigString("Messages.PluginEnabled").replace("%plugin%", plugin.getDescription().getName()));
								} else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(FileUtils.getConfigString("Settings.SettingsInventory.Disable.DisplayName"))) {
									if(plugin.isEnabled())
										Bukkit.getPluginManager().disablePlugin(plugin);
									
									p.closeInventory();
									p.sendMessage(Utils.prefix + FileUtils.getConfigString("Messages.PluginDisabled").replace("%plugin%", plugin.getDescription().getName()));
								} else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(FileUtils.getConfigString("Settings.SettingsInventory.Back.DisplayName"))) {
									p.closeInventory();
									
									Utils.openInventory(p);
								}
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onInvenoryClose(InventoryCloseEvent e) {
		if(e.getPlayer() instanceof Player) {
			Player p = (Player) e.getPlayer();
			
			for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
				if(e.getView().getTitle().equalsIgnoreCase(FileUtils.getConfigString("Settings.PluginsInventory.Title").replace("%plugin%", plugin.getDescription().getName())) || e.getView().getTitle().equalsIgnoreCase(FileUtils.getConfigString("Settings.SettingsInventory.Title").replace("%plugin%", plugin.getDescription().getName()))) {
					Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
						
						@Override
						public void run() {
							if(p.isOnline()) {
								if((p.getOpenInventory() == null) || ((p.getOpenInventory() == null) && !(p.getOpenInventory().getTitle().equalsIgnoreCase(FileUtils.getConfigString("Settings.PluginsInventory.Title").replace("%plugin%", plugin.getDescription().getName())) || p.getOpenInventory().getTitle().equalsIgnoreCase(FileUtils.getConfigString("Settings.SettingsInventory.Title").replace("%plugin%", plugin.getDescription().getName()))))) {
									if(Utils.currentPages.containsKey(p.getUniqueId()))
										Utils.currentPages.remove(p.getUniqueId());
								}
							} else if(Utils.currentPages.containsKey(p.getUniqueId()))
								Utils.currentPages.remove(p.getUniqueId());
						}
					}, 20);
				}
			}
		}
	}
	
}
