package Sunflower.messages;

import Sunflower.MainPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.logging.Logger;

public class MessageManager {

    private final MainPlugin plugin;
    private FileConfiguration messagesConfig;
    private final File messagesFile;
    private final Logger logger;

    public MessageManager(MainPlugin plugin, File dataFolder, Logger logger) {
        this.plugin = plugin;
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
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
            logger.info("✅ File messages.yml đã được khôi phục!");
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        logger.info("🔄 messages.yml đã được tải lại.");
    }

    public String get(String key) {
        return get(key, null);
    }

    public String get(String key, Map<String, String> placeholders) {
        if (key == null) {
            logger.severe("❌ Gọi messageManager.get() với key = null! Kiểm tra lại mã nguồn.");
            return "⚠️ [Lỗi] Key không hợp lệ!";
        }

        String message = messagesConfig.getString(key);

        if (message == null) {
            logger.warning("⚠️ Thiếu key trong messages.yml: " + key + ". Tự động tải lại file...");
            reload();
            message = messagesConfig.getString(key, "⚠️ [Lỗi] Không tìm thấy tin nhắn!");

            if (message.equals("⚠️ [Lỗi] Không tìm thấy tin nhắn!")) {
                logger.severe("❌ Key '" + key + "' vẫn bị thiếu sau khi reload!");
            }
        }

        // 📌 Kiểm tra nếu placeholders bị null để tránh lỗi NullPointerException
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace("%" + entry.getKey() + "%", entry.getValue());
            }
        }

        return message;
    }

    public void save() {
        try {
            messagesConfig.save(messagesFile);
        } catch (IOException e) {
            logger.severe("Lỗi khi lưu messages.yml: " + e.getMessage());
        }
    }
}
