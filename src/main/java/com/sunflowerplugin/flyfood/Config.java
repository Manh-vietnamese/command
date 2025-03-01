package com.sunflowerplugin.flyfood;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

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

    // Lấy thời gian cooldown cho lệnh /food từ cấu hình
    public int getFoodCooldown(String permission) {
        List<?> foodConfig = config.getList("food");
        if (foodConfig != null) {
            for (Object obj : foodConfig) {
                if (obj instanceof Map) {
                    Map<?, ?> foodEntry = (Map<?, ?>) obj;
                    String foodPermission = (String) foodEntry.get("permission");
                    if (foodPermission != null && foodPermission.equals(permission)) {
                        Object cooldownValue = foodEntry.get("cooldown");
                        // Kiểm tra và ép kiểu nếu giá trị có trong cấu hình
                        if (cooldownValue instanceof Integer) {
                            return (int) cooldownValue;
                        }
                    }
                }
            }
        }
        plugin.getLogger().warning("⚠️ Không tìm thấy quyền cấu hình cho: " + permission);
        return 40; // Trả về giá trị mặc định nếu không tìm thấy quyền
    }

    // Lấy thời gian countdown cho lệnh /fly từ cấu hình
    public int getFlyCountdown(String permission) {
        List<?> flyConfig = config.getList("fly");
        if (flyConfig != null) {
            for (Object obj : flyConfig) {
                if (obj instanceof Map) {
                    Map<?, ?> flyEntry = (Map<?, ?>) obj;
                    String flyPermission = (String) flyEntry.get("permission");
                    if (flyPermission != null && flyPermission.equals(permission)) {
                        Object countdownValue = flyEntry.get("countdown");
                        // Kiểm tra và ép kiểu nếu giá trị có trong cấu hình
                        if (countdownValue instanceof Integer) {
                            return (int) countdownValue;
                        }
                    }
                }
            }
        }
        plugin.getLogger().warning("⚠️ Không tìm thấy quyền cấu hình cho: " + permission);
        return 60; // Trả về giá trị mặc định nếu không tìm thấy quyền
    }

    // Lấy thời gian sử dụng cho lệnh /fly từ cấu hình
    public int getFlyUsageTime(String permission) {
        List<?> flyConfig = config.getList("fly");
        if (flyConfig != null) {
            for (Object obj : flyConfig) {
                if (obj instanceof Map) {
                    Map<?, ?> flyEntry = (Map<?, ?>) obj;
                    String flyPermission = (String) flyEntry.get("permission");
                    if (flyPermission != null && flyPermission.equals(permission)) {
                        Object usageTimeValue = flyEntry.get("usage_time");
                        // Kiểm tra và ép kiểu nếu giá trị có trong cấu hình
                        if (usageTimeValue instanceof Integer) {
                            return (int) usageTimeValue;
                        }
                    }
                }
            }
        }
        plugin.getLogger().warning("⚠️ Không tìm thấy quyền cấu hình cho: " + permission);
        return 15; // Trả về giá trị mặc định nếu không tìm thấy quyền
    }
}
