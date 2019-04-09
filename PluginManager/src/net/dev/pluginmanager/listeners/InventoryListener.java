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
				if(e.getInventory().getName().equalsIgnoreCase("§aPlugins")) {
					if(p.hasPermission("pluginmanager.use")) {
						e.setCancelled(true);
						
						if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§7Next")) {
							if(Bukkit.getPluginManager().getPlugins().length >= (45 * (Utils.currentPages.get(p.getUniqueId()) + 1))) {
								Utils.currentPages.put(p.getUniqueId(), Utils.currentPages.get(p.getUniqueId()) + 1);
								
								Utils.openInventory(p);
							} else {
								p.sendMessage(Utils.prefix + FileUtils.getConfigString("Messages.NoMorePages"));
							}
						} else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§7Back")) {
							if(Utils.currentPages.get(p.getUniqueId()) != 0) {
								Utils.currentPages.put(p.getUniqueId(), Utils.currentPages.get(p.getUniqueId()) - 1);
								
								Utils.openInventory(p);
							} else {
								p.sendMessage(Utils.prefix + FileUtils.getConfigString("Messages.AlreadyFirstPage"));
							}
						} else {
							for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
								if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§e" + plugin.getDescription().getName())) {
									if(e.isRightClick()) {
										if(plugin.isEnabled()) {
											Bukkit.getPluginManager().disablePlugin(plugin);
											
											ItemStack newItem = new ItemStack(e.getCurrentItem().getType(), e.getCurrentItem().getAmount(), (byte) 14);
											newItem.setItemMeta(e.getCurrentItem().getItemMeta());
											
											e.getInventory().setItem(e.getSlot(), newItem);
											p.updateInventory();
										} else {
											Bukkit.getPluginManager().enablePlugin(plugin);
											
											ItemStack newItem = new ItemStack(e.getCurrentItem().getType(), e.getCurrentItem().getAmount(), (byte) 5);
											newItem.setItemMeta(e.getCurrentItem().getItemMeta());
											
											e.getInventory().setItem(e.getSlot(), newItem);
											p.updateInventory();
										}
									} else {
										p.closeInventory();
										
										Inventory inv = Bukkit.createInventory(null, 27, "§aPlugin-Settings§8: §e" + plugin.getDescription().getName());
										
										for (int i = 0; i < inv.getSize(); i++)
											inv.setItem(i, Utils.createItem(Utils.getVersion().contains("1_13") ? Material.getMaterial("LEGACY_STAINED_GLASS_PANE") : Material.getMaterial("STAINED_GLASS_PANE"), 1, 0, ""));
											
										inv.setItem(10, Utils.createItem(Utils.getVersion().contains("1_13") ? Material.getMaterial("LEGACY_WOOL") : Material.getMaterial("WOOL"), 1, 5, "§aEnable", "§7Click here to enable the plugin"));
										inv.setItem(13, Utils.createItem(Utils.getVersion().contains("1_13") ? Material.getMaterial("LEGACY_ANVIL") : Material.getMaterial("ANVIL"), 1, 0, "§e" + plugin.getDescription().getName(), "§7Version§8: §d" + plugin.getDescription().getVersion()));
										inv.setItem(16, Utils.createItem(Utils.getVersion().contains("1_13") ? Material.getMaterial("LEGACY_WOOL") : Material.getMaterial("WOOL"), 1, 14, "§cDisable", "§7Click here to enable the plugin"));
										
										inv.setItem(18, Utils.createItem(Utils.getVersion().contains("1_13") ? Material.PAPER : Material.getMaterial("PAPER"), 1, 0, "§7Back", "§7Click here to go back"));
										
										p.openInventory(inv);
									}

									break;
								}
							}
						}
					}
				} else {
					for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
						if(e.getInventory().getName().equalsIgnoreCase("§aPlugin-Settings§8: §e" + plugin.getDescription().getName())) {
							if(p.hasPermission("pluginmanager.use")) {
								e.setCancelled(true);
								
								if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§aEnable")) {
									if(!(plugin.isEnabled()))
										Bukkit.getPluginManager().enablePlugin(plugin);
									
									p.closeInventory();
									p.sendMessage(Utils.prefix + FileUtils.getConfigString("Messages.PluginEnabled").replace("%plugin%", plugin.getDescription().getName()));
								} else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§cDisable")) {
									if(plugin.isEnabled())
										Bukkit.getPluginManager().disablePlugin(plugin);
									
									p.closeInventory();
									p.sendMessage(Utils.prefix + FileUtils.getConfigString("Messages.PluginDisabled").replace("%plugin%", plugin.getDescription().getName()));
								} else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§7Back")) {
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
			
			if(e.getInventory().getName().startsWith("§aPlugin")) {
				Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
					
					@Override
					public void run() {
						if(p.isOnline()) {
							if((p.getOpenInventory() != null) && !(p.getOpenInventory().getTitle().startsWith("§aPlugin"))) {
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
