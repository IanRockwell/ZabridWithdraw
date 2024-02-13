package me.zabrid.zabridwithdraw;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        reloadConfig();
    }

    public void reloadConfig() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        File file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public Object getValue(String path) {
        return config.get(path);
    }

    public <T> T getValue(String path, Class<T> type) {
        Object value = config.get(path);
        if (value != null && type.isInstance(value)) {
            return type.cast(value);
        } else {
            return null;
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void saveConfig() {
        File file = new File(plugin.getDataFolder(), "config.yml");
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save config.yml!");
        }
    }
}