package Sunflower;

import Sunflower.commands.FlyCommand;
import Sunflower.commands.FoodCommand;
import Sunflower.commands.HealCommand;
import Sunflower.commands.ReloadCommand;
import Sunflower.config.Config;
import Sunflower.messages.MessageManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public class MainPlugin extends JavaPlugin {

    private Config configManager;
    private MessageManager messageManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        checkAndCreateConfig();

        this.configManager = new Config(this);

        // 🔥 Sửa lỗi: Khởi tạo `messageManager` trước khi đăng ký lệnh
        messageManager = new MessageManager(this, getDataFolder(), getLogger());

        FoodCommand foodCmd = new FoodCommand(this);
        HealCommand healCmd = new HealCommand(this);

        // 📌 Đăng ký lệnh (Chỉ đăng ký nếu `messageManager` đã được tạo)
        Objects.requireNonNull(getCommand("fly")).setExecutor(new FlyCommand(this));
        Objects.requireNonNull(getCommand("food")).setExecutor(foodCmd);
        Objects.requireNonNull(getCommand("freload")).setExecutor(new ReloadCommand(this, foodCmd));
        Objects.requireNonNull(getCommand("heal")).setExecutor(healCmd);

        getLogger().info("✅ Plugin FlyFood đã bật thành công!");
    }

    public void reloadConfigs() {
        try {
            checkAndCreateConfig();
            configManager.reloadConfig();
            getLogger().info("✅ Cấu hình đã được tải lại thành công!");
        } catch (Exception e) {
            getLogger().severe("❌ Lỗi khi tải lại cấu hình: " + e.getMessage());
            for (StackTraceElement element : e.getStackTrace()) {
                getLogger().severe("    at " + element.toString());
            }
        }
    }

    private void checkAndCreateConfig() {
        createFile("config.yml", true);
        createFile("messages.yml", false);
    }

    private void createFile(String fileName, boolean isConfig) {
        File file = new File(getDataFolder(), fileName);
        if (!file.exists()) {
            getLogger().warning("⚠️ Không tìm thấy " + fileName + "! Đang tạo file mới...");
            if (isConfig) {
                saveDefaultConfig();
                reloadConfig();
            } else {
                saveResource(fileName, false);
            }
            getLogger().info("✅ File " + fileName + " đã được khôi phục!");
        }
    }

    public Config getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        if (messageManager == null) {
            getLogger().severe("❌ MessageManager chưa được khởi tạo! Kiểm tra lại MainPlugin.java.");
        }
        return messageManager;
    }
}
