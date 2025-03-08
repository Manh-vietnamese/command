package com.sunflowerplugin.flyfood.messages;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.logging.Logger;

public class MessageManager {

    private FileConfiguration messagesConfig;
    private final File messagesFile;
    private final Logger logger;

    public MessageManager(File dataFolder, Logger logger) {
        this.messagesFile = new File(dataFolder, "messages.yml");
        this.logger = logger;
        validateFile();
        reload();
    }

    /**
     * Kiểm tra file messages.yml có hợp lệ không, nếu không sẽ đổi tên và tạo mới
     */
    private void validateFile() {
        if (!messagesFile.exists()) {
            logger.warning("messages.yml không tồn tại, tạo mới...");
            createNewFile();
            return;
        }

        try {
            FileConfiguration config = YamlConfiguration.loadConfiguration(messagesFile);
            if (config.getKeys(false).isEmpty()) {
                throw new IOException("File trống hoặc không đúng định dạng!");
            }
            logger.info("messages.yml hợp lệ.");
        } catch (Exception e) {
            logger.severe("Lỗi trong messages.yml: " + e.getMessage());
            backupAndCreateNewFile();
        }
    }

    private void backupAndCreateNewFile() {
        File backupFile = new File(messagesFile.getParent(), "messages.yml.backup");

        try {
            Files.move(messagesFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            logger.warning("messages.yml bị lỗi, đã đổi tên thành: " + backupFile.getName());
        } catch (IOException e) {
            logger.severe("Không thể đổi tên file lỗi: " + e.getMessage());
        }

        createNewFile();
    }

    private void createNewFile() {
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        // Nội dung mặc định của messages.yml
        messagesConfig.set("reload_success", "&aĐã reload thành công!");
        messagesConfig.set("reload_missing_args", "&cThiếu tham số! Vui lòng nhập lại.");
        messagesConfig.set("config_invalid", "&cLỗi trong file cấu hình! Đã tạo file mới.");
        messagesConfig.set("config_fixed", "&aFile cấu hình hợp lệ.");

        save();
        logger.info("Tạo file mới: messages.yml với nội dung mặc định.");
    }

    public void reload() {
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public String get(String key) {
        return get(key, null);
    }

    public String get(String key, Map<String, String> placeholders) {
        String msg = messagesConfig.getString(key, "&c[Không tìm thấy key: " + key + "]");

        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                msg = msg.replace("%" + entry.getKey() + "%", entry.getValue());
            }
        }

        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public void save() {
        try {
            messagesConfig.save(messagesFile);
        } catch (IOException e) {
            logger.severe("Lỗi khi lưu messages.yml: " + e.getMessage());
        }
    }
}
