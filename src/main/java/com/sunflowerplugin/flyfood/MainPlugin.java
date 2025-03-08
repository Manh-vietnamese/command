package com.sunflowerplugin.flyfood;

import com.sunflowerplugin.flyfood.commands.FlyCommand;
import com.sunflowerplugin.flyfood.commands.FoodCommand;
import com.sunflowerplugin.flyfood.commands.HealCommand;
import com.sunflowerplugin.flyfood.commands.ReloadCommand;
import com.sunflowerplugin.flyfood.config.Config;
import com.sunflowerplugin.flyfood.messages.MessageManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.util.Objects;

public class MainPlugin extends JavaPlugin {

    private Config configManager;
    private MessageManager messageManager;

    @Override
    public void onEnable() {
        checkAndCreateConfig();

        this.configManager = new Config(this);
        this.messageManager = new MessageManager(getDataFolder()); // 📌 KHỞI TẠO messageManager TRƯỚC

        // 📌 Đảm bảo messageManager được khởi tạo trước khi truyền vào các command

        FoodCommand foodCmd = new FoodCommand(this);
        HealCommand healCmd = new HealCommand(this);

        // 📌 Đăng ký lệnh
        Objects.requireNonNull(getCommand("fly")).setExecutor(new FlyCommand(this));
        Objects.requireNonNull(getCommand("food")).setExecutor(foodCmd);
        Objects.requireNonNull(getCommand("freload")).setExecutor(new ReloadCommand(this, foodCmd));
        Objects.requireNonNull(getCommand("heal")).setExecutor(healCmd);
    }

    public void reloadConfigs() {
        try {
            checkAndCreateConfig(); // 📌 Kiểm tra file khi reload
            configManager.reloadConfig();
            getLogger().info("✅ Cấu hình đã được tải lại thành công!");
        } catch (Exception e) {
            getLogger().severe("❌ Lỗi khi tải lại cấu hình: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 📌 Kiểm tra và tạo lại file config.yml nếu bị mất
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
        return messageManager;
    }
}
