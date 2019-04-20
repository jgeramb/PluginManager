package net.dev.pluginmanager.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public class FileUtils {

	public static File folder = new File("plugins/PluginManager/");
	public static File file = new File("plugins/PluginManager/setup.yml");
	public static YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
	
	public static void saveFile() {
		try {
			cfg.save(file);
		} catch (IOException e) {
		}
		
		Utils.prefix = getConfigString("Messages.Prefix");
		Utils.noPerm = getConfigString("Messages.NoPerm");
		Utils.notPlayer = getConfigString("Messages.NotPlayer");
	}
	
	public static void setupFiles() {
		if(!(folder.exists()))
			folder.mkdir();
		
		if(!(file.exists())) {
			try {
				file.createNewFile();
			} catch (IOException e) {
			}
		}
		
		boolean is1_13 = Utils.getVersion().contains("1_13");
		
		cfg.addDefault("Settings.PluginsInventory.Title", "&aPlugins");
		cfg.addDefault("Settings.PluginsInventory.Plugin.DisplayName", "&e%plugin%");
		cfg.addDefault("Settings.PluginsInventory.Plugin.Lore", getListFromStrings("&7Version&8: &d%plugin%"));
		cfg.addDefault("Settings.PluginsInventory.Plugin.Type.Enabled", is1_13 ? "LEGACY_STAINED_CLAY" : "STAINED_CLAY");
		cfg.addDefault("Settings.PluginsInventory.Plugin.Type.Disabled", is1_13 ? "LEGACY_STAINED_CLAY" : "STAINED_CLAY");
		cfg.addDefault("Settings.PluginsInventory.Plugin.MetaData.Enabled", 5);
		cfg.addDefault("Settings.PluginsInventory.Plugin.MetaData.Disabled", 14);
		cfg.addDefault("Settings.PluginsInventory.Info.DisplayName", "&6&lInfo");
		cfg.addDefault("Settings.PluginsInventory.Info.Lore", getListFromStrings("&7Leftclick a plugin to open it's settings", "&7Rightclick a plugin to toggle it's status"));
		cfg.addDefault("Settings.PluginsInventory.Info.Type", is1_13 ? "LEGACY_NETHER_STAR" : "NETHER_STAR");
		cfg.addDefault("Settings.PluginsInventory.Next.DisplayName", "&8Next");
		cfg.addDefault("Settings.PluginsInventory.Next.Lore", getListFromStrings("&7Click here to go to the next page"));
		cfg.addDefault("Settings.PluginsInventory.Next.Type", is1_13 ? "LEGACY_ARROW" : "ARROW");
		cfg.addDefault("Settings.PluginsInventory.Back.DisplayName", "&8Back");
		cfg.addDefault("Settings.PluginsInventory.Back.Lore", getListFromStrings("&7Click here to go to the previous page"));
		cfg.addDefault("Settings.PluginsInventory.Back.Type", is1_13 ? "LEGACY_ARROW" : "ARROW");
		
		cfg.addDefault("Settings.SettingsInventory.Title", "&aPlugin-Settings&8: &e%plugin%");
		cfg.addDefault("Settings.SettingsInventory.UsePlaceHolders", true);
		cfg.addDefault("Settings.SettingsInventory.PlaceHolder.DisplayName", "&8&k,&r");
		cfg.addDefault("Settings.SettingsInventory.PlaceHolder.Type", is1_13 ? "LEGACY_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE");
		cfg.addDefault("Settings.SettingsInventory.Enable.DisplayName", "&aEnable");
		cfg.addDefault("Settings.SettingsInventory.Enable.Lore", getListFromStrings("&7Click here to enable the plugin"));
		cfg.addDefault("Settings.SettingsInventory.Enable.Type", is1_13 ? "LEGACY_STAINED_CLAY" : "STAINED_CLAY");
		cfg.addDefault("Settings.SettingsInventory.Enable.MetaData", 5);
		cfg.addDefault("Settings.SettingsInventory.Disable.DisplayName", "&cDisable");
		cfg.addDefault("Settings.SettingsInventory.Disable.Lore", getListFromStrings("&7Click here to disable the plugin"));
		cfg.addDefault("Settings.SettingsInventory.Disable.Type", is1_13 ? "LEGACY_STAINED_CLAY" : "STAINED_CLAY");
		cfg.addDefault("Settings.SettingsInventory.Disable.MetaData", 14);
		cfg.addDefault("Settings.SettingsInventory.Info.DisplayName", "&6&l%plugin%");
		cfg.addDefault("Settings.SettingsInventory.Info.Lore", getListFromStrings("&7Version&8: &d%version%"));
		cfg.addDefault("Settings.SettingsInventory.Info.Type", is1_13 ? "LEGACY_NETHER_STAR" : "NETHER_STAR");
		cfg.addDefault("Settings.SettingsInventory.Back.DisplayName", "&8Back");
		cfg.addDefault("Settings.SettingsInventory.Back.Lore", getListFromStrings("&7Click here to go back"));
		cfg.addDefault("Settings.SettingsInventory.Back.Type", is1_13 ? "LEGACY_ARROW" : "ARROW");
		
		cfg.addDefault("Messages.Prefix", "&a&lPluginManager &8» &7");
		cfg.addDefault("Messages.NoPerm", "Unknown command. Type \"/help\" for help.");
		cfg.addDefault("Messages.NotPlayer", "&cOnly players can perform this command");
		cfg.addDefault("Messages.NoMorePages", "&7There are &cno &7more pages");
		cfg.addDefault("Messages.AlreadyFirstPage", "&7You are &calready &7on the first page");
		cfg.addDefault("Messages.PluginNotFound", "&7The plugin &e%plugin% &7could &cnot &7be found");
		cfg.addDefault("Messages.PluginEnabled", "&7The plugin &e%plugin% &7has been &aenabled");
		cfg.addDefault("Messages.PluginDisabled", "&7The plugin &e%plugin% &7has been &cdisabled");
		cfg.addDefault("Messages.AllPluginsEnabled", "&7All plugins have been &aenabled");
		cfg.addDefault("Messages.AllPluginsDisabled", "&7All plugins have been &cdisabled");
		cfg.addDefault("Messages.PluginCanNotBeToggled", "&7The plugin &e%plugin% &7can &cnot &7be enabled/disabled");
		cfg.addDefault("Messages.PluginLoaded", "&7The plugin &e%plugin% &7has been &aloaded &7and &aenabled");
		cfg.addDefault("Messages.AllPluginsLoaded", "&7All plugins have been &aloaded &7and &aenabled");
		cfg.addDefault("Messages.PluginUnloaded", "&7The plugin &e%plugin% &7has been &cunloaded &7and &cdisabled");
		cfg.addDefault("Messages.AllPluginsUnloaded", "&7All plugins have been &cunloaded &7and &cdisabled");
		cfg.addDefault("Messages.PluginReloaded", "&7The plugin &e%plugin% &7has been &ereloaded &7and &aenabled");
		cfg.addDefault("Messages.AllPluginsReloaded", "&7All plugins have been &ereloaded &7and &aenabled");
		cfg.addDefault("Messages.PluginCanNotBeLoaded", "&7The plugin &e%plugin% &7could &cnot &7be loaded");
		cfg.addDefault("Messages.AllPlugins.Header", "&8⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ &aAll plugins &8⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯");
		cfg.addDefault("Messages.AllPlugins.Plugin", "%state%%name% &7(%version%)");
		cfg.addDefault("Messages.AllPlugins.Footer", "&8⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯");
		cfg.addDefault("Messages.PluginRestarted", "&7The plugin &e%plugin% &7has been &erestarted");
		cfg.addDefault("Messages.AllPluginsRestarted", "&7All plugins have been &erestarted");
		cfg.addDefault("Messages.PluginCommands.Header", "&8⎯⎯⎯⎯⎯ &aAll commands of &e%plugin% &8⎯⎯⎯⎯⎯");
		cfg.addDefault("Messages.PluginCommands.Command", "&a%name% &7(%usage%)");
		cfg.addDefault("Messages.PluginCommands.Footer", "&8⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯");
		cfg.addDefault("Messages.NoPluginCommands", "&7The plugin &e%plugin% &7has &cno &7commands");
		cfg.addDefault("Messages.CommandBelongsToPlugin", "&7The command &e%command% &7belongs to the plugin &a%plugin%");
		cfg.addDefault("Messages.CommandNotFound", "&7The command &e%command% &7does &cnot &7exist");
		cfg.addDefault("Messages.PluginAlreadyLoaded", "&7The plugin &e%plugin% &7is &calready &7loaded");
		
		List<String> lines = new ArrayList<>();
		lines.add("&8⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ &a%name% &8⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯");
		lines.add("&7Author(s)&8: &b%authors%");
		lines.add("&7Version&8: &d%version%");
		lines.add("&8⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ &a%name% &8⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯");
		
		cfg.addDefault("Messages.PluginInfo", lines);
		cfg.options().copyDefaults(true);
		saveFile();
	}
	
	public static String getConfigString(String path) {
		return ChatColor.translateAlternateColorCodes('&', cfg.getString(path));
	}
	
	private static List<String> getListFromStrings(String... strings) {
		return Arrays.asList(strings);
	}

	public static List<String> getStringList(String path) {
		List<String> list = new ArrayList<>();
		
		for (String string : cfg.getStringList(path))
			list.add(ChatColor.translateAlternateColorCodes('&', string));
		
		return list;
	}

}
