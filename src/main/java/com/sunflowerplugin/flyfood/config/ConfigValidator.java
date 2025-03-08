package com.sunflowerplugin.flyfood.config;

import com.sunflowerplugin.flyfood.MainPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ConfigValidator {

    private final MainPlugin plugin;

    public ConfigValidator(MainPlugin plugin) {
        this.plugin = plugin;
    }

    public void validateFiles() {
        checkFile("config.yml", getDefaultConfigContent());
        checkFile("messages.yml", getDefaultMessagesContent());
    }

    private void checkFile(String fileName, String defaultContent) {
        File file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists()) {
            plugin.getLogger().warning(fileName + " không tồn tại, tạo mới...");
            createNewFile(file, defaultContent);
            return;
        }

        try {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            if (config.getKeys(false).isEmpty()) {
                throw new IOException("File trống hoặc không đúng định dạng!");
            }
            plugin.getLogger().info(fileName + " hợp lệ.");
        } catch (Exception e) {
            plugin.getLogger().severe("Lỗi trong " + fileName + ": " + e.getMessage());
            backupAndCreateNewFile(file, defaultContent);
        }
    }

    private void backupAndCreateNewFile(File file, String defaultContent) {
        File backupFile = new File(file.getParent(), file.getName() + ".backup");

        try {
            Files.move(file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            plugin.getLogger().warning("Đã đổi tên file lỗi thành: " + backupFile.getName());
        } catch (IOException e) {
            plugin.getLogger().severe("Không thể đổi tên file: " + e.getMessage());
        }

        createNewFile(file, defaultContent);
    }

    private void createNewFile(File file, String content) {
        try {
            Files.write(file.toPath(), content.getBytes());
            plugin.getLogger().info("Tạo file mới: " + file.getName());
        } catch (IOException e) {
            plugin.getLogger().severe("Lỗi khi tạo file mới: " + e.getMessage());
        }
    }

    private String getDefaultConfigContent() {
        return "setting1: true\nsetting2: 100\n";
    }

    private String getDefaultMessagesContent() {
        return "reload_success: 'Đã reload thành công!'\nreload_missing_args: 'Thiếu tham số!'\n";
    }
}
