package com.sunflowerplugin.flyfood.messages;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class MessageManager {

    private FileConfiguration messagesConfig;
    private final File messagesFile;

    public MessageManager(File dataFolder) {
        this.messagesFile = new File(dataFolder, "messages.yml");
        reload();
    }

    public void reload() {
        if (!messagesFile.exists()) {
            try {
                messagesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public String get(String key) {
        return get(key, null);
    }

    public String get(String key, Map<String, String> placeholders) {
        String msg = messagesConfig.getString(key, "&c[Không tìm thấy key: " + key + "]");

        // Thay thế các placeholder (ví dụ %code%)
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                msg = msg.replace("%" + entry.getKey() + "%", entry.getValue());
            }
        }

        // Chuyển & thành ký tự màu §
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public void save() {
        try {
            messagesConfig.save(messagesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
