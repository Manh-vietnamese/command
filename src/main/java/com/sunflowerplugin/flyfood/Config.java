package com.sunflowerplugin.flyfood;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Config {

    private FileConfiguration config;
    private final JavaPlugin plugin;

    public Config(MainPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();  // Đảm bảo config được tải
    }

    // Reload cấu hình khi cần thiết
    public void reloadConfig() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();  // Cập nhật lại config sau khi reload
    }

    // Lấy thời gian cooldown cho lệnh /food, dựa trên quyền
    public int getFoodCooldown(String permission) {
        if (!config.contains("food." + permission)) {
            plugin.getLogger().severe("❌ Không thể lấy giá trị cấu hình cho: food." + permission);
            plugin.getServer().getPluginManager().disablePlugin(plugin);  // Vô hiệu hóa plugin
            return -1;  // Không có giá trị
        }
        return config.getInt("food." + permission, -1);  // Trả về giá trị cấu hình
    }

    // Lấy thời gian countdown cho lệnh /fly
    public int getFlyCountdown(String permission) {
        if (!config.contains("fly.countdown." + permission)) {
            plugin.getLogger().severe("❌ Không thể lấy giá trị cấu hình cho: fly.countdown." + permission);
            plugin.getServer().getPluginManager().disablePlugin(plugin);  // Vô hiệu hóa plugin
            return -1;  // Không có giá trị
        }
        return config.getInt("fly.countdown." + permission, -1);  // Trả về giá trị cấu hình
    }

    // Lấy thời gian sử dụng cho lệnh /fly
    public int getFlyUsageTime(String permission) {
        if (!config.contains("fly.usage_time." + permission)) {
            plugin.getLogger().severe("❌ Không thể lấy giá trị cấu hình cho: fly.usage_time." + permission);
            plugin.getServer().getPluginManager().disablePlugin(plugin);  // Vô hiệu hóa plugin
            return -1;  // Không có giá trị
        }
        return config.getInt("fly.usage_time." + permission, -1);  // Trả về giá trị cấu hình
    }

    public void saveDefaultConfig() {
        plugin.saveDefaultConfig();
    }

    public FileConfiguration getConfig() {
        return this.config;
    }
}
