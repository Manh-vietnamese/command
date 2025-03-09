package Sunflower.config;

import Sunflower.MainPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    private final MainPlugin plugin;

    public Config(MainPlugin plugin) {
        this.plugin = plugin;
    }

    public void reloadConfig() {
        plugin.reloadConfig();
    }

    public FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    // 📌 Lấy cooldown cho lệnh /food từ config.yml
    public int getFoodCountdown(String rank) {
        return getConfig().getInt("food-countdown." + rank, 30);
    }

    // 📌 Lấy cooldown cho lệnh /heal từ config.yml
    public int getHealCountdown(String rank) {
        return getConfig().getInt("heal-countdown." + rank, 300);
    }

    // 📌 Lấy cooldown cho lệnh /fly từ config.yml
    public int getFlyCountdown(String rank) {
        return getConfig().getInt("fly-countdown." + rank, 30);
    }

    public int getFlyUsageTime(String rank) {
        return getConfig().getInt("fly-usage-time." + rank, 30);
    }
}
